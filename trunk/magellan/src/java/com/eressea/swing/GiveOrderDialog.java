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
 * $Id$
 */

package com.eressea.swing;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.eressea.util.CollectionFactory;

/**
 * DOCUMENT ME!
 *
 * @author Ulrich Küster A Dialog that asks the user for a string input
 * 		   (usually an eressea order). In Addition the user is asked, if the
 * 		   order shall extend or replace existing orders.
 */
public class GiveOrderDialog extends InternationalizedDialog {
	private JCheckBox  replaceOrders;
	private JTextField order;
	private JButton    ok;
	private JButton    cancel;

	/**
	 * Creates a new GiveOrderDialog object.
	 *
	 * @param owner TODO: DOCUMENT ME!
	 */
	public GiveOrderDialog(Frame owner) {
		super(owner, true);
		setTitle(getString("window.title"));

		Container cp = getContentPane();
		cp.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints(0, 0, 1, 1, 0, 0,
													  GridBagConstraints.WEST,
													  GridBagConstraints.BOTH,
													  new Insets(3, 3, 3, 3),
													  0, 0);

		cp.add(new JLabel(getString("window.message")), c);

		order = new JTextField();
		order.setPreferredSize(new Dimension(200, 25));
		c.gridx   = 1;
		c.weightx = 0.5;
		cp.add(order, c);

		replaceOrders = new JCheckBox(getString("chkbox.replaceOrder.title"));
		c.gridx		  = 0;
		c.gridy		  = 1;
		c.weightx     = 0;
		cp.add(replaceOrders, c);

		ok = new JButton(getString("btn.ok.caption"));
		ok.setMnemonic(getString("btn.ok.mnemonic").charAt(0));

		// actionListener is added in the show() method
		c.gridx  = 0;
		c.gridy  = 2;
		c.anchor = GridBagConstraints.EAST;
		cp.add(ok, c);

		cancel = new JButton(getString("btn.cancel.caption"));
		cancel.setMnemonic(getString("btn.cancel.mnemonic").charAt(0));
		cancel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					quit();
				}
			});
		c.gridx  = 1;
		c.anchor = GridBagConstraints.WEST;
		cp.add(cancel, c);
	}

	/**
	 * Shows the dialog and returns a String-Array with to values: - [0] : The
	 * order that was given - [1] : A String represantative of the boolean
	 * value for "replace Orders"
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String[] showGiveOrderDialog() {
		final String     retVal[]	    = new String[2];
		ActionListener   okButtonAction = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				retVal[0] = GiveOrderDialog.this.order.getText();
				retVal[1] = String.valueOf(GiveOrderDialog.this.replaceOrders.isSelected());
				quit();
			}
		};

		ok.addActionListener(okButtonAction);
		order.addActionListener(okButtonAction);
		pack();
		setLocationRelativeTo(getOwner());
		show();

		return retVal;
	}

	// pavkovic 2003.01.28: this is a Map of the default Translations mapped to this class
	// it is called by reflection (we could force the implementation of an interface,
	// this way it is more flexible.)
	// Pls use this mechanism, so the translation files can be created automagically
	// by inspecting all classes.
	private static Map defaultTranslations;

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static synchronized Map getDefaultTranslations() {
		if(defaultTranslations == null) {
			defaultTranslations = CollectionFactory.createHashtable();
			defaultTranslations.put("window.title", "Give order");
			defaultTranslations.put("window.message", "Please insert an order:");
			defaultTranslations.put("chkbox.replaceOrder.title",
									"Replace existing orders");

			defaultTranslations.put("btn.ok.caption", "OK");
			defaultTranslations.put("btn.ok.mnemonic", "o");
			defaultTranslations.put("btn.cancel.caption", "Cancel");
			defaultTranslations.put("btn.cancel.mnemonic", "c");
		}

		return defaultTranslations;
	}
}
