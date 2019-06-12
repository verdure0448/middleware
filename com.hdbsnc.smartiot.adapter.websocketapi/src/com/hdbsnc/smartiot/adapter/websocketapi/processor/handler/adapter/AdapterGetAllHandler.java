package com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.adapter;

import java.nio.ByteBuffer;
import java.util.List;

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
 * fullPath: adt/get/all
 * 아답터 리스트를 json으로 전달한다. 
 * 
 * @author hjs0317
 *
 */
public class AdapterGetAllHandler extends AbstractFunctionHandler{
	
	private IAdapterManager am;
	
	public AdapterGetAllHandler(IAdapterManager am){
		super("all");
		this.am = am;
	}
	
	@Override
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		List<IAdapter> adtList = am.getAdapterList();
		IAdapterManifest mani;
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonAdapter;

		for(IAdapter adapter: adtList){
			mani = adapter.getManifest();
			jsonAdapter = new JSONObject();
			
			jsonAdapter.put(WebSocketAdapterConst.AID, mani.getAdapterId());
			jsonAdapter.put(WebSocketAdapterConst.ADT_NAME, mani.getAdapterName());
			jsonAdapter.put(WebSocketAdapterConst.ADT_KIND, mani.getKind());
			jsonAdapter.put(WebSocketAdapterConst.ADT_TYPE, mani.getType());
			jsonAdapter.put(WebSocketAdapterConst.DEFAULT_DEV_ID, mani.getDefaultDevId());
			jsonAdapter.put(WebSocketAdapterConst.SESSION_TIMEOUT, mani.getSessionTimeout());
			jsonAdapter.put(WebSocketAdapterConst.INIT_DEV_STATUS, mani.getInitDevStatus());
			jsonAdapter.put(WebSocketAdapterConst.IP, mani.getIp());
			jsonAdapter.put(WebSocketAdapterConst.PORT, mani.getPort());
			jsonAdapter.put(WebSocketAdapterConst.LAT, mani.getLatitude());
			jsonAdapter.put(WebSocketAdapterConst.LON, mani.getLongitude());
			jsonAdapter.put(WebSocketAdapterConst.SELF_ID, mani.getSelfId());
			jsonAdapter.put(WebSocketAdapterConst.SELF_PW, mani.getSelfPw());
			jsonAdapter.put(WebSocketAdapterConst.REMARK, mani.getRemark());
			
			jsonAdapter.put(WebSocketAdapterConst.ADT_DESCRIPTION, mani.getDescription());
			jsonAdapter.put(WebSocketAdapterConst.ADT_HYPERLINK, mani.getHyperLink());
			
//			if(mani.getAttributeMap()!=null) jsonAttributes.putAll(mani.getAttributeMap());
//			if(mani.getFunctionList()!=null) jsonFunctions.addAll(mani.getFunctionList());
//			jsonAdapter.put("attribute.map", jsonAttributes);
//			jsonAdapter.put("function.list", jsonFunctions);
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
