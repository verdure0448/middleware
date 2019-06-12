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
import com.hdbsnc.smartiot.common.pm.vo.IModifyUserPoolObj;
import com.hdbsnc.smartiot.common.pm.vo.IUserPoolObj;

/**
 * userpool/set
 * 
 * @author KANG
 *
 */
public class UserPoolSetHandler extends AbstractFunctionHandler {

	private IProfileManager pm;

	public UserPoolSetHandler(IProfileManager pm) {
		super("set");
		this.pm = pm;
	}

	@Override
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		String upid = inboundCtx.getParams().get(WebSocketAdapterConst.UPID);

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

		IModifyUserPoolObj mUserPool = pm.getModifyUserPoolObj();

		// 유저풀ID
		mUserPool.userPoolId(upid);
		// 유저풀명
		mUserPool.userPoolNm((String) inputJson.get(WebSocketAdapterConst.USER_POOL_NAME));
		// 비고
		mUserPool.remark((String) inputJson.get(WebSocketAdapterConst.REMARK));

		//////////////////////////////////////////////////////////////////////
		// 도메인 변경대상 유무 체크
		//////////////////////////////////////////////////////////////////////
		IDomainIdMastObj domainObj = pm.getDomainIdMastObj(upid);
		// if (domainObj == null) throw new
		// ContextHandlerApplicationException(1008,
		// CommonException.TYPE_WARNNING, "도메인 정보가 존재하지 않습니다.");
		if (domainObj == null)
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":010", new String[]{upid});

		//////////////////////////////////////////////////////////////////////
		// 유저풀 변경대상 유무 체크
		//////////////////////////////////////////////////////////////////////
		IUserPoolObj userPoolObj = pm.getUserPoolObj(upid);
		// if (userPoolObj == null) throw new
		// ContextHandlerApplicationException(1030,
		// CommonException.TYPE_WARNNING, "유저풀 정보가 존재하지 않습니다.");
		if (userPoolObj == null)
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":015", new String[]{upid});

		//////////////////////////////////////////////////////////////////////
		// 도메인 변경
		//////////////////////////////////////////////////////////////////////
		IModifyDomainIdMastObj mDomainObj = pm.getModifyDomainIdMastObj();
		try {
			mDomainObj.domainId(upid).domainNm((String) inputJson.get(WebSocketAdapterConst.USER_POOL_NAME))
					.domainType(WebSocketAdapterConst.UPID).update();
		} catch (Exception e) {// 도메인 변경 실패시 에러 처리
			// throw new ContextHandlerApplicationException(312,
			// CommonException.TYPE_ERROR, "도메인 변경에 실패 했습니다.", e);
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":020", new String[]{upid},
					e);
		}
		//////////////////////////////////////////////////////////////////////
		// 유저풀 변경
		//////////////////////////////////////////////////////////////////////
		try {
			mUserPool.update();
		} catch (Exception ex) {
			// 도메인 롤백
			mDomainObj = pm.getModifyDomainIdMastObj(domainObj);
			mDomainObj.update();

			// throw new ContextHandlerApplicationException(301,
			// CommonException.TYPE_ERROR, "데이터 변경에 실패 했습니다.", ex);
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":025", new String[]{upid},
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
