package com.hdbsnc.smartiot.adapter.mb.mc.bin.handler.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.hdbsnc.smartiot.adapter.mb.mc.bin.api.MitsubishiQSeriesApi;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.api.frame.AbstractBlocksFrame.TransMode;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.processor.handler.ReadBatchProcessHandler;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.protocol.obj.StartRequest;
import com.hdbsnc.smartiot.common.aim.IAdapterInstanceManager;
import com.hdbsnc.smartiot.common.context.handler.exception.ElementNotFoundException;
import com.hdbsnc.smartiot.common.context.handler.exception.ElementNullOrEmptyPathException;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractTransactionTimeoutFunctionHandler;
import com.hdbsnc.smartiot.common.context.handler2.impl.RootHandler;
import com.hdbsnc.smartiot.common.em.IEventManager;
import com.hdbsnc.smartiot.util.logger.Log;

/**
 * @author dbkim 핸들러를 동적으로 생성, 제거, 폴링한다.
 */
public class DynamicHandlerManager implements ICreatePolling, IDeletePolling, IRunningStatus {

	// 핸들러를 저장하기 위한 핸들러
	private Map<String, AbstractTransactionTimeoutFunctionHandler> _handlerMap;
	// 이벤트 매니저의 키를 저장
	private Set<String> _emKeySet;
	private Map<String, MitsubishiQSeriesApi> _apiMap;
	private Map<String, StartRequest.Param> _status;

	private IEventManager _em;
	private RootHandler _root;

	private Log _log;
	
	private IAdapterInstanceManager _aim;

	private String _did;
	private String _sid;

	public DynamicHandlerManager(RootHandler root, IAdapterInstanceManager aim, IEventManager em, String did, String sid, Log log) {

		_handlerMap = new Hashtable<String, AbstractTransactionTimeoutFunctionHandler>();
		_apiMap = new Hashtable<String, MitsubishiQSeriesApi>();
		_emKeySet = new HashSet<String>();
		_status = new Hashtable<String, StartRequest.Param>();

		_em = em;
		_aim = aim;
		_root = root;
		_log = log;

		_did = did;
		_sid = sid;
	}

	@Override
	public void start(HandlerType kind, String path, String ip, int port, int pollingIntervalSec, StartRequest startRequest) throws Exception{

		String sHandlerPath = getHandlerPath(path);
		String sHandlerName = getHandlerName(path);
		String sEmKey = path;
	
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
			AbstractTransactionTimeoutFunctionHandler handler = new ReadBatchProcessHandler(sHandlerName, 3000,_aim, _sid, api, startRequest, _log); 
			_root.putHandler(sHandlerPath, handler);
			_handlerMap.put(path, handler);
			break;
		default:
			throw new Exception("지원하지 않는 핸들러입니다.");
		}

		startPolling(sEmKey, pollingIntervalSec);
		_status.put(sEmKey, startRequest.getParam());
		_apiMap.put(sEmKey, api);

		_root.printString();
	}

	private boolean isConnection(String sIP, int iPort) {

		if(_apiMap.size()==0) {
			return false;
		}
		
		Iterator it = _apiMap.keySet().iterator();
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
	public Map statusAll() {
		return _status;
	}

	@Override
	public void delete(String path) throws IOException, ElementNullOrEmptyPathException, ElementNotFoundException {
		
		if (_handlerMap.containsKey(path)) {
			
			_root.deleteHandler(path.split("/"));
			_handlerMap.remove(path);
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
	public String[] deleteAll() throws IOException {
		
		List<String> eventIdList = new ArrayList<String>();
		String eventId;
		
		// 키가 존재
		Iterator it = _apiMap.keySet().iterator();
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
		for (String emKey : _emKeySet) {
			stopPolling(emKey);
			
			//read/polling/프로토콜id/프로토콜event.id
			eventId = emKey.split("/")[3];
			eventIdList.add(eventId);
		}

		// 핸들러 해제
		it = _handlerMap.keySet().iterator();
		while (it.hasNext()) {
			path = (String) it.next();
			_root.removeHandler(_handlerMap.get(path));
			_handlerMap.remove(path);
			_log.info("[Handler] : " + path + " 해제");
		}

		
		_emKeySet.clear();
		_handlerMap.clear();
		_apiMap.clear();
		_status.clear();
		
		return (String[]) eventIdList.toArray();
	}

	private void startPolling(String emKey, int intervalSec) {
		_emKeySet.add(emKey);
		_em.addPollingAdapterProcessorEvent(emKey, _sid, _did, emKey, null, intervalSec);
		_log.info("[EventManager] : " + emKey + " 등록");
	}

	private void stopPolling(String emKey) {

		if (_em.containPollingAdapterProcessor(emKey)) {
			_em.removePollingAdapterProcessorEvent(emKey);

			_log.info("[EventManager] : " + emKey + " 해제");
			
			_emKeySet.remove(emKey);
			
			//[PLC 수집 조회 프로토콜]에서 제외 되므로 삭제
			_status.remove(emKey);
		}
	}

	private String getHandlerName(String path) {

		int length = path.split("/").length;
		String result = path.split("/")[length-1];

		return result;
	}

	private String getHandlerPath(String path) {
		String result = "";

		for (int i = 0; i < path.split("/").length - 1; i++) {
			if (i != 0) {
				result += "/";
			}
			result += path.split("/")[i];
		}
		return result;
	}
}
