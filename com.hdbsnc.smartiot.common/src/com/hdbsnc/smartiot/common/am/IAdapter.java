package com.hdbsnc.smartiot.common.am;

import com.hdbsnc.smartiot.common.ICommonService;
import com.hdbsnc.smartiot.common.factory.IAdapterFactory;

public interface IAdapter {
	
	String getAdapterId();
	
	IAdapterManifest getManifest();
	
	IAdapterFactory getFactory(ICommonService service);
	
	void registe();
	
	void unregiste();
	
	
}