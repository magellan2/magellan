// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan G�tz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.util;


import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.tree.DefaultMutableTreeNode;

import com.eressea.Item;
import com.eressea.Rules;
import com.eressea.StringID;
import com.eressea.Unit;
import com.eressea.rules.ItemCategory;
import com.eressea.rules.ItemType;
import com.eressea.swing.tree.ItemCategoryNodeWrapper;
import com.eressea.swing.tree.NodeWrapperFactory;
import com.eressea.util.logging.Logger;

/**
 * A class providing various utility functions regarding units.
 */
public class Units {
	private final static Logger log = Logger.getInstance(Units.class);
	private Rules rules = null;
	// items without category
	private StatItemContainer catLessContainer = null;
	private Map itemCategoriesMap = CollectionFactory.createHashtable();
	private NodeWrapperFactory nodeWrapperFactory = null;

	public Units(Rules rules) {
		setRules(rules);
		nodeWrapperFactory = new NodeWrapperFactory(new Properties());
	}

	public void setRules(Rules rules) {
		this.rules = rules;
		if (rules != null) {
			initItemCategories();
		}
	}

	public Collection categorizeUnitItems(Collection units) {
		if (itemCategoriesMap == null || itemCategoriesMap.size() == 0) {
			log.warn("categorizeUnitItems(): the category map is not initialized!");
			return null;
		}

		clearItemContainers();

		for (Iterator it = units.iterator(); it.hasNext();) {
			Unit u = (Unit)it.next();
			for (Iterator i = u.getItems().iterator(); i.hasNext();) {
				Item item = (Item)i.next();

				// get the container this item is stored in
				Map container = getItemContainer(item.getItemType());

				// get the stat item from the category container
				StatItem stored = (StatItem)container.get(item.getItemType().getID());
				if (stored != null) {
					// add up the amount in the stat item
					stored.setAmount(stored.getAmount() + item.getAmount());
				} else {
					// create a new stat item for this item type
					stored = new StatItem(item.getItemType(), item.getAmount());
					container.put(stored.getItemType().getID(), stored);
				}
				// add the unit owning the item to the stat item
				stored.units.add(new UnitWrapper(u, item.getAmount()));
			}
		}

		List sortedCategories = CollectionFactory.createLinkedList(itemCategoriesMap.values());
		Collections.sort(sortedCategories);
		return sortedCategories;
	}

	public Collection sumUpUnitItems(Collection units) {
		Map items = CollectionFactory.createHashtable();

		for (Iterator it = units.iterator(); it.hasNext();) {
			Unit u = (Unit)it.next();
			for (Iterator i = u.getItems().iterator(); i.hasNext();) {
				Item item = (Item)i.next();

				// get the stat item by item type
				StatItem stored = (StatItem)items.get(item.getItemType().getID());
				if (stored != null) {
					// add up the amount in the stat item
					stored.setAmount(stored.getAmount() + item.getAmount());
				} else {
					// create a new stat item for this item type
					stored = new StatItem(item.getItemType(), item.getAmount());
					items.put(stored.getItemType().getID(), stored);
				}
				// add the unit owning the item to the stat item
				stored.units.add(new UnitWrapper(u, item.getAmount()));
			}
		}

		return items.values();
	}

	/**
	 * This method takes all items carried by units in the units
	 * Collection and sorts them by their category. Then it adds
	 * the non-empty categories to the specified parent node and puts
	 * the corresponding items in each category node. Optionally, the
	 * units carrying an item are listed as child nodes of the
	 * respective item nodes.
	 *
	 * @param units a collection of Unit objects carrying items.
	 * @param parentNode a tree node to add the new nodes to.
	 * @param itemComparator a comparator to sort the items with. If
	 * itemComparator is null the items are sorted by name.
	 * @param unitComparator a comparator to sort the units with. If
	 * unitComparator is null the units are sorted by the amount of
	 * the item carried underneath which they appear.
	 * @param showUnits if true each item node gets child nodes
	 * containing the unit(s) carrying that item.
	 * @return a collection of DefaultMutableTreeNode objects with
	 * user objects of class ItemCategory or null if the
	 * categorization of the items failed.
	 */
	public Collection addCategorizedUnitItems(Collection units, DefaultMutableTreeNode parentNode, Comparator itemComparator, Comparator unitComparator, boolean showUnits) {
		return addCategorizedUnitItems(units,parentNode,itemComparator,unitComparator,showUnits,nodeWrapperFactory);
	}

