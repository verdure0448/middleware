package com.hdbsnc.smartiot.common.context.handler2.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.hdbsnc.smartiot.common.ICommonService;
import com.hdbsnc.smartiot.common.context.handler.IDirectoryHandler;
import com.hdbsnc.smartiot.common.context.handler.IElementHandler;
import com.hdbsnc.smartiot.common.context.handler2.IFunctionHandler;
import com.hdbsnc.smartiot.common.ism.sm.ISessionManager;

public abstract class AbstractFunctionHandler implements IFunctionHandler{
	protected IDirectoryHandler parent;
	protected String name;
	protected int type = IElementHandler.FUNCTION;
	
	protected AbstractFunctionHandler(String name){
		this(null, name);
	}
	
	protected AbstractFunctionHandler(IDirectoryHandler parent, String name){
		this.parent = parent;
		this.name = name;
	}
	
	public ISessionManager getSessionManager(){
		IDirectoryHandler temp;
		temp = this.parent;
		while(temp!=null){
			if(temp instanceof RootHandler){
				return ((RootHandler) temp).getSessionManager();
			}else{
				temp = temp.getParent();
			}
		}
		return null;
	}
	
	public ICommonService getCommonService(){
		IDirectoryHandler temp;
		temp = this.parent;
		while(temp!=null){
			if(temp instanceof RootHandler){
				return ((RootHandler) temp).getCommonService();
			}else{
				temp = temp.getParent();
			}
		}
		return null;
	}
	
	@Override
	public IDirectoryHandler getParent() {
		return this.parent;
	}

	@Override
	public void setParent(IDirectoryHandler handler) {
		this.parent = handler;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public int type() {
		return type;
	}
	
	public List<String> currentPaths(){
		List<String> currPaths = new ArrayList<String>();
		IElementHandler pHandler = this;
		String name = pHandler.getName();
		currPaths.add(name);
		pHandler = pHandler.getParent();
		while(pHandler!=null){
			name = pHandler.getName();
			if(name!=null && !name.equals("root")) currPaths.add(name);
			pHandler = pHandler.getParent();
		}
		Collections.reverse(currPaths);
		return currPaths;
	}
	
	public String currentPathString(){
		StringBuilder sb = new StringBuilder();
		IElementHandler pHandler = this;
		String name = pHandler.getName();
		sb.append(name);
		pHandler = pHandler.getParent();
		while(pHandler!=null){
			name = pHandler.getName();
			sb.insert(0, "/");
			sb.insert(0, name);
			pHandler = pHandler.getParent();
		}
		return sb.toString();
	}

}