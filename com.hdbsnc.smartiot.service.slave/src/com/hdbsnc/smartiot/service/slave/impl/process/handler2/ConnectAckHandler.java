package com.hdbsnc.smartiot.service.slave.impl.process.handler2;

import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractFunctionHandler;
import com.hdbsnc.smartiot.service.slave.impl.Sss2;

public class ConnectAckHandler extends AbstractFunctionHandler{
	
	private Sss2 sss;
	
	public ConnectAckHandler(Sss2 sss){
		super("ack");
		this.sss = sss;
	}

	@Override
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		//PutConnectionHandler에서 이미 세션키로 컨넥션을 관리하고 있으므로, 여기서 따로 처리할 루틴 없음.
		//슬래이브 서비스가 활성화 된 상태이다. 이제 부터 서버와 정상적인 세션 통신이 가능한 상태.
		this.getCommonService().getLogger().info("마스터 서버와 연결 완료. 세션키: "+inboundCtx.getSID());
		this.sss.setSessionId(inboundCtx.getSID());
		this.sss.changeState(Sss2.STATE_ACTIVATE);
		outboundCtx.dispose();
	}

}
