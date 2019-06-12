////////////////////////////////////////////////////////////////////
// Copyright 2015,2016 <hd-bsnc.com>. All rights reserved.
////////////////////////////////////////////////////////////////////
package com.hdbsnc.smartiot.pm.vo.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hdbsnc.smartiot.common.pm.vo.IModifyMsgMastObj;
import com.hdbsnc.smartiot.common.pm.vo.IMsgMastObj;
import com.hdbsnc.smartiot.pm.cache.NitroCacheManager;
import com.hdbsnc.smartiot.pm.constant.IConst;

public class ModifyMsgMastObj implements IModifyMsgMastObj {

	private MsgMastObj inVo;
	private List<MsgMastObj> outVoList;

	public ModifyMsgMastObj() {
		this.inVo = new MsgMastObj();
		this.outVoList = new ArrayList<MsgMastObj>();
	}

	public ModifyMsgMastObj(IMsgMastObj vo) {
		this.inVo = new MsgMastObj();
		this.outVoList = new ArrayList<MsgMastObj>();

		this.inVo.innerCode = vo.getInnerCode();
		this.inVo.outerCode = vo.getOuterCode();
		this.inVo.type = vo.getType();
		this.inVo.msg = vo.getMsg();
		this.inVo.causeContext = vo.getCauseContext();
		this.inVo.solutionContext = vo.getSolutionContext();
	}

	@Override
	public void select() throws Exception {
		this.outVoList.clear();
		this.outVoList.add(NitroCacheManager.getMsgMast(this.inVo.innerCode));
	}


	@Override
	public void update() throws Exception {
		// 미구현
	}

	@Override
	public void insert() throws Exception {
		// 미구현
	}

	@Override
	public void delete() throws Exception {
		// 미구현
	}

	
	@Override
	public IMsgMastObj getInputVo() {
		return this.inVo;
	}

	@Override
	public IMsgMastObj getResultVo() {
		return this.outVoList.get(0);
	}

	@Override
	public IModifyMsgMastObj innerCode(String innerCode) {
		this.inVo.innerCode = innerCode;
		return this;
	}

	@Override
	public IModifyMsgMastObj outerCode(String outerCode) {
		this.inVo.outerCode = outerCode;
		return this;
	}

	@Override
	public IModifyMsgMastObj type(String type) {
		this.inVo.type = type;
		return this;
	}

	@Override
	public IModifyMsgMastObj msg(String msg) {
		this.inVo.msg = msg;
		return this;
	}
	
	@Override
	public IModifyMsgMastObj causeContext(String causeContext) {
		this.inVo.causeContext = causeContext;
		return this;
	}

	@Override
	public IModifyMsgMastObj solutionContext(String solutionContext) {
		this.inVo.solutionContext = solutionContext;
		return this;
	}
	
	public static MsgMastObj createVoFromMap(Map<String, String> map) {
		MsgMastObj resultVo = null;
		if (map != null && map.size() != 0) {
			resultVo = new MsgMastObj();
			resultVo.innerCode = map.get(IConst.MsgMast.C_INNER_CODE);
			resultVo.outerCode = map.get(IConst.MsgMast.C_OUTER_CODE);
			resultVo.type = map.get(IConst.MsgMast.C_TYPE);
			resultVo.msg = map.get(IConst.MsgMast.C_MSG);
			resultVo.causeContext = map.get(IConst.MsgMast.C_CAUSE_CONTEXT);
			resultVo.solutionContext = map.get(IConst.MsgMast.C_SOLUTION_CONTEXT);
		}

		return resultVo;
	}

	public static Map<String, String> createMapFromVo(MsgMastObj vo) {
		Map<String, String> resultMap = null;
		if (vo != null) {
			resultMap = new HashMap<String, String>();
			resultMap.put(IConst.MsgMast.C_INNER_CODE, vo.innerCode);
			resultMap.put(IConst.MsgMast.C_OUTER_CODE, vo.outerCode);
			resultMap.put(IConst.MsgMast.C_TYPE, vo.type);
			resultMap.put(IConst.MsgMast.C_MSG, vo.msg);
			resultMap.put(IConst.MsgMast.C_CAUSE_CONTEXT, vo.causeContext);
			resultMap.put(IConst.MsgMast.C_SOLUTION_CONTEXT, vo.solutionContext);
		}
		return resultMap;
	}

}
