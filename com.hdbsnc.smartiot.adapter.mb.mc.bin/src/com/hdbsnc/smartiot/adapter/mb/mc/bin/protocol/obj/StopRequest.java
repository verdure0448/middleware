package com.hdbsnc.smartiot.adapter.mb.mc.bin.protocol.obj;

import com.google.gson.annotations.SerializedName;

public class StopRequest {
	
	@SerializedName("jsonrpc")
	private String jsonrpc;
	
	@SerializedName("method")
	private String method;
	
	@SerializedName("id")
	private String id;
	
	@SerializedName("param")
	private Param param;

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

	public Param getParam() {
		return param;
	}

	public void setParam(Param param) {
		this.param = param;
	}	
	
	public class Param {
		
		@SerializedName("protocol.version")
		private String version;	
		
		@SerializedName("event.id")
		private String eventID;
		
		public String getVersion() {
			return version;
		}

		public void setVersion(String version) {
			this.version = version;
		}

		public String getEventID() {
			return eventID;
		}

		public void setEventID(String eventID) {
			this.eventID = eventID;
		}
	}
}
