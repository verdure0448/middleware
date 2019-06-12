package com.hdbsnc.smartiot.common.context.handler2.impl;

import java.util.Arrays;

import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler.IDirectoryHandler;
import com.hdbsnc.smartiot.common.context.handler.IElementHandler;
import com.hdbsnc.smartiot.common.context.handler.exception.ContextHandlerUnSupportedMethodException;
import com.hdbsnc.smartiot.common.context.handler.exception.ElementNotFoundException;
import com.hdbsnc.smartiot.common.context.handler.exception.ElementNullOrEmptyPathException;
import com.hdbsnc.smartiot.common.context.handler2.IAttributeHandler;
import com.hdbsnc.smartiot.common.context.handler2.IFunctionHandler;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.ism.sm.ISession;

public class Tester implements IFunctionHandler, IAttributeHandler{

	IDirectoryHandler parent = null;
	String selfName;
	public Tester(String name){
		selfName = name;
	}
	
	public static void main(String[] args) {
		RootHandler root = new RootHandler(null, null);

		IFunctionHandler func1 = new Tester("func1");
		IFunctionHandler func2 = new Tester("func2");
		IAttributeHandler att1 = new Tester("att1");
		IAttributeHandler att2 = new Tester("att2");
		IFunctionHandler func3 = new Tester("1");
		
		
		// 전체 등록
		// 아답터에서는 등록만 한다.
		root.regHandler(func1);
		root.regHandler(func2);
		root.regHandler(att1);
		root.regHandler(att2);
		root.regHandler(func3);
		
		// MANIFEST 파일에서 읽어와서 자동 등록 
		root.createHandlerTree(Arrays.asList(
				"plc/light/1",
				"plc/light/1/func1",
				"plc/light/2/func2", 
				"att1", 
				"plc/light/att2"
				
				));
		
		
		try {
			IElementHandler handler = null;
			handler = root.findHandler("plc/light/1/func1");
			System.out.println("parent: "+ handler.getParent().getName());
			
			handler = root.findHandler("plc/light");
			System.out.println("parent: "+ handler.getParent().getName());
			if( handler instanceof IDirectoryHandler){
				System.out.println("directory");
			}else{
				System.out.println("fail.");
			}
			handler = root.findHandler("plc/light/1");
			System.out.println("parent: "+ handler.getParent().getName());
			if( handler instanceof IFunctionHandler){
				System.out.println("function");
			}else{
				System.out.println("fail.");
			}
			
		} catch (ElementNullOrEmptyPathException e1) {
			e1.printStackTrace();
		} catch (ElementNotFoundException e1) {
			e1.printStackTrace();
		}
		
		try {
			//개별 등록 
			
//			root.putHandler("", att2);
//			root.putHandler("plc/light/office1", func1);
//			root.putHandler("plc/light/office1/", func2);
//			root.putHandler("plc/light/room1", att1);
//			root.putHandler("plc/light/room2", att2);
			
			
			
			
			
			
//			
//			IElementHandler result = null;
//			result = root.findHandler(Arrays.asList("plc","light", "office1","func2"));
//			if(result!=null) System.out.println("finding success");
//			else System.out.println("finding fail");
//			result = root.findHandler(Arrays.asList("plc","light", "room2","att2"));
//			if(result!=null) System.out.println("finding success");
//			else System.out.println("finding fail");
//			result = root.findHandler("att1");
//			if(result!=null) System.out.println("finding success");
//			else System.out.println("finding fail");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	@Override
	public IDirectoryHandler getParent() {
		return parent;
	}

	@Override
	public void setParent(IDirectoryHandler handler) {
		this.parent = handler;
	}

	@Override
	public String getName() {
		return selfName;
	}

	@Override
	public int type() {
		return IElementHandler.FUNCTION | IElementHandler.ATTRIBUTE;
	}

	@Override
	public void read(IContext inCtx, OutboundContext outCtx, ISession session, String value)
			throws ContextHandlerUnSupportedMethodException, Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(IContext inCtx, OutboundContext outCtx, ISession session, String value)
			throws ContextHandlerUnSupportedMethodException, Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void create(IContext inCtx, OutboundContext outCtx, ISession session, String value)
			throws ContextHandlerUnSupportedMethodException, Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(IContext inCtx, OutboundContext outCtx, ISession session, String value)
			throws ContextHandlerUnSupportedMethodException, Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
