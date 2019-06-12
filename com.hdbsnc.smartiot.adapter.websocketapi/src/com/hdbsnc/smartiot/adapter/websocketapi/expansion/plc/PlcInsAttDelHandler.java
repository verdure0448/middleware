package com.hdbsnc.smartiot.adapter.websocketapi.expansion.plc;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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
 * plc/ins/att/del
 * 
 * @author KANG
 *
 */
public class PlcInsAttDelHandler extends AbstractFunctionHandler {

	private IProfileManager pm;
	private ISessionManager sm;
	private Pattern p1 = Pattern.compile("(.*)\\?(.*)");
	
	public PlcInsAttDelHandler(IProfileManager pm, ISessionManager sm) {
		super("del");
		this.pm = pm;
		this.sm = sm;
	}

	@Override
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		String iid = inboundCtx.getParams().get(WebSocketAdapterConst.IID);
		String attKey = inboundCtx.getParams().get(WebSocketAdapterConst.ATT_KEY);

		ISessionManager iSessionMgr = sm.getIntegratedSessionManager().getSessionManager(iid);
		//////////////////////////////////////////////////////////////////////
		// 삭제 대상 인스턴스 속성이 기동중인 경우 에러 응답
		//////////////////////////////////////////////////////////////////////
		if (iSessionMgr != null) {
			List<ISession> sessionList = iSessionMgr.getSessionList();
			for (ISession iSession : sessionList) {
				if (ISessionState.SESSION_STATE_DISPOSE > iSession.getState())
					throw getCommonService().getExceptionfactory().createAppException(
							this.getClass().getName() + ":005", new String[]{iid, attKey}, null);
			}
		}

		//////////////////////////////////////////////////////////////////////
		// 인스턴스 속성 삭제
		//////////////////////////////////////////////////////////////////////
		List<IInstanceAttributeObj> insAttList = pm.getInstanceAttributeList(iid);
		List<String> deleteAttkeyList = new ArrayList<String>();
		if(insAttList != null){
			for (IInstanceAttributeObj iInstanceAttributeObj : insAttList) {
				Matcher m1 = p1.matcher(iInstanceAttributeObj.getKey());
				if(m1.find()){
					if(m1.group(1).equals(attKey)){
						deleteAttkeyList.add(iInstanceAttributeObj.getKey());
					}
				} else {
					if(iInstanceAttributeObj.getKey().equals(attKey)){
						deleteAttkeyList.add(iInstanceAttributeObj.getKey());
					}
				}
			}
		}
		
		
		IModifyInstanceAttributeObj mInsAttObj = pm.getModifyInstanceAttributeObj();

		try {
			for (String deleteAttKey : deleteAttkeyList) {
				mInsAttObj.insId(iid).key(deleteAttKey).delete();
			}
			
		} catch (Exception e) {
			throw getCommonService().getExceptionfactory().createAppException(
					this.getClass().getName() + ":010", new String[]{iid, attKey}, e);
		}

		outboundCtx.getPaths().add("ack");
		outboundCtx.setSID(inboundCtx.getSID());
		outboundCtx.setSPort(inboundCtx.getSPort());
		outboundCtx.setTID("this");
		outboundCtx.setTPort(inboundCtx.getTPort());
		outboundCtx.setTransmission("res");

	}
}
