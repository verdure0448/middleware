////////////////////////////////////////////////////////////////////
// Copyright 2015,2016 <hd-bsnc.com>. All rights reserved.
////////////////////////////////////////////////////////////////////
package com.hdbsnc.smartiot.common.pm.vo;

/**
 * 도메인 식별자 마스터
 * 
 * @author KANG
 *
 */
public interface IModifyMsgMastObj extends IModifyObj {
	/** 내부코드 */
	public IModifyMsgMastObj innerCode(String innerCode);
	
	/** 외부코드 */
	public IModifyMsgMastObj outerCode(String outerCode);
	
	/** 에러타입 */
	public IModifyMsgMastObj type(String type);

	/** 메세지 */
	public IModifyMsgMastObj msg(String msg);
	
	/** 원인 내용 */
	public IModifyMsgMastObj causeContext(String causeContext);
	
	/** 대처 내용 */
	public IModifyMsgMastObj solutionContext(String solutionContext);
	
	/** 입력VO 취득  */
	public IMsgMastObj getInputVo();
	
	/** 결과VO 취득 */
	public IMsgMastObj getResultVo();
}
