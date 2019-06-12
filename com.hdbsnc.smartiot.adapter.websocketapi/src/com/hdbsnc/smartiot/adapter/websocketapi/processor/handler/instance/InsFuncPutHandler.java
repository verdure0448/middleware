package com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.instance;

import java.nio.charset.StandardCharsets;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.hdbsnc.smartiot.adapter.websocketapi.constant.WebSocketAdapterConst;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractFunctionHandler;
import com.hdbsnc.smartiot.common.pm.IProfileManager;
import com.hdbsnc.smartiot.common.pm.vo.IInstanceFunctionObj;
import com.hdbsnc.smartiot.common.pm.vo.IModifyInstanceFunctionObj;

/**
 * ins/func/put 인스턴스 기능 등록
 * 
 * @author KANG
 *
 */
public class InsFuncPutHandler extends AbstractFunctionHandler {

	private IProfileManager pm;

	public InsFuncPutHandler(IProfileManager pm) {
		super("put");
		this.pm = pm;
	}

	@Override
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		String iid = inboundCtx.getParams().get(WebSocketAdapterConst.IID);
		String funcKey = inboundCtx.getParams().get(WebSocketAdapterConst.FUNC_KEY);

		JSONObject inputJson = null;

		try {
			inputJson = (JSONObject) (new JSONParser())
					.parse(new String(inboundCtx.getContent().array(), StandardCharsets.UTF_8));
		} catch (Exception e) {
			// throw new ContextHandlerApplicationException(1005,
			// CommonException.TYPE_WARNNING, "컨텐츠 데이터 형식이 부정합이 있습니다.", e);
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":005");
		} 
		IModifyInstanceFunctionObj mObj = pm.getModifyInstanceFunctionObj();

		// 인스턴스ID
		mObj.insId(iid);
		// 기능키
		mObj.key(funcKey);
		// 기능이름
		mObj.dsct((String) inputJson.get(WebSocketAdapterConst.FUNC_DESCRIPTION));
		// 컨텐츠 타입
		mObj.contType((String) inputJson.get(WebSocketAdapterConst.CONTENT_TYPE));
		// 파라미터1
		mObj.param1((String) inputJson.get(WebSocketAdapterConst.PARAM_1));
		// 파라미터2
		mObj.param2((String) inputJson.get(WebSocketAdapterConst.PARAM_2));
		// 파라미터3
		mObj.param3((String) inputJson.get(WebSocketAdapterConst.PARAM_3));
		// 파라미터4
		mObj.param4((String) inputJson.get(WebSocketAdapterConst.PARAM_4));
		// 파라미터5
		mObj.param5((String) inputJson.get(WebSocketAdapterConst.PARAM_5));
		// 파라미터타입1
		mObj.paramType1((String) inputJson.get(WebSocketAdapterConst.PARAM_TYPE1));
		// 파라미터타입2
		mObj.paramType2((String) inputJson.get(WebSocketAdapterConst.PARAM_TYPE2));
		// 파라미터타입3
		mObj.paramType3((String) inputJson.get(WebSocketAdapterConst.PARAM_TYPE3));
		// 파라미터타입4
		mObj.paramType4((String) inputJson.get(WebSocketAdapterConst.PARAM_TYPE4));
		// 파라미터타입5
		mObj.paramType5((String) inputJson.get(WebSocketAdapterConst.PARAM_TYPE5));
		// 비고
		mObj.remark((String) inputJson.get(WebSocketAdapterConst.REMARK));

		//////////////////////////////////////////////////////////////////////
		// 등록 인스턴스 속성이 중복일 경우 에러 응답
		//////////////////////////////////////////////////////////////////////
		mObj.select();
		IInstanceFunctionObj iInsFuncObj = mObj.getResultVo();
		if (iInsFuncObj != null) {
			// throw new ContextHandlerApplicationException(1016,
			// CommonException.TYPE_INFO, "인스턴스 기능 정보가 존재합니다.");
			throw getCommonService().getExceptionfactory().createAppException(
					this.getClass().getName() + ":010", new String[]{iid, funcKey});
		}

		// insert
		try {
			mObj.insert();
		} catch (Exception ex) {
			// throw new ContextHandlerApplicationException(320,
			// CommonException.TYPE_ERROR, "데이터가 존재하지 않습니다.", ex);
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":015", new String[]{iid, funcKey},
					ex);
		}

		outboundCtx.getPaths().add("ack");
		outboundCtx.setSID(inboundCtx.getSID());
		outboundCtx.setContenttype(null);
		outboundCtx.setContent(null);
		outboundCtx.setSPort(inboundCtx.getSPort());
		outboundCtx.setTID("this");
		outboundCtx.setTPort(inboundCtx.getTPort());
		outboundCtx.setTransmission("res");

	}
}
