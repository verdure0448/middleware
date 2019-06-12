package com.hdbsnc.smartiot.common.otp;

/**
 * 버젼: 201509151154
 * @author hjs0317
 *
 */
public interface IOtp {

	IHeader getHeader();
	
	IContent getContent();
	
	boolean hasContent();
}
