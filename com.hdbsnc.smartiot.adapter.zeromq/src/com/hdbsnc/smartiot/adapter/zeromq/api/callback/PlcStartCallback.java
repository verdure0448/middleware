package com.hdbsnc.smartiot.adapter.zeromq.api.callback;


import com.hdbsnc.smartiot.adapter.zeromq.api.ZeromqApi;
import com.hdbsnc.smartiot.common.context.IContextCallback;
import com.hdbsnc.smartiot.common.context.IContextTracer;

public class PlcStartCallback implements IContextCallback{
	
	PlcStartCallback (ZeromqApi zmqApi){
		
	}
	
	@Override
	public void responseSuccess(IContextTracer ctxTracer) {
		
		
	}

	@Override
	public void responseFail(IContextTracer ctxTracer) {
		// TODO Auto-generated method stub
		
	}

}
