package com.hdbsnc.smartiot.common.ism.sm;

import com.hdbsnc.smartiot.common.exception.CommonException;

public class AuthCertificationException extends CommonException{

	public AuthCertificationException() {
		super("100", TYPE_ERROR, "");
	}

	@Override
	public String getMessage() {
		return "장치 혹은 사용자 인증을 실패 하였습니다.";
	}

	
}
