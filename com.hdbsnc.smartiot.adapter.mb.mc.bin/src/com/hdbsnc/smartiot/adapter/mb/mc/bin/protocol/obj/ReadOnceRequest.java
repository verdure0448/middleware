package com.hdbsnc.smartiot.adapter.mb.mc.bin.protocol.obj;

import com.google.gson.annotations.SerializedName;

public class ReadOnceRequest extends CommonRequest {

	@SerializedName("param")
	private Param param;

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

		@SerializedName("plc.ip")
		private String plcIp;

		@SerializedName("plc.port")
		private String plcPort;

		@SerializedName("items")
		private Items[] items;

		public String getVersion() {
			return version;
		}

		public void setVersion(String version) {
			this.version = version;
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

		public Items[] getItems() {
			return items;
		}

		public void setItems(Items[] items) {
			this.items = items;
		}

	}
}
