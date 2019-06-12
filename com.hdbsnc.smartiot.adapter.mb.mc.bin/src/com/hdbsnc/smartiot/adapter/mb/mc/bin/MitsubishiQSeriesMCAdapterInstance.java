
package com.hdbsnc.smartiot.adapter.mb.mc.bin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.hdbsnc.smartiot.adapter.mb.mc.bin.api.MitsubishiQSeriesApi;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.api.frame.AbstractBlocksFrame.TransMode;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.obj.FunctionParamParser;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.obj.RequestWriteObj;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.processor.handler.BatchPollingHealthcheckHandler;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.processor.handler.BatchPollingProcessHandler;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.processor.handler.BlcokBatchPollingHealthcheckHandler;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.processor.handler.BlcokBatchPollingProcessHandler;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.processor.handler.WriteBatchProcessHandler;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.processor.handler.WriteProcessHandler;
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
	private MitsubishiQSeriesApi api;
	private IProfileManager pm;

	private ISession session = null;

	private List<AbstractTransactionTimeoutFunctionHandler> plcProcessHandlerList;
	private List<String> emKeyList;
	
	private static final String MELSEC_MC_PROTOCOL_BATCH_TYPE = "Batch";
	private static final String MELSEC_MC_PROTOCOL_BLOCK_BATCH_TYPE = "BlockBatch";
	private static final String MELSEC_MC_PROTOCOL_BLOCK_BATCH_TYPE_HEALTH_CHECK = "BlockBatchHealthCheck";
	private static final String MELSEC_MC_PROTOCOL_BATCH_TYPE_HEALTH_CHECK = "BatchHealthCheck";
	private static final String MELSEC_MC_PROTOCOL_BLOCK_BATCH_TYPE_WRITE = "BlockBatchWrite";
	private static final String MELSEC_MC_PROTOCOL_BATCH_TYPE_WRITE = "BatchWrite";
	
	private static final String POLLING_HANDLER_TYPE = "Polling";
	private static final String STATIC_HANDLER_TYPE = "Static";

	public MitsubishiQSeriesMCAdapterInstance(ICommonService service, IEventManager em, IProfileManager pm) {

		this.service = service;
		this.em = em;
		this.pm = pm;

		plcProcessHandlerList = new ArrayList<AbstractTransactionTimeoutFunctionHandler>();
		emKeyList = new ArrayList<>();
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

		if(this.api == null) {
			this.api = new MitsubishiQSeriesApi(TransMode.BINARY, log);	
		}		
		IInstanceObj instanceInfo = ctx.getAdapterInstanceInfo();
		String ip = instanceInfo.getIp();
		String ports =  instanceInfo.getPort();
		if (ports == null || ports.equals("")) {
			throw new Exception("Invalidate Port.");
		}

		String userId = instanceInfo.getSelfId();
		String upass = instanceInfo.getSelfPw();
		String defaultDid = instanceInfo.getDefaultDevId();

		session = ctx.getSessionManager().certificate(defaultDid, userId, upass);
		try {
			this.api.connect(ip, ports);
		} catch (Exception e) {
			if (this.api != null) {
				this.api.disconnect();
			}
			throw e;
		}

		RootHandler root = this.processor.getRootHandler();

		List<IInstanceFunctionObj> funclist = pm.getInstanceFunctionList(instanceInfo.getInsId());
		String functionKey;
		for (IInstanceFunctionObj funcObj : funclist) {
			// 모든정보를 가지고 옴
			functionKey = funcObj.getKey();
			putHandler(root, functionKey, funcObj.getParam1());
			putHandler(root, functionKey, funcObj.getParam2());
			putHandler(root, functionKey, funcObj.getParam3());
			putHandler(root, functionKey, funcObj.getParam4());
			putHandler(root, functionKey, funcObj.getParam5());
		}
	}
	
	/**
	 * json 데이터를 가지고 와서 핸들러에 등록 및 EM을 등록 한다.
	 * 
	 * @param root
	 * @param jsonData
	 * @throws Exception 
	 */
	private void putHandler(RootHandler root, String key, String jsonData) throws Exception {
		FunctionParamParser dataObj = null;

		// json 데이터가 null이 아니고
		if (jsonData != null) {
			// json 데이터가 비어있지 않으면
			if (!("".equals(jsonData))) {

				dataObj = new FunctionParamParser(jsonData);
				try {
					dataObj.parser();
				} catch (Exception e) {
 					log.err(e);
					throw e;
				} 

				String handlerType = dataObj.getHandlerType();
				String name = dataObj.getOperation();
				String emKey = key + "/" + name;

				try {
					if (!(root.contains(emKey))) {
						
						if(handlerType.equals(POLLING_HANDLER_TYPE)) {
					
							AbstractTransactionTimeoutFunctionHandler handler = getHandler(name, dataObj); 
							
							// 핸들러를 저장해둠
							plcProcessHandlerList.add(handler);
							// em을 등록할 키를 저장함
							emKeyList.add(emKey);

							// 핸들러를 List에 닮아 둠
							root.putHandler(key, handler);
							log.info("[Handler] : " + emKey + " 등록");

							// em을 등록함.
							em.addPollingAdapterProcessorEvent(emKey, session.getSessionKey(), session.getDeviceId(), emKey, null, Integer.parseInt(dataObj.getInterval()));
							log.info("[EventManager] : " + emKey + " 등록");
						} else if(handlerType.equals(STATIC_HANDLER_TYPE)) {
							
							AbstractTransactionTimeoutFunctionHandler handler = getHandler(name, dataObj); 

							plcProcessHandlerList.add(handler);
							root.putHandler(key, handler);
							log.info("[Handler] : " + emKey + " 등록");
							
						} else {
							throw new Exception("존재하지 않는 핸들러 타입입니다.");
						}
					} else {
						throw new Exception("이미 존재하는 키이므로 다음을 진행합니다.");
					}

				} catch (Exception e) {
					log.err(e);
				}
			}
		}
	}
	
	/**
	 * Handler에 대한 클래스를 리턴한다.
	 * 
	 * @param name
	 * @param dataObj
	 * @return
	 * @throws Exception
	 */
	private AbstractTransactionTimeoutFunctionHandler getHandler(String name, FunctionParamParser dataObj) throws Exception {

		AbstractTransactionTimeoutFunctionHandler handler = null;
		
		//protocol이 null이라면 블록배치를 사용하도록 함.1차년도와 호환성을 맞추기 위해 적용
		if(dataObj.getProtocolType()==null) {
			handler = new BlcokBatchPollingProcessHandler(name, 3000, api, dataObj.getRequestReadObjList(), dataObj.getRequestWriteObjList(), dataObj.getJsonSchema(),log);
		}else if(MELSEC_MC_PROTOCOL_BLOCK_BATCH_TYPE.equals(dataObj.getProtocolType()))	{
			handler = new BlcokBatchPollingProcessHandler(name, 3000, api, dataObj.getRequestReadObjList(), dataObj.getRequestWriteObjList(), dataObj.getJsonSchema(),log);
		}else if(MELSEC_MC_PROTOCOL_BATCH_TYPE.equals(dataObj.getProtocolType())) {
			handler = new BatchPollingProcessHandler(name, 3000, api, dataObj.getRequestReadObjList(), dataObj.getRequestWriteObjList(), dataObj.getJsonSchema(),log);
		}else if(MELSEC_MC_PROTOCOL_BLOCK_BATCH_TYPE_HEALTH_CHECK.equals(dataObj.getProtocolType())) {
			handler = new BlcokBatchPollingHealthcheckHandler(name, 3000, api, dataObj.getRequestReadObjList(), dataObj.getRequestWriteObjList(), dataObj.getJsonSchema(),log);
		}else if(MELSEC_MC_PROTOCOL_BATCH_TYPE_HEALTH_CHECK.equals(dataObj.getProtocolType())) {
			handler = new BatchPollingHealthcheckHandler(name, 3000, api, dataObj.getRequestReadObjList(), dataObj.getRequestWriteObjList(), dataObj.getJsonSchema(),log);
		}else if(MELSEC_MC_PROTOCOL_BLOCK_BATCH_TYPE_WRITE.equals(dataObj.getProtocolType())) {
			handler = new WriteProcessHandler(name, 3000, api, log);
		}else if(MELSEC_MC_PROTOCOL_BATCH_TYPE_WRITE.equals(dataObj.getProtocolType())) {
			handler = new WriteBatchProcessHandler(name, 3000, api, log);
		}else {
			throw new Exception("지원하지 않는 프로토콜입니다.");
		}

		return handler;
	}

	@Override
	public void stop(IAdapterContext ctx) throws Exception {
		log.info("stop");
		if (this.api != null) {
			try {
				api.disconnect();
			} catch (IOException e) {
				log.err(e);
			}
		}

		// 컨슈머 해제
		for (String emKey : emKeyList) {
			if (em.containPollingAdapterProcessor(emKey)) {
				em.removePollingAdapterProcessorEvent(emKey);

				log.info("[EventManager] : " + emKey + " 해제");
			}
		}

		// 핸들러 해제
		for (AbstractTransactionTimeoutFunctionHandler handler : plcProcessHandlerList) {
			this.processor.getRootHandler().removeHandler(handler);
			log.info("[Handler] : " + handler.getName() + " 해제");
		}

		emKeyList.clear();
		plcProcessHandlerList.clear();
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
