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

package com.eressea.swing.context;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import com.eressea.GameData;
import com.eressea.Region;
import com.eressea.TempUnit;
import com.eressea.Unit;
import com.eressea.demo.EMapDetailsPanel;
import com.eressea.event.OrderConfirmEvent;
import com.eressea.event.UnitOrdersEvent;
import com.eressea.relation.TeachRelation;
import com.eressea.swing.GiveOrderDialog;
import com.eressea.util.CollectionFactory;
import com.eressea.util.ShipRoutePlanner;
import com.eressea.util.UnitRoutePlanner;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class UnitContextMenu extends JPopupMenu {
	private Unit unit;
	private GameData data;
	private com.eressea.event.EventDispatcher dispatcher;

	/**
	 * The selected Units (that are a subset of the selected objects in the overview tree). Notice:
	 * this.unit does not need to be element of this collection!
	 */
	private Collection selectedUnits = CollectionFactory.createLinkedList();

	/**
	 * Creates new UnitContextMenu
	 *
	 * @param unit TODO: DOCUMENT ME!
	 * @param selectedObjects TODO: DOCUMENT ME!
	 * @param dispatcher TODO: DOCUMENT ME!
	 * @param data TODO: DOCUMENT ME!
	 */
	public UnitContextMenu(Unit unit, Collection selectedObjects,
						   com.eressea.event.EventDispatcher dispatcher, GameData data) {
		super(unit.toString());
		this.unit = unit;
		this.data = data;
		this.dispatcher = dispatcher;

		if(selectedObjects != null) {
			for(Iterator iter = selectedObjects.iterator(); iter.hasNext();) {
				Object o = iter.next();

				if(o instanceof Unit) {
					selectedUnits.add(o);
				}
			}
		}

		if(selectedUnits.size() <= 1) {
			// This part for single-unit-selections
			JMenuItem unitString = new JMenuItem(unit.toString());
			unitString.setEnabled(false);
			add(unitString);

			JMenuItem copyUnitID = new JMenuItem(getString("menu.copyid.caption"));
			copyUnitID.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						event_copyID(e);
					}
				});
			add(copyUnitID);

			JMenuItem copyUnitNameID = new JMenuItem(getString("menu.copyidandname.caption"));
			copyUnitNameID.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						event_copyNameID(e);
					}
				});
			add(copyUnitNameID);

			if(EMapDetailsPanel.isPrivilegedAndNoSpy(unit)) {
				JMenuItem hideID = new JMenuItem(getString("menu.disguise.caption"));

				hideID.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							event_hideID(e);
						}
					});
				add(hideID);
			}

			// is student of someone?
			Collection c = unit.getRelations(TeachRelation.class);

			for(Iterator iter = c.iterator(); iter.hasNext();) {
				TeachRelation tr = (TeachRelation) iter.next();

				if(tr.target == unit) {
					Unit teacher = tr.source;
					JMenuItem removeFromTeachersList = new JMenuItem(getString("menu.removeFromTeachersList") +
																	 ": " + teacher.toString());
					add(removeFromTeachersList);
					removeFromTeachersList.addActionListener(new RemoveUnitFromTeachersListAction(unit,
																								  teacher));
				}
			}
			if(unit.getShip() != null && unit.equals(unit.getShip().getOwnerUnit()))  {
				JMenuItem planShipRoute = new JMenuItem(getString("menu.planshiproute.caption"));
				planShipRoute.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							planShipRoute();
						}
					});
				planShipRoute.setEnabled(ShipRoutePlanner.canPlan(unit.getShip()));
				add(planShipRoute);
			}
		} else {
			// this part for multiple unit-selections
			JMenuItem unitString = new JMenuItem(selectedUnits.size() + " " + getString("units"));
			unitString.setEnabled(false);
			add(unitString);

			JMenuItem copyMultipleID = new JMenuItem(getString("menu.copyids.caption"));
			copyMultipleID.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						event_copyMultipleID(e);
					}
				});
			add(copyMultipleID);

			JMenuItem copyMultipleNameID = new JMenuItem(getString("menu.copyidsandnames.caption"));
			copyMultipleNameID.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						event_copyMultipleNameID(e);
					}
				});
			add(copyMultipleNameID);
		}

		// this part for both (but only for selectedUnits)
		if(containsPrivilegedUnit()) {
			JMenuItem validateOrders = new JMenuItem(getString("menu.confirm.caption"));
			validateOrders.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						event_confirmOrders();
					}
				});
			add(validateOrders);

			JMenuItem addOrder = new JMenuItem(getString("menu.addorder.caption"));
			addOrder.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						event_addOrder();
					}
				});
			add(addOrder);
		}

		// test route planning capability
		boolean canPlan = UnitRoutePlanner.canPlan(unit);
		Region reg = unit.getRegion();

		if(canPlan && (selectedUnits != null)) {
			Iterator it = selectedUnits.iterator();

			while(canPlan && it.hasNext()) {
				Unit u = (Unit) it.next();
				canPlan = UnitRoutePlanner.canPlan(u);

				if((u.getRegion() == null) || !reg.equals(u.getRegion())) {
					canPlan = false;
				}
			}
		}

		if(canPlan) {
			if(getComponentCount() > 0) {
				addSeparator();
			}

			JMenuItem planRoute = new JMenuItem(getString("menu.planroute"));

			planRoute.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						planRoute(e);
					}
				});
			add(planRoute);
		}
	}

	/**
	 * Gives an order (optional replacing the existing ones) to the selected units.
	 */
	private void event_addOrder() {
		String s[] = (new GiveOrderDialog(JOptionPane.getFrameForComponent(this))).showGiveOrderDialog();

		// remember: s[0] : inserted Order (null if the ok-button wasn't pressed)
		// s[1] : String represantative for "replace order ?"
        // c[2] : String represantative for "keep comments"
		if(s[0] != null) {
			boolean replace = Boolean.valueOf(s[1]).booleanValue();
            boolean keepComments = Boolean.valueOf(s[2]).booleanValue();
			for(Iterator iter = selectedUnits.iterator(); iter.hasNext();) {
				Unit u = (Unit) iter.next();

				if(EMapDetailsPanel.isPrivilegedAndNoSpy(u)) {
					if(replace) {
                        if (keepComments) {
                            Collection oldOrders = u.getOrders();
                            Collection newOrders = CollectionFactory.createLinkedList();
                            for (Iterator iterator = oldOrders.iterator(); iterator.hasNext(); ) {
                                String order = (String)iterator.next();
                                if (order.trim().startsWith("//") || order.trim().startsWith(";")) {
                                    newOrders.add(order);
                                }
                            }
                            newOrders.add(s[0]);
                            u.setOrders(newOrders);
                        } else {
							u.setOrders(Collections.singleton(s[0]));
						}
					} else {
						u.addOrder(s[0], false, 0);
					}

					dispatcher.fire(new UnitOrdersEvent(this, u));
				}
			}
		}

		unit = null;
		selectedUnits.clear();
	}

	/**
	 * Changes the confirmation state of the selected units.
	 */
	private void event_confirmOrders() {
		boolean status = !((Unit) selectedUnits.iterator().next()).ordersConfirmed;

		for(Iterator iter = selectedUnits.iterator(); iter.hasNext();) {
			Unit u = (Unit) iter.next();
			u.ordersConfirmed = status;
		}

		dispatcher.fire(new OrderConfirmEvent(this, selectedUnits));

		unit = null;
		selectedUnits.clear();
	}

	private void event_copyID(ActionEvent e) {
		String idString = null;

		if(unit instanceof TempUnit) {
			idString = "TEMP " + unit.getID().toString();
		} else {
			idString = unit.getID().toString();
		}

		java.awt.datatransfer.StringSelection strSel = new java.awt.datatransfer.StringSelection(idString);

		java.awt.datatransfer.Clipboard cb = getToolkit().getSystemClipboard();

		cb.setContents(strSel, null);

		unit = null;
		selectedUnits.clear();
	}

	private void event_copyNameID(ActionEvent e) {
		java.awt.datatransfer.StringSelection strSel = new java.awt.datatransfer.StringSelection(unit.toString());

		java.awt.datatransfer.Clipboard cb = getToolkit().getSystemClipboard();

		cb.setContents(strSel, null);

		unit = null;
		selectedUnits.clear();
	}

	private void event_hideID(ActionEvent e) {
		data.getGameSpecificStuff().getOrderChanger().addMultipleHideOrder(unit);
		dispatcher.fire(new UnitOrdersEvent(this, unit));

		unit = null;
		selectedUnits.clear();
	}

	private void event_copyMultipleID(ActionEvent e) {
		String idString = "";

		for(Iterator iter = selectedUnits.iterator(); iter.hasNext();) {
			Unit u = (Unit) iter.next();

			if(u instanceof TempUnit) {
				idString += ("TEMP " + u.getID().toString() + " ");
			} else {
				idString += (u.getID().toString() + " ");
			}
		}

		StringSelection strSel = new StringSelection(idString);
		Clipboard cb = getToolkit().getSystemClipboard();
		cb.setContents(strSel, null);

		unit = null;
		selectedUnits.clear();
	}

	private void event_copyMultipleNameID(ActionEvent e) {
		String s = "";

		for(Iterator iter = selectedUnits.iterator(); iter.hasNext();) {
			Unit u = (Unit) iter.next();
			s += (u.toString() + "\n");
		}

		StringSelection strSel = new StringSelection(s);
		Clipboard cb = getToolkit().getSystemClipboard();
		cb.setContents(strSel, null);

		unit = null;
		selectedUnits.clear();
	}

	private void planRoute(ActionEvent e) {
		if(UnitRoutePlanner.planUnitRoute(unit, data, this, selectedUnits)) {
			if(selectedUnits != null) {
				Iterator it = selectedUnits.iterator();

				while(it.hasNext()) {
					Unit u = (Unit) it.next();

					if(!u.equals(unit)) {
						dispatcher.fire(new UnitOrdersEvent(this, u));
					}
				}
			}

			dispatcher.fire(new UnitOrdersEvent(this, unit));
		}

		unit = null;
		selectedUnits.clear();
	}

	private void planShipRoute() {
		Unit unit = ShipRoutePlanner.planShipRoute(this.unit.getShip(), data, this);

		if(unit != null) {
			dispatcher.fire(new UnitOrdersEvent(this, unit));
		}
	}

	/**
	 * Checks whether the selectedUnits contain at least one Unit-object, that belongs to a
	 * privileged faction.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	private boolean containsPrivilegedUnit() {
		for(Iterator iter = selectedUnits.iterator(); iter.hasNext();) {
			Unit u = (Unit) iter.next();

			if(EMapDetailsPanel.isPrivilegedAndNoSpy(u)) {
				return true;
			}
		}

		return false;
	}

	private String getString(String key) {
		return com.eressea.util.Translations.getTranslation(this, key);
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
			defaultTranslations.put("menu.copyid.caption", "Copy ID");
			defaultTranslations.put("menu.copyidandname.caption", "Copy ID and name");
			defaultTranslations.put("menu.disguise.caption", "Disguise unit");
			defaultTranslations.put("units", "units");
			defaultTranslations.put("menu.addorder.caption", "Add order");
			defaultTranslations.put("menu.planroute", "Plan route");
			defaultTranslations.put("menu.planshiproute.caption", "Ship route scheduler");
			defaultTranslations.put("menu.confirm.caption", "Confirm");
			defaultTranslations.put("menu.copyids.caption", "Copy IDs");
			defaultTranslations.put("menu.copyidsandnames.caption", "Copy IDs and names");
			defaultTranslations.put("menu.removeFromTeachersList", "Don't be taught any more by");
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
