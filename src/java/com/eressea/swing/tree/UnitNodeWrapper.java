// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Ulrich Küster, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.swing.tree;


import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import com.eressea.Faction;
import com.eressea.Item;
import com.eressea.Skill;
import com.eressea.Unit;
import com.eressea.swing.context.ContextChangeable;
import com.eressea.swing.context.ContextObserver;
import com.eressea.util.CollectionFactory;
import com.eressea.util.comparator.SkillComparator;
import com.eressea.util.comparator.SkillTypeComparator;
import com.eressea.util.comparator.SkillTypeRankComparator;
import com.eressea.util.logging.Logger;

public class UnitNodeWrapper implements CellObject2, SupportsClipboard, SupportsEmphasizing {
	private final static Logger log = Logger.getInstance(UnitNodeWrapper.class);

	private final static Comparator skillComparator = new SkillComparator();
	private static Comparator rankComparator = null;
	// just so that we can return an empty List without creating
	// all the time a new one (for implementation of SupportsEmphasizing)
	private List subordinatedElements = null;

	private final static String COLOR_TAG = "unitColor";
	private final static String STYLE_TAG = "unitStyle";
	private final static String SKILL_CHANGE_STYLE_PREFIX = "Talent";
	private final static String WARNING_IMAGE = "warnung";

	private Unit unit = null;
	private int amount = -1;
	private String text = null;
	private boolean iconNamesCreated = false;
	private List iconNames = null;
	private Boolean reverse;
	private String additionalIcon = null;
	
	private UnitNodeWrapperDrawPolicy adapter;
	
	public UnitNodeWrapper(Unit u, String prfx, int num, int mod) {
		this.unit = u;
		this.amount = num;
		this.text = getText(u, prfx, num, mod);
	}

	public UnitNodeWrapper(Unit u, String text) {
		unit = u;
		this.text = text;
	}

	public Unit getUnit() {
		return unit;
	}

	public int getAmount() {
		return amount;
	}

	public String toString() {
		return text;
	}

	public void setAdditionalIcon(String icon) {
		additionalIcon = icon;
	}

	public List getSubordinatedElements() {
		if (subordinatedElements == null) {
			subordinatedElements = CollectionFactory.createLinkedList();
		}
		return subordinatedElements;
	}

	// we just don't support old style
	public List getIconNames() {
		return null;
	}

