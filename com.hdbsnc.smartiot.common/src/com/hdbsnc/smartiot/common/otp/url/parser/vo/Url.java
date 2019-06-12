package com.hdbsnc.smartiot.common.otp.url.parser.vo;

import java.util.List;
import java.util.Map;

import com.hdbsnc.smartiot.common.otp.url.IUrl;

public class Url implements IUrl{
	
	private Scheme scheme;
	private HPart hpart;
	private Query query;
	private Frag fragment;
	
	public Url(Scheme v1, HPart v2, Query v3, Frag v4){
		this.scheme = v1;
		this.hpart = v2;
		this.query = v3;
		this.fragment = v4;
	}
	
	public Url(){
		this(new Scheme(), new HPart(), new Query(), new Frag());
	}
	
	public static Url createEmpty(){
		return new Url(null, null, null, null);
	}
	
	public static Url createOtp(List<String> paths, Map<String, String> querys){
		return new Url(Scheme.OTP, new HPart(new Auth(), paths), new Query(querys), new Frag());
	}
	
	public static Url createOtp(){
		return new Url(Scheme.OTP, new HPart(), new Query(), new Frag());
	}
	
	@Override
	public Scheme getScheme() {
		return this.scheme;
	}

	@Override
	public HPart getHierarchicalPart() {
		return this.hpart;
	}

	@Override
	public Query getQuery() {
		return this.query;
	}

	@Override
	public Frag getFragment() {
		return this.fragment;
	}
	
//	public Url setUserInfo(String userInfo){
//		this.hpart.setUserInfo(userInfo);
//		return this;
//	}
	
	public Url setUserInfo(String userInfo, String sequence){
		this.hpart.setUserInfo(userInfo);
		this.hpart.setSequence(sequence);
		return this;
	}

	public Url setHostInfo(String hostName, String hostPort){
		this.hpart.setHostname(hostName);
		if(hostPort!=null && !hostPort.equals("")) this.hpart.setHostPort(hostPort);
		return this;
	}

	public Url addPath(String path){
		this.hpart.addPath(path);
		return this;
	}
	
	public Url setPaths(List<String> paths){
		this.hpart.setPaths(paths);
		return this;
	}
	
	public Url addFrag(String fragName, String fragValue){
		this.fragment.putFrag(fragName, fragValue);
		return this;
	}
	
	public Url addSchemePath(String schemePath){
		this.scheme.addPath(schemePath);
		return this;
	}
	
	public Url addQuery(String paramName, String value){
		if(value==null)value = "";
		this.query.put(paramName, value);
		return this;
	}
	
	public Url setQuery(Map<String, String> querys){
		this.query.set(querys);
		return this;
	}

	public Url setScheme(Scheme v1){
		this.scheme = v1;
		return this;
	}
	
	public Url setHPart(HPart v2){
		this.hpart = v2;
		return this;
	}
	
	public Url setQuery(Query v3){
		this.query = v3;
		return this;
	}
	
	public Url setFragment(Frag v4){
		this.fragment = v4;
		return this;
	}
	
	public void print(){
		this.scheme.print();
		this.hpart.print();
		this.query.print();
		this.fragment.print();
	}
}
