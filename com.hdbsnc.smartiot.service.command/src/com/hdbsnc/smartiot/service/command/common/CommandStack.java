package com.hdbsnc.smartiot.service.command.common;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CommandStack {

	List<IShellCommandExecuter> executers;
	LinkedList<ShellCommand> cmdHistory;
	int historyMaxCount;
	
	public CommandStack(int historyMaxCount){
		this.historyMaxCount = historyMaxCount;
		this.executers = new ArrayList<IShellCommandExecuter>();
		this.cmdHistory = new LinkedList<ShellCommand>();
	}
	
	public void addExecuter(IShellCommandExecuter executer){
		synchronized(executers){
			this.executers.add(executer);
		}
	}
	
	public void addShellCommand(ShellCommand shellCmd){
		synchronized(cmdHistory){
			if(cmdHistory.size()==historyMaxCount){
				cmdHistory.removeLast();
				cmdHistory.addFirst(shellCmd);
			}else{
				cmdHistory.addFirst(shellCmd);
			}
		}
	}
	
	public void rcmd(String p1){
		if(p1.equals("clear") || p1.equals("c")){
			rcmdClear();
		}else if(p1.equals("list") || p1.equals("l")){
			rcmdList();
		}else{
			try{
				int intValue = Integer.parseInt(p1);
				rcmdExecute(intValue);
			}catch(NumberFormatException e){
				System.out.println(e.getMessage());
			}
		}
	}
	
	public void rcmd(){
		if(cmdHistory.size()>0){
			rcmdExecute(0);//가장 최근 실행했던 커맨드를 실행한다.
		}else{
			System.out.println("Command History size : 0");
		}
		
	}
	
	private void rcmdList(){
		StringBuilder sb = new StringBuilder();
		synchronized(cmdHistory){
			ShellCommand cmd;
			for(int i=0,s=cmdHistory.size();i<s;i++){
				cmd = cmdHistory.get(i);
				sb.append(i).append(" : ").append(cmd.toLineString()).append("\r\n");
			}
		}
		System.out.println(sb.toString());
	}
	
	private void rcmdClear(){
		synchronized(cmdHistory){
			cmdHistory.clear();
			System.out.println("Command History Cleared.("+cmdHistory.size()+")");
		}
	}
	
	private void rcmdExecute(int num){
		ShellCommand cmd = null;
		int size = 0;
		synchronized(cmdHistory){
			cmd = cmdHistory.get(num);
			size = cmdHistory.size();
		}
		if(cmd==null) System.out.println("No Executed.("+size+")");
		
		IShellCommandExecuter exec = null;
		boolean isExec = false;
		synchronized(executers){
			for(int i=0,s=executers.size();i<s;i++){
				exec = executers.get(i);
				if(exec.isExecuteable(cmd)){
					isExec = true;
					break;
				}
			}
		}
		if(isExec){
			exec.execute(cmd);
		}else{
			System.out.println("No Executed.");
		}
	}
}
