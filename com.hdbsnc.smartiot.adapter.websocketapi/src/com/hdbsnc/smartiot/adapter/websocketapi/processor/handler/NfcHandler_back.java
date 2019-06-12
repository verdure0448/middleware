package com.hdbsnc.smartiot.adapter.websocketapi.processor.handler;

import java.util.Arrays;
import java.util.HashMap;

import com.hdbsnc.smartiot.common.aim.IAdapterInstanceManager;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler.exception.ContextHandlerApplicationException;
import com.hdbsnc.smartiot.common.context.handler.exception.ContextHandlerUnSupportedMethodException;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractAttributeHandler;
import com.hdbsnc.smartiot.common.exception.CommonException;
import com.hdbsnc.smartiot.common.ism.IIntegratedSessionManager;
import com.hdbsnc.smartiot.common.ism.sm.ISession;
import com.hdbsnc.smartiot.common.ism.sm.ISessionManager;
import com.hdbsnc.smartiot.common.otp.url.parser.UrlParser;

public class NfcHandler_back extends AbstractAttributeHandler{
			
	private String[] devices = {"smartiot.nfc.1", //LG  
								"smartiot.nfc.2", //SONY 
								"smartiot.nfc.3"};
	
	private String[] tags = { 	"568b76f9", //황준석 
								"96eb9ff9", //강대현 
								"b4ba2ca6", //이한 
								"a6d99df9", //김태희  
								"366879f9", //구혜선
								"53025055", //황준석 
								"c90d5055", //강대현
								"c9854f55"};//이한
	
	private IAdapterInstanceManager aim;
	
	public NfcHandler_back(IAdapterInstanceManager aim) {
		super("nfc");
		this.aim = aim;
	}

//	@Override
//	protected IContext read(IContext inboundCtx, ISession session, String value) throws Exception {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	protected IContext update(IContext inboundCtx, ISession session, String value) throws Exception {
//		if(value==null || value.equals("")){
//			//에러 메시지를 보낸다. 
//			
//			return null;
//		}
//		IIntegratedSessionManager ism = sm.getIntegratedSessionManager();
//
//		String did = session.getDeviceId();
//		if(did.equals(devices[0])){// smartiot.nfc.1  [퇴실] 
//			if(value.equals(tags[0])){//황준석 
//				//deviceOnOff(did, "1", "off");
////				deviceOnOff(did, "2", "off");
////				deviceOnOff(did, "3", "off");
//				deviceHue(did, "1", "54");
//				Thread.sleep(500);
//				deviceOnOff(did, "1", "off");
//				officeOnOff(did, "office1", "off");
//				socketOnOff(did, "power1", "off");
//			}else if(value.equals(tags[1])){//강대현 
//				//deviceOnOff(did, "1", "off");
////				deviceOnOff(did, "2", "off");
////				deviceOnOff(did, "3", "off");
//				deviceHue(did, "2", "54");
//				Thread.sleep(500);
//				deviceOnOff(did, "2", "off");
//				officeOnOff(did, "office2", "off");
//				socketOnOff(did, "power2", "off");
//			}else if(value.equals(tags[2])){//이한 
//				//deviceOnOff(did, "1", "off");
////				deviceOnOff(did, "2", "off");
////				deviceOnOff(did, "3", "off");
//				deviceHue(did, "3", "54");
//				Thread.sleep(500);
//				deviceOnOff(did, "3", "off");
//				officeOnOff(did, "office3", "off");
//				socketOnOff(did, "power3", "off");
//			}else if(value.equals(tags[3])){//김태희 
//				//초기화 
//				initClose(did);
//			}else if(value.equals(tags[4])){
//				initOpen(did);
//			}else{
////				deviceOnOff(did, "1", "off");
////				deviceOnOff(did, "2", "off");
////				deviceOnOff(did, "3", "off");
//			}
//		}else if(did.equals(devices[1])){// smartiot.nfc.2  [입실]
//			if(value.equals(tags[0])){//황준석 
//				//deviceCall(did, "on", "100", "on", "100", inboundCtx, ism);
//				deviceOnOff(did, "1", "on");
//				Thread.sleep(500);
//				deviceHue(did, "1", "100");
//				Thread.sleep(5000);
//				officeOnOff(did, "office1", "on");
//				Thread.sleep(500);
//				socketOnOff(did, "power1", "on");
//				deviceHue(did, "1", "54");
//				Thread.sleep(1000);
//				deviceOnOff(did, "1", "off");
//			}else if(value.equals(tags[1])){//강대현 
//				//deviceCall(did, "on", "70", "on", "70", inboundCtx, ism);
//				deviceOnOff(did, "2", "on");
//				Thread.sleep(500);
//				deviceHue(did, "2", "70");
//				Thread.sleep(5000);
//				officeOnOff(did, "office2", "on");
//				Thread.sleep(500);
//				socketOnOff(did, "power2", "on");
//				deviceHue(did, "2", "54");
//				Thread.sleep(1000);
//				deviceOnOff(did, "2", "off");
//			}else if(value.equals(tags[2])){//이한 
//				//deviceCall(did, "on", "25", "on", "25", inboundCtx, ism);
//				deviceOnOff(did, "3", "on");
//				Thread.sleep(500);
//				deviceHue(did, "3", "25");
//				Thread.sleep(5000);
//				officeOnOff(did, "office3", "on");
//				socketOnOff(did, "power3", "on");
//				deviceHue(did, "3", "54");
//				Thread.sleep(1000);
//				deviceOnOff(did, "3", "off");
//			}else if(value.equals(tags[3])){//김태희 
//				//초기화 
//				initOpen(did);
//			}else if(value.equals(tags[4])){
//				//초기화 
//				initClose(did);
//			}else{
//				
//			}
//		}else{
//			
//		}
//		
//		return null;
//	}
	
