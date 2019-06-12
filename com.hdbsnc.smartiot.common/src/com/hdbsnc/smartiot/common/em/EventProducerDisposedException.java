package com.hdbsnc.smartiot.common.em;

public class EventProducerDisposedException extends Exception{

	private AbstractEventProducer producer;
	
	public EventProducerDisposedException(AbstractEventProducer producer){
		this.producer = producer;
	}
	
	@Override
	public String getMessage() {
		return "EventProducer("+producer.getModuleValue()+") disposed.";
	}
	
	
}
