
package com.hdbsnc.smartiot.adapter.zeromq;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.zeromq.SocketType;

import com.hdbsnc.smartiot.adapter.zeromq.api.ZeromqApi;
import com.hdbsnc.smartiot.adapter.zeromq.processor.handler.PubHandler;
import com.hdbsnc.smartiot.adapter.zeromq.processor.handler.RepHandler;
import com.hdbsnc.smartiot.common.ICommonService;
import com.hdbsnc.smartiot.common.aim.IAdapterContext;
import com.hdbsnc.smartiot.common.aim.IAdapterInstance;
import com.hdbsnc.smartiot.common.aim.IAdapterInstanceManager;
import com.hdbsnc.smartiot.common.aim.IAdapterProcessor;
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

	private static final int DEFAULT_ZMQ_THREAD_COUNT = 1;
	
	private ICommonService service;
	private Log log;
	private ZeroMqAdapterProcessor processor = null;

	// private IEventManager em;
	// private IProfileManager pm;

	private ISession session = null;

	private List<String> handlerList;

	private ZeromqApi mainZmqRep = null;
	private ZeromqApi readonceZmqRep = null;
	private ZeromqApi zmqPub = null;

	public ZeroMqAdapterInstance(ICommonService service) {

		this.service = service;

		handlerList = new ArrayList<>();
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
		
		String home_path = System.getenv("SMARTIOT_HOME");
		File propFile = new File(home_path+"//conf//config.conf");
		
		Properties config = new Properties();
		if(propFile.exists()){
			config.load(new FileInputStream(propFile));
		}
		String ip = config.getProperty("zeromq.ip");
		String mainReqPort = config.getProperty("zeromq.req.main.port");
		String readonceReqPort = config.getProperty("zeromq.req.readonce.port");
		String publicPort = config.getProperty("zeromq.pub.port");
		
		IInstanceObj instanceInfo = ctx.getAdapterInstanceInfo();
		
		String userId = instanceInfo.getSelfId();
		String upass = instanceInfo.getSelfPw();
		String defaultDid = instanceInfo.getDefaultDevId();

		session = ctx.getSessionManager().certificate(defaultDid, userId, upass);

		mainZmqRep = new ZeromqApi(DEFAULT_ZMQ_THREAD_COUNT, SocketType.REP, "tcp://" + ip + ":" + mainReqPort);
		readonceZmqRep = new ZeromqApi(DEFAULT_ZMQ_THREAD_COUNT, SocketType.REP, "tcp://" + ip + ":" + readonceReqPort);
		zmqPub = new ZeromqApi(DEFAULT_ZMQ_THREAD_COUNT, SocketType.PUB, "tcp://" + ip + ":" + publicPort);
		
		RootHandler root = this.processor.getRootHandler();
		
		root.putHandler("zmq/main", new RepHandler("req", aim, 3000, mainZmqRep, log));
		root.putHandler("zmq/readonce", new RepHandler("req", aim, 3000, readonceZmqRep, log));
		root.putHandler("zmq", new PubHandler("pub", 3000, zmqPub, log));

		//Stop시 삭제할 핸들러 명 저장.
		handlerList.add("zmq/main/req");
		handlerList.add("zmq/readonce/req");
		handlerList.add("zmq/pub");
		

		////////////////////////////////////////////////////////////////////////////////////
		// [PLC 수집 시작 명령 프로토콜]
		// [PLC 수집 정지 명령 프로토콜]
		// [PLC 수집 일괄 정지 명령 프로토콜]
		// PLC 수집조회 프로토콜의 경우 위의 3가지 프로토콜 명령과 중첩하여 날아 갈 수 있으므로 따른 포트를 통해 생성
		// EX) 수집 시작 명령 REQ 동작 중 수집명령 REQ가 날아올 수 있지만 나머지 위 3개 명령은 그런 확률이 거이 없음.
		////////////////////////////////////////////////////////////////////////////////////
		ZeromqApi.IEvent mainRepEvent = new ZeromqApi.IEvent() {

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
				ICtx.setPaths(Arrays.asList("zmq", "main", "req"));
				
				
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

		ZeromqApi.IEvent readOnceRepEvent = new ZeromqApi.IEvent() {

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
				ICtx.setPaths(Arrays.asList("zmq", "readonce", "req"));
				
				
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
		mainZmqRep.start(mainRepEvent);
		readonceZmqRep.start(readOnceRepEvent);

		// ZMQ PUB 서버 기동
		zmqPub.start(null);
	}

	@Override
	public void stop(IAdapterContext ctx) throws Exception {

		log.info("stop");
		RootHandler root = this.processor.getRootHandler();

		//등록된 핸들러 삭제
		for(int i=0; i<handlerList.size(); i++) {
			String handlerPath = handlerList.get(i);
			if (root.findHandler(handlerPath) != null) {
				root.deleteHandler(handlerPath.split("/"));
			}
		}

		// Main ZMQ 중지
		if (this.mainZmqRep != null) {
			try {
				mainZmqRep.stop();
			} catch (Exception e) {
				log.err(e);
			}
		}

		// readonce ZMQ 중지
		if (this.mainZmqRep != null) {
			try {
				mainZmqRep.stop();
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
