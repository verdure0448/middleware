package com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.device;

import com.hdbsnc.smartiot.adapter.websocketapi.constant.WebSocketAdapterConst;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractFunctionHandler;
import com.hdbsnc.smartiot.common.ism.sm.ISession;
import com.hdbsnc.smartiot.common.ism.sm.ISessionManager;
import com.hdbsnc.smartiot.common.ism.sm.ISessionState;
import com.hdbsnc.smartiot.common.pm.IProfileManager;
import com.hdbsnc.smartiot.common.pm.vo.IInstanceObj;
import com.hdbsnc.smartiot.common.pm.vo.IModifyDeviceObj;
import com.hdbsnc.smartiot.common.pm.vo.IModifyDomainIdMastObj;

/**
 * dev/del
 * 
 * @author KANG
 *
 */
public class DeviceDelHandler extends AbstractFunctionHandler {

	private IProfileManager pm;
	private ISessionManager sm;

	public DeviceDelHandler(IProfileManager pm, ISessionManager sm) {
		super("del");
		this.pm = pm;
		this.sm = sm;
	}

	@Override
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		String did = inboundCtx.getParams().get(WebSocketAdapterConst.DID);

		ISession iSession = sm.getIntegratedSessionManager().getSession(did);

		//////////////////////////////////////////////////////////////////////
		// 장치를 사용중인 경우(세션에서 사용중인 경우) 에러 응답
		//////////////////////////////////////////////////////////////////////
		// if (iSession != null && iSession.getState() < ISessionState.SESSION_STATE_DISPOSE)
		// throw new ContextHandlerApplicationException(1001, CommonException.TYPE_WARNNING, "장치가 사용중 입니다.");
		if (iSession != null && iSession.getState() < ISessionState.SESSION_STATE_DISPOSE)
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":005", new String[]{did});
		//////////////////////////////////////////////////////////////////////
		// 삭제 대상 장치가 인스턴스 디폴트장치로 사용중인 경우 에러 응답
		//////////////////////////////////////////////////////////////////////
		IInstanceObj insObj = pm.searchInstanceByDefaultDevId(did);
		// if (insObj != null) throw new
		// ContextHandlerApplicationException(1002,
		// CommonException.TYPE_WARNNING, "인스턴스 디폴트장치로 사용중 입니다.");
		if (insObj != null)
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":010", new String[]{insObj.getInsId(), did});
		//////////////////////////////////////////////////////////////////////
		// 도메인 삭제
		//////////////////////////////////////////////////////////////////////
		IModifyDomainIdMastObj mDomainObj = pm.getModifyDomainIdMastObj();
		try {
			mDomainObj.domainId(did).delete();
		} catch (Exception e) {
			// throw new ContextHandlerApplicationException(307,
			// CommonException.TYPE_ERROR, "도메인 삭제에 실패 했습니다.", e);
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":015", new String[]{did}, e);
		}

		//////////////////////////////////////////////////////////////////////
		// 장치 삭제
		//////////////////////////////////////////////////////////////////////
		IModifyDeviceObj mDevObj = pm.getModifyDeviceObj();
		try {
			mDevObj.devId(did).delete();
		} catch (Exception e) {
			// 삭제한 도메인 정보를 롤백
			IModifyDomainIdMastObj orgMDomainObj = pm.getModifyDomainIdMastObj(mDomainObj.getResultVo());
			orgMDomainObj.insert();
			// throw new ContextHandlerApplicationException(308,
			// CommonException.TYPE_ERROR, "장치 삭제에 실패 했습니다.",e);
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":020", new String[]{did}, e);
		}

		// 정상 응답
		outboundCtx.getPaths().add("ack");
		outboundCtx.setSID(inboundCtx.getSID());
		outboundCtx.setSPort(inboundCtx.getSPort());
		outboundCtx.setTID("this");
		outboundCtx.setTPort(inboundCtx.getTPort());
		outboundCtx.setTransmission("res");

	}
}
