package com.hdbsnc.smartiot.ecm.impl;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.hdbsnc.smartiot.common.aim.IAdapterProcessorEvent;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.ecm.IEvent;

public class Event implements IEvent{

	private IAdapterProcessorEvent ape;
	private Map<Object, Object> datas = null;
	
	public Event(IAdapterProcessorEvent ape){
		this.ape = ape;
	}
	
	@Override
	public long getCreateTime() {
		return this.ape.getCreatedTime();
	}

	@Override
	public IContext getContext() {
		return ape.getContext();
	}

	@Override
	public List<IContext> getContextList() {
		
		return null;
	}

	@Override
	public boolean isContextList() {
		
		return false;
	}

	@Override
	public void putData(Object key, Object data) {
		if(datas==null){
			datas = new Hashtable();
		}
		datas.put(key, data);
	}

	@Override
	public Object getData(Object key) {
		if(datas!=null){
			return datas.get(key);
		}else{
			return null;
		}
	}

	@Override
	public Set<Object> getDataKeySet() {
		synchronized(datas){
			if(datas!=null){
				return datas.keySet();
			}else{
				return null;
			}
		}
	}





}
