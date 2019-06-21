package com.hdbsnc.smartiot.adapter.zeromq.processor.handler;


import com.hdbsnc.smartiot.adapter.zeromq.api.ZeromqApi;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractTransactionTimeoutFunctionHandler;

/**
 * 맬섹 핸들러에서 PLC 수집 시작/정지/조회에 대한 결과 전달(handover) -> 데이터 가공 -> ZMQ Send(REP) 처리를 위한 핸들러 
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