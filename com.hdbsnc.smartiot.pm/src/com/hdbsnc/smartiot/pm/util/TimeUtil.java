package com.hdbsnc.smartiot.pm.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class TimeUtil {

	static SimpleDateFormat formatter = new java.text.SimpleDateFormat(
			"yyyyMMddHHmmss");
	
	static SimpleDateFormat formatter2 = new java.text.SimpleDateFormat(
			"yyyy/MM/dd HH:mm:ss");
	static TimeZone tz = TimeZone.getTimeZone("Asia/Seoul");
	
	public static String getYYYYMMddHHss() {
		formatter.setTimeZone(tz);
		return formatter.format(new Date());
	}
	
	
	public static String getYYYYMMddHHss2(long time) {
		formatter2.setTimeZone(tz);
		return formatter2.format(new Date(time));
	}
	
	public static String changeFormat(String yyyyMMddHHmmss){

		StringBuilder sb = new StringBuilder();				
		if(yyyyMMddHHmmss.length()>3){
			sb.append(yyyyMMddHHmmss.substring(0,4));
		}if(yyyyMMddHHmmss.length()>5){
			sb.append("/").append(yyyyMMddHHmmss.substring(4,6));			
		}if(yyyyMMddHHmmss.length()>7){
			sb.append("/").append(yyyyMMddHHmmss.substring(6,8));			
		}if(yyyyMMddHHmmss.length()>9){
			sb.append(" ").append(yyyyMMddHHmmss.substring(8,10));			
		}if(yyyyMMddHHmmss.length()>11){
			sb.append(":").append(yyyyMMddHHmmss.substring(10,12));			
		}if(yyyyMMddHHmmss.length()>13){
			sb.append(":").append(yyyyMMddHHmmss.substring(12,14));			
		}
		return sb.toString();
	}
}
