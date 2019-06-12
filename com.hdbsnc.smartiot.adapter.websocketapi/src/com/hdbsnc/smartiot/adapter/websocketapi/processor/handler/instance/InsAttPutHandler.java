package com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.instance;

import java.nio.charset.StandardCharsets;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.hdbsnc.smartiot.adapter.websocketapi.constant.WebSocketAdapterConst;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractFunctionHandler;
import com.hdbsnc.smartiot.common.pm.IProfileManager;
import com.hdbsnc.smartiot.common.pm.vo.IInstanceAttributeObj;
import com.hdbsnc.smartiot.common.pm.vo.IModifyInstanceAttributeObj;

/**
 * ins/att/put
 * 
 * @author KANG
 *
 */
public class InsAttPutHandler extends AbstractFunctionHandler {

	private IProfileManager pm;

	public InsAttPutHandler(IProfileManager pm) {
		super("put");
		this.pm = pm;
	}

	@Override
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {

		String iid = inboundCtx.getParams().get(WebSocketAdapterConst.IID);
		String attKey = inboundCtx.getParams().get(WebSocketAdapterConst.ATT_KEY);

		JSONObject inputJson = null;

		try {
			inputJson = (JSONObject) (new JSONParser())
					.parse(new String(inboundCtx.getContent().array(), StandardCharsets.UTF_8));
		} catch (Exception e) {
			// throw new ContextHandlerApplicationException(1005,
			// CommonException.TYPE_WARNNING, "컨텐츠 데이터 형식이 부정합이 있습니다.");
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":005", null,
					e);
		}
		
		IModifyInstanceAttributeObj mObj = pm.getModifyInstanceAttributeObj();

		// 인스턴스ID
		mObj.insId(iid);
		// 속성키
		mObj.key(attKey);
		// 속성이름
		mObj.dsct((String) inputJson.get(WebSocketAdapterConst.ATT_DESCRIPTION));
		// 속성값
		mObj.value((String) inputJson.get(WebSocketAdapterConst.ATT_VALUE));
		// 속성타입
		mObj.valueType((String) inputJson.get(WebSocketAdapterConst.ATT_VALUE_TYPE));
		// 비고
		mObj.remark((String) inputJson.get(WebSocketAdapterConst.REMARK));

		//////////////////////////////////////////////////////////////////////
		// 등록 인스턴스 속성이 중복일 경우 에러 응답
		//////////////////////////////////////////////////////////////////////
		mObj.select();
		IInstanceAttributeObj iInsAttObj = mObj.getResultVo();
		if (iInsAttObj != null)
			// throw new ContextHandlerApplicationException(1014,
			// CommonException.TYPE_WARNNING, "인스턴스속성이 중복입니다");
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":010", new String[]{iid, attKey}, null);
		// insert
		try {
			mObj.insert();
		} catch (Exception ex) {
			// throw new ContextHandlerApplicationException(317,
			// CommonException.TYPE_ERROR, "인스턴스 속성 등록에 실패 했습니다.", ex);
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":015", new String[]{iid, attKey}, ex);
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
