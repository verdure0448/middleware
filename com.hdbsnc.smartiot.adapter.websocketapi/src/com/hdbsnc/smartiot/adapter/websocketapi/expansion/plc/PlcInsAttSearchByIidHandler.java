package com.hdbsnc.smartiot.adapter.websocketapi.expansion.plc;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.hdbsnc.smartiot.adapter.websocketapi.constant.WebSocketAdapterConst;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractFunctionHandler;
import com.hdbsnc.smartiot.common.em.IEventManager;
import com.hdbsnc.smartiot.common.pm.IProfileManager;
import com.hdbsnc.smartiot.common.pm.vo.IInstanceAttributeObj;
import com.hdbsnc.smartiot.common.pm.vo.IInstanceObj;

/**
 * plc/ins/att/search/by-iid
 * 
 * @author KANG
 *
 */
public class PlcInsAttSearchByIidHandler extends AbstractFunctionHandler {

	private IProfileManager pm;
	private IEventManager em;

	private Pattern p1 = Pattern.compile("(.*)\\?(.*)");
	private Pattern pp1 = Pattern.compile("(.*)=(.*)");
	
	public PlcInsAttSearchByIidHandler(IProfileManager pm, IEventManager em) {
		super("by-iid");
		this.pm = pm;
		this.em = em;
	}

	@Override
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		String iid = inboundCtx.getParams().get(WebSocketAdapterConst.IID);
		List<IInstanceAttributeObj> insAttList = pm.getInstanceAttributeList(iid);
		if (insAttList == null || insAttList.size() == 0)
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":005");

		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObj = null;

		// 인스턴스ID로 장치ID조회
		IInstanceObj insObj = pm.getInstanceObj(iid);
		
		
		for (IInstanceAttributeObj iInstanceAttributeObj : insAttList) {	
			Matcher m1 = p1.matcher(iInstanceAttributeObj.getKey());
			if(m1.find()){
				jsonObj = new JSONObject();
				jsonObj.put(WebSocketAdapterConst.IID, iInstanceAttributeObj.getInsId());
				
				String[] paramInfos = m1.group(2).split("&");
				Map paramMap = new HashMap<String, String>();
				
				if(paramInfos != null && paramInfos.length > 0){
					for (String paramInfo : paramInfos) {
						Matcher mm1 = pp1.matcher(paramInfo);
						if(mm1.find()){
							paramMap.put(mm1.group(1), mm1.group(2));
						} else {
							paramMap.put(paramInfo, "");
						}
					}
				}
				// 이벤트 상태
				String eventID = insObj.getDefaultDevId() + "/" + m1.group(1);
				if(em.containPollingAdapterProcessor(eventID)){
					jsonObj.put(WebSocketAdapterConst.EVENT_STATUS, "1");
				} else {
					jsonObj.put(WebSocketAdapterConst.EVENT_STATUS, "0");
				}
				
				// 디바이스 구분
				jsonObj.put(WebSocketAdapterConst.DEVICE_TYPE, paramMap.get("dtype"));
				// 디바이스 어드레스
				jsonObj.put(WebSocketAdapterConst.DEVICE_ADDRESS, paramMap.get("address"));
				// 디바이스 점수
				jsonObj.put(WebSocketAdapterConst.DEVICE_SCORE, paramMap.get("score"));
				// 수집주기
				jsonObj.put(WebSocketAdapterConst.GATHERING_PERIOD, paramMap.get("period"));
				// 속성키
				jsonObj.put(WebSocketAdapterConst.ATT_KEY, m1.group(1));
				// 속성명
				jsonObj.put(WebSocketAdapterConst.ATT_DESCRIPTION, iInstanceAttributeObj.getDsct());
				// 속성값 타입
				jsonObj.put(WebSocketAdapterConst.ATT_VALUE_TYPE, iInstanceAttributeObj.getValueType());
				// 속성값
				jsonObj.put(WebSocketAdapterConst.ATT_VALUE, iInstanceAttributeObj.getValue());
				// 비고
				jsonObj.put(WebSocketAdapterConst.REMARK, iInstanceAttributeObj.getRemark());
				// 변경일시
				jsonObj.put(WebSocketAdapterConst.ALTER_DATE, iInstanceAttributeObj.getAlterDate());
				// 등록일시
				jsonObj.put(WebSocketAdapterConst.REG_DATE, iInstanceAttributeObj.getRegDate());
				
				jsonArray.add(jsonObj);
			} else {
				// PLC속성이 올바르지 않으므로 일단은 무시한다.
			}
		}

		outboundCtx.getPaths().add("ack");
		outboundCtx.setSID(inboundCtx.getSID());
		outboundCtx.setSPort(inboundCtx.getSPort());
		outboundCtx.setTID("this");
		outboundCtx.setTPort(inboundCtx.getTPort());
		outboundCtx.setTransmission("res");
		outboundCtx.setContenttype("json");
		outboundCtx.setContent(ByteBuffer.wrap(jsonArray.toJSONString().getBytes()));

	}

}
