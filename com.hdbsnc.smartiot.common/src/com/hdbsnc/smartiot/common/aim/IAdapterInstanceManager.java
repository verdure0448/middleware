package com.hdbsnc.smartiot.common.aim;

import java.util.List;

import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.IContextCallback;
import com.hdbsnc.smartiot.common.context.IContextProcessor;
import com.hdbsnc.smartiot.common.context.IContextTracer;




public interface IAdapterInstanceManager {

	
//	public List<IAdapterContext> getAdapterContestList();
	public List<IAdapterInstanceContainer> getAdapterContainerList();
	public IAdapterInstanceContainer getAdapterInstance(String adapterInstanceId);
//	public IAdapterContext getAdapterContext(String adapterInstanceId);
	
	public void initialize(String adapterInstanceId) throws AimException;
	public void start(String adapterInstanceId) throws AimException;
	public void suspend(String adapterInstanceId) throws AimException;
	public void stop(String adapterInstanceId) throws AimException;
	public void dispose(String adapterInstanceId) throws AimException;
	
	public void start(String adapterInstanceId, IAdapterInstanceEventListener completedListener) throws AimException;
	public void suspend(String adapterInstanceId, IAdapterInstanceEventListener completedListener) throws AimException;
	public void stop(String adapterInstanceId, IAdapterInstanceEventListener completedListener) throws AimException;
	
	/**
	 * [사용용도]  
	 *  1. 아답터와 아답터간의 통신을 지원하기 위함 메소드.
	 *  2. 혹은 다른 서버에 존재하는 장치를 제어하기 위한 메소드.
	 *  
	 *  파라미터의 IContext SID, TID는 this나 default가 들어오면 안됨.
	 *  
	 *  내부적으로 ism의 contextSupport 메소드들을 사용하여 context 호출에 따른 비동기 결과물(response Context) 수신시 callback을 호출하도록 한다.
	 * @param exec
	 * @param callback
	 * @throws AimException
	 */
	
	public void handOverContext(IContext exec, IContextCallback callback) throws Exception;
	public void handOverContextByCurrentThread(IContext exec, IContextCallback callback) throws Exception;
	
	// 2017-07-10 hjs0317 신규 추가된 동기식 호출 handOverContext
	public IContextTracer handOverContext(IContext request) throws Exception;
	public IContextTracer handOverContextByCurrentThread(IContext request) throws Exception;
	
	/**
	 * 인스턴스매니져 자신이 가지고 있지 않는 장치에 대한 명령을 위임하는 브록커를 셋팅하는 메소드. 
	 * 없다면 null이고, 내부적으로 handOverContext로 context가 들어오면 해당장치 세션이 없을때 위임한다. 
	 * @param broker
	 */
	void setContextBroker(IContextProcessor broker);

}
