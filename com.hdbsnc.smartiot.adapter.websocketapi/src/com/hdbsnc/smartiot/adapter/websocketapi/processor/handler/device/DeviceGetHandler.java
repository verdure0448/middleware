package com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.device;

import java.nio.ByteBuffer;

import org.json.simple.JSONObject;

import com.hdbsnc.smartiot.adapter.websocketapi.constant.WebSocketAdapterConst;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractFunctionHandler;
import com.hdbsnc.smartiot.common.pm.IProfileManager;
import com.hdbsnc.smartiot.common.pm.vo.IDeviceObj;

/**
 * dev/get 장치 정보를 가져오는 function
 * 
 * @author KANG
 *
 */
public class DeviceGetHandler extends AbstractFunctionHandler {

	private IProfileManager pm;

	public DeviceGetHandler(IProfileManager pm) {
		super("get");
		this.pm = pm;
	}
	
	@Override
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		String did = inboundCtx.getParams().get(WebSocketAdapterConst.DID);

		IDeviceObj devObj = pm.getDeviceObj(did);

		// if (devObj == null) throw new
		// ContextHandlerApplicationException(2001, CommonException.TYPE_INFO,
		// "데이터가 존재하지 않습니다.");
		if (devObj == null)
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":005", new String[]{did});
		JSONObject json = new JSONObject();
		json.put(WebSocketAdapterConst.DID, devObj.getDevId());
		json.put(WebSocketAdapterConst.DPID, devObj.getDevPoolId());
		json.put(WebSocketAdapterConst.DEV_NAME, devObj.getDevNm());
		json.put(WebSocketAdapterConst.DEV_TYPE, devObj.getDevType());
		json.put(WebSocketAdapterConst.IS_USE, devObj.getIsUse());
		json.put(WebSocketAdapterConst.SESSION_TIMEOUT, devObj.getSessionTimeout());
		json.put(WebSocketAdapterConst.IP, devObj.getIp());
		json.put(WebSocketAdapterConst.PORT, devObj.getPort());
		json.put(WebSocketAdapterConst.LAT, devObj.getLat());
		json.put(WebSocketAdapterConst.LON, devObj.getLon());
		json.put(WebSocketAdapterConst.REMARK, devObj.getRemark());
		json.put(WebSocketAdapterConst.ALTER_DATE, devObj.getAlterDate());
		json.put(WebSocketAdapterConst.REG_DATE, devObj.getRegDate());

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
