package com.hdbsnc.smartiot.service.command.ism;

import java.util.Arrays;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.hdbsnc.smartiot.common.aim.IAdapterInstanceContainer;
import com.hdbsnc.smartiot.common.aim.IAdapterInstanceEvent;
import com.hdbsnc.smartiot.common.ism.IIntegratedSessionManager;
import com.hdbsnc.smartiot.common.ism.sm.ISession;
import com.hdbsnc.smartiot.common.ism.sm.ISessionManager;
import com.hdbsnc.smartiot.common.pm.vo.IInstanceObj;
import com.hdbsnc.smartiot.server.IServerInstance;
import com.hdbsnc.smartiot.service.command.common.CommandStack;
import com.hdbsnc.smartiot.service.command.common.IShellCommandExecuter;
import com.hdbsnc.smartiot.service.command.common.ShellCommand;
import com.hdbsnc.smartiot.service.command.common.util.text.impl.StringHeaderRow;
import com.hdbsnc.smartiot.service.command.common.util.text.impl.StringRow;
import com.hdbsnc.smartiot.service.command.common.util.text.impl.StringTable;
import com.hdbsnc.smartiot.service.master.slavemanager.Server.Instance.Device;

public class IsmCommand implements IShellCommandExecuter {

	private IServerInstance server;
	private IIntegratedSessionManager ism;
	private Hashtable props;
	private CommandStack cStack;
	private List<String> funcList = Arrays.asList("ismscnt", "ismlist");
	final int MAX_SIZE = 150;

	public IsmCommand(CommandStack cStack, IServerInstance server) {
		this.server = server;
		this.cStack = cStack;
		this.cStack.addExecuter(this);
		this.ism = server.getISM();
		this.props = new Hashtable();
		this.props.put("osgi.command.scope", "smartiot");
		this.props.put("osgi.command.function", (String[]) funcList.toArray());
	}

	public void ismscnt() {
		StringTable table = new StringTable(MAX_SIZE);
		table.setHeader(new StringHeaderRow().add("NO", 5).add("IID", 30).add("SCNT", 20));

		Map<String, ISessionManager> smMap = ism.getSessionManagerMap();
		Iterator<Entry<String, ISessionManager>> iter = smMap.entrySet().iterator();
		Entry<String, ISessionManager> entry;
		String iid;
		ISessionManager sm;
		int sCnt;
		int index = 0;
		while (iter.hasNext()) {
			entry = iter.next();
			sm = entry.getValue();

			iid = entry.getKey();
			sCnt = sm.getSessionCount();
			table.addRowData(String.valueOf(++index), iid, String.valueOf(sCnt));
		}
		if (index == 0) {
			table.addRowData(String.valueOf(++index), "-", "-");
		}
		table.simplePrint();
		cStack.addShellCommand(new ShellCommand("ismscnt", null));
	}

	// 전체 세션리스트
	public void ismlist() {
		StringTable table = new StringTable(MAX_SIZE);
		table.setHeader(
				new StringHeaderRow().add("NO", 5).add("IID", 33).add("DID", 33).add("UserID", 8).add("SessionID", 27));
		Map<String, ISessionManager> smMap = ism.getSessionManagerMap();
		Iterator<Entry<String, ISessionManager>> iter = smMap.entrySet().iterator();
		Entry<String, ISessionManager> entry;
		String iid;
		ISessionManager sm;
		List<ISession> sessionList;
		Device device;
		int index = 0;
		while (iter.hasNext()) {
			entry = iter.next();
			sm = entry.getValue();
			iid = entry.getKey();
			sessionList = sm.getSessionList();
			for (int i = 0; i < sessionList.size(); i++) {
				if (sessionList.get(i).getSessionKey() != null) {
					table.addRowData(String.valueOf(++index), iid, sessionList.get(i).getDeviceId(),
							sessionList.get(i).getUserId(), sessionList.get(i).getSessionKey());
				}
			}
		}
		if (index == 0) {
			table.addRowData(String.valueOf(++index), "-", "-", "-", "-");
		}
		table.simplePrint();
		// table.headerOrderPrint();
		cStack.addShellCommand(new ShellCommand("ismlist", null));
	}

