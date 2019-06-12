////////////////////////////////////////////////////////////////////
// Copyright 2015,2016 <hd-bsnc.com>. All rights reserved.
////////////////////////////////////////////////////////////////////
package com.hdbsnc.smartiot.common.pm.vo;

import java.util.List;

/**
 * 도메인 식별자 마스터
 * 
 * @author KANG
 *
 */
public interface IModifyDomainIdMastObj extends IModifyObj {

	/** 도메인ID */
	public IModifyDomainIdMastObj domainId(String domainId);

	/** 도메인명 */
	public IModifyDomainIdMastObj domainNm(String domainNm);

	/** 도메인구분 */
	public IModifyDomainIdMastObj domainType(String domainType);

	/** 비고 */
	public IModifyDomainIdMastObj remark(String remark);

	/** 변경일시 */
	public IModifyDomainIdMastObj alterDate(String alterDate);

	/** 등록일시 */
	public IModifyDomainIdMastObj regDate(String regDate);

	/** 입력VO 취득  */
	public IDomainIdMastObj getInputVo();
	
	/** 결과VO 취득 */
	public IDomainIdMastObj getResultVo();
	
	public void selectByDomainType() throws Exception;
	
	public List getResultVoList();
}
