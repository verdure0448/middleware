package com.hdbsnc.smartiot.common.connection.impl;


import java.io.IOException;

import com.hdbsnc.smartiot.common.connection.IConnection;
import com.hdbsnc.smartiot.common.connection.impl.ConnectionHandleChain.Handle;
import com.hdbsnc.smartiot.common.otp.impl.Otp;
import com.hdbsnc.smartiot.common.otp.url.parser.UrlParseException;
import com.hdbsnc.smartiot.common.otp.url.parser.UrlParser;
import com.hdbsnc.smartiot.common.otp.url.parser.vo.Url;
import com.hdbsnc.smartiot.util.logger.Log;


public class OtpParserHandler extends AbstractConnectionHandler{

	private UrlParser parser;
	private Otp needContent = null;
	private Log log;
	
	public OtpParserHandler(Log log) {
		super("OtpParser");
		this.log = log;
		// success 하지 않으면 뒤로 넘어가지 않는다. 
		this.setFailToNext(false);
		this.parser = UrlParser.getInstance();

	}

	@Override
	public boolean resolve(Handle msg) {
		if(msg.getPacket()==null) return false;
		Url tempUrl = null;
		Otp tempRequest = null;
		if(needContent!=null){
			needContent.setContent(msg.getPacket());
			tempRequest = needContent;
			needContent = null;
		}else{
			String tempPacket = new String(msg.getPacket());
			try {
				tempUrl = parser.parse(tempPacket);
			} catch (UrlParseException e) {
				log.err(e);
				return false;
			}
			tempRequest = new Otp(tempUrl);
			if(tempRequest.hasContent()){
				needContent = tempRequest;
				return false;
			}
		}
		msg.setMsg(tempRequest);
		return true;	
	}

	@Override
	public void success(Handle msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fail(Handle msg) {
		IConnection con = msg.getConnection();
		Url resUrl = Url.createOtp();
		resUrl.setUserInfo("none", null);
		resUrl.setHostInfo("none", null);
		resUrl.addPath("nack");
		resUrl.getQuery().put("code", "103");
		resUrl.getQuery().put("type", "error");
		resUrl.getQuery().put("msg", "알 수 없는 프로토콜 유형입니다.");
		resUrl.addFrag("trans", "res");
		
		try {
			con.write(UrlParser.getInstance().parse(resUrl));
		} catch (IOException e) {
			log.err(e);
		} catch (UrlParseException e) {
			log.err(e);
		}
		
	}

}
