package com.hdbsnc.smartiot.adapter.mb.mc.bin.handler;

import java.nio.ByteBuffer;

import com.google.gson.Gson;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.api.frame.exception.ApplicationException;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.handler.manager.IRunningStatus;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.protocol.obj.StartRequest;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.protocol.obj.StatusRequest;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.util.ProtocolCollection;
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
		byte[] sResContents = null;
		try {
			String jsonContents = new String(inboundCtx.getContent().array(), "UTF-8");
			StatusRequest req = _gson.fromJson(jsonContents, StatusRequest.class);
			sId = req.getId();
			
			String protocolVerion = req.getParam().getVersion();
			String protocolMethod = req.getMethod();
			if(!ProtocolCollection.PROTOCOL_VERSION.equals(protocolVerion)) {
				throw new ApplicationException("-33402", String.format("프로토콜 버전이 일치하지 않습니다(%s)", protocolVerion));
			}else if(!ADAPTER_HANDLER_PROTOCOL_METHOD_NAME.equals(protocolMethod)) {
				throw new ApplicationException("-33403", String.format("지원하는 않는 Method 입니다(%s)", protocolMethod));
			}
			
			Object[] statusMap = _manager.statusAll();

			//정상 Start 후 응답
			sResContents = ProtocolCollection.makeSuccessStatusResponseJson(sId, statusMap);
		}catch(Exception e) {
			//비정상 Start 후 응답
			_log.warn(e.getMessage());
			sResContents = ProtocolCollection.makeFailStopAllResponseJson(sId, "-33400", "PLC 수집 조회 핸들러 호출에 실패 하였습니다");
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
		StatusRequest req = _gson.fromJson(jsonContents, StatusRequest.class);
		String sId = req.getId();
		
		byte[] sResContents = ProtocolCollection.makeFailStatusResponseJson(sId, "-33401", "PLC 수집조회 핸들러의 트랜젝션이 잠겨 있습니다");

		outboundCtx.getPaths().add("nack");
		outboundCtx.setTID("this");
		outboundCtx.setTransmission("res");
		outboundCtx.setContenttype("json");
		outboundCtx.setContent(ByteBuffer.wrap(sResContents));

		_log.warn("핸들러 트랜젝션 경고 : " + UrlParser.getInstance().convertToString(outboundCtx));		
	}

}
