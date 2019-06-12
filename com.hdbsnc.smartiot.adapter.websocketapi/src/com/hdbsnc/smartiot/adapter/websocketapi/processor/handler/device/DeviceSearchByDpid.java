package com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.device;

import java.nio.ByteBuffer;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.hdbsnc.smartiot.adapter.websocketapi.constant.WebSocketAdapterConst;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractFunctionHandler;
import com.hdbsnc.smartiot.common.otp.url.parser.UrlParser;
import com.hdbsnc.smartiot.common.pm.IProfileManager;
import com.hdbsnc.smartiot.common.pm.vo.IDeviceObj;

/**
 * dev/search/by-dpid
 * 
 * 
 * @author KANG
 *
 */
public class DeviceSearchByDpid extends AbstractFunctionHandler {

	private IProfileManager pm;

	public DeviceSearchByDpid(IProfileManager pm) {
		super("by-dpid");
		this.pm = pm;
	}

	@Override
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		String dpid = inboundCtx.getParams().get(WebSocketAdapterConst.DPID);

		List<IDeviceObj> devList = pm.searchDeviceByDevPoolId(dpid);

		// if (devList == null || devList.size() == 0) throw new
		// ContextHandlerApplicationException(2001, CommonException.TYPE_INFO,
		// "데이터가 존재하지 않습니다.");
		if (devList == null || devList.size() == 0)
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":005");

		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObj = null;
		for (IDeviceObj iDeviceObj : devList) {
			jsonObj = new JSONObject();

			jsonObj.put(WebSocketAdapterConst.DID, iDeviceObj.getDevId());
			jsonObj.put(WebSocketAdapterConst.DPID, iDeviceObj.getDevPoolId());
			jsonObj.put(WebSocketAdapterConst.DEV_NAME, iDeviceObj.getDevNm());
			jsonObj.put(WebSocketAdapterConst.IS_USE, iDeviceObj.getIsUse());
			jsonObj.put(WebSocketAdapterConst.SESSION_TIMEOUT, iDeviceObj.getSessionTimeout());
			jsonObj.put(WebSocketAdapterConst.IP, iDeviceObj.getIp());
			jsonObj.put(WebSocketAdapterConst.PORT, iDeviceObj.getPort());
			jsonObj.put(WebSocketAdapterConst.LAT, iDeviceObj.getLat());
			jsonObj.put(WebSocketAdapterConst.LON, iDeviceObj.getLon());
			jsonObj.put(WebSocketAdapterConst.REMARK, iDeviceObj.getRemark());
			jsonObj.put(WebSocketAdapterConst.ALTER_DATE, iDeviceObj.getAlterDate());
			jsonObj.put(WebSocketAdapterConst.REG_DATE, iDeviceObj.getRegDate());

			jsonArray.add(jsonObj);
		}
		
//		System.out.println(jsonArray.toString());

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
