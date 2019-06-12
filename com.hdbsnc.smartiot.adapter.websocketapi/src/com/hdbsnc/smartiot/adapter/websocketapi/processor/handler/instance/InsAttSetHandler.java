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
 * ins/att/set
 * 
 * @author KANG
 *
 */
public class InsAttSetHandler extends AbstractFunctionHandler {

	private IProfileManager pm;

	public InsAttSetHandler(IProfileManager pm) {
		super("set");
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
			// CommonException.TYPE_WARNNING, "컨텐츠 데이터 형식이 부정합이 있습니다.", e);
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
		// 변경 인스턴스 속성이 존재하지 않을 경우 에러 응답
		//////////////////////////////////////////////////////////////////////
		mObj.select();
		IInstanceAttributeObj iInsAttObj = mObj.getResultVo();
		// if (iInsAttObj == null) throw new
		// ContextHandlerApplicationException(1015,
		// CommonException.TYPE_WARNNING, "변경할 인스턴스 속성 정보가 없습니다.");
		if (iInsAttObj == null)
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":010", new String[]{iid, attKey});

		// update
		try {
			mObj.update();
		} catch (Exception ex) {
			// throw new ContextHandlerApplicationException(318,
			// CommonException.TYPE_WARNNING, "인스턴스 속성 변경에 실패 했습니다.",
			// ex);
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":015", new String[]{iid, attKey}, ex);
		}

		outboundCtx.getPaths().add("ack");
		outboundCtx.setSID(inboundCtx.getSID());
		outboundCtx.setSPort(inboundCtx.getSPort());
		outboundCtx.setTID("this");
		outboundCtx.setTPort(inboundCtx.getTPort());
		outboundCtx.setTransmission("res");

	}
}
