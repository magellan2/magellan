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

package com.eressea.swing.preferences;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.eressea.util.CollectionFactory;

/**
 * DOCUMENT ME!
 *
 * @author Andreas
 * @version
 */
public abstract class DetailedPreferencesAdapter extends JPanel implements PreferencesAdapter {
	/** TODO: DOCUMENT ME! */
	public static final String TRUE = "true";

	/** TODO: DOCUMENT ME! */
	public static final String FALSE = "false";

	/** TODO: DOCUMENT ME! */
	public boolean properties[];
	protected JCheckBox boxes[];
	protected JPanel detailContainers[];
	protected String detailHelps[];
	protected String detailTitles[];
	protected String settKeys[][];
	protected String langKeys[];
	protected Properties settings;
	protected String prefix;
	protected int rows = 0;
	protected int count = 0;
	protected int subcount[];

	/**
	 * Creates new DetailedPreferencesAdapter
	 *
	 * @param count TODO: DOCUMENT ME!
	 * @param subcount TODO: DOCUMENT ME!
	 * @param p TODO: DOCUMENT ME!
	 * @param prefix TODO: DOCUMENT ME!
	 * @param sK TODO: DOCUMENT ME!
	 * @param lK TODO: DOCUMENT ME!
	 * @param rows TODO: DOCUMENT ME!
	 */
	public DetailedPreferencesAdapter(int count, int subcount[], Properties p, String prefix,
									  String sK[][], String lK[], int rows) {
		this(count, subcount, p, prefix, sK, lK, rows, false);
	}

	/**
	 * Creates a new DetailedPreferencesAdapter object.
	 *
	 * @param count TODO: DOCUMENT ME!
	 * @param subcount TODO: DOCUMENT ME!
	 * @param p TODO: DOCUMENT ME!
	 * @param prefix TODO: DOCUMENT ME!
	 * @param sK TODO: DOCUMENT ME!
	 * @param lK TODO: DOCUMENT ME!
	 * @param rows TODO: DOCUMENT ME!
	 * @param waitWithInit TODO: DOCUMENT ME!
	 */
	public DetailedPreferencesAdapter(int count, int subcount[], Properties p, String prefix,
									  String sK[][], String lK[], int rows, boolean waitWithInit) {
		settings = p;
		this.prefix = prefix;
		settKeys = sK;
		langKeys = lK;
		this.rows = rows;
		this.subcount = subcount;
		this.count = count;

		int sum = 0;

		if(subcount != null) {
			for(int i = 0; i < count; i++) {
				sum += Math.max(0, subcount[i]);
				sum++;
			}
		} else {
			sum = count;
		}

		properties = new boolean[sum];
		boxes = new JCheckBox[sum];

		initProperties();

		if(!waitWithInit) {
			init();
		}
	}

	protected void init() {
		initBoxes();

		detailContainers = new JPanel[count];
		detailTitles = new String[count];
		detailHelps = new String[count];

		setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

		JPanel help = new JPanel(new GridBagLayout());
		GridBagConstraints con = new GridBagConstraints();
		con.gridwidth = 1;
		con.gridheight = 1;
		con.fill = GridBagConstraints.NONE;
		con.anchor = GridBagConstraints.NORTHWEST;
		con.gridx = 0;
		con.gridy = 0;

		int cBox = 0;
		ActionListener al = new DetailListener(this);
		String bTitle = getOwnString("button.title");

		for(int i = 0; i < count; i++) {
			help.add(boxes[cBox], con);
			cBox++;

			if((subcount != null) && (subcount[i] != 0)) { // create detail

				// create the dialog panel
				if(subcount[i] > 0) {
					detailContainers[i] = new JPanel();
					detailContainers[i].setLayout(new BoxLayout(detailContainers[i],
																BoxLayout.Y_AXIS));

					for(int j = 0; j < subcount[i]; j++) {
						detailContainers[i].add(boxes[cBox + j]);
					}

					cBox += subcount[i];
				} else {
					detailContainers[i] = getExternalDetailContainer(i);
				}

				detailTitles[i] = getString("prefs.dialogs." + String.valueOf(i) + ".title");

				try {
					detailHelps[i] = getString("prefs.dialogs." + String.valueOf(i) + ".help");
				} catch(Exception mexc) {
				}

				// maybe no help available
				// create the button
				JButton button = new JButton(bTitle);
				button.setActionCommand(String.valueOf(i));
				button.addActionListener(al);
				con.gridx++;
				help.add(button, con);
				con.gridx--;
			}

			con.gridy++;

			if((rows > 0) && (con.gridy >= rows)) {
				con.gridy = 0;
				con.gridx += 2;
			}
		}

		add(help);
	}

	protected JPanel getExternalDetailContainer(int index) {
		return null;
	}

	protected void initProperties() {
		for(int i = 0; i < properties.length; i++) {
			properties[i] = settings.getProperty(prefix + "." + settKeys[i][0], settKeys[i][1])
									.equals(TRUE);
		}
	}

