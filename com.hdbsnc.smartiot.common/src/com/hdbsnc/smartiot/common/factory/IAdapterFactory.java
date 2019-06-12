package com.hdbsnc.smartiot.common.factory;

import com.hdbsnc.smartiot.common.aim.IAdapterInstance;

public interface IAdapterFactory {
	
	IAdapterInstance createInstance();
	
}
