
package com.hdbsnc.smartiot.adapter.mb.mc.bin.handler;

import java.nio.ByteBuffer;

import com.google.gson.Gson;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.api.frame.exception.ApplicationException;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.handler.manager.IDeletePolling;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.protocol.obj.StopAllRequest;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.util.Util;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractTransactionTimeoutFunctionHandler;
import com.hdbsnc.smartiot.common.otp.url.parser.UrlParser;
import com.hdbsnc.smartiot.util.logger.Log;

/**
 * @author dbkim
 * 핸들러를 전체 종료 한다.
 * 생성정보는 [PLC수집 일괄정지 프로토콜] 명세서를 따른다.
 * 종료 후 결과에 대한 RES를 전달한다.
 */
public class DeleteAllRequestHandler extends AbstractTransactionTimeoutFunctionHandler {

	private static final String ADAPTER_HANDLER_PROTOCOL_METHOD_NAME = "stop.all";
	
	private IDeletePolling _manager;
	private Log _log;
	private Gson _gson;
	
	public DeleteAllRequestHandler(String name, long timeout, IDeletePolling manager, Log log) {
		super(name, timeout);
		
		_manager = manager;
		_log = log.logger(this.getClass());
		_gson = new Gson();
	}
	
	@Override
	public void transactionProcess(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
//		{"jsonrpc":"2.0","method":"stop.all","id":"1","param":{"protocol.version":"1.0"}}
		String sId = null;
		String sResContents = null;
		try {
			String jsonContents = new String(inboundCtx.getContent().array(), "UTF-8");
			StopAllRequest req = _gson.fromJson(jsonContents, StopAllRequest.class);
			sId = req.getId();
			
			String protocolVerion = req.getParam().getVersion();
			String protocolMethod = req.getMethod();
			if(!Util.PROTOCOL_VERSION.equals(protocolVerion)) {
				throw new ApplicationException("프로토콜 버전이 일치 하지 않습니다. 프로토콜 버전을 확인해주세요");
			}else if(!ADAPTER_HANDLER_PROTOCOL_METHOD_NAME.equals(protocolMethod)) {
				throw new ApplicationException("프로토콜 기능명이 일치 하지 않습니다. 기능명을 확인해주세요");
			}
			
			String[] eventIdArray = _manager.deleteAll();

			//정상 Start 후 응답
			sResContents = Util.makeSuccessStopAllResponseJson(sId, eventIdArray);
		}catch(Exception e) {
			//비정상 Start 후 응답
			sResContents = Util.makeFailStopAllResponseJson(sId, "-1", e.getMessage());
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
