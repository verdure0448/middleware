////////////////////////////////////////////////////////////////////
// Copyright 2015,2016 <hd-bsnc.com>. All rights reserved.
////////////////////////////////////////////////////////////////////
package com.hdbsnc.smartiot.common.pm.vo;

import java.util.List;

public interface IModifyDeviceObj extends IModifyObj {

	/** 장치ID */
	public IModifyDeviceObj devId(String devId);

	/** 장치풀ID */
	public IModifyDeviceObj devPoolId(String devPoolId);

	/** 장치명 */
	public IModifyDeviceObj devNm(String devNm);

	/** 장치구분 */
	public IModifyDeviceObj devType(String devType);
	
	/** 사용여부 */
	public IModifyDeviceObj isUse(String isUse);

	public IModifyDeviceObj sessionTimeout(String sessionTimeout);
	
	/** IP */
	public IModifyDeviceObj ip(String ip);

	/** Port */
	public IModifyDeviceObj port(String port);

	/** 위도 */
	public IModifyDeviceObj lat(String lat);

	/** 경도 */
	public IModifyDeviceObj lon(String lon);
	
	/** 비고 */
	public IModifyDeviceObj remark(String remark);

	/** 변경일시 */
	public IModifyDeviceObj alterDate(String alterDate);

	/** 등록일시 */
	public IModifyDeviceObj regDate(String regDate);

	/** 입력VO 취득  */
	public IDeviceObj getInputVo();
	
	/** 결과VO 취득 */
	public IDeviceObj getResultVo();
	
	public List getResultVoList();
	
	public void selectByDevPoolId() throws Exception;

}
