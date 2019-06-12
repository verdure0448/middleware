package com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.user;

import java.nio.ByteBuffer;

import org.json.simple.JSONObject;

import com.hdbsnc.smartiot.adapter.websocketapi.constant.WebSocketAdapterConst;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractFunctionHandler;
import com.hdbsnc.smartiot.common.pm.IProfileManager;
import com.hdbsnc.smartiot.common.pm.vo.IUserPoolObj;

/**
 * userpool/get
 * 
 * @author KANG
 *
 */
public class UserPoolGetHandler extends AbstractFunctionHandler {

	private IProfileManager pm;

	public UserPoolGetHandler(IProfileManager pm) {
		super("get");
		this.pm = pm;
	}

	@Override
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		String upid = inboundCtx.getParams().get(WebSocketAdapterConst.UPID);

		IUserPoolObj userPool = pm.getUserPoolObj(upid);

		// if (userPool == null) throw new
		// ContextHandlerApplicationException(2001, CommonException.TYPE_INFO,
		// "데이터가 존재하지 않습니다.");
		if (userPool == null)
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":005", new String[]{upid});

		JSONObject json = new JSONObject();

		// 유저풀ID
		json.put(WebSocketAdapterConst.UPID, userPool.getUserPoolId());
		// 유저풀명
		json.put(WebSocketAdapterConst.USER_POOL_NAME, userPool.getUserPoolNm());
		// 비고
		json.put(WebSocketAdapterConst.REMARK, userPool.getRemark());
		// 변경일시
		json.put(WebSocketAdapterConst.ALTER_DATE, userPool.getAlterDate());
		// 등록일시
		json.put(WebSocketAdapterConst.REG_DATE, userPool.getRegDate());

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
