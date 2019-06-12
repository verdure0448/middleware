package com.hdbsnc.smartiot.adapter.websocketapi.connection.impl;

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

import com.hdbsnc.smartiot.common.connection.IConnection;
import com.hdbsnc.smartiot.common.connection.IConnectionManager;
import com.hdbsnc.smartiot.common.connection.IConnector;
import com.hdbsnc.smartiot.util.logger.Log;

public class ServerSocketChannelConnector  implements IConnector{

	public static Log LOG;
	private int readBufferSize = 4096;	
	public static final String KEY_IP = "IpAddress";
	public static final String KEY_PORT = "Port";
	public static final String KEY_READBUFFERSIZE = "ReadBufferSize";

	
	private boolean isStart = false;
	private InetSocketAddress inetAddress;
	private ServerSocketChannel serverChannel;
	private Selector selector;
	private ConnectionHandleChain_old chc;
	private Thread self = null;
	
	public ServerSocketChannelConnector(ConnectionHandleChain_old chc, Log logger){
		this.chc = chc;
		this.LOG = logger;
		
	}

	@Override
	public void initialize(Map params) {
		
		String param1 = (String) params.get(KEY_IP);
		String param2 = (String) params.get(KEY_PORT);
		String param3 = (String) params.get(KEY_READBUFFERSIZE);


		this.inetAddress = new InetSocketAddress(param1, Integer.parseInt(param2));
		this.readBufferSize = Integer.parseInt(param3);
	}

	@Override
	public void start() {
		this.isStart = true;
		this.self = new Thread(this);
		this.self.start();
		
	}

	@Override
	public void stop() {
		this.isStart = false;
		this.selector.wakeup();
		
	}

	@Override
	public boolean isStart() {
		return this.isStart;
	}
	

	@Override
	public void run() {
		LOG.info("SocketChannelConnector start.");
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
			SocketChannelConnection con;
			
			byte[] packet;
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
							key.attach(new SocketChannelConnection(key, this.readBufferSize));
						}else if(selected.isReadable()){
							con = (SocketChannelConnection) selected.attachment();
							
							if(con.isConnected()==false) {
								con.disconnect();
								continue;
							}
							try {
								packet = con.read();//내부적으로 반복문 안에서 읽을 수 없을때까지 계속해서 읽어서 하나의 패킷으로 리턴. 
							} catch (IOException e1) {
								con.disconnect();
								continue;
							}
							if(packet==null){
								con.disconnect();
								continue;
							}
							chc.handle(con, packet);
						}
					}
				}catch(CancelledKeyException e){
					LOG.err(e);
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			LOG.err(e);
		} finally{
			try{
				selector.close();
				serverChannel.close();
				serverChannel.socket().close();
			}catch(IOException e){
				LOG.err(e);
			}
		}
		LOG.info("SocketChannelConnector stop.");
	}



}