package com.hdbsnc.smartiot.pm.storage;

import java.net.URL;


public abstract class Storage {

	IStorageObject obj = null;
	
	public static IStorageObject getInstance(String classPath){
		
		Class<?> cls;
		IStorageObject obj = null;
		try {
			cls = Class.forName(classPath);
			obj = (IStorageObject) cls.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}
}
