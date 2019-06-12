package com.hdbsnc.smartiot.adapter.websocketapi;

import com.hdbsnc.smartiot.common.ICommonService;
import com.hdbsnc.smartiot.common.aim.IAdapterContext;
import com.hdbsnc.smartiot.common.aim.IAdapterInstanceManager;
import com.hdbsnc.smartiot.common.connection.IConnection;
import com.hdbsnc.smartiot.common.connection.IConnectionManager;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler.IElementHandler;
import com.hdbsnc.smartiot.common.context.handler.exception.ContextHandlerUnimplementedFunctionException;
import com.hdbsnc.smartiot.common.context.handler2.IFunctionHandler;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.RootHandler;
import com.hdbsnc.smartiot.common.ism.sm.ISessionManager;
import com.hdbsnc.smartiot.common.otp.url.parser.vo.Url;
import com.hdbsnc.smartiot.pdm.ap.instance.AbstractAdapterInstanceProcessor;
import com.hdbsnc.smartiot.util.servicepool.ServicePool;

public class WebsocketApiContextProcessor extends AbstractAdapterInstanceProcessor{

	private ServicePool pool;
	private IConnectionManager cm;
	private ISessionManager sm;
	private IAdapterInstanceManager aim;
	private RootHandler root;
	private com.hdbsnc.smartiot.common.otp.url.parser.UrlParser parser;

	public WebsocketApiContextProcessor(ICommonService service, IAdapterContext ctx, IConnectionManager cm) {
		super(service, ctx);
		this.pool = service.getServicePool();
		this.sm = ctx.getSessionManager();
		this.aim = ctx.getAdapterInstanceManager();
		this.cm = cm;
		this.root = getRootHandler();
		this.parser = com.hdbsnc.smartiot.common.otp.url.parser.UrlParser.getInstance();
	}
	
	@Override
	protected void transfer(IContext outboundCtx) throws Exception {
		IConnection con = cm.getConnection(outboundCtx.getSID());
		Url resUrl = Url.createOtp(outboundCtx.getPaths(), outboundCtx.getParams());
		resUrl.setUserInfo(outboundCtx.getSID(), outboundCtx.getSPort());
		resUrl.setHostInfo("this", outboundCtx.getTPort());
		if(outboundCtx.getTransmission()!=null) resUrl.addFrag("trans", outboundCtx.getTransmission());
		if(outboundCtx.containsContent()){
			resUrl.addFrag("cont", "json");
			StringBuilder sb = new StringBuilder();
			sb.append(parser.parse(resUrl));// \r\n 자동으로 추가됨. 
			sb.append(new String(outboundCtx.getContent().array()));
			sb.append("\r\n");
			con.write(sb.toString());
		}else{
			con.write(parser.parse(resUrl));
		}
	}

	@Override
	protected void request(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		IElementHandler currHandler = root.findHandler(inboundCtx.getPaths());
		if ((currHandler.type() & IElementHandler.FUNCTION) == IElementHandler.FUNCTION) {
			((IFunctionHandler) currHandler).process(inboundCtx, outboundCtx);
		} else {
			throw new ContextHandlerUnimplementedFunctionException(inboundCtx);
		}
	}

	@Override
	protected void response(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		IElementHandler currHandler = root.findHandler(inboundCtx.getPaths());
		if ((currHandler.type() & IElementHandler.FUNCTION) == IElementHandler.FUNCTION) {
			((IFunctionHandler) currHandler).process(inboundCtx, outboundCtx);
		} else {
			throw new ContextHandlerUnimplementedFunctionException(inboundCtx);
		}
	}

	@Override
	protected void event(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		IElementHandler currHandler = root.findHandler(inboundCtx.getPaths());
		if ((currHandler.type() & IElementHandler.FUNCTION) == IElementHandler.FUNCTION) {
			((IFunctionHandler) currHandler).process(inboundCtx, outboundCtx);
		} else {
			throw new ContextHandlerUnimplementedFunctionException(inboundCtx);
		}
	}

}
