package com.hdbsnc.smartiot.adapter.mb.mc.bin.obj;

public class RequestWriteObj {

	private String devCode;
	private String devNum;
	private String devScore;
	private String dataType;
	private String data;
	private int seq;
	
	public int getSeq() {
		return seq;
	}
	public void setSeq(int seq) {
		this.seq = seq;
	}
	public String getDevCode() {
		return devCode;
	}
	public void setDevCode(String devCode) {
		this.devCode = devCode;
	}
	public String getDevNum() {
		return devNum;
	}
	public void setDevNum(String devNum) {
		this.devNum = devNum;
	}
	public String getDevScore() {
		return devScore;
	}
	public void setDevScore(String devScore) {
		this.devScore = devScore;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	
}
