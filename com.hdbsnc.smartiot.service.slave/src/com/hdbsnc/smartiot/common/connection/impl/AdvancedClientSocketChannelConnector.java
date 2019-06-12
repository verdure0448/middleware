package com.hdbsnc.smartiot.common.connection.impl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import com.hdbsnc.smartiot.common.connection.IConnection;
import com.hdbsnc.smartiot.common.connection.IConnectionManager;
import com.hdbsnc.smartiot.common.connection.IConnector;
import com.hdbsnc.smartiot.common.otp.url.parser.UrlParseException;
import com.hdbsnc.smartiot.common.otp.url.parser.UrlParser;
import com.hdbsnc.smartiot.common.otp.url.parser.vo.Url;
import com.hdbsnc.smartiot.service.slave.impl.Sss2;
import com.hdbsnc.smartiot.util.logger.Log;
import com.hdbsnc.smartiot.util.servicepool.AlreadyClosedException;
import com.hdbsnc.smartiot.util.servicepool.ServicePool;

public class AdvancedClientSocketChannelConnector implements IConnector{
	//public static Log LOG = Bootstrap.LOG2.logger("SocketChannelConnector");
	public Log log;
//	private static final String NAME = "com.hdbsnc.smartiot.server.connector.ClientSocketChannellConnector";
//	private int writeBufferSize = 4096;
//	private int shareBufferSize = 4096;
	private int readBufferSize = 1024;
	private int retryMs = 5000;
	
	public static final String KEY_IP = "IpAddress";
	public static final String KEY_PORT = "Port";
	public static final String KEY_READBUFFERSIZE = "ReadBufferSize";
	public static final String KEY_RETRY_MS = "RetryMs";
//	public static final String KEY_WRITEBUFFERSIZE = "WriteBufferSize";
//	public static final String KEY_SHAREBUFFERSIZE = "ShareBufferSize";
	
	private boolean isStart = false;
	private InetSocketAddress inetAddress;
	private Selector selector;
	private ConnectionHandleChain chc;
	private IConnectionManager cm;
	private Thread self = null;
	private String slaveServerId;
	private String masterServerId;
	private ServicePool pool;
	
	public AdvancedClientSocketChannelConnector(IConnectionManager cm, ConnectionHandleChain chc1, Log logger3, ServicePool pool){
		this.chc = chc1;
		this.log = logger3.logger("AdvancedClientChannelConnector");
		this.cm = cm;
		this.pool = pool;
	}
	
	@Override
	public void initialize(Map params) {
		this.slaveServerId = (String) params.get(Sss2.KEY_SLAVE_SERVERID);
		this.masterServerId = (String) params.get(Sss2.KEY_MASTER_SERVERID);
		
		String retryMsString = (String) params.get(KEY_RETRY_MS);
		this.retryMs = Integer.parseInt(retryMsString);
		
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
		log.info("AdvancedClientSocketChannelConnector start.");
		SocketChannel clientChannel = null;
		Socket client = null;
		while(isStart){
			try {
				clientChannel = SocketChannel.open();
				client = clientChannel.socket();
				client.setReuseAddress(true);
				client.setSoTimeout(0);
				clientChannel.configureBlocking(false);
				selector = Selector.open();
				clientChannel.register(selector, SelectionKey.OP_CONNECT);
				
				
				clientChannel.connect(inetAddress);
				
				Iterator<SelectionKey> iter;
				SelectionKey selected;
				AdvancedSocketChannelConnection con;
				int readCount = 0;
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
								con = new AdvancedSocketChannelConnection(selected, this.readBufferSize, chc, pool, log);
								selected.attach(con);
								
								//서버에 로그인을 한다는지 최초 연결후 해야할 요청이 있다면 여기에 한다.
								//Context를 생성해서 processor에 태운다.
								
								connect(con);
							}else if(selected.isReadable()){
								con = (AdvancedSocketChannelConnection) selected.attachment();	
								if(con.isConnected()==false) {
									con.disconnect();
									continue;
								}
								try {
									readCount = con.gatlingRead();
								} catch (IOException e1) {
									log.err(e1);
									if(con!=null){
										String remoteAddress = con.getRemoteAddress();
										if(remoteAddress!=null){
											remoteAddress = "remoteAddress:none";
										}
										log.err("연결이 끊어짐.("+remoteAddress+")");
										con.disconnect();
									}
									continue;
								} catch (AlreadyClosedException e) {
									log.err(e);
									continue;
								}
								if(readCount==-1){//연결이 끊어졌다.
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
					}
				}
			} catch (IOException e) {
				log.err(e.getMessage()+": 마스터 서버로 연결이 되지 않음. ("+retryMs+"ms 후 재연결 시도)");
				try {
					Thread.sleep(retryMs);
				} catch (InterruptedException e1) {
					log.err(e);
				}
				//LOG.err(e);
			}
			try{
				selector.close();
				if(clientChannel!=null) clientChannel.close();
				if(client!=null) client.close();
			}catch(IOException e){
				log.err(e);
			}
		}
		log.info("AdvancedClientSocketChannelConnector stop.");
	}
	
	private void connect(IConnection con){
		Url request = Url.createOtp();
		request.setUserInfo(this.slaveServerId, null);
		request.setHostInfo(this.masterServerId, null);
		request.setPaths(Arrays.asList("master","connect"));
		cm.putConnection(this.slaveServerId, con);
		try {
			con.write(UrlParser.getInstance().parse(request).getBytes());
		} catch (IOException e) {
			log.err(e);
		} catch (UrlParseException e) {
			log.err(e);
		}
	}
}