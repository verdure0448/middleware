package com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.instance;

import java.nio.ByteBuffer;

import org.json.simple.JSONObject;

import com.hdbsnc.smartiot.adapter.websocketapi.constant.WebSocketAdapterConst;
import com.hdbsnc.smartiot.common.aim.IAdapterInstanceContainer;
import com.hdbsnc.smartiot.common.aim.IAdapterInstanceManager;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractFunctionHandler;
import com.hdbsnc.smartiot.common.pm.IProfileManager;
import com.hdbsnc.smartiot.common.pm.vo.IInstanceObj;

/**
 * ins/get 인스턴스 정보를 가져오는 function
 * 
 * @author hjs0317
 *
 */
public class InstanceGetHandler extends AbstractFunctionHandler {

	private IProfileManager pm;
	private IAdapterInstanceManager aim;

	public InstanceGetHandler(IProfileManager pm, IAdapterInstanceManager aim) {
		super("get");
		this.pm = pm;
		this.aim = aim;
	}

	@Override
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		String iid = inboundCtx.getParams().get(WebSocketAdapterConst.IID);

		IInstanceObj insObj = pm.getInstanceObj(iid);

		// if (insObj == null) throw new
		// ContextHandlerApplicationException(2001, CommonException.TYPE_INFO,
		// "데이터가 존재하지 않습니다.");
		if (insObj == null)
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":005", new String[]{iid});

		JSONObject json = new JSONObject();

		json.put(WebSocketAdapterConst.IID, insObj.getInsId());
		json.put(WebSocketAdapterConst.INS_NAME, insObj.getInsNm());
		json.put(WebSocketAdapterConst.INS_KIND, insObj.getInsKind());
		json.put(WebSocketAdapterConst.DEFAULT_DEV_ID, insObj.getDefaultDevId());
		json.put(WebSocketAdapterConst.INS_TYPE, insObj.getInsType());
		json.put(WebSocketAdapterConst.AID, insObj.getAdtId());
		json.put(WebSocketAdapterConst.DPID, insObj.getDevPoolId());
		json.put(WebSocketAdapterConst.SELF_ID, insObj.getSelfId());
		json.put(WebSocketAdapterConst.SELF_PW, insObj.getSelfPw());
		json.put(WebSocketAdapterConst.INIT_DEV_STATUS, insObj.getInitDevStatus());
		json.put(WebSocketAdapterConst.IP, insObj.getIp());
		json.put(WebSocketAdapterConst.PORT, insObj.getPort());
		json.put(WebSocketAdapterConst.URL, insObj.getUrl());
		json.put(WebSocketAdapterConst.SESSION_TIMEOUT, insObj.getSessionTimeout());
		json.put(WebSocketAdapterConst.REMARK, insObj.getRemark());
		json.put(WebSocketAdapterConst.LAT, insObj.getLat());
		json.put(WebSocketAdapterConst.LON, insObj.getLon());
		json.put(WebSocketAdapterConst.REG_DATE, insObj.getRegDate());
		json.put(WebSocketAdapterConst.ALTER_DATE, insObj.getAlterDate());

		// 인스턴스 상태 조회
		IAdapterInstanceContainer iAic = aim.getAdapterInstance(iid);
		if (iAic != null) {
			json.put(WebSocketAdapterConst.INS_EVENT, String.valueOf(iAic.getLastEvent().getEventType()));
			json.put(WebSocketAdapterConst.INS_STATUS, String.valueOf(iAic.getLastEvent().getStateType()));
		} else {
			json.put(WebSocketAdapterConst.INS_EVENT, "0"); // defined 정의만 된 상태
			json.put(WebSocketAdapterConst.INS_STATUS, "0"); // defined 정의만 된 상태
		}

		outboundCtx.getPaths().add("ack");
		outboundCtx.setSID(inboundCtx.getSID());
		outboundCtx.setSPort(inboundCtx.getSPort());
		outboundCtx.setTID("this");
		outboundCtx.setTPort(inboundCtx.getTPort());
		outboundCtx.setTransmission("res");
		outboundCtx.setContenttype("json");
		outboundCtx.setContent(ByteBuffer.wrap(json.toJSONString().getBytes()));

	}
}
