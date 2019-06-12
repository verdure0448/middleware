package com.hdbsnc.smartiot.pm.vo.impl;

import java.io.Serializable;

import com.hdbsnc.smartiot.common.pm.vo.ICacheManagerObj;

public class CacheManagerObj implements ICacheManagerObj, Serializable {

	private static final long serialVersionUID = 1L;

	private long cacheAcessTime;

	public CacheManagerObj() {
		this.cacheAcessTime = 0;
	}

	@Override
	public long getCacheAcessTime() {
		return this.cacheAcessTime;
	}

	public void setCacheAcessTime(long cacheAcessTime) {
		this.cacheAcessTime = cacheAcessTime;
	}


}
