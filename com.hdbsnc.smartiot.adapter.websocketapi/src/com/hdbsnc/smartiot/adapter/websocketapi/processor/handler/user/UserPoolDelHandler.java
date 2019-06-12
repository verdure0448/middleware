package com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.user;

import java.util.List;

import com.hdbsnc.smartiot.adapter.websocketapi.constant.WebSocketAdapterConst;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractFunctionHandler;
import com.hdbsnc.smartiot.common.pm.IProfileManager;
import com.hdbsnc.smartiot.common.pm.vo.IModifyDomainIdMastObj;
import com.hdbsnc.smartiot.common.pm.vo.IModifyUserPoolObj;
import com.hdbsnc.smartiot.common.pm.vo.IUserObj;

/**
 * userpool/del
 * 
 * @author KANG
 *
 */
public class UserPoolDelHandler extends AbstractFunctionHandler {

	private IProfileManager pm;

	public UserPoolDelHandler(IProfileManager pm) {
		super("del");
		this.pm = pm;
	}

	@Override
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		String upid = inboundCtx.getParams().get(WebSocketAdapterConst.UPID);

		//////////////////////////////////////////////////////////////////////
		// 유저에서 사용중인가를 체크
		//////////////////////////////////////////////////////////////////////
		List<IUserObj> userObjList = pm.searchUserByUserPoolId(upid);
		// if (userObjList != null && userObjList.size() > 0) throw new
		// ContextHandlerApplicationException(1027, CommonException.TYPE_INFO,
		// "유저 정보에서 사용중입니다.");
		if (userObjList != null && userObjList.size() > 0)
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":005", new String[]{upid, userObjList.get(0).getUserId()});

		//////////////////////////////////////////////////////////////////////
		// 도메인 삭제
		//////////////////////////////////////////////////////////////////////
		IModifyDomainIdMastObj mDomainObj = pm.getModifyDomainIdMastObj();

		try {
			mDomainObj.domainId(upid).delete();
		} catch (Exception e) {
			// throw new ContextHandlerApplicationException(307,
			// CommonException.TYPE_ERROR, "도메인 삭제에 실패 했습니다.", e);
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":010", new String[]{upid},
					e);
		}

		//////////////////////////////////////////////////////////////////////
		// 유저풀 삭제
		//////////////////////////////////////////////////////////////////////
		IModifyUserPoolObj mUserPoolObj = pm.getModifyUserPoolObj();

		try {
			mUserPoolObj.userPoolId(upid).delete();
		} catch (Exception e) {
			// 도메인 롤백
			IModifyDomainIdMastObj orgMDomainObj = pm.getModifyDomainIdMastObj(mDomainObj.getResultVo());
			orgMDomainObj.insert();

			// throw new ContextHandlerApplicationException(328,
			// CommonException.TYPE_ERROR, "유저풀 삭제에 실패 했습니다.", e);
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":015", new String[]{upid},
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
