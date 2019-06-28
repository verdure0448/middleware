
package com.hdbsnc.smartiot.adapter.mb.mc.bin.handler;

import java.nio.ByteBuffer;

import com.google.gson.Gson;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.api.frame.exception.ApplicationException;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.handler.manager.IDeletePolling;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.protocol.obj.StopRequest;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.util.EditUtil;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.util.ProtocolCollection;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractTransactionTimeoutFunctionHandler;
import com.hdbsnc.smartiot.common.otp.url.parser.UrlParser;
import com.hdbsnc.smartiot.util.logger.Log;

/**
 * @author dbkim
 * 핸들러를 종료 한다.
 * 생성정보는 [PLC 수집정지 프로토콜] 명세서를 따른다.
 * 종료 후 결과에 대한 RES를 전달한다.
 */
public class DeleteRequestHandler extends AbstractTransactionTimeoutFunctionHandler {

	private static final String ADAPTER_HANDLER_PROTOCOL_METHOD_NAME = "stop.part";
	
	private IDeletePolling _manager;
	private Log _log;
	private Gson _gson;
	
	public DeleteRequestHandler(String name, long timeout, IDeletePolling manager, Log log) {
		super(name, timeout);
		
		_manager = manager;
		_log = log.logger(this.getClass());
		_gson = new Gson();
	}
	
	@Override
	public void transactionProcess(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
//		{"jsonrpc":"2.0","method":"stop.part","id":"1","param":{"protocol.version":"1.0","event.id":"event1"}}
		String sId = null;
		String sResContents = null;
		try {
			String jsonContents = new String(inboundCtx.getContent().array(), "UTF-8");
			StopRequest req = _gson.fromJson(jsonContents, StopRequest.class);
			sId = req.getId();
			
			String protocolVerion = req.getParam().getVersion();
			String protocolMethod = req.getMethod();
			if(!ProtocolCollection.PROTOCOL_VERSION.equals(protocolVerion)) {
				throw new ApplicationException("프로토콜 버전이 일치 하지 않습니다. 프로토콜 버전을 확인해주세요");
			}else if(!ADAPTER_HANDLER_PROTOCOL_METHOD_NAME.equals(protocolMethod)) {
				throw new ApplicationException("프로토콜 기능명이 일치 하지 않습니다. 기능명을 확인해주세요");
			}
			
			String sPath = makePath(req);
			String sEventId = req.getParam().getEventID();
	
			_manager.delete(sPath);

			//정상 Start 후 응답
			sResContents = ProtocolCollection.makeSuccessStopResponseJson(sId, sEventId);
		}catch(Exception e) {
			//비정상 Start 후 응답
			_log.err(e);
			sResContents = ProtocolCollection.makeFailStopResponseJson(sId, "-1", e.getMessage());
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


	/**
	 * 핸들러 풀경로를 만들어 준다.
	 * 경로 : read/polling/프로토콜event.id
	 * @param req
	 * @return
	 */
	public static String makePath(StopRequest req) {

		StringBuffer sbPath = new StringBuffer();
		sbPath.append("read/");
		sbPath.append("polling/");
		sbPath.append(req.getParam().getEventID());
		
		return sbPath.toString();
	}
}
