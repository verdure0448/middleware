////////////////////////////////////////////////////////////////////
// Copyright 2015,2016 <hd-bsnc.com>. All rights reserved.
////////////////////////////////////////////////////////////////////
package com.hdbsnc.smartiot.common.pm;

import java.util.List;

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



/**
 * PM 외부 인터페이스
 * 
 * @author KANG
 *
 */
public interface IProfileManager {

	// ////////////////////////////////////////////////////////////////////////
	// 아답타 인스턴스
	// ////////////////////////////////////////////////////////////////////////
	/**
	 * 아답타 인스턴스 조회, 변경, 등록, 삭제 객체 조회
	 */
	public IInstanceObj getInstanceObj(String insId) throws Exception;

	public IModifyInstanceObj getModifyInstanceObj() throws Exception;

	public IModifyInstanceObj getModifyInstanceObj(IInstanceObj vo) throws Exception;

	// public IAdapterInstanceSelectObj getSelectAdapterInstanceObj();
	//
	// public IAdapterInstanceUpdateObj getUpdateAdapterInstanceObj();
	//
	// public IAdapterInstanceInsertObj getInsertAdapterInstanceObj();
	//
	// public IAdapterInstanceDeleteObj getDeleteAdapterInstanceObj();

	// ////////////////////////////////////////////////////////////////////////
	// 인스턴스 속성
	// ////////////////////////////////////////////////////////////////////////
	/**
	 * 인스턴스 속성 조회, 변경, 등록, 삭제 객체 조회
	 */

	public IInstanceAttributeObj getInstanceAttributeObj(String insId, String attKey) throws Exception;

	public IModifyInstanceAttributeObj getModifyInstanceAttributeObj() throws Exception;

	public IModifyInstanceAttributeObj getModifyInstanceAttributeObj(IInstanceAttributeObj vo) throws Exception;

	/**
	 * 인스턴스ID로 속성리스트 조회
	 * 
	 * @param insId
	 *            인스턴스ID
	 * @return 속성(키)리스트
	 * @throws Exception
	 */
	public List<String> getInstanceAttributeKeyList(String insId) throws Exception;

	/**
	 * 인스턴스ID로 속성리스트 조회
	 * 
	 * @param insId
	 *            인스턴스ID
	 * @return 속성(VO)리스트
	 * @throws Exception
	 */
	public List<IInstanceAttributeObj> getInstanceAttributeList(String insId) throws Exception;

	// public IAdapterAttributeUpdateObj getUpdateAdapterAttributeObj(
	// String insId, String attNm);
	//
	// public IAdapterAttributeInsertObj getInsertAdapterAttributeObj();
	//
	// public IAdapterAttributeDeleteObj getDeleteAdapterAttributeObj();

	// ////////////////////////////////////////////////////////////////////////
	// 인스턴스 기능
	// ////////////////////////////////////////////////////////////////////////
	/**
	 * 인스턴스 기능 조회, 변경, 등록, 삭제 객체 조회
	 */
	public IInstanceFunctionObj getInstanceFunctionObj(String insId, String funcKey) throws Exception;

	public IModifyInstanceFunctionObj getModifyInstanceFunctionObj() throws Exception;

	public IModifyInstanceFunctionObj getModifyInstanceFunctionObj(IInstanceFunctionObj vo) throws Exception;

	public List<IInstanceFunctionObj> getInstanceFunctionList(String insId) throws Exception;

	// ////////////////////////////////////////////////////////////////////////
	// 장치풀
	// ////////////////////////////////////////////////////////////////////////
	/**
	 * 장치풀 조회, 변경, 등록, 삭제 객체 조회
	 */
	public IDevicePoolObj getDevicePoolObj(String devPoolId) throws Exception;

	public IModifyDevicePoolObj getModifyDevicePoolObj() throws Exception;

	public IModifyDevicePoolObj getModifyDevicePoolObj(IDevicePoolObj vo) throws Exception;

