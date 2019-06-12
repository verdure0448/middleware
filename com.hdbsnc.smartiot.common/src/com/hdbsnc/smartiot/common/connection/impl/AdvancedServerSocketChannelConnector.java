package com.hdbsnc.smartiot.common.connection.impl;

import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;

import com.hdbsnc.smartiot.common.ICommonService;
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
	private ICommonService service;
	
	public AdvancedServerSocketChannelConnector(ICommonService commonService, Log parentLogger){
		this.service = commonService;
		if(parentLogger!=null){
			this.log = parentLogger;
		}else{
			this.log = commonService.getLogger();
		}
		this.pool = commonService.getServicePool();
		this.chc = new ConnectionHandleChain(pool, log);
	}
	
	public ConnectionHandleChain getConnectionHandleChain(){
		return this.chc;
	}

	ServerSocket server = null;
	@Override
	public void initialize(Map params) throws Exception{
		String param1 = (String) params.get(KEY_IP);
		if(param1==null || param1.equals("")) throw service.getExceptionfactory().createSysException("108", new String[]{"잘못된 IP 입니다."});
		String param2 = (String) params.get(KEY_PORT);
		if(param2==null || param2.equals("")) throw service.getExceptionfactory().createSysException("108", new String[]{"잘못된 Port 입니다."});
		String param3 = (String) params.get(KEY_READBUFFERSIZE);
		if(param3!=null && !param3.equals("")) this.readBufferSize = Integer.parseInt(param3);

		this.inetAddress = new InetSocketAddress(InetAddress.getByName(param1), Integer.parseInt(param2));
		
		serverChannel = ServerSocketChannel.open();
		serverChannel.configureBlocking(false);
		server = serverChannel.socket();
		server.setReuseAddress(true);
		try{
			server.bind(inetAddress);
		}catch(BindException e){
			throw new IOException("서버 소켓 IP, Port 가 잘못되었습니다.");
		}
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
		if(this.selector!=null) this.selector.wakeup();
		
	}

	@Override
	public synchronized boolean isStart() {
		return this.isStart;
	}
	

	@Override
	public void run() {
		log.info("AdvancedSocketChannelConnector start.");
		try {
//			serverChannel = ServerSocketChannel.open();
//			ServerSocket server = serverChannel.socket();
//			server.setReuseAddress(true);
//			server.bind(inetAddress);
//			serverChannel.configureBlocking(false);
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
				if(selector!=null)selector.close();
				if(serverChannel!=null)serverChannel.close();
				if(serverChannel!=null)serverChannel.socket().close();
			}catch(IOException e){
				log.err(e);
			}
		}
		log.info("AdvancedSocketChannelConnector stop.");
	}



}