	private void initOpen(String did) throws Exception{
		deviceHue(did, "1", "54");
		socketOnOff(did, "power1", "on");
		officeOnOff(did, "office1", "on");
		Thread.sleep(500);
		deviceHue(did, "2", "54");
		socketOnOff(did, "power2", "on");
		officeOnOff(did, "office2", "on");
		Thread.sleep(500);
		deviceHue(did, "3", "54");
		socketOnOff(did, "power3", "on");
		officeOnOff(did, "office3", "on");
		Thread.sleep(1000);
		deviceOnOff(did, "1", "on");
		deviceOnOff(did, "2", "on");
		deviceOnOff(did, "3", "on");
	}
	
	private void initClose(String did) throws Exception{
		deviceHue(did, "1", "54");
		socketOnOff(did, "power1", "off");
		officeOnOff(did, "office1", "off");
		Thread.sleep(500);
		deviceHue(did, "2", "54");
		socketOnOff(did, "power2", "off");
		officeOnOff(did, "office2", "off");
		Thread.sleep(500);
		deviceHue(did, "3", "54");
		socketOnOff(did, "power3", "off");
		officeOnOff(did, "office3", "off");
		Thread.sleep(500);
		officeOnOff(did, "room1", "off");
		Thread.sleep(500);
		officeOnOff(did, "room2", "off");
		Thread.sleep(1000);
		deviceOnOff(did, "1", "off");
		deviceOnOff(did, "2", "off");
		deviceOnOff(did, "3", "off");
	}
	
