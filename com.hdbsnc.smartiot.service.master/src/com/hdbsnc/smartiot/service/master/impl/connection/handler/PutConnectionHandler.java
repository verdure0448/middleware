package com.hdbsnc.smartiot.service.master.impl.connection.handler;

import java.util.List;

import com.hdbsnc.smartiot.common.connection.IConnectionManager;
import com.hdbsnc.smartiot.common.otp.impl.Otp;
import com.hdbsnc.smartiot.service.master.connection.impl.AbstractConnectionHandler;
import com.hdbsnc.smartiot.service.master.connection.impl.ConnectionHandleChain.Handle;

public class PutConnectionHandler extends AbstractConnectionHandler{

	private IConnectionManager cm;
	
	public PutConnectionHandler(IConnectionManager cm){
		super("ConnectionManager");
		//CM에 connection을 넣지 못하면 뒤로 가지 못한다.
		this.setFailToNext(false);
		this.cm = cm;
	}
	
	@Override
	public boolean resolve(Handle msg) {
		Object obj = msg.getMsg();
		if(obj instanceof Otp){
			return true;
		}else if( obj instanceof List<?>){
			List otpList = (List) obj;
			if(otpList.size()>0) return true;
		}
		return false;
	}

	@Override
	public void success(Handle msg) {
		Object obj = msg.getMsg();
		Otp tempOtp = null;
		String sid;
		if(obj instanceof Otp){
			tempOtp = (Otp) obj;
			
		}else if( obj instanceof List<?>){
			List<Otp> otpList = (List<Otp>) obj;
			tempOtp = otpList.get(0);
		}
		sid = tempOtp.getHeader().getSID();
		cm.putConnection(sid, msg.getConnection());
	}

	@Override
	public void fail(Handle msg) {
		// TODO Auto-generated method stub
		
	}

}
