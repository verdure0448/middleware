package com.hdbsnc.smartiot.adapter.mb.mc.bin.processor.handler;

import java.util.List;

import com.hdbsnc.smartiot.adapter.mb.mc.bin.api.MitsubishiQSeriesApi;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.obj.ReadPlcVo;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractTransactionTimeoutFunctionHandler;
import com.hdbsnc.smartiot.util.logger.Log;

/**
 * @author dbkim
 * Mc 프로토콜 3E프레임 블록일괄 읽기를 실행 할 수 있는 핸들러
 */
public class ReadBlockBatchProcessHandler extends AbstractTransactionTimeoutFunctionHandler{

	public ReadBlockBatchProcessHandler(String name, long timeout, MitsubishiQSeriesApi api, List<ReadPlcVo> list, Log _log) {
		super(name, timeout);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void transactionProcess(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rejectionProcess(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		// TODO Auto-generated method stub
		
	}

}