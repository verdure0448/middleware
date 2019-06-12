package com.hdbsnc.smartiot.common.otp.url;

import java.util.List;

public interface IHierarchicalPart {

	public static final String SEPARATOR_ROOT = "//";
	public static final String SEPARATOR ="/";
	
	IAuthority getAuthentication();
	String getFirstPath();
	String getLastPath();
	List<String> getPath();
	
}
