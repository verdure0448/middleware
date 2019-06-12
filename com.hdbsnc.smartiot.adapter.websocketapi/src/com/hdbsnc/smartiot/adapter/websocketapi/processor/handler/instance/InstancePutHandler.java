package com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.instance;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.hdbsnc.smartiot.adapter.websocketapi.constant.WebSocketAdapterConst;
import com.hdbsnc.smartiot.common.am.IAdapter;
import com.hdbsnc.smartiot.common.am.IAdapterManager;
import com.hdbsnc.smartiot.common.am.IAdapterManifest;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractFunctionHandler;
import com.hdbsnc.smartiot.common.pm.IProfileManager;
import com.hdbsnc.smartiot.common.pm.vo.IDeviceObj;
import com.hdbsnc.smartiot.common.pm.vo.IDevicePoolObj;
import com.hdbsnc.smartiot.common.pm.vo.IDomainIdMastObj;
import com.hdbsnc.smartiot.common.pm.vo.IInstanceObj;
import com.hdbsnc.smartiot.common.pm.vo.IModifyDomainIdMastObj;
import com.hdbsnc.smartiot.common.pm.vo.IModifyInstanceAttributeObj;
import com.hdbsnc.smartiot.common.pm.vo.IModifyInstanceFunctionObj;
import com.hdbsnc.smartiot.common.pm.vo.IModifyInstanceObj;

/**
 * ins/put 인스턴스 등록
 * 
 * @author KANG
 *
 */
public class InstancePutHandler extends AbstractFunctionHandler {

	private IProfileManager pm;
	private IAdapterManager am;

	public InstancePutHandler(IProfileManager pm, IAdapterManager am) {
		super("put");
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
		mInsObj.insType((String) inputJson.get(WebSocketAdapterConst.INS_TYPE));

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
		// 도메인ID 유무 체크
		//////////////////////////////////////////////////////////////////////
		IDomainIdMastObj iDomainObj = pm.getDomainIdMastObj(iid);
		// 도메인이 존재할 경우 에러 처리
		if (iDomainObj != null)
			// throw new ContextHandlerApplicationException(1018,
			// CommonException.TYPE_WARNNING, "인스턴스ID가 도메인에 존재합니다.");
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":010", new String[]{iid});
		//////////////////////////////////////////////////////////////////////
		// 아답터 유무 체크
		//////////////////////////////////////////////////////////////////////
		List<IAdapter> adapterList = am.getAdapterList();
		String aid = (String) inputJson.get(WebSocketAdapterConst.AID);
		boolean isExistAdapter = false;
		IAdapterManifest mani;
		for (IAdapter iAdapter : adapterList) {
			mani = iAdapter.getManifest();
			if (aid.equals(mani.getAdapterId())) {
				isExistAdapter = true;
				break;
			}
		}

