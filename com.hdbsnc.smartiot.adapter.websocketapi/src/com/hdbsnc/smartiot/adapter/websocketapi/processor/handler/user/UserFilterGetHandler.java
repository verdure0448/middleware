package com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.user;

import java.nio.ByteBuffer;

import org.json.simple.JSONObject;

import com.hdbsnc.smartiot.adapter.websocketapi.constant.WebSocketAdapterConst;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractFunctionHandler;
import com.hdbsnc.smartiot.common.pm.IProfileManager;
import com.hdbsnc.smartiot.common.pm.vo.IUserFilterObj;

/**
 * user/filter/get
 * 
 * @author KANG
 *
 */
public class UserFilterGetHandler extends AbstractFunctionHandler {

	private IProfileManager pm;

	public UserFilterGetHandler(IProfileManager pm) {
		super("get");
		this.pm = pm;
	}

	@Override
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		String uid = inboundCtx.getParams().get(WebSocketAdapterConst.UID);
		String authFilter = inboundCtx.getParams().get(WebSocketAdapterConst.AUTHORITY_FILTER);

		IUserFilterObj iUserFilter = pm.getUserFilterObj(uid, authFilter);

		// if (iUserFilter == null) throw new
		// ContextHandlerApplicationException(2001, CommonException.TYPE_INFO,
		// "데이터가 존재하지 않습니다.");
		if (iUserFilter == null)
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":005", new String[]{uid, authFilter});

		JSONObject json = new JSONObject();

		// 유저ID
		json.put(WebSocketAdapterConst.UID, iUserFilter.getUserId());
		// 권한필터
		json.put(WebSocketAdapterConst.AUTHORITY_FILTER, iUserFilter.getAuthFilter());
		// 비고
		json.put(WebSocketAdapterConst.REMARK, iUserFilter.getRemark());
		// 변경일시
		json.put(WebSocketAdapterConst.ALTER_DATE, iUserFilter.getAlterDate());
		// 등록일시
		json.put(WebSocketAdapterConst.REG_DATE, iUserFilter.getRegDate());

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
