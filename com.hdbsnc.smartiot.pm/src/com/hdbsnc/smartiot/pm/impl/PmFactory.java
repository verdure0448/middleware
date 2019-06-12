package com.hdbsnc.smartiot.pm.impl;

import java.net.URL;
import java.util.Date;
import java.util.Map;

import com.hdbsnc.smartiot.common.ICommonService;
import com.hdbsnc.smartiot.common.factory.IProfileManagerFactory;
import com.hdbsnc.smartiot.common.pm.IProfileManager;
import com.hdbsnc.smartiot.pm.cache.NitroCacheManager;

public class PmFactory implements IProfileManagerFactory{

	private ProfileManagerImpl pm = null;
	
	@Override
	public IProfileManager createPm(ICommonService commonService, Map<String, String> config) {
		
		
		//config 객체에서 /smartiot/conf/config.conf 파일의 key 이름 그대로 value를 가져오면 됨. 
		
		if(pm==null){
			// 캐쉬 초기화
			URL propUrl = this.getClass().getResource("/pm_props.xml");
			
			NitroCacheManager.init(propUrl, config);
			// 캐쉬클린 스케줄러 등록
			int interval = Integer.parseInt(NitroCacheManager.getEnv("CacheCleanPeriod")) * 1000;
	
			commonService.getLogger().info("캐쉬정리 타임 간격(ms) : " + interval);
	
			commonService.getServicePool().addSchedule(new NitroCacheManager.ScheduledCacheClean(), new Date(), interval);

			this.pm = new ProfileManagerImpl();
		}
		return pm;
		
	}
	
	

}
