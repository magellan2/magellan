// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.swing;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

import com.eressea.Faction;
import com.eressea.StringID;
import com.eressea.Unit;
import com.eressea.event.SelectionEvent;
import com.eressea.event.SelectionListener;
import com.eressea.rules.EresseaOption;
import com.eressea.rules.Options;
import com.eressea.util.CollectionFactory;
import com.eressea.util.EresseaOrderConstants;
import com.eressea.util.Locales;
import com.eressea.util.Translations;

/**
 * A component providing a GUI for the standard options available to
 * any faction in Eressea.
 */
public class EresseaOptionPanel extends InternationalizedPanel implements SelectionListener {
	private boolean isEnabled = true;
	private Faction currentFaction = null;
	private JTextArea txtInfo = null;
	private JButton btnApply = null;
	private JCheckBox chkComputerReport = null;
	private JCheckBox chkNormalReport = null;
	private JCheckBox chkAddresses = null;
	private JCheckBox chkStatistics = null;
	private JCheckBox chkTemplate = null;
	private JCheckBox chkCompressReport = null;
	private JRadioButton rdbZIPCompression = null;
	private JRadioButton rdbBZIP2Compression = null;
	private JCheckBox chkSilverPool = null;
	private JCheckBox chkItemPool = null;

	public EresseaOptionPanel() {
		init();
	}

	private void init() {
		GridBagConstraints c = new GridBagConstraints();
		this.setLayout(new GridBagLayout());

		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0.1;
		c.weighty = 0.0;
		this.add(getButtonPanel(), c);

		c.gridx = 0;
		c.gridy = 1;
		this.add(getReportPanel(), c);

		c.gridx = 0;
		c.gridy = 2;
		this.add(getGamePanel(), c);

		c.gridx = 0;
		c.gridy = 3;
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0.1;
		c.weighty = 1.0;
		this.add(new JPanel(), c);

		this.setEnabled(false);	// has to be done after the controls are initialized
	}

