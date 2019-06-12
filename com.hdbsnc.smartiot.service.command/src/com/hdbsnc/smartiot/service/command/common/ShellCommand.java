package com.hdbsnc.smartiot.service.command.common;

import java.util.List;

public class ShellCommand {

	private String cmd;
	private List<String> params = null;
	
	public ShellCommand(String cmd, List<String> params){
		this.cmd = cmd;
		this.params = params;
	}
	
	public String getCommand(){
		return this.cmd;
	}
	
	public List<String> getParams(){
		return this.params;
	}
	
	public boolean isEmptyParams(){
		if(this.params==null || (this.params!=null && this.params.size()==0)) return true;
		else return false;
	}
	
	public String toLineString(){
		if(params!=null && params.size()!=0){
			StringBuilder sb = new StringBuilder();
			sb.append(cmd);
			String temp;
			for(int i=0,s=params.size();i<s;i++){
				temp = params.get(i);
				sb.append(" ").append(temp);
			}
			return sb.toString();
		}else{
			return cmd;
		}
	}
}
