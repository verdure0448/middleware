package com.hdbsnc.smartiot.common.otp.url.parser;

import com.hdbsnc.smartiot.common.otp.url.parser.vo.Frag;
import com.hdbsnc.smartiot.common.otp.url.parser.vo.HPart;
import com.hdbsnc.smartiot.common.otp.url.parser.vo.Query;
import com.hdbsnc.smartiot.common.otp.url.parser.vo.Scheme;
import com.hdbsnc.smartiot.common.otp.url.parser.vo.Url;

public class UrlContext {
	
	public static final int STATE_SCHEME = 100;
	public static final int STATE_HPART = 200;
//	public static final int STATE_HPART_AUTHORITY = 300;
//	public static final int STATE_HPART_PATH = 400;
	public static final int STATE_QUERY = 500;
	public static final int STATE_FRAG = 600;
	public static final int STATE_FINISH = 700;
	
	public static final char EMPTY_CHAR = '\r';
	public static final String EMPTY_STRING = "";
	
	private String fullPacket;
	private StringBuilder buffer;
	private Url gscp = null;
	private int currentIndex = -1;
	private int state = STATE_SCHEME;
	
	
	public UrlContext(String packet){
		this.fullPacket = packet;
		this.buffer = new StringBuilder();
		//this.gscp = Gscp.createEmpty();
		this.gscp = new Url();
	}
	
	int getState(){
		return this.state;
	}
	
	void setState(int v){
		this.state = v;
	}
	
	String getFullString(){
		return this.fullPacket;
	}
	
	Url getResult(){
		return this.gscp;
	}
	
//	void setResult(Gscp gscp){
//		this.gscp = gscp;
//	}
	
	void setScheme(Scheme scheme){
		gscp.setScheme(scheme);
	}
	
	void setHPart(HPart hpart){
		gscp.setHPart(hpart);
	}
	
	void setQuery(Query query){
		gscp.setQuery(query);
	}
	
	void setFrag(Frag frag){
		gscp.setFragment(frag);
	}
	
	boolean hasNextChar(){
		return ( (fullPacket.length()-1)>=(currentIndex+1) );
	}
	
	char nextChar(){
		if( (fullPacket.length()-1)>=(currentIndex+1) ){
			char tempChar = fullPacket.charAt(++currentIndex);
			buffer.append(tempChar);
			return tempChar;
		}else{
			return EMPTY_CHAR; //���̻� ���� char�� ���� ��
		}
	}
	
	int getCurrentIndex(){
		return this.currentIndex;
	}
	
	char getCurrentChar(){
		if( (fullPacket.length()-1)>=(currentIndex) ){
			return fullPacket.charAt(currentIndex);
		}else{
			return EMPTY_CHAR; //���̻� ���� char�� ���� ��
		}
	}
	
	String getCurrentString(){
		int bufferIndex = buffer.length()-1;
		if(buffer.length()<1) {
			clearBuffer();
			return EMPTY_STRING;
		}
		String returnString;
		if(currentIndex!=fullPacket.length()-1){
			returnString = this.buffer.substring(0, bufferIndex);
		}else{
			char curChar = getCurrentChar();
			switch(curChar){
			case UrlParser.SEPARATOR_SLASH:
			case UrlParser.SEPARATOR_COLON:
			case UrlParser.SEPARATOR_QUESTION:
			case UrlParser.SEPARATOR_EQUAL:
			case UrlParser.SEPARATOR_AND:
			case UrlParser.SEPARATOR_AT:
			case UrlParser.SEPARATOR_SHARP:
				returnString = this.buffer.substring(0, bufferIndex);
				break;
			default:
				returnString = this.buffer.substring(0, buffer.length());	
			}
			
		}
		clearBuffer();
		returnString = UrlParser.decodeUTF8(returnString);
		return returnString;
	}
	
	String getCurrentStringByOriginal(){
		int bufferIndex = buffer.length()-1;
		if(buffer.length()<1) {
			clearBuffer();
			return EMPTY_STRING;
		}
		String returnString;
		if(currentIndex!=fullPacket.length()-1){
			returnString = this.buffer.substring(0, bufferIndex);
		}else{
			char curChar = getCurrentChar();
			switch(curChar){
			case UrlParser.SEPARATOR_SLASH:
			case UrlParser.SEPARATOR_COLON:
			case UrlParser.SEPARATOR_QUESTION:
			case UrlParser.SEPARATOR_EQUAL:
			case UrlParser.SEPARATOR_AND:
			case UrlParser.SEPARATOR_AT:
			case UrlParser.SEPARATOR_SHARP:
				returnString = this.buffer.substring(0, bufferIndex);
				break;
			default:
				returnString = this.buffer.substring(0, buffer.length());	
			}
			
		}
		clearBuffer();
		return returnString;
	}
	
	StringBuilder getBuffer(){
		return this.buffer;
	}
	
	void clearBuffer(){
		this.buffer.delete(0, buffer.length());
	}
}
