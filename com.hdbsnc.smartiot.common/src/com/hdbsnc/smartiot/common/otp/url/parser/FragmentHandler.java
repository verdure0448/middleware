package com.hdbsnc.smartiot.common.otp.url.parser;

import java.util.HashMap;
import java.util.Map;

import com.hdbsnc.smartiot.common.otp.url.parser.vo.Frag;
import com.hdbsnc.smartiot.common.otp.url.parser.vo.Url;

public class FragmentHandler extends AbstractHandler{

//	public static final char SEPARATOR_COLON = ':';
	public static final String EMPTY_VALUE = "";
	
	@Override
	protected boolean resolve(UrlContext ctx) {
		if(ctx.getState()==UrlContext.STATE_FRAG){
			return true;
		}
		ctx.setFrag(Frag.EMPTY);
		return false;
	}

	@Override
	protected void parse(UrlContext ctx) throws UrlParseException {
		Url result = ctx.getResult();
		char curChar;
		String value1 = null;
		String value2 = null;
		boolean checkColon = false;
		Map<String, String> fragMap = new HashMap<String, String>();
		while(ctx.hasNextChar()){
			curChar = ctx.nextChar();
			switch(curChar){
			case UrlParser.SEPARATOR_COLON:
				if(!checkColon){
					value1 = ctx.getCurrentString();
					value2 = null;
					checkColon = true;
				}else{
					throw new UrlParseException("Frag invalid", ctx);
				}
				break;
			case UrlParser.SEPARATOR_AND:
				if(checkColon){
					value2 = ctx.getCurrentString();
					fragMap.put(value1, value2);
					checkColon = false;
					value1 = null;
					value2 = null;
				}else{
					throw new UrlParseException("Frag invalid", ctx);
				}
				break;
			}
		}
		if(checkColon){
			value2 = ctx.getCurrentString();
			fragMap.put(value1, value2);
			checkColon = false;
		}
		
		if(fragMap.size()==0){
			result.setFragment(Frag.EMPTY);
		}else{
			result.setFragment(new Frag(fragMap));
		}
		ctx.setState(UrlContext.STATE_FINISH);
	}
}
