package com.hdbsnc.smartiot.common.otp.url.parser;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.otp.IOtp;
import com.hdbsnc.smartiot.common.otp.impl.Otp;
import com.hdbsnc.smartiot.common.otp.url.IAuthority;
import com.hdbsnc.smartiot.common.otp.url.IFragment;
import com.hdbsnc.smartiot.common.otp.url.IHierarchicalPart;
import com.hdbsnc.smartiot.common.otp.url.IQuery;
import com.hdbsnc.smartiot.common.otp.url.IScheme;
import com.hdbsnc.smartiot.common.otp.url.parser.vo.Frag;
import com.hdbsnc.smartiot.common.otp.url.parser.vo.HPart;
import com.hdbsnc.smartiot.common.otp.url.parser.vo.Query;
import com.hdbsnc.smartiot.common.otp.url.parser.vo.Url;

public class UrlParser {

	public static final char SEPARATOR_SLASH = '/';
	public static final char SEPARATOR_COLON = ':';
	public static final char SEPARATOR_AT = '@';
	public static final char SEPARATOR_QUESTION = '?';
	public static final char SEPARATOR_AND = '&';
	public static final char SEPARATOR_EQUAL = '=';
	public static final char SEPARATOR_SHARP = '#';
	
	private static UrlParser instance = null;
	
	private AbstractHandler root;
	
	private  UrlParser(){
		this.root = new SchemeHandler();
		this.root
			.setNext(new HierarchicalPartHandler())
			.setNext(new AuthorityHandler())
			.setNext(new PathHandler())
			.setNext(new QueryHandler())
			.setNext(new FragmentHandler());
	}
	
	public synchronized static UrlParser getInstance(){
		if(instance==null) {
			instance = new UrlParser();
		}
		return instance;
	}
	
	public Url parse(String packet) throws UrlParseException{
		return root.process(new UrlContext(packet)).getResult();
	}
	
