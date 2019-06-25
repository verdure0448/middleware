package com.hdbsnc.smartiot.adapter.zeromq.processor.handler;

import java.util.Arrays;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.InnerContext;
import com.hdbsnc.smartiot.adapter.zeromq.api.ZeromqApi;
import com.hdbsnc.smartiot.adapter.zeromq.api.callback.RepCallback;
import com.hdbsnc.smartiot.adapter.zeromq.obj.StartRequest;
import com.hdbsnc.smartiot.adapter.zeromq.obj.StatusRequest;
import com.hdbsnc.smartiot.adapter.zeromq.obj.StopRequest;
import com.hdbsnc.smartiot.common.aim.IAdapterInstanceManager;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractTransactionTimeoutFunctionHandler;
import com.hdbsnc.smartiot.util.logger.Log;

/**
 * ZMQ의 REP의 Request(수집 시작/정지/조회/일괄정지) 요청 -> 맬섹 핸들러 호출(handover)을 위한 핸들러 ->
 * callback 처리
 * 
 * @author admin
 *
 */
public class RepHandler extends AbstractTransactionTimeoutFunctionHandler {

	private IAdapterInstanceManager aim = null;
	private ZeromqApi zmqApi = null;
	private Log log = null;
	
	public RepHandler(String name, IAdapterInstanceManager aim, long timeout, ZeromqApi pZmqApi, Log log) {
		super(name, timeout);
		zmqApi = pZmqApi;
	}

	@Override
	public void transactionProcess(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		
		String content = inboundCtx.getContent().toString();
		InnerContext ICtx = new InnerContext();
		
		JsonParser parser = new JsonParser();
		JsonObject jsonObj = parser.parse(content).getAsJsonObject();
		String method = jsonObj.get("").getAsString();
				
		Gson gson = new Gson();
		
		
		switch(method) {
		case "start":
			// [json string] -> [vo]
			StartRequest req = gson.fromJson(content, StartRequest.class);
			
			// TODO 삭제 예정 [vo] -> [json string]
			String sReq = gson.toJson(req);
			
			ICtx.sid = inboundCtx.getSID(); // Device ID
			ICtx.tid = "xxxxxx"; // Target ID
			ICtx.paths = Arrays.asList("xxxxxx", "xxxxxxx");
			break;
		case "stop":
			// 포멧 체크
			gson.fromJson(content, StopRequest.class);
			
			ICtx.sid = inboundCtx.getSID(); // Device ID
			ICtx.tid = "xxxxxx"; // Target ID
			ICtx.paths = Arrays.asList("xxxxxx", "xxxxxxx");
			break;
		case "stop.all":
			// TODO
			break;
		case "status":
			// 포멧 체크
			gson.fromJson(content, StatusRequest.class);
			
			ICtx.sid = inboundCtx.getSID(); // Device ID
			ICtx.tid = "xxxxxx"; // Target ID
			ICtx.paths = Arrays.asList("xxxxxx", "xxxxxxx");
			break;
		default:
			// 로그처리
			log.err(String.format("지원하는 않는 Method 요청(%s) ", method));
			// TODO 이상 응답 처리
			return;
		}

		
		//버프 포지션 초기화
		inboundCtx.getContent().rewind();
		ICtx.content = inboundCtx.getContent();
	

		try {
			aim.handOverContext(ICtx, new RepCallback(zmqApi));
		} catch (Exception e) {
			// 로그 출력
			log.err(e);
			// TODO 이상 응답 처리
			
		}
	}

	@Override
	public void rejectionProcess(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		// 미들웨어 내부 장에로 에러 로그 처리만
		log.err("Rejection Process 발생.");
	}
}