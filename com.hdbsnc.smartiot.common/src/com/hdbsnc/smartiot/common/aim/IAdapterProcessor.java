package com.hdbsnc.smartiot.common.aim;

import com.hdbsnc.smartiot.common.context.IContextProcessor;



public interface IAdapterProcessor extends IContextProcessor{
	
	IAdapterProcessorEvent getLastEvent();
	
	//아답터 프로세서로 부터 올라오는 모든 이벤트를 상위로 전파.
	void addEventListener(IAdapterProcessorEventListener listener);
	void removeEventListener(IAdapterProcessorEventListener listener);
	void removeAllEventListener();
	void updateEvent(IAdapterProcessorEvent e);
}
