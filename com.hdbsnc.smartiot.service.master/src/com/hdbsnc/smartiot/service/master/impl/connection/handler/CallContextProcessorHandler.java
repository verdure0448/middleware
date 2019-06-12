package com.hdbsnc.smartiot.service.master.impl.connection.handler;

import java.util.List;

import com.hdbsnc.smartiot.common.context.IContextProcessor;
import com.hdbsnc.smartiot.common.otp.impl.Otp;
import com.hdbsnc.smartiot.service.master.connection.impl.AbstractConnectionHandler;
import com.hdbsnc.smartiot.service.master.connection.impl.ConnectionHandleChain.Handle;
import com.hdbsnc.smartiot.service.master.impl.connection.OutterContext;

public class CallContextProcessorHandler extends AbstractConnectionHandler{

	private IContextProcessor cp;
	
	public CallContextProcessorHandler(IContextProcessor cp){
		super("CallContextProcessor");
		this.cp = cp;
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
				cp.process(oc);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}else if(obj instanceof List){
			List<Otp> list = (List<Otp>) obj;
			for(Otp otp: list){
				oc = new OutterContext(otp, msg.getConnection());
				try {
					cp.process(oc);
				} catch (Exception e) {
					e.printStackTrace();
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
