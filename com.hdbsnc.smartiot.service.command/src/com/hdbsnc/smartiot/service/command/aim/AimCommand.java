package com.hdbsnc.smartiot.service.command.aim;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import com.hdbsnc.smartiot.common.aim.AimException;
import com.hdbsnc.smartiot.common.aim.IAdapterInstanceContainer;
import com.hdbsnc.smartiot.common.aim.IAdapterInstanceEvent;
import com.hdbsnc.smartiot.common.aim.IAdapterInstanceManager;
import com.hdbsnc.smartiot.common.ism.IIntegratedSessionManager;
import com.hdbsnc.smartiot.common.pm.vo.IInstanceObj;
import com.hdbsnc.smartiot.server.IServerInstance;
import com.hdbsnc.smartiot.service.command.common.CommandStack;
import com.hdbsnc.smartiot.service.command.common.IShellCommandExecuter;
import com.hdbsnc.smartiot.service.command.common.ShellCommand;
import com.hdbsnc.smartiot.service.command.common.util.text.impl.StringHeaderRow;
import com.hdbsnc.smartiot.service.command.common.util.text.impl.StringRow;
import com.hdbsnc.smartiot.service.command.common.util.text.impl.StringTable;

public class AimCommand implements IShellCommandExecuter{
	
	private IServerInstance server;
	private IAdapterInstanceManager aim;
	private IIntegratedSessionManager ism;
	private Hashtable props;
	private CommandStack cStack;
	private List<String> funcList = Arrays.asList("istart", "istop", "isuspend", "ilist");
	final int MAX_SIZE = 150;
	
	
//	public static final SimpleDateFormat yyMMdd = new SimpleDateFormat("yyMMdd");
//	public static final SimpleDateFormat hhMMssSSS = new SimpleDateFormat("HH:mm:ss.SSS");
//	public static final SimpleDateFormat hhMMss = new SimpleDateFormat("HHmmss_SSS");
	public static final SimpleDateFormat yyMMddhhMMss = new SimpleDateFormat("yyMMddHHmmss");
	
	public AimCommand(CommandStack cStack, IServerInstance server){
		this.server = server;
		this.aim = server.getAIM();
		this.props = new Hashtable();
		this.cStack = cStack;
		this.cStack.addExecuter(this);
		props.put("osgi.command.scope", "smartiot");
		props.put("osgi.command.function", (String[])funcList.toArray());
	}
	
	public void istart(String iid){
		try{
			this.aim.start(iid);
		}catch(AimException e){
			System.out.println(e.getMessage());
		}
		cStack.addShellCommand(new ShellCommand("istart", Arrays.asList(iid)));
	}
	
	public void istop(String iid){
		try{
			this.aim.stop(iid);
		}catch(AimException e){
			System.out.println(e.getMessage());
		}
		cStack.addShellCommand(new ShellCommand("istop", Arrays.asList(iid)));
	}
	
	public void isuspend(String iid){
		try{
			this.aim.suspend(iid);
		}catch(AimException e){
			System.out.println(e.getMessage());
		}
		cStack.addShellCommand(new ShellCommand("isuspend", Arrays.asList(iid)));
	}
	
	public void istart(int arg){
		List<IAdapterInstanceContainer> aicList = aim.getAdapterContainerList();
		IAdapterInstanceContainer aic;
		IAdapterInstanceEvent aie;
		IInstanceObj info;
		String state=null;
		String event=null;
		
		aic = aicList.get(arg-1);
		aie = aic.getLastEvent();
		info = aie.getInstanceInfo();
		try{
			this.aim.start(info.getInsId());
		}catch(AimException e){
			System.out.println(e.getMessage());
		}
		cStack.addShellCommand(new ShellCommand("istart", Arrays.asList(info.getInsId())));
	}
	public void istop(int arg){
		List<IAdapterInstanceContainer> aicList = aim.getAdapterContainerList();
		IAdapterInstanceContainer aic;
		IAdapterInstanceEvent aie;
		IInstanceObj info;
		String state=null;
		String event=null;
		
		aic = aicList.get(arg-1);
		aie = aic.getLastEvent();
		info = aie.getInstanceInfo();

		try{
			this.aim.stop(info.getInsId());
		}catch(AimException e){
			System.out.println(e.getMessage());
		}
		cStack.addShellCommand(new ShellCommand("istop", Arrays.asList(info.getInsId())));
	}
	
	public void isuspend(int arg){
		List<IAdapterInstanceContainer> aicList = aim.getAdapterContainerList();
		IAdapterInstanceContainer aic;
		IAdapterInstanceEvent aie;
		IInstanceObj info;
		String state=null;
		String event=null;
		
		aic = aicList.get(arg-1);
		aie = aic.getLastEvent();
		info = aie.getInstanceInfo();
		
		try{
			this.aim.suspend(info.getInsId());
		}catch(AimException e){
			System.out.println(e.getMessage());
		}
		cStack.addShellCommand(new ShellCommand("isuspend", Arrays.asList(info.getInsId())));
	}
	
	public void ilist(){
		StringTable table = new StringTable(MAX_SIZE);
		StringHeaderRow headerRow = new StringHeaderRow();
		headerRow.add("NO",5).add("AID", 30).add("IID", 30).add("CREATE", 10).add("EVENT", 7).add("STATE", 7);
		table.setHeader(headerRow);
		
		List<IAdapterInstanceContainer> aicList = aim.getAdapterContainerList();
		IAdapterInstanceContainer aic;
		IAdapterInstanceEvent aie;
		IInstanceObj info;
		String state=null;
		String event=null;
		for(int i=0, s=aicList.size();i<s;i++){
			aic = aicList.get(i);
			aie = aic.getLastEvent();
			info = aie.getInstanceInfo();
			if(aie.getEventType()==1){event="CREATE";}
			else if(aie.getEventType()==2){event="INITIALIZE";}
			else if(aie.getEventType()==4){	event="START";}
			else if(aie.getEventType()==8){	event="SUSPEND";}
			else if(aie.getEventType()==16){event="STOP";	}
			else if(aie.getEventType()==32){event="DISPOSE";}
			
			if(aie.getStateType()==1){state="CREATE";}
			else if(aie.getStateType()==2){state="BEGIN";}
			else if(aie.getStateType()==4){state="DOING";}
			else if(aie.getStateType()==8){state="COMPLETE";}
			else if(aie.getStateType()==16){state="END";}
			
			table.addRowData(String.valueOf(i+1), info.getAdtId(), info.getInsId(), yyMMddhhMMss.format(new Date(aie.getCreatedTime())),event, state);
		}
		table.simplePrint();
		cStack.addShellCommand(new ShellCommand("ilist", null));
	}
	
	@Override
	public String getExecuterName() {
		return "AimCommand";
	}

	@Override
	public Dictionary<String, String> getProps() {
		return this.props;
	}

	@Override
	public boolean isExecuteable(ShellCommand cmd) {
		if(funcList.contains(cmd.getCommand())){
			return true;
		}
		return false;
	}

	@Override
	public void execute(ShellCommand cmd) {
		String cmdTxt = cmd.getCommand();
		switch(cmdTxt){
		case "istart":
			istart(cmd.getParams().get(0));
			break;
		case "istop":
			istop(cmd.getParams().get(0));
			break;
		case "isuspend":
			isuspend(cmd.getParams().get(0));
			break;
		case "ilist":
			ilist();
			break;
		}
	}
}
