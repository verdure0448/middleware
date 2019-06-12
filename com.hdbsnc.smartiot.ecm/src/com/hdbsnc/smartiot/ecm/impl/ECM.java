package com.hdbsnc.smartiot.ecm.impl;

import java.util.List;

import com.hdbsnc.smartiot.common.ICommonService;
import com.hdbsnc.smartiot.common.aim.IAdapterInstanceManager;
import com.hdbsnc.smartiot.common.ecm.IEventContextManager;
import com.hdbsnc.smartiot.common.ecm.ec.IEClifeCycleEvent;
import com.hdbsnc.smartiot.common.ecm.ec.IEClifeCycleListener;
import com.hdbsnc.smartiot.common.ecm.ec.IEventContext;
import com.hdbsnc.smartiot.common.ecm.ec.IEventContextInstance;
import com.hdbsnc.smartiot.common.ecm.eh.IEventHandler;
import com.hdbsnc.smartiot.common.ecm.profile.IEventContextProfile;
import com.hdbsnc.smartiot.common.ecm.profile.IEventHandlerProfile;
import com.hdbsnc.smartiot.common.em.IEventManager;
import com.hdbsnc.smartiot.util.logger.Log;

public class ECM implements IEventContextManager{
	private ICommonService cs;
	private IAdapterInstanceManager aim;
	private IEventManager em;
	private ECRegistry ecRegistry;
	private EHRegistry ehRegistry;
	private Log log;
	
	public ECM(ICommonService cs, IAdapterInstanceManager aim, IEventManager em){
		this.cs = cs;
		this.aim = aim;
		this.em = em;
		this.ecRegistry = new ECRegistry();
		this.ehRegistry = new EHRegistry();
		this.log = cs.getLogger().logger("ECM");
	}
	
	private void autoStart(){
		List<IEventContext> ecList = ecRegistry.getEClist();
		List<IEventHandlerProfile> ehpList;
		String isAutoStart = null;
		IEClifeCycleEvent ecEvt;
		boolean isStart = false;
		for(IEventContext ec: ecList){
			ecEvt = ec.getLastLifeCycleEvent();
			if(ecEvt!=null) return; //최초 기동시에만 적용됨.
			isAutoStart = ec.getProfile().getIsAutoStart();
			if(isAutoStart!=null && isAutoStart.equals("true")){
				ehpList = ec.getProfile().getEventHandlerProfileList();
				for(IEventHandlerProfile ehp: ehpList){
					if(this.ehRegistry.containsEhid(ehp.getEHID())){
						isStart = true;
					}else{
						isStart = false;
						break;
					}
				}
				if(isStart==true) {
					IEventContextInstance eci = ec.getInstance();
					if(eci==null){
						try {
							start(ec.getEID());
						} catch (Exception e) {
							log.err(e);
						}
					}
				}
				
			}
		}
	}
	
	@Override
	public synchronized void regEventHandler(IEventHandler eh) {
		this.ehRegistry.add(eh);
		autoStart();
	}

	@Override
	public synchronized void unRegEventHandler(String ehid) {
		this.ehRegistry.remove(ehid);
	}

	@Override
	public void addEventContext(IEventContextProfile eventContextProfile) throws Exception{
		DefaultEC dec = new DefaultEC(cs, aim, eventContextProfile);
		this.ecRegistry.add(dec);
	}

	@Override
	public void removeEventContext(String eid) {
		this.ecRegistry.remove(eid);
	}
	
	public List<IEventContext> getEventContextList(){
		return this.ecRegistry.getEClist();
	}

