package com.hdbsnc.smartiot.adapter.mb.mc.bin.processor.handler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import com.hdbsnc.smartiot.adapter.mb.mc.bin.api.MitsubishiQSeriesApi;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.api.frame.exception.ApplicationException;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.api.frame.exception.MCProtocolResponseException;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.protocol.obj.StartRequest;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.util.ProtocolCollection;
import com.hdbsnc.smartiot.common.aim.IAdapterInstanceManager;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractTransactionTimeoutFunctionHandler;
import com.hdbsnc.smartiot.common.otp.url.parser.UrlParser;
import com.hdbsnc.smartiot.util.logger.Log;

/**
 * @author dbkim
 * Mc 프로토콜 3E프레임 일괄 읽기를 실행 할 수 있는 핸들러
 */
public class ReadBatchProcessHandler extends AbstractTransactionTimeoutFunctionHandler{

	private static final String ADAPTER_HANDLER_TARGET_ID = "zeromq.1";
	private static final String ADAPTER_HANDLER_TARGET_HANDLER_PATH = "zmq/pub";
	
	private MitsubishiQSeriesApi _api;
	private StartRequest _startRequest;
	private Log _log;
	private IAdapterInstanceManager _aim;
	private String _sid;
	
	public ReadBatchProcessHandler(String name, long timeout, IAdapterInstanceManager aim, String sid, MitsubishiQSeriesApi api, StartRequest startRequest, Log log) {
		super(name, timeout);
		
		_aim = aim;
		_sid = sid;
		_api = api;
		_startRequest = startRequest;
		_log = log.logger(this.getClass());
	}

	@Override
	public void transactionProcess(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		
		//PLC DATA Key,Value 저장 
		//Key = startRequest, Value = PLC로부터 수집한 값
		Map<String, String> plcData;
		byte[] bContents = null;
		String sId = _startRequest.getId();
		String sEventId = _startRequest.getParam().getEventID();
		
		try {
			try {
				// 연결이 되어 있는지 확인
				if (!_api.isConnected()) {
					_log.debug("재연결 시도 1.");
					try {
						// 연결이 안되어있다면 재연결
						_api.disconnect();
						_api.reConnect();
					} catch (Exception e) {
						throw e;
					}
				}

				plcData = plcRead(outboundCtx);
				bContents = ProtocolCollection.makeSucessPublishJson(sId, sEventId, plcData);
			} catch(ApplicationException e) {
				throw e;
			} catch(MCProtocolResponseException e) {
				//PLC Response에러 응답
				throw e;
			} catch (IOException e) {
				_log.err(e);
				_log.debug("재연결 시도 2.");
				try {
					_api.reConnect();
				} catch (Exception e1) {
					throw e1;
				}
				
				plcData = plcRead(outboundCtx);
				bContents = ProtocolCollection.makeSucessPublishJson(sId, sEventId, plcData);
				
			} catch (Exception e) {
				throw e;
			}
		} catch(ApplicationException e) {
			_log.err(e);
			bContents = ProtocolCollection.makeFailPublishJson(sId, sEventId, e.getCode(), e.getMsg());
		} catch(MCProtocolResponseException e) {
			//PLC Response에러 응답
			_log.err(e);
			bContents = ProtocolCollection.makeFailPublishJson(sId, sEventId, e.getCode(), e.getMsg());			
		} catch (Exception e) {
			_log.err(e);
			bContents = ProtocolCollection.makeFailPublishJson(sId, sEventId, "-33005", e.getMessage());			
		}
		
		_log.debug("[PUB] " + new String(bContents, "UTF-8"));
		ProtocolCollection.callHandler(_aim, ADAPTER_HANDLER_TARGET_HANDLER_PATH, _sid, ADAPTER_HANDLER_TARGET_ID, bContents);
		outboundCtx.dispose();
	}

	/**
	 * 수집된 값을 Key, value로 값을 반환한다.
	 * @param outboundCtx
	 * @return
	 * @throws Exception
	 */
	private Map<String, String> plcRead(OutboundContext outboundCtx) throws IOException, MCProtocolResponseException, Exception  {
		Map<String, String> result= new HashMap<String, String>();
		String sRawData;
		String sDevCode, sDevNum, sDevScore;
		StartRequest.Items[] items = _startRequest.getParam().getItems();
		StartRequest.Items item;

		for (int i = 0; i < items.length; i++) {
			item = items[i];
			sDevCode = item.getDeviceCode();
			sDevNum = item.getDeviceNum();
			sDevScore = item.getDeviceScore();
			
			sRawData = _api.read(sDevCode,sDevNum, sDevScore);

			result.put(item.getKey(), sRawData);
		}
		
		return result;
	}
	
	@Override
	public void rejectionProcess(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {

		String sId = _startRequest.getId();
		String sEventId = _startRequest.getParam().getEventID();
		
		byte[] sContents = ProtocolCollection.makeFailPublishJson(sId, sEventId, "-33001", "PLC데이터수집 핸들러의 트랜젝션이 잠겨 있습니다");			

		outboundCtx.getPaths().add("nack");
		outboundCtx.setTID("this");
		outboundCtx.setTransmission("res");
		outboundCtx.setContenttype("json");
		outboundCtx.setContent(ByteBuffer.wrap(sContents));

		_log.warn("핸들러 트랜젝션 경고 : " + UrlParser.getInstance().convertToString(outboundCtx));
	}
}
