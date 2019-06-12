package com.hdbsnc.smartiot.ism.impl;

import com.hdbsnc.smartiot.common.ism.sm.IFunctionMetaData;

public class DefaultFunctionMetaData implements IFunctionMetaData{

	private String key;
	private String contentType;
	private String[] paramNames;
	private String[] paramTypes;
	
	public DefaultFunctionMetaData(String key, String contentType,
			String[] paramNames, String[] paramTypes){
		this.key = key;
		this.contentType = contentType;
		this.paramNames = paramNames;
		this.paramTypes = paramTypes;
	}
	
	@Override
	public String getName() {
		return this.key;
	}

	@Override
	public String getContentType() {
		return this.contentType;
	}

	@Override
	public int getParametersCount() {
		if(paramNames==null) return 0;
		return paramNames.length;
	}

	@Override
	public String getParamterName(int index) {
		if(paramNames!=null && paramNames.length>index){
			return paramNames[index];
		}
		return null;
	}

	@Override
	public String getParamterType(int index) {
		if(paramTypes!=null && paramTypes.length>index){
			return paramTypes[index];
		}
		return null;
	}

}
