package com.hdbsnc.smartiot;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.hdbsnc.smartiot.common.CommonServiceImpl;
import com.hdbsnc.smartiot.common.factory.ICommonExceptionFactory;
import com.hdbsnc.smartiot.common.webserver.IWebservicePool;
import com.hdbsnc.smartiot.common.webserver.impl.WebservicePool;
import com.hdbsnc.smartiot.server.ServerInstance;
import com.hdbsnc.smartiot.util.logger.Log;
import com.hdbsnc.smartiot.util.logger.impl.logback.Logback;
import com.hdbsnc.smartiot.util.servicepool.ServicePool;
import com.hdbsnc.smartiot.util.servicepool.impl.DefaultServicePool;
/**
 * 구동 필수 번들 리스트
 * 1. javax.servlet
 * 2. org.apache.felix.gogo.command
 * 3. org.apache.felix.gogo.runtime
 * 4. org.apache.felix.gogo.shell
 * 5. org.eclipse.equinox.console
 * 6. org.eclipse.osgi
 * 7. org.eclipse.osgi.services
 * 
 * @author hjs0317
 *
 */
public class Bootstrap implements BundleActivator {

	private ServicePool servicePool;
	private WebservicePool wsPool;
	private Log LOG;

	private ServiceRegistration service;
	private ServiceRegistration webservice;
	private ServerInstance newServer;
	
//	static {
//		//로그백의 xml 위치를 지정한다.
//		System.setProperty("logback.configurationFile", System.getenv("SMARTIOT_HOME") + "/loggerProperties/logback.xml");
//	}
	
	public void start(BundleContext context) throws Exception {

		String home_path = System.getenv("SMARTIOT_HOME");
		
		if(home_path == null) {
			home_path = System.getenv("HOME");
			if(home_path==null){
				home_path = System.getenv("USERPROFILE");
				System.out.println("Running Windows Platform.");
			}else{
				System.out.println("Running Linux Platform.");
			}
			
			home_path = home_path+"//smartiot";
		}

		//logback property 위치 설정
		System.setProperty("logback.configurationFile", home_path + "/conf/loggerProperties/logback.xml");
		
		File propPath = new File(home_path+"//conf");
		File propFile = new File(home_path+"//conf//config.conf");
		
		Properties config = new Properties();
		if(propFile.exists()){
			System.out.println(propFile.getAbsolutePath() + " exists.");
			config.load(new FileInputStream(propFile));
			config.put("smartiot.home", home_path);//다른 모듈에서 사용할 수 있도록 설정정보에 런타임으로 삽입.
		}else{
			if(propPath.exists()){
				propFile.createNewFile();
			}else{
				propPath.mkdirs();
				propFile.createNewFile();
			}
			//파일이 없을 경우 기본 설정 파일 생성
			createDefaultProps(propFile);
			config.load(new FileInputStream(propFile));
			config.put("smartiot.home", home_path);//다른 모듈에서 사용할 수 있도록 설정정보에 런타임으로 삽입.
			System.out.println(propFile.getAbsolutePath() + " created.");
		}
		
		/**
		 * 로그서비스 초기화 
		 */
//		String logPath = config.getProperty("log.path", "//smartiot//logs");
//		String logFile = config.getProperty("log.file",".log");
//		String logBase = config.getProperty("log.base");
		
//		LOG = SimpleLog.createRootLog("root");
//		if(logBase==null || logBase.equals("")){
//			LOG.addLogHandler(new FileLogHandler(home_path+"//smartiot"+logPath, logFile));
//		}else{
//			LOG.addLogHandler(new FileLogHandler(logBase+"//smartiot"+logPath, logFile));
//		}
		
//		if(logBase==null || logBase.equals("")){
//			LOG.addLogHandler(new FileLogSplitHandler(home_path+"//smartiot"+logPath, logFile));
//		}else{
//			LOG.addLogHandler(new FileLogSplitHandler(logBase+"//smartiot"+logPath, logFile));
//		}
//
//		LOG.addLogHandler(new ConsoleLogHandler());
//		LOG.initializeLogHandlers();
//		LOG.setInfoWarnErrDebugVisible(true, true, true, true);
//		loggerService = context.registerService(Log.class.getName(), LOG, null);
		
		LOG = (Logback) new Logback().logger(this.getClass());
		//로그백 서비스 등록
		context.registerService(Log.class.getName(), LOG, null);
		LOG.info("Log service registed");
		
		/**
		 * 설정값 출력 
		 */
		Enumeration prop_names = config.propertyNames();
		while(prop_names.hasMoreElements()) {//키가 있는 동안
            String prop_name = (String) prop_names.nextElement(); //key 객체 받기
            String property = config.getProperty(prop_name); //key에 대한 value값 받기
//            LOG.info(prop_name + " = "+property);
        }	
		
		
		
		String initThreadCount = config.getProperty("tpool.initthreadcount", "20");
		String maxThreadCount = config.getProperty("tpool.maxthreadcount", "100");
		String minThreadCount = config.getProperty("tpool.minthreadc127.0.0.1ount", "20");
		String allowedIdleCount = config.getProperty("tpool.allowedidlecount", "50");
		String maxScheduleCount = config.getProperty("timer.purge.shcheduleCount","500");
		
		this.servicePool = new DefaultServicePool(	Integer.parseInt(initThreadCount),
													Integer.parseInt(maxThreadCount),
													Integer.parseInt(minThreadCount),
													Integer.parseInt(allowedIdleCount),
													Integer.parseInt(maxScheduleCount)
													); 
		service = context.registerService(ServicePool.class.getName(), servicePool, null);
		LOG.info("ServicePool_JR1.4 start");
		LOG.info(this.servicePool.getStatus());
		
		wsPool = new WebservicePool(config.getProperty("server.localhost", "localhost"), config.getProperty("ass.websocket.ip", "localhost"));
		webservice = context.registerService(IWebservicePool.class.getName(), wsPool, null);
		LOG.info("WebservicePool start");
		
		// 공통예외 팩토리 생성 및 서비스 등록
		CommonServiceImpl commonService = new CommonServiceImpl();
		commonService.setServicePool(servicePool);
		commonService.setWebservicePool(wsPool);
		commonService.setLogger(LOG);
		// PM서비스 기동후 설정
		commonService.setExceptionfactory(null);
		
		newServer = new ServerInstance(context, commonService);
		newServer.init(config);
		newServer.start();
	}

