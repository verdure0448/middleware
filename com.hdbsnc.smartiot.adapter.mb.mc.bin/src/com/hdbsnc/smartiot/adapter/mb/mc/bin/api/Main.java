package com.hdbsnc.smartiot.adapter.mb.mc.bin.api;

import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.hdbsnc.smartiot.adapter.mb.mc.bin.api.frame.AbstractBlocksFrame.TransMode;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.obj.FunctionParamParser;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.obj.RequestReadObj;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.obj.RequestReadObj.FormatterObj;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.util.EditUtil;

public class Main {
	
	private static final int BLOCK_SIZE = 4;
	
	public static void main(String[] args) throws Exception{

		FunctionParamParser parser = new FunctionParamParser("{\"Operation\":\"standard\",\"Interval\":\"1000\",\"Read\":[{\"OpreationCode\":\"OP10\",\"Seq\":\"0\",\"DevType\":\"R*\",\"Address\":\"0\",\"Score\":\"27\",\"Formatter\":[{\"Name\":\"PlcControl.ShiftReset\",\"Pattern\":\"0:0:BIT\"},{\"Name\":\"PlcControl.ClockSynch\",\"Pattern\":\"0:1:BIT\"},{\"Name\":\"PlcStatus.Heartbeat\",\"Pattern\":\"1:0:BIT\"},{\"Name\":\"PlcStatus.ManualMode\",\"Pattern\":\"1:1:BIT\"},{\"Name\":\"PlcStatus.AutoCycleRunning\",\"Pattern\":\"1:2:BIT\"},{\"Name\":\"PlcStatus.StoppedByOperator\",\"Pattern\":\"1:3:BIT\"},{\"Name\":\"PlcStatus.MachineFault\",\"Pattern\":\"1:4:BIT\"},{\"Name\":\"PlcStatus.MachineFaultAcknowledged\",\"Pattern\":\"1:5:BIT\"},{\"Name\":\"PlcStatus.MachineWarning\",\"Pattern\":\"1:6:BIT\"},{\"Name\":\"PlcStatus.MachineWarningAcknowledged\",\"Pattern\":\"1:7:BIT\"},{\"Name\":\"PlcStatus.Bypassed\",\"Pattern\":\"1:8:BIT\"},{\"Name\":\"PlcStatus.E-StopConditionActive\",\"Pattern\":\"2:0:BIT\"},{\"Name\":\"PlcStatus.NoWork\",\"Pattern\":\"2:1:BIT\"},{\"Name\":\"PlcStatus.FullWork\",\"Pattern\":\"2:2:BIT\"},{\"Name\":\"PlcStatus.MasterCheckWarning\",\"Pattern\":\"2:3:BIT\"},{\"Name\":\"PlcStatus.MaterialLowWarning\",\"Pattern\":\"2:4:BIT\"},{\"Name\":\"PlcStatus.ToolChangeWarning\",\"Pattern\":\"2:5:BIT\"},{\"Name\":\"PlcStatus.ToolChangeFault\",\"Pattern\":\"2:6:BIT\"},{\"Name\":\"PlcAlarm.Fault\",\"Pattern\":\"3:1:UINT\",\"Index\":\"0\"},{\"Name\":\"PlcAlarm.Warning\",\"Pattern\":\"4:1:UINT\",\"Index\":\"0\"},{\"Name\":\"PlcProduction.InPieceCount\",\"Pattern\":\"5:1:UINT\",\"Index\":\"0\"},{\"Name\":\"PlcProduction.GoodPieceCount\",\"Pattern\":\"6:1:UINT\",\"Index\":\"0\"},{\"Name\":\"PlcProduction.RejectPieceCount\",\"Pattern\":\"7:1:UINT\",\"Index\":\"0\"},{\"Name\":\"PlcProduction.ReworkPieceCount\",\"Pattern\":\"8:1:UINT\",\"Index\":\"0\"},{\"Name\":\"PlcProduction.LastCycleTime\",\"Pattern\":\"9:1:UINT\",\"Index\":\"0\"},{\"Name\":\"PlcProduction.PartType\",\"Pattern\":\"10:5:ASCII\",\"Index\":\"0\"},{\"Name\":\"PlcProduction.SerialNo\",\"Pattern\":\"20:7:ASCII\",\"Index\":\"0\"}]},{\"OpreationCode\":\"OP20\",\"Seq\":\"1\",\"DevType\":\"R*\",\"Address\":\"103\",\"Score\":\"24\",\"Formatter\":[{\"Name\":\"PlcAlarm.Fault\",\"Pattern\":\"0:1:UINT\",\"Index\":\"1\"},{\"Name\":\"PlcAlarm.Warning\",\"Pattern\":\"1:1:UINT\",\"Index\":\"1\"},{\"Name\":\"PlcProduction.InPieceCount\",\"Pattern\":\"2:1:UINT\",\"Index\":\"1\"},{\"Name\":\"PlcProduction.GoodPieceCount\",\"Pattern\":\"3:1:UINT\",\"Index\":\"1\"},{\"Name\":\"PlcProduction.RejectPieceCount\",\"Pattern\":\"4:1:UINT\",\"Index\":\"1\"},{\"Name\":\"PlcProduction.ReworkPieceCount\",\"Pattern\":\"5:1:UINT\",\"Index\":\"1\"},{\"Name\":\"PlcProduction.LastCycleTime\",\"Pattern\":\"6:1:UINT\",\"Index\":\"1\"},{\"Name\":\"PlcProduction.PartType\",\"Pattern\":\"7:5:ASCII\",\"Index\":\"1\"},{\"Name\":\"PlcProduction.SerialNo\",\"Pattern\":\"17:7:ASCII\",\"Index\":\"1\"}]},{\"OpreationCode\":\"OP30\",\"Seq\":\"2\",\"DevType\":\"R*\",\"Address\":\"203\",\"Score\":\"24\",\"Formatter\":[{\"Name\":\"PlcAlarm.Fault\",\"Pattern\":\"0:1:UINT\",\"Index\":\"2\"},{\"Name\":\"PlcAlarm.Warning\",\"Pattern\":\"1:1:UINT\",\"Index\":\"2\"},{\"Name\":\"PlcProduction.InPieceCount\",\"Pattern\":\"2:1:UINT\",\"Index\":\"2\"},{\"Name\":\"PlcProduction.GoodPieceCount\",\"Pattern\":\"3:1:UINT\",\"Index\":\"2\"},{\"Name\":\"PlcProduction.RejectPieceCount\",\"Pattern\":\"4:1:UINT\",\"Index\":\"2\"},{\"Name\":\"PlcProduction.ReworkPieceCount\",\"Pattern\":\"5:1:UINT\",\"Index\":\"2\"},{\"Name\":\"PlcProduction.LastCycleTime\",\"Pattern\":\"6:1:UINT\",\"Index\":\"2\"},{\"Name\":\"PlcProduction.PartType\",\"Pattern\":\"7:5:ASCII\",\"Index\":\"2\"},{\"Name\":\"PlcProduction.SerialNo\",\"Pattern\":\"17:7:ASCII\",\"Index\":\"2\"}]},{\"OpreationCode\":\"OP40\",\"Seq\":\"3\",\"DevType\":\"R*\",\"Address\":\"303\",\"Score\":\"24\",\"Formatter\":[{\"Name\":\"PlcAlarm.Fault\",\"Pattern\":\"0:1:UINT\",\"Index\":\"3\"},{\"Name\":\"PlcAlarm.Warning\",\"Pattern\":\"1:1:UINT\",\"Index\":\"3\"},{\"Name\":\"PlcProduction.InPieceCount\",\"Pattern\":\"2:1:UINT\",\"Index\":\"3\"},{\"Name\":\"PlcProduction.GoodPieceCount\",\"Pattern\":\"3:1:UINT\",\"Index\":\"3\"},{\"Name\":\"PlcProduction.RejectPieceCount\",\"Pattern\":\"4:1:UINT\",\"Index\":\"3\"},{\"Name\":\"PlcProduction.ReworkPieceCount\",\"Pattern\":\"5:1:UINT\",\"Index\":\"3\"},{\"Name\":\"PlcProduction.LastCycleTime\",\"Pattern\":\"6:1:UINT\",\"Index\":\"3\"},{\"Name\":\"PlcProduction.PartType\",\"Pattern\":\"7:5:ASCII\",\"Index\":\"3\"},{\"Name\":\"PlcProduction.SerialNo\",\"Pattern\":\"17:7:ASCII\",\"Index\":\"3\"}]},{\"OpreationCode\":\"OP50\",\"Seq\":\"4\",\"DevType\":\"R*\",\"Address\":\"403\",\"Score\":\"24\",\"Formatter\":[{\"Name\":\"PlcAlarm.Fault\",\"Pattern\":\"0:1:UINT\",\"Index\":\"4\"},{\"Name\":\"PlcAlarm.Warning\",\"Pattern\":\"1:1:UINT\",\"Index\":\"4\"},{\"Name\":\"PlcProduction.InPieceCount\",\"Pattern\":\"2:1:UINT\",\"Index\":\"4\"},{\"Name\":\"PlcProduction.GoodPieceCount\",\"Pattern\":\"3:1:UINT\",\"Index\":\"4\"},{\"Name\":\"PlcProduction.RejectPieceCount\",\"Pattern\":\"4:1:UINT\",\"Index\":\"4\"},{\"Name\":\"PlcProduction.ReworkPieceCount\",\"Pattern\":\"5:1:UINT\",\"Index\":\"4\"},{\"Name\":\"PlcProduction.LastCycleTime\",\"Pattern\":\"6:1:UINT\",\"Index\":\"4\"},{\"Name\":\"PlcProduction.PartType\",\"Pattern\":\"7:5:ASCII\",\"Index\":\"4\"},{\"Name\":\"PlcProduction.SerialNo\",\"Pattern\":\"17:7:ASCII\",\"Index\":\"4\"}]},{\"OpreationCode\":\"SUB1-1\",\"Seq\":\"5\",\"DevType\":\"R*\",\"Address\":\"503\",\"Score\":\"24\",\"Formatter\":[{\"Name\":\"PlcAlarm.Fault\",\"Pattern\":\"0:1:UINT\",\"Index\":\"5\"},{\"Name\":\"PlcAlarm.Warning\",\"Pattern\":\"1:1:UINT\",\"Index\":\"5\"},{\"Name\":\"PlcProduction.InPieceCount\",\"Pattern\":\"2:1:UINT\",\"Index\":\"5\"},{\"Name\":\"PlcProduction.GoodPieceCount\",\"Pattern\":\"3:1:UINT\",\"Index\":\"5\"},{\"Name\":\"PlcProduction.RejectPieceCount\",\"Pattern\":\"4:1:UINT\",\"Index\":\"5\"},{\"Name\":\"PlcProduction.ReworkPieceCount\",\"Pattern\":\"5:1:UINT\",\"Index\":\"5\"},{\"Name\":\"PlcProduction.LastCycleTime\",\"Pattern\":\"6:1:UINT\",\"Index\":\"5\"},{\"Name\":\"PlcProduction.PartType\",\"Pattern\":\"7:5:ASCII\",\"Index\":\"5\"},{\"Name\":\"PlcProduction.SerialNo\",\"Pattern\":\"17:7:ASCII\",\"Index\":\"5\"}]},{\"OpreationCode\":\"SUB1-2\",\"Seq\":\"6\",\"DevType\":\"R*\",\"Address\":\"603\",\"Score\":\"24\",\"Formatter\":[{\"Name\":\"PlcAlarm.Fault\",\"Pattern\":\"0:1:UINT\",\"Index\":\"6\"},{\"Name\":\"PlcAlarm.Warning\",\"Pattern\":\"1:1:UINT\",\"Index\":\"6\"},{\"Name\":\"PlcProduction.InPieceCount\",\"Pattern\":\"2:1:UINT\",\"Index\":\"6\"},{\"Name\":\"PlcProduction.GoodPieceCount\",\"Pattern\":\"3:1:UINT\",\"Index\":\"6\"},{\"Name\":\"PlcProduction.RejectPieceCount\",\"Pattern\":\"4:1:UINT\",\"Index\":\"6\"},{\"Name\":\"PlcProduction.ReworkPieceCount\",\"Pattern\":\"5:1:UINT\",\"Index\":\"6\"},{\"Name\":\"PlcProduction.LastCycleTime\",\"Pattern\":\"6:1:UINT\",\"Index\":\"6\"},{\"Name\":\"PlcProduction.PartType\",\"Pattern\":\"7:5:ASCII\",\"Index\":\"6\"},{\"Name\":\"PlcProduction.SerialNo\",\"Pattern\":\"17:7:ASCII\",\"Index\":\"6\"}]},{\"OpreationCode\":\"SUB1-3\",\"Seq\":\"7\",\"DevType\":\"R*\",\"Address\":\"703\",\"Score\":\"24\",\"Formatter\":[{\"Name\":\"PlcAlarm.Fault\",\"Pattern\":\"0:1:UINT\",\"Index\":\"7\"},{\"Name\":\"PlcAlarm.Warning\",\"Pattern\":\"1:1:UINT\",\"Index\":\"7\"},{\"Name\":\"PlcProduction.InPieceCount\",\"Pattern\":\"2:1:UINT\",\"Index\":\"7\"},{\"Name\":\"PlcProduction.GoodPieceCount\",\"Pattern\":\"3:1:UINT\",\"Index\":\"7\"},{\"Name\":\"PlcProduction.RejectPieceCount\",\"Pattern\":\"4:1:UINT\",\"Index\":\"7\"},{\"Name\":\"PlcProduction.ReworkPieceCount\",\"Pattern\":\"5:1:UINT\",\"Index\":\"7\"},{\"Name\":\"PlcProduction.LastCycleTime\",\"Pattern\":\"6:1:UINT\",\"Index\":\"7\"},{\"Name\":\"PlcProduction.PartType\",\"Pattern\":\"7:5:ASCII\",\"Index\":\"7\"},{\"Name\":\"PlcProduction.SerialNo\",\"Pattern\":\"17:7:ASCII\",\"Index\":\"7\"}]},{\"Seq\":\"8\",\"DevType\":\"X*\",\"Address\":\"0\",\"Score\":\"112\",\"Formatter\":[{\"Name\":\"PlcSensors.X0\",\"Pattern\":\"0:112:BINARY\"}]},{\"Seq\":\"9\",\"DevType\":\"X*\",\"Address\":\"1000\",\"Score\":\"56\",\"Formatter\":[{\"Name\":\"PlcSensors.X1000\",\"Pattern\":\"0:56:BINARY\"}]},{\"Seq\":\"10\",\"DevType\":\"Y*\",\"Address\":\"0\",\"Score\":\"112\",\"Formatter\":[{\"Name\":\"PlcSensors.Y0\",\"Pattern\":\"0:112:BINARY\"}]},{\"Seq\":\"11\",\"DevType\":\"Y*\",\"Address\":\"1000\",\"Score\":\"56\",\"Formatter\":[{\"Name\":\"PlcSensors.Y1000\",\"Pattern\":\"0:56:BINARY\"}]}],\"FormatterShema\":{\"PlcControl\":{\"Type\":\"Object\"},\"PlcStatus\":{\"Type\":\"Object\"},\"PlcProduction\":{\"Type\":\"List\",\"Length\":\"8\",\"StaticItems\":[{\"Index\":\"0\",\"Items\":[{\"Name\":\"OpCode\",\"Value\":\"OP10\"}]},{\"Index\":\"1\",\"Items\":[{\"Name\":\"OpCode\",\"Value\":\"OP20\"}]},{\"Index\":\"2\",\"Items\":[{\"Name\":\"OpCode\",\"Value\":\"OP30\"}]},{\"Index\":\"3\",\"Items\":[{\"Name\":\"OpCode\",\"Value\":\"OP40\"}]},{\"Index\":\"4\",\"Items\":[{\"Name\":\"OpCode\",\"Value\":\"OP50\"}]},{\"Index\":\"5\",\"Items\":[{\"Name\":\"OpCode\",\"Value\":\"SUB1-1\"}]},{\"Index\":\"6\",\"Items\":[{\"Name\":\"OpCode\",\"Value\":\"SUB1-2\"}]},{\"Index\":\"7\",\"Items\":[{\"Name\":\"OpCode\",\"Value\":\"SUB1-3\"}]}]},\"PlcAlarm\":{\"Type\":\"List\",\"Length\":\"8\",\"StaticItems\":[{\"Index\":\"0\",\"Items\":[{\"Name\":\"OpCode\",\"Value\":\"OP10\"}]},{\"Index\":\"1\",\"Items\":[{\"Name\":\"OpCode\",\"Value\":\"OP20\"}]},{\"Index\":\"2\",\"Items\":[{\"Name\":\"OpCode\",\"Value\":\"OP30\"}]},{\"Index\":\"3\",\"Items\":[{\"Name\":\"OpCode\",\"Value\":\"OP40\"}]},{\"Index\":\"4\",\"Items\":[{\"Name\":\"OpCode\",\"Value\":\"OP50\"}]},{\"Index\":\"5\",\"Items\":[{\"Name\":\"OpCode\",\"Value\":\"SUB1-1\"}]},{\"Index\":\"6\",\"Items\":[{\"Name\":\"OpCode\",\"Value\":\"SUB1-2\"}]},{\"Index\":\"7\",\"Items\":[{\"Name\":\"OpCode\",\"Value\":\"SUB1-3\"}]}]},\"PlcSensors\":{\"Type\":\"Object\"}}}");
		
		parser.parser();
		List<RequestReadObj> readObjList = parser.getRequestReadObjList();
		
		MitsubishiQSeriesApi api = new MitsubishiQSeriesApi(TransMode.BINARY, null);
		
		String rowData = api.multipleRead(readObjList);
		//스키마 형식
		System.out.println(parser.getJsonSchema().toJSONString());
		//rowData
		System.out.println(rowData);
		
		String result = hexToJson(rowData, readObjList, parser.getJsonSchema());
		//결과
		System.out.println(result);
		
	}
	

