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

package com.eressea.swing.tree;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.eressea.swing.context.ContextFactory;

import com.eressea.util.CollectionFactory;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class SimpleNodeWrapper implements CellObject, SupportsClipboard, Changeable,
										  SupportsEmphasizing
{
	protected static final List defaultIcon = CollectionFactory.singletonList("simpledefault");
	private List subordinatedElements = null;
	protected List icons;
	protected List returnIcons;
	protected Object text;
	protected String clipboardValue = null;
	protected DetailsNodeWrapperDrawPolicy adapter;
	protected boolean showIcons = true;
	protected ContextFactory contextFactory = null;
	protected Object contextArgument = null;
	protected int amount = -1;

	/**
	 * Creates new SimpleNodeWrapper
	 *
	 * @param text TODO: DOCUMENT ME!
	 * @param icons TODO: DOCUMENT ME!
	 * @param clipboardValue TODO: DOCUMENT ME!
	 */
	public SimpleNodeWrapper(Object text, Object icons, String clipboardValue) {
		this(text, icons);
		this.clipboardValue = clipboardValue;
	}

	/**
	 * Creates a new SimpleNodeWrapper object.
	 *
	 * @param text TODO: DOCUMENT ME!
	 * @param icons TODO: DOCUMENT ME!
	 */
	public SimpleNodeWrapper(Object text, Object icons) {
		this.text = text;
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

	/**
	 * Controls whether the tree cell renderer should display this item more noticeably than other
	 * nodes.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean emphasized() {
		if(subordinatedElements != null) {
			for(Iterator iter = subordinatedElements.iterator(); iter.hasNext();) {
				SupportsEmphasizing se = (SupportsEmphasizing) iter.next();

				if(se.emphasized()) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public List getSubordinatedElements() {
		if(subordinatedElements == null) {
			subordinatedElements = CollectionFactory.createLinkedList();
		}

		return subordinatedElements;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean isShowingIcons() {
		if(adapter != null) {
			return adapter.properties[0];
		}

		return showIcons;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param b TODO: DOCUMENT ME!
	 */
	public void setShowIcons(boolean b) {
		showIcons = b;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public List getIconNames() {
		if(returnIcons == null) {
			if(!isShowingIcons() || (icons == null)) {
				returnIcons = defaultIcon;
			} else {
				returnIcons = icons;
			}
		}

		return returnIcons;
	}

	/**
	 * TODO: DOCUMENT ME!
	 */
	public void propertiesChanged() {
		returnIcons = null;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param i TODO: DOCUMENT ME!
	 */
	public void setAmount(int i) {
		this.amount = i;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getAmount() {
		return this.amount;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public java.lang.String toString() {
		if(amount == -1) {
			return text.toString();
		} else {
			return text.toString() + ": " + amount;
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Object getText() {
		return text;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getClipboardValue() {
		if(clipboardValue == null) {
			return toString();
		} else {
			return clipboardValue;
		}
	}

	protected NodeWrapperDrawPolicy createSimpleDrawPolicy(Properties settings, String prefix) {
		return new DetailsNodeWrapperDrawPolicy(1, null, settings, prefix,
												new String[][] {
													{ "simple.showIcon", "true" }
												}, new String[] { "icons.text" }, 0, getClass(),
												getDefaultTranslations());
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

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public ContextFactory getContextFactory() {
		return contextFactory;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Object getArgument() {
		return contextArgument;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getChangeModes() {
		return Changeable.CONTEXT_MENU;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param contextFactory TODO: DOCUMENT ME!
	 */
	public void setContextFactory(ContextFactory contextFactory) {
		this.contextFactory = contextFactory;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param argument TODO: DOCUMENT ME!
	 */
	public void setArgument(Object argument) {
		contextArgument = argument;
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
}
