package com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.adapter;

import java.nio.ByteBuffer;

import org.json.simple.JSONObject;

import com.hdbsnc.smartiot.adapter.websocketapi.constant.WebSocketAdapterConst;
import com.hdbsnc.smartiot.common.am.IAdapter;
import com.hdbsnc.smartiot.common.am.IAdapterManager;
import com.hdbsnc.smartiot.common.am.IAdapterManifest;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractFunctionHandler;

/**
 * fullPath: adt/get 아답터 정보를 json으로 전달한다.
 * 
 * @author dhkang
 *
 */
public class AdapterGetHandler extends AbstractFunctionHandler {

	private IAdapterManager am;

	public AdapterGetHandler(IAdapterManager am) {
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
		//if (iAdt == manifest) throw new ContextHandlerApplicationException(2001, CommonException.TYPE_INFO, "아답터 속성정보가 존재하지 않습니다.");
		if (manifest == null) throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":010", new String[]{aid});
		
		
		JSONObject jsonAdapter = new JSONObject();

		jsonAdapter.put(WebSocketAdapterConst.AID, manifest.getAdapterId());
		jsonAdapter.put(WebSocketAdapterConst.ADT_NAME, manifest.getAdapterName());
		jsonAdapter.put(WebSocketAdapterConst.ADT_KIND, manifest.getKind());
		jsonAdapter.put(WebSocketAdapterConst.ADT_TYPE, manifest.getType());
		jsonAdapter.put(WebSocketAdapterConst.DEFAULT_DEV_ID, manifest.getDefaultDevId());
		jsonAdapter.put(WebSocketAdapterConst.SESSION_TIMEOUT, manifest.getSessionTimeout());
		jsonAdapter.put(WebSocketAdapterConst.INIT_DEV_STATUS, manifest.getInitDevStatus());
		jsonAdapter.put(WebSocketAdapterConst.IP, manifest.getIp());
		jsonAdapter.put(WebSocketAdapterConst.PORT, manifest.getPort());
		jsonAdapter.put(WebSocketAdapterConst.LAT, manifest.getLatitude());
		jsonAdapter.put(WebSocketAdapterConst.LON, manifest.getLongitude());
		jsonAdapter.put(WebSocketAdapterConst.SELF_ID, manifest.getSelfId());
		jsonAdapter.put(WebSocketAdapterConst.SELF_PW, manifest.getSelfPw());
		jsonAdapter.put(WebSocketAdapterConst.REMARK, manifest.getRemark());
		
		jsonAdapter.put(WebSocketAdapterConst.ADT_DESCRIPTION, manifest.getDescription());
		jsonAdapter.put(WebSocketAdapterConst.ADT_HYPERLINK, manifest.getHyperLink());
		
		outboundCtx.getPaths().add("ack");
		outboundCtx.setSID(inboundCtx.getSID());
		outboundCtx.setSPort(inboundCtx.getSPort());
		outboundCtx.setTID("this");
		outboundCtx.setTPort(inboundCtx.getTPort());
		outboundCtx.setTransmission("res");
		outboundCtx.setContenttype("json");
		outboundCtx.setContent(ByteBuffer.wrap(jsonAdapter.toJSONString().getBytes()));
		
	}

}
