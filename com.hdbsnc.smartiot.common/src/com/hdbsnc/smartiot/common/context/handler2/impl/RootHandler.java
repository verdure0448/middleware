package com.hdbsnc.smartiot.common.context.handler2.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.hdbsnc.smartiot.common.ICommonService;
import com.hdbsnc.smartiot.common.context.handler.IDirectoryHandler;
import com.hdbsnc.smartiot.common.context.handler.IElementHandler;
import com.hdbsnc.smartiot.common.context.handler.exception.ElementNotFoundException;
import com.hdbsnc.smartiot.common.context.handler.exception.ElementNullOrEmptyPathException;
import com.hdbsnc.smartiot.common.context.handler.exception.ElementUnSupportedInstanceException;
import com.hdbsnc.smartiot.common.context.handler.impl.ArrayListDirectoryHandler;
import com.hdbsnc.smartiot.common.context.handler2.IAttributeHandler;
import com.hdbsnc.smartiot.common.context.handler2.IFunctionHandler;
import com.hdbsnc.smartiot.common.ism.sm.ISessionManager;

public class RootHandler implements IDirectoryHandler{
	
	private List<IElementHandler> childList;
	private Map<String, IElementHandler> handlerMap = null;
	protected ICommonService commonService;
	private ISessionManager sm;
	
	public RootHandler(ICommonService commonService, ISessionManager sm){
		this.commonService = commonService;
		this.sm = sm;
		this.childList = new ArrayList<IElementHandler>();
	}
	
	public ICommonService getCommonService(){
		return this.commonService;
	}
	
	public ISessionManager getSessionManager(){
		return this.sm;
	}
	
