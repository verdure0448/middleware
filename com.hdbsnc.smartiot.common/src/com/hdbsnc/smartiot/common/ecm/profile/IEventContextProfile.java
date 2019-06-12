package com.hdbsnc.smartiot.common.ecm.profile;

import java.util.List;

public interface IEventContextProfile {

	String getEID();
	
	String getName();
	
	String getType();
	
	String getRemark();
	
	String getIsAutoStart();
	
	List<String> getDIDlist();
	
	List<String> getPatternStringList();
	
	List<IEventHandlerProfile> getEventHandlerProfileList();
	
}
