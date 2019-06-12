package com.hdbsnc.smartiot.em.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.hdbsnc.smartiot.common.aim.IAdapterInstanceManager;
import com.hdbsnc.smartiot.common.context.impl.InnerContext;
import com.hdbsnc.smartiot.common.em.AbstractEventProducer;
import com.hdbsnc.smartiot.common.em.EventFilterFormatException;
import com.hdbsnc.smartiot.common.em.IAdapterProcessorEventConsumer;
import com.hdbsnc.smartiot.common.em.IEventConsumer;
import com.hdbsnc.smartiot.common.em.IEventManager;
import com.hdbsnc.smartiot.common.em.event.IEvent;
import com.hdbsnc.smartiot.common.ism.IIntegratedSessionManager;
import com.hdbsnc.smartiot.util.logger.Log;

public class Em implements IEventManager, Runnable{
	
	private List<EventConsumerContainer> consumerList;
	private List<AbstractEventProducer> producerList;
	
	private boolean isStart = false;
	private Thread workerThread;
	
	private Log log;
	private IIntegratedSessionManager ism;
	private IAdapterInstanceManager aim;
	
	public Em(Log logger, IIntegratedSessionManager ism,IAdapterInstanceManager aim){
		this.log = logger.logger("EM");
		this.ism = ism;
		this.aim = aim;
		this.consumerList = new ArrayList<EventConsumerContainer>();
		this.producerList = new ArrayList<AbstractEventProducer>();
	}
	
	@Override
	public void addEventConsumer(IEventConsumer consumer, String eventFilterString) throws EventFilterFormatException {
		EventConsumerContainer ecc = new EventConsumerContainer(consumer, eventFilterString);
		try {
			consumer.initialize();
		} catch (Exception e) {
			log.err(e);
			return;
		}
		synchronized(consumerList){
			consumerList.add(ecc);
		}
		log.info("EventConsumer("+ecc.filterString()+") added.");
	}
	
	@Override
	public void addEventConsumer(IEventConsumer consumer, int eventFilter) {
		EventConsumerContainer ecc = new EventConsumerContainer(consumer, eventFilter);
		try {
			consumer.initialize();
		} catch (Exception e) {
			log.err(e);
			return;
		}
		synchronized(consumerList){
			consumerList.add(ecc);
		}
		log.info("EventConsumer("+ecc.filterString()+") added.");
	}

	@Override
	public void removeEventConsumer(IEventConsumer consumer) {
		synchronized(consumerList){
			Iterator<EventConsumerContainer> iter = this.consumerList.iterator();
			EventConsumerContainer ecc;
			while(iter.hasNext()){
				ecc = iter.next();
				if(ecc.consumer.equals(consumer)) {
					iter.remove();
					ecc.consumer.dispose();
					log.info("EventConsumer("+ecc.consumer.getName()+":"+ecc.filterString()+") removed.");
					return;
				}
			}
		}
	}
	
	@Override
	public void removeEventConsumer(String consumerName){
		synchronized(consumerList){
			Iterator<EventConsumerContainer> iter = this.consumerList.iterator();
			EventConsumerContainer ecc;
			while(iter.hasNext()){
				ecc = iter.next();
				if(ecc.consumer.getName().equals(consumerName)) {
					iter.remove();
					ecc.consumer.dispose();
					log.info("EventConsumer("+consumerName+":"+ecc.filterString()+") removed.");
					return;
				}
			}
		}
	}

	@Override
	public void addEventProducer(AbstractEventProducer producer) {
		synchronized(producerList){
			this.producerList.add(producer);
			producer.setEm(this);
		}
		log.info("EventProducer("+producer.getModuleValue()+") added.");
	}

	@Override
	public void removeEventProducer(AbstractEventProducer producer) {
		synchronized(producerList){
			this.producerList.remove(producer);
		}
		log.info("EventProducer("+producer.getModuleValue()+") removed.");
	}

