package com.hdbsnc.smartiot.common.ism.sm;

public interface IFunctionMetaData {

	String getName();
	String getContentType();
	
	int getParametersCount();
	String getParamterName(int index);
	String getParamterType(int index);
	
}
