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
import com.hdbsnc.smartiot.common.pm.vo.IUserFilterObj;

/**
 * user/filter/search/by-uid
 * 
 * @author KANG
 *
 */
public class UserFilterSearchByUid extends AbstractFunctionHandler {

	private IProfileManager pm;

	public UserFilterSearchByUid(IProfileManager pm) {
		super("by-uid");
		this.pm = pm;
	}

	@Override
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		String uid = inboundCtx.getParams().get(WebSocketAdapterConst.UID);

		List<IUserFilterObj> iUserFilterList = pm.searchUserFilterByUserId(uid);

		// if (iUserFilterList == null || iUserFilterList.size() == 0) throw new
		// ContextHandlerApplicationException(2001, CommonException.TYPE_INFO,
		// "데이터가 존재하지 않습니다.");
		if (iUserFilterList == null || iUserFilterList.size() == 0)
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":005", new String[]{uid});

		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObj = null;

		for (IUserFilterObj iUserFilterObj : iUserFilterList) {
			jsonObj = new JSONObject();

			// 유저ID
			jsonObj.put(WebSocketAdapterConst.UID, iUserFilterObj.getUserId());
			// 권한필터
			jsonObj.put(WebSocketAdapterConst.AUTHORITY_FILTER, iUserFilterObj.getAuthFilter());
			// 비고
			jsonObj.put(WebSocketAdapterConst.REMARK, iUserFilterObj.getRemark());
			// 변경일시
			jsonObj.put(WebSocketAdapterConst.ALTER_DATE, iUserFilterObj.getAlterDate());
			// 등록일시
			jsonObj.put(WebSocketAdapterConst.REG_DATE, iUserFilterObj.getRegDate());

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
