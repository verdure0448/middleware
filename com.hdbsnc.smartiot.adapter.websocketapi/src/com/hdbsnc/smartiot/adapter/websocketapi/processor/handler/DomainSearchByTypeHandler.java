package com.hdbsnc.smartiot.adapter.websocketapi.processor.handler;

import java.nio.ByteBuffer;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.hdbsnc.smartiot.adapter.websocketapi.constant.WebSocketAdapterConst;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractFunctionHandler;
import com.hdbsnc.smartiot.common.pm.IProfileManager;
import com.hdbsnc.smartiot.common.pm.vo.IDomainIdMastObj;

/**
 * domain/search/by-type
 * 
 * @author KANG
 *
 */
public class DomainSearchByTypeHandler extends AbstractFunctionHandler {

	private IProfileManager pm;

	public DomainSearchByTypeHandler(IProfileManager pm) {
		super("by-type");
		this.pm = pm;
	}


	@Override
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		String domainType = inboundCtx.getParams().get(WebSocketAdapterConst.DOMAIN_TYPE);

		List<IDomainIdMastObj> iDomainObjList = pm.searchDomainByDomainType(domainType);

		//if (iDomainObjList == null || iDomainObjList.size() == 0) throw new ContextHandlerApplicationException(2001, CommonException.TYPE_INFO, "데이터가 존재하지 않습니다.");
		if (iDomainObjList == null || iDomainObjList.size() == 0) throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":005");

		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObj = null;

		for (IDomainIdMastObj iDomainObj : iDomainObjList) {
			jsonObj = new JSONObject();
			// 도메인ID
			jsonObj.put(WebSocketAdapterConst.DOMAIN_ID, iDomainObj.getDomainId());
			// 도메인명
			jsonObj.put(WebSocketAdapterConst.DOMAIN_NAME, iDomainObj.getDomainNm());
			// 도메인구분
			jsonObj.put(WebSocketAdapterConst.DOMAIN_TYPE, iDomainObj.getDomainType());
			// 비고
			jsonObj.put(WebSocketAdapterConst.REMARK, iDomainObj.getRemark());
			// 변경일시
			jsonObj.put(WebSocketAdapterConst.ALTER_DATE, iDomainObj.getAlterDate());
			// 등록일시
			jsonObj.put(WebSocketAdapterConst.REG_DATE, iDomainObj.getRegDate());

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
