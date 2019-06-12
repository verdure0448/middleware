package com.hdbsnc.smartiot.common.context.handler;

import java.util.List;

public interface IDirectoryHandler extends IElementHandler{

	
	int getHandlerCount();
	List<IElementHandler> getHandlerList();
	IElementHandler getHandler(String name);
	void addHandler(IElementHandler handler);
	void removeHandler(IElementHandler handler);
	boolean contains(String name);
}
