package com.hdbsnc.smartiot.common.otp.url.parser;

import com.hdbsnc.smartiot.common.otp.url.parser.vo.Auth;
import com.hdbsnc.smartiot.common.otp.url.parser.vo.Url;

public class AuthorityHandler extends AbstractHandler{

//	public static final char SEPARATOR_SLASH = '/';
//	public static final char SEPARATOR_COLON = ':';
//	public static final char SEPARATOR_AT = '@';
//	public static final char SEPARATOR_SHARP = '#';
//	public static final char SEPARATOR_QUESTION = '?';
	
	@Override
	protected void parse(UrlContext ctx) throws UrlParseException {
		Url result = ctx.getResult();
		char curChar;
		String value1 = null;
		String value2 = null;
		wh:while(ctx.hasNextChar()){
			curChar = ctx.nextChar();	
			sw:switch(curChar){
				case UrlParser.SEPARATOR_COLON:
					if(value1!=null) throw new UrlParseException("Authority invalid", ctx);
					value1 = ctx.getCurrentString();
					break sw;
				case UrlParser.SEPARATOR_AT:
					value2 = ctx.getCurrentString();
					if(value1!=null){
						//result.setUserInfo(value1+':'+value2);
						result.setUserInfo(value1, value2);
					}else{
						result.setUserInfo(value2, Auth.EMPTY_VALUE);
					}
					value1 = null;
					value2 = null;
					break sw;
				case UrlParser.SEPARATOR_SLASH:
					setHostInfo(result, value1, ctx.getCurrentString());
					return;
				case UrlParser.SEPARATOR_QUESTION:
					if(value1==null){
						value1 = ctx.getCurrentString();
					}else{
						value2 = ctx.getCurrentString();
					}
					if(value1==null || value1.equals("")) throw new UrlParseException("Authority host invalid", ctx);
					setHostInfo(result, value1, value2);
					ctx.setState(UrlContext.STATE_QUERY);
					return;
				case UrlParser.SEPARATOR_SHARP:
					if(value1==null){
						value1 = ctx.getCurrentString();
					}else{
						value2 = ctx.getCurrentString();
					}
					if(value1==null || value1.equals("")) throw new UrlParseException("Authority host invalid", ctx);
					setHostInfo(result, value1, ctx.getCurrentString());
					ctx.setState(UrlContext.STATE_FRAG);
					return;
				default:
					//�ƹ��͵� ���� �ʴ´�. ctx ������ StringBuilder�� ���ڿ��� �������� ����.
			}
		}
		if(value1==null){
			value1 = ctx.getCurrentString();
		}else{
			value2 = ctx.getCurrentString();
		}
		if(value1==null || value1.equals("")) throw new UrlParseException("Authority invalid: URI grammer [username:password@hostname:port]");
		setHostInfo(result, value1, value2);
		ctx.setState(UrlContext.STATE_FINISH);
	}
	
	private void setHostInfo(Url result, String value1, String value2){
		if(value1!=null){
			result.setHostInfo(value1, value2);
		}else{
			result.setHostInfo(value2, Auth.EMPTY_VALUE);
		}
	}

	@Override
	protected boolean resolve(UrlContext ctx) {
		if(ctx.getState()==UrlContext.STATE_HPART) return true;
		return false;
	}

	

}
