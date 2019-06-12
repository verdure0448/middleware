////////////////////////////////////////////////////////////////////
// Copyright 2015,2016 <hd-bsnc.com>. All rights reserved.
////////////////////////////////////////////////////////////////////
package com.hdbsnc.smartiot.pm;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

import com.hdbsnc.smartiot.common.factory.IAdapterManagerFactory;
import com.hdbsnc.smartiot.common.factory.IProfileManagerFactory;
import com.hdbsnc.smartiot.common.pm.IProfileManager;
import com.hdbsnc.smartiot.common.pm.vo.IInstanceObj;
import com.hdbsnc.smartiot.common.pm.vo.IModifyInstanceAttributeObj;
import com.hdbsnc.smartiot.common.pm.vo.IModifyInstanceFunctionObj;
import com.hdbsnc.smartiot.common.pm.vo.IModifyInstanceObj;
import com.hdbsnc.smartiot.common.pm.vo.IModifyUserPoolObj;
import com.hdbsnc.smartiot.pm.cache.NitroCacheManager;
import com.hdbsnc.smartiot.pm.impl.PmFactory;
import com.hdbsnc.smartiot.pm.impl.ProfileManagerImpl;
import com.hdbsnc.smartiot.pm.storage.db.SqliteDBManager;
import com.hdbsnc.smartiot.util.logger.Log;
import com.hdbsnc.smartiot.util.servicepool.ServicePool;

public class Bootstrap implements BundleActivator, CommandProvider {
	private static BundleContext context;
	public static Log LOG2;
	private ServiceTracker logTracker;
	private ServiceTracker poolTracker;
	ServicePool pool;
	private ServiceRegistration pmService;

	IProfileManager pm;
	
