package com.hdbsnc.smartiot.service.command;

import java.util.Hashtable;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.hdbsnc.smartiot.server.IServerInstance;
import com.hdbsnc.smartiot.service.IService;
import com.hdbsnc.smartiot.service.command.aim.AimCommand;
import com.hdbsnc.smartiot.service.command.common.CommandStack;
import com.hdbsnc.smartiot.service.command.common.CommonCommand;
import com.hdbsnc.smartiot.service.command.ism.IsmCommand;
import com.hdbsnc.smartiot.service.command.mss.MssCommand;
import com.hdbsnc.smartiot.util.logger.Log;

public class CommandService implements IService{

	private int currentState = IService.SERVICE_STATE_REG;
	private long lastAccessedTime = 0;
	private IServerInstance server;
	private BundleContext ctx;
	private Log logger;
	
	
	public CommandService(BundleContext ctx, IServerInstance server){
		this.lastAccessedTime = System.currentTimeMillis();
		this.server = server;
		this.logger = server.getCommonService().getLogger().logger(getServiceName());
		this.ctx = ctx;
	}
	
	@Override
	public String getServiceName() {
		return "CommandService";
	}

	@Override
	public int getServiceState() {
		return this.currentState;
	}

	@Override
	public long getLastAccessedTime() {
		return lastAccessedTime;
	}
	CommandStack cStack = null;
	@Override
	public void init(Map<String, String> config) throws Exception {
		this.cStack = new CommandStack(20);
		
		this.currentState = IService.SERVICE_STATE_INIT;
	}

	ServiceRegistration ismSr;
	ServiceRegistration aimSr;
	ServiceRegistration mssSr;
	ServiceRegistration ccSr;
	
	@Override
	public void start() throws Exception {
		Hashtable props = new Hashtable();
		props.put("osgi.command.scope", "smartiot");
		props.put("osgi.command.function", new String[]{"rcmd"});
		ctx.registerService(CommandStack.class.getName(), cStack, props);
		
		IsmCommand ismCmd = new IsmCommand(cStack, server);
		ctx.registerService(IsmCommand.class.getName(), ismCmd, ismCmd.getProps());
		
		AimCommand aimCmd = new AimCommand(cStack, server);
		ctx.registerService(AimCommand.class.getName(), aimCmd, aimCmd.getProps());
		
		MssCommand mssCmd = new MssCommand(cStack, server);
		ctx.registerService(MssCommand.class.getName(), mssCmd, mssCmd.getProps());
		
		CommonCommand ccCmd = new CommonCommand(cStack, server);
		ctx.registerService(CommonCommand.class.getName(), ccCmd , ccCmd.getProps());
		
		this.currentState = IService.SERVICE_STATE_START;
	}

	@Override
	public void stop() throws Exception {
		
		ismSr.unregister();
		aimSr.unregister();
		mssSr.unregister();
		ccSr.unregister();
		this.currentState = IService.SERVICE_STATE_STOP;
	}

}
