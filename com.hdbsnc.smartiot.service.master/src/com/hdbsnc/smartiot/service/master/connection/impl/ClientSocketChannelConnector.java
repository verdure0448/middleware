package com.hdbsnc.smartiot.service.master.connection.impl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;

import com.hdbsnc.smartiot.common.connection.IConnector;
import com.hdbsnc.smartiot.util.logger.Log;

@Deprecated
public class ClientSocketChannelConnector implements IConnector{
	//public static Log LOG = Bootstrap.LOG2.logger("SocketChannelConnector");
	public static Log LOG;
//	private static final String NAME = "com.hdbsnc.smartiot.server.connector.ClientSocketChannellConnector";
//	private int writeBufferSize = 4096;
//	private int shareBufferSize = 4096;
	private int readBufferSize = 4096;
	
	public static final String KEY_IP = "IpAddress";
	public static final String KEY_PORT = "Port";
	public static final String KEY_READBUFFERSIZE = "ReadBufferSize";
//	public static final String KEY_WRITEBUFFERSIZE = "WriteBufferSize";
//	public static final String KEY_SHAREBUFFERSIZE = "ShareBufferSize";
	
	private boolean isStart = false;
	private InetSocketAddress inetAddress;
	private Selector selector;
	private ConnectionHandleChain chc;
	private Thread self = null;
	
	public ClientSocketChannelConnector(ConnectionHandleChain chc1, Log logger3){
		this.chc = chc1;
		this.LOG = logger3.logger("ClientChannelConnector");
	}
	
	@Override
	public void initialize(Map params) {
		String param1 = (String) params.get(KEY_IP);
		String param2 = (String) params.get(KEY_PORT);
		String param3 = (String) params.get(KEY_READBUFFERSIZE);
//		String param4 = (String) params.get(KEY_WRITEBUFFERSIZE);
//		String param5 = (String) params.get(KEY_SHAREBUFFERSIZE);
	
		this.inetAddress = new InetSocketAddress(param1, Integer.parseInt(param2));
		this.readBufferSize = Integer.parseInt(param3);
//		this.writeBufferSize = Integer.parseInt(param4);
//		this.shareBufferSize = Integer.parseInt(param5);
		
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
		LOG.info("ClientSocketChannelConnector start.");
		SocketChannel clientChannel = null;
		Socket client = null;
		try {
			clientChannel = SocketChannel.open();
			client = clientChannel.socket();
			client.setReuseAddress(true);
			//client.bind(selfAddress);
			clientChannel.configureBlocking(false);
			selector = Selector.open();
			clientChannel.register(selector, SelectionKey.OP_CONNECT);
			clientChannel.connect(inetAddress);
			Iterator<SelectionKey> iter;
			SelectionKey selected;
			SocketChannelConnection con;
			
			byte[] packet;
			while(isStart){
				selector.select();
				iter = selector.selectedKeys().iterator();
				
				try{
					while(iter.hasNext()){
						selected = iter.next();
						iter.remove();
						if(selected.isConnectable()){
							SocketChannel channel = (SocketChannel) selected.channel();
							if(!channel.isConnected()){
								if(channel.isConnectionPending()) channel.finishConnect();
							}
							selected.interestOps(SelectionKey.OP_READ);
							
							selected.attach(new SocketChannelConnection(selected, this.readBufferSize));
							
							//서버에 로그인을 한다는지 최초 연결후 해야할 요청이 있다면 여기에 한다.
//							callLogin(selected);
						
						}else if(selected.isReadable()){
							con = (SocketChannelConnection) selected.attachment();
							
							if(con.isConnected()==false) {
								con.disconnect();
								continue;
							}
							try {
								packet = con.read();
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
				if(clientChannel!=null) clientChannel.close();
				if(client!=null) client.close();
			}catch(IOException e){
				LOG.err(e);
			}
		}
		LOG.info("ClientSocketChannelConnector stop.");
	}
}