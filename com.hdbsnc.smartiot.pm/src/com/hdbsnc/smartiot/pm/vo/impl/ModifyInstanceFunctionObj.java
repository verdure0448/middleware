////////////////////////////////////////////////////////////////////
// Copyright 2015,2016 <hd-bsnc.com>. All rights reserved.
////////////////////////////////////////////////////////////////////
package com.hdbsnc.smartiot.pm.vo.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hdbsnc.smartiot.common.pm.vo.IInstanceFunctionObj;
import com.hdbsnc.smartiot.common.pm.vo.IModifyInstanceFunctionObj;
import com.hdbsnc.smartiot.pm.cache.NitroCacheManager;
import com.hdbsnc.smartiot.pm.constant.IConst;

/**
 * 
 * 아답터 기능 클래스
 * 
 * @author KANG
 *
 */
public class ModifyInstanceFunctionObj implements IModifyInstanceFunctionObj {

	private InstanceFunctionObj inVo;

	private List<InstanceFunctionObj> outVoList;

	public ModifyInstanceFunctionObj() {
		this.inVo = new InstanceFunctionObj();
		this.outVoList = new ArrayList<InstanceFunctionObj>();
	}

	public ModifyInstanceFunctionObj(IInstanceFunctionObj vo) {
		this.inVo = new InstanceFunctionObj();
		this.outVoList = new ArrayList<InstanceFunctionObj>();

		this.inVo.insId = vo.getInsId();
		this.inVo.key = vo.getKey();
		this.inVo.dsct = vo.getDsct();
		this.inVo.contType = vo.getContType();
		this.inVo.param1 = vo.getParam1();
		this.inVo.param2 = vo.getParam2();
		this.inVo.param3 = vo.getParam3();
		this.inVo.param4 = vo.getParam4();
		this.inVo.param5 = vo.getParam5();
		this.inVo.paramType1 = vo.getParamType1();
		this.inVo.paramType2 = vo.getParamType2();
		this.inVo.paramType3 = vo.getParamType3();
		this.inVo.paramType4 = vo.getParamType4();
		this.inVo.paramType5 = vo.getParamType5();
		this.inVo.remark = vo.getRemark();
		this.inVo.alterDate = vo.getAlterDate();
		this.inVo.regDate = vo.getRegDate();
	}

	@Override
	public void select() throws Exception {
		this.outVoList.clear();
		this.outVoList.add(NitroCacheManager.getInstanceFunction(this.inVo.insId,
				this.inVo.key));
		return;
	}

	@Override
	public void selectList() throws Exception {
		this.outVoList.clear();
		this.outVoList = NitroCacheManager
				.getInstanceFunctionList(this.inVo.insId);
		return;
	}

	@Override
	public void update() throws Exception {
		this.outVoList.clear();
		this.outVoList.add(NitroCacheManager.setInstanceFunction(this.inVo));
		return;
	}

	@Override
	public void insert() throws Exception {
		this.outVoList.clear();
		this.outVoList.add(NitroCacheManager.putInstanceFunction(this.inVo));
		return;
	}

	@Override
	public void delete() throws Exception {
		this.outVoList.clear();
		this.outVoList.add(NitroCacheManager.removeInstanceFunction(this.inVo.insId,
				this.inVo.key));
		return;
	}

	@Override
	public ModifyInstanceFunctionObj insId(String insId) {
		this.inVo.insId = insId;
		return this;
	}

	@Override
	public IModifyInstanceFunctionObj key(String key) {
		this.inVo.key = key;
		return this;
	}
	
	@Override
	public IModifyInstanceFunctionObj dsct(String dsct) {
		this.inVo.dsct = dsct;
		return this;
	}
	
	@Override
	public IModifyInstanceFunctionObj contType(String contType) {
		this.inVo.contType = contType;
		return this;
	}

	@Override
	public IModifyInstanceFunctionObj param1(String param1) {
		this.inVo.param1 = param1;
		return this;
	}

	@Override
	public IModifyInstanceFunctionObj param2(String param2) {
		this.inVo.param2 = param2;
		return this;
	}

	@Override
	public IModifyInstanceFunctionObj param3(String param3) {
		this.inVo.param3 = param3;
		return this;
	}

	@Override
	public IModifyInstanceFunctionObj param4(String param4) {
		this.inVo.param4 = param4;
		return this;
	}

	@Override
	public IModifyInstanceFunctionObj param5(String param5) {
		this.inVo.param5 = param5;
		return this;
	}

	@Override
	public IModifyInstanceFunctionObj paramType1(String paramType1) {
		this.inVo.paramType1 = paramType1;
		return this;
	}

	@Override
	public IModifyInstanceFunctionObj paramType2(String paramType2) {
		this.inVo.paramType2 = paramType2;
		return this;
	}

