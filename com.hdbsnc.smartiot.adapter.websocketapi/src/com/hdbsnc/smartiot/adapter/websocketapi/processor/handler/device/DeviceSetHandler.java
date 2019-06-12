package com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.device;

import java.nio.charset.StandardCharsets;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.hdbsnc.smartiot.adapter.websocketapi.constant.WebSocketAdapterConst;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractFunctionHandler;
import com.hdbsnc.smartiot.common.pm.IProfileManager;
import com.hdbsnc.smartiot.common.pm.vo.IDeviceObj;
import com.hdbsnc.smartiot.common.pm.vo.IDevicePoolObj;
import com.hdbsnc.smartiot.common.pm.vo.IDomainIdMastObj;
import com.hdbsnc.smartiot.common.pm.vo.IModifyDeviceObj;
import com.hdbsnc.smartiot.common.pm.vo.IModifyDomainIdMastObj;

/**
 * dev/set
 * 
 * @author KANG
 *
 */
public class DeviceSetHandler extends AbstractFunctionHandler {

	private IProfileManager pm;

	public DeviceSetHandler(IProfileManager pm) {
		super("set");
		this.pm = pm;
	}

	@Override
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		String did = inboundCtx.getParams().get(WebSocketAdapterConst.DID);

		JSONObject inputJson = null;

		try {
			inputJson = (JSONObject) (new JSONParser())
					.parse(new String(inboundCtx.getContent().array(), StandardCharsets.UTF_8));
		} catch (Exception e) {
			// throw new ContextHandlerApplicationException(1005,
			// CommonException.TYPE_WARNNING, "컨텐츠 데이터 형식이 부정합이 있습니다.", e);
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":005", null,
					e);
		}

		IDeviceObj iDevObj = pm.getDeviceObj(did);
		IModifyDeviceObj mObj = pm.getModifyDeviceObj();

		// 장치ID
		mObj.devId(did);

		// 장치풀ID
		mObj.devPoolId((String) inputJson.get(WebSocketAdapterConst.DPID));

		// 장치명
		mObj.devNm((String) inputJson.get(WebSocketAdapterConst.DEV_NAME));
		
		// 장치구분
		mObj.devType(iDevObj.getDevType());

		// 사용여부
		mObj.isUse((String) inputJson.get(WebSocketAdapterConst.IS_USE));

		// 세션타임아웃
		mObj.sessionTimeout((String) inputJson.get(WebSocketAdapterConst.SESSION_TIMEOUT));

		// IP
		mObj.ip((String) inputJson.get(WebSocketAdapterConst.IP));

		// PORT
		mObj.port((String) inputJson.get(WebSocketAdapterConst.PORT));

		// 위도
		mObj.lat((String) inputJson.get(WebSocketAdapterConst.LAT));

		// 경도
		mObj.lon((String) inputJson.get(WebSocketAdapterConst.LON));

		// 비고
		mObj.remark((String) inputJson.get(WebSocketAdapterConst.REMARK));

		//////////////////////////////////////////////////////////////////////
		// 도메인 변경대상 유무 체크
		//////////////////////////////////////////////////////////////////////
		IDomainIdMastObj domainObj = pm.getDomainIdMastObj(did);
		if (domainObj == null)
			// throw new ContextHandlerApplicationException(1008,
			// CommonException.TYPE_WARNNING, "도메인 정보가 존재하지 않습니다.");
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":010", new String[]{did}, null);

		//////////////////////////////////////////////////////////////////////
		// 장치 변경대상 유무 체크
		//////////////////////////////////////////////////////////////////////
		IDeviceObj devObj = pm.getDeviceObj(did);
		if (devObj == null)
			// throw new ContextHandlerApplicationException(1012,
			// CommonException.TYPE_WARNNING, "장치 정보가 존재하지 않습니다.");
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":015", new String[]{did}, null);

		//////////////////////////////////////////////////////////////////////
		// 장치풀 유무 체크
		//////////////////////////////////////////////////////////////////////
		IDevicePoolObj devPoolObj = pm.getDevicePoolObj((String) inputJson.get(WebSocketAdapterConst.DPID));
		if (devPoolObj == null)
			// throw new ContextHandlerApplicationException(1009,
			// CommonException.TYPE_WARNNING, "장치풀 정보가 존재하지 않습니다.");
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":020", 
					new String[]{(String) inputJson.get(WebSocketAdapterConst.DPID)}, null);

		//////////////////////////////////////////////////////////////////////
		// 도메인 변경
		//////////////////////////////////////////////////////////////////////
		IModifyDomainIdMastObj mDomainObj = pm.getModifyDomainIdMastObj();
		try {
			mDomainObj.domainId(did).domainNm((String) inputJson.get(WebSocketAdapterConst.DEV_NAME))
					.domainType(WebSocketAdapterConst.DID).update();
		} catch (Exception e) {// 도메인 변경 실패시 에러 처리
			// throw new ContextHandlerApplicationException(312,
			// CommonException.TYPE_ERROR, "도메인 변경에 실패 했습니다.", e);
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":025", new String[]{did},
					e);
		}
		//////////////////////////////////////////////////////////////////////
		// 장치 변경
		//////////////////////////////////////////////////////////////////////
		try {
			mObj.update();
		} catch (Exception ex) {
			// 도메인 롤백
			mDomainObj = pm.getModifyDomainIdMastObj(domainObj);
			mDomainObj.update();

			// throw new ContextHandlerApplicationException(315,
			// CommonException.TYPE_ERROR, "장치 변경에 실패 했습니다.", ex);
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":030", null,
					ex);
		}

		// 정상 응답

		outboundCtx.getPaths().add("ack");
		outboundCtx.setSID(inboundCtx.getSID());
		outboundCtx.setSPort(inboundCtx.getSPort());
		outboundCtx.setTID("this");
		outboundCtx.setTPort(inboundCtx.getTPort());
		outboundCtx.setTransmission("res");

	}
}
