package com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.device;

import com.hdbsnc.smartiot.adapter.websocketapi.constant.WebSocketAdapterConst;
import com.hdbsnc.smartiot.adapter.websocketapi.event.consumer.DeviceMsgAdapterProcessorEventConsumer;
import com.hdbsnc.smartiot.common.connection.IConnection;
import com.hdbsnc.smartiot.common.connection.IConnectionManager;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractFunctionHandler;
import com.hdbsnc.smartiot.common.em.IEventManager;
import com.hdbsnc.smartiot.common.ism.sm.ISession;
import com.hdbsnc.smartiot.common.ism.sm.ISessionManager;
import com.hdbsnc.smartiot.common.pm.IProfileManager;

/**
 * event/dmsg/start
 * 
 * @author KANG
 *
 */
public class DeviceMsgStartEventHandler extends AbstractFunctionHandler {

	private IEventManager em;
	private IConnectionManager cm;
	private IProfileManager pm;
	private ISessionManager sm;
	public DeviceMsgStartEventHandler(IConnectionManager cm, IEventManager em, IProfileManager pm, ISessionManager sm) {
		super("start");
		this.cm = cm;
		this.em = em;
		this.pm = pm;
		this.sm = sm;
	}


	@Override
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		String did = inboundCtx.getParams().get(WebSocketAdapterConst.DID);
		String evtId = inboundCtx.getParams().get(WebSocketAdapterConst.EVENT_ID);//deviceId
		evtId = inboundCtx.getSID()+":"+evtId;
		IConnection con = cm.getConnection(inboundCtx.getSID());
		String iid = pm.searchInstanceByDevId(did).getInsId();
		
		
		// 이벤트 등록
//		em.addEventConsumer(new DeviceMsgEventConsumer(inboundCtx, con, getCommonService(), session.getAdapterInstanceId(), did, evtId, pm), "1.4.0.0");
		/**
		 * AdapterProcessorEventConsumer는 필터로 장치식별자의 정규식을 받는다.
		 * 정규식 규칙에 매칭되는 장치식별자들은 모두 이 컨슈머로 들어오게 된다.
		 * 
		 * 기존 EventConsumer는 모듈간의 상태변화를 체크하는 용도로 사용한다.
		 * 
		 * 현재는 정규식 필터 값에 did가 들어 가있으므로 해당장치아이디와 일치하는 정보만 소비 됨.
		 */
		String regularExpr = did+".*"; // did이하 모든 패스는 처리된다는 뜻임.
		
		em.addAdapterProcessorEventConsumer(new DeviceMsgAdapterProcessorEventConsumer(inboundCtx, con, getCommonService(), iid, evtId, pm), regularExpr);
		
		outboundCtx.getPaths().add("ack");
		outboundCtx.setSID(inboundCtx.getSID());
		outboundCtx.setSPort(inboundCtx.getSPort());
		outboundCtx.setTID("this");
		outboundCtx.setTPort(inboundCtx.getTPort());
		outboundCtx.setTransmission("res");
	}

}
