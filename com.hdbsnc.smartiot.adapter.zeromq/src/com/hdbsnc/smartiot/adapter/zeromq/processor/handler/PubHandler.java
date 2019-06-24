package com.hdbsnc.smartiot.adapter.zeromq.processor.handler;

import java.nio.ByteBuffer;

import com.hdbsnc.smartiot.adapter.zeromq.api.ZeromqApi;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractTransactionTimeoutFunctionHandler;

/**
 *
 * 정정 핸들러로써
 * 멜섹 아답터에서 handover로 호출 -> 데이터 가공 -> ZMQ Send(Publish) 처리를 위한 핸들러 
 * 
 * @author dbkim
 *
 */
public class PubHandler extends AbstractTransactionTimeoutFunctionHandler {

	private ZeromqApi zmqApi = null;
	public PubHandler(String name, long timeout, ZeromqApi pZmqApi) {
		super(name, timeout);
		this.zmqApi = pZmqApi;
	}
	@Override
	public void transactionProcess(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		
		ByteBuffer content = inboundCtx.getContent();
		
		
		
		
		content.toString();
		
		////////////////////////////////////////////////////////////////////////////////////
		// PUB으로 데이터 브로드캐스팅
		////////////////////////////////////////////////////////////////////////////////////
		this.zmqApi.publish("topic".getBytes(), content.array());
		
	}

	@Override
	public void rejectionProcess(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		// TODO Auto-generated method stub
		
	} 
}
