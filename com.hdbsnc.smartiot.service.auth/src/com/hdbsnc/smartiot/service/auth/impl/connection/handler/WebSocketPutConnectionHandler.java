package com.hdbsnc.smartiot.service.auth.impl.connection.handler;

import com.hdbsnc.smartiot.common.connection.IConnectionManager;
import com.hdbsnc.smartiot.common.otp.impl.Otp;
import com.hdbsnc.smartiot.service.auth.connection.impl.AbstractConnectionHandler;
import com.hdbsnc.smartiot.service.auth.connection.impl.ConnectionHandleChain.Handle;

public class WebSocketPutConnectionHandler extends AbstractConnectionHandler{

	private IConnectionManager cm;
	
	public WebSocketPutConnectionHandler(IConnectionManager cm){
		super("ConnectionManager");
		//CM에 connection을 넣지 못하면 뒤로 가지 못한다.
		this.setFailToNext(false);
		this.cm = cm;
	}
	
	@Override
	public boolean resolve(Handle msg) {
		Otp otp = msg.getOtp();
		if(otp==null) return false;
		return true;
	}

	@Override
	public void success(Handle msg) {
		Otp otp = msg.getOtp();
		String sid = otp.getHeader().getSID();//장치 식별자 임. 
		cm.putConnection(sid, msg.getConnection());
	}

	@Override
	public void fail(Handle msg) {
		// TODO Auto-generated method stub
		
	}

}
