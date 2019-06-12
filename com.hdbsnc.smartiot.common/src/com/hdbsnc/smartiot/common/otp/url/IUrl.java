package com.hdbsnc.smartiot.common.otp.url;

public interface IUrl {

	IScheme getScheme();
	IHierarchicalPart getHierarchicalPart();
	IQuery getQuery();
	IFragment getFragment();
	
}
