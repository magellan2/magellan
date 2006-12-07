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

package com.eressea.swing.context;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import com.eressea.GameData;
import com.eressea.Region;
import com.eressea.Unit;
import com.eressea.demo.EMapDetailsPanel;
import com.eressea.event.EventDispatcher;
import com.eressea.event.OrderConfirmEvent;
import com.eressea.event.SelectionEvent;
import com.eressea.event.UnitOrdersEvent;
import com.eressea.relation.TeachRelation;
import com.eressea.swing.GiveOrderDialog;
import com.eressea.swing.context.actions.ContextAction;
import com.eressea.util.CollectionFactory;
import com.eressea.util.ShipRoutePlanner;
import com.eressea.util.Translations;
import com.eressea.util.UnitRoutePlanner;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 313 $
 */
public class UnitCapacityContextMenu extends JPopupMenu {

	private GameData data;
	private EventDispatcher dispatcher;

	
	/**
	 * Creates new UnitCapacityContextMenu
	 *
	 * @param unit last selected unit - is not required to be in selected objects
	 * @param selectedObjects null or Collection of selected objects
	 * @param dispatcher EventDispatcher
	 * @param data the actual GameData or World
	 */
	public UnitCapacityContextMenu(EventDispatcher dispatcher, GameData data) {
		super(":-)");

		this.data = data;
		this.dispatcher = dispatcher;

        init();
    }
    
    private void init() {
    	JMenuItem toogleAllItems = new JMenuItem(getString("menu.toggleShowAllItems.caption"));
        toogleAllItems.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                   
                }
            });
        add(toogleAllItems);
	}
  	
	

	private String getString(String key) {
		return Translations.getTranslation(this, key);
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
			defaultTranslations.put("menu.toggleShowAllItems.caption", "all Items");

		}

		return defaultTranslations;
	}

	private class RemoveUnitFromTeachersListAction implements ActionListener {
		private Unit student;
		private Unit teacher;

		/**
		 * Creates a new RemoveUnitFromTeachersListAction object.
		 *
		 * @param student TODO: DOCUMENT ME!
		 * @param teacher TODO: DOCUMENT ME!
		 */
		public RemoveUnitFromTeachersListAction(Unit student, Unit teacher) {
			this.student = student;
			this.teacher = teacher;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param e TODO: DOCUMENT ME!
		 */
		public void actionPerformed(ActionEvent e) {
			String id = student.getID().toString();
			Collection orders = teacher.getOrders();
			int i = 0;
			boolean found = false;
			String order = null;

			for(Iterator iter = orders.iterator(); iter.hasNext(); i++) {
				order = (String) iter.next();

				if(order.toUpperCase().trim().startsWith("LEHRE")) {
					if(order.indexOf(id) > -1) {
						found = true;

						break;
					}
				}
			}

			if(found) {
				String newOrder = order.substring(0, order.indexOf(id)) +
								  order.substring(java.lang.Math.min(order.indexOf(id) + 1 +
																	 id.length(), order.length()),
												  order.length());

				// FIXME(pavkovic: Problem hier!
				UnitOrdersEvent event = new UnitOrdersEvent(this, teacher);
				teacher.removeOrderAt(i, false);

				if(!newOrder.trim().equalsIgnoreCase("LEHREN") &&
					   !newOrder.trim().equalsIgnoreCase("LEHRE")) {
					// FIXME(pavkovic: Problem hier!
					teacher.addOrderAt(i, newOrder);
				}

				dispatcher.fire(event);
			}
		}
	}
}
