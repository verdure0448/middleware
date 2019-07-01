package com.hdbsnc.smartiot.adapter.zeromq.api.callback;

import java.io.UnsupportedEncodingException;

import com.google.gson.Gson;
import com.hdbsnc.smartiot.adapter.zeromq.api.ZeromqApi;
import com.hdbsnc.smartiot.adapter.zeromq.obj.CommonResponse;
import com.hdbsnc.smartiot.common.context.IContextCallback;
import com.hdbsnc.smartiot.common.context.IContextTracer;
import com.hdbsnc.smartiot.util.logger.Log;

public class RepCallback implements IContextCallback {

	private ZeromqApi zmqApi = null;
	private Log log = null;

	public RepCallback(ZeromqApi pZmqApi, Log log) {
		this.zmqApi = pZmqApi;
		this.log = log.logger(this.getClass());
	}

	@Override
	public void responseSuccess(IContextTracer ctxTracer) {

		
		byte[] content = ctxTracer.getResponseContext().getContent().array();
		
		
		try {
			zmqApi.send(content);
			log.debug("Zmq Response: " + new String(content, "UTF-8"));
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

		String resContent;
		try {
			resContent = new String(ctxTracer.getResponseContext().getContent().array(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// 후속처리 불가이므로 로그처리만
			log.err(e);
			return;
		}

		// 요청 컨텐츠
		Gson gson = new Gson();
		CommonResponse res = gson.fromJson(resContent, CommonResponse.class);

		String sRes = gson.toJson(res);

		// 장애 통신 전송
		try {
			zmqApi.send(sRes.getBytes("UTF-8"));
			log.debug("Zmq Response: " + sRes);
		} catch (Exception e) {
			// 통신 장애이므로 에러로그 처리만
			log.err(e);
			return;
		}
	}

}
