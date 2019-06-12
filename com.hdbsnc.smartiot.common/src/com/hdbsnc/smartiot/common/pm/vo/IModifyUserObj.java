////////////////////////////////////////////////////////////////////
// Copyright 2015,2016 <hd-bsnc.com>. All rights reserved.
////////////////////////////////////////////////////////////////////
package com.hdbsnc.smartiot.common.pm.vo;

import java.util.List;

public interface IModifyUserObj extends IModifyObj {

	/** 사용자ID */
	public IModifyUserObj userId(String userId);

	/** 사용자풀ID */
	public IModifyUserObj userPoolId(String userPoolId);

	/** 사용자암호 */
	public IModifyUserObj userPw(String userPw);

	/** 사용자구분 */
	public IModifyUserObj userType(String userType);

	/** 사용자명 */
	public IModifyUserObj userNm(String userNm);

	/** 회사명 */
	public IModifyUserObj compNm(String compNm);

	/** 부서명 */
	public IModifyUserObj deptNm(String deptNm);

	/** 직책 */
	public IModifyUserObj titleNm(String titleNm);

	/** 비고 */
	public IModifyUserObj remark(String remark);

	/** 변경일시 */
	public IModifyUserObj alterDate(String alterDate);

	/** 등록일시 */
	public IModifyUserObj regDate(String regDate);

	/** 입력VO 취득  */
	public IUserObj getInputVo();
	
	/** 결과VO 취득 */
	public IUserObj getResultVo();
	
	public void selectByUserPoolId() throws Exception;
	
	public List getResultVoList();
	
}
