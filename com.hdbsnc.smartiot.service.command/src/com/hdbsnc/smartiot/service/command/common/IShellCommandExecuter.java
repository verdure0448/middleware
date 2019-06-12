package com.hdbsnc.smartiot.service.command.common;

import java.util.Dictionary;

public interface IShellCommandExecuter {

	String getExecuterName();
	
	Dictionary<String, String> getProps();
	
	boolean isExecuteable(ShellCommand cmd);
	
	void execute(ShellCommand cmd);
}
