package com.hdbsnc.smartiot.ism;

import com.hdbsnc.smartiot.common.ICommonService;
import com.hdbsnc.smartiot.common.factory.IIntegratedSessionManagerFactory;
import com.hdbsnc.smartiot.common.ism.IIntegratedSessionManager;
import com.hdbsnc.smartiot.common.pm.IProfileManager;
import com.hdbsnc.smartiot.ism.impl.Ism;

public class IsmFactory implements IIntegratedSessionManagerFactory{

	@Override
	public IIntegratedSessionManager createISM(ICommonService commonService, IProfileManager pm, String serverId) {
		return new Ism(pm, commonService.getServicePool(), commonService.getLogger(), serverId);
	}

}
