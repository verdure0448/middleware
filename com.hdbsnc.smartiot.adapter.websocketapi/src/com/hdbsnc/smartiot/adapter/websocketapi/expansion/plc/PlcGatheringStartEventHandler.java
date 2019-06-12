package com.hdbsnc.smartiot.adapter.websocketapi.expansion.plc;

import java.nio.charset.StandardCharsets;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.hdbsnc.smartiot.adapter.websocketapi.constant.WebSocketAdapterConst;
import com.hdbsnc.smartiot.common.aim.IAdapterInstanceContainer;
import com.hdbsnc.smartiot.common.aim.IAdapterInstanceManager;
import com.hdbsnc.smartiot.common.connection.IConnection;
import com.hdbsnc.smartiot.common.connection.IConnectionManager;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractFunctionHandler;
import com.hdbsnc.smartiot.common.em.IEventManager;
import com.hdbsnc.smartiot.common.ism.sm.ISessionManager;
import com.hdbsnc.smartiot.common.pm.IProfileManager;

/**
 * plc/gathering/start
 * 
 * @author KANG
 *
 */
public class PlcGatheringStartEventHandler extends AbstractFunctionHandler {

//	public static List<String> eventList = new ArrayList<>();
	
	private IEventManager em;
	private IConnectionManager cm;
	private IProfileManager pm;
	private IAdapterInstanceManager aim;
	public PlcGatheringStartEventHandler(IConnectionManager cm, IEventManager em, IProfileManager pm, IAdapterInstanceManager aim) {
		super("start");
		this.cm = cm;
		this.em = em;
		this.pm = pm;
		this.aim = aim;
	}


	@Override
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		String did = inboundCtx.getParams().get(WebSocketAdapterConst.DID);
		//String evtId = inboundCtx.getParams().get(WebSocketAdapterConst.EVENT_ID);
		IConnection con = cm.getConnection(inboundCtx.getSID());
		
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
		
		String requestSid = inboundCtx.getSID();
		
		IAdapterInstanceContainer aic = this.aim.getAdapterInstance(iid);
		ISessionManager sm = aic.getContext().getSessionManager();
		if(sm.getSessionCount()>0) requestSid = sm.getSessionList().get(0).getSessionKey();
		
		/**
		 * addPollingAdapterProcessorEventConsumer()는 did, path, intervalSecond를 파라미터로 받아서
		 * intervalSecond 주기대로 did/path의 속성 값을 읽어 온다.
		 * 
		 */
		String evtId, interval;
		int intervalSecond;
		for(int i=0; i < attInfoList.size(); i++){
			JSONObject attInfo = (JSONObject) attInfoList.get(i);
			evtId = did + "/" + attInfo.get("attribution.key");
			
			interval = (String) attInfo.get("gathering.period"); //인터벌값이 없어서 임시로 1초로 넣음.
			intervalSecond = Integer.parseInt(interval)*1000;
//			em.addPollingAdapterProcessorEventConsumer(
//					new PlcGatheringEventConsumer(inboundCtx, con, getCommonService(), iid, evtId, pm),
//					inboundCtx.getSID(),
//					did,
//					(String)attInfo.get("attribution.key"),
//					intervalSecond);
			em.addPollingAdapterProcessorEvent(evtId, requestSid, did, (String)attInfo.get("attribution.key"), intervalSecond);
		}
		
		  
		outboundCtx.getPaths().add("ack");
		outboundCtx.setSID(inboundCtx.getSID());
		outboundCtx.setSPort(inboundCtx.getSPort());
		outboundCtx.setTID("this");
		outboundCtx.setTPort(inboundCtx.getTPort());
		outboundCtx.setTransmission("res");
	}

}
