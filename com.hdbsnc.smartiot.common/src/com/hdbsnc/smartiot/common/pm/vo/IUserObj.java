////////////////////////////////////////////////////////////////////
// Copyright 2015,2016 <hd-bsnc.com>. All rights reserved.
////////////////////////////////////////////////////////////////////
package com.hdbsnc.smartiot.common.pm.vo;

public interface IUserObj {
	/** 사용자ID */
	public String getUserId();

	/** 사용자풀ID */
	public String getUserPoolId();

	/** 사용자암호 */
	public String getUserPw();

	/** 사용자구분 */
	public String getUserType();

	/** 사용자명 */
	public String getUserNm();

	/** 회사명 */
	public String getCompNm();

	/** 부서명 */
	public String getDeptNm();

	/** 직책 */
	public String getTitleNm();

	/** 비고 */
	public String getRemark();

	/** 변경일시 */
	public String getAlterDate();

	/** 등록일시 */
	public String getRegDate();
}
