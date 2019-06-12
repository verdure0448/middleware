package com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.instance;

import java.nio.ByteBuffer;

import org.json.simple.JSONObject;

import com.hdbsnc.smartiot.adapter.websocketapi.constant.WebSocketAdapterConst;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractFunctionHandler;
import com.hdbsnc.smartiot.common.pm.IProfileManager;
import com.hdbsnc.smartiot.common.pm.vo.IInstanceFunctionObj;

/**
 * ins/func/get 기능 정보 조회
 * 
 * @author KANG
 *
 */
public class InsFuncGetHandler extends AbstractFunctionHandler {

	private IProfileManager pm;

	public InsFuncGetHandler(IProfileManager pm) {
		super("get");
		this.pm = pm;
	}


	@Override
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		String iid = inboundCtx.getParams().get(WebSocketAdapterConst.IID);
		String funcKey = inboundCtx.getParams().get(WebSocketAdapterConst.FUNC_KEY);

		IInstanceFunctionObj insFuncObj = pm.getInstanceFunctionObj(iid, funcKey);

		// if(insFuncObj == null) throw new
		// ContextHandlerApplicationException(2001, CommonException.TYPE_INFO,
		// "데이터가 존재하지 않습니다.");
		if (insFuncObj == null)
			throw getCommonService().getExceptionfactory().createAppException(
					this.getClass().getName() + ":005", new String[]{iid, funcKey});

		JSONObject json = new JSONObject();

		json.put(WebSocketAdapterConst.IID, insFuncObj.getInsId());
		json.put(WebSocketAdapterConst.FUNC_KEY, insFuncObj.getKey());
		json.put(WebSocketAdapterConst.FUNC_DESCRIPTION, insFuncObj.getDsct());
		json.put(WebSocketAdapterConst.CONTENT_TYPE, insFuncObj.getContType());
		json.put(WebSocketAdapterConst.PARAM_1, insFuncObj.getParam1());
		json.put(WebSocketAdapterConst.PARAM_2, insFuncObj.getParam2());
		json.put(WebSocketAdapterConst.PARAM_3, insFuncObj.getParam3());
		json.put(WebSocketAdapterConst.PARAM_4, insFuncObj.getParam4());
		json.put(WebSocketAdapterConst.PARAM_5, insFuncObj.getParam5());
		json.put(WebSocketAdapterConst.PARAM_TYPE1, insFuncObj.getParamType1());
		json.put(WebSocketAdapterConst.PARAM_TYPE2, insFuncObj.getParamType2());
		json.put(WebSocketAdapterConst.PARAM_TYPE3, insFuncObj.getParamType3());
		json.put(WebSocketAdapterConst.PARAM_TYPE4, insFuncObj.getParamType4());
		json.put(WebSocketAdapterConst.PARAM_TYPE5, insFuncObj.getParamType5());
		json.put(WebSocketAdapterConst.REMARK, insFuncObj.getRemark());
		json.put(WebSocketAdapterConst.ALTER_DATE, insFuncObj.getAlterDate());
		json.put(WebSocketAdapterConst.REG_DATE, insFuncObj.getRegDate());

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
