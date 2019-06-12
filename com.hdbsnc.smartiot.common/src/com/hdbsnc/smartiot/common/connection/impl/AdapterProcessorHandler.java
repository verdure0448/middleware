package com.hdbsnc.smartiot.common.connection.impl;


import com.hdbsnc.smartiot.common.aim.IAdapterProcessor;
import com.hdbsnc.smartiot.common.connection.impl.ConnectionHandleChain.Handle;
import com.hdbsnc.smartiot.common.otp.impl.Otp;
import com.hdbsnc.smartiot.util.logger.Log;

public class AdapterProcessorHandler extends AbstractConnectionHandler{

	private IAdapterProcessor ap;
	private Log log;
	
	public AdapterProcessorHandler(IAdapterProcessor ap, Log log) {
		super("AdapterProcessor");
		this.ap = ap;
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
			try{
				ap.process(oc);
			}catch(Exception e){
				e.printStackTrace();
				log.err(e);
			}
			return true;
		}
		return false;
	}

	@Override
	public void success(Handle msg) {
		// 성공시 로그를 남기거나 할 것. 
		
	}

	@Override
	public void fail(Handle msg) {
		// 실패시 로그를 남기거나 할 것. 
		
	}

}