	public Collection addCategorizedUnitItems(Collection units, DefaultMutableTreeNode parentNode, Comparator itemComparator, Comparator unitComparator, boolean showUnits, NodeWrapperFactory factory) {
		DefaultMutableTreeNode catNode = null;
		Collection catNodes = CollectionFactory.createLinkedList();

		Collection catContainers = categorizeUnitItems(units);
		if (catContainers == null) {
			log.warn("addCategorizedUnitItems(): categorizing unit items failed!");
			return null;
		}

		for (Iterator contIter = catContainers.iterator(); contIter.hasNext();) {
			StatItemContainer sic = (StatItemContainer)contIter.next();
			if (sic.size() > 0) {
				ItemCategoryNodeWrapper wrapper = new ItemCategoryNodeWrapper(sic.getCategory(), -1);
				catNode = new DefaultMutableTreeNode(wrapper);
				parentNode.add(catNode);
				catNodes.add(catNode);

				List sortedItems = CollectionFactory.createLinkedList(sic.values());
				if (itemComparator != null) {
					Collections.sort(sortedItems, itemComparator);
				} else {
					Collections.sort(sortedItems);
				}
				int catNumber = 0;
				for (Iterator iter = sortedItems.iterator(); iter.hasNext();) {
					StatItem si = (StatItem)iter.next();
					catNumber += si.getAmount();
					DefaultMutableTreeNode itemNode = new DefaultMutableTreeNode(factory.createSimpleNodeWrapper(si.getItemType().getName() + ": " + si.getAmount(),"items/"+si.getItemType().getIconName()));
					catNode.add(itemNode);
					if (showUnits && si.units != null) {
						Collections.sort(si.units, new UnitWrapperComparator(unitComparator));
						for (Iterator it = si.units.iterator(); it.hasNext();) {
							UnitWrapper uw = (UnitWrapper)it.next();
							itemNode.add(new DefaultMutableTreeNode(factory.createUnitNodeWrapper(uw.getUnit(), uw.getAmount())));
						}
					}
				}
				if (catNumber > 0 && !sic.category.equals(rules.getItemCategory(StringID.create("misc")))) {
					wrapper.setAmount(catNumber);
				}
			}
		}

		return catNodes;
	}

	public void addUnitItems(Collection units, DefaultMutableTreeNode parentNode, Comparator itemComparator, Comparator unitComparator, boolean showUnits) {
		addUnitItems(units,parentNode,itemComparator,unitComparator,showUnits,nodeWrapperFactory);
	}

	public void addUnitItems(Collection units, DefaultMutableTreeNode parentNode, Comparator itemComparator, Comparator unitComparator, boolean showUnits,NodeWrapperFactory factory) {
		List sortedItems = CollectionFactory.createLinkedList(sumUpUnitItems(units));

		if (itemComparator != null) {
			Collections.sort(sortedItems, itemComparator);
		} else {
			Collections.sort(sortedItems);
		}

		for (Iterator iter = sortedItems.iterator(); iter.hasNext();) {
			StatItem si = (StatItem)iter.next();
			DefaultMutableTreeNode itemNode = new DefaultMutableTreeNode(factory.createSimpleNodeWrapper(si.getItemType().getName() + ": " + si.getAmount(),"items/"+si.getItemType().getIconName()));
			parentNode.add(itemNode);
			if (showUnits && si.units != null) {
				Collections.sort(si.units, new UnitWrapperComparator(unitComparator));
				for (Iterator it = si.units.iterator(); it.hasNext();) {
					UnitWrapper uw = (UnitWrapper)it.next();
					itemNode.add(new DefaultMutableTreeNode(factory.createUnitNodeWrapper(uw.getUnit(), uw.getAmount())));
				}
			}
		}
	}

	private static class StatItem extends Item implements Comparable {
		public List units = CollectionFactory.createLinkedList();

		public StatItem(ItemType type, int amount) {
			super(type, amount);
		}

		public int compareTo(Object o) {
			return this.getItemType().getName().compareTo(((StatItem)o).getItemType().getName());
		}
	}

	private static class UnitWrapper {
		private Unit unit = null;
		private int number = -1;

		public UnitWrapper(Unit u) {
			this(u, -1);
		}

		public UnitWrapper(Unit u, int num) {
			unit = u;
			number = num;
		}

		public Unit getUnit() {
			return unit;
		}

		public int getAmount() {
			return number;
		}

		public String toString() {
			if (number > -1) {
				return unit.toString() + ": " + number;
			}
			return unit.toString();
		}
	}

	private static class UnitWrapperComparator implements Comparator {
		private Comparator unitCmp = null;

		public UnitWrapperComparator(Comparator unitCmp) {
			this.unitCmp = unitCmp;
		}

		public int compare(Object o1, Object o2) {
			if (unitCmp != null) {
				return unitCmp.compare(((UnitWrapper)o1).getUnit(), ((UnitWrapper)o2).getUnit());
			} else {
				return ((UnitWrapper)o2).getAmount() - ((UnitWrapper)o1).getAmount();
			}
		}

		public boolean equals(Object o) {
			return false;
		}
	}

	private static class StatItemContainer extends Hashtable implements Comparable {
		private ItemCategory category = null;

		public ItemCategory getCategory() {
			return category;
		}

		public StatItemContainer(ItemCategory category) {
			this.category = category;
		}

		public int compareTo(Object o) {
			return this.category.compareTo(((StatItemContainer)o).getCategory());
		}
	}

	private void initItemCategories() {
		for (Iterator iter = rules.getItemCategories(); iter.hasNext();) {
			ItemCategory cat = (ItemCategory)iter.next();
			StatItemContainer sic = new StatItemContainer(cat);
			itemCategoriesMap.put(cat, sic);
			if (EresseaItemCategoryConstants.C_MISC.equals(cat.getID())) {
				catLessContainer = sic;
			}
		}
	}

	private StatItemContainer getItemContainer(ItemType type) {
		StatItemContainer sic = null;
		if (type.getCategory() != null) {
			sic = (StatItemContainer)itemCategoriesMap.get(type.getCategory());
		} else {
			sic = (StatItemContainer)itemCategoriesMap.get(rules.getItemCategory(EresseaItemCategoryConstants.C_MISC));
		}
		if (sic == null) {
			sic = catLessContainer;
		}
		return sic;
	}

	private void clearItemContainers() {
		for (Iterator iter = itemCategoriesMap.values().iterator(); iter.hasNext();) {
			StatItemContainer sic = (StatItemContainer)iter.next();
			sic.clear();
		}
	}
}
