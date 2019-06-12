package com.hdbsnc.smartiot.adapter.websocketapi.connection.impl;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import com.hdbsnc.smartiot.common.connection.IConnection;
import com.hdbsnc.smartiot.common.otp.impl.Otp;
import com.hdbsnc.smartiot.common.otp.url.parser.UrlParseException;
import com.hdbsnc.smartiot.common.otp.url.parser.UrlParser;
import com.hdbsnc.smartiot.common.otp.url.parser.vo.Url;
import com.hdbsnc.smartiot.util.servicepool.AlreadyClosedException;
import com.hdbsnc.smartiot.util.servicepool.ServicePool;

public class ConnectionHandleChain_old {

	private ServicePool pool;
	private AbstractConnectionHandler firstHandler;
	private UrlParser parser;
	
	public ConnectionHandleChain_old(ServicePool pool){
		this.pool = pool;
		this.parser = UrlParser.getInstance();
	}
	
	public AbstractConnectionHandler setRootHandler(AbstractConnectionHandler handler){
		this.firstHandler = handler;
		return this.firstHandler;
	}
	
	public void handle(IConnection con, Otp otp){
		try {
			this.pool.execute(new Worker(con, otp));
		} catch (AlreadyClosedException e) {
			e.printStackTrace();
		}
	}
	
	public void handle(IConnection con, byte[] packets){
		try {
			this.pool.execute(new TsWorker(con, packets));
		} catch (AlreadyClosedException e) {
			e.printStackTrace();
		}
	}
	
	public void currentThreadhandle(IConnection con, byte[] packets){
		new TsWorker(con, packets).run();
	}
	
	public void currentThreadHandle(IConnection con, Otp otp){
		new Worker(con, otp).run();
	}
	
	private Otp needContent = null;
	public void currentThreadHandle(IConnection con, byte[] packet){
		if(packet==null) {
			//이벤트 메시지 생성해서 전파 할 것...
			return;
		}
		
		Url tempUrl = null;
		Otp tempOtp = null;
		if(needContent!=null){
			needContent.setContent(packet);
			tempOtp = needContent;
			needContent = null;
		}else{
			String tempPacket = new String(packet);
//			System.out.println(tempPacket);
			try {
				tempUrl = parser.parse(tempPacket);
			} catch (UrlParseException e) {
				e.printStackTrace();
				return;
			}
			tempOtp = new Otp(tempUrl);
			if(tempOtp.hasContent()){
				needContent = tempOtp;
				return;
			}
		}
		
		firstHandler.process(new Handle(con,tempOtp));
	}
	
	private class TsWorker implements Runnable{
		private IConnection con;
		private byte[] packets;
		private TsWorker(IConnection con, byte[] packets){
			this.con = con;
			this.packets = packets;
		}
		
		@Override
		public void run() {
			BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(packets)));
			String tempPacket = null;
			Url tempUrl = null;
			Otp tempRequest = null;
			try {
				while(br.ready()){
					tempPacket = br.readLine();
//					System.out.println(tempPacket);
					tempUrl = parser.parse(tempPacket);
					tempRequest = new Otp(tempUrl);
					if(tempRequest.hasContent()){
						tempPacket = br.readLine();
//						System.out.println(tempPacket);
						tempRequest.setContent(tempPacket.getBytes());
					}
					firstHandler.process(new Handle(con,tempRequest));
				}
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			} catch (UrlParseException e) {
				e.printStackTrace();
				return;
			}
		}
	}
	
	private class Worker implements Runnable{
		private IConnection con;
		private Otp otp;
		
		private Worker(IConnection con, Otp otp){
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
		private Otp otp;
		
		private Handle(IConnection con, Otp otp){
			this.con = con;
			this.otp = otp;
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
		
		public Otp getOtp(){
			return this.otp;
		}
		
	}	
}
