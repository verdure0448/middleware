package com.hdbsnc.smartiot.adapter.websocketapi.processor.handler;

import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractFunctionHandler;

public class LogoutHandler extends AbstractFunctionHandler{

	public LogoutHandler() {
		super("logout");
	}

	@Override
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		getSessionManager().disposeSession(inboundCtx.getSID());
		getCommonService().getLogger().info("세션(" + inboundCtx.getSID() + ")을 종료했습니다.");
		outboundCtx.dispose();
	}

}
