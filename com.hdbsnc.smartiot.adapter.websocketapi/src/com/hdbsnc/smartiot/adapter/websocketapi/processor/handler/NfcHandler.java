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

public class NfcHandler extends AbstractAttributeHandler{
			
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
	
	public NfcHandler(IAdapterInstanceManager aim) {
		super("nfc");
		this.aim = aim;
	}


	
	private void initOpen(String did) throws Exception{

		socketOnOff(did, "con" ,"air", "on");
		Thread.sleep(100);
		socketOnOff(did, "socket","power1", "on"); 
		Thread.sleep(100);
		socketOnOff(did, "con","fan", "on");
		Thread.sleep(100);
		socketOnOff(did, "socket","power2", "on");


	}
	
	private void initClose(String did) throws Exception{
		
		socketOnOff(did, "con" ,"air", "off");
		Thread.sleep(100);
		socketOnOff(did, "socket","power1", "off"); 
		Thread.sleep(100);
		socketOnOff(did, "con","fan", "off");
		Thread.sleep(100);
		socketOnOff(did, "socket","power2", "off");

	}
	
	private void socketOnOff(String did,String path ,String deviceNm, String onOffValue) throws Exception {
		InnerContext request = new InnerContext();
		request.sid = did;
		request.tid = "factory.1";
		request.paths = Arrays.asList("plc", path, deviceNm);
		request.params = new HashMap<String, String>();
		request.params.put("update", onOffValue);
		aim.handOverContext(request, null);
	}

	@Override
	public void read(IContext inCtx, OutboundContext outCtx, ISession session, String value)
			throws ContextHandlerUnSupportedMethodException, Exception {
		throw new ContextHandlerUnSupportedMethodException(inCtx, "read");
	}

	@Override
	public void update(IContext inboundCtx, OutboundContext outboundCtx, ISession session, String value)
			throws ContextHandlerUnSupportedMethodException, Exception {
		if(value==null || value.equals("")) throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":005");
		
		String did = session.getDeviceId();
		if(did.equals(devices[0])){// smartiot.nfc.1  [퇴실] 
			if(value.equals(tags[0]) || value.equals(tags[5])){//황준석 
				socketOnOff(did, "con" ,"air", "off");
				socketOnOff(did, "socket","power1", "off");
			}else if(value.equals(tags[1]) || value.equals(tags[6])){//강대현 
				socketOnOff(did, "con","fan", "off");
				socketOnOff(did, "socket","power2", "off");
			}
			else if(value.equals(tags[3])){//김태희 
				//초기화 
				initClose(did);
			}else if(value.equals(tags[4])){
				initOpen(did);
			}else{

			}
		}
		else if(did.equals(devices[1])){// smartiot.nfc.2  [입실]
			if(value.equals(tags[0]) || value.equals(tags[5])){//황준석
				socketOnOff(did, "con" ,"air", "on");
				socketOnOff(did, "socket","power1", "on"); 
			}else if(value.equals(tags[1]) || value.equals(tags[6])){//강대현
				socketOnOff(did, "con","fan", "on");
				socketOnOff(did, "socket","power2", "on");
			}
			else if(value.equals(tags[3])){//김태희 
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
