package com.hdbsnc.smartiot.adapter.websocketapi.event.consumer;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import org.json.simple.JSONObject;
import com.hdbsnc.smartiot.adapter.websocketapi.constant.ProtocolConst;
import com.hdbsnc.smartiot.adapter.websocketapi.constant.WebSocketAdapterConst;
import com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.InnerContext;
import com.hdbsnc.smartiot.common.ICommonService;
import com.hdbsnc.smartiot.common.aim.IAdapterProcessor;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.em.IEventConsumer;
import com.hdbsnc.smartiot.common.em.event.IEvent;
import com.hdbsnc.smartiot.common.exception.CommonException;
import com.hdbsnc.smartiot.common.pm.vo.IMsgMastObj;
import com.hdbsnc.smartiot.em.impl.DefaultSystemEvent;
import com.hdbsnc.smartiot.pdm.ap.instance.AdapterProcessorEvent;

public class DeviceMsgEventConsumer2 implements IEventConsumer{
	private ICommonService comService;
	private String did;
	private String evtId;
	IContext inboundCtx;
	IAdapterProcessor processor;
	private CollectData data = null;
	private CollectWorker worker = null;
	
	public DeviceMsgEventConsumer2(IContext inboundCtx, ICommonService comService, IAdapterProcessor processor, String did, String evtId){
		this.inboundCtx = inboundCtx;
		this.comService = comService;
		this.did = did;
		this.evtId = evtId;
		this.processor = processor;
	}
	
	@Override
	public String getName() {
		return evtId;
	}
	
	@Override
	public synchronized void initialize() throws Exception{
		if(worker!=null && worker.isRunning()) worker.stop();
		worker = new CollectWorker(1000);
		worker.start();
	}
	
	@Override
	public synchronized void dispose(){
		if(worker!=null) {
			worker.stop();
		}
	}
	
	@Override
	public void updateEvent(IEvent event) throws CommonException{
		if(event instanceof AdapterProcessorEvent){
			AdapterProcessorEvent ape = (AdapterProcessorEvent) event;
			IContext iContext = ape.getContext();
			if(!did.equals(iContext.getTID())) return; //did가 다르면 무시. 
			
			switch(ape.eventValue()){
			case IEvent.EVENT_INSTANCE_LIFECYCLE:
				switch(ape.eventStateValue()){
				case IEvent.STATE_COMPLETED:
					JSONObject json = new JSONObject();
					json.put("module.name", ape.moduleName());
					json.put("event.name", ape.eventName());
					json.put("event.type", ape.eventTypeName());
					json.put("event.state", ape.eventStateName());
					json.put("otp.fullpath", iContext.getFullPath());			
					json.put(WebSocketAdapterConst.EVENT_ID, evtId);
					
					InnerContext evtContext = new InnerContext();
					
					evtContext.sid = inboundCtx.getSID();
					evtContext.tid = ProtocolConst.THIS;
					evtContext.paths = Arrays.asList("event", "dmsg", ProtocolConst.ACK);
					evtContext.params.put("module.name", ape.moduleName());
					evtContext.transmission = ProtocolConst.TRANS_EVT;
					evtContext.contentType = ProtocolConst.CONT_JSON;
					evtContext.content = ByteBuffer.wrap(json.toJSONString().getBytes());
					
					try {
						processor.process(evtContext);
					} catch (Exception e) {
						comService.getLogger().err(e);
					}
					break;
				default: //completed아닌 이벤트는 전송하지 않음. 
					break;
				}
				
				break;
			case IEvent.EVENT_INSTANCE_PROCESSOR:
				switch(ape.eventTypeValue()){
				case IEvent.TYPE_REQUEST:
				case IEvent.TYPE_RESPONSE:
				case IEvent.TYPE_EVENT:
					// eventStateValue() 로 begin/success/transfer순으로 연속으로 이벤트 전달됨. 중간에 실패시 fail 전달됨. 
					// 상기 이벤트 스테이트 중에서 success만 보낼지 전부다 보낼지 고민해야 함.
					switch(ape.eventStateValue()){
					case IEvent.STATE_BEGIN:
					case IEvent.STATE_TRANSFER:
						break; // success / fail만 전송함. 
					case IEvent.STATE_SUCCESS:
					case IEvent.STATE_FAIL:
						//전송할 메시지 만들어서 전송. 
						
						
						break;
					}
					break;
				}
				break;
			default:
				//이외 이벤트는 전송하지 않는다. 
				break;
			}
			
		}else if(event instanceof DefaultSystemEvent){
			DefaultSystemEvent dse = (DefaultSystemEvent) event;
			//향후 에러 유형이 추가되면 여기서 분기타서 처리해줘야 함. 
			if(dse.eventID() == (IEvent.MODULE_EM | IEvent.EVENT_EVENTCONSUMER | IEvent.TYPE_UPDATEEVENT | IEvent.STATE_FAIL)){
				IMsgMastObj msgObj = comService.getExceptionfactory().getMsgInfo("402");
				
				InnerContext evtContext = new InnerContext();
				evtContext.sid = inboundCtx.getSID();
				evtContext.tid = ProtocolConst.THIS;
				evtContext.paths = Arrays.asList("event", "dmsg", ProtocolConst.NACK);
				evtContext.params = new HashMap<String, String>();
				evtContext.params.put("code", msgObj.getOuterCode());
				evtContext.params.put("type", msgObj.getType());
				evtContext.params.put("msg", msgObj.getMsg()+"("+dse.eventCodeToUser()+")");
				evtContext.transmission = ProtocolConst.TRANS_EVT;
				
				try {
					processor.process(evtContext);
				} catch (Exception e) {
					comService.getLogger().err(e);
				}
			}
			//이외의 이벤트 유형이 여기에 떨어진다면 처리해주어야 한다.
		}		
	}
	
	private class CollectData {
		int requestCnt = 0;
		int responseCnt = 0;
		int eventCnt = 0;
		
		boolean isEmpty(){
			if(requestCnt == 0 && responseCnt == 0 && eventCnt == 0){
				return true;
			}else{
				return false;
			}
		}
		
		void clear(){
			requestCnt = 0;
			responseCnt = 0;
			eventCnt = 0;
		}
	}

	private class CollectWorker implements Runnable{
		boolean isRunning = false;
		Thread t = null;
		int interval = 1000;
		
		CollectWorker(int interval){
			this.interval = interval;
		}
		
		public synchronized void start(){
			if(t!=null){
				isRunning = false;
				t.interrupt();
				t = null;
			}
			isRunning = true;
			t = new Thread(this);
			t.start();
		}
		
		public synchronized void stop(){
			if(t!=null){
				isRunning = false;
				t.interrupt();
				t = null;
			}else{
				isRunning = false;
			}
		}
		
		public synchronized boolean isRunning(){
			return isRunning;
		}
		
		@Override
		public void run() {
			data = new CollectData();
			
			
			while(isRunning){
				
				synchronized(data){
					if(!data.isEmpty()){
						System.out.println("requestCNT: "+data.requestCnt+", responseCNT: "+data.responseCnt+", eventCNT: "+data.eventCnt);
						data.clear();
					}
				}
				
				try {
					Thread.sleep(interval);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			data = null;
		}
	}

	
}
