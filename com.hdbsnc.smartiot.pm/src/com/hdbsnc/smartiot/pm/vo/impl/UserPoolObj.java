package com.hdbsnc.smartiot.pm.vo.impl;

import java.io.Serializable;

import com.hdbsnc.smartiot.common.pm.vo.IUserPoolObj;
import com.hdbsnc.smartiot.pm.constant.IConst;

public class UserPoolObj extends CacheManagerObj implements
		IUserPoolObj, Serializable {

	private static final long serialVersionUID = 3744692818749762378L;

	String userPoolId;
	String userPoolNm;
	String remark;
	String alterDate;
	String regDate;

	public UserPoolObj() {
		this.userPoolId = IConst.EMPTY_STRING;
		this.userPoolNm = IConst.EMPTY_STRING;
		this.remark = IConst.EMPTY_STRING;
		this.alterDate = IConst.EMPTY_STRING;
		this.regDate = IConst.EMPTY_STRING;
	}

	@Override
	public String getUserPoolId() {
		return this.userPoolId;
	}

	@Override
	public String getUserPoolNm() {
		return this.userPoolNm;
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
