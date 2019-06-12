package com.hdbsnc.smartiot.common.connection.impl;

import com.hdbsnc.smartiot.common.connection.IConnection;
import com.hdbsnc.smartiot.common.otp.impl.Otp;
import com.hdbsnc.smartiot.util.logger.Log;
import com.hdbsnc.smartiot.util.servicepool.AlreadyClosedException;
import com.hdbsnc.smartiot.util.servicepool.ServicePool;

public class ConnectionHandleChain {

	private ServicePool pool;
	private Log logger;
	private AbstractConnectionHandler firstHandler;
	
	public ConnectionHandleChain(ServicePool pool, Log parentLogger){
		this.pool = pool;
		this.logger = parentLogger;
	}
	
	public AbstractConnectionHandler setRootHandler(AbstractConnectionHandler handler){
		this.firstHandler = handler;
		return this.firstHandler;
	}
	
	public void handle(IConnection con, byte[] packet){
		
		try {
			this.pool.execute(new Worker(con, packet));
		} catch (AlreadyClosedException e) {
			logger.err(e);
		}
	}
	
	public void handle(IConnection con, Otp otp){
		try {
			this.pool.execute(new Worker2(con, otp));
		} catch (AlreadyClosedException e) {
			logger.err(e);
		}
	}
	
	public void currentThreadHandle(IConnection con, byte[] packet){
		new Worker(con, packet).run();
	}
	
	public void currentThreadHandle(IConnection con, Otp otp){
		new Worker2(con, otp).run();
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
	
	private class Worker2 implements Runnable{
		private IConnection con;
		private Otp otp;
		
		private Worker2(IConnection con, Otp otp){
			this.con = con;
			this.otp = otp;
		}
		
		@Override
		public void run() {
			firstHandler.process(new Handle(con, otp));
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
		
		private Handle(IConnection con, Otp otp){
			this.con = con;
			this.msg = otp;
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
