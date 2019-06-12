package com.hdbsnc.smartiot.service.auth.connection.impl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;

import com.hdbsnc.smartiot.common.connection.IConnector;
import com.hdbsnc.smartiot.util.logger.Log;
import com.hdbsnc.smartiot.util.servicepool.AlreadyClosedException;
import com.hdbsnc.smartiot.util.servicepool.ServicePool;

public class AdvancedServerSocketChannelConnector  implements IConnector{

	public Log log;
	private int readBufferSize = 1024;	
	public static final String KEY_IP = "IpAddress";
	public static final String KEY_PORT = "Port";
	public static final String KEY_READBUFFERSIZE = "ReadBufferSize";

	
	private boolean isStart = false;
	private InetSocketAddress inetAddress;
	private ServerSocketChannel serverChannel;
	private Selector selector;
	private ConnectionHandleChain chc;
	private Thread self = null;
	private ServicePool pool;
	
	public AdvancedServerSocketChannelConnector(ConnectionHandleChain chc, Log logger, ServicePool servicePool){
		this.chc = chc;
		this.log = logger;
		this.pool = servicePool;
		
	}

	@Override
	public void initialize(Map<String, String> params) {
		
		String param1 = (String) params.get(KEY_IP);
		String param2 = (String) params.get(KEY_PORT);
		String param3 = (String) params.get(KEY_READBUFFERSIZE);


		this.inetAddress = new InetSocketAddress(param1, Integer.parseInt(param2));
		this.readBufferSize = Integer.parseInt(param3);
	}

	@Override
	public synchronized void start() {
		this.isStart = true;
		this.self = new Thread(this);
		this.self.start();
		
	}

	@Override
	public synchronized void stop() {
		this.isStart = false;
		this.selector.wakeup();
		
	}

	@Override
	public synchronized boolean isStart() {
		return this.isStart;
	}
	

	@Override
	public void run() {
		log.info("AdvancedSocketChannelConnector start.");
		try {
			serverChannel = ServerSocketChannel.open();
			ServerSocket server = serverChannel.socket();
			server.setReuseAddress(true);
			server.bind(inetAddress);
			serverChannel.configureBlocking(false);
			selector = Selector.open();
			serverChannel.register(selector, SelectionKey.OP_ACCEPT);
			
			SelectionKey selected = null;
			Iterator<SelectionKey> iter;
			ServerSocketChannel serverChannel;
			SocketChannel clientChannel;
			SelectionKey key;
			AdvancedSocketChannelConnection con;
			
			int readCount = 0;
			while(isStart){
				selector.select();
				iter = selector.selectedKeys().iterator();
				try{
					while(iter.hasNext()){
						selected = iter.next();
						iter.remove();
						if(selected.isAcceptable()){
							serverChannel = (ServerSocketChannel) selected.channel();
							clientChannel = serverChannel.accept();
							if(clientChannel==null) return;
							clientChannel.configureBlocking(false);
							key = clientChannel.register(selector, SelectionKey.OP_READ);
							key.attach(new AdvancedSocketChannelConnection(key, this.readBufferSize, chc, pool, log));
						}else if(selected.isReadable()){
							con = (AdvancedSocketChannelConnection) selected.attachment();
							
							if(con.isConnected()==false) {
								con.disconnect();
								continue;
							}
							try {
								readCount = con.gatlingRead(); 
							} catch (IOException e1) {
								con.disconnect();
								log.err(e1);
								continue;
							} catch (AlreadyClosedException e) {
								e.printStackTrace();
								log.err(e);
								continue;
							}
							if(readCount==-1){
								String remoteAddress = con.getRemoteAddress();
								if(remoteAddress!=null){
									remoteAddress = "remoteAddress:none";
								}
								log.err("연결이 끊어짐.("+remoteAddress+")");
								con.disconnect();
								continue;
							}
						}
					}
				}catch(CancelledKeyException e){
					log.err(e);
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			log.err(e);
		} finally{
			try{
				selector.close();
				serverChannel.close();
				serverChannel.socket().close();
			}catch(IOException e){
				log.err(e);
			}
		}
		log.info("AdvancedSocketChannelConnector stop.");
	}



}