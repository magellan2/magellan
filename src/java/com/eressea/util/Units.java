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

package com.eressea.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;

import com.eressea.Item;
import com.eressea.Rules;
import com.eressea.StringID;
import com.eressea.Unit;
import com.eressea.relation.ItemTransferRelation;
import com.eressea.rules.ItemCategory;
import com.eressea.rules.ItemType;
import com.eressea.swing.tree.ItemCategoryNodeWrapper;
import com.eressea.swing.tree.NodeWrapperFactory;
import com.eressea.swing.tree.UnitNodeWrapper;
import com.eressea.util.logging.Logger;

/**
 * A class providing various utility functions regarding units.
 */
public class Units {
	private static final Logger log = Logger.getInstance(Units.class);
	private Rules rules = null;

	// items without category
	private StatItemContainer catLessContainer = null;
	private Map itemCategoriesMap = CollectionFactory.createHashtable();

	private static ItemType silberbeutel = new ItemType(StringID.create("Silberbeutel"));
	private static ItemType silberkassette = new ItemType(StringID.create("Silberkassette"));

	/**
	 * Creates a new Units object.
	 *
	 * @param rules TODO: DOCUMENT ME!
	 */
	public Units(Rules rules) {
		setRules(rules);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param rules TODO: DOCUMENT ME!
	 */
	public void setRules(Rules rules) {
		this.rules = rules;

		if(rules != null) {
			initItemCategories();
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param units TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Collection categorizeUnitItems(Collection units) {
		if((itemCategoriesMap == null) || (itemCategoriesMap.size() == 0)) {
			log.warn("categorizeUnitItems(): the category map is not initialized!");

			return null;
		}

		clearItemContainers();

		for(Iterator it = units.iterator(); it.hasNext();) {
			Unit u = (Unit) it.next();

			for(Iterator i = u.getModifiedItems().iterator(); i.hasNext();) {
				Item item = (Item) i.next();

				// get the container this item is stored in
				Map container = getItemContainer(item.getItemType());

				// get the stat item from the category container
				StatItem stored = (StatItem) container.get(item.getItemType().getID());

				if(stored == null) {
					stored = new StatItem(item.getItemType(), 0);
					container.put(stored.getItemType().getID(), stored);
				}

				// add up the amount in the stat item
				// multiply amount with unit.persons if item is
				// silver
				int amount = item.getAmount();

				if(item.getItemType().equals(silberbeutel) ||
					   item.getItemType().equals(silberkassette)) {
					amount *= u.persons;
				}

				stored.setAmount(stored.getAmount() + amount);

				// add the unit owning the item to the stat item
				stored.units.add(new UnitWrapper(u, amount));
			}
		}

		List sortedCategories = CollectionFactory.createLinkedList(itemCategoriesMap.values());
		Collections.sort(sortedCategories);

		return sortedCategories;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param units TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Collection sumUpUnitItems(Collection units) {
		Map items = CollectionFactory.createHashtable();

		for(Iterator it = units.iterator(); it.hasNext();) {
			Unit u = (Unit) it.next();

			for(Iterator i = u.getModifiedItems().iterator(); i.hasNext();) {
				Item item = (Item) i.next();

				// get the stat item by item type
				StatItem stored = (StatItem) items.get(item.getItemType().getID());

				if(stored == null) {
					stored = new StatItem(item.getItemType(), 0);
					items.put(stored.getItemType().getID(), stored);
				}

				// add up the amount in the stat item
				// multiply amount with unit.persons if item is
				// silver
				int amount = item.getAmount();

				if(item.getItemType().equals(silberbeutel) ||
					   item.getItemType().equals(silberkassette)) {
					amount *= u.persons;
				}

				stored.setAmount(stored.getAmount() + amount);

				// add the unit owning the item to the stat item
				stored.units.add(new UnitWrapper(u, amount));
			}
		}

		return items.values();
	}

	/**
	 * This method takes all items carried by units in the units Collection and sorts them by their
	 * category. Then it adds the non-empty categories to the specified parent node and puts the
	 * corresponding items in each category node. Optionally, the units carrying an item are
	 * listed as child nodes of the respective item nodes.
	 *
	 * @param units a collection of Unit objects carrying items.
	 * @param parentNode a tree node to add the new nodes to.
	 * @param itemComparator a comparator to sort the items with. If itemComparator is null the
	 * 		  items are sorted by name.
	 * @param unitComparator a comparator to sort the units with. If unitComparator is null the
	 * 		  units are sorted by the amount of the item carried underneath which they appear.
	 * @param showUnits if true each item node gets child nodes containing the unit(s) carrying
	 * 		  that item.
	 * @param factory TODO: DOCUMENT ME!
	 *
	 * @return a collection of DefaultMutableTreeNode objects with user objects of class
	 * 		   ItemCategory or null if the categorization of the items failed.
	 */
	public Collection addCategorizedUnitItems(Collection units, DefaultMutableTreeNode parentNode,
											  Comparator itemComparator, Comparator unitComparator,
											  boolean showUnits, NodeWrapperFactory factory) {

		DefaultMutableTreeNode catNode = null;
		Collection catNodes = CollectionFactory.createLinkedList();

		Collection catContainers = categorizeUnitItems(units);

		if(catContainers == null) {
			log.warn("addCategorizedUnitItems(): categorizing unit items failed!");

			return null;
		}

		for(Iterator contIter = catContainers.iterator(); contIter.hasNext();) {
			StatItemContainer sic = (StatItemContainer) contIter.next();

			if(sic.size() > 0) {
				ItemCategoryNodeWrapper wrapper = new ItemCategoryNodeWrapper(sic.getCategory(), -1);
				catNode = new DefaultMutableTreeNode(wrapper);
				parentNode.add(catNode);
				catNodes.add(catNode);

				List sortedItems = CollectionFactory.createLinkedList(sic.values());

				if(itemComparator != null) {
					Collections.sort(sortedItems, itemComparator);
				} else {
					Collections.sort(sortedItems);
				}

				int catNumber = 0;

				Unit u = null;
				if(units.size() == 1) {
					u = (Unit) units.iterator().next();
				}
				for(Iterator iter = sortedItems.iterator(); iter.hasNext();) {
					StatItem si = (StatItem) iter.next();
					catNumber += si.getAmount();

					DefaultMutableTreeNode itemNode = new DefaultMutableTreeNode(factory.createItemNodeWrapper(u,si));

// 					DefaultMutableTreeNode itemNode = new DefaultMutableTreeNode(factory.createSimpleNodeWrapper(si.getItemType()
// 																												   .getName() +
// 																												 ": " +
// 																												 si.getAmount(),
// 																												 "items/" +
// 																												 si.getItemType()
// 																												   .getIconName()));
					catNode.add(itemNode);

					if(!showUnits && units.size() == 1) {
						boolean addItemNode = false;

						for(Iterator iter2 = u.getItemTransferRelations(si.getItemType()).iterator(); iter2.hasNext();) {
							ItemTransferRelation itr = (ItemTransferRelation) iter2.next();
							String prefix = String.valueOf(itr.amount) + " ";
							String addIcon = null;
							Unit u2 = null;
							
							if(itr.source == u) {
								addIcon = "get";
								u2 = itr.target;
							} else if(itr.target == u) {
								addIcon = "give";
								u2 = itr.source;
							}
							
							UnitNodeWrapper unw = factory.createUnitNodeWrapper(u2, prefix,
																				u2.getPersons(),
																				u2.getModifiedPersons());
							unw.setAdditionalIcon(addIcon);
							unw.setReverseOrder(true);


							itemNode.add(new DefaultMutableTreeNode(unw));

							addItemNode = true;
						}
						if(addItemNode) {
							// FIXME: we return different objects here!!
							catNode.add(itemNode);
						}
					}

					if(showUnits && (si.units != null)) {
						Collections.sort(si.units, new UnitWrapperComparator(unitComparator));

						for(Iterator it = si.units.iterator(); it.hasNext();) {
							UnitWrapper uw = (UnitWrapper) it.next();
							itemNode.add(new DefaultMutableTreeNode(factory.createUnitNodeWrapper(uw.getUnit(),
																								  uw.getAmount())));
						}
					}
				}
					
				if((catNumber > 0) &&
					   !sic.category.equals(rules.getItemCategory(StringID.create("misc")))) {
					wrapper.setAmount(catNumber);
				}
			}
		}

		return catNodes;
	}

	private static class StatItem extends Item implements Comparable {
		/** TODO: DOCUMENT ME! */
		public List units = CollectionFactory.createLinkedList();

		/**
		 * Creates a new StatItem object.
		 *
		 * @param type TODO: DOCUMENT ME!
		 * @param amount TODO: DOCUMENT ME!
		 */
		public StatItem(ItemType type, int amount) {
			super(type, amount);
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param o TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public int compareTo(Object o) {
			return this.getItemType().getName().compareTo(((StatItem) o).getItemType().getName());
		}
	}

	private static class UnitWrapper {
		private Unit unit = null;
		private int number = -1;

		/**
		 * Creates a new UnitWrapper object.
		 *
		 * @param u TODO: DOCUMENT ME!
		 */
		public UnitWrapper(Unit u) {
			this(u, -1);
		}

		/**
		 * Creates a new UnitWrapper object.
		 *
		 * @param u TODO: DOCUMENT ME!
		 * @param num TODO: DOCUMENT ME!
		 */
		public UnitWrapper(Unit u, int num) {
			unit = u;
			number = num;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public Unit getUnit() {
			return unit;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public int getAmount() {
			return number;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public String toString() {
			if(number > -1) {
				return unit.toString() + ": " + number;
			}

			return unit.toString();
		}
	}

	private static class UnitWrapperComparator implements Comparator {
		private Comparator unitCmp = null;

		/**
		 * Creates a new UnitWrapperComparator object.
		 *
		 * @param unitCmp TODO: DOCUMENT ME!
		 */
		public UnitWrapperComparator(Comparator unitCmp) {
			this.unitCmp = unitCmp;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param o1 TODO: DOCUMENT ME!
		 * @param o2 TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public int compare(Object o1, Object o2) {
			if(unitCmp != null) {
				return unitCmp.compare(((UnitWrapper) o1).getUnit(), ((UnitWrapper) o2).getUnit());
			} else {
				return ((UnitWrapper) o2).getAmount() - ((UnitWrapper) o1).getAmount();
			}
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param o TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public boolean equals(Object o) {
			return false;
		}
	}

	private static class StatItemContainer extends Hashtable implements Comparable {
		private ItemCategory category = null;

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public ItemCategory getCategory() {
			return category;
		}

		/**
		 * Creates a new StatItemContainer object.
		 *
		 * @param category TODO: DOCUMENT ME!
		 */
		public StatItemContainer(ItemCategory category) {
			this.category = category;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param o TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public int compareTo(Object o) {
			return this.category.compareTo(((StatItemContainer) o).getCategory());
		}
	}

	private void initItemCategories() {
		for(Iterator iter = rules.getItemCategoryIterator(); iter.hasNext();) {
			ItemCategory cat = (ItemCategory) iter.next();
			StatItemContainer sic = new StatItemContainer(cat);
			itemCategoriesMap.put(cat, sic);

			if(!iter.hasNext()) {
				// pavkovic 2003.11.20: we assume that the last item category is the "misc" category
				catLessContainer = sic;
			}
		}
	}

	private StatItemContainer getItemContainer(ItemType type) {
		if((type.getCategory() == null) || (itemCategoriesMap.get(type.getCategory()) == null)) {
			return catLessContainer;
		}

		return (StatItemContainer) itemCategoriesMap.get(type.getCategory());
	}

	private void clearItemContainers() {
		for(Iterator iter = itemCategoriesMap.values().iterator(); iter.hasNext();) {
			StatItemContainer sic = (StatItemContainer) iter.next();
			sic.clear();
		}
	}
}
