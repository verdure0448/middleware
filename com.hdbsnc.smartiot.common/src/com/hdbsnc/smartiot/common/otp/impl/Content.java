package com.hdbsnc.smartiot.common.otp.impl;

import java.nio.ByteBuffer;

import com.hdbsnc.smartiot.common.otp.IContent;

public class Content implements IContent{

	private String contentType;
	private ByteBuffer content;
	
	public Content(String contentType, ByteBuffer content){
		this.contentType = contentType;
		this.content = content;
	}
	
	public Content(String contentType, byte[] content){
		this.contentType = contentType;
		this.content = ByteBuffer.wrap(content);
	}
	
	@Override
	public String getContentType() {
		return this.contentType;
	}

	@Override
	public ByteBuffer getContent() {	
		return this.content;
	}

}
