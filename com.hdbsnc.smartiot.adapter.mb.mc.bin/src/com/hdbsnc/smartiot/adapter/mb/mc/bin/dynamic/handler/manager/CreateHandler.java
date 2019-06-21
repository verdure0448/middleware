package com.hdbsnc.smartiot.adapter.mb.mc.bin.dynamic.handler.manager;

/**
 * @author dbkim
 * 핸들러를 동적 생성 한다.
 */
public interface CreateHandler {

	//MC Protocol 3E프레임 일괄읽기 핸들러
	public static int READ_BATCH_PROCESS_HANDLER = 1;
	//MC Protocol 3E프레임 블록일괄 읽기 핸들러
	public static int READ_BLOCK_BATCH_PROCESS_HANDLER = 2;
	//MC Protocol 3E프레임 일괄쓰기 핸들러
	public static int WRITE_BATCH_PROCESS_HANDLER = 3;
	//MC Protocol 3E프레임 블록일괄쓰기 핸들러
	public static int WRITE_BLOCK_BATCH_PROCESS_HANDLER = 4;
	
	//MC Protocol 3E프레임 일괄 읽기/쓰기 핸들러 
	//단 읽기를 먼저 시도한다.
	public static int READ_WRITE_BATCH_PROCESS_HANDLER = 5;
	//MC Protocol 3E프레임 블록 일괄 읽기/쓰기 핸들러
	//단 읽기를 먼저 시도한다.
	public static int READ_WRITE_BLOCK_BATCH_PROCESS_HANDLER = 6;
	
	
	/**
	 * 지정된 PATH로 핸들러를 동적 생성한다.
	 * @param path - 핸들러 경로
	 */
	public void start(int kind, String path) throws Exception;
	

	/**
	 * 지정된 PATH로 핸들러를 동적 생성한다.
	 * @param type - 동적 생성할 핸들러 타입 BlockBatch ,Batch  
	 * @param path - 핸들러 경로
	 * @param json - 사용할 데이터를 전달
	 */
	public void start(int kind, String path, String json) throws Exception;
	
}
