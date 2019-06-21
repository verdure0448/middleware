package com.hdbsnc.smartiot.adapter.mb.mc.bin.obj;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * @author dbkim
 *
 */
public class JsonToPlcVoParser {

	private List<ReadPlcVo> requestReadObjList;
	private List<WritePlcVo> requestWriteObjList;

	private String jsonData;
	
	private String operation;
	private String interval;
	private String handlerType;
	
	private String protocolType;
	
	private JSONObject jsonSchema;
	
	public JsonToPlcVoParser(String pJsonData) {

		this.jsonData = pJsonData;
		requestWriteObjList = new ArrayList<>();
		//requestReadObjList = new ArrayList<>();
	}
	
	/**
	 * json 컨텐츠 파서를 시작하여 오브젝트에 저장한다 
	 * "{\"Operation\":\"xxxxx\",\"Interval\":3000,\"Read\":[{\"DevType\":\"R*\",\"Address\":\"0\",\"Score\":\"60\",\"Formatter\":[{\"Name\":\"PlcControl.ShiftReset\",\"Pattern\":\"0:0:BIT\"},{\"Name\":\"PlcControl.ClockSynch\",\"Pattern\":\"0:1:BIT\"},{\"Name\":\"PlcStatus.Heartbeat\",\"Pattern\":\"1:0:BIT\"},{\"Name\":\"PlcStatus.ManualMode\",\"Pattern\":\"1:1:BIT\"},{\"Name\":\"PlcStatus.AutoCycleRunning\",\"Pattern\":\"1:2:BIT\"},{\"Name\":\"PlcStatus.StoppedByOperator\",\"Pattern\":\"1:3:BIT\"},{\"Name\":\"PlcStatus.MachineFault\",\"Pattern\":\"1:4:BIT\"},{\"Name\":\"PlcStatus.MachineFaultAcknowledged\",\"Pattern\":\"1:5:BIT\"},{\"Name\":\"PlcStatus.MachineWarning\",\"Pattern\":\"1:6:BIT\"},{\"Name\":\"PlcStatus.MachineWarningAcknowledged\",\"Pattern\":\"1:7:BIT\"},{\"Name\":\"PlcStatus.Bypassed\",\"Pattern\":\"1:8:BIT\"},{\"Name\":\"PlcStatus.E-StopConditionActive\",\"Pattern\":\"2:0:BIT\"},{\"Name\":\"PlcStatus.NoWork\",\"Pattern\":\"2:1:BIT\"},{\"Name\":\"PlcStatus.FullWork\",\"Pattern\":\"2:2:BIT\"},{\"Name\":\"PlcStatus.MasterCheckWarning\",\"Pattern\":\"2:3:BIT\"},{\"Name\":\"PlcStatus.MaterialLowWarning\",\"Pattern\":\"2:4:BIT\"},{\"Name\":\"PlcStatus.ToolChangeWarning\",\"Pattern\":\"2:5:BIT\"},{\"Name\":\"PlcStatus.ToolChangeFault\",\"Pattern\":\"2:6:BIT\"},{\"Name\":\"PlcAlarm.Fault\",\"Pattern\":\"3:1:UINT\"},{\"Name\":\"PlcAlarm.Warning\",\"Pattern\":\"4:1:UINT\"},{\"Name\":\"PlcProduction.InPieceCount\",\"Pattern\":\"5:1:UINT\"},{\"Name\":\"PlcProduction.GoodPieceCount\",\"Pattern\":\"6:1:UINT\"},{\"Name\":\"PlcProduction.RejectPieceCount\",\"Pattern\":\"7:1:UINT\"},{\"Name\":\"PlcProduction.ReworkPieceCount\",\"Pattern\":\"8:1:UINT\"},{\"Name\":\"PlcProduction.LastCycleTime\",\"Pattern\":\"9:1:UINT\"},{\"Name\":\"PlcProduction.PartType\",\"Pattern\":\"10:5:ASCII\"},{\"Name\":\"PlcProduction.SerialNo\",\"Pattern\":\"20:7:ASCII\"},{\"Name\":\"PlcAlarmHistory.Alarm\",\"Pattern\":\"30:10:UINT\"},{\"Name\":\"PlcAlarmHistory.Time\",\"Pattern\":\"40:20:SHORT\"}]}],\"Write\":[{\"DevType\":\"R*\",\"Address\":\"30\",\"Score\":\"30\",\"DataType\":\"HEX\",\"Data\":\"000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\"}]}";
	 * @throws ParseException 
	 */
	public void parser() throws Exception {

		JSONParser parser = new JSONParser();	 
		Object obj = parser.parse(jsonData);

		JSONObject jsonObject = (JSONObject) obj;

		
		//고유한 이름
		operation = (String) jsonObject.get("Operation");
		//PLC로 부터의 수집간격
		interval = (String)jsonObject.get("Interval");
		//프로토콜 Type
		protocolType = (String)jsonObject.get("ProtocolType");
		//프로토콜 Type
		handlerType = (String)jsonObject.get("HandlerType");
		
		//read데이터를 파서하여 VO로 변환
		readParser(jsonObject);
		//write데이터를 파서하여 VO로 변환
		writeParser(jsonObject);	
		//사용자가 미리 정의한 formatter의 schema를 json형태로 가공한다. 
		formatterSchemaParser(jsonObject);
	}

	
	/**
	 * 사용자가 정의한 Read를 VO로 변환한다.
	 * @param jsonData
	 */
	private void readParser(JSONObject jsonObject) {
		
		//formatter에 read라는 항목이 있는지 확인한다.
		if(jsonObject.containsKey("Read")){
			ReadPlcVo requestReadObj;
			
			//read가 있다면 Json배열 형태로 가져온다
			JSONArray readArray = (JSONArray) jsonObject.get("Read");
			
			//리스트의 사이즈를 정확하게 맞추기 위해 사용자가 정의한 Read의 갯수만큼 배열을 만들어 준다.
			requestReadObjList = new ArrayList<>(readArray.size());
			for (int i = 0; i < readArray.size(); i++) {
				requestReadObjList.add(i, null);
			}
			
			
			//formatter read에 관한 파서 부분 구현
			for (int i = 0; i < readArray.size(); i++) {
				requestReadObj=new ReadPlcVo();
				jsonObject = (JSONObject) readArray.get(i);
				
				int seq = Integer.parseInt((String)jsonObject.get("Seq"));
				
				String score = (String) jsonObject.get("Score");
				requestReadObj.setDevScore(score);

				String devType = (String) jsonObject.get("DevType");
				requestReadObj.setDevCode(devType);

				String address = (String) jsonObject.get("Address");
				requestReadObj.setDevNum(address);

				JSONArray formatter = (JSONArray) jsonObject.get("Formatter");
				for (int j = 0; j < formatter.size(); j++) {
					jsonObject = (JSONObject) formatter.get(j);

					String name = (String) jsonObject.get("Name");
					String pattern = (String) jsonObject.get("Pattern");
					String index = (String) jsonObject.get("Index");
					String type = (String) jsonObject.get("Type");
					requestReadObj.addFormater(name, pattern, type, index);
										 
				}
				
				//사용자가 Seq번호를 잘못 설정하면 예외를 처리한다.
				// *seq를 설정하는 이유는 json특성상 MAP으로 데이터를 가져오기 때문에 설정한 순서에 맞게 데이터를 가지고 오기위해 Sequence 번호를 설정함
				if(requestReadObjList.get(seq) != null){
					throw new IllegalArgumentException("Read속성의 Seq가 중복 되었습니다.");
				}
				requestReadObjList.remove(seq);
				requestReadObjList.add(seq, requestReadObj);
			}
			
			for (int i = 0; i < readArray.size(); i++) {
				if(requestReadObjList.get(i) == null){
					throw new IllegalArgumentException("Read속성의 Seq 범위 지정이 잘못되었습니다.");
				}
			}
		}
		
	}
	
