package com.hdbsnc.smartiot.service.command.mss;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.hdbsnc.smartiot.common.ism.IIntegratedSessionManager;
import com.hdbsnc.smartiot.common.ism.sm.ISessionManager;
import com.hdbsnc.smartiot.server.IServerInstance;
import com.hdbsnc.smartiot.service.IService;
import com.hdbsnc.smartiot.service.command.common.CommandStack;
import com.hdbsnc.smartiot.service.command.common.IShellCommandExecuter;
import com.hdbsnc.smartiot.service.command.common.ShellCommand;
import com.hdbsnc.smartiot.service.command.common.util.text.impl.StringHeaderRow;
import com.hdbsnc.smartiot.service.command.common.util.text.impl.StringTable;
import com.hdbsnc.smartiot.service.master.IMasterService;
import com.hdbsnc.smartiot.service.master.slavemanager.Server;
import com.hdbsnc.smartiot.service.master.slavemanager.SlaveServerManager;
import com.hdbsnc.smartiot.service.master.slavemanager.Server.Instance.Device;
import com.hdbsnc.smartiot.common.ICommonService;
import com.hdbsnc.smartiot.common.connection.IConnection;

public class MssCommand implements IShellCommandExecuter {

	private IServerInstance server;
	private IIntegratedSessionManager ism;

	// private IServiceManager sss;

	private Hashtable props;
	private CommandStack cStack;
	private List<String> funcList = Arrays.asList("mslist", "msdlist","msthreadcnt");
	private IMasterService ms = null;
	private SlaveServerManager ssm = null;
	final int MAX_SIZE = 170;
	private ICommonService ics = null;
	
	
	public MssCommand(CommandStack cStack, IServerInstance server) {
		this.server = server;
		this.cStack = cStack;
		this.cStack.addExecuter(this);
		
		this.props = new Hashtable();
		this.props.put("osgi.command.scope", "smartiot");
		this.props.put("osgi.command.function", (String[]) funcList.toArray());
		
		this.ics = server.getCommonService();
	}
	
	// 마스터 슬레이브 서버
	public void mslist() {
		List<IService> serviceList = server.getServiceList();
		IService service = null;
		for (int i = 0, s = serviceList.size(); i < s; i++) {
			service = serviceList.get(i);
			if (service instanceof IMasterService) {
				ms = (IMasterService) service;
				ssm = ms.getSlaveServerManager();
				break;
			}
		}
		if (ms == null) {
			System.out.println("Master Server is nothing");
			return;
		}

		int index = 0;
		StringTable table = new StringTable(MAX_SIZE);
		table.setHeader(new StringHeaderRow().add("NO", 5).add("Master", 30).add("Slave", 30));

		Collection<Server> serverList = ssm.getServerList();
		Iterator<Server> it = serverList.iterator();
		Server s = null;
		while (it.hasNext()) {
			s = it.next();
			table.addRowData(String.valueOf(++index), ssm.getMasterServerId(), s.getServerId());
		}
		if(index==0){
			table.addRowData(String.valueOf(++index),"-" ,"-" ,"-");
		}
		table.simplePrint();
		cStack.addShellCommand(new ShellCommand("mslist", null));
	}

