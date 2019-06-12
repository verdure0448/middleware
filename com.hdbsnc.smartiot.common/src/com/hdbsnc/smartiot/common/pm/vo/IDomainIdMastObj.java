////////////////////////////////////////////////////////////////////
// Copyright 2015,2016 <hd-bsnc.com>. All rights reserved.
////////////////////////////////////////////////////////////////////
package com.hdbsnc.smartiot.common.pm.vo;

/**
 * 도메인 식별자 마스터
 * 
 * @author KANG
 *
 */
public interface IDomainIdMastObj {

	/** 도메인ID */
	public String getDomainId();

	/** 도메인명 */
	public String getDomainNm();

	/** 도메인구분 */
	public String getDomainType();

	/** 비고 */
	public String getRemark();

	/** 변경일시 */
	public String getAlterDate();

	/** 등록일시 */
	public String getRegDate();
}
