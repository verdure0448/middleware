package com.hdbsnc.smartiot.ecm.xml;

import java.util.ArrayList;
import java.util.List;

import com.hdbsnc.smartiot.common.ecm.profile.IEventContextProfile;
import com.hdbsnc.smartiot.common.ecm.profile.IEventHandlerProfile;

public class XmlEventContextProfile implements IEventContextProfile{
	
	public static final String ATT_ISAUTOSTART = "isautostart";
	public static final String TAG_EVENTCONTEXT = "eventcontext";
	public static final String TAG_EID = "eid";
	public static final String TAG_NAME = "name";
	public static final String TAG_TYPE = "type";
	public static final String TAG_REMARK = "remark";
	public static final String TAG_DEVICELIST = "devicelist";
	public static final String TAG_PATTERNLIST = "patternlist";
	public static final String TAG_EVENTHANDLERLIST = "eventhandlerlist";
	
	
	String isAutoStart = null;
	String eid = null;
	String name = null;
	String type = null;
	String remark = null;
	List<String> deviceList = null;
	List<Pattern> patternList = null;
	List<XmlEventHandlerProfile> eventHandlerList = null;
	
	public String isAutoStart(){
		return this.isAutoStart;
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getType(){
		return this.type;
	}
	
	public String getRemark(){
		return this.remark;
	}
	
	public List<Pattern> getPatternList(){
		return this.patternList;
	}
	
	public List<XmlEventHandlerProfile> getEventHandlerList(){
		return this.eventHandlerList;
	}
	
	public String printToString(){
		StringBuilder sb = new StringBuilder();
		sb.append("[EventContext]\n");
		sb.append("isAutoStart=");
		if(isAutoStart!=null) sb.append(isAutoStart);
		sb.append("\n");
		sb.append("eid=");
		if(eid!=null) sb.append(eid);
		sb.append("\n");
		sb.append("name=");
		if(name!=null) sb.append(name);
		sb.append("\n");
		sb.append("type=");
		if(type!=null) sb.append(type);
		sb.append("\n");
		sb.append("remark=");
		if(remark!=null) sb.append(remark);
		sb.append("\n");
		if(patternList!=null){ for(Pattern p: patternList){ p.print(sb); } }
		if(eventHandlerList!=null){ for(XmlEventHandlerProfile eh: eventHandlerList){ eh.print(sb); } }
		return sb.toString();
	}

	@Override
	public String getEID() {
		return this.eid;
	}
	
	public String getIsAutoStart(){
		return this.isAutoStart;
	}

	@Override
	public List<String> getDIDlist() {
		return this.deviceList;
	}
	
	public List<String> getPatternStringList(){
		List<String> result = new ArrayList<String>();
		for(Pattern p: this.patternList){
			result.add(p.getPattern());
		}
		return result;
	}

	@Override
	public List<IEventHandlerProfile> getEventHandlerProfileList() {
		List<IEventHandlerProfile> result = new ArrayList<IEventHandlerProfile>();
		for(XmlEventHandlerProfile p: eventHandlerList){
			result.add(p);
		}
		return result;
	}
}
