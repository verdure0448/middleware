package com.hdbsnc.smartiot.adapter.websocketapi.connection.handler;

import java.io.IOException;
import java.util.HashMap;

import com.hdbsnc.smartiot.adapter.websocketapi.connection.impl.AbstractConnectionHandler;
import com.hdbsnc.smartiot.adapter.websocketapi.connection.impl.ConnectionHandleChain_old.Handle;
import com.hdbsnc.smartiot.common.connection.IConnection;
import com.hdbsnc.smartiot.common.connection.IConnectionManager;
import com.hdbsnc.smartiot.common.ism.sm.ISession;
import com.hdbsnc.smartiot.common.ism.sm.ISessionManager;
import com.hdbsnc.smartiot.common.otp.impl.Otp;
import com.hdbsnc.smartiot.common.otp.url.parser.UrlParseException;
import com.hdbsnc.smartiot.common.otp.url.parser.UrlParser;
import com.hdbsnc.smartiot.common.otp.url.parser.vo.Url;
import com.hdbsnc.smartiot.util.logger.Log;

public class ConnectionManagerHandler_old extends AbstractConnectionHandler{

	private IConnectionManager cm;
	private ISessionManager sm;
	private Log logger;
	
	public ConnectionManagerHandler_old(IConnectionManager cm, ISessionManager sm, Log logger){
		super("ConnectionManager");
		//CM에 connection을 넣지 못하면 뒤로 가지 못한다.
		this.setFailToNext(false);
		this.cm = cm;
		this.sm = sm;
		this.logger = logger;
	}
	
	@Override
	public boolean resolve(Handle msg) {
		Otp otp = msg.getOtp();
		if(otp==null) {
			return false;
		}
		String sid = otp.getHeader().getSID();
		ISession sess = sm.getSessionBySessionKey(sid);
		if(sess!=null && sess.getState() == ISession.SESSION_STATE_ACTIVATE){
			logger.info("세션키가 할당된 상태이므로 처리가능.(did: "+sess.getDeviceId()+")");
			return true;
		}else{
			logger.info("존재하지 않는 세션키로 처리 거부.");
			return false;
		}
	}

	@Override
	public void success(Handle msg) {
		Otp otp = msg.getOtp();
		String sid = otp.getHeader().getSID(); 
		cm.putConnection(sid, msg.getConnection());
	}

	@Override
	public void fail(Handle msg) {
		IConnection con = msg.getConnection();
		Otp request = msg.getOtp();
		Url resUrl = Url.createOtp(request.getHeader().getPaths(), new HashMap<String, String>());
		resUrl.setUserInfo(request.getHeader().getSID(), request.getHeader().getSPort());
		resUrl.setHostInfo(request.getHeader().getTID(), request.getHeader().getTPort());
		resUrl.addPath("nack");
		resUrl.getQuery().put("code", "101");
		resUrl.getQuery().put("type", "error");
		resUrl.getQuery().put("msg", "세션키가 유효하지 않습니다.");
		resUrl.addFrag("trans", "res");
		
		try {
			con.write(UrlParser.getInstance().parse(resUrl));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UrlParseException e) {
			e.printStackTrace();
		}
		
	}

}
