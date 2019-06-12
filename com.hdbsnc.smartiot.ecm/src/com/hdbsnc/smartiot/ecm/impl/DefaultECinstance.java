package com.hdbsnc.smartiot.ecm.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.hdbsnc.smartiot.common.ecm.IEventProcessor;
import com.hdbsnc.smartiot.common.ecm.ec.IEClifeCycleEvent;
import com.hdbsnc.smartiot.common.ecm.ec.IEClifeCycleHandler;
import com.hdbsnc.smartiot.common.ecm.ec.IEventContext;
import com.hdbsnc.smartiot.common.ecm.ec.IEventContextInstance;
import com.hdbsnc.smartiot.common.ecm.eh.IEventHandler;
import com.hdbsnc.smartiot.common.ecm.profile.IEventContextProfile;
import com.hdbsnc.smartiot.common.ecm.profile.IEventHandlerProfile;
import com.hdbsnc.smartiot.common.em.IAdapterProcessorEventConsumer;
import com.hdbsnc.smartiot.common.em.IEventManager;
import com.hdbsnc.smartiot.ecm.impl.DefaultEC.EClifeCycleListenerCondition;
import com.hdbsnc.smartiot.util.logger.Log;
import com.hdbsnc.smartiot.util.servicepool.AlreadyClosedException;
import com.hdbsnc.smartiot.util.servicepool.ServicePool;

public class DefaultECinstance implements IEventContextInstance{

	//private IEventContextInstance eci;
	private DefaultEC dec;
	private EHRegistry ehReg;
	private DefaultEHinstanceContainer root = null;
	private Log log;
	private IEventManager em;
	private ServicePool pool;
	private IEClifeCycleEvent lastLifeCycleEvent = null;
	
	public DefaultECinstance(DefaultEC dec, EHRegistry ehReg, IEventManager em){
		this.dec = dec;
		//this.eci = dec.getInstance();
		this.ehReg = ehReg;
		this.log = dec.getCommonService().getLogger().logger(dec.getEID());
		this.em = em;
		this.pool = dec.getCommonService().getServicePool();
	}
	
	DefaultEC getDefaultEC(){
		return dec;
	}
	
	DefaultEHinstanceContainer getRoot(){
		return root;
	}
	
	public void initialize() throws Exception{
		//핸들러 조립 
		IEventContextProfile ecp = dec.getProfile();
		List<IEventHandlerProfile> ehpList = ecp.getEventHandlerProfileList();
		DefaultEHinstanceContainer current = null;
		DefaultEHinstanceContainer tempEHIC;
		IEventHandlerProfile ehp;
		IEventHandler eh;
		for(int i=0,s= ehpList.size();i<s;i++){
			ehp = ehpList.get(i);
			eh = ehReg.getEventHandler(ehp.getEHID());
			if(eh==null) throw new Exception();
			tempEHIC = new DefaultEHinstanceContainer(eh);
			if(current==null){
				root = current = tempEHIC;
			}else{
				current.setNext(tempEHIC);
				current = tempEHIC;
			}
		}
		root.initialize(dec);
	}
	
	private List<IAdapterProcessorEventConsumer> consumerList = null;
	public void start() throws Exception{
		log.info("start");
		root.start(dec);
		//컨슈머 등록
		if(consumerList==null) consumerList = new ArrayList();
		IEventContextProfile ecp = dec.getProfile();
		List<String> patternList = ecp.getPatternStringList();
		IAdapterProcessorEventConsumer consumer;
		for(String rExp: patternList){
			consumer = new DefaultECadapterProcessorEventConsumer(dec.getEID()+":"+rExp, this);
			em.addAdapterProcessorEventConsumer(consumer, rExp);
			consumerList.add(consumer);
		}
		
	}
	
	
	public void stop() throws Exception{
		log.info("stop");
		root.stop(dec);
		//컨슈머 등록 해제 
		if(consumerList!=null){
			for(IAdapterProcessorEventConsumer consumer: consumerList){
				em.removeAdapterProcessorEventConsumer(consumer);
			}
			consumerList.clear();
		}
		
	}
	
	public void resume() throws Exception{
		log.info("resume");
		root.resume(dec);
		//일시정지 
		
	}
	
	public void suspend() throws Exception{
		log.info("suspend");
		root.suspend(dec);
		//일시정지 해제 
		
	}
	
	public void dispose() throws Exception{
		log.info("dispose");
		root.dispose(dec);
		//모든 리소스 해제 
		this.consumerList.clear();
		
		
	}
	
