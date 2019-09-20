package com.hdbsnc.smartiot.adapter.mb.mc.bin.handler.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.hdbsnc.smartiot.adapter.mb.mc.bin.api.MitsubishiQSeriesApi;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.api.frame.AbstractBlocksFrame.TransMode;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.api.frame.exception.ApplicationException;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.api.frame.exception.MCProtocolResponseException;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.processor.handler.ReadBatchProcessHandler;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.protocol.obj.StartRequest;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.util.EditUtil;
import com.hdbsnc.smartiot.common.aim.IAdapterInstanceManager;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractTransactionTimeoutFunctionHandler;
import com.hdbsnc.smartiot.common.context.handler2.impl.RootHandler;
import com.hdbsnc.smartiot.common.em.IEventManager;
import com.hdbsnc.smartiot.util.logger.Log;

/**
 * @author dbkim 핸들러를 동적으로 생성, 제거, 폴링한다.
 */
public class DynamicHandlerManager implements ICreatePolling, IDeletePolling, IRunningStatus {

	// EM, MelsecAPI, Hanlder을 관리한다.
	private Map<String, ManagerVo> _handleManager;

	private IEventManager _em;
	private RootHandler _root;

	private Log _log;

	private IAdapterInstanceManager _aim;

	private String _did;
	private String _sid;

	public DynamicHandlerManager(RootHandler root, IAdapterInstanceManager aim, IEventManager em, String did,
			String sid, Log log) {

		_handleManager = new HashMap<String, ManagerVo>();

		_em = em;
		_aim = aim;
		_root = root;
		_log = log;

		_did = did;
		_sid = sid;
	}

	@Override
	public synchronized void start(HandlerType kind, String path, String ip, int port, int pollingIntervalSec,
			StartRequest startRequest) throws ApplicationException, MCProtocolResponseException, Exception {

		String key = path;
		ManagerVo value = new ManagerVo(key, startRequest);

		if (isConnection(ip, port)) {
			throw new ApplicationException("-33104", String.format("이미 기동중인 IP(%s), Port(%s)입니다", ip, port));
		} else if (isHandleManagerKey(path)) {
			throw new ApplicationException("-33105",
					String.format("이미 존재하는 event.id(%s) 입니다", startRequest.getParam().getEventID()));
		}

		switch (kind) {
		case READ_BATCH_PROCESS_HANDLER:
			try {
				value.createApi(ip, port);
				value.createHandler();
				value.createEm(pollingIntervalSec);
				_handleManager.put(key, value);
				_root.printString();
			} catch (MCProtocolResponseException e) {
				value.cancleAll();
				throw e;
			}

			break;
		default:
			throw new Exception("지원하지 않는 핸들러입니다.");
		}

	}

	@Override
	public Object[] statusAll() throws ApplicationException {
		// 키가 존재
		Iterator it = _handleManager.keySet().iterator();
		String path;
		List<StartRequest.Param> stauslist = new ArrayList();
		ManagerVo value;

		while (it.hasNext()) {
			path = (String) it.next();
			value = _handleManager.get(path);
			stauslist.add(value.getParam());
		}

		Object[] result = stauslist.toArray(new StartRequest.Param[stauslist.size()]);

		return result;
	}

	@Override
	public synchronized void delete(String key) throws ApplicationException, Exception {

		if (!_handleManager.containsKey(key)) {
			String[] paths = key.split("/");
			String name = paths[paths.length - 1];
			throw new ApplicationException("-33204", String.format("존재하지 않는 event.id(%s) 입니다", name));
		}

		_handleManager.get(key).cancleAll();
		_log.info("[Handler] : " + key + " 해제");

		_handleManager.remove(key);
		_root.printString();
	}

	@Override
	public synchronized String[] deleteAll() throws Exception {

		// DEEP COPY
		Map tmp = new HashMap();
		tmp.putAll(_handleManager);

		Iterator it = tmp.keySet().iterator();
		String key, eventId;
		List<String> eventIdList = new ArrayList();
		while (it.hasNext()) {
			key = (String) it.next();
			delete(key);
			eventId = EditUtil.getHandlerName(key);
			eventIdList.add(eventId);
		}

		String[] result = eventIdList.toArray(new String[eventIdList.size()]);

		return result;
	}
	

	private void startPolling(String emKey, int intervalSec) {
		_em.addPollingAdapterProcessorEvent(emKey, _sid, _did, emKey, null, intervalSec);
		_log.info("[EventManager] : " + emKey + " 등록");
	}

	private void stopPolling(String emKey) {

		if (_em.containPollingAdapterProcessor(emKey)) {
			_em.removePollingAdapterProcessorEvent(emKey);

			_log.info("[EventManager] : " + emKey + " 해제");
		}
	}

	/**
	 * Path가 존재 하는지 확인한다. 이미 키가 존재하면 return true 키가 존재 하지 않으면 return false
	 * 
	 * @param path
	 * @return
	 */
	boolean isHandleManagerKey(String path) {

		if (_handleManager.containsKey(path)) {
			return true;
		}

		return false;
	}

