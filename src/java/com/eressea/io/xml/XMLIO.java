package com.eressea.io.xml;

import java.io.IOException;
import java.io.Reader;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.eressea.util.logging.Logger;

public class XMLIO {
	public final static Logger log = Logger.getInstance(XMLIO.class);
	
	public Document getDocument(Reader reader) throws IOException {
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
				//log.warn("Could not find a dtd.");
			} else {
				is.setSystemId(dtd.toString());
			}
			return db.parse(is);
		} catch(FactoryConfigurationError fce) {
			throw new XMLIOException(fce.getException());
		} catch(ParserConfigurationException e) {
			throw new XMLIOException(e);
		} catch (SAXException e) {
			throw new XMLIOException(e);
		}
	}	
}
