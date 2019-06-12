package com.hdbsnc.smartiot.pdm;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

import com.hdbsnc.smartiot.common.aim.IAdapterContext;
import com.hdbsnc.smartiot.common.aim.IAdapterInstanceContainer;
import com.hdbsnc.smartiot.common.aim.IAdapterInstanceEvent;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.IContextCallback;
import com.hdbsnc.smartiot.common.context.IContextTracer;
import com.hdbsnc.smartiot.common.factory.IAdapterInstanceManagerFactory;
import com.hdbsnc.smartiot.common.factory.IAdapterManagerFactory;
import com.hdbsnc.smartiot.pdm.aim.impl.Aim;
import com.hdbsnc.smartiot.pdm.am.impl.Am;
import com.hdbsnc.smartiot.util.logger.Log;
import com.hdbsnc.smartiot.util.servicepool.ServicePool;

public class Bootstrap implements BundleActivator, CommandProvider{

	public static Log bootstrap_logger;
	private ServiceTracker logTracker;
	private ServiceTracker poolTracker;
	ServicePool pool;
	
	private ServiceRegistration amService;
	private ServiceRegistration aimService;
	private ServiceRegistration cmdService;
	private ServiceTracker pmTracker;
	private ServiceTracker ismTracker;
	private ServiceTracker emTracker;   
	
	private Am am;
	private Aim aim;
	
	private ServiceRegistration amFactoryService;
	private ServiceRegistration aimFactoryService;
	
	@Override
	public void start(BundleContext ctx) throws Exception {
//		logTracker = new ServiceTracker(ctx, Log.class.getName(), null);
//		logTracker.open();
//		bootstrap_logger = (Log) logTracker.getService();
//		bootstrap_logger = bootstrap_logger.logger("com.hdbsnc.smartiot.pdm");
//		
//		poolTracker = new ServiceTracker(ctx, ServicePool.class.getName(), null);
//		poolTracker.open();
//		pool = (ServicePool) poolTracker.getService();
//		
//		
//		
//		ICommonService mwService = new ICommonService(){
//
//			@Override
//			public ServicePool getServicePool() {
//				return pool;
//			}
//
//			@Override
//			public Log getLogger() {
//				return bootstrap_logger;
//			}
//
//			@Override
//			public IWebservicePool getWebservicePool() {
//				return null;
//			}
//			
//		};
//		
//		pmTracker = new ServiceTracker(ctx, IProfileManager.class.getName(), null);
//		pmTracker.open();
//		IProfileManager pm = (IProfileManager) pmTracker.getService();
//		
//		ismTracker = new ServiceTracker(ctx, IIntegratedSessionManager.class.getName(), null);
//		ismTracker.open();
//		IIntegratedSessionManager ism = (IIntegratedSessionManager) ismTracker.getService();
//		
//		
//		am = new Am(mwService);
//
//		aim = new Aim(am, pm, ism, mwService);
//		
//		amService = ctx.registerService(IAdapterManager.class.getName(), am, null);
//		aimService = ctx.registerService(IAdapterInstanceManager.class.getName(), aim, null);
//		
//		cmdService  = ctx.registerService(CommandProvider.class.getName(), this, null);
//		
//		emTracker = new ServiceTracker(ctx, IEventManager.class.getName(), null);
//		emTracker.open();
//		IEventManager em = (IEventManager) emTracker.getService();
//		em.addEventProducer(am);
//		em.addEventProducer(aim);
//		
//		bootstrap_logger.info("pdm 커맨드 서비스 등록 완료.");
		
		PdmFactory pdmFactory = new PdmFactory(ctx);
		amFactoryService = ctx.registerService(IAdapterManagerFactory.class.getName(), pdmFactory, null);
		System.out.println("registerService: "+IAdapterManagerFactory.class.getName());
		
		aimFactoryService = ctx.registerService(IAdapterInstanceManagerFactory.class.getName(), pdmFactory, null);
		System.out.println("registerService: "+IAdapterInstanceManagerFactory.class.getName());
	}

	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		
//		aimService.unregister();
//		amService.unregister();
//		cmdService.unregister();
//		
//		logTracker.close();
//		poolTracker.close();
//		pmTracker.close();
//		ismTracker.close();
		
