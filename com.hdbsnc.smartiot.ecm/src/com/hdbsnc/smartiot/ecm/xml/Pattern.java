package com.hdbsnc.smartiot.ecm.xml;

public class Pattern {

	String pattern;
	
	public String getPattern(){
		return pattern;
	}
	
	public void print(StringBuilder sb){
		sb.append("pattern=");
		if(pattern!=null) sb.append(pattern);
		sb.append("\n");
	}
}
