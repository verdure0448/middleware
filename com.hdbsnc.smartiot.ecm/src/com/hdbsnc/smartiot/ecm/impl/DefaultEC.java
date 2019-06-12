package com.hdbsnc.smartiot.ecm.impl;

import java.util.ArrayList;
import java.util.List;

import com.hdbsnc.smartiot.common.ICommonService;
import com.hdbsnc.smartiot.common.aim.IAdapterInstanceManager;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.IContextCallback;
import com.hdbsnc.smartiot.common.context.IContextTracer;
import com.hdbsnc.smartiot.common.ecm.IEventProcessor;
import com.hdbsnc.smartiot.common.ecm.ec.IEClifeCycleEvent;
import com.hdbsnc.smartiot.common.ecm.ec.IEClifeCycleHandler;
import com.hdbsnc.smartiot.common.ecm.ec.IEClifeCycleListener;
import com.hdbsnc.smartiot.common.ecm.ec.IEventContext;
import com.hdbsnc.smartiot.common.ecm.ec.IEventContextInstance;
import com.hdbsnc.smartiot.common.ecm.profile.IEventContextProfile;

public class DefaultEC implements IEventContext{

	private IEventContextProfile ecp;
	private IAdapterInstanceManager aim;
	private ICommonService cs;
	private IEventContextInstance instance = null;
	private IEClifeCycleEvent lastEvent = null;
	private List<IEventProcessor> preHandlerList = null;
	private List<IEventProcessor> afterHandlerList = null;
	private List<IEClifeCycleHandler> lcPreHandlerList = null;
	private List<IEClifeCycleHandler> lcAfterHandlerList = null;
	
	private List<EClifeCycleListenerCondition> listenerList = null;
	
	DefaultEC(ICommonService cs, IAdapterInstanceManager aim, IEventContextProfile ecp){
		this.ecp = ecp;
		this.aim = aim;
		this.cs = cs;
		this.listenerList = new ArrayList();
	}
	
	@Override
	public String getEID() {
		return ecp.getEID();
	}

	@Override
	public ICommonService getCommonService() {
		return cs;
	}

	@Override
	public IEventContextProfile getProfile() {
		return ecp;
	}

	@Override
	public void handOverContext(IContext requestCtx, IContextCallback callback) throws Exception {
		aim.handOverContext(requestCtx, callback);
	}

	@Override
	public void handOverContextByCurrentThread(IContext requestCtx, IContextCallback callback) throws Exception{
		aim.handOverContextByCurrentThread(requestCtx, callback);
	}
	
	@Override
	public IContextTracer handOverContext(IContext request) throws Exception{
		return aim.handOverContext(request);
	}
	
	@Override
	public IContextTracer handOverContextByCurrentThread(IContext request) throws Exception{
		return aim.handOverContextByCurrentThread(request);
	}

	@Override
	public IEClifeCycleEvent getLastLifeCycleEvent() {
		return this.lastEvent;
	}

	@Override
	public IEventContextInstance getInstance() {
		return instance;
	}
	
	void setECinstance(IEventContextInstance ecInstance){
		this.instance = ecInstance;
	}

	@Override
	public void addEClifeCycleListener(int eventType, int eventState, IEClifeCycleListener listener) {
		synchronized(listenerList){
			this.listenerList.add(new EClifeCycleListenerCondition(listener, eventType, eventState, false));
		}
	}
	
	@Override
	public void addEClifeCycleListener(int eventType, int eventState, boolean once, IEClifeCycleListener listener) {
		synchronized(listenerList){
			this.listenerList.add(new EClifeCycleListenerCondition(listener, eventType, eventState, once));
		}
	}

	@Override
	public void removeEClifeCycleListener(IEClifeCycleListener listener1) {
		synchronized(listenerList){
			for(EClifeCycleListenerCondition con: listenerList){
				if(con.isContainsListener(listener1)){
					listenerList.remove(con);
					return;
				}
			}
		}
	}

//	@Override
//	public void removeEClifeCycleListener(String listenerID) {
//		synchronized(listenerList){
//			if(listenerList==null) return;
//			IEClifeCycleListener listener;
//			for(EClifeCycleListenerCondition con: listenerList){
//				listener = con.listener;
//				if(listener.getListenerID().equals(listenerID)){
//					listenerList.remove(listener);
//					break;
//				}
//			}
//		}
//	}

	@Override
	public void removeAllEClifeCycleListener() {
		synchronized(listenerList){
			listenerList.clear();
		}
	}

