package com.hdbsnc.smartiot.common.context.handler;

import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.IContextCallback;

@Deprecated
public interface IFunctionHandler extends IElementHandler{

	void process(IContext inboundCtx) throws Exception;
}
