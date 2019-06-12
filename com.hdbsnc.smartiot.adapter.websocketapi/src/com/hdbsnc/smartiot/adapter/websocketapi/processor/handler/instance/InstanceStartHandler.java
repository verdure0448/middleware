package com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.instance;

import com.hdbsnc.smartiot.adapter.websocketapi.constant.WebSocketAdapterConst;
import com.hdbsnc.smartiot.common.aim.AimException;
import com.hdbsnc.smartiot.common.aim.IAdapterInstanceContainer;
import com.hdbsnc.smartiot.common.aim.IAdapterInstanceEvent;
import com.hdbsnc.smartiot.common.aim.IAdapterInstanceManager;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractFunctionHandler;

/**
 * ins/start
 * 
 * @author KANG
 *
 */
public class InstanceStartHandler extends AbstractFunctionHandler {

	private IAdapterInstanceManager aim;

	public InstanceStartHandler(IAdapterInstanceManager aim) {
		super("start");
		this.aim = aim;
	}

	@Override
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		String iid = inboundCtx.getParams().get(WebSocketAdapterConst.IID);
//		String sWaitTime = inboundCtx.getParams().get("wait.time");
//		int iWaitTime = 0;
//		
//		if(sWaitTime == null){
//			iWaitTime = 10000;
//		}else{
//			iWaitTime = Integer.valueOf(sWaitTime);
//		}
		try {
			aim.start(iid);
			
//			int cnt = 0;
//			// 인스턴스 상태 조회
//			while(true){
//				IAdapterInstanceContainer iAic = aim.getAdapterInstance(iid);
//				
//				Thread.sleep(100);
//				if(iAic != null && IAdapterInstanceEvent.START_EVENT == iAic.getLastEvent().getEventType()){
//					break;
//				}
//				cnt = cnt + 1;
//				if(cnt*100 > iWaitTime) {
//					throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":005", new String[]{iid},
//							null);
//				}
//			}
		} catch (AimException aimEx) {
			// throw new ContextHandlerApplicationException(304,
			// CommonException.TYPE_ERROR, "인스턴스 기동에 실패 했습니다.", aimEx);
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":005", new String[]{iid, aimEx.getMessage()},
					aimEx);
		}

		outboundCtx.getPaths().add("ack");
		outboundCtx.setSID(inboundCtx.getSID());
		outboundCtx.setSPort(inboundCtx.getSPort());
		outboundCtx.setTID("this");
		outboundCtx.setTPort(inboundCtx.getTPort());
		outboundCtx.setTransmission("res");

	}

}
