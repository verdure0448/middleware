package com.hdbsnc.smartiot.common.context.impl;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

import com.hdbsnc.smartiot.common.context.IContext;

public class InnerContext implements IContext{

	private String sid;
	private String sPort;
	private String tid;
	private String tPort;
	private List<String> paths;
	private Map<String, String> params;
	private String contentType;
	private ByteBuffer content;
	private String transmission;
	
	public InnerContext(){
		sPort = null;
		tPort = null;
		paths = null;
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
	public InnerContext(IContext ctx){
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
	public String getSid() {
		return sid;
	}
	public void setSid(String sid) {
		this.sid = sid;
	}
	public String getsPort() {
		return sPort;
	}
	public void setsPort(String sPort) {
		this.sPort = sPort;
	}
	public String getTid() {
		return tid;
	}
	public void setTid(String tid) {
		this.tid = tid;
	}
	public String gettPort() {
		return tPort;
	}
	public void settPort(String tPort) {
		this.tPort = tPort;
	}
	public void setPaths(List<String> paths) {
		this.paths = paths;
	}
	public void setParams(Map<String, String> params) {
		this.params = params;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public void setContent(ByteBuffer content) {
		this.content = content;
	}
	public void setTransmission(String transmission) {
		this.transmission = transmission;
	}
}
