package com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.device;

import com.hdbsnc.smartiot.adapter.websocketapi.constant.WebSocketAdapterConst;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractFunctionHandler;
import com.hdbsnc.smartiot.common.em.IEventManager;

/**
 * event/dmsg/stop
 * 
 * @author KANG
 *
 */
public class DeviceMsgStopEventHandler extends AbstractFunctionHandler {

	private IEventManager em;

	public DeviceMsgStopEventHandler(IEventManager em) {
		super("stop");
		this.em = em;
	}

	@Override
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		String evtId = inboundCtx.getParams().get(WebSocketAdapterConst.EVENT_ID);
		evtId = inboundCtx.getSID()+":"+evtId;
		// 이벤트 해제
		em.removeEventConsumer(evtId);
		
		outboundCtx.getPaths().add("ack");
		outboundCtx.setSID(inboundCtx.getSID());
		outboundCtx.setSPort(inboundCtx.getSPort());
		outboundCtx.setTID("this");
		outboundCtx.setTPort(inboundCtx.getTPort());
		outboundCtx.setTransmission("res");
	}

}
