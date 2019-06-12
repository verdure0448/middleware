package com.hdbsnc.smartiot.pdm.ap.instance;

import com.hdbsnc.smartiot.common.aim.IAdapterProcessorEvent;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.em.event.IEvent;
import com.hdbsnc.smartiot.em.impl.DefaultSystemEvent;

public class AdapterProcessorEvent extends DefaultSystemEvent implements IAdapterProcessorEvent{
	private int eventType;
	private int stateType;
	private Exception e = null;
	private IContext ctx = null;
	private String iid;
	
	public AdapterProcessorEvent(IAdapterProcessorEvent apEvent){
		this(apEvent.getIID(), apEvent.getAdapterProcessEventType(), apEvent.getAdapterProcessEventStateType(), apEvent.getContext(), apEvent.getException());
	}
	
	public AdapterProcessorEvent(String iid, int eventType, int stateType, IContext ctx, Exception e){
		this(iid, eventType, stateType);
		this.ctx =ctx;
		this.e = e;
		super.contents = ctx;
		
	}
	
	public AdapterProcessorEvent(String iid, int eventType, int stateType, IContext ctx){
		this(iid, eventType, stateType);
		this.ctx = ctx;
		this.e = null;
		super.contents = ctx;
	}
	
	public AdapterProcessorEvent(String iid, int eventType, int stateType){
		super();
		this.iid = iid;
		this.eventType = eventType;
		this.stateType = stateType;
		int type = 0;
		switch(eventType){
		case IAdapterProcessorEvent.TYPE_REQUEST:
			type = IEvent.TYPE_REQUEST;
			break;
		case IAdapterProcessorEvent.TYPE_RESPONSE:
			type = IEvent.TYPE_RESPONSE;
			break;
		case IAdapterProcessorEvent.TYPE_EVENT:
			type = IEvent.TYPE_EVENT;
			break;
		default:
			type = IEvent.ALL;
		}
		
		int state = 0;
		switch(stateType){
		case IAdapterProcessorEvent.STATE_BEGIN:
			state = IEvent.STATE_BEGIN;
			break;
		case IAdapterProcessorEvent.STATE_SUCCESS:
			state = IEvent.STATE_SUCCESS;
			break;
		case IAdapterProcessorEvent.STATE_FAIL:
			state = IEvent.STATE_FAIL;
			break;
		case IAdapterProcessorEvent.STATE_ERROR:
			state = IEvent.STATE_ERROR;
			break;
		case IAdapterProcessorEvent.STATE_INBOUND_TRANSFER:
			state = IEvent.STATE_TRANSFER;
			break;
		case IAdapterProcessorEvent.STATE_OUTBOUND_TRANSFER:
			state = IEvent.STATE_TRANSFER; //   구분할려면 수정 필요함. 
			break;
		default:
			state = IEvent.ALL;
		}
		setEventID(IEvent.MODULE_PDM|IEvent.EVENT_INSTANCE_PROCESSOR|type|state);
	}
	
	@Override
	public long getCreatedTime() {
		return super.eventTime;
	}

	@Override
	public int getAdapterProcessEventType() {
		return this.eventType;
	}

	@Override
	public int getAdapterProcessEventStateType() {
		return this.stateType;
	}

	@Override
	public Exception getException() {
		return this.e;
	}

	@Override
	public IContext getContext() {
		return this.ctx;
	}
	
	public String getIID(){
		return this.iid;
	}
	
}
