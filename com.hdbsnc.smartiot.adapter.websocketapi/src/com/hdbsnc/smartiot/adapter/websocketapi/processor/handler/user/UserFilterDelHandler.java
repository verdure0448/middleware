package com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.user;

import com.hdbsnc.smartiot.adapter.websocketapi.constant.WebSocketAdapterConst;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractFunctionHandler;
import com.hdbsnc.smartiot.common.pm.IProfileManager;
import com.hdbsnc.smartiot.common.pm.vo.IModifyUserFilterObj;
import com.hdbsnc.smartiot.common.pm.vo.IUserObj;

public class UserFilterDelHandler extends AbstractFunctionHandler {

	private IProfileManager pm;

	public UserFilterDelHandler(IProfileManager pm) {
		super("del");
		this.pm = pm;
	}

	@Override
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		String uid = inboundCtx.getParams().get(WebSocketAdapterConst.UID);
		String authFilter = inboundCtx.getParams().get(WebSocketAdapterConst.AUTHORITY_FILTER);

		
		IUserObj iUserObj = pm.getUserObj(uid);
		if(iUserObj != null && WebSocketAdapterConst.DEFAULT_ADMIN.equals(iUserObj.getUserType())){
			throw getCommonService().getExceptionfactory().createAppException(
					this.getClass().getName() + ":010");
		}
		
		//////////////////////////////////////////////////////////////////////
		// 유저필터 삭제
		//////////////////////////////////////////////////////////////////////
		IModifyUserFilterObj mUserFilterObj = pm.getModifyUserFilterObj();
		try {
			mUserFilterObj.userId(uid).authFilter(authFilter).delete();
		} catch (Exception e) {
			// throw new ContextHandlerApplicationException(325,
			// CommonException.TYPE_ERROR, "유저필터 삭제에 실패 했습니다.", e);
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":005", new String[]{uid, authFilter},
					e);
		}

		outboundCtx.getPaths().add("ack");
		outboundCtx.setSID(inboundCtx.getSID());
		outboundCtx.setSPort(inboundCtx.getSPort());
		outboundCtx.setTID("this");
		outboundCtx.setTPort(inboundCtx.getTPort());
		outboundCtx.setTransmission("res");

	}
}
