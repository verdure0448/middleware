package com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.adapter;

import java.util.List;

import com.hdbsnc.smartiot.adapter.websocketapi.constant.WebSocketAdapterConst;
import com.hdbsnc.smartiot.common.am.IAdapterManager;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractFunctionHandler;
import com.hdbsnc.smartiot.common.ism.sm.ISessionManager;
import com.hdbsnc.smartiot.common.pm.IProfileManager;
import com.hdbsnc.smartiot.common.pm.vo.IInstanceObj;

/**
 * fullPath: adt/get 아답터 정보를 json으로 전달한다.
 * 
 * @author dhkang
 *
 */
public class AdapterUninstallHandler extends AbstractFunctionHandler {

	private IAdapterManager am;
	private IProfileManager pm;

	public AdapterUninstallHandler(IProfileManager pm, IAdapterManager am) {
		super("uninstall");
		this.pm = pm;
		this.am = am;
	}

	@Override
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		String aid = inboundCtx.getParams().get(WebSocketAdapterConst.AID);

		// PM조회
		List<IInstanceObj> insObjs = pm.searchInstanceByAid(aid);

		if (insObjs != null && insObjs.size() > 0) {
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":005",
					new String[] { insObjs.get(0).getInsId() });
		}

//		if (insObjs != null) {
//			for (IInstanceObj iInstanceObj : insObjs) {
//				if (WebSocketAdapterConst.DEFAULT_ADMIN.equals(iInstanceObj.getInsType())) {
//					throw getCommonService().getExceptionfactory()
//							.createAppException(this.getClass().getName() + ":010", new String[] { aid });
//				}
//			}
//		}

		// //////////////////////////////////////////////////////////////////////
		// // 삭제 대상 인스턴스가 기동중인 경우 에러 응답
		// //////////////////////////////////////////////////////////////////////
		// if(insObjs != null){
		// for (IInstanceObj insObj : insObjs){
		// ISessionManager iSessionMgr =
		// sm.getIntegratedSessionManager().getSessionManager(insObj.getInsId());
		// if (iSessionMgr != null) {
		// List<ISession> sessionList = iSessionMgr.getSessionList();
		// for (ISession iSession : sessionList) {
		// if (ISessionState.SESSION_STATE_DISPOSE > iSession.getState())
		// throw getCommonService().getExceptionfactory().createAppException(
		// this.getClass().getName() + ":005", new String[]{insObj.getInsId()});
		// }
		// }
		// }
		// }
		//
		// if(insObjs != null){
		// for (IInstanceObj insObj : insObjs){
		// //////////////////////////////////////////////////////////////////////
		// // 도메인 삭제
		// //////////////////////////////////////////////////////////////////////
		// IModifyDomainIdMastObj mDomainObj = pm.getModifyDomainIdMastObj();
		//
		// try {
		// mDomainObj.domainId(insObj.getInsId()).delete();
		// } catch (Exception e) {
		// throw getCommonService().getExceptionfactory().createAppException(
		// this.getClass().getName() + ":010", new String[]{insObj.getInsId()},
		// e);
		// }
		//
		// //////////////////////////////////////////////////////////////////////
		// // 인스턴스, 속성, 기능 삭제
		// //////////////////////////////////////////////////////////////////////
		// IModifyInstanceObj mInsObj = pm.getModifyInstanceObj();
		// try {
		// mInsObj.insId(insObj.getInsId()).delete();
		// } catch (Exception e) {
		// // 삭제한 도메인 정보를 롤백
		// IModifyDomainIdMastObj orgMDomainObj =
		// pm.getModifyDomainIdMastObj(mDomainObj.getResultVo());
		// orgMDomainObj.insert();
		// throw
		// getCommonService().getExceptionfactory().createAppException(this.getClass().getName()
		// + ":015", null, e);
		// }
		// }
		// }

		try {
			am.uninstallAdapter(aid);
		} catch (Exception e) {
			throw getCommonService().getExceptionfactory().createSysException("208", new String[] { aid }, e);
		}

		outboundCtx.getPaths().add("ack");
		outboundCtx.setSID(inboundCtx.getSID());
		outboundCtx.setSPort(inboundCtx.getSPort());
		outboundCtx.setTID("this");
		outboundCtx.setTPort(inboundCtx.getTPort());
		outboundCtx.setTransmission("res");
	}

}
