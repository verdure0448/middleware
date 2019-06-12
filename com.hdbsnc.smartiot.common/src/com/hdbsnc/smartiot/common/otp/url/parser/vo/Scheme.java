package com.hdbsnc.smartiot.common.otp.url.parser.vo;

import java.util.ArrayList;
import java.util.List;

import com.hdbsnc.smartiot.common.otp.url.IScheme;

public class Scheme implements IScheme{

	public static final Scheme EMPTY = new Scheme(new ArrayList<String>(0));

	public static final Scheme OTP = getOtp();
	
	private List<String> schemePaths;
	
	public Scheme(List<String> paths) {
		this.schemePaths = paths;
	}
	
	private static Scheme getOtp(){
		ArrayList<String> temp = new ArrayList<String>(1);
		temp.add("otp");
		return new Scheme(temp);
	}
	
	public Scheme(){
		this(new ArrayList<String>());
	}
	
	@Override
	public int getLength() {
		return schemePaths.size();
	}

	@Override
	public List<String> getSchemePaths() {
		return schemePaths;
	}

	@Override
	public String getSchemePath(int index) {
		return schemePaths.get(index);
	}

	@Override
	public String getFirstSchemePath() {
		return schemePaths.get(0);
	}

	@Override
	public String getLastSchemePath() {
		if(schemePaths.size()==0) return null;
		return schemePaths.get(schemePaths.size()-1);
	}
	
	public void addPath(String path){
		this.schemePaths.add(path);
	}
	
	public void print(){
		System.out.println("[Scheme]");
		for(int i=0;i<this.getLength();i++){
			System.out.println(this.schemePaths.get(i));
		}
	}

}
