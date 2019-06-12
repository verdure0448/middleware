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
 * ins/func/set 인스턴스 기능 변경
 * 
 * @author KANG
 *
 */
public class InsFuncSetHandler extends AbstractFunctionHandler {

	private IProfileManager pm;

	public InsFuncSetHandler(IProfileManager pm) {
		super("set");
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
			// CommonException.TYPE_INFO, "컨텐츠 데이터 형식이 부정합이 있습니다.", e);
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":005", null,
					e);
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
		// 변경 인스턴스 기능이 존재하지 않을 경우 에러 응답
		//////////////////////////////////////////////////////////////////////
		mObj.select();
		IInstanceFunctionObj iInsFuncObj = mObj.getResultVo();
		// if(iInsFuncObj == null) throw new
		// ContextHandlerApplicationException(1017,
		// CommonException.TYPE_WARNNING, "변경할 인스턴스 기능 정보가 없습니다.");
		if (iInsFuncObj == null)
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":010");

		// update
		try {
			mObj.update();
		} catch (Exception ex) {
			// throw new ContextHandlerApplicationException(321,
			// CommonException.TYPE_ERROR, "인스턴스 기능 정보 변경에 실패 했습니다.",
			// ex);
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":015", null,
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
