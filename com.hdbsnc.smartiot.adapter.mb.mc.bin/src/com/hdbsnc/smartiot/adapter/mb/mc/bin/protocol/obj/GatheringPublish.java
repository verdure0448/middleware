package com.hdbsnc.smartiot.adapter.mb.mc.bin.protocol.obj;

import com.google.gson.annotations.SerializedName;

public class GatheringPublish {
	
	@SerializedName("jsonrpc")
	private String jsonrpc;
	
	@SerializedName("id")
	private String id;
	
	@SerializedName("result")
	private GatheringResult result ;

	public String getJsonrpc() {
		return jsonrpc;
	}

	public void setJsonrpc(String jsonrpc) {
		this.jsonrpc = jsonrpc;
	}


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public GatheringResult getResult() {
		return result;
	}

	public void setResult(GatheringResult result) {
		this.result = result;
	}	
	
	
}
