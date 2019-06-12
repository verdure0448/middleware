package com.hdbsnc.smartiot.common.context;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

public interface IContext {
//push용 주석 
	public static final String CONTENTTYPE_NONE = "none";
	public static final String CONTENTTYPE_JSON = "json";
	public static final String CONTENTTYPE_BINNERY = "bin";
	
	public static final String TRANSMISSION_REQUEST = "request";
	public static final String TRANSMISSION_REQUEST1 = "req";
	public static final String TRANSMISSION_RESPONSE = "response";
	public static final String TRANSMISSION_RESPONSE1 = "res";
	public static final String TRANSMISSION_EVENT = "event";
	public static final String TRANSMISSION_EVENT1 = "evt";
	
//	public String getAdapterInstanceId();
	
	public String getSID(); //세션키, 세션키:시퀀스, 요청자측의 식별자 등이 올수있음.
	
	public String getSPort();
	
	public String getTID(); 
	
	public String getTPort();
	
	public List<String> getPaths();
	
	public String getFullPath();
	
	public Map<String, String> getParams();
	
	public String getContentType();
	
	public ByteBuffer getContent();
	
	public boolean containsContent();
	
	public String getTransmission();
}
