/*
 * ItemNodeWrapper.java
 *
 * Created on 16. August 2001, 16:26
 */

package com.eressea.swing.tree;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;

import com.eressea.Faction;
import com.eressea.Item;
import com.eressea.Unit;

import com.eressea.util.CollectionFactory;
import com.eressea.util.StringFactory;

/**
 *
 * @author  Andreas
 * @version
 */
public class ItemNodeWrapper implements CellObject, SupportsClipboard {
	
	// Achtung: Das modifizierte Item!
	protected Item modItem;
	protected Unit unit;
	protected String text;
	//protected ItemNodeWrapperPreferencesAdapter adapter=null;
	protected boolean showRegionItemAmount=false;
	protected DetailsNodeWrapperDrawPolicy adapter;
	
	protected final static java.text.NumberFormat weightNumberFormat = java.text.NumberFormat.getNumberInstance();
	
	/** Creates new ItemNodeWrapper */
	public ItemNodeWrapper(Unit unit,Item item) {
		this.unit=unit;
		this.modItem=item;
	}
	
	public boolean emphasized() {
		return false;
	}
	
	public boolean isShowingRegionItemAmount() {
		if (adapter!=null)
			return adapter.properties[0];
		return showRegionItemAmount;
	}
	
	public void setShowRegionItemAmount(boolean b) {
		adapter=null;
		showRegionItemAmount=b;
		propertiesChanged();
	}
	

	// pavkovic 2003.10.01: prevent multiple Lists to be generated for nearly static code
	private static Map iconNamesLists = CollectionFactory.createHashtable();
	public List getIconNames() {
		Object key = modItem.getType().getIconName();
		List iconNames = (List) iconNamesLists.get(key);
		if (iconNames == null) {
			iconNames=CollectionFactory.singletonList(StringFactory.getFactory().intern("items/"+key));
			iconNamesLists.put(key,iconNames);
		}
		return iconNames;
	}
	
	public void propertiesChanged() {
		text=null;
	}
	
	public String toString() {
		if (text==null) {
			boolean showRegion=isShowingRegionItemAmount();
			// do not show region amounts if faction is not priviliged
			if (unit==null || unit.getFaction().trustLevel<Faction.TL_PRIVILEGED)
				showRegion=false;
			Item item=null;
			if (unit!=null) {
				item=unit.getItem(modItem.getType());
				if (item==null)
					item=new Item(modItem.getType(),0);
			}
			StringBuffer nodeText = new StringBuffer();
			if (item==null) {
				nodeText.append(modItem.getAmount()).append(' ');
				if (showRegion) {
					Item ri = unit.getRegion().getItem(modItem.getType());
					if (ri != null) {
						nodeText.append(getString("node.of")).append(' ').append(ri.getAmount()).append(' ');
					}
				}
				nodeText.append(modItem.getName());
				if (modItem.getType().getWeight() > 0) {
					float weight = (((int)(modItem.getType().getWeight() * 100)) * modItem.getAmount()) / 100.0f;
					nodeText.append(": ").append(weightNumberFormat.format(new Float(weight)));
					nodeText.append(" " + getString("node.weightunits"));
				}
			} else {
				nodeText.append(item.getAmount()).append(" ");
				if (modItem.getAmount() != item.getAmount()) {
					nodeText.append("(").append(modItem.getAmount()).append(") ");
				}
				if (showRegion) {
					Item ri = unit.getRegion().getItem(modItem.getType());
					if (ri != null) {
						nodeText.append(getString("node.of")).append(' ').append(ri.getAmount()).append(' ');
					}
				}
				nodeText.append(modItem.getName());
				if (modItem.getType().getWeight() > 0) {
					if (item.getType().getWeight() > 0) {
						float weight = (((int)(item.getType().getWeight() * 100)) * item.getAmount()) / 100.0f;
						nodeText.append(": ").append(weightNumberFormat.format(new Float(weight)));
						if (modItem.getAmount() != item.getAmount()) {
							float modWeight = (((int)(modItem.getType().getWeight() * 100)) * modItem.getAmount()) / 100.0f;
							nodeText.append(" (").append(weightNumberFormat.format(new Float(modWeight))).append(")");
						}
						nodeText.append(" " + getString("node.weightunits"));
					}
				}
			}
			text=nodeText.toString();
		}
		return text;
	}
	
	protected NodeWrapperDrawPolicy createItemDrawPolicy(Properties settings, String prefix) {
		return new DetailsNodeWrapperDrawPolicy(1,null,settings,prefix,
			new String[][] {{"units.showRegionItemAmount","true"}},
			new String[] {"prefs.region.text"},
			0,getClass(),getDefaultTranslations());
	}
			
	/**
	 * Returns a translation for the specified key.
	 */
	protected String getString(String key) {
		return com.eressea.util.Translations.getTranslation(this,key);
	}
	
	// pavkovic 2003.01.28: this is a Map of the default Translations mapped to this class
	// it is called by reflection (we could force the implementation of an interface,
	// this way it is more flexible.)
	// Pls use this mechanism, so the translation files can be created automagically
	// by inspecting all classes.
	private final static Map defaultTranslations = CollectionFactory.createHashtable();
	static {
		
		defaultTranslations.put("prefs.region.text" , "Show region amount");
		defaultTranslations.put("node.of" , "of");
		defaultTranslations.put("prefs.title" , "Items");
		defaultTranslations.put("node.weightunits" , "lbs");
	}
	public static Map getDefaultTranslations() {
		return defaultTranslations;
	}
	
	
	
	public String getClipboardValue() {
		if (modItem != null) {
			return modItem.getName();
		} else {
			return toString();
		}
	}
	
	public NodeWrapperDrawPolicy init(Properties settings, NodeWrapperDrawPolicy adapter) {
		return init(settings,"ItemNodeWrapper",adapter);
	}
	
	public NodeWrapperDrawPolicy init(Properties settings, String prefix, NodeWrapperDrawPolicy adapter) {
		if (adapter==null)
			adapter = createItemDrawPolicy(settings,prefix);
		adapter.addCellObject(this);
		this.adapter=(DetailsNodeWrapperDrawPolicy)adapter;
		return adapter;
	}
	
}
