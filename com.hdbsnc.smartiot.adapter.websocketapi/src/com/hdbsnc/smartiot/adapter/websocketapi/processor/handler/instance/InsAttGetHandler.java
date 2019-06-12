package com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.instance;

import java.nio.ByteBuffer;

import org.json.simple.JSONObject;

import com.hdbsnc.smartiot.adapter.websocketapi.constant.WebSocketAdapterConst;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractFunctionHandler;
import com.hdbsnc.smartiot.common.pm.IProfileManager;
import com.hdbsnc.smartiot.common.pm.vo.IInstanceAttributeObj;

/**
 * ins/att/get 속성 정보 조회
 * 
 * @author KANG
 *
 */
public class InsAttGetHandler extends AbstractFunctionHandler {

	private IProfileManager pm;

	public InsAttGetHandler(IProfileManager pm) {
		super("get");
		this.pm = pm;

	}

	@Override
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		String iid = inboundCtx.getParams().get(WebSocketAdapterConst.IID);
		String attKey = inboundCtx.getParams().get(WebSocketAdapterConst.ATT_KEY);

		IInstanceAttributeObj insAttObj = pm.getInstanceAttributeObj(iid, attKey);

		// if(insAttObj == null) throw new
		// ContextHandlerApplicationException(2001, CommonException.TYPE_INFO,
		// "데이터가 존재하지 않습니다.");
		if (insAttObj == null)
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":005", new String[]{iid, attKey});

		JSONObject json = new JSONObject();

		json.put(WebSocketAdapterConst.IID, insAttObj.getInsId());
		json.put(WebSocketAdapterConst.ATT_KEY, insAttObj.getKey());
		json.put(WebSocketAdapterConst.ATT_DESCRIPTION, insAttObj.getDsct());
		json.put(WebSocketAdapterConst.ATT_VALUE, insAttObj.getValue());
		json.put(WebSocketAdapterConst.ATT_VALUE_TYPE, insAttObj.getValueType());
		json.put(WebSocketAdapterConst.REMARK, insAttObj.getRemark());
		json.put(WebSocketAdapterConst.ALTER_DATE, insAttObj.getAlterDate());
		json.put(WebSocketAdapterConst.REG_DATE, insAttObj.getRegDate());

		outboundCtx.getPaths().add("ack");
		outboundCtx.setSID(inboundCtx.getSID());
		outboundCtx.setSPort(inboundCtx.getSPort());
		outboundCtx.setTID("this");
		outboundCtx.setTPort(inboundCtx.getTPort());
		outboundCtx.setTransmission("res");
		outboundCtx.setContenttype("json");
		outboundCtx.setContent(ByteBuffer.wrap(json.toJSONString().getBytes()));

	}
}
