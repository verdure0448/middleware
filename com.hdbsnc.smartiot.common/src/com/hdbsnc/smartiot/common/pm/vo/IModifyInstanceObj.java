////////////////////////////////////////////////////////////////////
// Copyright 2015,2016 <hd-bsnc.com>. All rights reserved.
////////////////////////////////////////////////////////////////////
package com.hdbsnc.smartiot.common.pm.vo;

import java.util.List;

/**
 * 
 * 아답터 인스턴스의 인터페이스 클래스
 * 
 * @author KANG
 *
 */
public interface IModifyInstanceObj extends IModifyObj {
	/** 인스턴스ID */
	public IModifyInstanceObj insId(String insId);

	/** 장치풀ID */
	public IModifyInstanceObj devPoolId(String devPoolId);

	/** 아답터ID */
	public IModifyInstanceObj adtId(String adtId);

	/** 인스턴스명 */
	public IModifyInstanceObj insNm(String insNm);

	/** 인스턴스종류 */
	public IModifyInstanceObj insKind(String insKind);
	
	/** 디폴트 장치ID */
	public IModifyInstanceObj defaultDevId(String defaultDevId);

	/** 인스턴스 구분 */
	public IModifyInstanceObj insType(String insType);

	/** 사용여부 */
	public IModifyInstanceObj isUse(String isUse);

	/** 섹션타임아웃 */
	public IModifyInstanceObj sessionTimeout(String sessionTimeout);

	/** 초기기동상태 */
	public IModifyInstanceObj initDevStatus(String initDevStatus);

	/** 아이피 */
	public IModifyInstanceObj ip(String ip);

	/** 포트번호 */
	public IModifyInstanceObj port(String port);

	/** url */
	public IModifyInstanceObj url(String url);
	
	/** 위도 */
	public IModifyInstanceObj lat(String lat);

	/** 경도 */
	public IModifyInstanceObj lon(String lon);

	/** 장치 아이디 */
	public IModifyInstanceObj selfId(String selfId);

	/** 장치 암호 */
	public IModifyInstanceObj selfPw(String selfPw);

	/** 비고 */
	public IModifyInstanceObj remark(String remark);

	/** 변경일시 */
	public IModifyInstanceObj alterDate(String alterDate);

	/** 등록일시 */
	public IModifyInstanceObj regDate(String regDate);

	/** 입력 VO 취득  */
	public IInstanceObj getInputVo();
	
	/** 결과 VO 취득 */
	public IInstanceObj getResultVo();
	
	/** 결과 VO LIST 취득 */
	public List getResultVoList();
	
	/** 장치풀ID로 조회 */
	public void selectByDevPoolId() throws Exception;
	
	/** 아답터ID로 조회 */
	public void selectByAdtId() throws Exception;
	
	public void selectByDefaultDevId() throws Exception;
}
