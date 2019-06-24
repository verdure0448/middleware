package com.hdbsnc.smartiot.adapter.mb.mc.bin.protocol.obj;

import com.google.gson.annotations.SerializedName;

public class StartRequest {
	
	@SerializedName("jsonrpc")
	private String jsonrpc;
	
	@SerializedName("method")
	private String method;
	
	@SerializedName("id")
	private String id;
	
	@SerializedName("param")
	private StartParam param;

	public String getJsonrpc() {
		return jsonrpc;
	}

	public void setJsonrpc(String jsonrpc) {
		this.jsonrpc = jsonrpc;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public StartParam getParam() {
		return param;
	}

	public void setParam(StartParam param) {
		this.param = param;
	}	
	
	
}
