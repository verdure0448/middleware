package com.hdbsnc.smartiot.common.otp.url.parser.vo;

import com.hdbsnc.smartiot.common.otp.url.IAuthority;

public class Auth implements IAuthority{

	public static final Auth EMPTY = new Auth();
	public static final String EMPTY_VALUE = "";
	
	private String userInfo;
	private String seq;
	private String hostname;
	private String port;
	
	public Auth(){
		this(EMPTY_VALUE,EMPTY_VALUE,EMPTY_VALUE,EMPTY_VALUE);
	}
	
	public Auth(String userInfo1, String sequence2, String hostname3, String port4){
		this.userInfo = userInfo1;
		this.seq = sequence2;
		this.hostname = hostname3;
		this.port = port4;
	}
	
	@Override
	public boolean isExistUserInfo() {
		if(userInfo!=null && !userInfo.equals(EMPTY_VALUE)) return true;
		else return false;
	}
	
	@Override
	public boolean isExistSequence(){
		if(seq!=null && !seq.equals(EMPTY_VALUE)) return true;
		else return false;
	}

	@Override
	public boolean isExistPort() {
		if(port!=null && !port.equals(EMPTY_VALUE)) return true;
		else return false;
	}

	@Override
	public String getUserInfo() {
		return this.userInfo;
	}
	
	@Override
	public String getSequence(){
		return this.seq;
	}

	@Override
	public String getHostname() {
		return this.hostname;
	}

	@Override
	public String getPort() {
		return this.port;
	}
	
	public void setUserInfo(String userInfo){
		this.userInfo = userInfo;
	}
	
	public void setSequence(String seq){
		this.seq = seq;
	}
	
	public void setHostname(String hostname){
		this.hostname = hostname;
	}

	public void setPort(String port){
		this.port = port;
	}
	

	public void print(){
		System.out.println("[Auth]");
		System.out.println(this.userInfo);
		if(this.seq!=null && !this.seq.equals("")) System.out.println(this.seq);
		System.out.println(this.hostname);
		if(this.port!=null && !this.port.equals("")) System.out.println(this.port);
	}
}
