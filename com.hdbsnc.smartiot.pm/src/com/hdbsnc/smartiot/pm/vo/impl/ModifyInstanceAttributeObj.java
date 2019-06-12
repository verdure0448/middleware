////////////////////////////////////////////////////////////////////
// Copyright 2015,2016 <hd-bsnc.com>. All rights reserved.
////////////////////////////////////////////////////////////////////
package com.hdbsnc.smartiot.pm.vo.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hdbsnc.smartiot.common.pm.vo.IInstanceAttributeObj;
import com.hdbsnc.smartiot.common.pm.vo.IModifyInstanceAttributeObj;
import com.hdbsnc.smartiot.pm.cache.NitroCacheManager;
import com.hdbsnc.smartiot.pm.constant.IConst;

/**
 * 
 * 아답터 속성 클래스
 * 
 * @author KANG
 *
 */
public class ModifyInstanceAttributeObj implements IModifyInstanceAttributeObj {

	private InstanceAttributeObj inVo;

	private List<InstanceAttributeObj> outVoList;

	public ModifyInstanceAttributeObj() {
		this.inVo = new InstanceAttributeObj();
		this.outVoList = new ArrayList<InstanceAttributeObj>();
	}

	public ModifyInstanceAttributeObj(IInstanceAttributeObj vo) {
		this.inVo = new InstanceAttributeObj();
		this.outVoList = new ArrayList<InstanceAttributeObj>();
		
		this.inVo.insId = vo.getInsId();
		this.inVo.key = vo.getKey();
		this.inVo.dsct = vo.getDsct();
		this.inVo.value = vo.getValue();
		this.inVo.valueType = vo.getValueType();
		this.inVo.init = vo.getInit();
		this.inVo.remark = vo.getRemark();
		this.inVo.alterDate = vo.getAlterDate();
		this.inVo.regDate = vo.getRegDate();
	}

	@Override
	public void select() throws Exception {
		this.outVoList.clear();
		this.outVoList.add(NitroCacheManager.getInstanceAttribute(this.inVo.insId,
				this.inVo.key));
		return;
	}

	@Override
	public void selectList() throws Exception {
		this.outVoList.clear();
		this.outVoList = NitroCacheManager
				.getInstanceAttributeList(this.inVo.insId);
		return;
	}

	@Override
	public void update() throws Exception {
		this.outVoList.clear();
		this.outVoList.add(NitroCacheManager.setInstanceAttribute(this.inVo));
		return;
	}

	@Override
	public void insert() throws Exception {
		this.outVoList.clear();
		this.outVoList.add(NitroCacheManager.putInstanceAttribute(this.inVo));
		return;
	}

	@Override
	public void delete() throws Exception {
		this.outVoList.clear();
		this.outVoList.add(NitroCacheManager.removeInstanceAttribute(this.inVo.insId,
				this.inVo.key));
		return;
	}

	@Override
	public ModifyInstanceAttributeObj insId(String insId) {
		this.inVo.insId = insId;
		return this;
	}

	@Override
	public IModifyInstanceAttributeObj key(String key) {
		this.inVo.key = key;
		return this;
	}
	
	@Override
	public ModifyInstanceAttributeObj dsct(String dsct) {
		this.inVo.dsct = dsct;
		return this;
	}
	
	@Override
	public ModifyInstanceAttributeObj value(String value) {
		this.inVo.value = value;
		return this;
	}

	@Override
	public IModifyInstanceAttributeObj valueType(String valueType) {
		this.inVo.valueType = valueType;
		return this;
	}
	
	@Override
	public ModifyInstanceAttributeObj init(String init) {
		this.inVo.init= init;
		return this;
	}
	
	@Override
	public ModifyInstanceAttributeObj remark(String remark) {
		this.inVo.remark = remark;
		return this;
	}

	@Override
	public ModifyInstanceAttributeObj alterDate(String alterDate) {
		this.inVo.alterDate = alterDate;
		return this;
	}

	@Override
	public ModifyInstanceAttributeObj regDate(String regDate) {
		this.inVo.regDate = regDate;
		return this;
	}

	@Override
	public InstanceAttributeObj getInputVo() {
		return this.inVo;
	}

	@Override
	public InstanceAttributeObj getResultVo() {
		return this.outVoList.get(0);
	}
	
	@Override
	public List<InstanceAttributeObj> getResultVoList() {
		return this.outVoList;
	}

	public static InstanceAttributeObj createVoFromMap(Map<String, String> map) {
		InstanceAttributeObj resultVo = null;

		if (map != null && map.size() != 0) {
			resultVo = new InstanceAttributeObj();
			resultVo.insId = map.get(IConst.InstanceAttribute.C_INS_ID);
			resultVo.key = map.get(IConst.InstanceAttribute.C_KEY);
			resultVo.dsct = map.get(IConst.InstanceAttribute.C_DSCT);
			resultVo.value = map.get(IConst.InstanceAttribute.C_VALUE);
			resultVo.valueType = map.get(IConst.InstanceAttribute.C_VALUE_TYPE);
			resultVo.init = map.get(IConst.InstanceAttribute.C_INIT);
			resultVo.remark = map.get(IConst.InstanceAttribute.C_REMARK);
			resultVo.alterDate = map.get(IConst.InstanceAttribute.C_ALTER_DATE);
			resultVo.regDate = map.get(IConst.InstanceAttribute.C_REG_DATE);
		}

		return resultVo;
	}

	public static Map<String, String> createMapFromVo(InstanceAttributeObj vo) {
		Map<String, String> resultMap = null;

		if (vo != null) {
			resultMap = new HashMap<String, String>();
			resultMap.put(IConst.InstanceAttribute.C_INS_ID, vo.insId);
			resultMap.put(IConst.InstanceAttribute.C_KEY, vo.key);
			resultMap.put(IConst.InstanceAttribute.C_DSCT, vo.dsct);
			resultMap.put(IConst.InstanceAttribute.C_VALUE, vo.value);
			resultMap.put(IConst.InstanceAttribute.C_VALUE_TYPE, vo.valueType);
			resultMap.put(IConst.InstanceAttribute.C_INIT, vo.init);
			resultMap.put(IConst.InstanceAttribute.C_REMARK, vo.remark);
			resultMap.put(IConst.InstanceAttribute.C_ALTER_DATE, vo.alterDate);
			resultMap.put(IConst.InstanceAttribute.C_REG_DATE, vo.regDate);
		}

		return resultMap;
	}

}
