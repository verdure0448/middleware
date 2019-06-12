package com.hdbsnc.smartiot.common.aim;

import java.util.List;

public interface IAdapterInstanceContainer{
	
	void init() throws Exception;
	
	void asyncProcess(int eventType); //인스턴스의 상태를 변경시키는 메소드. 내부적으로 쓰레드 사용.
	
	IAdapterInstanceEvent getLastEvent();
	
	IAdapterContext getContext();
	
	IAdapterProcessor getProcessor();
	
	List<IAdapterInstanceHandler> getBeforeHandlerList();
	List<IAdapterInstanceHandler> getAfterHandlerList();
	
	void addHandler(IAdapterInstanceHandler handler);
	void removeHandler(IAdapterInstanceHandler handler);
	void clearHandlers();
	
	void addEventListener(IAdapterInstanceEventListener listener);
	void addEventListener(IAdapterInstanceEventListener listener, int eventTypes);
	void addEventListener(IAdapterInstanceEventListener listener, int eventTypes, int stateTypes);
	void addEventListener(IAdapterInstanceEventListener listener, int eventTypes, int stateTypes, boolean isOnce);
	void removeEventListener(IAdapterInstanceEventListener listener);
	void removeAllEventListener();
	void updateAdapterInstanceEvent(int eventType, int stateType, Exception e);
}
