
package com.hdbsnc.smartiot.adapter.mb.mc.bin.handler;

import java.nio.ByteBuffer;

import com.google.gson.Gson;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.api.frame.exception.ApplicationException;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.api.frame.exception.MCProtocolResponseException;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.handler.manager.ICreatePolling;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.handler.manager.ICreatePolling.HandlerType;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.protocol.obj.StartRequest;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.util.ProtocolCollection;
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
		String sEventId = null;
		byte[] sResContents = null;
		try {
			String jsonContents = new String(inboundCtx.getContent().array(), "UTF-8");
			StartRequest req = _gson.fromJson(jsonContents, StartRequest.class);
			sId = req.getId();
			sEventId = req.getParam().getEventID();
						
			String protocolVerion = req.getParam().getVersion();
			String protocolMethod = req.getMethod();
			if(!ProtocolCollection.PROTOCOL_VERSION.equals(protocolVerion)) {
				throw new ApplicationException("-33102", String.format("프로토콜 버전이 일치하지 않습니다(%s)", protocolVerion));
			}else if(!ADAPTER_HANDLER_PROTOCOL_METHOD_NAME.equals(protocolMethod)) {
				throw new ApplicationException("-33103", String.format("지원하는 않는 Method 입니다(%s)", protocolMethod));
			}
			
			String sPath = makePath(req);
	
//			int iPollingIntervalSec = Integer.parseInt(req.getParam().getPollingPeriod());
			int iPollingIntervalSec = Integer.parseInt(req.getParam().getPollingPeriod()) * 1000;
			String sIP = req.getParam().getPlcIp();
			int iPort = Integer.parseInt(req.getParam().getPlcPort());
			
			_manager.start(HandlerType.READ_BATCH_PROCESS_HANDLER, sPath, sIP, iPort, iPollingIntervalSec, req);

			//정상 Start 후 응답
			sResContents = ProtocolCollection.makeSuccessStartResponseJson(sId, sEventId);
		} catch (MCProtocolResponseException e) {
			_log.warn(e.getMessage());
			sResContents = ProtocolCollection.makeFailStartResponseJson(sId, sEventId, e.getCode(), e.getMsg());
		} catch(ApplicationException e) {
			_log.warn(e.getMessage());
			sResContents = ProtocolCollection.makeFailStartResponseJson(sId, sEventId, e.getCode(), e.getMsg());
		} catch(Exception e) {
			//비정상 Start 후 응답
			_log.warn(e.getMessage());
			sResContents = ProtocolCollection.makeFailStartResponseJson(sId, sEventId,"-33100", "PLC 수집시작 핸들러 생성에 실패 하였습니다.");
		}

		outboundCtx.getPaths().add("ack");
		outboundCtx.setTID("this");
		outboundCtx.setTransmission("res");
		outboundCtx.setContenttype("json");
		outboundCtx.setContent(ByteBuffer.wrap(sResContents));
		_log.trace(UrlParser.getInstance().convertToString(outboundCtx));
	}
	 
	@Override
	public void rejectionProcess(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		String jsonContents = new String(inboundCtx.getContent().array(), "UTF-8");
		StartRequest req = _gson.fromJson(jsonContents, StartRequest.class);
		String sId = req.getId();
		String sEventId = req.getParam().getEventID();
		
		byte[] sResContents = ProtocolCollection.makeFailStartResponseJson(sId, sEventId, "-33101", "PLC 수집시작 핸들러의 트랜젝션이 잠겨 있습니다");

		outboundCtx.getPaths().add("nack");
		outboundCtx.setTID("this");
		outboundCtx.setTransmission("res");
		outboundCtx.setContenttype("json");
		outboundCtx.setContent(ByteBuffer.wrap(sResContents));

		_log.warn("핸들러 트랜젝션 경고 : " + UrlParser.getInstance().convertToString(outboundCtx));		
	}
	
	/**
	 * 생성할 핸들러의 경로를 만들어 준다.
	 * 경로 : read/polling/프로토콜event.id
	 * @param req
	 * @return
	 */
	private String makePath(StartRequest req) {

		StringBuffer sbPath = new StringBuffer();
		sbPath.append("read/");
		sbPath.append("polling/");
		sbPath.append(req.getParam().getEventID());
		
		return sbPath.toString();
	}
}
