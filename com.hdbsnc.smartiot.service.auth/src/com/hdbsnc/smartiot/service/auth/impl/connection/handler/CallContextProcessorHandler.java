package com.hdbsnc.smartiot.service.auth.impl.connection.handler;

import com.hdbsnc.smartiot.common.context.IContextProcessor;
import com.hdbsnc.smartiot.common.otp.impl.Otp;
import com.hdbsnc.smartiot.service.auth.connection.impl.AbstractConnectionHandler;
import com.hdbsnc.smartiot.service.auth.connection.impl.ConnectionHandleChain.Handle;
import com.hdbsnc.smartiot.service.auth.impl.connection.OutterContext;
import com.hdbsnc.smartiot.util.logger.Log;

public class CallContextProcessorHandler extends AbstractConnectionHandler{

	private IContextProcessor cp;
	private Log log;
	
	public CallContextProcessorHandler(IContextProcessor cp, Log log){
		super("CallContextProcessor");
		this.cp = cp;
		this.log = log;
	}
	
	@Override
	public boolean resolve(Handle msg) {
		Otp otp = msg.getOtp();
		if(otp==null) return false;
		return true;
	}

	@Override
	public void success(Handle msg) {
		OutterContext ctx = new OutterContext(msg.getOtp(), msg.getConnection());
		try {
			//현재 쓰레드에서 context 수행. 
			cp.process(ctx);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.err(e);
		}
		
	}

	@Override
	public void fail(Handle msg) {
		// TODO Auto-generated method stub
		
	}

}
