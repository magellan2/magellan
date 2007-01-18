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

package com.eressea.demo;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.metal.MetalLookAndFeel;

import com.eressea.swing.CenterLayout;
import com.eressea.swing.InternationalizedPanel;
import com.eressea.swing.MagellanLookAndFeel;
import com.eressea.swing.layout.GridBagHelper;
import com.eressea.swing.preferences.ExtendedPreferencesAdapter;
import com.eressea.swing.preferences.PreferencesAdapter;
import com.eressea.util.CollectionFactory;
import com.eressea.util.IDBaseConverter;
import com.eressea.util.Locales;
import com.eressea.util.NameGenerator;
import com.eressea.util.FileNameGenerator;
import com.eressea.util.PropertiesHelper;
import com.eressea.util.TextEncodingPreferences;
import com.eressea.util.logging.Logger;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class ClientPreferences extends InternationalizedPanel implements ExtendedPreferencesAdapter {
	private static final Logger log = Logger.getInstance(ClientPreferences.class);
	Properties settings = null;
	Client source = null;
	private javax.swing.JComboBox cmbGUILocale = null;
	private javax.swing.JComboBox cmbOrderLocale = null;

	// The initial value for GameData.curTempID

	/**
	 * @see com.eressea.GameData#curTempID
	 */
	private JTextField tempIDsInitialValue;

	// number will allways be increased by one after a TempUnit has been created
	private JRadioButton countBase36;

	// tries to count decimal:
	// for example: A005 -> A006 -> A007 -> A008 -> A009 -> A010 (and not A00A)
	private JRadioButton countDecimal;
	private JRadioButton ascendingOrder;
	private JRadioButton descendingOrder;
	private JCheckBox showTempUnitDialog;
	private JCheckBox showProgress;
	private JCheckBox createVoidRegions;	
	protected List subAdapters;

	/**
	 * Creates a new ClientPreferences object.
	 *
	 * @param p TODO: DOCUMENT ME!
	 * @param source TODO: DOCUMENT ME!
	 */
	public ClientPreferences(Properties p, Client source) {
		settings = p;
		this.source = source;

		initGUI();
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public List getChildren() {
		return subAdapters;
	}

	private void initGUI() {
		subAdapters = CollectionFactory.createArrayList(3);

		// add font and l&f only as sub adapter
		subAdapters.add(new LAndF());

		// add the history only as sub adapter
		subAdapters.add(new HistoryPanel());

		// add the file name generator only as sub adapter
		FileNameGenerator.init(settings);
		subAdapters.add(FileNameGenerator.getInstance().createPreferencesAdapter());
		
		// add the name generator only as sub adapter
		subAdapters.add(NameGenerator.getInstance().createPreferencesAdapter());
		
		// add the TextEncodingPreferences only as sub adapter
		subAdapters.add(new TextEncodingPreferences(settings));

		// layout this container
		setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();

		c.insets.top = 10;
		c.insets.bottom = 10;
		GridBagHelper.setConstraints(c, 0, 0, GridBagConstraints.REMAINDER, 1, 1.0, 0.0,
									 GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
									 c.insets, 0, 0);

		c.insets.top = 0;

		// locales
		add(getLocalesPanel(), c);

		GridBagHelper.setConstraints(c, 0, 1, GridBagConstraints.REMAINDER, 1, 1.0, 0.0,
									 GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
									 c.insets, 0, 0);

		// temp unit panel
		add(getTempUnitPanel(), c);

		GridBagHelper.setConstraints(c, 0, 2, GridBagConstraints.REMAINDER, 1, 1.0, 0.0,
									 GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
									 c.insets, 0, 0);

		// progress panel
		add(getProgressPanel(), c);
		
		GridBagHelper.setConstraints(c, 0, 3, GridBagConstraints.REMAINDER, 1, 1.0, 1.0, /* different weighty!*/
				 					 GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
				 					 c.insets, 0, 0);
		
		// create void regions panel
		add(getCreateVoidPanel(), c);
		
	}

	private Component getProgressPanel() {
		// progress
		JPanel progressPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		progressPanel.setBorder(new TitledBorder(getString("border.progress")));

		showProgress = new JCheckBox(getString("progress.caption"), source.isShowingStatus());
		progressPanel.add(showProgress);

		return progressPanel;
	}
	
	private Component getCreateVoidPanel() {
		// create Void Regions
		JPanel voidPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		voidPanel.setBorder(new TitledBorder(getString("create.void.regions.border")));

		createVoidRegions = new JCheckBox(getString("create.void.regions.caption"), PropertiesHelper.getboolean(settings, "map.creating.void", true));
		voidPanel.add(createVoidRegions);

		return voidPanel;
	}

	private Component getTempUnitPanel() {
		// tempUnitIDs
		JPanel tempIDs = new JPanel(new GridBagLayout());
		GridBagConstraints c2 = new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
													   GridBagConstraints.WEST,
													   GridBagConstraints.VERTICAL,
													   new Insets(4, 4, 4, 4), 0, 0);
		tempIDs.setBorder(new TitledBorder(getString("border.temps")));

		JLabel label = new JLabel(getString("tempids"));
		tempIDs.add(label, c2);
		c2.gridy = 1;

		String s = settings.getProperty("ClientPreferences.TempIDsInitialValue", "");
		tempIDsInitialValue = new JTextField(s);
		tempIDsInitialValue.setPreferredSize(new Dimension(100, 40));
		tempIDsInitialValue.setBorder(new TitledBorder(getString("tempidsinitialvalue.caption")));
		tempIDsInitialValue.setMargin(new Insets(2, 2, 2, 2));
		tempIDsInitialValue.setHorizontalAlignment(JTextField.CENTER);
		tempIDs.add(tempIDsInitialValue, c2);

		Boolean b = Boolean.valueOf(settings.getProperty("ClientPreferences.countDecimal", "true"));
		countDecimal = new JRadioButton(getString("tempids.countdecimal.caption"), b.booleanValue());
		countBase36 = new JRadioButton(getString("tempids.countbase36.caption"), !b.booleanValue());

		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(countDecimal);
		buttonGroup.add(countBase36);
		c2.gridx = 1;
		tempIDs.add(countDecimal, c2);
		c2.gridx = 2;
		tempIDs.add(countBase36, c2);
		c2.weightx = 1.0;
		c2.gridx = 3;
		tempIDs.add(new JPanel(), c2);

		//pavkovic
		Boolean ascending = Boolean.valueOf(settings.getProperty("ClientPreferences.ascendingOrder",
																 "true"));
		ascendingOrder = new JRadioButton(getString("tempids.ascendingorder.caption"),
										  ascending.booleanValue());
		descendingOrder = new JRadioButton(getString("tempids.descendingorder.caption"),
										   !ascending.booleanValue());

		ButtonGroup buttonGroup2 = new ButtonGroup();
		buttonGroup2.add(ascendingOrder);
		buttonGroup2.add(descendingOrder);
		c2.gridy = 2;
		c2.gridx = 1;
		tempIDs.add(ascendingOrder, c2);
		c2.gridx = 2;
		tempIDs.add(descendingOrder, c2);
		c2.weightx = 1.0;

		c2.gridy = 3;
		c2.gridx = 0;
		c2.gridwidth = 3;
		showTempUnitDialog = new JCheckBox(getString("showtempunitdialog"),
										   settings.getProperty("MultiEditorOrderEditorList.ButtonPanel.ShowTempUnitDialog",
																"true").equalsIgnoreCase("true"));
		tempIDs.add(showTempUnitDialog, c2);

		// tooltips
		tempIDsInitialValue.setToolTipText(getString("tempidsinitialvalue.tooltip"));
		countDecimal.setToolTipText(getString("tempids.countdecimal.tooltip"));
		countBase36.setToolTipText(getString("tempids.countbase36.tooltip"));

		return tempIDs;
	}

	/**
	 * Creates a panel containing the GUI elements for setting the locales.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	private Component getLocalesPanel() {
		Object availLocales[] = { new LocaleWrapper(Locale.GERMAN), new LocaleWrapper(Locale.ENGLISH) };

		JPanel pnlLocales = new JPanel(new GridBagLayout());
		pnlLocales.setBorder(new javax.swing.border.TitledBorder(BorderFactory.createEtchedBorder(),
																 getString("border.locales")));

		cmbGUILocale = new JComboBox(availLocales);
		cmbGUILocale.setSelectedItem(new LocaleWrapper(Locales.getGUILocale()));

		JLabel lblGUILocale = new JLabel(getString("lbl.guilocale.caption"));
		lblGUILocale.setDisplayedMnemonic(getString("lbl.guilocale.mnemonic").charAt(0));
		lblGUILocale.setLabelFor(cmbGUILocale);

		cmbOrderLocale = new JComboBox(availLocales);
		cmbOrderLocale.setSelectedItem(new LocaleWrapper(Locales.getOrderLocale()));

		JLabel lblOrderLocale = new JLabel(getString("lbl.orderlocale.caption"));
		lblOrderLocale.setDisplayedMnemonic(getString("lbl.orderlocale.mnemonic").charAt(0));
		lblOrderLocale.setLabelFor(cmbOrderLocale);

		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(2, 2, 2, 2);
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		c.weightx = 0.0;
		pnlLocales.add(lblGUILocale, c);
		c.gridx = 1;
		c.gridy = 0;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		c.weightx = 0.1;
		pnlLocales.add(cmbGUILocale, c);

		c.gridx = 0;
		c.gridy = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		c.weightx = 0.0;
		pnlLocales.add(lblOrderLocale, c);
		c.gridx = 1;
		c.gridy = 1;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		c.weightx = 0.1;
		pnlLocales.add(cmbOrderLocale, c);

		return pnlLocales;
	}

    /**
     * TODO: implement it
     * @deprecated not implemented!
     * 
     * @see com.eressea.swing.preferences.PreferencesAdapter#initPreferences()
     */
    public void initPreferences() {
        // TODO: implement it
    }

	/**
	 * TODO: DOCUMENT ME!
	 */
	public void applyPreferences() {
		Locales.setGUILocale(((LocaleWrapper) cmbGUILocale.getSelectedItem()).getLocale());
		Locales.setOrderLocale(((LocaleWrapper) cmbOrderLocale.getSelectedItem()).getLocale());

		if(source.getData() != null) {
			source.getData().setCurTempID(-1);
		}

		String s = tempIDsInitialValue.getText();

		if((s == null) || s.equalsIgnoreCase("")) {
			settings.setProperty("ClientPreferences.TempIDsInitialValue", "");
		} else {
			int tempID = 0;

            int base = 10;
            if(source.getData() != null) {
                base = source.getData().base;
            }
            
			try {
				tempID = IDBaseConverter.parse(tempIDsInitialValue.getText(),base);
			} catch(java.lang.NumberFormatException nfe) {
				log.warn("ClientPreferences.applyPreferences: Error when parsing the initial value of the temp ids: " +
						 tempIDsInitialValue.getText());
			}

			if(tempID > IDBaseConverter.getMaxId(base)) {
				log.warn("ClientPreferences.applyPreferences: Found tempID out of valid values: " +
						 tempID);
				tempIDsInitialValue.setText("");
				settings.setProperty("ClientPreferences.TempIDsInitialValue", "");
				tempID = 0;
			}

			if(tempID != 0) {
				settings.setProperty("ClientPreferences.TempIDsInitialValue",
									 IDBaseConverter.toString(tempID,base));
			}
		}

		settings.setProperty("ClientPreferences.countDecimal",
							 String.valueOf(countDecimal.isSelected()));
		settings.setProperty("ClientPreferences.ascendingOrder",
							 String.valueOf(ascendingOrder.isSelected()));
		settings.setProperty("MultiEditorOrderEditorList.ButtonPanel.ShowTempUnitDialog",
							 String.valueOf(showTempUnitDialog.isSelected()));

		settings.setProperty("map.creating.void",
							String.valueOf(createVoidRegions.isSelected()));
		
		source.setShowStatus(showProgress.isSelected());
	}

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
		return getString("title");
	}

	/**
	 * A wrapper for storing Locale objects in a combo box while displaying a full name instead of
	 * only a language code.
	 */
	private static class LocaleWrapper {
		private Locale locale = null;

		/**
		 * Creates a new LocaleWrapper object.
		 *
		 * @param l TODO: DOCUMENT ME!
		 */
		public LocaleWrapper(Locale l) {
			this.locale = l;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public Locale getLocale() {
			return this.locale;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public String toString() {
			return this.locale.getDisplayName();
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param o TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public boolean equals(Object o) {
			try {
				return this.locale.equals(((LocaleWrapper) o).getLocale());
			} catch(ClassCastException e) {
				return false;
			}
		}
	}

	protected void changeMetalBackground() {
		LookAndFeel laf = UIManager.getLookAndFeel();

		if(laf instanceof javax.swing.plaf.metal.MetalLookAndFeel) {
			Color col = JColorChooser.showDialog(this, getString("desktopcolor.title"),
												 MetalLookAndFeel.getWindowBackground());

			if(col != null) {
				MagellanLookAndFeel.setBackground(col, settings);
			}
		}
	}

	protected class LAndF extends JPanel implements PreferencesAdapter, ActionListener {
		protected JTextField editFontSize;
		protected JList jComboBoxLaF;

		/**
		 * Creates a new LAndF object.
		 */
		public LAndF() {
			initGUI();
		}

		private void initGUI() {
			this.setLayout(new GridBagLayout());

			GridBagConstraints c = new GridBagConstraints();
			c.insets.top = 10;
			c.insets.bottom = 10;
			GridBagHelper.setConstraints(c, 0, 0, GridBagConstraints.REMAINDER, 1, 1.0, 0.0,
										 GridBagConstraints.NORTHWEST,
										 GridBagConstraints.HORIZONTAL, c.insets, 0, 0);

			// font panel
			add(createFontPanel(), c);
			c.insets.top = 0;

			GridBagHelper.setConstraints(c, 0, 1, GridBagConstraints.REMAINDER, 1, 1.0, 1.0, /* different weighty!*/
										 GridBagConstraints.NORTHWEST,
										 GridBagConstraints.HORIZONTAL, c.insets, 0, 0);

			// Look And Feel Panel
			add(createLAndFPanel(), c);
		}

		protected Container createFontPanel() {
			JPanel panel = new JPanel(new GridBagLayout());
			panel.setBorder(new javax.swing.border.TitledBorder(BorderFactory.createEtchedBorder(),
																getString("border.fontsize")));

			GridBagConstraints con = new GridBagConstraints(0, 0, 1, 1, 0, 0,
															GridBagConstraints.WEST,
															GridBagConstraints.HORIZONTAL,
															new Insets(3, 3, 3, 3), 0, 0);

			panel.add(new JLabel(getString("lbl.relativefontsize.caption")), con);

			editFontSize = new JTextField(5);
			editFontSize.setPreferredSize(new java.awt.Dimension(50, 20));
			editFontSize.setText("100");

			try {
				float fScale = Float.valueOf(settings.getProperty("Client.FontScale", "1.0"))
									.floatValue();
				fScale *= 100.0f;
				editFontSize.setText(Float.toString(fScale));
			} catch(Exception exc) {
			}

			editFontSize.setMinimumSize(new java.awt.Dimension(50, 20));

			con.insets.left = 0;
			con.gridx = 1;
			panel.add(editFontSize, con);

			con.gridx = 2;
			panel.add(new JLabel("%"), con);

			JTextArea help = new JTextArea(getString("txt.restartforfontsize.caption"));
			help.setEditable(false);
			help.setLineWrap(true);
			help.setWrapStyleWord(true);
			help.setMinimumSize(new java.awt.Dimension(546, 20));
			help.setBackground((java.awt.Color) javax.swing.UIManager.getDefaults().get("Label.background"));
			help.setForeground((java.awt.Color) javax.swing.UIManager.getDefaults().get("Label.foreground"));

			con.gridx = 0;
			con.gridy = 1;
			con.gridwidth = 3;
			con.weightx = 1;
			panel.add(help, con);

			return panel;
		}

		protected Container createLAndFPanel() {
			JPanel panel = new JPanel(new GridBagLayout());

			GridBagConstraints con = new GridBagConstraints(0, 0, 1, 1, 0, 0,
															GridBagConstraints.NORTHWEST,
															GridBagConstraints.HORIZONTAL,
															new Insets(3, 3, 3, 3), 0, 0);

			panel.add(new JLabel(getString("lbl.lafrenderer.caption")), con);

			String renderer[] = source.getLookAndFeels();
			jComboBoxLaF = new JList(renderer);
			jComboBoxLaF.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			jComboBoxLaF.setSelectedValue(settings.getProperty("Client.lookAndFeel", "Metal"), true);
			con.gridx = 1;
			con.weightx = 0;
			panel.add(new JScrollPane(jComboBoxLaF), con);

			JButton button = new JButton(getString("desktopcolor.button"));
			button.addActionListener(this);
			con.gridx = 1;
			con.gridy = 1;
			con.weightx = 0;
			panel.add(button, con);

			JPanel panel2 = new JPanel(new FlowLayout(FlowLayout.LEADING));
			panel2.setBorder(new javax.swing.border.TitledBorder(BorderFactory.createEtchedBorder(),
																 getString("border.lookandfeel")));
			panel2.add(panel);

			return panel2;
		}

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
			return getString("border.lookandfeel");
		}

        /**
         * TODO: implement it
         * @deprecated not implemented!
         * 
         * @see com.eressea.swing.preferences.PreferencesAdapter#initPreferences()
         */
        public void initPreferences() {
            // TODO: implement it
        }

		/**
		 * TODO: DOCUMENT ME!
		 */
		public void applyPreferences() {
			try {
				float fScale = new Float(editFontSize.getText()).floatValue();
				fScale /= 100.0f;

				settings.setProperty("Client.FontScale", Float.toString(fScale));
			} catch(NumberFormatException ex) {
				log.error(ex);
				javax.swing.JOptionPane.showMessageDialog(this,
														  getString("msg.fontsizeerror.text") +
														  ex.toString(),
														  getString("msg.fontsizeerror.title"),
														  javax.swing.JOptionPane.ERROR_MESSAGE);
			}

			//source.setLookAndFeel((String)jComboBoxLaF.getSelectedItem());
			source.setLookAndFeel((String) jComboBoxLaF.getSelectedValue());
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param actionEvent TODO: DOCUMENT ME!
		 */
		public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
			changeMetalBackground();
		}
	}

	protected class HistoryPanel extends JPanel implements PreferencesAdapter {
		protected JTextField txtFileHistorySize;

		/**
		 * Creates a new HistoryPanel object.
		 */
		public HistoryPanel() {
			initGUI();
		}

		private void initGUI() {
			// set up the panel for the maximum file history size
			this.setLayout(CenterLayout.SPAN_X_LAYOUT);

			JPanel help = new JPanel();
			help.setBorder(new TitledBorder(new CompoundBorder(BorderFactory.createEtchedBorder(),
															   new EmptyBorder(0, 3, 3, 3)),
											getString("border.filehistory")));
			help.setLayout(new GridBagLayout());

			GridBagConstraints con = new GridBagConstraints(0, 0, 1, 1, 0, 0,
															GridBagConstraints.NORTHWEST,
															GridBagConstraints.HORIZONTAL,
															new Insets(0, 0, 0, 0), 0, 0);

			help.add(new JLabel(getString("lbl.filehistoryentries.caption")), con);
			con.gridx = 1;
			con.weightx = 1;
			txtFileHistorySize = new JTextField(Integer.toString(source.getMaxFileHistorySize()));
			txtFileHistorySize.setPreferredSize(new java.awt.Dimension(50, 20));

			//editFontSize.setMinimumSize(new java.awt.Dimension(50, 20));
			txtFileHistorySize.setMaximumSize(new java.awt.Dimension(50, 20));

			help.add(txtFileHistorySize, con);

			con.gridx = 0;
			con.gridy = 1;
			con.gridwidth = 2;

			JTextArea txtFoo = new JTextArea(getString("txt.filehistorydescription.text"));
			txtFoo.setLineWrap(true);
			txtFoo.setWrapStyleWord(true);
			txtFoo.setEditable(false);
			txtFoo.setOpaque(false);
			txtFoo.setForeground((java.awt.Color) javax.swing.UIManager.getDefaults().get("Label.foreground"));
			help.add(txtFoo, con);

			// layout this container
			setLayout(new GridBagLayout());

			GridBagConstraints c = new GridBagConstraints();

			c.insets.top = 10;
			c.insets.bottom = 10;
			GridBagHelper.setConstraints(c, 0, 0, GridBagConstraints.REMAINDER, 1, 1.0, 1.0,
										 GridBagConstraints.NORTHWEST,
										 GridBagConstraints.HORIZONTAL, c.insets, 0, 0);

			// help panel
			this.add(help, c);
			c.insets.top = 0;
		}

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
			return getString("border.filehistory");
		}

        /**
         * TODO: implement it
         * @deprecated not implemented!
         * 
         * @see com.eressea.swing.preferences.PreferencesAdapter#initPreferences()
         */
        public void initPreferences() {
            // TODO: implement it
        }

		/**
		 * TODO: DOCUMENT ME!
		 */
		public void applyPreferences() {
			try {
				int i = Integer.parseInt(txtFileHistorySize.getText());
				source.setMaxFileHistorySize(i);
			} catch(NumberFormatException e) {
				log.error("ClientPreferences(): Unable to set maximum file history size", e);
			}
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
			defaultTranslations.put("border.fontsize", "Font size");
			defaultTranslations.put("lbl.relativefontsize.caption", "relative font size");
			defaultTranslations.put("txt.restartforfontsize.caption",
									"Magellan has to be restarted to have the new font size setting take effect.");
			defaultTranslations.put("border.lookandfeel", "Look & Feel");
			defaultTranslations.put("lbl.lafrenderer.caption", "Look&Feel/Skin: ");
			defaultTranslations.put("border.filehistory", "File history");
			defaultTranslations.put("lbl.filehistoryentries.caption",
									"Number of file-history entries: ");

			defaultTranslations.put("txt.filehistorydescription.text",
									"This determines how many recently loaded report files are listed in the File menu.");
			defaultTranslations.put("msg.fontsizeerror.text",
									"The following error occurred while setting the new font-size: ");
			defaultTranslations.put("msg.fontsizeerror.title", "Error");
			defaultTranslations.put("border.locales", "Locale settings");
			defaultTranslations.put("lbl.guilocale.caption", "GUI locale: ");
			defaultTranslations.put("lbl.guilocale.mnemonic", "g");
			defaultTranslations.put("lbl.orderlocale.caption", "Locale of unit orders: ");
			defaultTranslations.put("lbl.orderlocale.mnemonic", "o");
			defaultTranslations.put("title", "System");
			defaultTranslations.put("desktopcolor.button", "Change desktop color...");
			defaultTranslations.put("desktopcolor.title", "Desktop color");
			defaultTranslations.put("progress.caption", "Show progress in title");
			defaultTranslations.put("border.progress", "Progress display");
			defaultTranslations.put("border.temps", "Tempunits");
			defaultTranslations.put("tempids", "IDs of tempunits: ");
			defaultTranslations.put("tempids.countdecimal.caption", "Count decimal");
			defaultTranslations.put("tempids.countbase36.caption", "Count base 36");
			defaultTranslations.put("tempids.ascendingorder.caption", "Increase");
			defaultTranslations.put("tempids.descendingorder.caption", "Decrease");
			defaultTranslations.put("tempidsinitialvalue.caption", "Initial value");
			defaultTranslations.put("tempidsinitialvalue.tooltip",
									"Starting with this value the suggested tempunit ids gets counted. The number can be in- or decreased.");
			defaultTranslations.put("tempids.countdecimal.tooltip", "0, 1, 2, ..., 9, 10, 11, ...");
			defaultTranslations.put("tempids.countbase36.tooltip", "0, 1, 2, ..., 9, a, b, ...");
			defaultTranslations.put("showtempunitdialog", "Show dialog on temp unit creation");
			defaultTranslations.put("create.void.regions.border", "Helpers");
			defaultTranslations.put("create.void.regions.caption", "Create regions \"The Void\" wherever regions are missing");
			
			
			// create.void.regions.border
		}

		return defaultTranslations;
	}
}
