package com.hdbsnc.smartiot.service.command.common.util.text.impl;

import com.hdbsnc.smartiot.service.command.common.util.text.IColumn;

public class StringColumn implements IColumn{

	private String name;
	private String value;
	
	public StringColumn(String value){
		this("", value);
	}
	
	public StringColumn(String name, String value){
		this.name = name;
		this.value = value;
	}
	
	@Override
	public int getValueLength() {
		return value.length();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getValue() {
		return value;
	}

}
