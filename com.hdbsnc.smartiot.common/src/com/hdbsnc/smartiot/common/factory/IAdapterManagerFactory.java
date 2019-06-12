package com.hdbsnc.smartiot.common.factory;

import java.util.Map;

import com.hdbsnc.smartiot.common.ICommonService;
import com.hdbsnc.smartiot.common.am.IAdapterManager;

public interface IAdapterManagerFactory {

	IAdapterManager createAM(ICommonService service, Map<String, String> config);
}
