package com.hdbsnc.smartiot.service.slave.impl.process.handler2;

import java.util.Map;

import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractFunctionHandler;
import com.hdbsnc.smartiot.service.slave.impl.Sss2;

public class ConnectNackHandler extends AbstractFunctionHandler{

	public static final String KEY_CODE = "code";
	public static final String KEY_TYPE = "type";
	public static final String KEY_MSG = "msg";
	
	private Sss2 sss;
	public ConnectNackHandler(Sss2 sss) {
		super("nack");
		this.sss = sss;
	}

	@Override
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		// 마스터서버에서 nack로 오면 파라미터로, code, type, msg를 보내준다.
		Map<String, String> params = inboundCtx.getParams();
		String code = params.get(KEY_CODE);
		String type = params.get(KEY_TYPE);
		String msg = params.get(KEY_MSG);
		
		this.getCommonService().getLogger().warn(inboundCtx.getFullPath()+" code="+code+", type="+type+", msg="+msg);
		
		sss.changeState(Sss2.STATE_UNACTIVATE);
		
		outboundCtx.dispose();
	}

}
