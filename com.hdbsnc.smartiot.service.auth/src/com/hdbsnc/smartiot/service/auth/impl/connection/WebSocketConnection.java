package com.hdbsnc.smartiot.service.auth.impl.connection;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.eclipse.jetty.websocket.api.Session;

import com.hdbsnc.smartiot.common.connection.IConnection;

public class WebSocketConnection implements IConnection {

	private long createdTime;
	private long lastAccessedTime;
	private Session session;
	
	public WebSocketConnection(Session session){
		this.createdTime = System.currentTimeMillis();
		this.lastAccessedTime = this.createdTime;
		this.session = session;
	}
	
	public Session getSession(){
		return this.session;
	}
	
	public void changeSession(Session session){
		this.session = session;
	}
	
	@Override
	public String getRemoteAddress() {
		if(session==null || !session.isOpen()) return "none";
		return session.getRemoteAddress().getAddress().toString();
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
		if(session==null) return false;
		return this.session.isOpen();
	}

	@Override
	public void disconnect() throws IOException {
		if(this.session!=null) this.session.disconnect();
	}

	@Override
	public void write(byte[] bytes) throws IOException {
		if(session==null) return;
		this.session.getRemote().sendBytes(ByteBuffer.wrap(bytes));
		this.lastAccessedTime = System.currentTimeMillis();
	}

	@Override
	public void write(String packets) throws IOException {
		if(session==null) return;
		this.session.getRemote().sendString(packets);
		this.lastAccessedTime = System.currentTimeMillis();
	}

	@Override
	public byte[] read() throws IOException {
		throw new UnsupportedOperationException("WebSocket은 지원하지 않음.");
	}

}
