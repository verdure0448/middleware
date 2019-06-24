package com.hdbsnc.smartiot.adapter.mb.mc.bin.dynamic.handler.manager;

import com.hdbsnc.smartiot.adapter.mb.mc.bin.dynamic.handler.manager.CreateHandler.HandlerType;

/**
 * @author dbkim 핸들러를 동적 생성 한다.
 */
public interface CreateHandler {

	public enum HandlerType {
		// MC Protocol 3E프레임 일괄읽기 핸들러
		READ_BATCH_PROCESS_HANDLER,
		// MC Protocol 3E프레임 블록일괄 읽기 핸들러
		READ_BLOCK_BATCH_PROCESS_HANDLER,
		// MC Protocol 3E프레임 일괄쓰기 핸들러
		WRITE_BATCH_PROCESS_HANDLER,
		// MC Protocol 3E프레임 블록일괄쓰기 핸들러
		WRITE_BLOCK_BATCH_PROCESS_HANDLER,

		// MC Protocol 3E프레임 일괄 읽기/쓰기 핸들러
		// 단 읽기를 먼저 시도한다.
		READ_WRITE_BATCH_PROCESS_HANDLER,
		// MC Protocol 3E프레임 블록 일괄 읽기/쓰기 핸들러
		// 단 읽기를 먼저 시도한다.
		READ_WRITE_BLOCK_BATCH_PROCESS_HANDLER
	}


	/**
	 * 지정된 PATH로 핸들러를 동적 생성한다.
	 * 
	 * @param path - 핸들러 경로
	 */
	public void start(HandlerType kind, String path, String ip, int port, int intervalSec) throws Exception;
}
