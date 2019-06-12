package com.hdbsnc.smartiot.common.otp;

import java.nio.ByteBuffer;

public interface IContent {
	
	String getContentType();
	ByteBuffer getContent();
}
