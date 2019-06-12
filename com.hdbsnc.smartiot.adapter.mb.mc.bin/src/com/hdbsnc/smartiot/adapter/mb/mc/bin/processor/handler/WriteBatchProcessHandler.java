package com.hdbsnc.smartiot.adapter.mb.mc.bin.processor.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import com.hdbsnc.smartiot.adapter.mb.mc.bin.api.MitsubishiQSeriesApi;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.api.frame.exception.MitsubishiQSeriesMCCompleteException;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.obj.FunctionParamParser;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.obj.RequestWriteObj;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractTransactionTimeoutFunctionHandler;
import com.hdbsnc.smartiot.common.otp.url.parser.UrlParser;
import com.hdbsnc.smartiot.util.logger.Log;

public class WriteBatchProcessHandler extends AbstractTransactionTimeoutFunctionHandler {

	private MitsubishiQSeriesApi api;
	private Log log = null;
	private Log parentsLog = null;

	public WriteBatchProcessHandler(String name, long timeout, MitsubishiQSeriesApi api, Log log) {
		super(name, timeout);

		this.api = api;
		this.parentsLog = log;
	}

	@Override
	public void transactionProcess(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		if (log == null) {
			String curtPath = (this.currentPathString().replace("/", ".")).replace("root.", "");
			this.log = parentsLog.logger(curtPath);
		}

		String jsonData = new String(inboundCtx.getContent().array(), "UTF-8");
		
		if(jsonData==null || "".equals(jsonData)) {
			log.err("JSON데이터를 확인해주세요");
			return;
		}
		
		List<RequestWriteObj> writeObjList = getWriteData(jsonData);

		try {
			try {
				// 연결이 되어 있는지 확인
				if (!api.isConnected()) {
					log.debug("재연결 시도 1.");
					try {
						// 연결이 안되어있다면 재연결
						api.reConnect();
					} catch (Exception e) {
						throw e;
					}
				}
				
				for(int i=0; i<writeObjList.size(); i++) {
					api.write(writeObjList.get(i));
						
				}

				outboundCtx.getPaths().add("ack");
				outboundCtx.setSID(inboundCtx.getSID());
				outboundCtx.setSPort(inboundCtx.getSPort());
				outboundCtx.setTID(inboundCtx.getTID());
				outboundCtx.setTPort(inboundCtx.getTPort());
				outboundCtx.setTransmission("res");
				log.trace(UrlParser.getInstance().convertToString(outboundCtx));
			} catch (MitsubishiQSeriesMCCompleteException e) {
				log.err(e);
				outboundCtx.getPaths().add("nack");
				outboundCtx.setSID(inboundCtx.getSID());
				outboundCtx.setSPort(inboundCtx.getSPort());
				outboundCtx.setTID(inboundCtx.getTID());
				outboundCtx.setTPort(inboundCtx.getTPort());
				if (outboundCtx.getParams() == null)
					outboundCtx.setParams(new HashMap<String, String>());
				outboundCtx.getParams().put("code", e.getErrorCode());
				outboundCtx.getParams().put("msg", e.getErrMsg());
				outboundCtx.setTransmission("res");
				log.err(UrlParser.getInstance().convertToString(outboundCtx));
			} catch (IOException e) {
				log.err(e);
				log.debug("재연결 시도 2.");
				try {
					api.reConnect();
					for(int i=0; i<writeObjList.size(); i++) {
						api.write(writeObjList.get(i));
					}
				} catch (Exception e1) {
					throw e1;
				}
			} catch (Exception e) {
				throw e;
			}
		} catch (Exception e) {
			log.err(e);
			outboundCtx.getPaths().add("nack");
			outboundCtx.setSID(inboundCtx.getSID());
			outboundCtx.setSPort(inboundCtx.getSPort());
			outboundCtx.setTID(inboundCtx.getTID());
			outboundCtx.setTPort(inboundCtx.getTPort());
			if (outboundCtx.getParams() == null)
				outboundCtx.setParams(new HashMap<String, String>());
			outboundCtx.getParams().put("msg", e.getMessage());
			outboundCtx.getParams().put("type", "error");
			outboundCtx.setTransmission("res");
			log.err(UrlParser.getInstance().convertToString(outboundCtx));
		}
	}

	private List<RequestWriteObj> getWriteData(String jsonData) throws Exception {

		FunctionParamParser parser = new FunctionParamParser(jsonData);
		parser.parser();
		
		return parser.getRequestWriteObjList();
	}

	@Override
	public void rejectionProcess(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		outboundCtx.getPaths().add("nack");
		outboundCtx.setSID(inboundCtx.getSID());
		outboundCtx.setSPort(inboundCtx.getSPort());
		outboundCtx.setTID(inboundCtx.getTID());
		outboundCtx.setTPort(inboundCtx.getTPort());
		if (outboundCtx.getParams() == null)
			outboundCtx.setParams(new HashMap<String, String>());
		outboundCtx.getParams().put("code", "W9001");
		outboundCtx.getParams().put("type", "warn");
		outboundCtx.getParams().put("msg", "트랜젝션이 잠겨 있습니다.(다른 request가 선행 호출되어 있을 수 있습니다.)");
		outboundCtx.setTransmission("res");

		log.warn("핸들러 트랜젝션 경고 : " + UrlParser.getInstance().convertToString(outboundCtx));
	}

}