	@Override
	public IModifyInstanceFunctionObj paramType3(String paramType3) {
		this.inVo.paramType3 = paramType3;
		return this;
	}

	@Override
	public IModifyInstanceFunctionObj paramType4(String paramType4) {
		this.inVo.paramType4 = paramType4;
		return this;
	}

	@Override
	public IModifyInstanceFunctionObj paramType5(String paramType5) {
		this.inVo.paramType5 = paramType5;
		return this;
	}
	
	@Override
	public ModifyInstanceFunctionObj remark(String remark) {
		this.inVo.remark = remark;
		return this;
	}

	@Override
	public ModifyInstanceFunctionObj alterDate(String alterDate) {
		this.inVo.alterDate = alterDate;
		return this;
	}

	@Override
	public ModifyInstanceFunctionObj regDate(String regDate) {
		this.inVo.regDate = regDate;
		return this;
	}

	@Override
	public InstanceFunctionObj getInputVo() {
		return this.inVo;
	}

	@Override
	public InstanceFunctionObj getResultVo() {
		return this.outVoList.get(0);
	}
	
	@Override
	public List<InstanceFunctionObj> getResultVoList() {
		return this.outVoList;
	}

	public static InstanceFunctionObj createVoFromMap(Map<String, String> map) {
		InstanceFunctionObj resultVo = null;

		if (map != null && map.size() != 0) {
			resultVo = new InstanceFunctionObj();
			resultVo.insId = map.get(IConst.InstanceFunction.C_INS_ID);
			resultVo.key = map.get(IConst.InstanceFunction.C_KEY);
			resultVo.dsct = map.get(IConst.InstanceFunction.C_DSCT);
			resultVo.contType = map.get(IConst.InstanceFunction.C_CONT_TYPE);
			resultVo.param1 = map.get(IConst.InstanceFunction.C_PARAM1);
			resultVo.param2 = map.get(IConst.InstanceFunction.C_PARAM2);
			resultVo.param3 = map.get(IConst.InstanceFunction.C_PARAM3);
			resultVo.param4 = map.get(IConst.InstanceFunction.C_PARAM4);
			resultVo.param5 = map.get(IConst.InstanceFunction.C_PARAM5);
			resultVo.paramType1 = map.get(IConst.InstanceFunction.C_PARAM_TYPE1);
			resultVo.paramType2 = map.get(IConst.InstanceFunction.C_PARAM_TYPE2);
			resultVo.paramType3 = map.get(IConst.InstanceFunction.C_PARAM_TYPE3);
			resultVo.paramType4 = map.get(IConst.InstanceFunction.C_PARAM_TYPE4);
			resultVo.paramType5 = map.get(IConst.InstanceFunction.C_PARAM_TYPE5);
			resultVo.remark = map.get(IConst.InstanceFunction.C_REMARK);
			resultVo.alterDate = map.get(IConst.InstanceFunction.C_ALTER_DATE);
			resultVo.regDate = map.get(IConst.InstanceFunction.C_REG_DATE);
		}

		return resultVo;
	}

	public static Map<String, String> createMapFromVo(InstanceFunctionObj vo) {
		Map<String, String> resultMap = null;

		if (vo != null) {
			resultMap = new HashMap<String, String>();
			resultMap.put(IConst.InstanceFunction.C_INS_ID, vo.insId);
			resultMap.put(IConst.InstanceFunction.C_KEY, vo.key);
			resultMap.put(IConst.InstanceFunction.C_DSCT, vo.dsct);
			resultMap.put(IConst.InstanceFunction.C_CONT_TYPE, vo.contType);
			resultMap.put(IConst.InstanceFunction.C_PARAM1, vo.param1);
			resultMap.put(IConst.InstanceFunction.C_PARAM2, vo.param2);
			resultMap.put(IConst.InstanceFunction.C_PARAM3, vo.param3);
			resultMap.put(IConst.InstanceFunction.C_PARAM4, vo.param4);
			resultMap.put(IConst.InstanceFunction.C_PARAM5, vo.param5);
			resultMap.put(IConst.InstanceFunction.C_PARAM_TYPE1, vo.paramType1);
			resultMap.put(IConst.InstanceFunction.C_PARAM_TYPE2, vo.paramType2);
			resultMap.put(IConst.InstanceFunction.C_PARAM_TYPE3, vo.paramType3);
			resultMap.put(IConst.InstanceFunction.C_PARAM_TYPE4, vo.paramType4);
			resultMap.put(IConst.InstanceFunction.C_PARAM_TYPE5, vo.paramType5);
			resultMap.put(IConst.InstanceFunction.C_REMARK, vo.remark);
			resultMap.put(IConst.InstanceFunction.C_ALTER_DATE, vo.alterDate);
			resultMap.put(IConst.InstanceFunction.C_REG_DATE, vo.regDate);
		}

		return resultMap;
	}

}
