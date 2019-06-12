package com.hdbsnc.smartiot.common.connection.impl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;

import com.hdbsnc.smartiot.common.ICommonService;
import com.hdbsnc.smartiot.common.connection.IConnection;
import com.hdbsnc.smartiot.common.connection.IConnectionManager;
import com.hdbsnc.smartiot.common.connection.IConnector;
import com.hdbsnc.smartiot.common.ism.sm.ISession;
import com.hdbsnc.smartiot.util.logger.Log;
import com.hdbsnc.smartiot.util.servicepool.AlreadyClosedException;
import com.hdbsnc.smartiot.util.servicepool.ServicePool;

public class AdvancedClientSocketChannelConnector implements IConnector{
	public Log log;
	private int readBufferSize = 1024;
	private int retryMs = 5000;
	
	public static final String KEY_IP = "IpAddress";
	public static final String KEY_PORT = "Port";
	public static final String KEY_READBUFFERSIZE = "ReadBufferSize";
	public static final String KEY_RETRY_MS = "RetryMs";
	public static final String KEY_SESSION = "Session";
	
	private boolean isStart = false;
	private InetSocketAddress inetAddress;
	private Selector selector;
	private ConnectionHandleChain chc;
	private IConnectionManager cm;
	private Thread self = null;
	private ServicePool pool;
//	private ISession session;
	ICommonService service;
	
	public AdvancedClientSocketChannelConnector(IConnectionManager cm, ConnectionHandleChain chc1, Log logger3, ICommonService commonService){
		this.chc = chc1;
		this.log = logger3.logger(this.getClass());
		this.cm = cm;
		this.service = commonService;
		this.pool = service.getServicePool();
	}
	
	IAfterConnect work = null;
	public void setAfterConnect(IAfterConnect work){
		this.work = work;
	}
	
	@Override
	public void initialize(Map params) throws Exception{
		
//		String param1 = (String) params.get(KEY_IP);
//		String param2 = (String) params.get(KEY_PORT);
//		String param3 = (String) params.get(KEY_READBUFFERSIZE);
//		this.inetAddress = new InetSocketAddress(param1, Integer.parseInt(param2));
//		
//		this.readBufferSize = Integer.parseInt(param3);
////		this.session = (ISession) params.get(KEY_SESSION);
		
		String param1 = (String) params.get(KEY_IP);
		if(param1==null || param1.equals("")) throw service.getExceptionfactory().createSysException("108", new String[]{"잘못된 IP 입니다."});
		String param2 = (String) params.get(KEY_PORT);
		if(param2==null || param2.equals("")) throw service.getExceptionfactory().createSysException("108", new String[]{"잘못된 Port 입니다."});
		String param3 = (String) params.get(KEY_READBUFFERSIZE);
		if(param3!=null && !param3.equals("")) this.readBufferSize = Integer.parseInt(param3);

		this.inetAddress = new InetSocketAddress(InetAddress.getByName(param1), Integer.parseInt(param2));
		//테스트 연결을 시도해서 되지 않으면 예외 발생.
		Socket socket = new Socket();
		socket.connect(inetAddress, 5000);
	}
	
	@Override
	public void start() {
		synchronized(this){
			this.isStart = true;
			this.self = new Thread(this);
			this.self.start();
		}
		
	}
	
	@Override
	public void stop() {
		synchronized(this){
			this.isStart = false;
			if(this.selector!=null) this.selector.wakeup();
		}
	}
	
	@Override
	public synchronized boolean isStart() {
		return this.isStart;
	}
	
	
	@Override
	public void run() {
		log.info("start.");
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
								if(work!=null)
									try {
										work.afterConnect(con);
									} catch (Exception e) {
										log.warn(e.getMessage());
										con.disconnect();
										continue;
									} 
//								if(session!=null) cm.putConnection(session.getSessionKey(), con);
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
				log.err(e.getMessage()+": 연결이 되지 않음. ("+retryMs+"ms 후 재연결 시도)");
				try {
					Thread.sleep(retryMs);
				} catch (InterruptedException e1) {
					log.err(e);
				}
			}
			try{
				if(selector!=null)selector.close();
				if(clientChannel!=null) clientChannel.close();
				if(client!=null) client.close();
			}catch(IOException e){
				log.err(e);
			}
		}
		log.info("stop.");
	}
	
	public interface IAfterConnect{
		
		public void afterConnect(IConnection con) throws Exception;
	}
}