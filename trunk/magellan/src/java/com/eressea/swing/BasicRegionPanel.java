// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.swing;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.LookAndFeel;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.View;

import com.eressea.HasRegion;
import com.eressea.Region;
import com.eressea.event.EventDispatcher;
import com.eressea.event.GameDataEvent;
import com.eressea.event.SelectionEvent;
import com.eressea.event.SelectionListener;
import com.eressea.swing.layout.GridBagHelper;
import com.eressea.swing.preferences.PreferencesAdapter;
import com.eressea.util.CollectionFactory;
import com.eressea.util.logging.Logger;
import com.eressea.util.replacers.ReplacerHelp;
import com.eressea.util.replacers.ReplacerSystem;


/**
 * A GUI component displaying very basic data about regions.
 */
public class BasicRegionPanel extends InternationalizedDataPanel implements SelectionListener {
	private final static Logger log = Logger.getInstance(BasicRegionPanel.class);

	private ReplacerSystem replacer;
	
    private HTMLLabel html;
    //private JLabel html;

	private String def;
	
	private Region lastRegion;
	
	public BasicRegionPanel(EventDispatcher d, Properties p) {
		super(d, p);
		dispatcher.addSelectionListener(this);
		init();
	}

	public void gameDataChanged(GameDataEvent e) {
		lastRegion = null;
		parseDefinition(def);
	}
	
	public void selectionChanged(SelectionEvent e) {
		Object o = e.getActiveObject();
		if (o instanceof Region) {
			show((Region)o);
		} else if (o instanceof HasRegion) {
			show(((HasRegion)o).getRegion());
		}
	}

	private void show(Region r) {
		lastRegion = r;
		Object rep = replacer.getReplacement(r);
		// we dont take care if there are unreplaceable stuff
		html.setText(rep.toString());
	}
			
	private void init() {
		def = settings.getProperty("BasicRegionPanel.Def");
		if (def == null) {
			def = getString("default");
		}
        if(!BasicHTML.isHTMLString(def)) {
			// preparse the definition if not html format
			def = makeHTMLFromString(def,false);
			settings.setProperty("BasicRegionPanel.Def",def);
		}
		parseDefinition(def);
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

        GridBagHelper.setConstraints(
            c, 0, 0, GridBagConstraints.REMAINDER, 1, 1.0, 1.0, /* different weighty!*/
            GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, c.insets, 0, 0);

		//html = new JLabel();
		;
		//html.setContentType("text/html");
		//html.setFont(new Font("Arial", Font.PLAIN, html.getFont().getSize()));
		//html.setEditable(false);
		add(html = new HTMLLabel(), c);

		// parse the def for ourself
		show(null);
	}

	// one separator on x labels, or no separators (x<=0)
	private final static int separatorDist = 2;

	/** 
	 * this is a helper function to migrate the old stuff to the new html layout 
	 */
	public static String makeHTMLFromString(String def,boolean filter) {
		StringBuffer sb = new StringBuffer();
		sb.append("<html>\n<body>\n<table  cellpadding=0 width=100%>\n");
		if(log.isDebugEnabled()) {
			log.debug("BasicRegionPanel.makeHTMLFromString: string (of length "+def.length()+"):\n"+def);
		}
		for(Iterator iterRow = new BasicStringTokenizer(def,"\\\\"); iterRow.hasNext(); ) {
			// create new rows
			String row = (String) iterRow.next();
			if(log.isDebugEnabled()) {
				log.debug("BasicRegionPanel.makeHTMLFromString: working on row "+row);
			}
			sb.append("<tr>\n");
			int i=0;
			for(Iterator iter = new BasicStringTokenizer(row,"&&"); iter.hasNext(); ) {
				String str = (String) iter.next();
				sb.append("<td>");
				if (!filter || str.indexOf('§') == -1) {
					// filter unreplaced strings?
					sb.append(str);
				}
				sb.append("</td>\n");
				i++;
				if(i % separatorDist == 0 && iter.hasNext()) {
					// mod seperatorDist: include new separator
					sb.append("<td></td>\n");
				}
			}
			sb.append("</tr>\n");
		}
		sb.append("</table>\n</body>\n</html>");
		
		String htmlText = sb.toString();
		
		if(log.isDebugEnabled()) {
			log.debug("BasicRegionPanel.makeHTMLFromString: transforming string \n"+def.replace('§','#')+
					  "\" to "+htmlText.replace('§','#'));
		}
		return htmlText;
	}
	
