package com.hdbsnc.smartiot.pm.vo.impl;

import java.io.Serializable;

import com.hdbsnc.smartiot.common.pm.vo.IUserFilterObj;
import com.hdbsnc.smartiot.pm.constant.IConst;

public class UserFilterObj extends CacheManagerObj implements
		IUserFilterObj, Serializable {

	private static final long serialVersionUID = 1L;

	String userId;
	String authFilter;
	String remark;
	String alterDate;
	String regDate;

	public UserFilterObj() {
		this.userId = IConst.EMPTY_STRING;
		this.authFilter = IConst.EMPTY_STRING;
		this.remark = IConst.EMPTY_STRING;
		this.alterDate = IConst.EMPTY_STRING;
		this.regDate = IConst.EMPTY_STRING;
	}

	@Override
	public String getUserId() {
		return this.userId;
	}

	@Override
	public String getAuthFilter() {
		return this.authFilter;
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
