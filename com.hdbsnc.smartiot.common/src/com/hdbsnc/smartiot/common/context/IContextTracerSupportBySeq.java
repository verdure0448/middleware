package com.hdbsnc.smartiot.common.context;

public interface IContextTracerSupportBySeq {

	void cancelAll();
	
	IContextTracer pollAndCallContextTracer(String portOrSeq, IContext outboundCtx);
	
	IContextTracer offerContextTracer(IContext inboundCtx, IContextCallback callback);
	
	boolean isExistContextTracer(String portOrSeq);
	
	int count();
}
