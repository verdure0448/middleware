package com.hdbsnc.smartiot.adapter.websocketapi.expansion.plc;

import java.nio.charset.StandardCharsets;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.hdbsnc.smartiot.adapter.websocketapi.constant.WebSocketAdapterConst;
import com.hdbsnc.smartiot.common.connection.IConnection;
import com.hdbsnc.smartiot.common.connection.IConnectionManager;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractFunctionHandler;
import com.hdbsnc.smartiot.common.em.IEventManager;
import com.hdbsnc.smartiot.common.pm.IProfileManager;

/**
 * plc/monitoring/start
 * 
 * @author KANG
 *
 */
public class PlcMonitoringStartEventHandler extends AbstractFunctionHandler {

//	public static List<String> eventList = new ArrayList<>();
	
	private IEventManager em;
	private IConnectionManager cm;
	private IProfileManager pm;
	public PlcMonitoringStartEventHandler(IConnectionManager cm, IEventManager em, IProfileManager pm) {
		super("start");
		this.cm = cm;
		this.em = em;
		this.pm = pm;
	}


	@Override
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		String did = inboundCtx.getParams().get(WebSocketAdapterConst.DID);
		//String evtId = inboundCtx.getParams().get(WebSocketAdapterConst.EVENT_ID);
		IConnection con = cm.getConnection(inboundCtx.getSID());
		
		// client타입 아답터 인스턴스는 디바이스풀을 공유하면 안된다. 1:1 관계여야 된다.
		// device Pool을 공유하면 아래의 코드에서 어떤 인스턴스 아이디를 가져올지 알 수 없게 된다.
		// 향후 정책 방향 결정하여 수정해야 함. 2016.05.03 hjs0317
		String iid = pm.searchInstanceByDevId(did).getInsId();
		
		JSONObject inputJson = null;

		try {
			inputJson = (JSONObject) (new JSONParser())
					.parse(new String(inboundCtx.getContent().array(), StandardCharsets.UTF_8));
		} catch (Exception e) {
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":005", null,
					e);
		}
		//속성키 취득
		JSONArray attInfoList = (JSONArray)inputJson.get("attribution.info"); 
		
		if(attInfoList == null || attInfoList.size() == 0){
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":010");
		}
		
		/**
		 * addPollingAdapterProcessorEventConsumer()는 did, path, intervalSecond를 파라미터로 받아서
		 * intervalSecond 주기대로 did/path의 속성 값을 읽어 온다.
		 * 
		 */
		String evtId;

		for(int i=0; i < attInfoList.size(); i++){
			JSONObject attInfo = (JSONObject) attInfoList.get(i);
			evtId = did + "/" + attInfo.get("attribution.key");
			 
			em.addAdapterProcessorEventConsumer(new PlcMonitoringEventConsumer(inboundCtx, con, getCommonService(), iid, evtId, pm), evtId + ".*");
		}
		
		
		outboundCtx.getPaths().add("ack");
		outboundCtx.setSID(inboundCtx.getSID());
		outboundCtx.setSPort(inboundCtx.getSPort());
		outboundCtx.setTID("this");
		outboundCtx.setTPort(inboundCtx.getTPort());
		outboundCtx.setTransmission("res");
	}

}
