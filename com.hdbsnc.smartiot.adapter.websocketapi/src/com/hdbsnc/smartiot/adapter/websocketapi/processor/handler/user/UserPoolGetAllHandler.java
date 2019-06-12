package com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.user;

import java.nio.ByteBuffer;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.hdbsnc.smartiot.adapter.websocketapi.constant.WebSocketAdapterConst;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractFunctionHandler;
import com.hdbsnc.smartiot.common.pm.IProfileManager;
import com.hdbsnc.smartiot.common.pm.vo.IUserPoolObj;

/**
 * userpool/get/all
 * 
 * @author KANG
 *
 */
public class UserPoolGetAllHandler extends AbstractFunctionHandler {

	private IProfileManager pm;

	public UserPoolGetAllHandler(IProfileManager pm) {
		super("all");
		this.pm = pm;
	}

	@Override
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		List<IUserPoolObj> userPoolList = pm.getAllUserPoolObj();

		// if (userPoolList == null || userPoolList.size() == 0) throw new
		// ContextHandlerApplicationException(2001, CommonException.TYPE_INFO,
		// "데이터가 존재하지 않습니다.");
		if (userPoolList == null || userPoolList.size() == 0)
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":005");

		JSONArray jsonArray = new JSONArray();

		JSONObject jsonObj = null;

		for (IUserPoolObj iUserPoolObj : userPoolList) {
			jsonObj = new JSONObject();
			// 유저풀ID
			jsonObj.put(WebSocketAdapterConst.UPID, iUserPoolObj.getUserPoolId());
			// 유저풀명
			jsonObj.put(WebSocketAdapterConst.USER_POOL_NAME, iUserPoolObj.getUserPoolNm());
			// 비고
			jsonObj.put(WebSocketAdapterConst.REMARK, iUserPoolObj.getRemark());
			// 변경일시
			jsonObj.put(WebSocketAdapterConst.ALTER_DATE, iUserPoolObj.getAlterDate());
			// 등록일시
			jsonObj.put(WebSocketAdapterConst.REG_DATE, iUserPoolObj.getRegDate());

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
