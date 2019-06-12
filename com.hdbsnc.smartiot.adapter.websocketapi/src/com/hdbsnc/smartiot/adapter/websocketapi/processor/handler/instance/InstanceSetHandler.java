package com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.instance;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.hdbsnc.smartiot.adapter.websocketapi.constant.WebSocketAdapterConst;
import com.hdbsnc.smartiot.common.am.IAdapter;
import com.hdbsnc.smartiot.common.am.IAdapterManager;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractFunctionHandler;
import com.hdbsnc.smartiot.common.pm.IProfileManager;
import com.hdbsnc.smartiot.common.pm.vo.IDeviceObj;
import com.hdbsnc.smartiot.common.pm.vo.IDevicePoolObj;
import com.hdbsnc.smartiot.common.pm.vo.IDomainIdMastObj;
import com.hdbsnc.smartiot.common.pm.vo.IInstanceObj;
import com.hdbsnc.smartiot.common.pm.vo.IModifyDomainIdMastObj;
import com.hdbsnc.smartiot.common.pm.vo.IModifyInstanceObj;

/**
 * ins/set 인스턴스 변경
 * 
 * @author KANG
 *
 */
public class InstanceSetHandler extends AbstractFunctionHandler {

	private IProfileManager pm;
	private IAdapterManager am;

	public InstanceSetHandler(IProfileManager pm, IAdapterManager am) {
		super("set");
		this.pm = pm;
		this.am = am;
	}

	@Override
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		String iid = inboundCtx.getParams().get(WebSocketAdapterConst.IID);

		JSONObject inputJson = null;

		try {
			inputJson = (JSONObject) (new JSONParser())
					.parse(new String(inboundCtx.getContent().array(), StandardCharsets.UTF_8));
		} catch (Exception e) {
			// throw new ContextHandlerApplicationException(1005,
			// CommonException.TYPE_WARNNING, "컨텐츠 데이터 형식이 부정합이 있습니다.",
			// e);
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":005", null,
					e);
		}

		IInstanceObj iInsObj = pm.getInstanceObj(iid);
		IModifyInstanceObj mInsObj = pm.getModifyInstanceObj();

		// 인스턴스ID
		mInsObj.insId(iid);

		// 장치풀ID
		mInsObj.devPoolId((String) inputJson.get(WebSocketAdapterConst.DPID));

		// 아답터ID
		mInsObj.adtId((String) inputJson.get(WebSocketAdapterConst.AID));

		// 인스턴스명
		mInsObj.insNm((String) inputJson.get(WebSocketAdapterConst.INS_NAME));

		// 인스턴스 종류
		mInsObj.insKind((String) inputJson.get(WebSocketAdapterConst.INS_KIND));

		// 디폴트장치 ID
		mInsObj.defaultDevId((String) inputJson.get(WebSocketAdapterConst.DEFAULT_DEV_ID));

		// 인스턴스 구분
		mInsObj.insType(iInsObj.getInsType());

		// 사용여부
		mInsObj.isUse((String) inputJson.get(WebSocketAdapterConst.IS_USE));

		// 세션타임아웃
		mInsObj.sessionTimeout((String) inputJson.get(WebSocketAdapterConst.SESSION_TIMEOUT));

		// 초기기동상태
		mInsObj.initDevStatus((String) inputJson.get(WebSocketAdapterConst.INIT_DEV_STATUS));

		// IP
		mInsObj.ip((String) inputJson.get(WebSocketAdapterConst.IP));

		// PORT
		mInsObj.port((String) inputJson.get(WebSocketAdapterConst.PORT));

		// url
		mInsObj.url((String) inputJson.get(WebSocketAdapterConst.URL));

		// 위도
		mInsObj.lat((String) inputJson.get(WebSocketAdapterConst.LAT));

		// 경도
		mInsObj.lon((String) inputJson.get(WebSocketAdapterConst.LON));

		// 셀프ID
		mInsObj.selfId((String) inputJson.get(WebSocketAdapterConst.SELF_ID));

		// 셀프암호
		mInsObj.selfPw((String) inputJson.get(WebSocketAdapterConst.SELF_PW));

		// 비고
		mInsObj.remark((String) inputJson.get(WebSocketAdapterConst.REMARK));

		//////////////////////////////////////////////////////////////////////
		// 아답터 유무 체크
		//////////////////////////////////////////////////////////////////////
		List<IAdapter> adapterList = am.getAdapterList();
		String aid = (String) inputJson.get(WebSocketAdapterConst.AID);
		boolean isExistAdapter = false;
		for (IAdapter iAdapter : adapterList) {
			if (aid.equals(iAdapter.getAdapterId())) {
				isExistAdapter = true;
				break;
			}
		}

		if (!isExistAdapter)
			// throw new ContextHandlerApplicationException(1019,
			// CommonException.TYPE_WARNNING, "아답터 정보가 존재하지 않습니다.");
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":010", new String[]{aid});

		//////////////////////////////////////////////////////////////////////
		// 장치풀ID 유무체크
		//////////////////////////////////////////////////////////////////////
		IDevicePoolObj devPoolObj = pm.getDevicePoolObj((String) inputJson.get(WebSocketAdapterConst.DPID));
		if (devPoolObj == null)
			// throw new ContextHandlerApplicationException(1009,
			// CommonException.TYPE_WARNNING, "장치풀 정보가 존재하지 않습니다.");
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":015", new String[]{(String) inputJson.get(WebSocketAdapterConst.DPID)});

		//////////////////////////////////////////////////////////////////////
		// 디폴트장치ID 유무체크
		//////////////////////////////////////////////////////////////////////
		if("client".equals((String) inputJson.get(WebSocketAdapterConst.INS_KIND))){
			IDeviceObj devObj = pm.getDeviceObj((String) inputJson.get(WebSocketAdapterConst.DEFAULT_DEV_ID));
			if (devObj == null)
				// throw new ContextHandlerApplicationException(1020,
				// CommonException.TYPE_WARNNING, "디폴트장치 정보가 존재하지 않습니다.");
				throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":020", new String[]{(String) inputJson.get(WebSocketAdapterConst.DEFAULT_DEV_ID)});
		}
		//////////////////////////////////////////////////////////////////////
		// 도메인 유무 체크
		//////////////////////////////////////////////////////////////////////

		IDomainIdMastObj orgDomainObj = pm.getDomainIdMastObj(iid);
		if (orgDomainObj == null)
			// throw new ContextHandlerApplicationException(1008,
			// CommonException.TYPE_WARNNING, "도메인 정보가 존재하지 않습니다.");
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":025", new String[]{iid});

		//////////////////////////////////////////////////////////////////////
		// 인스턴스 유무 체크
		//////////////////////////////////////////////////////////////////////
		IInstanceObj insObj = pm.getInstanceObj(iid);
		if (insObj == null)
			// throw new ContextHandlerApplicationException(1019,
			// CommonException.TYPE_WARNNING, "인스턴스 정보가 존재하지 않습니다.");
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":030");

