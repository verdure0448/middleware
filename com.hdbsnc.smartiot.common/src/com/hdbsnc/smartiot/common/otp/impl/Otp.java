package com.hdbsnc.smartiot.common.otp.impl;

import java.nio.ByteBuffer;

import com.hdbsnc.smartiot.common.otp.IOtp;
import com.hdbsnc.smartiot.common.otp.url.parser.vo.Url;

public class Otp implements IOtp{

	private Header header;
	private Content content;
	
	public Otp(Header header){
		this.header = header;
		this.content = null;
	}
	
	public Otp(Url url){
		this(new Header(url));
	}
	
	@Override
	public Header getHeader() {
		return header;
	}
	
	public void setHeader(Url url){
		this.header = new Header(url);
	}

	@Override
	public Content getContent() {
		return content;
	}
	
	public void setContent(Content content){
		this.content = content;
	}
	
	public void setContent(String contentType, ByteBuffer content){
		this.content = new Content(contentType, content);
	}
	
	public void setContent(String contentType, byte[] content){
		this.content = new Content(contentType, content);
	}
	
	public void setContent(byte[] content){
		this.content = new Content(header.getContentType(), content);
	}
	
	public void setContent(ByteBuffer content){
		this.content = new Content(this.header.getContentType(), content);
	}
	
	public boolean hasContent(){
		return this.header.hasContent();
	}

}
