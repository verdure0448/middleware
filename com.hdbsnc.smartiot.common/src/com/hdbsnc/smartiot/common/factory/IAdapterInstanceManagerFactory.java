package com.hdbsnc.smartiot.common.factory;

import com.hdbsnc.smartiot.common.ICommonService;
import com.hdbsnc.smartiot.common.aim.IAdapterInstanceManager;
import com.hdbsnc.smartiot.common.am.IAdapterManager;
import com.hdbsnc.smartiot.common.ism.IIntegratedSessionManager;
import com.hdbsnc.smartiot.common.pm.IProfileManager;

public interface IAdapterInstanceManagerFactory {

	IAdapterInstanceManager createAIM(ICommonService service, IAdapterManager am1, IProfileManager pm2, IIntegratedSessionManager ism);
}