	public boolean emphasized() {
		Faction f = unit.getFaction();
		if (f != null && f.trustLevel >= Faction.TL_PRIVILEGED) {
			if (!unit.ordersConfirmed) {
				return true;
			}
			if (subordinatedElements != null) {
				for (Iterator iter = subordinatedElements.iterator(); iter.hasNext(); ) {
					SupportsEmphasizing se = (SupportsEmphasizing)iter.next();
					if (se.emphasized()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean isShowingAdditional() {
		return adapter.properties[adapter.SHOW_ADDITIONAL];
	}

	public boolean isShowingContainerIcons() {
		return isShowingAdditional() && adapter.properties[adapter.SHOW_CONTAINER];
	}

	public boolean isShowingSkillIcons() {
		return isShowingAdditional() && adapter.properties[adapter.SHOW_SKILL];
	}

	public boolean isShowingOtherIcons() {
		return isShowingAdditional() && adapter.properties[adapter.SHOW_OTHER];
	}

	public boolean isShowingIconText() {
		return isShowingAdditional() && adapter.properties[adapter.SHOW_TEXT];
	}

	public boolean isShowingSkillsLessThanOne() {
		return isShowingAdditional() && adapter.properties[adapter.SHOW_SKILL_LESS_ONE];
	}

	public boolean isShowingExpectedOnly() {
		return isShowingAdditional() && adapter.properties[adapter.SHOW_EXPECTED_ONLY];
	}

	public boolean isShowingChanges() {
		return adapter.properties[adapter.SHOW_CHANGES];
	}

	public boolean isShowingChangesStyled() {
		return isShowingChanges() && adapter.properties[adapter.SHOW_CHANGE_STYLED];
	}

	public boolean isShowingChangesText() {
		return isShowingChanges() && adapter.properties[adapter.SHOW_CHANGE_TEXT];
	}

	public boolean isShowingCategorized() {
		return adapter.properties[adapter.SHOW_CATEGORIZED];
	}

	public boolean isShowingCatagorized(int type) {
		return adapter.properties[adapter.CATEGORIZE_START + type];
	}

	public boolean isUsingWarner() {
		return adapter.properties[adapter.SHOW_WARNINGS] && adapter.uWarning != null;
	}

	public UnitWarning getWarner() {
		return adapter.uWarning;
	}

	private static String getText(Unit u, String prefix, int amount1, int amount2) {
		StringBuffer sb = new StringBuffer();
		if (prefix != null) {
			sb.append(prefix);
		}
		sb.append(u.toString());
		if (amount1 > -1) {
			sb.append(": ").append(amount1);
			if (amount2 > -1 && amount2 != amount1) {
				sb.append(" (").append(amount2).append(")");
			}
		}
		return sb.toString();
	}

	private List createGraphicsElements(Unit u, List c) {
		List names = null;
		if (c != null) {
			names = c;
			names.clear();
		} else {
			names = CollectionFactory.createLinkedList();
		}
		List skills = CollectionFactory.createLinkedList();
		if (isShowingSkillIcons() && u.skills != null) {
			boolean bool = isShowingSkillsLessThanOne();
			for (Iterator iter = u.getSkills().iterator(); iter.hasNext();) {
				Skill s = (Skill)iter.next();
				if (s.getLevel() > 0 || bool) {
					skills.add(s);
				}
			}
			if (adapter.getSettings().getProperty("EMapOverviewPanel.useBestSkill", "true").equalsIgnoreCase("true") ) {
				// use best skill to sort icons
				Collections.sort(skills, skillComparator);
			} else {
				// use skilltyperanking to sort icons
				Collections.sort(skills, rankComparator);
			}
		}

		Collection others = null;
		if (isShowingOtherIcons()) {
			others = u.getModifiedItems();
		}

		// main
		Tag2Element.start(u);

		GraphicsElement start=new UnitGraphicsElement(text);
		start.setType(GraphicsElement.MAIN);
		if (additionalIcon != null) {
			start.setImageName(additionalIcon);
		}
		Tag2Element.apply(start);

		GraphicsElement ge = null;

		if (isShowingContainerIcons()) {
			if ( unit.getBuilding() != null ) {
				ge=new GraphicsElement(null,null,unit.getBuilding().getType().getID().toString());
				ge.setTooltip(unit.getBuilding().getName());
				ge.setType(GraphicsElement.ADDITIONAL);
				names.add(ge);
			}
			if ( unit.getShip() != null ) {
				ge=new GraphicsElement(null,null,unit.getShip().getType().getID().toString());
				ge.setTooltip(unit.getShip().getName());
				ge.setType(GraphicsElement.ADDITIONAL);
				names.add(ge);
			}
		}

		for (Iterator iter = skills.iterator(); iter.hasNext();) {
			Skill s = (Skill)iter.next();
			ge=null;
			if (isShowingIconText()) {
				ge=new GraphicsElement(new Integer(s.getLevel()),null,null,s.getType().getID().toString());
				if (s.isLostSkill()) {
					ge.setObject("-");
				}
			}
			else {
				ge=new GraphicsElement(null,null,s.getType().getID().toString());
			}
			ge.setTooltip(s.getType().getName());
			ge.setType(GraphicsElement.ADDITIONAL);
			if (isShowingChanges() && s.isLevelChanged()) {
				if (isShowingChangesStyled()) {
					ge.setStyleset(SKILL_CHANGE_STYLE_PREFIX+(s.getChangeLevel()>=0?">.":"<.")+SKILL_CHANGE_STYLE_PREFIX+String.valueOf(s.getChangeLevel()));
				}
				if (isShowingChangesText() && isShowingIconText()) {
					ge.setObject(ge.getObject().toString()+"("+(s.getChangeLevel()>=0?"+":"")+String.valueOf(s.getChangeLevel())+")");
				}
			} else {
				Tag2Element.apply(ge);
			}
			names.add(ge);
		}


		if (others != null) {
			if (isShowingCategorized()) {
				List categories[] = new List[7];
				boolean anything = false;
				for(int i=0;i<7;i++) {
					if (isShowingCatagorized(i)) {
						categories[i] = CollectionFactory.createLinkedList();
						anything = true;
					}
				}

				if (anything) {
					Iterator it = others.iterator();
					while(it.hasNext()) {
						Item item = (Item)it.next();
						try {
							String cat = item.getType().getCategory().getID().toString();

							int j = -1;
							for(int i=0;i<7;i++) {
								if (adapter.categories[i].equals(cat) && isShowingCatagorized(i)) {
									j = i;
								}
							}
							if (j != -1) {
								it.remove();
								categories[j].add(item);
							}
						} catch(Exception exc) {}
					}
					StringBuffer buffer = new StringBuffer();
					for(int i=0;i<7;i++) {
						if (categories[i]!=null) {
							it = categories[i].iterator();
							int count = 0;
							buffer.setLength(0);
							Item item = null;
							while(it.hasNext()) {
								item = (Item)it.next();
								buffer.append(item.getAmount());
								buffer.append(' ');
								buffer.append(item.getName());
								if (it.hasNext()) {
									buffer.append(',');
								}
								count += item.getAmount();
							}

							if (count > 0) {
								if (isShowingIconText())
									ge=new GraphicsElement(new Integer(count),null,null,"items/" + item.getType().getIconName());
								else
									ge=new GraphicsElement(null,null,"items/" + item.getType().getIconName());
								ge.setTooltip(buffer.toString());
								ge.setType(GraphicsElement.ADDITIONAL);
								Tag2Element.apply(ge);
								names.add(ge);
							}
							categories[i] = null;
						}
					}
					buffer = null;
				}
				categories = null;
			}

			for (Iterator iter = others.iterator(); iter.hasNext();) {
				Item s = (Item)iter.next();
				if(isShowingExpectedOnly()) {
					if(s.getAmount() <= 0) {
						// skip "empty" items
						continue;
					}
				}
				ge=null;
				if (isShowingIconText()) {
					ge=new GraphicsElement(null, null, null,"items/" + s.getType().getIconName());
					Item oldItem = u.getItem(s.getType());
					int oldAmount = 0;
					if (oldItem != null) {
						oldAmount = oldItem.getAmount();
					}
					if (oldAmount != s.getAmount()) {
						if(isShowingExpectedOnly()) {
							// only show expected future value
							ge.setObject(String.valueOf(s.getAmount()));
						} else {
							ge.setObject(String.valueOf(oldAmount)+"("+String.valueOf(s.getAmount())+")");
						}
					} else {
						if (oldAmount == 0) {
							continue;
						}
						ge.setObject(new Integer(oldAmount));
					}
				}	else {
					ge=new GraphicsElement(null,null,"items/" + s.getType().getIconName());
				}
				ge.setTooltip(s.getName());
				ge.setType(GraphicsElement.ADDITIONAL);
				Tag2Element.apply(ge);
				names.add(ge);
			}
		}
		if (reverseOrder()) {
			Collections.reverse(names);
			if (isUsingWarner()) {
				if (getWarner().warn(this.unit)) {
					start.setImageName(WARNING_IMAGE);
				}
			}
		} else {
			if (isUsingWarner()) {
				if (getWarner().warn(this.unit)) {
					names.add(0, new GraphicsElement(null, null, null, WARNING_IMAGE));
				}
			}
		}
		names.add(0,start);

		// ease garbage collection
		skills.clear();
		//others.clear();

		return names;
	}

	public void setReverseOrder(boolean bool) {
		reverse = new Boolean(bool);
	}

	public boolean reverseOrder() {
		if (reverse != null) {
			return reverse.booleanValue();
		}
		return adapter.properties[adapter.SHOW_NAMEFIRST];
	}

	public void clearBuffer() {
		if (iconNames != null) {
			iconNames.clear();
			iconNames = null;
		}
		iconNamesCreated = false;
	}

	public void propertiesChanged() {
		if (iconNames != null) {
			iconNames.clear();
			iconNames = null;
		}
		iconNamesCreated = false;
	}

	public NodeWrapperDrawPolicy init(Properties settings,NodeWrapperDrawPolicy adapter) {
		return init(settings,"UnitNodeWrapper",adapter);
	}

	public NodeWrapperDrawPolicy init(Properties settings,String prefix,NodeWrapperDrawPolicy adapter) {
		// return the adapter
		if (adapter==null) {
			adapter = new UnitNodeWrapperDrawPolicy(settings,prefix,getDefaultTranslations());
		}
		adapter.addCellObject(this);
		this.adapter = (UnitNodeWrapperDrawPolicy)adapter;
		if (rankComparator == null) {
			rankComparator = new SkillTypeComparator(new SkillTypeRankComparator(null, settings), null);
		}
		return adapter;
	}

	public List getGraphicsElements() {
		if (!iconNamesCreated) {
			this.iconNames = createGraphicsElements(this.unit, iconNames);
			iconNamesCreated = true;
		}
		return iconNames;
	}
	
	private class UnitNodeWrapperDrawPolicy extends DetailsNodeWrapperDrawPolicy implements ContextChangeable, ActionListener {
		
		public final int SHOW_ADDITIONAL      = 0;
		public final int SHOW_CONTAINER       = 1;
		public final int SHOW_SKILL           = 2;
		public final int SHOW_SKILL_LESS_ONE  = 3;
		public final int SHOW_OTHER           = 4;
		public final int SHOW_TEXT            = 5;
		public final int SHOW_NAMEFIRST       = 6;
		public final int SHOW_EXPECTED_ONLY   = 7;

		public final int SHOW_CHANGES         = 8;
		public final int SHOW_CHANGE_STYLED   = 9;
		public final int SHOW_CHANGE_TEXT     = 10;

		public final int SHOW_CATEGORIZED     = 11;
		public final int CATEGORIZE_START     = 12;

		public final int SHOW_WARNINGS        = 19;

		protected String categories[] = {
			"weapons",
			"armour",
			"resources",
			"luxuries",
			"herbs",
			"potions",
			"misc"
		};

		// for menu use
		protected ContextObserver obs;
		protected JMenu contextMenu;
		protected JCheckBoxMenuItem itemItem, skillItem;

		protected UnitWarning uWarning;
		
		public UnitNodeWrapperDrawPolicy(Properties settings,String prefix, Map defaultTrans) {
			// super(5, new int[] {6, 2, 7, -1, -1}, settings, prefix,new String[][] {
			super(4, new int[] {7, 2, 7, 0}, settings, prefix,new String[][] {

				{"showAdditional", "true"},
				{"showContainerIcons", "true"},
				{"showSkillIcons", "true"},
				{"showSkillLessThanOneIcons", "false"},
				{"showOtherIcons", "true"},
				{"showIconText", "true"},
				{"showNamesFirst", "false"},
				{"showExpectedOnly", "false"},

				{"showChanges", "true"},
				{"showChangesStyled", "true"},
				{"showChangesText", "false"},

				{"showCategorized", "false"},
				{"showCategorized.0", "false"},
				{"showCategorized.1", "false"},
				{"showCategorized.2", "false"},
				{"showCategorized.3", "false"},
				{"showCategorized.4", "false"},
				{"showCategorized.5", "false"},
				{"showCategorized.6", "false"},

				{"showWarnings", "false"}
				
			}, new String[] {
				"prefs.additional.text",
				"prefs.container.text",
				"prefs.skill.text",
				"prefs.skilllessthanone.text",
				"prefs.other.text",
				"prefs.icontext.text",
				"prefs.nfirst.text",
				"prefs.showExpectedOnly",

				"prefs.changes.text",
				"prefs.changes.mode0.text",
				"prefs.changes.mode1.text",

				"prefs.categorized.text",
				"prefs.categorized.0",
				"prefs.categorized.1",
				"prefs.categorized.2",
				"prefs.categorized.3",
				"prefs.categorized.4",
				"prefs.categorized.5",
				"prefs.categorized.6",

				"prefs.showWarnings"
			}, 0, UnitNodeWrapper.class, defaultTrans);
			// context menu
			contextMenu = new JMenu(getString("prefs.title"));
			itemItem = new JCheckBoxMenuItem(getString("prefs.other.text"), properties[SHOW_OTHER]);
			itemItem.addActionListener(this);
			contextMenu.add(itemItem);
			skillItem = new JCheckBoxMenuItem(getString("prefs.skill.text"), properties[SHOW_SKILL]);
			skillItem.addActionListener(this);
			contextMenu.add(skillItem);
		}
		
		public void applyPreferences() {
			// check warner
			//uWarning.getAdapter(null, null).applyPreferences();

			// update all wrappers
			super.applyPreferences();

			skillItem.setSelected(properties[SHOW_SKILL]);
			itemItem.setSelected(properties[SHOW_OTHER]);
		}

		public JMenuItem getContextAdapter() {
			return contextMenu;
		}

		public void setContextObserver(ContextObserver co) {
			obs = co;
		}

		public void actionPerformed(ActionEvent e) {
			boolean changed=false;
			if (properties[SHOW_SKILL]!=skillItem.isSelected()) {
				properties[SHOW_SKILL]=skillItem.isSelected();
				if ((properties[SHOW_SKILL] && sK[SHOW_SKILL][1].equals("true")) || (!properties[SHOW_SKILL] && sK[SHOW_SKILL][1].equals("false"))) {
					settings.remove(prefix+"."+sK[SHOW_SKILL][0]);
				} else {
					settings.setProperty(prefix+"."+sK[SHOW_SKILL][0], properties[SHOW_OTHER]?"true":"false");
				}
				changed=true;
			}
			if (properties[SHOW_OTHER]!=itemItem.isSelected()) {
				properties[SHOW_OTHER]=skillItem.isSelected();
				if ((properties[SHOW_OTHER] && sK[SHOW_OTHER][1].equals("true")) || (!properties[SHOW_OTHER] && sK[SHOW_OTHER][1].equals("false"))) {
					settings.remove(prefix+"."+sK[SHOW_OTHER][0]);
				} else {
					settings.setProperty(prefix+"."+sK[SHOW_OTHER][0], properties[SHOW_OTHER]?"true":"false");
				}
				changed=true;
			}
			if (changed) {
				// update all wrappers
				applyPreferences();
				if (obs != null) {
					obs.contextDataChanged();
				}
			}
		}

		public Properties getSettings() {
			return settings;
		}
	}

	protected class UnitGraphicsElement extends GraphicsElement {
		public UnitGraphicsElement(Object o,Icon i,Image im,String s) {
			super(o,i,im,s);
		}
		public UnitGraphicsElement(Icon i,Image im,String s) {
			super(i,im,s);
		}
		public UnitGraphicsElement(Object o) {
			super(o);
		}
		public boolean isEmphasized() {
			return emphasized();
		}
	}

	public String getClipboardValue() {
		if (unit != null) {
			return unit.toString();
		} else {
			return toString();
		}
	}

	// pavkovic 2003.01.28: this is a Map of the default Translations mapped to this class
	// it is called by reflection (we could force the implementation of an interface,
	// this way it is more flexible.)
	// Pls use this mechanism, so the translation files can be created automagically
	// by inspecting all classes.
	private final static Map defaultTranslations = CollectionFactory.createHashtable();
	static {
		defaultTranslations.put("prefs.skill.text" , "Show skill icons");
		defaultTranslations.put("prefs.skilllessthanone.text" , "Show skill icons also if level is less than one");
		defaultTranslations.put("prefs.categorized.6" , "Miscellaneous");
		defaultTranslations.put("prefs.categorized.5" , "Potions");
		defaultTranslations.put("prefs.categorized.4" , "Herbs");
		defaultTranslations.put("prefs.categorized.3" , "Luxuries");
		defaultTranslations.put("prefs.categorized.2" , "Resources");
		defaultTranslations.put("prefs.categorized.1" , "Armour");
		defaultTranslations.put("prefs.categorized.0" , "Weapons");
		defaultTranslations.put("prefs.categorized.text" , "Categorize items");
		defaultTranslations.put("prefs.nfirst.text" , "Show name first");
		defaultTranslations.put("prefs.container.text" , "Show building and ship icons");
		defaultTranslations.put("prefs.other.text" , "Show inventory icons");
		defaultTranslations.put("prefs.icontext.text" , "Display text");
		defaultTranslations.put("prefs.changes.mode1.text" , "Via Text");
		defaultTranslations.put("prefs.changes.mode0.text" , "Via Styleset");
		defaultTranslations.put("prefs.additional.text.tooltip" , "Shows additional values like skills and items.");
		defaultTranslations.put("prefs.changes.mode1.text.tooltip" , "Shows the difference in brackets behind the current value.");
		defaultTranslations.put("prefs.changes.mode0.text.tooltip" , "Uses a customizable styleset for display. You may change the text font, fore- and background color.");
		defaultTranslations.put("prefs.changes.text.tooltip" , "Makes skill changes visible");
		defaultTranslations.put("prefs.categorized.text.tooltip" , "Put items of same category together.");
		defaultTranslations.put("prefs.dialogs.2.help" , "Choose the item categories that should be shown only once. The amount will be accumulated and the icon of the last item will be used.");
		defaultTranslations.put("prefs.dialogs.1.help" , "Choose the type of talent change visualisation you wish to be used.");
		defaultTranslations.put("prefs.dialogs.0.help" , "You may choose the type of content that will be displayed left (or right) of the unit's name.");
		defaultTranslations.put("prefs.dialogs.2.title" , "Categorize items...");
		defaultTranslations.put("prefs.dialogs.1.title" , "Skill changes...");
		defaultTranslations.put("prefs.dialogs.0.title" , "Additional icons and texts...");
		defaultTranslations.put("prefs.details" , "Details...");
		defaultTranslations.put("prefs.additional.text" , "Show additional icon and texts");
		defaultTranslations.put("prefs.changes.text" , "Show skill changes");
		defaultTranslations.put("prefs.title" , "Units");

		defaultTranslations.put("prefs.showWarnings" , "Show warning");

		defaultTranslations.put("prefs.showExpectedOnly", "Show expected items only");

		defaultTranslations.put("prefs.dialogs.3.title" , "Error warning");

		defaultTranslations.put("prefs.dialogs.3.help" , "Select the tests you would like to have performed for warnings.");
	}
	public static Map getDefaultTranslations() {
		return defaultTranslations;
	}

}
