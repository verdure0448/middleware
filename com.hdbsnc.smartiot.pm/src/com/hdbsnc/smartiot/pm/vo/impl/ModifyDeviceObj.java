////////////////////////////////////////////////////////////////////
// Copyright 2015,2016 <hd-bsnc.com>. All rights reserved.
////////////////////////////////////////////////////////////////////
package com.hdbsnc.smartiot.pm.vo.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hdbsnc.smartiot.common.pm.vo.IDeviceObj;
import com.hdbsnc.smartiot.common.pm.vo.IModifyDeviceObj;
import com.hdbsnc.smartiot.pm.cache.NitroCacheManager;
import com.hdbsnc.smartiot.pm.constant.IConst;

public class ModifyDeviceObj implements IModifyDeviceObj {

	private DeviceObj inVo;

	private List<DeviceObj> outVoList;

	public ModifyDeviceObj() {
		this.inVo = new DeviceObj();
		this.outVoList = new ArrayList<DeviceObj>();
	}

	public ModifyDeviceObj(IDeviceObj vo) {
		this.inVo = new DeviceObj();
		this.outVoList = new ArrayList<DeviceObj>();

		this.inVo.devId = vo.getDevId();
		this.inVo.devPoolId = vo.getDevPoolId();
		this.inVo.devNm = vo.getDevNm();
		this.inVo.devType = vo.getDevType();
		this.inVo.isUse = vo.getIsUse();
		this.inVo.sessionTimeout = vo.getSessionTimeout();
		this.inVo.ip = vo.getIp();
		this.inVo.port = vo.getPort();
		this.inVo.lat = vo.getLat();
		this.inVo.lon = vo.getLon();
		this.inVo.remark = vo.getRemark();
		this.inVo.alterDate = vo.getAlterDate();
		this.inVo.regDate = vo.getRegDate();
	}

	@Override
	public void select() throws Exception {
		this.outVoList.clear();
		this.outVoList.add(NitroCacheManager.getDevice(this.inVo.devId));
	}

	@Override
	public void selectByDevPoolId() throws Exception {
		this.outVoList.clear();
		this.outVoList = NitroCacheManager.getDeviceByDevPoolId(this.inVo.devPoolId);
	}
	
	
	@Override
	public void update() throws Exception {
		this.outVoList.clear();
		this.outVoList.add(NitroCacheManager.setDevice(this.inVo));
	}

	@Override
	public void insert() throws Exception {
		this.outVoList.clear();
		this.outVoList.add(NitroCacheManager.putDevice(this.inVo));
	}

	@Override
	public void delete() throws Exception {
		this.outVoList.clear();
		this.outVoList.add(NitroCacheManager.removeDevice(this.inVo.devId));
	}

	@Override
	public IModifyDeviceObj devId(String devId) {
		this.inVo.devId = devId;
		return this;
	}

	@Override
	public IModifyDeviceObj devPoolId(String devPoolId) {
		this.inVo.devPoolId = devPoolId;
		return this;
	}

	@Override
	public IModifyDeviceObj devNm(String devNm) {
		this.inVo.devNm = devNm;
		return this;
	}
	
	@Override
	public IModifyDeviceObj devType(String devType) {
		this.inVo.devType = devType;
		return this;
	}

	@Override
	public IModifyDeviceObj isUse(String isUse) {
		this.inVo.isUse = isUse;
		return this;
	}

	@Override
	public IModifyDeviceObj sessionTimeout(String sessionTimeout) {
		this.inVo.sessionTimeout = sessionTimeout;
		return this;
	}
	
	@Override
	public IModifyDeviceObj ip(String ip) {
		this.inVo.ip = ip;
		return this;
	}

	@Override
	public IModifyDeviceObj port(String port) {
		this.inVo.port = port;
		return this;
	}

	@Override
	public IModifyDeviceObj lat(String lat) {
		this.inVo.lat = lat;
		return this;
	}

	@Override
	public IModifyDeviceObj lon(String lon) {
		this.inVo.lon = lon;
		return this;
	}

	@Override
	public IModifyDeviceObj remark(String remark) {
		this.inVo.remark = remark;
		return this;
	}

	@Override
	public IModifyDeviceObj alterDate(String alterDate) {
		this.inVo.alterDate = alterDate;
		return this;
	}

	@Override
	public IModifyDeviceObj regDate(String regDate) {
		this.inVo.regDate = regDate;
		return this;
	}

	@Override
	public IDeviceObj getInputVo() {
		return this.inVo;
	}

	@Override
	public IDeviceObj getResultVo() {
		return this.outVoList.get(0);
	}
	
	@Override
	public List<DeviceObj> getResultVoList() {
		return this.outVoList;
	}

	public static DeviceObj createVoFromMap(Map<String, String> map) {
		DeviceObj resultVo = null;
		if (map != null && map.size() != 0) {
			resultVo = new DeviceObj();
			resultVo.devId = map.get(IConst.Device.C_DEVICE_ID);
			resultVo.devPoolId = map.get(IConst.Device.C_DEVICE_POOL_ID);
			resultVo.devNm = map.get(IConst.Device.C_DEVICE_NAME);
			resultVo.devType = map.get(IConst.Device.C_DEVICE_TYPE);
			resultVo.isUse = map.get(IConst.Device.C_IS_USE);
			resultVo.sessionTimeout = map.get(IConst.Device.C_SESSION_TIMEOUT);
			resultVo.ip = map.get(IConst.Device.C_IP);
			resultVo.port = map.get(IConst.Device.C_PORT);
			resultVo.lat = map.get(IConst.Device.C_LAT);
			resultVo.lon = map.get(IConst.Device.C_LON);
			resultVo.remark = map.get(IConst.Device.C_REMARK);
			resultVo.alterDate = map.get(IConst.Device.C_ALTER_DATE);
			resultVo.regDate = map.get(IConst.Device.C_REG_DATE);
		}
		return resultVo;
	}

	public static Map<String, String> createMapFromVo(DeviceObj vo) {
		Map<String, String> resultMap = null;
		if (vo != null) {
			resultMap = new HashMap<String, String>();
			resultMap.put(IConst.Device.C_DEVICE_ID, vo.getDevId());
			resultMap.put(IConst.Device.C_DEVICE_POOL_ID, vo.getDevPoolId());
			resultMap.put(IConst.Device.C_DEVICE_NAME, vo.getDevNm());
			resultMap.put(IConst.Device.C_DEVICE_TYPE, vo.getDevType());
			resultMap.put(IConst.Device.C_IS_USE, vo.getIsUse());
			resultMap.put(IConst.Device.C_SESSION_TIMEOUT, vo.getSessionTimeout());
			resultMap.put(IConst.Device.C_IP, vo.getIp());
			resultMap.put(IConst.Device.C_PORT, vo.getPort());
			resultMap.put(IConst.Device.C_LAT, vo.getLat());
			resultMap.put(IConst.Device.C_LON, vo.getLon());
			resultMap.put(IConst.Device.C_REMARK, vo.getRemark());
			resultMap.put(IConst.Device.C_ALTER_DATE, vo.getAlterDate());
			resultMap.put(IConst.Device.C_REG_DATE, vo.getRegDate());
		}
		return resultMap;
	}

	

}
