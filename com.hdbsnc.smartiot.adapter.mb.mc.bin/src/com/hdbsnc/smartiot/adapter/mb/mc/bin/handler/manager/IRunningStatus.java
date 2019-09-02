package com.hdbsnc.smartiot.adapter.mb.mc.bin.handler.manager;

import com.hdbsnc.smartiot.adapter.mb.mc.bin.handler.manager.DynamicHandlerManager.ManagerVo;

/**
 * @author dbkim
 * 핸들러의 상태를 확인한다.
 */
public interface IRunningStatus {
	
	/**
	 * 핸들러의 모든 상태를 불러온다.
	 */
	public Object[] statusAll() throws Exception;

	/**
	 * IP, PORT에 해당하는 매니저 객체를 찾는다.
	 * 
	 * @param ip
	 * @param port
	 * @return
	 */
	public ManagerVo getManagerInstance(String ip, int port);
}
