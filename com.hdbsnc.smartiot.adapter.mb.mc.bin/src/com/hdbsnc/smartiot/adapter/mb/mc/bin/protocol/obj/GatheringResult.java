package com.hdbsnc.smartiot.adapter.mb.mc.bin.protocol.obj;

import com.google.gson.annotations.SerializedName;

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
 