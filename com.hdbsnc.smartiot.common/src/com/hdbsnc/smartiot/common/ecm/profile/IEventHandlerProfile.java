package com.hdbsnc.smartiot.common.ecm.profile;

import java.util.List;
import java.util.Map;

public interface IEventHandlerProfile {

	String getSeq();
	
	String getEHID();
	
	String getName();
	
	String getType();
	
	String getRemark();
	
	List<IParameter> getParameterList();
	
	Map<String, String> getParameterMap();
	
	public interface IParameter{
		
		String getName();
		
		String getValue();
	}
}