	protected Dimension getDimension(String def) {
		int cols = 1, rows = 1, index = 0;
		int curcols = 1;
		while(def.indexOf("&&", index)>=0 || def.indexOf("\\\\", index) >= 0) {
			int index1 = def.indexOf("&&", index);
			if (index1 == -1) {
				index1 = Integer.MAX_VALUE;
			}
			int index2 = def.indexOf("\\\\", index);
			if (index2 == -1) {
				index2 = Integer.MAX_VALUE;
			}
			if (index1 < index2) {
				curcols++;
				index = index1+2;
			} else {
				if (curcols > cols) {
					cols = curcols;					
				}
				curcols = 1;
				rows++;
				index = index2+2;
			}
			if (curcols > cols) {
				cols = curcols;
			}
		}
		return new Dimension(cols, rows);
	}
		
	protected void parseDefinition(String def) {
		replacer = ReplacerHelp.createReplacer(def);
	}
	
	public PreferencesAdapter getPreferredAdapter() {
		return new BRPPreferences();
	}
	
	public String getDefinition() {
		return def;
	}
	
	public void setDefinition(String d) {
		settings.setProperty("BasicRegionPanel.Def", d);
		def = d;
		parseDefinition(def);
		show(lastRegion);
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
			defaultTranslations.put("prefs.title", "Region short info");
			defaultTranslations.put("default", "<html>\n<body>\n<table  cellpadding=0 width=100%>\n<tr>\n<td>Peasants:</td>\n<td align=right>\u00A7peasants\u00A7</td>\n<td></td>\n<td>Trade:</td>\n<td align=right>\u00A7maxtrade\u00A7</td>\n</tr>\n<tr>\n<td>Recruits:</td>\n<td align=right>\u00A7recruit\u00A7</td>\n<td></td>\n<td>Trees:</td>\n<td align=right>\u00A7trees\u00A7</td>\n</tr>\n<tr>\n<td>Max. taxes:</td>\n<td align=right>\u00A7if\u00A7<\u00A7peasants\u00A7maxWorkers\u00A7*\u00A7peasants\u00A7-\u00A7peasantWage\u00A710\u00A7else\u00A7-\u00A7*\u00A7maxWorkers\u00A7peasantWage\u00A7*\u00A710\u00A7peasants\u00A7end\u00A7</td>\n<td></td>\n<td>Young trees:</td>\n<td align=right>\u00A7sprouts\u00A7</td>\n</tr>\n<tr>\n<td>Max. Entertain.:</td>\n<td align=right>\u00A7entertain\u00A7</td>\n<td></td>\n<td>Horses:</td>\n<td align=right>\u00A7horses\u00A7</td>\n</tr>\n<tr>\n<td>Pool-silver:</td>\n<td align=right>\u00A7priv\u00A7100\u00A7item\u00A7Silber\u00A7priv\u00A7clear\u00A7</td>\n<td></td>\n<td>Iron/Laen:</td>\n<td align=right>\u00A7+\u00A7laen\u00A7iron</td>\n</tr>\n</table>\n</body>\n</html>");
		}
		return defaultTranslations;
	}
	


	protected class BRPPreferences extends JPanel implements PreferencesAdapter {
		
		protected JTextPane defText;
		
		public BRPPreferences() {

			this.setLayout(new BorderLayout());
			this.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), getString("prefs.title")));
			//text pane
			defText = new JTextPane();
			defText.setText(getDefinition());
			this.add(new JScrollPane(defText),BorderLayout.CENTER);
		}
		
		public void applyPreferences() {
			String t = defText.getText();
			if (!t.equals(getDefinition())) {
				setDefinition(t);
			}
		}
		
		public Component getComponent() {
			return this;
		}
		
		public String getTitle() {
			return getString("prefs.title");
		}		
	}

	/**
	 * This class emulates the behaviour of StringTokenizer with a string as delimiter.
	 */
	public static class BasicStringTokenizer implements Iterator {
		int    newPosition = -1;
		int    currentPosition = 0;
		int    maxPosition = 0;
		String str;
		String delim;
		BasicStringTokenizer(String str, String delim) {
			this.str = delim+str+delim;
			this.delim = delim;
			maxPosition = str.length();		   
		}
		
		public boolean hasNext() {
			newPosition = skipDelims(currentPosition);
			return newPosition < maxPosition;
		}

		
		/**
		 * Skips ahead from startPos and returns the index of the next delimiter
		 * character encountered, or maxPosition if no such delimiter is found.
		 */
		private int scanToken(int startPos) {
			int position = str.indexOf(delim,startPos);
			if(position == -1) {
				position = maxPosition;
			}
			return position;
		}

		/**
		 * Skips delimiters starting from the specified position. Returns the 
		 * index of the first non-delimiter character at or after startPos. 
		 */
		private int skipDelims(int startPos) {
			int position = str.indexOf(delim, startPos);
			int ret = startPos;
			if(position == startPos)  ret +=delim.length();

			return ret;
		}

		public Object next() {
			currentPosition = newPosition > 0 ? newPosition : skipDelims(currentPosition);

			if (currentPosition >= maxPosition) {
				throw new java.util.NoSuchElementException();
			}

			// reset newPosition
			newPosition = -1;

			int start = currentPosition;
			currentPosition = scanToken(currentPosition);

			String ret = str.substring(start,currentPosition);
			return ret;
		}
		
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	public static class HTMLLabel extends JComponent {
		private String text;
		
		private transient View view;
		

		/**  requires: 'text' is HTML string. */
		public HTMLLabel(String text) {
			// we need to install the LookAndFeel Fonts from the beginning
			LookAndFeel.installColorsAndFont(this, "Label.background", "Label.foreground", "Label.font");
			setText(text);
		}

		public HTMLLabel() {
			this("<html></html>");
		}

		public void updateUI() {
			super.updateUI();
			LookAndFeel.installColorsAndFont(this, "Label.background", "Label.foreground", "Label.font");
		}
    
		/**  requires: 's' is HTML string. */
		public void setText(String s) {
			if (equal(s, text)) {
				return;
			}
			
			if (!BasicHTML.isHTMLString(s)) {
				throw new IllegalArgumentException();
			}

			text = s;

			revalidate();
			repaint();
			
			view = null;
		}

		public String getText() {
			return text;
		}

		
		public Dimension getMinimumSize() {
			if (isMinimumSizeSet()) {
				return super.getMinimumSize();
			}

			Insets i = getInsets();
			
			Dimension d = new Dimension(i.left + i.right, i.top + i.bottom);
			
			d.width += view().getMinimumSpan(View.X_AXIS);
			d.height += view().getMinimumSpan(View.Y_AXIS);
			
			return d;
		}
		
		public Dimension getPreferredSize() {
			if (isPreferredSizeSet())
				return super.getPreferredSize();
			
			Insets i = getInsets();
			
			Dimension d = new Dimension(i.left + i.right, i.top + i.bottom);
			
			d.width += view().getPreferredSpan(View.X_AXIS);
			d.height += view().getPreferredSpan(View.Y_AXIS);
			
			return d;
		}
		
		public Dimension getMaximumSize() {
			if (isMaximumSizeSet())
				return super.getMaximumSize();
			
			Insets i = getInsets();
			
			Dimension d = new Dimension(i.left + i.right, i.top + i.bottom);
			
			d.width += view().getMaximumSpan(View.X_AXIS);
			d.height += view().getMaximumSpan(View.Y_AXIS);
			
			return d;
		}
		
		protected View view() {
			if (view == null) 
				view = BasicHTML.createHTMLView(this, text);
			
			return view;
		}
		
		public void setForeground(Color c) {
			if (!equal(c, getForeground()))
				view = null;
			
			super.setForeground(c);
		}
		
		
		public void setFont(Font f) {
			if (!equal(f, getFont()))
				view = null;
			

			if(log.isDebugEnabled()) {
				log.debug("HTMLLabel.setFont("+f+" called");
			}
			super.setFont(f);
		}
		
		private static boolean equal(Object a, Object b) {
			return a == null ? b == null : a.equals(b);
		}
		
		protected void paintComponent(Graphics g) {
			if (isOpaque()) // incorrect, but done as everywhere
				{
					g.setColor(getBackground());
					g.fillRect(0, 0, getWidth(), getHeight());
				}
			
			view().paint(g, HTMLLabel.calculateInnerArea(this, null));
		}

		public static Rectangle calculateInnerArea(JComponent c, Rectangle r) {
			if (c == null) {
				return null;
			}
			Rectangle rect = r;
			Insets insets = c.getInsets();
			
			if (rect == null) {
				rect = new Rectangle();
			}
			
			rect.x = insets.left;
			rect.y = insets.top;
			rect.width = c.getWidth() - insets.left - insets.right;
			rect.height = c.getHeight() - insets.top - insets.bottom;
			
			return rect;
		}
	}
}
