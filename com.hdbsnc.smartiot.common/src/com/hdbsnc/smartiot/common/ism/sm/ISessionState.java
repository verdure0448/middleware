package com.hdbsnc.smartiot.common.ism.sm;

public interface ISessionState {

	public static final int SESSION_CALL_INNER					= 1 << 0;
	public static final int SESSION_CALL_OUTTER					= 1 << 1;
	
	public static final int SESSION_STATE_CREATED 				= 1 << 0;//생성만 된 상태. 인증이 되지 않았음.
	public static final int SESSION_STATE_ACTIVATE 				= 1 << 1;//인증을 통과 했거나, 무인증 아답터인스턴스의 세션일 경우.
	public static final int SESSION_STATE_DEACTIVATE 			= 1 << 2;//세션 유효기간이 지났거나, 인증을 실패한 상태.
	public static final int SESSION_STATE_DISPOSE 		= 1 << 3;//사용하지 않는 세션. 메모리에서 제거 대기상태.

	
	public int getState();
	public void updateState(int caller, int sessionState);
	
	public void addSessionStateListener(ISessionStateListener listener);
	public void removeSessionStateListener(ISessionStateListener listener);
	public void removeAllSessionStateListener();
}
