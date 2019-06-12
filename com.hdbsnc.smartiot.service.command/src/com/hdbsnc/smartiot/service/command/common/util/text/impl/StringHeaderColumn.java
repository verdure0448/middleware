package com.hdbsnc.smartiot.service.command.common.util.text.impl;

import com.hdbsnc.smartiot.service.command.common.util.text.IHeaderColumn;

public class StringHeaderColumn implements IHeaderColumn{

	int ratio;
	String name;
	
	public StringHeaderColumn(String name){
		this(name, 20);
	}
	
	public StringHeaderColumn(String name, int ratio){
		this.name = name;
		this.ratio = ratio;
	}
	
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public int ratio() {
		return this.ratio;
	}

}
