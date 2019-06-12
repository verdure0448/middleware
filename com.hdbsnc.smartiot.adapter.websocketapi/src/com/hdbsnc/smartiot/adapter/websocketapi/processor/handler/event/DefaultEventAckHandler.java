package com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.event;

import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractFunctionHandler;

public class DefaultEventAckHandler extends AbstractFunctionHandler {

	public DefaultEventAckHandler(){
		super("ack");
	}
	
	@Override
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		//inboundCtx에 보내야할 이벤트가 고스란히 있음.
		//outboundCtx에는 inboundCtx를 deepcopy 해둔 상태이므로 따로 처리할 부분이 없다면 그대로 이벤트 전파.
		//필요하다면 경로 혹은 ack/nack등을 확인해서 이벤트를 보낼 것인지 말것인지 처리 해야함. 
		
		
//		if(inboundCtx instanceof InnerContext){
//			
//		}else{
//			
//		}
	}

}
