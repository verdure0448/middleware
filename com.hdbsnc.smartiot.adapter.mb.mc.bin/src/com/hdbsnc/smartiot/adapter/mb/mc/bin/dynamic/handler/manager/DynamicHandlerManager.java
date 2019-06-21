package com.hdbsnc.smartiot.adapter.mb.mc.bin.dynamic.handler.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.hdbsnc.smartiot.adapter.mb.mc.bin.api.MitsubishiQSeriesApi;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.obj.JsonToPlcVoParser;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.processor.handler.BatchProcessHandler;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.processor.handler.BlockBatchProcessHandler;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.processor.handler.ReadBatchProcessHandler;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.processor.handler.ReadBlockBatchProcessHandler;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.processor.handler.WriteBatchProcessHandler;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.processor.handler.WriteBlockBatchProcessHandler;
import com.hdbsnc.smartiot.common.context.handler.exception.ElementNullOrEmptyPathException;
import com.hdbsnc.smartiot.common.context.handler.exception.ElementUnSupportedInstanceException;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractTransactionTimeoutFunctionHandler;
import com.hdbsnc.smartiot.common.context.handler2.impl.RootHandler;
import com.hdbsnc.smartiot.common.em.IEventManager;
import com.hdbsnc.smartiot.util.logger.Log;

/**
 * @author dbkim
 * 핸들러를 동적으로 생성, 제거, 폴링한다.
 */
public class DynamicHandlerManager implements CreateHandler, RemoveHandler, StatusHandler, StartPolling{
	
	//핸들러를 저장하기 위한 핸들러
	private List<AbstractTransactionTimeoutFunctionHandler> _handlerList;
	//이벤트 매니저의 키를 저장
	private List<String> _emKeyList;
	
	private IEventManager _em;
	private RootHandler _root;
	
	private Log _log;
	
	private MitsubishiQSeriesApi api;
	
	
	public DynamicHandlerManager(RootHandler root, IEventManager em, Log log) {

		_handlerList = new ArrayList<AbstractTransactionTimeoutFunctionHandler>();
		_emKeyList = new ArrayList<>();
		
		_em = em;
		_root = root;
		_log = log;
		
	}
	
	@Override
	public void start(int kind, String path) {

	
	}

	@Override
	public void start(int kind, String path, String json) throws Exception {
		
		String sPath = getHandlerPath(path);
		String sName = getHandlerName(path);
		
		JsonToPlcVoParser parser = new JsonToPlcVoParser(json);
				
		switch(kind) {
		case CreateHandler.READ_BATCH_PROCESS_HANDLER:
			_root.putHandler(sPath, new ReadBatchProcessHandler(sName, 3000, api, parser.getRequestReadObjList(), _log));
			break;
		case CreateHandler.READ_BLOCK_BATCH_PROCESS_HANDLER:
			_root.putHandler(sPath, new ReadBlockBatchProcessHandler(sName, 3000, api, parser.getRequestReadObjList(), _log));
			break;
		case CreateHandler.WRITE_BATCH_PROCESS_HANDLER:
			_root.putHandler(sPath, new WriteBatchProcessHandler(sName, 3000, api, parser.getRequestWriteObjList(), _log));
			break;
		case CreateHandler.WRITE_BLOCK_BATCH_PROCESS_HANDLER:
			_root.putHandler(sPath, new WriteBlockBatchProcessHandler(sName, 3000, api, parser.getRequestWriteObjList(), _log));
			break;
		case CreateHandler.READ_WRITE_BATCH_PROCESS_HANDLER:
			_root.putHandler(sPath, new BatchProcessHandler(sName, 3000, api, parser.getRequestReadObjList(), parser.getRequestWriteObjList(), _log));
			break;
		case CreateHandler.READ_WRITE_BLOCK_BATCH_PROCESS_HANDLER:
			_root.putHandler(sPath, new BlockBatchProcessHandler(sName, 3000, api, parser.getRequestReadObjList(), parser.getRequestWriteObjList(), _log));
			break;
		default :
			throw new Exception("지원하지 않는 핸들러입니다.");
		}
	}

	private String getHandlerName(String path) {
		
		int length = path.split("/").length;
		String result = path.split("/")[length];
		
		return result;
	}

	private String getHandlerPath(String path) {
		String result = "";
		
		for(int i=0; i<path.split("/").length-1; i++) {
			if(i != 0) {
				result = "/";
			}
			result += path.split("/")[i]; 
		}
		return result;
	}

	@Override
	public void status() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void remove(String path) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeAll() {
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
	public void startPolling() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stopPolling() {
		// TODO Auto-generated method stub
		
	}
}
