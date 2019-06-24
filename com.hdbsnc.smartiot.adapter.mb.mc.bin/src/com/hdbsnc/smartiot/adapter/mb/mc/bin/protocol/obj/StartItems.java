package com.hdbsnc.smartiot.adapter.mb.mc.bin.protocol.obj;

import com.google.gson.annotations.SerializedName;

public class StartItems {
	
	@SerializedName("key")
	private String key;	
	
	@SerializedName("device.code")
	private String deviceCode;
	
	@SerializedName("device.num")
	private String deviceNum;
	
	@SerializedName("device.score")
	private String deviceScore;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getDeviceCode() {
		return deviceCode;
	}

	public void setDeviceCode(String deviceCode) {
		this.deviceCode = deviceCode;
	}
 
	public String getDeviceNum() {
		return deviceNum;
	}

	public void setDeviceNum(String deviceNum) {
		this.deviceNum = deviceNum;
	}

	public String getDeviceScore() {
		return deviceScore;
	}

	public void setDeviceScore(String deviceScore) {
		this.deviceScore = deviceScore;
	}
	
	

}