	private void deviceCall(String did, String onOffValue1, String hueValue1, String onOffValue2, String hueValue2, IContext inboundCtx, IIntegratedSessionManager ism) throws Exception{
		InnerContext request = new InnerContext();
		request.sid = did;
		request.tid = "philips.1";
//		request.tid = "com.hdbsnc.smartiot.philips.hue.1";
		request.paths = Arrays.asList("lights", "2", "on");
		request.params = new HashMap<String, String>();
		request.params.put("update", onOffValue1);
		//ism.pollAndCallContextTracer(inboundCtx, request);
		aim.handOverContext(request, null);
		
		
		request = new InnerContext();
		request.sid = did;
		request.tid = "philips.1";
//		request.tid = "com.hdbsnc.smartiot.philips.hue.1";
		request.paths = Arrays.asList("lights", "3", "on");
		request.params = new HashMap<String, String>();
		request.params.put("update", onOffValue2);
		//ism.pollAndCallContextTracer(inboundCtx, request);
		aim.handOverContext(request, null);
		
		request = new InnerContext();
		request.sid = did;
		request.tid = "philips.1";
//		request.tid = "com.hdbsnc.smartiot.philips.hue.1";
		request.paths = Arrays.asList("lights", "2", "hue");
		request.params = new HashMap<String, String>();
		request.params.put("update", hueValue1);
		//ism.pollAndCallContextTracer(inboundCtx, request);
		aim.handOverContext(request, null);
		
		request = new InnerContext();
		request.sid = did;
		request.tid = "philips.1";
//		request.tid = "com.hdbsnc.smartiot.philips.hue.1";
		request.paths = Arrays.asList("lights", "3", "hue");
		request.params = new HashMap<String, String>();
		request.params.put("update", hueValue2);
		//ism.pollAndCallContextTracer(inboundCtx, request);
		aim.handOverContext(request, null);
	}
	
	private void deviceOnOff(String did, String deviceNum, String onOffValue) throws Exception{
		InnerContext request = new InnerContext();
		request.sid = did;
		request.tid = "philips.1";
//		request.tid = "com.hdbsnc.smartiot.philips.hue.1";
		request.paths = Arrays.asList("lights", deviceNum, "on");
		request.params = new HashMap<String, String>();
		request.params.put("update", onOffValue);
		aim.handOverContext(request, null);
	}
	
	private void deviceHue(String did, String deviceNum, String hueValue) throws Exception{
		InnerContext request = new InnerContext();
		request.sid = did;
		request.tid = "philips.1";
//		request.tid = "com.hdbsnc.smartiot.philips.hue.1";
		request.paths = Arrays.asList("lights", deviceNum, "hue");
		request.params = new HashMap<String, String>();
		request.params.put("update", hueValue);
		aim.handOverContext(request, null);
	}
	
	private void officeOnOff(String did, String deviceNm, String onOffValue) throws Exception {
		InnerContext request = new InnerContext();
		request.sid = did;
		request.tid = "office.1";
		request.paths = Arrays.asList("plc", "light", deviceNm);
		request.params = new HashMap<String, String>();
		request.params.put("update", onOffValue);
		aim.handOverContext(request, null);
	}
	
	private void socketOnOff(String did, String deviceNm, String onOffValue) throws Exception {
		InnerContext request = new InnerContext();
		request.sid = did;
		request.tid = "factory.1";
//		request.tid = "com.hdbsnc.smartiot.lsis.xgtseries.factory.1";
		request.paths = Arrays.asList("plc", "socket", deviceNm);
		request.params = new HashMap<String, String>();
		request.params.put("update", onOffValue);
		aim.handOverContext(request, null);
	}
	

//	@Override
//	protected IContext create(IContext inboundCtx, ISession session, String value) throws Exception {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	protected IContext delete(IContext inboundCtx, ISession session, String value) throws Exception {
//		// TODO Auto-generated method stub
//		return null;
//	}

	@Override
	public void read(IContext inCtx, OutboundContext outCtx, ISession session, String value)
			throws ContextHandlerUnSupportedMethodException, Exception {
		throw new ContextHandlerUnSupportedMethodException(inCtx, "read");
	}

	@Override
	public void update(IContext inboundCtx, OutboundContext outboundCtx, ISession session, String value)
			throws ContextHandlerUnSupportedMethodException, Exception {
		// if(value==null || value.equals("")) throw new ContextHandlerApplicationException(2001, CommonException.TYPE_INFO, "데이터가 존재하지 않습니다.");
		if(value==null || value.equals("")) throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":005");
		
