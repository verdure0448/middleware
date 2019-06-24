package com.hdbsnc.smartiot.adapter.mb.mc.bin.dynamic.handler;

import com.hdbsnc.smartiot.adapter.mb.mc.bin.dynamic.handler.manager.DeleteHandler;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.dynamic.handler.manager.StatusHandler;
import com.hdbsnc.smartiot.common.aim.IAdapterInstanceManager;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractTransactionTimeoutFunctionHandler;
import com.hdbsnc.smartiot.util.logger.Log;

/**
 * @author user
 * 동적생성된 핸들러의 상태를 반환한다.
 */
public class StatusDynamicHandler extends AbstractTransactionTimeoutFunctionHandler {

	private StatusHandler _manager;
	private IAdapterInstanceManager _aim;
	private Log _log;
	
	public StatusDynamicHandler(String name, long timeout, StatusHandler manager, IAdapterInstanceManager aim, Log log) {
		super(name, timeout);
		
		_manager = manager;
		_aim = aim;
		_log = log.logger(this.getClass());
		
		System.out.println("STATUS DYNAMIC HANDLER");
	}

	@Override
	public void transactionProcess(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		
	}

	@Override
	public void rejectionProcess(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		
	}

}
