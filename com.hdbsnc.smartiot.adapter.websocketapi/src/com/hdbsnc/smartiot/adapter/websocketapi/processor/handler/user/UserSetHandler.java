package com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.user;

import java.nio.charset.StandardCharsets;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.hdbsnc.smartiot.adapter.websocketapi.constant.WebSocketAdapterConst;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractFunctionHandler;
import com.hdbsnc.smartiot.common.pm.IProfileManager;
import com.hdbsnc.smartiot.common.pm.vo.IDomainIdMastObj;
import com.hdbsnc.smartiot.common.pm.vo.IModifyDomainIdMastObj;
import com.hdbsnc.smartiot.common.pm.vo.IModifyUserObj;
import com.hdbsnc.smartiot.common.pm.vo.IUserObj;
import com.hdbsnc.smartiot.common.pm.vo.IUserPoolObj;

/**
 * user/set
 * 
 * @author KANG
 *
 */
public class UserSetHandler extends AbstractFunctionHandler {

	private IProfileManager pm;

	public UserSetHandler(IProfileManager pm) {
		super("set");
		this.pm = pm;
	}

	@Override
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		String uid = inboundCtx.getParams().get(WebSocketAdapterConst.UID);

		JSONObject inputJson = null;

		try {
			inputJson = (JSONObject) (new JSONParser())
					.parse(new String(inboundCtx.getContent().array(), StandardCharsets.UTF_8));
		} catch (Exception e) {
			// throw new ContextHandlerApplicationException(1005,
			// CommonException.TYPE_WARNNING, "컨텐츠 데이터 형식이 부정합이 있습니다.", e);
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":005", null,
					e);
		}

		IUserObj iUserObj = pm.getUserObj(uid);
		IModifyUserObj mUser = pm.getModifyUserObj();

		// 유저ID
		mUser.userId(uid);
		// 유저풀ID
		mUser.userPoolId((String) inputJson.get(WebSocketAdapterConst.UPID));
		// 유저암호
		mUser.userPw((String) inputJson.get(WebSocketAdapterConst.USER_PASSWORD));
		// 유저구분
		mUser.userType(iUserObj.getUserType());
		// 유저명
		mUser.userNm((String) inputJson.get(WebSocketAdapterConst.USER_NAME));
		// 회사명
		mUser.compNm((String) inputJson.get(WebSocketAdapterConst.COMPANY_NAME));
		// 부서명
		mUser.deptNm((String) inputJson.get(WebSocketAdapterConst.DEPARTMENT_NAME));
		// 직책
		mUser.titleNm((String) inputJson.get(WebSocketAdapterConst.JOB_TITLE));
		// 비고
		mUser.remark((String) inputJson.get(WebSocketAdapterConst.REMARK));

		//////////////////////////////////////////////////////////////////////
		// 도메인 유무 체크
		//////////////////////////////////////////////////////////////////////
		IDomainIdMastObj domainObj = pm.getDomainIdMastObj(uid);
		// if (domainObj == null) throw new
		// ContextHandlerApplicationException(1008,
		// CommonException.TYPE_WARNNING, "도메인 정보가 존재하지 않습니다.");
		if (domainObj == null)
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":010", new String[]{uid});

		//////////////////////////////////////////////////////////////////////
		// 유저 유무 체크
		//////////////////////////////////////////////////////////////////////
		IUserObj userObj = pm.getUserObj(uid);
		// if (userObj == null) throw new
		// ContextHandlerApplicationException(1025,
		// CommonException.TYPE_WARNNING, "유저 정보가 존재하지 않습니다.");
		if (userObj == null)
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":015", new String[]{uid});

		//////////////////////////////////////////////////////////////////////
		// 유저풀 유무 체크
		//////////////////////////////////////////////////////////////////////
		IUserPoolObj userPoolObj = pm.getUserPoolObj((String) inputJson.get(WebSocketAdapterConst.UPID));
		// if (userPoolObj == null) throw new
		// ContextHandlerApplicationException(1030,
		// CommonException.TYPE_WARNNING, "유저풀 정보가 존재하지 않습니다.");
		if (userPoolObj == null)
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":020", new String[]{(String) inputJson.get(WebSocketAdapterConst.UPID), uid});

		//////////////////////////////////////////////////////////////////////
		// 도메인 변경
		//////////////////////////////////////////////////////////////////////
		IModifyDomainIdMastObj mDomainObj = pm.getModifyDomainIdMastObj();
		try {
			mDomainObj.domainId(uid).domainNm((String) inputJson.get(WebSocketAdapterConst.DEV_NAME))
					.domainType(WebSocketAdapterConst.UID).update();
		} catch (Exception e) {// 도메인 변경 실패시 에러 처리
			// throw new ContextHandlerApplicationException(312,
			// CommonException.TYPE_ERROR, "도메인 변경에 실패 했습니다.", e);
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":025", new String[]{uid},
					e);
		}

		//////////////////////////////////////////////////////////////////////
		// 유저 변경
		//////////////////////////////////////////////////////////////////////
		try {
			mUser.update();
		} catch (Exception ex) {
			// 도메인 롤백
			mDomainObj = pm.getModifyDomainIdMastObj(domainObj);
			mDomainObj.update();

			// throw new ContextHandlerApplicationException(330,
			// CommonException.TYPE_ERROR, "유저 변경에 실패 했습니다.", ex);
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":030", new String[]{uid},
					ex);
		}

		outboundCtx.getPaths().add("ack");
		outboundCtx.setSID(inboundCtx.getSID());
		outboundCtx.setSPort(inboundCtx.getSPort());
		outboundCtx.setTID("this");
		outboundCtx.setTPort(inboundCtx.getTPort());
		outboundCtx.setTransmission("res");

	}
}
