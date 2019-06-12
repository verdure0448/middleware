package com.hdbsnc.smartiot.pm.vo.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hdbsnc.smartiot.common.pm.vo.IModifyUserObj;
import com.hdbsnc.smartiot.common.pm.vo.IUserObj;
import com.hdbsnc.smartiot.pm.cache.NitroCacheManager;
import com.hdbsnc.smartiot.pm.constant.IConst;

public class ModifyUserObj implements IModifyUserObj {

	private UserObj inVo;
	private List<UserObj> outVoList;

	public ModifyUserObj() {
		this.inVo = new UserObj();
		this.outVoList = new ArrayList<UserObj>();
	}

	public ModifyUserObj(IUserObj vo) {
		this.inVo = new UserObj();
		this.outVoList = new ArrayList<UserObj>();

		this.inVo.userId = vo.getUserId();
		this.inVo.userPoolId = vo.getUserPoolId();
		this.inVo.userPw = vo.getUserPw();
		this.inVo.userType = vo.getUserType();
		this.inVo.userNm = vo.getUserNm();
		this.inVo.compNm = vo.getCompNm();
		this.inVo.deptNm = vo.getDeptNm();
		this.inVo.titleNm = vo.getTitleNm();
		this.inVo.remark = vo.getRemark();
		this.inVo.alterDate = vo.getAlterDate();
		this.inVo.regDate = vo.getRegDate();
	}

	@Override
	public void select() throws Exception {
		this.outVoList.clear();
		this.outVoList.add(NitroCacheManager.getUser(this.inVo.userId));
	}

	@Override
	public void selectByUserPoolId() throws Exception {
		this.outVoList.clear();
		this.outVoList = NitroCacheManager.getUserByUserPoolId(this.inVo.userPoolId);
	}
	
	@Override
	public void update() throws Exception {
		this.outVoList.clear();
		this.outVoList.add(NitroCacheManager.setUser(this.inVo));
	}

	@Override
	public void insert() throws Exception {
		this.outVoList.clear();
		this.outVoList.add(NitroCacheManager.putUser(this.inVo));
	}

	@Override
	public void delete() throws Exception {
		this.outVoList.clear();
		this.outVoList.add(NitroCacheManager.removeUser(this.inVo.userId));
	}

	@Override
	public IModifyUserObj userId(String userId) {
		this.inVo.userId = userId;
		return this;
	}

	@Override
	public IModifyUserObj userPoolId(String userPoolId) {
		this.inVo.userPoolId = userPoolId;
		return this;
	}

	@Override
	public IModifyUserObj userPw(String userPw) {
		this.inVo.userPw = userPw;
		return this;
	}

	@Override
	public IModifyUserObj userType(String userType) {
		this.inVo.userType = userType;
		return this;
	}

	@Override
	public IModifyUserObj userNm(String userNm) {
		this.inVo.userNm = userNm;
		return this;
	}

	@Override
	public IModifyUserObj compNm(String compNm) {
		this.inVo.compNm = compNm;
		return this;
	}

	@Override
	public IModifyUserObj deptNm(String deptNm) {
		this.inVo.deptNm = deptNm;
		return this;
	}

	@Override
	public IModifyUserObj titleNm(String title) {
		this.inVo.titleNm = title;
		return this;
	}

	@Override
	public IModifyUserObj remark(String remark) {
		this.inVo.remark = remark;
		return this;
	}

	@Override
	public IModifyUserObj alterDate(String alterDate) {
		this.inVo.alterDate = alterDate;
		return this;
	}

	@Override
	public IModifyUserObj regDate(String regDate) {
		this.inVo.regDate = regDate;
		return this;
	}

	@Override
	public IUserObj getInputVo() {
		return this.inVo;
	}

	@Override
	public IUserObj getResultVo() {
		return this.outVoList.get(0);
	}

	@Override
	public List<UserObj> getResultVoList(){
		return this.outVoList;
	}
	
	public static UserObj createVoFromMap(Map<String, String> map) {
		UserObj resultVo = null;

		if (map != null && map.size() != 0) {
			resultVo = new UserObj();
			resultVo.userId = map.get(IConst.User.C_USER_ID);
			resultVo.userPoolId = map.get(IConst.User.C_USER_POOL_ID);
			resultVo.userPw = map.get(IConst.User.C_USER_PW);
			resultVo.userType = map.get(IConst.User.C_USER_TYPE);
			resultVo.userNm = map.get(IConst.User.C_USER_NAME);
			resultVo.compNm = map.get(IConst.User.C_COMP_NAME);
			resultVo.deptNm = map.get(IConst.User.C_DEPT_NAME);
			resultVo.titleNm = map.get(IConst.User.C_TITLE_NAME);
			resultVo.remark = map.get(IConst.User.C_REMARK);
			resultVo.alterDate = map.get(IConst.User.C_ALTER_DATE);
			resultVo.regDate = map.get(IConst.User.C_REG_DATE);
		}

		return resultVo;
	}

	public static Map<String, String> createMapFromVo(UserObj vo) {
		Map<String, String> resultMap = null;

		if (vo != null) {
			resultMap = new HashMap<String, String>();
			resultMap.put(IConst.User.C_USER_ID, vo.userId);
			resultMap.put(IConst.User.C_USER_POOL_ID, vo.userPoolId);
			resultMap.put(IConst.User.C_USER_PW, vo.userPw);
			resultMap.put(IConst.User.C_USER_TYPE, vo.userType);
			resultMap.put(IConst.User.C_USER_NAME, vo.userNm);
			resultMap.put(IConst.User.C_COMP_NAME, vo.compNm);
			resultMap.put(IConst.User.C_DEPT_NAME, vo.deptNm);
			resultMap.put(IConst.User.C_TITLE_NAME, vo.titleNm);
			resultMap.put(IConst.User.C_REMARK, vo.remark);
			resultMap.put(IConst.User.C_ALTER_DATE, vo.alterDate);
			resultMap.put(IConst.User.C_REG_DATE, vo.regDate);
		}

		return resultMap;
	}

}
