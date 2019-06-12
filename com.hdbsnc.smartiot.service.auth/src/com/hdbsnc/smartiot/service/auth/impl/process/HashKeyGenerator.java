package com.hdbsnc.smartiot.service.auth.impl.process;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashKeyGenerator {

	private MessageDigest gen;
	private long count = 0;
	private String serverId;
	
	public HashKeyGenerator(String algorithm, String serverId) throws NoSuchAlgorithmException {
		this.gen = MessageDigest.getInstance(algorithm);
		this.serverId = serverId;
	}
	
	//아답터에서 내부용으로 관리할 경우 사용
	//서버아이디+인스턴스아이디+카운트+현재MS
	public synchronized String generateKey(String instanceId){
		count++;
		StringBuffer sb = new StringBuffer(); 
		sb.append(serverId).append(instanceId).append(count).append(System.currentTimeMillis());
		gen.update(sb.toString().getBytes()); 
		sb.delete(0, sb.length());
		ByteBuffer byteData = ByteBuffer.wrap(gen.digest());
		for(int i = 0 ; i < byteData.limit() ; i++){
			sb.append(Integer.toString((byteData.get()&0xff) + 0x100, 16).substring(1));
		}
		return "sid-"+sb.toString();
	}
	
	//서버에서 전체적으로 관리할 경우 사용, 인스턴스아이디 없이 발급할 경우
	//서버아이디+카운트+현재MS
	public synchronized String generateKey(){
		count++;
		StringBuffer sb = new StringBuffer(); 
		sb.append(serverId).append(count).append(System.currentTimeMillis());
		gen.update(sb.toString().getBytes()); 
		sb.delete(0, sb.length());
		ByteBuffer byteData = ByteBuffer.wrap(gen.digest());
		for(int i = 0 ; i < byteData.limit() ; i++){
			sb.append(Integer.toString((byteData.get()&0xff) + 0x100, 16).substring(1));
		}
		return "sid-"+sb.toString();
	}
	
	public long count(){
		return count;
	}
}
