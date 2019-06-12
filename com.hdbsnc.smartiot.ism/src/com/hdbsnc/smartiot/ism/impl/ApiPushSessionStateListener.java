package com.hdbsnc.smartiot.ism.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import com.hdbsnc.smartiot.common.connection.IConnection;
import com.hdbsnc.smartiot.common.connection.IConnectionManager;
import com.hdbsnc.smartiot.common.ism.IIntegratedSessionManager;
import com.hdbsnc.smartiot.common.ism.sm.ISession;
import com.hdbsnc.smartiot.common.ism.sm.ISessionManager;
import com.hdbsnc.smartiot.common.ism.sm.ISessionState;
import com.hdbsnc.smartiot.common.ism.sm.ISessionStateListener;
import com.hdbsnc.smartiot.common.otp.url.parser.UrlParser;
import com.hdbsnc.smartiot.common.otp.url.parser.vo.Url;
import com.hdbsnc.smartiot.common.pm.IProfileManager;
import com.hdbsnc.smartiot.common.pm.vo.IInstanceObj;
import com.hdbsnc.smartiot.util.logger.Log;

public class ApiPushSessionStateListener implements ISessionStateListener{
	private IIntegratedSessionManager ism;
	private Log log;
	private IProfileManager pm;
	private UrlParser parser;
	
	static final String INSTANCE_TYPE = "smartiot.admin";
	static SimpleDateFormat formatter = new java.text.SimpleDateFormat(
			"yyyy/MM/dd/ HH:mm:ss sss");
	
	public ApiPushSessionStateListener(IIntegratedSessionManager ism, IProfileManager pm, Log log){
		this.ism = ism;
		this.log = log;
		this.pm = pm;
		this.parser = UrlParser.getInstance();
	}
	
	@Override
	public void changedState(int caller, int sessionState, ISession session) {
		switch(sessionState){
		case ISessionState.SESSION_STATE_DEACTIVATE:
		case ISessionState.SESSION_STATE_DISPOSE:
			Map<String, ISessionManager>  smMap = ism.getSessionManagerMap();
			ISessionManager sm;
			String iid;
			String insType;
			IInstanceObj insObj = null;
			synchronized(smMap){
				Iterator<ISessionManager> iter = smMap.values().iterator();
				while(iter.hasNext()){
					sm = iter.next();
					if(sm==null) continue;
					iid = sm.getAdapterInstanceId();
					try {
						insObj = pm.getInstanceObj(iid);
					} catch (Exception e) {
						log.err(iid+" : "+e.getMessage());
					}
					if(insObj==null) continue;
					insType = insObj.getInsType();
					if(insType!=null && insType.equals(INSTANCE_TYPE)){
						try {
							pushEvent(sm, sessionState, session);
						} catch (Exception e) {
							log.err(iid+" : "+e.getMessage());
						}
					}
				}
			}
			break;
		}
	}
	
	
//	opt://[SID]@[TID]/session/disconnect#tran:evt&cont:json/r/n
//	{
//	   instance.id : xxx,
//	   instance.name : xxx,
//	   device.id : xxxx,
//	   device.name : xxxx,
//	   user.id : xxxxxx,
//	   session.status : xxxxxx,    ===> 4:deactivate, 8:dispose
//	   event.time : xxxxxx
//	}/r/n
	
	private void pushEvent(ISessionManager sm, int sessionState, ISession evtSession) throws Exception{
		String iid = evtSession.getAdapterInstanceId();
		IInstanceObj insObj = pm.getInstanceObj(iid);
		if(insObj==null) return;
		String insName = insObj.getInsNm();
		
		IConnectionManager cm = sm.getConnectionManager();
		IConnection con;
		List<ISession> sessList = sm.getSessionList();
		if(sessList==null) return;
		ISession session;
		Iterator<ISession> iter = sessList.iterator();
		while(iter.hasNext()){
			session = iter.next();
			if(session==null) continue;
			if(session.getDeviceId().equals(evtSession.getDeviceId())) continue;
			con = cm.getConnection(session.getSessionKey());
			if(con!=null && con.isConnected()){
				sendEvent(con, evtSession, sessionState, insName);
			}
		}
	}
	
	private void sendEvent(IConnection con, ISession session, int sessionState, String insName){
		JSONObject json = new JSONObject();
		json.put("instance.id", session.getAdapterInstanceId());
		json.put("instance.name", insName);
		json.put("device.id", session.getDeviceId());
		json.put("device.name", session.getDeviceProfile().getDeviceNm());
		json.put("user.id", session.getUserId());
		json.put("session.status", sessionState);
		json.put("event.time", formatter.format(new Date()));
		Url resUrl = Url.createOtp();
		resUrl.addPath("session").addPath("disconnect");
		resUrl.setUserInfo(session.getSessionKey(), null);
		resUrl.setHostInfo("this", null);
		resUrl.addFrag("trans", "evt");
		resUrl.addFrag("cont", "json");
		
		try {
			String sendPacket = parser.parse(resUrl);
			sendPacket = sendPacket + json.toJSONString();
			con.write(sendPacket);
			log.debug(sendPacket);
		} catch (Exception e) {
			log.err(session.getAdapterInstanceId()+" : "+e.getMessage());
		}		
	}

}
