package com.hdbsnc.smartiot.common.context.handler.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.hdbsnc.smartiot.common.context.IContextCallback;
import com.hdbsnc.smartiot.common.context.handler.IDirectoryHandler;
import com.hdbsnc.smartiot.common.context.handler.IElementHandler;
import com.hdbsnc.smartiot.common.context.handler.IFunctionHandler;

@Deprecated
public abstract class AbstractFuncAndDirHandler implements IFunctionHandler, IDirectoryHandler{

	protected String name;
	protected IDirectoryHandler parent;
	protected List<IElementHandler> childList;
	protected int type;
	
	public AbstractFuncAndDirHandler(String name){
		this.name = name;
		this.parent = null;
		this.childList = new ArrayList<IElementHandler>();
		this.type = IElementHandler.DIRECTORY | IElementHandler.FUNCTION;
	}
	
	@Override
	public IDirectoryHandler getParent() {
		return parent;
	}

	@Override
	public void setParent(IDirectoryHandler handler) {
		this.parent = handler;
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
		return this.childList.size();
	}

	@Override
	public List<IElementHandler> getHandlerList() {
		return this.childList;
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
		handler.setParent(this);
		this.childList.add(handler);
		
	}

	@Override
	public void removeHandler(IElementHandler handler) {
		if(this.childList.remove(handler)){
			handler.setParent(null);
		}
		
	}

	@Override
	public boolean contains(String name) {
		for(IElementHandler handler: childList){
			if(handler.getName().equals(name)){
				return true;
			}
		}
		return false;
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
