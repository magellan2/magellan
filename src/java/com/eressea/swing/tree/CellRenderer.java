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

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import com.eressea.main.MagellanContext;
import com.eressea.util.Colors;
import com.eressea.util.JVMUtilities;
import com.eressea.util.Umlaut;
import com.eressea.util.logging.Logger;

/**
 * DOCUMENT ME!
 *
 * @author Sebastian
 * @version
 */
public class CellRenderer extends JPanel implements TreeCellRenderer {
	private static final Logger log = Logger.getInstance(CellRenderer.class);
    
	private DefaultTreeCellRenderer defaultRenderer = null;
	private Border focusedBorder = null;
	private Border selectedBorder = null;
	private Border plainBorder = null;
	private Border emptyBorder = null;
	private Icon missingIcon = null;
	private JLabel label = null;
	private JLabel iconLabels[] = new JLabel[10];
	private static Map mapIcons = new HashMap();
	private static Map boldFonts = new HashMap();
	private static Map stylesets = null;

	/** TODO: DOCUMENT ME! */
	public static Map colorMap;

	/** TODO: DOCUMENT ME! */
	public static boolean showTooltips;
	private boolean initialized = false;
	private CellObject cellObj = null;
	private CellObject2 cellObj2 = null;

	// default stylesets for GraphicsElement types + fallback styleset(last in array)
	protected static GraphicsStyleset typeSets[] = null;

	// stores joined data that comes out of getStyleset
	private GraphicsStyleset styleset = new GraphicsStyleset("swap");
	private static Properties settings;

	/** TODO: DOCUMENT ME! */
	public static int emphasizeStyleChange = 0;

	/** TODO: DOCUMENT ME! */
	public static Color emphasizeColor = null;

