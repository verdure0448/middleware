package com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.adapter;

import java.nio.ByteBuffer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.hdbsnc.smartiot.adapter.websocketapi.constant.WebSocketAdapterConst;
import com.hdbsnc.smartiot.common.am.IAdapter;
import com.hdbsnc.smartiot.common.am.IAdapterManager;
import com.hdbsnc.smartiot.common.am.IAdapterManifest;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractFunctionHandler;

/**
 * adt/func/get 아답터 기능 정보를 조회한다.
 * 
 * @author KANG
 *
 */
public class AdapterFuncGetHandler extends AbstractFunctionHandler {

	private IAdapterManager am;
	
	public AdapterFuncGetHandler(IAdapterManager am) {
		super("get");
		this.am = am;
	}

	@Override
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		String aid = inboundCtx.getParams().get(WebSocketAdapterConst.AID);
		IAdapter iAdt = am.getAdapter(aid);

		//if (iAdt == null) throw new ContextHandlerApplicationException(2001, CommonException.TYPE_INFO, "데이터가 존재하지 않습니다.");
		if (iAdt == null) throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":005", new String[]{aid});

		IAdapterManifest manifest = iAdt.getManifest();
		//if (iAdt == manifest) throw new ContextHandlerApplicationException(2001, CommonException.TYPE_INFO, "아답터 기능정보가 존재하지 않습니다.");
		if (manifest == null) throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":010", new String[]{aid});	
		
		if (manifest.getFunctions().size() == 0) throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":015", new String[]{aid});
		
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonAdapter = null;

		for (String func : manifest.getFunctions()) {
			jsonAdapter = new JSONObject();
			jsonAdapter.put(WebSocketAdapterConst.AID, aid);
			jsonAdapter.put(WebSocketAdapterConst.ADT_FUNCTION, func);
			jsonArray.add(jsonAdapter);
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
