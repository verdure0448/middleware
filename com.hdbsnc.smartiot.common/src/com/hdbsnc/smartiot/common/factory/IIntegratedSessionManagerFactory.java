package com.hdbsnc.smartiot.common.factory;

import com.hdbsnc.smartiot.common.ICommonService;
import com.hdbsnc.smartiot.common.ism.IIntegratedSessionManager;
import com.hdbsnc.smartiot.common.pm.IProfileManager;

public interface IIntegratedSessionManagerFactory {

	IIntegratedSessionManager createISM(ICommonService commonService, IProfileManager pm, String serverId);
}
