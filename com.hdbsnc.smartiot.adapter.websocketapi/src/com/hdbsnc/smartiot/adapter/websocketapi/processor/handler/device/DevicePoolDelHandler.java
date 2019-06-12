package com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.device;

import java.util.List;

import com.hdbsnc.smartiot.adapter.websocketapi.constant.WebSocketAdapterConst;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractFunctionHandler;
import com.hdbsnc.smartiot.common.pm.IProfileManager;
import com.hdbsnc.smartiot.common.pm.vo.IDeviceObj;
import com.hdbsnc.smartiot.common.pm.vo.IDomainIdMastObj;
import com.hdbsnc.smartiot.common.pm.vo.IInstanceObj;
import com.hdbsnc.smartiot.common.pm.vo.IModifyDevicePoolObj;
import com.hdbsnc.smartiot.common.pm.vo.IModifyDomainIdMastObj;

/**
 * devpool.del
 * 
 * @author KANG
 *
 */
public class DevicePoolDelHandler extends AbstractFunctionHandler {

	private IProfileManager pm;

	public DevicePoolDelHandler(IProfileManager pm) {
		super("del");
		this.pm = pm;
	}
	
	@Override
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		String dpid = inboundCtx.getParams().get(WebSocketAdapterConst.DPID);

		//////////////////////////////////////////////////////////////////////
		// 삭제 대상 장치풀이 인스턴스에서 사용중인 경우 에러 응답
		//////////////////////////////////////////////////////////////////////
		IInstanceObj insObj = pm.searchInstanceByDevPoolId(dpid);
		// if (insObj != null) throw new
		// ContextHandlerApplicationException(1003,
		// CommonException.TYPE_WARNNING, "장치풀이 인스턴스에서 사용중입니다.");
		if (insObj != null)
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":005", new String[]{insObj.getInsId(), dpid});
		//////////////////////////////////////////////////////////////////////
		// 삭제 대상 장치풀이 장치에서 사용중인 경우 에러 응답
		//////////////////////////////////////////////////////////////////////
		List<IDeviceObj> devObjList = pm.searchDeviceByDevPoolId(dpid);
		// if (devObjList != null && devObjList.size() > 0) throw new
		// ContextHandlerApplicationException(1004,
		// CommonException.TYPE_WARNNING, "장치풀이 장치에서 사용중입니다.");
		if (devObjList != null && devObjList.size() > 0)
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":010", new String[]{devObjList.get(0).getDevId(), dpid});
		//////////////////////////////////////////////////////////////////////
		// 도메인 삭제
		//////////////////////////////////////////////////////////////////////
		IModifyDomainIdMastObj mDomainObj = pm.getModifyDomainIdMastObj();
		IDomainIdMastObj orgDomainObj = null;
		try {
			mDomainObj.domainId(dpid).delete();
			orgDomainObj = mDomainObj.getResultVo();
		} catch (Exception e) {// 도메인 삭제 실패
			// throw new ContextHandlerApplicationException(307,
			// CommonException.TYPE_ERROR, "도메인 삭제에 실패 했습니다.", e);
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":015", new String[]{dpid}, e);
		}
		//////////////////////////////////////////////////////////////////////
		// 장치풀 삭제
		//////////////////////////////////////////////////////////////////////
		IModifyDevicePoolObj mDevPoolObj = pm.getModifyDevicePoolObj();
		try {
			mDevPoolObj.devPoolId(dpid).delete();
		} catch (Exception e) {// 장치풀 삭제 실패
			// 도메인 롤백
			mDomainObj = pm.getModifyDomainIdMastObj(orgDomainObj);
			mDomainObj.insert();
			//throw new ContextHandlerApplicationException(309, CommonException.TYPE_ERROR, "장치풀 삭제에 실패 했습니다.", e);
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":020", new String[]{dpid}, e);
		}

		// 정상응답
		outboundCtx.getPaths().add("ack");
		outboundCtx.setSID(inboundCtx.getSID());
		outboundCtx.setSPort(inboundCtx.getSPort());
		outboundCtx.setTID("this");
		outboundCtx.setTPort(inboundCtx.getTPort());
		outboundCtx.setTransmission("res");

	}
}
