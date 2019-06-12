package com.hdbsnc.smartiot.common.ecm.ec;

public interface IEClifeCycleListener {

	//String getListenerID();
	
	void onChangeLifeCycle(IEClifeCycleEvent evt);
}