	public static String encodeUTF8(String data){
		if(data!=null && !data.equals("")){
			try {
				return URLEncoder.encode(data, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return data;
			}
		}else return "";
	}
	

	
	public static String decodeUTF8(String data){
		if(data!=null && !data.equals(""))
			try {
				return URLDecoder.decode(data, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return data;
			}
		else return "";
	}
	
	public String parse(Url packetObj) throws UrlParseException{
		StringBuilder sb = new StringBuilder();
		IScheme scheme = packetObj.getScheme();
		if(scheme.getLength()==0) throw new UrlParseException("Scheme invalid: scheme is required.");
		for(int i=0;i<scheme.getLength();i++){
			sb.append(encodeUTF8(scheme.getSchemePath(i))).append(SEPARATOR_COLON);
		}
		IHierarchicalPart hPart = packetObj.getHierarchicalPart();
		if(!hPart.equals(HPart.EMPTY)){
			sb.append(SEPARATOR_SLASH).append(SEPARATOR_SLASH);
			IAuthority auth = hPart.getAuthentication();
			if(auth.isExistUserInfo()){
				//sb.append(encodeUTF8(auth.getUserInfo())).append(SEPARATOR_AT);
				sb.append(auth.getUserInfo());
				if(auth.isExistSequence()){
					sb.append(SEPARATOR_COLON).append(auth.getSequence());
				}
				sb.append(SEPARATOR_AT);
			}
			String hostName = auth.getHostname();
			if(hostName==null) throw new UrlParseException("Authority invaild: hostname is required.");
			sb.append(encodeUTF8(hostName));
			if(auth.isExistPort()){
				sb.append(SEPARATOR_COLON).append(encodeUTF8(auth.getPort()));
			}
			
			List<String> pathList = hPart.getPath();
			String path;
			for(int i=0;i<pathList.size();i++){
				path = pathList.get(i);
				sb.append(SEPARATOR_SLASH).append(encodeUTF8(path));
			}
		}
		IQuery query = packetObj.getQuery();
		if(!query.equals(Query.EMPTY)){
			Map<String, String> params = query.getParameters();
			if(params!=null){
				Iterator<Entry<String,String>> iter = params.entrySet().iterator();
				Entry<String,String> entry;
				int index = 0;
				while(iter.hasNext()){
					entry = iter.next();
					if(index==0){
						sb.append(SEPARATOR_QUESTION);
					}else{
						sb.append(SEPARATOR_AND);
					}
					sb.append(encodeUTF8(entry.getKey())).append(SEPARATOR_EQUAL).append(encodeUTF8(entry.getValue()));
					index++;
				}
			}
		}
		IFragment frag = packetObj.getFragment();
		if(!(frag.equals(Frag.EMPTY) || frag.getfragCount()==0)){
			sb.append(SEPARATOR_SHARP);
			Iterator<Entry<String, String>> iter =frag.getFrags().entrySet().iterator();
			Entry<String, String> entry;
			while(iter.hasNext()){
				entry = iter.next();
				sb.append(encodeUTF8(entry.getKey())).append(SEPARATOR_COLON).append(encodeUTF8(entry.getValue()));
				sb.append(SEPARATOR_AND);
			}
			char checkChar = sb.charAt(sb.length()-1);
			if(checkChar==SEPARATOR_AND){
				sb.deleteCharAt(sb.length()-1);
			}
		}
		sb.append("\r\n");
		return sb.toString();
	}
	
	public String parseByNormal(Url packetObj) throws UrlParseException{
		StringBuilder sb = new StringBuilder();
		IScheme scheme = packetObj.getScheme();
		if(scheme.getLength()==0) throw new UrlParseException("Scheme invalid: scheme is required.");
		for(int i=0;i<scheme.getLength();i++){
			sb.append(scheme.getSchemePath(i)).append(SEPARATOR_COLON);
		}
		IHierarchicalPart hPart = packetObj.getHierarchicalPart();
		if(!hPart.equals(HPart.EMPTY)){
			sb.append(SEPARATOR_SLASH).append(SEPARATOR_SLASH);
			IAuthority auth = hPart.getAuthentication();
			if(auth.isExistUserInfo()){
				//sb.append(auth.getUserInfo()).append(SEPARATOR_AT);
				sb.append(auth.getUserInfo());
				if(auth.isExistSequence()){
					sb.append(SEPARATOR_COLON).append(auth.getSequence());
				}
				sb.append(SEPARATOR_AT);
			}
			String hostName = auth.getHostname();
			if(hostName==null) throw new UrlParseException("Authority invaild: hostname is required.");
			sb.append(hostName);
			if(auth.isExistPort()){
				sb.append(SEPARATOR_COLON).append(auth.getPort());
			}
			
			List<String> pathList = hPart.getPath();
			String path;
			for(int i=0;i<pathList.size();i++){
				path = pathList.get(i);
				sb.append(SEPARATOR_SLASH).append(path);
			}
		}
		IQuery query = packetObj.getQuery();
		if(!query.equals(Query.EMPTY)){
			Iterator<Entry<String,String>> iter = query.getParameters().entrySet().iterator();
			Entry<String,String> entry;
			int index = 0;
			while(iter.hasNext()){
				entry = iter.next();
				if(index==0){
					sb.append(SEPARATOR_QUESTION);
				}else{
					sb.append(SEPARATOR_AND);
				}
				sb.append(entry.getKey()).append(SEPARATOR_EQUAL).append(entry.getValue());
				index++;
			}
		}
		IFragment frag = packetObj.getFragment();		
		if(!(frag.equals(Frag.EMPTY) || frag.getfragCount()==0)){
			sb.append(SEPARATOR_SHARP);
			Iterator<Entry<String, String>> iter =frag.getFrags().entrySet().iterator();
			Entry<String, String> entry;
			while(iter.hasNext()){
				entry = iter.next();
				sb.append(entry.getKey()).append(SEPARATOR_COLON).append(entry.getValue());
				sb.append(SEPARATOR_AND);
			}
			char checkChar = sb.charAt(sb.length()-1);
			if(checkChar==SEPARATOR_AND){
				sb.deleteCharAt(sb.length()-1);
			}
		}
		sb.append("\r\n");
		return sb.toString();
	}
	
	public IOtp convertToOtp(IContext outboundCtx){
		Url resUrl = Url.createOtp(outboundCtx.getPaths(), outboundCtx.getParams());
		resUrl.setHostInfo(outboundCtx.getTID(), outboundCtx.getTPort());
		resUrl.setUserInfo(outboundCtx.getSID(), outboundCtx.getSPort());
		if(outboundCtx.getTransmission()!=null) resUrl.addFrag("trans", outboundCtx.getTransmission());
		if(outboundCtx.containsContent()){
			resUrl.addFrag("cont", outboundCtx.getContentType());
		}
		Otp otp = new Otp(resUrl);
		if(outboundCtx.containsContent()){
			otp.setContent(outboundCtx.getContent());
		}
		return otp;
	}
	
	public String convertToString(IContext outboundCtx) throws UrlParseException, UnsupportedEncodingException{
		Url resUrl = Url.createOtp(outboundCtx.getPaths(), outboundCtx.getParams());
		resUrl.setHostInfo(outboundCtx.getTID(), outboundCtx.getTPort());
		resUrl.setUserInfo(outboundCtx.getSID(), outboundCtx.getSPort());
		if(outboundCtx.getTransmission()!=null) resUrl.addFrag("trans", outboundCtx.getTransmission());
		if(outboundCtx.containsContent()){
			resUrl.addFrag("cont", outboundCtx.getContentType());
		}
		StringBuilder sb = new StringBuilder();
		sb.append(parse(resUrl));
		if(outboundCtx.containsContent()){
			sb.append(new String(outboundCtx.getContent().array(), "UTF-8"));
		}
		return sb.toString();
	}
}
