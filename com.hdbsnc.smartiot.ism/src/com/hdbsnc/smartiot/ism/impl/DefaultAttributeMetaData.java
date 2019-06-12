package com.hdbsnc.smartiot.ism.impl;

import com.hdbsnc.smartiot.common.ism.sm.IAttributeMetaData;

public class DefaultAttributeMetaData implements IAttributeMetaData{

	private String key;
	private String value;
	
	public DefaultAttributeMetaData(String key, String value){
		this.key =key;
		this.value = value;
	}
	
	@Override
	public String getName() {
		return key;
	}

	@Override
	public String getValue() {
		return value;
	}
	
	public void setValue(String value){
		this.value = value;
	}

}