	@Override
	public void start() {
		stop();
		synchronized(this){
			this.isStart = true;
			this.workerThread = new Thread(this);
			this.workerThread.start();
		}
	}

	@Override
	public void stop() {
		synchronized(this){
			this.isStart = false;
			if(this.workerThread!=null){
				this.workerThread.interrupt();
				try {
					this.workerThread.join();
				} catch (InterruptedException e) {}
			}
			this.workerThread = null;
		}
	}
	
	public static int[] parseFilter(String filter) throws EventFilterFormatException{
		int[] results = new int[4];
		String[] div = filter.split("\\.");
		if(div.length!=4) throw new EventFilterFormatException(filter);
		try{
			for(int i=0, s=4;i<s;i++){
				results[i] = Integer.parseInt(div[i]);
			}
		}catch(NumberFormatException e){
			throw new EventFilterFormatException(filter);
		}
		return results;
	}
	
	private class EventConsumerContainer{
		IEventConsumer consumer;
		int[] filterArray;
		
		private EventConsumerContainer(IEventConsumer consumer, String filter) throws EventFilterFormatException{
			this.consumer = consumer;
			filterArray = parseFilter(filter);
		}
		
		private EventConsumerContainer(IEventConsumer consumer, int filter){
			this.consumer = consumer;
			filterArray = new int[4];
			if(filter==IEvent.ALL){
				filterArray[0] = 0;
				filterArray[1] = 0;
				filterArray[2] = 0;
				filterArray[3] = 0;
			}else{
				filterArray[0] = (filter >> 12) & 0xF;  	//module
				filterArray[1] = (filter >> 8) & 0xF; 	//event
				filterArray[2] = (filter >> 4) & 0xF;	//type
				filterArray[3] = filter & 0xF;	//state
			}
		}
		
		public String filterString(){
			StringBuilder sb = new StringBuilder();
			sb.append(filterArray[0]).append(".").append(filterArray[1]).append(".").append(filterArray[2]).append(".").append(filterArray[3]);
			return sb.toString();
		}
	}

	@Override
	public void run() {
		log.info("EventManager worker start.");
		List<IEvent> collectEvents = new ArrayList<IEvent>();
		int counter = 0;
		while(isStart){
			
			synchronized(producerList){
				AbstractEventProducer producer;
				Iterator<AbstractEventProducer> iter = producerList.iterator();
				while(iter.hasNext()){
					producer = iter.next();
					if(producer.isDisposed()) {
						iter.remove();
						continue;
					}
					if(!producer.isEmpty()){
						collectEvents.add(producer.consumeFirstEvent());
					}
				}
			}
			
			if(collectEvents.size()!=0){
				synchronized(consumerList){
					for(IEvent cEvent: collectEvents){
						counter++;
						notifyEvent(cEvent);	
					}
				}
				collectEvents.clear();
			}else{
//				System.out.println("EM: EventConsumed("+counter+") and Wait.");
				synchronized(this){
					try { this.wait(); } catch (InterruptedException e) {}
				}
//				System.out.println("EM: PushEvent and Interrupt.");
				counter = 0;
			}
		}
		log.info("EventManager worker stop.");
	}
	
