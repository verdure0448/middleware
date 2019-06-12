////////////////////////////////////////////////////////////////////
// Copyright 2015,2016 <hd-bsnc.com>. All rights reserved.
////////////////////////////////////////////////////////////////////
package com.hdbsnc.smartiot.common.pm.vo;

/**
 * 아답타 인스턴스 속성의 인터페이스 클래스
 * 
 * @author KANG
 *
 */
public interface IInstanceAttributeObj {

	/** 인스턴스ID */
	public String getInsId();

	/** 속성경로 */
	public String getKey();
	
	/** 속성이름 */
	public String getDsct();
	
	/** 속성값 */
	public String getValue();
	
	/** 속성값타입 */
	public String getValueType();

	/** 자동기동 유무 */
	public String getInit();
	
	/** 비고 */
	public String getRemark();

	/** 등록일시 */
	public String getAlterDate();

	/** 등록일시 */
	public String getRegDate();
}
