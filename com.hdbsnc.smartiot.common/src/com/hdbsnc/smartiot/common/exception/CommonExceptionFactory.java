package com.hdbsnc.smartiot.common.exception;

import com.hdbsnc.smartiot.common.factory.ICommonExceptionFactory;
import com.hdbsnc.smartiot.common.pm.IProfileManager;
import com.hdbsnc.smartiot.common.pm.vo.IMsgMastObj;

public class CommonExceptionFactory implements ICommonExceptionFactory {

	IProfileManager pm;
	
	public CommonExceptionFactory(IProfileManager pm) {
		this.pm = pm;
	}

	public IMsgMastObj getMsgInfo(String innerCode){
		IMsgMastObj iMsgObj = null;
		try {
			iMsgObj = pm.getMsgMastObj(innerCode);
		} catch (Exception e) {
			// 처리 없음
		}
		
		return iMsgObj;
	}
	
	@Override
	public ApplicationException createAppException(String innerCode, String[] param, Throwable e) {
		IMsgMastObj iMsgObj = null;
		try {
			iMsgObj = pm.getMsgMastObj(innerCode);
		} catch (Exception ex) {
			// 처리 없음
		}

		ApplicationException ex = null;
		if (iMsgObj != null) {
			ex = new ApplicationException(iMsgObj.getOuterCode(), iMsgObj.getType(), editMsg(iMsgObj.getMsg(), param),
					e);
		} else {
			ex = new ApplicationException(this.getClass().getName() + ":005", CommonException.TYPE_ERROR,
					editMsg("에러메세지 정의가 존재하지 않습니다.내부코드[{0}]", new String[] { innerCode }), e);
		}
		return ex;
	}

	@Override
	public ApplicationException createAppException(String innerCode, String[] param) {
		return createAppException(innerCode, param, null);
	}

	@Override
	public ApplicationException createAppException(String innerCode) {
		return createAppException(innerCode, null, null);
	}

	@Override
	public SystemException createSysException(String innerCode, String[] param, Throwable e) {
		IMsgMastObj iMsgObj = null;
		try {
			iMsgObj = pm.getMsgMastObj(innerCode);
		} catch (Exception ex) {
			// 처리 없음
		}

		SystemException ex = null;
		if (iMsgObj != null) {
			ex = new SystemException(iMsgObj.getOuterCode(), iMsgObj.getType(), editMsg(iMsgObj.getMsg(), param),
					e);
		} else {
			ex = new SystemException(this.getClass().getName() + ":010", CommonException.TYPE_ERROR,
					editMsg("에러메세지 정의가 존재하지 않습니다.내부코드[{0}]", new String[] { innerCode }), e);
		}
		return ex;
	}

	@Override
	public SystemException createSysException(String innerCode, String[] param) {
		return createSysException(innerCode, param, null);
	}

	@Override
	public SystemException createSysException(String innerCode) {
		return createSysException(innerCode, null, null);
	}

	private String editMsg(String context, String[] param) {

		if (param == null)
			return context;

		for (int i = 0; i < param.length; i++) {

			context = context.replace("{" + String.valueOf(i) + "}", param[i]);
		}
		return context;
	}
}
