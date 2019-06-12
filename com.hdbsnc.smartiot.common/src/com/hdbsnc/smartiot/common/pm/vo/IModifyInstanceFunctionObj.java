////////////////////////////////////////////////////////////////////
// Copyright 2015,2016 <hd-bsnc.com>. All rights reserved.
////////////////////////////////////////////////////////////////////
package com.hdbsnc.smartiot.common.pm.vo;

import java.util.List;

/**
 * 아답타 인스턴스 기능의 인터페이스 클래스
 * 
 * @author KANG
 *
 */
public interface IModifyInstanceFunctionObj extends IModifyObj {

	/** 인스턴스ID */
	public IModifyInstanceFunctionObj insId(String insId);

	/** 기능키 */
	public IModifyInstanceFunctionObj key(String key);
	
	/** 기능이름 */
	public IModifyInstanceFunctionObj dsct(String dsct);

	/** 컨텐츠타입 */
	public IModifyInstanceFunctionObj contType(String contType);

	/** 파라미터1 */
	public IModifyInstanceFunctionObj param1(String param1);

	/** 파라미터2 */
	public IModifyInstanceFunctionObj param2(String param2);

	/** 파라미터3 */
	public IModifyInstanceFunctionObj param3(String param3);

	/** 파라미터4 */
	public IModifyInstanceFunctionObj param4(String param4);

	/** 파라미터5 */
	public IModifyInstanceFunctionObj param5(String param5);

	/** 파라미터타입1 */
	public IModifyInstanceFunctionObj paramType1(String paramType1);

	/** 파라미터타입2 */
	public IModifyInstanceFunctionObj paramType2(String paramType2);

	/** 파라미터타입3 */
	public IModifyInstanceFunctionObj paramType3(String paramType3);

	/** 파라미터타입4 */
	public IModifyInstanceFunctionObj paramType4(String paramType4);

	/** 파라미터타입5 */
	public IModifyInstanceFunctionObj paramType5(String paramType5);

	/** 비고 */
	public IModifyInstanceFunctionObj remark(String remark);

	/** 등록일시 */
	public IModifyInstanceFunctionObj alterDate(String alterDate);

	/** 등록일시 */
	public IModifyInstanceFunctionObj regDate(String regDate);

	/** 입력VO 취득 */
	public IInstanceFunctionObj getInputVo();

	/** 결과VO 취득 */
	public IInstanceFunctionObj getResultVo();

	public void selectList() throws Exception;

	public List getResultVoList();

}
