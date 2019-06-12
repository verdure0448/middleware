package com.hdbsnc.smartiot.common.otp.url.parser;

import com.hdbsnc.smartiot.common.otp.url.parser.vo.HPart;

public class HierarchicalPartHandler extends AbstractHandler {

//	public static final char SEPARATOR_SLASH = '/';

	@Override
	protected void parse(UrlContext ctx) throws UrlParseException {
		if(ctx.hasNextChar()){
			char temp = ctx.nextChar();
			if(temp==UrlParser.SEPARATOR_SLASH){
				ctx.clearBuffer();
				return;
			}
		}
		
		throw new UrlParseException("HPart invalid",ctx);
		
		
	}

	@Override
	protected boolean resolve(UrlContext ctx) {
		if(ctx.getState()==UrlContext.STATE_HPART){
			return true;
		}
		ctx.getResult().setHPart(HPart.EMPTY);
		return false;
	}
}
