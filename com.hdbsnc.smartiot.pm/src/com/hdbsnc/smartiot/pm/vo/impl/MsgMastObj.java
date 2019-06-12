////////////////////////////////////////////////////////////////////
// Copyright 2015,2016 <hd-bsnc.com>. All rights reserved.
////////////////////////////////////////////////////////////////////
package com.hdbsnc.smartiot.pm.vo.impl;

import java.io.Serializable;

import com.hdbsnc.smartiot.common.pm.vo.IMsgMastObj;
import com.hdbsnc.smartiot.pm.constant.IConst;

public class MsgMastObj extends CacheManagerObj implements IMsgMastObj, Serializable {

	private static final long serialVersionUID = 1L;

	String innerCode;
	String outerCode;
	String type;
	String msg;
	String causeContext;
	String solutionContext;

	public MsgMastObj() {
		this.innerCode = IConst.EMPTY_STRING;
		this.outerCode = IConst.EMPTY_STRING;
		this.type = IConst.EMPTY_STRING;
		this.msg = IConst.EMPTY_STRING;
		this.causeContext = IConst.EMPTY_STRING;
		this.solutionContext = IConst.EMPTY_STRING;
	}

	@Override
	public String getInnerCode() {
		return this.innerCode;
	}
	
	public void setInnerCode(String innerCode) {
		this.innerCode = innerCode;
	}

	@Override
	public String getOuterCode() {
		return this.outerCode;
	}
	
	public void setOuterCode(String outerCode) {
		this.outerCode = outerCode;
	}

	@Override
	public String getType() {
		return this.type;
	}
	
	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String getMsg() {
		return this.msg;
	}
	
	public void setMsg(String msg) {
		this.msg = msg;
	}

	@Override
	public String getCauseContext() {
		return this.causeContext;
	}
	
	public void setCauseContext(String causeContext) {
		this.causeContext = causeContext;
	}

	@Override
	public String getSolutionContext() {
		return this.solutionContext;
	}
	
	public void setSolutionContext(String solutionContext) {
		this.solutionContext = solutionContext;
	}

}
