package com.hdbsnc.smartiot.pdm.aim.impl;

import com.hdbsnc.smartiot.common.aim.IAdapterInstanceEvent;
import com.hdbsnc.smartiot.common.am.IAdapterManifest;
import com.hdbsnc.smartiot.common.em.event.IEvent;
import com.hdbsnc.smartiot.common.pm.vo.IInstanceObj;
import com.hdbsnc.smartiot.em.impl.DefaultSystemEvent;

public class AimEvent extends DefaultSystemEvent implements IAdapterInstanceEvent{

	private int eventType;
	private int eventState;
	private IAdapterManifest adapterProfile;
	private IInstanceObj instanceProfile;
	private Exception e;
	
	public AimEvent(int eventType, int eventState, IAdapterManifest adapterProfile, IInstanceObj instanceProfile){
		this(eventType, eventState, adapterProfile, instanceProfile, null);
	}
	
	public AimEvent(int eventType, int eventState, Exception e){
		this(eventType, eventState, null, null, e);
	}
	
	public AimEvent(int eventType, int eventState, IAdapterManifest adapterProfile, IInstanceObj instanceProfile, Exception e){
		super();
		this.eventType = eventType;
		this.eventState = eventState;
		this.adapterProfile = adapterProfile;
		this.instanceProfile = instanceProfile;
		this.e = e;
		super.contents = instanceProfile;
		
		int type = 0;
		switch(eventType){
		case IAdapterInstanceEvent.CREATE_EVENT:
			type = IEvent.TYPE_CREATE;
			break;
		case IAdapterInstanceEvent.INITIALIZE_EVENT:
			type = IEvent.TYPE_INITIALIZE;
			break;
		case IAdapterInstanceEvent.START_EVENT:
			type = IEvent.TYPE_START;
			break;
		case IAdapterInstanceEvent.STOP_EVENT:
			type = IEvent.TYPE_STOP;
			break;
		case IAdapterInstanceEvent.SUSPEND_EVENT:
			type = IEvent.TYPE_SUSPEND;
			break;
		case IAdapterInstanceEvent.DISPOSE_EVENT:
			type = IEvent.TYPE_DISPOSE;
			break;
		default:
			type = IEvent.ALL;
			break;
		}
		int state = 0;
		switch(eventState){
		case IAdapterInstanceEvent.CREATED_STATE:
			state = IEvent.STATE_CREATED;
			break;
		case IAdapterInstanceEvent.BEGIN_STATE:
			state = IEvent.STATE_BEGIN;
			break;
		case IAdapterInstanceEvent.DOING_STATE:
			state = IEvent.STATE_DOING;
			break;
		case IAdapterInstanceEvent.COMPLETED_STATE:
			state = IEvent.STATE_COMPLETED;
			break;
		case IAdapterInstanceEvent.END_STATE:
			state = IEvent.STATE_END;
			break;
		case IAdapterInstanceEvent.ERROR_STATE:
			state = IEvent.STATE_ERROR;
			break;
		default:
			state = IEvent.ALL;
		}
		setEventID(IEvent.MODULE_PDM|IEvent.EVENT_INSTANCE_LIFECYCLE|type|state);
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
	public int getStateType() {
		return this.eventState;
	}

	@Override
	public Exception getException() {
		return this.e;
	}

	@Override
	public IAdapterManifest getManifest() {
		return this.adapterProfile;
	}

	@Override
	public IInstanceObj getInstanceInfo() {
		return this.instanceProfile;
	}

}
