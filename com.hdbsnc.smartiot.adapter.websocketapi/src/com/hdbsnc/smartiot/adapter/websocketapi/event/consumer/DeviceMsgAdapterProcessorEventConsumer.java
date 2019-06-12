package com.hdbsnc.smartiot.adapter.websocketapi.event.consumer;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import org.json.simple.JSONObject;

import com.hdbsnc.smartiot.adapter.websocketapi.constant.ProtocolConst;
import com.hdbsnc.smartiot.adapter.websocketapi.constant.WebSocketAdapterConst;
import com.hdbsnc.smartiot.common.ICommonService;
import com.hdbsnc.smartiot.common.aim.IAdapterProcessorEvent;
import com.hdbsnc.smartiot.common.connection.IConnection;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.em.IAdapterProcessorEventConsumer;
import com.hdbsnc.smartiot.common.em.event.IEvent;
import com.hdbsnc.smartiot.common.exception.CommonException;
import com.hdbsnc.smartiot.common.otp.url.parser.UrlParser;
import com.hdbsnc.smartiot.common.otp.url.parser.vo.Url;
import com.hdbsnc.smartiot.common.pm.IProfileManager;
import com.hdbsnc.smartiot.common.pm.vo.IDeviceObj;
import com.hdbsnc.smartiot.common.pm.vo.IInstanceAttributeObj;
import com.hdbsnc.smartiot.common.pm.vo.IInstanceObj;

public class DeviceMsgAdapterProcessorEventConsumer implements IAdapterProcessorEventConsumer{
	
	
	private IConnection con;
	private UrlParser parser;
	private ICommonService comService;
	private String evtId;
	private String iid;
	IContext inboundCtx;
	private IProfileManager pm;

	private SimpleDateFormat formatter;
	private TimeZone tz;
	
	public DeviceMsgAdapterProcessorEventConsumer(IContext inboundCtx, IConnection con, ICommonService comService, String iid, String evtId, IProfileManager pm) {
		this.inboundCtx = inboundCtx;
		this.con = con;
		this.comService = comService;
		this.evtId = evtId;
		this.iid = iid;
		this.parser = UrlParser.getInstance();
		this.pm = pm;
		
		formatter = new java.text.SimpleDateFormat("yyyy/MM/dd/ HH:mm:ss");
		this.tz = TimeZone.getTimeZone("Asia/Seoul");
		this.formatter.setTimeZone(tz);
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
//		int apeType = ape.getAdapterProcessEventType();
		int apeState = ape.getAdapterProcessEventStateType();
		
		IContext iContext = ape.getContext();
		if(!iid.equals(ape.getIID())) return;
		String stateName = "";
		if(apeState==IAdapterProcessorEvent.STATE_BEGIN){
			stateName = "BEGIN";
		}else if(apeState==IAdapterProcessorEvent.STATE_ERROR){
			stateName = "ERROR";
		}else if(apeState==IAdapterProcessorEvent.STATE_FAIL){
			stateName = "FAIL";
		}else if(apeState==IAdapterProcessorEvent.STATE_INBOUND_TRANSFER){
			stateName = "INBOUND_TRANSFER";
		}else if(apeState==IAdapterProcessorEvent.STATE_OUTBOUND_TRANSFER){
			stateName = "OUTBOUND_TRANSFER";
		}else if(apeState==IAdapterProcessorEvent.STATE_SUCCESS){
			stateName = "SUCCESS";
		}
		
		JSONObject json = new JSONObject();
		IInstanceObj insObj = null;
		IDeviceObj devObj = null;
		IInstanceAttributeObj insAttObj = null;
		json.put(WebSocketAdapterConst.SID, iContext.getSID());
		json.put(WebSocketAdapterConst.IID, ape.getIID());
		try {
			insObj = this.pm.getInstanceObj(ape.getIID());
		} catch (Exception e) {}
		if (insObj != null) {
			json.put(WebSocketAdapterConst.INS_NAME, insObj.getInsNm());
		} else {
			json.put(WebSocketAdapterConst.INS_NAME, "");
		}
		json.put(WebSocketAdapterConst.TID, iContext.getTID());
		
		try {
			devObj = pm.getDeviceObj(iContext.getTID());
		} catch (Exception e) {}
		if (devObj != null) {
			json.put(WebSocketAdapterConst.DEV_NAME, devObj.getDevNm());
		} else {
			json.put(WebSocketAdapterConst.DEV_NAME, "");
		}
		String attKey = iContext.getFullPath();
		String tran = ape.getContext().getTransmission();
		
		if(tran==null || tran.equals("")){
			json.put(WebSocketAdapterConst.TRAN, "REQUEST/"+stateName);
		}else{
			if(tran.equals("req")){
				tran = "REQUEST/"+stateName;
			}else if(tran.equals("res")){
				tran = "RESPONSE/"+stateName;
			}else if(tran.equals("evt")){
				tran = "EVENT/"+stateName;
			}else{
				tran = tran.toUpperCase()+"/"+stateName;
			}
			json.put(WebSocketAdapterConst.TRAN, tran);
		}
		json.put(WebSocketAdapterConst.ATT_KEY, attKey);
		
		try {
			insAttObj = pm.getInstanceAttributeObj(ape.getIID(), attKey);
		} catch (Exception e) {}
		if (insAttObj != null) {
			json.put(WebSocketAdapterConst.ATT_DESCRIPTION, insAttObj.getDsct());
		} else {
			json.put(WebSocketAdapterConst.ATT_DESCRIPTION, "");
		}
		
		Map<String, String> params = iContext.getParams();
		StringBuffer commandKeyBuf = new StringBuffer();
		StringBuffer commandValueBuf = new StringBuffer();
		for (Map.Entry<String, String> elem : params.entrySet()) {
			commandKeyBuf.append("[").append(elem.getKey()).append("]");
			commandValueBuf.append("[").append(elem.getValue()).append("]");
		}
		json.put(WebSocketAdapterConst.CMD_KEY, commandKeyBuf.toString());
		json.put(WebSocketAdapterConst.CMD_VALUE, commandValueBuf.toString());

		if(iContext.getContentType() != null && iContext.getContent() !=null ){
			json.put(WebSocketAdapterConst.CONTENT_TYPE, iContext.getContentType());
			json.put(WebSocketAdapterConst.CONTENT,	new String(iContext.getContent().array(), Charset.forName("UTF-8")));
		}else{
			json.put(WebSocketAdapterConst.CONTENT_TYPE, "");
			json.put(WebSocketAdapterConst.CONTENT,	"");
		}

		json.put(WebSocketAdapterConst.EVENT_ID, evtId);
		json.put(WebSocketAdapterConst.EVENT_TIME, formatter.format(new Date()));
		
		Url resUrl = Url.createOtp();
		resUrl.addPath("event").addPath("dmsg").addPath("start");
		resUrl.addPath(ProtocolConst.ACK);
		resUrl.setUserInfo(inboundCtx.getSID(), inboundCtx.getSPort());
		resUrl.setHostInfo(ProtocolConst.THIS, inboundCtx.getTPort());
		resUrl.addFrag(ProtocolConst.TRANS, ProtocolConst.TRANS_EVT);
		resUrl.addFrag(ProtocolConst.CONT, ProtocolConst.CONT_JSON);
		try {
			this.con.write(parser.parse(resUrl) + json.toJSONString());
		} catch (Exception e) {
			e.printStackTrace();
			throw comService.getExceptionfactory().createSysException(this.getClass().getName() + ":005",
					null, e);
		}		
	}

}