package com.hdbsnc.smartiot.service.slave.impl.process;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.hdbsnc.smartiot.common.ICommonService;
import com.hdbsnc.smartiot.common.aim.IAdapterInstanceManager;
import com.hdbsnc.smartiot.common.connection.IConnection;
import com.hdbsnc.smartiot.common.connection.IConnectionManager;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.IContextCallback;
import com.hdbsnc.smartiot.common.context.IContextTracer;
import com.hdbsnc.smartiot.common.context.handler.IElementHandler;
import com.hdbsnc.smartiot.common.context.handler2.IFunctionHandler;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.ism.IIntegratedSessionManager;
import com.hdbsnc.smartiot.common.otp.url.parser.UrlParseException;
import com.hdbsnc.smartiot.common.otp.url.parser.UrlParser;
import com.hdbsnc.smartiot.common.otp.url.parser.vo.Url;
import com.hdbsnc.smartiot.common.pm.vo.IMsgMastObj;
import com.hdbsnc.smartiot.pdm.ap.instance.AbstractServiceProcessor;
import com.hdbsnc.smartiot.service.slave.impl.Sss2;
import com.hdbsnc.smartiot.util.logger.Log;

public class SlaveContextProcessor2 extends AbstractServiceProcessor{

	private IAdapterInstanceManager aim;
	private IIntegratedSessionManager ism;
	private IConnectionManager cm;
	private UrlParser parser;
	private Sss2 sss;
	private Log log;
	
	public SlaveContextProcessor2(ICommonService service, String iid, IAdapterInstanceManager aim, Sss2 sss, IIntegratedSessionManager ism, IConnectionManager cm) {
		super(service, iid, ism);
		this.aim = aim;
		this.ism = ism;
		this.cm = cm;
		this.parser = UrlParser.getInstance();
		this.sss = sss;
		this.log = service.getLogger();
	}

