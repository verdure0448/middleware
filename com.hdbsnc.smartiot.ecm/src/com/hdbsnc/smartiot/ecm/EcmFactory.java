package com.hdbsnc.smartiot.ecm;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.hdbsnc.smartiot.common.ICommonService;
import com.hdbsnc.smartiot.common.ecm.IEventContextManager;
import com.hdbsnc.smartiot.common.factory.IEventContextManagerFactory;
import com.hdbsnc.smartiot.ecm.impl.ECM;
import com.hdbsnc.smartiot.ecm.xml.EventContextXml;
import com.hdbsnc.smartiot.ecm.xml.XmlEventContextProfile;
import com.hdbsnc.smartiot.server.ServerInstance;

public class EcmFactory implements IEventContextManagerFactory{
	public static final String FILE_NAME = "EventContextList.xml";
	
	@Override
	public IEventContextManager createECM(ICommonService service, ServerInstance server) {
		ECM ecm = new ECM(service, server.getAIM(), server.getEM());
		
		EventContextXml ecXml = EventContextXml.getInstnace();
		String home_path = System.getenv("SMARTIOT_HOME");
		String path = home_path + "//conf//"+FILE_NAME;
		
		if(home_path==null) {
			home_path = System.getenv("HOME");
			if(home_path==null){
				home_path = System.getenv("USERPROFILE");
			}
			path = home_path + "//smartiot//conf//"+FILE_NAME;
		}
		
		List<XmlEventContextProfile> ecList = null;
		try {
			ecList = ecXml.loadXml(path);
			for(XmlEventContextProfile ecp: ecList){
				ecm.addEventContext(ecp);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		return ecm;
	}

}

