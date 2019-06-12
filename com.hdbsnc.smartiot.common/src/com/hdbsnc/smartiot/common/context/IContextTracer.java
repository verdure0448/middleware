package com.hdbsnc.smartiot.common.context;

public interface IContextTracer {

	//ism에서 poll한후 내부적으로 보관한다.
	IContextTracer pollAndCallParent();
	boolean hasParent(); //ism에 있는지 체크 
	
	String getSeq();
	IContext getRequestContext();
	IContext getResponseContext();
	IContextCallback getCallback();
	
	//동기식 호출을 위해 추가로 업데이트. 2017-07-10 hjs0317
	void update();
	IContext waitResponseContext() throws Exception; //60초 이후에 자동 타임 아웃.
	IContext waitResponseContext(long timeout) throws Exception; //사용자가 정한 시간 이후에 타임 아웃.
	
	boolean cancel();
}
