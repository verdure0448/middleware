////////////////////////////////////////////////////////////////////
// Copyright 2015,2016 <hd-bsnc.com>. All rights reserved.
////////////////////////////////////////////////////////////////////
package com.hdbsnc.smartiot.pm.vo.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hdbsnc.smartiot.common.pm.vo.IModifyUserFilterObj;
import com.hdbsnc.smartiot.common.pm.vo.IUserFilterObj;
import com.hdbsnc.smartiot.pm.cache.NitroCacheManager;
import com.hdbsnc.smartiot.pm.constant.IConst;

public class ModifyUserFilterObj implements IModifyUserFilterObj {

	private UserFilterObj inVo;

	private List<UserFilterObj> outVoList;

	public ModifyUserFilterObj() {
		this.inVo = new UserFilterObj();
		this.outVoList = new ArrayList<UserFilterObj>();
	}

	public ModifyUserFilterObj(IUserFilterObj vo) {
		this.inVo = new UserFilterObj();
		this.outVoList = new ArrayList<UserFilterObj>();

		this.inVo.userId = vo.getUserId();
		this.inVo.authFilter = vo.getAuthFilter();
		this.inVo.remark = vo.getRemark();
		this.inVo.alterDate = vo.getAlterDate();
		this.inVo.regDate = vo.getRegDate();
	}

	@Override
	public void select() throws Exception {
		this.outVoList.clear();
		this.outVoList.add(NitroCacheManager.getUserFilter(this.inVo.userId,
				this.inVo.authFilter));
	}

	@Override
	public void selectList() throws Exception {
		this.outVoList.clear();
		this.outVoList = NitroCacheManager
				.getUserFilterList(this.inVo.userId);
	}

	@Override
	public void update() throws Exception {
		this.outVoList.clear();
		this.outVoList.add(NitroCacheManager.setUserFilter(this.inVo));
	}

	@Override
	public void insert() throws Exception {
		this.outVoList.clear();
		this.outVoList.add(NitroCacheManager.putUserFilter(this.inVo));
	}

	@Override
	public void delete() throws Exception {
		this.outVoList.clear();
		this.outVoList.add(NitroCacheManager.removeUserFilter(
				this.inVo.userId, this.inVo.authFilter));
	}

	@Override
	public IModifyUserFilterObj userId(String userId) {
		this.inVo.userId = userId;
		return this;
	}

	@Override
	public IModifyUserFilterObj authFilter(String authFilter) {
		this.inVo.authFilter = authFilter;
		return this;
	}

	@Override
	public IModifyUserFilterObj remark(String remark) {
		this.inVo.remark = remark;
		return this;
	}

	@Override
	public IModifyUserFilterObj alterDate(String alterDate) {
		this.inVo.alterDate = alterDate;
		return this;
	}

	@Override
	public IModifyUserFilterObj regDate(String regDate) {
		this.inVo.regDate = regDate;
		return this;
	}

	@Override
	public IUserFilterObj getInputVo() {
		return this.inVo;
	}

	@Override
	public IUserFilterObj getResultVo() {
		return this.outVoList.get(0);
	}

	@Override
	public List<UserFilterObj> getResultVoList() {
		return this.outVoList;
	}
	
	public static UserFilterObj createVoFromMap(Map<String, String> map) {
		UserFilterObj resultVo = null;

		if (map != null && map.size() != 0) {
			resultVo = new UserFilterObj();
			resultVo.userId = map.get(IConst.UserFilter.C_USER_ID);
			resultVo.authFilter = map
					.get(IConst.UserFilter.C_AUTH_FILTER);
			resultVo.remark = map.get(IConst.UserFilter.C_REMARK);
			resultVo.alterDate = map.get(IConst.UserFilter.C_ALTER_DATE);
			resultVo.regDate = map.get(IConst.UserFilter.C_REG_DATE);
		}

		return resultVo;
	}

	public static Map<String, String> createMapFromVo(UserFilterObj vo) {
		Map<String, String> resultMap = null;

		if (vo != null) {
			resultMap = new HashMap<String, String>();
			resultMap.put(IConst.UserFilter.C_USER_ID, vo.userId);
			resultMap
					.put(IConst.UserFilter.C_AUTH_FILTER, vo.authFilter);
			resultMap.put(IConst.UserFilter.C_REMARK, vo.remark);
			resultMap.put(IConst.UserFilter.C_ALTER_DATE, vo.alterDate);
			resultMap.put(IConst.UserFilter.C_REG_DATE, vo.regDate);
		}

		return resultMap;
	}

}
