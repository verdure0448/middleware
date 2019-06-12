////////////////////////////////////////////////////////////////////
// Copyright 2015,2016 <hd-bsnc.com>. All rights reserved.
////////////////////////////////////////////////////////////////////
package com.hdbsnc.smartiot.common.pm.vo;

public interface IDeviceObj {

	/** 장치ID */
	public String getDevId();

	/** 장치풀ID */
	public String getDevPoolId();

	/** 장치명 */
	public String getDevNm();

	/** 장치구분 */
	public String getDevType();
	
	/** 사용여부 */
	public String getIsUse();

	/** 세션 타임아웃 */
	public String getSessionTimeout();
	
	/** IP */
	public String getIp();

	/** Port */
	public String getPort();

	/** 위도 */
	public String getLat();

	/** 경도 */
	public String getLon();

	/** 비고 */
	public String getRemark();
	
	/** 변경일시 */
	public String getAlterDate();
	
	/** 등록일시 */
	public String getRegDate();

}
