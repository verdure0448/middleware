package com.hdbsnc.smartiot.common.context.handler2;

import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler.IElementHandler;

public interface IFunctionHandler extends IElementHandler{

	
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception;
}
