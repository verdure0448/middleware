////////////////////////////////////////////////////////////////////
// Copyright 2015,2016 <hd-bsnc.com>. All rights reserved.
////////////////////////////////////////////////////////////////////
package com.hdbsnc.smartiot.pm.vo.impl;

import java.io.Serializable;

import com.hdbsnc.smartiot.common.pm.vo.IInstanceObj;
import com.hdbsnc.smartiot.pm.constant.IConst;

public class InstanceObj extends CacheManagerObj implements
		IInstanceObj, Serializable {

	private static final long serialVersionUID = 1L;

	protected String insId;
	protected String devPoolId;
	protected String adtId;
	protected String insNm;
	protected String insKind;
	protected String defaultDevId;
	protected String insType;
	protected String isUse;
	protected String sessionTimeout;
	protected String initDevStatus;
	protected String ip;
	protected String port;
	protected String url;
	protected String lat;
	protected String lon;
	protected String selfId;
	protected String selfPw;
	protected String remark;
	protected String alterDate;
	protected String regDate;

	// private List<IAdapterAttribute> adtAttList;

	InstanceObj() {
		this.insId = IConst.EMPTY_STRING;
		this.adtId = IConst.EMPTY_STRING;
		this.insNm = IConst.EMPTY_STRING;
		this.insKind = IConst.EMPTY_STRING;
		this.defaultDevId = IConst.EMPTY_STRING;
		this.insType = IConst.EMPTY_STRING;
		this.isUse = IConst.EMPTY_STRING;
		this.sessionTimeout = IConst.EMPTY_STRING;
		this.initDevStatus = IConst.EMPTY_STRING;
		this.ip = IConst.EMPTY_STRING;
		this.port = IConst.EMPTY_STRING;
		this.url = IConst.EMPTY_STRING;
		this.lat = IConst.EMPTY_STRING;
		this.lon = IConst.EMPTY_STRING;
		this.selfId = IConst.EMPTY_STRING;
		this.selfPw = IConst.EMPTY_STRING;
		this.devPoolId = IConst.EMPTY_STRING;
		this.regDate = IConst.EMPTY_STRING;
		this.alterDate = IConst.EMPTY_STRING;
		this.remark = IConst.EMPTY_STRING;
	}

	@Override
	public String getInsId() {
		return this.insId;
	}

	@Override
	public String getAdtId() {
		return this.adtId;
	}

	@Override
	public String getInsNm() {
		return this.insNm;
	}

	@Override
	public String getInsKind() {
		return this.insKind;
	}

	@Override
	public String getDefaultDevId() {
		return defaultDevId;
	}
	
	@Override
	public String getInsType() {
		return this.insType;
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
	public String getInitDevStatus() {
		return this.initDevStatus;
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
	public String getUrl() {
		return this.url;
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
	public String getSelfId() {
		return this.selfId;
	}

	@Override
	public String getSelfPw() {
		return this.selfPw;
	}

	@Override
	public String getDevPoolId() {
		return this.devPoolId;
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
