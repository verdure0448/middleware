package com.hdbsnc.smartiot.adapter.zeromq.obj;

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
		private String eventID;

	}
}
