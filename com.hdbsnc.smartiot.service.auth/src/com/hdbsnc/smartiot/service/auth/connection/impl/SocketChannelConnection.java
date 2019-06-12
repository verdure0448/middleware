package com.hdbsnc.smartiot.service.auth.connection.impl;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import com.hdbsnc.smartiot.common.connection.IConnection;


public class SocketChannelConnection implements IConnection{

	private SocketChannel channel;
	private long createdTime;
	private long lastAccessedTime;
	private ByteBuffer readBuffer;
	private SelectionKey key;
	private ByteArrayOutputStream bos;
	
	
	public SocketChannelConnection(SelectionKey key, int ioReadBuffer){
		this.createdTime = System.currentTimeMillis();
		this.channel = (SocketChannel) key.channel();
		this.key = key;
		this.readBuffer = ByteBuffer.allocate(ioReadBuffer);
		this.bos = new ByteArrayOutputStream();
	}

	

	public long getCreationTime() {
		return this.createdTime;
	}

	public long getLastAccessedTime() {
		return this.lastAccessedTime;
	}

	public boolean isConnected() {
		if(this.channel.isConnected() && this.channel.socket().isConnected()==true) {
			return true;
		}else{
			return false;
		}
	}

	public void disconnect() throws IOException {
		if(channel!=null) {
			channel.close();
			if(channel.socket()!=null) channel.socket().close();
		}
	}
	
	public synchronized byte[] read() throws IOException {
		bos.reset();
		while(channel.read(readBuffer)>0){
			readBuffer.flip();
			if (readBuffer.order().equals(ByteOrder.LITTLE_ENDIAN)) {
				readBuffer.order(ByteOrder.BIG_ENDIAN);
			}
			bos.write(readBuffer.array(), readBuffer.position(), readBuffer.limit());
			readBuffer.clear();
		}
		bos.flush();
		lastAccessedTime = System.currentTimeMillis();
		return bos.toByteArray();
	}
	
	public Socket socket(){
		return channel.socket();
	}
	
	public BufferedReader bufferedReader() throws IOException{
		return new BufferedReader(new InputStreamReader(channel.socket().getInputStream()));
	}
	
	public InputStream readAndReturnInputStream() throws IOException {
		synchronized(readBuffer){
			byte[] results = read();
			if(results!=null){
				return new ByteArrayInputStream(results);
			}else{
				return null;
			}
		}
	}
	
	public BufferedReader readAndReturnBufferedReader() throws IOException {
		InputStream is = readAndReturnInputStream();
		if(is == null) return null;
		return new BufferedReader(new InputStreamReader(is));	
	}
	
	public void write(byte[] bytes) throws IOException {
		if(bytes==null)return;
		ByteBuffer tempBuffer = ByteBuffer.wrap(bytes);
		while(tempBuffer.hasRemaining()){
			channel.write(tempBuffer);
		}
		lastAccessedTime = System.currentTimeMillis();
	}
	
	public void interestOps(int selectionKeyOps){
		if(key.isValid()){
			key.interestOps(selectionKeyOps);
		}
	}

	public String getRemoteAddress() {
		if(channel.isConnected()){
			return channel.socket().getInetAddress().toString();
		}else{
			return "none";
		}
	}



	@Override
	public void write(String packets) throws IOException {
		if(packets==null)return;
		ByteBuffer tempBuffer = ByteBuffer.wrap(packets.getBytes());
		while(tempBuffer.hasRemaining()){
			channel.write(tempBuffer);
		}
		lastAccessedTime = System.currentTimeMillis();
		
	}
}
