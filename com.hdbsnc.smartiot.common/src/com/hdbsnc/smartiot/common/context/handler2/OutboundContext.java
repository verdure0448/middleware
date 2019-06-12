package com.hdbsnc.smartiot.common.context.handler2;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hdbsnc.smartiot.common.context.IContext;

public class OutboundContext implements IContext{

	private boolean isDisposed = false;
	private String sid;
	private String sSeq;
	private String tid;
	private String tSeq;
	private List<String> paths;
	private Map<String, String> params;
	private String contentType;
	private ByteBuffer content;
	private String transmission;
	
	public OutboundContext(){
		this.sid = null;
		this.sSeq = null;
		this.tid = null;
		this.tSeq = null;
		this.paths = null;
		this.params = null;
		this.contentType = null;
		this.content = null;
		this.transmission = null;
	}
	
	public OutboundContext(IContext inboundCtx){
		if(inboundCtx!=null){
			this.sid = inboundCtx.getSID();
			this.sSeq = inboundCtx.getSPort();
			this.tid = inboundCtx.getTID();
			this.tSeq = inboundCtx.getTPort();
			if(inboundCtx.getPaths()==null) {
				this.paths = new ArrayList<String>();
			}else{
				this.paths = new ArrayList<String>(inboundCtx.getPaths());
			}
			if(inboundCtx.getParams()==null) {
				this.params = new HashMap<String, String>();
			}else{
				this.params = new HashMap<String, String>(inboundCtx.getParams());
			}
		}
		
		this.contentType = inboundCtx.getContentType();
		this.content = inboundCtx.getContent();
		this.transmission = null;
		
//		this.contentType = inboundCtx.getContentType();
//		this.content = inboundCtx.getContent();
//		this.transmission = inboundCtx.getTransmission();
//		this.params = new HashMap<String,String>(inboundCtx.getParams());
		
		
//		this.contentType = inboundCtx.getContentType();
//		this.content = inboundCtx.getContent();
//		this.transmission = inboundCtx.getTransmission();
//		this.params = new HashMap<String,String>(inboundCtx.getParams());
		
	}
	
	
	@Override
	public String getSID() {
		return this.sid;
	}
	
	public void setSID(String sid){
		this.sid = sid;
	}

	@Override
	public String getSPort() {
		return this.sSeq;
	}
	
	public void setSPort(String sSeq){
		this.sSeq = sSeq;
	}

	@Override
	public String getTID() {
		return this.tid;
	}
	
	public void setTID(String tid){
		this.tid = tid;
	}

	@Override
	public String getTPort() {
		return this.tSeq;
	}
	
	public void setTPort(String tSeq){
		this.tSeq = tSeq;
	}

	@Override
	public List<String> getPaths() {
		return this.paths;
	}
	
	public List<String> addPath(String path){
		if(paths==null) paths = new ArrayList<String>();
		paths.add(path);
		return paths;
	}
	
	public void setPaths(List<String> paths){
		this.paths = paths;
	}

	@Override
	public String getFullPath() {
		if(paths==null) return "";
		StringBuilder sb = new StringBuilder();
		for(int i=0, s=paths.size();i<s;i++){
			if(i!=0) sb.append("/");
			sb.append(paths.get(i));
		}
		return sb.toString();
	}

	@Override
	public Map<String, String> getParams() {
		return this.params;
	}
	
	public Map<String, String> putParam(String key, String value){
		if(params==null) params = new HashMap<String, String>();
		params.put(key, value);
		return params;
	}
	
	public void setParams(Map<String, String> params){
		this.params = params;
	}

	@Override
	public String getContentType() {
		return this.contentType;
	}
	
	public void setContenttype(String contentType){
		this.contentType = contentType;
	}

	@Override
	public ByteBuffer getContent() {
		return this.content;
	}
	
	public void setContent(ByteBuffer content){
		this.content = content;
	}

	@Override
	public boolean containsContent() {
		if(this.content==null) return false;
		return true;
	}

	@Override
	public String getTransmission() {
		return this.transmission;
	}
	
	public void setTransmission(String transmission){
		this.transmission = transmission;
	}
	
	public void dispose(){
		this.isDisposed = true;
		this.content = null;
		this.contentType = null;
		if(this.params!=null) this.params.clear();
		this.params = null;
		if(this.paths!=null) this.paths.clear();
		this.paths = null;
		this.sid = null;
		this.sSeq = null;
		this.tid = null;
		this.tSeq = null;
		this.transmission = null;
	}
	
	public boolean isDisposed(){
		return this.isDisposed;
	}
	
	

	

}
