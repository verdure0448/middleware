package com.hdbsnc.smartiot.adapter.mb.mc.bin.protocol.obj;

import com.google.gson.annotations.SerializedName;

public class GatheringPublish {

	@SerializedName("jsonrpc")
	private String jsonrpc;

	@SerializedName("id")
	private String id;

	@SerializedName("result")
	private Result result;

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

		@SerializedName("items")
		private Items[] items;

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

		public Items[] getItems() {
			return items;
		}

		public void setItems(Items[] items) {
			this.items = items;
		}

	}

	public class Items {

		@SerializedName("key")
		private String key;

		@SerializedName("value")
		private String value;

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

	}
}