		String did = session.getDeviceId();
		if(did.equals(devices[0])){// smartiot.nfc.1  [퇴실] 
			if(value.equals(tags[0]) || value.equals(tags[5])){//황준석 
				//deviceOnOff(did, "1", "off");
//				deviceOnOff(did, "2", "off");
//				deviceOnOff(did, "3", "off");
				deviceHue(did, "1", "54");
				Thread.sleep(500);
				deviceOnOff(did, "1", "off");
				officeOnOff(did, "office1", "off");
				socketOnOff(did, "power1", "off");
			}else if(value.equals(tags[1]) || value.equals(tags[6])){//강대현 
				//deviceOnOff(did, "1", "off");
//				deviceOnOff(did, "2", "off");
//				deviceOnOff(did, "3", "off");
				deviceHue(did, "2", "54");
				Thread.sleep(500);
				deviceOnOff(did, "2", "off");
				officeOnOff(did, "office2", "off");
				socketOnOff(did, "power2", "off");
			}else if(value.equals(tags[2]) || value.equals(tags[7])){//이한 
				//deviceOnOff(did, "1", "off");
//				deviceOnOff(did, "2", "off");
//				deviceOnOff(did, "3", "off");
				deviceHue(did, "3", "54");
				Thread.sleep(500);
				deviceOnOff(did, "3", "off");
				officeOnOff(did, "office3", "off");
				socketOnOff(did, "power3", "off");
			}else if(value.equals(tags[3])){//김태희 
				//초기화 
				initClose(did);
			}else if(value.equals(tags[4])){
				initOpen(did);
			}else{
//				deviceOnOff(did, "1", "off");
//				deviceOnOff(did, "2", "off");
//				deviceOnOff(did, "3", "off");
			}
		}else if(did.equals(devices[1])){// smartiot.nfc.2  [입실]
			if(value.equals(tags[0]) || value.equals(tags[5])){//황준석 
				//deviceCall(did, "on", "100", "on", "100", inboundCtx, ism);
				deviceOnOff(did, "1", "on");
				Thread.sleep(500);
				deviceHue(did, "1", "100");
				Thread.sleep(5000);
				officeOnOff(did, "office1", "on");
				Thread.sleep(500);
				socketOnOff(did, "power1", "on");
				deviceHue(did, "1", "54");
				Thread.sleep(1000);
				deviceOnOff(did, "1", "off");
			}else if(value.equals(tags[1]) || value.equals(tags[6])){//강대현 
				//deviceCall(did, "on", "70", "on", "70", inboundCtx, ism);
				deviceOnOff(did, "2", "on");
				Thread.sleep(500);
				deviceHue(did, "2", "70");
				Thread.sleep(5000);
				officeOnOff(did, "office2", "on");
				Thread.sleep(500);
				socketOnOff(did, "power2", "on");
				deviceHue(did, "2", "54");
				Thread.sleep(1000);
				deviceOnOff(did, "2", "off");
			}else if(value.equals(tags[2]) || value.equals(tags[7])){//이한 
				//deviceCall(did, "on", "25", "on", "25", inboundCtx, ism);
				deviceOnOff(did, "3", "on");
				Thread.sleep(500);
				deviceHue(did, "3", "25");
				Thread.sleep(5000);
				officeOnOff(did, "office3", "on");
				socketOnOff(did, "power3", "on");
				deviceHue(did, "3", "54");
				Thread.sleep(1000);
				deviceOnOff(did, "3", "off");
			}else if(value.equals(tags[3])){//김태희 
				//초기화 
				initOpen(did);
			}else if(value.equals(tags[4])){
				//초기화 
				initClose(did);
			}else{
				
			}
		}else{
			
		}
		
		outboundCtx.getPaths().add("ack");
		outboundCtx.setSID(inboundCtx.getSID());
		outboundCtx.setSPort(inboundCtx.getSPort());
		outboundCtx.setTID("this");
		outboundCtx.setTPort(inboundCtx.getTPort());
		outboundCtx.setTransmission("res");
//		System.out.println("TEST : " + UrlParser.getInstance().convertToString(outboundCtx));
	}

	@Override
	public void create(IContext inCtx, OutboundContext outCtx, ISession session, String value)
			throws ContextHandlerUnSupportedMethodException, Exception {
		throw new ContextHandlerUnSupportedMethodException(inCtx, "create");
		
	}

	@Override
	public void delete(IContext inCtx, OutboundContext outCtx, ISession session, String value)
			throws ContextHandlerUnSupportedMethodException, Exception {
		throw new ContextHandlerUnSupportedMethodException(inCtx, "delete");
		
	}

}
