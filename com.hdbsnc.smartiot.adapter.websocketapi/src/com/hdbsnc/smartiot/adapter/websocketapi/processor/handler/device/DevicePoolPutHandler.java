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
 * dev/pool/put 장치풀 등록
 * 
 * @author KANG
 *
 */
public class DevicePoolPutHandler extends AbstractFunctionHandler {

	private IProfileManager pm;

	public DevicePoolPutHandler(IProfileManager pm) {
		super("put");
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
		// 도메인 유무 체크
		//////////////////////////////////////////////////////////////////////
		IDomainIdMastObj domainObj = pm.getDomainIdMastObj(dpid);
		// 도메인이 존재할 경우 에러 처리
		if (domainObj != null)
			// throw new ContextHandlerApplicationException(1006,
			// CommonException.TYPE_WARNNING, "장치풀ID가 도메인에 존재합니다.");
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":010", new String[]{dpid}, null);

		//////////////////////////////////////////////////////////////////////
		// 장치풀 유무 체크
		//////////////////////////////////////////////////////////////////////
		IDevicePoolObj devPoolObj = pm.getDevicePoolObj(dpid);
		// 장치풀이 존재할 경우 에러 처리
		if (devPoolObj != null)
			// throw new ContextHandlerApplicationException(1007,
			// CommonException.TYPE_WARNNING, "장치풀 정보가 중복입니다.");
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":015", new String[]{dpid}, null);

		//////////////////////////////////////////////////////////////////////
		// 도메인 등록
		//////////////////////////////////////////////////////////////////////
		IModifyDomainIdMastObj mDomainObj = pm.getModifyDomainIdMastObj();

		try {
			mDomainObj.domainId(dpid).domainNm((String) inputJson.get(WebSocketAdapterConst.DEV_POOL_NAME))
					.domainType(WebSocketAdapterConst.DPID).remark("").insert();
		} catch (Exception e) {// 도메인 등록 실패시 에러 처리
			// throw new ContextHandlerApplicationException(310,
			// CommonException.TYPE_ERROR, "도메인 등록에 실패 했습니다.", e);
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":020", new String[]{dpid},
					e);
		}

		//////////////////////////////////////////////////////////////////////
		// 장치풀 등록
		//////////////////////////////////////////////////////////////////////
		try {
			mObj.insert();
		} catch (Exception ex) {
			// 도메인 롤백
			mDomainObj.delete();
			// throw new ContextHandlerApplicationException(311,
			// CommonException.TYPE_ERROR, "장치풀 등록에 실패 했습니다.", ex);
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":025", new String[]{dpid},
					ex);
		}

		// 정상응답
		outboundCtx.getPaths().add("ack");
		outboundCtx.setContenttype(null);
		outboundCtx.setContent(null);
		outboundCtx.setSID(inboundCtx.getSID());
		outboundCtx.setSPort(inboundCtx.getSPort());
		outboundCtx.setTID("this");
		outboundCtx.setTPort(inboundCtx.getTPort());
		outboundCtx.setTransmission("res");

	}
}
