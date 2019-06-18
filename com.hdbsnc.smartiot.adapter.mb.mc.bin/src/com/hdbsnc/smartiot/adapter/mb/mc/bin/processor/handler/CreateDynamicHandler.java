
package com.hdbsnc.smartiot.adapter.mb.mc.bin.processor.handler;

import com.hdbsnc.smartiot.adapter.mb.mc.bin.api.MitsubishiQSeriesApi;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.api.frame.AbstractBlocksFrame.TransMode;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractTransactionTimeoutFunctionHandler;

/**
 * @author dbkim
 * 핸들러를 동적으로 생성 및 종료 한다.
 * 생성정보는 [PLC 수집시작 프로토콜] 명세서를 따른다.
 * 생성이 정상적으로 되었을 경우 RES 한다.
 */
public class CreateDynamicHandler extends AbstractTransactionTimeoutFunctionHandler {

	public CreateDynamicHandler(String name, long timeout) {
		super(name, timeout);
	}
	@Override
	public void transactionProcess(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {

		////////////////////////////////////////////////////////////////////////////////////
		//전달받은 [PLC수집 시작 프로토콜] 정보를 토대로 호출이 가능한 상태인지 확인
		////////////////////////////////////////////////////////////////////////////////////
		try {
			MitsubishiQSeriesApi api = new MitsubishiQSeriesApi(TransMode, log);
			api.connect(ip, port);

			//접속가능 상태라면 핸들러와 이벤트 매니저를 생성하여 폴링
			//핸들러 Path는 read/polling/프로토콜id/프로토콜event.id
			root.puthandler("read/polling/1/1",new ReadPollingProcessHandler(name, timeout, reqJson););
			em.putEventProcess(.............);

			JsonObject sucessJsonResult = "{result :ok.....}";

			//resHandler 호출
			Aim.handovers(sucessJsonResult));
			
		}catch(Exception e) {
			JsonObject failJsonResult = "{result :ng.....}";
			//resHandler 호출
			Aim.handover(.........);
		}
		
		
		outboundCtx.dispose();
		
		
		
		
		
	}

	@Override
	public void rejectionProcess(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		// TODO Auto-generated method stub
		
	}
}
