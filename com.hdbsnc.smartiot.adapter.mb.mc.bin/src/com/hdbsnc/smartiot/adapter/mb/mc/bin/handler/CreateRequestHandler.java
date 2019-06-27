
package com.hdbsnc.smartiot.adapter.mb.mc.bin.handler;

import java.nio.ByteBuffer;

import com.google.gson.Gson;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.handler.manager.ICreatePolling;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.handler.manager.ICreatePolling.HandlerType;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.protocol.obj.StartRequest;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.util.Util;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractTransactionTimeoutFunctionHandler;
import com.hdbsnc.smartiot.common.otp.url.parser.UrlParser;
import com.hdbsnc.smartiot.util.logger.Log;

/**
 * @author dbkim
 * 핸들러를 생성 한다.
 * 생성정보는 [PLC 수집시작 프로토콜] 명세서를 따른다.
 * 생성 후 결과에 대한 RES를 전달한다.
 */
public class CreateRequestHandler extends AbstractTransactionTimeoutFunctionHandler {
	
	private static final String ADAPTER_HANDLER_PROTOCOL_METHOD_NAME = "start";
	
	private ICreatePolling _manager;
	private Log _log;
	private Gson _gson;
	
	public CreateRequestHandler(String name, long timeout, ICreatePolling manager, Log log) {
		super(name, timeout);
		
		_manager = manager;
		_log = log.logger(this.getClass());
		_gson = new Gson();
	}
	
	@Override
	public void transactionProcess(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
//		{"jsonrpc":"2.0","method":"start","id":"1","param":{"protocol.version":"1.0","event.id":"event1","plc.ip":"127.0.0.1","plc.port":"8192","polling.period":"3","items":[{"key":"lot","device.code":"D*","device.num":"10000","device.score":"4"},{"key":"quality","device.code":"D*","device.num":"10004","device.score":"1"}]}}

		//호출한 상대의 tid 및 path를 가지고 옴 
		//호출자의 tid 및 path인지 확인필요
		//호출자의 tid 및 path가 아니라면 아래 주석 과정 수행
		String sId = null;
		String sResContents = null;
		try {
			String jsonContents = new String(inboundCtx.getContent().array(), "UTF-8");
			StartRequest req = _gson.fromJson(jsonContents, StartRequest.class);
			sId = req.getId();
						
			String protocolVerion = req.getParam().getVersion();
			String protocolMethod = req.getMethod();
			if(!Util.PROTOCOL_VERSION.equals(protocolVerion)) {
				throw new Exception("프로토콜 버전 불일치 .");
			}else if(!ADAPTER_HANDLER_PROTOCOL_METHOD_NAME.equals(protocolMethod)) {
				throw new Exception("지원하지 않는 Method 요청.");
			}
			
			String sPath = makePath(req);
			String sEventId = req.getParam().getEventID();
	
			int iPollingIntervalSec = Integer.parseInt(req.getParam().getPollingPeriod());
			String sIP = req.getParam().getPlcIp();
			int iPort = Integer.parseInt(req.getParam().getPlcPort());
			
			_manager.start(HandlerType.READ_BATCH_PROCESS_HANDLER, sPath, sIP, iPort, iPollingIntervalSec, req);

			//정상 Start 후 응답
			sResContents = Util.makeSuccessStartResponseJson(sId, sEventId);
		}catch(Exception e) {
			//비정상 Start 후 응답
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

	/**
	 * 생성할 핸들러의 경로를 만들어 준다.
	 * 경로 : read/polling/프로토콜id/프로토콜event.id
	 * @param req
	 * @return
	 */
	private String makePath(StartRequest req) {

		StringBuffer sbPath = new StringBuffer();
		sbPath.append("read/");
		sbPath.append("polling/");
		sbPath.append(req.getId());
		sbPath.append("/");
		sbPath.append(req.getParam().getEventID());
		
		return sbPath.toString();
	}
}
