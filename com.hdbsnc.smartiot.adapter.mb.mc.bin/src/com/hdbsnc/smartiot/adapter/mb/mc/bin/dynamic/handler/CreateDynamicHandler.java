
package com.hdbsnc.smartiot.adapter.mb.mc.bin.dynamic.handler;

import java.util.List;

import com.hdbsnc.smartiot.adapter.mb.mc.bin.api.MitsubishiQSeriesApi;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.api.frame.AbstractBlocksFrame.TransMode;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.dynamic.handler.manager.CreateHandler;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.dynamic.handler.manager.DynamicHandlerManager;
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
	
	private List<AbstractTransactionTimeoutFunctionHandler> plcProcessHandlerList;
	private List<String> emKeyList;
	
	//아이피와 포트를 관리하며 만들어야함.!
	private MitsubishiQSeriesApi api;

	public CreateDynamicHandler(String name, long timeout, CreateHandler manager) {
		super(name, timeout);
	}
	@Override
	public void transactionProcess(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {

//		{
//			  “jsonrpc”: “2.0”,
//			  “method”: “start”,
//			  “id”: “1”,
//			  “param”: {
//			    “protocol.version”: ”1.0”,
//			    “event.id”: “event1”,
//			    “plc.ip”: “127.0.0.1”,
//			    “plc.port”: “8192”,
//			    “polling.period”: “3”,
//			    “publish.port”: ”5000”,
//			    “items”: [
//			      {
//			        “key”: “lot”,
//			        “device.code”: “D*”,
//			        “device.num”: “10000”,
//			        “device.score”: “4”
//			      },
//			      {
//			        “key”: “quality”,
//			        “device.code”: “D*”,
//			        “device.num”: “10004”,
//			        “device.score”: “1”
//			      }
//			    ]
//			  }
//			}\r\n
		
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

//			{
//				“jsonrpc” : “2.0”,
//				“id” : “1”,
//			“result” : {
//				“protocol.version” : “1.0”,
//				“event.id” : “event1”,
//				“proc.data” : “20190801 17:32:54.100”
//			},
//				“error” : {
//					“code” : -32000,
//					“message” : “PLC connection failed.”
//				}
//			}\r\n
//
			
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
