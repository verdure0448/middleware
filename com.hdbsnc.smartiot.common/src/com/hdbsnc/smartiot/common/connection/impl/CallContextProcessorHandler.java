package com.hdbsnc.smartiot.common.connection.impl;

import java.util.List;


import com.hdbsnc.smartiot.common.connection.impl.AbstractConnectionHandler;
import com.hdbsnc.smartiot.common.connection.impl.ConnectionHandleChain.Handle;
import com.hdbsnc.smartiot.common.context.IContextProcessor;
import com.hdbsnc.smartiot.common.otp.impl.Otp;
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
		Object obj = msg.getMsg();
		Otp tempOtp;
		OutterContext oc;
		if(obj instanceof Otp){
			tempOtp = (Otp) obj;
			oc = new OutterContext(tempOtp, msg.getConnection());
			try {
				cp.process(oc);//현재 쓰레드에서 순차적으로 처리된다. 
			} catch (Exception e) {
				log.err(e);
			}
			return true;
		}else if(obj instanceof List){
			List<Otp> list = (List<Otp>) obj;
			for(Otp otp: list){
				oc = new OutterContext(otp, msg.getConnection());
				try {
					cp.process(oc);//현재 쓰레드에서 순차적으로 처리된다.
				} catch (Exception e) {
					log.err(e);
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public void success(Handle msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fail(Handle msg) {
		// TODO Auto-generated method stub
		
	}

}
