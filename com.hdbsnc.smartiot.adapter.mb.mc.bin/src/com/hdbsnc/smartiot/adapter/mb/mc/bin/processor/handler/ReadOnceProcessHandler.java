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
import com.hdbsnc.smartiot.adapter.mb.mc.bin.protocol.obj.StartRequest;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.util.ProtocolCollection;
import com.hdbsnc.smartiot.common.aim.IAdapterInstanceManager;
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

	private Log _log;
	private IAdapterInstanceManager _aim;
	private String _sid;
	private DynamicHandlerManager _manager;

	private String jsonID = null;;

	public ReadOnceProcessHandler(String name, long timeout, IAdapterInstanceManager aim, DynamicHandlerManager manager,
			String sid, Log log) {
		super(name, timeout);

		_aim = aim;
		_sid = sid;
		_log = log.logger(this.getClass());
	}

	@Override
	public void transactionProcess(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {

		// PLC DATA Key,Value 저장
		// Key = startRequest, Value = PLC로부터 수집한 값
		Map<String, String> plcData;

		Gson gson = new Gson();
		String jsonContents = new String(inboundCtx.getContent().array(), "UTF-8");
		ReadOnceRequest req = gson.fromJson(jsonContents, ReadOnceRequest.class);

		this.jsonID = req.getId();
		String plcIP = req.getParam().getPlcIp();
		int plcPort = Integer.parseInt(req.getParam().getPlcPort());
		ManagerVo managerVo = _manager.getManagerInstance(plcIP, plcPort);

		managerVo.isUse = true;
		boolean isCreateMQApi = false;
		MitsubishiQSeriesApi mqApi = managerVo.getMQApi();
		if (mqApi == null) {
			mqApi = new MitsubishiQSeriesApi(TransMode.BINARY, _log);
			mqApi.connect(plcIP, plcPort);
			isCreateMQApi = true;
		}

		try {
			plcData = plcRead(mqApi, req);

			// TODO 정상응답 구현
		} catch (ApplicationException e) {
			_log.err(e);
			// TODO 이상응답 구현
		} catch (MCProtocolResponseException e) {
			_log.err(e);
			// TODO 이상응답 구현
		} catch (Exception e) {
			_log.err(e);
			// TODO 이상응답 구현
		} finally {
			if (isCreateMQApi) {
				mqApi.disconnect();
			}
			managerVo.isUse = false;
		}
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
		;

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

		// TODO 에러코드 할당후 수정 필요
		byte[] sContents = ProtocolCollection.makeRejectionResponseJson(this.jsonID, "-3xxxx",
				"PLC데이터수집 핸들러의 트랜젝션이 잠겨 있습니다");

		outboundCtx.getPaths().add("nack");
		outboundCtx.setTID("this");
		outboundCtx.setTransmission("res");
		outboundCtx.setContenttype("json");
		outboundCtx.setContent(ByteBuffer.wrap(sContents));

		_log.warn("핸들러 트랜젝션 경고 : " + UrlParser.getInstance().convertToString(outboundCtx));
	}
}
