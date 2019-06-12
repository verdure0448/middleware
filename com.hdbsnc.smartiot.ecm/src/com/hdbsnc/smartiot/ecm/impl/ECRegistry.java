package com.hdbsnc.smartiot.ecm.impl;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.hdbsnc.smartiot.common.ecm.ec.IEventContext;

public class ECRegistry {

	private Map<String, IEventContext> registry;
	
	public ECRegistry(){
		registry = new Hashtable<String, IEventContext>();
	}
	
	public IEventContext getEventContext(String eid){
		return registry.get(eid);
	}
	
	public void add(IEventContext ectx){
		registry.put(ectx.getEID(), ectx);
	}
	
	public void remove(String eid){
		registry.remove(eid);
	}
	
	public Set<String> getEIDlist(){
		return registry.keySet();
	}
	
	public List<IEventContext> getEClist(){
		return new ArrayList(registry.values());
	}
}
