// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.swing;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.eressea.EntityID;
import com.eressea.Faction;
import com.eressea.GameData;
import com.eressea.ID;
import com.eressea.IntegerID;
import com.eressea.event.EventDispatcher;
import com.eressea.event.GameDataEvent;
import com.eressea.event.SelectionEvent;
import com.eressea.event.SelectionListener;
import com.eressea.resource.ResourcePathClassLoader;
import com.eressea.util.CollectionFactory;
import com.eressea.util.TrustLevels;
import com.eressea.util.comparator.FactionTrustComparator;
import com.eressea.util.comparator.IDComparator;
import com.eressea.util.comparator.NameComparator;
import com.eressea.util.logging.Logger;

/**
 * A dialog wrapper for the faction statistics display.
 */
public class FactionStatsDialog extends InternationalizedDataDialog {
	private final static Logger log = Logger.getInstance(FactionStatsDialog.class);

	private List factions = null;
	private FactionStatsPanel pnlStats = null;
	private JList lstFaction = null;
	private JSplitPane splFaction = null;
	private JTabbedPane tabPane = null;
	//private EresseaOptionPanel optionPanel = null;

	private static FactionTrustComparator factionTrustComparator = FactionTrustComparator.DEFAULT_COMPARATOR;
	private static NameComparator nameComparator = new NameComparator(new IDComparator());

	/**
	 * Create a new FactionStatsDialog object as a dialog with a parent
	 * window.
	 */
	public FactionStatsDialog(Frame owner, boolean modal, EventDispatcher ed, GameData initData, Properties p) {
		super(owner, modal, ed, initData, p);
		pnlStats = new FactionStatsPanel(dispatcher, data, p);
		init();
	}

	/**
	 * Create a new FactionStatsDialog object as a dialog with a parent
	 * window and with the given faction selected.
	 */
	public FactionStatsDialog(Frame owner, boolean modal, EventDispatcher ed, GameData initData, Properties p, Faction f) {
		this(owner, modal, ed, initData, p);
		lstFaction.setSelectedValue(f, true);
	}


	private void init() {
		setContentPane(getMainPane());
		setTitle(getString("window.title"));
		int width = Integer.parseInt(settings.getProperty("FactionStatsDialog.width", "500"));
		int height = Integer.parseInt(settings.getProperty("FactionStatsDialog.height", "300"));
		setSize(width, height);
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int x = Integer.parseInt(settings.getProperty("FactionStatsDialog.x", ((screen.width - getWidth()) / 2) + ""));
		int y = Integer.parseInt(settings.getProperty("FactionStatsDialog.y", ((screen.height - getHeight()) / 2) + ""));
		setLocation(x, y);
		splFaction.setDividerLocation(Integer.parseInt(settings.getProperty("FactionStatsDialog.split", (width/2) + "")));
		ID selFacID = EntityID.createEntityID(settings.getProperty("FactionStatsDialog.selFacID", "-1"), 10);
		Faction selFac = data.getFaction(selFacID);
		if (selFac != null) {
			lstFaction.setSelectedValue(selFac, true);
		} else {
			lstFaction.setSelectedIndex(0);
		}
	}

