package com.hdbsnc.smartiot.adapter.mb.mc.bin.handler;

import java.nio.ByteBuffer;
import java.util.Map;

import com.google.gson.Gson;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.api.frame.exception.ApplicationException;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.handler.manager.IRunningStatus;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.protocol.obj.StatusRequest;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.util.Util;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractTransactionTimeoutFunctionHandler;
import com.hdbsnc.smartiot.common.otp.url.parser.UrlParser;
import com.hdbsnc.smartiot.util.logger.Log;

/**
 * @author user
 * 동적생성된 핸들러의 상태를 반환한다.
 */
public class RunningStatusCheckHandler extends AbstractTransactionTimeoutFunctionHandler {

	private static final String ADAPTER_HANDLER_PROTOCOL_METHOD_NAME = "status";

	private IRunningStatus _manager;
	private Log _log;
	private Gson _gson;
	
	public RunningStatusCheckHandler(String name, long timeout, IRunningStatus manager, Log log) {
		super(name, timeout);
		
		_manager = manager;
		_log = log.logger(this.getClass());
		_gson = new Gson();
	}

	@Override
	public void transactionProcess(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
	
		String sId = null;
		String sResContents = null;
		try {
			String jsonContents = new String(inboundCtx.getContent().array(), "UTF-8");
			StatusRequest req = _gson.fromJson(jsonContents, StatusRequest.class);
			sId = req.getId();
			
			String protocolVerion = req.getParam().getVersion();
			String protocolMethod = req.getMethod();
			if(!Util.PROTOCOL_VERSION.equals(protocolVerion)) {
				throw new ApplicationException("프로토콜 버전이 일치 하지 않습니다. 프로토콜 버전을 확인해주세요");
			}else if(!ADAPTER_HANDLER_PROTOCOL_METHOD_NAME.equals(protocolMethod)) {
				throw new ApplicationException("프로토콜 기능명이 일치 하지 않습니다. 기능명을 확인해주세요");
			}
			
			Map statusMap = _manager.statusAll();

			//정상 Start 후 응답
			sResContents = Util.makeSuccessStatusResponseJson(sId, statusMap);
		}catch(Exception e) {
			//비정상 Start 후 응답
			_log.err(e);
			sResContents = Util.makeFailStartResponseJson(sId, "-1", e.getMessage());
		}

		outboundCtx.getPaths().add("ack");
		outboundCtx.setTID("this");
		outboundCtx.setTransmission("res");
		outboundCtx.setContenttype("json");
		outboundCtx.setContent(ByteBuffer.wrap(sResContents.getBytes()));
		_log.trace(UrlParser.getInstance().convertToString(outboundCtx));
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

		_log.warn("핸들러 트랜젝션 경고 : " + UrlParser.getInstance().convertToString(outboundCtx));		
	}

}
