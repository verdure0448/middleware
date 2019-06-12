package com.hdbsnc.smartiot.service.master.impl;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

import com.hdbsnc.smartiot.common.context.IContext;

public class InnerContext implements IContext{

	String sid;
	String sPort;
	String tid;
	String tPort;
	List<String> paths;
	Map<String, String> params;
	String contentType;
	ByteBuffer content;
	String transmission;
	
	InnerContext(){
		sPort = null;
		tPort = null;
		params = null;
		contentType = null;
		content = null;
		transmission = null;
	}
	/**
	 * 이미 생성된 IContext객체를 복제하는 생성자.
	 * 
	 * @param ctx
	 */
	InnerContext(IContext ctx){
		sid = ctx.getSID();
		sPort = ctx.getSPort();
		tid = ctx.getTID();
		tPort = ctx.getTPort();
		paths = ctx.getPaths();
		params = ctx.getParams();
		contentType = ctx.getContentType();
		content = ctx.getContent();
		transmission = ctx.getTransmission();
	}
	
	@Override
	public String getSID() {
		return sid;
	}

	@Override
	public String getSPort() {
		return sPort;
	}

	@Override
	public String getTID() {
		return tid;
	}

	@Override
	public String getTPort() {
		return tPort;
	}

	@Override
	public List<String> getPaths() {
		return paths;
	}

	@Override
	public String getFullPath() {
		StringBuilder sb = new StringBuilder();
		for(int i=0, s=paths.size();i<s;i++){
			if(i!=0) sb.append("/");
			sb.append(paths.get(i));
		}
		return sb.toString();
	}

	@Override
	public Map<String, String> getParams() {
		return params;
	}

	@Override
	public String getContentType() {
		return contentType;
	}

	@Override
	public ByteBuffer getContent() {
		return content;
	}

	@Override
	public boolean containsContent() {
		if(content==null) return false;
		return true;
	}
	@Override
	public String getTransmission() {
		return this.transmission;
	}

}
