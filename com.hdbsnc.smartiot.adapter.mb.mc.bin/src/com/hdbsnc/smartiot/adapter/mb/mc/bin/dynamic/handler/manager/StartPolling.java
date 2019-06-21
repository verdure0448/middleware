package com.hdbsnc.smartiot.adapter.mb.mc.bin.dynamic.handler.manager;

/**
 * @author dbkim
 * 생성된 핸들러를 주기적으로 호출하도록 한다.
 */
public interface StartPolling {

	/**
	 * 핸들러를 주기적으로 호출을 시작한다.
	 */
	public void startPolling();
	/**
	 * 핸들러의 주기적 호출을 삭제한다.
	 */
	public void stopPolling();
}
