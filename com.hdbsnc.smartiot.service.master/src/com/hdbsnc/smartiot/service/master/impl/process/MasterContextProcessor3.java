package com.hdbsnc.smartiot.service.master.impl.process;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.hdbsnc.smartiot.common.ICommonService;
import com.hdbsnc.smartiot.common.aim.IAdapterInstanceManager;
import com.hdbsnc.smartiot.common.connection.IConnection;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.IContextCallback;
import com.hdbsnc.smartiot.common.context.IContextTracer;
import com.hdbsnc.smartiot.common.context.handler.IDirectoryHandler;
import com.hdbsnc.smartiot.common.context.handler.IElementHandler;
import com.hdbsnc.smartiot.common.context.handler2.IFunctionHandler;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.ism.IIntegratedSessionManager;
import com.hdbsnc.smartiot.common.otp.url.parser.UrlParseException;
import com.hdbsnc.smartiot.common.otp.url.parser.UrlParser;
import com.hdbsnc.smartiot.common.otp.url.parser.vo.Url;
import com.hdbsnc.smartiot.common.pm.vo.IMsgMastObj;
import com.hdbsnc.smartiot.pdm.ap.instance.AbstractServiceProcessor;
import com.hdbsnc.smartiot.service.master.slavemanager.Server;
import com.hdbsnc.smartiot.service.master.slavemanager.SlaveServerManager;

public class MasterContextProcessor3 extends AbstractServiceProcessor{

	private SlaveServerManager ssm;
	private UrlParser parser;
	
	public MasterContextProcessor3(ICommonService service, String masterId, IIntegratedSessionManager ism, SlaveServerManager ssm) {
		super(service, masterId, ism);
		this.ssm = ssm;
		this.parser = UrlParser.getInstance();
	}

	@Override
	protected void transfer(OutboundContext outboundCtx) throws Exception {
		if(outboundCtx==null) return; // outboundCtx가 없으므로 처리할 것이 없음. event나 다른 곳으로 전달되는 response들 이다.
		String tid = outboundCtx.getTID();
		String sid = outboundCtx.getSID();
		Server server = null;
		IConnection serverCon = null;
		String transferType = outboundCtx.getTransmission();
		if(transferType==null || transferType.equals("req") || transferType.equals("request")){
			if(tid==null || tid.equals("") || tid.equals("this")){
				server = ssm.getServerByServerSessionKey(sid);
				if(server==null) throw service.getExceptionfactory().createSysException("503", new String[]{ sid });
			}else{
				server = ssm.getServerByDeviceId(tid);
				if(server==null) throw service.getExceptionfactory().createSysException("502", new String[]{ tid });
			}
			serverCon = server.getConnection();
			outboundCtx.setSID(server.getSessionKey());
		}else if(transferType.equals("res") || transferType.equals("response")){
			server = ssm.getServerByServerSessionKey(sid);
			if(server==null) throw service.getExceptionfactory().createSysException("503", new String[]{ sid });
			serverCon = server.getConnection();
		}else if(transferType.equals("evt") || transferType.equals("event")){
			server = ssm.getServerByDeviceId(tid);
			if(server==null) throw service.getExceptionfactory().createSysException("502", new String[]{ tid });
			serverCon = server.getConnection();
			outboundCtx.setSID(server.getSessionKey());
		}else{
			throw service.getExceptionfactory().createSysException("504");
		}
		
		if(serverCon==null) throw service.getExceptionfactory().createSysException("505");
		Url url = Url.createOtp(outboundCtx.getPaths(), outboundCtx.getParams());
		url.setUserInfo(outboundCtx.getSID(), outboundCtx.getSPort());
		url.setHostInfo(outboundCtx.getTID(), outboundCtx.getTPort());	
		if(outboundCtx.getTransmission()!=null) url.addFrag("trans", outboundCtx.getTransmission());
		if(outboundCtx.containsContent()) url.addFrag("cont", outboundCtx.getContentType());
		try {
			if(outboundCtx.containsContent()){
				StringBuilder sb = new StringBuilder();
				sb.append(parser.parse(url));// \r\n 자동으로 추가됨. 
				sb.append(new String(outboundCtx.getContent().array()));
				sb.append("\r\n");
				server.getConnection().write(sb.toString());
			}else{
				server.getConnection().write(parser.parse(url));
			}
		} catch (IOException | UrlParseException e) {
			throw service.getExceptionfactory().createSysException("506", new String[]{e.getMessage()});
		}	
	}

