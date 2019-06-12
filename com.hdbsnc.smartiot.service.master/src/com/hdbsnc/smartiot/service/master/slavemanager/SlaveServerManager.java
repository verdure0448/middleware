package com.hdbsnc.smartiot.service.master.slavemanager;

import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.hdbsnc.smartiot.common.ICommonService;
import com.hdbsnc.smartiot.common.connection.IConnection;
import com.hdbsnc.smartiot.common.ism.IIntegratedSessionManager;
import com.hdbsnc.smartiot.util.logger.Log;
import com.hdbsnc.smartiot.util.servicepool.ServicePool;

public class SlaveServerManager implements Comparator<Server>{
	private Map<IConnection, Server> serverMap; //IConnection:Server
	private HashKeyGenerator keyGen;
	private String masterServerId;
	private IIntegratedSessionManager ism;
	private ServicePool pool;
	private ICommonService commonService;
	private Log log;
	
	
	public SlaveServerManager(String masterServerId, ICommonService commonService, IIntegratedSessionManager ism, Log log){
		this.commonService = commonService;
		this.pool = commonService.getServicePool();
		this.masterServerId = masterServerId;
		this.log = log;
		this.serverMap = new Hashtable<IConnection, Server>();
		
		try {
			this.keyGen = new HashKeyGenerator("MD5", masterServerId);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		this.ism = ism;
	}
		
	public String getMasterServerId(){
		return this.masterServerId;
	}
	
	public Server getServer(String sid){
		if(sid==null) return null;
		if(sid.startsWith("sid-")){
			//sessionId
			return this.getServerByServerSessionKey(sid);
		}else{
			//did
			return this.getServerByServerId(sid);
		}
	}
	
	public Server addServer(IConnection con, String serverId){
		Server server = this.getServerByServerId(serverId);
		if(server==null){
			log.info("slave server("+serverId+") 신규 생성.");
		}else{
			log.info("slave server("+serverId+") 다시 연결이 들어 왔으므로 기존 리소스 정리 후 다시 생성.");
			serverMap.remove(server.con);
			server.cancelAll();
		}
		server = new Server(pool, ism, con, serverId, keyGen.generateKey(serverId));
		serverMap.put(con, server);
		return server;
	}
	
	public void removeServer(IConnection con){
		synchronized(this){
			Server server = serverMap.remove(con);
			if(server!=null) server.cancelAll();
		}
	}
	
	public Server getServerByDeviceSessionKey(String deviceSessionKey){
		Collection<Server> servers = serverMap.values();
		for(Server server: servers){
			if(server.containsDevice(deviceSessionKey)){
				return server;
			}
		}
		return null;
	}
	
	public Server getServerByServerSessionKey(String serverSessionKey){
		Collection<Server> servers = serverMap.values();
		for(Server server: servers){
			if(server.getSessionKey().equals(serverSessionKey)){
				return server;
			}
		}
		return null;
	}
	
	public boolean containsServerSessionKey(String serverSessionKey){
		Collection<Server> servers = serverMap.values();
		for(Server server: servers){
			if(server.getSessionKey().equals(serverSessionKey)){
				return true;
			}
		}
		return false;
	}
	
	public Server getServerByServerId(String serverId){
		Collection<Server> servers = serverMap.values();
		for(Server server: servers){
			if(server.getServerId().equals(serverId)){
				return server;
			}
		}
		return null;
	}
	
	public Server getServerByDeviceId(String deviceId){
		Collection<Server> servers = serverMap.values();
		for(Server server: servers){
			if( server.containsDeviceByDeviceId(deviceId)){
				return server;
			}
		}
		return null;
	}
	
	public Collection<Server> getServerList(){
		return this.serverMap.values();
	}
	
	
	
	public boolean containsDevicesId(String deviceId){
		Collection<Server> servers = serverMap.values();
		for(Server server: servers){
			if( server.containsDeviceByDeviceId(deviceId)){
				return true;
			}
		}
		return false;
	}
	
	public Server getServer(IConnection con){
		return serverMap.get(con);
	}
	
	public boolean containsDeviceSessionKey(String deviceSessionKey){
		Collection<Server> servers = serverMap.values();
		for(Server server: servers){
			if(server.containsDevice(deviceSessionKey)){
				return true;
			}
		}
		return false;
	}
	
	public void dispose(){
		Collection<Server> servers = serverMap.values();
		for(Server server: servers){
			server.cancelAll();
		}
		serverMap.clear();
	}
	
	int counter = 0;
	public synchronized Server roundRobinSelectSlaveServer(String iid) throws Exception{
		List<Server>sortedList = serverMap.values().stream()
				.filter(x->x.containsInstance(iid))
				.filter(x->x.getInstance(iid).getState().equals(Server.Instance.STATE_START))
				.sorted(this)
				.collect(Collectors.toList());	
		
		if(sortedList.size()==0){
			counter = 0;
			throw commonService.getExceptionfactory().createSysException("509", new String[]{iid});
		}
		if(sortedList.size()>=counter){
			counter = 0;
		}
		return sortedList.get(counter++);
	}

	@Override
	public int compare(Server server1, Server server2) {
		return server2.deviceMap.values().size() - server1.deviceMap.values().size();
	}

}
