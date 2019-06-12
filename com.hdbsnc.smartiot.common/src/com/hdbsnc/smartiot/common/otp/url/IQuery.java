package com.hdbsnc.smartiot.common.otp.url;

import java.util.Map;
import java.util.Set;

public interface IQuery {

	public static final String SEPARATOR = "?";
	
	boolean isEmpty();
	
	int getLength();
	
	Map<String, String> getParameters();
	
	Set<String> getParamNames();
	String getParamValue(String parameterName);
}
