package com.hdbsnc.smartiot.adapter.zeromq.processor.handler;


import com.hdbsnc.smartiot.adapter.zeromq.api.ZeromqApi;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractTransactionTimeoutFunctionHandler;

/**
 * ZMQ의 REP의 Request(수집 시작/정지/조회/일괄정지) 요청 -> 맬섹 핸들러 호출(handover)을 위한 핸들러
 * 
 * @author admin
 *
 */
public class ReqHandler extends AbstractTransactionTimeoutFunctionHandler {

	public ReqHandler(String name, long timeout, ZeromqApi zmqApi) {
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