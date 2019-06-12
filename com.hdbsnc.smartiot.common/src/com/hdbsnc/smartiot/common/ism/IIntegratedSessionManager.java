package com.hdbsnc.smartiot.common.ism;

import java.util.Map;

import com.hdbsnc.smartiot.common.context.IContextTracerSupport;
import com.hdbsnc.smartiot.common.ism.sm.ISession;
import com.hdbsnc.smartiot.common.ism.sm.ISessionManager;

/**
 * ISM에서 사용하는 IContextTracerSupport는 AIM에서 handOverContext(IContext request)호출시 내부적으로 사용한다.
 * 아답터와 아답터사이의 context 전달 및 결과물 수, 아답터와 외부 서버간의 context전달 및 결과물 수신에 사용된다.
 * @author hjs0317
 *
 */
public interface IIntegratedSessionManager extends IContextTracerSupport{
	
	String getServerId();

	ISessionManager getSessionManager(String instanceId);
	
	Map<String, ISessionManager> getSessionManagerMap();
	
	ISessionManager createSessionManager(String instanceId);
	
	/**
	 * 인스턴스가 dispose 될 경우 호출되는 메소드.
	 * 그외의 케이스에서는 호출되면 안된다. 특히 STOP, SUSPEND시 !!!
	 * 
	 * @param instanceId
	 */
	void disposeSessionManager(String instanceId);
	
	/**
	 * 서버가 꺼지면 호출해야할 메소드. 모든 자원을 정리한다. 
	 */
	void dispose();
	
	ISession getSession(String deviceId);
	
	ISession getSessionBySessionId(String sessionKey);
	
	boolean containsDeviceId(String deviceId);
	
	/**
	 * MSS 혹은 SSS가 들어온다. 없으면 스탠드 얼론 모드. 
	 * 
	 * @param allocater
	 */
	void setSessionAllocater(ISessionAllocater allocater);
	
	/**
	 * SM에서 세션 생성시 호출해주어야 하는 메소드.
	 * 이를 통해 MSS나 SSS에게 통보가 되어 관련해서 자원관리를 할 수 있게 한다.  
	 * 
	 * @param session
	 * @return
	 */
	void innerNewSession(ISession session) throws Exception; 
	
	/**
	 * 외부에서 세션 생성시 호출해주어야 하는 메소드. 
	 * MSS나 SSS가 주요 호출자가 될것이다. 
	 * 
	 * @param deviceId
	 * @param sessionKey
	 * @throws Exception
	 */
	void outterNewSession(String deviceId, String userId, String sessionKey) throws Exception; //비정상 케이스로 세션객체 생성 및 등록이 안될 경우 발생. 
	
	void innerDisposeSession(String sid) throws Exception;
	
	void outterDisposeSession(String sid) throws Exception;
	
	/**
	 * 이미 존재하는 세션의 정보를 업데이트 하는 경우 사용.
	 * 키는 결국 deviceId를 기준으로 한다.
	 * 
	 * @param deviceId
	 * @param userId
	 * @param sessionKey
	 * @throws Exception
	 */
	void updateSession(String deviceId, String userId, String sessionKey) throws Exception;
}
