package com.hdbsnc.smartiot.adapter.mb.mc.bin;

import com.hdbsnc.smartiot.common.ICommonService;
import com.hdbsnc.smartiot.common.aim.IAdapterContext;
import com.hdbsnc.smartiot.common.connection.IConnectionManager;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler.IElementHandler;
import com.hdbsnc.smartiot.common.context.handler.exception.ContextHandlerUnimplementedFunctionException;
import com.hdbsnc.smartiot.common.context.handler2.IFunctionHandler;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.otp.url.parser.UrlParser;
import com.hdbsnc.smartiot.pdm.ap.instance.AbstractAdapterInstanceProcessor;

public class MitsubishiQSeriesMCAdapterProcessor extends AbstractAdapterInstanceProcessor{

	IConnectionManager cm;
	UrlParser parser;
	public MitsubishiQSeriesMCAdapterProcessor(ICommonService service, IAdapterContext ctx) {
		super(service, ctx);
		this.cm = ctx.getSessionManager().getConnectionManager();
		this.parser = UrlParser.getInstance();
	}

	@Override
	protected void transfer(IContext outboundCtx) throws Exception {
//		System.out.println("여기서는 정상적으로 호출되는 케이스가 없음!!!!!");
	}
	
	@Override
	protected void responseInnerTransfer(IContext inboundCtx, OutboundContext outboundCtx) throws Exception{
		//클라이언트 타입의 아답터인 경우 장치로 내려가는 케이스가 없다. 내부 Polling시 처리 결과를 다시 원래요청자에게 돌려주어야 하므로 이렇게처리함.
		//원래 코드는 상기의 구현 메소드인 transfer()를 호출하도록 되어 있었음.
		sm.getIntegratedSessionManager().pollAndCallContextTracer(inboundCtx, outboundCtx);
	}

	@Override
	protected void request(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		IElementHandler currHandler = this.getRootHandler().findHandler(inboundCtx.getPaths());
		if((currHandler.type() & IElementHandler.FUNCTION) == IElementHandler.FUNCTION){
			IFunctionHandler funcHandler = (IFunctionHandler) currHandler;
			funcHandler.process(inboundCtx, outboundCtx);
		}else{
			throw new ContextHandlerUnimplementedFunctionException(inboundCtx);
		}
	}

	@Override
	protected void response(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		IElementHandler currHandler = this.getRootHandler().findHandler(inboundCtx.getPaths());
		if((currHandler.type() & IElementHandler.FUNCTION) == IElementHandler.FUNCTION){
			IFunctionHandler funcHandler = (IFunctionHandler) currHandler;
			funcHandler.process(inboundCtx, outboundCtx);
		}else{
			throw new ContextHandlerUnimplementedFunctionException(inboundCtx);
		}
	}

	@Override
	protected void event(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		IElementHandler currHandler = this.getRootHandler().findHandler(inboundCtx.getPaths());
		if((currHandler.type() & IElementHandler.FUNCTION) == IElementHandler.FUNCTION){
			IFunctionHandler funcHandler = (IFunctionHandler) currHandler;
			funcHandler.process(inboundCtx, outboundCtx);
		}else{
			throw new ContextHandlerUnimplementedFunctionException(inboundCtx);
		}
	}

}
