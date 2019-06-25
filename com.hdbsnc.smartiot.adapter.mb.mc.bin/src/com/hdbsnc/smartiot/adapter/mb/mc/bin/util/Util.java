package com.hdbsnc.smartiot.adapter.mb.mc.bin.util;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;

import com.google.gson.Gson;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.protocol.obj.Error;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.protocol.obj.GatheringPublish;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.protocol.obj.GatheringPublish.GatheringItems;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.protocol.obj.StartResponse;
import com.hdbsnc.smartiot.common.aim.IAdapterInstanceManager;

public class Util {

	//성공 시 
	public final static String SUCCESS_START_RESPONSE = "0";
	public final static String PROTOCOL_VERSION = "1.0";
	public final static String JSON_RPC_VERSION= "2.0";
	
	public final static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH:mm:ss.SSS");

	static {
		sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
	}
	
	public static void callHandler(IAdapterInstanceManager aim, String path, String sid, String tid, String contents) throws Exception {

		InnerContext request = new InnerContext();
		request.sid = sid;
		request.tid = tid;
		request.paths = Arrays.asList(path.split("/"));
		aim.handOverContext(request, null);	
	}
	
	public static String makeSucessPublishJson(String id, String eventId, Map<String, String> plcData) {
		
		GatheringPublish pub = new GatheringPublish();
		pub.setJsonrpc(JSON_RPC_VERSION);
		pub.setId(id);
		
		GatheringPublish.GatheringResult result = pub.new GatheringResult();
		result.setVersion(PROTOCOL_VERSION);
		result.setEventID(eventId);
		result.setProcData(sdf.format(new Date(System.currentTimeMillis())));

		GatheringPublish.GatheringItems[] items = new GatheringItems[plcData.size()];
		
		int idx = 0;
		Iterator it = (Iterator) plcData.keySet();
		String sKey, sValue;
		while(it.hasNext()) {
			sKey = (String) it.next();
			sValue = plcData.get(sKey);
			
			items[idx] = pub.new GatheringItems();
			items[idx].setKey(sKey);
			items[idx].setValue(sValue);
			
			idx ++;
		}

		Error error = new Error();
		error.setCode(SUCCESS_START_RESPONSE);
		error.setMessage("");

		result.setItems(items);
		pub.setResult(result);
		pub.setError(error);
		
		return (new Gson()).toJson(pub);
	}
	
	public static String makeFailPublishJson(String id, String eventId, String errorCode, String errorMsg) {
	
		GatheringPublish pub = new GatheringPublish();
		pub.setJsonrpc(JSON_RPC_VERSION);
		pub.setId(id);
		
		GatheringPublish.GatheringResult result = pub.new GatheringResult();
		result.setVersion(PROTOCOL_VERSION);
		result.setEventID(eventId);
		result.setProcData(sdf.format(new Date(System.currentTimeMillis())));

		Error error = new Error();
		error.setCode(errorCode);
		error.setMessage(errorMsg);

		pub.setResult(result);
		pub.setError(error);
		
		return (new Gson()).toJson(pub);
	}
	
	public static String makeSucessStartResponseJson(String id, String eventId) {
		
		StartResponse res = new StartResponse();
		res.setJsonrpc(JSON_RPC_VERSION);
		res.setId(id);
		
		StartResponse.Result result = res.new Result();
		result.setVersion(PROTOCOL_VERSION);
		result.setEventID(eventId);
		result.setProcData(sdf.format(new Date(System.currentTimeMillis())));
		
		Error error = new Error();
		error.setCode(SUCCESS_START_RESPONSE);
		error.setMessage("");
		
		res.setResult(result);
		res.setError(error);
		
		return (new Gson()).toJson(res);
		
	}
	
	public static String makeFailStartResponseJson(String id, String eventId, String errorCode, String errorMsg) {
		
		StartResponse res = new StartResponse();
		res.setJsonrpc(JSON_RPC_VERSION);
		res.setId(id);
		
		StartResponse.Result result = res.new Result();
		result.setVersion(PROTOCOL_VERSION);
		result.setEventID(eventId);
		result.setProcData(sdf.format(new Date(System.currentTimeMillis())));
		
		Error error = new Error();
		error.setCode(errorCode);
		error.setMessage(errorMsg);
		
		res.setResult(result);
		
		return (new Gson()).toJson(res);
		
	}
}
