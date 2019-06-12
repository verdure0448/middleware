////////////////////////////////////////////////////////////////////
// Copyright 2015,2016 <hd-bsnc.com>. All rights reserved.
////////////////////////////////////////////////////////////////////
package com.hdbsnc.smartiot.pm.vo.impl;

import java.io.Serializable;

import com.hdbsnc.smartiot.common.pm.vo.IUserObj;

/**
 * 
 * @author KANG
 *
 */
public class UserObj extends CacheManagerObj implements IUserObj,
		Serializable {

	private static final long serialVersionUID = -8777259710076493585L;

	String userId;
	String userPoolId;
	String userPw;
	String userType;
	String userNm;
	String compNm;
	String deptNm;
	String titleNm;
	String remark;
	String alterDate;
	String regDate;

	public UserObj() {

	}

	@Override
	public String getUserId() {
		return this.userId;
	}

	@Override
	public String getUserPoolId() {
		return this.userPoolId;
	}

	@Override
	public String getUserPw() {
		return this.userPw;
	}

	@Override
	public String getUserType() {
		return this.userType;
	}

	@Override
	public String getUserNm() {
		return this.userNm;
	}

	@Override
	public String getCompNm() {
		return this.compNm;
	}

	@Override
	public String getDeptNm() {
		return this.deptNm;
	}

	@Override
	public String getTitleNm() {
		return this.titleNm;
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