	void onChangeInstance(int evetType){
		//상태 변경. ECM에서 호출. 즉외부에서 호출함. 
		try {
			pool.execute(new Executor(evetType, this));
		} catch (AlreadyClosedException e) {
			log.err(e);
		}
		
	}
	
	private void updateLifeCycle(int eventType, int eventState, Exception e){
		//리스너에 전파
		this.lastLifeCycleEvent = createEvent(eventType, eventState, e);
		List<EClifeCycleListenerCondition> coList = dec.getListenerConditionList();
		EClifeCycleListenerCondition con;
		Iterator<EClifeCycleListenerCondition> iter = coList.iterator();
		while(iter.hasNext()){
			con = iter.next();
			if((eventType&con.eventType)==con.eventType && (eventState&con.eventState)==con.eventState){
				con.listener.onChangeLifeCycle(lastLifeCycleEvent);
				iter.remove();
			}
		}
	}
	
	private IEClifeCycleEvent createEvent(int eventType, int eventState, Exception e){
		IEClifeCycleEvent evt = new DefaultEClifeCycleEvent(this.dec, eventType, eventState, e);
		return evt;
	}
	
	private class Executor implements Runnable{
		private int eventType;
		private IEventContextInstance eci;
		
		private Executor(int eventType, IEventContextInstance eci){
			this.eventType = eventType;
			this.eci = eci;
		}
		
		@Override
		public void run() {
			synchronized(eci){
				try{
					updateLifeCycle(eventType, IEClifeCycleEvent.STATE_BEGIN, null);
					
					//preHandler 실행. 
					List<IEClifeCycleHandler> preHandlerList = dec.getEClifeCyclePreHandlerList();
					if(preHandlerList!=null){
						for(IEClifeCycleHandler handler: preHandlerList){
							handler.handle(dec);
						}
					}
					String msg;
					if(lastLifeCycleEvent==null){
						msg = "NULL";
					}else{
						msg = String.valueOf(lastLifeCycleEvent.getLifeCycleEventType());
					}
					//인스턴스 상태 변경 실행.
					updateLifeCycle(eventType, IEClifeCycleEvent.STATE_DOING, null);
					switch(eventType){
					case IEClifeCycleEvent.TYPE_INITIALIZE:
						initialize();
						break;
					case IEClifeCycleEvent.TYPE_START:
						if(lastLifeCycleEvent!=null || lastLifeCycleEvent.getLifeCycleEventType()==IEClifeCycleEvent.TYPE_INITIALIZE){
							start();
						}else{
							throw new Exception("EClifeCycleEvent START ERROR.(preEventType:"+msg);
						}
						break;
					case IEClifeCycleEvent.TYPE_STOP:
						stop();
						break;
					case IEClifeCycleEvent.TYPE_SUSPEND:
						if(lastLifeCycleEvent!=null || lastLifeCycleEvent.getLifeCycleEventType()==IEClifeCycleEvent.TYPE_START){
							suspend();
						}else{
							throw new Exception("EClifeCycleEvent SUSPEND ERROR.(preEventType:"+msg);
						}
						break;
					case IEClifeCycleEvent.TYPE_RESUME:
						if(lastLifeCycleEvent!=null || lastLifeCycleEvent.getLifeCycleEventType()==IEClifeCycleEvent.TYPE_SUSPEND){
							resume();
						}else{
							throw new Exception("EClifeCycleEvent RESUME ERROR.(preEventType:"+msg);
						}
						break;
					case IEClifeCycleEvent.TYPE_DISPOSE:
						if(lastLifeCycleEvent!=null || lastLifeCycleEvent.getLifeCycleEventType()==IEClifeCycleEvent.TYPE_STOP){
							dispose();
						}else{
							throw new Exception("EClifeCycleEvent DISPOSE ERROR.(preEventType:"+msg);
						}
						break;
					default:
						throw new Exception("EClifeCycleEvent UNKNOWN TYPE ERRROR.");
					}
					updateLifeCycle(eventType, IEClifeCycleEvent.STATE_COMPLETED, null);
					//afterHandler 실행.
					List<IEClifeCycleHandler> afterHandlerList = dec.getEClifeCycleAfterHandlerList();
					if(afterHandlerList!=null){
						for(IEClifeCycleHandler handler: afterHandlerList){
							handler.handle(dec);
						}
					}
					
					updateLifeCycle(eventType, IEClifeCycleEvent.STATE_END, null);
				}catch(Exception e){
					updateLifeCycle(eventType, IEClifeCycleEvent.STATE_ERROR, e);
				}
			}
		}
		
	}

	@Override
	public IEventProcessor getProcessor() {
		return this.root;
	}

	@Override
	public IEventContext getEventContext() {
		return this.dec;
	}
}
