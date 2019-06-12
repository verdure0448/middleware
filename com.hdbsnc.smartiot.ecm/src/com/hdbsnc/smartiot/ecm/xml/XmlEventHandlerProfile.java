package com.hdbsnc.smartiot.ecm.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.hdbsnc.smartiot.common.ecm.profile.IEventHandlerProfile;

public class XmlEventHandlerProfile implements IEventHandlerProfile{
	
	public static final String TAG_EVENTHANDLER = "eventhandler";
	public static final String ATT_SEQ = "seq";
	public static final String TAG_EHID = "ehid";
	public static final String TAG_NAME = "name";
	public static final String TAG_TYPE = "type";
	public static final String TAG_REMARK = "remark";
	public static final String TAG_PARAMETERLIST = "parameterlist";
	public static final String ATT_NAME = "name";
	public static final String ATT_MEMORYTYPE = "memory.type";
	public static final String ATT_PATHEXTENTION = "path.extention";
	public static final String ATT_IP = "ip";
	public static final String ATT_PORT = "port";
	public static final String ATT_DATABASENAME = "database.name";
	public static final String ATT_USER = "user";
	public static final String ATT_PASSWORD = "password";
	
	String seq;
	String ehid;
	String name;
	String type;
	String remark;
	Map<String, String> parameters;
	
	public String getSeq(){
		return this.seq;
	}
	
	public String getEhid(){
		return this.ehid;
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
	
	public Map<String, String> getParameters(){
		return this.parameters;
	}
	
	public void print(StringBuilder sb){
		sb.append("[EventHandler]\n");
		sb.append("seq=");
		if(seq!=null) sb.append(seq);
		sb.append("\n");
		sb.append("ehid=");
		if(ehid!=null) sb.append(ehid);
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
		if(parameters!=null){
			sb.append("[Parameters]\n");
			for(Entry<String, String> entry: this.parameters.entrySet()){
				sb.append(entry.getKey()).append("=").append(entry.getValue()).append("\n");
			}
		}
	}

	@Override
	public String getEHID() {
		return this.ehid;
	}

	@Override
	public List<IParameter> getParameterList() {
		if(parameters==null) return null;
		List<IParameter> result = new ArrayList<IParameter>();
		for(Entry<String, String> entry: this.parameters.entrySet()){
			result.add(new DefaultParameter(entry.getKey(), entry.getValue()));
		}
		return result;
	}
	
	@Override
	public Map<String, String> getParameterMap() {
		return this.parameters;
	}
	
	class DefaultParameter implements IParameter{
		private String name;
		private String value;
		private DefaultParameter(String name, String value){
			this.name = name;
			this.value = value;
		}

		@Override
		public String getName() {
			return this.name;
		}

		@Override
		public String getValue() {
			return this.value;
		}
	}

	
}