	// 마스터서버의 슬레이브 서버의 전체 장치리스트
	public void msdlist() {
		List<IService> serviceList = server.getServiceList();
		IService service = null;
		for (int i = 0, s = serviceList.size(); i < s; i++) {
			service = serviceList.get(i);
			if (service instanceof IMasterService) {
				ms = (IMasterService) service;
				ssm = ms.getSlaveServerManager();
				break;
			}
		}
		if (ms == null) {
			System.out.println("Master Server is nothing");
			return;
		}
		Device device;
		int index = 0;
		StringTable table = new StringTable(MAX_SIZE);
		table.setHeader(new StringHeaderRow().add("NO", 5).add("Slave", 15).add("Instance", 17)
				.add("DeviceID", 30));

		Collection<Server> serverList = ssm.getServerList();
		Iterator<Server> it = serverList.iterator();
		Server s = null;
		
		while (it.hasNext()) {
			s = it.next();
			//s.getServerId();
			Server server = ssm.getServerByServerId(s.getServerId());
			Set<String> set = server.getDeviceSessionKeySet();

			Iterator<String> itstr = set.iterator();
			String str = null;
			while (itstr.hasNext()) {
				str = itstr.next();
				device = server.getDevice(str);

				table.addRowData(String.valueOf(++index),  s.getServerId(), device.getInstanceId(), device.getDeviceId());
			}
		}
		if(index==0){
			table.addRowData(String.valueOf(++index),"-" ,"-" ,"-");
		}
		table.simplePrint();
		cStack.addShellCommand(new ShellCommand("msilist", null));
	}
	//사라질친구
	// 마스터서버의 슬레이브별 인스턴스의 장치리스트
	public void msdlist(String instanceId) {
		List<IService> serviceList = server.getServiceList();
		IService service = null;
		for (int i = 0, s = serviceList.size(); i < s; i++) {
			service = serviceList.get(i);
			if (service instanceof IMasterService) {
				ms = (IMasterService) service;
				ssm = ms.getSlaveServerManager();
				break;
			}
		}
		if (ms == null) {
			System.out.println("Master Server is nothing");
			return;
		}
		Device device;
		int index = 0;
		StringTable table = new StringTable(MAX_SIZE);
		table.setHeader(new StringHeaderRow().add("NO", 5).add("Slave", 15)
				.add("DeviceID", 30).add("SessionKey", 30).add("UserID", 10));
		// System.out.println("serverid : " + ssm.getMasterServerId());
		Collection<Server> serverList = ssm.getServerList();
		Iterator<Server> it = serverList.iterator();
		Server s = null;
		while (it.hasNext()) {
			s = it.next();
			Server server = ssm.getServerByServerId(s.getServerId());
			Set<String> set = server.getDeviceSessionKeySet();

			Iterator<String> itstr = set.iterator();
			String str = null;
			while (itstr.hasNext()) {
				str = itstr.next();
				device = server.getDevice(str);

				if (instanceId.equals(device.getInstanceId())) {
					table.addRowData(String.valueOf(++index),  s.getServerId(), device.getDeviceId(), device.getSessionKey(), device.getUserId());
				}
			}
		}
		if(index==0){
			table.addRowData(String.valueOf(++index),"-" ,"-" ,"-","-");
		}
		table.simplePrint();
		cStack.addShellCommand(new ShellCommand("msilist", null));
	}

	// 마스터서버의 슬레이브 서버의 전체 장치리스트
	public void msdlist(int arg) {
		List<IService> serviceList = server.getServiceList();
		IService service = null;
		for (int i = 0, s = serviceList.size(); i < s; i++) {
			service = serviceList.get(i);
			if (service instanceof IMasterService) {
				ms = (IMasterService) service;
				ssm = ms.getSlaveServerManager();
				break;
			}
		}
		if (ms == null) {
			System.out.println("Master Server is nothing");
			return;
		}
		Device device;
		int index = 0;
		int cnt = 0;
		StringTable table = new StringTable(MAX_SIZE);
		table.setHeader(new StringHeaderRow().add("NO", 5).add("Slave", 15).add("Instance", 17)
				.add("DeviceID", 30).add("SessionKey", 30).add("UserID", 6));

		Collection<Server> serverList = ssm.getServerList();
		Iterator<Server> it = serverList.iterator();
		Server s = null;
		while (it.hasNext()) {
			s = it.next();
			Server server = ssm.getServerByServerId(s.getServerId());
			Set<String> set = server.getDeviceSessionKeySet();
			cnt++;
			Iterator<String> itstr = set.iterator();
			String str = null;
			while (itstr.hasNext()) {
				str = itstr.next();
				device = server.getDevice(str);
				if (index++ == arg-1) {
					table.addRowData(String.valueOf(index),  s.getServerId(), device.getInstanceId(), device.getDeviceId(), device.getSessionKey(), device.getUserId());
				}
			}
		}
		if(index==0){
			table.addRowData(String.valueOf(++index),"-" ,"-" ,"-","-","-");
		}
		table.simplePrint();
		cStack.addShellCommand(new ShellCommand("msilist", null));
	}

