package com.hdbsnc.smartiot.adapter.mb.mc.bin.dynamic.handler.manager;

import com.hdbsnc.smartiot.adapter.mb.mc.bin.protocol.obj.StartRequest;

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

//	/**
//	 * 지정된 PATH를 핸들러를 생성 한다.
//	 * @param kind - 사용할 핸들러 종류
//	 * @param path - 핸들러 PATH
//	 * @param ip - PLC IP
//	 * @param port - PLC PORT
//	 * @param pollingIntervalSec - 데이터 폴링 주기 
//	 * @param items - 수집 정보
//	 * @throws Exception
//	 */
//	public void start(HandlerType kind, String path, String ip, int port, Items[] items) throws Exception;
	/**
	 * 지정된 PATH를 핸들러를 동적 생성 후 폴링 한다.
	 * @param kind - 사용할 핸들러 종류
	 * @param path - 핸들러 PATH
	 * @param ip - PLC IP
	 * @param port - PLC PORT
	 * @param pollingIntervalSec - 데이터 폴링 주기
	 * @param startRequest - startRequest 정보
	 * @throws Exception
	 */
	public void start(HandlerType kind, String path, String ip, int port, int pollingIntervalSec, StartRequest startRequest) throws Exception;
	
}
