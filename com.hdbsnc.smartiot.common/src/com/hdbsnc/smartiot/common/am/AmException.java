package com.hdbsnc.smartiot.common.am;

public class AmException extends Exception {

	public AmException(){
		super();
	}
	
	public AmException(String msg, Exception e){
		super(msg, e);
	}
	
	public AmException(Exception e){
		super(e);
	}
	
	public AmException(String msg){
		super(msg);
	}
	
}
