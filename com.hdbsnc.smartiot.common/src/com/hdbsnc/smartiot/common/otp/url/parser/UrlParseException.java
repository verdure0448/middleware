package com.hdbsnc.smartiot.common.otp.url.parser;


public class UrlParseException extends Exception {

	public UrlParseException(String msg){
		super(msg);
	}
	
	public UrlParseException(String msg, Throwable e){
		super(msg, e);
	}
	
	public UrlParseException(String msg, int index, String packet){
		super(msg+": "+"index=["+index+"], packet=["+packet+"]");
	}
	
	public UrlParseException(String msg, UrlContext ctx){
		this(msg, ctx.getCurrentIndex(), ctx.getFullString());
	}
}
