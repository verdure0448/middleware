package com.hdbsnc.smartiot.pdm.ap;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import com.hdbsnc.smartiot.common.am.AmException;
import com.hdbsnc.smartiot.common.am.IAdapter;
import com.hdbsnc.smartiot.common.am.IAdapterManager;
import com.hdbsnc.smartiot.common.am.IAdapterManifest;


public abstract class AbstractAdapter implements IAdapter{
	private IAdapterManifest manifest;
	private BundleContext ctx;
	private IAdapterManager am;
	
	public AbstractAdapter(BundleContext ctx){
		this.manifest = new InnerManifest(ctx.getBundle().getHeaders());
		this.ctx = ctx;
	}
	
	public void registe(){
		final IAdapter self = this;
		new Thread(new Runnable(){

			@Override
			public void run() {
				ServiceTracker amTracker = new ServiceTracker(ctx, IAdapterManager.class.getName(), null);
				amTracker.open();
				try {
					am = (IAdapterManager) amTracker.waitForService(0);
					am.regAdapter(self);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				} catch (AmException e) {
					System.err.println("아답터 등록 실패.");
					e.printStackTrace();
				}
				amTracker.close();				
			}
			
		}).start();
		
	}
	
	public void unregiste(){
		try {
			if(am==null){
				ServiceTracker amTracker = new ServiceTracker(ctx, IAdapterManager.class.getName(), null);
				amTracker.open();
				am = (IAdapterManager) amTracker.getService();
				if(am!=null) am.unregAdapter(this);	
			}
			System.out.println(this.getAdapterId()+ " 아답터 등록제거 완료.");
		} catch (AmException e) {
			System.err.println("아답터 제거 실패.");
			e.printStackTrace();
		}
	}
	
	
	@Override
	public String getAdapterId() {
		return manifest.getAdapterId();
	}

	@Override
	public IAdapterManifest getManifest() {
		return manifest;
	}
	
	private class InnerManifest implements IAdapterManifest{
		private Map<String, String> profile;
		private List<String> functions;
		private List<String> attributes;
		private List<String> attInits;
		private static final String HEADER_SMARTIOT_ADAPTER_PROFILE ="SmartIoT-AdapterProfile";
		private static final String HEADER_SMARTIOT_ADAPTER_ATTRIBUTE ="SmartIoT-AdapterAttribute";
		private static final String HEADER_SMARTIOT_ADAPTER_FUNCTION ="SmartIoT-AdapterFunction";
		private static final String HEADER_SMARTIOT_ADAPTER_INITIALIZE = "SmartIoT-Adapter-Initialize";
		
		static final String AP_ID = "adapter.id";
		static final String AP_NAME = "adapter.name";
		static final String AP_KIND = "adapter.kind";
		static final String AP_TYPE = "adapter.type";
		static final String AP_DEFAULTDEVID ="default.device.id";
		static final String AP_SESSIONTIMEOUT = "session.timeout";
		static final String AP_INITDEVSTATUS = "init.device.status";
		static final String AP_IP = "ip";
		static final String AP_PORT = "port";
		static final String AP_LATITUDE = "latitude";
		static final String AP_LONGITUDE = "longitude";
		static final String AP_SELFID = "self.id";
		static final String AP_SELFPW = "self.pw";
		static final String AP_REMARK = "remark";
		static final String AP_DESCRIPTION = "description";
		static final String AP_HYPERLINK = "hyperlink";

		InnerManifest(Dictionary<String, String> manifest){
			profile = createDic(createStringList(manifest, HEADER_SMARTIOT_ADAPTER_PROFILE));
			functions = createStringList(manifest, HEADER_SMARTIOT_ADAPTER_FUNCTION);
			attributes = createStringList(manifest, HEADER_SMARTIOT_ADAPTER_ATTRIBUTE);
			attInits = createStringList(manifest, HEADER_SMARTIOT_ADAPTER_INITIALIZE);
			
			
//			System.out.println("["+HEADER_SMARTIOT_ADAPTER_PROFILE+"]");
//			Set<Entry<String,String>> entrySet = profile.entrySet();
//			for(Entry<String, String> entry: entrySet){
//				System.out.println(" "+entry.getKey()+" : "+entry.getValue());
//			}
//			System.out.println("["+HEADER_SMARTIOT_ADAPTER_ATTRIBUTE+"]");
//			for(String name: attributes){
//				System.out.println(" "+name);
//			}
//			System.out.println("["+HEADER_SMARTIOT_ADAPTER_FUNCTION+"]");
//			for(String name: functions){
//				System.out.println(" "+name);
//			}
		}
		
		private List<String> createStringList(Dictionary<String, String> dic, String headerName){
			String txt = dic.get(headerName);
			List<String> list = new ArrayList<String>();
			if (txt != null) {
				StringTokenizer st = new StringTokenizer(txt, ",");
				while (st.hasMoreTokens()) {
					list.add(st.nextToken().trim());
				}
			}
			return list;
		}
		
		private Map<String,String> createDic(List<String> list){
			HashMap<String, String> dic = new HashMap<String, String>();
			String[] temp;
			for(String txt: list){
				temp = txt.split("=");
				if(temp.length==0){
					continue;
				}else if(temp.length==1){
					dic.put(temp[0], "");
				}else if(temp.length>=2){
					dic.put(temp[0], temp[1]);
				}
			}
			return dic;
		}

		@Override
		public String getAdapterId() {
			return profile.get(AP_ID);
		}

		@Override
		public String getAdapterName() {
			return profile.get(AP_NAME);
		}

		@Override
		public String getKind() {
			return profile.get(AP_KIND);
		}

		@Override
		public String getType() {
			return profile.get(AP_TYPE);
		}

		@Override
		public String getDefaultDevId() {
			return profile.get(AP_DEFAULTDEVID);
		}

		@Override
		public String getSessionTimeout() {
			return profile.getOrDefault(AP_SESSIONTIMEOUT, "0");
		}

		@Override
		public String getInitDevStatus() {
			return profile.get(AP_INITDEVSTATUS);
		}

		@Override
		public String getIp() {
			return profile.get(AP_IP);
		}

		@Override
		public String getPort() {
			return profile.get(AP_PORT);
		}

		@Override
		public String getLatitude() {
			return profile.get(AP_LATITUDE);
		}

		@Override
		public String getLongitude() {
			return profile.get(AP_LONGITUDE);
		}

		@Override
		public String getSelfId() {
			return profile.get(AP_SELFID);
		}

		@Override
		public String getSelfPw() {
			return profile.get(AP_SELFPW);
		}

		@Override
		public String getRemark() {
			return profile.get(AP_REMARK);
		}

		@Override
		public List<String> getAttributes() {
			return this.attributes;
		}

		@Override
		public List<String> getFunctions() {
			return this.functions;
		}
		
		@Override
		public List<String> getInitializeAttributes(){
			return this.attInits;
		}

		@Override
		public String getDescription() {
			return profile.getOrDefault(AP_DESCRIPTION, "아답터 설명이 없습니다.\n정확한 사용을 위해 개발사에 문의하세요.");
		}

		@Override
		public String getHyperLink() {
			return profile.getOrDefault(AP_HYPERLINK, "아답터 매뉴얼 링크 없음.");
		}
	}
}
