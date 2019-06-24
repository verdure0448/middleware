package com.hdbsnc.smartiot.adapter.zeromq.obj;

import com.google.gson.annotations.SerializedName;

public class StartResponse {
	
	@SerializedName("jsonrpc")
	private String jsonrpc;
	
	@SerializedName("id")
	private String id;
	
	@SerializedName("param")
	private StartParam param;

	@SerializedName("result")
	private StartResult result;
	
	@SerializedName("erroe")
	private ResponseError error;

	
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

	public StartParam getParam() {
		return param;
	}

	public void setParam(StartParam param) {
		this.param = param;
	}

	public StartResult getResult() {
		return result;
	}

	public void setResult(StartResult result) {
		this.result = result;
	}

	public ResponseError getError() {
		return error;
	}

	public void setError(ResponseError error) {
		this.error = error;
	}
	
}