//		//////////////////////////////////////////////////////////////////////
//		// 장치풀ID가 다른 인스턴스에서 사용중인지를 체크
//		//////////////////////////////////////////////////////////////////////
//		IInstanceObj iInsObj = pm.searchInstanceByDevPoolId((String) inputJson.get(WebSocketAdapterConst.DPID));
//		if (iInsObj != null && !iInsObj.getDevPoolId().equals((String) inputJson.get(WebSocketAdapterConst.DPID)))
//			// throw new ContextHandlerApplicationException(1022,
//			// CommonException.TYPE_WARNNING, "장치풀이 다른 인스턴스에서 사용중입니다.");
//			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":035", new String[]{(String) inputJson.get(WebSocketAdapterConst.DPID)});

		//////////////////////////////////////////////////////////////////////
		// 디폴트 장치ID가 장치풀 소속인지 체크
		//////////////////////////////////////////////////////////////////////
		if(!"".equals((String) inputJson.get(WebSocketAdapterConst.DEFAULT_DEV_ID))){
			IDeviceObj iDevObj = pm.getDeviceObj((String) inputJson.get(WebSocketAdapterConst.DEFAULT_DEV_ID));
			if (!iDevObj.getDevPoolId().equals((String) inputJson.get(WebSocketAdapterConst.DPID)))
				// throw new ContextHandlerApplicationException(1023,
				// CommonException.TYPE_WARNNING, "장치풀에 디폴트 장치 정보가 없습니다.");
				throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":040",
						new String[]{(String) inputJson.get(WebSocketAdapterConst.DPID), (String) inputJson.get(WebSocketAdapterConst.DEFAULT_DEV_ID)});
		}
		//////////////////////////////////////////////////////////////////////
		// 도메인 변경
		//////////////////////////////////////////////////////////////////////
		IModifyDomainIdMastObj mDomainObj = pm.getModifyDomainIdMastObj();
		try {
			mDomainObj.domainId(iid).domainNm((String) inputJson.get(WebSocketAdapterConst.INS_NAME))
					.domainType(WebSocketAdapterConst.IID).update();
		} catch (Exception e) {
			// throw new ContextHandlerApplicationException(312,
			// CommonException.TYPE_ERROR, "도메인 변경에 실패 했습니다.", e);
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":045", new String[]{iid},
					e);
		}

		//////////////////////////////////////////////////////////////////////
		// 인스턴스 변경
		//////////////////////////////////////////////////////////////////////
		try {
			mInsObj.update();
		} catch (Exception ex) {
			// 도메인 롤백
			mDomainObj = pm.getModifyDomainIdMastObj(orgDomainObj);
			mDomainObj.update();

			// throw new ContextHandlerApplicationException(318,
			// CommonException.TYPE_ERROR, "인스턴스 변경에 실패 했습니다.", ex);
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":050", new String[]{iid},
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
