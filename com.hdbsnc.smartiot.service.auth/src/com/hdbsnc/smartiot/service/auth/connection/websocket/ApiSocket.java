package com.hdbsnc.smartiot.service.auth.connection.websocket;

import java.io.IOException;
import java.util.Arrays;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;

import com.hdbsnc.smartiot.common.otp.impl.Otp;
import com.hdbsnc.smartiot.common.otp.url.parser.UrlParseException;
import com.hdbsnc.smartiot.common.otp.url.parser.UrlParser;
import com.hdbsnc.smartiot.common.otp.url.parser.vo.Url;
import com.hdbsnc.smartiot.service.auth.connection.impl.ConnectionHandleChain;
import com.hdbsnc.smartiot.service.auth.impl.connection.WebSocketConnection;
import com.hdbsnc.smartiot.util.logger.Log;
import com.hdbsnc.smartiot.util.servicepool.ServicePool;

public class ApiSocket implements WebSocketListener{

	private WebSocketConnection con = null;
	private UrlParser parser;
	private ServicePool pool;
	private Log logger;
	private ConnectionHandleChain chc;
	
	public ApiSocket(ServicePool pool, Log logger, ConnectionHandleChain chc){
		this.parser = UrlParser.getInstance();
		this.pool = pool;
		this.logger = logger;
		this.chc = chc;
	}
	
	public Session getSession(){
		return this.con.getSession();
	}
	
	@Override
	public void onWebSocketClose(int arg0, String arg1) {
		try {
			this.con.disconnect();
			this.con = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void onWebSocketConnect(Session session) {
		if(con==null) {
			this.con = new WebSocketConnection(session);
		}else{
			this.con.changeSession(session);
		}
	}

	@Override
	public void onWebSocketError(Throwable cause) {
		cause.printStackTrace(System.err);
	}

	private Otp otp = null;
	@Override
	public void onWebSocketBinary(byte[] payload, int offset, int len) {
		if(otp!=null){
			if(len<=0){
				return; 
			}else {
				len = len-1;
				otp.setContent(Arrays.copyOfRange(payload, offset, len));
//				chc.currentThreadHandle(con, otp);
				chc.handle(con, otp);
			}
		}else{
			//널인데 바이너리로 올 수 있는가?
		}
		
	}

	@Override
	public void onWebSocketText(String message) {
		if(con.isConnected()){
			if(message==null) return;	
			logger.debug(message);
			String[] div = message.split("\r\n");
			message = div[0];
			Url msgUrl;
			try {
				msgUrl = parser.parse(message);
			} catch (UrlParseException e) {
				// OTP파싱이 불가능하면 처리 불가.
				e.printStackTrace();
				return;
			}
			otp = new Otp(msgUrl);
			if(otp.hasContent()){
				if(div.length>1){
					otp.setContent(div[1].getBytes());
				}else{
					//onWebSocketBinary에서 컨텐츠를 otp에 넣어주어야 하므로 그냥 리턴. 
					return;
				}
			}
			//chc.currentThreadHandle(con, otp);
			chc.handle(con, otp);
		}
		
	}

}
