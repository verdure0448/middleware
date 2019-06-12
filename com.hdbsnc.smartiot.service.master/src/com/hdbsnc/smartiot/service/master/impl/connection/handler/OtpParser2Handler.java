package com.hdbsnc.smartiot.service.master.impl.connection.handler;

import com.hdbsnc.smartiot.common.otp.impl.Otp;
import com.hdbsnc.smartiot.common.otp.url.parser.UrlParseException;
import com.hdbsnc.smartiot.common.otp.url.parser.UrlParser;
import com.hdbsnc.smartiot.common.otp.url.parser.vo.Url;
import com.hdbsnc.smartiot.service.master.connection.impl.AbstractConnectionHandler;
import com.hdbsnc.smartiot.service.master.connection.impl.ConnectionHandleChain.Handle;
import com.hdbsnc.smartiot.util.logger.Log;

public class OtpParser2Handler  extends AbstractConnectionHandler{
	private UrlParser parser;
	private Log log;
	
	public OtpParser2Handler(Log log){
		super("OtpParser");
		//파싱이 실패하면 뒤로 핸들러로 넘어가면 안된다. 
		this.setFailToNext(false);
		this.log = log;
		parser = UrlParser.getInstance();
	}
	private Otp needContent = null;
	
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
			log.debug("RECV: "+tempPacket);
			try {
				tempUrl = parser.parse(tempPacket);
			} catch (UrlParseException e) {
				e.printStackTrace();
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
		
	}

	@Override
	public void fail(Handle msg) {
		// 오류 코드를 보내준다.
		
	}

}