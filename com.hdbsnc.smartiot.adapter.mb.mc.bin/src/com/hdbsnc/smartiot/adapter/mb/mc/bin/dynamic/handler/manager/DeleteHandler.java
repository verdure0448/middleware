package com.hdbsnc.smartiot.adapter.mb.mc.bin.dynamic.handler.manager;

import java.io.IOException;

/**
 * @author dbkim
 * 생성된 핸들러를 삭제한다.
 */
public interface DeleteHandler {

	/**
	 * 등록된 핸들러의 PATH를 삭제 한다.
	 * @param path - 핸들러 PATH
	 */
	public void delete(String path) throws IOException;
	/**
	 * 등록된 핸들러를 전부 삭제 한다.
	 */
	public void deleteAll() throws IOException;
}
