////////////////////////////////////////////////////////////////////
// Copyright 2015,2016 <hd-bsnc.com>. All rights reserved.
////////////////////////////////////////////////////////////////////
package com.hdbsnc.smartiot.pm.impl;

import java.util.ArrayList;
import java.util.List;

import com.hdbsnc.smartiot.common.pm.IProfileManager;
import com.hdbsnc.smartiot.common.pm.vo.IDeviceObj;
import com.hdbsnc.smartiot.common.pm.vo.IDevicePoolObj;
import com.hdbsnc.smartiot.common.pm.vo.IDomainIdMastObj;
import com.hdbsnc.smartiot.common.pm.vo.IInstanceAttributeObj;
import com.hdbsnc.smartiot.common.pm.vo.IInstanceFunctionObj;
import com.hdbsnc.smartiot.common.pm.vo.IInstanceObj;
import com.hdbsnc.smartiot.common.pm.vo.IModifyDeviceObj;
import com.hdbsnc.smartiot.common.pm.vo.IModifyDevicePoolObj;
import com.hdbsnc.smartiot.common.pm.vo.IModifyDomainIdMastObj;
import com.hdbsnc.smartiot.common.pm.vo.IModifyInstanceAttributeObj;
import com.hdbsnc.smartiot.common.pm.vo.IModifyInstanceFunctionObj;
import com.hdbsnc.smartiot.common.pm.vo.IModifyInstanceObj;
import com.hdbsnc.smartiot.common.pm.vo.IModifyMsgMastObj;
import com.hdbsnc.smartiot.common.pm.vo.IModifyUserFilterObj;
import com.hdbsnc.smartiot.common.pm.vo.IModifyUserObj;
import com.hdbsnc.smartiot.common.pm.vo.IModifyUserPoolObj;
import com.hdbsnc.smartiot.common.pm.vo.IMsgMastObj;
import com.hdbsnc.smartiot.common.pm.vo.IUserFilterObj;
import com.hdbsnc.smartiot.common.pm.vo.IUserObj;
import com.hdbsnc.smartiot.common.pm.vo.IUserPoolObj;
import com.hdbsnc.smartiot.pm.vo.impl.InstanceAttributeObj;
import com.hdbsnc.smartiot.pm.vo.impl.InstanceFunctionObj;
import com.hdbsnc.smartiot.pm.vo.impl.ModifyDeviceObj;
import com.hdbsnc.smartiot.pm.vo.impl.ModifyDevicePoolObj;
import com.hdbsnc.smartiot.pm.vo.impl.ModifyDomainIdMastObj;
import com.hdbsnc.smartiot.pm.vo.impl.ModifyInstanceAttributeObj;
import com.hdbsnc.smartiot.pm.vo.impl.ModifyInstanceFunctionObj;
import com.hdbsnc.smartiot.pm.vo.impl.ModifyInstanceObj;
import com.hdbsnc.smartiot.pm.vo.impl.ModifyMsgMastObj;
import com.hdbsnc.smartiot.pm.vo.impl.ModifyUserFilterObj;
import com.hdbsnc.smartiot.pm.vo.impl.ModifyUserObj;
import com.hdbsnc.smartiot.pm.vo.impl.ModifyUserPoolObj;

public class ProfileManagerImpl implements IProfileManager {

	/**
	 * 아답타 인스턴스 조회, 변경, 등록, 삭제 객체 취득
	 * 
	 * @throws Exception
	 */
	@Override
	public IInstanceObj getInstanceObj(String insId) throws Exception {
		IModifyInstanceObj mObj = new ModifyInstanceObj();
		mObj.insId(insId).select();
		return mObj.getResultVo();
	}

	@Override
	public IModifyInstanceObj getModifyInstanceObj() {
		return new ModifyInstanceObj();
	}

	@Override
	public IModifyInstanceObj getModifyInstanceObj(IInstanceObj vo) {
		return new ModifyInstanceObj(vo);
	}

	/**
	 * 인스턴스 속성 조회, 변경, 등록, 삭제 객체 취득
	 */
	@Override
	public IInstanceAttributeObj getInstanceAttributeObj(String insId, String attKey) throws Exception {
		IModifyInstanceAttributeObj mObj = new ModifyInstanceAttributeObj();
		mObj.insId(insId).key(attKey).select();
		return mObj.getResultVo();
	}

	@Override
	public IModifyInstanceAttributeObj getModifyInstanceAttributeObj() {
		return new ModifyInstanceAttributeObj();
	}