	private void notifyEvent(IEvent evt){
		if(evt==null) {
			log.warn("EM notifyEvent에서 Event == null 예외 발생. EventProvider에서 동기화되지 않은 자료구조를 사용할 가능성이 높음.");
			return;
		}
		int filterValue;
		int evtValue;
		boolean isMatched = false;
		Iterator<EventConsumerContainer> iter = consumerList.iterator();
		EventConsumerContainer ecc;
		while(iter.hasNext()){
			ecc = iter.next();
			for(int i=0,s=4;i<s;i++){
				filterValue = ecc.filterArray[i];
				evtValue = evt.eventValues()[i];
				if(filterValue==0 || filterValue==evtValue){
					isMatched = true;
					continue;
				}else{
					isMatched = false;
					break;
				}
			}
			if(isMatched){
//				log.info("이벤트컨슈머("+ecc.filterString()+")에서 처리됨.(type:"+evt.eventTypeValue()+", state:"+evt.eventStateValue()+")");
				try{
					ecc.consumer.updateEvent(evt);
				}catch(Exception e){
					String exceptionName = e.getClass().getName();
					String exceptionMsg = e.getMessage();
					if(exceptionMsg==null) exceptionMsg = "No Message.";
					String consumerName = ecc.consumer.getName();
					String consumerFilter = ecc.filterString();
					log.err("Consumer(name: "+consumerName+", filter: "+consumerFilter+"), Exception(name: "+exceptionName+", msg: "+exceptionMsg+")" );
//					iter.remove();
//					log.warn("이벤트컨슈머(name: "+ecc.consumer.getName()+", filter: "+ecc.filterString()+")가 제거됨.");
					try {
						ecc.consumer.updateEvent(new DefaultSystemEvent(IEvent.MODULE_SSS | IEvent.EVENT_EVENTCONSUMER | IEvent.TYPE_UPDATEEVENT | IEvent.STATE_FAIL));
					} catch (Exception e1) {
						log.err(e1.getMessage());
					}
				}
			}
		}
	}

	public void wakeup(){
		synchronized(this){
			this.notifyAll();
		}
	}

	@Override
	public void addAdapterProcessorEventConsumer(IAdapterProcessorEventConsumer consumer, String regularExpression)
			throws EventFilterFormatException {
		Pattern p = null;
		try{
			p = Pattern.compile(regularExpression);
		}catch(PatternSyntaxException e){
			throw new EventFilterFormatException(regularExpression);
		}
		/**
		 * 이벤트 필터는 1.4.0.0
		 * PDM에서 EventProcessor관련 이벤트를 수신해서 정규식 조건에 TID가 만족한다면 이벤트를 전송한다.
		 * 
		 */
		this.addEventConsumer(new ApeConsumerContainer(ism, consumer, p), IEvent.MODULE_PDM | IEvent.EVENT_INSTANCE_PROCESSOR | IEvent.ALL | IEvent.ALL);
	}

	@Override
	public void removeAdapterProcessorEventConsumer(IAdapterProcessorEventConsumer consumer) {
		this.removeEventConsumer(consumer.getName());
	}

	@Override
	public void removeAdapterProcessorEventConsumer(String consumerName) {
		this.removeEventConsumer(consumerName);
	}

	
	
	/**
	 * 
	 * 2016 05 02 
	 * 0.9 버전을 0.6으로 합치는 과정에서 생겨난 메서드 Start
	 * 
	 */	
	Map<String, PollingWorker> pollingWorkerMap = new Hashtable();
	public void addPollingAdapterProcessorEventConsumer(IAdapterProcessorEventConsumer consumer, String sid, String tid, String pathOnly, int intervalMs) throws EventFilterFormatException{
		String exp = tid+"/" + pathOnly + ".*";
		this.addAdapterProcessorEventConsumer(consumer, exp);
		
		synchronized(pollingWorkerMap){
			String consumerName = consumer.getName();
			PollingWorker worker;
			if(pollingWorkerMap.containsKey(consumerName)){
				worker = pollingWorkerMap.get(consumerName);
				worker.stop();
			}
			worker = new PollingWorker(consumerName,sid, tid, pathOnly, null, intervalMs);
			worker.start();
			pollingWorkerMap.put(consumerName, worker);
		}
	}
	
	public void removePollingAdapterProcessorEventConsumer(String consumerName){
		this.removeEventConsumer(consumerName);
		synchronized(pollingWorkerMap){
			if(pollingWorkerMap.containsKey(consumerName)){
				PollingWorker worker = pollingWorkerMap.get(consumerName);
				worker.stop();
				pollingWorkerMap.remove(consumerName);
			}
		}
	}	
	