	/**
	 * Hex코드를 Jon형태로 가공
	 * 
	 * @param packet
	 * @return
	 * @throws Exception
	 */
	private static String hexToJson(String hexData, List<RequestReadObj> reqReadObjList, JSONObject resultObj) throws Exception {
		// payload가 있다면 아래에 json으로 가공함
		String subHexData;

		int index = 0; // 문자열 시작
		int length = 0;// 문자열 사이즈

		//스코어와 시작 지점을 계산하여 필요한 부분만 잘라 내 준다.
		for (RequestReadObj readObj : reqReadObjList) {
			length = Integer.parseInt(readObj.getDevScore()) * BLOCK_SIZE;
			subHexData = hexData.substring(index, index + length);
			index += length;

			jsonFormatterData(subHexData, readObj, resultObj);
		}

		return resultObj.toJSONString();
	}

	private static void jsonFormatterData(String hexData, RequestReadObj readObj, JSONObject resultObj) throws Exception {

		String[] keys;

		for (FormatterObj formatter : readObj.getFormater()) {
			keys = formatter.getName().split("\\.");
			
			if(!resultObj.containsKey(keys[0])){
				throw new Exception("사용자가 정의한 FormatterShema가 올바르지 않습니다. FormatterShema를 확인해주세요");
			}else {
				//객체가 JsonObject라면 원래 방식으로 put 한다.
				if(resultObj.get(keys[0]) instanceof JSONObject) {
					putStringIntoObj(keys, EditUtil.parserRecvData(hexData, formatter.getPattern()), resultObj);
				}else {
					//객체가 JsonList라면 List에 담는방법으로 put 한다
					putStringIntoList(keys, EditUtil.parserRecvData(hexData, formatter.getPattern()), formatter.getIndex(),resultObj);
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private static void putStringIntoObj(String[] keys, String value, JSONObject jsonObj) {

		JSONObject targetJsonObj = null;
		JSONObject tempJsonObj = jsonObj;
		
		
		//끝부분이 될떄 까지 Json을 파고 들어간다.
		for (int i = 0; i < keys.length - 1; i++) {
			targetJsonObj = (JSONObject) tempJsonObj.get(keys[i]);
			tempJsonObj = targetJsonObj;
		}
		//타겟 json이 null이라면 tempJson으로 취환한다.
		if (targetJsonObj == null)
			targetJsonObj = jsonObj;
		
		targetJsonObj.put(keys[keys.length - 1], value);
	}

	@SuppressWarnings("unchecked")
	private static void putStringIntoList(String[] keys, String value, String index, JSONObject jsonObj) {

		JSONArray tmpArray = null;

		JSONObject targetJsonObj = null;
		JSONObject tempJsonObj = jsonObj;

		//끝부분이 될떄 까지 Json을 파고 들어간다. 
		for (int i = 0; i < keys.length - 1; i++) {
			tmpArray = (JSONArray) tempJsonObj.get(keys[i]);
			tempJsonObj = targetJsonObj;
		}
		
		//타겟 json이 null이라면 tempJson으로 취환한다.
		if (targetJsonObj == null)
			targetJsonObj = jsonObj;
		
		//선언된 인덱스에 추가한다.
		tempJsonObj = (JSONObject) tmpArray.get(Integer.parseInt(index));
		tempJsonObj.put(keys[keys.length-1], value);
		
	}
}
