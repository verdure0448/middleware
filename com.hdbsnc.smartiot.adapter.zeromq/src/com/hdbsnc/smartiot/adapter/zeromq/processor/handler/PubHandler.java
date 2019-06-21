package com.hdbsnc.smartiot.adapter.zeromq.processor.handler;

import com.hdbsnc.smartiot.adapter.zeromq.api.ZeromqApi;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractTransactionTimeoutFunctionHandler;

/**
 * @author dbkim
 *
 */
public class PubHandler extends AbstractTransactionTimeoutFunctionHandler {

	public PubHandler(String name, long timeout, ZeromqApi zmqApi) {
		super(name, timeout);
	}
	@Override
	public void transactionProcess(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		
		////////////////////////////////////////////////////////////////////////////////////
		// 정해진 포트 및 기본정보를 통하여 PUB으로 데이터 전송
		////////////////////////////////////////////////////////////////////////////////////
		
	}

	@Override
	public void rejectionProcess(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		// TODO Auto-generated method stub
		
	} 
}
