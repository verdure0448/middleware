package com.hdbsnc.smartiot.adapter.zeromq.processor.handler;

import com.google.gson.Gson;
import com.hdbsnc.smartiot.adapter.zeromq.api.ZeromqApi;
import com.hdbsnc.smartiot.adapter.zeromq.obj.GatheringPublish;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractTransactionTimeoutFunctionHandler;
import com.hdbsnc.smartiot.util.logger.Log;

/**
 *
 * 정정 핸들러로써 멜섹 아답터에서 handover로 호출 -> 데이터 가공 -> ZMQ Send(Publish) 처리를 위한 핸들러
 * 
 * @author dbkim
 *
 */
public class PubHandler extends AbstractTransactionTimeoutFunctionHandler {

	private ZeromqApi zmqApi = null;
	private Log log = null;
	
	public PubHandler(String name, long timeout, ZeromqApi pZmqApi, Log log) {
		super(name, timeout);
		this.zmqApi = pZmqApi;
		this.log = log;
	}

	@Override
	public void transactionProcess(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {


		String content = new String(inboundCtx.getContent().array(), "UTF-8");
		
		Gson gson = new Gson();

		GatheringPublish publish = null;
		try {
			publish = gson.fromJson(content, GatheringPublish.class);
		} catch (Exception e) {
			// topic을 알수 없어 통신이 불가한상태이므로 내부 에러로그 출력 처리만
			// 로그출력
			log.err(e);
			return;
		}
		////////////////////////////////////////////////////////////////////////////////////
		// PUB으로 데이터 브로드캐스팅
		////////////////////////////////////////////////////////////////////////////////////
		this.zmqApi.publish(publish.getResult().getEventID().getBytes("UTF-8"), content.getBytes("UTF-8"));
		
		outboundCtx.dispose();
	}

	@Override
	public void rejectionProcess(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		// 미들웨어 내부 장에로 에러 로그 처리만
		log.err("Rejection Process 발생.");

	}
}
