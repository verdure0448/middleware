package com.hdbsnc.smartiot.pdm.am.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.apache.commons.codec.binary.Base64;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Version;

import com.hdbsnc.smartiot.common.ICommonService;
import com.hdbsnc.smartiot.common.am.AmException;
import com.hdbsnc.smartiot.common.am.IAdapter;
import com.hdbsnc.smartiot.common.am.IAdapterEvent;
import com.hdbsnc.smartiot.common.am.IAdapterEventListener;
import com.hdbsnc.smartiot.common.am.IAdapterManager;
import com.hdbsnc.smartiot.common.am.IAdapterManifest;
import com.hdbsnc.smartiot.common.em.AbstractEventProducer;
import com.hdbsnc.smartiot.common.em.EventProducerDisposedException;
import com.hdbsnc.smartiot.common.em.event.IEvent;
import com.hdbsnc.smartiot.common.factory.IAdapterFactory;
import com.hdbsnc.smartiot.util.logger.Log;

public class Am extends AbstractEventProducer implements IAdapterManager{

	private Queue<IEvent> eventQueue;
	private Map<String, IAdapter> adapterMap;
	private Map<String, IAdapterManifest> adapterManifestMap;
	private List<IAdapterEventListener> adapterListenerList;
	private ICommonService service;
	private Log logger;
	private Map<String, String> config;
	private BundleContext ctx;
	private String smartiot_home_path;
	
	public Am(ICommonService service, Map<String, String> config, BundleContext ctx){
		this.config = config;
		this.smartiot_home_path = config.get("smartiot.home");
		this.ctx = ctx;
		adapterMap = new Hashtable<String, IAdapter>();
		adapterManifestMap = new Hashtable<String, IAdapterManifest>();
		adapterListenerList = new ArrayList<IAdapterEventListener>();
		this.service = service;
		this.eventQueue = new LinkedList<IEvent>();
		this.logger = service.getLogger().logger("AM");
	}
	
	@Override
	public void regAdapter(IAdapter adapter) throws AmException{
		IAdapterManifest manifest = adapter.getManifest();
		if(manifest==null) {
			AmException e = new AmException("manifest 정보가 없습니다.");
			broadcastEvent(IAdapterEvent.REG_FAIL_EVENT, adapter, e);
			throw e;
		}
		IAdapterFactory factory = adapter.getFactory(service);
		if(factory==null) {
			AmException e = new AmException("factory 인스턴스가 없습니다.");
			broadcastEvent(IAdapterEvent.REG_FAIL_EVENT, adapter, e);
			throw e;
		}
		
		adapterMap.put(adapter.getAdapterId(), adapter);
		adapterManifestMap.put(adapter.getAdapterId(), adapter.getManifest());
		broadcastEvent(IAdapterEvent.REG_EVENT, adapter, null);
		logger.info("아답터 등록 완료.("+adapter.getAdapterId()+")");
	}

	@Override
	public void unregAdapter(IAdapter adapter) throws AmException{
		adapterMap.remove(adapter.getAdapterId());
		adapterManifestMap.remove(adapter.getAdapterId());
		broadcastEvent(IAdapterEvent.UNREG_EVENT, adapter, null);
		logger.info("아답터 제거 완료.("+adapter.getAdapterId()+")");
	}

	@Override
	public IAdapter getAdapter(String profileId) {
		return adapterMap.get(profileId);
	}

	@Override
	public IAdapterManifest getAdapterManifest(String profileId) {
		IAdapter adapter = adapterMap.get(profileId);
		if(adapter!=null){
			return adapter.getManifest();
		}
		return null;
	}

	@Override
	public List<IAdapter> getAdapterList() {
		return new ArrayList<IAdapter>(adapterMap.values());
	}

	@Override
	public List<IAdapterManifest> getAdapterManifest() {
		return new ArrayList<IAdapterManifest>(adapterManifestMap.values());
	}

	@Override
	public void addAdapterListener(IAdapterEventListener listener) {
		synchronized(adapterListenerList){
			adapterListenerList.add(listener);
		}
	}

	@Override
	public void removeAdapterListener(IAdapterEventListener listener) {
		synchronized(adapterListenerList){
			adapterListenerList.remove(listener);
		}
	}

	@Override
	public List<IAdapterEventListener> getAdapterListenrer() {
		return adapterListenerList;
	}

	@Override
	public void removeAllAdapterListener() {
		synchronized(adapterListenerList){
			adapterListenerList.clear();
		}
	}
	
	private void broadcastEvent(int eventType, IAdapter adapter, Exception e){
		synchronized(adapterListenerList){
			AmEvent newEvent = new AmEvent(eventType, adapter.getManifest(), e);
			try {
				pushEvent(newEvent);
			} catch (EventProducerDisposedException e1) {
				logger.err(e1);
			}
			IAdapterEventListener listener;
			for(int i=0, s=adapterListenerList.size();i<s;i++){
				listener = adapterListenerList.get(i);
				listener.onChangeAdapter(newEvent);
			}
			
		}
	}

