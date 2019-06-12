package com.hdbsnc.smartiot.common.pm.vo;

import java.util.List;

public interface IModifyUserFilterObj extends IModifyObj {

	/** 사용자ID */
	public IModifyUserFilterObj userId(String userId);

	/** 권한필터 */
	public IModifyUserFilterObj authFilter(String authFilter);

	/** 비고 */
	public IModifyUserFilterObj remark(String remark);
	
	/** 변경일시 */
	public IModifyUserFilterObj alterDate(String alterDate);

	/** 등록일시 */
	public IModifyUserFilterObj regDate(String regDate);

	/** 입력VO 취득  */
	public IUserFilterObj getInputVo();
	
	/** 결과VO 취득 */
	public IUserFilterObj getResultVo();
	
	public void selectList() throws Exception;

	public List getResultVoList();
}
