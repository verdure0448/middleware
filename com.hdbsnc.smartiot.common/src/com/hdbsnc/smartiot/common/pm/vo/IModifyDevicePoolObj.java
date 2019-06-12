////////////////////////////////////////////////////////////////////
// Copyright 2015,2016 <hd-bsnc.com>. All rights reserved.
////////////////////////////////////////////////////////////////////
package com.hdbsnc.smartiot.common.pm.vo;

import java.util.List;



public interface IModifyDevicePoolObj extends IModifyObj {

	/** 장치풀ID */
	public IModifyDevicePoolObj devPoolId(String devPoolId);

	/** 장치풀명 */
	public IModifyDevicePoolObj devPoolNm(String devPoolNm);

	/** 비고 */
	public IModifyDevicePoolObj remark(String remark);

	/** 변경일시 */
	public IModifyDevicePoolObj alterDate(String alterDate);

	/** 등록일시 */
	public IModifyDevicePoolObj regDate(String regDate);

	/** 입력VO 취득  */
	public IDevicePoolObj getInputVo();
	
	/** 결과VO 취득 */
	public IDevicePoolObj getResultVo();
	
	/** 전체 조회 */
	public void selectAll() throws Exception;
	
	public List getResultVoList();
}
