package com.hdbsnc.smartiot.common.otp.url.parser;

import com.hdbsnc.smartiot.common.otp.url.parser.vo.Url;


public class SchemeHandler extends AbstractHandler{

//	public static final char SEPARATOR_COLON = ':';
//	public static final char SEPARATOR_SLASH = '/';
//	public static final char SEPARATOR_QUESTION = '?';
//	public static final char SEPARATOR_SHARP = '#';
	
	@Override
	protected void parse(UrlContext ctx) throws UrlParseException {
		Url gscp = ctx.getResult();
		boolean isColon = false;
		char curChar;
		while(ctx.hasNextChar()){
			curChar = ctx.nextChar();
			if(isColon){
				switch(curChar){
				case UrlParser.SEPARATOR_SLASH:
					ctx.setState(UrlContext.STATE_HPART);
					ctx.clearBuffer();
					return;
				case UrlParser.SEPARATOR_COLON:
				case UrlParser.SEPARATOR_QUESTION:
				case UrlParser.SEPARATOR_SHARP:
					throw new UrlParseException("Scheme invalid",ctx);
				default:
					//�ƹ��͵� ���� �ʴ´�. ctx ������ StringBuilder�� ���ڿ��� �������� ����.
				}
				isColon = false;
			}else{
				switch(curChar){
				case UrlParser.SEPARATOR_COLON:
					gscp.addSchemePath(ctx.getCurrentString());
					isColon = true;
					break;
				case UrlParser.SEPARATOR_QUESTION:
					gscp.addSchemePath(ctx.getCurrentString());
					ctx.setState(UrlContext.STATE_QUERY);
					return;
				case UrlParser.SEPARATOR_SHARP:
					gscp.addSchemePath(ctx.getCurrentString());
					ctx.setState(UrlContext.STATE_FRAG);
					return;
				case UrlParser.SEPARATOR_SLASH:
					throw new UrlParseException("Scheme invalid", ctx);
				default:
					//�ƹ��͵� ���� �ʴ´�. ctx ������ StringBuilder�� ���ڿ��� �������� ����.
				}
			}
		}
		String value = ctx.getCurrentString();
		if(value!=null){
			gscp.addSchemePath(value);
		}
		if(gscp.getScheme().getLength()==0){
			throw new UrlParseException("Scheme invalid",ctx);
		}
		ctx.setState(UrlContext.STATE_FINISH);
	}

	@Override
	protected boolean resolve(UrlContext ctx) {
		//��Ű���� ������ �־�� �ϹǷ� �׻� ó���ϱ� ���� true ����
		return true;
	}


}