	/**
	 * IP 및 PORT가 이미 기동중인지 확인한다. 기동 중이라면 return true 기동 중이 아니라면 false
	 * 
	 * @param ip
	 * @param port
	 * @return
	 */
	boolean isConnection(String ip, int port) {

		if (_handleManager.size() == 0) {
			return false;
		}

		// 핸들러 이름은 틀리지만 이미 기동중인 IP, PORT가 있을 수 있으므로 전체에서 검색
		// DEEP COPY
		Map tmp = new HashMap();
		tmp.putAll(_handleManager);

		Iterator it = tmp.keySet().iterator();
		String key;
		MitsubishiQSeriesApi api;
		while (it.hasNext()) {
			key = (String) it.next();
			api = _handleManager.get(key).api;

			if (api.getIp().equals(ip) && api.getPort() == port) {
				return true;
			}
		}

		return false;
	}
	
	/**
	 * MQ API 검색 
	 *  ip, port에 해당하는 MQ API 객체를 리턴한다. 존재하지 않을 경우는 null을 리턴
	 *  
	 * @param ip
	 * @param port
	 * @return API 객체
	 */
	public synchronized ManagerVo getManagerInstance(String ip, int port) {
		if (_handleManager.size() == 0) {
			return null;
		} 

		// 핸들러 이름은 틀리지만 이미 기동중인 IP, PORT가 있을 수 있으므로 전체에서 검색
		// DEEP COPY
		Map tmp = new HashMap();
		tmp.putAll(_handleManager);

		Iterator it = tmp.keySet().iterator();
		String key;
		MitsubishiQSeriesApi api;
		while (it.hasNext()) {
			key = (String) it.next();
			api = _handleManager.get(key).api;

			if (api.getIp().equals(ip) && api.getPort() == port) {
				return _handleManager.get(key);
			}
		}

		return null;
	}
	
	/**
	 * MQ API 검색 
	 * ip, port에 해당하는 MQ API 검색
	 * 존재할 경우 true
	 * 존재하지 않을 경우 false 
	 *  
	 * @param ip
	 * @param port
	 * @return API 객체
	 */
	public synchronized boolean isUseManager(String ip, int port) {
		if (_handleManager.size() == 0) {
			return false;
		} 

		// 핸들러 이름은 틀리지만 이미 기동중인 IP, PORT가 있을 수 있으므로 전체에서 검색
		// DEEP COPY
		Map tmp = new HashMap();
		tmp.putAll(_handleManager);

		Iterator it = tmp.keySet().iterator();
		String key;
		MitsubishiQSeriesApi api;
		while (it.hasNext()) {
			key = (String) it.next();
			api = _handleManager.get(key).api;

			if (api.getIp().equals(ip) && api.getPort() == port) {
				return true;
			}
		}

		return false;
	}

	/**
	 * @author user 핸들러 동적 생성 시 관리할 내용들을 저장할 클래스
	 */
	public class ManagerVo {

		String path;
		AbstractTransactionTimeoutFunctionHandler handler;
		MitsubishiQSeriesApi api;
		StartRequest startRequest;

		// api disconnect 처리시 사용중인경우에는 대기하여 처리하기 위한 플래그
		public boolean isUse = false;

		ManagerVo(String key, StartRequest startRequest) {
			this.path = key;
			this.startRequest = startRequest;
		}

		/**
		 * Melsec API를 만들어 준다.
		 * 
		 * @param ip
		 * @param port
		 * @throws MCProtocolResponseException
		 * @throws Exception
		 */
		void createApi(String ip, int port) throws MCProtocolResponseException, Exception {

			try {
				api = new MitsubishiQSeriesApi(TransMode.BINARY, _log);
				api.connect(ip, port);
			} catch (MCProtocolResponseException e) {
				throw e;
			}
		}

		/**
		 * 멜섹 핸들러를 만들어 준다.
		 * 
		 * @throws ApplicationException
		 */
		void createHandler() throws Exception {
			try {
				String sHandlerPath = EditUtil.getHandlerPath(path);
				String sHandlerName = EditUtil.getHandlerName(path);

				handler = new ReadBatchProcessHandler(sHandlerName, 3000, _aim, _sid, api, startRequest, _log);
				_root.putHandler(sHandlerPath, handler);
				_root.printString();
			} catch (Exception e) {
				throw e;
			}
		}

		/**
		 * 정해진 Path를 주기적으로 폴링한다.
		 * 
		 * @param intervalSec
		 * @throws Exception
		 */
		void createEm(int intervalSec) throws Exception {
			startPolling(path, intervalSec);
		}

		StartRequest.Param getParam() {
			return startRequest.getParam();
		}

		/**
		 * 생성된 모든 객체를 제거한다.
		 * 
		 * @throws Exception
		 */
		void cancleAll() throws Exception {

			// 폴링 정지
			stopPolling(path);

			// api가 있으면 제거
			if (api != null) {
				if (api.isConnected()) {
					int cnt = 0;
					// 사용중일경우  대기. 3초후에도 사용중일경우 강제 해제
					while (isUse && cnt++ < 100) {
						Thread.sleep(30);
					}
					api.disconnect();
				}
			}
			// _root에 핸들러가 있으면 제거
			if (handler != null) {
				if (_root.findHandler(path) != null) {
					_root.deleteHandler(path.split("/"));
				}
			}

			handler = null;
			api = null;
			path = null;
		}

		public MitsubishiQSeriesApi getMQApi() {
			return api;
		}
	}

}
