package com.hdbsnc.smartiot.common.otp.url.parser.vo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.hdbsnc.smartiot.common.otp.url.IFragment;

public class Frag implements IFragment{

	public static final Frag EMPTY = new Frag(null);
	
	private Map<String, String> fragMap;
	
	public Frag(){
		this.fragMap = new HashMap<String, String>();
	}
	
	public Frag(Map<String, String> frags){
		this.fragMap = frags;
	}
	
	public void print(){
		System.out.println("[Frag]");
		if(isEmpty()){
			System.out.println("EMPTY");
			return;
		}
		Iterator<Entry<String,String>> iter = fragMap.entrySet().iterator();
		Entry<String, String> entry;
		while(iter.hasNext()){
			entry = iter.next();
			System.out.println(entry.getKey()+" "+entry.getValue());
		}
		
	}

	@Override
	public boolean isEmpty() {
		if(this.fragMap==null){
			return true;
		}else{
			return false;
		}
	}

	@Override
	public String getFragValue(String fragName) {
		if(fragMap!=null){
			return fragMap.get(fragName);
		}
		return null;
	}

	@Override
	public int getfragCount() {
		if(fragMap!=null){
			return fragMap.size();
		}
		return -1;
	}

	@Override
	public Map<String, String> getFrags() {
		return fragMap;
	}
	
	@Override
	public Set<String> getFragNames(){
		if(fragMap!=null){
			return fragMap.keySet();
		}
		return null;
	}
	
	public void putFrag(String name, String value){
		this.fragMap.put(name, value);
	}

}
