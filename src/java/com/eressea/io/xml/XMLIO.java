/*
 *  Copyright (C) 2000-2003 Roger Butenuth, Andreas Gampe,
 *                          Stefan Goetz, Sebastian Pappert,
 *                          Klaas Prause, Enno Rehling,
 *                          Sebastian Tusk, Ulrich Kuester,
 *                          Ilja Pavkovic
 *
 * This file is part of the Eressea Java Code Base, see the
 * file LICENSING for the licensing information applying to
 * this file.
 *
 */

package com.eressea.io.xml;

import java.io.IOException;
import java.io.Reader;

import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import com.eressea.util.logging.Logger;

import org.w3c.dom.Document;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class XMLIO {
	/** TODO: DOCUMENT ME! */
	public static final Logger log = Logger.getInstance(XMLIO.class);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param reader TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 * @throws XMLIOException TODO: DOCUMENT ME!
	 */
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
		} catch(SAXException e) {
			throw new XMLIOException(e);
		}
	}
}
