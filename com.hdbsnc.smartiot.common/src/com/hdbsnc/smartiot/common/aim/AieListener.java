package com.hdbsnc.smartiot.common.aim;

public abstract class AieListener implements IAdapterInstanceEventListener{

	private boolean isRemove = false;
	
	@Override
	public void setRemoveFlag() {
		this.isRemove = true;
	}
	
	public boolean isRemoveable(){
		return isRemove;
	}
}
