package com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.instance;

import java.nio.ByteBuffer;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.hdbsnc.smartiot.adapter.websocketapi.constant.WebSocketAdapterConst;
import com.hdbsnc.smartiot.common.aim.IAdapterInstanceContainer;
import com.hdbsnc.smartiot.common.aim.IAdapterInstanceManager;
import com.hdbsnc.smartiot.common.aim.IAdapterProcessorEvent;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractFunctionHandler;
import com.hdbsnc.smartiot.common.pm.IProfileManager;
import com.hdbsnc.smartiot.common.pm.vo.IInstanceObj;

/**
 * ins/search/by-aid 아답터ID로 인스턴스 정보를 가져오는 클래스
 * 
 * @author KANG
 *
 */
public class InstanceSearchByAidHandler extends AbstractFunctionHandler {

	private IProfileManager pm;
	private IAdapterInstanceManager aim;

	public InstanceSearchByAidHandler(IProfileManager pm, IAdapterInstanceManager aim) {
		super("by-aid");
		this.pm = pm;
		this.aim = aim;
	}

	@Override
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		String aid = inboundCtx.getParams().get(WebSocketAdapterConst.AID);

		// PM조회
		List<IInstanceObj> insObjs = pm.searchInstanceByAid(aid);

		// if(insObjs==null || insObjs.size() == 0) throw new
		// ContextHandlerApplicationException(2001, CommonException.TYPE_INFO,
		// "데이터가 존재하지 않습니다.");
		if (insObjs == null || insObjs.size() == 0)
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":005", new String[]{aid});

		// 데이터 생성
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObj = null;

		//현재 이벤트 진행도
		IAdapterProcessorEvent lastEvent=null;
		IAdapterInstanceContainer iAic = null;
		for (IInstanceObj iInstanceObj : insObjs) {
			jsonObj = new JSONObject();

			jsonObj.put(WebSocketAdapterConst.IID, iInstanceObj.getInsId());
			jsonObj.put(WebSocketAdapterConst.DPID, iInstanceObj.getDevPoolId());
			jsonObj.put(WebSocketAdapterConst.AID, iInstanceObj.getAdtId());
			jsonObj.put(WebSocketAdapterConst.INS_NAME, iInstanceObj.getInsNm());
			jsonObj.put(WebSocketAdapterConst.INS_KIND, iInstanceObj.getInsKind());
			jsonObj.put(WebSocketAdapterConst.DEFAULT_DEV_ID, iInstanceObj.getDefaultDevId());
			jsonObj.put(WebSocketAdapterConst.INS_TYPE, iInstanceObj.getInsType());
			jsonObj.put(WebSocketAdapterConst.IS_USE, iInstanceObj.getIsUse());
			jsonObj.put(WebSocketAdapterConst.SESSION_TIMEOUT, iInstanceObj.getSessionTimeout());
			jsonObj.put(WebSocketAdapterConst.INIT_DEV_STATUS, iInstanceObj.getInitDevStatus());
			jsonObj.put(WebSocketAdapterConst.IP, iInstanceObj.getIp());
			jsonObj.put(WebSocketAdapterConst.PORT, iInstanceObj.getPort());
			jsonObj.put(WebSocketAdapterConst.URL, iInstanceObj.getUrl());
			jsonObj.put(WebSocketAdapterConst.LAT, iInstanceObj.getLat());
			jsonObj.put(WebSocketAdapterConst.LON, iInstanceObj.getLon());
			jsonObj.put(WebSocketAdapterConst.SELF_ID, iInstanceObj.getSelfId());
			jsonObj.put(WebSocketAdapterConst.SELF_PW, iInstanceObj.getSelfPw());
			jsonObj.put(WebSocketAdapterConst.REMARK, iInstanceObj.getRemark());
			jsonObj.put(WebSocketAdapterConst.ALTER_DATE, iInstanceObj.getAlterDate());
			jsonObj.put(WebSocketAdapterConst.REG_DATE, iInstanceObj.getRegDate());

			// 인스턴스 상태 조회
			iAic = aim.getAdapterInstance(iInstanceObj.getInsId());
			if (iAic != null) {
				jsonObj.put(WebSocketAdapterConst.INS_EVENT, String.valueOf(iAic.getLastEvent().getEventType()));
				jsonObj.put(WebSocketAdapterConst.INS_STATUS, String.valueOf(iAic.getLastEvent().getStateType()));
				
				lastEvent = iAic.getProcessor().getLastEvent();
				if(lastEvent!=null){
					jsonObj.put(WebSocketAdapterConst.ADAPTER_PROCESS_EVNET, lastEvent.getAdapterProcessEventType());
					jsonObj.put(WebSocketAdapterConst.ADAPTER_PROCESS_STATE_EVENT, lastEvent.getAdapterProcessEventStateType());
				}else{
					jsonObj.put(WebSocketAdapterConst.ADAPTER_PROCESS_EVNET, IAdapterProcessorEvent.TYPE_INIT);
					jsonObj.put(WebSocketAdapterConst.ADAPTER_PROCESS_STATE_EVENT, IAdapterProcessorEvent.STATE_TYPE_INIT);
				}
			} else {
				jsonObj.put(WebSocketAdapterConst.INS_EVENT, "0"); // defined 정의만 된 상태
				jsonObj.put(WebSocketAdapterConst.INS_STATUS, "0"); // defined 정의만 된 상태

				jsonObj.put(WebSocketAdapterConst.ADAPTER_PROCESS_EVNET, IAdapterProcessorEvent.TYPE_NONE);
				jsonObj.put(WebSocketAdapterConst.ADAPTER_PROCESS_STATE_EVENT, IAdapterProcessorEvent.STATE_TYPE_NONE);
			}

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