	private Container getMainPane() {
		JPanel mainPanel = new JPanel(new BorderLayout(0, 5));
		mainPanel.setBorder(new EmptyBorder(4, 4, 4, 4));

		//optionPanel = new EresseaOptionPanel();

		tabPane = new JTabbedPane();
		tabPane.addTab(getString("tab.stats.caption"), null, pnlStats, null);

		JPanel skillChartPanel = getSkillChartPanel();
		if (skillChartPanel != null) {
			tabPane.addTab(getString("tab.skillchart.caption"), skillChartPanel);
		}

		// pavkovic 2003.11.19: deactivated, because EresseaOptionPanel is currently broken
		// tabPane.addTab(getString("tab.options.caption"), null, optionPanel, null);

		splFaction = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, getFactionPanel(), tabPane);
		mainPanel.add(splFaction, BorderLayout.CENTER);
		mainPanel.add(getButtonPanel(), BorderLayout.SOUTH);
		return mainPanel;
	}

	private Container getButtonPanel() {

		JButton btnClose = new JButton(getString("btn.close.caption"));
		btnClose.setMnemonic(getString("btn.close.menmonic").charAt(0));
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				quit();
			}
		});

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(btnClose);

		return buttonPanel;
	}

	private Container getFactionPanel() {
		factions = CollectionFactory.createLinkedList(data.factions().values());

		boolean sortByTrustLevel = settings.getProperty("FactionStatsDialog.SortByTrustLevel", "true").equalsIgnoreCase("true");

		// sort factions
		if (sortByTrustLevel) {
			Collections.sort(factions, factionTrustComparator);
		} else {
			Collections.sort(factions, nameComparator);
		}

		final FactionStatsDialog d = this;
		lstFaction = new JList(factions.toArray());
		// to jump to first faction which name starts with the typed key
		lstFaction.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				Faction dummy = new Faction(IntegerID.create(-2), null);
				char c = e.getKeyChar();
				if (!Character.isLetter(c)) {
					return;
				}
				dummy.setName(String.valueOf(c));
				int index = Collections.binarySearch(factions, dummy, nameComparator);
				if (index < 0) {
					index = -index - 1;
				}
				if (index == lstFaction.getModel().getSize()) {
					index--;
				}
				Object o = lstFaction.getModel().getElementAt(index);
				lstFaction.setSelectedValue(o, true);
			}
		});
		lstFaction.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		lstFaction.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting()) {
					return;
				}

				SelectionEvent se = null;
				JList list = (JList)e.getSource();
				if (list.getModel().getSize() > 0 && !list.isSelectionEmpty()) {
					se = new SelectionEvent(d, Arrays.asList(list.getSelectedValues()), list.getSelectedValue());
				} else {
					se = new SelectionEvent(d, CollectionFactory.createLinkedList(), null);
				}

				// notify all components in the tabbed pane
				/**
				 * Ulrich Küster:
				 * (!) Special care has to be taken for the FactionStatsDialog.
				 * It can not be differed, if SelectionEvents come from the
				 * faction list in FactionStatsDialog or from other components of Magellan.
				 * To keep the faction list in this object consistent to the
				 * displayed data in the FactionStatsPanel object, FactionStatsPanel.setFaction()
				 * should be _never_ called by FactionStatsPanel.selectionChanged(), but
				 * always directly by this method.
				 */
				for (int i = 0; i < tabPane.getTabCount(); i++) {
					Component c = tabPane.getComponentAt(i);
					if (c instanceof FactionStatsPanel) {
						((FactionStatsPanel)c).setFactions(se.getSelectedObjects());
					} else if (c instanceof SelectionListener) {
						((SelectionListener)c).selectionChanged(se);
					}
				}
			}
		});

		String s;
		if (sortByTrustLevel) {
			s = getString("btn.sort.name.caption");
		} else {
			s = getString("btn.sort.trustlevel.caption");
		}
		final JButton sort = new JButton(s);
		sort.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean sortByTrust = !settings.getProperty("FactionStatsDialog.SortByTrustLevel", "true").equalsIgnoreCase("true");
				settings.setProperty("FactionStatsDialog.SortByTrustLevel", String.valueOf(sortByTrust));
				if (sortByTrust) {
					sort.setText(getString("btn.sort.name.caption"));
					Collections.sort(factions, factionTrustComparator);
				} else {
					sort.setText(getString("btn.sort.trustlevel.caption"));
					Collections.sort(factions, nameComparator);
				}
				Object o = lstFaction.getSelectedValue();
				lstFaction.setListData(factions.toArray());
				lstFaction.setSelectedValue(o, true);
				lstFaction.repaint();
			}
		});

		JButton btnDeleteFaction = new JButton(getString("btn.deletefaction.caption"));
		btnDeleteFaction.setMnemonic(getString("btn.deletefaction.mnemonic").charAt(0));
		btnDeleteFaction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				List victims = CollectionFactory.createLinkedList(Arrays.asList(lstFaction.getSelectedValues()));

				for (ListIterator iter = (ListIterator)victims.iterator(); iter.hasNext();) {
					Faction f = (Faction)iter.next();
					boolean veto = false;

					if (f.units().size() > 0) {
						Object[] msgArgs = {
							f
						};
						JOptionPane.showMessageDialog(d, (new java.text.MessageFormat(getString("msg.factioncontainsunits.text"))).format(msgArgs));
						veto = true;
					}

					if (!veto) {
						for (Iterator allFactions = data.factions().values().iterator(); allFactions.hasNext();) {
							Faction dummy = (Faction)allFactions.next();
							if (dummy.units().size() > 0 && dummy.allies != null && dummy.allies.containsKey(f.getID())) {
								Object[] msgArgs = {
									f,
									dummy.allies.get(f.getID())
								};
								JOptionPane.showMessageDialog(d, (new java.text.MessageFormat(getString("msg.factionisallied.text"))).format(msgArgs));
								veto = true;
								break;
							}
						}
					}

					if (!veto) {
						Object[] msgArgs = {
							f,
						};
						if (JOptionPane.showConfirmDialog(d, (new java.text.MessageFormat(getString("msg.confirmdeletefaction.text"))).format(msgArgs), getString("msg.confirmdeletefaction.title"), JOptionPane.YES_NO_OPTION) != JOptionPane.OK_OPTION) {
							veto = true;
						}
					}

					if (veto) {
						iter.remove();
					}
				}

				if (victims.size() > 0) {
					for (Iterator iter = victims.iterator(); iter.hasNext();) {
						Faction f = (Faction)iter.next();
						data.factions().remove(f.getID());
					}
					// should notify game data listeners here
					factions.removeAll(victims);
					lstFaction.setListData(factions.toArray());
					lstFaction.repaint();
					pnlStats.setFactions(CollectionFactory.createLinkedList());
				}
			}
		});

		JButton btnPassword = new JButton(getString("btn.setpwd.caption"));
		btnPassword.setMnemonic(getString("btn.setpwd.menmonic").charAt(0));
		btnPassword.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (lstFaction.getModel().getSize() <= 0 || lstFaction.isSelectionEmpty()) {
					return;
				}

				Faction f = (Faction)lstFaction.getSelectedValue();

				String pwd = "";
				if (f.password != null) {
					pwd = f.password;
				}
				// ask user for password
				Object[] msgArgs = {
					f
				};
				pwd = (String)JOptionPane.showInputDialog(getRootPane(), (new java.text.MessageFormat(getString("msg.passwdinput.text"))).format(msgArgs), getString("msg.passwdinput.title"), JOptionPane.QUESTION_MESSAGE, null, null, pwd);

				if (pwd != null) {	// if user did not hit the cancel button
					if (!pwd.equals("")) {	// if this password is valid
						f.password = pwd;
					} else {
						f.password = null;
					}

					// store the password to the settings even if it is invalid
					settings.setProperty("Faction.password." + ((EntityID)f.getID()).intValue(), f.password != null ? f.password : "");

					// if the pw is valid increase this faction's trust level
					if (f.password != null) {
						f.trustLevel = Faction.TL_PRIVILEGED;
					} else {
						// default is okay here, combat ally trust levels are restored
						// in the next loop anyway
						f.trustLevel = Faction.TL_DEFAULT;
					}
					TrustLevels.recalculateTrustLevels(data);
				}
				// notify game data listeners
				dispatcher.fire(new GameDataEvent(this, data));
			}
		});

		JButton btnTrustlevel = new JButton(getString("btn.trustlevel.caption"));
		btnTrustlevel.setMnemonic(getString("btn.trustlevel.mnemonic").charAt(0));
		btnTrustlevel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (lstFaction.getModel().getSize() <= 0 || lstFaction.isSelectionEmpty()) {
					return;
				}
				Object[] selectedFactions = lstFaction.getSelectedValues();
				boolean validInput = false;
				while (!validInput) {
					// ask for Trustlevel
					validInput = true;
					String oldTrustLevel;
					if (selectedFactions.length == 1) {
						Faction faction = (Faction)selectedFactions[0];
						if (faction.trustLevelSetByUser) {
							oldTrustLevel = String.valueOf(faction.trustLevel);
						} else {
							oldTrustLevel = ""; // indicates default
						}
					} else {
						oldTrustLevel = ""; // more than one faction selected
					}
					String stringValue = (String)JOptionPane.showInputDialog(FactionStatsDialog.this, getString("msg.trustlevelinput.text"), getString("msg.trustlevelinput.title"), JOptionPane.OK_CANCEL_OPTION, null, null, oldTrustLevel);
					if (stringValue != null) {
						for (int index = 0; index < selectedFactions.length; index++) {
							Faction faction = (Faction)selectedFactions[index];
							// this indicates, that further on Magellan shall
							// calculate the trustlevel for this faction on its own
							if (stringValue.length() == 0) {
								faction.trustLevelSetByUser = false;
								faction.trustLevel = Faction.TL_DEFAULT;
							} else {
								try {
									int intValue = Integer.parseInt(stringValue);
									faction.trustLevel = intValue;
									faction.trustLevelSetByUser = true;
								} catch (NumberFormatException exc) {
									// ask again for input
									validInput = false;
								}
							}
						}
						if (validInput) {
							TrustLevels.recalculateTrustLevels(data);
							// GameData did probably change
							dispatcher.fire(new GameDataEvent(this, data));
						} else {
							JOptionPane.showMessageDialog(FactionStatsDialog.this, getString("msg.trustlevelinputinvalid"));
						}
					}
				}
			}
		});

		JPanel pnlButtons = new JPanel(new GridLayout(4, 1, 0, 3));
		pnlButtons.add(sort);
		pnlButtons.add(btnPassword);
		pnlButtons.add(btnTrustlevel);
		pnlButtons.add(btnDeleteFaction);

		JPanel pnlFactions = new JPanel(new BorderLayout(0, 5));
		pnlFactions.add(new JScrollPane(lstFaction), BorderLayout.CENTER);
		pnlFactions.add(pnlButtons, BorderLayout.SOUTH);

		return pnlFactions;
	}

	/**
	 * Tries to instantiate a SkillChartPanel-Object to display skillcharts.
	 * If the necessary library can't be found, null is returned
	 */
	private JPanel getSkillChartPanel() {
		// try to load the skillchart classes
		ResourcePathClassLoader loader = new ResourcePathClassLoader(settings);
		Class SkillChartPanel = null;
		try {
			SkillChartPanel = loader.loadClass("com.eressea.skillchart.SkillChartPanel");
		} catch (java.lang.ClassNotFoundException cnf) {
			return null;
		}
		// get it's constructor
		java.lang.reflect.Constructor constructor = null;
		try {
			constructor = SkillChartPanel.getConstructor(new Class[]{Class.forName("com.eressea.event.EventDispatcher"), Class.forName("com.eressea.GameData"), Class.forName("java.util.Properties")});
		} catch (java.lang.NoSuchMethodException e) {
			log.error(e);
			return null;
		} catch (java.lang.ClassNotFoundException e) {
			log.error(e);
			return null;
		} catch (java.lang.NoClassDefFoundError e) {
			log.error(e);
			return null;
		}
		// create an instance of this class
		Object skillChartPanel = null;
		try {
			skillChartPanel = constructor.newInstance(new Object[]{dispatcher, data, settings});
		} catch (java.lang.reflect.InvocationTargetException e) {
			log.error(e);
			return null;
		} catch (java.lang.IllegalAccessException e) {
			log.error(e);
			return null;
		} catch (java.lang.InstantiationException e) {
			log.error(e);
			return null;
		}
		// return casted Panel
		return (JPanel)skillChartPanel;
	}

	private void storeSettings() {
		settings.setProperty("FactionStatsDialog.x", getX() + "");
		settings.setProperty("FactionStatsDialog.y", getY() + "");
		settings.setProperty("FactionStatsDialog.width", getWidth() + "");
		settings.setProperty("FactionStatsDialog.height", getHeight() + "");
		settings.setProperty("FactionStatsDialog.split", splFaction.getDividerLocation() + "");
		if (lstFaction.getModel().getSize() > 0 && lstFaction.getSelectedValue() != null) {
			settings.setProperty("FactionStatsDialog.selFacID", ((EntityID)((Faction)lstFaction.getSelectedValue()).getID()).intValue() + "");
		}
	}

	protected void quit() {
		storeSettings();
		super.quit();
	}

	public void gameDataChanged(GameDataEvent e) {
		data = e.getGameData();
		factions = CollectionFactory.createLinkedList(data.factions().values());
		// sort factions
		Collections.sort(factions, factionTrustComparator);
		lstFaction.setListData(factions.toArray());

		ID selFacID = EntityID.createEntityID(settings.getProperty("FactionStatsDialog.selFacID", "-1"), 10);
		Faction selFac = data.getFaction(selFacID);
		if (selFac != null) {
			lstFaction.setSelectedValue(selFac, true);
		} else {
			lstFaction.setSelectedIndex(0);
		}
		lstFaction.repaint();
	}

	//public void showEresseaOptions() {
	//	tabPane.setSelectedComponent(optionPanel);
	//}

	// pavkovic 2003.01.28: this is a Map of the default Translations mapped to this class
	// it is called by reflection (we could force the implementation of an interface,
	// this way it is more flexible.)
	// Pls use this mechanism, so the translation files can be created automagically
	// by inspecting all classes.
	private static Map defaultTranslations;
	public synchronized static Map getDefaultTranslations() {
		if(defaultTranslations == null) {
			defaultTranslations = CollectionFactory.createHashtable();
			defaultTranslations.put("window.title" , "Faction statistics");
			
			defaultTranslations.put("btn.close.caption" , "Close");
			defaultTranslations.put("btn.close.menmonic" , "c");
			defaultTranslations.put("btn.deletefaction.caption" , "Delete faction");
			defaultTranslations.put("btn.deletefaction.mnemonic" , "d");
			defaultTranslations.put("btn.setpwd.caption" , "Set password");
			defaultTranslations.put("btn.setpwd.menmonic" , "p");
			defaultTranslations.put("btn.trustlevel.caption" , "Set trustlevel");
			defaultTranslations.put("btn.trustlevel.mnemonic" , "t");
			defaultTranslations.put("btn.sort.trustlevel.caption" , "Sort by trustlevel");
			defaultTranslations.put("btn.sort.name.caption" , "Sort by name");
			
			defaultTranslations.put("tab.options.caption" , "Eressea options");
			defaultTranslations.put("tab.stats.caption" , "Statistics");
			defaultTranslations.put("tab.skillchart.caption" , "Skill charts");
			
			defaultTranslations.put("msg.factioncontainsunits.text" , "The faction \"{0}\" cannot be deleted because it contains units.");
			defaultTranslations.put("msg.factionisallied.text" , "The faction \"{0}\" cannot be deleted because faction \"{1}\" has an alliance with it.");
			defaultTranslations.put("msg.factionownsbuilding.text" , "The faction \"{0}\" cannot be deleted because it is the owner of building \"{1}\".");
			defaultTranslations.put("msg.factionownsship.text" , "The faction \"{0}\" cannot be deleted because it is the owner of ship \"{0}\".");
			defaultTranslations.put("msg.confirmdeletefaction.text" , "Are you sure you want to delete faction \"{0}\" from the report?");
			defaultTranslations.put("msg.confirmdeletefaction.title" , "Delete faction");
			defaultTranslations.put("msg.passwdinput.text" , "Please enter the password to be used for faction \"{0}\":");
			defaultTranslations.put("msg.passwdinput.title" , "Enter password");
			defaultTranslations.put("msg.trustlevelinput.text=Please input a trustlevel (signed integer).\nNote:\n - Factions with a trust > 0 will be treated as allied\n - Factions with a trust >" , " 100 are privileged and will therefore have\nthe same rights just like factions with a set password\n - making an empty input will cause Magellan to calculate\nthe trustlevel for this faction on its own.");
			defaultTranslations.put("msg.trustlevelinput.title" , "Set trustlevel");
			defaultTranslations.put("msg.trustlevelinputinvalid" , "Invalid input format!");
		}
		return defaultTranslations;
	}

}
