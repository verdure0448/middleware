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
import com.hdbsnc.smartiot.common.pm.vo.IUserObj;

/**
 * user/serach/by-upid
 * 
 * @author KANG
 *
 */
public class UserSearchByUpid extends AbstractFunctionHandler {

	private IProfileManager pm;

	public UserSearchByUpid(IProfileManager pm) {
		super("by-upid");
		this.pm = pm;
	}

	@Override
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		String upid = inboundCtx.getParams().get(WebSocketAdapterConst.UPID);

		List<IUserObj> iUserList = pm.searchUserByUserPoolId(upid);

		// if (iUserList == null || iUserList.size() == 0) throw new
		// ContextHandlerApplicationException(2001, CommonException.TYPE_INFO,
		// "데이터가 존재하지 않습니다.");
		if (iUserList == null || iUserList.size() == 0)
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":005", new String[]{upid});

		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject = null;

		for (IUserObj iUserObj : iUserList) {

			jsonObject = new JSONObject();
			// 유저ID
			jsonObject.put(WebSocketAdapterConst.UID, iUserObj.getUserId());
			// 유저풀ID
			jsonObject.put(WebSocketAdapterConst.UPID, iUserObj.getUserPoolId());
			// 유저암호
			jsonObject.put(WebSocketAdapterConst.USER_PASSWORD, iUserObj.getUserPw());
			// 유저구분
			jsonObject.put(WebSocketAdapterConst.USER_TYPE, iUserObj.getUserType());
			// 유저명
			jsonObject.put(WebSocketAdapterConst.USER_NAME, iUserObj.getUserNm());
			// 회사명
			jsonObject.put(WebSocketAdapterConst.COMPANY_NAME, iUserObj.getCompNm());
			// 부서명
			jsonObject.put(WebSocketAdapterConst.DEPARTMENT_NAME, iUserObj.getDeptNm());
			// 직책
			jsonObject.put(WebSocketAdapterConst.JOB_TITLE, iUserObj.getTitleNm());
			// 비고
			jsonObject.put(WebSocketAdapterConst.REMARK, iUserObj.getRemark());
			// 변경일시
			jsonObject.put(WebSocketAdapterConst.ALTER_DATE, iUserObj.getAlterDate());
			// 등록일시
			jsonObject.put(WebSocketAdapterConst.REG_DATE, iUserObj.getRegDate());

			jsonArray.add(jsonObject);
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
