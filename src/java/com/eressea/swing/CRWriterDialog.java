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

import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import com.eressea.Building;
import com.eressea.Faction;
import com.eressea.GameData;
import com.eressea.Message;
import com.eressea.Region;
import com.eressea.Ship;
import com.eressea.Spell;
import com.eressea.Unit;
import com.eressea.io.cr.CRWriter;
import com.eressea.io.file.FileType;
import com.eressea.io.file.FileTypeFactory;
import com.eressea.util.CollectionFactory;
import com.eressea.util.PropertiesHelper;
import com.eressea.util.logging.Logger;

/**
 * A GUI for writing a CR to a file or copy it to the clipboard. This class can be used as a
 * stand-alone application or can be integrated as dialog into a different application.
 */
public class CRWriterDialog extends InternationalizedDataDialog {
	private static final Logger log = Logger.getInstance(CRWriterDialog.class);
	private boolean standAlone = false;
	private Collection regions = null;
	private JComboBox comboOutputFile = null;
	private JCheckBox chkServerConformance = null;
	private JCheckBox chkIslands = null;
	private JCheckBox chkRegions = null;
	private JCheckBox chkRegionDetails = null;
	private JCheckBox chkBuildings = null;
	private JCheckBox chkShips = null;
	private JCheckBox chkUnits = null;
	private JCheckBox chkMessages = null;
	private JCheckBox chkSpellsAndPotions = null;
	private JCheckBox chkSelRegionsOnly = null;
	private JCheckBox chkDelStats = null;
	private JCheckBox chkDelTrans = null;

	/**
	 * Create a stand-alone instance of CRWriterDialog.
	 *
	 * @param data TODO: DOCUMENT ME!
	 */
	public CRWriterDialog(GameData data) {
		super(null, false, null, data, new Properties());
		standAlone = true;

		try {
			settings.load(new FileInputStream(new File(System.getProperty("user.home"),
													   "CRWriterDialog.ini")));
		} catch(IOException e) {
			log.error("CRWriterDialog.CRWriterDialog()", e);
		}

		init();
	}

	/**
	 * Create a new CRWriterDialog object as a dialog with a parent window.
	 *
	 * @param owner TODO: DOCUMENT ME!
	 * @param modal TODO: DOCUMENT ME!
	 * @param initData TODO: DOCUMENT ME!
	 * @param p TODO: DOCUMENT ME!
	 */
	public CRWriterDialog(Frame owner, boolean modal, GameData initData, Properties p) {
		super(owner, modal, null, initData, p);
		init();
	}

	/**
	 * Create a new CRWriterDialog object as a dialog with a parent window and a set of selected
	 * regions.
	 *
	 * @param owner TODO: DOCUMENT ME!
	 * @param modal TODO: DOCUMENT ME!
	 * @param initData TODO: DOCUMENT ME!
	 * @param p TODO: DOCUMENT ME!
	 * @param selectedRegions TODO: DOCUMENT ME!
	 */
	public CRWriterDialog(Frame owner, boolean modal, GameData initData, Properties p,
						  Collection selectedRegions) {
		super(owner, modal, null, initData, p);
		this.regions = selectedRegions;
		init();
	}

	private void init() {
		setContentPane(getMainPane());
		setTitle(getString("window.title"));
		setSize(450, 250);

		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int x = Integer.parseInt(settings.getProperty("CRWriterDialog.x",
													  ((screen.width - getWidth()) / 2) + ""));
		int y = Integer.parseInt(settings.getProperty("CRWriterDialog.y",
													  ((screen.height - getHeight()) / 2) + ""));
		setLocation(x, y);
	}

	private Container getMainPane() {
		JPanel mainPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		mainPanel.setBorder(new EmptyBorder(4, 4, 4, 4));

		c.anchor = GridBagConstraints.NORTH;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.1;
		c.weighty = 0.0;
		mainPanel.add(getOptionPanel(), c);

		c.anchor = GridBagConstraints.NORTH;
		c.gridx = 1;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.0;
		c.weighty = 0.0;
		mainPanel.add(getButtonPanel(), c);

		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 2;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.1;
		c.weighty = 0.0;
		mainPanel.add(getFilePanel(), c);

		return mainPanel;
	}

