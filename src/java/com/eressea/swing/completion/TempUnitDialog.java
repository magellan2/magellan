// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.swing.completion;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.eressea.swing.InternationalizedDialog;
import com.eressea.util.CollectionFactory;
import com.eressea.util.NameGenerator;


public class TempUnitDialog extends InternationalizedDialog implements ActionListener {

	protected JTextField id, name;
	protected JButton more, ok, cancel;
	protected JPanel bPanel;
	protected JPanel morePanel;
	protected JTextField recruit;
	protected JTextArea descript;
	protected JTextField order;
	protected JCheckBox giveRecruitCost, giveMaintainCost;

	protected GridBagConstraints con;
	protected GridBagLayout layout;
	protected Component posC;

	protected boolean approved = false;
	protected Properties settings;
	
	protected JButton nameGen;
	protected Container nameCon;

	public final static String SETTINGS_KEY = "TempUnitDialog.ExtendedDialog";

	/** Creates new TempUnitDialog */
	public TempUnitDialog(Frame owner, Component parent, Properties settings) {
		super (owner, true);
		this.settings = settings;

		posC = parent;
		Container c = getContentPane();

		c.setLayout(layout = new GridBagLayout());

		con = new GridBagConstraints();
		con.gridwidth = 1;
		con.gridheight = 1;
		con.weightx = 0;
		con.anchor = GridBagConstraints.WEST;
		con.fill = GridBagConstraints.NONE;
		Insets i = new Insets(1, 2, 1, 2);

		con.insets = i;

		c.add(new JLabel(getString("id.label")), con);
		con.gridy = 1;
		c.add(new JLabel(getString("name.label")), con);

		con.gridy = 0;
		con.gridx = 1;
		con.weightx = 1;
		con.fill = GridBagConstraints.HORIZONTAL;
		c.add(id = new JTextField(5), con);
		id.addActionListener(this);
		con.gridy = 1;
		nameCon = new JPanel(new BorderLayout());
		((JPanel)nameCon).setPreferredSize(id.getPreferredSize());
		nameCon.add(name = new JTextField(20), BorderLayout.CENTER);
		c.add(nameCon, con);
		name.addActionListener(this);
		
		nameGen = new JButton("...");
		nameGen.addActionListener(this);
		nameGen.setEnabled(false);
		Insets insets = nameGen.getMargin();
		insets.left = 1;
		insets.right = 1;
		nameGen.setMargin(insets);
		nameGen.setFocusPainted(false);

		con.gridx = 0;
		con.gridy = 2;
		con.gridwidth = 3;
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));

		panel.add(more = new JButton(getString("more.more")));
		panel.setOpaque(false);
		more.addActionListener(this);
		bPanel = panel;
		c.add(panel, con);

		con.gridx = 2;
		con.gridy = 0;
		con.gridwidth = 1;
		con.gridheight = 2;
		panel = new JPanel(new GridLayout(2, 1));
		panel.setOpaque(false);
		panel.add(ok = new JButton(getString("ok")));
		ok.addActionListener(this);
		panel.add(cancel = new JButton(getString("cancel")));
		cancel.addActionListener(this);
		con.fill=GridBagConstraints.NONE;
		c.add(panel, con);
		
		morePanel = new JPanel(new GridBagLayout());
		morePanel.setOpaque(false);
		con.gridx = 0;
		con.gridy = 0;
		con.gridwidth = 1;
		con.gridheight = 1;
		morePanel.add(new JLabel(getString("recruit.label")), con);
		con.gridy = 2;
		morePanel.add(new JLabel(getString("order.label")), con);
		con.gridy = 3;
		con.anchor = GridBagConstraints.NORTHWEST;
		morePanel.add(new JLabel(getString("descript.label")), con);
		con.anchor = GridBagConstraints.CENTER;
		con.gridy = 1;		
		con.gridx = 1;
		con.fill = GridBagConstraints.HORIZONTAL;
		morePanel.add(giveRecruitCost = new JCheckBox(getString("recruitCost.label")), con);
		con.gridx = 2;
		morePanel.add(giveMaintainCost = new JCheckBox(getString("maintainCost.label")), con);
		giveRecruitCost.setSelected(settings.getProperty("TempUnitDialog.AddRecruitCost","false").equals("true"));
		giveMaintainCost.setSelected(settings.getProperty("TempUnitDialog.AddMaintainCost","false").equals("true"));
		
		con.gridx = 1;
		con.gridwidth = 2;
		con.gridy = 0;
		morePanel.add(recruit = new JTextField(5), con);
		con.gridy = 2;
		morePanel.add(order = new JTextField(10), con);
		con.gridy = 3;
		con.fill=GridBagConstraints.BOTH;
		morePanel.add(new JScrollPane(descript = new TablessTextArea(3, 10)), con);
		descript.setLineWrap(true);
		descript.setWrapStyleWord(true);

		// Focus management/Default opening state
		boolean open = false;

		if (settings.containsKey(SETTINGS_KEY)) {
			String s = settings.getProperty(SETTINGS_KEY);

			if (s.equalsIgnoreCase("true"))
				open = true;
		}
		if (open)
			changeDialog();
		else
			setFocusList(open);
		
		loadBounds();

	}
	
	// overrides InternationalizedDialog.quit()
	// do not dispose
	protected void quit() {
		settings.setProperty("TempUnitDialog.LastOrderEmpty", order.getText().length() == 0 ? "true" : "false");
		approved=false;
		saveBounds();		
		setVisible(false);
	}
	
	public void setVisible(boolean b) {
		super.setVisible(b);
		if (order != null) {
			settings.setProperty("TempUnitDialog.LastOrderEmpty", order.getText().length() == 0 ? "true" : "false");
		}
	}
	
	protected void loadBounds() {
		if (settings.containsKey("TempUnitDialog.bounds")) {
			int x=0,y=0,w=0,h=0;
			StringTokenizer st=new StringTokenizer(settings.getProperty("TempUnitDialog.bounds"),",");
			x=Integer.parseInt(st.nextToken());
			y=Integer.parseInt(st.nextToken());
			w=Integer.parseInt(st.nextToken());
			h=Integer.parseInt(st.nextToken());
			setBounds(x,y,w,h);
		}
		else {
			pack();
			setLocationRelativeTo(posC);
		}
	}
	
	protected void saveBounds() {
		Rectangle r=getBounds();
		settings.setProperty("TempUnitDialog.bounds",r.x+","+r.y+","+r.width+","+r.height);
	}

	protected void setFocusList(boolean extended) {
		if (extended) {
			id.setNextFocusableComponent(name);
			name.setNextFocusableComponent(recruit);
			recruit.setNextFocusableComponent(order);
			order.setNextFocusableComponent(descript);
			descript.setNextFocusableComponent(more);
			more.setNextFocusableComponent(ok);
			ok.setNextFocusableComponent(cancel);
			cancel.setNextFocusableComponent(id);
		}
		else {
			id.setNextFocusableComponent(name);
			name.setNextFocusableComponent(more);
			more.setNextFocusableComponent(ok);
			ok.setNextFocusableComponent(cancel);
			cancel.setNextFocusableComponent(id);
		}
	}

	public void actionPerformed(java.awt.event.ActionEvent p1) {
		if (p1.getSource() == more) {
			changeDialog();
			return;
		} else if (p1.getSource() == nameGen) {
			NameGenerator gen = NameGenerator.getInstance();
			name.setText(gen.getName());
			nameGen.setEnabled(gen.isAvailable());
			return;
		}
		approved = (p1.getSource() instanceof JTextField) || (p1.getSource() == ok);
		saveBounds();		
		setVisible(false);
	}

	public void show(String newID) {
		id.setText(newID);
		name.setText(null);
		recruit.setText(null);
		if (settings.getProperty("TempUnitDialog.LastOrderEmpty", "false").equals("true")) {
			order.setText(null);
		} else {
			order.setText(getString("order.default") + " ");
		}
		descript.setText(null);
		name.requestFocus();
		show();
	}

	public void show() {
		approved = false;
		checkNameGen();
		super.show();
		saveCostStates();
	}
	
	protected void saveCostStates() {
		if (giveRecruitCost.isSelected()) {
			settings.setProperty("TempUnitDialog.AddRecruitCost","true");
		} else {
			settings.remove("TempUnitDialog.AddRecruitCost");
		}
		if (giveMaintainCost.isSelected()) {
			settings.setProperty("TempUnitDialog.AddMaintainCost","true");
		} else {
			settings.remove("TempUnitDialog.AddMaintainCost");
		}		
	}
	
	protected void checkNameGen() {
		NameGenerator gen = NameGenerator.getInstance();
		if (gen.isActive()) {
			nameCon.add(nameGen, BorderLayout.EAST);
		} else {
			nameCon.remove(nameGen);
		}
		nameGen.setEnabled(gen.isAvailable());		
	}

	public boolean isApproved() {
		return approved;
	}

	public boolean wasExtendedDialog() {
		return getContentPane().getComponentCount() == 7;
	}

	public String getID() {
		return id.getText();
	}

	public String getName() {
		return name.getText();
	}

	public String getRecruit() {
		return recruit.getText();
	}

	public String getOrder() {
		return order.getText();
	}

	public String getDescript() {
		return descript.getText();
	}
	
	public boolean isGiveRecruitCost() {
		return giveRecruitCost.isSelected();
	}
	
	public boolean isGiveMaintainCost() {
		return giveMaintainCost.isSelected();
	}

	protected void changeDialog() {
		Container c = getContentPane();
		int count = c.getComponentCount();

		if (count == 6) {// add
			con.gridx = 0;
			con.gridy = 4;
			con.gridwidth = 3;
			con.gridheight = 1;
			con.fill = GridBagConstraints.HORIZONTAL;
			layout.setConstraints(bPanel, con);
			con.gridy = 2;
			con.gridheight = 2;
			con.gridwidth = 2;
			con.fill=GridBagConstraints.BOTH;
			c.add(morePanel, con);
			more.setText(getString("more.less"));
			setFocusList(true);
			settings.setProperty(SETTINGS_KEY, "true");
		}
		else {// remove
			c.remove(morePanel);
			con.gridx = 0;
			con.gridy = 2;
			con.gridwidth = 3;
			con.gridheight = 1;
			con.fill = GridBagConstraints.HORIZONTAL;
			layout.setConstraints(bPanel, con);
			more.setText(getString("more.more"));
			setFocusList(false);
			settings.setProperty(SETTINGS_KEY, "false");
		}
		pack();
		setLocationRelativeTo(posC);		
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
			defaultTranslations.put("more.less" , "Less...");
			defaultTranslations.put("more.more" , "More...");
			defaultTranslations.put("descript.label" , "Description");
			defaultTranslations.put("order.label" , "Order");
			defaultTranslations.put("recruit.label" , "Recruit");
			defaultTranslations.put("ok" , "OK");
			defaultTranslations.put("cancel" , "Cancel");
			defaultTranslations.put("name.label" , "Name");
			defaultTranslations.put("id.label" , "ID");
			defaultTranslations.put("order.default" , "LEARN");
			defaultTranslations.put("recruitCost.label" , "Recruit. cost");
			defaultTranslations.put("maintainCost.label" , "Maintain. cost");
		}
		return defaultTranslations;
	}


	protected class TablessTextArea extends JTextArea {
		public TablessTextArea(int rows, int columns) {
			super (rows, columns);
		}
		public boolean isManagingFocus() {
			return false;
		}
	}

}