	public void stop(BundleContext context) throws Exception {
		newServer.stop();
		
		wsPool.dispose();
		this.servicePool.cancel();
		this.servicePool.close();
		service.unregister();
		webservice.unregister();
	}
	
//	private void createProps(File propFile, Properties config) throws IOException{
//		FileOutputStream fout = new FileOutputStream(propFile);
//		config.store(fout, "SmartIoT2.0 Configuration.");
//		config.clear();
//		config.put("server.id", "com.hdbsnc.smartiot.gs.1");
//		config.put("server.localhost", "192.168.76.13"); //웹서버 기동시 사용해야할 주소 
//		config.put("server.dynamichost", "192.168.76.13"); //외부에서 본서버에 접속해야할 때 사용해야할 주소 
//		config.store(fout, "Server Instance Name");
//		config.clear();
//		config.put("ass.websocket.ip", "192.168.76.13");
//		config.put("ass.websocket.port", "8899");
//		config.put("ass.websocket.path", "/auth");
//		config.put("ass.tcpsocket.ip", "192.168.76.13");
//		config.put("ass.tcpsocket.port", "8898");
//		config.put("ass.tcpsocket.readbuffersize", "1024");
//		config.put("ass.tcpsocket.retryms", "10000");
//		config.store(fout, "Auth Server Service (ASS)");
//		config.clear();
//		config.put("mss.id", "com.hdbsnc.smartiot.master.gs.1");
//		config.put("mss.tcpsocket.ip", "192.168.76.13");
//		config.put("mss.tcpsocket.port", "9988");
//		config.put("mss.tcpsocket.readbuffersize", "1024");
//		config.put("mss.tcpsocket.retryms", "1000");
//		config.store(fout, "Master Server Service (MSS)");
//		config.clear();
//		config.put("sss.id", "com.hdbsnc.smartiot.slave.gs.1");
//		config.put("sss.tcpsocket.ip", "192.168.76.13");
//		config.put("sss.tcpsocket.port", "9988");
//		config.put("sss.tcpsocket.readbuffersize", "1024");
//		config.put("sss.tcpsocket.retryms", "1000");
//		config.store(fout, "Slave Server Service (SSS)");
//		config.clear();
//		config.put("tpool.initthreadcount", "100");
//		config.put("tpool.maxthreadcount", "1000");
//		config.put("tpool.minthreadcount", "100");
//		config.put("tpool.allowedidlecount", "500");
//		config.put("timer.purge.shcheduleCount", "1000");
//		config.store(fout,  "ThreadPool Service");
//		config.clear();
//		config.put("log.path", "//logs");
//		config.put("log.file", ".log");
//		config.store(fout,  "Log Service");
//		config.clear();
//		config.put("adapter.instance.autostart", "com.hdbsnc.smartiot.instance.websocketapi.1,com.hdbsnc.smartiot.instance.philips.hue.1,com.hdbsnc.smartiot.instance.lsis.xgtseries.office.1,com.hdbsnc.smartiot.instance.lsis.xgtseries.factory.1,tcpapi.ins.1");
////		config.put("adapter.instance.autostart", "com.hdbsnc.smartiot.instance.websocketapi.1, com.hdbsnc.smartiot.instance.lsis.xgtseries.office.1, tcpapi.ins.1");
//		config.store(fout,  "AdapterInstance AutoStart");
//		config.clear();
//		// adapter.instance.autostart
//	}
	private static final SimpleDateFormat yyMMddHHmmss = new SimpleDateFormat("yy.MM.dd HH:mm:ss");

