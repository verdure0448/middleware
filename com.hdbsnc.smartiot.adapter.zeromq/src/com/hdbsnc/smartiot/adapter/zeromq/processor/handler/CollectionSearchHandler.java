package com.hdbsnc.smartiot.adapter.zeromq.processor.handler;

import com.hdbsnc.smartiot.adapter.zeromq.api.HttpApi;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractTransactionTimeoutFunctionHandler;

public class CollectionSearchHandler extends AbstractTransactionTimeoutFunctionHandler {

	public CollectionSearchHandler(String name, long timeout, HttpApi httpApi1) {
		super(name, timeout);
	}
	@Override
	public void transactionProcess(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		 
	}

	@Override
	public void rejectionProcess(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		// TODO Auto-generated method stub
		
	} 
}