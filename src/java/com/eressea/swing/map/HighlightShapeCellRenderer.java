// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Ilja Pavkovic, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.swing.map;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.Properties;

import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.eressea.Coordinate;
import com.eressea.Region;
import com.eressea.swing.preferences.PreferencesAdapter;
import com.eressea.util.CollectionFactory;
import com.eressea.util.Colors;

public class HighlightShapeCellRenderer extends HexCellRenderer {

	private final static int ALPHALEVEL  = 100;

	private Color   selectedColor = Color.white;
	private Color   activeColor   = Color.red;
	private boolean drawFilled    = true;

	public HighlightShapeCellRenderer(CellGeometry geo, Properties settings) {
		super(geo, settings);
		if(settings != null) {
			try {
				selectedColor = Colors.decode(settings.getProperty("HighlightShapeCellRenderer.selectedColor", Colors.encode(selectedColor)));
			} catch (NumberFormatException e) {	}
			try {
				activeColor = Colors.decode(settings.getProperty("HighlightShapeCellRenderer.activeColor", Colors.encode(activeColor)));
			} catch (NumberFormatException e) {	}
			drawFilled = new Boolean(settings.getProperty("HighlightShapeCellRenderer.drawfilled", "true")).booleanValue();
		}
	}
		
	public void render(Object obj, boolean active, boolean selected) {
		if (obj instanceof Region) {
			Region r = (Region) obj;
			if (selected) {
				drawAndPossiblyFillPolygon(r, selectedColor);
			}
			if (active) {
				drawAndPossiblyFillPolygon(r, activeColor);
			}
		}
	}
	

	private void drawAndPossiblyFillPolygon(Region r, Color col) {
		Coordinate c = r.getCoordinate();
		
		Rectangle rect = cellGeo.getCellRect(c.x, c.y);
		rect.translate(-offset.x, -offset.y);
		
		Polygon p = cellGeo.getScaledPolygon();
		// make a copy of the polygon
		p = new Polygon(p.xpoints, p.ypoints, p.npoints);
		p.translate(rect.x, rect.y);

		if(drawFilled) {
			Color newCol = new Color(col.getRed(),col.getGreen(), col.getBlue(), ALPHALEVEL);
			graphics.setColor(newCol);
			graphics.fillPolygon(p);
		}

		graphics.setColor(col);
		if(graphics instanceof Graphics2D) {
			((Graphics2D) graphics).setStroke(getDefaultStroke());
		}
		graphics.drawPolygon(p);
		if(1==2) {
			p.translate(1, 0);
			graphics.drawPolygon(p);
			p.translate(0, 1);
			graphics.drawPolygon(p);
			p.translate(-1, 0);
			graphics.drawPolygon(p);
		}
	}

	// use this as singleton to this object
	private static BasicStroke defaultStroke = new BasicStroke(2.0f);
	private static BasicStroke getDefaultStroke() {
		return defaultStroke;
	}
	
	
	public int getPlaneIndex() {
		return Mapper.PLANE_HIGHLIGHT;
	}

	private boolean getDrawFilled() {
		return drawFilled;
	}
	private void setDrawFilled(boolean bool) {
		drawFilled = bool;
		if(settings != null) {
			settings.setProperty("HighlightShapeCellRenderer.drawfilled", (new Boolean(bool)).toString());
		}
	}

	private Color getActiveColor() {
		return activeColor;
	}
	private void setActiveColor(Color c) {
		activeColor = c;
		if(settings != null) {
			settings.setProperty("HighlightShapeCellRenderer.activeColor",Colors.encode(activeColor));
		}
	}

	private Color getSelectedColor() {
		return selectedColor;
	}
	private void setSelectedColor(Color c) {
		selectedColor = c;
		if(settings != null) {
			settings.setProperty("HighlightShapeCellRenderer.selectedColor",Colors.encode(selectedColor));
		}
	}

	public PreferencesAdapter getPreferencesAdapter() {
		return new Preferences(this);
	}
	
	// pavkovic 2003.01.28: this is a Map of the default Translations mapped to this class
	// it is called by reflection (we could force the implementation of an interface,
	// this way it is more flexible.)
	// Pls use this mechanism, so the translation files can be created automagically
	// by inspecting all classes.
	private static Map defaultTranslations;
	public synchronized static Map getDefaultTranslations() {
		if(defaultTranslations == null) {
			defaultTranslations = CollectionFactory.createHashtable();
			defaultTranslations.put("name" , "Marker renderer (shapes)");
			defaultTranslations.put("textcolor" , "text color");
			defaultTranslations.put("drawfilled" , "fill with border color");
			
			defaultTranslations.put("lblselectedcolor" , "border color of selected regions: ");
			defaultTranslations.put("lblactivecolor" , "border color of active regions: ");
		}
		return defaultTranslations;
	}


	protected class Preferences extends JPanel implements PreferencesAdapter {
		// The source component to configure
		protected HighlightShapeCellRenderer source = null;
		
		// GUI elements
		private JPanel pnlSelectedColor  = null;
		private JPanel pnlActiveColor    = null;
		private JCheckBox  chkDrawFilled = null;

		public Preferences(HighlightShapeCellRenderer r) {
			this.source = r;
			init();
		}
		
		private void init() {
			pnlSelectedColor = new JPanel();
			pnlSelectedColor.setSize(50, 200);
			pnlSelectedColor.setBackground(source.getSelectedColor());
			pnlSelectedColor.addMouseListener(new MouseAdapter() {
					public void mousePressed(MouseEvent e) {
						Color newColor = JColorChooser.showDialog(pnlSelectedColor.getTopLevelAncestor(),
																  getString("textcolor"),
																  pnlSelectedColor.getBackground());
						if (newColor != null) {
							pnlSelectedColor.setBackground(newColor);
						}
					}
				});

			JLabel lblSelectedColor = new JLabel(getString("lblselectedcolor"));
			lblSelectedColor.setLabelFor(pnlSelectedColor);

			pnlActiveColor = new JPanel();
			pnlActiveColor.setSize(50, 200);
			pnlActiveColor.setBackground(source.getActiveColor());
			pnlActiveColor.addMouseListener(new MouseAdapter() {
					public void mousePressed(MouseEvent e) {
						Color newColor = JColorChooser.showDialog(pnlActiveColor.getTopLevelAncestor(),
																  getString("textcolor"),
																  pnlActiveColor.getBackground());
						if (newColor != null) {
							pnlActiveColor.setBackground(newColor);
						}
					}
				});

			JLabel lblActiveColor = new JLabel(getString("lblactivecolor"));
			lblActiveColor.setLabelFor(pnlActiveColor);			

			chkDrawFilled = new JCheckBox(getString("drawfilled"), source.getDrawFilled());
			
			this.setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.anchor = GridBagConstraints.WEST;
			c.gridx = 0;
			c.gridy = 0;
			this.add(lblSelectedColor, c);
			c.gridx = 1;
			c.gridy = 0;
			this.add(pnlSelectedColor, c);
			c.gridx = 0;
			c.gridy = 1;
			this.add(lblActiveColor, c);
			c.gridx = 1;
			c.gridy = 1;
			this.add(pnlActiveColor, c);
			c.gridx = 0;
			c.gridy = 2;
			this.add(chkDrawFilled, c);
		}
		
		public void applyPreferences() {
			source.setSelectedColor(pnlSelectedColor.getBackground());
			source.setActiveColor(pnlActiveColor.getBackground());
			source.setDrawFilled(chkDrawFilled.isSelected());
		}

		public Component getComponent() {
			return this;
		}

		public String getTitle() {
			return source.getName();
		}
	}
}
