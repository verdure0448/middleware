package com.hdbsnc.smartiot.common.otp.url.parser;

import com.hdbsnc.smartiot.common.otp.url.parser.vo.Query;
import com.hdbsnc.smartiot.common.otp.url.parser.vo.Url;

public class QueryHandler extends AbstractHandler{

//	public static final char SEPARATOR_EQUAL = '=';
//	public static final char SEPARATOR_AND = '&';
//	public static final char SEPARATOR_SHARP = '#';
	
	public static final String EMPTY_VALUE = "";
	
	@Override
	protected void parse(UrlContext ctx) throws UrlParseException {
		Url result = ctx.getResult();
		char curChar;
		String value1 = null;
		String value2 = null;
		while(ctx.hasNextChar()){
			curChar = ctx.nextChar();
			switch(curChar){
			case UrlParser.SEPARATOR_EQUAL:
				if(value1!=null) throw new UrlParseException("Query invalid",ctx);
				value1 = ctx.getCurrentString();
				break;
			case UrlParser.SEPARATOR_AND:
				if(value1==null){
					value1 = ctx.getCurrentString();
				}else{
					value2 = ctx.getCurrentString();
				}
				result.addQuery(value1, value2);
				value1 = null;
				value2 = null;
				break;
			case UrlParser.SEPARATOR_SHARP:
				if(value1==null){
					value1 = ctx.getCurrentString();
				}else{
					value2 = ctx.getCurrentString();
				}
				result.addQuery(value1, value2);
				ctx.setState(UrlContext.STATE_FRAG);
				return;
			default:
				break;
			}
		}
		if(value1==null){
			value1 = ctx.getCurrentString();
		}else{
			value2 = ctx.getCurrentString();
		}
		if(value1==null || value1.equals("")) throw new UrlParseException("Query invalid", ctx);
		if(value2==null) value2 = "";
		result.addQuery(value1, value2);
		ctx.setState(UrlContext.STATE_FINISH);
	}

	@Override
	protected boolean resolve(UrlContext ctx) {
		if(ctx.getState()==UrlContext.STATE_QUERY){
			return true;
		}
		ctx.setQuery(Query.EMPTY);
		return false;
	}
	
}
