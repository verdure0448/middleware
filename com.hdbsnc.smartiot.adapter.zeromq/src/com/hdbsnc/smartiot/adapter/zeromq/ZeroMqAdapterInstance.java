
package com.hdbsnc.smartiot.adapter.zeromq;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.hdbsnc.smartiot.adapter.zeromq.api.HttpApi;
import com.hdbsnc.smartiot.adapter.zeromq.processor.handler.PubHandler;
import com.hdbsnc.smartiot.adapter.zeromq.processor.handler.ReqHandler;
import com.hdbsnc.smartiot.common.ICommonService;
import com.hdbsnc.smartiot.common.aim.IAdapterContext;
import com.hdbsnc.smartiot.common.aim.IAdapterInstance;
import com.hdbsnc.smartiot.common.aim.IAdapterProcessor;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractTransactionTimeoutFunctionHandler;
import com.hdbsnc.smartiot.common.context.handler2.impl.RootHandler;
import com.hdbsnc.smartiot.common.em.IEventManager;
import com.hdbsnc.smartiot.common.ism.sm.ISession;
import com.hdbsnc.smartiot.common.pm.IProfileManager;
import com.hdbsnc.smartiot.common.pm.vo.IInstanceObj;
import com.hdbsnc.smartiot.util.logger.Log;

/**
 * @author dbkim
 * 
 */
public class ZeroMqAdapterInstance implements IAdapterInstance {

	private ICommonService service;
	private Log log;
	private ZeroMqAdapterProcessor processor = null;
	private IEventManager em;
	private HttpApi api;
	private IProfileManager pm;

	private ISession session = null;

	private List<AbstractTransactionTimeoutFunctionHandler> plcProcessHandlerList;
	private List<String> emKeyList;
	
	public ZeroMqAdapterInstance(ICommonService service, IEventManager em, IProfileManager pm) {

		this.service = service;
		this.em = em;
		this.pm = pm;

		plcProcessHandlerList = new ArrayList<AbstractTransactionTimeoutFunctionHandler>();
		emKeyList = new ArrayList<>();
	}

	@Override
	public void initialize(IAdapterContext ctx) throws Exception {

		log = service.getLogger().logger(ctx.getAdapterInstanceInfo().getInsId());
		log.info("initialize");
		this.processor = new ZeroMqAdapterProcessor(service, ctx);
	}

	@Override
	public void start(IAdapterContext ctx) throws Exception {
		log.info("start");

		IInstanceObj instanceInfo = ctx.getAdapterInstanceInfo();
		String ip = instanceInfo.getIp();
		int port =  Integer.parseInt(instanceInfo.getPort());

		String userId = instanceInfo.getSelfId();
		String upass = instanceInfo.getSelfPw();
		String defaultDid = instanceInfo.getDefaultDevId();

		session = ctx.getSessionManager().certificate(defaultDid, userId, upass);

		RootHandler root = this.processor.getRootHandler();
		
		
		////////////////////////////////////////////////////////////////////////////////////
		//[PLC 수집 시작 명령 프로토콜]
		//[PLC 수집 정지 명령 프로토콜]
		//[PLC 수집 일괄 정지 명령 프로토콜]
		//PLC 수집조회 프로토콜의 경우 위의 3가지 프로토콜 명령과 중첩하여 날아 갈 수 있으므로 따른 포트를 통해 생성
		//EX) 수집 시작 명령 REQ 동작 중 수집명령 REQ가 날아올 수 있지만 나머지 위 3개 명령은 그런 확률이 거이 없음.
		////////////////////////////////////////////////////////////////////////////////////
		HttpApi httpApi1 = new HttpApi(log);
		
		////////////////////////////////////////////////////////////////////////////////////
		//[PLC 수집 조회 프로토콜]
		//★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★
		//★★★★★★ECM을 쓰면 간단하지만 안쓰고 할 수 있도록 방안 모색 중
		//★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★
		////////////////////////////////////////////////////////////////////////////////////
		HttpApi httpApi2 = new HttpApi(log);
		root.putHandler("http/req/collection", new ReqHandler("search", 3000, httpApi2));
		
		
		

		////////////////////////////////////////////////////////////////////////////////////
		//Class : ReqHandler
		//ZeroMQ의 REQ에 [PLC 수집 시작 프로토콜]이 들어올 경우 
		//멜섹 PLC 아답터의 "CreateDynamicHandler" 핸들러를 호출한다
		////////////////////////////////////////////////////////////////////////////////////
		root.putHandler("http/req", new ReqHandler("start", 3000, httpApi1));
		////////////////////////////////////////////////////////////////////////////////////
		//Class : ResHandler
		//ZeroMQ의 REQ에 [PLC 수집 시작 프로토콜]의 결과를 반환한다. 
		//멜섹 PLC 아답터의 "CreateDynamicHandler" 핸들러를 호출한다
		////////////////////////////////////////////////////////////////////////////////////
		root.putHandler("http/res", new ReqHandler("start", 3000, httpApi1));
		
		////////////////////////////////////////////////////////////////////////////////////
		//Class : ReqHandler
		//ZeroMQ의 REQ에 [PLC 수집 중지 프로토콜], [PLC 수집 일괄정지 프로토콜] 이 들어올 경우 
		//멜섹 PLC 아답터의 "DeleteDynamicHandler" 핸들러를 호출한다
		////////////////////////////////////////////////////////////////////////////////////
		root.putHandler("http/req", new ReqHandler("stop", 3000, httpApi1));
		////////////////////////////////////////////////////////////////////////////////////
		//Class : ResHandler
		//ZeroMQ의 REQ에 [PLC 수집 중지 프로토콜], [PLC 수집 일괄정지 프로토콜] 의 결과를 반환한다. 
		//멜섹 PLC 아답터의 "DeleteDynamicHandler" 핸들러를 호출한다
		////////////////////////////////////////////////////////////////////////////////////
		root.putHandler("http/res", new ReqHandler("stop", 3000, httpApi1));
		

		////////////////////////////////////////////////////////////////////////////////////
		//ZeroMQ의 PUB 핸들러를 만들어준다
		////////////////////////////////////////////////////////////////////////////////////
		root.putHandler("message/queue", new PubHandler("pub", 3000));
		
	}
	
	@Override
	public void stop(IAdapterContext ctx) throws Exception {
		log.info("stop");
		if (this.api != null) {
			try {
				api.disconnect();
			} catch (IOException e) {
				log.err(e);
			}
		}

		// 컨슈머 해제
		for (String emKey : emKeyList) {
			if (em.containPollingAdapterProcessor(emKey)) {
				em.removePollingAdapterProcessorEvent(emKey);

				log.info("[EventManager] : " + emKey + " 해제");
			}
		}

		// 핸들러 해제
		for (AbstractTransactionTimeoutFunctionHandler handler : plcProcessHandlerList) {
			this.processor.getRootHandler().removeHandler(handler);
			log.info("[Handler] : " + handler.getName() + " 해제");
		}

		emKeyList.clear();
		plcProcessHandlerList.clear();
	}

	@Override
	public void suspend(IAdapterContext ctx) throws Exception {

	}

	@Override
	public void dispose(IAdapterContext ctx) throws Exception {

	}

	@Override
	public IAdapterProcessor getProcessor() {
		return this.processor;
	}

}
