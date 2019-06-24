package com.hdbsnc.smartiot.adapter.zeromq.obj;

import com.google.gson.annotations.SerializedName;

public class GatheringPublish {

	@SerializedName("jsonrpc")
	private String jsonrpc;

	@SerializedName("id")
	private String id;

	@SerializedName("result")
	private GatheringResult result;

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

	public class GatheringResult {

		@SerializedName("protocol.version")
		private String version;

		@SerializedName("event.id")
		private String eventID;

		@SerializedName("proc.data")
		private String procData;

		@SerializedName("items")
		private GatheringItems items;

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

		public GatheringItems getItems() {
			return items;
		}

		public void setItems(GatheringItems items) {
			this.items = items;
		}

	}

	public class GatheringItems {

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
