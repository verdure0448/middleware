package com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.session;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.TimeZone;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.hdbsnc.smartiot.adapter.websocketapi.constant.WebSocketAdapterConst;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractFunctionHandler;
import com.hdbsnc.smartiot.common.ism.sm.IFunctionMetaData;
import com.hdbsnc.smartiot.common.ism.sm.ISession;
import com.hdbsnc.smartiot.common.ism.sm.ISessionManager;
import com.hdbsnc.smartiot.common.pm.IProfileManager;
import com.hdbsnc.smartiot.pm.util.TimeUtil;

/**
 * session/func/get/all 세션기능(제어) 정보 조회
 * 
 * @author KANG
 *
 */
public class SessionFuncGetAllHandler extends AbstractFunctionHandler {

	private ISessionManager sm;
	private IProfileManager pm;

	private SimpleDateFormat formatter;
	private TimeZone tz;
	
	public SessionFuncGetAllHandler(IProfileManager pm, ISessionManager sm) {
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

		Set<String> funcKeys = session.getFunctionKeys();

		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObj = null;

		IFunctionMetaData functionMetadata ;
//		IInstanceFunctionObj iInsFuncObj =null;
		for (String key : funcKeys) { 
//			iInsFuncObj = pm.getInstanceFunctionObj(session.getAdapterInstanceId(), key);
//
//			if(iInsFuncObj==null){
//				continue;
//			}
			functionMetadata=session.getFunction(key);
			
			if(functionMetadata==null){
				continue;
			}
			
			jsonObj = new JSONObject();

			jsonObj.put(WebSocketAdapterConst.IID, session.getAdapterInstanceId());
			jsonObj.put(WebSocketAdapterConst.FUNC_KEY, key);
			jsonObj.put(WebSocketAdapterConst.CONTENT_TYPE, session.getFunction(key).getContentType());
			
			for(int i=0; i<functionMetadata.getParametersCount(); i++){
				jsonObj.put(WebSocketAdapterConst.PARAM+(i+1), functionMetadata.getParamterName(i));
				jsonObj.put(WebSocketAdapterConst.PARAM_TYPE+(i+1), functionMetadata.getParamterType(i));					
			}
//			jsonObj.put(WebSocketAdapterConst.REMARK, iInsFuncObj.getRemark());
			jsonObj.put(WebSocketAdapterConst.ALTER_DATE, TimeUtil.changeFormat(formatter.format(new Date(session.getCreatedTime()))));
			jsonObj.put(WebSocketAdapterConst.REG_DATE, TimeUtil.changeFormat(formatter.format(new Date(session.getLastAccessedTime()))));

			jsonArray.add(jsonObj);
		}

//		Collections.sort(jsonArray, new NameAscCompare());

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

//class NameAscCompare implements Comparator<JSONObject> {
//
//	@Override
//	public int compare(JSONObject arg0, JSONObject arg1) {
//
//		return ((String) arg0.get(WebSocketAdapterConst.ATT_DESCRIPTION))
//				.compareTo((String) arg1.get(WebSocketAdapterConst.ATT_DESCRIPTION));
//	}
//
//}
