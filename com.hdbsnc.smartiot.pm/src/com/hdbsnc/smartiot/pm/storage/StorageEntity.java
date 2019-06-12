package com.hdbsnc.smartiot.pm.storage;

import java.io.Serializable;

public class StorageEntity<K, T> implements Serializable {

	private static final long serialVersionUID = 1385184636537481657L;

	private K voName;
	private T voObj;

	public StorageEntity(K voName, T voObj) {
		this.voName = voName;
		this.voObj = voObj;
	}

	public K getVoName() {
		return voName;
	}

	public void setVoName(K voName) {
		this.voName = voName;
	}

	public T getVoObj() {
		return voObj;
	}

	public void setVoObj(T voObj) {
		this.voObj = voObj;
	}
}