	// public IDevicePoolUpdateObj getUpdateDevicePoolObj(String devPoolId);
	//
	// public IDevicePoolInsertObj getInsertDevicePoolObj();
	//
	// public IDevicePoolDeleteObj getDeleteDevicePoolObj();

	// ////////////////////////////////////////////////////////////////////////
	// 장치
	// ////////////////////////////////////////////////////////////////////////
	/**
	 * 장치 조회, 변경, 등록, 삭제 객체 조회
	 */
	public IDeviceObj getDeviceObj(String devId) throws Exception;

	public IModifyDeviceObj getModifyDeviceObj() throws Exception;

	public IModifyDeviceObj getModifyDeviceObj(IDeviceObj vo) throws Exception;

	// public IDeviceUpdateObj getUpdateDeviceObj(String devId);
	//
	// public IDeviceInsertObj getInsertDeviceObj();
	//
	// public IDeviceDeleteObj getDeleteDeviceObj();

	// ////////////////////////////////////////////////////////////////////////
	// 유저 프로파일 마스터
	// ////////////////////////////////////////////////////////////////////////
	/**
	 * 유저 프로파일 마스터 조회, 변경, 등록, 삭제 객체 조회
	 */
	public IUserPoolObj getUserPoolObj(String userPoolId) throws Exception;

	public IModifyUserPoolObj getModifyUserPoolObj() throws Exception;

	public IModifyUserPoolObj getModifyUserPoolObj(IUserPoolObj vo) throws Exception;

	// public IUserProfileMastUpdateObj getUpdateUserProfileMastObj(
	// String userPoolId);
	//
	// public IUserProfileMastInsertObj getInsertUserProfileMastObj();
	//
	// public IUserProfileMastDeleteObj getDeleteUserProfileMastObj();

	// ////////////////////////////////////////////////////////////////////////
	// 유저 프로파일
	// ////////////////////////////////////////////////////////////////////////
	/**
	 * 유저 프로파일 조회, 변경, 등록, 삭제 객체 조회
	 */
	public IUserObj getUserObj(String userId) throws Exception;

	public IModifyUserObj getModifyUserObj() throws Exception;

	public IModifyUserObj getModifyUserObj(IUserObj vo) throws Exception;

	// public IUserProfileUpdateObj getUpdateUserProfileObj(String userId);
	//
	// public IUserProfileInsertObj getInsertUserProfileObj();
	//
	// public IUserProfileDeleteObj getDeleteUserProfileObj();

	// ////////////////////////////////////////////////////////////////////////
	// 유저 프로파일 필터
	// ////////////////////////////////////////////////////////////////////////
	/**
	 * 유저 프로파일 필터 조회, 변경, 등록, 삭제 객체 조회
	 */
	public IUserFilterObj getUserFilterObj(String userId, String authFilter) throws Exception;

	public IModifyUserFilterObj getModifyUserFilterObj() throws Exception;

	public IModifyUserFilterObj getModifyUserFilterObj(IUserFilterObj vo) throws Exception;

	// public IUserProfileFilterUpdateObj getUpdateUserProfileFilterObj(
	// String userId);
	//
	// public IUserProfileFilterInsertObj getInsertUserProfileFilterObj();
	//
	// public IUserProfileFilterDeleteObj getDeleteUserProfileFilterObj();

	// ////////////////////////////////////////////////////////////////////////
	// 도메인 식별자 마스터
	// ////////////////////////////////////////////////////////////////////////
	/**
	 * 도메인 식별자 마스터 조회, 변경, 등록, 삭제 객체 조회
	 */
	public IDomainIdMastObj getDomainIdMastObj(String domainId) throws Exception;

	public IModifyDomainIdMastObj getModifyDomainIdMastObj() throws Exception;

	public IModifyDomainIdMastObj getModifyDomainIdMastObj(IDomainIdMastObj vo) throws Exception;

	// public IDomainIdMastUpdateObj getUpdateDomainIdMast(String domainId);
	//
	// public IDomainIdMastInsertObj getInsertDomainIdMast();
	//
	// public IDomainIdMastDeleteObj getDeleteDomainIdMast();

