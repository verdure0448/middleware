package com.hdbsnc.smartiot.service.autostart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hdbsnc.smartiot.common.aim.IAdapterInstanceManager;
import com.hdbsnc.smartiot.common.am.IAdapter;
import com.hdbsnc.smartiot.common.am.IAdapterManager;
import com.hdbsnc.smartiot.common.context.impl.InnerContext;
import com.hdbsnc.smartiot.common.otp.url.parser.UrlParser;
import com.hdbsnc.smartiot.common.pm.IProfileManager;
import com.hdbsnc.smartiot.common.pm.vo.IInstanceAttributeObj;
import com.hdbsnc.smartiot.common.pm.vo.IInstanceObj;
import com.hdbsnc.smartiot.server.IServerInstance;
import com.hdbsnc.smartiot.service.IService;
import com.hdbsnc.smartiot.util.logger.Log;

public class AutoStart implements IService {

	private static final String INSTANCE_AUTO_START = "true";
	private static final String INSTANCE_ATTRIBUTE_AUTO_START = "true";

	private int currentState = IService.SERVICE_STATE_REG;
	private long lastAccessedTime = 0;
	private IAdapterInstanceManager aim;
	private Log log;
	private IProfileManager pm;
	private IAdapterManager am;
	private List<IInstanceObj> instanceIdArray = null;

	public AutoStart(IServerInstance server) {
		this.lastAccessedTime = System.currentTimeMillis();
		this.aim = server.getAIM();
		this.log = server.getCommonService().getLogger().logger(getServiceName());
		this.pm = server.getPM();
		this.am = server.getAM();

		instanceIdArray = new ArrayList();
	}

	@Override
	public String getServiceName() {
		return "AutoStartAdapterInstance";
	}

	@Override
	public int getServiceState() {
		return currentState;
	}

	@Override
	public long getLastAccessedTime() {
		return this.lastAccessedTime;
	}

	@Override
	public void init(Map<String, String> config) throws Exception {

		String adapterId;
		// 모든 아답터 ID 가지고 오기

		for (IAdapter adapter : am.getAdapterList()) {
			adapterId = adapter.getAdapterId();
			if (pm.searchInstanceByAid(adapterId) == null) {
				continue;
			}

			// AUTO START인 Instance를 자동실행 시킨다.
			for (IInstanceObj instanceObj : pm.searchInstanceByAid(adapterId)) {
				if (INSTANCE_AUTO_START.equals(instanceObj.getInitDevStatus())) {
					instanceIdArray.add(instanceObj);
				}
			}
		}

	}

	@Override
	public void start() throws Exception {

		log.info("인스턴스 기동을 1초 후에 실행...........");
		Thread.sleep(1000);
		log.info("인스턴스 기동 시작");

		if (instanceIdArray != null) {
			String insId, did;
			for (int i = 0; i < instanceIdArray.size(); i++) {
				insId = instanceIdArray.get(i).getInsId().trim();
				try {
					log.info("IID(" + insId + ") starting.");
					aim.start(insId);
					log.info("IID(" + insId + ") started.");
				} catch (Exception e) {
					log.err(e.getMessage() + ": IID(" + insId + ") start failed.");
				}
			}
		}

		log.info("인스턴스 속성 기동을 1초 후에 실행...........");
		Thread.sleep(1000);
		log.info("인스턴스 속성 기동을 시작");
		if (instanceIdArray != null) {
			String insId, did;
			for (int i = 0; i < instanceIdArray.size(); i++) {
				insId = instanceIdArray.get(i).getInsId().trim();
				did = instanceIdArray.get(i).getDefaultDevId();
				
				// 자동 기동된 인스턴스 중 속성을 초기화 해줘야하는 Attribute가 존재 하면 자동 실행 시킨다.
				if (pm.getInstanceAttributeList(insId) == null) {
					continue;
				}

				for (IInstanceAttributeObj obj : pm.getInstanceAttributeList(insId)) {
					if (INSTANCE_ATTRIBUTE_AUTO_START.equals(obj.getInit())) {

						InnerContext request = new InnerContext();
						request.setSid(did);
						request.setTid(did);

						Map params = new HashMap<>();
						params.put("update", obj.getValue());

						request.setParams(params);
						request.setPaths(Arrays.asList(obj.getKey().split("/")));

						try {
							aim.handOverContext(request);
							System.out.println(UrlParser.getInstance().convertToString(request));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	@Override
	public void stop() throws Exception {
		// 구현할 내용 없음.

	}

}
