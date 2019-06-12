package com.hdbsnc.smartiot.common.connection.impl;

public abstract class AbstractConnectionHandler {

	private String handlerName;
	private AbstractConnectionHandler nextHandler;
	private boolean isFailToNext = false;
	
	public AbstractConnectionHandler(String handlerNm){
		this.handlerName = handlerNm;
	}
	
	public void setFailToNext(boolean bool){
		this.isFailToNext = bool;
	}
	
	public String getHandlerName(){
		return this.handlerName;
	}
	
	public AbstractConnectionHandler setNext(AbstractConnectionHandler handler){
		this.nextHandler = handler;
		return handler;
	}
	
	public void process(ConnectionHandleChain.Handle	 msg){
		
		if(resolve(msg)){
			innerSuccess(msg);
		}else{
			innerFail(msg);
		}
	}
	
	private void innerSuccess(ConnectionHandleChain.Handle msg){
		success(msg);
		if(nextHandler!=null)
		nextHandler.process(msg);
	}
	
	private void innerFail(ConnectionHandleChain.Handle msg){
		fail(msg);
		if(isFailToNext){
			nextHandler.process(msg);
		}
	}
	
	public abstract boolean resolve(ConnectionHandleChain.Handle msg);
	
	public abstract void success(ConnectionHandleChain.Handle msg);
	
	public abstract void fail(ConnectionHandleChain.Handle msg);
}
