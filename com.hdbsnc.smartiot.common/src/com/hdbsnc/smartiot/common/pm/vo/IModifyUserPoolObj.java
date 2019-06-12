////////////////////////////////////////////////////////////////////
// Copyright 2015,2016 <hd-bsnc.com>. All rights reserved.
////////////////////////////////////////////////////////////////////
package com.hdbsnc.smartiot.common.pm.vo;

import java.util.List;

public interface IModifyUserPoolObj extends IModifyObj {

	/** 사용자풀ID */
	public IModifyUserPoolObj userPoolId(String userPoolId);

	/** 사용자풀명 */
	public IModifyUserPoolObj userPoolNm(String userPoolNm);

	/** 비고 */
	public IModifyUserPoolObj remark(String remark);

	/** 변경일시 */
	public IModifyUserPoolObj alterDate(String alterDate);

	/** 등록일시 */
	public IModifyUserPoolObj regDate(String regDate);

	/** 입력VO 취득  */
	public IUserPoolObj getInputVo();
	
	/** 결과VO 취득 */
	public IUserPoolObj getResultVo();
	
	public void selectAll() throws Exception;
	
	public List getResultVoList();
}
