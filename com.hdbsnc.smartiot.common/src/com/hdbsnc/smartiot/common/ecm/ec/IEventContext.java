package com.hdbsnc.smartiot.common.ecm.ec;

import com.hdbsnc.smartiot.common.ICommonService;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.IContextCallback;
import com.hdbsnc.smartiot.common.context.IContextTracer;
import com.hdbsnc.smartiot.common.ecm.IEventProcessor;
import com.hdbsnc.smartiot.common.ecm.profile.IEventContextProfile;

public interface IEventContext {

	String getEID();
	
	ICommonService getCommonService();
	
	IEventContextProfile getProfile();
	
	//비동기식 호출.
	void handOverContext(IContext requestCtx, IContextCallback callback) throws Exception;	
	void handOverContextByCurrentThread(IContext requestCtx, IContextCallback callback) throws Exception;
	
	//동기식 호출. 2017-07-10 hjs0317 신규 추가된 동기식 호출
	public IContextTracer handOverContext(IContext request) throws Exception;
	public IContextTracer handOverContextByCurrentThread(IContext request) throws Exception;
	
	IEventContextInstance getInstance();
	
    IEClifeCycleEvent getLastLifeCycleEvent();
	
	void addEClifeCycleListener(int lifeCycleEventType, int lifeCycleEventState, IEClifeCycleListener listener);
	void addEClifeCycleListener(int lifeCycleEventType, int lifeCycleEventState, boolean once, IEClifeCycleListener listener);
	void removeEClifeCycleListener(IEClifeCycleListener listener);
	//void removeEClifeCycleListener(String listenerId);
	void removeAllEClifeCycleListener();
	
	boolean containsEClifeCycleListener(IEClifeCycleListener listener);
	//boolean containsEClifeCycleListener(String listenerId);
	boolean containsEClifeCyclePreHandler(IEClifeCycleHandler handler);
	boolean containsEClifeCycleAfterHandler(IEClifeCycleHandler handler);
	boolean containsProcessorPreHandler(IEventProcessor handler);
	boolean containsProcessorAfterHandler(IEventProcessor handler);
	
	void addLifeCyclePreHandler(IEClifeCycleHandler preHandler);
	void removeLifeCyclePreHandler(IEClifeCycleHandler preHandler);	
	void addLifeCycleAfterHandler(IEClifeCycleHandler afterHandler);
	void removeLifeCycleAfterHandler(IEClifeCycleHandler afterHandler);
	
	void addProcessorPreHandler(IEventProcessor prehandler);
	void removeProcessorPreHandler(IEventProcessor prehandler);	
	void addProcessorAfterHandler(IEventProcessor afterHandler);
	void removeProcessorAfterHandler(IEventProcessor afterHandler);
}