		amFactoryService.unregister();
		aimFactoryService.unregister();
		
	}
	
	public void _instancelist(CommandInterpreter ci) throws Exception {
		List<IAdapterInstanceContainer> list = aim.getAdapterContainerList();
		IAdapterInstanceEvent e;
		IAdapterContext ctx;
		IAdapterInstanceContainer container;
		StringBuilder sb = new StringBuilder();
		for(int i=0, s=list.size();i<s;i++){
			container = list.get(i);
			ctx = container.getContext();
			e = container.getLastEvent();
			sb.append(i).append(" ");
			sb.append(e.getEventType()).append(" ");
			sb.append(e.getStateType()).append(" ");
			sb.append("aid:").append(ctx.getAdapterInstanceInfo().getAdtId()).append(" ");
			sb.append("iid:").append(ctx.getAdapterInstanceInfo().getInsId()).append(" ");
			sb.append("\n");
		}
		System.out.println(sb.toString());
	}
	
	public void _istart1(CommandInterpreter ci) throws Exception {
		String instanceId = ci.nextArgument();
//		if (instanceId == null) {
//			System.out.println("arg0 : instanceId");
//			return;
//		}
		
		aim.start("com.hdbsnc.smartiot.instance.test.1");
	}
	
	public void _istart2(CommandInterpreter ci) throws Exception {
		String instanceId = ci.nextArgument();
//		if (instanceId == null) {
//			System.out.println("arg0 : instanceId");
//			return;
//		}
		
		aim.start("com.hdbsnc.smartiot.instance.websocketapi.1");
	}
	
	public void _istart3(CommandInterpreter ci) throws Exception {
		String instanceId = ci.nextArgument();
//		if (instanceId == null) {
//			System.out.println("arg0 : instanceId");
//			return;
//		}
		
		aim.start("com.hdbsnc.smartiot.instance.lsis.xgtseries.office.1");
	}
	
	public void _istart4(CommandInterpreter ci) throws Exception {
		String instanceId = ci.nextArgument();
//		if (instanceId == null) {
//			System.out.println("arg0 : instanceId");
//			return;
//		}
		
		aim.start("com.hdbsnc.smartiot.instance.lsis.xgtseries.factory.1");
	}
	
	public void _istart5(CommandInterpreter ci) throws Exception {
		String instanceId = ci.nextArgument();
//		if (instanceId == null) {
//			System.out.println("arg0 : instanceId");
//			return;
//		}
		
		aim.start("com.hdbsnc.smartiot.instance.philips.hue.1");
	}
	
	public void _istart6(CommandInterpreter ci) throws Exception {
		String instanceId = ci.nextArgument();
//		if (instanceId == null) {
//			System.out.println("arg0 : instanceId");
//			return;
//		}
		
		aim.start("com.hdbsnc.smartiot.instance.philips.hue.2");
	}
	
	public void _istart(CommandInterpreter ci) throws Exception {
		String instanceId = ci.nextArgument();
//		if (instanceId == null) {
//			System.out.println("arg0 : instanceId");
//			return;
//		}
		
		aim.start(instanceId);
	}
	
	public void _istop(CommandInterpreter ci) throws Exception {
		String instanceId = ci.nextArgument();
		
		aim.stop(instanceId);
	}
	
	public void _istop1(CommandInterpreter ci) throws Exception {
		String instanceId = ci.nextArgument();
		
		aim.stop("com.hdbsnc.smartiot.instance.test.1");
	}
	
	public void _istop2(CommandInterpreter ci) throws Exception {
		String instanceId = ci.nextArgument();
		
		aim.stop("com.hdbsnc.smartiot.instance.websocketapi.1");
	}
	
	public void _istop3(CommandInterpreter ci) throws Exception {
		String instanceId = ci.nextArgument();
		
		aim.stop("com.hdbsnc.smartiot.instance.lsis.xgtseries.office.1");
	}
	
	public void _call1on(CommandInterpreter ci) throws Exception {
		InnerContext request = new InnerContext();
		request.sid=null;
		request.tid="com.hdbsnc.smartiot.lsis.xgtseries.office.1";
		request.paths = Arrays.asList("plc","light","room1");
		request.params = new HashMap<String, String>();
		request.params.put("update", "on");
		
		aim.handOverContext(request, new IContextCallback(){

			@Override
			public void responseSuccess(IContextTracer ctxTracer) {
				System.out.println("room1 on 응답 메시지가 왔음.");
				
			}

			@Override
			public void responseFail(IContextTracer ctxTracer) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}
	public void _call1off(CommandInterpreter ci) throws Exception {
		InnerContext request = new InnerContext();
		request.sid=null;
		request.tid="com.hdbsnc.smartiot.lsis.xgtseries.office.1";
		request.paths = Arrays.asList("plc","light","room1");
		request.params = new HashMap<String, String>();
		request.params.put("update", "off");
		
		aim.handOverContext(request, new IContextCallback(){

			@Override
			public void responseSuccess(IContextTracer ctxTracer) {
				System.out.println("room1 on 응답 메시지가 왔음.");
				
			}

			@Override
			public void responseFail(IContextTracer ctxTracer) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}
	
	public void _call(CommandInterpreter ci) throws Exception {
		InnerContext request = new InnerContext();
		request.sid=null;
		request.tid="com.hdbsnc.smartiot.lsis.xgtseries.office.1";
		request.paths = Arrays.asList("plc","light","room2");
		request.params = new HashMap<String, String>();
		request.params.put("update", "off");
		for(int i=0;i<20;i++){
			if(i%2==0) {
				request.params.put("update", "off");
			}
			else {
				request.params.put("update", "on");
			}
			aim.handOverContext(request, new IContextCallback(){

				@Override
				public void responseSuccess(IContextTracer ctxTracer) {
					System.out.println(ctxTracer.getResponseContext().getFullPath()+" : room1 on 응답 메시지가 왔음.");
					
				}

				@Override
				public void responseFail(IContextTracer ctxTracer) {
					// TODO Auto-generated method stub
					
				}
				
			});
			Thread.sleep(100);
		}
	}
	
	public void _getVoltage(CommandInterpreter ci) throws Exception {
		InnerContext request = new InnerContext();
		request.sid=null;
		request.tid="com.hdbsnc.smartiot.lsis.xgtseries.office.1";
		request.paths = Arrays.asList("plc","voltage");
		request.params = new HashMap<String, String>();
		
		aim.handOverContext(request, new IContextCallback(){

			@Override
			public void responseSuccess(IContextTracer ctxTracer) {
				IContext res = ctxTracer.getResponseContext();
				if(res.getPaths().contains("ack")){
					System.out.println("Voltage: "+res.getParams().get("read"));
				}else{
					System.out.println("Voltage Nack.");
				}
				
			}

			@Override
			public void responseFail(IContextTracer ctxTracer) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}
	
	public void _getTemperature(CommandInterpreter ci) throws Exception {
		InnerContext request = new InnerContext();
		request.sid=null;
		request.tid="com.hdbsnc.smartiot.lsis.xgtseries.factory.1";
		request.paths = Arrays.asList("plc","temperature");
		request.params = new HashMap<String, String>();
		
		aim.handOverContext(request, new IContextCallback(){

			@Override
			public void responseSuccess(IContextTracer ctxTracer) {
				IContext res = ctxTracer.getResponseContext();
				if(res.getPaths().contains("ack")){
					System.out.println("Temperature: "+res.getParams().get("read"));
				}else{
					System.out.println("Temperature Nack.");
				}
				
			}

			@Override
			public void responseFail(IContextTracer ctxTracer) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}
	
	public void _getIntensity(CommandInterpreter ci) throws Exception {
		InnerContext request = new InnerContext();
		request.sid=null;
		request.tid="com.hdbsnc.smartiot.lsis.xgtseries.office.1";
		request.paths = Arrays.asList("plc","intensity");
		request.params = new HashMap<String, String>();
		
		aim.handOverContext(request, new IContextCallback(){

			@Override
			public void responseSuccess(IContextTracer ctxTracer) {
				IContext res = ctxTracer.getResponseContext();
				if(res.getPaths().contains("ack")){
					System.out.println("intensity: "+res.getParams().get("read"));
				}else{
					System.out.println("intensity Nack.");
				}
				
			}

			@Override
			public void responseFail(IContextTracer ctxTracer) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}
	
	public void _light1On(CommandInterpreter ci) throws Exception {
		InnerContext request = new InnerContext();
		request.sid=null;
		request.tid="com.hdbsnc.smartiot.philips.hue.1";
		request.paths = Arrays.asList("lights","1","on");
		request.params = new HashMap<String, String>();
		
		aim.handOverContext(request, new IContextCallback(){

			@Override
			public void responseSuccess(IContextTracer ctxTracer) {
				IContext res = ctxTracer.getResponseContext();
				if(res.getPaths().contains("ack")){
					System.out.println("lights/1/on: "+res.getParams().get("read"));
				}else{
					System.out.println("lights/1/on Nack.");
				}
				
			}

			@Override
			public void responseFail(IContextTracer ctxTracer) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}
	
	public void _light1Hue(CommandInterpreter ci) throws Exception {
		String hueValue = ci.nextArgument();
		System.out.println("param value: "+hueValue);
		InnerContext request = new InnerContext();
		request.sid=null;
		request.tid="com.hdbsnc.smartiot.philips.hue.1";
		request.paths = Arrays.asList("lights","1","hue");
		request.params = new HashMap<String, String>();
		request.params.put("update", hueValue);
		aim.handOverContext(request, new IContextCallback(){

			@Override
			public void responseSuccess(IContextTracer ctxTracer) {
				IContext res = ctxTracer.getResponseContext();
				if(res.getPaths().contains("ack")){
					System.out.println("lights/1/hue: "+res.getParams().get("update"));
				}else{
					System.out.println("lights/1/hue Nack.");
				}
			}

			@Override
			public void responseFail(IContextTracer ctxTracer) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}

	@Override
	public String getHelp() {
		
		return null;
	}
	
	

}
