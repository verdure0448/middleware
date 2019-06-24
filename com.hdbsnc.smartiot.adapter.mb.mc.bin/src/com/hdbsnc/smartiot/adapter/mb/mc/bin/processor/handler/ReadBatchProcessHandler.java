package com.hdbsnc.smartiot.adapter.mb.mc.bin.processor.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.hdbsnc.smartiot.adapter.mb.mc.bin.api.MitsubishiQSeriesApi;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.protocol.obj.StartRequest;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.protocol.obj.StartRequest.Items;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.util.Util;
import com.hdbsnc.smartiot.common.aim.IAdapterInstanceManager;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractTransactionTimeoutFunctionHandler;
import com.hdbsnc.smartiot.util.logger.Log;

/**
 * @author dbkim
 * Mc 프로토콜 3E프레임 일괄 읽기를 실행 할 수 있는 핸들러
 */
public class ReadBatchProcessHandler extends AbstractTransactionTimeoutFunctionHandler{

	private static final String ADAPTER_HANDLER_TARGET_ID = "test";
	private static final String ADAPTER_HANDLER_TARGET_HANDLER_PATH = "test";
	
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
		String sContents = null;
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
				sContents = Util.makeSucessPublishJson(sId, sEventId, plcData);
				
				
			} catch (IOException e) {
				_log.err(e);
				_log.debug("재연결 시도 2.");
				try {
					_api.reConnect();
				} catch (Exception e1) {
					throw e1;
				}
				
				plcData = plcRead(outboundCtx);
				sContents = Util.makeSucessPublishJson(sId, sEventId, plcData);
				
			} catch (Exception e) {
				throw e;
			}
		} catch (Exception e) {
			_log.err(e);
			sContents = Util.makeFailPublishJson(sId, sEventId, "-1", e.getMessage());			
		}
		
		Util.callHandler(_aim, ADAPTER_HANDLER_TARGET_HANDLER_PATH, _sid, ADAPTER_HANDLER_TARGET_ID, sContents);
		outboundCtx.dispose();
	}

	/**
	 * 수집된 값을 Key, value로 값을 반환한다.
	 * @param outboundCtx
	 * @return
	 * @throws Exception
	 */
	private Map<String, String> plcRead(OutboundContext outboundCtx) throws Exception {
		Map<String, String> result = new HashMap<String, String>();
		String sRawData;
		String sDevCode, sDevNum, sDevScore;
		Items[] items = _startRequest.getParam().getItems();
		Items item;

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
		// TODO Auto-generated method stub
		
	}
}