	/**
	 * 사용자가 정의한 Write를 VO로 변환한다.
	 * @param jsonData
	 */
	private void writeParser(JSONObject jsonObject) {
		
		//formatter에 wrtie에 관한 항목이 있는지 확인한다.
		if(jsonObject.containsKey("Write")){
			WritePlcVo requestWriteObj;	
			
			//write가 있다면 Json배열 형태로 가져온다
			JSONArray writeArray = (JSONArray) jsonObject.get("Write");

			for (int i = 0; i < writeArray.size(); i++) {
				requestWriteObj = new WritePlcVo();		
				jsonObject = (JSONObject) writeArray.get(i);
				
				
				int seq = Integer.parseInt((String)jsonObject.get("Seq"));

				String score = (String) jsonObject.get("Score");
				requestWriteObj.setDevScore(score);

				String devType = (String) jsonObject.get("DevType");
				requestWriteObj.setDevCode(devType);

				String address = (String) jsonObject.get("Address");
				requestWriteObj.setDevNum(address);

				String dataType = (String) jsonObject.get("DataType");
				requestWriteObj.setDataType(dataType);

				String data = (String) jsonObject.get("Data");
				requestWriteObj.setData(data);
				
			//사용자가 Seq번호를 잘못 설정하면 예외를 처리한다.
			// *seq를 설정하는 이유는 json특성상 MAP으로 데이터를 가져오기 때문에 설정한 순서에 맞게 데이터를 가지고 오기위해 Sequence 번호를 설정함
			if(requestWriteObjList.get(seq) != null){
				throw new IllegalArgumentException("Write속성의 Seq가 중복 되었습니다.");
			}
			requestWriteObjList.remove(seq);
			requestWriteObjList.add(seq, requestWriteObj);
		}
		
		for (int i = 0; i < writeArray.size(); i++) {
			if(requestReadObjList.get(i) == null){
				throw new IllegalArgumentException("Write속성의 Seq 범위 지정이 잘못되었습니다.");
			}
		}
		}
	}

