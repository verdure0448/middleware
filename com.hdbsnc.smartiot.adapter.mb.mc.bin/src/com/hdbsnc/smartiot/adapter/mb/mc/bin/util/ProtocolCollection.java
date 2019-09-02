package com.hdbsnc.smartiot.adapter.mb.mc.bin.util;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;

import com.google.gson.Gson;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.protocol.obj.CommonResponse;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.protocol.obj.GatheringPublish;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.protocol.obj.ReadOnceResponse;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.protocol.obj.ResError;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.protocol.obj.StartRequest;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.protocol.obj.StartResponse;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.protocol.obj.StatusResponse;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.protocol.obj.StopAllResponse;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.protocol.obj.StopResponse;
import com.hdbsnc.smartiot.common.aim.IAdapterInstanceManager;
import com.hdbsnc.smartiot.common.context.impl.InnerContext;

public class ProtocolCollection {

	//성공 시 
	public final static String PROTOCOL_VERSION = "0.1.3";
	public final static String JSON_RPC_VERSION= "2.0";
	
	public final static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss.SSS");

	static {
		sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
	}
	
	public synchronized static void callHandler(IAdapterInstanceManager aim, String path, String sid, String tid, byte[] contents) throws Exception {

		InnerContext request = new InnerContext();
		request.setSid(sid);
		request.setTid(tid);
		request.setPaths(Arrays.asList(path.split("/")));
		request.setContent(ByteBuffer.wrap(contents));
		aim.handOverContext(request, null);	
	}
	
