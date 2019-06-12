package com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.device;

import java.nio.ByteBuffer;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.hdbsnc.smartiot.adapter.websocketapi.constant.WebSocketAdapterConst;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractFunctionHandler;
import com.hdbsnc.smartiot.common.pm.IProfileManager;
import com.hdbsnc.smartiot.common.pm.vo.IDevicePoolObj;

/**
 * devpool/get/all
 * 
 * @author KANG
 *
 */
public class DevicePoolGetAllHandler extends AbstractFunctionHandler {

	private IProfileManager pm;

	public DevicePoolGetAllHandler(IProfileManager pm) {
		super("all");
		this.pm = pm;
	}

	@Override
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		List<IDevicePoolObj> devPoolObjList = pm.getAllDevicePoolObj();

		// if (devPoolObjList == null || devPoolObjList.size() == 0) throw new
		// ContextHandlerApplicationException(2001, CommonException.TYPE_INFO,
		// "데이터가 존재하지 않습니다.");
		if (devPoolObjList == null || devPoolObjList.size() == 0)
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":005");

		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObj = null;

		for (IDevicePoolObj devPoolObj : devPoolObjList) {
			jsonObj = new JSONObject();
			jsonObj.put(WebSocketAdapterConst.DPID, devPoolObj.getDevPoolId());
			// 장치풀명
			jsonObj.put(WebSocketAdapterConst.DEV_POOL_NAME, devPoolObj.getDevPoolNm());
			// 비고
			jsonObj.put(WebSocketAdapterConst.REMARK, devPoolObj.getRemark());
			// 변경일시
			jsonObj.put(WebSocketAdapterConst.ALTER_DATE, devPoolObj.getAlterDate());
			// 등록일시
			jsonObj.put(WebSocketAdapterConst.REG_DATE, devPoolObj.getRegDate());

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
