package com.hdbsnc.smartiot.adapter.zeromq.processor.handler;

import java.io.UnsupportedEncodingException;

import com.hdbsnc.smartiot.adapter.zeromq.api.ZeromqApi;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractTransactionTimeoutFunctionHandler;
import com.hdbsnc.smartiot.common.otp.url.parser.UrlParser;
import com.hdbsnc.smartiot.util.logger.Log;

/**
 * @author user
 * 동적생성된 핸들러의 상태를 반환한다.
 */
public class ResHandler extends AbstractTransactionTimeoutFunctionHandler {
	
	private ZeromqApi zmqApi = null;
	private Log log = null;
	
	public ResHandler(String name, long timeout, ZeromqApi pZmqApi, Log log) {
		super(name, timeout);

		zmqApi = pZmqApi;
		this.log = log.logger(this.getClass());
	}

	@Override
	public void transactionProcess(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {

		String content;
		try {
			content = new String(inboundCtx.getContent().array(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// 후속처리 불가이므로 로그처리만
			log.err(e);
			return;
		}

		try {
			zmqApi.send(content.getBytes("UTF-8"));
		} catch (Exception e) {
			// 통신 장애이므로 에러로그 처리만
			log.err(e);
			return;
		}
		
		outboundCtx.dispose();
	}

	@Override
	public void rejectionProcess(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		outboundCtx.getPaths().add("nack");
		outboundCtx.setSID(inboundCtx.getSID());
		outboundCtx.setSPort(inboundCtx.getSPort());
		outboundCtx.setTID(inboundCtx.getTID());
		outboundCtx.setTPort(inboundCtx.getTPort());
		outboundCtx.getParams().put("code", "W9001");
		outboundCtx.getParams().put("type", "warn");
		outboundCtx.getParams().put("msg", "트랜젝션이 잠겨 있습니다.(다른 request가 선행 호출되어 있을 수 있습니다.)");
		outboundCtx.setTransmission("res");		

		log.warn("핸들러 트랜젝션 경고 : " + UrlParser.getInstance().convertToString(outboundCtx));		
	}

}
