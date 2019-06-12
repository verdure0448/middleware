////////////////////////////////////////////////////////////////////
// Copyright 2015,2016 <hd-bsnc.com>. All rights reserved.
////////////////////////////////////////////////////////////////////
package com.hdbsnc.smartiot.pm.vo.impl;

import java.io.Serializable;

import com.hdbsnc.smartiot.common.pm.vo.IDeviceObj;
import com.hdbsnc.smartiot.pm.constant.IConst;

public class DeviceObj extends CacheManagerObj implements IDeviceObj,
		Serializable {

	private static final long serialVersionUID = 1L;

	String devId;
	String devPoolId;
	String devNm;
	String devType;
	String isUse;
	String sessionTimeout;
	String ip;
	String port;
	String lat;
	String lon;
	String remark;
	String alterDate;
	String regDate;

	public DeviceObj() {
		this.devId = IConst.EMPTY_STRING;
		this.devPoolId = IConst.EMPTY_STRING;
		this.devNm = IConst.EMPTY_STRING;
		this.devType = IConst.EMPTY_STRING;
		this.isUse = IConst.EMPTY_STRING;
		this.sessionTimeout = IConst.EMPTY_STRING;
		this.ip = IConst.EMPTY_STRING;
		this.port = IConst.EMPTY_STRING;
		this.lat = IConst.EMPTY_STRING;
		this.lon = IConst.EMPTY_STRING;
		this.remark = IConst.EMPTY_STRING;
		this.alterDate = IConst.EMPTY_STRING;
		this.regDate = IConst.EMPTY_STRING;
	}

	@Override
	public String getDevId() {
		return this.devId;
	}

	@Override
	public String getDevPoolId() {
		return this.devPoolId;
	}

	@Override
	public String getDevNm() {
		return this.devNm;
	}
	
	@Override
	public String getDevType() {
		return this.devType;
	}

	@Override
	public String getIsUse() {
		return this.isUse;
	}

	@Override
	public String getSessionTimeout() {
		return this.sessionTimeout;
	}
	
	@Override
	public String getIp() {
		return this.ip;
	}

	@Override
	public String getPort() {
		return this.port;
	}

	@Override
	public String getLat() {
		return this.lat;
	}

	@Override
	public String getLon() {
		return this.lon;
	}

	@Override
	public String getRemark() {
		return this.remark;
	}

	@Override
	public String getAlterDate() {
		return this.alterDate;
	}

	@Override
	public String getRegDate() {
		return this.regDate;
	}

}