	/**
	 * 사용자가 정의한 formatter를 Json형태로 가공한다
	 * @param jsonData
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	private void formatterSchemaParser(JSONObject jsonObject) throws Exception {
		
		if(jsonObject.containsKey("FormatterShema")){

			//FormatterSchema에 관한 Json을 가지고 온다.
			jsonObject = (JSONObject) jsonObject.get("FormatterShema");
			
			JSONObject result = new JSONObject();
			
			JSONObject tmpJsonObj;
			JSONArray tmpJsonArray;
			
			JSONObject subJsonObj;
			JSONArray subJsonArray;
			
			for(Object tmp : jsonObject.keySet()) {
			
				//type, Length, StaticItem
				tmpJsonObj = (JSONObject) jsonObject.get(tmp.toString());
				if(("Value".equals(tmpJsonObj.get("Type")))) {
					tmpJsonArray = (JSONArray) tmpJsonObj.get("StaticItems");
					//staticItem에 있는 Json 배열 Object에 넣어주기
					for(int i=0; i< tmpJsonArray.size(); i++) {
					//{"Item":[{"Value":"OP10","Name":"OpCode"}],"Index":"0"}
						tmpJsonObj = (JSONObject) tmpJsonArray.get(i);
						
						//Item이 존재하는지 확인
						if(tmpJsonObj.containsKey("Items")) {
							JSONArray items = (JSONArray) tmpJsonObj.get("Items");
							
							//Item에 있는 Json 배열 받아오기 
							for(int j=0; j< items.size(); j++) {
								//items List에 있는 item 가지고 오기
								JSONObject item = (JSONObject) items.get(j);
								result.put(item.get("Name"), item.get("Value"));
							}
						}else {
							throw new Exception("StaticItems에 아이템이 존재하지 않습니다. 사용을 하시려면 Item을 추가해주세요");
						}
					}
					
				}
				else if("Object".equals(tmpJsonObj.get("Type"))) {

					subJsonObj = new JSONObject();  
					result.put(tmp.toString(), subJsonObj);

					if(tmpJsonObj.containsKey("StaticItems")){
						
						tmpJsonArray = (JSONArray) tmpJsonObj.get("StaticItems");
						//staticItem에 있는 Json 배열 Object에 넣어주기
						for(int i=0; i< tmpJsonArray.size(); i++) {
						//{"Item":[{"Value":"OP10","Name":"OpCode"}],"Index":"0"}
							tmpJsonObj = (JSONObject) tmpJsonArray.get(i);
							
							//Item이 존재하는지 확인
							if(tmpJsonObj.containsKey("Items")) {
								JSONArray items = (JSONArray) tmpJsonObj.get("Items");
								
								//Item에 있는 Json 배열 받아오기 
								for(int j=0; j< items.size(); j++) {
									//items List에 있는 item 가지고 오기
									JSONObject item = (JSONObject) items.get(j);
									subJsonObj.put(item.get("Name"), item.get("Value"));
								}
							}else {
								throw new Exception("StaticItems에 아이템이 존재하지 않습니다. 사용을 하시려면 Item을 추가해주세요");
							}
						}
						
					}		
					
				}else if("List".equals(tmpJsonObj.get("Type"))) {

					subJsonArray = new JSONArray();
					//리스트의 길이를 가지고 온다.
					int listLength = Integer.parseInt((String)tmpJsonObj.get("Length"));
							
					//선언된 Json배열크기(Length)만큼 List안에 Object 생성
					for (int i = 0; i < listLength; i++) {
						subJsonArray.add(new JSONObject());
					}

					
					if(tmpJsonObj.containsKey("StaticItems")){
						
						tmpJsonArray = (JSONArray) tmpJsonObj.get("StaticItems");
						//staticItem에 있는 Json 배열 Object에 넣어주기
						for(int i=0; i< tmpJsonArray.size(); i++) {
						//{"Item":[{"Value":"OP10","Name":"OpCode"}],"Index":"0"}
							tmpJsonObj = (JSONObject) tmpJsonArray.get(i);
							
							//Item이 존재하는지 확인
							if(tmpJsonObj.containsKey("Items")) {
								JSONArray items = (JSONArray) tmpJsonObj.get("Items");
								
								//Item에 있는 Json 배열 받아오기 
								for(int j=0; j< items.size(); j++) {
									//items List에 있는 item 가지고 오기
									JSONObject item = (JSONObject) items.get(j);
									subJsonObj = new JSONObject();				
									subJsonObj.put(item.get("Name"), item.get("Value"));
									subJsonArray.set(Integer.parseInt((String) tmpJsonObj.get("Index")), subJsonObj);
								}
							}else {
								throw new Exception("StaticItems에 아이템이 존재하지 않습니다. 사용을 하시려면 Item을 추가해주세요");
							}
						}
						
					}		
					//만들어진 결과를 담아줌
					result.put(tmp.toString(), subJsonArray);
				}
				
			}			
			
			jsonSchema = result;
			
//			System.out.println(result.toJSONString());
			
		}
	}
	
	public JSONObject getJsonSchema() {
		return jsonSchema;
	}
	
	/**
	 * json에 닮긴 데이터 중 Read부분 Obj를 가지고 온다.
	 * 
	 * @return
	 */
	public List<ReadPlcVo> getRequestReadObjList() {
		//
		return requestReadObjList;
	}

	/**
	 * json에 닮긴 데이터 중 Write부분 Obj를 가지고 온다.
	 * 
	 * @return
	 */
	public List<WritePlcVo> getRequestWriteObjList() {

		return requestWriteObjList;
	}
	
	public String getProtocolType() {
		return protocolType;
	}

	/**
	 * 핸들러의 타입을 가지고 옵니다
	 * 핸들러 주기적 호출 or 핸들 생성
	 * @return
	 */
	public String getHandlerType() {
		return handlerType;
	}

	/**
	 * Json데이터에 Plc에서 읽어야 할 데이터가 있으면 true 없으면 false
	 * @return
	 */
	public boolean isReadObj(){
		if(requestReadObjList.size()==0) 
			return false;
		else 
			return true;
	}
	/**
	 * Json데이터에 Plc에서 써야 할 데이터가 있으면 true 없으면 false
	 * @return
	 */
	public boolean isWriteObj(){
		if(requestWriteObjList.size()==0) 
			return false;
		else 
			return true;
	}

	public String getOperation() {
		return operation;
	}

	public String getInterval() {
		return interval;
	}
}
