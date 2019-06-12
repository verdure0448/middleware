////////////////////////////////////////////////////////////////////
// Copyright 2015,2016 <hd-bsnc.com>. All rights reserved.
////////////////////////////////////////////////////////////////////
package com.hdbsnc.smartiot.common.pm.vo;

public interface IDevicePoolObj {

	/** 장치풀ID */
	public String getDevPoolId();

	/** 장치풀명 */
	public String getDevPoolNm();

	/** 비고 */
	public String getRemark();

	/** 변경일시 */
	public String getAlterDate();
	
	/** 등록일시 */
	public String getRegDate();
}