	// ////////////////////////////////////////////////////////////////////////
	// 메세지 마스터
	// ////////////////////////////////////////////////////////////////////////
	public IMsgMastObj getMsgMastObj(String innerCode) throws Exception;
	
	public IModifyMsgMastObj getModifyMsgMastObj() throws Exception;
	
	public IModifyMsgMastObj getModifyMsgMastObj(IMsgMastObj vo) throws Exception;

	// ////////////////////////////////////////////////////////////////////////
	// 조회성 편리기능 모음
	// ////////////////////////////////////////////////////////////////////////

	/**
	 * 장치ID로 인스턴스 정보 조회
	 * 
	 * @param deviceId
	 * @return
	 */
	public IInstanceObj searchInstanceByDevId(String devId) throws Exception;

	/**
	 * 장비 인증
	 * 
	 * @param devPool
	 *            장치풀
	 * @param devId
	 *            장치ID
	 * @return 장치VO
	 */
	public IDeviceObj authDevice(String devPool, String devId) throws Exception;

	public boolean isAuthDevice(String devPool, String devId) throws Exception;

	/**
	 * 유저 인증
	 * 
	 * @param userPool
	 *            유저풀
	 * @param userId
	 *            유저ID
	 * @param userPw
	 *            유저암호
	 * @return 유저VO
	 */
	public IUserObj authUser(String userPool, String userId, String userPw) throws Exception;

	public boolean isAuthUser(String userPool, String userId, String userPw) throws Exception;

	/**
	 * 유저ID로 권한필터 리스트 조회
	 * 
	 * @param userId
	 * @return 권한필터 리스트
	 * @throws Exception
	 */
	public List<IUserFilterObj> searchUserFilterByUserId(String userId) throws Exception;

	/**
	 * 통합인증
	 * 
	 * @param userId
	 *            유저ID
	 * @param userPw
	 *            유저암호
	 * @param devId
	 *            장치ID
	 * @return 인스턴스VO
	 */
	public IInstanceObj integrationAuth(String userId, String userPw, String devId) throws Exception;

	/**
	 * 아답터ID로 인스턴스 정보 조회
	 * 
	 * @param aid
	 * @return
	 * @throws Exception
	 */
	public List<IInstanceObj> searchInstanceByAid(String aid) throws Exception;

	/**
	 * 전 장치풀 정보 조회
	 * 
	 * @return 장치풀 리스트
	 * @throws Exception
	 */
	public List<IDevicePoolObj> getAllDevicePoolObj() throws Exception;

	/**
	 * 장치풀ID로 장치 리스트 조회
	 * 
	 * @param devPoolId
	 * @return
	 * @throws Exception
	 */
	public List<IDeviceObj> searchDeviceByDevPoolId(String devPoolId) throws Exception;

	/**
	 * 전 유저풀 정보 조회
	 * 
	 * @return 유저풀 리스트
	 * @throws Exception
	 */
	public List<IUserPoolObj> getAllUserPoolObj() throws Exception;

	/**
	 * 유저풀ID로 유저 리스트 조회
	 * 
	 * @param userPoolId
	 * @return
	 * @throws Exception
	 */
	public List<IUserObj> searchUserByUserPoolId(String userPoolId) throws Exception;

	/**
	 * 도메인 타입으로 도메인 조회
	 * 
	 * @param domainType
	 * @return
	 * @throws Exception
	 */
	public List<IDomainIdMastObj> searchDomainByDomainType(String domainType) throws Exception;

	/**
	 * 장치풀ID로 인스턴스 검색
	 * 
	 * @param devPoolId
	 * @return
	 */
	public IInstanceObj searchInstanceByDevPoolId(String devPoolId) throws Exception;

	/**
	 * 디폴트장치ID로 인스턴스 검색
	 * 
	 * @param defaultDevId
	 * @return
	 * @throws Exception
	 */
	public IInstanceObj searchInstanceByDefaultDevId(String defaultDevId) throws Exception;
	

}
