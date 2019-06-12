package com.hdbsnc.smartiot.common.aim;

public interface IAdapterInstance {

	/**
	 * [초기화]
	 *  최초 인스턴스 시작전에 호출됨. 
	 *  dispose가 호출되지 않는한 다시 호출되는 케이스 없음.
	 *  dispose가 호출되면 다시 start시에 initialize가 호출되어야 함.
	 * @param ctx
	 * @throws Exception
	 */
	void initialize(IAdapterContext ctx) throws Exception;
	
	/**
	 * [시작]
	 * 클라이언트 아답터: 장치와 실제 연결되고, 디폴트세션 인증 정보를 가지고와서 ism인증  세션객체를 만든다.
	 * 서버 아답터: 서버 소켓을 가동하거나 장치에서 제공하는 API를 초기화시켜서 장치 연결을 기다리는 상태로 만든다. 
	 * @param ctx
	 * @throws Exception
	 */
	void start(IAdapterContext ctx) throws Exception;
	
	/**
	 * [정지]
	 * SM 에 존재하는 session을 dispose하고, CM 에 존재하는 connection을 disconnect 한 후 clear한다.
	 * Connection이 존재하면 해당 연결을 모두 끊는다. 
	 * 이 후 start시에 SM과 CM이 처음부터 다 시작할 수 있도록 한다.
	 * SM과 CM객체 자체가 사라지는 것은 아님.
	 * @param ctx
	 * @throws Exception
	 */
	void stop(IAdapterContext ctx) throws Exception;
	
	/**
	 * [일시정지]
	 * 세션정보 및 컨넥션 정보들은(연결을 끊지도 않는다.) 손대지 않고 외부에서 오는 inbound/oubound 만 처리하지 않는 상태. 정확히는 inbound를 차단한 상태.
	 * @param ctx
	 * @throws Exception
	 */
	void suspend(IAdapterContext ctx) throws Exception;
	
	/**
	 * [소멸] 영진사마가 소멸로 한글명을 정하자고 요청함.
	 * 현재 인스턴스와 관련한 모든 리소스를 dispose 시켜서 재활용할 수 없도록 완전히 정리한다.
	 * 이 후 start시에는 initialize를 먼저해주어야 한다.
	 * @param ctx
	 * @throws Exception
	 */
	void dispose(IAdapterContext ctx) throws Exception;
	
	public IAdapterProcessor getProcessor();
}
