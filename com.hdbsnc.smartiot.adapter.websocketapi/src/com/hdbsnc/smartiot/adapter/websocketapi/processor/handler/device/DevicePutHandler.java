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
 * dev/put
 * 
 * @author KANG
 *
 */
public class DevicePutHandler extends AbstractFunctionHandler {

	private IProfileManager pm;

	public DevicePutHandler(IProfileManager pm) {
		super("put");
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

		IModifyDeviceObj mObj = pm.getModifyDeviceObj();

		// 장치ID
		mObj.devId(did);

		// 장치풀ID
		mObj.devPoolId((String) inputJson.get(WebSocketAdapterConst.DPID));

		// 장치명
		mObj.devNm((String) inputJson.get(WebSocketAdapterConst.DEV_NAME));
		
		// 장치구분
		mObj.devType((String) inputJson.get(WebSocketAdapterConst.DEV_TYPE));

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
		// 도메인 등록대상 유무 체크
		//////////////////////////////////////////////////////////////////////
		IDomainIdMastObj domainObj = pm.getDomainIdMastObj(did);
		if (domainObj != null)
			// throw new ContextHandlerApplicationException(1010,
			// CommonException.TYPE_WARNNING, "장치ID가 도메인에 존재합니다.");
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":010", new String[]{did}, null);

		//////////////////////////////////////////////////////////////////////
		// 장치 등록대상 유무 체크
		//////////////////////////////////////////////////////////////////////
		IDeviceObj devObj = pm.getDeviceObj(did);
		if (devObj != null)
			// throw new ContextHandlerApplicationException(1011,
			// CommonException.TYPE_WARNNING, "장치 정보가 중복입니다.");
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
		// 도메인 등록
		//////////////////////////////////////////////////////////////////////
		IModifyDomainIdMastObj mDomainObj = pm.getModifyDomainIdMastObj();

		try {
			mDomainObj.domainId(did).domainNm((String) inputJson.get(WebSocketAdapterConst.DEV_NAME))
					.domainType(WebSocketAdapterConst.DID).remark("").insert();
		} catch (Exception e) {// 도메인 등록 실패시 에러 처리
			// throw new ContextHandlerApplicationException(310,
			// CommonException.TYPE_ERROR, "도메인 등록에 실패 했습니다.", e);
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":025", new String[]{did}, e);
		}
		//////////////////////////////////////////////////////////////////////
		// 장치 등록
		//////////////////////////////////////////////////////////////////////
		try {
			mObj.insert();
		} catch (Exception ex) {
			// 도메인 롤백
			mDomainObj.delete();

			//throw new ContextHandlerApplicationException(314, CommonException.TYPE_ERROR, "장치 등록에 실패 했습니다.", ex);
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":030", new String[]{did}, ex);
		}

		outboundCtx.getPaths().add("ack");
		outboundCtx.setSID(inboundCtx.getSID());
		outboundCtx.setSPort(inboundCtx.getSPort());
		outboundCtx.setContenttype(null);
		outboundCtx.setContent(null);
		outboundCtx.setTID("this");
		outboundCtx.setTPort(inboundCtx.getTPort());
		outboundCtx.setTransmission("res");

	}
}
