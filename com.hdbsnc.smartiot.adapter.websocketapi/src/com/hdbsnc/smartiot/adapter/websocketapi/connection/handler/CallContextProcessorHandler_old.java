package com.hdbsnc.smartiot.adapter.websocketapi.connection.handler;

import com.hdbsnc.smartiot.adapter.websocketapi.connection.OutterContext;
import com.hdbsnc.smartiot.adapter.websocketapi.connection.impl.AbstractConnectionHandler;
import com.hdbsnc.smartiot.adapter.websocketapi.connection.impl.ConnectionHandleChain_old.Handle;
import com.hdbsnc.smartiot.common.context.IContextProcessor;
import com.hdbsnc.smartiot.common.otp.impl.Otp;


public class CallContextProcessorHandler_old extends AbstractConnectionHandler{

	private IContextProcessor cp;
	
	public CallContextProcessorHandler_old(IContextProcessor cp){
		super("CallContextProcessor");
		this.cp = cp;
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
		}
		
	}

	@Override
	public void fail(Handle msg) {
		// TODO Auto-generated method stub
		
	}

}
