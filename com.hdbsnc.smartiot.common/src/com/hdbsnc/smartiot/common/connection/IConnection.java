package com.hdbsnc.smartiot.common.connection;

import java.io.IOException;
import java.net.Socket;

public interface IConnection {

	public String getRemoteAddress();
	
	public long getCreationTime();
	
	public long getLastAccessedTime();
	
	public boolean isConnected();
	
	public void disconnect() throws IOException;
	
	public void write(byte[] bytes) throws IOException;
	
	public void write(String packets) throws IOException;
	
	public byte[] read() throws IOException;
	
//	public Socket socket() throws IOException;
}
