package com.hdbsnc.smartiot.adapter.websocketapi.event.consumer;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.json.simple.JSONObject;

import com.hdbsnc.smartiot.adapter.websocketapi.constant.ProtocolConst;
import com.hdbsnc.smartiot.adapter.websocketapi.constant.WebSocketAdapterConst;
import com.hdbsnc.smartiot.common.ICommonService;
import com.hdbsnc.smartiot.common.connection.IConnection;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.em.IEventConsumer;
import com.hdbsnc.smartiot.common.em.event.IEvent;
import com.hdbsnc.smartiot.common.exception.CommonException;
import com.hdbsnc.smartiot.common.otp.url.parser.UrlParser;
import com.hdbsnc.smartiot.common.otp.url.parser.vo.Url;
import com.hdbsnc.smartiot.common.pm.IProfileManager;
import com.hdbsnc.smartiot.common.pm.vo.IDeviceObj;
import com.hdbsnc.smartiot.common.pm.vo.IInstanceAttributeObj;
import com.hdbsnc.smartiot.common.pm.vo.IInstanceObj;
import com.hdbsnc.smartiot.common.pm.vo.IMsgMastObj;
import com.hdbsnc.smartiot.em.impl.DefaultSystemEvent;
import com.hdbsnc.smartiot.pdm.ap.instance.AdapterProcessorEvent;


public class DeviceMsgEventConsumer implements IEventConsumer {

	static SimpleDateFormat formatter = new java.text.SimpleDateFormat(
			"yyyy/MM/dd/ HH:mm:ss sss");
	
	private IConnection con;
	private UrlParser parser;
	private ICommonService comService;
	private String iid;
	private String did;
	private String evtId;
	IContext inboundCtx;
	private IProfileManager pm;

	public DeviceMsgEventConsumer(IContext inboundCtx, IConnection con, ICommonService comService, String iid,
			String did, String evtId, IProfileManager pm) {
		this.inboundCtx = inboundCtx;
		this.con = con;
		this.comService = comService;
		this.iid = iid;
		this.did = did;
		this.evtId = evtId;
		this.parser = UrlParser.getInstance();
		this.pm = pm;
	}

	@Override
	public String getName() {
		return evtId;
	}

	@Override
	public synchronized void initialize() throws Exception {

	}

	@Override
	public synchronized void dispose() {

	}

