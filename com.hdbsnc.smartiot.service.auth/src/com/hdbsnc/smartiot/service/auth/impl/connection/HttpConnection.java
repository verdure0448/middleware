package com.hdbsnc.smartiot.service.auth.impl.connection;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.AsyncContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hdbsnc.smartiot.common.connection.IConnection;

public class HttpConnection implements IConnection{
	private final AsyncContext asyncContext;
	private long createdTime = 0;
	private long lastAccessedTime = 0;
	
	public HttpConnection(final AsyncContext asyncContext){
		this.createdTime = System.currentTimeMillis();
		this.lastAccessedTime = this.createdTime;
		this.asyncContext = asyncContext;
	}
	
	@Override
	public String getRemoteAddress() {
		return this.asyncContext.getRequest().getLocalAddr();
	}

	@Override
	public long getCreationTime() {
		return this.createdTime;
	}

	@Override
	public long getLastAccessedTime() {
		return this.lastAccessedTime;
	}

	@Override
	public boolean isConnected() {
		try {
			return this.asyncContext.getResponse().getOutputStream().isReady();
		} catch (IOException e) {
			return false;
		}
	}

	@Override
	public void disconnect() throws IOException {
		this.asyncContext.getResponse().getOutputStream().close();
	}

	@Override
	public void write(byte[] bytes) throws IOException {
		ServletResponse response = this.asyncContext.getResponse();
		response.setContentType("smartiot/otp");
		ServletOutputStream out = response.getOutputStream();
		out.write(bytes);
		this.asyncContext.complete();
	}

	@Override
	public void write(String packets) throws IOException {
		ServletResponse response = this.asyncContext.getResponse();
		response.setContentType("smartiot/otp");
		PrintWriter out = response.getWriter();
		out.print(packets);
		this.asyncContext.complete();
	}

	@Override
	public byte[] read() throws IOException {
		ServletRequest request = asyncContext.getRequest();
		int contentLength = request.getContentLength();
		if(contentLength<0) return null;
		byte[] tempBuffer = new byte[contentLength];
		request.getInputStream().read(tempBuffer);
		return tempBuffer;
	}

}