	protected void initBoxes() {
		for(int i = 0; i < boxes.length; i++) {
			boxes[i] = new JCheckBox(getString(langKeys[i]), properties[i]);

			try {
				boxes[i].setToolTipText(getString(langKeys[i] + ".tooltip"));
			} catch(Exception mexc) {
			}

			// maybe no tooltip
		}
	}

	protected boolean checkBox(int i, String key, String def) {
		if(properties[i] != boxes[i].isSelected()) {
			properties[i] = boxes[i].isSelected();

			if((properties[i] && def.equals(TRUE)) || (!properties[i] && def.equals(FALSE))) {
				settings.remove(prefix + "." + key);
			} else {
				settings.setProperty(prefix + "." + key, properties[i] ? TRUE : FALSE);
			}

			return true;
		}

		return false;
	}

    public void initPreferences() {
        // TODO: implement it
    }

	/**
	 * TODO: DOCUMENT ME!
	 */
	public void applyPreferences() {
		List l = CollectionFactory.createLinkedList();

		for(int i = 0; i < boxes.length; i++) {
			if(checkBox(i, settKeys[i][0], settKeys[i][1])) {
				l.add(new Integer(i));
			}
		}

		if(l.size() > 0) {
			int indices[] = new int[l.size()];

			for(int i = 0, max = l.size(); i < max; i++) {
				indices[i] = ((Integer) l.get(i)).intValue();
			}

			applyChanges(indices);
		}
	}

	protected abstract void applyChanges(int indices[]);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Component getComponent() {
		return this;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getTitle() {
		return getString("prefs.title");
	}

	protected abstract String getString(String key);

	protected String getOwnString(String key) {
		return com.eressea.util.Translations.getTranslation(DetailedPreferencesAdapter.class, key);
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
			defaultTranslations.put("button.title", "Details...");
			defaultTranslations.put("ddialog.options.title", "Options");
			defaultTranslations.put("ok", "OK");
			defaultTranslations.put("ddialog.help.title", "Help");
		}

		return defaultTranslations;
	}

	protected class DetailListener implements ActionListener {
		protected JComponent src;

		/**
		 * Creates a new DetailListener object.
		 *
		 * @param src TODO: DOCUMENT ME!
		 */
		public DetailListener(JComponent src) {
			this.src = src;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param actionEvent TODO: DOCUMENT ME!
		 */
		public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
			int index = -1;

			try {
				index = Integer.parseInt(actionEvent.getActionCommand());
			} catch(Exception exc) {
				return;
			}

			java.awt.Component c = src.getTopLevelAncestor();
			DetailsDialog dialog = null;
			String title = detailTitles[index];

			if(c instanceof java.awt.Frame) {
				dialog = new DetailsDialog((java.awt.Frame) c, title);
			} else if(c instanceof java.awt.Dialog) {
				dialog = new DetailsDialog((java.awt.Dialog) c, title);
			}

			if(dialog != null) {
				dialog.init(detailContainers[index], detailHelps[index]);
				dialog.pack();
				dialog.setLocationRelativeTo(src);
				dialog.show();
			}
		}
	}

	protected class DetailsDialog extends JDialog implements ActionListener {
		/**
		 * Creates new DetailsDialog
		 *
		 * @param parent TODO: DOCUMENT ME!
		 * @param title TODO: DOCUMENT ME!
		 */
		public DetailsDialog(Frame parent, String title) {
			super(parent, title, true);
		}

		/**
		 * Creates a new DetailsDialog object.
		 *
		 * @param parent TODO: DOCUMENT ME!
		 * @param title TODO: DOCUMENT ME!
		 */
		public DetailsDialog(Dialog parent, String title) {
			super(parent, title, true);
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param adapter TODO: DOCUMENT ME!
		 * @param help TODO: DOCUMENT ME!
		 */
		public void init(JComponent adapter, String help) {
			adapter.setBorder(BorderFactory.createTitledBorder(getOwnString("ddialog.options.title")));

			JButton okButton = new JButton(getOwnString("ok"));
			okButton.addActionListener(this);

			JPanel south = null;

			if(help != null) {
				JTextArea text = new JTextArea(help, 5, 40);
				text.setLineWrap(true);
				text.setWrapStyleWord(true);
				text.setEditable(false);
				text.setBackground(getContentPane().getBackground());
				text.setForeground(Color.black); // don't show in disabled color

				JScrollPane pane = new JScrollPane(text, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
												   JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
				pane.setBorder(BorderFactory.createTitledBorder(getOwnString("ddialog.help.title")));

				south = new JPanel(new BorderLayout());
				south.add(pane, BorderLayout.CENTER);

				JPanel button = new JPanel(new FlowLayout(FlowLayout.CENTER));
				button.add(okButton);
				south.add(button, BorderLayout.SOUTH);
			} else {
				south = new JPanel(new FlowLayout(FlowLayout.CENTER));
				south.add(okButton);
			}

			getContentPane().add(adapter, BorderLayout.CENTER);
			getContentPane().add(south, BorderLayout.SOUTH);
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param e TODO: DOCUMENT ME!
		 */
		public void actionPerformed(ActionEvent e) {
			this.setVisible(false);
		}
	}
}
