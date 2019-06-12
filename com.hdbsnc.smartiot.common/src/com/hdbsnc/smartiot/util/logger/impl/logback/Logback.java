package com.hdbsnc.smartiot.util.logger.impl.logback;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hdbsnc.smartiot.util.logger.Log;

public class Logback implements Log {

	private Logger logger;
	private static Map<Class<?>, Log> classMap = new Hashtable<>();;
	private static Map<String, Log> stringMap = new Hashtable<>();;
	private Class<?> clazz;
	private String uniqueKey;
	
	@Override
	public Log logger(Class<?> clazz) {
		
		//로그가 만들어져 있는지를 확인합니다.
		if(!classMap.containsKey(clazz))
		{
			Logback newLog = new Logback();
			newLog.logger = LoggerFactory.getLogger(clazz); 
			newLog.clazz = clazz;
			
			newLog.logger.info("새로운  ["+clazz.getName()+"] 로그를 추가하였습니다.");
			
			classMap.put(clazz, newLog);		
		}
		
		return classMap.get(clazz);
	}
	
	@Override
	public Log logger(String uniqueKey) {
		
		//로그가 만들어져 있는지를 확인합니다.
		if(!stringMap.containsKey(uniqueKey))
		{
			Logback newLog = new Logback();
			newLog.logger = LoggerFactory.getLogger(uniqueKey); 
			newLog.clazz = clazz;
			
			newLog.logger.info("새로운 ["+uniqueKey+"] 로그를 추가하였습니다.");
			
			stringMap.put(uniqueKey, newLog);		
		}
		
		return stringMap.get(uniqueKey);
	}
	
	@Override
	public void info(String log) {
		logger.info(log);
	}

	@Override
	public void warn(String log) {
		logger.warn(log);
	}

	@Override
	public void debug(String log) {
		logger.debug(log);
	}

	@Override
	public void trace(String log) {
		logger.trace(log);
	}
	
	@Override
	public void err(String log) {
		logger.error(log);
	}

	@Override
	public void err(Exception e) {
		logger.error(createStackTrace(e));
	}
	

	/**
	 * printStackTrace와 같이 출력하도록합니다.
	 * @param e
	 * @return
	 */
	private String createStackTrace(Exception e){
		StackTraceElement[] stackArray = e.getStackTrace();
		StringBuffer sb = new StringBuffer();
		sb.append(e.getClass().getName()).append("(").append(e.getMessage()).append(")\n");
		StackTraceElement stack;
		for(int i=0;i<stackArray.length;i++){
			stack = stackArray[i];
			sb.append("\tat ").append(stack.getClassName()).append("(").append(stack.getFileName()).append(":").append(stack.getLineNumber()).append(")\n");
		}
		return sb.toString();
	}


	@Override
	public List<Class<?>> getLoggerClassList() {
		List result = new ArrayList<>();
		
		Iterator it = classMap.keySet().iterator();
		while(it.hasNext()) {
			result.add(it.next());
		}
		
		return result;
	}

	@Override
	public List<String> getLoggerStringList() {
		List result = new ArrayList<>();
		
		Iterator it = stringMap.keySet().iterator();
		while(it.hasNext()) {
			result.add(it.next());
		}
		
		return result;
	}
	
	@Override
	public void removeLogHandler(Class<?> clazz) {
		classMap.remove(clazz);
	}


	@Override
	public Class<?> getLogClass() {
		return clazz;
	}

	@Override
	public String getLogString() {
		return uniqueKey;
	}


}
