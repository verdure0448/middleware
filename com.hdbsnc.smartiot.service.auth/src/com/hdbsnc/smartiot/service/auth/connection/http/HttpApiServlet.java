package com.hdbsnc.smartiot.service.auth.connection.http;

import java.io.IOException;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hdbsnc.smartiot.common.connection.IConnection;
import com.hdbsnc.smartiot.common.otp.impl.Otp;
import com.hdbsnc.smartiot.common.otp.url.parser.UrlParseException;
import com.hdbsnc.smartiot.common.otp.url.parser.UrlParser;
import com.hdbsnc.smartiot.common.otp.url.parser.vo.Url;
import com.hdbsnc.smartiot.service.auth.connection.impl.ConnectionHandleChain;
import com.hdbsnc.smartiot.service.auth.impl.connection.HttpConnection;

public class HttpApiServlet extends HttpServlet{

	private static final String CONTENT_TYPE = "smartiot/otp";
	
	private ConnectionHandleChain chc;
	private UrlParser parser;
	
	public HttpApiServlet(ConnectionHandleChain chc){
		this.chc = chc;
		this.parser = UrlParser.getInstance();
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		//super.doPost(request, response);
		final AsyncContext asyncContext = request.startAsync();
		
		
		String contentType = request.getContentType();
		if(contentType!=null && contentType.equals(CONTENT_TYPE) && request.getContentLength()>0){
			IConnection con = new HttpConnection(asyncContext);
			byte[] packets = con.read();
			//System.out.println("출력: "+new String(packets));
			if(packets==null || packets.length==0){
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
			String packetString = new String(packets);
			String[] div = packetString.split("\r\n");
			String message = div[0];
			Url msgUrl;
			try {
				msgUrl = parser.parse(message);
			} catch (UrlParseException e) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
			Otp otp = new Otp(msgUrl);
			if(otp.hasContent()){
				if(div.length>1){
					otp.setContent(div[1].getBytes());
				}else{
					response.sendError(HttpServletResponse.SC_BAD_REQUEST);
					return;
				}
			}
			chc.handle(con, otp);
			return;
		}
		response.sendError(HttpServletResponse.SC_BAD_REQUEST);
	}
	
	
}
