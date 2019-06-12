package com.hdbsnc.smartiot.ecm.xml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class EventContextXml {

	static EventContextXml instance = null;
	
	public static synchronized EventContextXml getInstnace(){
		if(instance==null){
			instance = new EventContextXml();
		}
		return instance;
	}
	
	public List<XmlEventContextProfile> loadXml(String pathAndFileName) throws ParserConfigurationException, SAXException, IOException{
		List<XmlEventContextProfile> resultList = new ArrayList<XmlEventContextProfile>();
		File xmlFile = new File(pathAndFileName);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(xmlFile);
		doc.getDocumentElement().normalize();
		NodeList eventContextList = doc.getElementsByTagName(XmlEventContextProfile.TAG_EVENTCONTEXT);
		Node ecXml;
		Element ecEle;
		XmlEventContextProfile ec;
		Node tempNode;
		NodeList tempNodeList;
		for(int i=0,s=eventContextList.getLength();i<s;i++){
			ecXml = eventContextList.item(i);
			if(ecXml.getNodeType()==Node.ELEMENT_NODE){
				ecEle = (Element) ecXml;
				ec = new XmlEventContextProfile();
				ec.isAutoStart = ecEle.getAttribute(XmlEventContextProfile.ATT_ISAUTOSTART);
				tempNode = ecEle.getElementsByTagName(XmlEventContextProfile.TAG_EID).item(0);
				if(tempNode!=null) ec.eid = tempNode.getTextContent();
				tempNode = ecEle.getElementsByTagName(XmlEventContextProfile.TAG_NAME).item(0);
				if(tempNode!=null) ec.name = tempNode.getTextContent();
				tempNode = ecEle.getElementsByTagName(XmlEventContextProfile.TAG_TYPE).item(0);
				if(tempNode!=null) ec.type = tempNode.getTextContent();
				tempNode = ecEle.getElementsByTagName(XmlEventContextProfile.TAG_REMARK).item(0);
				if(tempNode!=null) ec.remark = tempNode.getTextContent();
				
				tempNode = ecEle.getElementsByTagName(XmlEventContextProfile.TAG_DEVICELIST).item(0);
				if(tempNode!=null){
					tempNodeList = tempNode.getChildNodes();
					if(tempNodeList!=null) ec.deviceList = createDeviceList(tempNodeList);
				}
				
				tempNode = ecEle.getElementsByTagName(XmlEventContextProfile.TAG_PATTERNLIST).item(0);
				if(tempNode!=null){
					tempNodeList = tempNode.getChildNodes();
					if(tempNodeList!=null) ec.patternList = createPatternList(tempNodeList);
				}
				tempNode = ecEle.getElementsByTagName(XmlEventContextProfile.TAG_EVENTHANDLERLIST).item(0);
				if(tempNode!=null){
					tempNodeList = tempNode.getChildNodes(); //EventHandler를 리스트로 가져온다.
					if(tempNodeList!=null) ec.eventHandlerList = createEventHandlerList(tempNodeList);
				}
				resultList.add(ec);
			}
		}
		return resultList;
	}
	
//	List<Attribute> createAttributeList(NodeList tempNodeList){
//		List<Attribute> attributeList = new ArrayList<Attribute>();
//		Attribute a;
//		Node tempNode;
//		Element ele;
//		Attribute att;
//		for(int i=0,s=tempNodeList.getLength();i<s;i++){
//			tempNode = tempNodeList.item(i);
//			if(tempNode.getNodeType()!=Node.ELEMENT_NODE) continue;
//			att = new Attribute();
//			ele = (Element) tempNode;
//			att.did = ele.getAttribute(Attribute.ATT_DID);
//			att.path = ele.getAttribute(Attribute.ATT_PATH);
//			att.method = ele.getAttribute(Attribute.ATT_METHOD);
//			att.intervalsec = ele.getAttribute(Attribute.ATT_INTERVALSEC);
//			att.value = ele.getTextContent();
//			attributeList.add(att);
//		}
//		return attributeList;
//	}
	
	List<String> createDeviceList(NodeList tempNodeList){
		List<String> deviceList = new ArrayList<String>();
		Node tempNode;
		String did;
		for(int k=0,l=tempNodeList.getLength();k<l;k++){
			tempNode = tempNodeList.item(k);
			if(tempNode.getNodeType()!=Node.ELEMENT_NODE) continue;
			did = tempNode.getTextContent();
			deviceList.add(did);
		}
		return deviceList;
	}
	
	List<Pattern> createPatternList(NodeList tempNodeList){
		List<Pattern> patternList = new ArrayList<Pattern>();
		Pattern p;
		String patternString;
		Node tempNode;
		Element ele;
		for(int k=0,l=tempNodeList.getLength();k<l;k++){
			tempNode = tempNodeList.item(k);
			if(tempNode.getNodeType()!=Node.ELEMENT_NODE) continue;
			patternString = tempNode.getTextContent();
			if(patternString!=null){
				p = new Pattern();
				p.pattern = patternString;
				patternList.add(p);
			}
		}
		return patternList;
	}
	
	List<XmlEventHandlerProfile> createEventHandlerList(NodeList tempNodeList){
		List<XmlEventHandlerProfile> eventHandlerList = new ArrayList<XmlEventHandlerProfile>();
		XmlEventHandlerProfile eh;
		Element elEh;
		Node tempNode;
		NodeList childNodeList;
		for(int m=0,n=tempNodeList.getLength();m<n;m++){
			tempNode = tempNodeList.item(m);
			if(tempNode.getNodeType()==Node.ELEMENT_NODE){
				eh = new XmlEventHandlerProfile();
				elEh = (Element) tempNode;
				eh.seq = elEh.getAttribute(XmlEventHandlerProfile.ATT_SEQ);
				tempNode = elEh.getElementsByTagName(XmlEventHandlerProfile.TAG_EHID).item(0);
				if(tempNode!=null) eh.ehid = tempNode.getTextContent();
				tempNode = elEh.getElementsByTagName(XmlEventHandlerProfile.TAG_NAME).item(0);
				if(tempNode!=null) eh.name = tempNode.getTextContent();
				tempNode = elEh.getElementsByTagName(XmlEventHandlerProfile.TAG_TYPE).item(0);
				if(tempNode!=null) eh.type = tempNode.getTextContent();
				tempNode = elEh.getElementsByTagName(XmlEventHandlerProfile.TAG_REMARK).item(0);
				if(tempNode!=null) eh.remark = tempNode.getTextContent();
				tempNode = elEh.getElementsByTagName(XmlEventHandlerProfile.TAG_PARAMETERLIST).item(0);
				if(tempNode!=null){
					childNodeList = tempNode.getChildNodes();
					if(childNodeList!=null) eh.parameters = createParameters(childNodeList);
				}
				eventHandlerList.add(eh);
			}
			
		}
		return eventHandlerList;
	}
	
	Map<String, String> createParameters(NodeList tempNodeList){
		Map<String, String> resultMap = new HashMap<String, String>();
		Node tempNode;
		Element ele;
		String key, value;
		for(int i=0,s=tempNodeList.getLength();i<s;i++){
			tempNode = tempNodeList.item(i);
			if(tempNode.getNodeType()==Node.ELEMENT_NODE){
				ele = (Element) tempNode;
				key = ele.getAttribute("name");
				value = ele.getTextContent();
				resultMap.put(key, value);
			}
		}
		return resultMap;
	}
}
