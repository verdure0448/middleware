package com.hdbsnc.smartiot.common.em;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IEventManager {

	/**
	 * EventManager 쓰레드에서 구동되는 Consumer
	 * eventFilterString이 널이면 전체 이벤트 수신. 
	 * "0.0.0.0": 모든 이벤트를 받음. 
	 * "1.0.0.0": 1번 모듈에서 발생하는 모든 이벤트를 받음. 
	 * "1.2.1.7": 1번 모듈의 2번 이벤트의 1번 타입,7번 상태의 이벤트를 받음.
	 * @param eventFilterString
	 */
	void addEventConsumer(IEventConsumer consumer, String eventFilterString) throws EventFilterFormatException;	
	void addEventConsumer(IEventConsumer consumer, int eventFilter);
	void removeEventConsumer(IEventConsumer consumer);
	void removeEventConsumer(String consumerName);
	
	void addEventProducer(AbstractEventProducer producer);
	void removeEventProducer(AbstractEventProducer producer);

	List<IEventConsumer> getEventConsumerList();
	
	void start();
	void stop();
	
	void wakeup();
	
	/**
	 * 정규식 필터는 장치식별자와 경로까지 포함한 규칙.
	 * 예) com.hdbsnc.dev.1의 모든 경로의 장치정보를 수집하고 싶다면 필터는 com.hdbsnc.dev.1.* 로 넣어야 한다. [.*]는 정규식으로 이하 모든 문자를 허용한다는 뜻.
	 * @param consumer
	 * @param regularExpression
	 * @throws EventFilterFormatException
	 */
	void addAdapterProcessorEventConsumer(IAdapterProcessorEventConsumer consumer, String regularExpression) throws EventFilterFormatException;
	
	
	/**
	 * tid의 pathOnly 속성에 대해서 인터벌값 만큼 주기적으로 호출하고 그이벤트를 수집함.
	 * 예) com.hdbsnc.dev.1/plc/temperature 의 속성값을 1초마다 요청하고 발생하는 이벤트를 수집.
	 * @param consumer
	 * @param tid
	 * @param pathOnly
	 * @param intervalMs
	 * @throws EventFilterFormatException
	 */
	void addPollingAdapterProcessorEventConsumer(IAdapterProcessorEventConsumer consumer,String sid, String tid, String pathOnly, int intervalMs) throws EventFilterFormatException;
	void removePollingAdapterProcessorEventConsumer(String consumerName);
	void addPollingAdapterProcessorEvent(String evtId, String sid, String tid, String pathOnly, int intervalMs);
	void removePollingAdapterProcessorEvent(String evtId);
	void addPollingAdapterProcessorEvent(String evtId, String sid, String tid, String pathOnly, Map<String, String> params, int intervalMs);
	boolean containPollingAdapterProcessor(String evtId);
	Set<String> getPollingAdapterProcessorNameList();
	
	
	void removeAdapterProcessorEventConsumer(IAdapterProcessorEventConsumer consumer);
	void removeAdapterProcessorEventConsumer(String consumerName);
	

	List<IEventConsumer> getAdapterProcessorEventConsumerList();
}
