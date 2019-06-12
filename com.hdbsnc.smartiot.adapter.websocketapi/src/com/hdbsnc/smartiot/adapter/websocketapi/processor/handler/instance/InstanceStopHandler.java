package com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.instance;

import com.hdbsnc.smartiot.adapter.websocketapi.constant.WebSocketAdapterConst;
import com.hdbsnc.smartiot.common.aim.AimException;
import com.hdbsnc.smartiot.common.aim.IAdapterInstanceManager;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractFunctionHandler;
import com.hdbsnc.smartiot.common.pm.IProfileManager;
import com.hdbsnc.smartiot.common.pm.vo.IInstanceObj;

/**
 * ins/stop
 * 
 * @author KANG
 *
 */
public class InstanceStopHandler extends AbstractFunctionHandler {

	private IAdapterInstanceManager aim;
	private IProfileManager pm;

	public InstanceStopHandler(IProfileManager pm, IAdapterInstanceManager aim) {
		super("stop");
		this.pm = pm;
		this.aim = aim;
	}

	@Override
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		String iid = inboundCtx.getParams().get(WebSocketAdapterConst.IID);

		IInstanceObj iInstanceObj = pm.getInstanceObj(iid);
		if(iInstanceObj != null && 
				WebSocketAdapterConst.DEFAULT_ADMIN.equals(iInstanceObj.getInsType())){
			throw getCommonService().getExceptionfactory().createAppException(
					this.getClass().getName() + ":010", new String[]{iid});
		}
		
		
		try {
			aim.stop(iid);
		} catch (AimException aimEx) {
			// throw new ContextHandlerApplicationException(305,
			// CommonException.TYPE_ERROR, "인스턴스 정지에 실패 했습니다.", aimEx);
			throw getCommonService().getExceptionfactory().createAppException(this.getClass().getName() + ":005", new String[]{iid},
					aimEx);
		}

		outboundCtx.getPaths().add("ack");
		outboundCtx.setSID(inboundCtx.getSID());
		outboundCtx.setSPort(inboundCtx.getSPort());
		outboundCtx.setTID("this");
		outboundCtx.setTPort(inboundCtx.getTPort());
		outboundCtx.setTransmission("res");

	}

}
