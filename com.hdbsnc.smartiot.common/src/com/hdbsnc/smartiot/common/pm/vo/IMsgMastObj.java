////////////////////////////////////////////////////////////////////
// Copyright 2015,2016 <hd-bsnc.com>. All rights reserved.
////////////////////////////////////////////////////////////////////
package com.hdbsnc.smartiot.common.pm.vo;

/**
 * 메시지 마스터
 * 
 * @author KANG
 *
 */
public interface IMsgMastObj {
	
	/** 내부코드 */
	public String getInnerCode();
	
	/** 외부코드 */
	public String getOuterCode();
	
	/** 에러타입 */
	public String getType();

	/** 메세지 */
	public String getMsg();
	
	/** 원인 내용 */
	public String getCauseContext();
	
	/** 대처 내용 */
	public String getSolutionContext();
}
