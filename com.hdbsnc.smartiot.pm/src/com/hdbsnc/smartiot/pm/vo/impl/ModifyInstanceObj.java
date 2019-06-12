////////////////////////////////////////////////////////////////////
// Copyright 2015,2016 <hd-bsnc.com>. All rights reserved.
////////////////////////////////////////////////////////////////////
package com.hdbsnc.smartiot.pm.vo.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hdbsnc.smartiot.common.pm.vo.IInstanceObj;
import com.hdbsnc.smartiot.common.pm.vo.IModifyInstanceObj;
import com.hdbsnc.smartiot.pm.cache.NitroCacheManager;
import com.hdbsnc.smartiot.pm.constant.IConst;

/**
 * 
 * 수정 가능 아답터 인스턴스 클래스
 * 
 * @author KANG
 *
 */
public class ModifyInstanceObj implements IModifyInstanceObj {

	private InstanceObj inVo;
	private List<InstanceObj> outVoList;

	public ModifyInstanceObj() {
		this.inVo = new InstanceObj();
		this.outVoList = new ArrayList<InstanceObj>();
	}

	public ModifyInstanceObj(IInstanceObj vo) {
		this.inVo = new InstanceObj();
		this.outVoList = new ArrayList<InstanceObj>();

		this.inVo.insId = vo.getInsId();
		this.inVo.devPoolId = vo.getDevPoolId();
		this.inVo.adtId = vo.getAdtId();
		this.inVo.insNm = vo.getInsNm();
		this.inVo.insKind = vo.getInsKind();
		this.inVo.defaultDevId = vo.getDefaultDevId();
		this.inVo.insType = vo.getInsType();
		this.inVo.isUse = vo.getIsUse();
		this.inVo.sessionTimeout = vo.getSessionTimeout();
		this.inVo.initDevStatus = vo.getInitDevStatus();
		this.inVo.ip = vo.getIp();
		this.inVo.port = vo.getPort();
		this.inVo.url = vo.getUrl();
		this.inVo.lat = vo.getLat();
		this.inVo.lon = vo.getLon();
		this.inVo.selfId = vo.getSelfId();
		this.inVo.selfPw = vo.getSelfPw();
		this.inVo.remark = vo.getRemark();
		this.inVo.alterDate = vo.getAlterDate();
		this.inVo.regDate = vo.getRegDate();
	}

	@Override
	public void select() throws Exception {
		this.outVoList.clear();
		this.outVoList.add(NitroCacheManager.getInstance(this.inVo.insId));
		return;
	}

	@Override
	public void selectByDevPoolId() throws Exception {
		this.outVoList.clear();
		this.outVoList.add(NitroCacheManager.getInstanceByDevPoolId(this.inVo.devPoolId));
		return;
	}

	@Override
	public void selectByAdtId() throws Exception {
		this.outVoList.clear();
		this.outVoList = NitroCacheManager.getInstanceByAdtId(this.inVo.adtId);
		return;
	}

	@Override
	public void selectByDefaultDevId() throws Exception {
		this.outVoList.clear();
		this.outVoList.add(NitroCacheManager.getInstanceByDefaultDevId(this.inVo.defaultDevId));
		return;
	}

	@Override
	public void update() throws Exception {
		this.outVoList.clear();
		this.outVoList.add(NitroCacheManager.setInstance(this.inVo));
		return;
	}

	@Override
	public void insert() throws Exception {
		this.outVoList.clear();
		this.outVoList.add(NitroCacheManager.putInstance(this.inVo));
		return;
	}

	@Override
	public void delete() throws Exception {
		this.outVoList.clear();
		this.outVoList.add(NitroCacheManager.removeInstance(this.inVo.insId));
		return;

	}

	@Override
	public IModifyInstanceObj insId(String insId) {
		this.inVo.insId = insId;
		return this;
	}

	@Override
	public IModifyInstanceObj devPoolId(String devPoolId) {
		this.inVo.devPoolId = devPoolId;
		return this;
	}

	@Override
	public IModifyInstanceObj adtId(String adtId) {
		this.inVo.adtId = adtId;
		return this;
	}

	@Override
	public IModifyInstanceObj insNm(String insNm) {
		this.inVo.insNm = insNm;
		return this;
	}

	@Override
	public IModifyInstanceObj insKind(String insKind) {
		this.inVo.insKind = insKind;
		return this;
	}

	@Override
	public IModifyInstanceObj defaultDevId(String defaultDevId) {
		this.inVo.defaultDevId = defaultDevId;
		return this;
	}

	@Override
	public IModifyInstanceObj insType(String insType) {
		this.inVo.insType = insType;
		return this;
	}

	@Override
	public IModifyInstanceObj isUse(String isUse) {
		this.inVo.isUse = isUse;
		return this;
	}

	@Override
	public IModifyInstanceObj sessionTimeout(String sessionTimeout) {
		this.inVo.sessionTimeout = sessionTimeout;
		return this;
	}

	@Override
	public IModifyInstanceObj initDevStatus(String initDevStatus) {
		this.inVo.initDevStatus = initDevStatus;
		return this;
	}

	@Override
	public IModifyInstanceObj ip(String ip) {
		this.inVo.ip = ip;
		return this;
	}

	@Override
	public IModifyInstanceObj port(String port) {
		this.inVo.port = port;
		return this;
	}
	
	@Override
	public IModifyInstanceObj url(String url) {
		this.inVo.url = url;
		return this;
	}

