package com.hdbsnc.smartiot.service.slave.impl.process.handler2;

import java.util.Map;

import com.hdbsnc.smartiot.common.ICommonService;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractFunctionHandler;
import com.hdbsnc.smartiot.common.ism.IIntegratedSessionManager;
import com.hdbsnc.smartiot.common.ism.sm.ISession;
import com.hdbsnc.smartiot.common.pm.vo.IMsgMastObj;
import com.hdbsnc.smartiot.util.logger.Log;

public class SlaveAllocateHandler extends AbstractFunctionHandler{

	public static final String KEY_IID = "iid";
	public static final String KEY_DID = "did";
	public static final String KEY_UID = "uid";
	public static final String KEY_SID = "sid";
	
	public static final String KEY_CODE = "code";
	public static final String KEY_TYPE = "type";
	public static final String KEY_MSG	= "msg";
	
	private IIntegratedSessionManager ism;
	
	public SlaveAllocateHandler(IIntegratedSessionManager ism){
		super("alloc");
		this.ism = ism;
	}
	
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception{
		Log log = getCommonService().getLogger();
		String requestSid = inboundCtx.getSID();
		String requestSeq = inboundCtx.getSPort();
		
		Map<String, String> params = inboundCtx.getParams();
		
		String iid = params.get(KEY_IID);
		String did = params.get(KEY_DID);
		String uid = params.get(KEY_UID);
		String sid = params.get(KEY_SID);
		
		if(iid==null || did==null || uid==null || sid==null){
			nack(outboundCtx, "204");
			return;
		}
		log.info("슬래이브 할당요청: "+did+", "+uid+", "+sid);
		
		ISession tempSession = ism.getSession(did);
		if(tempSession!=null){
			log.info(did+" 세션이 존재함. updateSession.");
			ism.updateSession(did, uid, sid);
		}else{
			try{
				ism.outterNewSession(did, uid, sid);
			}catch(Exception e){
				log.err(e);
				//인스턴스가 없거나 디바이스 정보가 없을 경우 발생 
				nack(outboundCtx, "605");
				return;
			}
		}
		outboundCtx.setTID("this");
		outboundCtx.getPaths().add("ack");
		outboundCtx.setTransmission("res");
	}
	
	private void nack(OutboundContext outboundCtx, String code) {
		IMsgMastObj obj = getCommonService().getExceptionfactory().getMsgInfo(code);
		outboundCtx.setTID("this");
		outboundCtx.getPaths().add("nack");
		outboundCtx.getParams().put(KEY_CODE, code);
		outboundCtx.getParams().put(KEY_TYPE, obj.getType());
		outboundCtx.getParams().put(KEY_MSG, obj.getMsg());
		outboundCtx.setTransmission("res");
	}
}
