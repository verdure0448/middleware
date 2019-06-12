package com.hdbsnc.smartiot.service.auth.impl.process.handler;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.hdbsnc.smartiot.common.connection.IConnection;
import com.hdbsnc.smartiot.common.connection.IConnectionManager;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.IContextCallback;
import com.hdbsnc.smartiot.common.context.IContextTracer;
import com.hdbsnc.smartiot.common.context.handler.impl.AbstractFunctionHandler;
import com.hdbsnc.smartiot.common.otp.url.parser.UrlParseException;
import com.hdbsnc.smartiot.common.otp.url.parser.UrlParser;
import com.hdbsnc.smartiot.common.otp.url.parser.vo.Url;
import com.hdbsnc.smartiot.common.pm.IProfileManager;
import com.hdbsnc.smartiot.common.pm.vo.IInstanceObj;
import com.hdbsnc.smartiot.service.auth.impl.process.HashKeyGenerator;
import com.hdbsnc.smartiot.service.master.IMasterService;

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
	private IConnectionManager cm;
	private UrlParser parser;
	private HashKeyGenerator keyGen;
	
	public AuthOpenHandler(IProfileManager pm, IMasterService mss, IConnectionManager cm){
		super("open");
		this.pm = pm;
		this.mss = mss;
		this.cm = cm;
		this.parser = UrlParser.getInstance();
		try {
			this.keyGen = new HashKeyGenerator("MD5", mss.getServerId());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void process(IContext inboundCtx) throws Exception {
		String did = inboundCtx.getSID();//자기 자신의 브라우져, 혹은 단말기의 식별자. 꼭 있어야 함. 없다면 uid의 기본 did를 사용. 
		String tid = inboundCtx.getTID();//인증서버 식별자 이므로 무시해도 됨. this 혹은 서버 식별자. 
		Map<String, String> params = inboundCtx.getParams();
		String uid = params.get(KEY_UID);
		
		//upass는 Base64로 변환해서 처리하는 것으로 향후 수정.
		//upass = new String(Base64.decodeBase64(upass));
		String upass = params.get(KEY_UPASS);
		
		//did, iid, sid, uid 가 필요함. 
		
		//pm에서 인증을 수행하여 sid를 발급할 것. 인증실패시 바로 fail루틴 타서 실패 메시지 보내면 끝. 
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
			return;
		}else{
			//로그인 실패.
			//현재 연결로 로그인 실패 메시지를 보낸다.
			Url resUrl = Url.createOtp();
			resUrl.setUserInfo(inboundCtx.getSID(), inboundCtx.getSPort());
			resUrl.setHostInfo("this", null);
			resUrl.setPaths(currentPaths()).addPath("nack");
			resUrl.addQuery("code", "100");
			resUrl.addQuery("msg","장치식별자, 사용자아이디, 사용자패스워드를 다시 확인하세요.");
			resUrl.addQuery("type", "warn");
			resUrl.addFrag("trans", "res");
			IConnection con = cm.getConnection(inboundCtx.getSID());			
			try {
				String p = parser.parse(resUrl);
				System.out.println("AUTH SEND: "+p);
				con.write(p);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (UrlParseException e) {
				e.printStackTrace();
			}
			return;
		}		
	}

	@Override
	public void responseSuccess(IContextTracer ctxTracer) {
		//응답을 확인해서 ack이면 요청자에게 응답을 보낸다.
		IContext request = ctxTracer.getRequestContext();
		IContext response = ctxTracer.getResponseContext();
		
		if(!response.getPaths().contains("ack")){
			//nack이므로 로그인 실패 메시지를 보낸다. 
			Url resUrl = Url.createOtp(response.getPaths(), response.getParams());
			resUrl.setUserInfo(request.getSID(), request.getSPort());
			resUrl.setHostInfo("this", null);
			resUrl.addFrag("trans", "res");
			IConnection con = cm.getConnection(request.getSID());			
			try {
				String p = parser.parse(resUrl);
				System.out.println("AUTH SEND: "+p);
				con.write(p);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (UrlParseException e) {
				e.printStackTrace();
			}
			return;
		}
		String sid = request.getParams().get(KEY_SID); //로그인 성공하여 발급받은 세션키. 
		String connectDomain = response.getParams().get(KEY_CONNECT_DOMAIN);
		String url = response.getParams().get(KEY_URL);
		
		Url resUrl = Url.createOtp();
		resUrl.setUserInfo(sid, request.getSPort());//인증된 sid
		resUrl.setHostInfo("this", request.getTPort());
		resUrl.setPaths(currentPaths()).addPath("ack");
		resUrl.addQuery(KEY_CONNECT_DOMAIN, connectDomain);
		resUrl.addQuery(KEY_URL, url);
		resUrl.addFrag("trans", "res");
		
		IConnection con = cm.getConnection(request.getSID());
		//인증을 받았으므로 세션키를 넣어둔다. 다음 요청시 확인을 빨리 하기 위해서...
		cm.putConnection(sid, con);
		
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
	
	
	
//	private boolean check(List l1, List l2){
//		if(l1.size()==l2.size()){
//			for(int i=0,s=l1.size();i<s;i++){
//				if(l1.get(i).equals(l2.get(i))){
//					continue;
//				}else{
//					return false;
//				}
//			}
//			return true;
//		}else{
//			return false;
//		}
//	}

	@Override
	public void responseFail(IContextTracer ctxTracer) {
		//인증에 실패했거나 마스터와 슬래이브의 통신등의 문제로 타임아웃 되었다. 
		System.out.println("인증실패(타임아웃).");
	}
	
	private IInstanceObj auth(String did, String uid, String upass) throws Exception{
		
		//PM 구현되면 추가해야 함. 
		return pm.integrationAuth(uid, upass, did);
	}
	
	

}
