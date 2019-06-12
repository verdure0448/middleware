package com.hdbsnc.smartiot.common.connection.impl;

import java.io.IOException;
import java.util.HashMap;

import com.hdbsnc.smartiot.common.connection.IConnection;
import com.hdbsnc.smartiot.common.connection.IConnectionManager;
import com.hdbsnc.smartiot.common.connection.impl.ConnectionHandleChain.Handle;
import com.hdbsnc.smartiot.common.ism.sm.ISession;
import com.hdbsnc.smartiot.common.ism.sm.ISessionManager;
import com.hdbsnc.smartiot.common.otp.impl.Otp;
import com.hdbsnc.smartiot.common.otp.url.parser.UrlParseException;
import com.hdbsnc.smartiot.common.otp.url.parser.UrlParser;
import com.hdbsnc.smartiot.common.otp.url.parser.vo.Url;
import com.hdbsnc.smartiot.util.logger.Log;

public class ConnectionManagerHandler extends AbstractConnectionHandler{

	private ISessionManager sm;
	private IConnectionManager cm;
	private Log log;
	
	public ConnectionManagerHandler(ISessionManager sm, Log log) {
		super("ConnectionManager");
		// success 를 하지 않으면 뒤로 넘어가지 않는다. 
		this.setFailToNext(false);
		this.cm = sm.getConnectionManager();
		this.sm = sm;
		this.log = log;
	}

	// gscp://user01:pwd1234@pulmuone.abdu.01/login#res:uri
	@Override
	public boolean resolve(Handle msg) {
		Object obj = msg.getMsg();
		if(obj instanceof Otp){
			Otp tempOtp = (Otp) obj;
			//TID 에 장치 식별자가 있음. OTP 프로토콜과는 다름. 
			String tempId = tempOtp.getHeader().getSID();
			
			if(tempId!=null && !tempId.equals("") && tempId.startsWith("sid-")){
				ISession sess = sm.getSessionBySessionKey(tempId);
				if(sess!=null && sess.getState() == ISession.SESSION_STATE_ACTIVATE){
					log.info("세션키가 할당된 상태이므로 처리가능.("+tempId+", "+sess.getDeviceId()+")");
					cm.putConnection(tempId, msg.getConnection());
					return true;
				}else{
					if(tempId==null) tempId = "sid-none";
					log.info("존재하지 않는 세션키로 처리 거부.("+tempId+")");
					return false;
				}
			}else{// 처음 로그인시에 타는 루틴. 최초 로그인시엔 TID로 con을 가져와야 한다. 
				tempId = tempOtp.getHeader().getTID();
				if(tempId!=null && !tempId.equals("") && !tempId.equals("this")){
					ISession sess = sm.getSessionByDeviceId(tempId);
					if(sess!=null && sess.getState() == ISession.SESSION_STATE_ACTIVATE){
						log.info("세션키가 할당된 상태이므로 처리가능.("+tempId+", "+sess.getDeviceId()+")");
						cm.putConnection(tempId, msg.getConnection());
						return true;
					}else{
						if(tempId==null) tempId = "sid-none";
						log.info("존재하지 않는 세션키로 처리 거부.("+tempId+")");
						return false;
					}
				}else{
					return false;
				}
			}
		}
		return false;
	}

	@Override
	public void success(Handle msg) {
		
	}

	@Override
	public void fail(Handle msg) {
		Object obj = msg.getMsg();
		if(obj instanceof Otp){
			IConnection con = msg.getConnection();
			Otp request = (Otp) obj;
			Url resUrl = Url.createOtp(request.getHeader().getPaths(), new HashMap<String, String>());
			resUrl.setUserInfo(request.getHeader().getSID(), request.getHeader().getSPort());
			resUrl.setHostInfo(request.getHeader().getTID(), request.getHeader().getTPort());
			resUrl.addPath("nack");
			resUrl.getQuery().put("code", "101");
			resUrl.getQuery().put("type", "error");
			
			if(cm.containsConnection(con) && !sm.containsSessionKey(request.getHeader().getSID())){
				resUrl.getQuery().put("msg", "재로그인 혹은 세션타임아웃이 확인되어 현재 세션의 접속을 종료 합니다.");
			}else{
				resUrl.getQuery().put("msg", "세션키가 유효하지 않아 현재 세션의 접속을 종료 합니다.(세션타임아웃 혹은 잘못된 세션키로 접속)");
			}
			resUrl.addFrag("trans", "res");
			
			try {
				con.write(UrlParser.getInstance().parse(resUrl));
			} catch (IOException e) {
				log.err(e);
			} catch (UrlParseException e) {
				log.err(e);
			}
		}else{
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

}
