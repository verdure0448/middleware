package com.hdbsnc.smartiot.pdm.am.impl;

import com.hdbsnc.smartiot.common.am.IAdapterEvent;
import com.hdbsnc.smartiot.common.am.IAdapterManifest;
import com.hdbsnc.smartiot.common.em.event.IEvent;
import com.hdbsnc.smartiot.em.impl.DefaultSystemEvent;

public class AmEvent  extends DefaultSystemEvent implements IAdapterEvent{
	private int eventType;
	private Exception e;
	private IAdapterManifest manifest;
	
	public AmEvent(int eventType){
		super();
		this.eventType = eventType;
		int eventID = 0;
		switch(eventType){
		case IAdapterEvent.REG_EVENT:
			eventID = IEvent.MODULE_PDM | IEvent.EVENT_ADAPTER_LIFECYCLE | IEvent.TYPE_INSTALL | IEvent.STATE_SUCCESS;
			break;
		case IAdapterEvent.REG_FAIL_EVENT:
			eventID = IEvent.MODULE_PDM | IEvent.EVENT_ADAPTER_LIFECYCLE | IEvent.TYPE_INSTALL | IEvent.STATE_FAIL;
			break;
		case IAdapterEvent.UNREG_EVENT:
			eventID = IEvent.MODULE_PDM | IEvent.EVENT_ADAPTER_LIFECYCLE | IEvent.TYPE_UNINSTALL | IEvent.STATE_SUCCESS;
			break;
		case IAdapterEvent.UNREG_FAIL_EVENT:
			eventID = IEvent.MODULE_PDM | IEvent.EVENT_ADAPTER_LIFECYCLE | IEvent.TYPE_UNINSTALL | IEvent.STATE_FAIL;
			break;
		case IAdapterEvent.ERROR_EVENT:
			eventID = IEvent.MODULE_PDM | IEvent.EVENT_ADAPTER_LIFECYCLE | IEvent.ALL | IEvent.STATE_ERROR;
			break;
		}
		setEventID(eventID);
	}
	
	public AmEvent(int eventType, IAdapterManifest manifest, Exception e){
		this(eventType);
		this.manifest = manifest;
		this.e = e;
		if(e!=null){
			super.contents = e;
		}else{
			super.contents = manifest;
		}
	}
	
	public AmEvent(int eventType, IAdapterManifest manifest){
		this(eventType);
		this.manifest = manifest;
		this.e = null;
		super.contents = manifest;
	}
	
	public AmEvent(int eventType, Exception e){
		this(eventType);
		this.manifest = null;
		this.e = e;
		super.contents = e;
	}
	

	@Override
	public long getCreatedTime() {
		return super.eventTime;
	}

	@Override
	public int getEventType() {
		return this.eventType;
	}

	@Override
	public Exception getException() {
		return e;
	}

	@Override
	public IAdapterManifest getManifest() {
		return manifest;
	}
}
