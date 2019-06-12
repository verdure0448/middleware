package com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.instance;

import java.nio.ByteBuffer;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.hdbsnc.smartiot.adapter.websocketapi.constant.WebSocketAdapterConst;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractFunctionHandler;
import com.hdbsnc.smartiot.common.pm.IProfileManager;
import com.hdbsnc.smartiot.common.pm.vo.IInstanceFunctionObj;

/**
 * ins/func/search/by-iid
 * 
 * @author KANG
 *
 */
public class InsFuncSearchByIidHandler extends AbstractFunctionHandler {

	private IProfileManager pm;

	public InsFuncSearchByIidHandler(IProfileManager pm) {
		super("by-iid");
		this.pm = pm;
	}

	@Override
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		String iid = inboundCtx.getParams().get(WebSocketAdapterConst.IID);
		List<IInstanceFunctionObj> insFuncList = pm.getInstanceFunctionList(iid);

		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObj = null;

		// if(insFuncList == null || insFuncList.size() == 0) throw new
		// ContextHandlerApplicationException(2001, CommonException.TYPE_INFO,
		// "데이터가 존재하지 않습니다.");
		if (insFuncList == null || insFuncList.size() == 0)
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":005", new String[]{iid});

		for (IInstanceFunctionObj iInstanceFunctionObj : insFuncList) {
			jsonObj = new JSONObject();

			jsonObj.put(WebSocketAdapterConst.IID, iInstanceFunctionObj.getInsId());
			jsonObj.put(WebSocketAdapterConst.FUNC_KEY, iInstanceFunctionObj.getKey());
			jsonObj.put(WebSocketAdapterConst.FUNC_DESCRIPTION, iInstanceFunctionObj.getDsct());
			jsonObj.put(WebSocketAdapterConst.CONTENT_TYPE, iInstanceFunctionObj.getContType());
			jsonObj.put(WebSocketAdapterConst.PARAM_1, iInstanceFunctionObj.getParam1());
			jsonObj.put(WebSocketAdapterConst.PARAM_2, iInstanceFunctionObj.getParam2());
			jsonObj.put(WebSocketAdapterConst.PARAM_3, iInstanceFunctionObj.getParam3());
			jsonObj.put(WebSocketAdapterConst.PARAM_4, iInstanceFunctionObj.getParam4());
			jsonObj.put(WebSocketAdapterConst.PARAM_5, iInstanceFunctionObj.getParam5());
			jsonObj.put(WebSocketAdapterConst.PARAM_TYPE1, iInstanceFunctionObj.getParamType1());
			jsonObj.put(WebSocketAdapterConst.PARAM_TYPE2, iInstanceFunctionObj.getParamType2());
			jsonObj.put(WebSocketAdapterConst.PARAM_TYPE3, iInstanceFunctionObj.getParamType3());
			jsonObj.put(WebSocketAdapterConst.PARAM_TYPE4, iInstanceFunctionObj.getParamType4());
			jsonObj.put(WebSocketAdapterConst.PARAM_TYPE5, iInstanceFunctionObj.getParamType5());
			jsonObj.put(WebSocketAdapterConst.REMARK, iInstanceFunctionObj.getRemark());
			jsonObj.put(WebSocketAdapterConst.ALTER_DATE, iInstanceFunctionObj.getAlterDate());
			jsonObj.put(WebSocketAdapterConst.REG_DATE, iInstanceFunctionObj.getRegDate());

			jsonArray.add(jsonObj);
		}

		outboundCtx.getPaths().add("ack");
		outboundCtx.setSID(inboundCtx.getSID());
		outboundCtx.setSPort(inboundCtx.getSPort());
		outboundCtx.setTID("this");
		outboundCtx.setTPort(inboundCtx.getTPort());
		outboundCtx.setTransmission("res");
		outboundCtx.setContenttype("json");
		outboundCtx.setContent(ByteBuffer.wrap(jsonArray.toJSONString().getBytes()));

	}

}
