package com.hdbsnc.smartiot.adapter.zeromq.obj;

import com.google.gson.annotations.SerializedName;

public class StartResponse {

	@SerializedName("jsonrpc")
	private String jsonrpc;

	@SerializedName("id")
	private String id;

	@SerializedName("result")
	private Result result;

	@SerializedName("erroe")
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

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

	public ResError getError() {
		return error;
	}

	public void setError(ResError error) {
		this.error = error;
	}

	public class Result {

		@SerializedName("protocol.version")
		private String version;

		@SerializedName("event.id")
		private String eventID;

		@SerializedName("proc.data")
		private String procData;

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

		public String getProcData() {
			return procData;
		}

		public void setProcData(String procData) {
			this.procData = procData;
		}
	}

}