	@Override
	public void start(String eid) throws Exception {
		DefaultEC ec = (DefaultEC) this.ecRegistry.getEventContext(eid);
		if(ec==null){
			//프로파일정의가 안되어 있다는 이야기임.
			throw new Exception("START failed. (존재하지 않는 EID 입니다.)");
		}else{
			IEClifeCycleEvent evt = ec.getLastLifeCycleEvent();
			if(evt==null || evt.getLifeCycleEventType()==IEClifeCycleEvent.TYPE_STOP ||  evt.getLifeCycleEventType()==IEClifeCycleEvent.TYPE_DISPOSE){
				DefaultECinstance ecic = new DefaultECinstance(ec, this.ehRegistry, em);
				ec.setECinstance(ecic);
				ec.addEClifeCycleListener(IEClifeCycleEvent.TYPE_INITIALIZE, IEClifeCycleEvent.STATE_END, true, new IEClifeCycleListener(){

					@Override
					public void onChangeLifeCycle(IEClifeCycleEvent evt) {
						ecic.onChangeInstance(IEClifeCycleEvent.TYPE_START);
					}
					
				});
				ecic.onChangeInstance(IEClifeCycleEvent.TYPE_INITIALIZE);
			}else{
				throw new Exception("START failed.(type:"+evt.getLifeCycleEventType()+", state:"+evt.getLifeCycleEventState()+")");
			}
		}
	}

	@Override
	public void stop(String eid) throws Exception {
		DefaultEC ec = (DefaultEC) this.ecRegistry.getEventContext(eid);
		if(ec==null){
			//프로파일정의가 안되어 있다는 이야기임.
			throw new Exception("STOP failed. (존재하지 않는 EID 입니다.)");
		}else{
			IEClifeCycleEvent evt = ec.getLastLifeCycleEvent();
			int evtType = evt.getLifeCycleEventType();
			if(evt!=null && (evtType==IEClifeCycleEvent.TYPE_START || evtType==IEClifeCycleEvent.TYPE_SUSPEND || evtType==IEClifeCycleEvent.TYPE_RESUME) ){
				DefaultECinstance ecic = (DefaultECinstance) ec.getInstance();
				ecic.onChangeInstance(IEClifeCycleEvent.TYPE_STOP);
			}else{
				throw new Exception("STOP failed.(type:"+evt.getLifeCycleEventType()+", state:"+evt.getLifeCycleEventState()+")");
			}
		}
	}

	@Override
	public void suspend(String eid) throws Exception {
		DefaultEC ec = (DefaultEC) this.ecRegistry.getEventContext(eid);
		if(ec==null){
			//프로파일정의가 안되어 있다는 이야기임.
			throw new Exception("SUSEPEND failed. (존재하지 않는 EID 입니다.)");
		}else{
			IEClifeCycleEvent evt = ec.getLastLifeCycleEvent();
			int evtType = evt.getLifeCycleEventType();
			if(evt!=null && (evtType==IEClifeCycleEvent.TYPE_START || evtType==IEClifeCycleEvent.TYPE_RESUME) ){
				DefaultECinstance ecic = (DefaultECinstance) ec.getInstance();
				ecic.onChangeInstance(IEClifeCycleEvent.TYPE_SUSPEND);
			}else{
				throw new Exception("SUSPEND failed.(type:"+evt.getLifeCycleEventType()+", state:"+evt.getLifeCycleEventState()+")");
			}
		}
		
	}

	@Override
	public void resume(String eid) throws Exception {
		DefaultEC ec = (DefaultEC) this.ecRegistry.getEventContext(eid);
		if(ec==null){
			//프로파일정의가 안되어 있다는 이야기임.
			throw new Exception("RESUME failed. (존재하지 않는 EID 입니다.)");
		}else{
			IEClifeCycleEvent evt = ec.getLastLifeCycleEvent();
			int evtType = evt.getLifeCycleEventType();
			if(evt!=null && (evtType==IEClifeCycleEvent.TYPE_SUSPEND) ){
				DefaultECinstance ecic = (DefaultECinstance) ec.getInstance();
				ecic.onChangeInstance(IEClifeCycleEvent.TYPE_RESUME);
			}else{
				throw new Exception("RESUME failed.(type:"+evt.getLifeCycleEventType()+", state:"+evt.getLifeCycleEventState()+")");
			}
		}
		
	}
	
	

}
