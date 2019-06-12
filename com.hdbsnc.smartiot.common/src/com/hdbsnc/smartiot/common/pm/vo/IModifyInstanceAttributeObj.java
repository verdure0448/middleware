////////////////////////////////////////////////////////////////////
// Copyright 2015,2016 <hd-bsnc.com>. All rights reserved.
////////////////////////////////////////////////////////////////////
package com.hdbsnc.smartiot.common.pm.vo;

import java.util.List;

/**
 * 아답타 인스턴스 속성의 인터페이스 클래스
 * 
 * @author KANG
 *
 */
public interface IModifyInstanceAttributeObj extends IModifyObj {

	/** 인스턴스ID */
	public IModifyInstanceAttributeObj insId(String insId);

	/** 속성키 */
	public IModifyInstanceAttributeObj key(String attKey);
	
	/** 속성이름 */
	public IModifyInstanceAttributeObj dsct(String dsct);
	
	/** 속성값 */
	public IModifyInstanceAttributeObj value(String attValue);
	
	/** 속성값타입 */
	public IModifyInstanceAttributeObj valueType(String valueType);
	
	/** 초기기동 */
	public IModifyInstanceAttributeObj init(String init);
	
	/** 비고 */
	public IModifyInstanceAttributeObj remark(String remark);

	/** 등록일시 */
	public IModifyInstanceAttributeObj alterDate(String alterDate);

	/** 등록일시 */
	public IModifyInstanceAttributeObj regDate(String regDate);
	
	/** 입력VO 취득  */
	public IInstanceAttributeObj getInputVo();
	
	/** 결과VO 취득 */
	public IInstanceAttributeObj getResultVo();
	
	public void selectList() throws Exception;
	
	public List getResultVoList();

}
