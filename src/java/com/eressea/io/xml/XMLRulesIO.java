package com.eressea.io.xml;

import java.io.*;
import java.net.*;
import java.lang.reflect.*;


import com.eressea.*;
import com.eressea.io.*;
import com.eressea.resource.*;
import com.eressea.rules.*;

import com.eressea.util.file.*;
import com.eressea.util.logging.*;


import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.*;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class XMLRulesIO implements RulesIO {
	public final static Logger log = Logger.getInstance(XMLRulesIO.class);
	
	public Rules readRules(InputStream is) throws IOException {
		return readRules(FileType.createEncodingReader(is));
	} 

	private Rules readRules(Reader reader) throws IOException {
		try {
			DocumentBuilderFactory dbf = null;
			dbf = DocumentBuilderFactory.newInstance();
			
			// This makes ID/IDREF attributes to have a meaning.
			//dbf.setValidating(true);
			
			DocumentBuilder db = dbf.newDocumentBuilder();
			
			InputSource is = new InputSource(reader);
			
			// FIXME: take care of errors via org.xml.sax.ErrorHandler !!!
			//URL dtd = ResourcePathClassLoader.getResourceStatically("rules/rules.dtd");
			URL dtd = null;
			if(dtd == null) {
				log.warn("Could not find rules/rules.dtd.");
			} else {
				is.setSystemId(dtd.toString());
			}
			return readRules(db.parse(is));
		} catch(FactoryConfigurationError fce) {
			throw new XMLIOException(fce.getException());
		} catch(ParserConfigurationException e) {
			throw new XMLIOException(e);
		} catch (SAXException e) {
			throw new XMLIOException(e);
		}
	}

	private final static String T_RULES = "Rules";
	private final static String A_ID    = "id";

	private Rules readRules(Document aDocument) throws SAXException, IOException {
		// check for Rules
		if(aDocument.getDocumentElement() == null || ! T_RULES.equals(aDocument.getDocumentElement().getTagName())) {
			throw new XMLIOException("Element "+T_RULES+" is missing.");
		}
		return readRules(aDocument.getDocumentElement());
	}
	
	private Rules readRules(Element aDocumentElement) throws SAXException, IOException {
		Rules rules = new GenericRules();
		
		// two step mechanism: 
		// a) build child nodes first
		// b) descend into child nodes afterwards and build dependencies
		NodeList children = aDocumentElement.getChildNodes();
		for(int i = 0; i< children.getLength(); i++) {
			if(children.item(i).getNodeType() == Element.ELEMENT_NODE) {
				Element child = (Element) children.item(i);
				addElement(rules, child);
			}
		}
		return rules;
	}

	// use a generic builder mechanism that uses Reflection
	// this enforces a name binding between com.eressea.rules.* Files and 
	// rules.dtd, but hey, we reduce complexity by a notifiable factor
	private void addElement(Rules rules, Element child) throws SAXException, IOException {
		if(log.isDebugEnabled()) {
			log.debug("XMLRulesIO.addElement(): Adding element "+child);
		}
		String objectType = child.getTagName();
		String id = child.getAttribute(A_ID);
		if(id == null) {
			throw new XMLIOException("Missing attribute "+A_ID+" at element "+child);
		}
		
		try {
			// call get<ObjectType>(id, true);
			Object objectTypeObj = Rules.class.getMethod("get"+objectType, new Class[] { String.class, Boolean.TYPE })
				.invoke(rules, new Object[] { objectType, Boolean.TRUE });
			
			// now dynamically add all attributes (expect "id")
			NamedNodeMap attributes = child.getAttributes();
			for(int i = 0; i< attributes.getLength(); i++) {
				Attr attr = (Attr) attributes.item(i);
				if(! A_ID.equals(attr.getName())) {
					// convert attrname to Attrname
					String attrName = attr.getName().substring(0,1).toUpperCase()+attr.getName().substring(1);
					// call set<Attrname>(attrValue);
					objectTypeObj.getClass().getMethod("set"+attrName, new Class[] { String.class })
						.invoke(objectTypeObj, new Object[] { attr.getValue() });
				}
			}
		} catch(NoSuchMethodException e) {
			throw new XMLIOException(e);
		} catch(IllegalAccessException e) {
			throw new XMLIOException(e);
		} catch(InvocationTargetException e) {
			throw new XMLIOException(e);
		}

	}
}