    private MagellanContext context;
	/**
	 * Creates new CellRenderer
	 *
	 * @param settings TODO: DOCUMENT ME!
	 */
	public CellRenderer(MagellanContext context) {
		CellRenderer.settings = context.getProperties();
        this.context = context;
        
		if(!initialized) {
			emptyBorder = new EmptyBorder(0, 0, 0, 1);

			loadTypesets(); // creates the array
			loadStylesets(); // create custom stylesets

			loadAdditionalValueProperties(); // loads type set "ADDITIONAL"
			loadEmphasizeData();
			initialized = true;
		}

		applyUIDefaults(); // loads type set "DEFAULT"

		// initialize icon labels array
		for(int i = 0; i < iconLabels.length; i++) {
			iconLabels[i] = new JLabel();
			iconLabels[i].setOpaque(false);
			iconLabels[i].setIconTextGap(1);
			iconLabels[i].setBorder(emptyBorder);
		}

		// load missing icon
		// pavkovic 2003.09.11: only initialize once
		if(missingIcon == null) {
			missingIcon = context.getImageFactory().loadImageIcon("missing");
		}

		javax.swing.UIManager.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
				public void propertyChange(java.beans.PropertyChangeEvent evt) {
					removeAll();
					applyUIDefaults();
				}
			});
	}

	protected void loadTypesets() {
		typeSets = new GraphicsStyleset[4];

		// load the stylesets
		loadStyleset("SIMPLE");
		loadStyleset("MAIN");
		loadStyleset("ADDITIONAL");
		loadStyleset("DEFAULT");

		typeSets[0] = (GraphicsStyleset) stylesets.get("SIMPLE");
		typeSets[1] = (GraphicsStyleset) stylesets.get("MAIN");
		typeSets[2] = (GraphicsStyleset) stylesets.get("ADDITIONAL");
		typeSets[3] = (GraphicsStyleset) stylesets.get("DEFAULT");

		typeSets[0].setParent("DEFAULT");
		typeSets[1].setParent("DEFAULT");
		typeSets[2].setParent("DEFAULT");
	}

	/**
	 * Loads the display values out of the settings.
	 */
	protected void loadAdditionalValueProperties() {
		Map cMap = null;
		boolean tTip = true;

		// Text -> color mapping
		String cMapS = settings.getProperty("CellRenderer.SkillIconTextColorMap");

		try {
			StringTokenizer st = new StringTokenizer(cMapS, ";");
			int c = st.countTokens() / 4;
			cMap = new HashMap();

			for(int i = 0; i < c; i++) {
				String value = st.nextToken();
				String redS = st.nextToken();
				String greenS = st.nextToken();
				String blueS = st.nextToken();

				try {
					cMap.put(value, Colors.decode(redS + "," + greenS + "," + blueS));
				} catch(Exception inner) {
				}
			}
		} catch(Exception exc) {
		}

		// Show Tooltips
		String tTipS = settings.getProperty("CellRenderer.ShowToolTips");
		tTip = ((tTipS != null) && tTipS.equals("true"));

		// now give the renderer our values
		colorMap = cMap;
		showTooltips = tTip;
	}

	protected static void loadEmphasizeData() {
		emphasizeStyleChange = 0;

		try {
			emphasizeStyleChange = Integer.parseInt(settings.getProperty("CellRenderer.Emphasize.Style"));
		} catch(Exception exc) {
			emphasizeStyleChange = Font.BOLD;
		}

		emphasizeColor = null;

		try {
			emphasizeColor = Color.decode(settings.getProperty("CellRenderer.Emphasize.Color"));
		} catch(Exception exc) {
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param sChange TODO: DOCUMENT ME!
	 * @param sColor TODO: DOCUMENT ME!
	 */
	public static void setEmphasizeData(int sChange, Color sColor) {
		if((emphasizeStyleChange != sChange) && (boldFonts != null)) {
			boldFonts.clear();
		}

		emphasizeStyleChange = sChange;

		if(sChange == 0) {
			settings.remove("CellRenderer.Emphasize.Style");
		} else {
			settings.setProperty("CellRenderer.Emphasize.Style", String.valueOf(sChange));
		}

		emphasizeColor = sColor;

		if(sColor == null) {
			settings.remove("CellRenderer.Emphasize.Color");
		} else {
			settings.setProperty("CellRenderer.Emphasize.Color", encodeColor(sColor));
		}
	}

	/**
	 * Sets the display values and saves them in the settings
	 *
	 * @param colorM TODO: DOCUMENT ME!
	 * @param sTip TODO: DOCUMENT ME!
	 */
	public static void setAdditionalValueProperties(Map colorM, boolean sTip) {
		showTooltips = sTip;
		settings.setProperty("CellRenderer.ShowToolTips", sTip ? "true" : "false");

		setColorMap(colorM);
	}

	/**
	 * Sets the current color mapping and stores it in the settings.
	 *
	 * @param colorM TODO: DOCUMENT ME!
	 */
	public static void setColorMap(Map colorM) {
		if(((colorMap != null) && (colorM == null)) ||
			   ((colorMap != null) && !colorMap.equals(colorM)) ||
			   ((colorMap == null) && (colorM != null))) {
			colorMap = colorM;

			if(colorMap == null) {
				settings.setProperty("CellRenderer.SkillIconTextColorMap", "none");
			} else {
				StringBuffer str = new StringBuffer();
				Iterator it = colorMap.keySet().iterator();

				while(it.hasNext()) {
					if(str.length() > 0) {
						str.append(';');
					}

					String value = (String) it.next();
					str.append(value);
					str.append(';');

					Color col = (Color) colorMap.get(value);
					str.append(Colors.encode(col).replace(',', ';'));
				}

				if(str.length() > 0) {
					settings.setProperty("CellRenderer.SkillIconTextColorMap", str.toString());
				} else {
					settings.setProperty("CellRenderer.SkillIconTextColorMap", "none");
				}
			}
		}
	}

	/**
	 * Applies the default values to type set 4 (DEFAULT).
	 */
	protected void applyUIDefaults() {
		defaultRenderer = new DefaultTreeCellRenderer();

		typeSets[3].setForeground((Color) UIManager.getDefaults().get("Tree.textForeground"));
		typeSets[3].setBackground((Color) UIManager.getDefaults().get("Tree.textBackground"));
		typeSets[3].setSelectedForeground((Color) UIManager.getDefaults().get("Tree.selectionForeground"));
		typeSets[3].setSelectedBackground((Color) UIManager.getDefaults().get("Tree.selectionBackground"));

		// pavkovic 2003.10.17: prevent jvm 1.4.2_01 bug
		focusedBorder = new MatteBorder(1, 1, 1, 1, JVMUtilities.getTreeSelectionBorderColor());
		selectedBorder = new MatteBorder(1, 1, 1, 1, typeSets[3].getSelectedBackground());
		plainBorder = new EmptyBorder(1, 1, 1, 1);

		this.setOpaque(false);
		this.setLayout(new SameHeightBoxLayout());
		this.setBackground(typeSets[3].getBackground());
		this.setForeground(typeSets[3].getBackground());

		label = new JLabel();
		label.setOpaque(false);

		this.removeAll();
		this.add(label);

		Font plainFont = label.getFont().deriveFont(Font.PLAIN);
		typeSets[3].setFont(plainFont);

		GraphicsStyleset set = getStyleset(1);
		defaultRenderer.setFont(set.getFont());
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param tree TODO: DOCUMENT ME!
	 * @param value TODO: DOCUMENT ME!
	 * @param selected TODO: DOCUMENT ME!
	 * @param expanded TODO: DOCUMENT ME!
	 * @param leaf TODO: DOCUMENT ME!
	 * @param row TODO: DOCUMENT ME!
	 * @param hasFocus TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public java.awt.Component getTreeCellRendererComponent(JTree tree, Object value,
														   boolean selected, boolean expanded,
														   boolean leaf, int row, boolean hasFocus) {
		cellObj = null;
		cellObj2 = null;

		if(value instanceof DefaultMutableTreeNode) {
			Object object = ((DefaultMutableTreeNode) value).getUserObject();

			if(object instanceof CellObject2) {
				cellObj2 = (CellObject2) object;
			} else if(object instanceof CellObject) {
				cellObj = (CellObject) object;
			}
		}

		if(cellObj2 != null) {
			layoutComponent2(selected, hasFocus);

			return this;
		}

		if(cellObj != null) {
			layoutComponent(selected, hasFocus);

			return this;
		} else {
			return defaultRenderer.getTreeCellRendererComponent(tree, value, selected, expanded,
																leaf, row, hasFocus);
		}
	}

	protected void layoutComponent2(boolean isSelected, boolean hasFocus) {
		Collection iconNames = cellObj2.getGraphicsElements();
		int iconNamesSize = iconNames.size() - 1;

		Iterator it = iconNames.iterator();

		// construct the full information for MAIN
		GraphicsElement ge = (GraphicsElement) it.next();
		GraphicsStyleset set = getStyleset(ge);

		if(cellObj2.reverseOrder()) {
			Object last = ge;

			if(it.hasNext()) {
				last = it.next();
			}

			it = iconNames.iterator();
			ge = (GraphicsElement) last;
			set = getStyleset(ge);
			formatLabel(label, set, false, false);
			fillLabel(label, ge);
		} else {
			formatLabel(label, set, isSelected, hasFocus);
			fillLabel(label, ge);
		}

		// if necessary, increase the size of the iconLabels array
		if(iconNamesSize > iconLabels.length) {
			for(int i = 0; i < iconLabels.length; i++) {
				iconLabels[i] = null;
			}

			iconLabels = new JLabel[iconNamesSize];

			for(int i = 0; i < iconLabels.length; i++) {
				iconLabels[i] = new JLabel();
				iconLabels[i].setOpaque(false);
				iconLabels[i].setIconTextGap(1);
				iconLabels[i].setBorder(emptyBorder);
			}

			// remove all remaining (old) icon labels
			while(this.getComponentCount() > 1) {
				this.remove(0);
			}
		}

		// add or remove icon labels to match the number of icons
		// to be displayed
		int iconLabelCount = this.getComponentCount() - 1;

		if(iconNamesSize < iconLabelCount) {
			for(int i = iconLabelCount - 1; i >= iconNamesSize; i--) {
				if(this.getComponent(i) == label) {
					log.info("iconLabelCount: " + iconLabelCount + ", iconNamesSize: " +
							 iconNamesSize);
				}

				this.remove(i);
			}
		} else {
			for(int i = iconLabelCount; i < iconNamesSize; i++) {
				this.add(iconLabels[i], i);
			}
		}

		// load icons and put them into the icon labels
		if(cellObj2.reverseOrder()) {
			if(iconNames.size() > 1) {
				ge = (GraphicsElement) it.next();
				set = getStyleset(ge);
				formatLabel(iconLabels[0], set, isSelected, hasFocus);
				fillLabel(iconLabels[0], ge);
				it.next(); // skip the new last

				int i = 1;

				while(it.hasNext()) {
					ge = (GraphicsElement) it.next();
					set = getStyleset(ge);
					formatLabel(iconLabels[iconNamesSize - i], set, false, false);
					fillLabel(iconLabels[iconNamesSize - i], ge);
					i++;
				}
			}
		} else {
			int i = 0;

			while(it.hasNext()) {
				ge = (GraphicsElement) it.next();
				set = getStyleset(ge);
				formatLabel(iconLabels[i], set, false, false);
				fillLabel(iconLabels[i], ge);
				i++;
			}
		}
	}

	private void layoutComponent(boolean isSelected, boolean hasFocus) {
		Collection iconNames = cellObj.getIconNames();
		int iconNamesSize = 0;

		if(iconNames != null) {
			iconNamesSize = iconNames.size();
		}

		// we have to use a "full" styleset
		GraphicsStyleset set = getStyleset(1);

		formatLabel(label, set, isSelected, hasFocus);

		if(cellObj.emphasized()) {
			label.setFont(getBoldFont(set.getFont()));

			if(emphasizeColor != null) {
				label.setForeground(emphasizeColor);
			}
		}

		label.setText(cellObj.toString());

		// if necessary, increase the size of the iconLabels array
		if(iconNamesSize > iconLabels.length) {
			for(int i = 0; i < iconLabels.length; i++) {
				iconLabels[i] = null;
			}

			iconLabels = new JLabel[iconNamesSize];

			for(int i = 0; i < iconLabels.length; i++) {
				iconLabels[i] = new JLabel();
				iconLabels[i].setOpaque(false);
				iconLabels[i].setIconTextGap(1);
				iconLabels[i].setBorder(emptyBorder);
			}

			// remove all remaining (old) icon labels
			while(this.getComponentCount() > 1) {
				this.remove(0);
			}
		}

		// add or remove icon labels to match the number of icons
		// to be displayed
		int iconLabelCount = this.getComponentCount() - 1;

		if(iconNamesSize < iconLabelCount) {
			for(int i = iconLabelCount - 1; i >= iconNamesSize; i--) {
				if(this.getComponent(i) == label) {
					log.info("iconLabelCount: " + iconLabelCount + ", iconNamesSize: " +
							 iconNamesSize);
				}

				this.remove(i);
			}
		} else {
			for(int i = iconLabelCount; i < iconNamesSize; i++) {
				this.add(iconLabels[i], i);
			}
		}

		// load icons and put them into the icon labels
		if(iconNames != null) {
			int i = 0;

			for(Iterator iter = iconNames.iterator(); iter.hasNext(); i++) {
				Object o = iter.next();
				String iconName = null;

				if(o instanceof String) {
					iconName = (String) o;
				} else { // unknown type

					continue;
				}

				Icon icon = getIcon(iconName);

				// typeSets[3] is always full, so we don't need to
				// call getStyleset()
				formatLabel(iconLabels[i], set, false, false);
				iconLabels[i].setIcon(icon);

				if(showTooltips) {
					iconLabels[i].setToolTipText(iconName);
				} else {
					iconLabels[i].setToolTipText(null);
				}
			}
		}
	}

	/**
	 * Fills the given label with the information out of the given element.
	 *
	 * @param l TODO: DOCUMENT ME!
	 * @param ge TODO: DOCUMENT ME!
	 */
	protected void fillLabel(JLabel l, GraphicsElement ge) {
		// object
		if(ge.getObject() != null) {
			l.setText(ge.getObject().toString());
		}

		String text = l.getText();

		if((text != null) && (colorMap != null) && colorMap.containsKey(text)) {
			l.setForeground((Color) colorMap.get(text));
		}

		// icon
		if(ge.getIcon() != null) {
			l.setIcon(ge.getIcon());
		} else if(ge.getImage() != null) {
			l.setIcon(getIcon(ge.getImage()));
		} else if(ge.getImageName() != null) {
			l.setIcon(getIcon(ge.getImageName()));
		}

		// tooltip
		if(ge.getTooltip() != null) {
			l.setToolTipText(ge.getTooltip());
		} else {
			l.setToolTipText(null);
		}

		// emphasize
		if(ge.isEmphasized()) {
			l.setFont(getBoldFont(l.getFont()));

			if(emphasizeColor != null) {
				label.setForeground(emphasizeColor);
			}
		}
	}

	/**
	 * Returns an icon constructed out of the given information.
	 * 
	 * <p>
	 * Following parseing is done:
	 * 
	 * <ol>
	 * <li>
	 * If given object is an icon, return it.
	 * </li>
	 * <li>
	 * If given object is an image, construct an ImageIcon and return.
	 * </li>
	 * <li>
	 * If given object is a String, search an image with that name and construct an ImageIcon.
	 * </li>
	 * </ol>
	 * 
	 * All icons are cached(except (1)). Non-found images of (3) are replaced with missingIcon. All
	 * unparseable objects return missingIcon.
	 * </p>
	 *
	 * @param icon TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	protected Icon getIcon(Object icon) {
		if(icon instanceof Icon) {
			return (Icon) icon;
		}

		if(mapIcons.containsKey(icon)) {
			return (Icon) mapIcons.get(icon);
		}

		if(icon instanceof Image) {
			ImageIcon ii = new ImageIcon((Image) icon);
			mapIcons.put(icon, ii);

			return ii;
		}

		if(icon instanceof String) {
			String iconName = (String) icon;
			String normalizedIconName = Umlaut.convertUmlauts(iconName).toLowerCase();

			Icon ic = context.getImageFactory().loadImageIcon(normalizedIconName);

			if(ic == null) {
				ic = missingIcon;
			}

			mapIcons.put(icon, ic);

			return ic;
		}

		return missingIcon;
	}

	/**
	 * Returns the bold font of the given font. These fonts are stored in boldFonts. If the given
	 * font is bold it's returned directly. If there's no bold font yet, a new one is created and
	 * placed into boldFonts.
	 * 
	 * <p>
	 * In this implementation all other style features(only italic yet) are save.
	 * </p>
	 *
	 * @param f TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	protected Font getBoldFont(Font f) {
		if((emphasizeStyleChange == 0) || ((f.getStyle() & emphasizeStyleChange) != 0)) {
			return f;
		}

		if(!boldFonts.containsKey(f)) {
			boldFonts.put(f, f.deriveFont(f.getStyle() | emphasizeStyleChange));
		}

		return (Font) boldFonts.get(f);
	}

	/**
	 * Formats a label using the given styleset. <b>The styleset must be complete!</b> Try using
	 * getStyleset() to assure a complete set.
	 * 
	 * <p>
	 * The label will also be resetted meaning tooltip, icon text are cleared.
	 * </p>
	 *
	 * @param l TODO: DOCUMENT ME!
	 * @param set TODO: DOCUMENT ME!
	 * @param isSelected TODO: DOCUMENT ME!
	 * @param hasFocus TODO: DOCUMENT ME!
	 */
	protected void formatLabel(JLabel l, GraphicsStyleset set, boolean isSelected, boolean hasFocus) {
		l.setToolTipText(null);
		l.setIcon(null);
		l.setText(null);

		if(isSelected) {
			l.setBackground(set.getSelectedBackground());
			l.setForeground(set.getSelectedForeground());
			l.setOpaque(true);
		} else {
			if(Color.white.equals(set.getBackground()) || (set.getBackground() == null)) {
				l.setOpaque(false);
			} else {
				l.setOpaque(true);
			}

			l.setBackground(set.getBackground());
			l.setForeground(set.getForeground());
		}

		if(hasFocus) {
			l.setBorder(focusedBorder);
		} else {
			if(isSelected) {
				l.setBorder(selectedBorder);
			} else {
				l.setBorder(plainBorder);
			}
		}

		l.setFont(set.getFont());
		l.setHorizontalTextPosition(set.getHorizontalPos());
		l.setVerticalTextPosition(set.getVerticalPos());
	}

	/**
	 * Returns a styleset that supplies all variables. This is done with a union of the given
	 * styleset of the element (if given) and the two fallbacks(type set and default set).
	 *
	 * @param ge TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	protected GraphicsStyleset getStyleset(GraphicsElement ge) {
		GraphicsStyleset fallback = null;

		if(ge.hasStyleset()) {
			if((stylesets == null) || !stylesets.containsKey(ge.getStyleset())) {
				loadStyleset(ge.getStyleset());
			}

			fallback = (GraphicsStyleset) stylesets.get(ge.getStyleset());
		} else {
			fallback = typeSets[ge.getType()];
		}

		fallbackCode(fallback, ge.getType());

		return styleset;
	}

	/**
	 * Returns a styleset that supplies all variables. This is done with a union of the given
	 * styleset and the two fallbacks(type set[given via parameter] and default set).
	 *
	 * @param set TODO: DOCUMENT ME!
	 * @param type TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	protected GraphicsStyleset getStyleset(GraphicsStyleset set, int type) {
		fallbackCode(set, type);

		return styleset;
	}

	/**
	 * Returns a full styleset created out if the given styleset.
	 *
	 * @param type TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	protected GraphicsStyleset getStyleset(int type) {
		fallbackCode(typeSets[type], type);

		return styleset;
	}

	private void fallbackCode(GraphicsStyleset fallback, int type) {
		styleset.setBackground(null);
		styleset.setFont(null);
		styleset.setForeground(null);
		styleset.setSelectedBackground(null);
		styleset.setSelectedForeground(null);

		// always use first supplied position
		styleset.setHorizontalPos(fallback.getHorizontalPos());
		styleset.setVerticalPos(fallback.getVerticalPos());

		boolean allFound;

		do {
			allFound = true;

			if(styleset.getBackground() == null) {
				if(fallback.getBackground() == null) {
					allFound = false;
				} else {
					styleset.setBackground(fallback.getBackground());
				}
			}

			if(styleset.getFont() == null) {
				if(fallback.getFont() == null) {
					allFound = false;
				} else {
					styleset.setFont(fallback.getFont());
				}
			}

			if(styleset.getForeground() == null) {
				if(fallback.getForeground() == null) {
					allFound = false;
				} else {
					styleset.setForeground(fallback.getForeground());
				}
			}

			if(styleset.getFont() == null) {
				if(fallback.getFont() == null) {
					allFound = false;
				} else {
					styleset.setFont(fallback.getFont());
				}
			}

			if(styleset.getSelectedBackground() == null) {
				if(fallback.getSelectedBackground() == null) {
					allFound = false;
				} else {
					styleset.setSelectedBackground(fallback.getSelectedBackground());
				}
			}

			if(styleset.getSelectedForeground() == null) {
				if(fallback.getSelectedForeground() == null) {
					allFound = false;
				} else {
					styleset.setSelectedForeground(fallback.getSelectedForeground());
				}
			}

			if(fallback.getParent() != null) {
				if(!stylesets.containsKey(fallback.getParent())) {
					loadStyleset(fallback.getParent());
				}

				fallback = (GraphicsStyleset) stylesets.get(fallback.getParent());
			} else {
				fallback = typeSets[type];
			}
		} while(!allFound);
	}

	/**
	 * Adds the given styleset to the styleset table. The name of the set is used as a key.
	 *
	 * @param set TODO: DOCUMENT ME!
	 */
	public static void addStyleset(GraphicsStyleset set) {
		if(stylesets == null) {
			stylesets = new HashMap();
		}

		stylesets.put(set.getName(), set);
		saveStyleset(set.getName());
	}

	/**
	 * Loads a styleset out of the property "CellRenderer.Stylesets."+name
	 *
	 * @param name TODO: DOCUMENT ME!
	 */
	protected static void loadStyleset(String name) {
		if(stylesets == null) {
			stylesets = new HashMap();
		}

		if(!stylesets.containsKey(name)) {
			GraphicsStyleset set = new GraphicsStyleset(name);
			String propName = "CellRenderer.Stylesets." + name;

			if(settings.containsKey(propName)) {
				String def = settings.getProperty(propName);
				StringTokenizer st = new StringTokenizer(def, ",;");

				while(st.hasMoreTokens()) {
					String defpart = st.nextToken();
					int index = defpart.indexOf('=');

					if(index == -1) {
						index = defpart.indexOf(':');
					}

					if(index == -1) {
						continue;
					}

					String partName = defpart.substring(0, index);
					String partValue = defpart.substring(index + 1);

					if(partName.equalsIgnoreCase("foreground")) {
						try {
							set.setForeground(Color.decode(partValue));
						} catch(NumberFormatException nfe) {
						}
					}

					if(partName.equalsIgnoreCase("background")) {
						try {
							set.setBackground(Color.decode(partValue));
						} catch(NumberFormatException nfe) {
						}
					}

					if(partName.equalsIgnoreCase("selectedforeground")) {
						try {
							set.setSelectedForeground(Color.decode(partValue));
						} catch(NumberFormatException nfe) {
						}
					}

					if(partName.equalsIgnoreCase("selectedbackground")) {
						try {
							set.setSelectedBackground(Color.decode(partValue));
						} catch(NumberFormatException nfe) {
						}
					}

					if(partName.equalsIgnoreCase("font")) {
						set.setFont(Font.decode(partValue));
					}

					if(partName.equalsIgnoreCase("horizontaltextposition")) {
						if(partValue.equalsIgnoreCase("LEFT")) {
							set.setHorizontalPos(SwingConstants.LEFT);
						}

						if(partValue.equalsIgnoreCase("CENTER")) {
							set.setHorizontalPos(SwingConstants.CENTER);
						}

						if(partValue.equalsIgnoreCase("RIGHT")) {
							set.setHorizontalPos(SwingConstants.RIGHT);
						}
					}

					if(partName.equalsIgnoreCase("verticaltextposition")) {
						if(partValue.equalsIgnoreCase("TOP")) {
							set.setVerticalPos(SwingConstants.TOP);
						}

						if(partValue.equalsIgnoreCase("CENTER")) {
							set.setVerticalPos(SwingConstants.CENTER);
						}

						if(partValue.equalsIgnoreCase("BOTTOM")) {
							set.setVerticalPos(SwingConstants.BOTTOM);
						}
					}

					if(partName.equalsIgnoreCase("parent")) {
						set.setParent(partValue);
					}
				}
			} else { // Make a random styleset to attract user interest

				if(!name.equals("SIMPLE") && !name.equals("MAIN") && !name.equals("ADDITIONAL") &&
					   !name.equals("DEFAULT")) {
					// Note: With extended Stylesets which have parents do not do this.
					if(set.getParent() == null) {
						try {
							set.setForeground(Color.black);
							set.setBackground(Color.red);
						} catch(Exception exc) {
						}
					}

					stylesets.put(set.getName(), set);
					saveStyleset(set.getName());
				}
			}

			stylesets.put(name, set);
		}
	}

	/**
	 * Returns the styleset map.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static Map getStylesets() {
		return stylesets;
	}

	/**
	 * Returns the styleset of a certain type.
	 *
	 * @param i TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static GraphicsStyleset getTypeset(int i) {
		return typeSets[i];
	}

	/**
	 * Loads all custom stylesets. Checks the property "CellRenderer.CustomStylesets" for names of
	 * stylesets and searches the given sets.
	 */
	public static void loadStylesets() {
		String custom = settings.getProperty("CellRenderer.CustomStylesets");

		if(custom != null) {
			StringTokenizer st = new StringTokenizer(custom, ";");

			while(st.hasMoreElements()) {
				loadStyleset(st.nextToken());
			}
		}
	}

	/**
	 * Saves a single styleset.
	 *
	 * @param name TODO: DOCUMENT ME!
	 */
	protected static void saveStyleset(String name) {
		if((stylesets != null) && stylesets.containsKey(name)) {
			GraphicsStyleset set = (GraphicsStyleset) stylesets.get(name);
			String custom = settings.getProperty("CellRenderer.CustomStylesets", "");

			if(custom.indexOf(name) == -1) {
				if(custom.length() == 0) {
					custom = name;
				} else {
					custom += (";" + name);
				}

				settings.setProperty("CellRenderer.CustomStylesets", custom);
			}

			String def = createDefinitionString(set);
			settings.setProperty("CellRenderer.Stylesets." + name, def);
		}
	}

	/**
	 * Stores all stylesets(custom and type) into the settings.
	 */
	public static void saveStylesets() {
		// custom sets
		if(stylesets != null) {
			StringBuffer custom = new StringBuffer();
			Iterator it = stylesets.keySet().iterator();

			while(it.hasNext()) {
				String name = (String) it.next();
				GraphicsStyleset set = (GraphicsStyleset) stylesets.get(name);
				String def = createDefinitionString(set);
				settings.setProperty("CellRenderer.Stylesets." + name, def);

				if(custom.length() > 0) {
					custom.append(';');
				}

				custom.append(name);
			}

			settings.setProperty("CellRenderer.CustomStylesets", custom.toString());
		}
	}

	/**
	 * Deletes a styleset out of the stylesets map.
	 *
	 * @param styleset TODO: DOCUMENT ME!
	 */
	public static void removeStyleset(String styleset) {
		if(stylesets != null) {
			stylesets.remove(styleset);
			settings.remove("CellRenderer.Stylesets." + styleset);
		}
	}

	protected static String encodeColor(Color c) {
		int i = (c.getRed() << 16) | (c.getGreen() << 8) | c.getBlue();

		return '#' + Integer.toHexString(i);
	}

	protected static String encodeFont(Font f) {
		StringBuffer buf = new StringBuffer();
		buf.append(f.getFamily());
		buf.append('-');

		if(f.isPlain()) {
			buf.append("plain-");
		} else {
			if(f.isBold()) {
				buf.append("bold-");
			}

			if(f.isItalic()) {
				buf.append("italic-");
			}
		}

		buf.append(f.getSize());

		return buf.toString();
	}

	protected static String createDefinitionString(GraphicsStyleset set) {
		StringBuffer buf = new StringBuffer();

		if(set.getForeground() != null) {
			buf.append("foreground=");
			buf.append(encodeColor(set.getForeground()));
		}

		if(set.getBackground() != null) {
			if(buf.length() > 0) {
				buf.append(';');
			}

			buf.append("background=");
			buf.append(encodeColor(set.getBackground()));
		}

		if(set.getSelectedForeground() != null) {
			if(buf.length() > 0) {
				buf.append(';');
			}

			buf.append("selectedforeground=");
			buf.append(encodeColor(set.getSelectedForeground()));
		}

		if(set.getSelectedBackground() != null) {
			if(buf.length() > 0) {
				buf.append(';');
			}

			buf.append("selectedbackground=");
			buf.append(encodeColor(set.getSelectedBackground()));
		}

		if(set.getFont() != null) {
			if(buf.length() > 0) {
				buf.append(';');
			}

			buf.append("font=");
			buf.append(encodeFont(set.getFont()));
		}

		if(buf.length() > 0) {
			buf.append(';');
		}

		buf.append("horizontaltextposition=");

		int i = set.getHorizontalPos();

		if(i == SwingConstants.LEFT) {
			buf.append("LEFT");
		} else if(i == SwingConstants.CENTER) {
			buf.append("CENTER");
		} else if(i == SwingConstants.RIGHT) {
			buf.append("RIGHT");
		} else {
			buf.append("unknown");
		}

		buf.append(';');

		buf.append("verticaltextposition=");
		i = set.getVerticalPos();

		if(i == SwingConstants.TOP) {
			buf.append("TOP");
		} else if(i == SwingConstants.CENTER) {
			buf.append("CENTER");
		} else if(i == SwingConstants.BOTTOM) {
			buf.append("BOTTOM");
		} else {
			buf.append("unknown");
		}

		if(!set.isExtractedParent() && (set.getParent() != null)) {
			buf.append(";parent=");
			buf.append(set.getParent());
		}

		return buf.toString();
	}

	/**
	 * Overrides JComponent.getToolTipText to return the tooltip of the underlying label or null,
	 * if no label found.
	 *
	 * @param e TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getToolTipText(MouseEvent e) {
		if(e != null) {
			// reprocess layout to have the sizes that were displayed
			doLayout();

			Point p = e.getPoint();
			Rectangle rect = new Rectangle();

			for(int i = 0; i < getComponentCount(); i++) {
				Component c = getComponent(i);
				rect = c.getBounds(rect);

				if(rect.contains(p.x, p.y)) {
					if(c instanceof JComponent) {
						return ((JComponent) c).getToolTipText();
					}

					return null;
				}
			}
		}

		return null;
	}

	/**
	 * A box layout assuring that all components have the same height.
	 */
	protected class SameHeightBoxLayout implements LayoutManager {
		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param s TODO: DOCUMENT ME!
		 * @param c TODO: DOCUMENT ME!
		 */
		public void addLayoutComponent(String s, Component c) {
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param c TODO: DOCUMENT ME!
		 */
		public void removeLayoutComponent(Component c) {
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param target TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public Dimension minimumLayoutSize(Container target) {
			return preferredLayoutSize(target);
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param target TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public Dimension preferredLayoutSize(Container target) {
			Dimension dim = new Dimension();

			if(target.getComponentCount() > 0) {
				for(int i = 0; i < target.getComponentCount(); i++) {
					Dimension dim2 = target.getComponent(i).getPreferredSize();
					dim.width += dim2.width;

					if(dim2.height > dim.height) {
						dim.height = dim2.height;
					}
				}

				dim.height++;
			}

			return dim;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param target TODO: DOCUMENT ME!
		 */
		public void layoutContainer(Container target) {
			if(target.getComponentCount() > 0) {
				int x = 0;
				int height = target.getHeight();

				if(height <= 0) {
					height = preferredLayoutSize(target).height;
				}

				for(int i = 0; i < target.getComponentCount(); i++) {
					Component c = target.getComponent(i);
					Dimension dim = c.getPreferredSize();
					c.setBounds(x, 0, dim.width, height);
					x += dim.width;
				}
			}
		}
	}
}
