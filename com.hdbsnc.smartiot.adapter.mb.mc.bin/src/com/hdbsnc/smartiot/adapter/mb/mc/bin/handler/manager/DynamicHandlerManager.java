package com.hdbsnc.smartiot.adapter.mb.mc.bin.handler.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.hdbsnc.smartiot.adapter.mb.mc.bin.api.MitsubishiQSeriesApi;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.api.frame.AbstractBlocksFrame.TransMode;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.api.frame.exception.ApplicationException;
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

	//EM, MelsecAPI, Hanlder을 관리한다.
	private Map<String, ManagerVo> _handleManager;

	private IEventManager _em;
	private RootHandler _root;

	private Log _log;
	
	private IAdapterInstanceManager _aim;

	private String _did;
	private String _sid;

	public DynamicHandlerManager(RootHandler root, IAdapterInstanceManager aim, IEventManager em, String did, String sid, Log log) {

		_handleManager = new HashMap<String, ManagerVo>();

		_em = em;
		_aim = aim;
		_root = root;
		_log = log;

		_did = did;
		_sid = sid;
	}

	@Override
	public synchronized void start(HandlerType kind, String path, String ip, int port, int pollingIntervalSec, StartRequest startRequest) throws Exception{

		String key = path;
		ManagerVo value = new ManagerVo(key, startRequest);
		
		switch (kind) {
		case READ_BATCH_PROCESS_HANDLER:
			try {
				value.createApi(ip, port);
				value.createHandler();
				value.createEm(pollingIntervalSec);
				_handleManager.put(key, value);
				_root.printString();
			}catch(Exception e) {
				value.cancleAll();
				throw e;
			}
			
			break;
		default:
			throw new ApplicationException("지원하지 않는 핸들러입니다.");
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
	public synchronized void delete(String key) throws Exception {
		
		if (!_handleManager.containsKey(key)) {
			throw new ApplicationException("존재하지 않는 핸들러 입니다.");
		}

		_handleManager.get(key).cancleAll();
		_log.info("[Handler] : " + key + " 해제");
		
		_handleManager.remove(key);
		_root.printString();
	}

	@Override
	public synchronized String[] deleteAll() throws Exception {

		//DEEP COPY
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
	 * @author user
	 * 핸들러 동적 생성 시 관리할 내용들을 저장할 클래스
	 */
	class ManagerVo {

		String path;
		AbstractTransactionTimeoutFunctionHandler handler;
		MitsubishiQSeriesApi api;
		StartRequest startRequest;

		ManagerVo(String key, StartRequest startRequest){
			this.path = key;
			this.startRequest = startRequest;
		}
		
		/**
		 * Melsec API를 만들어 준다.
		 * @param ip
		 * @param port
		 * @throws Exception
		 */
		void createApi(String ip, int port) throws Exception {

			// 이미 만들어진 IP 및 PORT가 있는지 확인
			if (isConnection(ip, port)) {
				throw new ApplicationException("이미 기동 중인 IP : " + ip + " Port : " + port + " 입니다.");
			}
			api = null;
			try {
				api = new MitsubishiQSeriesApi(TransMode.BINARY, _log);
				api.connect(ip, port);
			}catch(Exception e) {
				throw e;
			}
		}
		
		/**
		 * 멜섹 핸들러를 만들어 준다.
		 * @throws ApplicationException 
		 */
		void createHandler() throws Exception {
			try {
				String sHandlerPath = EditUtil.getHandlerPath(path);
				String sHandlerName = EditUtil.getHandlerName(path);
				
				// 이미 만들어진 핸들러가 있는지 확인
				if(isHandler(path)) {
					throw new ApplicationException("이미 존재하는 핸들러입니다. id 및 event.id를 확인해주세요.");
				}
				
				// 만들어진 IP 및 PORT가 없다면 생성
				handler = new ReadBatchProcessHandler(sHandlerName, 3000,_aim, _sid, api, startRequest, _log); 
				_root.putHandler(sHandlerPath, handler);
				_root.printString();
			}catch(Exception e) {
				throw e;
			}
		}
		
		/**
		 * 정해진 Path를 주기적으로 폴링한다.
		 * @param intervalSec
		 * @throws Exception
		 */
		void createEm(int intervalSec) throws Exception{
			startPolling(path, intervalSec);
		}
		
		/**
		 * 생성된 모든 객체를 제거한다.
		 * @throws Exception
		 */
		void cancleAll() throws Exception {

			//폴링 정지
			stopPolling(path);
			
			//api가 있으면 제거
			if(api != null) {
				if(api.isConnected()) {
					api.disconnect();
				}
			}
			//_root에 핸들러가 있으면 제거
			if(handler != null) {
				if(_root.findHandler(path)!=null) {
					_root.deleteHandler(path.split("/"));
				}
			}
			
			handler = null;
			api = null;
			path = null;
		}
		
		StartRequest.Param getParam(){
			return startRequest.getParam();
		}
		
		/**
		 * 인자로 전달 받은 Path에 핸들러가 존재하는지 확인한다.
		 * @param path
		 * @return
		 */
		boolean isHandler(String path) throws Exception{

			if(handler != null) {
				if(_root.findHandler(path)!=null) {
					return true;
				}
			}
			
			return false;
		}

		/**
		 * IP 및 PORT가 이미 기동중인지 확인한다.
		 * @param sIP
		 * @param iPort
		 * @return
		 */
		boolean isConnection(String sIP, int iPort) {

			if(_handleManager.size()==0) {
				return false;
			}

			//DEEP COPY
			Map tmp = new HashMap();
			tmp.putAll(_handleManager);
			
			Iterator it = tmp.keySet().iterator();
			String key;
			MitsubishiQSeriesApi api;
			while (it.hasNext()) {
				key = (String) it.next();
				api = _handleManager.get(key).api;

				if (api.getIp().equals(sIP) && api.getPort() == iPort) {
					return true;
				}
			}

			return false;
		}

	}
	
}
