
package com.hdbsnc.smartiot.adapter.mb.mc.bin.dynamic.handler;

import java.util.List;

import com.hdbsnc.smartiot.adapter.mb.mc.bin.dynamic.handler.manager.DeleteHandler;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractTransactionTimeoutFunctionHandler;

/**
 * @author dbkim
 * 핸들러를 동적으로 생성 및 종료 한다.
 * 생성정보는 [PLC 수집시작 프로토콜] 명세서를 따른다.
 * 생성이 정상적으로 되었을 경우 RES 한다.
 */
public class DeleteDynamicHandler extends AbstractTransactionTimeoutFunctionHandler {
	
	private List<AbstractTransactionTimeoutFunctionHandler> plcProcessHandlerList;
	private List<String> emKeyList;
	
	public DeleteDynamicHandler(String name, long timeout, DeleteHandler manager) {
		super(name, timeout);
	}
	@Override
	public void transactionProcess(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {

//		{
//			“jsonrpc” : “2.0”,
//			“method” : “stop.part”,
//			“id” : “1”,
//			“param” : {
//				“protocol.version” :”1.0”,
//				“event.id” : “event1”,
//			}
//		}\r\n

//		{
//			“jsonrpc” : “2.0”,
//			“method” : “stop.all”,
//			“id” : {“type” : “string”},
//			“param” : {
//				“protocol.version” : {“type” : “string”},
//			}
//		}\r\n


		
		
		////////////////////////////////////////////////////////////////////////////////////
		// 전달받은 [PLC수집 중지 프로토콜] 정보를 토대로 전체 인지 일부인지 확인 후 
		// rootHandler에 동적등록된 핸들러를 찾아 제거 한다
		// PATH : read/polling/프로토콜id/프로토콜event.id
		////////////////////////////////////////////////////////////////////////////////////
		try {
			em.removeEventProcess();
			root.removeHandler();
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

//			{
//				“jsonrpc” : “2.0”,
//				“id” : {“type” : “string”},
//				“result” : {
//					“protocol.version” : {“type” : “string”}, 
//					“stop.all” : [
//						{
//							“event.id” : {“type” : “string”},
//							“plc.ip” : {“type” : “string”},
//							“plc.port” : {“type” : “string”},
//							“polling.period” : {“type” : “string”},
//							“publish.port” : {“type” : “string”}
//						}
//					]
//				},
//				“error” : {
//					“code” : {“type” : “number”},
//					“message” : {“type” : “string”}
//				}
//			}\r\n

			
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
