package com.hdbsnc.smartiot.adapter.mb.mc.bin.processor.handler;

import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractTransactionTimeoutFunctionHandler;

/**
 * @author dbkim
 * 멜섹PLC로 부터 폴링하여 데이터를 수집한다. 
 */
public class ReadPollingProcessHandler extends AbstractTransactionTimeoutFunctionHandler {

	public ReadPollingProcessHandler(String name, long timeout) {
		super(name, timeout);
	}
	@Override
	public void transactionProcess(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {

		////////////////////////////////////////////////////////////////////////////////////
		// 데이터 수집 
		////////////////////////////////////////////////////////////////////////////////////
		read = melsecAPI.read();
		
		
		////////////////////////////////////////////////////////////////////////////////////
		//PubHandler 호출
		////////////////////////////////////////////////////////////////////////////////////
		Aim.handovers(sucessJsonResult);
		
	}

	@Override
	public void rejectionProcess(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		//
	}
}
