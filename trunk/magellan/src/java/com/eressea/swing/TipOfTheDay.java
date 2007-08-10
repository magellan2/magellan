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

package com.eressea.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import com.eressea.demo.desktop.DesktopEnvironment;
import com.eressea.demo.desktop.MagellanDesktop;
import com.eressea.util.CollectionFactory;

/**
 * DOCUMENT ME!
 *
 * @author Andreas
 * @version
 */
public class TipOfTheDay extends InternationalizedDialog implements ActionListener {
	// the pane to display the text
	protected JTextPane tipText;
	protected HTMLDocument doc;
	protected HTMLEditorKit kit;
	protected int lastNumber = -1;

	// a checkbox for marking the show state
	protected JCheckBox showTips;

	// settings to store used tips
	protected Properties settings;

	// all tips in language file
	protected List allTips;

	// current usable (non-shown) tips
	protected List nonShown;
	protected static final String NEXT = "next";

	/** TODO: DOCUMENT ME! */
	public static final String HTML_START = "<html><body style=\"margin-left: 20px;margin-top: 15px\"><font color=#4f3f30><b>";

	/** TODO: DOCUMENT ME! */
	public static final String HTML_END = "</b></font></body></html>";

	/** TODO: DOCUMENT ME! */
	public static final String E_HTML_START = "<html><body style=\"margin-left: 20px;margin-top: 2px\"><font color=#4f3f30><b>";

	/** TODO: DOCUMENT ME! */
	public static final String E_HTML_END = "</b></font></body></html>";

	/** TODO: DOCUMENT ME! */
	public static final String E_KEY = "ETIP";

	/** TODO: DOCUMENT ME! */
	public static boolean active = false;

	/** TODO: DOCUMENT ME! */
	public static boolean firstTime = false;

	/**
	 * Creates new TipOfTheDay
	 *
	 * @param parent TODO: DOCUMENT ME!
	 * @param settings TODO: DOCUMENT ME!
	 */
	public TipOfTheDay(Frame parent, Properties settings) {
		super(parent, false);

		setTitle(getString("title"));

		this.settings = settings;

		// init the interface
		initUI();

		// load tips
		initTips();

		pack();

		setLocation(parent);
	}

	protected void setLocation(Component parent) {
		// center if using frame mode
		if(DesktopEnvironment.getMode() == MagellanDesktop.MODE_FRAME) {
			Dimension screen = getToolkit().getScreenSize();
			Dimension size = getSize();
			int x = (screen.width - size.width) / 2;
			int y = (screen.height - size.height) / 2;
			setLocation(x, y);
		} else {
			setLocationRelativeTo(parent);
		}
	}

	/* initializes the dialog
	 *
	 * design is:
	 *
	 * ++++++++++++++++++++++++++++++
	 * + Image + "Did you know?"    +
	 * +       ++++++++++++++++++++++
	 * +       +        Tip         +
	 * ++++++++++++++++++++++++++++++
	 * + Use           +Next/Close  +
	 * ++++++++++++++++++++++++++++++
	 *
	 **/
	protected void initUI() {
		Color foreground = new Color(79, 63, 48);
		Color background = new Color(213, 169, 131);

		JPanel panel = new JPanel(new BorderLayout(2, 2));
		panel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		panel.setBackground(foreground);

		// try to find the image
		Icon icon = null;
		JLabel iconLabel = null;

		try {
			java.net.URL url = com.eressea.resource.ResourcePathClassLoader.getResourceStatically("images/gui/totd.gif");
			icon = new ImageIcon(url);
		} catch(RuntimeException exc) {
		}

		if(icon != null) {
			iconLabel = new JLabel(icon);
			iconLabel.setBackground(background);
			iconLabel.setOpaque(true);
			panel.add(iconLabel, BorderLayout.WEST);
		} else {
			iconLabel = new JLabel(" Pic here ");
			iconLabel.setBackground(background);
			iconLabel.setOpaque(true);
			panel.add(iconLabel, BorderLayout.WEST);
		}

		JLabel didyouknow = new JLabel("    " + getString("didyouknow"));
		Font old = didyouknow.getFont();
		didyouknow.setFont(old.deriveFont(old.getStyle() | Font.ITALIC, 18f));
		didyouknow.setForeground(foreground);
		didyouknow.setBackground(background);
		didyouknow.setOpaque(true);

		kit = new HTMLEditorKit();
		tipText = new JTextPane();
		tipText.setEditorKit(kit);
		tipText.setForeground(foreground);
		tipText.setBackground(background);
		tipText.setBorder(null);

		JScrollPane pane = new JScrollPane(tipText);
		pane.setBorder(null);

		JPanel content = new JPanel(new BorderLayout(0, 2));
		content.setBackground(foreground);
		content.add(didyouknow, BorderLayout.NORTH);
		content.add(pane, BorderLayout.CENTER);
		content.setPreferredSize(new Dimension(300, 200));

		panel.add(content, BorderLayout.CENTER);

		showTips = new JCheckBox(getString("showTips"),
								 settings.getProperty("TipOfTheDay.showTips", "true").equals("true"));
		showTips.setBackground(background);
		showTips.setForeground(foreground);

		JButton close = new JButton(getString("close"));
		close.addActionListener(this);
		close.setBackground(background);
		close.setForeground(foreground);

		JButton next = new JButton(getString("nextTip"));
		next.addActionListener(this);
		next.setActionCommand(NEXT);
		next.setBackground(background);
		next.setForeground(foreground);

		JPanel actions = new JPanel(new GridBagLayout());
		actions.setBackground(background);

		GridBagConstraints c = new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.WEST,
													  GridBagConstraints.NONE,
													  new Insets(1, 1, 1, 1), 0, 0);
		actions.add(showTips, c);

		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttons.setBackground(background);
		buttons.add(next);
		buttons.add(close);
		c.gridx = 1;
		c.anchor = GridBagConstraints.EAST;
		actions.add(buttons, c);

