package com.hdbsnc.smartiot.common.otp;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IHeader {

	static final String TRANSMISSION1 = "transmission";
	static final String TRANSMISSION2 = "trans";
	static final String TRANSMISSION3 = "t";
	
	static final String CONTENT1 = "content";
	static final String CONTENT2 = "cont";
	static final String CONTENT3 = "c";
	
	static final String TRANS_RES = "res";
	static final String TRANS_END = "end";
	static final String TRANS_PUSH = "push";
	
	static final String CONT_URL = "url";
	static final String CONT_JSON = "json";
	static final String CONT_XML = "xml";
	static final String CONT_HTML = "html";
	static final String CONT_TEXT = "text";
	static final String CONT_BIN = "bin";
	static final String CONT_OTP = "otp";
	
	String getSID(); //Self-Identifier: 자기자신의 장치식별자 혹은 세션키
	
	String getSPort(); //Self-Port: seqnuence 용도로 세션별로 필요없다면 생략가능하고, 생략된값은 기본이 0임  생략하면 세션키나 장치식별자:0이 붙어있는것과 같음.
	
	String getTID(); //Target-Identifier
	
	String getTPort(); //Target-Port
	
	String getFullPath();
	
	List<String> getPaths();
	
	String getParam(String key);
	
	String getTransmissionType();
	
	String getContentType();
	
	boolean hasContent();
	
	Map<String, String> getParams();
	
}
