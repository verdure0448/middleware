
package com.hdbsnc.smartiot.adapter.zeromq;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.zeromq.SocketType;

import com.hdbsnc.smartiot.adapter.zeromq.api.ZeromqApi;
import com.hdbsnc.smartiot.adapter.zeromq.processor.handler.PubHandler;
import com.hdbsnc.smartiot.adapter.zeromq.processor.handler.RepHandler;
import com.hdbsnc.smartiot.common.ICommonService;
import com.hdbsnc.smartiot.common.aim.IAdapterContext;
import com.hdbsnc.smartiot.common.aim.IAdapterInstance;
import com.hdbsnc.smartiot.common.aim.IAdapterInstanceManager;
import com.hdbsnc.smartiot.common.aim.IAdapterProcessor;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractTransactionTimeoutFunctionHandler;
import com.hdbsnc.smartiot.common.context.handler2.impl.RootHandler;
import com.hdbsnc.smartiot.common.context.impl.InnerContext;
import com.hdbsnc.smartiot.common.ism.sm.ISession;
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

	// private IEventManager em;
	// private IProfileManager pm;

	private ISession session = null;

	private List<AbstractTransactionTimeoutFunctionHandler> plcProcessHandlerList;
	private List<String> emKeyList;

	private ZeromqApi zmqRep = null;
	private ZeromqApi zmqPub = null;

	public ZeroMqAdapterInstance(ICommonService service) {

		this.service = service;
		// this.em = em;
		// this.pm = pm;

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

		IAdapterInstanceManager aim = ctx.getAdapterInstanceManager();
		
		IInstanceObj instanceInfo = ctx.getAdapterInstanceInfo();
		String ip = instanceInfo.getIp();
		//int port = Integer.parseInt(instanceInfo.getPort());
		
		// ","구분자로 Rep 포트와 Pub포트 정의
		String sPort = instanceInfo.getPort();
		String[] ports = sPort.split(",");
		if(ports.length != 2) {
			log.err("ZQM 포트 설정 오류.");
			throw service.getExceptionfactory().createAppException(this.getClass().getName() + ":001", new String[] {sPort});
		}
		
		String userId = instanceInfo.getSelfId();
		String upass = instanceInfo.getSelfPw();
		String defaultDid = instanceInfo.getDefaultDevId();

		session = ctx.getSessionManager().certificate(defaultDid, userId, upass);

		zmqRep = new ZeromqApi(1, SocketType.REP, "tcp://" + ip + ":" + ports[0]);
		zmqPub = new ZeromqApi(1, SocketType.PUB, "tcp://" + ip + ":" + ports[1]);
		
		////////////////////////////////////////////////////////////////////////////////////
		// [PLC 수집 시작 명령 프로토콜]
		// [PLC 수집 정지 명령 프로토콜]
		// [PLC 수집 일괄 정지 명령 프로토콜]
		// PLC 수집조회 프로토콜의 경우 위의 3가지 프로토콜 명령과 중첩하여 날아 갈 수 있으므로 따른 포트를 통해 생성
		// EX) 수집 시작 명령 REQ 동작 중 수집명령 REQ가 날아올 수 있지만 나머지 위 3개 명령은 그런 확률이 거이 없음.
		////////////////////////////////////////////////////////////////////////////////////

		ZeromqApi.IEvent repEvent = new ZeromqApi.IEvent() {

			@Override
			public void onRecv(byte[] msg) {
				// JSON 파싱 및 멜셀 수집/정지/조회 처리
				log.debug("Zmq Request: " + new String(msg));

				// 응답메세지 전송(이벤트 핸들러 호출 결과에 따른 )
				// zeromqApi.send(msg);
				
				// TODO zmq/req 핸들러 호출
				// 자기 자신의 핸들로를 호출하는 처리로 hdadover 방식이 아니라 직접 핸들러를 호출하는 방법도 ?
				
				String did = session.getDeviceId();
				
				InnerContext ICtx = new InnerContext();
				ICtx.setSid(did); // Device ID
				ICtx.setTid(did); // Target ID
				ICtx.setPaths(Arrays.asList("zmq", "req"));
				
				
				ByteBuffer buf = ByteBuffer.allocate(msg.length);
				buf.put(msg);
				
				// 버퍼 포지션 초기화
				buf.rewind();
				ICtx.setContent(buf);
				ICtx.setContentType("json");
				
				try {
					aim.handOverContext(ICtx, null);
				} catch (Exception e) {
					// TODO 에러 발생 로그 처리 및 응답 전송
					e.printStackTrace();
				}
			}
		};

		// ZMQ REP 서버 기동
		zmqRep.start(repEvent);

		// ZMQ PUB 서버 기동
		zmqPub.start(null);
		
		RootHandler root = this.processor.getRootHandler();

		root.putHandler("zmq", new RepHandler("req", aim, 3000, zmqRep, log));
		root.putHandler("zmq", new PubHandler("pub", 3000, zmqPub, log));
	}

	@Override
	public void stop(IAdapterContext ctx) throws Exception {

		log.info("stop");

		// 컨슈머 해제
//		for (String emKey : emKeyList) {
//			if (em.containPollingAdapterProcessor(emKey)) {
//				em.removePollingAdapterProcessorEvent(emKey);
//
//				log.info("[EventManager] : " + emKey + " 해제");
//			}
//		}

		// 핸들러 해제
		for (AbstractTransactionTimeoutFunctionHandler handler : plcProcessHandlerList) {
			this.processor.getRootHandler().removeHandler(handler);
			log.info("[Handler] : " + handler.getName() + " 해제");
		}

		emKeyList.clear();
		plcProcessHandlerList.clear();

		// ZMQ 중지
		if (this.zmqRep != null) {
			try {
				zmqRep.stop();
			} catch (Exception e) {
				log.err(e);
			}
		}

		if (this.zmqPub != null) {
			try {
				zmqPub.stop();
			} catch (Exception e) {
				log.err(e);
			}
		}

	}

	@Override
	public void suspend(IAdapterContext ctx) throws Exception {
		// 해당함수는 기능 구현 없음
	}

	@Override
	public void dispose(IAdapterContext ctx) throws Exception {
		// 해당함수는 기능 구현 없음
	}

	@Override
	public IAdapterProcessor getProcessor() {
		return this.processor;
	}

}
