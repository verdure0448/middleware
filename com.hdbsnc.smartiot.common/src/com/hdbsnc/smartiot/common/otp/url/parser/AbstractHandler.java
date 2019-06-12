package com.hdbsnc.smartiot.common.otp.url.parser;


public abstract class AbstractHandler{

	private AbstractHandler nextHandler = null;
	
	UrlContext process(UrlContext ctx) throws UrlParseException {
		if(resolve(ctx)){
			parse(ctx);
		}
		if(nextHandler!=null){
			nextHandler.process(ctx);
		}
		return ctx;
	}
	
	public AbstractHandler setNext(AbstractHandler handler) {
		this.nextHandler = handler;
		return this.nextHandler;
	}
	
	protected abstract boolean resolve(UrlContext ctx);
	
	protected abstract void parse(UrlContext ctx) throws UrlParseException;

}
