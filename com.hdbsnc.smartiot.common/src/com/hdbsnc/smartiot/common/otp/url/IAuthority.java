package com.hdbsnc.smartiot.common.otp.url;

public interface IAuthority {	
	
	public static final String SEPARATOR_AT = "@";
	public static final String SEPARATOR = ":";
	
	boolean isExistUserInfo();
	boolean isExistSequence();
	boolean isExistPort();
	String getUserInfo();
	String getSequence();
	
	String getHostname();
	String getPort();
}
