package com.hdbsnc.smartiot.adapter.mb.mc.bin.protocol.obj;

import com.google.gson.annotations.SerializedName;

public class StatusResponse extends CommonResponse {
	

	@SerializedName("result")
	private Result result;
	

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

	
	public class Result {

		@SerializedName("protocol.version")
		private String version;	
		
		@SerializedName("status")
		private Status[] status;


		public String getVersion() {
			return version;
		}

		public void setVersion(String version) {
			this.version = version;
		}


		public Status[] getStatus() {
			return status;
		}


		public void setStatus(Status[] status) {
			this.status = status;
		}
		
	}
	
	public class Status {

		@SerializedName("event.id")
		private String eventID;
		
		@SerializedName("plc.ip")
		private String plcIp;
		
		@SerializedName("plc.port")
		private String plcPort;
		
		@SerializedName("polling.period")
		private String pollingPeriod;

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
		
	}
}