	@Override
	public void addPollingAdapterProcessorEvent(String evtId, String sid, String tid, String pathOnly, int intervalMs){
		synchronized(pollingWorkerMap){
			PollingWorker worker;
			if(pollingWorkerMap.containsKey(evtId)){
				worker = pollingWorkerMap.get(evtId);
				worker.stop();
			}
			worker = new PollingWorker(evtId,sid, tid, pathOnly, null, intervalMs);
			worker.start();
			pollingWorkerMap.put(evtId, worker);
		}
	}
	
	@Override
	public void addPollingAdapterProcessorEvent(String evtId, String sid, String tid, String pathOnly, Map<String, String> params, int intervalMs){
		synchronized(pollingWorkerMap){
			PollingWorker worker;
			if(pollingWorkerMap.containsKey(evtId)){
				worker = pollingWorkerMap.get(evtId);
				worker.stop();
			}
			worker = new PollingWorker(evtId,sid, tid, pathOnly, params, intervalMs);
			worker.start();
			pollingWorkerMap.put(evtId, worker);
		}
	}
	
	@Override
	public void removePollingAdapterProcessorEvent(String evtId){
		synchronized(pollingWorkerMap){
			if(pollingWorkerMap.containsKey(evtId)){
				PollingWorker worker = pollingWorkerMap.get(evtId);
				worker.stop();
				pollingWorkerMap.remove(evtId);
			}
		}
	}
	
	@Override
	public boolean containPollingAdapterProcessor(String evtId) {
		return this.pollingWorkerMap.containsKey(evtId);
	}

	@Override
	public Set<String> getPollingAdapterProcessorNameList() {
		return this.pollingWorkerMap.keySet();
	}
	
	private class PollingWorker implements Runnable{
		String consumerName;
		String sid;
		String tid;
		String path;
		int intervalMs;
		Thread t = null;
		boolean isStart = false;
		Map<String, String> params = null;
		
		private PollingWorker(String consumerName,String sid, String tid, String path, Map<String, String> params, int intervalMs){
			super();
			this.consumerName = consumerName;
			this.sid = sid;
			this.tid = tid;
			this.path = path;
			this.params = params;
			this.intervalMs = intervalMs;
		}
		
		public void start(){
			stop();
			synchronized(this){
				this.isStart = true;
				t = new Thread(this);
				t.start();
			}
		}
		
		public void stop(){
			synchronized(this){
				this.isStart = false;
				if(t!=null){
					t.interrupt();
					try {
						t.join();
					} catch (InterruptedException e) {}
					t = null;
				}
			}
		}
		

		@Override
		public void run() {
			log.info("PollingWorker("+consumerName+") start.");
			InnerContext request = new InnerContext();
			request.setSid(sid);
			request.setTid(tid);
			request.setPaths(Arrays.asList(path.split("/")));
			
			if(params!=null && params.size()>0){
				request.setParams(params);
			}
			while(isStart){
				try {
					aim.handOverContextByCurrentThread(request, null);
					Thread.sleep(intervalMs);
				} catch (Exception e1) {
					log.err(e1);
				}
			}
			log.info("PollingWorker("+consumerName+") stoped.");
		}
		
		
	}

	@Override
	public List<IEventConsumer> getEventConsumerList() {
		List<IEventConsumer> ecList = new ArrayList<IEventConsumer>(this.consumerList.size());
		EventConsumerContainer ecc;
		for(int i=0,s=consumerList.size();i<s;i++){
			ecc = consumerList.get(i);
			ecList.add(ecc.consumer);
		}
		return ecList;
	}

	@Override
	public List<IEventConsumer> getAdapterProcessorEventConsumerList() {
		List<IEventConsumer> ecList = getEventConsumerList();
		Iterator<IEventConsumer> iter = ecList.iterator();
		IEventConsumer ec;
		while(iter.hasNext()){
			ec = iter.next();
			if(!(ec instanceof ApeConsumerContainer)){
				iter.remove();
			}
		}
		return ecList;
		
	}

}
