package com.hdbsnc.smartiot.common.otp.impl;

import java.util.List;
import java.util.Map;

import com.hdbsnc.smartiot.common.otp.IHeader;
import com.hdbsnc.smartiot.common.otp.url.IFragment;
import com.hdbsnc.smartiot.common.otp.url.parser.vo.Auth;
import com.hdbsnc.smartiot.common.otp.url.parser.vo.Frag;
import com.hdbsnc.smartiot.common.otp.url.parser.vo.Url;

public class Header implements IHeader{

	private Url url;
	
	public Header(Url url){
		this.url = url;
	}
	
	public Url getUrl(){
		return this.url;
	}
	
	@Override
	public String getSID() {
		Auth auth = url.getHierarchicalPart().getAuthentication();
		if(auth!=null){
			return auth.getUserInfo();
		}
		return null;
	}
	
	@Override
	public String getSPort(){
		Auth auth = url.getHierarchicalPart().getAuthentication();
		if(auth!=null){
			return auth.getSequence();
		}
		return null;
	}

	@Override
	public String getTID() {
		Auth auth = url.getHierarchicalPart().getAuthentication();
		if(auth!=null){
			return auth.getHostname();
		}
		return null;
	}
	
	@Override
	public String getTPort(){
		Auth auth = url.getHierarchicalPart().getAuthentication();
		if(auth!=null){
			return auth.getPort();
		}
		return null;
	}

	@Override
	public String getFullPath() {
		//   actuator/aircon   상대경로 임.
		List<String> paths = url.getHierarchicalPart().getPath();
		StringBuilder sb = new StringBuilder();
		String temp;
		int i, s;
		s = paths.size();
		for(i=0;i<s;i++){
			temp = paths.get(i);
			sb.append(temp);
			if(i!=(s-1)){
				sb.append("/");
			}
		}
		return sb.toString();
	}

	@Override
	public String getParam(String key) {
		return url.getQuery().getParamValue(key);
	}

	@Override
	public String getTransmissionType() {
		Frag frag = url.getFragment();
		String value = null;
		if(!frag.equals(Frag.EMPTY)){
			value = frag.getFragValue(IFragment.TRANSMISSION3);
			if(value==null){
				value = frag.getFragValue(IFragment.TRANSMISSION2);
				if(value==null){
					value = frag.getFragValue(IFragment.TRANSMISSION1);
					if(value==null){
						value = IFragment.TRANS_TYPE_REQUEST; //생략시 기본 값은 req
					}
				}
			}
		}
		return value;
	}

	@Override
	public String getContentType() {
		Frag frag = url.getFragment();
		String value = null;
		if(frag!=null && !frag.equals(Frag.EMPTY)){
			value = frag.getFragValue(IFragment.CONTENT3);
			if(value==null){
				value = frag.getFragValue(IFragment.CONTENT2);
				if(value==null){
					value = frag.getFragValue(IFragment.CONTENT1);
					if(value==null){
						value = IFragment.CONT_TYPE_NONE; //생략시 기본 값은 res
					}
				}
			}
		}
		return value;
	}
	
	@Override
	public boolean hasContent(){
		String value = getContentType();
		if(value==null || value.equals(IFragment.CONT_TYPE_NONE)){
			return false;
		}
		return true;
	}

	@Override
	public Map<String, String> getParams() {
		return url.getQuery().getParameters();
	}

	@Override
	public List<String> getPaths() {
		return url.getHierarchicalPart().getPath();
	}

}
