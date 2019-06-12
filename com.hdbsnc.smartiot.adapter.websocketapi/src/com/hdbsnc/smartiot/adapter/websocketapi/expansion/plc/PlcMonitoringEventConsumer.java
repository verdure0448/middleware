package com.hdbsnc.smartiot.adapter.websocketapi.expansion.plc;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;

import com.hdbsnc.smartiot.adapter.websocketapi.constant.ProtocolConst;
import com.hdbsnc.smartiot.adapter.websocketapi.constant.WebSocketAdapterConst;
import com.hdbsnc.smartiot.common.ICommonService;
import com.hdbsnc.smartiot.common.aim.IAdapterProcessorEvent;
import com.hdbsnc.smartiot.common.connection.IConnection;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.em.IAdapterProcessorEventConsumer;
import com.hdbsnc.smartiot.common.exception.CommonException;
import com.hdbsnc.smartiot.common.otp.url.parser.UrlParser;
import com.hdbsnc.smartiot.common.otp.url.parser.vo.Url;
import com.hdbsnc.smartiot.common.pm.IProfileManager;
import com.hdbsnc.smartiot.common.pm.vo.IDeviceObj;
import com.hdbsnc.smartiot.common.pm.vo.IInstanceAttributeObj;
import com.hdbsnc.smartiot.common.pm.vo.IInstanceObj;

public class PlcMonitoringEventConsumer implements IAdapterProcessorEventConsumer {
	static SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy/MM/dd/ HH:mm:ss");

	private IConnection con;
	private UrlParser parser;
	private ICommonService comService;
	private String evtId;
	private String iid;
	IContext inboundCtx;
	private IProfileManager pm;

	private Pattern p1 = Pattern.compile("(.*)\\?(.*)");
	private Pattern pp1 = Pattern.compile("(.*)=(.*)");

	public PlcMonitoringEventConsumer(IContext inboundCtx, IConnection con, ICommonService comService, String iid,
			String evtId, IProfileManager pm) {
		this.inboundCtx = inboundCtx;
		this.con = con;
		this.comService = comService;
		this.evtId = evtId;
		this.iid = iid;
		this.parser = UrlParser.getInstance();
		this.pm = pm;
	}

	@Override
	public String getName() {
		return evtId;
	}

	@Override
	public synchronized void initialize() throws Exception {

	}

	@Override
	public synchronized void dispose() {

	}

	@Override
	public void updateEvent(IAdapterProcessorEvent ape) throws CommonException {
		int apeState = ape.getAdapterProcessEventStateType();

		IContext iContext = ape.getContext();
		if (!iid.equals(ape.getIID()))
			return;
		if(iContext.getTransmission() == null || "req".equals(iContext.getTransmission())){
			return;
		}

		String stateName = "";
		if (apeState == IAdapterProcessorEvent.STATE_BEGIN) {
			stateName = "BEGIN";
		} else if (apeState == IAdapterProcessorEvent.STATE_ERROR) {
			stateName = "ERROR";
		} else if (apeState == IAdapterProcessorEvent.STATE_FAIL) {
			stateName = "FAIL";
		} else if (apeState == IAdapterProcessorEvent.STATE_INBOUND_TRANSFER) {
			stateName = "INBOUND_TRANSFER";
		} else if (apeState == IAdapterProcessorEvent.STATE_OUTBOUND_TRANSFER) {
			stateName = "OUTBOUND_TRANSFER";
		} else if (apeState == IAdapterProcessorEvent.STATE_SUCCESS) {
			stateName = "SUCCESS";
		}

		// 속성키
		String attKey = ape.getContext().getFullPath().replaceAll("/ack$", "").replaceAll("/nack$", "");
		Map<String, String> attParmaMap = new HashMap<String, String>();
		JSONObject json = new JSONObject();
		IInstanceObj insObj = null;
		IDeviceObj devObj = null;
		IInstanceAttributeObj insAttObj = null;

		// 처리시간
		json.put(WebSocketAdapterConst.EVENT_TIME, formatter.format(new Date()));

		// pm객체 조회
		try {
			insObj = pm.getInstanceObj(ape.getIID());
			List<IInstanceAttributeObj> insAttObjList = pm.getInstanceAttributeList(ape.getIID());
			if (insAttObjList != null) {
				for (IInstanceAttributeObj iInstanceAttributeObj : insAttObjList) {
					Matcher m1 = p1.matcher(iInstanceAttributeObj.getKey());
					if (m1.find() && attKey.equals(m1.group(1))) {
						insAttObj = iInstanceAttributeObj;
						String[] paramInfos = m1.group(2).split("&");
						if (paramInfos != null && paramInfos.length > 0) {
							for (String paramInfo : paramInfos) {
								Matcher mm1 = pp1.matcher(paramInfo);
								if (mm1.find()) {
									attParmaMap.put(mm1.group(1), mm1.group(2));
								} else {
									attParmaMap.put(paramInfo, "");
								}
							}
						}
						break;
					}
				}
			}
			devObj = pm.getDeviceObj(iContext.getTID());
		} catch (Exception e) {
			// 처리 없음
		}

		json.put(WebSocketAdapterConst.IID, ape.getIID());
		json.put(WebSocketAdapterConst.INS_NAME, insObj.getInsNm());
		// 장치ID, 장치명
		json.put(WebSocketAdapterConst.DID, devObj.getDevId());
		json.put(WebSocketAdapterConst.DEV_NAME, devObj.getDevNm());
		// 속성키, 속성명
		json.put(WebSocketAdapterConst.ATT_KEY, attKey);
		json.put(WebSocketAdapterConst.ATT_DESCRIPTION, insAttObj.getDsct());
		// 디바이스 구분
		json.put(WebSocketAdapterConst.DEVICE_TYPE, attParmaMap.get("dtype"));
		// 디바이스 번지
		json.put(WebSocketAdapterConst.DEVICE_ADDRESS, attParmaMap.get("address"));
		// 디바이스 점수
		json.put(WebSocketAdapterConst.DEVICE_SCORE, attParmaMap.get("score"));
		// 수집주기
		json.put(WebSocketAdapterConst.GATHERING_PERIOD, attParmaMap.get("period"));
		// 속성값(응답 파라미터에서)
		Map<String, String> params = iContext.getParams();
		json.put(WebSocketAdapterConst.ATT_VALUE, params.get("read"));
		// 종료코드(응답 파라미터에서)
		json.put("code", params.get("code"));
		// 메세지(응답 파라미터에서)
		json.put("msg", params.get("msg"));
	
		Url resUrl = Url.createOtp();
		
		resUrl.addPath("plc").addPath("monitoring").addPath("start");
		resUrl.addPath(ProtocolConst.ACK);
		resUrl.setUserInfo(inboundCtx.getSID(), inboundCtx.getSPort());
		resUrl.setHostInfo(ProtocolConst.THIS, inboundCtx.getTPort());
		resUrl.addFrag(ProtocolConst.TRANS, ProtocolConst.TRANS_EVT);
		resUrl.addFrag(ProtocolConst.CONT, ProtocolConst.CONT_JSON);
		try {
			this.con.write(parser.parse(resUrl) + json.toJSONString());
//			System.out.println("[PlcGatheringEventConsumer]" + parser.parse(resUrl) + json.toJSONString());
		} catch (Exception e) {
			e.printStackTrace();
			throw comService.getExceptionfactory().createSysException(this.getClass().getName() + ":005", null, e);
		}
	}

}