	@Override
	public IModifyInstanceObj lat(String lat) {
		this.inVo.lat = lat;
		return this;
	}

	@Override
	public IModifyInstanceObj lon(String lon) {
		this.inVo.lon = lon;
		return this;
	}

	@Override
	public IModifyInstanceObj selfId(String devId) {
		this.inVo.selfId = devId;
		return this;
	}

	@Override
	public IModifyInstanceObj selfPw(String devPw) {
		this.inVo.selfPw = devPw;
		return this;
	}

	@Override
	public IModifyInstanceObj remark(String remark) {
		this.inVo.remark = remark;
		return this;
	}

	@Override
	public IModifyInstanceObj alterDate(String alterDate) {
		this.inVo.alterDate = alterDate;
		return this;
	}

	@Override
	public IModifyInstanceObj regDate(String regDate) {
		this.inVo.regDate = regDate;
		return this;
	}

	@Override
	public IInstanceObj getInputVo() {
		return this.inVo;
	}

	@Override
	public IInstanceObj getResultVo() {
		return this.outVoList.get(0);
	}

	@Override
	public List<InstanceObj> getResultVoList() {
		return this.outVoList;
	}

	public IModifyInstanceObj setInputVo(InstanceObj vo) {
		this.inVo = vo;
		return this;
	}

	public static InstanceObj createVoFromMap(Map<String, String> map) {
		InstanceObj resultVo = null;

		if (map != null && map.size() != 0) {
			resultVo = new InstanceObj();
			resultVo.insId = map.get(IConst.AdapterInstance.C_INS_ID);
			resultVo.devPoolId = map.get(IConst.AdapterInstance.C_DEV_POOL_ID);
			resultVo.adtId = map.get(IConst.AdapterInstance.C_ADT_ID);
			resultVo.insNm = map.get(IConst.AdapterInstance.C_INS_NAME);
			resultVo.insKind = map.get(IConst.AdapterInstance.C_INS_KIND);
			resultVo.defaultDevId = map.get(IConst.AdapterInstance.C_DEFAULT_DID);
			resultVo.insType = map.get(IConst.AdapterInstance.C_INS_TYPE);
			resultVo.isUse = map.get(IConst.AdapterInstance.C_IS_USE);
			resultVo.sessionTimeout = map.get(IConst.AdapterInstance.C_SESSION_TIMEOUT);
			resultVo.initDevStatus = map.get(IConst.AdapterInstance.C_INIT_DEV_STATUS);
			resultVo.ip = map.get(IConst.AdapterInstance.C_IP);
			resultVo.port = map.get(IConst.AdapterInstance.C_PORT);
			resultVo.url = map.get(IConst.AdapterInstance.C_URL);
			resultVo.lat = map.get(IConst.AdapterInstance.C_LAT);
			resultVo.lon = map.get(IConst.AdapterInstance.C_LON);
			resultVo.selfId = map.get(IConst.AdapterInstance.C_SELF_ID);
			resultVo.selfPw = map.get(IConst.AdapterInstance.C_SELF_PW);
			resultVo.remark = map.get(IConst.AdapterInstance.C_REMARK);
			resultVo.alterDate = map.get(IConst.AdapterInstance.C_ALTER_DATE);
			resultVo.regDate = map.get(IConst.AdapterInstance.C_REG_DATE);
		}

		return resultVo;
	}

	public static Map<String, String> createMapFromVo(InstanceObj vo) {
		Map<String, String> resultMap = null;

		if (vo != null) {
			resultMap = new HashMap<String, String>();
			resultMap.put(IConst.AdapterInstance.C_INS_ID, vo.insId);
			resultMap.put(IConst.AdapterInstance.C_DEV_POOL_ID, vo.devPoolId);
			resultMap.put(IConst.AdapterInstance.C_ADT_ID, vo.adtId);
			resultMap.put(IConst.AdapterInstance.C_INS_NAME, vo.insNm);
			resultMap.put(IConst.AdapterInstance.C_INS_KIND, vo.insKind);
			resultMap.put(IConst.AdapterInstance.C_DEFAULT_DID, vo.defaultDevId);
			resultMap.put(IConst.AdapterInstance.C_INS_TYPE, vo.insType);
			resultMap.put(IConst.AdapterInstance.C_IS_USE, vo.isUse);
			resultMap.put(IConst.AdapterInstance.C_SESSION_TIMEOUT, vo.sessionTimeout);
			resultMap.put(IConst.AdapterInstance.C_INIT_DEV_STATUS, vo.initDevStatus);
			resultMap.put(IConst.AdapterInstance.C_IP, vo.ip);
			resultMap.put(IConst.AdapterInstance.C_PORT, vo.port);
			resultMap.put(IConst.AdapterInstance.C_URL, vo.url);
			resultMap.put(IConst.AdapterInstance.C_LAT, vo.lat);
			resultMap.put(IConst.AdapterInstance.C_LON, vo.lon);
			resultMap.put(IConst.AdapterInstance.C_SELF_ID, vo.selfId);
			resultMap.put(IConst.AdapterInstance.C_SELF_PW, vo.selfPw);
			resultMap.put(IConst.AdapterInstance.C_REMARK, vo.remark);
			resultMap.put(IConst.AdapterInstance.C_ALTER_DATE, vo.alterDate);
			resultMap.put(IConst.AdapterInstance.C_REG_DATE, vo.regDate);
		}

		return resultMap;
	}
}
