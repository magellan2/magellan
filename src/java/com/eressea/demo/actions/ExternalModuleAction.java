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

package com.eressea.demo.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import com.eressea.demo.Client;

import com.eressea.extern.ExternalModule;
import com.eressea.extern.ExternalModule2;

import com.eressea.util.logging.Logger;

/**
 * DOCUMENT ME!
 *
 * @author Ilja Pavkovic
 * @version
 */
public class ExternalModuleAction extends AbstractAction {
	private static final Logger log		 = Logger.getInstance(ExternalModuleAction.class);
	private Client			    client;
	private Object			    external;

	/**
	 * Creates a new ExternalModuleAction object.
	 *
	 * @param c TODO: DOCUMENT ME!
	 * @param name TODO: DOCUMENT ME!
	 * @param o TODO: DOCUMENT ME!
	 */
	public ExternalModuleAction(Client c, String name, Object o) {
		client   = c;
		external = o;
		putValue(Action.NAME, name);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param e TODO: DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent e) {
		try {
			log.warn("ExternalModuleAction.actionPerformed on " +
					 external.getClass() + " delivered by " +
					 external.getClass().getProtectionDomain().getCodeSource()
							 .getLocation());
		} catch(Exception ex) {
			if(log.isDebugEnabled()) {
				log.debug(ex);
			}
		}

		if(external instanceof ExternalModule) {
			ExternalModule extModule = (ExternalModule) external;
			extModule.start(client.getData(), client.getDispatcher(),
							client.getSettings());
		}

		if(external instanceof ExternalModule2) {
			ExternalModule2 extModule = (ExternalModule2) external;
			extModule.start(client);
		}
	}
}