	@Override
	public void updateEvent(IEvent event) throws CommonException {
		if (event instanceof AdapterProcessorEvent) {
			AdapterProcessorEvent ape = (AdapterProcessorEvent) event;
			IContext iContext = ape.getContext();
			int eventValue = ape.eventValue() << 8;
			int eventTypeValue = ape.eventTypeValue() << 4;
			int eventStateValue = ape.eventStateValue();
			
			if (!(did.equals(iContext.getTID()) && iid.equals(ape.getIID()))){
				return; // iid, did가 다르면 무시.
			}
			System.out.println(ape.eventCodeToUser() + ", SID=" + iContext.getSID() + ", IID=" + ape.getIID() + ", TID="
					+ iContext.getTID() + ", " + iContext.getFullPath() + ", " + iContext.getTransmission());
			switch (eventValue) {
			case IEvent.EVENT_INSTANCE_LIFECYCLE:
				switch (eventStateValue) {
				case IEvent.STATE_COMPLETED:
//					JSONObject json = new JSONObject();
//
//					// SID(user), TID, IID, path, parm, time, transmission,
//					// content type, content
//					// json.put("module.name", ape.moduleName());
//					// json.put("event.name", ape.eventName());
//					// json.put("event.type", ape.eventTypeName());
//					// json.put("event.state", ape.eventStateName());
//					// json.put("otp.fullpath", iContext.getFullPath());
//
//					// SID
//					json.put(WebSocketAdapterConst.SID, iContext.getSID());
//					// IID
//					json.put(WebSocketAdapterConst.IID, ape.getIID());
//					// IID Name
//					IInstanceObj insObj = null;
//					try {
//						insObj = this.pm.getInstanceObj(ape.getIID());
//					} catch (Exception e) {
//						// 예외 무시
//					}
//					if (insObj != null) {
//						json.put(WebSocketAdapterConst.INS_NAME, insObj.getInsNm());
//					} else {
//						json.put(WebSocketAdapterConst.INS_NAME, "undefined");
//					}
//					// TID
//					json.put(WebSocketAdapterConst.TID, iContext.getTID());
//					IDeviceObj devObj = null;
//					try {
//						devObj = pm.getDeviceObj(iContext.getTID());
//					} catch (Exception e) {
//						// 예외 무시
//					}
//					if (devObj != null) {
//						json.put(WebSocketAdapterConst.DEV_NAME, devObj.getDevNm());
//					} else {
//						json.put(WebSocketAdapterConst.DEV_NAME, "undefined");
//					}
//
//					json.put(WebSocketAdapterConst.TRAN, iContext.getTransmission());
//					String attKey = iContext.getFullPath().replaceAll("/ack$", "");
//					json.put(WebSocketAdapterConst.ATT_KEY, attKey);
//					IInstanceAttributeObj insAttObj = null;
//					try {
//						insAttObj = pm.getInstanceAttributeObj(ape.getIID(), attKey);
//					} catch (Exception e) {
//						//  무시
//					}
//					if (insAttObj != null) {
//						json.put(WebSocketAdapterConst.ATT_DESCRIPTION, insAttObj.getDsct());
//					} else {
//						json.put(WebSocketAdapterConst.ATT_DESCRIPTION, "undefined");
//					}
//
//					Map<String, String> params = iContext.getParams();
//					StringBuffer commandKeyBuf = new StringBuffer();
//					StringBuffer commandValueBuf = new StringBuffer();
//					for (Map.Entry<String, String> elem : params.entrySet()) {
//						commandKeyBuf.append("[").append(elem.getKey()).append("]");
//						commandValueBuf.append("[").append(elem.getValue()).append("]");
//					}
//					json.put(WebSocketAdapterConst.CMD_KEY, commandKeyBuf.toString());
//					json.put(WebSocketAdapterConst.CMD_VALUE, commandValueBuf.toString());
//
//					json.put(WebSocketAdapterConst.CONTENT_TYPE, iContext.getContentType());
//					json.put(WebSocketAdapterConst.CONTENT,
//							new String(iContext.getContent().array(), Charset.forName("UTF-8")));
//
//					// 이벤트ID
//					json.put(WebSocketAdapterConst.EVENT_ID, evtId);
//
//					Url resUrl = Url.createOtp();
//					resUrl.addPath("event").addPath("dmsg").addPath("start");
//					resUrl.addPath(ProtocolConst.ACK);
//					resUrl.setUserInfo(inboundCtx.getSID(), inboundCtx.getSPort());
//					resUrl.setHostInfo(ProtocolConst.THIS, inboundCtx.getTPort());
//					resUrl.addFrag(ProtocolConst.TRANS, ProtocolConst.TRANS_EVT);
//					resUrl.addFrag(ProtocolConst.CONT, ProtocolConst.CONT_JSON);
//					try {
//						this.con.write(parser.parse(resUrl) + json.toJSONString());
//					} catch (Exception e) {
//						throw comService.getExceptionfactory().createSysException(this.getClass().getName() + ":005",
//								null, e);
//					}
					break;
				default: // completed아닌 이벤트는 전송하지 않음.
					break;
				}
				break;
			case IEvent.EVENT_INSTANCE_PROCESSOR:
				switch (eventTypeValue) {
				case IEvent.TYPE_REQUEST:
					switch (eventStateValue) {
						case IEvent.STATE_BEGIN:
							//worker.data.addInboundCount();
							break;
						case IEvent.STATE_TRANSFER:
							//worker.data.addOutboundCount();
							break;
						
						case IEvent.STATE_SUCCESS:
							JSONObject json = new JSONObject();

							// SID
							json.put(WebSocketAdapterConst.SID, iContext.getSID());
							// IID
							json.put(WebSocketAdapterConst.IID, ape.getIID());
							// IID Name
							IInstanceObj insObj = null;
							try {
								insObj = this.pm.getInstanceObj(ape.getIID());
							} catch (Exception e) {
								// 예외 무시
							}
							if (insObj != null) {
								json.put(WebSocketAdapterConst.INS_NAME, insObj.getInsNm());
							} else {
								json.put(WebSocketAdapterConst.INS_NAME, "");
							}
							// TID
							json.put(WebSocketAdapterConst.TID, iContext.getTID());
							IDeviceObj devObj = null;
							try {
								devObj = pm.getDeviceObj(iContext.getTID());
							} catch (Exception e) {
								// 예외 무시
							}
							if (devObj != null) {
								json.put(WebSocketAdapterConst.DEV_NAME, devObj.getDevNm());
							} else {
								json.put(WebSocketAdapterConst.DEV_NAME, "");
							}

							json.put(WebSocketAdapterConst.TRAN, "req");
							String attKey = iContext.getFullPath().replaceAll("/ack$", "");
							json.put(WebSocketAdapterConst.ATT_KEY, attKey);
							IInstanceAttributeObj insAttObj = null;
							try {
								insAttObj = pm.getInstanceAttributeObj(ape.getIID(), attKey);
							} catch (Exception e) {
								//  무시
							}
							if (insAttObj != null) {
								json.put(WebSocketAdapterConst.ATT_DESCRIPTION, insAttObj.getDsct());
							} else {
								json.put(WebSocketAdapterConst.ATT_DESCRIPTION, "");
							}

							Map<String, String> params = iContext.getParams();
							StringBuffer commandKeyBuf = new StringBuffer();
							StringBuffer commandValueBuf = new StringBuffer();
							for (Map.Entry<String, String> elem : params.entrySet()) {
								commandKeyBuf.append("[").append(elem.getKey()).append("]");
								commandValueBuf.append("[").append(elem.getValue()).append("]");
							}
							json.put(WebSocketAdapterConst.CMD_KEY, commandKeyBuf.toString());
							json.put(WebSocketAdapterConst.CMD_VALUE, commandValueBuf.toString());

							if(iContext.getContentType() != null && iContext.getContent() !=null ){
								json.put(WebSocketAdapterConst.CONTENT_TYPE, iContext.getContentType());
								json.put(WebSocketAdapterConst.CONTENT,	new String(iContext.getContent().array(), Charset.forName("UTF-8")));
							}else{
								json.put(WebSocketAdapterConst.CONTENT_TYPE, "");
								json.put(WebSocketAdapterConst.CONTENT,	"");
							}

							// 이벤트ID
							json.put(WebSocketAdapterConst.EVENT_ID, evtId);
							
							json.put(WebSocketAdapterConst.EVENT_TIME, formatter.format(new Date()));

							Url resUrl = Url.createOtp();
							resUrl.addPath("event").addPath("dmsg").addPath("start");
							resUrl.addPath(ProtocolConst.ACK);
							resUrl.setUserInfo(inboundCtx.getSID(), inboundCtx.getSPort());
							resUrl.setHostInfo(ProtocolConst.THIS, inboundCtx.getTPort());
							resUrl.addFrag(ProtocolConst.TRANS, ProtocolConst.TRANS_EVT);
							resUrl.addFrag(ProtocolConst.CONT, ProtocolConst.CONT_JSON);
							try {
								this.con.write(parser.parse(resUrl) + json.toJSONString());
							} catch (Exception e) {
								throw comService.getExceptionfactory().createSysException(this.getClass().getName() + ":005",
										null, e);
							}	
							break;
					}
				case IEvent.TYPE_RESPONSE:
					switch (eventStateValue) {
						case IEvent.STATE_BEGIN:
							//worker.data.addInboundCount();
							break;
						case IEvent.STATE_TRANSFER:
							//worker.data.addOutboundCount();
							break;
						case IEvent.STATE_SUCCESS:
							JSONObject json = new JSONObject();

							// SID
							json.put(WebSocketAdapterConst.SID, iContext.getSID());
							// IID
							json.put(WebSocketAdapterConst.IID, ape.getIID());
							// IID Name
							IInstanceObj insObj = null;
							try {
								insObj = this.pm.getInstanceObj(ape.getIID());
							} catch (Exception e) {
								// 예외 무시
							}
							if (insObj != null) {
								json.put(WebSocketAdapterConst.INS_NAME, insObj.getInsNm());
							} else {
								json.put(WebSocketAdapterConst.INS_NAME, "");
							}
							// TID
							json.put(WebSocketAdapterConst.TID, iContext.getTID());
							IDeviceObj devObj = null;
							try {
								devObj = pm.getDeviceObj(iContext.getTID());
							} catch (Exception e) {
								// 예외 무시
							}
							if (devObj != null) {
								json.put(WebSocketAdapterConst.DEV_NAME, devObj.getDevNm());
							} else {
								json.put(WebSocketAdapterConst.DEV_NAME, "");
							}

							json.put(WebSocketAdapterConst.TRAN, "res");
							String attKey = iContext.getFullPath().replaceAll("/ack$", "");
							json.put(WebSocketAdapterConst.ATT_KEY, attKey);
							IInstanceAttributeObj insAttObj = null;
							try {
								insAttObj = pm.getInstanceAttributeObj(ape.getIID(), attKey);
							} catch (Exception e) {
								//  무시
							}
							if (insAttObj != null) {
								json.put(WebSocketAdapterConst.ATT_DESCRIPTION, insAttObj.getDsct());
							} else {
								json.put(WebSocketAdapterConst.ATT_DESCRIPTION, "");
							}

							Map<String, String> params = iContext.getParams();
							StringBuffer commandKeyBuf = new StringBuffer();
							StringBuffer commandValueBuf = new StringBuffer();
							for (Map.Entry<String, String> elem : params.entrySet()) {
								commandKeyBuf.append("[").append(elem.getKey()).append("]");
								commandValueBuf.append("[").append(elem.getValue()).append("]");
							}
							json.put(WebSocketAdapterConst.CMD_KEY, commandKeyBuf.toString());
							json.put(WebSocketAdapterConst.CMD_VALUE, commandValueBuf.toString());

							if(iContext.getContentType() != null && iContext.getContent() !=null ){
								json.put(WebSocketAdapterConst.CONTENT_TYPE, iContext.getContentType());
								json.put(WebSocketAdapterConst.CONTENT,	new String(iContext.getContent().array(), Charset.forName("UTF-8")));
							}else{
								json.put(WebSocketAdapterConst.CONTENT_TYPE, "");
								json.put(WebSocketAdapterConst.CONTENT,	"");
							}

							// 이벤트ID
							json.put(WebSocketAdapterConst.EVENT_ID, evtId);
							
							json.put(WebSocketAdapterConst.EVENT_TIME, formatter.format(new Date()));

							Url resUrl = Url.createOtp();
							resUrl.addPath("event").addPath("dmsg").addPath("start");
							resUrl.addPath(ProtocolConst.ACK);
							resUrl.setUserInfo(inboundCtx.getSID(), inboundCtx.getSPort());
							resUrl.setHostInfo(ProtocolConst.THIS, inboundCtx.getTPort());
							resUrl.addFrag(ProtocolConst.TRANS, ProtocolConst.TRANS_EVT);
							resUrl.addFrag(ProtocolConst.CONT, ProtocolConst.CONT_JSON);
							try {
								this.con.write(parser.parse(resUrl) + json.toJSONString());
							} catch (Exception e) {
								throw comService.getExceptionfactory().createSysException(this.getClass().getName() + ":005",
										null, e);
							}	
							break;
							
						
					}
					break;
				case IEvent.TYPE_EVENT:
					// eventStateValue() 로 begin/transfer/success순으로 연속으로 이벤트
					// 전달됨. 중간에 실패시 fail 전달됨.
					// 상기 이벤트 스테이트 중에서 success만 보낼지 전부다 보낼지 고민해야 함.
					switch (eventStateValue) {
					case IEvent.STATE_BEGIN:
						//worker.data.addInboundCount();
						break;
					case IEvent.STATE_TRANSFER:
						//worker.data.addOutboundCount();
						break;
					case IEvent.STATE_SUCCESS:
						JSONObject json = new JSONObject();

						// SID
						json.put(WebSocketAdapterConst.SID, iContext.getSID());
						// IID
						json.put(WebSocketAdapterConst.IID, ape.getIID());
						// IID Name
						IInstanceObj insObj = null;
						try {
							insObj = this.pm.getInstanceObj(ape.getIID());
						} catch (Exception e) {
							// 예외 무시
						}
						if (insObj != null) {
							json.put(WebSocketAdapterConst.INS_NAME, insObj.getInsNm());
						} else {
							json.put(WebSocketAdapterConst.INS_NAME, "");
						}
						// TID
						json.put(WebSocketAdapterConst.TID, iContext.getTID());
						IDeviceObj devObj = null;
						try {
							devObj = pm.getDeviceObj(iContext.getTID());
						} catch (Exception e) {
							// 예외 무시
						}
						if (devObj != null) {
							json.put(WebSocketAdapterConst.DEV_NAME, devObj.getDevNm());
						} else {
							json.put(WebSocketAdapterConst.DEV_NAME, "");
						}

						json.put(WebSocketAdapterConst.TRAN, "evt");
						String attKey = iContext.getFullPath().replaceAll("/ack$", "");
						json.put(WebSocketAdapterConst.ATT_KEY, attKey);
						IInstanceAttributeObj insAttObj = null;
						try {
							insAttObj = pm.getInstanceAttributeObj(ape.getIID(), attKey);
						} catch (Exception e) {
							//  무시
						}
						if (insAttObj != null) {
							json.put(WebSocketAdapterConst.ATT_DESCRIPTION, insAttObj.getDsct());
						} else {
							json.put(WebSocketAdapterConst.ATT_DESCRIPTION, "");
						}

						Map<String, String> params = iContext.getParams();
						StringBuffer commandKeyBuf = new StringBuffer();
						StringBuffer commandValueBuf = new StringBuffer();
						for (Map.Entry<String, String> elem : params.entrySet()) {
							commandKeyBuf.append("[").append(elem.getKey()).append("]");
							commandValueBuf.append("[").append(elem.getValue()).append("]");
						}
						json.put(WebSocketAdapterConst.CMD_KEY, commandKeyBuf.toString());
						json.put(WebSocketAdapterConst.CMD_VALUE, commandValueBuf.toString());

						if(iContext.getContentType() != null && iContext.getContent() !=null ){
							json.put(WebSocketAdapterConst.CONTENT_TYPE, iContext.getContentType());
							json.put(WebSocketAdapterConst.CONTENT,	new String(iContext.getContent().array(), Charset.forName("UTF-8")));
						}else{
							json.put(WebSocketAdapterConst.CONTENT_TYPE, "");
							json.put(WebSocketAdapterConst.CONTENT,	"");
						}

						// 이벤트ID
						json.put(WebSocketAdapterConst.EVENT_ID, evtId);
						
						json.put(WebSocketAdapterConst.EVENT_TIME, formatter.format(new Date()));

						Url resUrl = Url.createOtp();
						resUrl.addPath("event").addPath("dmsg").addPath("start");
						resUrl.addPath(ProtocolConst.ACK);
						resUrl.setUserInfo(inboundCtx.getSID(), inboundCtx.getSPort());
						resUrl.setHostInfo(ProtocolConst.THIS, inboundCtx.getTPort());
						resUrl.addFrag(ProtocolConst.TRANS, ProtocolConst.TRANS_EVT);
						resUrl.addFrag(ProtocolConst.CONT, ProtocolConst.CONT_JSON);
						try {
							this.con.write(parser.parse(resUrl) + json.toJSONString());
						} catch (Exception e) {
							throw comService.getExceptionfactory().createSysException(this.getClass().getName() + ":005",
									null, e);
						}	
						break;
					case IEvent.STATE_FAIL:
						// 전송할 메시지 만들어서 전송.
						break;
					}
					break;
				}
				break;
			default:
				// 이외 이벤트는 전송하지 않는다.
				break;
			}
		} else if (event instanceof DefaultSystemEvent) {
			DefaultSystemEvent dse = (DefaultSystemEvent) event;
			// 향후 에러 유형이 추가되면 여기서 분기타서 처리해줘야 함.
			if (dse.eventID() == (IEvent.MODULE_EM | IEvent.EVENT_EVENTCONSUMER | IEvent.TYPE_UPDATEEVENT
					| IEvent.STATE_FAIL)) {
				IMsgMastObj msgObj = comService.getExceptionfactory().getMsgInfo("402");
				Url resUrl = Url.createOtp();
				resUrl.addPath("event").addPath("dmsg").addPath(ProtocolConst.NACK);
				resUrl.addQuery("code", msgObj.getOuterCode());
				resUrl.addQuery("type", msgObj.getType());
				resUrl.addQuery("msg", msgObj.getMsg() + "(" + dse.eventCodeToUser() + ")");
				resUrl.setUserInfo(inboundCtx.getSID(), inboundCtx.getSPort());
				resUrl.setHostInfo(ProtocolConst.THIS, inboundCtx.getTPort());
				resUrl.addFrag(ProtocolConst.TRANS, ProtocolConst.TRANS_EVT);
				try {
					this.con.write(parser.parse(resUrl));
				} catch (Exception e) {
					throw comService.getExceptionfactory().createSysException(this.getClass().getName() + ":005", null,
							e);
				}
			}
			// 이외의 이벤트 유형이 여기에 떨어진다면 처리해주어야 한다.
		}
	}

	

}
