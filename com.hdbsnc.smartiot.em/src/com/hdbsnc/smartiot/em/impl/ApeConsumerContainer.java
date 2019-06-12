package com.hdbsnc.smartiot.em.impl;

import java.util.regex.Pattern;

import com.hdbsnc.smartiot.common.aim.IAdapterProcessorEvent;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.em.IAdapterProcessorEventConsumer;
import com.hdbsnc.smartiot.common.em.IEventConsumer;
import com.hdbsnc.smartiot.common.em.event.IEvent;
import com.hdbsnc.smartiot.common.exception.CommonException;
import com.hdbsnc.smartiot.common.ism.IIntegratedSessionManager;
import com.hdbsnc.smartiot.common.ism.sm.ISession;

public class ApeConsumerContainer implements IEventConsumer{

	private IIntegratedSessionManager ism;
	private IAdapterProcessorEventConsumer consumer;
	private Pattern p;
	
	public ApeConsumerContainer(IIntegratedSessionManager ism, IAdapterProcessorEventConsumer consumer, Pattern regularPattern){
		this.consumer = consumer;
		this.p = regularPattern;
		this.ism = ism;
	}
	
	@Override
	public String getName() {
		return this.consumer.getName();
	}

	@Override
	public void initialize() throws Exception {
		this.consumer.initialize();
	}

	@Override
	public void dispose() {
		this.consumer.dispose();
	}

	@Override
	public void updateEvent(IEvent event) throws CommonException {
		if(event instanceof IAdapterProcessorEvent){
			IAdapterProcessorEvent ape = (IAdapterProcessorEvent) event;
			IContext ctx = ape.getContext();
			String sid = ctx.getSID();
			String tid = ctx.getTID();
			if(tid!=null && !tid.equals("") && tid.equals("this")){
				ISession session = ism.getSessionBySessionId(sid);
				if(session==null) {
					System.out.println("session 정보가 없습니다.(sid="+sid+", tid="+tid+")");
				}else{
					tid = session.getDeviceId();
				}
			}
			String fullPath = ctx.getFullPath();
			if(fullPath!=null && !fullPath.equals("")) tid= tid + "/" +fullPath;
			if(p.matcher(tid).matches()){
				this.consumer.updateEvent(ape);
			}
		}
	}
}
