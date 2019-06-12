package com.hdbsnc.smartiot.adapter.mb.mc.bin.processor.handler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.hdbsnc.smartiot.adapter.mb.mc.bin.api.MitsubishiQSeriesApi;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.api.frame.exception.MitsubishiQSeriesMCCompleteException;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.obj.RequestReadObj;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.obj.RequestReadObj.FormatterObj;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.obj.RequestWriteObj;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.util.EditUtil;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractTransactionTimeoutFunctionHandler;
import com.hdbsnc.smartiot.common.otp.url.parser.UrlParseException;
import com.hdbsnc.smartiot.common.otp.url.parser.UrlParser;
import com.hdbsnc.smartiot.util.logger.Log;

/**
 * @author DBeom 읽기및 쓰기를 하기 위한 핸들러
 */
public class BatchPollingProcessHandler extends AbstractTransactionTimeoutFunctionHandler {

	private static final int BLOCK_SIZE = 4;

	private MitsubishiQSeriesApi api;
	private List<RequestReadObj> readObjList;
	private List<RequestWriteObj> writeObjList;

	private Log _log = null;
	private Log _parentsLog = null;

	private JSONObject _schema;

	public BatchPollingProcessHandler(String name, long timeout, MitsubishiQSeriesApi api,
			List<RequestReadObj> readObjList, List<RequestWriteObj> writeObjList, JSONObject schema, Log log) {
		super(name, timeout);
		this.api = api;
		this.readObjList = readObjList;
		this.writeObjList = writeObjList;
		this._parentsLog = log;
		_schema = schema;
	}

	@Override
	public void transactionProcess(IContext inboundCtx, OutboundContext outboundCtx) throws UrlParseException {

		if (_log == null) {
			String curtPath = (this.currentPathString().replace("/", ".")).replace("root.", "");
			this._log = _parentsLog.logger(curtPath);
		}

		try {
			try {
				// 연결이 되어 있는지 확인
				if (!api.isConnected()) {
					_log.debug("재연결 시도 1.");
					try {
						// 연결이 안되어있다면 재연결
						api.disconnect();
						api.reConnect();
					} catch (Exception e) {
						throw e;
					}
				}

				String rowData = "";
				String tmp = "";

				for (int i = 0; i < readObjList.size(); i++) {
					tmp = api.read(readObjList.get(i));
					// 블록읽괄읽기가 아니라 일괄읽기 이기때문에 데이터를 모두 붙여야함
					rowData += tmp;
				}

				String jsonData = hexToJson(rowData, readObjList, _schema);
				outboundCtx.getPaths().add("ack");
				outboundCtx.setSID(inboundCtx.getSID());
				outboundCtx.setSPort(inboundCtx.getSPort());
				outboundCtx.setTID(inboundCtx.getTID());
				outboundCtx.setTPort(inboundCtx.getTPort());
				outboundCtx.setTransmission("res");
				outboundCtx.setContenttype("json");
				outboundCtx.setContent(ByteBuffer.wrap(jsonData.getBytes()));

				_log.trace(UrlParser.getInstance().convertToString(outboundCtx));

			} catch (MitsubishiQSeriesMCCompleteException e) {
				throw e;
			} catch (IOException e) {
				_log.err(e);
				_log.debug("재연결 시도 2.");
				try {
					api.reConnect();

					String rowData = "";
					String tmp = "";

					for (int i = 0; i < readObjList.size(); i++) {
						tmp = api.read(readObjList.get(i));
						// 블록읽괄읽기가 아니라 일괄읽기 이기때문에 데이터를 모두 붙여야함
						rowData += tmp;
					}

					String jsonData = hexToJson(rowData, readObjList, _schema);
					outboundCtx.getPaths().add("ack");
					outboundCtx.setSID(inboundCtx.getSID());
					outboundCtx.setSPort(inboundCtx.getSPort());
					outboundCtx.setTID(inboundCtx.getTID());
					outboundCtx.setTPort(inboundCtx.getTPort());
					outboundCtx.setTransmission("res");
					outboundCtx.setContenttype("json");
					outboundCtx.setContent(ByteBuffer.wrap(jsonData.getBytes()));
					_log.trace(UrlParser.getInstance().convertToString(outboundCtx));

				} catch (Exception e1) {
					throw e1;
				}
			} catch (Exception e) {
				throw e;
			}
		} catch (Exception e) {
			_log.err(e);
			outboundCtx.getPaths().add("nack");
			outboundCtx.setSID(inboundCtx.getSID());
			outboundCtx.setSPort(inboundCtx.getSPort());
			outboundCtx.setTID(inboundCtx.getTID());
			outboundCtx.setTPort(inboundCtx.getTPort());
			if (outboundCtx.getParams() == null)
				outboundCtx.setParams(new HashMap<String, String>());
			outboundCtx.getParams().put("msg", e.getMessage());
			outboundCtx.getParams().put("type", "error");
			outboundCtx.setTransmission("res");
		}

	}

