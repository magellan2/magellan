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
import java.awt.Container;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.eressea.Faction;
import com.eressea.GameData;
import com.eressea.Region;
import com.eressea.StringID;
import com.eressea.event.EventDispatcher;
import com.eressea.event.GameDataEvent;
import com.eressea.event.SelectionEvent;
import com.eressea.event.SelectionListener;
import com.eressea.gamebinding.eressea.EresseaConstants;
import com.eressea.io.file.FileType;
import com.eressea.util.CollectionFactory;
import com.eressea.util.JECheck;
import com.eressea.util.JVMUtilities;
import com.eressea.util.OrderWriter;
import com.eressea.util.PropertiesHelper;
import com.eressea.util.logging.Logger;

/**
 * A panel for showing statistics about factions.
 */
public class ECheckPanel extends InternationalizedDataPanel implements SelectionListener {
	private static final Logger log = Logger.getInstance(ECheckPanel.class);
	private JTextField txtECheckEXE = null;
	private JList lstMessages = null;
	private JComboBox cmbFactions = null;
	private JTextArea txtOutput = null;
	private File orderFile = null;
	private JComboBox usedOptions = new JComboBox();
	private JTextField txtOptions = (JTextField) usedOptions.getEditor().getEditorComponent();
	private JCheckBox chkConfirmedOnly = null;
	private JCheckBox chkSelRegionsOnly = null;
	private Collection regions = null;

	/**
	 * Creates a new ECheckPanel object.
	 *
	 * @param d TODO: DOCUMENT ME!
	 * @param initData TODO: DOCUMENT ME!
	 * @param p TODO: DOCUMENT ME!
	 * @param regions TODO: DOCUMENT ME!
	 */
	public ECheckPanel(EventDispatcher d, GameData initData, Properties p, Collection regions) {
		super(d, initData, p);
		this.regions = regions;
		init(d);
	}

	/**
	 * Creates a new ECheckPanel object.
	 *
	 * @param d TODO: DOCUMENT ME!
	 * @param initData TODO: DOCUMENT ME!
	 * @param p TODO: DOCUMENT ME!
	 */
	public ECheckPanel(EventDispatcher d, GameData initData, Properties p) {
		super(d, initData, p);
		init(d);
	}

