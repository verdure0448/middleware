package com.hdbsnc.smartiot.adapter.mb.mc.bin.obj;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * @author user
 * 핸들러를 동적생성을 사용하는 JSON프로토콜을 파서 후 VO로 만들어준다.
 */
public class CreateParserVo {

	private String jsonrpc;
	private String method;
	private String id;
	
	private Param param;
	
	public CreateParserVo(String json) {

		List<Items> itemList = new ArrayList<>();
		
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(json);
		JsonObject jsonObj = element.getAsJsonObject();
		this.jsonrpc= jsonObj.get("jsonrpc").getAsString();
		this.method = jsonObj.get("method").getAsString();
		this.id = jsonObj.get("id").getAsString();
		
		JsonObject paramJsonObj = jsonObj.get("param").getAsJsonObject();
		String version = paramJsonObj.get("protocol.version").getAsString();
		String eventId = paramJsonObj.get("event.id").getAsString();
		String ip = paramJsonObj.get("plc.ip").getAsString();
		String port = paramJsonObj.get("plc.port").getAsString();
		String period = paramJsonObj.get("polling.period").getAsString();

		JsonArray itemJsonArray = paramJsonObj.get("items").getAsJsonArray();
		JsonObject tmpObj;
		for(int i=0; i<itemJsonArray.size(); i++) {
			tmpObj = (JsonObject) itemJsonArray.get(i);
			String key = tmpObj.get("key").getAsString();
			String code = tmpObj.get("device.code").getAsString();
			String num = tmpObj.get("device.num").getAsString();
			String score = tmpObj.get("device.score").getAsString();
			itemList.add(new Items(key, code, num, score));
		}

		this.param = new Param(version, eventId, ip, port, period, itemList);
	}
	
	public String getJsonRpc() {
		return jsonrpc;
	}
	public String getMethod() {
		return method;
	}
	public String getId() {
		return id;
	}
	public Param getParam() {
		return param;
	}

	public class Param{
		
		String version;
		String eventId;
		String ip;
		String port;
		String period;
		List<Items> items;
		
		Param(String version, String eventId, String ip, String port, String period, List<Items> items){
			
			this.version = version;
			this.eventId = eventId;
			this.ip = ip;
			this.port = port;
			this.period = period;
			this.items = items;
		}
		
		public String getVersion() {
			return version;
		}
		public String getEventId() {
			return eventId;
		}
		public String getIp() {
			return ip;
		}
		public String getPort() {
			return port;
		}
		public String getPeriod() {
			return period;
		}
		public List<Items> getItems() {
			return items;
		}
	}

	class Items {
		
		String key;
		String code;
		String num;
		String score;

		Items(String key, String code, String num, String score){
			
			this.key = key;
			this.code = code;
			this.num = num;
			this.score = score;
		}
		public String getKey() {
			return key;
		}
		public String getCode() {
			return code;
		}
		public String getNum() {
			return num;
		}
		public String getScore() {
			return score;
		}
	}
}


