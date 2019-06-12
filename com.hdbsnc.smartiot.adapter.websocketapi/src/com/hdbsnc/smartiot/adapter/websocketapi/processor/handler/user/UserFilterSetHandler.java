package com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.user;

import java.nio.charset.StandardCharsets;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.hdbsnc.smartiot.adapter.websocketapi.constant.WebSocketAdapterConst;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractFunctionHandler;
import com.hdbsnc.smartiot.common.pm.IProfileManager;
import com.hdbsnc.smartiot.common.pm.vo.IModifyUserFilterObj;
import com.hdbsnc.smartiot.common.pm.vo.IUserFilterObj;

/**
 * user/filter/set
 * 
 * @author KANG
 *
 */
public class UserFilterSetHandler extends AbstractFunctionHandler {

	private IProfileManager pm;

	public UserFilterSetHandler(IProfileManager pm) {
		super("set");
		this.pm = pm;
	}

	@Override
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		String uid = inboundCtx.getParams().get(WebSocketAdapterConst.UID);
		String authFilter = inboundCtx.getParams().get(WebSocketAdapterConst.AUTHORITY_FILTER);

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

		IModifyUserFilterObj iModifyUserFilterObj = pm.getModifyUserFilterObj();

		// 유저ID
		iModifyUserFilterObj.userId(uid);
		// 권한필터
		iModifyUserFilterObj.authFilter(authFilter);
		// 비고
		iModifyUserFilterObj.remark((String) inputJson.get(WebSocketAdapterConst.REMARK));

		//////////////////////////////////////////////////////////////////////
		// 유저필터 유무 체크
		//////////////////////////////////////////////////////////////////////
		IUserFilterObj userFilterObj = pm.getUserFilterObj(uid, authFilter);
		// if (userFilterObj == null) throw new
		// ContextHandlerApplicationException(1026,
		// CommonException.TYPE_WARNNING, "유저필터 정보가 존재하지 않습니다.");
		if (userFilterObj == null)
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":010", new String[]{uid, authFilter});

		//////////////////////////////////////////////////////////////////////
		// 유저필터 변경
		//////////////////////////////////////////////////////////////////////
		try {
			iModifyUserFilterObj.update();
		} catch (Exception ex) {
			// throw new ContextHandlerApplicationException(327,
			// CommonException.TYPE_ERROR, "유저필터 변경에 실패 했습니다.", ex);
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":015", new String[]{uid, authFilter},
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