		panel.add(actions, BorderLayout.SOUTH);
		setContentPane(panel);

		addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					quit();
				}
			});
	}

	protected void initTips() {
		allTips = CollectionFactory.createLinkedList();
		nonShown = CollectionFactory.createLinkedList();

		// load all tips
		int count = 0;

		try {
			count = Integer.parseInt(getString("numTips"));
		} catch(Exception exc) {
		}

		if(count > 0) {
			for(int i = 0; i < count; i++) {
				try {
					String s = getString("tip." + String.valueOf(i));

					if(s != null) {
						Tip tip = new Tip();
						tip.number = i;
						tip.text = s;
						allTips.add(tip);
					}
				} catch(Exception exc2) {
				}
			}

			String nonShownProperty = settings.getProperty("TipOfTheDay.Tips");

			if((nonShownProperty != null) && !nonShownProperty.equals("")) {
				StringTokenizer st = new StringTokenizer(nonShownProperty, ",");

				while(st.hasMoreTokens()) {
					try {
						int num = Integer.parseInt(st.nextToken());
						Iterator it = allTips.iterator();

						while(it.hasNext()) {
							Tip tip = (Tip) it.next();

							if(tip.number == num) {
								nonShown.add(tip);

								break;
							}
						}
					} catch(Exception exc3) {
					}
				}
			} else {
				reloadTips();
			}
		}

		if(settings.getProperty("TipOfTheDay.firstTime", "true").equals("true")) {
			firstTime = true;
			settings.setProperty("TipOfTheDay.firstTime", "false");
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean doShow() {
		return firstTime || hasTips();
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean hasTips() {
		return allTips.size() > 0;
	}

	/**
	 * TODO: DOCUMENT ME!
	 */
	public void showNextTip() {
		if(hasTips() || firstTime) {
			// should be
			if(nonShown.size() == 0) {
				reloadTips();
			}

			Tip tip = null;

			if(firstTime) {
				firstTime = false;
				tip = new Tip();
				tip.text = getString("warningString");
			} else {
				int i = 0;

				do {
					i++;

					int pos = (int) (Math.random() * nonShown.size());
					tip = (Tip) nonShown.get(pos);
				} while((i < 10) && (nonShown.size() != 1) && (tip.number == lastNumber));

				lastNumber = tip.number;
				nonShown.remove(tip);

				if(nonShown.size() == 0) {
					reloadTips();
				}
			}

			try {
				HTMLDocument doc2 = new HTMLDocument();

				if(tip.text.startsWith(E_KEY)) {
					kit.insertHTML(doc2, 0,
								   E_HTML_START + "<font size=+1>" + getString("tip.eressea") +
								   "</font><br>" + tip.text.substring(E_KEY.length()) + E_HTML_END,
								   0, 0, null);
				} else {
					kit.insertHTML(doc2, 0, HTML_START + tip.text + HTML_END, 0, 0, null);
				}

				tipText.setStyledDocument(doc2);
				doc = doc2;
				repaint();
			} catch(Exception exc) {
				System.out.println(exc);
			}
		}
	}

	protected void reloadTips() {
		nonShown.clear();
		nonShown.addAll(allTips);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param e TODO: DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent e) {
		if(NEXT.equals(e.getActionCommand())) {
			showNextTip();

			return;
		}

		quit();
	}

	/**
	 * TODO: DOCUMENT ME!
	 */
	public void show() {
		active = true;
		super.show();
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param flag TODO: DOCUMENT ME!
	 */
	public void setVisible(boolean flag) {
		super.setVisible(flag);
		active = flag;
	}

	// close the dialog and save the settings
	protected void quit() {
		setVisible(false);

		StringBuffer buf = new StringBuffer();
		Iterator it = nonShown.iterator();

		while(it.hasNext()) {
			Tip tip = (Tip) it.next();
			buf.append(tip.number);

			if(it.hasNext()) {
				buf.append(",");
			}
		}

		settings.setProperty("TipOfTheDay.Tips", buf.toString());

		settings.setProperty("TipOfTheDay.showTips", showTips.isSelected() ? "true" : "false");
	}

	// simple pair of number and html text
	protected class Tip {
		protected int number;
		protected String text;
	}

	// pavkovic 2003.01.28: this is a Map of the default Translations mapped to this class
	// it is called by reflection (we could force the implementation of an interface,
	// this way it is more flexible.)
	// Pls use this mechanism, so the translation files can be created automagically
	// by inspecting all classes.
	private static Map defaultTranslations;

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static synchronized Map getDefaultTranslations() {
		if(defaultTranslations == null) {
			defaultTranslations = CollectionFactory.createHashtable();
			defaultTranslations.put("title", "Tip Of The Day");
			defaultTranslations.put("showTips", "Show tips at startup");
			defaultTranslations.put("tip.eressea", "Eressea Tip:");
			defaultTranslations.put("tip.obsolete.11",
			"If there are too many icons for skills and items in the region overview it may happen that you cannot see the unit's name. In this case you may turn of the icons by choosing the appropriate icon view state from the 'Tree - Regions' menu. You can turn them on or off there.");
			defaultTranslations.put("tip.10",
					"If you experience strange behaviour and reports do not get saved properly sometimes, it might be a memory problem. Try starting Magellan with \"java -Xmx 256M -jar magellan.jar\" and read the FAQ.");
			defaultTranslations.put("tip.2",
									"The fastest way to load the last report is the key combo <u><i>CTRL-o ALT-o</i></u>.");
			defaultTranslations.put("tip.9",
									"ETIPYou can directly write to other units, even faction disguised ones.  With an E-Mail to <i><u>einheit-NUMBER@eressea.kn-bremen.de</u></i> you send a message to the chieftain of the faction the unit belongs to, i.e.: <u><i>einheit-wegi@eressea.kn-bremen.de</i></u>");
			defaultTranslations.put("tip.8",
									"Bugs or Feature-Requests can be told at <i>http://magellan-client.sourceforge.net/mantis/</i>.");
			defaultTranslations.put("tip.7",
									"To always have known spells and potions in your report you need to add the report of the current week to the report of the last week and save them together.");
			defaultTranslations.put("tip.6",
									"To see the changes (in resources etc.) of a week just add the report of the current week with <u><i>Add...</i></u> to your old report.");
			defaultTranslations.put("tip.5",
									"If you choose <i>\"Skip units with unconfirmed orders\"</i> at the order save dialog, you can send orders for only a part of the units. The game server will use only orders of confirmed units, the orders of other units will remain.");
			defaultTranslations.put("tip.4",
									"Magellan is able to send your orders per Mail. You can find this ability under \"Files - Save orders\" by choosing \"E-Mail\".");
			defaultTranslations.put("tip.3",
									"If you altered the Magellan options but do not want to save them during quitting you can choose \"Abort\" from the Files menu.");
			defaultTranslations.put("tip.1",
									"Magellan is able to open your report out of a zip archive. You just have to choose <i>Zip</i> from the file-type box at the open dialog.");
			defaultTranslations.put("tip.0",
									"To mark regions on the map, simply hold the <i><u>Shift<u><i>-key and click at the region(s).");
			defaultTranslations.put("numTips", "11");
			defaultTranslations.put("didyouknow", "Did you know...");
			defaultTranslations.put("nextTip", "Next tip");
			defaultTranslations.put("close", "Close");

			defaultTranslations.put("warningString",
									"<font size=+2>Important!</font><p>Magellan has some features that are only prognostic. Use them with care for Magellan is not the Eressea Server. The developers are not responsible for any damage on your faction resulting from these features.</p><p>For more information please consult the Magellan help.");
		}

		return defaultTranslations;
	}
}
