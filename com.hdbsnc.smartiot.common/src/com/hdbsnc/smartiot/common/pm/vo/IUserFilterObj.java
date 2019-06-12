package com.hdbsnc.smartiot.common.pm.vo;

public interface IUserFilterObj {

	/** 사용자ID */
	public String getUserId();

	/** 권한필터 */
	public String getAuthFilter();

	/** 비고 */
	public String getRemark();

	/** 변경일시 */
	public String getAlterDate();

	/** 등록일시 */
	public String getRegDate();

}