	// iId를 입력받아 입력값과같은 친구들을 모두부른다.
	public void ismlist(String inputIid) {
		StringTable table = new StringTable(MAX_SIZE);
		table.setHeader(new StringHeaderRow().add("NO", 5).add("DID", 33).add("UserID", 8).add("SessionID", 30)
				.add("createdtime", 10).add("lastAccessTime",10).add("State", 7));

		Map<String, ISessionManager> smMap = ism.getSessionManagerMap();
		Iterator<Entry<String, ISessionManager>> iter = smMap.entrySet().iterator();
		Entry<String, ISessionManager> entry;
		ISessionManager sm;
		List<ISession> sessionList;
		String iid;
		int index = 0;
		String state = null;
		while (iter.hasNext()) {
			entry = iter.next();
			sm = entry.getValue();
			iid = entry.getKey();
			sessionList = sm.getSessionList();
			for (int i = 0; i < sessionList.size(); i++) {
				if (sessionList.get(i).getSessionKey() != null) {
					if (iid.equals(inputIid)) {
						if (sessionList.get(i).getState() == 1 << 0) {
							state = "CREATED";
						} else if (sessionList.get(i).getState() == 1 << 1) {
							state = "ACTIVATE";
						} else if (sessionList.get(i).getState() == 1 << 2) {
							state = "DEACTIVATE";
						} else if (sessionList.get(i).getState() == 1 << 3) {
							state = "DISPOSE";
						}
						// sessionList.get(i).getState();
						table.addRowData(String.valueOf(++index), sessionList.get(i).getDeviceId(),
								sessionList.get(i).getUserId(), sessionList.get(i).getSessionKey(),
								String.valueOf(sessionList.get(i).getCreatedTime()), String.valueOf(sessionList.get(i).getLastAccessedTime()), state);
					}
				}
			}
		}
		if (index == 0) {
			table.addRowData(String.valueOf(++index), "-", "-", "-", "-");
		}
		table.simplePrint();
		cStack.addShellCommand(new ShellCommand("smlist", null));
	}

	public void ismlist(int arg) {

		Map<String, ISessionManager> smMap = ism.getSessionManagerMap();
		Iterator<Entry<String, ISessionManager>> iter = smMap.entrySet().iterator();
		Entry<String, ISessionManager> entry;
		String iid;
		ISessionManager sm;
		List<ISession> sessionList;
		Device device;
		int index = 0;
		String inputIid = null;

		while (iter.hasNext()) {
			entry = iter.next();
			sm = entry.getValue();
			iid = entry.getKey();
			sessionList = sm.getSessionList();
			for (int i = 0; i < sessionList.size(); i++) {
				if (sessionList.get(i).getSessionKey() != null) {
					// table.addRowData(String.valueOf(++index),
					// iid,sessionList.get(i).getDeviceId(),
					// sessionList.get(i).getUserId(),
					// sessionList.get(i).getSessionKey());
					++index;
					if (arg == index) {
						inputIid = iid;
					}
				}
			}
		}

		StringTable table = new StringTable(MAX_SIZE);
		table.setHeader(new StringHeaderRow().add("NO", 5).add("DID", 30).add("UserID", 8).add("SessionID", 29)
				.add("createdtime", 12).add("lastAccessTime",12).add("State", 9));

		smMap = ism.getSessionManagerMap();
		iter = smMap.entrySet().iterator();
		entry = null;
		sm = null;
		sessionList = null;
		iid = null;

		index = 0;
		String state = null;
		while (iter.hasNext()) {
			entry = iter.next();
			sm = entry.getValue();
			iid = entry.getKey();
			sessionList = sm.getSessionList();
			for (int i = 0; i < sessionList.size(); i++) {
				if (sessionList.get(i).getSessionKey() != null) {
					if (iid.equals(inputIid)) {
						if (sessionList.get(i).getState() == 1 << 0) {
							state = "CREATED";
						} else if (sessionList.get(i).getState() == 1 << 1) {
							state = "ACTIVATE";
						} else if (sessionList.get(i).getState() == 1 << 2) {
							state = "DEACTIVATE";
						} else if (sessionList.get(i).getState() == 1 << 3) {
							state = "DISPOSE";
						}
						// sessionList.get(i).getState();
						table.addRowData(String.valueOf(++index), sessionList.get(i).getDeviceId(),
								sessionList.get(i).getUserId(), sessionList.get(i).getSessionKey(),
								String.valueOf(sessionList.get(i).getCreatedTime()), String.valueOf(sessionList.get(i).getLastAccessedTime()), state);
					}
				}
			}
		}
		if (index == 0) {
			table.addRowData(String.valueOf(++index), "-", "-", "-", "-");
		}
		table.simplePrint();
		cStack.addShellCommand(new ShellCommand("ismlist", null));

	}

	@Override
	public String getExecuterName() {
		return "IsmCommand";
	}

	@Override
	public Dictionary<String, String> getProps() {
		return this.props;
	}

	@Override
	public boolean isExecuteable(ShellCommand cmd) {
		if (funcList.contains(cmd.getCommand())) {
			return true;
		}
		return false;
	}

	@Override
	public void execute(ShellCommand cmd) {
		String cmdTxt = cmd.getCommand();
		switch (cmdTxt) {
		case "ismscnt":
			ismscnt();
			break;
		case "ismlist":
			ismlist();
			break;
		}
	}
}