	// 슬레이브에 들어있는 session 조회
	public void mslist(String slaveId) {
		int index = 0;
		int cnt=0;
		StringTable table = new StringTable(MAX_SIZE);
		table.setHeader(new StringHeaderRow().add("NO", 5).add("Slave", 10).add("DeviceID", 30).add("sessionID", 30).add("Userid", 10));
		
		IService service = null;
		List<IService> serviceList = server.getServiceList();
		for (int i = 0, s = serviceList.size(); i < s; i++) {
			service = serviceList.get(i);
			if (service instanceof IMasterService) {
				ms = (IMasterService) service;
				ssm = ms.getSlaveServerManager();
				break;
			}
		}
		
		Device device;
		Collection<Server> serverList = ssm.getServerList();
		Iterator<Server> it = serverList.iterator();
		Server s = null;
		while (it.hasNext()) {
			s = it.next();
			Server server = ssm.getServerByServerId(s.getServerId());
			Set<String> set = server.getDeviceSessionKeySet();
			Iterator<String> itstr = set.iterator();
			String str = null;
			while (itstr.hasNext()) {
				str = itstr.next();
				device = server.getDevice(str);
				if (slaveId.equals(device.getServerId())) {
					table.addRowData(String.valueOf(++index),  s.getServerId(), device.getDeviceId(), device.getSessionKey(),device.getUserId());
					cnt++;
				}
			}
		}
		table.addRowData("총계 : " + String.valueOf(cnt));
		table.simplePrint();
		cStack.addShellCommand(new ShellCommand("msslist", null));
	}
	
	public void mslist(int arg){
		String slaveId=null;
		int index = 0;
		int cnt=0;
		StringTable table = new StringTable(MAX_SIZE);
		table.setHeader(new StringHeaderRow().add("NO", 5).add("Slave", 10).add("DeviceID", 30).add("sessionID", 30).add("Userid", 10));
		
		Collection<Server> serverList = ssm.getServerList();
		Iterator<Server> it = serverList.iterator();
		Server s = null;
		while (it.hasNext()) {
			s = it.next();
			cnt++;
			if(arg == cnt){
				slaveId = s.getServerId();
			}
		}
		cnt=0;
		IService service = null;
		List<IService> serviceList = server.getServiceList();
		for (int i = 0, j = serviceList.size(); i < j; i++) {
			service = serviceList.get(i);
			if (service instanceof IMasterService) {
				ms = (IMasterService) service;
				ssm = ms.getSlaveServerManager();
				break;
			}
		}
		Device device;
		serverList = ssm.getServerList();
		it = serverList.iterator();
		s = null;
		while (it.hasNext()) {
			s = it.next();
			Server server = ssm.getServerByServerId(s.getServerId());
			Set<String> set = server.getDeviceSessionKeySet();
			Iterator<String> itstr = set.iterator();
			String str = null;
			while (itstr.hasNext()) {
				str = itstr.next();
				device = server.getDevice(str);
				if (slaveId.equals(device.getServerId())) {
					table.addRowData(String.valueOf(++index),  s.getServerId(), device.getDeviceId(), device.getSessionKey(),device.getUserId());
					cnt++;
				}
			}
		}
		table.addRowData("총계 : " + String.valueOf(cnt));
		table.simplePrint();
		cStack.addShellCommand(new ShellCommand("msslist", null));
	}
	
	@Override
	public String getExecuterName() {
		return "MssCommand";
	}

	@Override
	public Dictionary<String, String> getProps() {
		return this.props;
	}

	@Override
	public boolean isExecuteable(ShellCommand cmd) {
		if (funcList.contains(cmd.getCommand())) {
			return true;
		}
		return false;
	}

	@Override
	public void execute(ShellCommand cmd) {
		String cmdTxt = cmd.getCommand();
		switch (cmdTxt) {
		case "mslist":
			mslist();
			break;
		case "msilist":
			msdlist(cmd.getParams().get(0));
			break;
		}
	}

}
