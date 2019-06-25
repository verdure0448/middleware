package com.hdbsnc.smartiot.adapter.zeromq.obj;

import com.google.gson.annotations.SerializedName;

public class CommonRequest {
	@SerializedName("jsonrpc")
	private String jsonrpc;
	
	@SerializedName("id")
	private String id;
	
	@SerializedName("method")
	private String method;

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

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}	
	
	
}
