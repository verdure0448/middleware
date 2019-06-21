package com.hdbsnc.smartiot.adapter.zeromq.processor.handler;


import com.hdbsnc.smartiot.adapter.zeromq.api.ZeromqApi;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractTransactionTimeoutFunctionHandler;

/**
 * ZMQ의 REP의 Request에 대한 Response처리를 위한 핸들러
 * 
 * @author admin
 *
 */
public class ResHandler extends AbstractTransactionTimeoutFunctionHandler {

	public ResHandler(String name, long timeout, ZeromqApi zmqApi) {
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