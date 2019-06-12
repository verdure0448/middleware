package com.hdbsnc.smartiot.common.context;


public interface IContextProcessor {

	void parallelProcess(IContext inboundCtx) throws Exception;
	
	void process(IContext inboundCtx) throws Exception;
	
//	void process(IContext inboundCtx, IContextCallback callback) throws Exception;
	
}