	@Override
	public int getModuleValue() {
		return IEvent.MODULE_PDM; //PDM
	}

	@Override
	public IEvent consumeFirstEvent() {
		synchronized(eventQueue){
			return eventQueue.poll();
		}
	}

	@Override
	public boolean isEmpty() {
		return eventQueue.isEmpty();
	}

	@Override
	protected void putEvent(IEvent event) {
		synchronized(eventQueue){
			eventQueue.offer(event);
		}
	}
	
	public void installAdapter(String adapterUri) throws AmException{
		try {
			Bundle bundle = ctx.installBundle(adapterUri);
			if(bundle!=null) bundle.start();
		} catch (BundleException e) {
			logger.err(e);
			throw new AmException(e);
		}
		logger.info(adapterUri+" 아답터가 설치되었습니다.");
	}
	
	public void installAdapterByLocal(String adapterFileName) throws AmException{
		String fullPath = "file:///"+smartiot_home_path + File.separator+"adapters"+File.separator+adapterFileName;
		Bundle bundle = null;
		try {
			bundle = ctx.installBundle(fullPath);
			if(bundle!=null) {
				Dictionary<String, String> dic = bundle.getHeaders();
				if(dic==null) {
					bundle.uninstall();
					throw new AmException("정상적인 아답터 파일이 아니므로 설치를 할 수 없습니다.");
				}
				String value = dic.get("SmartIoT-AdapterProfile");
				if(value==null || value.equals("")){
					bundle.uninstall();
					throw new AmException("아답터 MANIFEST.MF에 SmartIoT-AdapterProfile이 존재하지 않아 설치를 할 수 없습니다.");
				}
				
				bundle.uninstall();
				bundle = ctx.installBundle(fullPath);
				if(bundle.getState() == bundle.ACTIVE){
					bundle.stop();
				}
				bundle.start();
			}
		} catch (BundleException e) {
			logger.err(e);
			if(bundle!=null)
				try {
					bundle.uninstall();
				} catch (BundleException e1) {
				}
			throw new AmException("정상적인 아답터 파일이 아니므로 설치를 할 수 없습니다.");
		}
		logger.info(adapterFileName+" 아답터가 설치되었습니다.");
		
	}
	
	public void installAdapter(String fileName, String fileSize, ByteBuffer base64File) throws AmException{
		byte[] decodeFile = Base64.decodeBase64(base64File.array());
		String savePath = smartiot_home_path + File.separator+"adapters";
		File dir = new File(savePath);
		File file = new File(savePath+File.separator+fileName);
		if(!dir.exists()){
			dir.mkdirs();
		}
		if(file.exists()){
			file.delete(); //파일이 존재하면 지운다. 
			logger.info(fileName+" 아답터가 "+savePath+" 위치에 이미 존재하여 덮어씁니다.");
		}
		try {
			file.createNewFile();
			FileOutputStream out = new FileOutputStream(file);
			out.write(decodeFile);
			out.close();
		} catch (IOException e) {
			logger.err(e);
			throw new AmException(e);
		}
		logger.info(fileName+" 아답터가 "+savePath+" 위치에 저장되었습니다.");
		installAdapterByLocal(fileName);
	}
	
	/**
	 * AID는 Bundle의 심볼릭네임(파일확장자 제외 이름)과 일치해야 uninstall이 된다. 
	 * @param aid
	 * @throws BundleException 
	 * @throws AmException 
	 */
	public void uninstallAdapter(String adapterId) throws AmException{
		IAdapter uninstallAdapter = this.adapterMap.get(adapterId);
		if(uninstallAdapter==null){
			logger.info(adapterId+" 아답터가 존재하지 않아 제거되지 않았습니다.");
			return;
		}
		Bundle[] bundleArray = ctx.getBundles();
		Bundle bundle;
		for(int i=0;i<bundleArray.length;i++){
			bundle = bundleArray[i];
			Version ver = bundle.getVersion();
			String makeId = bundle.getSymbolicName()+"_"+ver.getMajor()+"."+ver.getMinor()+"."+ver.getMicro();
//			System.out.println("symbolcName_major.minor.micro: "+makeId);
			if(makeId.equals(adapterId)){
				try {
					bundle.uninstall();
				} catch (BundleException e) {
					logger.err(e);
					throw new AmException(e);
				}
				this.unregAdapter(uninstallAdapter);
				logger.info(adapterId+" 아답터가 제거되었습니다. 서비스 중인 인스턴스에는 영향을 주지 않습니다.");
				return;
			}
		}
		throw new AmException(adapterId+" 설치된 아답터를 찾지 못해 제거되지 않았습니다.");
	}
}
