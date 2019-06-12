package com.hdbsnc.smartiot.common.aim;


public interface IAdapterInstanceEventListener {
	
	void onChangeAdapterInstance(IAdapterInstanceEvent e);
	
	void setRemoveFlag();
	
	boolean isRemoveable();
}
