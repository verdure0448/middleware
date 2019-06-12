package com.hdbsnc.smartiot.service.command.common;

import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import com.hdbsnc.smartiot.common.ICommonService;
import com.hdbsnc.smartiot.common.ism.IIntegratedSessionManager;
import com.hdbsnc.smartiot.server.IServerInstance;
import com.hdbsnc.smartiot.service.IService;
import com.hdbsnc.smartiot.service.command.common.util.text.impl.StringHeaderRow;
import com.hdbsnc.smartiot.service.command.common.util.text.impl.StringTable;
import com.hdbsnc.smartiot.service.master.IMasterService;
import com.hdbsnc.smartiot.service.master.slavemanager.SlaveServerManager;

public class CommonCommand implements IShellCommandExecuter {
	
	private IServerInstance server;
	private IIntegratedSessionManager ism;
	
	private Hashtable props;
	private CommandStack cStack;
	private List<String> funcList = Arrays.asList("ctcnt");
	private IMasterService ms = null;
	private SlaveServerManager ssm = null;
	final int MAX_SIZE = 170;
	private ICommonService ics = null;
	
	
	public CommonCommand(CommandStack cStack, IServerInstance server) {
		// TODO Auto-generated constructor stub
		this.server = server;
		this.cStack = cStack;
		this.cStack.addExecuter(this);
		
		this.props = new Hashtable();
		this.props.put("osgi.command.scope", "smartiot");
		this.props.put("osgi.command.function", (String[]) funcList.toArray());
		
		this.ics = server.getCommonService();
	}
	
	public void ctcnt(){
		List<IService> serviceList = server.getServiceList();
		IService service = null;
		int index = 0;
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
		
		StringTable table = new StringTable(MAX_SIZE);
		table.setHeader(new StringHeaderRow().add("NO", 3).add("CreatedCnt", 7).add("AllowedCnt", 7).add("IdleCnt", 7).add("MaxCnt", 7).add("MinCnt", 7).add("WorkCnt", 7));
		
		table.addRowData(String.valueOf(++index), String.valueOf(ics.getServicePool().getCreatedThreadCount()), String.valueOf(ics.getServicePool().getAllowedIdleCount()), String.valueOf(ics.getServicePool().getIdleThreadCount()),String.valueOf(ics.getServicePool().getMaxThreadCount()),String.valueOf(ics.getServicePool().getMinThreadCount()),String.valueOf(ics.getServicePool().getWorkThreadCount()));
		table.simplePrint();
		cStack.addShellCommand(new ShellCommand("msthreadcnt", null));
	}
	
	


	@Override
	public String getExecuterName() {
		// TODO Auto-generated method stub
		return "MssCommand";
	}



	@Override
	public Dictionary<String, String> getProps() {
		// TODO Auto-generated method stub
		return this.props;
	}



	@Override
	public boolean isExecuteable(ShellCommand cmd) {
		// TODO Auto-generated method stub
		if(funcList.contains(cmd.getCommand())){
			return true;
		}
		return false;
	}

	@Override
	public void execute(ShellCommand cmd) {
		// TODO Auto-generated method stub
		String cmdTxt = cmd.getCommand();
		switch (cmdTxt) {
		case "ctcnt":
			ctcnt();
			break;
		}
	}
	
}
