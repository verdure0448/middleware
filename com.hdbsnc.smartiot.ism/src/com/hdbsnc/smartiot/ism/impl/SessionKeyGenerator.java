package com.hdbsnc.smartiot.ism.impl;

import org.apache.commons.codec.binary.Base64;


@Deprecated
public class SessionKeyGenerator {
	
	private long count = 0;
	private long lastGenerated;
	private HashCodeBuilder builder;
	
	public SessionKeyGenerator(int initialNonZeroOddNumber, int multiplierNonZeroOddNumber){
		builder = new HashCodeBuilder(initialNonZeroOddNumber, multiplierNonZeroOddNumber);
	}
	
	public SessionKeyGenerator(){
		this(3,17);
	}
	
	public synchronized String generateSessionKey(){
		if(++count==Long.MAX_VALUE) count = 0; // long �ִ밪�� �����ϸ� count�� �ʱ�ȭ. 9223372036854775807�� �α����ϸ� �ʱ�ȭ �Ǵ°Ŵ�.
		
		this.lastGenerated = System.currentTimeMillis();
		int value = builder.append(count).append(lastGenerated).toHashCode();
		return Base64.encodeBase64String(String.valueOf(value).getBytes());
	}
	
	public long count(){
		return this.count;
	}
	
	public long getLastGeneratedMs(){
		return this.lastGenerated;
	}
}
