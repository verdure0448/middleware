package com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.device;

import java.nio.charset.StandardCharsets;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.hdbsnc.smartiot.adapter.websocketapi.constant.WebSocketAdapterConst;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractFunctionHandler;
import com.hdbsnc.smartiot.common.pm.IProfileManager;
import com.hdbsnc.smartiot.common.pm.vo.IDevicePoolObj;
import com.hdbsnc.smartiot.common.pm.vo.IDomainIdMastObj;
import com.hdbsnc.smartiot.common.pm.vo.IModifyDevicePoolObj;
import com.hdbsnc.smartiot.common.pm.vo.IModifyDomainIdMastObj;

/**
 * devpool/set
 * 
 * @author KANG
 *
 */
public class DevicePoolSetHandler extends AbstractFunctionHandler {

	private IProfileManager pm;

	public DevicePoolSetHandler(IProfileManager pm) {
		super("set");
		this.pm = pm;
	}

	@Override
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		String dpid = inboundCtx.getParams().get(WebSocketAdapterConst.DPID);

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

		IModifyDevicePoolObj mObj = pm.getModifyDevicePoolObj();

		// 장치풀ID
		mObj.devPoolId(dpid);
		// 장치풀명
		mObj.devPoolNm((String) inputJson.get(WebSocketAdapterConst.DEV_POOL_NAME));
		// 비고
		mObj.remark((String) inputJson.get(WebSocketAdapterConst.REMARK));

		//////////////////////////////////////////////////////////////////////
		// 변경할 도메인 정보 유무 체크
		//////////////////////////////////////////////////////////////////////
		// 도메인 조회
		IDomainIdMastObj orgDomainObj = pm.getDomainIdMastObj(dpid);
		// 도메인 정보가 존재하지 않을 경우 에러처리
		if (orgDomainObj == null)
			// throw new ContextHandlerApplicationException(1008,
			// CommonException.TYPE_WARNNING, "도메인 정보가 존재하지 않습니다.");
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":010", new String[]{dpid}, null);

		//////////////////////////////////////////////////////////////////////
		// 변경할 장치풀 정보 유무 체크
		//////////////////////////////////////////////////////////////////////
		IDevicePoolObj orgDevPoolObj = pm.getDevicePoolObj(dpid);
		// 장치풀 정보가 존재하지 않을 경우 에러처리
		if (orgDevPoolObj == null)
			// throw new ContextHandlerApplicationException(1009,
			// CommonException.TYPE_WARNNING, "장치풀 정보가 존재하지 않습니다.");
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":015", new String[]{dpid}, null);

		//////////////////////////////////////////////////////////////////////
		// 도메인 변경
		//////////////////////////////////////////////////////////////////////
		IModifyDomainIdMastObj mDomainObj = pm.getModifyDomainIdMastObj();
		try {
			mDomainObj.domainId(dpid).domainNm((String) inputJson.get(WebSocketAdapterConst.DEV_POOL_NAME))
					.domainType(WebSocketAdapterConst.DPID).update();
		} catch (Exception e) {// 도메인 변경 실패시 에러 처리
			// throw new ContextHandlerApplicationException(312,
			// CommonException.TYPE_ERROR, "도메인 변경에 실패 했습니다.", e);
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":020", new String[]{dpid},
					e);
		}

		//////////////////////////////////////////////////////////////////////
		// 장치풀 변경
		//////////////////////////////////////////////////////////////////////
		try {
			mObj.update();
		} catch (Exception ex) {
			// 도메인 롤백
			mDomainObj = pm.getModifyDomainIdMastObj(orgDomainObj);
			mDomainObj.update();
			// throw new ContextHandlerApplicationException(313,
			// CommonException.TYPE_ERROR, "장치풀 변경에 실패 했습니다.", ex);
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":025", new String[]{dpid},
					ex);
		}

		outboundCtx.getPaths().add("ack");
		outboundCtx.setSID(inboundCtx.getSID());
		outboundCtx.setSPort(inboundCtx.getSPort());
		outboundCtx.setTID("this");
		outboundCtx.setTPort(inboundCtx.getTPort());
		outboundCtx.setTransmission("res");

	}
}
