package com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.instance;

import java.util.List;

import com.hdbsnc.smartiot.adapter.websocketapi.constant.WebSocketAdapterConst;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractFunctionHandler;
import com.hdbsnc.smartiot.common.ism.sm.ISession;
import com.hdbsnc.smartiot.common.ism.sm.ISessionManager;
import com.hdbsnc.smartiot.common.ism.sm.ISessionState;
import com.hdbsnc.smartiot.common.pm.IProfileManager;
import com.hdbsnc.smartiot.common.pm.vo.IInstanceAttributeObj;
import com.hdbsnc.smartiot.common.pm.vo.IInstanceFunctionObj;
import com.hdbsnc.smartiot.common.pm.vo.IModifyDomainIdMastObj;
import com.hdbsnc.smartiot.common.pm.vo.IModifyInstanceObj;

/**
 * ins/del 인스턴스 삭제
 * 
 * @author KANG
 *
 */
public class InstanceDelHandler extends AbstractFunctionHandler {

	private IProfileManager pm;
	private ISessionManager sm;

	public InstanceDelHandler(IProfileManager pm, ISessionManager sm) {
		super("del");
		this.pm = pm;
		this.sm = sm;
	}

	@Override
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		String iid = inboundCtx.getParams().get(WebSocketAdapterConst.IID);

		IModifyInstanceObj mInsObj = pm.getModifyInstanceObj();

		ISessionManager iSessionMgr = sm.getIntegratedSessionManager().getSessionManager(iid);

		//////////////////////////////////////////////////////////////////////
		// 삭제 대상 인스턴스가 기동중인 경우 에러 응답
		//////////////////////////////////////////////////////////////////////
		if (iSessionMgr != null) {
			List<ISession> sessionList = iSessionMgr.getSessionList();
			for (ISession iSession : sessionList) {
				// if (ISessionState.SESSION_STATE_DISPOSE >
				// iSession.getState()) throw new
				// ContextHandlerApplicationException(1013,
				// CommonException.TYPE_WARNNING, "인스턴스가 사용중입니다.");
				if (ISessionState.SESSION_STATE_DISPOSE > iSession.getState())
					throw getCommonService().getExceptionfactory()
							.createAppException(this.getClass().getName() + ":005", new String[]{iid});
			}
		}

		//////////////////////////////////////////////////////////////////////
		// 인스턴스 속성 또는 기능이 존재할 경우 에러 응답
		//////////////////////////////////////////////////////////////////////
		List<IInstanceAttributeObj> insAttList = pm.getInstanceAttributeList(iid);
		if(insAttList != null && insAttList.size() > 0){
			throw getCommonService().getExceptionfactory().createAppException(
					this.getClass().getName() + ":020", new String[]{iid, insAttList.get(0).getKey()});
		}
		
		List<IInstanceFunctionObj> insFuncList = pm.getInstanceFunctionList(iid);
		if(insFuncList != null && insFuncList.size() > 0){
			throw getCommonService().getExceptionfactory().createAppException(
					this.getClass().getName() + ":025", new String[]{iid, insFuncList.get(0).getKey()});
		}
		
		//////////////////////////////////////////////////////////////////////
		// 도메인 삭제
		//////////////////////////////////////////////////////////////////////
		IModifyDomainIdMastObj mDomainObj = pm.getModifyDomainIdMastObj();

		try {
			mDomainObj.domainId(iid).delete();
		} catch (Exception e) {
			// throw new ContextHandlerApplicationException(307,
			// CommonException.TYPE_ERROR, "도메인 삭제에 실패 했습니다.", e);
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":010", new String[]{iid},
					e);
		}
		//////////////////////////////////////////////////////////////////////
		// 인스턴스삭제
		//////////////////////////////////////////////////////////////////////
		try {
			mInsObj.insId(iid).delete();
		} catch (Exception e) {
			// 삭제한 도메인 정보를 롤백
			IModifyDomainIdMastObj orgMDomainObj = pm.getModifyDomainIdMastObj(mDomainObj.getResultVo());
			orgMDomainObj.insert();

			// throw new ContextHandlerApplicationException(322,
			// CommonException.TYPE_ERROR, "인스턴스 삭제에 실패 했습니다.", e);
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":015", null,
					e);
		}

		outboundCtx.getPaths().add("ack");
		outboundCtx.setSID(inboundCtx.getSID());
		outboundCtx.setSPort(inboundCtx.getSPort());
		outboundCtx.setTID("this");
		outboundCtx.setTPort(inboundCtx.getTPort());
		outboundCtx.setTransmission("res");

	}
}
