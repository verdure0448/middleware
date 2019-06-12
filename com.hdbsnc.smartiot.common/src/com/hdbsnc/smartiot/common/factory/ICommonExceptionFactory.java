package com.hdbsnc.smartiot.common.factory;

import com.hdbsnc.smartiot.common.exception.ApplicationException;
import com.hdbsnc.smartiot.common.exception.CommonException;
import com.hdbsnc.smartiot.common.exception.SystemException;
import com.hdbsnc.smartiot.common.pm.vo.IMsgMastObj;

public interface ICommonExceptionFactory {

	public ApplicationException createAppException(String innerCode);
	public ApplicationException createAppException(String innerCode, String[] param);
	public ApplicationException createAppException(String innerCode, String[] param, Throwable e);
	
	public SystemException createSysException(String innerCode);
	public SystemException createSysException(String innerCode, String[] param);
	public SystemException createSysException(String innerCode, String[] param, Throwable e);

	public IMsgMastObj getMsgInfo(String innerCode);
//	public CommonException createAppException(String innerCode);
//	public CommonException createAppException(String innerCode, String[] param);
//	public CommonException createAppException(String innerCode, String[] param, Throwable e);
//	
//	public CommonException createSystemException(String innerCode);
//	public CommonException createSystemException(String innerCode, String[] param);
//	public CommonException createSystemException(String innerCode, String[] param, Throwable e);
	
}
