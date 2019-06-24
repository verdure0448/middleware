
package com.hdbsnc.smartiot.adapter.mb.mc.bin.dynamic.handler;

import java.util.Arrays;

import com.hdbsnc.smartiot.adapter.mb.mc.bin.dynamic.handler.manager.CreateHandler;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.dynamic.handler.manager.CreateHandler.HandlerType;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.dynamic.handler.manager.DynamicHandlerManager;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.obj.CreateParserVo;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.util.InnerContext;
import com.hdbsnc.smartiot.common.aim.IAdapterInstanceManager;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractTransactionTimeoutFunctionHandler;
import com.hdbsnc.smartiot.util.logger.Log;

/**
 * @author dbkim
 * 핸들러를 동적으로 생성 및 종료 한다.
 * 생성정보는 [PLC 수집시작 프로토콜] 명세서를 따른다.
 * 생성이 정상적으로 되었을 경우 RES 한다.
 */
public class CreateDynamicHandler extends AbstractTransactionTimeoutFunctionHandler {
	
	private CreateHandler _manager;
	private IAdapterInstanceManager _aim;
	private Log _log;
	private String _sid;
	
	public CreateDynamicHandler(String name, long timeout, String sid, DynamicHandlerManager manager, IAdapterInstanceManager aim, Log log) {
		super(name, timeout);
		
		_manager = manager;
		_aim = aim;
		_log = log.logger(this.getClass());
		_sid = sid;
		
		System.out.println("CREATE DYNAMIC HANDLER");
	}
	@Override
	public void transactionProcess(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
//		{"jsonrpc":"2.0","method":"start","id":"1","param":{"version":"1.0","id":"event1","ip":"127.0.0.1","port":"8192","period":"3","items":[{"key":"lot","code":"D*","num":"10000","score":"4"},{"key":"quality","code":"D*","num":"10004","score":"1"}]}}

		//호출한 상대의 tid 및 path를 가지고 옴 
		//호출자의 tid 및 path인지 확인필요
		//호출자의 tid 및 path가 아니라면 아래 주석 과정 수행
		String reqTid = inboundCtx.getTID();
		String reqPath = inboundCtx.getFullPath();
		String jsonContents = new String(inboundCtx.getContent().array(), "UTF-8");
		
		try {
			CreateParserVo vo = new CreateParserVo(jsonContents);
	
			//경로를 만들어 준다.
			//read/polling/프로토콜id/프로토콜event.id
			StringBuffer sbPath = new StringBuffer();
			sbPath.append("read/");
			sbPath.append("polling/");
			sbPath.append(vo.getId());
			sbPath.append("/");
			sbPath.append(vo.getParam().getEventId());
			
			String path = sbPath.toString();
	
			int iIntervalSec = Integer.parseInt(vo.getParam().getPeriod());
			String sIP = vo.getParam().getIp();
			int iPort = Integer.parseInt(vo.getParam().getPort());
			
			_manager.start(HandlerType.READ_BATCH_PROCESS_HANDLER, path, sIP, iPort, iIntervalSec);

			
			//정상 Start 후 응답
			InnerContext request = new InnerContext();
			request.sid = _sid;
			request.tid = reqTid;
			request.paths = Arrays.asList(reqPath.split("/"));
			_aim.handOverContext(request, null);	
			
			_log.debug("handover : " + reqTid + " path : " + reqPath);
			
		}catch(Exception e) {
			//비정상 Start 후 응답
			InnerContext request = new InnerContext();
			request.sid = _sid;
			request.tid = reqTid;
			request.paths = Arrays.asList(reqPath.split("/"));
			_aim.handOverContext(request, null);	
			
			_log.debug("handover : " + reqTid + " path : " + reqPath);
		
		}
	}

	@Override
	public void rejectionProcess(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		// TODO Auto-generated method stub
		
	}
}