	private Container getButtonPanel() {
		JButton saveButton = new JButton(getString("btn.save.caption"));
		saveButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					File outputFile = new File((String) comboOutputFile.getSelectedItem());

					try {
						write(FileTypeFactory.singleton().createFileType(outputFile, false)
											 .createWriter(data.encoding));
						
						quit();
					} catch(IOException ioe) {
						log.error(ioe);

						String msgArgs[] = { outputFile.getPath() };
						JOptionPane.showMessageDialog((JButton) e.getSource(),
													  (new java.text.MessageFormat(getString("msg.writeerror.text"))).format(msgArgs),
													  getString("msg.exporterror.title"),
													  JOptionPane.WARNING_MESSAGE);
					}
				}
			});

		JButton clipboardButton = new JButton(getString("btn.clipboard.caption"));
		clipboardButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					StringWriter sw = new StringWriter();
					write(sw);
					getToolkit().getSystemClipboard().setContents(new java.awt.datatransfer.StringSelection(sw.toString()),
																  null);
					quit();
				}
			});

		JButton cancelButton = new JButton(getString("btn.close.caption"));
		cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					quit();
				}
			});

		JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 0, 4));
		buttonPanel.add(saveButton);
		buttonPanel.add(clipboardButton);
		buttonPanel.add(cancelButton);

		return buttonPanel;
	}

	private Container getFilePanel() {
		comboOutputFile = new JComboBox(PropertiesHelper.getList(settings,
																 "CRWriterDialog.outputFile")
														.toArray());
		comboOutputFile.setEditable(true);

		JLabel lblOutputFile = new JLabel(getString("lbl.targetfile"));
		lblOutputFile.setLabelFor(comboOutputFile);

		JButton btnOutputFile = new JButton("...");
		btnOutputFile.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String outputFile = getFileName((String) comboOutputFile.getSelectedItem());

					if(outputFile != null) {
						// comboOutputFile.addItem(outputFile);
						// bug Fiete 20061217
						comboOutputFile.insertItemAt(outputFile,0);
						comboOutputFile.setSelectedItem(outputFile);
					}
				}
			});

		JPanel pnlFiles = new JPanel(new GridBagLayout());
		pnlFiles.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(),
											getString("border.files")));

		GridBagConstraints c = new GridBagConstraints();

		// outputFile
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.0;
		c.weighty = 0.0;
		pnlFiles.add(lblOutputFile, c);
		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 1;
		c.gridy = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.1;
		c.weighty = 0.0;
		pnlFiles.add(comboOutputFile, c);
		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 2;
		c.gridy = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.0;
		c.weighty = 0.0;
		pnlFiles.add(btnOutputFile, c);

		return pnlFiles;
	}

	private Container getOptionPanel() {
		// TODO: add tooltips
		chkServerConformance = new JCheckBox(getString("chk.servercompatibility.caption"),
											 (Boolean.valueOf(settings.getProperty("CRWriterDialog.serverConformance",
																			   "true"))).booleanValue());
		chkIslands = new JCheckBox(getString("chk.islands.caption"),
								   (Boolean.valueOf(settings.getProperty("CRWriterDialog.includeIslands",
																	 "true"))).booleanValue());
		chkRegions = new JCheckBox(getString("chk.regions.caption"),
								   (Boolean.valueOf(settings.getProperty("CRWriterDialog.includeRegions",
																	 "true"))).booleanValue());
		chkRegionDetails = new JCheckBox(getString("chk.regiondetails.caption"),
										 (Boolean.valueOf(settings.getProperty("CRWriterDialog.includeRegionDetails",
																		   "true"))).booleanValue());
		chkBuildings = new JCheckBox(getString("chk.buildings.caption"),
									 (Boolean.valueOf(settings.getProperty("CRWriterDialog.includeBuildings",
																	   "true"))).booleanValue());
		chkShips = new JCheckBox(getString("chk.ships.caption"),
								 (Boolean.valueOf(settings.getProperty("CRWriterDialog.includeShips",
																   "true"))).booleanValue());
		chkUnits = new JCheckBox(getString("chk.units.caption"),
								 (Boolean.valueOf(settings.getProperty("CRWriterDialog.includeUnits",
																   "true"))).booleanValue());
		chkMessages = new JCheckBox(getString("chk.messages.caption"),
									(Boolean.valueOf(settings.getProperty("CRWriterDialog.includeMessages",
																	  "true"))).booleanValue());
		chkSpellsAndPotions = new JCheckBox(getString("chk.spellsandpotions.caption"),
											(Boolean.valueOf(settings.getProperty("CRWriterDialog.includeSpellsAndPotions",
																			  "true"))).booleanValue());
		chkSelRegionsOnly = new JCheckBox(getString("chk.selectedregions.caption"),
										  (Boolean.valueOf(settings.getProperty("CRWriterDialog.includeSelRegionsOnly",
																			"false"))).booleanValue());
		chkSelRegionsOnly.setEnabled((regions != null) && (regions.size() > 0));
		chkDelStats = new JCheckBox(getString("chk.delstats.caption"),
									(Boolean.valueOf(settings.getProperty("CRWriterDialog.delStats",
																	  "false"))).booleanValue());
		chkDelTrans = new JCheckBox(getString("chk.deltrans.caption"),
									(Boolean.valueOf(settings.getProperty("CRWriterDialog.delTrans",
																	  "false"))).booleanValue());

		JPanel pnlOptions = new JPanel(new GridLayout(6, 2));
		pnlOptions.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(),
											  getString("border.options")));
		pnlOptions.add(chkServerConformance);
		pnlOptions.add(chkIslands);
		pnlOptions.add(chkRegions);
		pnlOptions.add(chkRegionDetails);
		pnlOptions.add(chkBuildings);
		pnlOptions.add(chkShips);
		pnlOptions.add(chkUnits);
		pnlOptions.add(chkMessages);
		pnlOptions.add(chkSpellsAndPotions);
		pnlOptions.add(chkSelRegionsOnly);
		pnlOptions.add(chkDelStats);
		pnlOptions.add(chkDelTrans);

		return pnlOptions;
	}

	private void storeSettings() {
		settings.setProperty("CRWriterDialog.x", getX() + "");
		settings.setProperty("CRWriterDialog.y", getY() + "");

		PropertiesHelper.setList(settings, "CRWriterDialog.outputFile",
								 getNewOutputFiles(comboOutputFile));
		settings.setProperty("CRWriterDialog.serverConformance",
							 String.valueOf(chkServerConformance.isSelected()));
		settings.setProperty("CRWriterDialog.includeIslands",
							 String.valueOf(chkIslands.isSelected()));
		settings.setProperty("CRWriterDialog.includeRegions",
							 String.valueOf(chkRegions.isSelected()));
		settings.setProperty("CRWriterDialog.includeRegionDetails",
							 String.valueOf(chkRegionDetails.isSelected()));
		settings.setProperty("CRWriterDialog.includeBuildings",
							 String.valueOf(chkBuildings.isSelected()));
		settings.setProperty("CRWriterDialog.includeShips",
							 String.valueOf(chkShips.isSelected()));
		settings.setProperty("CRWriterDialog.includeUnits",
							 String.valueOf(chkUnits.isSelected()));
		settings.setProperty("CRWriterDialog.includeMessages",
							 String.valueOf(chkMessages.isSelected()));
		settings.setProperty("CRWriterDialog.includeSpellsAndPotions",
							 String.valueOf(chkSpellsAndPotions.isSelected()));
		settings.setProperty("CRWriterDialog.delStats",
							 String.valueOf(chkDelStats.isSelected()));
		settings.setProperty("CRWriterDialog.delTrans",
							 String.valueOf(chkDelTrans.isSelected()));

		if(chkSelRegionsOnly.isEnabled()) {
			settings.setProperty("CRWriterDialog.includeSelRegionsOnly",
								 String.valueOf(chkSelRegionsOnly.isSelected()));
		}

		if(standAlone == true) {
			try {
				settings.store(new FileOutputStream(new File(System.getProperty("user.home"),
															 "CRWriterDialog.ini")), "");
			} catch(IOException e) {
				log.error("CRWriterDialog.storeSettings():", e);
			}
		}
	}

	private List getNewOutputFiles(JComboBox combo) {
		List ret = CollectionFactory.createArrayList(combo.getItemCount() + 1);

		if(combo.getSelectedIndex() == -1) {
			ret.add(combo.getEditor().getItem());
		}

		for(int i = 0; i < Math.min(combo.getItemCount(), 6); i++) {
			ret.add(combo.getItemAt(i));
		}

		return ret;
	}

	protected void quit() {
		storeSettings();

		if(standAlone == true) {
			System.exit(0);
		} else {
			super.quit();
		}
	}

	private String getFileName(String defaultFile) {
		String retVal = null;

		JFileChooser fc = new JFileChooser();

		if(defaultFile != null) {
			fc.setSelectedFile(new File(defaultFile));
		}

		if(fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			retVal = fc.getSelectedFile().getPath();
		}

		return retVal;
	}

	private void write(Writer out) {
		setCursor(new Cursor(Cursor.WAIT_CURSOR));

		try {
			CRWriter crw = new CRWriter(out);
			crw.setServerConformance(chkServerConformance.isSelected());
			crw.setIncludeIslands(chkIslands.isSelected());
			crw.setIncludeRegions(chkRegions.isSelected());
			crw.setIncludeRegionDetails(chkRegionDetails.isSelected());
			crw.setIncludeBuildings(chkBuildings.isSelected());
			crw.setIncludeShips(chkShips.isSelected());
			crw.setIncludeUnits(chkUnits.isSelected());
			crw.setIncludeMessages(chkMessages.isSelected());
			crw.setIncludeSpellsAndPotions(chkSpellsAndPotions.isSelected());

			GameData newData = data;

			if(chkDelStats.isSelected()) {
				try {
					newData = (GameData) data.clone();
				} catch(CloneNotSupportedException e) {
					log.error("CRWriterDialog: trying to clone gamedata failed, fallback to merge method.",
							  e);
					newData = GameData.merge(data, data);
				}

				/* delete points, person counts, spell school, alliances, messages
				 * of privileged factions
				 **/
				if(newData.factions() != null) {
					Iterator it = newData.factions().values().iterator();
					boolean excludeBRegions = (crw.getIncludeMessages() &&
											  chkSelRegionsOnly.isSelected() && (regions != null) &&
											  (regions.size() > 0));

					while(it.hasNext()) {
						Faction f = (Faction) it.next();
						boolean found = true;

						if(excludeBRegions) {
							Iterator it2 = regions.iterator();
							found = false;

							while(!found && it2.hasNext()) {
								Region reg = (Region) it2.next();
								Iterator it3 = reg.units().iterator();

								while(!found && it3.hasNext()) {
									Unit unit = (Unit) it3.next();
									found = f.equals(unit.getFaction());
								}
							}

							if(!found) {
								it.remove();
							}
						}

						if(found && f.isPrivileged()) {
							f.averageScore = -1;
							f.score = -1;
							f.persons = -1;
							f.migrants = -1;
							f.maxMigrants = -1;
							f.spellSchool = null;
							f.allies = null;
							// TODO: heroes?

							if(excludeBRegions && (f.messages != null)) {
								Iterator it2 = f.messages.iterator();

								while(it2.hasNext()) {
									Message mes = (Message) it2.next();
									found = false;

									Iterator it3 = regions.iterator();

									while(it3.hasNext() && !found) {
										Region reg = (Region) it3.next();

										if(reg.messages != null) {
											Iterator it4 = reg.messages.iterator();

											while(!found && it4.hasNext()) {
												found = mes.equals(it4.next());
											}
										}
									}

									if(!found) {
										it2.remove();
									}
								}
							}
						}
					}
				}
			}

			if(chkDelTrans.isSelected()) {
				// clean translation table
				List trans = CollectionFactory.createLinkedList(newData.translations().keySet());

				// some static data that is not connected but needed
				trans.remove("Einheit");
				trans.remove("Person");
				trans.remove("verwundet");
				trans.remove("schwer verwundet");
				trans.remove("ersch�pft");

				Collection lookup = data.regions().values();

				if(chkSelRegionsOnly.isSelected() && (regions != null) && (regions.size() > 0)) {
					lookup = regions;
				}

				Iterator it = lookup.iterator();
				Iterator it2 = null;
				Iterator it3 = null;
				boolean checkShips = chkShips.isSelected();
				boolean checkUnits = chkUnits.isSelected();
				boolean checkBuildings = chkBuildings.isSelected();
				boolean checkSpells = chkSpellsAndPotions.isSelected();
				boolean checkRegDetails = chkRegionDetails.isSelected();

				while(it.hasNext()) {
					Region r = (Region) it.next();
					trans.remove(r.getType().getID().toString());

					if(checkRegDetails) {
						it2 = r.resources().iterator();

						while(it2.hasNext()) {
							com.eressea.RegionResource res = (com.eressea.RegionResource) it2.next();
							trans.remove(res.getID().toString());
							trans.remove(res.getType().getID().toString());
						}
					}

					if(checkShips) {
						it2 = r.ships().iterator();

						while(it2.hasNext()) {
							trans.remove(((Ship) it2.next()).getType().getID().toString());
						}
					}

					if(checkBuildings) {
						it2 = r.buildings().iterator();

						while(it2.hasNext()) {
							trans.remove(((Building) it2.next()).getType().getID().toString());
						}
					}

					if(checkUnits) {
						it2 = r.units().iterator();

						while(it2.hasNext()) {
							Unit u = (Unit) it2.next();
							trans.remove(u.race.getID().toString());

							if(u.getRaceNamePrefix() != null) {
								trans.remove(u.getRaceNamePrefix());
							} else {
								if((u.getFaction() != null) &&
									   (u.getFaction().getRaceNamePrefix() != null)) {
									trans.remove(u.getFaction().getRaceNamePrefix());
								}
							}

							it3 = u.getItems().iterator();

							while(it3.hasNext()) {
								trans.remove(((com.eressea.Item) it3.next()).getItemType().getID()
											  .toString());
							}

							it3 = u.getSkills().iterator();

							while(it3.hasNext()) {
								trans.remove(((com.eressea.Skill) it3.next()).getSkillType().getID()
											  .toString());
							}
						}
					}
				}

				if(checkSpells) {
					it = data.spells().values().iterator();

					while(it.hasNext()) {
						Spell sp = (Spell) it.next();
						trans.remove(sp.getID().toString());
                        trans.remove(sp.getName());
                        it2 = sp.getComponents().keySet().iterator();
                        while(it2.hasNext()) {
                        	trans.remove(it2.next());
                        }
					}

					it = data.potions().values().iterator();

					while(it.hasNext()) {
						com.eressea.Potion sp = (com.eressea.Potion) it.next();
						trans.remove(sp.getID().toString());
						it2 = sp.ingredients().iterator();

						while(it2.hasNext()) {
							trans.remove(((com.eressea.Item) it2.next()).getItemType().getID()
									.toString());
						}
					}
				}

				if(trans.size() > 0) {
					log.debug("Following translations will be removed:");
					it = trans.iterator();

					java.util.Map newTrans = newData.translations();

					while(it.hasNext()) {
						Object o = it.next();
						newTrans.remove(o);

						if(log.isDebugEnabled()) {
							log.debug("Removing: " + o);
						}
					}
				}
			}

			if(chkSelRegionsOnly.isSelected() && (regions != null) && (regions.size() > 0)) {
				crw.setRegions(regions);
			}
			
			crw.write(newData);
			crw.close();
		} catch(Exception exc) {
			log.error(exc);
			JOptionPane.showMessageDialog(this, getString("msg.exporterror.text") + exc.toString(),
										  getString("msg.exporterror.title"),
										  JOptionPane.WARNING_MESSAGE);
		}

		setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}
}