	/**
	 * 마스터서버로 올 수 있는 request 유형들
	 * 1. 내부에서 요청. TID를 확인해서 해당 슬래이브서버로 보내야한다.
	 * 2. 슬래이브서버에서 요청. TID가 내부에서 처리할 수 없으므로 바이패스. 다른 슬래이브서버로 보내야한다.
	 * 3. 슬래이브서버에서 요청. TID가 내부에 존재하므로 AIM으로 보낸다.
	 * 4. 마스터서버의 특정 기능 수행을 요청. process를 타면 된다.
	 * @param ctx
	 * @throws Exception
	 */
	@Override
	protected void request(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		String tid = inboundCtx.getTID();
		if(tid==null || tid.equals("this") || tid.equals(ssm.getMasterServerId())){
			masterProcess(inboundCtx, outboundCtx);
		}else{
			//TID가 존재하는 슬래이브 서버를 찾아서 전달. 
			requestToSlaveServer(inboundCtx, outboundCtx);
		}
	}

	/**
	 * 마스터서버로 올 수 있는 response 유형들 
	 * 1. 내부에서 온 응답. 슬래이브서버로 보내야 한다.
	 * 2. 슬래이브서버에서 온 응답. TID가 내부에 존재하면 내부에서 처리.
	 * 3. 슬래이브서버에서 온 응답. TID가 존재하는 슬래이브 서버를 찾아서 전달.
	 * 4. 마스터서버의 요청에 의한 응답. 내부 contextTracer 를 호출. 
	 * @param ctx
	 * @throws Exception
	 */
	@Override
	protected void response(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		String tid = inboundCtx.getTID();
		String seq = inboundCtx.getSPort();
		if(tid==null || tid.equals("this") || tid.equals(ssm.getMasterServerId())){
			if(seq==null || seq.equals("")){
				masterProcess(inboundCtx, outboundCtx);
				outboundCtx.dispose();
			}else{
				//ContextTracer에게 넘겨서 원 호출자가 처리할 수 있도록 해 주어야 한다. 
				String sid = inboundCtx.getSID();
				Server server;
				if(sid.startsWith("sid-")){
					server = ssm.getServerByServerSessionKey(sid);
				}else{
					server = ssm.getServerByServerId(sid);
				}
				server.pollAndCallContextTracer(seq, inboundCtx);
				outboundCtx.dispose();
			}
		}else{
			responseToSlaveServer(inboundCtx);
			outboundCtx.dispose();
		}
	}

	/**
	 * 마스터서버로 올 수 있는 event 유형들 
	 * 1. 내부에서 슬래이브로 보내는 이벤트.
	 * 2. 슬래이브서버에서 마스터로 보내는 이벤트.
	 * 3. 마스터서버에서 슬래이브로 보내는 이벤트.
	 * 4. 슬래이브서버에서 슬래이브서버로 전달되는 이벤트.
	 * @param ctx
	 * @throws Exception
	 */
	@Override
	protected void event(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		String tid = inboundCtx.getTID();
		if(tid==null || tid.equals("this") || tid.equals(ssm.getMasterServerId())){
			masterProcess(inboundCtx, outboundCtx);
		}else{
			eventToSlaveServer(inboundCtx, outboundCtx);
		}
	}

	private void masterProcess(IContext inboundCtx, OutboundContext outboundCtx) throws Exception{
		IElementHandler currHandler = this.getRootHandler().findHandler(inboundCtx.getPaths());
		if ((currHandler.type() & IElementHandler.FUNCTION) == IElementHandler.FUNCTION) {
			((IFunctionHandler) currHandler).process(inboundCtx, outboundCtx);
		} else {
			throw service.getExceptionfactory().createSysException("203", new String[]{inboundCtx.getFullPath()});
		}
	}
	
