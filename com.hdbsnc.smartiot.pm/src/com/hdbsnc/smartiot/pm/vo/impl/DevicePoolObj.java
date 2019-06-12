////////////////////////////////////////////////////////////////////
// Copyright 2015,2016 <hd-bsnc.com>. All rights reserved.
////////////////////////////////////////////////////////////////////
package com.hdbsnc.smartiot.pm.vo.impl;

import java.io.Serializable;

import com.hdbsnc.smartiot.common.pm.vo.IDevicePoolObj;
import com.hdbsnc.smartiot.pm.constant.IConst;

public class DevicePoolObj extends CacheManagerObj implements IDevicePoolObj,
		Serializable {

	private static final long serialVersionUID = 1L;

	String devPoolId;
	String devPoolNm;
	String remark;
	String alterDate;
	String regDate;

	/**
	 * 생성자
	 */
	public DevicePoolObj() {
		this.devPoolId = IConst.EMPTY_STRING;
		this.devPoolNm = IConst.EMPTY_STRING;
		this.remark = IConst.EMPTY_STRING;
		this.alterDate = IConst.EMPTY_STRING;
		this.regDate = IConst.EMPTY_STRING;
	}

	@Override
	public String getDevPoolId() {
		return this.devPoolId;
	}

	@Override
	public String getDevPoolNm() {
		return this.devPoolNm;
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
