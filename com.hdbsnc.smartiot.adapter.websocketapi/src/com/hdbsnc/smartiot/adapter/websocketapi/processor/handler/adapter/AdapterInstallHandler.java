package com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.adapter;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import com.hdbsnc.smartiot.adapter.websocketapi.constant.WebSocketAdapterConst;
import com.hdbsnc.smartiot.common.am.AmException;
import com.hdbsnc.smartiot.common.am.IAdapterManager;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractFunctionHandler;
import com.hdbsnc.smartiot.common.ism.sm.ISession;

/**
 * 
 * 
 * @author dhkang
 *
 */
public class AdapterInstallHandler extends AbstractFunctionHandler {

	private IAdapterManager am;

	public AdapterInstallHandler(IAdapterManager am) {
		super("install");
		this.am = am;
	}

	@Override
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		String fileName = inboundCtx.getParams().get(WebSocketAdapterConst.ADT_FILE_NAME);
		String fileSize = inboundCtx.getParams().get(WebSocketAdapterConst.ADT_FILE_SIZE);
		String totalSeq = inboundCtx.getParams().get(WebSocketAdapterConst.ADT_TOTAL_SEQUENCE);
		String currSeq = inboundCtx.getParams().get(WebSocketAdapterConst.ADT_CURRENT_SEQUENCE);
		//currSeq 는 1부터 시작함. 
		
		String sid = inboundCtx.getSID();
		ISession session =null;
		if(sid!=null && sid.startsWith("sid-")){
			session = getSessionManager().getSessionBySessionKey(sid);
		}else{
			session = getSessionManager().getSessionByDeviceId(sid);
		}
		
		String bufferKey = fileName;
		Map<String, IContext> tempBuffer = getFileBuffer(session, bufferKey, inboundCtx, currSeq);
		System.out.println("Downloading. (fileName: "+fileName+", fileSize: "+fileSize+", currSize: "+inboundCtx.getContent().limit()+", "+currSeq+"/"+totalSeq+")");
		int total = Integer.parseInt(totalSeq);
		
		if(tempBuffer.size()!=total){
			
			outboundCtx.dispose();
			return;
		}//각 요청에 대해서 잘받았다고 응답을 줄것인가?
		
		System.out.println("Download Completed. (fileName: "+fileName+", fileSize: "+fileSize+")");
		ByteBuffer buffer = null;
		try{
			buffer = createByteBuffer(tempBuffer);
			session.removeBuffers(bufferKey);
		}catch(Exception e){
			//파일을 합치는데 오류가 발생함.
			throw getCommonService().getExceptionfactory().createSysException("207", new String[]{"파일 전송시 일부 누락이 발견 되었습니다.", fileName, fileSize});
		}
		if(buffer==null) throw getCommonService().getExceptionfactory().createSysException("207", new String[]{"파일 전송시 일부 누락이 발견 되었습니다.", fileName, fileSize});
		
		if(!fileName.endsWith(".jar")) throw getCommonService().getExceptionfactory().createSysException("207", new String[]{"jar 파일만 설치가 가능합니다.", fileName, fileSize});
		try{		
			am.installAdapter(fileName, fileSize, buffer);
		}catch(AmException e){
			throw getCommonService().getExceptionfactory().createSysException("207", new String[]{e.getMessage(), fileName, fileSize});
		}catch(Exception e){
			throw getCommonService().getExceptionfactory().createSysException("207", new String[]{e.getMessage(), fileName, fileSize});
		}
		outboundCtx.getPaths().add("ack");
		outboundCtx.setSID(inboundCtx.getSID());
		outboundCtx.setSPort(inboundCtx.getSPort());
		outboundCtx.setTID("this");
		outboundCtx.setTPort(inboundCtx.getTPort());
		outboundCtx.setTransmission("res");		
	}
	
	synchronized Map<String, IContext> getFileBuffer(ISession session, String bufferKey, IContext inboundCtx, String currSeq){
		Object obj = session.getBuffers(bufferKey);
		Map<String, IContext> tempMap;
		if(obj==null){
			tempMap = new HashMap<String,IContext>();
		}else{
			tempMap = (Map<String,IContext>) obj;
		}
		tempMap.put(currSeq,inboundCtx);
		session.putBuffers(bufferKey, tempMap);
		return tempMap;
	}
	
	synchronized ByteBuffer createByteBuffer(Map<String,IContext> map) throws Exception{
		IContext ctx;
		String currSeq;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		for(int i=1,s=map.size();i<=s;i++){
			currSeq = String.valueOf(i);
			ctx = map.get(currSeq);
			if(ctx==null || !ctx.containsContent()) throw new Exception("순서("+currSeq+")에 해당하는 다운로드 데이터가 없습니다.");
			out.write(ctx.getContent().array());
		}
		return ByteBuffer.wrap(out.toByteArray());
	}

}
