package com.hdbsnc.smartiot.common.ecm;

import java.util.List;
import java.util.Set;

import com.hdbsnc.smartiot.common.context.IContext;

public interface IEvent {

	long getCreateTime();
	
	IContext getContext();
	List<IContext> getContextList();
	boolean isContextList();
	
	void putData(Object key, Object data);
	Object getData(Object key);
	Set<Object> getDataKeySet();
	
}
