package com.hdbsnc.smartiot.common.context;


public interface IContextCallback {

	/**
	 * 응답이 정상적으로 올경우 호출되는 함수. ack/nack여부는 개별적으로 알아서 판단해야 함.
	 * @param ctxTracer
	 */
	void responseSuccess(IContextTracer ctxTracer);
	
	/**
	 * 응답이 오지 않거나, timeout 될경우 호출되는 함수. 
	 * @param ctxTracer
	 */
	void responseFail(IContextTracer ctxTracer);
}
