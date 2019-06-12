package com.hdbsnc.smartiot.common.context.handler.impl;

import java.util.ArrayList;
import java.util.List;

import com.hdbsnc.smartiot.common.context.handler.IDirectoryHandler;
import com.hdbsnc.smartiot.common.context.handler.IElementHandler;

@Deprecated
public class RootHandler implements IDirectoryHandler{
	
	private List<IElementHandler> childList;
	
	public RootHandler(){
		this.childList = new ArrayList<IElementHandler>();
	}
	
	@Override
	public IDirectoryHandler getParent() { return null; }

	@Override
	public void setParent(IDirectoryHandler handler) {}

	@Override
	public String getName() { return "root"; }

	@Override
	public int type() {
		return IElementHandler.ROOT;
	}

	@Override
	public int getHandlerCount() {
		return childList.size();
	}

	@Override
	public List<IElementHandler> getHandlerList() {
		return childList;
	}

	@Override
	public IElementHandler getHandler(String name) {
		for(IElementHandler handler: childList){
			if(handler.getName().equals(name)){
				return handler;
			}
		}
		return null;
	}

	@Override
	public void addHandler(IElementHandler handler) {
		childList.add(handler);
	}

	@Override
	public void removeHandler(IElementHandler handler) {
		childList.remove(handler);
	}

	@Override
	public boolean contains(String name) {
		for(IElementHandler handler:childList){
			if(handler.getName().equals(name)){
				return true;
			}
		}
		return false;
	}

}
