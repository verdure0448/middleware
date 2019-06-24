package com.hdbsnc.smartiot.adapter.zeromq.obj;

import com.google.gson.annotations.SerializedName;

public class StopRequest {
	
	@SerializedName("jsonrpc")
	private String jsonrpc;
	
	@SerializedName("method")
	private String method;
	
	@SerializedName("id")
	private String id;
	
	@SerializedName("param")
	private StopParam param;

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

	public StopParam getParam() {
		return param;
	}

	public void setParam(StopParam param) {
		this.param = param;
	}	
	
	
}
