package com.hdbsnc.smartiot.common.otp.url.parser.vo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.hdbsnc.smartiot.common.otp.url.IQuery;

public class Query implements IQuery{

	public static final Query EMPTY = new Query(new HashMap<String,String>(0));
	
	private Map<String,String> params;
	
	public Query(){
		this.params = new HashMap<String,String>();
	}
	
	public Query(Map<String,String> params){
		this.params = params;
	}
	
	public void put(String key, String value){
		this.params.put(key, value);
	}
	
	public void set(Map<String, String> querys){
		this.params = querys;
	}
	
	@Override
	public boolean isEmpty() {
		return this.params.isEmpty();
	}

	@Override
	public int getLength() {
		return this.params.size();
	}

	@Override
	public Map<String, String> getParameters() {
		return this.params;
	}

	@Override
	public String getParamValue(String parameterName) {
		return this.params.get(parameterName);
	}
	
	public void print(){
		System.out.println("[Query]");
		Iterator<Entry<String,String>> iter = params.entrySet().iterator();
		Entry<String, String> temp;
		while(iter.hasNext()){
			temp = iter.next();
			System.out.println(temp.getKey()+" "+temp.getValue());
		}
	}

	@Override
	public Set<String> getParamNames() {
		return params.keySet();
	}

}
