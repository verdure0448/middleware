package com.hdbsnc.smartiot.adapter.mb.mc.bin.dynamic.handler;

import com.hdbsnc.smartiot.adapter.mb.mc.bin.dynamic.handler.manager.DynamicHandlerManager;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractTransactionTimeoutFunctionHandler;

/**
 * @author user
 * 동적생성된 핸들러의 상태를 반환한다.
 */
public class StatusDynamicHandler extends AbstractTransactionTimeoutFunctionHandler {

	public StatusDynamicHandler(String name, long timeout, DynamicHandlerManager manager) {
		super(name, timeout);
	}

	@Override
	public void transactionProcess(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		
	}

	@Override
	public void rejectionProcess(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		
	}

}