	@Override
	public IModifyInstanceAttributeObj getModifyInstanceAttributeObj(IInstanceAttributeObj vo) {
		return new ModifyInstanceAttributeObj(vo);
	}

	@Override
	public List<String> getInstanceAttributeKeyList(String insId) throws Exception {
		ModifyInstanceAttributeObj mObj = new ModifyInstanceAttributeObj();
		mObj.insId(insId).selectList();
		List<InstanceAttributeObj> voList = mObj.getResultVoList();

		List<String> keyList = new ArrayList<String>();
		for (InstanceAttributeObj instanceAttributeObj : voList) {
			keyList.add(instanceAttributeObj.getDsct());
		}
		return keyList;
	}

	@Override
	public List<IInstanceAttributeObj> getInstanceAttributeList(String insId) throws Exception {
		ModifyInstanceAttributeObj mObj = new ModifyInstanceAttributeObj();
		mObj.insId(insId).selectList();
		List<InstanceAttributeObj> voList = mObj.getResultVoList();

		List<IInstanceAttributeObj> resultList = new ArrayList<IInstanceAttributeObj>();
		for (InstanceAttributeObj instanceAttributeObj : voList) {
			resultList.add(instanceAttributeObj);
		}
		return resultList;
	}

	/**
	 * 인스턴스 기능 조회, 변경, 등록, 삭제 객체 취득
	 */
	@Override
	public IInstanceFunctionObj getInstanceFunctionObj(String insId, String funcKey) throws Exception {
		ModifyInstanceFunctionObj mObj = new ModifyInstanceFunctionObj();
		mObj.insId(insId).key(funcKey).select();
		return mObj.getResultVo();
	}

	@Override
	public IModifyInstanceFunctionObj getModifyInstanceFunctionObj() throws Exception {
		return new ModifyInstanceFunctionObj();
	}

	@Override
	public IModifyInstanceFunctionObj getModifyInstanceFunctionObj(IInstanceFunctionObj vo) throws Exception {
		return new ModifyInstanceFunctionObj(vo);
	}

	public List<IInstanceFunctionObj> getInstanceFunctionList(String insId) throws Exception {
		ModifyInstanceFunctionObj mObj = new ModifyInstanceFunctionObj();
		mObj.insId(insId).selectList();
		List<InstanceFunctionObj> voList = mObj.getResultVoList();

		List<IInstanceFunctionObj> resultList = new ArrayList<IInstanceFunctionObj>();
		for (InstanceFunctionObj instanceFunctionObj : voList) {
			resultList.add(instanceFunctionObj);
		}
		return resultList;
	}

	/**
	 * 장치풀 조회, 변경, 등록, 삭제 객체 취득
	 */
	@Override
	public IDevicePoolObj getDevicePoolObj(String devPoolId) throws Exception {
		IModifyDevicePoolObj mObj = new ModifyDevicePoolObj();
		mObj.devPoolId(devPoolId).select();
		return mObj.getResultVo();
	}

	@Override
	public IModifyDevicePoolObj getModifyDevicePoolObj() {
		return new ModifyDevicePoolObj();
	}

	@Override
	public IModifyDevicePoolObj getModifyDevicePoolObj(IDevicePoolObj vo) {
		return new ModifyDevicePoolObj(vo);
	}

	/**
	 * 장치 조회, 변경, 등록, 삭제 객체 취득
	 */
	@Override
	public IDeviceObj getDeviceObj(String devId) throws Exception {
		IModifyDeviceObj mObj = new ModifyDeviceObj();
		mObj.devId(devId).select();
		return mObj.getResultVo();
	}

	@Override
	public IModifyDeviceObj getModifyDeviceObj() {
		return new ModifyDeviceObj();
	}

	@Override
	public IModifyDeviceObj getModifyDeviceObj(IDeviceObj vo) {
		return new ModifyDeviceObj(vo);
	}

	/**
	 * 유저 프로파일 마스터 조회, 변경, 등록, 삭제 객체 취득
	 */
	@Override
	public IUserPoolObj getUserPoolObj(String userPoolId) throws Exception {
		IModifyUserPoolObj mObj = new ModifyUserPoolObj();
		mObj.userPoolId(userPoolId).select();
		return mObj.getResultVo();
	}

	@Override
	public IModifyUserPoolObj getModifyUserPoolObj() {
		return new ModifyUserPoolObj();
	}

	@Override
	public IModifyUserPoolObj getModifyUserPoolObj(IUserPoolObj vo) {
		return new ModifyUserPoolObj(vo);
	}

