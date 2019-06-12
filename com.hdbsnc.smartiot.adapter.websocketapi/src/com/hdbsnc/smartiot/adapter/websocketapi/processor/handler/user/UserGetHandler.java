package com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.user;

import java.nio.ByteBuffer;

import org.json.simple.JSONObject;

import com.hdbsnc.smartiot.adapter.websocketapi.constant.WebSocketAdapterConst;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractFunctionHandler;
import com.hdbsnc.smartiot.common.pm.IProfileManager;
import com.hdbsnc.smartiot.common.pm.vo.IUserObj;

/**
 * user/get
 * 
 * @author KANG
 *
 */
public class UserGetHandler extends AbstractFunctionHandler {

	private IProfileManager pm;

	public UserGetHandler(IProfileManager pm) {
		super("get");
		this.pm = pm;
	}

	@Override
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		String uid = inboundCtx.getParams().get(WebSocketAdapterConst.UID);

		IUserObj user = pm.getUserObj(uid);

		// if (user == null) throw new ContextHandlerApplicationException(2001,
		// CommonException.TYPE_INFO, "데이터가 존재하지 않습니다.");
		if (user == null)
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":005", new String[]{uid});

		JSONObject json = new JSONObject();

		// 유저ID
		json.put(WebSocketAdapterConst.UID, user.getUserId());
		// 유저풀ID
		json.put(WebSocketAdapterConst.UPID, user.getUserPoolId());
		// 유저암호
		json.put(WebSocketAdapterConst.USER_PASSWORD, user.getUserPw());
		// 유저구분
		json.put(WebSocketAdapterConst.USER_TYPE, user.getUserType());
		// 유저명
		json.put(WebSocketAdapterConst.USER_NAME, user.getUserNm());
		// 회사명
		json.put(WebSocketAdapterConst.COMPANY_NAME, user.getCompNm());
		// 부서명
		json.put(WebSocketAdapterConst.DEPARTMENT_NAME, user.getDeptNm());
		// 직책
		json.put(WebSocketAdapterConst.JOB_TITLE, user.getTitleNm());
		// 비고
		json.put(WebSocketAdapterConst.REMARK, user.getRemark());
		// 변경일시
		json.put(WebSocketAdapterConst.ALTER_DATE, user.getAlterDate());
		// 등록일시
		json.put(WebSocketAdapterConst.REG_DATE, user.getRegDate());

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
