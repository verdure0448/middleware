package com.hdbsnc.smartiot.adapter.mb.mc.bin.dynamic.handler.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.hdbsnc.smartiot.adapter.mb.mc.bin.api.MitsubishiQSeriesApi;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.api.frame.AbstractBlocksFrame.TransMode;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.obj.CreateParserVo;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.processor.handler.BatchProcessHandler;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.processor.handler.BlockBatchProcessHandler;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.processor.handler.ReadBatchProcessHandler;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.processor.handler.ReadBlockBatchProcessHandler;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.processor.handler.WriteBatchProcessHandler;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.processor.handler.WriteBlockBatchProcessHandler;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractTransactionTimeoutFunctionHandler;
import com.hdbsnc.smartiot.common.context.handler2.impl.RootHandler;
import com.hdbsnc.smartiot.common.em.IEventManager;
import com.hdbsnc.smartiot.util.logger.Log;

/**
 * @author dbkim 핸들러를 동적으로 생성, 제거, 폴링한다.
 */
public class DynamicHandlerManager implements CreateHandler, DeleteHandler, StatusHandler, StartPolling {

	// 핸들러를 저장하기 위한 핸들러
	private Map<String, AbstractTransactionTimeoutFunctionHandler> _handlerMap;
	// 이벤트 매니저의 키를 저장
	private List<String> _emKeyList;
	private Map<String, MitsubishiQSeriesApi> _apiMap;

	private IEventManager _em;
	private RootHandler _root;

	private Log _log;

	private MitsubishiQSeriesApi api;

	private String _did;
	private String _sid;

	public DynamicHandlerManager(RootHandler root, IEventManager em, String did, String sid, Log log) {

		_handlerMap = new Hashtable<String, AbstractTransactionTimeoutFunctionHandler>();
		_apiMap = new Hashtable<String, MitsubishiQSeriesApi>();
		_emKeyList = new ArrayList<>();

		_em = em;
		_root = root;
		_log = log;

		_did = did;
		_sid = sid;
	}

	@Override
	public void start(HandlerType kind, String path, String ip, int port, int intervalSec) throws Exception{

		String sHandlerPath = getHandlerPath(path);
		String sHandlerName = getHandlerName(path);
		String sEemKey = path;
	
		// 이미 만들어진 IP 및 PORT가 있는지 확인
		if (isConnection(ip, port)) {
			throw new Exception("이미 기동 중인 IP : " + ip + " Port : " + port + " 입니다.");
		}
		// 만들어진 IP 및 PORT가 없다면 생성
		MitsubishiQSeriesApi api = new MitsubishiQSeriesApi(TransMode.BINARY, _log);
		
		//연결의 문제가 있을 경우 connection refuser로 Throw 발생
		api.connect(ip, port);

		switch (kind) {
		case READ_BATCH_PROCESS_HANDLER:
			_root.putHandler(sHandlerPath,
					new ReadBatchProcessHandler(sHandlerName, 3000, api, parser.getRequestReadObjList(), _log));
			break;
		case READ_BLOCK_BATCH_PROCESS_HANDLER:
			_root.putHandler(sHandlerPath,
					new ReadBlockBatchProcessHandler(sHandlerName, 3000, api, parser.getRequestReadObjList(), _log));
			break;
		case WRITE_BATCH_PROCESS_HANDLER:
			_root.putHandler(sHandlerPath,
					new WriteBatchProcessHandler(sHandlerName, 3000, api, parser.getRequestWriteObjList(), _log));
			break;
		case WRITE_BLOCK_BATCH_PROCESS_HANDLER:
			_root.putHandler(sHandlerPath,
					new WriteBlockBatchProcessHandler(sHandlerName, 3000, api, parser.getRequestWriteObjList(), _log));
			break;
		case READ_WRITE_BATCH_PROCESS_HANDLER:
			_root.putHandler(sHandlerPath, new BatchProcessHandler(sHandlerName, 3000, api,
					parser.getRequestReadObjList(), parser.getRequestWriteObjList(), _log));
			break;
		case READ_WRITE_BLOCK_BATCH_PROCESS_HANDLER:
			_root.putHandler(sHandlerPath, new BlockBatchProcessHandler(sHandlerName, 3000, api,
					parser.getRequestReadObjList(), parser.getRequestWriteObjList(), _log));
			break;
		default:
			throw new Exception("지원하지 않는 핸들러입니다.");
		}

		startPolling(sEemKey, iIntervalSec);

		_root.printString();
	}

	private boolean isConnection(String sIP, int iPort) {

		Iterator it = (Iterator) _apiMap.keySet();
		String key;
		MitsubishiQSeriesApi api;
		while (it.hasNext()) {
			key = (String) it.next();
			api = _apiMap.get(key);

			if (api.getIp().equals(sIP) && api.getPort() == iPort) {
				return true;
			}
		}

		return false;
	}

	@Override
	public void status() {

	}

	@Override
	public void delete(String path) throws IOException {

		if (_root.contains(path)) {
			_root.removeHandler(_handlerMap.get(path));
			_log.info("[Handler] : " + path + " 해제");
		}

		MitsubishiQSeriesApi api;
		// 키가 존재
		if (_apiMap.containsKey(path)) {
			api = _apiMap.get(path);
			if (api.isConnected()) {
				api.disconnect();
				api = null;
			}
			_apiMap.remove(path);
		}

		stopPolling(path);
		_root.printString();
	}

	@Override
	public void deleteAll() throws IOException {

		// 키가 존재
		Iterator it = (Iterator) _apiMap.keySet();
		String path;
		MitsubishiQSeriesApi api;

		while (it.hasNext()) {
			path = (String) it.next();
			api = _apiMap.get(path);
			if (api.isConnected()) {
				api.disconnect();
				api = null;
			}
			_apiMap.remove(path);
		}

		// 컨슈머 해제
		for (String emKey : _emKeyList) {
			stopPolling(emKey);
		}

		// 핸들러 해제
		it = (Iterator) _handlerMap.keySet();
		while (it.hasNext()) {
			path = (String) it.next();
			_handlerMap.get(path);

			if (_root.contains(path)) {
				_root.removeHandler(_handlerMap.get(path));
			}
			_log.info("[Handler] : " + path + " 해제");
		}

		_emKeyList.clear();
		_handlerMap.clear();
		_apiMap.clear();
	}

	@Override
	public void startPolling(String emKey, int intervalSec) {

		_em.addPollingAdapterProcessorEvent(emKey, _sid, _did, emKey, null, intervalSec);
		_log.info("[EventManager] : " + emKey + " 등록");
	}

	@Override
	public void stopPolling(String emKey) {

		if (_em.containPollingAdapterProcessor(emKey)) {
			_em.removePollingAdapterProcessorEvent(emKey);

			_log.info("[EventManager] : " + emKey + " 해제");
		}
	}

	private String getHandlerName(String path) {

		int length = path.split("/").length;
		String result = path.split("/")[length];

		return result;
	}

	private String getHandlerPath(String path) {
		String result = "";

		for (int i = 0; i < path.split("/").length - 1; i++) {
			if (i != 0) {
				result = "/";
			}
			result += path.split("/")[i];
		}
		return result;
	}
}
