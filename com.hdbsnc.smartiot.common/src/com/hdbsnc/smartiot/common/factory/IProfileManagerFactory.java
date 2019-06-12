package com.hdbsnc.smartiot.common.factory;

import java.util.Map;

import com.hdbsnc.smartiot.common.ICommonService;
import com.hdbsnc.smartiot.common.pm.IProfileManager;

public interface IProfileManagerFactory {

	IProfileManager createPm(ICommonService commonService, Map<String, String> config);
}
