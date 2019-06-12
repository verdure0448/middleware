package com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.session;

import com.hdbsnc.smartiot.adapter.websocketapi.constant.WebSocketAdapterConst;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractFunctionHandler;
import com.hdbsnc.smartiot.common.ism.IIntegratedSessionManager;
import com.hdbsnc.smartiot.common.ism.sm.ISession;
import com.hdbsnc.smartiot.common.ism.sm.ISessionManager;
import com.hdbsnc.smartiot.common.pm.IProfileManager;
import com.hdbsnc.smartiot.common.pm.vo.IInstanceObj;

/**
 * session/disconnect 세션 종료
 * 
 * @author KANG
 *
 */
public class SessionDisconnectHandler extends AbstractFunctionHandler {
	
	private IProfileManager pm;
	public SessionDisconnectHandler(IProfileManager pm) {
		super("disconnect");
		this.pm = pm;
	}

	@Override
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		String did = inboundCtx.getParams().get(WebSocketAdapterConst.DID);
		
		IIntegratedSessionManager ism = getSessionManager().getIntegratedSessionManager();
		ISession session = ism.getSession(did);
		
		if (session == null)
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":005", new String[]{did});

		IInstanceObj insInfo = pm.getInstanceObj(session.getAdapterInstanceId());
		String insKind = insInfo.getInsKind();
		if(insKind!=null && insKind.equals("client"))
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":010");
		
		ISessionManager didInstanceSm = ism.getSessionManager(session.getAdapterInstanceId());
		didInstanceSm.disposeDeviceId(did);

		outboundCtx.getPaths().add("ack");
		outboundCtx.setSID(inboundCtx.getSID());
		outboundCtx.setSPort(inboundCtx.getSPort());
		outboundCtx.setTID("this");
		outboundCtx.setTPort(inboundCtx.getTPort());
		outboundCtx.setTransmission("res");

	}

}
