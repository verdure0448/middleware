package com.hdbsnc.smartiot.adapter.websocketapi.connection.impl;

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
	
	public void process(ConnectionHandleChain_old.Handle	 msg){
		
		if(resolve(msg)){
			innerSuccess(msg);
		}else{
			innerFail(msg);
		}
	}
	
	private void innerSuccess(ConnectionHandleChain_old.Handle msg){
		success(msg);
		if(nextHandler!=null)
		nextHandler.process(msg);
	}
	
	private void innerFail(ConnectionHandleChain_old.Handle msg){
		fail(msg);
		if(isFailToNext){
			nextHandler.process(msg);
		}
	}
	
	public abstract boolean resolve(ConnectionHandleChain_old.Handle msg);
	
	public abstract void success(ConnectionHandleChain_old.Handle msg);
	
	public abstract void fail(ConnectionHandleChain_old.Handle msg);
}
