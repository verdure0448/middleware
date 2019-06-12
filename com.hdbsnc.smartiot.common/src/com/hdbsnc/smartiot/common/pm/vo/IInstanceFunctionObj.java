////////////////////////////////////////////////////////////////////
// Copyright 2015,2016 <hd-bsnc.com>. All rights reserved.
////////////////////////////////////////////////////////////////////
package com.hdbsnc.smartiot.common.pm.vo;

/**
 * 아답타 인스턴스 기능의 인터페이스 클래스
 * 
 * @author KANG
 *
 */
public interface IInstanceFunctionObj {

	/** 인스턴스ID */
	public String getInsId();

	/** 기능경로 */
	public String getKey();
	
	/** 기능이름 */
	public String getDsct();
	
	/** 기능리턴타입 */
	public String getContType();
	
	/** 파라미터1 */
	public String getParam1();
	
	/** 파라미터2 */
	public String getParam2();
	
	/** 파라미터3 */
	public String getParam3();
	
	/** 파라미터4 */
	public String getParam4();
	
	/** 파라미터5 */
	public String getParam5();
	
	/** 파라미터타입1 */
	public String getParamType1();
	
	/** 파라미터타입2 */
	public String getParamType2();
	
	/** 파라미터타입3 */
	public String getParamType3();
	
	/** 파라미터타입4 */
	public String getParamType4();
	
	/** 파라미터타입5 */
	public String getParamType5();
	
	/** 비고 */
	public String getRemark();

	/** 등록일시 */
	public String getAlterDate();

	/** 등록일시 */
	public String getRegDate();
}
