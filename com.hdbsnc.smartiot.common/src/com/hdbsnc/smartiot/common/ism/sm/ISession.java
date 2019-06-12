package com.hdbsnc.smartiot.common.ism.sm;

import java.util.Set;

import com.hdbsnc.smartiot.common.context.IContextTracerSupportBySeq;

public interface ISession extends IContextTracerSupportBySeq, ISessionState{


	
	public long getCreatedTime();
	
	public long getLastAccessedTime();
	
	public long sessionTimeout();
	
	public String getAdapterInstanceId(); //세션매니져를 사용하고 있는 아답터 인스턴스아이
	
	public String getSessionKey(); 	//유효기간이 있는 임시 세션
	
	public String getDeviceId();	//유일한 장치아이디
	
	public String getUserId();
	
	//속성은 아답터 개발자의 의도에 따라 휘발성값, 저장성값, 최신값 등이 저장될 수 있음.
	//속성값은 휘발성으로 PM에 저장되지 않음.
	public IAttributeMetaData getAttribute(String key);
	public IFunctionMetaData getFunction(String key);
	
	public Set<String> getAttributeKeys();
	public Set<String> getFunctionKeys();
	
	public boolean containsAttributeKey(String key);
	public boolean containsFunctionKey(String key);
	
	//속성값 변경 용도로 사용.
	public void setAttributeValue(String key, String value);

	
	public IDeviceProfile getDeviceProfile();
	public IUserProfile getUserProfile();
	
	/**
	 * 파일 분할전송이나 기타 세션내부적으로 휘발성 객체 저장공간으로 사용.
	 * @param key
	 * @return
	 */
	public Object getBuffers(String key);
	public void putBuffers(String key, Object obj);
	public Object removeBuffers(String key);
	public boolean containsKey(String key);
	public int buffersSize();
	public void clearBuffers();
	
	
	
}
