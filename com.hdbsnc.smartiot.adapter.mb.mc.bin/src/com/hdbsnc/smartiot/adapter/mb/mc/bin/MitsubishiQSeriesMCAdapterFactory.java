package com.hdbsnc.smartiot.adapter.mb.mc.bin;

import com.hdbsnc.smartiot.common.ICommonService;
import com.hdbsnc.smartiot.common.aim.IAdapterInstance;
import com.hdbsnc.smartiot.common.em.IEventManager;
import com.hdbsnc.smartiot.common.factory.IAdapterFactory;
import com.hdbsnc.smartiot.common.pm.IProfileManager;

public class MitsubishiQSeriesMCAdapterFactory implements IAdapterFactory{

	private ICommonService service;
	private IEventManager em;
	private IProfileManager pm;
	
	public MitsubishiQSeriesMCAdapterFactory(ICommonService service,IEventManager em, IProfileManager pm){
		this.service = service;
		this.em=em;
		this.pm = pm;
	}
	
	@Override
	public IAdapterInstance createInstance() {
		return new MitsubishiQSeriesMCAdapterInstance(service,em,pm);
	}

}
