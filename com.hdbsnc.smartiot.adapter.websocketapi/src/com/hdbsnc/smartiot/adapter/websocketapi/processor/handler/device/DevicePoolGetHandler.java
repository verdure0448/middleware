package com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.device;

import java.nio.ByteBuffer;

import org.json.simple.JSONObject;

import com.hdbsnc.smartiot.adapter.websocketapi.constant.WebSocketAdapterConst;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractFunctionHandler;
import com.hdbsnc.smartiot.common.pm.IProfileManager;
import com.hdbsnc.smartiot.common.pm.vo.IDevicePoolObj;

/**
 * devpool/get
 * 
 * @author KANG
 *
 */
public class DevicePoolGetHandler extends AbstractFunctionHandler {

	private IProfileManager pm;

	public DevicePoolGetHandler(IProfileManager pm) {
		super("get");
		this.pm = pm;
	}

	@Override
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		String dpid = inboundCtx.getParams().get(WebSocketAdapterConst.DPID);

		IDevicePoolObj devPoolObj = pm.getDevicePoolObj(dpid);

		// if(devPoolObj == null) throw new
		// ContextHandlerApplicationException(2001, CommonException.TYPE_INFO,
		// "데이터가 존재하지 않습니다.");
		if (devPoolObj == null)
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":005", new String[]{dpid});

		JSONObject json = new JSONObject();

		// 장치풀ID
		json.put(WebSocketAdapterConst.DPID, devPoolObj.getDevPoolId());
		// 장치풀명
		json.put(WebSocketAdapterConst.DEV_POOL_NAME, devPoolObj.getDevPoolNm());
		// 비고
		json.put(WebSocketAdapterConst.REMARK, devPoolObj.getRemark());
		// 변경일시
		json.put(WebSocketAdapterConst.ALTER_DATE, devPoolObj.getAlterDate());
		// 등록일시
		json.put(WebSocketAdapterConst.REG_DATE, devPoolObj.getRegDate());

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
