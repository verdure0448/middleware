package com.hdbsnc.smartiot.util.logger;

import java.util.List;

public interface Log {
	
	//일반적에러로그(String)
	public void err(String log);
	//일반적에러로그(Exception)
	public void err(Exception e);
	//에러는아니지만주의로그
	public void warn(String log);
	//일반정보로그
	public void info(String log);
	//일반정보debug로그(상세정보)
	public void debug(String log);
	//경로추적을위해사용
	public void trace(String log);

	public Log logger(Class<?> clazz);

	public Log logger(String uniqueKey);
	//로거 등록 및 정보
	//public ILog logger(Class<?> clazz);
	//일반적으로는 로그하나등록하겠지만, 여러로그로 나눌때 사용.
	public List<Class<?>> getLoggerClassList();
	public List<String> getLoggerStringList();
	public void removeLogHandler(Class<?> clazz);
	public Class<?> getLogClass();
	public String getLogString();
	
}