	public void createHandlerTree(List<String> fullPathList){
		String[] pathArray;
		String tempPath;
		IElementHandler tempHandler;
		for(String fullPath: fullPathList){
			fullPath = fullPath.split(":")[0];
			pathArray = fullPath.split("/");
			if(pathArray.length>0){
				tempPath = pathArray[pathArray.length-1];
				if(handlerMap==null) System.out.println("핸들러가 등록되어있지 않습니다.");
				tempHandler = handlerMap.get(tempPath);
				if(tempHandler==null){
					System.out.println("path("+fullPath+") 핸들러풀에 존재하지 않는 경로 입니다.");
					continue;
				}
				try {
					putHandler(Arrays.copyOf(pathArray, pathArray.length-1), tempHandler);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else{
				System.out.println("path("+fullPath+") 패스 경로가 맞지 않습니다.");
			}
		}
	}
	
	public void clearHandlerTree(){
		handlerMap.clear();
		childList.clear();
	}
	
	public void regHandler(IElementHandler handler){
		if(handlerMap==null) handlerMap = new Hashtable<String, IElementHandler>();
		handlerMap.put(handler.getName(), handler);
	}
	
	public void unRegHandler(IElementHandler handler){
		if(handlerMap!=null){
			handlerMap.remove(handler.getName());
		}
	}
	
	public void unRegHandler(String handlerName){
		if(handlerMap!=null){
			handlerMap.remove(handlerName);
		}
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
		handler.setParent(this);
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
	
	public synchronized void putHandler(String[] pathArray, IElementHandler handler) throws ElementNullOrEmptyPathException, ElementUnSupportedInstanceException{
		String path;
		IElementHandler currHandler = this;
		IElementHandler tempChildHandler = null;
		IDirectoryHandler tempDirHandler = null;
		for(int i=0;i<pathArray.length;i++){
			path = pathArray[i];
			if(path.equals("")) throw new ElementNullOrEmptyPathException();//NullOrEmptyElementPathException
			tempChildHandler = ((IDirectoryHandler) currHandler).getHandler(path);
			if(tempChildHandler == null){
				tempDirHandler = new ArrayListDirectoryHandler(path);
				((IDirectoryHandler) currHandler).addHandler(tempDirHandler);
				currHandler = tempDirHandler;
			}else if(tempChildHandler instanceof IDirectoryHandler){
				currHandler = tempChildHandler;
			}else if(tempChildHandler instanceof IFunctionHandler){
				((IDirectoryHandler) currHandler).removeHandler(tempChildHandler);
				tempDirHandler = new ArrayListDirectoryHandler(path);
				tempDirHandler.addHandler(tempChildHandler);
				((IDirectoryHandler) currHandler).addHandler(tempDirHandler);
				currHandler = tempDirHandler;
			}else if(tempChildHandler instanceof IAttributeHandler){
				((IDirectoryHandler) currHandler).removeHandler(tempChildHandler);
				tempDirHandler = new ArrayListDirectoryHandler(path);
				tempDirHandler.addHandler(tempChildHandler);
				((IDirectoryHandler) currHandler).addHandler(tempDirHandler);
				currHandler = tempDirHandler;
			}else{
				throw new ElementUnSupportedInstanceException();//UnSupportedElementInstanceException
			}
		}
		((IDirectoryHandler)currHandler).addHandler(handler);
	}
	
	public synchronized void putHandler(String paths, IElementHandler handler) throws ElementNullOrEmptyPathException, ElementUnSupportedInstanceException {
		if(paths.equals("") || paths.equals("root")){
			this.addHandler(handler);
			return;
		}
		String[] pathArray = paths.split("/");
		putHandler(pathArray, handler);
	}
	
	public IElementHandler findHandler(String[] pathArray) throws ElementNotFoundException{
		String path = null;
		IElementHandler currHandler = this;
		IElementHandler tempChildHandler = null;
		for(int i=0;i<pathArray.length;i++){
			path = pathArray[i];
			tempChildHandler = ((IDirectoryHandler) currHandler).getHandler(path);
			if(tempChildHandler==null){
				throw new ElementNotFoundException(path);
			}else if(tempChildHandler instanceof IDirectoryHandler){
				if(i==(pathArray.length-1)){
					IElementHandler resultHandler = ((IDirectoryHandler) tempChildHandler).getHandler(path);
					if(resultHandler == null){
						return tempChildHandler;
					}else{
						return resultHandler;
					}
				}
				currHandler = tempChildHandler;
			}else if(tempChildHandler instanceof IFunctionHandler){
				if(i== (pathArray.length-1)){
					return tempChildHandler;
				}else{
					throw new ElementNotFoundException(path);
				}
			}else if(currHandler instanceof IAttributeHandler){
				if(i== (pathArray.length-1)){
					return tempChildHandler;
				}else{
					throw new ElementNotFoundException(path);
				}
			}else{
				throw new ElementNotFoundException(path);
			}
		}
		throw new ElementNotFoundException(path);
	}
	
	public IElementHandler findHandler(String paths) throws ElementNullOrEmptyPathException, ElementNotFoundException{
		if(paths==null) throw new ElementNullOrEmptyPathException();
		return findHandler(paths.split("/"));
	}
	
	public IElementHandler findHandler(List<String> paths) throws ElementNullOrEmptyPathException, ElementNotFoundException{
		if(paths.size()==0) throw new ElementNullOrEmptyPathException();
		//return findHandler(paths.toArray(new String[paths.size()]));
		String path = null;
		IElementHandler currHandler = this;
		IElementHandler tempChildHandler = null;
		for(int i=0,s=paths.size();i<s;i++){
			path = paths.get(i);
			tempChildHandler = ((IDirectoryHandler) currHandler).getHandler(path);
			if(tempChildHandler==null){
				throw new ElementNotFoundException(path);
			}else if(tempChildHandler instanceof IDirectoryHandler){
				if(i==(s-1)){
					IElementHandler resultHandler = ((IDirectoryHandler) tempChildHandler).getHandler(path);
					if(resultHandler == null){
						return tempChildHandler;
					}else{
						return resultHandler;
					}
				}
				currHandler = tempChildHandler;
			}else if(tempChildHandler instanceof IFunctionHandler){
				if(i== (s-1)){
					return tempChildHandler;
				}else{
					throw new ElementNotFoundException(path);
				}
			}else if(currHandler instanceof IAttributeHandler){
				if(i== (s-1)){
					return tempChildHandler;
				}else{
					throw new ElementNotFoundException(path);
				}
			}else{
				throw new ElementNotFoundException(path);
			}
		}
		throw new ElementNotFoundException(path);
		
	}
	

	public void printString(){
		StringBuilder tempSb;
		synchronized(childList){
			IElementHandler handler;
			for(int i=0,s=childList.size();i<s;i++){
				handler = childList.get(i);
				tempSb = new StringBuilder();
				tempSb.append("root");
				if(handler instanceof IDirectoryHandler){
					makeDirString(tempSb, (IDirectoryHandler) handler);
				}else{
					makeString(tempSb, handler);
				}
			}
		}
	}
	
	public void makeDirString(StringBuilder main, IDirectoryHandler dir){
		StringBuilder tempSb;
		List<IElementHandler> childList = dir.getHandlerList();
		synchronized(childList){
			IElementHandler handler;
			for(int i=0,s=childList.size();i<s;i++){
				handler = childList.get(i);
				tempSb = new StringBuilder();
				tempSb.append(main);
				tempSb.append("/").append(dir.getName());
				if(handler instanceof IDirectoryHandler){
					makeDirString(tempSb, (IDirectoryHandler) handler);
				}else{
					makeString(tempSb, handler);
				}
			}
		}
	}
	
	public void makeString(StringBuilder sb, IElementHandler ele){
		sb.append("/").append(ele.getName());
		System.out.println(sb.toString());
	}

}
