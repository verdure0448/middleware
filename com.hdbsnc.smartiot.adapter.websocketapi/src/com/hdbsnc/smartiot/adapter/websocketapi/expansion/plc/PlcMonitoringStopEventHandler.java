package com.hdbsnc.smartiot.adapter.websocketapi.expansion.plc;

import com.hdbsnc.smartiot.adapter.websocketapi.constant.WebSocketAdapterConst;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractFunctionHandler;
import com.hdbsnc.smartiot.common.em.IEventManager;

/**
 * plc/monitoring/stop
 * 
 * @author KANG
 *
 */
public class PlcMonitoringStopEventHandler extends AbstractFunctionHandler {

	private IEventManager em;

	public PlcMonitoringStopEventHandler(IEventManager em) {
		super("stop");
		this.em = em;
	}

	@Override
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		String evtId = inboundCtx.getParams().get(WebSocketAdapterConst.EVENT_ID);
		String attKey = inboundCtx.getParams().get(WebSocketAdapterConst.ATT_KEY);
		// 동작 중인 ApeConsumer 리스트를 가져온다.
		// List<IEventConsumer> ecList =
		// em.getAdapterProcessorEventConsumerList();

		// 임시로 모든 이벤트 해제
		// String evtId;
		// for (IEventConsumer ec : ecList) {
		// evtId = ec.getName();
		// // 지정한 evtId에 맞는 녀석들만 제거해야함.
		// if (true) {
		// em.removeAdapterProcessorEventConsumer(evtId);
		// }
		// }
		em.removeAdapterProcessorEventConsumer(evtId + "/" + attKey);

		outboundCtx.getPaths().add("ack");
		outboundCtx.setSID(inboundCtx.getSID());
		outboundCtx.setSPort(inboundCtx.getSPort());
		outboundCtx.setTID("this");
		outboundCtx.setTPort(inboundCtx.getTPort());
		outboundCtx.setTransmission("res");
	}

}
