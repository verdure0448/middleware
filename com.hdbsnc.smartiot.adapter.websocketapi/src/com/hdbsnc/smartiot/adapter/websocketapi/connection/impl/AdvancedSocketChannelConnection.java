package com.hdbsnc.smartiot.adapter.websocketapi.connection.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import com.hdbsnc.smartiot.common.connection.IConnection;
import com.hdbsnc.smartiot.util.logger.Log;
import com.hdbsnc.smartiot.util.queue.BlockingQueue;
import com.hdbsnc.smartiot.util.servicepool.AlreadyClosedException;
import com.hdbsnc.smartiot.util.servicepool.ServicePool;


public class AdvancedSocketChannelConnection implements IConnection{

	public static final byte CARIAGE_RETURN_BYTE = 0x0D;
	public static final byte LINE_FEED_BYTE = 0x0A;
	
	private SocketChannel channel;
	private long createdTime;
	private long lastAccessedTime;
	private ByteBuffer readBuffer;
	private SelectionKey key;
	private BlockingQueue queue;
	private ServicePool pool;
	private Log log;
	private ConnectionHandleChain_old chc;
	private ByteArrayOutputStream bufferOs;
	private boolean isUseQueue = false;
	private Object lock = new Object();
	
	
	public AdvancedSocketChannelConnection(SelectionKey key, int ioReadBuffer, ConnectionHandleChain_old chc, ServicePool pool, Log logger){
		this.createdTime = System.currentTimeMillis();
		this.channel = (SocketChannel) key.channel();
		this.key = key;
		this.readBuffer = ByteBuffer.allocateDirect(ioReadBuffer);
		this.bufferOs = new ByteArrayOutputStream();
		this.queue = new BlockingQueue();
		this.pool = pool;
		this.log = logger;
		this.chc = chc;
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
		bufferOs.reset();
		while(channel.read(readBuffer)>0){
			readBuffer.flip();
			if (readBuffer.order().equals(ByteOrder.LITTLE_ENDIAN)) {
				readBuffer.order(ByteOrder.BIG_ENDIAN);
			}
			bufferOs.write(readBuffer.array(), readBuffer.position(), readBuffer.limit());
			readBuffer.clear();
		}
		bufferOs.flush();
		lastAccessedTime = System.currentTimeMillis();
		return bufferOs.toByteArray();
	}
	
	public synchronized int gatlingRead() throws IOException, AlreadyClosedException {
		int ioValue = channel.read(readBuffer);
		if(ioValue>0){
			readBuffer.flip();
			queue.enqueue(ByteBuffer.allocate(ioValue).put(readBuffer));
			readBuffer.clear();
			synchronized(lock){
				if(!isUseQueue){
					isUseQueue = true;
					pool.execute(new PacketMerger());
				}
			}
		}
		lastAccessedTime = System.currentTimeMillis();
		return ioValue;
	}
	
	private class PacketMerger implements Runnable{
		@Override
		public void run() {
			ByteBuffer packet;
			long counter = 0;
			try{
				do{
					packet = (ByteBuffer) queue.dequeue();
					packetMergeAndCall(packet);
					log.debug("PacketMerger count: "+ (++counter));
					synchronized(lock){
						if(queue.isEmpty()) isUseQueue = false;
					}
				}while(isUseQueue);
			} catch (IOException e) {
				e.printStackTrace();
				isUseQueue = false;
			}
			log.debug("PacketMerger finished.");
		}	
	}
	
	private static int checkBufferByStartIndex(ByteBuffer packet, int startIndex){
		byte currByte;
		boolean isCariageReturn = false, isLineFeed = false;
		packet.position(startIndex);
		while(packet.hasRemaining()){
			currByte = packet.get();
			if(isCariageReturn){
				if(currByte==LINE_FEED_BYTE){
					isLineFeed = true;
					break;
				}else{
					isCariageReturn = false;
					isLineFeed = false;
				}
			}else{
				if(currByte==CARIAGE_RETURN_BYTE){
					isCariageReturn = true;
					isLineFeed = false;
				}else{
					isCariageReturn = false;
					isLineFeed = false;
				}
			}
		}
		if(isCariageReturn && isLineFeed) return packet.position();
		return -1;
	}
	
	private void packetMergeAndCall(ByteBuffer packet) throws IOException{
		
		ByteBuffer checkBuffer;
		int oldBufferSize, packetPosition, checkPosition;
		byte[] resultArrays = null;
		byte[] remainArrays;
		oldBufferSize = bufferOs.size();
		if(oldBufferSize>0){
			bufferOs.write(packet.array());
			checkBuffer = ByteBuffer.wrap(bufferOs.toByteArray());
			checkPosition = oldBufferSize-1;
		}else{ //bufferOs에 아무것도 없을때.
			checkBuffer = packet;
			checkPosition = 0;
		}
		int startPos = 0;
		while((packetPosition = checkBufferByStartIndex(checkBuffer, checkPosition))>=0){
			resultArrays = new byte[packetPosition-startPos-2]; // \r\n 2개 바이트는 제외하고 읽는다.
			checkBuffer.position(startPos);
			checkBuffer.get(resultArrays);
			checkPosition=packetPosition;
			startPos = packetPosition;
			chc.currentThreadHandle(this, resultArrays);
		}
		if(startPos!=0){
			remainArrays = new byte[checkBuffer.limit()-checkPosition];
			checkBuffer.position(checkPosition);
			checkBuffer.get(remainArrays);
			bufferOs.reset();
			bufferOs.write(remainArrays);
		}else if(checkPosition==0 && packetPosition==-1){
			bufferOs.write(checkBuffer.array());
		}
	}
	
	public Socket socket(){
		return channel.socket();
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
