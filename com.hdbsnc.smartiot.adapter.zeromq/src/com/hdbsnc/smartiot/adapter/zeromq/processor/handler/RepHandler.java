package com.hdbsnc.smartiot.adapter.zeromq.processor.handler;

import java.util.Arrays;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.hdbsnc.smartiot.adapter.zeromq.api.ZeromqApi;
import com.hdbsnc.smartiot.adapter.zeromq.api.callback.RepCallback;
import com.hdbsnc.smartiot.adapter.zeromq.obj.CommonRequest;
import com.hdbsnc.smartiot.adapter.zeromq.obj.CommonResponse;
import com.hdbsnc.smartiot.adapter.zeromq.obj.ResError;
import com.hdbsnc.smartiot.adapter.zeromq.obj.StartRequest;
import com.hdbsnc.smartiot.adapter.zeromq.obj.StatusRequest;
import com.hdbsnc.smartiot.adapter.zeromq.obj.StopRequest;
import com.hdbsnc.smartiot.common.aim.IAdapterInstanceManager;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractTransactionTimeoutFunctionHandler;
import com.hdbsnc.smartiot.common.context.impl.InnerContext;
import com.hdbsnc.smartiot.util.logger.Log;

/**
 * ZMQ의 REP의 Request(수집 시작/정지/조회/일괄정지) 요청 -> 맬섹 핸들러 호출(handover)을 위한 핸들러 ->
 * callback 처리
 * 
 * @author admin
 *
 */
public class RepHandler extends AbstractTransactionTimeoutFunctionHandler {

	public static final String ADAPTER_TARGET_ID = "mc.1";
	public static final String ADAPTER_TARGET_CREATE_HANDLER_PATH = "create/mb/melsec/handler";
	public static final String ADAPTER_TARGET_DELETE_HANDLER_PATH = "delete/mb/melsec/handler";
	public static final String ADAPTER_TARGET_DELETE_ALL_HANDLER_PATH = "delete/all/mb/melsec/handler";
	public static final String ADAPTER_TARGET_STATUS_HANDLER_PATH = "status/mb/melsec/handler";
	
	private IAdapterInstanceManager aim = null;
	private ZeromqApi zmqApi = null;
	private Log log = null;

	public RepHandler(String name, IAdapterInstanceManager aim, long timeout, ZeromqApi pZmqApi, Log log) {
		super(name, timeout);
		zmqApi = pZmqApi;
		this.aim = aim;
		this.log = log.logger(this.getClass());
	}

	@Override
	public void transactionProcess(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {

		String content = new String(inboundCtx.getContent().array(), "UTF-8");

		InnerContext ICtx = new InnerContext();

		JsonParser parser = new JsonParser();
		JsonObject jsonObj = parser.parse(content).getAsJsonObject();
		String method = jsonObj.get("method").getAsString();

		Gson gson = new Gson();

		switch (method) {
		case "start":
			// [json string] -> [vo]
			StartRequest req = gson.fromJson(content, StartRequest.class);

			// TODO 삭제 예정 [vo] -> [json string]
			String sReq = gson.toJson(req);

			ICtx.setSid(inboundCtx.getSID()); // Device ID
			ICtx.setTid(ADAPTER_TARGET_ID); // Target ID
			ICtx.setPaths(Arrays.asList(ADAPTER_TARGET_CREATE_HANDLER_PATH.split("/")));
			break;
		case "stop":
			// 포멧 체크
			gson.fromJson(content, StopRequest.class);

			ICtx.setSid(inboundCtx.getSID()); // Device ID
			ICtx.setTid(ADAPTER_TARGET_ID); // Target ID
			ICtx.setPaths(Arrays.asList(ADAPTER_TARGET_DELETE_HANDLER_PATH.split("/")));
			break;
		case "stop.all":
			// TODO
			break;
		case "status":
			// 포멧 체크
			gson.fromJson(content, StatusRequest.class);

			ICtx.setSid(inboundCtx.getSID()); // Device ID
			ICtx.setTid(ADAPTER_TARGET_ID); // Target ID
			ICtx.setPaths(Arrays.asList(ADAPTER_TARGET_STATUS_HANDLER_PATH.split("/")));
			break;
		default:
			// 로그처리
			log.err(String.format("지원하는 않는 Method 요청(%s) ", method));
			// TODO 이상 응답 처리
			return;
		}

		// 버프 포지션 초기화
		inboundCtx.getContent().rewind();
		ICtx.setContent(inboundCtx.getContent());

		try {
//			aim.handOverContext(ICtx, new RepCallback(zmqApi, log));
			aim.handOverContext(ICtx, null);
		} catch (Exception e) {
			// 로그 출력
			log.err(e);
			// TODO 이상 응답 처리

		}
	}

	@Override
	public void rejectionProcess(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		// 에러로그
		log.err("Rejection Process 발생.");

		
		String reqContent = new String(inboundCtx.getContent().array(), "UTF-8");

		// 요청 컨텐츠
		Gson gson = new Gson();
		CommonRequest req = gson.fromJson(reqContent, CommonRequest.class);

		CommonResponse res = new CommonResponse();
		ResError error = new ResError();

		res.setJsonrpc("2.0");
		res.setId(req.getId());

		error.setCode("RepHandler:002");
		error.setMessage("Rep 요청 처리 실패.");
		res.setError(error);

		String sRes = gson.toJson(res);

		// 통신 전송
		try {
			zmqApi.send(sRes.getBytes("UTF-8"));
		} catch (Exception e) {
			// 통신 장애이므로 에러로그 처리만
			log.err(e);
			return;
		}
	}
}