package com.hdbsnc.smartiot.common.otp.url.parser;

import com.hdbsnc.smartiot.common.otp.url.parser.vo.Url;


public class PathHandler extends AbstractHandler{

//	public static final char SEPARATOR_SLASH = '/';
//	public static final char SEPARATOR_SHARP = '#';
//	public static final char SEPARATOR_QUESTION = '?';
	
	@Override
	protected void parse(UrlContext ctx) throws UrlParseException {		
		Url result = ctx.getResult();
		char curChar;
		while(ctx.hasNextChar()){
			curChar = ctx.nextChar();
			switch(curChar){
				case UrlParser.SEPARATOR_SLASH:
					result.addPath(ctx.getCurrentString());
					break;
				case UrlParser.SEPARATOR_QUESTION:
					result.addPath(ctx.getCurrentString());
					ctx.setState(UrlContext.STATE_QUERY);
					return;
				case UrlParser.SEPARATOR_SHARP:
					result.addPath(ctx.getCurrentString());
					ctx.setState(UrlContext.STATE_FRAG);
					return;
				default:
					//�ƹ��͵� ���� �ʴ´�. ctx ������ StringBuilder�� ���ڿ��� �������� ����.
			}
		}
		String value = ctx.getCurrentString();
		if(value!=null){
			result.addPath(value);
		}
		ctx.setState(UrlContext.STATE_FINISH);
	}

	@Override
	protected boolean resolve(UrlContext ctx) {
		if(ctx.getState()==UrlContext.STATE_HPART) return true;
		return false;
	}

}
