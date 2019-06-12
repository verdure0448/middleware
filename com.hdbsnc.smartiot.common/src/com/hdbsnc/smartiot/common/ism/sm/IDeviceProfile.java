package com.hdbsnc.smartiot.common.ism.sm;

public interface IDeviceProfile {

	public String getDevicePoolId();
	public String getDevicePoolNm();
	public String getDevicePoolRemark();
	
	public String getDeviceNm();
	public String getSessionTimeout();
	/**
	 * 장치 사용여부 
	 * 
	 * @return "true" | "false"
	 */
	public String getIsUse();
	public String getIp();
	public String getPort();
	public String getLatitude();
	public String getLongitude();
	public String getRemark();
}