	private Component getButtonPanel() {
		GridBagConstraints c = new GridBagConstraints();
		JPanel pnlButton = new JPanel(new GridBagLayout());
		pnlButton.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(), getString("border.info")));

		txtInfo = new JTextArea(getString("txt.info.text"));
		txtInfo.setEditable(false);
		txtInfo.setLineWrap(true);
		txtInfo.setWrapStyleWord(true);
		txtInfo.setOpaque(false);

		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.1;
		c.weighty = 0.1;
		c.insets = new Insets(2, 2, 2, 2);
		pnlButton.add(txtInfo, c);

		btnApply = new JButton(getString("btn.apply.caption"));
		btnApply.setMnemonic(getString("btn.apply.mnemonic").charAt(0));
		btnApply.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Unit u = generateOptionOrders(currentFaction, getOptions());
				JOptionPane.showMessageDialog(EresseaOptionPanel.this, getString("msg.optionsapplied.text") + u, getString("msg.optionsapplied.title"), JOptionPane.INFORMATION_MESSAGE);
			}
		});

		c.gridx = 1;
		c.gridy = 0;
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.0;
		c.weighty = 0.0;
		pnlButton.add(btnApply, c);

		return pnlButton;
	}

	private Component getReportPanel() {
		GridBagConstraints c = new GridBagConstraints();
		JPanel pnlReport = new JPanel(new GridBagLayout());
		pnlReport.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(), getString("border.report")));

		chkComputerReport = new JCheckBox(getString("chk.computerreport.caption"));
		chkComputerReport.setMnemonic(getString("chk.computerreport.mnemonic").charAt(0));
		chkComputerReport.setToolTipText(getString("chk.computerreport.tooltip"));
		chkComputerReport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!chkComputerReport.isSelected()) {
					JOptionPane.showMessageDialog(EresseaOptionPanel.this, getString("msg.nocr.text"), getString("msg.nocr.title"), JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.1;
		c.weighty = 0.1;
		c.insets = new Insets(0, 0, 4, 0);
		pnlReport.add(chkComputerReport, c);

		chkNormalReport = new JCheckBox(getString("chk.normalreport.caption"));
		chkNormalReport.setMnemonic(getString("chk.normalreport.mnemonic").charAt(0));
		chkNormalReport.setToolTipText(getString("chk.normalreport.tooltip"));
		chkNormalReport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chkAddresses.setEnabled(chkNormalReport.isSelected());
				chkStatistics.setEnabled(chkNormalReport.isSelected());
				chkTemplate.setEnabled(chkNormalReport.isSelected());
			}
		});
		c.gridx = 0;
		c.gridy = 1;
		c.insets = new Insets(4, 0, 0, 0);
		pnlReport.add(chkNormalReport, c);

		chkAddresses = new JCheckBox(getString("chk.addresses.caption"));
		chkAddresses.setMnemonic(getString("chk.addresses.mnemonic").charAt(0));
		chkAddresses.setToolTipText(getString("chk.addresses.tooltip"));
		c.gridx = 0;
		c.gridy = 2;
		c.insets = new Insets(0, 20, 0, 0);
		pnlReport.add(chkAddresses, c);

		chkStatistics = new JCheckBox(getString("chk.statistics.caption"));
		chkStatistics.setMnemonic(getString("chk.statistics.mnemonic").charAt(0));
		chkStatistics.setToolTipText(getString("chk.statistics.tooltip"));
		c.gridx = 0;
		c.gridy = 3;
		c.insets = new Insets(0, 20, 0, 0);
		pnlReport.add(chkStatistics, c);

		chkTemplate = new JCheckBox(getString("chk.template.caption"));
		chkTemplate.setMnemonic(getString("chk.template.mnemonic").charAt(0));
		chkTemplate.setToolTipText(getString("chk.template.tooltip"));
		c.gridx = 0;
		c.gridy = 4;
		c.insets = new Insets(0, 20, 4, 0);
		pnlReport.add(chkTemplate, c);

		chkCompressReport = new JCheckBox(getString("chk.compressreport.caption"));
		chkCompressReport.setMnemonic(getString("chk.compressreport.mnemonic").charAt(0));
		chkCompressReport.setToolTipText(getString("chk.compressreport.tooltip"));
		chkCompressReport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rdbZIPCompression.setEnabled(chkCompressReport.isSelected());
				rdbBZIP2Compression.setEnabled(chkCompressReport.isSelected());
			}
		});
		c.gridx = 0;
		c.gridy = 5;
		c.insets = new Insets(4, 0, 0, 0);
		pnlReport.add(chkCompressReport, c);

		rdbZIPCompression = new JRadioButton(getString("rdb.zipcompression.caption"));
		rdbZIPCompression.setMnemonic(getString("rdb.zipcompression.mnemonic").charAt(0));
		rdbZIPCompression.setToolTipText(getString("rdb.zipcompression.tooltip"));
		rdbZIPCompression.setSelected(true);
		c.gridx = 0;
		c.gridy = 6;
		c.insets = new Insets(0, 20, 0, 0);
		pnlReport.add(rdbZIPCompression, c);

		rdbBZIP2Compression = new JRadioButton(getString("rdb.bzip2compression.caption"));
		rdbBZIP2Compression.setMnemonic(getString("rdb.bzip2compression.mnemonic").charAt(0));
		rdbBZIP2Compression.setToolTipText(getString("rdb.bzip2compression.tooltip"));
		c.gridx = 0;
		c.gridy = 7;
		c.insets = new Insets(0, 20, 0, 0);
		pnlReport.add(rdbBZIP2Compression, c);

		ButtonGroup btnGroup = new ButtonGroup();
		btnGroup.add(rdbZIPCompression);
		btnGroup.add(rdbBZIP2Compression);

		return pnlReport;
	}

	private Component getGamePanel() {
		GridBagConstraints c = new GridBagConstraints();
		JPanel pnlGame = new JPanel(new GridBagLayout());
		pnlGame.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(), getString("border.game")));

		chkSilverPool = new JCheckBox(getString("chk.silverpool.caption"));
		chkSilverPool.setMnemonic(getString("chk.silverpool.mnemonic").charAt(0));
		chkSilverPool.setToolTipText(getString("chk.silverpool.tooltip"));
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.1;
		c.weighty = 0.1;
		pnlGame.add(chkSilverPool, c);

		chkItemPool = new JCheckBox(getString("chk.itempool.caption"));
		chkItemPool.setMnemonic(getString("chk.itempool.mnemonic").charAt(0));
		chkItemPool.setToolTipText(getString("chk.itempool.tooltip"));
		c.gridx = 1;
		c.gridy = 0;
		pnlGame.add(chkItemPool, c);

		return pnlGame;
	}

	private Unit generateOptionOrders(Faction faction, Options options) {
		Unit unit = null;

		if (!faction.units().isEmpty()) {
			Locale loc = faction.getLocale();
			if (loc == null) {
				loc = Locales.getOrderLocale();
			}

			unit = (Unit)faction.units().iterator().next();
			for (Iterator iter = unit.getOrders().iterator(); iter.hasNext();) {
				String order = ((String)iter.next()).trim().toUpperCase();
				int blankPos = order.indexOf(' ');
				if (blankPos > 0) {
					order = order.substring(0, blankPos);
				}
				if (Translations.getOrderTranslation(EresseaOrderConstants.O_OPTION, loc).startsWith(order)) {
					// FIXME(pavkovic): PROBLEM HIER!
					iter.remove();
				}
			}

			for (Iterator iter = options.options().iterator(); iter.hasNext();) {
				EresseaOption o = (EresseaOption)iter.next();
				if (o.isOrder() && (faction.options == null || o.isActive() != faction.options.isActive(o.getID()))) {
					String newOrder = Translations.getOrderTranslation(EresseaOrderConstants.O_OPTION, loc) + " " + o.getID();
					if (!o.isActive()) {
						newOrder += " " + Translations.getOrderTranslation(EresseaOrderConstants.O_NOT, loc);
					}
					unit.addOrders(newOrder);
				}
			}
		}

		return unit;
	}

	private void setOptions(Options options) {
		chkComputerReport.setSelected(options.isActive(StringID.create(EresseaOrderConstants.O_COMPUTER)));
		chkNormalReport.setSelected(options.isActive(StringID.create(EresseaOrderConstants.O_REPORT)));
		chkAddresses.setSelected(options.isActive(StringID.create(EresseaOrderConstants.O_ADDRESSES)));
		chkStatistics.setSelected(options.isActive(StringID.create(EresseaOrderConstants.O_STATISTICS)));
		chkTemplate.setSelected(options.isActive(StringID.create(EresseaOrderConstants.O_TEMPLATE)));
		chkCompressReport.setSelected(options.isActive(StringID.create(EresseaOrderConstants.O_ZIPPED)) || options.isActive(StringID.create(EresseaOrderConstants.O_BZIP2)));
		if (chkCompressReport.isSelected()) {
			rdbZIPCompression.setSelected(options.isActive(StringID.create(EresseaOrderConstants.O_ZIPPED)));
			rdbBZIP2Compression.setSelected(options.isActive(StringID.create(EresseaOrderConstants.O_BZIP2)));
		}
		chkSilverPool.setSelected(options.isActive(StringID.create(EresseaOrderConstants.O_SILVERPOOL)));
		chkItemPool.setSelected(options.isActive(StringID.create(EresseaOrderConstants.O_ITEMPOOL)));
	}

	private Options getOptions() {
		Options options = new Options();

		options.setActive(StringID.create(EresseaOrderConstants.O_COMPUTER), chkComputerReport.isSelected());
		options.setActive(StringID.create(EresseaOrderConstants.O_REPORT), chkNormalReport.isSelected());
		options.setActive(StringID.create(EresseaOrderConstants.O_ADDRESSES), chkAddresses.isSelected());
		options.setActive(StringID.create(EresseaOrderConstants.O_STATISTICS), chkStatistics.isSelected());
		options.setActive(StringID.create(EresseaOrderConstants.O_TEMPLATE), chkTemplate.isSelected());
		if (chkCompressReport.isSelected()) {
			options.setActive(StringID.create(EresseaOrderConstants.O_ZIPPED), rdbZIPCompression.isSelected());
			options.setActive(StringID.create(EresseaOrderConstants.O_BZIP2), rdbBZIP2Compression.isSelected());
		} else {
			options.setActive(StringID.create(EresseaOrderConstants.O_ZIPPED), false);
			options.setActive(StringID.create(EresseaOrderConstants.O_BZIP2), false);
		}
		options.setActive(StringID.create(EresseaOrderConstants.O_SILVERPOOL), chkSilverPool.isSelected());
		options.setActive(StringID.create(EresseaOrderConstants.O_ITEMPOOL), chkItemPool.isSelected());

		return options;
	}

	public void setEnabled(boolean bool) {
		this.isEnabled = bool;
		txtInfo.setEnabled(bool);
		btnApply.setEnabled(bool);
		chkComputerReport.setEnabled(bool);
		chkNormalReport.setEnabled(bool);
		chkAddresses.setEnabled(bool && chkNormalReport.isSelected());
		chkStatistics.setEnabled(bool && chkNormalReport.isSelected());
		chkTemplate.setEnabled(bool && chkNormalReport.isSelected());
		chkCompressReport.setEnabled(bool);
		rdbZIPCompression.setEnabled(bool && chkCompressReport.isSelected());
		rdbBZIP2Compression.setEnabled(bool && chkCompressReport.isSelected());
		chkSilverPool.setEnabled(bool);
		chkItemPool.setEnabled(bool);
	}

	public boolean isEnabled() {
		return this.isEnabled;
	}

	public void selectionChanged(SelectionEvent e) {
		if (e.getSelectedObjects() != null && e.getSelectedObjects().size() == 1) {
			if (e.getActiveObject() instanceof Faction) {
				currentFaction = (Faction)e.getActiveObject();
				if (currentFaction.options != null && currentFaction.trustLevel >= Faction.TL_PRIVILEGED) {
					this.setOptions(currentFaction.options);
					this.setEnabled(true);
				} else {
					this.setEnabled(false);
				}
			} else {
				this.setEnabled(false);
			}
		} else {
			this.setEnabled(false);
		}
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
			defaultTranslations.put("border.info" , "Apply options");
			defaultTranslations.put("border.report" , "Report options");
			defaultTranslations.put("border.game" , "Game options");

			defaultTranslations.put("txt.info.text" , "Eressea options are sent as unit orders to the Eressea server. Therefore the modified options will be visible here only from next turn on.");
			defaultTranslations.put("btn.apply.caption" , "Apply options");
			defaultTranslations.put("btn.apply.mnemonic" , "a");
			
			defaultTranslations.put("chk.computerreport.caption" , "Receive report in machine readable format (CR)");
			defaultTranslations.put("chk.computerreport.mnemonic" , "c");
			defaultTranslations.put("chk.computerreport.tooltip" , "The CR (computer report) is required by almost any tool for Eressea, Magellan including.");
			defaultTranslations.put("chk.normalreport.caption" , "Receive report in human readable format (NR)");
			defaultTranslations.put("chk.normalreport.mnemonic" , "n");
			defaultTranslations.put("chk.normalreport.tooltip=<html><font face" , "system><small>The NR (normal report) is an easily readable form of the report and<br>especially useful if you want to make your orders 'by hand'.</small></font></html>");
			defaultTranslations.put("chk.addresses.caption" , "Include e-mail addresses of other players in the report");
			defaultTranslations.put("chk.addresses.mnemonic" , "e");
			defaultTranslations.put("chk.addresses.tooltip" , "This option appends the e-mail addresses of all the other faction's players that can be found in a report.");
			defaultTranslations.put("chk.statistics.caption" , "Include regions statistics in the report");
			defaultTranslations.put("chk.statistics.mnemonic" , "r");
			defaultTranslations.put("chk.statistics.tooltip" , "Adds a few statistics to every region in the report.");
			defaultTranslations.put("chk.template.caption" , "Include order template in the report");
			defaultTranslations.put("chk.template.mnemonic" , "t");
			defaultTranslations.put("chk.template.tooltip" , "Appends a template for the unit orders consisting of the default orders to the report.");
			defaultTranslations.put("chk.compressreport.caption" , "Compress reports");
			defaultTranslations.put("chk.compressreport.mnemonic" , "o");
			defaultTranslations.put("chk.compressreport.tooltip=<html><font face" , "system><small>Having the Eressea server compress your reports is very useful as it<br>significantly reduces the size of the report e-mail you receive.</small></font></html>");
			defaultTranslations.put("rdb.zipcompression.caption" , "ZIP compression");
			defaultTranslations.put("rdb.zipcompression.mnemonic" , "z");
			defaultTranslations.put("rdb.zipcompression.tooltip" , "The ZIP format is very commonly used and supported by many compression tools.");
			defaultTranslations.put("rdb.bzip2compression.caption" , "BZIP2 compression");
			defaultTranslations.put("rdb.bzip2compression.mnemonic" , "b");
			defaultTranslations.put("rdb.bzip2compression.tooltip" , "The BZIP2 format is not as common as ZIP but yields even better compression results.");
			defaultTranslations.put("chk.silverpool.caption" , "Silver pool");
			defaultTranslations.put("chk.silverpool.mnemonic" , "s");
			defaultTranslations.put("chk.silverpool.tooltip=<html><font face" , "system><small>This option allows your units to automatically take silver from other<br>units of your faction whenever they need it.</small></font></html>");
			defaultTranslations.put("chk.itempool.caption" , "Item pool");
			defaultTranslations.put("chk.itempool.mnemonic" , "i");
			defaultTranslations.put("chk.itempool.tooltip=<html><font face" , "system><small>Allows your units to access the items carried by other units of your<br>faction when they need to, e.g. for production.</small></font></html>");
			
			defaultTranslations.put("msg.nocr.text" , "Are you sure you do not want to receive the computer report? Magellan\nrequires a report in CR format to process and display the game data.");
			defaultTranslations.put("msg.nocr.title" , "Receive CR?");
			defaultTranslations.put("msg.optionsapplied.text" , "The order necessary to apply the modified options have been added to the following unit:\n");
			defaultTranslations.put("msg.optionsapplied.title" , "Options applied");
			
		}
		return defaultTranslations;
	}

}