		if (!isExistAdapter)
			// throw new ContextHandlerApplicationException(1019,
			// CommonException.TYPE_WARNNING, "아답터 정보가 존재하지 않습니다.");
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":015", new String[]{aid});
		//////////////////////////////////////////////////////////////////////
		// 장치풀ID 유무체크
		//////////////////////////////////////////////////////////////////////
		IDevicePoolObj devPoolObj = pm.getDevicePoolObj((String) inputJson.get(WebSocketAdapterConst.DPID));
		if (devPoolObj == null)
			// throw new ContextHandlerApplicationException(1009,
			// CommonException.TYPE_WARNNING, "장치풀 정보가 존재하지 않습니다.");
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":020", new String[]{(String) inputJson.get(WebSocketAdapterConst.DPID)});
		//////////////////////////////////////////////////////////////////////
		// 디폴트장치ID 유무체크(인스턴스 종류가 "client"일 때만)
		//////////////////////////////////////////////////////////////////////
		if("client".equals((String) inputJson.get(WebSocketAdapterConst.INS_KIND))){
			IDeviceObj devObj = pm.getDeviceObj((String) inputJson.get(WebSocketAdapterConst.DEFAULT_DEV_ID));
			if (devObj == null)
				// throw new ContextHandlerApplicationException(1020,
				// CommonException.TYPE_WARNNING, "디폴트장치 정보가 존재하지 않습니다.");
				throw getCommonService().getExceptionfactory().createAppException(
						this.getClass().getName() + ":025", new String[]{(String) inputJson.get(WebSocketAdapterConst.DEFAULT_DEV_ID)});
		}
		//////////////////////////////////////////////////////////////////////
		// 인스턴스ID 유무 체크
		//////////////////////////////////////////////////////////////////////
		mInsObj.select();
		IInstanceObj insObj = mInsObj.getResultVo();
		if (insObj != null)
			// throw new ContextHandlerApplicationException(1021,
			// CommonException.TYPE_WARNNING, "인스턴스가 중복입니다.");
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":030", new String[]{iid});
//		//////////////////////////////////////////////////////////////////////
//		// 장치풀ID가 다른 인스턴스에서 사용중 일 경우 에러 응답
//		//////////////////////////////////////////////////////////////////////
//		IInstanceObj iInsObj = pm.searchInstanceByDevPoolId((String) inputJson.get(WebSocketAdapterConst.DPID));
//		if (iInsObj != null)
//			// throw new ContextHandlerApplicationException(1022,
//			// CommonException.TYPE_WARNNING, "장치풀이 다른 인스턴스에서 사용중입니다.");
//			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":035", new String[]{(String) inputJson.get(WebSocketAdapterConst.DPID)});
		//////////////////////////////////////////////////////////////////////
		// 디폴트 장치ID가 장치풀 소속이 아니면 에러 처리
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
		// 도메인 등록
		//////////////////////////////////////////////////////////////////////
		IModifyDomainIdMastObj mDomainObj = pm.getModifyDomainIdMastObj();
		try {
			mDomainObj.domainId(iid).domainNm((String) inputJson.get(WebSocketAdapterConst.INS_NAME))
					.domainType(WebSocketAdapterConst.IID).insert();
		} catch (Exception e) {
			// throw new ContextHandlerApplicationException(310,
			// CommonException.TYPE_ERROR, "도메인 등록에 실패 했습니다.", e);
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":045", new String[]{iid},
					e);
		}

		//////////////////////////////////////////////////////////////////////
		// 인스턴스 등록
		//////////////////////////////////////////////////////////////////////
		try {
			mInsObj.insert();
		} catch (Exception ex) {
			// 도메인 롤백
			mDomainObj.delete();

			// throw new ContextHandlerApplicationException(323,
			// CommonException.TYPE_ERROR, "인스턴스 등록에 실패 했습니다.", ex);
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":050", new String[]{iid},
					ex);
		}

		//////////////////////////////////////////////////////////////////////
		// 속성 등록
		//////////////////////////////////////////////////////////////////////
		IAdapter iAdt = am.getAdapter(aid);
		if (iAdt == null) throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":055", new String[]{aid});
		IAdapterManifest manifest = iAdt.getManifest();
		if (manifest == null) throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":060", new String[]{aid});
		
		List<String> regAttList = (List<String>) inputJson.get(WebSocketAdapterConst.ATTRIBUTE_LIST);
		if(regAttList != null && regAttList.size() >0){
			Pattern p1 = Pattern.compile("(.*)\\?(.*)");
			Pattern pp1 = Pattern.compile("(.*)=(.*)");
			List<String> manifestAttList = manifest.getAttributes();
			int i = 0;
			for (String regAttInfo : regAttList) {
				String attKey = null;
				String attValueType = null;
				String attValue = null;
				IModifyInstanceAttributeObj mAttObj = null;
				for(String menifestAttInfo : manifestAttList) {
					if(regAttInfo.equals(menifestAttInfo)){
						i = i + 1;
						// 변수 초기화
						attKey = "";
						attValueType = "";
						attValue = "";
						
						// '?' 구분자로 속성키와 타입 정보를 추출
						Matcher m1 = p1.matcher(menifestAttInfo);
						if(m1.find()) { // 타입 정보가 존재
							attKey = m1.group(1);
							
							// 속성타입 정보를 타입과 값으로 추출
							Matcher mm1 = pp1.matcher(m1.group(2));
							if(mm1.find()) { // 속성 타입, 값 존재
								attValueType = mm1.group(1);
								attValue = mm1.group(2);
							} else { // 속성 타입만 존재
								attValueType = m1.group(2);
							}
						} else { // 타입 정보 없이 속성 경로만 존재
							attKey = menifestAttInfo;
						}
					
						mAttObj = pm.getModifyInstanceAttributeObj();
						// 인스턴스ID
						mAttObj.insId(iid);
						// 속성키
						mAttObj.key(attKey);
						// 속성이름
						mAttObj.dsct(attKey);
						// 속성값
						mAttObj.value(attValue);
						// 속성타입
						mAttObj.valueType(attValueType);
						// 비고
						mAttObj.remark("자동 등록 속성");
						
						try {
							// 속성 등록
							mAttObj.insert();
						} catch (Exception ex) {
							// 무시
						}
					}
				}	
			}
		}
		//////////////////////////////////////////////////////////////////////
		// 기능 등록
		//////////////////////////////////////////////////////////////////////
		List<String> regFuncList = (List<String>) inputJson.get(WebSocketAdapterConst.FUNCTION_LIST);
		if(regFuncList != null && regFuncList.size() >0){
			// 모든 정보가 있는 경우 패턴
			Pattern p1 = Pattern.compile("(.*)\\?(.*)\\#(.*)");
			// 컨텐츠 정보가 없는 경우 패턴
			Pattern p2 = Pattern.compile("(.*)\\?(.*)");
			// 파라미터 정보가 없는 경우 패턴
			Pattern p3 = Pattern.compile("(.*)\\#(.*)");
			// 파라미터 정보 패턴
			Pattern pp1 = Pattern.compile("(.*):(.*)");
			// 컨텐츠 정보 패턴
			Pattern pp2 = Pattern.compile("(cont|content):(.*)");
			
			List<String> manifestFuncList = manifest.getFunctions();

			String funcKey = null;
			String[] param = null;
			String[] paramType = null;
			String  contType = null;
			
			IModifyInstanceFunctionObj mFuncObj = null;
			int i = 0;
			for (String manifestFuncInfo : manifestFuncList) {
				i = i + 1;
				for (String regFuncInfo : regFuncList) {
					if(regFuncInfo.equals(manifestFuncInfo)){
						
						funcKey = "";
						contType = "";
						param = new String[]{"", "", "", "", ""};
						paramType = new String[]{"", "", "", "", ""};
						
						Matcher m1 = p1.matcher(regFuncInfo);
						Matcher m2 = p2.matcher(regFuncInfo);
						Matcher m3 = p3.matcher(regFuncInfo);
						
						if(m1.find()){ // 모든 정보가 있는 경우
							funcKey = m1.group(1);
							
							String[] paramInfos = m1.group(2).split("&");
							
							if(paramInfos != null && paramInfos.length > 0){
								int j = 0;
								for (String paramInfo : paramInfos) {
									Matcher mm1 = pp1.matcher(paramInfo);
									if(mm1.find()){ // 파라미터 타입이 있는 경우
										param[j] = mm1.group(1);
										paramType[j] = mm1.group(2);
									} else { // 파라미터 타입이 없는 경우
										param[j] = paramInfo;
									}
									j = j + 1;
								}
							}
							
							//컨텐츠
							Matcher mm2 = pp2.matcher(m1.group(3));
							if(mm2.find()){
								contType = mm2.group(2);
							}
							
						} else if(m2.find()) { // 컨텐츠 정보가 없는 경우
							funcKey = m2.group(1);
							String[] paramInfos = m2.group(2).split("&");
							if(paramInfos != null && paramInfos.length > 0){
								int j = 0;
								for (String paramInfo : paramInfos) {
									Matcher mm1 = pp1.matcher(paramInfo);
									if(mm1.find()){
										param[j] = mm1.group(1);
										paramType[j] = mm1.group(2);
									} else {
										param[j] = paramInfo;
									}
									j++;
								}
							}
						} else if(m3.find()) { // 파라미터 정보가 없는 경우 
							funcKey = m3.group(1);
							
							Matcher mm2 = pp2.matcher(m3.group(2));
							if(mm2.find()){
								contType = mm2.group(2);
							}
						} else { // 기능키만 있는 경우
							funcKey = regFuncInfo;
						}
						
						mFuncObj = pm.getModifyInstanceFunctionObj();
						
						// 인스턴스ID
						mFuncObj.insId(iid);
						// 기능키
						mFuncObj.key(funcKey);

						// 기능이름
						mFuncObj.dsct(funcKey);
						// 컨텐츠 타입
						mFuncObj.contType(contType);
						// 파라미터1
						mFuncObj.param1(param[0]);
						// 파라미터2
						mFuncObj.param2(param[1]);
						// 파라미터3
						mFuncObj.param3(param[2]);
						// 파라미터4
						mFuncObj.param4(param[3]);
						// 파라미터5
						mFuncObj.param5(param[4]);
						// 파라미터타입1
						mFuncObj.paramType1(paramType[0]);
						// 파라미터타입2
						mFuncObj.paramType2(paramType[1]);
						// 파라미터타입3
						mFuncObj.paramType3(paramType[2]);
						// 파라미터타입4
						mFuncObj.paramType4(paramType[3]);
						// 파라미터타입5
						mFuncObj.paramType5(paramType[4]);
						// 비고
						mFuncObj.remark("자동 등록");
						
						try {
							// 기능 등록
							mFuncObj.insert();
						} catch (Exception ex) {
							// 무시
						}
					}
				}
			}
		}
		
		// 정상응답
		outboundCtx.getPaths().add("ack");
		outboundCtx.setSID(inboundCtx.getSID());
		outboundCtx.setSPort(inboundCtx.getSPort());
		outboundCtx.setContenttype(null);
		outboundCtx.setContent(null);
		outboundCtx.setTID("this");
		outboundCtx.setTPort(inboundCtx.getTPort());
		outboundCtx.setTransmission("res");
	}
}
