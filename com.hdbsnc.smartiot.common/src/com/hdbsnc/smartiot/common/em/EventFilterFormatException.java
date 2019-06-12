package com.hdbsnc.smartiot.common.em;

import com.hdbsnc.smartiot.common.exception.CommonException;

public class EventFilterFormatException extends CommonException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4197926638394158015L;
	private String eventFilterString;
	
	public EventFilterFormatException(String eventFilterString) {
		super("401", CommonException.TYPE_ERROR, "");
		if(eventFilterString==null){
			this.eventFilterString = "NULL";
		}else{
			this.eventFilterString = eventFilterString;
		}
		
	}
	
	@Override
	public String getMessage() {
		return "이벤트 필터 표현이 잘못되었습니다.("+eventFilterString+")";
	}

}
