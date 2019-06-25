package com.hdbsnc.smartiot.adapter.zeromq.api.callback;

import java.nio.ByteBuffer;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hdbsnc.smartiot.adapter.zeromq.api.ZeromqApi;
import com.hdbsnc.smartiot.adapter.zeromq.obj.CommonRequest;
import com.hdbsnc.smartiot.adapter.zeromq.obj.CommonResponse;
import com.hdbsnc.smartiot.adapter.zeromq.obj.ResError;
import com.hdbsnc.smartiot.adapter.zeromq.obj.StartRequest;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.IContextCallback;
import com.hdbsnc.smartiot.common.context.IContextTracer;
import com.hdbsnc.smartiot.util.logger.Log;

public class RepCallback implements IContextCallback {

	private ZeromqApi zmqApi = null;
	private Log log = null;

	public RepCallback(ZeromqApi pZmqApi, Log log) {
		this.zmqApi = pZmqApi;
		this.log = log;
	}

	@Override
	public void responseSuccess(IContextTracer ctxTracer) {
		ByteBuffer content = ctxTracer.getResponseContext().getContent();

		try {
			zmqApi.send(content.array());
		} catch (Exception e) {
			// 통신 장애이므로 에러로그 처리만
			log.err(e);
			return;
		}
	}

	@Override
	public void responseFail(IContextTracer ctxTracer) {
		// 로그 에러 처리
		log.err("Rep Callback처리에 장애 발생.");

		String reqContent = ctxTracer.getRequestContext().getContent().toString();
		
		// 요청 컨텐츠 
		Gson gson = new Gson();
		CommonRequest req = gson.fromJson(reqContent, CommonRequest.class);
		
		
		CommonResponse res = new CommonResponse();
		ResError error = new ResError();
		
		res.setJsonrpc("2.0");
		res.setId(req.getId());
		
		error.setCode("RepCallback:001");
		error.setMessage("Rep 요청 처리 실패.");
		res.setError(error);
		
		String sRes = gson.toJson(res);
		
		// 장애 통신 전송
		try {
			zmqApi.send(sRes.getBytes("UTF-8"));
		} catch (Exception e) {
			// 통신 장애이므로 에러로그 처리만
			log.err(e);
			return;
		}
	}

}
