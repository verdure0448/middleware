package com.hdbsnc.smartiot.pm.vo.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hdbsnc.smartiot.common.pm.vo.IModifyUserPoolObj;
import com.hdbsnc.smartiot.common.pm.vo.IUserPoolObj;
import com.hdbsnc.smartiot.pm.cache.NitroCacheManager;
import com.hdbsnc.smartiot.pm.constant.IConst;

public class ModifyUserPoolObj implements IModifyUserPoolObj {

	private UserPoolObj inVo;

	private List<UserPoolObj> outVoList;

	public ModifyUserPoolObj() {
		this.inVo = new UserPoolObj();
		this.outVoList = new ArrayList<UserPoolObj>();
	}

	public ModifyUserPoolObj(IUserPoolObj vo) {
		this.inVo = new UserPoolObj();
		this.outVoList = new ArrayList<UserPoolObj>();

		this.inVo.userPoolId = vo.getUserPoolId();
		this.inVo.userPoolNm = vo.getUserPoolNm();
		this.inVo.remark = vo.getRemark();
		this.inVo.alterDate = vo.getAlterDate();
		this.inVo.regDate = vo.getRegDate();
	}

	@Override
	public void select() throws Exception {
		this.outVoList.clear();
		this.outVoList.add(NitroCacheManager.getUserPool(this.inVo.userPoolId));
	}

	@Override
	public void selectAll() throws Exception{
		this.outVoList.clear();
		this.outVoList = NitroCacheManager.getAllUserPool();
	}
	
	@Override
	public void update() throws Exception {
		this.outVoList.clear();
		this.outVoList.add(NitroCacheManager.setUserPool(this.inVo));
	}

	@Override
	public void insert() throws Exception {
		this.outVoList.clear();
		this.outVoList.add(NitroCacheManager.putUserPool(this.inVo));
	}

	@Override
	public void delete() throws Exception {
		this.outVoList.clear();
		this.outVoList.add(NitroCacheManager.removeUserPool(this.inVo.userPoolId));
	}

	@Override
	public IModifyUserPoolObj userPoolId(String userPoolId) {
		this.inVo.userPoolId = userPoolId;
		return this;
	}

	@Override
	public IModifyUserPoolObj userPoolNm(String userPoolNm) {
		this.inVo.userPoolNm = userPoolNm;
		return this;
	}

	@Override
	public IModifyUserPoolObj remark(String remark) {
		this.inVo.remark = remark;
		return this;
	}

	@Override
	public IModifyUserPoolObj alterDate(String alterDate) {
		this.inVo.alterDate = alterDate;
		return this;
	}

	@Override
	public IModifyUserPoolObj regDate(String regDate) {
		this.inVo.regDate = regDate;
		return this;
	}

	@Override
	public IUserPoolObj getInputVo() {
		return this.inVo;
	}

	@Override
	public IUserPoolObj getResultVo() {
		return this.outVoList.get(0);
	}
	
	@Override
	public List<UserPoolObj> getResultVoList() {
		
		return this.outVoList;
	}

	public static UserPoolObj createVoFromMap(Map<String, String> map) {
		UserPoolObj resultVo = null;

		if (map != null && map.size() != 0) {
			resultVo = new UserPoolObj();
			resultVo.userPoolId = map.get(IConst.UserPool.C_USER_POOL_ID);
			resultVo.userPoolNm = map.get(IConst.UserPool.C_USER_POOL_NAME);
			resultVo.remark = map.get(IConst.UserPool.C_REMARK);
			resultVo.alterDate = map.get(IConst.UserPool.C_ALTER_DATE);
			resultVo.regDate = map.get(IConst.UserPool.C_REG_DATE);
		}

		return resultVo;
	}

	public static Map<String, String> createMapFromVo(UserPoolObj vo) {
		Map<String, String> resultMap = null;

		if (vo != null) {
			resultMap = new HashMap<String, String>();
			resultMap.put(IConst.UserPool.C_USER_POOL_ID, vo.userPoolId);
			resultMap.put(IConst.UserPool.C_USER_POOL_NAME, vo.userPoolNm);
			resultMap.put(IConst.UserPool.C_REMARK, vo.remark);
			resultMap.put(IConst.UserPool.C_ALTER_DATE, vo.alterDate);
			resultMap.put(IConst.UserPool.C_REG_DATE, vo.regDate);
		}

		return resultMap;
	}

}
