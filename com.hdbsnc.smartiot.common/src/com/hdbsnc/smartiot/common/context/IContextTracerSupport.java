package com.hdbsnc.smartiot.common.context;

public interface IContextTracerSupport {

	void cancelAll();
	
	IContextTracer pollAndCallContextTracer(IContext inboundCtx, IContext outboundCtx);
	
	IContextTracer offerContextTracer(IContext inboundCtx, IContextCallback callback);
	
	boolean isExistContextTracer(IContext inboundCtx);
	
	int count();
}
