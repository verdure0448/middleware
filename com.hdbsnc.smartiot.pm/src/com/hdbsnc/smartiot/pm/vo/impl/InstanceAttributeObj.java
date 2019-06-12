////////////////////////////////////////////////////////////////////
// Copyright 2015,2016 <hd-bsnc.com>. All rights reserved.
////////////////////////////////////////////////////////////////////
package com.hdbsnc.smartiot.pm.vo.impl;

import java.io.Serializable;

import com.hdbsnc.smartiot.common.pm.vo.IInstanceAttributeObj;
import com.hdbsnc.smartiot.pm.constant.IConst;

/**
 * 
 * 아답터 속성 클래스
 * 
 * @author KANG
 *
 */
public class InstanceAttributeObj extends CacheManagerObj implements
		IInstanceAttributeObj, Serializable {

	private static final long serialVersionUID = 1L;

	String insId;
	String key;
	String dsct;
	String value;
	String valueType;
	String init;
	String remark;
	String alterDate;
	String regDate;

	public InstanceAttributeObj() {
		this.insId = IConst.EMPTY_STRING;
		this.key = IConst.EMPTY_STRING;
		this.dsct = IConst.EMPTY_STRING;
		this.value = IConst.EMPTY_STRING;
		this.valueType = IConst.EMPTY_STRING;
		this.init = IConst.EMPTY_STRING;
		this.remark = IConst.EMPTY_STRING;
		this.alterDate = IConst.EMPTY_STRING;
		this.regDate = IConst.EMPTY_STRING;

	}

	@Override
	public String getInsId() {
		return this.insId;
	}

	@Override
	public String getKey() {
		return this.key;
	}
	
	@Override
	public String getDsct() {
		return this.dsct;
	}
	
	@Override
	public String getValue() {
		return this.value;
	}
	
	@Override
	public String getValueType() {
		return this.valueType;
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

	@Override
	public String getInit() {
		return this.init;
	}


}
