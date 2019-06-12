package com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.session;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Set;
import java.util.TimeZone;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.hdbsnc.smartiot.adapter.websocketapi.constant.WebSocketAdapterConst;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractFunctionHandler;
import com.hdbsnc.smartiot.common.ism.sm.ISession;
import com.hdbsnc.smartiot.common.ism.sm.ISessionManager;
import com.hdbsnc.smartiot.common.pm.IProfileManager;
import com.hdbsnc.smartiot.common.pm.vo.IInstanceAttributeObj;
import com.hdbsnc.smartiot.pm.util.TimeUtil;

/**
 * session/att/get/all 세션 속성(제어) 정보 조회
 * 
 * @author KANG
 *
 */
public class SessionAttGetAllHandler extends AbstractFunctionHandler {

	private ISessionManager sm;
	private IProfileManager pm;

	private SimpleDateFormat formatter;
	private TimeZone tz;
	
	public SessionAttGetAllHandler(IProfileManager pm, ISessionManager sm) {
		super("all");
		this.sm = sm;
		this.pm = pm;
		this.formatter = new java.text.SimpleDateFormat("yyyyMMddHHmmss");
		this.tz = TimeZone.getTimeZone("Asia/Seoul");
		this.formatter.setTimeZone(tz);
	}

	@Override
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		String did = inboundCtx.getParams().get(WebSocketAdapterConst.DID);

		ISession session = sm.getIntegratedSessionManager().getSession(did);

		// if (session == null) throw new
		// ContextHandlerApplicationException(2003, CommonException.TYPE_INFO,
		// "세션이 존재하지 않습니다.");
		if (session == null)
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":005", new String[]{did});

		Set<String> attKeys = session.getAttributeKeys();

		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObj = null;

		IInstanceAttributeObj iInsAttObj = null;
		for (String key : attKeys) { 
			iInsAttObj = pm.getInstanceAttributeObj(session.getAdapterInstanceId(), key);

			if(iInsAttObj==null){
				continue;
			}
			jsonObj = new JSONObject();

			jsonObj.put(WebSocketAdapterConst.IID, session.getAdapterInstanceId());
			jsonObj.put(WebSocketAdapterConst.ATT_KEY, key);
			jsonObj.put(WebSocketAdapterConst.ATT_DESCRIPTION, iInsAttObj.getDsct());
			jsonObj.put(WebSocketAdapterConst.ATT_VALUE, session.getAttribute(key).getValue());
			jsonObj.put(WebSocketAdapterConst.ATT_VALUE_TYPE, iInsAttObj.getValueType());
			jsonObj.put(WebSocketAdapterConst.REMARK, iInsAttObj.getRemark());
			jsonObj.put(WebSocketAdapterConst.ALTER_DATE, TimeUtil.changeFormat(formatter.format(new Date(session.getLastAccessedTime()))));
			jsonObj.put(WebSocketAdapterConst.REG_DATE, TimeUtil.changeFormat(formatter.format(new Date(session.getCreatedTime()))));

			jsonArray.add(jsonObj);
		}

		Collections.sort(jsonArray, new NameAscCompare());

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

class NameAscCompare implements Comparator<JSONObject> {

	@Override
	public int compare(JSONObject arg0, JSONObject arg1) {

		return ((String) arg0.get(WebSocketAdapterConst.ATT_DESCRIPTION))
				.compareTo((String) arg1.get(WebSocketAdapterConst.ATT_DESCRIPTION));
	}

}
