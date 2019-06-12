package com.hdbsnc.smartiot.ecm.impl;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.hdbsnc.smartiot.common.ecm.eh.IEventHandler;

public class EHRegistry {

	private Map<String, IEventHandler> registry;
	
	public EHRegistry(){
		this.registry = new Hashtable<String, IEventHandler>();
	}
	
	public boolean containsEhid(String ehid){
		return this.registry.containsKey(ehid);
	}
	
	public IEventHandler getEventHandler(String ehid){	
		return registry.get(ehid);
	}
	
	public void add(IEventHandler eventHandler){
		registry.put(eventHandler.getEHID(), eventHandler);
	}
	
	public void remove(String ehid){
		registry.remove(ehid);
	}
	
	public Set<String> getEHIDlist(){
		return registry.keySet();
	}
	
	public List<IEventHandler> getEHlist(){
		return new ArrayList(registry.values());
	}
	
}
