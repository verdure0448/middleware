package com.hdbsnc.smartiot.common.am;

import java.util.List;
import java.util.Map;

public interface IAdapterManifest {

	String getAdapterId(); 		//기계가 사용하는 URL 형식의 유니크 아이
	String getAdapterName();	//사람이 볼 수 있는 이름.
	String getKind();
	String getType();
	String getDefaultDevId();
	String getSessionTimeout();
	String getInitDevStatus();
	String getIp();
	String getPort();
	String getLatitude();
	String getLongitude();
	String getSelfId();
	String getSelfPw();
	String getRemark();
	String getDescription();
	String getHyperLink();
	List<String> getAttributes();
	List<String> getFunctions();
	List<String> getInitializeAttributes();
}
