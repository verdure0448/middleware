package com.hdbsnc.smartiot.service.auth.impl.process.handler2;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import com.hdbsnc.smartiot.common.connection.IConnection;
import com.hdbsnc.smartiot.common.connection.IConnectionManager;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.IContextCallback;
import com.hdbsnc.smartiot.common.context.IContextProcessor;
import com.hdbsnc.smartiot.common.context.IContextTracer;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractFunctionHandler;
import com.hdbsnc.smartiot.common.pm.IProfileManager;
import com.hdbsnc.smartiot.common.pm.vo.IInstanceObj;
import com.hdbsnc.smartiot.common.pm.vo.IMsgMastObj;
import com.hdbsnc.smartiot.service.auth.impl.process.HashKeyGenerator;
import com.hdbsnc.smartiot.service.master.IMasterService;
import com.hdbsnc.smartiot.util.logger.Log;

public class AuthOpenHandler extends AbstractFunctionHandler implements IContextCallback{

	public static final String KEY_UID = "uid";
	public static final String KEY_UPASS = "upass";
	
	public static final String KEY_IID = "iid";
	public static final String KEY_DID = "did";
	public static final String KEY_SID = "sid";
	
	
	public static final String KEY_URL = "url";
	public static final String KEY_CONNECT_DOMAIN = "comsvr";
	
	private IProfileManager pm;
	private IMasterService mss;
	private HashKeyGenerator keyGen;
	private IContextProcessor processor;
	private IConnectionManager cm;
	private Log log;
	
	public AuthOpenHandler(IProfileManager pm, IMasterService mss, IContextProcessor processor, IConnectionManager cm, Log log){
		super("open");
		this.pm = pm;
		this.mss = mss;
		this.processor = processor;
		this.cm = cm;
		this.log = log;
		try {
			this.keyGen = new HashKeyGenerator("MD5", mss.getServerId());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			log.err(e);
		}
	}
	
	@Override
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		String did = inboundCtx.getSID();//자기 자신의 브라우져, 혹은 단말기의 식별자. 꼭 있어야 함. 없다면 uid의 기본 did를 사용. 
		String tid = inboundCtx.getTID();//인증서버 식별자 이므로 무시해도 됨. this 혹은 서버 식별자. 
		Map<String, String> params = inboundCtx.getParams();
		String uid = params.get(KEY_UID);
		if(uid==null) uid = ""; //사용자아이디가 없으므로 빈값으로 치환.
		
		String upass = params.get(KEY_UPASS);
		if(upass!=null) {
			upass = new String(Base64.getDecoder().decode(upass));
		}else{
			upass = ""; //패스워드 없이 왔으므로 빈값으로 치환.
		}
		
		IInstanceObj result = auth(did, uid, upass);
		if(result!=null){
			//로그인 성공.
			//마스터에게 할당요청을 해야 한다.
			InnerContext innerCtx = new InnerContext();
			innerCtx.sid = did;
			innerCtx.tid = null;
			innerCtx.paths = Arrays.asList("slave","alloc");
			innerCtx.params = new HashMap<String, String>();
			innerCtx.params.put(KEY_IID, result.getInsId());
			innerCtx.params.put(KEY_DID, did);
			innerCtx.params.put(KEY_UID, uid);
			innerCtx.params.put(KEY_SID, keyGen.generateKey());
			
			mss.handOverContext(innerCtx, this);
			outboundCtx.dispose();
		}else{
			//로그인 실패.
			//현재 연결로 로그인 실패 메시지를 보낸다.
			outboundCtx.setTID("this");
			outboundCtx.addPath("nack");
			outboundCtx.putParam("code", "100");
			outboundCtx.putParam("msg","장치식별자, 사용자아이디, 사용자패스워드를 다시 확인하세요.");
			outboundCtx.putParam("type", "warn");
			outboundCtx.setTransmission("res");
		}		
	}

	@Override
	public void responseSuccess(IContextTracer ctxTracer) {
		//응답을 확인해서 ack이면 요청자에게 응답을 보낸다.
		IContext request = ctxTracer.getRequestContext();
		IContext response = ctxTracer.getResponseContext();
		
		OutboundContext resCtx = new OutboundContext(response);
		if(!response.getPaths().contains("ack")){
			//nack이므로 로그인 실패 메시지를 보낸다. 
			resCtx.setSID(request.getSID());
			resCtx.setSPort(request.getSPort());
			resCtx.setTID("this");
			resCtx.setTPort(null);
			resCtx.setTransmission("res");
			
		}else{
			String did = request.getSID();
			IConnection con = cm.getConnection(did);
			String sid = request.getParams().get(KEY_SID); //로그인 성공하여 발급받은 세션키. 
			cm.putConnection(sid, con);
			String connectDomain = response.getParams().get(KEY_CONNECT_DOMAIN);
			String url = response.getParams().get(KEY_URL);
			
			resCtx.setSID(sid);
			resCtx.setSPort(request.getSPort());
			resCtx.setTID("this");
			resCtx.setTPort(request.getTPort());
			resCtx.setPaths(currentPaths());
			resCtx.addPath("ack");
			resCtx.putParam(KEY_CONNECT_DOMAIN, connectDomain);
			resCtx.putParam(KEY_URL, url);
			resCtx.setTransmission("res");
		}
		try {
			processor.process(resCtx);
		} catch (Exception e) {
			e.printStackTrace();
			log.err(e);
		}
		
	}

	@Override
	public void responseFail(IContextTracer ctxTracer) {		
		IMsgMastObj obj = this.getCommonService().getExceptionfactory().getMsgInfo("104");
		IContext requestCtx = ctxTracer.getRequestContext(); //요청
		OutboundContext tempCtx = new OutboundContext(requestCtx);
		tempCtx.getPaths().add("nack");
		tempCtx.getParams().put("code", obj.getOuterCode());
		tempCtx.getParams().put("msg", obj.getMsg());
		tempCtx.getParams().put("type", obj.getType());
		tempCtx.setTransmission("res");
		try {
			processor.process(tempCtx);
		} catch (Exception e) {
			e.printStackTrace();
			log.err(e);
		}
	}
	
	private IInstanceObj auth(String did, String uid, String upass) throws Exception{
		return pm.integrationAuth(uid, upass, did);
	}
	
	

}