	private void init(EventDispatcher d) {
		d.addSelectionListener(this);
		this.setLayout(new BorderLayout());
		this.add(getControlsPanel(), BorderLayout.NORTH);

		JPanel pnlOutput = new JPanel(new GridLayout(0, 2));
		pnlOutput.add(getMessagesPanel());
		pnlOutput.add(getOutputPanel());

		this.add(pnlOutput, BorderLayout.CENTER);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param e TODO: DOCUMENT ME!
	 */
	public void selectionChanged(SelectionEvent e) {
		if((SelectionEvent.ST_REGIONS == e.getSelectionType()) && (e.getSelectedObjects() != null)) {
			if(regions == null) {
				regions = CollectionFactory.createHashSet();
			} else {
				regions.clear();
			}

			for(Iterator iter = e.getSelectedObjects().iterator(); iter.hasNext();) {
				Object o = iter.next();

				if(o instanceof Region) {
					regions.add(o);
				}
			}

			/*
			  System.err.println("ECheckPanel.selectionChanged:"+e.getSelectedObjects());
			  System.err.println("ECheckPanel.selectionChanged:"+e.getSelectionType());
			  System.err.println("ECheckPanel.selectionChanged:"+e.getActiveObject());
			  System.err.println("ECheckPanel.selectionChanged:"+regions);
			  System.err.println("ECheckPanel.selectionChanged:");
			*/
			chkSelRegionsOnly.setEnabled((regions != null) && (regions.size() > 0));
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param e TODO: DOCUMENT ME!
	 */
	public void gameDataChanged(GameDataEvent e) {
		data = e.getGameData();
	}

	/**
	 * TODO: DOCUMENT ME!
	 */
	public void runECheck() {
		// check the selected faction
		Faction selectedFaction = (Faction) cmbFactions.getSelectedItem();

		if(selectedFaction == null) {
			JOptionPane.showMessageDialog(this, getString("msg.nofaction.text"),
										  getString("msg.nofaction.title"),
										  JOptionPane.ERROR_MESSAGE);

			return;
		}

		String options = txtOptions.getText();

		/**
		 * If user changed the options save them
		 */
		if(!options.equalsIgnoreCase(getDefaultOptions(selectedFaction))) {
			usedOptions.removeItem(options);
			usedOptions.insertItemAt(options, 0);
			usedOptions.setSelectedIndex(0);

			if(usedOptions.getItemCount() > 5) {
				usedOptions.removeItemAt(5);
			}

			String s = "";

			for(int i = 0; i < usedOptions.getItemCount(); i++) {
				s += (usedOptions.getItemAt(i) + ";");
			}

			settings.setProperty("ECheckPanel.options." + selectedFaction.getID().toString(), s);
		}

		// check the given executable
		File exeFile = new File(txtECheckEXE.getText());

		if(!exeFile.exists()) {
			JOptionPane.showMessageDialog(this, getString("msg.echeckmissing.text"),
										  getString("msg.echeckmissing.title"),
										  JOptionPane.ERROR_MESSAGE);

			return;
		}

		this.setCursor(new Cursor(Cursor.WAIT_CURSOR));

		/* check version */
		try {
			if(!JECheck.checkVersion(exeFile,settings)) {
				this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

				Object msgArgs[] = { JECheck.getRequiredVersion() };
				JOptionPane.showMessageDialog(this,
											  java.text.MessageFormat.format(getString("msg.wrongversion.text"),
																			 msgArgs),
											  getString("msg.wrongversion.title"),
											  JOptionPane.ERROR_MESSAGE);

				return;
			}
		} catch(IOException e) {
			this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			JOptionPane.showMessageDialog(this, getString("msg.versionretrievalerror.text"),
										  getString("msg.versionretrievalerror.title"),
										  JOptionPane.ERROR_MESSAGE);

			return;
		}

		// save orders
		if(orderFile != null) {
			orderFile.delete();
		}

		try {
			orderFile = File.createTempFile("orders", null);

			//	apexo (Fiete) 20061205: if in properties, force ISO encoding
			Writer stream = null;
			if (!PropertiesHelper.getboolean(settings, "TextEncoding.ISOrunEcheck", false)) {
				// old = default = system dependend
				stream = new FileWriter(orderFile);
			} else {
				// new: force our default = ISO
				stream = new OutputStreamWriter(new FileOutputStream(orderFile), FileType.DEFAULT_ENCODING);
			}
			OrderWriter cmdWriter = new OrderWriter(data, selectedFaction, options);

			if(chkSelRegionsOnly.isSelected() && (regions != null) && (regions.size() > 0)) {
				cmdWriter.setRegions(regions);
			}

			cmdWriter.setConfirmedOnly(chkConfirmedOnly.isSelected());
			cmdWriter.write(stream);
			stream.close();
		} catch(IOException e) {
			this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			JOptionPane.showMessageDialog(this, getString("msg.ordersaveerror.text") + e,
										  getString("msg.ordersaveerror.title"),
										  JOptionPane.ERROR_MESSAGE);

			if(orderFile != null) {
				orderFile.delete();
				orderFile = null;
			}

			return;
		}

		// run ECheck and display the ECheck output
		try {
			LineNumberReader r = new LineNumberReader(new JECheck(exeFile, orderFile, options,settings));
			StringBuffer sb = new StringBuffer();

			while(r.ready()) {
				sb.append(r.readLine()).append("\n");
			}

			r.close();
			txtOutput.setText(sb.toString());
		} catch(IOException e) {
			log.error("ECheckPanel.runECheck(): error while reading ECheck output", e);
		}

		// run echeck to get the messages
		try {
			List messages = CollectionFactory.createLinkedList(JECheck.getMessages(exeFile,
																				   orderFile,
																				   options,settings));

			if(messages.size() > 0) {
				JECheck.determineAffectedObjects(data, orderFile, messages);
				lstMessages.setListData(messages.toArray());
			} else {
				JOptionPane.showMessageDialog(this, getString("msg.noecheckmessages.text"),
											  getString("msg.noecheckmessages.title"),
											  JOptionPane.INFORMATION_MESSAGE);
			}
		} catch(IOException e) {
			this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			JOptionPane.showMessageDialog(this, getString("msg.echeckerror.text") + e,
										  getString("msg.echeckerror.title"),
										  JOptionPane.ERROR_MESSAGE);

			if(orderFile != null) {
				orderFile.delete();
				orderFile = null;
			}

			this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

			return;
		} catch(java.text.ParseException e) {
			this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			JOptionPane.showMessageDialog(this, getString("msg.parseerror.text") + e,
										  getString("msg.parseerror.title"),
										  JOptionPane.ERROR_MESSAGE);

			if(orderFile != null) {
				orderFile.delete();
				orderFile = null;
			}
		}

		this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}

	/**
	 * TODO: DOCUMENT ME!
	 */
	public void quit() {
		if(dispatcher != null) {
			dispatcher.removeSelectionListener(this);
		}

		super.quit();
		settings.setProperty("JECheckPanel.echeckEXE", txtECheckEXE.getText());

		if(orderFile != null) {
			orderFile.delete();
		}
	}

	private Container getControlsPanel() {
		usedOptions.setEditable(true);

		JLabel lblECheckEXE = new JLabel(getString("lbl.echeck.caption"));
		lblECheckEXE.setDisplayedMnemonic(getString("lbl.echeck.mnemonic").charAt(0));
		txtECheckEXE = new JTextField(settings.getProperty("JECheckPanel.echeckEXE", ""));
		lblECheckEXE.setLabelFor(txtECheckEXE);

		JButton btnBrowse = new JButton("...");
		btnBrowse.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String exeFile = getFileName(txtECheckEXE.getText());

					if(exeFile != null) {
						txtECheckEXE.setText(exeFile);
					}
				}
			});

		JLabel lblFactions = new JLabel(getString("lbl.faction.caption"));
		lblFactions.setDisplayedMnemonic(getString("lbl.faction.mnemonic").charAt(0));
		cmbFactions = new JComboBox();
		lblFactions.setLabelFor(cmbFactions);

		for(Iterator iter = data.factions().values().iterator(); iter.hasNext();) {
			Faction f = (Faction) iter.next();

			if(f.isPrivileged()) {
				cmbFactions.addItem(f);
			}
		}

		cmbFactions.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Faction f = (Faction) cmbFactions.getSelectedItem();

					if(f != null) {
						// set the saved options sets of the new faction
						usedOptions.removeAllItems();

						String s = settings.getProperty("ECheckPanel.options." +
														f.getID().toString());

						if((s != null) && (s.length() > 0)) {
							String temp = "";

							for(int i = 0; i < s.length(); i++) {
								if(s.charAt(i) != ';') {
									temp += s.charAt(i);
								} else {
									usedOptions.addItem(temp);
									temp = "";
								}
							}

							if(usedOptions.getItemCount() > 0) {
								usedOptions.setSelectedIndex(0);
							} else {
								txtOptions.setText(getDefaultOptions(f));
							}
						} else {
							txtOptions.setText(getDefaultOptions(f));
						}
					}
				}
			});

		chkConfirmedOnly = new JCheckBox(getString("chk.skipunconfirmedorders.caption"));

		chkSelRegionsOnly = new JCheckBox(getString("chk.selectedregions.caption"));
		chkSelRegionsOnly.setEnabled((regions != null) && (regions.size() > 0));

		JLabel lblOptions = new JLabel(getString("lbl.options.caption"));
		lblOptions.setDisplayedMnemonic(getString("lbl.options.mnemonic").charAt(0));
		lblOptions.setLabelFor(usedOptions);

		/* trigger option creation */
		if(cmbFactions.getItemCount() > 0) {
			cmbFactions.setSelectedIndex(0);
		}

		JPanel controls = new JPanel(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(2, 1, 2, 1);

		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.0;
		c.weighty = 0.0;
		controls.add(lblECheckEXE, c);

		c.gridx = 1;
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;
		c.weighty = 0.0;
		controls.add(txtECheckEXE, c);

		c.gridx = 2;
		c.gridy = 0;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.0;
		c.weighty = 0.0;
		controls.add(btnBrowse, c);

		c.gridx = 0;
		c.gridy = 1;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.0;
		c.weighty = 0.0;
		controls.add(lblFactions, c);

		c.gridx = 1;
		c.gridy = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridwidth = 2;
		controls.add(cmbFactions, c);

		c.gridx = 0;
		c.gridy = 2;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		controls.add(lblOptions, c);

		c.gridx = 1;
		c.gridy = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridwidth = 2;
		controls.add(usedOptions, c);

		c.gridx = 1;
		c.gridy = 3;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridwidth = 2;
		controls.add(chkConfirmedOnly, c);

		c.gridx = 1;
		c.gridy = 4;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridwidth = 2;
		controls.add(chkSelRegionsOnly, c);

		return controls;
	}

	protected static String getOwnString(String key) {
		return com.eressea.util.Translations.getTranslation(ECheckPanel.class, key);
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
			defaultTranslations.put("msg.echeckmissing.text",
									"The specified ECheck executable does not exist!");
			defaultTranslations.put("msg.echeckmissing.title", "Error");
			defaultTranslations.put("msg.nofaction.text",
									"Please select a faction whose orders should be checked.");
			defaultTranslations.put("msg.nofaction.title", "Select faction");
			defaultTranslations.put("msg.wrongversion.title", "ECheck too old");
			defaultTranslations.put("msg.wrongversion.text",
									"The specified version of ECheck is too old. Please use version {0} or higher.");
			defaultTranslations.put("msg.versionretrievalerror.title", "Version undetermined");
			defaultTranslations.put("msg.versionretrievalerror.text",
									"While trying to gather version information about the specified ECheck the following error occurred:\n");
			defaultTranslations.put("msg.ordersaveerror.text",
									"The following error occurred while saving the faction's orders: ");
			defaultTranslations.put("msg.ordersaveerror.title", "Error on save");
			defaultTranslations.put("msg.noecheckmessages.text",
									"ECheck did not find any errors in your orders.");
			defaultTranslations.put("msg.noecheckmessages.title", "Orders checked");
			defaultTranslations.put("msg.echeckerror.text",
									"The following error occurred while executing ECheck:  ");
			defaultTranslations.put("msg.echeckerror.title", "ECheck error");
			defaultTranslations.put("msg.parseerror.text",
									"Unable to parse the messages generated by ECheck: ");
			defaultTranslations.put("msg.parseerror.title", "ECheck error");
			defaultTranslations.put("msg.messagetargetnotfound.text",
									"Unable to determine the unit or region the selected message refers to.");
			defaultTranslations.put("msg.messagetargetnotfound.title", "Error");

			defaultTranslations.put("lbl.echeck.caption", "ECheck:");
			defaultTranslations.put("lbl.echeck.mnemonic", "e");
			defaultTranslations.put("lbl.faction.caption", "Faction:");
			defaultTranslations.put("lbl.faction.mnemonic", "f");
			defaultTranslations.put("lbl.options.caption", "Options:");
			defaultTranslations.put("lbl.options.mnemonic", "o");

			defaultTranslations.put("chk.selectedregions.caption", "Selected regions");
			defaultTranslations.put("chk.skipunconfirmedorders.caption",
									"Skip units with unconfirmed orders");

			defaultTranslations.put("border.echeckmessages", "Retrieved messages");
			defaultTranslations.put("border.orders", "ECheck output");

			defaultTranslations.put("filter.echeckexe.desc", "ECheck executable");

			defaultTranslations.put("celllbl.warning", "Warning in line ");
			defaultTranslations.put("celllbl.error", "Error in line ");
		}

		return defaultTranslations;
	}

	private Container getMessagesPanel() {
		lstMessages = new JList();
		lstMessages.setCellRenderer(new ECheckMessageRenderer());
		lstMessages.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					if(!e.getValueIsAdjusting()) {
						JECheck.ECheckMessage msg = (JECheck.ECheckMessage) lstMessages.getSelectedValue();

						if(msg == null) {
							return;
						}

						if(msg.getAffectedObject() != null) {
							dispatcher.fire(new SelectionEvent(ECheckPanel.this, null,
															   msg.getAffectedObject()));
						} else {
							JOptionPane.showMessageDialog(ECheckPanel.this,
														  getString("msg.messagetargetnotfound.text"),
														  getString("msg.messagetargetnotfound.title"),
														  JOptionPane.INFORMATION_MESSAGE);
						}
					}
				}
			});

		JPanel messages = new JPanel(new BorderLayout());
		messages.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(),
											getString("border.echeckmessages")));
		messages.add(new JScrollPane(lstMessages), BorderLayout.CENTER);

		return messages;
	}

	private Container getOutputPanel() {
		txtOutput = new JTextArea();
		txtOutput.setEditable(false);

		JPanel orders = new JPanel(new BorderLayout());
		orders.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(),
										  getString("border.orders")));
		orders.add(new JScrollPane(txtOutput), BorderLayout.CENTER);

		return orders;
	}

	/**
	 * Returns a file name by showing the user a file selection dialog. If the user's selection is
	 * empty, null is returned.
	 *
	 * @param defaultFile TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	private String getFileName(String defaultFile) {
		String retVal = null;

		javax.swing.filechooser.FileFilter ff = new javax.swing.filechooser.FileFilter() {
			public boolean accept(File f) {
				String fileName = f.getName().toLowerCase();

				return f.isDirectory() || (fileName.endsWith(".exe") || fileName.equals("echeck"));
			}

			public String getDescription() {
				return getString("filter.echeckexe.desc");
			}
		};

		JFileChooser fc = new JFileChooser();
		fc.addChoosableFileFilter(ff);
		fc.setFileFilter(ff);
		fc.setMultiSelectionEnabled(false);

		if(defaultFile != null) {
			fc.setSelectedFile(new File(defaultFile));
		}

		if(fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			retVal = fc.getSelectedFile().getAbsolutePath();
		}

		return retVal;
	}

	/**
	 * Default echeck options for given faction.
	 *
	 * @param f TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	private String getDefaultOptions(Faction f) {
		String options = "";

		if((f.getType() != null) && (f.getRace().getRecruitmentCosts() > 0)) {
			options += (" -r" + f.getRace().getRecruitmentCosts());
		}

		if((f.options != null) &&
			   f.options.isActive(StringID.create(EresseaConstants.O_SILVERPOOL))) {
			options += " -l";
		}

		if(f.getLocale() != null) {
			options += (" -L" + f.getLocale().getLanguage());
		}

		return options;
	}

	private static final class ECheckMessageRenderer extends JPanel implements ListCellRenderer {
		private static javax.swing.border.Border focusedBorder = null;
		private static javax.swing.border.Border selectedBorder = null;
		private static javax.swing.border.Border plainBorder = null;
		private static Color textForeground = null;
		private static Color textBackground = null;
		private static Color selectedForeground = null;
		private static Color selectedBackground = null;
		private JLabel lblCaption = null;
		private JTextArea txtMessage = null;

		/**
		 * Creates a new ECheckMessageRenderer object.
		 */
		public ECheckMessageRenderer() {
			GridBagConstraints c = new GridBagConstraints();

			lblCaption = new JLabel();
			lblCaption.setOpaque(false);

			txtMessage = new JTextArea();
			txtMessage.setOpaque(false);

			this.setLayout(new GridBagLayout());

			c.anchor = GridBagConstraints.CENTER;
			c.gridx = 0;
			c.gridy = 0;
			c.gridwidth = 1;
			c.gridheight = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 0.5;
			c.weighty = 0.0;
			c.insets = new Insets(0, 0, 0, 0);
			this.add(lblCaption, c);

			c.anchor = GridBagConstraints.CENTER;
			c.gridx = 0;
			c.gridy = 1;
			c.gridwidth = 1;
			c.gridheight = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 0.0;
			c.weighty = 0.0;
			c.insets = new Insets(0, 8, 0, 0);
			this.add(txtMessage, c);

			applyUIDefaults();
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param list TODO: DOCUMENT ME!
		 * @param value TODO: DOCUMENT ME!
		 * @param index TODO: DOCUMENT ME!
		 * @param isSelected TODO: DOCUMENT ME!
		 * @param cellHasFocus TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public Component getListCellRendererComponent(JList list, Object value, int index,
													  boolean isSelected, boolean cellHasFocus) {
			try {
				JECheck.ECheckMessage m = (JECheck.ECheckMessage) value;

				if(isSelected) {
					this.setBackground(selectedBackground);
					lblCaption.setForeground(selectedForeground);
					txtMessage.setForeground(selectedForeground);
				} else {
					this.setBackground(textBackground);
					lblCaption.setForeground(textForeground);
					txtMessage.setForeground(textForeground);
				}

				if(cellHasFocus) {
					this.setBorder(focusedBorder);
				} else {
					if(isSelected) {
						this.setBorder(selectedBorder);
					} else {
						this.setBorder(plainBorder);
					}
				}

				if(m.getType() == JECheck.ECheckMessage.WARNING) {
					lblCaption.setText(getOwnString("celllbl.warning") + m.getLineNr());
				} else {
					lblCaption.setText(getOwnString("celllbl.error") + m.getLineNr());
				}

				txtMessage.setText(m.getMessage());
			} catch(ClassCastException e) {
			}

			return this;
		}

		private void applyUIDefaults() {
			textForeground = (Color) UIManager.getDefaults().get("Tree.textForeground");
			textBackground = (Color) UIManager.getDefaults().get("Tree.textBackground");
			selectedForeground = (Color) UIManager.getDefaults().get("Tree.selectionForeground");
			selectedBackground = (Color) UIManager.getDefaults().get("Tree.selectionBackground");

			// pavkovic 2003.10.17: prevent jvm 1.4.2_01 bug
			focusedBorder = new MatteBorder(1, 1, 1, 1, JVMUtilities.getTreeSelectionBorderColor());
			selectedBorder = new MatteBorder(1, 1, 1, 1, selectedBackground);
			plainBorder = new EmptyBorder(1, 1, 1, 1);

			this.setOpaque(true);
			this.setBackground(textBackground);
			lblCaption.setForeground(textBackground);
			txtMessage.setForeground(textBackground);
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean getSelRegionsOnly() {
		return chkSelRegionsOnly.isSelected();
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param b TODO: DOCUMENT ME!
	 */
	public void setSelRegionsOnly(boolean b) {
		chkSelRegionsOnly.setSelected(b);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean getConfirmedOnly() {
		return chkConfirmedOnly.isSelected();
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param b TODO: DOCUMENT ME!
	 */
	public void setConfirmedOnly(boolean b) {
		chkConfirmedOnly.setSelected(b);
	}
}
