package com.hdbsnc.smartiot.adapter.mb.mc.bin.protocol.obj;

import com.google.gson.annotations.SerializedName;

public class StatusParam {
	
	@SerializedName("protocol.version")
	private String version;	
	
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}


}
