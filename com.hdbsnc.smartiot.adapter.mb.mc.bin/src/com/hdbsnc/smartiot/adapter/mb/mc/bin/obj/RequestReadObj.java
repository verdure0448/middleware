package com.hdbsnc.smartiot.adapter.mb.mc.bin.obj;

import java.util.ArrayList;
import java.util.List;

public class RequestReadObj {

	private int index;
	private String devCode;
	private String devNum;
	private String devScore;
	private String opCode;
	
	private List<FormatterObj> formatter;

	public RequestReadObj() {
		formatter=new ArrayList<>();
	}
	
	public String getOpCode() {
		return opCode;
	}
	
	public void setOpCode(String opCode) {
		this.opCode = opCode;
	}

	public void setIndex(int index){
		this.index = index;
	}
	
	public int getIndex() {
		return index;
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

	public void addFormater(String name, String pattern, String type, String index) {

		FormatterObj obj = new FormatterObj();
		obj.name = name;
		obj.pattern = pattern;
		obj.index = index;
		obj.type = type;
		
		formatter.add(obj);
	}
	
	public List<FormatterObj> getFormater() {
		return formatter;
	}
	/**
	 * @author DBeom Json 포멧을 만들 정보를 저장할 오브젝트
	 */
	public class FormatterObj {

		private String index;
		private String name;
		private String pattern;
		private String type;
		
		public String getIndex() {
			
			return index;
		}
		public String getName() {
			return name;
		}
		public String getPattern() {
			return pattern;
		}
		public String getType() {
			return type;
		}
	}

}
