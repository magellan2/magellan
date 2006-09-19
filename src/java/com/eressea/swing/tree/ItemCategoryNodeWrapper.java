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

package com.eressea.swing.tree;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.eressea.rules.ItemCategory;
import com.eressea.swing.tree.CellObject;
import com.eressea.util.CollectionFactory;

/**
 * DOCUMENT ME!
 *
 * @author Ulrich Küster
 */
public class ItemCategoryNodeWrapper implements CellObject{
	private int amount = -1;
	private ItemCategory cat = null;
	private String setCatName = null;
	protected List icons;
	protected List returnIcons;
	
	protected DetailsNodeWrapperDrawPolicy adapter;
	
	/**
	 * Creates a new ItemCategoryNodeWrapper object.
	 *
	 * @param category TODO: DOCUMENT ME!
	 * @param amount TODO: DOCUMENT ME!
	 */
	public ItemCategoryNodeWrapper(ItemCategory category, int amount) {
		this.amount = amount;
		this.cat = category;
	}
	public ItemCategoryNodeWrapper(ItemCategory category, int amount, String _catName) {
		this.amount = amount;
		this.cat = category;
		this.setCatName = _catName;
	}
	

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param i TODO: DOCUMENT ME!
	 */
	public void setAmount(int i) {
		amount = i;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public ItemCategory getItemCategory() {
		return cat;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String toString() {
		if(amount == -1) {
			if (this.setCatName==null) {
				return cat.toString();
			} else {
				return this.setCatName;
			}
		} else {
			if (this.setCatName==null) {
				return cat.toString() + ": " + amount;
			} else {
				return this.setCatName + ": " + amount;
			}
		}
	}
	public NodeWrapperDrawPolicy init(Properties settings, NodeWrapperDrawPolicy adapter) {
		return init(settings, "SimpleNodeWrapper", adapter);
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
			adapter = createSimpleDrawPolicy(settings, prefix);
		}

		adapter.addCellObject(this);
		this.adapter = (DetailsNodeWrapperDrawPolicy) adapter;

		return adapter;
	}
	protected NodeWrapperDrawPolicy createSimpleDrawPolicy(Properties settings, String prefix) {
		return new DetailsNodeWrapperDrawPolicy(1, null, settings, prefix,
												new String[][] {
													{ "simple.showIcon", "true" }
												}, new String[] { "icons.text" }, 0, getClass(),
												getDefaultTranslations());
	}
//	 pavkovic 2003.01.28: this is a Map of the default Translations mapped to this class
	// it is called by reflection (we could force the implementation of an interface,
	// this way it is more flexible.)
	// Pls use this mechanism, so the translation files can be created automagically
	// by inspecting all classes.
	private static final Map defaultTranslations = CollectionFactory.createHashtable();

	static {
		defaultTranslations.put("prefs.title", "Simple");
		defaultTranslations.put("icons.text", "Show Icons");
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static Map getDefaultTranslations() {
		return defaultTranslations;
	}
	
	public void setIcons(Object icons) {
		this.icons = null;
        if(icons != null) {
            if(icons instanceof Collection) {
                this.icons = CollectionFactory.createArrayList((Collection) icons);
            } else if(icons instanceof Map) {
                Map m = (Map) icons;

                this.icons = CollectionFactory.createArrayList(m.size());

                for(Iterator iter = m.values().iterator(); iter.hasNext();) {
                    this.icons.add(iter.next().toString());
                }
            } else {
                this.icons = CollectionFactory.singletonList(icons.toString());
            }
        }
	}
	public boolean emphasized() {
		return false;
	}
	public void propertiesChanged() {
		returnIcons = null;
	}
	public List getIconNames() {
		if(returnIcons == null) {
			
				returnIcons = icons;
			
		}

		return returnIcons;
	}
	
}
