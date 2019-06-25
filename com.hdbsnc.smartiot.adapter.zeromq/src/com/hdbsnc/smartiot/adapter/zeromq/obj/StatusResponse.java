package com.hdbsnc.smartiot.adapter.zeromq.obj;

import com.google.gson.annotations.SerializedName;

public class StatusResponse {
	
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
