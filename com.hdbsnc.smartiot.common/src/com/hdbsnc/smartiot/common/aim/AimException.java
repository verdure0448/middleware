package com.hdbsnc.smartiot.common.aim;

public class AimException extends Exception {

	public AimException(){
		super();
	}
	
	public AimException(Exception e){
		super(e);
	}
	
	public AimException(String msg){
		super(msg);
	}
	
	public AimException(String msg, Exception e){
		super(msg, e);
	}
	
	

}
