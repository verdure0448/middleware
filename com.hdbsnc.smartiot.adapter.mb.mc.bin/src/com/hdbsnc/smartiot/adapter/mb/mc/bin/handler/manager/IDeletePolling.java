package com.hdbsnc.smartiot.adapter.mb.mc.bin.handler.manager;

import java.io.IOException;

/**
 * @author dbkim
 * 생성된 핸들러를 삭제한다.
 */
public interface IDeletePolling {

	/**
	 * 등록된 핸들러의 PATH를 삭제 한다.
	 * @param path - 핸들러 PATH
	 */
	public void delete(String path) throws Exception;
	/**
	 * 등록된 핸들러를 전부 삭제 한다.
	 * @return
	 * @throws IOException
	 */
	public String[] deleteAll()  throws Exception;
}
