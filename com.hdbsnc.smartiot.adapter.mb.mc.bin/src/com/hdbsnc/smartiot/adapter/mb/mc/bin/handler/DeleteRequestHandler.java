
package com.hdbsnc.smartiot.adapter.mb.mc.bin.handler;

import com.google.gson.Gson;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.handler.manager.IDeletePolling;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.protocol.obj.StopRequest;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.util.Util;
import com.hdbsnc.smartiot.common.aim.IAdapterInstanceManager;
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

	private static final String ADAPTER_HANDLER_TARGET_ID = "test";
	private static final String ADAPTER_HANDLER_TARGET_HANDLER_PATH = "test";
	
	private IDeletePolling _manager;
	private IAdapterInstanceManager _aim;
	private Log _log;
	private String _sid;
	private Gson _gson;
	
	public DeleteRequestHandler(String name, long timeout, String sid, IDeletePolling manager, IAdapterInstanceManager aim, Log log) {
		super(name, timeout);
		
		_manager = manager;
		_aim = aim;
		_log = log.logger(this.getClass());
		_sid = sid;
		 _gson = new Gson();
	}
	@Override
	public void transactionProcess(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
//		STOP JSON
//		{"jsonrpc":"2.0","method":"stop.part","id":"1","param":{"protocol.version":"1.0","event.id":"event1"}}
		String sId = null;
		String sResContents = null;
		try {
			String jsonContents = new String(inboundCtx.getContent().array(), "UTF-8");
			StopRequest req = _gson.fromJson(jsonContents, StopRequest.class);
			sId = req.getId();
			
			String protocolVerion = req.getParam().getVersion();
			if(Util.PROTOCOL_VERSION.equals(protocolVerion)) {
				throw new Exception("프로토콜 버전이 일치 하지 않습니다. 프로토콜 버전을 확인해주세요");
			}
			
			String sPath = makePath(req);
			String sEventId = req.getParam().getEventID();
	
			_manager.delete(sPath);

			//정상 Start 후 응답
			sResContents = Util.makeSuccessStopResponseJson(sId, sEventId);
		}catch(Exception e) {
			//비정상 Start 후 응답
			sResContents = Util.makeFailStopResponseJson(sId, "-1", e.getMessage());
		}

		Util.callHandler(_aim, ADAPTER_HANDLER_TARGET_HANDLER_PATH, _sid, ADAPTER_HANDLER_TARGET_ID, sResContents);
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

		_log.warn("핸들러 트랜젝션 경고 : " + UrlParser.getInstance().convertToString(outboundCtx));		
	}

	/**
	 * 삭제할 핸들러의 경로를 만들어 준다.
	 * 경로 : read/polling/프로토콜id/프로토콜event.id
	 * @param req
	 * @return
	 */
	private String makePath(StopRequest req) {

		StringBuffer sbPath = new StringBuffer();
		sbPath.append("read/");
		sbPath.append("polling/");
		sbPath.append(req.getId());
		sbPath.append("/");
		sbPath.append(req.getParam().getEventID());
		
		return sbPath.toString();
	}
}
