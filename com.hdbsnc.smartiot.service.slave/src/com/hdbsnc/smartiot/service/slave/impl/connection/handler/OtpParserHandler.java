package com.hdbsnc.smartiot.service.slave.impl.connection.handler;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.hdbsnc.smartiot.common.connection.impl.AbstractConnectionHandler;
import com.hdbsnc.smartiot.common.connection.impl.ConnectionHandleChain.Handle;
import com.hdbsnc.smartiot.common.otp.impl.Otp;
import com.hdbsnc.smartiot.common.otp.url.parser.UrlParseException;
import com.hdbsnc.smartiot.common.otp.url.parser.UrlParser;
import com.hdbsnc.smartiot.common.otp.url.parser.vo.Url;

@Deprecated
public class OtpParserHandler extends AbstractConnectionHandler{
	private UrlParser parser;
	
	
	public OtpParserHandler(){
		super("OtpParser");
		//파싱이 실패하면 뒤로 핸들러로 넘어가면 안된다. 
		this.setFailToNext(false);
		parser = UrlParser.getInstance();
	}
	
	@Override
	public boolean resolve(Handle msg) {
		if(msg.getPacket()==null) return false;
		
		BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(msg.getPacket())));
		String tempPacket = null;
		Url tempUrl = null;
		Otp tempRequest = null;
		List<Otp> otpList=null;
		int count = 0;
		try {
			while(br.ready()){
				tempPacket = br.readLine();
				System.out.println("["+this.getHandlerName()+"] read header ("+count+"): "+tempPacket);
				tempUrl = parser.parse(tempPacket);
				tempRequest = new Otp(tempUrl);
				
				if(tempRequest.hasContent()){
					tempPacket = br.readLine();
					System.out.println("["+this.getHandlerName()+"] read content ("+count+"): "+tempPacket);
					tempRequest.setContent(tempPacket.getBytes());
				}
				if(count==0 && !br.ready()) {
					msg.setMsg(tempRequest);
					return true;
				}
				if(count==0){
					otpList = new ArrayList<Otp>();
				}
				otpList.add(tempRequest);
				count++;
			}
			br.close();
			msg.setMsg(otpList);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (UrlParseException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public void success(Handle msg) {
		Object obj = msg.getMsg();
		if(obj instanceof List){
			List msgList = (List) obj;
			System.out.println("["+this.getHandlerName()+"] msg (list:"+msgList.size()+")");
		}else if(obj instanceof Otp){
			System.out.println("["+this.getHandlerName()+"] msg (single)");
		}
	}

	@Override
	public void fail(Handle msg) {
		// 오류 코드를 보내준다.
		
	}

}
