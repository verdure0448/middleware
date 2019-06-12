package com.hdbsnc.smartiot.common.context.handler;

public interface IElementHandler {

	static final int ROOT = 		0x00000000;
	static final int DIRECTORY = 	0x00000010;//(3)
	static final int FUNCTION = 	0x00000100;//(5)
	static final int ATTRIBUTE = 	0x00001000;
	
	IDirectoryHandler getParent();
	void setParent(IDirectoryHandler handler);
	
	String getName();
	int type();
	

}
