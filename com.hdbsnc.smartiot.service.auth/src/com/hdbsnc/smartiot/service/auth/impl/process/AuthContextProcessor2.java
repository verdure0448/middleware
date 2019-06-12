package com.hdbsnc.smartiot.service.auth.impl.process;

import java.io.IOException;

import com.hdbsnc.smartiot.common.ICommonService;
import com.hdbsnc.smartiot.common.connection.IConnection;
import com.hdbsnc.smartiot.common.connection.IConnectionManager;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler.IElementHandler;
import com.hdbsnc.smartiot.common.context.handler2.IFunctionHandler;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.ism.IIntegratedSessionManager;
import com.hdbsnc.smartiot.common.otp.url.parser.UrlParseException;
import com.hdbsnc.smartiot.common.otp.url.parser.UrlParser;
import com.hdbsnc.smartiot.common.otp.url.parser.vo.Url;
import com.hdbsnc.smartiot.pdm.ap.instance.AbstractServiceProcessor;
import com.hdbsnc.smartiot.util.logger.Log;


public class AuthContextProcessor2 extends AbstractServiceProcessor{

	private IConnectionManager cm;
	private UrlParser parser;
	private Log log;
	public AuthContextProcessor2(ICommonService service, String iid, IConnectionManager cm, IIntegratedSessionManager ism, Log log) {
		super(service, iid, ism);
		this.cm = cm;
		this.log = log;
		this.parser = UrlParser.getInstance();
	}

	@Override
	protected void request(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		IElementHandler currHandler = this.getRootHandler().findHandler(inboundCtx.getPaths());
		if ((currHandler.type() & IElementHandler.FUNCTION) == IElementHandler.FUNCTION) {
			((IFunctionHandler) currHandler).process(inboundCtx, outboundCtx);
		} else {
			throw service.getExceptionfactory().createSysException("203", new String[]{inboundCtx.getFullPath()});
		}
	}

	@Override
	protected void response(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		
	}

	@Override
	protected void event(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		
	}

	@Override
	protected void transfer(OutboundContext outboundCtx) throws Exception {
		IConnection con = null;
		con = cm.getConnection(outboundCtx.getSID());
		if(con==null) throw service.getExceptionfactory().createSysException("602");
		Url url = Url.createOtp(outboundCtx.getPaths(), outboundCtx.getParams());
		url.setUserInfo(outboundCtx.getSID(), outboundCtx.getSPort());
		url.setHostInfo(outboundCtx.getTID(), outboundCtx.getTPort());	
		if(outboundCtx.getTransmission()!=null) url.addFrag("trans", outboundCtx.getTransmission());
		if(outboundCtx.containsContent()) url.addFrag("cont", outboundCtx.getContentType());
		try {
			if(outboundCtx.containsContent()){
				StringBuilder sb = new StringBuilder();
				sb.append(parser.parse(url));// \r\n 자동으로 추가됨. 
				sb.append(new String(outboundCtx.getContent().array()));
				sb.append("\r\n");
				log.debug("SEND: "+sb.toString());
				con.write(sb.toString());
			}else{
				String p = parser.parse(url);
				log.debug("SEND: "+p);
				con.write(p);
			}
		} catch (IOException | UrlParseException e) {
			throw service.getExceptionfactory().createSysException("603", new String[]{e.getMessage()});
		}	
	}

}