	private void createDefaultProps(File propFile) throws IOException{
		PrintWriter out = new PrintWriter(propFile, "UTF-8");
		out.println("#SmartIoT2.0 Configuration");
		out.println("#"+yyMMddHHmmss.format(new Date(System.currentTimeMillis())));
		out.println("#");
		out.println("#Server Instance Name");
		out.println("server.id=com.hdbsnc.smartiot.gs.1");
		out.println("server.localhost=192.168.76.13");
		out.println("server.dynamichost=192.168.76.13");
		out.println("#");
		out.println("#Auth Server Service (ASS)");
		out.println("ass.websocket.ip=192.168.76.13");
		out.println("ass.websocket.port=8899");
		out.println("ass.websocket.path=/auth");
		out.println("ass.tcpsocket.ip=192.168.76.13");
		out.println("ass.tcpsocket.port=8898");
		out.println("ass.tcpsocket.readbuffersize=8024");
		out.println("ass.tcpsocket.retryms=10000");
		out.println("#");
		out.println("#Master Server Service (MSS)");
		out.println("mss.id=com.hdbsnc.smartiot.master.gs.1");
		out.println("mss.tcpsocket.ip=192.168.76.13");
		out.println("mss.tcpsocket.port=9988");
		out.println("mss.tcpsocket.readbuffersize=8024");
		out.println("mss.tcpsocket.retryms=1000");
		out.println("#");
		out.println("#Slave Server Service (SSS)");
		out.println("sss.id=com.hdbsnc.smartiot.slave.gs.1");
		out.println("sss.tcpsocket.ip=192.168.76.13");
		out.println("sss.tcpsocket.port=9988");
		out.println("sss.tcpsocket.readbuffersize=8024");
		out.println("sss.tcpsocket.retryms=1000");
		out.println("#");
		out.println("#ThreadPool Service");
		out.println("tpool.initthreadcount=100");
		out.println("tpool.maxthreadcount=1000");
		out.println("tpool.minthreadcount=100");
		out.println("tpool.allowedidlecount=500");
		out.println("timer.purge.shcheduleCount=1000");
		out.println("#");
		out.println("#Log Service");
		out.println("log.path=//logs");
		out.println("log.file=.log");
		out.println("#log.base=d:");
		// 추가할 로그 설정: log.db=jdbc:sqlserver://192.168.76.21:3389;databaseName=testdb;user=admin;password=123
		// 추가할 로그 설정: log.db.table=SMARTIOT_LOG
		// 추가할 로그 설정: log.handler=smartiot.log.console, smartiot.log.file, smartiot.log.db
		out.println("#");
		out.println("#Extension");
		out.println("service.filelog.path=//extension");
		out.println("service.filelog.file=_ext.log");
		out.println("service.filelog.filter=.*");
		out.println("service.storage.myunghwa.filename=EventContextList.xml");
		out.println("service.storage.myunghwa.eventhandler.filter=mh.eh.filter.1");
		out.println("service.storage.myunghwa.eventhandler.storage=mh.eh.storage.1");
		out.println("#");
		out.println("#AdapterInstance AutoStart");
		out.println("adapter.instance.autostart=com.hdbsnc.smartiot.instance.websocketapi.1,tcpapi.ins.1");

		out.flush();
		out.close();
	}
}
