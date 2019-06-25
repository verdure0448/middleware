package com.hdbsnc.smartiot.adapter.mb.mc.bin.util;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;

import com.google.gson.Gson;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.protocol.obj.GatheringPublish;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.protocol.obj.ResError;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.protocol.obj.StartResponse;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.protocol.obj.StopAllResponse;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.protocol.obj.StopResponse;
import com.hdbsnc.smartiot.common.aim.IAdapterInstanceManager;
import com.hdbsnc.smartiot.common.context.impl.InnerContext;

public class Util {

	//성공 시 
	public final static String PROTOCOL_VERSION = "1.0";
	public final static String JSON_RPC_VERSION= "2.0";
	
	public final static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss.SSS");

	static {
		sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
	}
	
	public synchronized static void callHandler(IAdapterInstanceManager aim, String path, String sid, String tid, String contents) throws Exception {

		InnerContext request = new InnerContext();
		request.setSid(sid);
		request.setTid(tid);
		request.setPaths(Arrays.asList(path.split("/")));
		aim.handOverContext(request, null);	
	}
	
	/**
	 * [PLC 수집정보 Publish] 성공시 JSON PROTOCOL을 만들어 준다.
	 * @param id
	 * @param eventId
	 * @param plcData
	 * @return
	 */
	public static String makeSucessPublishJson(String id, String eventId, Map<String, String> plcData) {
		
		String result;
		GatheringPublish pub = new GatheringPublish();
		pub.setJsonrpc(JSON_RPC_VERSION);
		pub.setId(id);
		
		GatheringPublish.Result pubResult = pub.new Result();
		pubResult.setVersion(PROTOCOL_VERSION);
		pubResult.setEventID(eventId);
		pubResult.setProcData(sdf.format(new Date(System.currentTimeMillis())));

		GatheringPublish.Items[] items = new GatheringPublish.Items[plcData.size()];
		
		int idx = 0;
		Iterator it = (Iterator) plcData.keySet();
		String sKey, sValue;
		while(it.hasNext()) {
			sKey = (String) it.next();
			sValue = plcData.get(sKey);
			
			items[idx] = pub.new Items();
			items[idx].setKey(sKey);
			items[idx].setValue(sValue);
			
			idx ++;
		}

		pubResult.setItems(items);
		pub.setResult(pubResult);
		
		result = (new Gson()).toJson(pub)+"\r\n";
		return result;
	}
	
	/**
	 * [PLC 수집정보 Publish] 실패시 JSON PROTOCOL을 만들어 준다.
	 * @param id
	 * @param errorCode
	 * @param errorMsg
	 * @return
	 */
	public static String makeFailPublishJson(String id, String errorCode, String errorMsg) {

		String result;
		GatheringPublish pub = new GatheringPublish();
		pub.setJsonrpc(JSON_RPC_VERSION);
		pub.setId(id);
		
		ResError pubError = new ResError();
		pubError.setCode(errorCode);
		pubError.setMessage(errorMsg);

		pub.setError(pubError);
		
		result = (new Gson()).toJson(pub)+"\r\n";
		return result;
	}
	
	/**
	 * [PLC 수집시작 프로토콜] 성공시 JSON PROTOCOL을 만들어 준다.
	 * @param id
	 * @param eventId
	 * @return
	 */
	public static String makeSuccessStartResponseJson(String id, String eventId) {
		
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
		return result;
	}
	
	/**
	 * [PLC 수집시작 프로토콜] 실패시 JSON PROTOCOL을 만들어 준다.
	 * @param id
	 * @param errorCode
	 * @param errorMsg
	 * @return
	 */
	public static String makeFailStartResponseJson(String id, String errorCode, String errorMsg) {
		
		String result;
		
		StartResponse startRes = new StartResponse();
		startRes.setJsonrpc(JSON_RPC_VERSION);
		startRes.setId(id);
		
		ResError startError = new ResError();
		startError.setCode(errorCode);
		startError.setMessage(errorMsg);
		
		startRes.setError(startError);

		result = (new Gson()).toJson(startRes)+"\r\n";
		return result;
		
	}
	
	/**
	 * [PLC 수집정지 프로토콜] 성공시 JSON PROTOCOL을 만들어준다.
	 * @param id
	 * @param eventId
	 * @return
	 */
	public static String makeSuccessStopResponseJson(String id, String eventId) {
		
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
		return result;
	}
	
	/**
	 * [PLC 수집정지 프로토콜] 실패시 JSON PROTOCOL을 만들어 준다.
	 * @param id
	 * @param errorCode
	 * @param errorMsg
	 * @return
	 */
	public static String makeFailStopResponseJson(String id, String errorCode, String errorMsg) {
		
		String result;
		
		StopResponse stopRes = new StopResponse();
		stopRes.setJsonrpc(JSON_RPC_VERSION);
		stopRes.setId(id);
		
		ResError stopError = new ResError();
		stopError.setCode(errorCode);
		stopError.setMessage(errorMsg);
		
		stopRes.setError(stopError);

		result = (new Gson()).toJson(stopRes)+"\r\n";
		return result;
		
	}

	/**
	 * [PLC 수집 일괄 정지 프로토콜] 성공시 JSON PROTOCOL을 만들어준다.
	 * @param id
	 * @param eventIdArray
	 * @return
	 */
	public static String makeSuccessStopAllResponseJson(String id, String[] eventIdArray) {
		
		String result;
		
		StopAllResponse stopAllRes = new StopAllResponse();
		stopAllRes.setJsonrpc(JSON_RPC_VERSION);
		stopAllRes.setId(id);
		
		StopAllResponse.Result stopAllResult = stopAllRes.new Result();
		stopAllResult.setVersion(PROTOCOL_VERSION);
		
		StopAllResponse.StopAll[] stopList = new StopAllResponse.StopAll[eventIdArray.length];
		
		for(int i=0; i<eventIdArray.length; i++){
			stopList[i] = stopAllRes.new StopAll();
			stopList[i].setEventId(eventIdArray[i]);
			stopList[i].setProcDate(sdf.format(new Date(System.currentTimeMillis())));
		}
		
		
		stopAllResult.setStopAll(stopList);
		stopAllRes.setResult(stopAllResult);

		result = (new Gson()).toJson(stopAllRes)+"\r\n";
		return result;
	}
	
	/**
	 * [PLC 수집 일괄 정지 프로토콜] 실패시 JSON PROTOCOL을 만들어 준다.
	 * @param id
	 * @param errorCode
	 * @param errorMsg
	 * @return
	 */
	public static String makeFailStopAllResponseJson(String id, String errorCode, String errorMsg) {

		String result;
		
		StopAllResponse stopAllRes = new StopAllResponse();
		stopAllRes.setJsonrpc(JSON_RPC_VERSION);
		stopAllRes.setId(id);
		
		ResError stopAllerror = new ResError();
		stopAllerror.setCode(errorCode);
		stopAllerror.setMessage(errorMsg);
		
		stopAllRes.setError(stopAllerror);

		result = (new Gson()).toJson(stopAllRes)+"\r\n";
		return result;
	}
}