	/**
	 * [PLC 수집정보 Publish] 성공시 JSON PROTOCOL을 만들어 준다.
	 * @param id
	 * @param eventId
	 * @param plcData
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public static byte[] makeSucessPublishJson(String id, String eventId, Map<String, String> plcData) throws UnsupportedEncodingException {
		
		String result;
		GatheringPublish pub = new GatheringPublish();
		pub.setJsonrpc(JSON_RPC_VERSION);
		pub.setId(id);
		
		GatheringPublish.Result pubResult = pub.new Result();
		pubResult.setVersion(PROTOCOL_VERSION);
		pubResult.setEventID(eventId);
		pubResult.setProcData(sdf.format(new Date(System.currentTimeMillis())));
		
		GatheringPublish.Items[] itemArray = new GatheringPublish.Items[plcData.size()];
		
		int idx = 0;
		Iterator it = (Iterator) plcData.keySet().iterator();
		String sKey, sValue;
		while(it.hasNext()) {
			sKey = (String) it.next();
			sValue = plcData.get(sKey);
			
			itemArray[idx] = pub.new Items();
			itemArray[idx].setKey(sKey);
			itemArray[idx].setValue(sValue);
			
			idx ++;
		}

		pubResult.setItems(itemArray);
		pub.setResult(pubResult);
		
		result = (new Gson()).toJson(pub)+"\r\n";
		return result.getBytes("UTF-8");
	}
	
	/**
	 * [PLC 수집정보 Publish] 실패시 JSON PROTOCOL을 만들어 준다.
	 * @param id
	 * @param errorCode
	 * @param errorMsg
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public static byte[] makeFailPublishJson(String id, String eventId, String errorCode, String errorMsg) throws UnsupportedEncodingException {

		String result;
		GatheringPublish pub = new GatheringPublish();
		pub.setJsonrpc(JSON_RPC_VERSION);
		pub.setId(id);
		
		GatheringPublish.Result pubResult = pub.new Result();
		pubResult.setEventID(eventId);
		pubResult.setProcData(sdf.format(new Date(System.currentTimeMillis())));
		pubResult.setVersion(PROTOCOL_VERSION );
		
		ResError pubError = new ResError();
		pubError.setCode(errorCode);
		pubError.setMessage(errorMsg);

		pub.setError(pubError);
		pub.setResult(pubResult);
		
		result = (new Gson()).toJson(pub)+"\r\n";
		return result.getBytes("UTF-8");
	}
	
	/**
	 * [PLC 수집시작 프로토콜] 성공시 JSON PROTOCOL을 만들어 준다.
	 * @param id
	 * @param eventId
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public static byte[] makeSuccessStartResponseJson(String id, String eventId) throws UnsupportedEncodingException {
		
		String result;
		StartResponse startRes = new StartResponse();
		startRes.setJsonrpc(JSON_RPC_VERSION);
		startRes.setId(id);
		
		StartResponse.Result startResult = startRes.new Result();
		startResult.setVersion(PROTOCOL_VERSION);
		startResult.setEventID(eventId);
		startResult.setProcData(sdf.format(new Date(System.currentTimeMillis())));
		
		startRes.setResult(startResult);
		
		result = (new Gson()).toJson(startRes)+"\r\n";
		return result.getBytes("UTF-8");
	}
	
	/**
	 * [PLC 수집시작 프로토콜] 실패시 JSON PROTOCOL을 만들어 준다.
	 * @param id
	 * @param eventId
	 * @param errorCode
	 * @param errorMsg
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public static byte[] makeFailStartResponseJson(String id, String eventId, String errorCode, String errorMsg) throws UnsupportedEncodingException {
		
		String result;
		
		StartResponse startRes = new StartResponse();
		startRes.setJsonrpc(JSON_RPC_VERSION);
		startRes.setId(id);

		StartResponse.Result startResult = startRes.new Result();
		startResult.setVersion(PROTOCOL_VERSION);
		startResult.setEventID(eventId);
		startResult.setProcData(sdf.format(new Date(System.currentTimeMillis())));
		
		ResError startError = new ResError();
		startError.setCode(errorCode);
		startError.setMessage(errorMsg);
		
		startRes.setError(startError);

		result = (new Gson()).toJson(startRes)+"\r\n";
		return result.getBytes("UTF-8");
		
	}
	
	/**
	 * [PLC 수집정지 프로토콜] 성공시 JSON PROTOCOL을 만들어준다.
	 * @param id
	 * @param eventId
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public static byte[] makeSuccessStopResponseJson(String id, String eventId) throws UnsupportedEncodingException {
		
		String result;
		
		StopResponse stopRes = new StopResponse();
		stopRes.setJsonrpc(JSON_RPC_VERSION);
		stopRes.setId(id);
		
		StopResponse.Result stopResult = stopRes.new Result();
		stopResult.setVersion(PROTOCOL_VERSION);
		stopResult.setEventID(eventId);
		stopResult.setProcData(sdf.format(new Date(System.currentTimeMillis())));
		
		stopRes.setResult(stopResult);

		result = (new Gson()).toJson(stopRes)+"\r\n";
		return result.getBytes("UTF-8");
	}
	
	/**
	 * [PLC 수집정지 프로토콜] 실패시 JSON PROTOCOL을 만들어 준다.
	 * @param id
	 * @param errorCode
	 * @param errorMsg
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public static byte[] makeFailStopResponseJson(String id, String eventId, String errorCode, String errorMsg) throws UnsupportedEncodingException {
		
		String result;
		
		StopResponse stopRes = new StopResponse();
		stopRes.setJsonrpc(JSON_RPC_VERSION);
		stopRes.setId(id);

		StopResponse.Result stopResult = stopRes.new Result();
		stopResult.setVersion(PROTOCOL_VERSION);
		stopResult.setEventID(eventId);
		stopResult.setProcData(sdf.format(new Date(System.currentTimeMillis())));
		
		ResError stopError = new ResError();
		stopError.setCode(errorCode);
		stopError.setMessage(errorMsg);
		
		stopRes.setError(stopError);

		result = (new Gson()).toJson(stopRes)+"\r\n";
		return result.getBytes("UTF-8");
		
	}

	/**
	 * [PLC 수집 일괄 정지 프로토콜] 성공시 JSON PROTOCOL을 만들어준다.
	 * @param id
	 * @param eventIdArray
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public static byte[] makeSuccessStopAllResponseJson(String id, String[] eventIdArray) throws UnsupportedEncodingException {
		
		String result;
		
		StopAllResponse stopAllRes = new StopAllResponse();
		stopAllRes.setJsonrpc(JSON_RPC_VERSION);
		stopAllRes.setId(id);
		
		StopAllResponse.Result stopAllResult = stopAllRes.new Result();
		stopAllResult.setVersion(PROTOCOL_VERSION);
		
		StopAllResponse.StopAll[] stopArray = new StopAllResponse.StopAll[eventIdArray.length];
		
		for(int i=0; i<eventIdArray.length; i++){
			stopArray[i] = stopAllRes.new StopAll();
			stopArray[i].setEventId(eventIdArray[i]);
			stopArray[i].setProcDate(sdf.format(new Date(System.currentTimeMillis())));
		}
		
		
		stopAllResult.setStopAll(stopArray);
		stopAllRes.setResult(stopAllResult);

		result = (new Gson()).toJson(stopAllRes)+"\r\n";
		return result.getBytes("UTF-8");
	}
	
	/**
	 * [PLC 수집 일괄 정지 프로토콜] 실패시 JSON PROTOCOL을 만들어 준다.
	 * @param id
	 * @param errorCode
	 * @param errorMsg
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public static byte[] makeFailStopAllResponseJson(String id, String errorCode, String errorMsg) throws UnsupportedEncodingException {

		String result;
		
		StopAllResponse stopAllRes = new StopAllResponse();
		stopAllRes.setJsonrpc(JSON_RPC_VERSION);
		stopAllRes.setId(id);
		
		ResError stopAllerror = new ResError();
		stopAllerror.setCode(errorCode);
		stopAllerror.setMessage(errorMsg);
		
		stopAllRes.setError(stopAllerror);

		result = (new Gson()).toJson(stopAllRes)+"\r\n";
		return result.getBytes("UTF-8");
	}

	/**
	 * [PLC 수집 조회 프로토콜] 성공시 JSON PROTOCOL을 만들어준다.
	 * @param id
	 * @param statusArray
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public static byte[] makeSuccessStatusResponseJson(String id, Object[] paramArray) throws UnsupportedEncodingException {
		
		String result;
		
		StatusResponse statusRes = new StatusResponse();
		statusRes.setJsonrpc(JSON_RPC_VERSION);
		statusRes.setId(id);
		
		StatusResponse.Result statusResult = statusRes.new Result();
		statusResult.setVersion(PROTOCOL_VERSION);
		
		StatusResponse.Status[] statusArray = new StatusResponse.Status[paramArray.length]; 

		StartRequest.Param param;
		for(int i=0; i< paramArray.length; i++) {
			param = (StartRequest.Param) paramArray[i];
			
			statusArray[i] = statusRes.new Status();
			statusArray[i].setEventID(param.getEventID());
			statusArray[i].setPlcIp(param.getPlcIp());
			statusArray[i].setPlcPort(param.getPlcPort());
			statusArray[i].setPollingPeriod(param.getPollingPeriod());
			
		}
		
		statusResult.setStatus(statusArray);
		statusRes.setResult(statusResult);

		result = (new Gson()).toJson(statusRes)+"\r\n";
		return result.getBytes("UTF-8");
	}
	
	/**
	 * [PLC 수집 조회 프로토콜] 실패시 JSON PROTOCOL을 만들어 준다.
	 * @param id
	 * @param errorCode
	 * @param errorMsg
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public static byte[] makeFailStatusResponseJson(String id, String errorCode, String errorMsg) throws UnsupportedEncodingException {
		
		String result;
		
		StatusResponse statusRes = new StatusResponse();
		statusRes.setJsonrpc(JSON_RPC_VERSION);
		statusRes.setId(id);
		
		ResError stopAllerror = new ResError();
		stopAllerror.setCode(errorCode);
		stopAllerror.setMessage(errorMsg);
		
		statusRes.setError(stopAllerror);

		result = (new Gson()).toJson(statusRes)+"\r\n";
		return result.getBytes("UTF-8");
	}
	
	public static byte[] makeRejectionResponseJson(String id, String errorCode, String errorMsg) throws UnsupportedEncodingException {
		
		String result;

		CommonResponse res = new CommonResponse();
		ResError error = new ResError();

		res.setJsonrpc("2.0");
		res.setId(id);
		error.setCode(errorCode);
		error.setMessage(errorMsg);
		res.setError(error);

		result = (new Gson()).toJson(res)+"\r\n";
		return result.getBytes("UTF-8");
	}
	
	
	/**
	 * [PLC 수집정보 Publish] 성공시 JSON PROTOCOL을 만들어 준다.
	 * @param id
	 * @param eventId
	 * @param plcData
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public static byte[] makeSucessReadOnceResJson(String id, String eventId, Map<String, String> plcData) throws UnsupportedEncodingException {
		
		String result;
		ReadOnceResponse res = new ReadOnceResponse();
		res.setJsonrpc(JSON_RPC_VERSION);
		res.setId(id);
		 
		ReadOnceResponse.Result resResult = res.new Result();
		resResult.setVersion(PROTOCOL_VERSION);
		resResult.setEventID(eventId);
		resResult.setProcData(sdf.format(new Date(System.currentTimeMillis())));
		
		ReadOnceResponse.Items[] itemArray = new ReadOnceResponse.Items[plcData.size()];
		
		int idx = 0;
		Iterator<String> it = plcData.keySet().iterator();
		String sKey, sValue;
		while(it.hasNext()) {
			sKey = it.next();
			sValue = plcData.get(sKey);
			
			itemArray[idx] = res.new Items();
			itemArray[idx].setKey(sKey);
			itemArray[idx].setValue(sValue);
			
			idx ++;
		}

		resResult.setItems(itemArray);
		res.setResult(resResult);
		
		result = (new Gson()).toJson(res)+"\r\n";
		return result.getBytes("UTF-8");
	}
	
	/**
	 * [PLC 수집정보 Publish] 실패시 JSON PROTOCOL을 만들어 준다.
	 * @param id
	 * @param errorCode
	 * @param errorMsg
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public static byte[] makeFailReadOnceResJson(String id, String eventId, String errorCode, String errorMsg) throws UnsupportedEncodingException {

		String result;
		ReadOnceResponse res = new ReadOnceResponse();
		res.setJsonrpc(JSON_RPC_VERSION);
		res.setId(id);
		 
		ReadOnceResponse.Result resResult = res.new Result();
		resResult.setEventID(eventId);
		resResult.setProcData(sdf.format(new Date(System.currentTimeMillis())));
		resResult.setVersion(PROTOCOL_VERSION );
		
		ResError resError = new ResError();
		resError.setCode(errorCode);
		resError.setMessage(errorMsg);

		res.setError(resError);
		res.setResult(resResult);
		
		result = (new Gson()).toJson(res)+"\r\n";
		return result.getBytes("UTF-8");
	}
}	