	@Override
	public boolean containsEClifeCycleListener(IEClifeCycleListener listener) {
		if(listenerList.contains(listener)){
			return true;
		}else{
			return false;
		}
	}

//	@Override
//	public boolean containsEClifeCycleListener(String listenerID) {
//		synchronized(listenerList){
//			if(listenerList==null) return false;
//			IEClifeCycleListener listener;
//			for(EClifeCycleListenerCondition con: listenerList){
//				listener = con.listener;
//				if(listener.getListenerID().equals(listenerID)){
//					return true;
//				}
//			}
//		}
//		return false;
//	}
	
	List<IEClifeCycleListener> getListenerList(){
		List<IEClifeCycleListener> newList = new ArrayList();
		for(EClifeCycleListenerCondition con: listenerList){
			newList.add(con.listener);
		}
		return newList;
	}
	
	List<EClifeCycleListenerCondition> getListenerConditionList(){
		return listenerList;
	}
	
	@Override
	public boolean containsProcessorPreHandler(IEventProcessor handler) {
		if(preHandlerList==null) return false;
		if(preHandlerList.contains(handler))return true;
		return false;
	}

	@Override
	public boolean containsProcessorAfterHandler(IEventProcessor handler) {
		if(afterHandlerList==null) return false;
		if(afterHandlerList.contains(handler))return true;
		return false;
	}

	@Override
	public void addProcessorPreHandler(IEventProcessor prehandler) {
		synchronized(preHandlerList){
			if(preHandlerList==null) preHandlerList = new ArrayList();
			this.preHandlerList.add(prehandler);
		}
	}

	@Override
	public void removeProcessorPreHandler(IEventProcessor prehandler) {
		synchronized(preHandlerList){
			if(preHandlerList==null) return;
			this.preHandlerList.remove(prehandler);
		}
		
	}

	@Override
	public void addProcessorAfterHandler(IEventProcessor afterHandler) {
		synchronized(afterHandlerList){
			if(afterHandlerList==null) afterHandlerList = new ArrayList();
			afterHandlerList.add(afterHandler);
		}
	}

	@Override
	public void removeProcessorAfterHandler(IEventProcessor afterHandler) {
		synchronized(afterHandlerList){
			if(afterHandlerList==null) return;
			afterHandlerList.remove(afterHandler);
		}
	}
	
	List<IEventProcessor> getProcessorPreHandlerList(){
		return this.preHandlerList;
	}
	
	List<IEventProcessor> getProcessorAfterHandlerList(){
		return this.afterHandlerList;
	}

	@Override
	public boolean containsEClifeCyclePreHandler(IEClifeCycleHandler handler) {
		if(lcPreHandlerList==null) return false;
		if(lcPreHandlerList.contains(handler)) return true;
		return false;
	}

	@Override
	public boolean containsEClifeCycleAfterHandler(IEClifeCycleHandler handler) {
		if(lcAfterHandlerList==null) return false;
		if(lcAfterHandlerList.contains(handler)) return true;
		return false;
	}

	@Override
	public void addLifeCyclePreHandler(IEClifeCycleHandler preHandler) {
		synchronized(lcPreHandlerList){
			if(lcPreHandlerList==null) lcPreHandlerList = new ArrayList();
			lcPreHandlerList.add(preHandler);
		}
	}

	@Override
	public void removeLifeCyclePreHandler(IEClifeCycleHandler preHandler) {
		synchronized(lcPreHandlerList){
			if(lcPreHandlerList==null) return;
			lcPreHandlerList.remove(preHandler);
		}
	}

	@Override
	public void addLifeCycleAfterHandler(IEClifeCycleHandler afterHandler) {
		synchronized(lcAfterHandlerList){
			if(lcAfterHandlerList==null) lcAfterHandlerList = new ArrayList();
			lcAfterHandlerList.add(afterHandler);
		}
	}

	@Override
	public void removeLifeCycleAfterHandler(IEClifeCycleHandler afterHandler) {
		synchronized(lcAfterHandlerList){
			if(lcAfterHandlerList==null) return;
			lcAfterHandlerList.remove(afterHandler);
		}	
	}
	
	List<IEClifeCycleHandler> getEClifeCyclePreHandlerList(){
		return this.lcPreHandlerList;
	}
	
	List<IEClifeCycleHandler> getEClifeCycleAfterHandlerList(){
		return this.lcAfterHandlerList;
	}
	
	class EClifeCycleListenerCondition{
		IEClifeCycleListener listener = null;
		int eventType;
		int eventState;
		boolean once = false;
		
		public EClifeCycleListenerCondition(IEClifeCycleListener listener, int eventType, int eventState, boolean once){
			this.listener = listener;
			this.eventType = eventType;
			this.eventState = eventState;
			this.once = once;
		}
		
		boolean isContainsListener(IEClifeCycleListener listener1){
			if(listener!=null && listener.equals(listener1)){
				return true;
			}else{
				return false;
			}
		}
	}


}
