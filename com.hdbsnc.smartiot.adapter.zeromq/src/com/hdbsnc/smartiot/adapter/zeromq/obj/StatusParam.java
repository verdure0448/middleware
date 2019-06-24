package com.hdbsnc.smartiot.adapter.zeromq.obj;

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
