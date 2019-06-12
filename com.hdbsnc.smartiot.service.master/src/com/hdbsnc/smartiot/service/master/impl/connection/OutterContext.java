package com.hdbsnc.smartiot.service.master.impl.connection;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

import com.hdbsnc.smartiot.common.connection.IConnection;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.otp.impl.Otp;

public class OutterContext implements IContext{

	private Otp inboundOtp;
	private IConnection con;
	
	public OutterContext(Otp otp, IConnection con){
		this.inboundOtp = otp;
		this.con = con;
	}
	
	public IConnection getConnection(){
		return con;
	}
	
	public Otp getInboundOtp(){
		return inboundOtp;
	}
	
	@Override
	public String getSID() {
		return inboundOtp.getHeader().getSID();
	}

	@Override
	public String getSPort() {
		return inboundOtp.getHeader().getSPort();
	}

	@Override
	public String getTID() {
		return inboundOtp.getHeader().getTID();
	}

	@Override
	public String getTPort() {
		return inboundOtp.getHeader().getTPort();
	}

	@Override
	public List<String> getPaths() {
		return inboundOtp.getHeader().getPaths();
	}

	@Override
	public String getFullPath() {
		return inboundOtp.getHeader().getFullPath();
	}

	@Override
	public Map<String, String> getParams() {
		return inboundOtp.getHeader().getParams();
	}

	@Override
	public String getContentType() {
		return inboundOtp.getHeader().getContentType();
	}

	@Override
	public ByteBuffer getContent() {
		if(inboundOtp.getContent()!=null){
			return inboundOtp.getContent().getContent();
		}else{
			return null;
		}
	}

	@Override
	public boolean containsContent() {
		return this.inboundOtp.getHeader().hasContent();
	}

	@Override
	public String getTransmission() {
		return this.inboundOtp.getHeader().getTransmissionType();
	}

}
