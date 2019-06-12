package com.hdbsnc.smartiot.adapter.websocketapi.expansion.plc;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.hdbsnc.smartiot.adapter.websocketapi.constant.WebSocketAdapterConst;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractFunctionHandler;
import com.hdbsnc.smartiot.common.ism.sm.ISession;
import com.hdbsnc.smartiot.common.ism.sm.ISessionManager;
import com.hdbsnc.smartiot.common.ism.sm.ISessionState;
import com.hdbsnc.smartiot.common.pm.IProfileManager;
import com.hdbsnc.smartiot.common.pm.vo.IInstanceAttributeObj;
import com.hdbsnc.smartiot.common.pm.vo.IModifyInstanceAttributeObj;

/**
 * 
 * PLC 인스턴스 속성 등록 경로 plc/ins/att/put
 * 
 * @author KANG
 *
 */
public class PlcInsAttPutHandler extends AbstractFunctionHandler {

	private IProfileManager pm;
	private ISessionManager sm;
	
	private Pattern p1 = Pattern.compile("(.*)\\?(.*)");
	
	public PlcInsAttPutHandler(IProfileManager pm, ISessionManager sm) {
		super("put");
		this.pm = pm;
		this.sm = sm;
	}

	@Override
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {

		String iid = inboundCtx.getParams().get(WebSocketAdapterConst.IID);
		String attKey = inboundCtx.getParams().get(WebSocketAdapterConst.ATT_KEY);

		JSONObject inputJson = null;

		try {
			inputJson = (JSONObject) (new JSONParser())
					.parse(new String(inboundCtx.getContent().array(), StandardCharsets.UTF_8));
		} catch (Exception e) {
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":005", null,
					e);
		}

		String devType = (String) inputJson.get(WebSocketAdapterConst.DEVICE_TYPE);
		String devAddress = (String) inputJson.get(WebSocketAdapterConst.DEVICE_ADDRESS);
		String devScore = (String) inputJson.get(WebSocketAdapterConst.DEVICE_SCORE);
		String gatheringPeriod = (String) inputJson.get(WebSocketAdapterConst.GATHERING_PERIOD);

		//////////////////////////////////////////////////////////////////////
		// 등록 대상 인스턴스가 기동중인 경우 에러 응답
		//////////////////////////////////////////////////////////////////////
		ISessionManager iSessionMgr = sm.getIntegratedSessionManager().getSessionManager(iid);
		if (iSessionMgr != null) {
			List<ISession> sessionList = iSessionMgr.getSessionList();
			for (ISession iSession : sessionList) {
				if (ISessionState.SESSION_STATE_DISPOSE > iSession.getState())
					throw getCommonService().getExceptionfactory()
							.createAppException(this.getClass().getName() + ":010", new String[] { iid, attKey }, null);
			}
		}

		
		
		IModifyInstanceAttributeObj mObj = pm.getModifyInstanceAttributeObj();

		// 인스턴스ID
		mObj.insId(iid);
		// 속성키 전체 경로 작성
		StringBuffer attFullKeyBuf = new StringBuffer(attKey);
		attFullKeyBuf.append("?create&read&dtype=").append(devType).append("&address=").append(devAddress)
				.append("&score=").append(devScore).append("&period=").append(gatheringPeriod);

		// 속성키
		mObj.key(attFullKeyBuf.toString());
		// 속성이름
		mObj.dsct((String) inputJson.get(WebSocketAdapterConst.ATT_DESCRIPTION));
		// 속성값
		mObj.value((String) inputJson.get(WebSocketAdapterConst.ATT_VALUE));
		// 속성타입
		mObj.valueType("smartiot.readonly");
		// 비고
		mObj.remark((String) inputJson.get(WebSocketAdapterConst.REMARK));

		//////////////////////////////////////////////////////////////////////
		// 등록 인스턴스 속성이 중복일 경우 에러 응답
		//////////////////////////////////////////////////////////////////////
		List<IInstanceAttributeObj> insAttList = pm.getInstanceAttributeList(iid);
		if(insAttList != null){
			for (IInstanceAttributeObj iInstanceAttributeObj : insAttList) {
				Matcher m1 = p1.matcher(iInstanceAttributeObj.getKey());
				if(m1.find()){
					if(m1.group(1).equals(attKey)){
						// 중복 발생
						throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":015",
								new String[] {attKey});
					}
				} else {
					if(iInstanceAttributeObj.getKey().equals(attKey)){
						// 중복 발생
						throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":020",
								new String[] {attKey});
					}
				}
			}
		}
		
		
		
		//////////////////////////////////////////////////////////////////////
		// 인스턴스 속성 등록 
		//////////////////////////////////////////////////////////////////////
		try {
			mObj.insert();
		} catch (Exception ex) {
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":025",
					new String[] { iid, attKey }, ex);
		}

		outboundCtx.getPaths().add("ack");
		outboundCtx.setSID(inboundCtx.getSID());
		outboundCtx.setSPort(inboundCtx.getSPort());
		outboundCtx.setContenttype(null);
		outboundCtx.setContent(null);
		outboundCtx.setTID("this");
		outboundCtx.setTPort(inboundCtx.getTPort());
		outboundCtx.setTransmission("res");

	}
}
