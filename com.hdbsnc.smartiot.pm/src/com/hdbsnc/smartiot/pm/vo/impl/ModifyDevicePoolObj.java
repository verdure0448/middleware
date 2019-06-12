////////////////////////////////////////////////////////////////////
// Copyright 2015,2016 <hd-bsnc.com>. All rights reserved.
////////////////////////////////////////////////////////////////////
package com.hdbsnc.smartiot.pm.vo.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hdbsnc.smartiot.common.pm.vo.IDevicePoolObj;
import com.hdbsnc.smartiot.common.pm.vo.IModifyDevicePoolObj;
import com.hdbsnc.smartiot.pm.cache.NitroCacheManager;
import com.hdbsnc.smartiot.pm.constant.IConst;

public class ModifyDevicePoolObj implements IModifyDevicePoolObj {

	private DevicePoolObj inVo;
	private List<DevicePoolObj> outVoList;

	public ModifyDevicePoolObj() {
		this.inVo = new DevicePoolObj();
		this.outVoList = new ArrayList<DevicePoolObj>();
	}

	public ModifyDevicePoolObj(IDevicePoolObj vo) {
		this.inVo = new DevicePoolObj();
		this.outVoList = new ArrayList<DevicePoolObj>();

		this.inVo.devPoolId = vo.getDevPoolId();
		this.inVo.devPoolNm = vo.getDevPoolNm();
		this.inVo.remark = vo.getRemark();
		this.inVo.alterDate = vo.getAlterDate();
		this.inVo.regDate = vo.getRegDate();
	}

	@Override
	public void select() throws Exception {
		this.outVoList.clear();
		this.outVoList.add(NitroCacheManager.getDevicePool(this.inVo.devPoolId));
	}
	
	@Override
	public void selectAll() throws Exception {
		this.outVoList.clear();
		this.outVoList = NitroCacheManager.getAllDevicePool();
	}

	@Override
	public void update() throws Exception {
		this.outVoList.clear();
		this.outVoList.add(NitroCacheManager.setDevicePool(this.inVo));
	}

	@Override
	public void insert() throws Exception {
		this.outVoList.clear();
		this.outVoList.add(NitroCacheManager.putDevicePool(this.inVo));
	}

	@Override
	public void delete() throws Exception {
		this.outVoList.clear();
		this.outVoList.add(NitroCacheManager.removeDevicePool(this.inVo.devPoolId));
	}

	@Override
	public IModifyDevicePoolObj devPoolId(String devPoolId) {
		this.inVo.devPoolId = devPoolId;
		return this;
	}

	@Override
	public IModifyDevicePoolObj devPoolNm(String devPoolNm) {
		this.inVo.devPoolNm = devPoolNm;
		return this;
	}

	@Override
	public IModifyDevicePoolObj remark(String remark) {
		this.inVo.remark = remark;
		return this;
	}

	@Override
	public IModifyDevicePoolObj alterDate(String alterDate) {
		this.inVo.alterDate = alterDate;
		return this;
	}

	@Override
	public IModifyDevicePoolObj regDate(String regDate) {
		this.inVo.regDate = regDate;
		return this;
	}

	@Override
	public IDevicePoolObj getInputVo() {
		return this.inVo;
	}

	@Override
	public IDevicePoolObj getResultVo() {
		return this.outVoList.get(0);
	}
	
	@Override
	public List<DevicePoolObj> getResultVoList() {
		return this.outVoList;
	}

	public static DevicePoolObj createVoFromMap(Map<String, String> map) {

		DevicePoolObj resultVo = null;
		if (map != null & map.size() != 0) {
			resultVo = new DevicePoolObj();
			resultVo.devPoolId = map.get(IConst.DevicePool.C_DEVICE_POOL_ID);
			resultVo.devPoolNm = map.get(IConst.DevicePool.C_DEVICE_POOL_NAME);
			resultVo.remark = map.get(IConst.DevicePool.C_REMARK);
			resultVo.alterDate = map.get(IConst.DevicePool.C_ALTER_DATE);
			resultVo.regDate = map.get(IConst.DevicePool.C_REG_DATE);
		}
		return resultVo;
	}

	public static Map<String, String> createMapFromVo(DevicePoolObj vo) {
		Map<String, String> resultMap = null;

		if (vo != null) {
			resultMap = new HashMap<String, String>();
			resultMap.put(IConst.DevicePool.C_DEVICE_POOL_ID, vo.devPoolId);
			resultMap.put(IConst.DevicePool.C_DEVICE_POOL_NAME, vo.devPoolNm);
			resultMap.put(IConst.DevicePool.C_REMARK, vo.remark);
			resultMap.put(IConst.DevicePool.C_ALTER_DATE, vo.alterDate);
			resultMap.put(IConst.DevicePool.C_REG_DATE, vo.regDate);
		}

		return resultMap;
	}

}
