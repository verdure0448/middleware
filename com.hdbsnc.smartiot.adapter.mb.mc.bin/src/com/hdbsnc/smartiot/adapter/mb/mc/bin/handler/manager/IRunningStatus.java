package com.hdbsnc.smartiot.adapter.mb.mc.bin.handler.manager;

import java.util.Map;

/**
 * @author dbkim
 * 핸들러의 상태를 확인한다.
 */
public interface IRunningStatus {
	
	/**
	 * 핸들러의 모든 상태를 불러온다.
	 */
	public Map statusAll();

}
