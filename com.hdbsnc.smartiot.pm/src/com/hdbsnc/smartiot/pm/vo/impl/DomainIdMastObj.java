////////////////////////////////////////////////////////////////////
// Copyright 2015,2016 <hd-bsnc.com>. All rights reserved.
////////////////////////////////////////////////////////////////////
package com.hdbsnc.smartiot.pm.vo.impl;

import java.io.Serializable;

import com.hdbsnc.smartiot.common.pm.vo.IDomainIdMastObj;
import com.hdbsnc.smartiot.pm.constant.IConst;

public class DomainIdMastObj extends CacheManagerObj implements
		IDomainIdMastObj, Serializable {

	private static final long serialVersionUID = 1L;

	String domainId;
	String domainNm;
	String domainType;
	String remark;
	String alterDate;
	String regDate;

	public DomainIdMastObj() {
		this.domainId = IConst.EMPTY_STRING;
		this.domainNm = IConst.EMPTY_STRING;
		this.domainType = IConst.EMPTY_STRING;
		this.remark = IConst.EMPTY_STRING;
		this.alterDate = IConst.EMPTY_STRING;
		this.regDate = IConst.EMPTY_STRING;
	}

	@Override
	public String getDomainId() {
		return this.domainId;
	}

	@Override
	public String getDomainNm() {
		return this.domainNm;
	}

	@Override
	public String getDomainType() {
		return this.domainType;
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
