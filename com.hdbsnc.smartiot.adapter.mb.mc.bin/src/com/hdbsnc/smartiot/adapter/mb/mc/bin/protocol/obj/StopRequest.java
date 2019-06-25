package com.hdbsnc.smartiot.adapter.mb.mc.bin.protocol.obj;

import com.google.gson.annotations.SerializedName;

public class StopRequest extends CommonRequest {

	@SerializedName("param")
	private Param param;

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
