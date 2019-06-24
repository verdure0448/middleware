package com.hdbsnc.smartiot.adapter.mb.mc.bin.protocol.obj;

import com.google.gson.annotations.SerializedName;

public class StartParam {
	
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
	private StartItems[] items;

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

	public StartItems[] getItems() {
		return items;
	}

	public void setItems(StartItems[] items) {
		this.items = items;
	}
	
	
}