	/**
	 * 유저 프로파일 조회, 변경, 등록, 삭제 객체 취득
	 */
	@Override
	public IUserObj getUserObj(String userId) throws Exception {
		IModifyUserObj mObj = new ModifyUserObj();
		mObj.userId(userId).select();
		return mObj.getResultVo();
	}

	@Override
	public IModifyUserObj getModifyUserObj() {
		return new ModifyUserObj();
	}

	@Override
	public IModifyUserObj getModifyUserObj(IUserObj vo) {
		return new ModifyUserObj(vo);
	}

	/**
	 * 유저 프로파일 필터 조회, 변경, 등록, 삭제 객체 취득
	 */
	@Override
	public IUserFilterObj getUserFilterObj(String userId, String authFilter) throws Exception {
		IModifyUserFilterObj mObj = new ModifyUserFilterObj();
		mObj.userId(userId).authFilter(authFilter).select();
		return mObj.getResultVo();
	}

	@Override
	public IModifyUserFilterObj getModifyUserFilterObj() {
		return new ModifyUserFilterObj();
	}

	@Override
	public IModifyUserFilterObj getModifyUserFilterObj(IUserFilterObj vo) {
		return new ModifyUserFilterObj(vo);
	}

	/**
	 * 도메인 식별자 마스터 조회, 변경, 등록, 삭제 객체 취득
	 */
	@Override
	public IDomainIdMastObj getDomainIdMastObj(String domainId) throws Exception {
		IModifyDomainIdMastObj mObj = new ModifyDomainIdMastObj();
		mObj.domainId(domainId).select();
		return mObj.getResultVo();
	}

	@Override
	public IModifyDomainIdMastObj getModifyDomainIdMastObj() {
		return new ModifyDomainIdMastObj();
	}

	@Override
	public IModifyDomainIdMastObj getModifyDomainIdMastObj(IDomainIdMastObj vo) {
		return new ModifyDomainIdMastObj(vo);
	}
	
	/**
	 * 메세지 마스터 
	 * @throws Exception 
	 */
	@Override
	public IMsgMastObj getMsgMastObj(String innerCode) throws Exception{
		IModifyMsgMastObj mObj = new ModifyMsgMastObj();
		mObj.innerCode(innerCode).select();
		return mObj.getResultVo();
	}
	
	@Override
	public IModifyMsgMastObj getModifyMsgMastObj() throws Exception {
		return new ModifyMsgMastObj();
	}

	@Override
	public IModifyMsgMastObj getModifyMsgMastObj(IMsgMastObj vo) throws Exception {
		return new ModifyMsgMastObj(vo);
	}

	/**
	 * 장치아이디로 아답터 인스턴스 조회
	 * 
	 * @throws Exception
	 */
	@Override
	public IInstanceObj searchInstanceByDevId(String devId) throws Exception {

		IModifyDeviceObj mDevObj = new ModifyDeviceObj();
		IModifyInstanceObj mInsObj = new ModifyInstanceObj();

		mDevObj.devId(devId).select();
		if (mDevObj.getResultVo() != null) {
			mInsObj.devPoolId(mDevObj.getResultVo().getDevPoolId()).selectByDevPoolId();
		}

		return mInsObj.getResultVo();
	}

	@Override
	public IDeviceObj authDevice(String devPool, String devId) throws Exception {
		IModifyDeviceObj mDevObj = new ModifyDeviceObj();
		mDevObj.devId(devId).select();

		IDeviceObj devObj = mDevObj.getResultVo();

		if (!devId.equals(devObj.getDevPoolId())) {
			devObj = null;
		}
		return devObj;
	}

	@Override
	public IUserObj authUser(String userPool, String userId, String userPw) throws Exception {
		IModifyUserObj mObj = new ModifyUserObj();
		mObj.userId(userId).select();

		IUserObj userProfileObj = mObj.getResultVo();

		if (!userPool.equals(userProfileObj.getUserPoolId()) || !userPw.equals(userProfileObj.getUserPw())) {
			userProfileObj = null;
		}

		return userProfileObj;
	}

	@Override
	public boolean isAuthDevice(String devPool, String devId) throws Exception {
		boolean result = false;

		IModifyDeviceObj mDevObj = new ModifyDeviceObj();
		mDevObj.devId(devId).select();

		IDeviceObj devObj = mDevObj.getResultVo();

		if (devPool.equals(devObj.getDevPoolId())) {
			result = true;
		}

		return result;
	}

