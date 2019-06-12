package com.hdbsnc.smartiot.adapter.websocketapi.expansion.plc;

import com.hdbsnc.smartiot.adapter.websocketapi.constant.WebSocketAdapterConst;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractFunctionHandler;
import com.hdbsnc.smartiot.common.em.IEventManager;

/**
 * plc/gathering/stop
 * 
 * @author KANG
 *
 */
public class PlcGatheringStopEventHandler extends AbstractFunctionHandler {

	private IEventManager em;

	public PlcGatheringStopEventHandler(IEventManager em) {
		super("stop");
		this.em = em;
	}

	@Override
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		String evtId = inboundCtx.getParams().get(WebSocketAdapterConst.EVENT_ID);
		String attKey = inboundCtx.getParams().get(WebSocketAdapterConst.ATT_KEY);

		em.removePollingAdapterProcessorEvent(evtId + "/" + attKey);
		
		outboundCtx.getPaths().add("ack");
		outboundCtx.setSID(inboundCtx.getSID());
		outboundCtx.setSPort(inboundCtx.getSPort());
		outboundCtx.setTID("this");
		outboundCtx.setTPort(inboundCtx.getTPort());
		outboundCtx.setTransmission("res");
	}

}
