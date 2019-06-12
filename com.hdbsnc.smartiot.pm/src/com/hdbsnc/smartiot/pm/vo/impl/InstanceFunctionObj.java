////////////////////////////////////////////////////////////////////
// Copyright 2015,2016 <hd-bsnc.com>. All rights reserved.
////////////////////////////////////////////////////////////////////
package com.hdbsnc.smartiot.pm.vo.impl;

import java.io.Serializable;

import com.hdbsnc.smartiot.common.pm.vo.IInstanceFunctionObj;
import com.hdbsnc.smartiot.pm.constant.IConst;

/**
 * 
 * 아답터 기능 클래스
 * 
 * @author KANG
 *
 */
public class InstanceFunctionObj extends CacheManagerObj implements
		IInstanceFunctionObj, Serializable {

	private static final long serialVersionUID = 1L;

	String insId;
	String key;
	String dsct;
	String contType;
	String param1;
	String param2;
	String param3;
	String param4;
	String param5;
	String paramType1;
	String paramType2;
	String paramType3;
	String paramType4;
	String paramType5;
	String remark;
	String alterDate;
	String regDate;

	public InstanceFunctionObj() {
		this.insId = IConst.EMPTY_STRING;
		this.key = IConst.EMPTY_STRING;
		this.dsct = IConst.EMPTY_STRING;
		this.contType = IConst.EMPTY_STRING;
		this.param1 = IConst.EMPTY_STRING;
		this.param2 = IConst.EMPTY_STRING;
		this.param3 = IConst.EMPTY_STRING;
		this.param4 = IConst.EMPTY_STRING;
		this.param5 = IConst.EMPTY_STRING;
		this.paramType1 = IConst.EMPTY_STRING;
		this.paramType2 = IConst.EMPTY_STRING;
		this.paramType3 = IConst.EMPTY_STRING;
		this.paramType4 = IConst.EMPTY_STRING;
		this.paramType5 = IConst.EMPTY_STRING;
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
	public String getContType() {
		return this.contType;
	}

	@Override
	public String getParam1() {
		return this.param1;
	}
	
	@Override
	public String getParam2() {
		return this.param2;
	}
	
	@Override
	public String getParam3() {
		return this.param3;
	}
	
	@Override
	public String getParam4() {
		return this.param4;
	}
	
	@Override
	public String getParam5() {
		return this.param5;
	}
	
	@Override
	public String getParamType1() {
		return this.paramType1;
	}
	
	@Override
	public String getParamType2() {
		return this.paramType2;
	}
	
	@Override
	public String getParamType3() {
		return this.paramType3;
	}
	
	@Override
	public String getParamType4() {
		return this.paramType4;
	}
	
	@Override
	public String getParamType5() {
		return this.paramType5;
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
