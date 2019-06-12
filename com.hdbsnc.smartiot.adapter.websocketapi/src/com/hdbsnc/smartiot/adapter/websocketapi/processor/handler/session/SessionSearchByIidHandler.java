package com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.session;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.hdbsnc.smartiot.adapter.websocketapi.constant.WebSocketAdapterConst;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractFunctionHandler;
import com.hdbsnc.smartiot.common.ism.sm.ISession;
import com.hdbsnc.smartiot.common.ism.sm.ISessionManager;
import com.hdbsnc.smartiot.pm.util.TimeUtil;
/**
 * session/search/by-iid 세션 조회
 * 
 * @author KANG
 *
 */
public class SessionSearchByIidHandler extends AbstractFunctionHandler {

	private ISessionManager sm;

	private SimpleDateFormat formatter;
	private TimeZone tz;
	
	public SessionSearchByIidHandler(ISessionManager sm) {
		super("by-iid");
		this.sm = sm;
		this.formatter = new java.text.SimpleDateFormat("yyyyMMddHHmmss");
		this.tz = TimeZone.getTimeZone("Asia/Seoul");
		this.formatter.setTimeZone(tz);
	}

	@Override
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		String iid = inboundCtx.getParams().get(WebSocketAdapterConst.IID);

		ISessionManager ism = sm.getIntegratedSessionManager().getSessionManager(iid);

		// if(ism == null) throw new ContextHandlerApplicationException(2003,
		// CommonException.TYPE_INFO, "세션이 존재하지 않습니다.");
		if (ism == null)
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":005", new String[]{iid});

		List<ISession> sessionList = ism.getSessionList();

		// if(sessionList == null || sessionList.size() == 0) throw new
		// ContextHandlerApplicationException(2003, CommonException.TYPE_INFO,
		// "세션이 존재하지 않습니다.");
		if (sessionList == null || sessionList.size() == 0)
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":010", new String[]{iid});
		
		// 데이터 생성
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObj = null;
		

		for (ISession iSession : sessionList) {
			// devProfile = pm.getDeviceObj(iSession.getDeviceId());
			jsonObj = new JSONObject();

			
			jsonObj.put(WebSocketAdapterConst.DID, iSession.getDeviceId());
			jsonObj.put(WebSocketAdapterConst.DPID, iSession.getDeviceProfile().getDevicePoolId());
			jsonObj.put(WebSocketAdapterConst.DEV_NAME, iSession.getDeviceProfile().getDevicePoolNm());
			jsonObj.put(WebSocketAdapterConst.IS_USE, iSession.getDeviceProfile().getIsUse());
			jsonObj.put(WebSocketAdapterConst.IP, iSession.getDeviceProfile().getIp());
			jsonObj.put(WebSocketAdapterConst.PORT, iSession.getDeviceProfile().getPort());
			jsonObj.put(WebSocketAdapterConst.LAT, iSession.getDeviceProfile().getLatitude());
			jsonObj.put(WebSocketAdapterConst.LON, iSession.getDeviceProfile().getLongitude());
			jsonObj.put(WebSocketAdapterConst.REMARK, iSession.getDeviceProfile().getRemark());
			jsonObj.put(WebSocketAdapterConst.ALTER_DATE, TimeUtil.changeFormat(formatter.format(new Date(iSession.getLastAccessedTime()))));
			jsonObj.put(WebSocketAdapterConst.REG_DATE, TimeUtil.changeFormat(formatter.format(new Date(iSession.getCreatedTime()))));

			jsonObj.put(WebSocketAdapterConst.UID, iSession.getUserId());
			jsonObj.put(WebSocketAdapterConst.SESSION_STATUS, String.valueOf(iSession.getState()));
			jsonObj.put(WebSocketAdapterConst.SID, iSession.getSessionKey());
			jsonObj.put(WebSocketAdapterConst.SESSION_TIMEOUT, iSession.sessionTimeout());
			
			jsonArray.add(jsonObj);
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
