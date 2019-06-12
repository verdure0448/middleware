////////////////////////////////////////////////////////////////////
// Copyright 2015,2016 <hd-bsnc.com>. All rights reserved.
////////////////////////////////////////////////////////////////////
package com.hdbsnc.smartiot.common.pm.vo;

/**
 * 
 * 아답터 인스턴스의 인터페이스 클래스
 * 
 * @author KANG
 *
 */
public interface IInstanceObj {

	/** 인스턴스ID */
	public String getInsId();

	/** 장치풀ID */
	public String getDevPoolId();

	/** 아답터ID */
	public String getAdtId();

	/** 인스턴스명 */
	public String getInsNm();

	/** 인스턴스종류 */
	public String getInsKind();

	/** 디폴트 장치ID */
	public String getDefaultDevId();
	
	/** 인스턴스 구분 */
	public String getInsType();

	/** 사용여부 */
	public String getIsUse();

	/** 섹션타임아웃 */
	public String getSessionTimeout();

	/** 초기기동상태 */
	public String getInitDevStatus();

	/** 아이피 */
	public String getIp();

	/** 포트번호 */
	public String getPort();

	/** url */
	public String getUrl();

	
	/** 위도 */
	public String getLat();

	/** 경도 */
	public String getLon();

	/** 셀프아이디 */
	public String getSelfId();
	
	/** 셀프패스워드 */
	public String getSelfPw();

	/** 비고 */
	public String getRemark();

	/** 변경일시 */
	public String getAlterDate();

	/** 등록일시 */
	public String getRegDate();
}
