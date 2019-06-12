package com.hdbsnc.smartiot.common.ism.sm;

import java.util.List;

import com.hdbsnc.smartiot.common.connection.IConnectionManager;
import com.hdbsnc.smartiot.common.ism.IIntegratedSessionManager;



public interface ISessionManager {

	IConnectionManager getConnectionManager();
	
	String getAdapterInstanceId();
	
	int getSessionCount();
	
	boolean containsDeviceId(String deviceId);
	boolean containsSessionKey(String sessionKey);
	
	/**
	 * 기본적으로 유효하지 않은 Session은 매니져상에 존재하지 않으므로 null을 리턴한다.
	 * @param deviceId
	 * @return
	 */
	ISession getSessionByDeviceId(String deviceId); 
	ISession getSessionBySessionKey(String sessionKey);
	
	List<ISession> getSessionList();
	
	ISession allocateSession(String deviceId, String userId, String sessionKey) throws Exception;
	void unallocateSession(String sessionKey) throws Exception;
	

	
	/**
	 * 성공하면 세션 생성하고 activate상태로 변경.
	 * 실패하면 세션 생성 하지 않음!
	 * 
	 * 다른 아답터의 인스턴스까지 인증이되는 통합인증 아답터 구현은 안됨!!!!
	 * 통합인증은 별도의 서비스로 구현할 것!!!
	 * 자기자신의 인스턴스로 소속된 장치풀의 소속 장비만 인증 통과가 가능 
	 * 
	 * @param deviceId
	 * @param userid
	 * @param password
	 * @return
	 */
	ISession certificate(String deviceId, String userid, String password) throws Exception; 
	
	/**
	 * dispose 상태로 리무브.
	 * @param deviceId
	 */
	void disposeSession(String sid);
	
	void disposeDeviceId(String deviceId); 
	
	void disposeSessionKey(String sessionKey);
	
	boolean isValidSession(String sessionKey);//deviceId가 this일 경우에는 sessionKey로만 유효성 검증이 가능하다.
	
	/**
	 * 보안상 모든 장치 아답터들이 ISM을 참조할 필요없으므로 향후제거할 것!!!! 2015-10-19 hjs0317
	 * @return
	 */
	public IIntegratedSessionManager getIntegratedSessionManager();
	
	void updateSession(String deviceId, String userId, String sessionKey) throws Exception;
	
	public void stop();
	
	public void dispose();
	

}