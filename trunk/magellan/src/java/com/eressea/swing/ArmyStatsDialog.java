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

/*
 * ArmyStatsDialog.java
 *
 * Created on 5. März 2002, 12:00
 */
package com.eressea.swing;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import com.eressea.GameData;
import com.eressea.StringID;
import com.eressea.event.EventDispatcher;
import com.eressea.rules.SkillType;
import com.eressea.util.CollectionFactory;

/**
 * DOCUMENT ME!
 *
 * @author Andreas
 * @version
 */
public class ArmyStatsDialog extends InternationalizedDataDialog implements ActionListener {
	protected ArmyStatsPanel panel;
	protected JCheckBox categorize;
	protected JButton excludeDialog;
	protected List excludeSkills;
	protected List excludeNames;
	protected List excludeCombatStates;

	/**
	 * Creates new ArmyStatsDialog
	 *
	 * @param owner TODO: DOCUMENT ME!
	 * @param ed TODO: DOCUMENT ME!
	 * @param data TODO: DOCUMENT ME!
	 * @param settings TODO: DOCUMENT ME!
	 */
	public ArmyStatsDialog(Frame owner, EventDispatcher ed, GameData data, Properties settings) {
		this(owner, ed, data, settings, null);
	}

	/**
	 * Creates a new ArmyStatsDialog object.
	 *
	 * @param owner TODO: DOCUMENT ME!
	 * @param ed TODO: DOCUMENT ME!
	 * @param data TODO: DOCUMENT ME!
	 * @param settings TODO: DOCUMENT ME!
	 * @param selRegions TODO: DOCUMENT ME!
	 */
	public ArmyStatsDialog(Frame owner, EventDispatcher ed, GameData data, Properties settings,
						   Collection selRegions) {
		super(owner, false, ed, data, settings);

		LayoutManager lm = new FlowLayout(FlowLayout.CENTER);

		JPanel help = new JPanel(lm);
		help.add(categorize = new JCheckBox(getString("categorized"),
											settings.getProperty("ArmyStatsDialog.Categorize",
																 "true").equals("true")));
		categorize.addActionListener(this);
		help.add(excludeDialog = new JButton(getString("exclude") + "..."));
		excludeDialog.addActionListener(this);

		JPanel inner = new JPanel(new BorderLayout());
		inner.setBorder(BorderFactory.createEtchedBorder());

		loadExclusionData();

		inner.add(panel = new ArmyStatsPanel(ed, data, settings, categorize.isSelected(), selRegions),
				  BorderLayout.CENTER);
		panel.setExcludeNames(excludeNames);
		panel.setExcludeSkills(excludeSkills);
		panel.setExcludeCombatStates(excludeCombatStates);
		panel.recreate(data);

		inner.add(help, BorderLayout.SOUTH);

		help = new JPanel(lm);

		JButton button = new JButton(getString("ok"));
		button.addActionListener(this);
		help.add(button);

		getContentPane().add(inner, BorderLayout.CENTER);
		getContentPane().add(help, BorderLayout.SOUTH);

		if(settings.containsKey("ArmyStatsDialog.bounds")) {
			StringTokenizer st = new StringTokenizer(settings.getProperty("ArmyStatsDialog.bounds"),
													 ",");

			if(st.countTokens() == 4) {
				try {
					setBounds(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()),
							  Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()));
				} catch(Exception exc) {
					setBounds(100, 100, 600, 600);
				}
			} else {
				setBounds(100, 100, 600, 600);
			}
		} else {
			setBounds(100, 100, 600, 600);
		}
	}

	protected void loadExclusionData() {
		String names = settings.getProperty("ArmyStatsDialog.ExcludedNames");

		if(names != null) {
			StringTokenizer st = new StringTokenizer(names, "~");
			excludeNames = CollectionFactory.createLinkedList();

			while(st.hasMoreTokens()) {
				excludeNames.add(st.nextToken());
			}

			if(excludeNames.size() == 0) {
				excludeNames = null;
			} else {
				Collections.sort(excludeNames);
			}
		}

		String skills = settings.getProperty("ArmyStatsDialog.ExcludedSkills");

		if((data != null) && (skills != null)) {
			StringTokenizer st = new StringTokenizer(skills, "~");
			excludeSkills = CollectionFactory.createArrayList(st.countTokens());

			while(st.hasMoreTokens()) {
				String skillName = st.nextToken();
				SkillType skill = data.rules.getSkillType(StringID.create(skillName), false);

				if(skill != null) {
					excludeSkills.add(skill);
				}
			}

			if(excludeSkills.size() == 0) {
				excludeSkills = null;
			}
		}

		String states = settings.getProperty("ArmyStatsDialog.ExcludedCombatStates");

		if(states != null) {
			StringTokenizer st = new StringTokenizer(states, "~");
			excludeCombatStates = CollectionFactory.createLinkedList();

			while(st.hasMoreTokens()) {
				try {
					excludeCombatStates.add(new Integer(st.nextToken()));
				} catch(Exception exc) {
				}
			}

			if(excludeCombatStates.size() == 0) {
				excludeCombatStates = null;
			}
		}
	}

	protected void saveExclusionData() {
		StringBuffer buf = new StringBuffer();

		if(excludeNames == null) {
			settings.remove("ArmyStatsDialog.ExcludedNames");
		} else {
			Iterator it = excludeNames.iterator();

			while(it.hasNext()) {
				String s = (String) it.next();
				buf.append(s);

				if(it.hasNext()) {
					buf.append('~');
				}
			}

			settings.setProperty("ArmyStatsDialog.ExcludedNames", buf.toString());
		}

		buf.setLength(0);

		if(excludeSkills == null) {
			settings.remove("ArmyStatsDialog.ExcludedSkills");
		} else {
			Iterator it = excludeSkills.iterator();

			while(it.hasNext()) {
				SkillType st = (SkillType) it.next();
				buf.append(st.getID().toString());

				if(it.hasNext()) {
					buf.append('~');
				}
			}

			settings.setProperty("ArmyStatsDialog.ExcludedSkills", buf.toString());
		}

		buf.setLength(0);

		if(excludeCombatStates == null) {
			settings.remove("ArmyStatsDialog.ExcludedCombatStates");
		} else {
			Iterator it = excludeCombatStates.iterator();

			while(it.hasNext()) {
				buf.append(((Integer) it.next()).intValue());

				if(it.hasNext()) {
					buf.append('~');
				}
			}

			settings.setProperty("ArmyStatsDialog.ExcludedCombatStates", buf.toString());
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param b TODO: DOCUMENT ME!
	 */
	public void setVisible(boolean b) {
		super.setVisible(b);

		if(b) {
			panel.doUpdate();
		} else {
			panel.quit();

			StringBuffer buf = new StringBuffer();
			Rectangle rect = getBounds();
			buf.append(rect.x);
			buf.append(',');
			buf.append(rect.y);
			buf.append(',');
			buf.append(rect.width);
			buf.append(',');
			buf.append(rect.height);
			settings.setProperty("ArmyStatsDialog.bounds", buf.toString());
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param e TODO: DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == categorize) {
			boolean state = categorize.isSelected();
			panel.setCategorized(state);
			panel.recreate(data);
			settings.setProperty("ArmyStatsDialog.Categorize", state ? "true" : "false");
		} else if(e.getSource() == excludeDialog) {
			if(data != null) {
				ExcludeDialog ed = new ExcludeDialog(this, getString("exclude.title"), data,
													 excludeNames, excludeSkills,
													 excludeCombatStates);
				ed.show();
				excludeNames = ed.getExcludedNames();
				excludeSkills = ed.getExcludedSkills();
				excludeCombatStates = ed.getExcludeCombatStates();
				panel.setExcludeNames(excludeNames);
				panel.setExcludeSkills(excludeSkills);
				panel.setExcludeCombatStates(excludeCombatStates);
				panel.recreate(data);
				saveExclusionData();
			}
		} else {
			setVisible(false);
		}
	}

	protected class ExcludeDialog extends JDialog implements ActionListener {
		protected JList names;
		protected JButton close;
		protected JButton add;
		protected JCheckBox skills[];
		protected JCheckBox states[];
		protected final String KEY = "SKILLTYPE";

		/**
		 * Creates a new ExcludeDialog object.
		 *
		 * @param owner TODO: DOCUMENT ME!
		 * @param title TODO: DOCUMENT ME!
		 * @param data TODO: DOCUMENT ME!
		 * @param eNames TODO: DOCUMENT ME!
		 * @param eSkills TODO: DOCUMENT ME!
		 * @param eStates TODO: DOCUMENT ME!
		 */
		public ExcludeDialog(Dialog owner, String title, GameData data, List eNames, List eSkills,
							 List eStates) {
			super(owner, title, true);

			javax.swing.border.Border border = BorderFactory.createEtchedBorder();

			// create names list
			DefaultListModel model = new DefaultListModel();

			if(eNames != null) {
				Iterator it = eNames.iterator();

				while(it.hasNext()) {
					model.addElement(it.next());
				}
			}

			names = new JList(model);
			names.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

			JPanel nPanel = new JPanel(new BorderLayout());
			nPanel.add(new JScrollPane(names), BorderLayout.CENTER);

			JPanel inner = new JPanel(new GridBagLayout());
			GridBagConstraints con = new GridBagConstraints();
			con.gridwidth = 1;
			con.gridheight = 1;
			con.fill = GridBagConstraints.HORIZONTAL;
			con.weightx = 1;
			add = new JButton(getString("add") + "...");
			add.addActionListener(this);
			inner.add(add, con);
			con.gridy = 1;

			JButton remove = new JButton(getString("remove"));
			remove.addActionListener(this);
			inner.add(remove, con);
			nPanel.add(inner, BorderLayout.EAST);
			nPanel.setBorder(new javax.swing.border.TitledBorder(border, getString("names")));

			// create skill-type check-boxes
			Iterator it = data.rules.getSkillTypeIterator();
			List l = CollectionFactory.createLinkedList();

			while(it.hasNext()) {
				l.add(it.next());
			}

			JPanel sPanel = new JPanel(new GridLayout(0, 1));

			if(l.size() > 0) {
				Collections.sort(l);
				skills = new JCheckBox[l.size()];
				it = l.iterator();

				int i = 0;

				while(it.hasNext()) {
					SkillType sk = (SkillType) it.next();
					skills[i] = new JCheckBox(sk.getName());

					if((eSkills != null) && eSkills.contains(sk)) {
						skills[i].setSelected(true);
					}

					skills[i].putClientProperty(KEY, sk);
					sPanel.add(skills[i]);
					i++;
				}
			}

			JPanel statePanel = new JPanel(new GridLayout(0, 1));
			statePanel.setBorder(new javax.swing.border.TitledBorder(border, getString("states")));
			states = new JCheckBox[7];

			for(int i = 0; i < 7; i++) {
				states[i] = new JCheckBox(getString("state" + i));

				if(eStates != null) {
					it = eStates.iterator();

					while(it.hasNext()) {
						Integer integer = (Integer) it.next();

						if(integer.intValue() == (i - 1)) {
							states[i].setSelected(true);
						}
					}
				}

				statePanel.add(states[i]);
			}

			Container cont = this.getContentPane();
			cont.setLayout(new BorderLayout());

			JPanel help = new JPanel(new GridLayout(1, 0));
			help.add(nPanel);

			JScrollPane scroll = new JScrollPane(sPanel);
			scroll.setBorder(new javax.swing.border.TitledBorder(border, getString("skills")));
			help.add(scroll);
			help.add(statePanel);
			cont.add(help, BorderLayout.CENTER);

			JPanel button = new JPanel(new FlowLayout(FlowLayout.CENTER));
			close = new JButton(getString("close"));
			close.addActionListener(this);
			button.add(close);
			cont.add(button, BorderLayout.SOUTH);

			this.setBounds(200, 200, 10, 10);
			this.pack();

			if(this.getWidth() < 600) {
				this.setSize(600, this.getHeight());
			}

			if(this.getHeight() > 600) {
				this.setSize(this.getWidth(), 600);
			}

			this.setLocationRelativeTo(owner);
		}

		protected void addName() {
			String ret = JOptionPane.showInputDialog(this, getString("newname"));

			if((ret != null) && !ret.equals("")) {
				((DefaultListModel) names.getModel()).addElement(ret);
			}
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param actionEvent TODO: DOCUMENT ME!
		 */
		public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
			if(actionEvent.getSource() == close) {
				this.setVisible(false);
			} else if(actionEvent.getSource() == add) {
				addName();

				return;
			}

			if(names.getSelectedIndex() >= 0) {
				((DefaultListModel) names.getModel()).remove(names.getSelectedIndex());
			}
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public List getExcludedNames() {
			List al = CollectionFactory.createArrayList(names.getModel().getSize());

			for(int i = 0; i < names.getModel().getSize(); i++) {
				al.add(names.getModel().getElementAt(i));
			}

			if(al.size() == 0) {
				al = null;
			}

			return al;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public List getExcludedSkills() {
			List sl = CollectionFactory.createLinkedList();

			if((skills != null) && (skills.length > 0)) {
				for(int i = 0; i < skills.length; i++) {
					if(skills[i].isSelected()) {
						sl.add(skills[i].getClientProperty(KEY));
					}
				}
			}

			if(sl.size() == 0) {
				sl = null;
			}

			return sl;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public List getExcludeCombatStates() {
			List sl = CollectionFactory.createLinkedList();

			for(int i = 0; i < 7; i++) {
				if(states[i].isSelected()) {
					sl.add(new Integer(i - 1));
				}
			}

			if(sl.size() == 0) {
				sl = null;
			}

			return sl;
		}
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
			defaultTranslations.put("categorized", "Sort in categories");
			defaultTranslations.put("exclude.title", "Exclude units");
			defaultTranslations.put("add", "Add");
			defaultTranslations.put("states", "Per combat state");
			defaultTranslations.put("state5", "Retreat");
			defaultTranslations.put("state4", "Not");
			defaultTranslations.put("state3", "Defensive");
			defaultTranslations.put("state2", "Back");
			defaultTranslations.put("state1", "Front");
			defaultTranslations.put("state0", "Aggressive");
			defaultTranslations.put("state-1", "Unknown");
			defaultTranslations.put("skills", "Per talent");
			defaultTranslations.put("close", "Close");
			defaultTranslations.put("names", "Per name pieces");
			defaultTranslations.put("newname", "New name piece");
			defaultTranslations.put("remove", "Remove");
			defaultTranslations.put("exclude", "Exclude units");
			defaultTranslations.put("ok", "OK");
		}

		return defaultTranslations;
	}
}
