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
import com.hdbsnc.smartiot.common.pm.vo.IModifyInstanceAttributeObj;

/**
 * ins/att/del
 * 
 * @author KANG
 *
 */
public class InsAttDelHandler extends AbstractFunctionHandler {

	private IProfileManager pm;
	private ISessionManager sm;

	public InsAttDelHandler(IProfileManager pm, ISessionManager sm) {
		super("del");
		this.pm = pm;
		this.sm = sm;
	}

	@Override
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		String iid = inboundCtx.getParams().get(WebSocketAdapterConst.IID);
		String attKey = inboundCtx.getParams().get(WebSocketAdapterConst.ATT_KEY);

		ISessionManager iSessionMgr = sm.getIntegratedSessionManager().getSessionManager(iid);

		//////////////////////////////////////////////////////////////////////
		// 삭제 대상 인스턴스 속성이 기동중인 경우 에러 응답
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
							.createAppException(this.getClass().getName() + ":005", new String[]{iid, attKey}, null);
			}
		}

		//////////////////////////////////////////////////////////////////////
		// 인스턴스 속성 삭제
		//////////////////////////////////////////////////////////////////////
		IModifyInstanceAttributeObj mInsAttObj = pm.getModifyInstanceAttributeObj();

		try {
			mInsAttObj.insId(iid).key(attKey).delete();
		} catch (Exception e) {
			// throw new ContextHandlerApplicationException(316,
			// CommonException.TYPE_ERROR, "인스턴스 속성 삭제에 실패 했습니다.", e);
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":010", new String[]{iid, attKey},
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