	private ServiceRegistration pmFactoryService;

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext bundleContext) throws Exception {
//		// 컨텍스트 설정
//		context = bundleContext;
//		logTracker = new ServiceTracker(bundleContext, Log.class.getName(), null);
//		logTracker.open();
//		LOG2 = (Log) logTracker.getService();
//		LOG2 = LOG2.logger("com.hdbsnc.smartiot.pm");
//
//		poolTracker = new ServiceTracker(bundleContext, ServicePool.class.getName(), null);
//		poolTracker.open();
//		pool = (ServicePool) poolTracker.getService();
//
//		// 캐쉬 초기화
//		URL propUrl = this.getClass().getResource("/pm_props.xml");
//		
////		String rootPath = System.getenv("SMARTIOT_HOME");
////		if(rootPath == null){
////			rootPath = "/home/smartiot";
////		}
////		URL propUrl = new File(rootPath + "/conf/pm_props.xml").toURI().toURL();
//		
//		NitroCacheManager.init(propUrl);
//		// 캐쉬클린 스케줄러 등록
//		int interval = Integer.parseInt(NitroCacheManager.getEnv("CacheCleanPeriod")) * 1000;
//
//		LOG2.info("캐쉬정리 타임 간격(ms) : " + interval);
//
//		pool.addSchedule(new NitroCacheManager.ScheduledCacheClean(), new Date(), interval);
//
//		pm = new ProfileManagerImpl();
//
//		pmService = bundleContext.registerService(IProfileManager.class.getName(), pm, null);

		// String bundleName = bundleContext.getBundle().getSymbolicName();
		// String bundlesInfo = System.getProperty("osgi.bundles");
		// int bundleNameStart = bundlesInfo.indexOf(bundleName);
		// int bundleNameEnd = bundleNameStart + bundleName.length();
		// String prependedBundlePath = bundlesInfo.substring(0, bundleNameEnd);
		// String prefix = "reference:file:";
		// int prefixPos = prependedBundlePath.lastIndexOf(prefix);
		// String bundlePath = prependedBundlePath;
		// if (prefixPos >= 0) bundlePath =
		// prependedBundlePath.substring(prefixPos + prefix.length(),
		// prependedBundlePath.length());
		// System.out.println("bundlePath=" + bundlePath);

		// String path1 = new File("./bundles").getAbsolutePath();
		// URL entry = this.getClass().getResource("SMARTIOT_ADAPTER.db");
		// if (entry != null)
		// {
		// URLConnection connection = entry.openConnection();
		// if (connection instanceof BundleURLConnection)
		// {
		// URL fileURL = ((BundleURLConnection) connection).getFileURL();
		// URI uri = new URI(fileURL.toString());
		// String path = new File(uri).getAbsolutePath();
		// System.out.printf("This is the path: %s\n", path);
		// }
		// }
		//
		// IAdapterInstanceObj voObj =
		// pm.getAdapterInstanceObj("bsnc.ins.test.1");
		// System.out.println("alterDate : " + voObj.getAlterDate());

//		bundleContext.registerService(CommandProvider.class.getName(), this, null);
//
//		LOG2.info("PM service registed");
		
		PmFactory pmFactory = new PmFactory();
		pmFactoryService = bundleContext.registerService(IProfileManagerFactory.class.getName(), pmFactory, null);
		System.out.println("registerService: "+IProfileManagerFactory.class.getName());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
//		dispose();
//		pmService.unregister();
//		logTracker.close();
//		pool.close();
//		poolTracker.close();
//		Bootstrap.context = null;
		pmFactoryService.unregister();
	}

	@Override
	public String getHelp() {

		return null;
	}

	/**
	 * Cache정보 출력
	 * 
	 * @param ci
	 * @throws Exception
	 */
	public void _cache(CommandInterpreter ci) throws Exception {
		NitroCacheManager.snapshot(ci.nextArgument());
	}

	private void dispose() {
		NitroCacheManager.cacheClear();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void _sql(CommandInterpreter ci) {

		SqliteDBManager dbMgr = new SqliteDBManager();
		try {
			dbMgr.selectQuery(ci.nextArgument());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}

	/**
	 * 인스턴스 테스트
	 * 
	 * @param ci
	 * @throws Exception
	 */
	public void _selectIns(CommandInterpreter ci) throws Exception {
		// PM 테스트 코드 작성
		String insId = ci.nextArgument();
		try {

			pm.getInstanceObj(insId);
		} catch (Exception e) {
			e.printStackTrace();
		}

		NitroCacheManager.snapshot("adt-ins");
	}

	public void _insertIns(CommandInterpreter ci) throws Exception {
		// PM 테스트 코드 작성
		try {

			IModifyInstanceObj mObj = pm.getModifyInstanceObj();
			mObj.insId("insId#").devPoolId("devPoolId#").adtId("adtId#").insNm("insNm#")
					.insKind("insKind#").insType("insType#").isUse("isUse#").sessionTimeout("sessionTimeout#")
					.initDevStatus("initDevStatus#").ip("ip#").port("port#").lat("lat#").lon("lon#").selfId("selfId#")
					.selfPw("selfPw#").remark("remark#").insert();

		} catch (Exception e) {
			e.printStackTrace();
		}

		NitroCacheManager.snapshot("adt-ins");
	}

	public void _updateIns(CommandInterpreter ci) throws Exception {
		// PM 테스트 코드 작성
		try {

			IModifyInstanceObj mObj = pm.getModifyInstanceObj();
			mObj.insId("insId#").devPoolId("devPoolId1").adtId("adtId1").insNm("insNm1")
					.insKind("insKind1").defaultDevId("defaultDevId1").insType("insType1").isUse("isUse1").sessionTimeout("sessionTimeout1")
					.initDevStatus("initDevStatus1").ip("ip1").port("port1").lat("lat1").lon("lon1").selfId("selfId1")
					.selfPw("selfPw1").remark("remark1").update();

		} catch (Exception e) {
			e.printStackTrace();
		}

		NitroCacheManager.snapshot("adt-ins");
	}

	public void _deleteIns(CommandInterpreter ci) throws Exception {
		try {

			IModifyInstanceObj mObj = pm.getModifyInstanceObj();
			mObj.insId("insId#").delete();

		} catch (Exception e) {
			e.printStackTrace();
		}

		NitroCacheManager.snapshot("adt-ins");
	}

	/**
	 * 인스턴스속성 테스트
	 * 
	 * @param ci
	 * @throws Exception
	 */
	public void _selectInsAtt(CommandInterpreter ci) throws Exception {
		// PM 테스트 코드 작성
		String insId = ci.nextArgument();
		String attPath = ci.nextArgument();
		try {
			pm.getInstanceAttributeObj(insId, attPath);
		} catch (Exception e) {
			e.printStackTrace();
		}

		NitroCacheManager.snapshot("ins-att");
	}

	public void _insertInsAtt(CommandInterpreter ci) throws Exception {
		// PM 테스트 코드 작성
		try {
			IModifyInstanceAttributeObj mObj = pm.getModifyInstanceAttributeObj();
			mObj.insId("insId#").key("key#").dsct("dsct#").value("value#").valueType("valueType#")
					.remark("remark#").insert();
		} catch (Exception e) {
			e.printStackTrace();
		}

		NitroCacheManager.snapshot("ins-att");
	}

	public void _updateInsAtt(CommandInterpreter ci) throws Exception {
		// PM 테스트 코드 작성
		try {
			IModifyInstanceAttributeObj mObj = pm.getModifyInstanceAttributeObj();
			mObj.insId("insId#").key("key#").dsct("dsct#").value("value1").valueType("valueType1")
					.remark("remark1").update();
		} catch (Exception e) {
			e.printStackTrace();
		}

		NitroCacheManager.snapshot("ins-att");
	}

	public void _deleteInsAtt(CommandInterpreter ci) throws Exception {
		// PM 테스트 코드 작성
		String insId = ci.nextArgument();
		String attPath = ci.nextArgument();
		try {
			IModifyInstanceAttributeObj mObj = pm.getModifyInstanceAttributeObj();
			mObj.insId(insId).key(attPath).delete();
		} catch (Exception e) {
			e.printStackTrace();
		}

		NitroCacheManager.snapshot("ins-att");
	}

	/**
	 * 인스턴스기능 테스트
	 * 
	 * @param ci
	 * @throws Exception
	 */
	public void _selectInsFunc(CommandInterpreter ci) throws Exception {
		// PM 테스트 코드 작성
		String insId = ci.nextArgument();
		String funcPath = ci.nextArgument();
		try {
			pm.getInstanceFunctionObj(insId, funcPath);
		} catch (Exception e) {
			e.printStackTrace();
		}

		NitroCacheManager.snapshot("ins-func");
	}

	public void _insertInsFunc(CommandInterpreter ci) throws Exception {
		// PM 테스트 코드 작성
		try {
			IModifyInstanceFunctionObj mObj = pm.getModifyInstanceFunctionObj();
			mObj.insId("insId#").key("key#").dsct("dsct#").contType("contType#").param1("param1#")
					.param2("param2#").param3("param3#").param4("param4#").param5("param5#").paramType1("paramType1#")
					.paramType2("paramType2#").paramType3("paramType3#").paramType4("paramType4#")
					.paramType5("paramType5#").remark("remark#").insert();
		} catch (Exception e) {
			e.printStackTrace();
		}

		NitroCacheManager.snapshot("ins-func");
	}

	public void _updateInsFunc(CommandInterpreter ci) throws Exception {
		// PM 테스트 코드 작성
		try {
			IModifyInstanceFunctionObj mObj = pm.getModifyInstanceFunctionObj();
			mObj.insId("insId#").key("key#").dsct("dsct1").contType("contType1").param1("param11")
					.param2("param21").param3("param31").param4("param41").param5("param51").paramType1("paramType11")
					.paramType2("paramType21").paramType3("paramType31").paramType4("paramType41")
					.paramType5("paramType51").remark("remark1").update();
		} catch (Exception e) {
			e.printStackTrace();
		}

		NitroCacheManager.snapshot("ins-func");
	}

	public void _deleteInsFunc(CommandInterpreter ci) throws Exception {
		// PM 테스트 코드 작성
		String insId = ci.nextArgument();
		String funcPath = ci.nextArgument();
		try {
			IModifyInstanceFunctionObj mObj = pm.getModifyInstanceFunctionObj();
			mObj.insId(insId).key(funcPath).delete();
		} catch (Exception e) {
			e.printStackTrace();
		}

		NitroCacheManager.snapshot("ins-func");
	}

	/**
	 * 유저풀 테스트
	 * 
	 * @param ci
	 * @throws Exception
	 */
	public void _selectUserPool(CommandInterpreter ci) throws Exception {
		String userPoolId = ci.nextArgument();
		try {
			pm.getUserPoolObj(userPoolId);
		} catch (Exception e) {
			e.printStackTrace();
		}

		NitroCacheManager.snapshot("user-pool");
	}
	
	public void _insertUserPool(CommandInterpreter ci) throws Exception {
		try {
			IModifyUserPoolObj mObj = pm.getModifyUserPoolObj();
			mObj.userPoolId("userPoolId#").userPoolNm("userPoolNm#").remark("remark#").insert();
		} catch (Exception e) {
			e.printStackTrace();
		}

		NitroCacheManager.snapshot("user-pool");
	}
	
	public void _updateUserPool(CommandInterpreter ci) throws Exception {
		try {
			IModifyUserPoolObj mObj = pm.getModifyUserPoolObj();
			mObj.userPoolId("userPoolId#").userPoolNm("userPoolNm1").remark("remark1").insert();
		} catch (Exception e) {
			e.printStackTrace();
		}

		NitroCacheManager.snapshot("user-pool");
	}
	
	public void _deleteUserPool(CommandInterpreter ci) throws Exception {
		try {
			IModifyUserPoolObj mObj = pm.getModifyUserPoolObj();
			mObj.userPoolId("userPoolId#").delete();
		} catch (Exception e) {
			e.printStackTrace();
		}

		NitroCacheManager.snapshot("user-pool");
	}

	public void _pmTest(CommandInterpreter ci) throws Exception {
//		IProfileManager mgr = new ProfileManagerImpl();
//		IInstanceObj obj = mgr.integrationAuth("user1", "1234", "com.hdbsnc.smartiot.test.1");// iid:com.hdbsnc.smartiot.instance.test.1
//		
//		if(obj != null){
//			System.out.println(obj.getInsId());
//		}
		IProfileManager mgr = new ProfileManagerImpl();
		IInstanceObj obj = mgr.searchInstanceByDevId("com.hdbsnc.smartiot.test.1");
		
		if(obj != null){
			System.out.println(obj.getInsId());
		}
	}
}
