package com.hdbsnc.smartiot.service.auth.impl.process.handler;

import java.io.IOException;

import com.hdbsnc.smartiot.common.connection.IConnection;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler.impl.AbstractFunctionHandler;
import com.hdbsnc.smartiot.common.otp.url.parser.UrlParseException;
import com.hdbsnc.smartiot.common.otp.url.parser.UrlParser;
import com.hdbsnc.smartiot.common.otp.url.parser.vo.Url;
import com.hdbsnc.smartiot.service.auth.impl.connection.OutterContext;
import com.hdbsnc.smartiot.service.master.IMasterService;

public class IsloginHandler extends AbstractFunctionHandler{

	private UrlParser parser;
	private IMasterService mss;
	public IsloginHandler(IMasterService mss) {
		super("islogin");
		this.parser = UrlParser.getInstance();
		this.mss = mss;
	}

	@Override
	public void process(IContext inboundCtx) throws Exception {
		String sid = inboundCtx.getSID();
		Url resUrl = Url.createOtp();
		resUrl.setUserInfo(inboundCtx.getSID(), inboundCtx.getSPort());
		resUrl.setHostInfo("this", null);
		resUrl.setPaths(currentPaths());
		if(sid==null || sid.equals("")){
			resUrl.addPath("nack");
		}else if(sid.startsWith("sid-")){
			if(mss.getSlaveServerManager().containsDeviceSessionKey(sid)){
				resUrl.addPath("ack");
			}else{
				resUrl.addPath("nack");
			}
		}else{
			if(mss.getSlaveServerManager().containsDevicesId(sid)){
				resUrl.addPath("ack");
			}else{
				resUrl.addPath("nack");
			}
		}
		resUrl.addFrag("trans", "res");
		OutterContext ctx = (OutterContext) inboundCtx;
		IConnection con = ctx.getConnection();
		try {
			String p = parser.parse(resUrl);
			System.out.println("AUTH SEND: "+p);
			con.write(p);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UrlParseException e) {
			e.printStackTrace();
		}
	}
	

}
