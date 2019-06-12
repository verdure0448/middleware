package com.hdbsnc.smartiot.common.context.handler.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.hdbsnc.smartiot.common.context.handler.IDirectoryHandler;
import com.hdbsnc.smartiot.common.context.handler.IElementHandler;

public class HashtableDirectoryHandler implements IDirectoryHandler{

	protected String name;
	protected IDirectoryHandler parent;
	protected Map<String, IElementHandler> childMap;
	protected int type = IElementHandler.DIRECTORY;
	
	public HashtableDirectoryHandler(String name){
		this.name = name;
		this.parent = null;
		this.childMap = new Hashtable<String, IElementHandler>();
	}
	
	@Override
	public IDirectoryHandler getParent() {
		return parent;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int type() {
		return type;
	}

	@Override
	public int getHandlerCount() {
		return this.childMap.size();
	}

	@Override
	public List<IElementHandler> getHandlerList() {
		return new ArrayList<IElementHandler>(childMap.values());
	}

	@Override
	public IElementHandler getHandler(String name) {
		return childMap.get(name);
	}

	@Override
	public void addHandler(IElementHandler handler) {
		handler.setParent(this);
		this.childMap.put(handler.getName(), handler);
	}

	@Override
	public void removeHandler(IElementHandler handler) {
		IElementHandler removeHandler = this.childMap.remove(handler.getName());
		if(removeHandler!=null) removeHandler.setParent(null);
	}

	@Override
	public void setParent(IDirectoryHandler handler) {
		this.parent = handler;
	}

	@Override
	public boolean contains(String name) {
		return childMap.containsKey(name);
	}

	public List<String> currentPaths(){
		List<String> currPaths = new ArrayList<String>();
		IElementHandler pHandler = this;
		String name = pHandler.getName();
		currPaths.add(name);
		pHandler = pHandler.getParent();
		while(pHandler!=null){
			name = pHandler.getName();
			currPaths.add(name);
			pHandler = pHandler.getParent();
		}
		Collections.reverse(currPaths);
		return currPaths;
	}
}
