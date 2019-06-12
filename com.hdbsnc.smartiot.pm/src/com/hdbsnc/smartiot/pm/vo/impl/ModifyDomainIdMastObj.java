////////////////////////////////////////////////////////////////////
// Copyright 2015,2016 <hd-bsnc.com>. All rights reserved.
////////////////////////////////////////////////////////////////////
package com.hdbsnc.smartiot.pm.vo.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hdbsnc.smartiot.common.pm.vo.IDomainIdMastObj;
import com.hdbsnc.smartiot.common.pm.vo.IModifyDomainIdMastObj;
import com.hdbsnc.smartiot.pm.cache.NitroCacheManager;
import com.hdbsnc.smartiot.pm.constant.IConst;

public class ModifyDomainIdMastObj implements IModifyDomainIdMastObj {

	private DomainIdMastObj inVo;
	private List<DomainIdMastObj> outVoList;

	public ModifyDomainIdMastObj() {
		this.inVo = new DomainIdMastObj();
		this.outVoList = new ArrayList<DomainIdMastObj>();
	}

	public ModifyDomainIdMastObj(IDomainIdMastObj vo) {
		this.inVo = new DomainIdMastObj();
		this.outVoList = new ArrayList<DomainIdMastObj>();

		this.inVo.domainId = vo.getDomainId();
		this.inVo.domainNm = vo.getDomainNm();
		this.inVo.domainType = vo.getDomainType();
		this.inVo.remark = vo.getRemark();
		this.inVo.alterDate = vo.getAlterDate();
		this.inVo.regDate = vo.getRegDate();
	}

	@Override
	public void select() throws Exception {
		this.outVoList.clear();
		this.outVoList.add(NitroCacheManager.getDomainIdMast(this.inVo.domainId));
	}

	@Override
	public void selectByDomainType() throws Exception {
		this.outVoList.clear();
		this.outVoList = NitroCacheManager.getDomainIdMastByDomainType(this.inVo.domainType);
	}

	@Override
	public void update() throws Exception {
		this.outVoList.clear();
		this.outVoList.add(NitroCacheManager.setDomainIdMast(this.inVo));
	}

	@Override
	public void insert() throws Exception {
		this.outVoList.clear();
		this.outVoList.add(NitroCacheManager.putDomainIdMast(this.inVo));
	}

	@Override
	public void delete() throws Exception {
		this.outVoList.clear();
		this.outVoList.add(NitroCacheManager.removeDomainIdMast(this.inVo.domainId));
	}

	@Override
	public IModifyDomainIdMastObj domainId(String domainId) {
		this.inVo.domainId = domainId;
		return this;
	}

	@Override
	public IModifyDomainIdMastObj domainNm(String domainNm) {
		this.inVo.domainNm = domainNm;
		return this;
	}

	@Override
	public IModifyDomainIdMastObj domainType(String domainType) {
		this.inVo.domainType = domainType;
		return this;
	}

	@Override
	public IModifyDomainIdMastObj remark(String remark) {
		this.inVo.remark = remark;
		return this;
	}

	@Override
	public IModifyDomainIdMastObj alterDate(String alterDate) {
		this.inVo.alterDate = alterDate;
		return this;
	}

	@Override
	public IModifyDomainIdMastObj regDate(String regDate) {
		this.inVo.regDate = regDate;
		return this;
	}

	@Override
	public IDomainIdMastObj getInputVo() {
		return this.inVo;
	}

	@Override
	public IDomainIdMastObj getResultVo() {
		return this.outVoList.get(0);
	}

	@Override
	public List<DomainIdMastObj> getResultVoList() {
		return this.outVoList;
	}

	public static DomainIdMastObj createVoFromMap(Map<String, String> map) {
		DomainIdMastObj resultVo = null;
		if (map != null && map.size() != 0) {
			resultVo = new DomainIdMastObj();
			resultVo.domainId = map.get(IConst.DomainIdMast.C_DOMAIN_ID);
			resultVo.domainNm = map.get(IConst.DomainIdMast.C_DOMAIN_NAME);
			resultVo.domainType = map.get(IConst.DomainIdMast.C_DOMAIN_TYPE);
			resultVo.alterDate = map.get(IConst.DomainIdMast.C_ALTER_DATE);
			resultVo.regDate = map.get(IConst.DomainIdMast.C_REG_DATE);
		}

		return resultVo;
	}

	public static Map<String, String> createMapFromVo(DomainIdMastObj vo) {
		Map<String, String> resultMap = null;
		if (vo != null) {
			resultMap = new HashMap<String, String>();
			resultMap.put(IConst.DomainIdMast.C_DOMAIN_ID, vo.domainId);
			resultMap.put(IConst.DomainIdMast.C_DOMAIN_NAME, vo.domainNm);
			resultMap.put(IConst.DomainIdMast.C_DOMAIN_TYPE, vo.domainType);
			resultMap.put(IConst.DomainIdMast.C_ALTER_DATE, vo.alterDate);
			resultMap.put(IConst.DomainIdMast.C_REG_DATE, vo.regDate);
		}
		return resultMap;
	}

}
