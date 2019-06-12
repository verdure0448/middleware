package com.hdbsnc.smartiot.common.am;

import java.nio.ByteBuffer;
import java.util.List;



public interface IAdapterManager {

	void regAdapter(IAdapter adapter) throws AmException;
	void unregAdapter(IAdapter adapter) throws AmException;

	IAdapter getAdapter(String adapterId);
	IAdapterManifest getAdapterManifest(String adapterId);
	
	List<IAdapter> getAdapterList();
	List<IAdapterManifest> getAdapterManifest();
	
	void addAdapterListener(IAdapterEventListener listener);
	void removeAdapterListener(IAdapterEventListener listener);
	void removeAllAdapterListener();
	List<IAdapterEventListener> getAdapterListenrer();
	
	void installAdapter(String fileName, String fileSize, ByteBuffer base64File) throws AmException;
	void installAdapter(String adapterUri) throws AmException;
	void installAdapterByLocal(String adapterFileName) throws AmException;
	void uninstallAdapter(String adapterId) throws AmException;
	
}
