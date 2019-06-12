package com.hdbsnc.smartiot.adapter.websocketapi.processor.handler;

import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractFunctionHandler;
import com.hdbsnc.smartiot.common.ism.sm.ISessionManager;

/**
 * auth/close
 * 
 * @author KANG
 *
 */
public class AuthCloseHandler extends AbstractFunctionHandler{
	
	public AuthCloseHandler(){
		super("close");
	}

//	@Override
//	public void process(IContext inboundCtx) throws Exception {
//		
//		sm.disposeSession(inboundCtx.getSID());
//		
//		IConnection con = cm.getConnectionBySessionId(inboundCtx.getSID());
//		Url resUrl = Url.createOtp();
//		resUrl.setPaths(currentPaths()).addPath(ProtocolConst.ACK);
//		resUrl.setUserInfo(inboundCtx.getSID(), inboundCtx.getSPort());
//		resUrl.setHostInfo(ProtocolConst.THIS, inboundCtx.getTPort());
//		resUrl.addFrag(ProtocolConst.TRANS, ProtocolConst.TRANS_RES);
//		con.write(parser.parse(resUrl));
//	}

	@Override
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		getSessionManager().disposeSession(inboundCtx.getSID());
//		
//		outboundCtx.getPaths().add("ack");
//		outboundCtx.setSID(inboundCtx.getSID());
//		outboundCtx.setSPort(inboundCtx.getSPort());
//		outboundCtx.setTID("this");
//		outboundCtx.setTPort(inboundCtx.getTPort());
//		outboundCtx.setTransmission("res");
		getCommonService().getLogger().info("세션[" + inboundCtx.getSID() + "]을 종료했습니다.");
		outboundCtx.dispose();
	}
}