	@Override
	protected void transfer(OutboundContext outboundCtx)
			throws Exception {
		if(outboundCtx==null) return; // outboundCtx가 없으므로 처리할 것이 없음. event나 다른 곳으로 전달되는 response들 이다.
		IConnection con = null;
		outboundCtx.setSID(sss.getSlaveServerSessionId());
		con = cm.getConnection(sss.getSlaveServerSessionId());
		if(con==null) throw service.getExceptionfactory().createSysException("602");
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
				con.write(sb.toString());
			}else{
				con.write(parser.parse(url));
			}
		} catch (IOException | UrlParseException e) {
			throw service.getExceptionfactory().createSysException("603", new String[]{e.getMessage()});
		}	
		
	}

	/**
	 * 슬래이브 서버로 올 수 있는 request 유형들
	 * 1. 마스터에서 슬래이브 서버의 기능 수행을 요청. 
	 * 2. 마스터에서 특정 DID의 장치를 제어하기 위한 명령을 요청. 
	 * 3. 내부에서 외부의 특정 DID의 장치를 제어하기 위해 마스터로 보내야하는 요청.
	 */
	@Override
	protected void request(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		String tid = inboundCtx.getTID();
		if(tid==null || tid.equals("this") || tid.equals(sss.getSlaveServerId())){
			slaveProcess(inboundCtx, outboundCtx);
		}else{
			if(ism.containsDeviceId(tid)){
				requestToAim(inboundCtx);
				outboundCtx.dispose();
			}else{
				requestToMaster(inboundCtx, outboundCtx);
			}
		}
	}

	/**
	 * 슬래이브 서버로 올 수 있는 response 유형들
	 * 1. 슬래이브 서버가 요청한 내용에 대한 응답을 마스터서버가 보내 줄 경우.
	 * 2. 내부에서 요청한 내용에 대한 응답을 마스터서버가 보내 줄 경우.
	 * 3. 마스터에서 요청한 내용에 대한 응답을 내부에서 보내 주 경우.
	 * 4. 마스터에서 요청한 내용에 대한 응답을 슬래이브서버에서 보내 줄 경우. (이케이스는 요청 핸들러에서 처리가 되므로 별도로 고민할 필요 없음) 
	 */
	@Override
	protected void response(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		String sid = inboundCtx.getSID();
		String tid = inboundCtx.getTID();
		if(!sss.currentState().equals(Sss2.STATE_ACTIVATE)){
			slaveProcess(inboundCtx, outboundCtx);
			return;
		}
		
		if(sid!=null && sid.equals("") && sid.equals("this") && sid.equals(sss.getSlaveServerSessionId())){
			String seq = inboundCtx.getSPort();
			if(seq==null || seq.equals("")){
				slaveProcess(inboundCtx, outboundCtx);
			}else{
				sss.pollAndCallContextTracer(seq, inboundCtx);
				outboundCtx.dispose();
			}
		}else{
			responseToMaster(inboundCtx);
			outboundCtx.dispose();
		}
	}

	/**
	 * 슬래이브 서버로 올 수 있는 event 유형들
	 * 1. 내부에서 마스터서버로 보내는 이벤트. 
	 * 2. 슬래이브서버에서 마스터서버로 보내는 이벤트. 
	 * 3. 마스터서버에서 슬래이브서버로 보내는 이벤트.
	 * 4. 마스터서버에서 내부장치로 보내는 이벤트. 
	 */
	@Override
	protected void event(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		String sid = inboundCtx.getSID();
		String tid = inboundCtx.getTID();
		if( tid!=null && (tid.equals("this") || tid.equals(sss.getSlaveServerId()))){
			//이벤트가 TID를 this 혹은 슬래이브ID로만 온경우는 전체 전파용도로 생각하면 될 것이다. 
			slaveProcess(inboundCtx, outboundCtx);
		}else{
			//TID가 특별하게 지정되었기에 해당 장치에게 보내주어야 한다.
			eventToAim(inboundCtx);
			outboundCtx.dispose();
			
			if(ism.containsDeviceId(tid)){
				eventToAim(inboundCtx);
				outboundCtx.dispose();
			}else{
				eventToMaster(inboundCtx, outboundCtx);
			}
		}
	}
	
	private void slaveProcess(IContext inboundCtx, OutboundContext outboundCtx) throws Exception{
//		List<String> pathList = inboundCtx.getPaths();
//		IElementHandler currHandler = this.getRootHandler();			
//		int type;
//		for(String path: pathList){
//			type = currHandler.type();
//			if( type==IElementHandler.ROOT || ((type & IElementHandler.DIRECTORY)==IElementHandler.DIRECTORY) ){
//				currHandler = ((IDirectoryHandler) currHandler).getHandler(path);
//				if(currHandler!=null) continue;
//			}
//			throw service.getExceptionfactory().createSysException("200", new String[]{inboundCtx.getFullPath()});
////			throw new Exception("핸들러가 없습니다.(path:"+inboundCtx.getFullPath()+")");
//		}
//		if( (currHandler.type() & IElementHandler.FUNCTION)==IElementHandler.FUNCTION){
//			((IFunctionHandler) currHandler).process(inboundCtx, outboundCtx);
//		}else{
//			//경로는 일치했으나 처리할 function이 구현되어있지 않는 케이스.
//			throw service.getExceptionfactory().createSysException("203", new String[]{inboundCtx.getFullPath()});
////			throw new Exception("핸들러 경로는 일치하나 처리할 Function이 구현되어 있지 않습니다.(path:"+inboundCtx.getFullPath()+")");
//		}
		IElementHandler currHandler = this.getRootHandler().findHandler(inboundCtx.getPaths());
		if ((currHandler.type() & IElementHandler.FUNCTION) == IElementHandler.FUNCTION) {
			((IFunctionHandler) currHandler).process(inboundCtx, outboundCtx);
		} else {
			// 경로는 일치했으나 처리할 function이 구현되어있지 않는 케이스.
			throw service.getExceptionfactory().createSysException("203", new String[]{inboundCtx.getFullPath()});
		}
	}
	
	private void requestToMaster(IContext inboundCtx, OutboundContext outboundCtx) throws Exception{
		outboundCtx.setSID(sss.getSlaveServerSessionId());
		outboundCtx.setSPort(sss.offerContextTracer(inboundCtx, new IContextCallback(){

			@Override
			public void responseSuccess(IContextTracer ctxTracer) {
				//내부 요청에 대한 응답으로 부모 contextTracer를 자동으로 호출해주므로 처리할 로직은 특별히 없다.
				//마스터 처럼 다른 슬래이브 서버로 전달할 로직도 없다. 
				log.info("마스터 서버로 부터 응답이 왔음.(path:"+inboundCtx.getFullPath()+")");
			}

			@Override
			public void responseFail(IContextTracer ctxTracer) {
				log.warn("마스터 서버로 부터 응답이 없음.(path:"+inboundCtx.getFullPath()+")");
			}
			
		}).getSeq());	
	}
	
	private void eventToMaster(IContext inboundCtx, OutboundContext outboundCtx) throws Exception{
		outboundCtx.setSID(sss.getSlaveServerSessionId());
	}
	
	private void requestToAim(IContext inboundCtx) throws Exception{
		aim.handOverContext(inboundCtx, new IContextCallback(){

			@Override
			public void responseSuccess(IContextTracer ctxTracer) {
				IContext inboundCtx = ctxTracer.getRequestContext();
				IContext outterCtx = ctxTracer.getResponseContext();
				log.info("장치로 부터 응답이 왔음.(path:"+inboundCtx.getFullPath()+", DID:"+inboundCtx.getTID()+")");
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
				log.warn("장치로 부터 응답이 없음.(path:"+inboundCtx.getFullPath()+", DID:"+inboundCtx.getTID()+")");
				OutboundContext outboundCtx = new OutboundContext();
				outboundCtx.setSID(inboundCtx.getSID());
				outboundCtx.setSPort(inboundCtx.getSPort());
				outboundCtx.setTID(inboundCtx.getTID());
				outboundCtx.setTPort(inboundCtx.getTPort());
				outboundCtx.setPaths(new ArrayList<String>(inboundCtx.getPaths()));
				outboundCtx.getPaths().add("nack");
				outboundCtx.setParams(new HashMap<String, String>());
				IMsgMastObj obj = service.getExceptionfactory().getMsgInfo("601");
				outboundCtx.getParams().put("code", obj.getOuterCode());
				outboundCtx.getParams().put("type", obj.getType());
				outboundCtx.getParams().put("msg", obj.getMsg());
				outboundCtx.setTransmission("res");
				//특정시간동안 응답이 오지 않아서 발생하는 실패. 
				try {
					transfer(outboundCtx);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		});
	}
	
	private void responseToMaster(IContext inboundCtx) throws Exception{
		sss.pollAndCallContextTracer(inboundCtx.getSPort(), inboundCtx);
	}

	private void eventToAim(IContext inboundCtx) throws Exception{
		requestToAim(inboundCtx);
	}
}
