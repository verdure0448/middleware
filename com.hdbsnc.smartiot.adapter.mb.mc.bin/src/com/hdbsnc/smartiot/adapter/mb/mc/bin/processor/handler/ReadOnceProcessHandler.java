package com.hdbsnc.smartiot.adapter.mb.mc.bin.processor.handler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.api.MitsubishiQSeriesApi;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.api.frame.AbstractBlocksFrame.TransMode;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.api.frame.exception.ApplicationException;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.api.frame.exception.MCProtocolResponseException;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.handler.manager.DynamicHandlerManager;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.handler.manager.DynamicHandlerManager.ManagerVo;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.protocol.obj.ReadOnceRequest;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.util.ProtocolCollection;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractTransactionTimeoutFunctionHandler;
import com.hdbsnc.smartiot.common.otp.url.parser.UrlParser;
import com.hdbsnc.smartiot.util.logger.Log;

/**
 * PLC 한번 읽기 요청에 대하여 PLC읽은 결과를 OutboundContext로 전달 MQ API객체는 요청 IP,PORT로 검색하여
 * API가 존재할 경우 DynamicHandlerManager의 API를 재사용, 존재하지 않을 경우에는 해당 IP, PORT로 생성하여
 * 사용후 해제(disconnect)한다.
 * 
 * @author dhkang
 */
public class ReadOnceProcessHandler extends AbstractTransactionTimeoutFunctionHandler {

	private static final String ADAPTER_HANDLER_PROTOCOL_METHOD_NAME = "readonce";
	
	private Log _log;
	private DynamicHandlerManager _manager;

	public ReadOnceProcessHandler(String name, long timeout, DynamicHandlerManager manager, Log log) {
		super(name, timeout);

		_manager = manager;
		_log = log.logger(this.getClass());
	}

	@Override
	public void transactionProcess(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {

		// PLC DATA Key,Value 저장
		// Key = startRequest, Value = PLC로부터 수집한 값
		Map<String, String> plcData;
		byte[] sContents = null;

		Gson gson = new Gson();
		String jsonContents = new String(inboundCtx.getContent().array(), "UTF-8");
		ReadOnceRequest req = gson.fromJson(jsonContents, ReadOnceRequest.class);
		
		String protocolVerion = req.getParam().getVersion();
		String protocolMethod = req.getMethod();
		if(!ProtocolCollection.PROTOCOL_VERSION.equals(protocolVerion)) {
			throw new ApplicationException("-33502", String.format("프로토콜 버전이 일치하지 않습니다(%s)", protocolVerion));
		}else if(!ADAPTER_HANDLER_PROTOCOL_METHOD_NAME.equals(protocolMethod)) {
			throw new ApplicationException("-33503", String.format("지원하는 않는 Method 입니다(%s)", protocolMethod));
		}
		
		String jsonID = req.getId();
		String plcIP = req.getParam().getPlcIp();
		int plcPort = Integer.parseInt(req.getParam().getPlcPort());

		try {
			MitsubishiQSeriesApi mqApi;
			// 이미 만들어진 객체가 존재한다면 예외처리
			if (_manager.isUseManager(plcIP, plcPort)) {
				throw new ApplicationException("-33504", String.format("이미 기동중인 IP(%s), Port(%s)입니다", plcIP, plcPort));
			}
			// 기존의 객체가 존재하지 않는다면 객체를 만들어서 Read
			else {
				mqApi = new MitsubishiQSeriesApi(TransMode.BINARY, _log);
				mqApi.connect(plcIP, plcPort);
			}

			plcData = plcRead(mqApi, req);
			sContents = ProtocolCollection.makeSucessReadOnceResJson(jsonID, plcData);
			mqApi.disconnect();
		}  catch (MCProtocolResponseException e) {
			_log.warn(e.getMessage());
			sContents = ProtocolCollection.makeFailReadOnceResJson(jsonID, e.getCode(), e.getMsg());
		} catch(ApplicationException e) {
			_log.warn(e.getMessage());
			sContents = ProtocolCollection.makeFailReadOnceResJson(jsonID, e.getCode(), e.getMsg());
		} catch (Exception e) {
			_log.err(e);
			// TODO 에러코드 할당후 수정 필요
			sContents = ProtocolCollection.makeFailReadOnceResJson(jsonID, "-33500", e.getMessage());
		} 

		outboundCtx.getPaths().add("ack");
		outboundCtx.setTID("this");
		outboundCtx.setTransmission("res");
		outboundCtx.setContenttype("json");
		outboundCtx.setContent(ByteBuffer.wrap(sContents));
		_log.trace(UrlParser.getInstance().convertToString(outboundCtx));
	}

	/**
	 * 수집된 값을 Key, value로 값을 반환한다.
	 * 
	 * @param outboundCtx
	 * @return
	 * @throws Exception
	 */
	private Map<String, String> plcRead(MitsubishiQSeriesApi mqApi, ReadOnceRequest req)
			throws IOException, MCProtocolResponseException, Exception {
		Map<String, String> result = new HashMap<String, String>();
		String sRawData;
		String sDevCode, sDevNum, sDevScore;
		ReadOnceRequest.Items[] items = req.getParam().getItems();
		ReadOnceRequest.Items item = null;

		for (int i = 0; i < items.length; i++) {
			item = items[i];
			sDevCode = item.getDeviceCode();
			sDevNum = item.getDeviceNum();
			sDevScore = item.getDeviceScore();

			sRawData = mqApi.read(sDevCode, sDevNum, sDevScore);

			result.put(item.getKey(), sRawData);
		}

		return result;
	}

	@Override
	public void rejectionProcess(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {

		Gson gson = new Gson();
		String jsonContents = new String(inboundCtx.getContent().array(), "UTF-8");
		ReadOnceRequest req = gson.fromJson(jsonContents, ReadOnceRequest.class);

		String jsonID = req.getId();
		
		byte[] bContents = ProtocolCollection.makeRejectionResponseJson(jsonID, "-33501",
				"PLC데이터수집 핸들러의 트랜젝션이 잠겨 있습니다");

		outboundCtx.getPaths().add("nack");
		outboundCtx.setTID("this");
		outboundCtx.setTransmission("res");
		outboundCtx.setContenttype("json");
		outboundCtx.setContent(ByteBuffer.wrap(bContents));

		_log.warn("핸들러 트랜젝션 경고 : " + UrlParser.getInstance().convertToString(outboundCtx));
	}
}
