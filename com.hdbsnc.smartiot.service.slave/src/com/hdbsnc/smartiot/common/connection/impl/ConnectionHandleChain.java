package com.hdbsnc.smartiot.common.connection.impl;

import com.hdbsnc.smartiot.common.connection.IConnection;
import com.hdbsnc.smartiot.util.servicepool.AlreadyClosedException;
import com.hdbsnc.smartiot.util.servicepool.ServicePool;

public class ConnectionHandleChain {

	private ServicePool pool;
	private AbstractConnectionHandler firstHandler;
	
	public ConnectionHandleChain(ServicePool pool){
		this.pool = pool;
	}
	
	public void setHandlerChain(AbstractConnectionHandler handler){
		this.firstHandler = handler;
	}
	
	public void handle(IConnection con, byte[] packet){
		
		try {
			this.pool.execute(new Worker(con, packet));
		} catch (AlreadyClosedException e) {
			e.printStackTrace();
		}
	}
	
	public void currentThreadHandle(IConnection con, byte[] packet){
		new Worker(con, packet).run();
	}
	
	private class Worker implements Runnable{
		private IConnection con;
		private byte[] packet;
		
		private Worker(IConnection con, byte[] packet1){
			this.con = con;
			this.packet = packet1;
		}
		
		@Override
		public void run() {
			firstHandler.process(new Handle(con, packet));
		}
		
	}
	
	public class Handle {
		public Object msg = null;
		private IConnection con;
		private byte[] packet;
		
		private Handle(IConnection con, byte[] packet1){
			this.con = con;
			this.packet = packet1;
		}
		
		public IConnection getConnection(){
			return this.con;
		}
		
		public void setMsg(Object msg){
			this.msg = msg;
		}
		
		public Object getMsg(){
			return msg;
		}
		
		public byte[] getPacket(){
			return this.packet;
		}
		
	}	
}
