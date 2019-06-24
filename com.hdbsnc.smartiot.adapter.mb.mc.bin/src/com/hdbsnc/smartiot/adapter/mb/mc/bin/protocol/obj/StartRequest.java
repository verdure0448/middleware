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
	
	public class Items {
		
		@SerializedName("key")
		private String key;	
		
		@SerializedName("device.code")
		private String deviceCode;
		
		@SerializedName("device.num")
		private String deviceNum;
		
		@SerializedName("device.score")
		private String deviceScore;

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public String getDeviceCode() {
			return deviceCode;
		}

		public void setDeviceCode(String deviceCode) {
			this.deviceCode = deviceCode;
		}
	 
		public String getDeviceNum() {
			return deviceNum;
		}

		public void setDeviceNum(String deviceNum) {
			this.deviceNum = deviceNum;
		}

		public String getDeviceScore() {
			return deviceScore;
		}

		public void setDeviceScore(String deviceScore) {
			this.deviceScore = deviceScore;
		}
	}
	
	public class Param {
		
		@SerializedName("protocol.version")
		private String version;	
		
		@SerializedName("event.id")
		private String eventID;
		
		@SerializedName("plc.ip")
		private String plcIp;
		
		@SerializedName("plc.port")
		private String plcPort;
		
		@SerializedName("polling.period")
		private String pollingPeriod;
		
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

		public String getPlcIp() {
			return plcIp;
		}

		public void setPlcIp(String plcIp) {
			this.plcIp = plcIp;
		}

		public String getPlcPort() {
			return plcPort;
		}

		public void setPlcPort(String plcPort) {
			this.plcPort = plcPort;
		}

		public String getPollingPeriod() {
			return pollingPeriod;
		}

		public void setPollingPeriod(String pollingPeriod) {
			this.pollingPeriod = pollingPeriod;
		}

		public Items[] getItems() {
			return items;
		}

		public void setItems(Items[] items) {
			this.items = items;
		}
		
	}
}
