package com.hdbsnc.smartiot.common.context.handler2;

import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler.IElementHandler;
import com.hdbsnc.smartiot.common.context.handler.exception.ContextHandlerUnSupportedMethodException;
import com.hdbsnc.smartiot.common.ism.sm.ISession;

public interface IAttributeHandler extends IElementHandler{

	void read(IContext inCtx, OutboundContext outCtx, ISession session, String value) throws ContextHandlerUnSupportedMethodException, Exception;
	void update(IContext inCtx, OutboundContext outCtx, ISession session, String value) throws ContextHandlerUnSupportedMethodException, Exception;
	void create(IContext inCtx, OutboundContext outCtx, ISession session, String value) throws ContextHandlerUnSupportedMethodException, Exception;
	void delete(IContext inCtx, OutboundContext outCtx, ISession session, String value) throws ContextHandlerUnSupportedMethodException, Exception;
}
