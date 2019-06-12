package com.hdbsnc.smartiot.common.otp.url;

import java.util.Map;
import java.util.Set;

public interface IFragment {
	public static final String SEPARATOR ="#";
	
	public static final String TRANSMISSION1 	= "transmission";
	public static final String TRANSMISSION2 	= "trans";
	public static final String TRANSMISSION3 	= "t";
	public static final String CONTENT1 		= "content";
	public static final String CONTENT2 		= "cont";
	public static final String CONTENT3 		= "c";
	
	public static final String TRANS_TYPE_REQUEST = "req";
	public static final String TRANS_TYPE_RESPONSE = "res";
	public static final String TRANS_TYPE_EVENT = "evt";
	
	public static final String CONT_TYPE_NONE	= "none";
	public static final String CONT_TYPE_URL 	= "url";
	public static final String CONT_TYPE_JSON 	= "json";
	public static final String CONT_TYPE_XML 	= "xml";
	public static final String CONT_TYPE_HTML 	= "html";
	public static final String CONT_TYPE_TEXT 	= "txt";
	public static final String CONT_TYPE_BIN 	= "bin";
	
	boolean isEmpty();
	
//	String getFrag();
//	String getResponseType();
	
	String getFragValue(String fragName);
	
	int getfragCount();
	
	Set<String> getFragNames();
	
	Map<String, String> getFrags();
}
