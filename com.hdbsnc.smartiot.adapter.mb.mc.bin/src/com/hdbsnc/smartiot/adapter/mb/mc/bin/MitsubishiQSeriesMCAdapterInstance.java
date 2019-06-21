
package com.hdbsnc.smartiot.adapter.mb.mc.bin;

import java.util.List;

import com.hdbsnc.smartiot.adapter.mb.mc.bin.dynamic.handler.CreateDynamicHandler;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.dynamic.handler.DeleteDynamicHandler;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.dynamic.handler.StatusDynamicHandler;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.dynamic.handler.manager.DynamicHandlerManager;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.obj.JsonToPlcVoParser;
import com.hdbsnc.smartiot.common.ICommonService;
import com.hdbsnc.smartiot.common.aim.IAdapterContext;
import com.hdbsnc.smartiot.common.aim.IAdapterInstance;
import com.hdbsnc.smartiot.common.aim.IAdapterProcessor;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractTransactionTimeoutFunctionHandler;
import com.hdbsnc.smartiot.common.context.handler2.impl.RootHandler;
import com.hdbsnc.smartiot.common.em.IEventManager;
import com.hdbsnc.smartiot.common.ism.sm.ISession;
import com.hdbsnc.smartiot.common.pm.IProfileManager;
import com.hdbsnc.smartiot.common.pm.vo.IInstanceFunctionObj;
import com.hdbsnc.smartiot.common.pm.vo.IInstanceObj;
import com.hdbsnc.smartiot.util.logger.Log;

public class MitsubishiQSeriesMCAdapterInstance implements IAdapterInstance {

	private ICommonService service;
	private Log log;
	private MitsubishiQSeriesMCAdapterProcessor processor = null;
	private IEventManager em;

	private DynamicHandlerManager manager;
	
	public MitsubishiQSeriesMCAdapterInstance(ICommonService service, IEventManager em, IProfileManager pm) {

		this.service = service;
		this.em = em;
	}

	@Override
	public void initialize(IAdapterContext ctx) throws Exception {

		log = service.getLogger().logger(ctx.getAdapterInstanceInfo().getInsId());
		log.info("initialize");
		this.processor = new MitsubishiQSeriesMCAdapterProcessor(service, ctx);
	}

	@Override
	public void start(IAdapterContext ctx) throws Exception {
		log.info("start");

		IInstanceObj instanceInfo = ctx.getAdapterInstanceInfo();

		String userId = instanceInfo.getSelfId();
		String upass = instanceInfo.getSelfPw();
		String did = instanceInfo.getDefaultDevId();

		ISession session = ctx.getSessionManager().certificate(did, userId, upass);

		RootHandler root = this.processor.getRootHandler();
		manager = new DynamicHandlerManager(root, em, did, session.getSessionKey(),log);
		
		//멜섹 프로토콜 핸들러를 동적으로 생성한다
		root.putHandler("create/mb/melsec", new CreateDynamicHandler("handler", 3000, manager));
		//멜섹 프로토콜 핸들러를  삭제한다.
		root.putHandler("delete/mb/melsec", new DeleteDynamicHandler("handler", 3000, manager));
		//멜섹 핸들러의 상태를 확인한다.
		root.putHandler("status/mb/melsec", new StatusDynamicHandler("handler", 3000, manager));
	}

	@Override
	public void stop(IAdapterContext ctx) throws Exception {

	}

	@Override
	public void suspend(IAdapterContext ctx) throws Exception {

	}

	@Override
	public void dispose(IAdapterContext ctx) throws Exception {

	}

	@Override
	public IAdapterProcessor getProcessor() {
		return this.processor;
	}

}
