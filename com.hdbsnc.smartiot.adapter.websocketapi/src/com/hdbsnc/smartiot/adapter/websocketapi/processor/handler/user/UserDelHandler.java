package com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.user;

import java.util.List;

import com.hdbsnc.smartiot.adapter.websocketapi.constant.WebSocketAdapterConst;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractFunctionHandler;
import com.hdbsnc.smartiot.common.pm.IProfileManager;
import com.hdbsnc.smartiot.common.pm.vo.IModifyDomainIdMastObj;
import com.hdbsnc.smartiot.common.pm.vo.IModifyUserObj;
import com.hdbsnc.smartiot.common.pm.vo.IUserFilterObj;
import com.hdbsnc.smartiot.common.pm.vo.IUserObj;

/**
 * user/del
 * 
 * @author KANG
 *
 */
public class UserDelHandler extends AbstractFunctionHandler {

	private IProfileManager pm;

	public UserDelHandler(IProfileManager pm) {
		super("del");
		this.pm = pm;
	}

	@Override
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		String uid = inboundCtx.getParams().get(WebSocketAdapterConst.UID);

		//////////////////////////////////////////////////////////////////////
		// 유저 필터가 존재할 경우 에러 응답
		//////////////////////////////////////////////////////////////////////
		List<IUserFilterObj> userFilterList = pm.searchUserFilterByUserId(uid);
		if(userFilterList != null && userFilterList.size() > 0){
			throw getCommonService().getExceptionfactory().createAppException(
					this.getClass().getName() + ":015", new String[]{uid, userFilterList.get(0).getAuthFilter()});
		}
		
		
		IUserObj iUserObj = pm.getUserObj(uid);
		if(iUserObj != null && WebSocketAdapterConst.DEFAULT_ADMIN.equals(iUserObj.getUserType())){
			throw getCommonService().getExceptionfactory().createAppException(
					this.getClass().getName() + ":020");
		}
		
		//////////////////////////////////////////////////////////////////////
		// 도메인 삭제
		//////////////////////////////////////////////////////////////////////
		IModifyDomainIdMastObj mDomainObj = pm.getModifyDomainIdMastObj();

		try {
			mDomainObj.domainId(uid).delete();
		} catch (Exception e) {
			// throw new ContextHandlerApplicationException(307,
			// CommonException.TYPE_ERROR, "도메인 삭제에 실패 했습니다.", e);
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":005", new String[]{uid},
					e);
		}
		//////////////////////////////////////////////////////////////////////
		// 유저 삭제
		//////////////////////////////////////////////////////////////////////
		
		IModifyUserObj mUserObj = pm.getModifyUserObj();

		try {
			mUserObj.userId(uid).delete();
		} catch (Exception e) {
			// 삭제한 도메인 정보를 롤백
			IModifyDomainIdMastObj orgMDomainObj = pm.getModifyDomainIdMastObj(mDomainObj.getResultVo());
			orgMDomainObj.insert();

			// throw new ContextHandlerApplicationException(324,
			// CommonException.TYPE_ERROR, "유저 삭제에 실패 했습니다.", e);
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":010", new String[]{uid},
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
