////////////////////////////////////////////////////////////////////
// Copyright 2015,2016 <hd-bsnc.com>. All rights reserved.
////////////////////////////////////////////////////////////////////
package com.hdbsnc.smartiot.common.pm.vo;

public interface IUserPoolObj {

	/** 사용자풀ID */
	public String getUserPoolId();
	
	/** 사용자풀명 */
	public String getUserPoolNm();

	/** 비고 */
	public String getRemark();

	/** 변경일시 */
	public String getAlterDate();

	/** 등록일시 */
	public String getRegDate();
}
