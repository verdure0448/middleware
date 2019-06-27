package com.hdbsnc.smartiot.adapter.zeromq.processor.handler;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.hdbsnc.smartiot.adapter.zeromq.api.ZeromqApi;
import com.hdbsnc.smartiot.adapter.zeromq.api.callback.RepCallback;
import com.hdbsnc.smartiot.adapter.zeromq.obj.CommonRequest;
import com.hdbsnc.smartiot.adapter.zeromq.obj.CommonResponse;
import com.hdbsnc.smartiot.adapter.zeromq.obj.ResError;
import com.hdbsnc.smartiot.adapter.zeromq.obj.StartRequest;
import com.hdbsnc.smartiot.adapter.zeromq.obj.StatusRequest;
import com.hdbsnc.smartiot.adapter.zeromq.obj.StopAllRequest;
import com.hdbsnc.smartiot.adapter.zeromq.obj.StopRequest;
import com.hdbsnc.smartiot.common.aim.IAdapterInstanceManager;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractTransactionTimeoutFunctionHandler;
import com.hdbsnc.smartiot.common.context.impl.InnerContext;
import com.hdbsnc.smartiot.common.otp.url.parser.UrlParser;
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

		String content = null;
		try {
			content = new String(inboundCtx.getContent().array(), "UTF-8");
		} catch (Exception ex) {
			// TODO 에러 응답
			outboundCtx.dispose();
			//RPC요청 ID를 알수 없으므로 null 설정
			sendErrorResponse(null, "-32000", "[UTF-8]코드 변환에 실패했습니다.");
			return;
		}

		InnerContext ICtx = new InnerContext();

		JsonParser parser = new JsonParser();
		JsonObject jsonObj = null;

		try {
			jsonObj = parser.parse(content).getAsJsonObject();
		} catch (JsonSyntaxException ex) {
			// TODO 에러 응답
			//RPC요청 ID를 알수 없으므로 null 설정
			sendErrorResponse(null, "-32001", "Json 파싱에 실패했습니다.");
			return;
		}

		String method = jsonObj.get("method").getAsString();
		String id = jsonObj.get("id").getAsString();

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
		case "stop.part":
			// 포멧 체크
			gson.fromJson(content, StopRequest.class);

			ICtx.setSid(inboundCtx.getSID()); // Device ID
			ICtx.setTid(ADAPTER_TARGET_ID); // Target ID
			ICtx.setPaths(Arrays.asList(ADAPTER_TARGET_DELETE_HANDLER_PATH.split("/")));
			break;
		case "stop.all":
			gson.fromJson(content, StopAllRequest.class);

			ICtx.setSid(inboundCtx.getSID()); // Device ID
			ICtx.setTid(ADAPTER_TARGET_ID); // Target ID
			ICtx.setPaths(Arrays.asList(ADAPTER_TARGET_DELETE_ALL_HANDLER_PATH.split("/")));
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
			sendErrorResponse(id, "-32002", String.format("지원하는 않는 Method 입니다(%s) ", method));
			return;
		}

		// 버프 포지션 초기화
		inboundCtx.getContent().rewind();
		ICtx.setContent(inboundCtx.getContent());

		try {
			aim.handOverContext(ICtx, new RepCallback(zmqApi, log));
			// aim.handOverContext(ICtx, null);
		} catch (Exception e) {
			// 로그 출력
			log.err(e);
			// 이상 응답 처리
			sendErrorResponse(id, "-32003", "핸들러 호출에 장애가 발생했습니다.");
			return;
		}

	}

	@Override
	public void rejectionProcess(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		// 에러로그
		log.err("Rejection Process 발생.");

		String reqContent = null;

		try {
			reqContent = new String(inboundCtx.getContent().array(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {

		}

		// 요청 컨텐츠
		Gson gson = new Gson();
		CommonRequest req = gson.fromJson(reqContent, CommonRequest.class);

		sendErrorResponse(req.getId(), "-32004", "트랜젝션이 잠겨 있습니다.");

	}

	/**
	 * 에러 응답 전송
	 * 
	 * @param id      RPC통신 ID
	 * @param code    에러코드
	 * @param message 에러메세지
	 */
	private void sendErrorResponse(String id, String code, String message) {
		Gson gson = new Gson();
		CommonResponse res = new CommonResponse();
		ResError error = new ResError();

		res.setJsonrpc("2.0");
		res.setId(id);
		error.setCode(code);
		error.setMessage(message);
		res.setError(error);

		// 통신 전송
		try {
			zmqApi.send(gson.toJson(res).getBytes("UTF-8"));
		} catch (Exception e) {
			// 통신 장애이므로 에러로그 처리만
			log.err(e);
			return;
		}

	}
}