	private void requestToSlaveServer(IContext inboundCtx, OutboundContext outboundCtx) throws Exception{
		String tid = inboundCtx.getTID();
		String tPort = inboundCtx.getTPort();
		Server targetServer = ssm.getServerByDeviceId(tid);
		if(targetServer==null){
			throw service.getExceptionfactory().createSysException("502", new String[]{tid});
			//throw new Exception("tid("+tid+")를 처리할 서버가 없음.");
		}
		outboundCtx.setTID(tid);
		outboundCtx.setTPort(tPort);
		if(inboundCtx.containsContent()){
			outboundCtx.setContenttype(inboundCtx.getContentType());
			outboundCtx.setContent(inboundCtx.getContent());
		}
		outboundCtx.setSID(targetServer.getSessionKey());
		outboundCtx.setSPort(targetServer.offerContextTracer(inboundCtx, new IContextCallback(){
			@Override
			public void responseSuccess(IContextTracer ctxTracer) {
				IContext inboundCtx = ctxTracer.getRequestContext();
				IContext outterCtx = ctxTracer.getResponseContext();
				OutboundContext outboundCtx = new OutboundContext();			
				outboundCtx.setSID(inboundCtx.getSID());
				outboundCtx.setSPort(inboundCtx.getSPort());
				outboundCtx.setTID(inboundCtx.getTID());
				outboundCtx.setTPort(inboundCtx.getTPort());
				outboundCtx.setPaths(outterCtx.getPaths());
				outboundCtx.setParams(outterCtx.getParams());
				if(outterCtx.containsContent()){
					outboundCtx.setContent(outterCtx.getContent());
					outboundCtx.setContenttype(outterCtx.getContentType());
				}
				try {
					transfer(outboundCtx);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void responseFail(IContextTracer ctxTracer) {
				IContext inboundCtx = ctxTracer.getRequestContext();
				OutboundContext outboundCtx = new OutboundContext();
				outboundCtx.setSID(inboundCtx.getSID());
				outboundCtx.setSPort(inboundCtx.getSPort());
				outboundCtx.setTID(inboundCtx.getTID());
				outboundCtx.setTPort(inboundCtx.getTPort());
				outboundCtx.setPaths(new ArrayList<String>(inboundCtx.getPaths()));
				outboundCtx.getPaths().add("nack");
				outboundCtx.setParams(new HashMap<String, String>());
				IMsgMastObj obj = service.getExceptionfactory().getMsgInfo("501");
				if(obj!=null){
					outboundCtx.getParams().put("code", obj.getOuterCode());
					outboundCtx.getParams().put("type", obj.getType());
					outboundCtx.getParams().put("msg", obj.getMsg());
				}else{
					outboundCtx.getParams().put("code", "none");
					outboundCtx.getParams().put("type", "none");
					outboundCtx.getParams().put("msg", "none");
				}
//				outboundCtx.getParams().put("msg", "슬래이브서버에서 응답이 오지 않습니다.");
				outboundCtx.setTransmission("res");
				//특정시간동안 응답이 오지 않아서 발생하는 실패. 
				try {
					transfer(outboundCtx);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		}).getSeq());
		outboundCtx.setTransmission("res");
	}
	
	private void responseToSlaveServer(IContext inboundCtx) throws Exception{
		String slaveSid = inboundCtx.getSID();
		Server selfServer = ssm.getServerByServerSessionKey(slaveSid);
		selfServer.pollAndCallContextTracer(inboundCtx.getSPort(), inboundCtx);
	}
	
	private void eventToSlaveServer(IContext inboundCtx, OutboundContext outboundCtx) throws Exception{
		String tid = inboundCtx.getTID();
		Server targetServer = ssm.getServerByDeviceId(tid);
		outboundCtx.setTPort(null); //혹시나 발생할 오동작을 위해 제거. 이벤트는 seq가 필요 없다. 
		outboundCtx.setSID(targetServer.getSessionKey());
		outboundCtx.setSPort(null); //혹시나 발생할 오동작을 위해 제거. 이벤트는 seq가 필요 없다.
		outboundCtx.setTransmission("evt");
		if(inboundCtx.containsContent()){
			outboundCtx.setContenttype(inboundCtx.getContentType());
			outboundCtx.setContent(inboundCtx.getContent());
		}
	}
	

}
