/*
 *  Copyright (C) 2000-2004 Roger Butenuth, Andreas Gampe,
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
import java.lang.reflect.InvocationTargetException;

import com.eressea.Rules;
import com.eressea.io.RulesIO;
import com.eressea.io.file.FileType;
import com.eressea.rules.GenericRules;
import com.eressea.util.logging.Logger;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class XMLRulesIO implements RulesIO {
	private static final Logger log = Logger.getInstance(XMLRulesIO.class);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param filetype TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 * @throws XMLIOException TODO: DOCUMENT ME!
	 */
	public Rules readRules(FileType filetype) throws IOException {
		try {
			return readRules(new XMLIO().getDocument(filetype.createReader()));
		} catch(SAXException e) {
			throw new XMLIOException(e);
		}
	}

	private static final String T_RULES = "Rules";
	private static final String A_ID = "id";

	private Rules readRules(Document aDocument) throws SAXException, IOException {
		// check for Rules
		if((aDocument.getDocumentElement() == null) ||
			   !T_RULES.equals(aDocument.getDocumentElement().getTagName())) {
			throw new XMLIOException("Element " + T_RULES + " is missing.");
		}

		return readRules(aDocument.getDocumentElement());
	}

	private Rules readRules(Element aDocumentElement) throws SAXException, IOException {
		Rules rules = new GenericRules();

		// two step mechanism: 
		// a) build child nodes first
		// b) descend into child nodes afterwards and build dependencies
		NodeList children = aDocumentElement.getChildNodes();

		for(int i = 0; i < children.getLength(); i++) {
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
			log.debug("XMLRulesIO.addElement(): Adding element " + child);
		}

		String objectType = child.getTagName();
		String id = child.getAttribute(A_ID);

		if(id == null) {
			throw new XMLIOException("Missing attribute " + A_ID + " at element " + child);
		}

		try {
			// call get<ObjectType>(id, true);
			Object objectTypeObj = Rules.class.getMethod("get" + objectType,
														 new Class[] { String.class, Boolean.TYPE })
											  .invoke(rules,
													  new Object[] { objectType, Boolean.TRUE });

			// now dynamically add all attributes (expect "id")
			NamedNodeMap attributes = child.getAttributes();

			for(int i = 0; i < attributes.getLength(); i++) {
				Attr attr = (Attr) attributes.item(i);

				if(!A_ID.equals(attr.getName())) {
					// convert attrname to Attrname
					String attrName = attr.getName().substring(0, 1).toUpperCase() +
									  attr.getName().substring(1);

					// call set<Attrname>(attrValue);
					objectTypeObj.getClass()
								 .getMethod("set" + attrName, new Class[] { String.class }).invoke(objectTypeObj,
																								   new Object[] {
																									   attr.getValue()
																								   });
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
