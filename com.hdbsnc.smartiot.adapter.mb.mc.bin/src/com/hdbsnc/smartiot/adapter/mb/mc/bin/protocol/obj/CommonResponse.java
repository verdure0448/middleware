package com.hdbsnc.smartiot.adapter.mb.mc.bin.protocol.obj;

import com.google.gson.annotations.SerializedName;

public class CommonResponse {
	@SerializedName("jsonrpc")
	private String jsonrpc;
	
	@SerializedName("id")
	private String id;
	 
	@SerializedName("error")
	private ResError error;

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

	public ResError getError() {
		return error;
	}

	public void setError(ResError error) {
		this.error = error;
	}
	
	
}