	/**
	 * Hex코드를 Jon형태로 가공
	 * 
	 * @param packet
	 * @return
	 * @throws Exception
	 */
	private String hexToJson(String hexData, List<RequestReadObj> reqReadObjList, JSONObject resultObj)
			throws Exception {
		// payload가 있다면 아래에 json으로 가공함
		String subHexData;

		int index = 0; // 문자열 시작
		int length = 0;// 문자열 사이즈

		// 스코어와 시작 지점을 계산하여 필요한 부분만 잘라 내 준다.
		for (RequestReadObj readObj : reqReadObjList) {
			length = Integer.parseInt(readObj.getDevScore()) * BLOCK_SIZE;
			subHexData = hexData.substring(index, index + length);
			index += length;

			jsonFormatterData(subHexData, readObj, resultObj);
		}

		return resultObj.toJSONString();
	}

	/**
	 * 사용자가 정의한 formatter를 보고 Json의 깊이까지 들어가서 Json객체를 추가 한다.
	 * 
	 * @param hexData
	 * @param readObj
	 * @param resultObj
	 * @throws Exception
	 */
	private void jsonFormatterData(String hexData, RequestReadObj readObj, JSONObject resultObj) throws Exception {

		String[] keys;

		JSONObject targetJsonObj = null;
		JSONObject tempJsonObj = resultObj;
		JSONArray tmpArray = null;
		int index;

		// formatter에 정의되어 있는 내용을 하나씩 가지고 온다.
		for (FormatterObj formatter : readObj.getFormater()) {
			// 사용자가 정의한 formatterName
			keys = formatter.getName().split("\\.");
			// tempJsonObj에 틀이되는 Json을 담아 준다.
			tempJsonObj = resultObj;

			// key가 -1이 될때까지 반복해준다.
			for (index = 0; index < keys.length - 1; index++) {

				// 선택된 객체가 Object형태라면 Object 형으로 한번안으로 들어간다.
				if (tempJsonObj.get(keys[index]) instanceof JSONObject) {
					targetJsonObj = (JSONObject) tempJsonObj.get(keys[index]);
					tempJsonObj = targetJsonObj;
				} else {
					// 선택된 객체가 List라면 List형으로 한번안으로 들어간다.
					tmpArray = (JSONArray) tempJsonObj.get(keys[index]);
					tempJsonObj = targetJsonObj;
				}
			}

			// tartJsonObj가 null이라면 tempJson으로 취환한다.
			if (targetJsonObj == null)
				targetJsonObj = resultObj;

			// 값을 넣어야 하는 Json이 Object라면 Object객체를 넣는 함수를 호출한다.
			if (resultObj.get(keys[index - 1]) instanceof JSONObject) {
				putStringIntoObj(keys[keys.length - 1], EditUtil.parserRecvData(hexData, formatter.getPattern()),
						formatter.getType(), targetJsonObj);
			} else if (resultObj.get(keys[index - 1]) instanceof JSONArray) {
				// 값을 넣어야 하는 Json이 List라면 List객체에 값을 넣는 함수를 호출한다.
				putStringIntoList(keys[keys.length - 1], EditUtil.parserRecvData(hexData, formatter.getPattern()),
						formatter.getType(), formatter.getIndex(), tmpArray);
			} else {
				// JSONObject, JSONList 둘다 아니라면 사용자 형식이 잘못되었기 때문에 예외를 던저준다.
				throw new Exception("사용자가 정의한 형식이 올바르지 않습니다. 다시 한번 확인해주세요");
			}
		}
	}

	/**
	 * JsonObjec에 Key,Value 하나를 추가 한다.
	 * 
	 * @param key
	 * @param value
	 * @param targetJsonObj
	 */
	@SuppressWarnings("unchecked")
	private void putStringIntoObj(String key, String value, String type, JSONObject targetJsonObj) {

		if (type.equals("INTEGER")) {
			targetJsonObj.put(key, Integer.parseInt(value));
		} else if (type.equals("STRING")) {
			targetJsonObj.put(key, value);
		} else if (type.equals("LONG")) {
			targetJsonObj.put(key, Long.parseLong(value));
		}
	}

	/**
	 * JSONList안에 Index가 맞는 곳에 JsonObject를 추가한다.
	 * 
	 * @param key
	 * @param value
	 * @param index
	 * @param targetJsonArray
	 */
	@SuppressWarnings("unchecked")
	private void putStringIntoList(String key, String value, String type, String index, JSONArray targetJsonArray) {

		if (type.equals("INTEGER")) {
			((JSONObject) targetJsonArray.get(Integer.parseInt(index))).put(key, Integer.parseInt(value));
		} else if (type.equals("LONG")) {
			((JSONObject) targetJsonArray.get(Integer.parseInt(index))).put(key, Long.parseLong(value));
		} else {
			((JSONObject) targetJsonArray.get(Integer.parseInt(index))).put(key, value);
		}
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
