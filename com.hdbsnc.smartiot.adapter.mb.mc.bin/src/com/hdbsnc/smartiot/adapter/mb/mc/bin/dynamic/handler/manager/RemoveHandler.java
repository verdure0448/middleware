package com.hdbsnc.smartiot.adapter.mb.mc.bin.dynamic.handler.manager;

/**
 * @author dbkim
 * 생성된 핸들러를 삭제한다.
 */
public interface RemoveHandler {

	/**
	 * 등록된 핸들러의 PATH를 삭제 한다.
	 * @param path - 핸들러 PATH
	 */
	public void remove(String path);
	/**
	 * 등록된 핸들러를 전부 삭제 한다.
	 */
	public void removeAll();
}
