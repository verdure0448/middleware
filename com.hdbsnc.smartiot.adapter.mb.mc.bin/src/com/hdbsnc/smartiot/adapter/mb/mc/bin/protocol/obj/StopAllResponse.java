package com.hdbsnc.smartiot.adapter.mb.mc.bin.protocol.obj;

import com.google.gson.annotations.SerializedName;

public class StopAllResponse extends CommonResponse {

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

		@SerializedName("stop.all")
		private StopAll[] stopAll;

		public String getVersion() {
			return version;
		}

		public void setVersion(String version) {
			this.version = version;
		}

		public StopAll[] getStopAll() {
			return stopAll;
		}

		public void setStopAll(StopAll[] stopAll) {
			this.stopAll = stopAll;
		}

	}
	
	public class StopAll{

		@SerializedName("event.id")
		private String eventId;
		@SerializedName("proc.date")
		private String procDate;
		
		public String getEventId() {
			return eventId;
		}
		
		public void setEventId(String eventId) {
			this.eventId = eventId;
		}
		
		public String getProcDate() {
			return procDate;
		}
		
		public void setProcDate(String procDate) {
			this.procDate = procDate;
		}
	}
}
