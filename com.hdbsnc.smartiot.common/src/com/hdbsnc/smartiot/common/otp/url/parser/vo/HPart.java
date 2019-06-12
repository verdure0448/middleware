package com.hdbsnc.smartiot.common.otp.url.parser.vo;

import java.util.ArrayList;
import java.util.List;

import com.hdbsnc.smartiot.common.otp.url.IHierarchicalPart;

public class HPart implements IHierarchicalPart{

	public static final HPart EMPTY = new HPart(Auth.EMPTY, new ArrayList<String>(0));
	
	private List<String> paths;
	private Auth auth;
	
	public HPart(Auth auth, List<String> paths){
		this.auth = auth;
		this.paths = paths;
	}
	
	public HPart(){
		this(new Auth(), new ArrayList<String>());
	}
	
	@Override
	public Auth getAuthentication() {
		return this.auth;
	}

	@Override
	public String getFirstPath() {
		return this.paths.get(0);
	}

	@Override
	public String getLastPath() {
		if(this.paths.size()!=0){
			return this.paths.get(this.paths.size()-1);
		}else{
			return this.paths.get(0);
		}
	}

	@Override
	public List<String> getPath() {
		return this.paths;
	}
	
	public void setPaths(List<String> paths){
		this.paths = paths;
	}
	
	public HPart addPath(String path){
		this.paths.add(path);
		return this;
	}

	public HPart setUserInfo(String userInfo){
		this.auth.setUserInfo(userInfo);
		return this;
	}
	
	public HPart setSequence(String seq){
		this.auth.setSequence(seq);
		return this;
	}
	
	public HPart setHostname(String hostname){
		this.auth.setHostname(hostname);
		return this;
	}
	
	public HPart setHostPort(String hostPort){
		this.auth.setPort(hostPort);
		return this;
	}
	

	public void print(){
		System.out.println("[HPart]");
		this.auth.print();
		System.out.println("[Path]");
		for(int i=0; i< this.paths.size();i++){
			System.out.println(this.paths.get(i));
		}
	}
}
