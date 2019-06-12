package com.hdbsnc.smartiot.adapter.websocketapi.connection.websocket;

import java.io.IOException;
import java.util.Arrays;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;

import com.hdbsnc.smartiot.adapter.websocketapi.connection.WebSocketConnection;
import com.hdbsnc.smartiot.common.connection.impl.ConnectionHandleChain;
import com.hdbsnc.smartiot.common.otp.impl.Otp;
import com.hdbsnc.smartiot.common.otp.url.parser.UrlParseException;
import com.hdbsnc.smartiot.common.otp.url.parser.UrlParser;
import com.hdbsnc.smartiot.common.otp.url.parser.vo.Url;
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
//		cause.printStackTrace(System.err);
		logger.err(cause.getMessage());
	}

	private Otp otp = null;
	/**
	 * 문자열로 모두 처리하므로사용할 케이스 없음.
	 */
	@Override
	public void onWebSocketBinary(byte[] payload, int offset, int len) {
		if(otp!=null){
			if(len<=0){
				return; 
			}else {
				len = len-1;
				otp.setContent(Arrays.copyOfRange(payload, offset, len));
				//chc.currentThreadHandle(con, otp);
				
				chc.handle(con, otp);
			}
		}else{
			//널인데 바이너리로 올 수 있는가?
		}
		
//		if(len<=0){
//			System.out.println("onWebSocketBinary가 호출되었으나 사이즈가 0 임.");
//			return;
//		}else{
//			String packet = new String(Arrays.copyOfRange(payload, offset, len));
//			//System.out.println("onWebSocketBinary("+len+"): "+packet);
//			onWebSocketText(packet);
//		}
	}
	//public static final SimpleDateFormat defaultTimeFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss SSS");
	@Override
	public void onWebSocketText(String message) {
		if(con.isConnected()){
			if(message==null) return;	
			logger.debug(message.trim());
			String[] div = message.split("\r\n");
			message = div[0];
			Url msgUrl;
			try {
				msgUrl = parser.parse(message);
			} catch (UrlParseException e) {
				logger.err(e);
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
