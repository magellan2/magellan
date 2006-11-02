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

/*
 * ItemNodeWrapper.java
 *
 * Created on 16. August 2001, 16:26
 */
package com.eressea.swing.tree;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.eressea.Faction;
import com.eressea.Item;
import com.eressea.Unit;
import com.eressea.util.CollectionFactory;
import com.eressea.util.StringFactory;

/**
 * DOCUMENT ME!
 *
 * @author Andreas
 * @version
 */
public class ItemNodeWrapper implements CellObject, SupportsClipboard {
	// Achtung: Das modifizierte Item!
	protected Item modItem;
	protected Unit unit;
	protected String text;

	protected boolean warning = false;
	
	//protected ItemNodeWrapperPreferencesAdapter adapter=null;
	protected boolean showRegionItemAmount = false;
	protected DetailsNodeWrapperDrawPolicy adapter;
	protected static final java.text.NumberFormat weightNumberFormat = java.text.NumberFormat.getNumberInstance();

	/**
	 * Creates new ItemNodeWrapper
	 *
	 * @param unit TODO: DOCUMENT ME!
	 * @param item TODO: DOCUMENT ME!
	 */
	public ItemNodeWrapper(Unit unit, Item item) {
		this.unit = unit;
		this.modItem = item;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean emphasized() {
		return false;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean isShowingRegionItemAmount() {
		if(adapter != null) {
			return adapter.properties[0];
		}

		return showRegionItemAmount;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param b TODO: DOCUMENT ME!
	 */
	public void setShowRegionItemAmount(boolean b) {
		adapter = null;
		showRegionItemAmount = b;
		propertiesChanged();
	}

	/**
	 * sets the warning flag for this node
	 * 
	 * @param b the new value of the warning flag
	 * @return the old value of the warning flag
	 */
	public boolean setWarningFlag(boolean b){
		boolean res = warning;
		warning = b;
		text = null;
		return res;
	}
	
	// pavkovic 2003.10.01: prevent multiple Lists to be generated for nearly static code
	private static Map iconNamesLists = CollectionFactory.createHashtable();

	/**
	 *  
	 * @return the modified item stored in this node
	 */
	public Item getItem() {
		return modItem;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public List getIconNames() {
		Object key = modItem.getItemType().getIconName();
		List iconNames = (List) iconNamesLists.get(key);

		if(iconNames == null) {
			iconNames = CollectionFactory.singletonList(StringFactory.getFactory().intern("items/" +
																						  key));
			iconNamesLists.put(key, iconNames);
		}

		return iconNames;
	}

	/**
	 * TODO: DOCUMENT ME!
	 */
	public void propertiesChanged() {
		text = null;
	}

	/**
	 * produces the string describing an item that a unit (or the like) has.
	 * 
	 * The string is:
	 *  "<amount>[(!!!)] of <regionamount> <itemname>: <weight> GE " 
	 *  for items, the unit already has or
	 *  "<amount> (<modamount>[,!!!]) of <regionamount> <itemname>: <weight> (<modweight>) GE [(!!!)]"
	 *  for new items.  
	 *  
	 *  (!!!) is added if the warning flag is set.
	 *
	 * @return the string representation of this item node.
	 */
	public String toString() {
		if(text == null) {
			boolean showRegion = isShowingRegionItemAmount();

			// do not show region amounts if faction is not priviliged
			// TODO: make this configurable
			if((unit == null) || (unit.getFaction().trustLevel < Faction.TL_PRIVILEGED)) {
				showRegion = false;
			}

			Item item = null;

			if(unit != null) {
				item = unit.getItem(modItem.getItemType());

				if(item == null) {
					item = new Item(modItem.getItemType(), 0);
				}
			}

			StringBuffer nodeText = new StringBuffer();

			if(item == null) {
				nodeText.append(modItem.getAmount()).append(' ');
				if (warning)
					  nodeText.append(" (!!!) ");

				if(showRegion) {
					Item ri = unit.getRegion().getItem(modItem.getItemType());

					if(ri != null) {
						nodeText.append(getString("node.of")).append(' ').append(ri.getAmount())
								.append(' ');
					}
				}

				nodeText.append(modItem.getName());

				if(modItem.getItemType().getWeight() > 0) {
					float weight = (((int) (modItem.getItemType().getWeight() * 100)) * modItem.getAmount()) / 100.0f;
					nodeText.append(": ").append(weightNumberFormat.format(new Float(weight)));
					nodeText.append(" " + getString("node.weightunits"));
				}
			} else {
				nodeText.append(item.getAmount()).append(" ");

				if(modItem.getAmount() != item.getAmount()) {
					nodeText.append("(").append(modItem.getAmount());
					if (warning)
						nodeText.append("!!!) ");
					else
						nodeText.append(") ");
					
				}else{
					if (warning)
						nodeText.append("(!!!) ");
				}

				if(showRegion) {
					Item ri = unit.getRegion().getItem(modItem.getItemType());

					if(ri != null) {
						nodeText.append(getString("node.of")).append(' ').append(ri.getAmount())
								.append(' ');
					}
				}

				nodeText.append(modItem.getName());

				if(modItem.getItemType().getWeight() > 0) {
					if(item.getItemType().getWeight() > 0) {
						float weight = (((int) (item.getItemType().getWeight() * 100)) * item.getAmount()) / 100.0f;
						nodeText.append(": ").append(weightNumberFormat.format(new Float(weight)));

						if(modItem.getAmount() != item.getAmount()) {
							float modWeight = (((int) (modItem.getItemType().getWeight() * 100)) * modItem.getAmount()) / 100.0f;
							nodeText.append(" (")
									.append(weightNumberFormat.format(new Float(modWeight))).append(")");
						}
						nodeText.append(" " + getString("node.weightunits"));
					}
				}
			}

			text = nodeText.toString();
		}

		return text;
	}

	protected NodeWrapperDrawPolicy createItemDrawPolicy(Properties settings, String prefix) {
		return new DetailsNodeWrapperDrawPolicy(1, null, settings, prefix,
												new String[][] {
													{ "units.showRegionItemAmount", "true" }
												}, new String[] { "prefs.region.text" }, 0,
												getClass(), getDefaultTranslations());
	}

	/**
	 * Returns a translation for the specified key.
	 *
	 * @param key TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	protected String getString(String key) {
		return com.eressea.util.Translations.getTranslation(this, key);
	}

	// pavkovic 2003.01.28: this is a Map of the default Translations mapped to this class
	// it is called by reflection (we could force the implementation of an interface,
	// this way it is more flexible.)
	// Pls use this mechanism, so the translation files can be created automagically
	// by inspecting all classes.
	private static final Map defaultTranslations = CollectionFactory.createHashtable();

	static {
		defaultTranslations.put("prefs.region.text", "Show region amount");
		defaultTranslations.put("node.of", "of");
		defaultTranslations.put("prefs.title", "Items");
		defaultTranslations.put("node.weightunits", "lbs");
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static Map getDefaultTranslations() {
		return defaultTranslations;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getClipboardValue() {
		if(modItem != null) {
			return modItem.getName();
		} else {
			return toString();
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param settings TODO: DOCUMENT ME!
	 * @param adapter TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public NodeWrapperDrawPolicy init(Properties settings, NodeWrapperDrawPolicy adapter) {
		return init(settings, "ItemNodeWrapper", adapter);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param settings TODO: DOCUMENT ME!
	 * @param prefix TODO: DOCUMENT ME!
	 * @param adapter TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public NodeWrapperDrawPolicy init(Properties settings, String prefix,
									  NodeWrapperDrawPolicy adapter) {
		if(adapter == null) {
			adapter = createItemDrawPolicy(settings, prefix);
		}

		adapter.addCellObject(this);
		this.adapter = (DetailsNodeWrapperDrawPolicy) adapter;

		return adapter;
	}
}
