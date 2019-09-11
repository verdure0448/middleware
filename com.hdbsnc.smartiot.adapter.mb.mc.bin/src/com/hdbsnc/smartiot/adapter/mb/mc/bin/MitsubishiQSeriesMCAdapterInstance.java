
package com.hdbsnc.smartiot.adapter.mb.mc.bin;

import java.util.ArrayList;
import java.util.List;

import com.hdbsnc.smartiot.adapter.mb.mc.bin.handler.CreateRequestHandler;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.handler.DeleteAllRequestHandler;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.handler.DeleteRequestHandler;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.handler.RunningStatusCheckHandler;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.handler.manager.DynamicHandlerManager;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.processor.handler.ReadOnceProcessHandler;
import com.hdbsnc.smartiot.common.ICommonService;
import com.hdbsnc.smartiot.common.aim.IAdapterContext;
import com.hdbsnc.smartiot.common.aim.IAdapterInstance;
import com.hdbsnc.smartiot.common.aim.IAdapterInstanceManager;
import com.hdbsnc.smartiot.common.aim.IAdapterProcessor;
import com.hdbsnc.smartiot.common.context.handler2.impl.RootHandler;
import com.hdbsnc.smartiot.common.em.IEventManager;
import com.hdbsnc.smartiot.common.ism.sm.ISession;
import com.hdbsnc.smartiot.common.pm.IProfileManager;
import com.hdbsnc.smartiot.common.pm.vo.IInstanceObj;
import com.hdbsnc.smartiot.util.logger.Log;

public class MitsubishiQSeriesMCAdapterInstance implements IAdapterInstance {

	private ICommonService _service;
	private Log _log;
	private MitsubishiQSeriesMCAdapterProcessor _processor = null;
	private IEventManager _em;

	private DynamicHandlerManager _manager;
	
	private List<String> _handlerList;

	public MitsubishiQSeriesMCAdapterInstance(ICommonService service, IEventManager em, IProfileManager pm) {

		_service = service;
		_em = em;
		
		_handlerList = new ArrayList<>();
	}

	@Override
	public void initialize(IAdapterContext ctx) throws Exception {

		_log = _service.getLogger().logger(ctx.getAdapterInstanceInfo().getInsId());
		_log.info("initialize");
		_processor = new MitsubishiQSeriesMCAdapterProcessor(_service, ctx);
	}

	@Override
	public void start(IAdapterContext ctx) throws Exception {
		_log.info("start");
		IAdapterInstanceManager aim = ctx.getAdapterInstanceManager();

		IInstanceObj instanceInfo = ctx.getAdapterInstanceInfo();

		String userId = instanceInfo.getSelfId();
		String upass = instanceInfo.getSelfPw();
		String did = instanceInfo.getDefaultDevId();

		ISession session = ctx.getSessionManager().certificate(did, userId, upass);

		RootHandler root = _processor.getRootHandler();
		String sid = session.getSessionKey();

		_manager = new DynamicHandlerManager(root, aim, _em, did, sid, _log);
		
		// 멜섹 프로토콜 핸들러를 동적으로 생성한다
		root.putHandler("create/mb/melsec", new CreateRequestHandler("handler", 3000, _manager, _log));
		// 멜섹 프로토콜 핸들러를 삭제한다.
		root.putHandler("delete/mb/melsec", new DeleteRequestHandler("handler", 3000, _manager, _log));
		// 멜섹 프로토콜 핸들러를 전체삭제한다.
		root.putHandler("delete/all/mb/melsec", new DeleteAllRequestHandler("handler", 3000, _manager, _log));
		// 멜섹 핸들러의 상태를 확인한다.
		root.putHandler("status/mb/melsec", new RunningStatusCheckHandler("handler", 3000, _manager, _log));
		// PLC 데이터 읽기 전용
		root.putHandler("readonce/mb/melsec", new ReadOnceProcessHandler("handler", 3000, _manager, _log));
		root.printString();
		
		//Stop시 삭제할 핸들러 명 저장.
		_handlerList.add("create/mb/melsec/handler");
		_handlerList.add("delete/mb/melsec/handler");
		_handlerList.add("delete/all/mb/melsec/handler");
		_handlerList.add("status/mb/melsec/handler");
		_handlerList.add("readonce/mb/melsec/handler");
	}

	@Override
	public void stop(IAdapterContext ctx) throws Exception {

		RootHandler root = _processor.getRootHandler();

		//등록된 핸들러 삭제
		for(int i=0; i<_handlerList.size(); i++) {
			String handlerPath = _handlerList.get(i);
			if (root.findHandler(handlerPath) != null) {
				root.deleteHandler(handlerPath.split("/"));
			}
		}
	}

	@Override
	public void suspend(IAdapterContext ctx) throws Exception {

	}

	@Override
	public void dispose(IAdapterContext ctx) throws Exception {

	}

	@Override
	public IAdapterProcessor getProcessor() {
		return _processor;
	}

}
