package com.hdbsnc.smartiot.adapter.mb.mc.bin;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import com.hdbsnc.smartiot.common.ICommonService;
import com.hdbsnc.smartiot.common.am.AmException;
import com.hdbsnc.smartiot.common.am.IAdapter;
import com.hdbsnc.smartiot.common.am.IAdapterManager;
import com.hdbsnc.smartiot.common.em.IEventManager;
import com.hdbsnc.smartiot.common.factory.IAdapterFactory;
import com.hdbsnc.smartiot.common.pm.IProfileManager;
import com.hdbsnc.smartiot.pdm.ap.AbstractAdapter;

public class MitsubishiQSeriesMCAdapter extends AbstractAdapter{

	private IProfileManager pm;
	private IAdapterManager am;
	private IEventManager em;
	private BundleContext ctx;
	
	public MitsubishiQSeriesMCAdapter(BundleContext ctx) {
		super(ctx);
		this.ctx=ctx;
	}

	@Override
	public IAdapterFactory getFactory(ICommonService service) {
		return new MitsubishiQSeriesMCAdapterFactory(service,em,pm);
	}
	

	@Override
	public void registe() {
		final IAdapter self = this;
		new Thread(new Runnable(){
			@Override
			public void run() {
				ServiceTracker amTracker = new ServiceTracker(ctx, IAdapterManager.class.getName(), null);
				amTracker.open();
				ServiceTracker pmTracker = new ServiceTracker(ctx, IProfileManager.class.getName(), null);
				pmTracker.open();
				ServiceTracker emTracker = new ServiceTracker(ctx, IEventManager.class.getName(), null);
				emTracker.open();				
				try {
					am = (IAdapterManager) amTracker.waitForService(0);
					pm = (IProfileManager) pmTracker.waitForService(0);
					em = (IEventManager)emTracker.waitForService(0);
					am.regAdapter(self);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				} catch (AmException e) {
					System.err.println("아답터 등록 실패.");
					e.printStackTrace();
				}
				amTracker.close();	
				pmTracker.close();
				emTracker.close();
			}
			
		}).start();
	}
}
