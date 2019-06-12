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
import com.hdbsnc.smartiot.common.pm.vo.IInstanceAttributeObj;

/**
 * ins/att/search/by-iid
 * 
 * @author KANG
 *
 */
public class InsAttSearchByIidHandler extends AbstractFunctionHandler {

	private IProfileManager pm;

	public InsAttSearchByIidHandler(IProfileManager pm) {
		super("by-iid");
		this.pm = pm;
	}

	@Override
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		String iid = inboundCtx.getParams().get(WebSocketAdapterConst.IID);
		List<IInstanceAttributeObj> insAttList = pm.getInstanceAttributeList(iid);

		// if(insAttList == null || insAttList.size() == 0) throw new
		// ContextHandlerApplicationException(2001, CommonException.TYPE_INFO,
		// "데이터가 존재하지 않습니다.");
		if (insAttList == null || insAttList.size() == 0)
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":005");

		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObj = null;

		for (IInstanceAttributeObj iInstanceAttributeObj : insAttList) {
			jsonObj = new JSONObject();

			jsonObj.put(WebSocketAdapterConst.IID, iInstanceAttributeObj.getInsId());
			jsonObj.put(WebSocketAdapterConst.ATT_KEY, iInstanceAttributeObj.getKey());
			jsonObj.put(WebSocketAdapterConst.ATT_DESCRIPTION, iInstanceAttributeObj.getDsct());
			jsonObj.put(WebSocketAdapterConst.ATT_VALUE_TYPE, iInstanceAttributeObj.getValueType());
			jsonObj.put(WebSocketAdapterConst.ATT_VALUE, iInstanceAttributeObj.getValue());
			jsonObj.put(WebSocketAdapterConst.REMARK, iInstanceAttributeObj.getRemark());
			jsonObj.put(WebSocketAdapterConst.ALTER_DATE, iInstanceAttributeObj.getAlterDate());
			jsonObj.put(WebSocketAdapterConst.REG_DATE, iInstanceAttributeObj.getRegDate());

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