	@Override
	public boolean isAuthUser(String userPool, String userId, String userPw) throws Exception {

		boolean result = false;
		IModifyUserObj mObj = new ModifyUserObj();
		mObj.userId(userId).select();

		IUserObj userProfileObj = mObj.getResultVo();

		if (userPool.equals(userProfileObj.getUserPoolId()) && userPw.equals(userProfileObj.getUserPw())) {
			result = true;
		}

		return result;
	}

	@Override
	public List<IUserFilterObj> searchUserFilterByUserId(String userId) throws Exception {
		IModifyUserFilterObj mObj = new ModifyUserFilterObj();
		mObj.userId(userId).selectList();
		return mObj.getResultVoList();
	}

	@Override
	public IInstanceObj integrationAuth(String userId, String userPw, String devId) throws Exception {

		IModifyUserObj mUserObj = new ModifyUserObj();
		mUserObj.userId(userId).select();

		IUserObj userProfileObj = mUserObj.getResultVo();

		if (userProfileObj == null || !userPw.equals(userProfileObj.getUserPw())) {
			return null;
		}

		IModifyUserFilterObj mUserFilterObj = new ModifyUserFilterObj();
		mUserFilterObj.userId(userId).selectList();
		List<IUserFilterObj> userFilterObjList = mUserFilterObj.getResultVoList();

		boolean bAuth = false;
		for (IUserFilterObj iUserFilterObj : userFilterObjList) {
			if ("*".equals(iUserFilterObj.getAuthFilter())) {
				bAuth = true;
				break;
			}
			
			// 권한필터 체크 추가
			if(devId.equals(iUserFilterObj.getAuthFilter())){
				bAuth = true;
				break;
			}
		}

		if (!bAuth) {
			return null;
		}

		IModifyDeviceObj mDevObj = new ModifyDeviceObj();
		mDevObj.devId(devId).select();
		if (mDevObj.getResultVo() == null) {
			return null;
		}

		IModifyInstanceObj mInsObj = new ModifyInstanceObj();
		mInsObj.devPoolId(mDevObj.getResultVo().getDevPoolId()).selectByDevPoolId();

		return mInsObj.getResultVo();
	}

	/**
	 * 아답터ID로 인스턴스 정보 조회
	 */
	@Override
	public List<IInstanceObj> searchInstanceByAid(String aid) throws Exception {
		IModifyInstanceObj mInsObj = new ModifyInstanceObj();
		mInsObj.adtId(aid).selectByAdtId();
		return mInsObj.getResultVoList();
	}

	@Override
	public List<IDevicePoolObj> getAllDevicePoolObj() throws Exception {
		IModifyDevicePoolObj mDevPoolObj = new ModifyDevicePoolObj();

		mDevPoolObj.selectAll();

		return mDevPoolObj.getResultVoList();
	}

	@Override
	public List<IDeviceObj> searchDeviceByDevPoolId(String devPoolId) throws Exception {

		IModifyDeviceObj mDevObj = new ModifyDeviceObj();
		mDevObj.devPoolId(devPoolId);
		mDevObj.selectByDevPoolId();

		return mDevObj.getResultVoList();
	}

	@Override
	public List<IUserPoolObj> getAllUserPoolObj() throws Exception {
		IModifyUserPoolObj mUserPoolObj = new ModifyUserPoolObj();

		mUserPoolObj.selectAll();

		return mUserPoolObj.getResultVoList();
	}

	@Override
	public List<IUserObj> searchUserByUserPoolId(String userPoolId) throws Exception {
		IModifyUserObj mObj = new ModifyUserObj();

		mObj.userPoolId(userPoolId);

		mObj.selectByUserPoolId();

		return mObj.getResultVoList();
	}

	@Override
	public List<IDomainIdMastObj> searchDomainByDomainType(String domainType) throws Exception {
		IModifyDomainIdMastObj mObj = new ModifyDomainIdMastObj();

		mObj.domainType(domainType);

		mObj.selectByDomainType();

		return mObj.getResultVoList();
	}

	@Override
	public IInstanceObj searchInstanceByDevPoolId(String devPoolId) throws Exception {
		IModifyInstanceObj mObj = new ModifyInstanceObj();

		mObj.devPoolId(devPoolId).selectByDevPoolId();

		return mObj.getResultVo();
	}

	@Override
	public IInstanceObj searchInstanceByDefaultDevId(String defaultDevId) throws Exception {
		IModifyInstanceObj mObj = new ModifyInstanceObj();

		mObj.defaultDevId(defaultDevId).selectByDefaultDevId();

		return mObj.getResultVo();

	}


}
