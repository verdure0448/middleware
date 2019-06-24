package com.hdbsnc.smartiot.adapter.zeromq.api.callback;


import java.util.Map;

import com.hdbsnc.smartiot.adapter.zeromq.api.ZeromqApi;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.IContextCallback;
import com.hdbsnc.smartiot.common.context.IContextTracer;

public class RepCallback implements IContextCallback{
	
	private ZeromqApi zmqApi = null;
	
	public RepCallback (ZeromqApi pZmqApi){
		zmqApi = pZmqApi;
	}
	 
	@Override
	public void responseSuccess(IContextTracer ctxTracer) {
		IContext ICtx = ctxTracer.getResponseContext();
		
		Map<String, String> params = ICtx.getParams();
		
		String res = params.get("response");
		
		try {
			zmqApi.send(res.getBytes("UTF-8"));
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void responseFail(IContextTracer ctxTracer) {
		// TODO Auto-generated method stub
		
	}

}
