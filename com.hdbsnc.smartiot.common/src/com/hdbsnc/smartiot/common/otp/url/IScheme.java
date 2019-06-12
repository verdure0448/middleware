package com.hdbsnc.smartiot.common.otp.url;

import java.util.List;

public interface IScheme {

	public static final String SEPARATOR = ":";
	
	int getLength();
	List<String> getSchemePaths();
	String getSchemePath(int index);
	
	String getFirstSchemePath();
	String getLastSchemePath();
}
