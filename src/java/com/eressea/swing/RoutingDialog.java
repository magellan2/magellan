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

package com.eressea.swing;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.eressea.Coordinate;
import com.eressea.GameData;
import com.eressea.Region;
import com.eressea.util.CollectionFactory;

/**
 * A dialog to fetch a destination region (usually for a ship). Out of the knowledge of this
 * destination region magellan can create respective orders that move the ship to that region. It
 * is also possible, to determine, if ship range shall be considered, whether Vorlage-meta-orders
 * shall be created if the destination region can not be reached in one week and if the captain's
 * orders shall be replaced.
 *
 * @author Ulrich Küster
 */
public class RoutingDialog extends InternationalizedDialog {
	// be careful, this object doesn't listen for GameDataEvents!
	private GameData data;
	private JButton ok;
	private JButton cancel;
	private JRadioButton createRoute;
	private JRadioButton createSingleTrip;
	private JCheckBox considerShipRange;
	private JCheckBox createVorlageOrders;
	private JCheckBox replaceOrders;
	private List regionList;
	private JComboBox regions;
	private JTextField regionName;
	private JTextField xCor;
	private JTextField yCor;

	/**
	 * Creates a new RoutingDialog object.
	 *
	 * @param owner TODO: DOCUMENT ME!
	 * @param data TODO: DOCUMENT ME!
	 */
	public RoutingDialog(Frame owner, GameData data) {
		this(owner, data, data.regions().values());
	}

	/**
	 * Creates a new RoutingDialog object.
	 *
	 * @param owner TODO: DOCUMENT ME!
	 * @param data TODO: DOCUMENT ME!
	 * @param destRegions TODO: DOCUMENT ME!
	 */
	public RoutingDialog(Frame owner, GameData data, Collection destRegions) {
		this(owner, data, destRegions, true);
	}

	/**
	 * Creates a new RoutingDialog object.
	 *
	 * @param owner TODO: DOCUMENT ME!
	 * @param data TODO: DOCUMENT ME!
	 * @param destRegions TODO: DOCUMENT ME!
	 * @param excludeUnnamed TODO: DOCUMENT ME!
	 */
	public RoutingDialog(Frame owner, GameData data, Collection destRegions, boolean excludeUnnamed) {
		super(owner, true);
		this.data = data;
		setTitle(getString("window.title"));

		Container cp = getContentPane();
		cp.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints(0, 0, 4, 1, 0, 0.2,
													  GridBagConstraints.CENTER,
													  GridBagConstraints.BOTH,
													  new Insets(1, 3, 1, 3), 0, 0);

		JPanel destSelect = new JPanel();
		destSelect.setLayout(new GridBagLayout());
		destSelect.setBorder(BorderFactory.createTitledBorder(getString("window.message"))); //BorderFactory.createBevelBorder(BevelBorder.LOWERED));

		//destSelect.add(new JLabel(getString("window.message")), c);
		c.gridwidth = 1;
		destSelect.add(new JLabel(getString("xcoor")), c);

		c.gridx = 1;
		c.weightx = 0.2;
		xCor = new JTextField();
		xCor.setPreferredSize(new Dimension(40, 20));
		destSelect.add(xCor, c);

		c.gridx = 2;
		c.weightx = 0;
		destSelect.add(new JLabel(getString("ycoor")), c);

		c.gridx = 3;
		c.weightx = 0.2;
		yCor = new JTextField();
		yCor.setPreferredSize(new Dimension(40, 20));
		destSelect.add(yCor, c);

		c.gridy = 1;
		c.gridx = 0;
		c.gridwidth = 4;
		destSelect.add(new JSeparator(SwingConstants.HORIZONTAL));

		if(destRegions != null) {
			c.gridy = 2;

			JPanel temp = new JPanel();
			temp.setLayout(new BorderLayout(6, 6));
			destSelect.add(temp, c);

			temp.add(new JLabel(getString("regionname")), BorderLayout.WEST);

			regionName = new JTextField();
			temp.add(regionName, BorderLayout.CENTER);
			regionName.getDocument().addDocumentListener(new DocumentListener() {
					public void insertUpdate(DocumentEvent e) {
						int i = Collections.binarySearch(regionList, regionName.getText(),
														 new RegionNameComparator());

						if(i < 0) {
							i = -i - 1;
						}

						regions.setSelectedIndex(i);
					}

					public void removeUpdate(DocumentEvent e) {
						int i = Collections.binarySearch(regionList, regionName.getText(),
														 new RegionNameComparator());

						if(i < 0) {
							i = -i - 1;
						}

						regions.setSelectedIndex(i);
					}

					public void changedUpdate(DocumentEvent e) {
						int i = Collections.binarySearch(regionList, regionName.getText(),
														 new RegionNameComparator());

						if(i < 0) {
							i = -i - 1;
						}

						regions.setSelectedIndex(i);
					}
				});

			c.gridx = 0;
			c.gridwidth = 4;
			c.gridy = 3;
			regionList = CollectionFactory.createLinkedList();

			if(destRegions == null) {
				destRegions = data.regions().values();
			}

			for(Iterator iter = destRegions.iterator(); iter.hasNext();) {
				Region r = (Region) iter.next();

				if(!excludeUnnamed || ((r.getName() != null) && (r.getName() != ""))) {
					regionList.add(r);
				}
			}

			Collections.sort(regionList, new RegionNameComparator());
			regions = new JComboBox(regionList.toArray());
			regions.setPreferredSize(new Dimension(300, 25));
			regions.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						Region r = (Region) regions.getSelectedItem();
						Coordinate co = r.getCoordinate();
						xCor.setText(co.x + "");
						yCor.setText(co.y + "");
					}
				});
			destSelect.add(regions, c);
		}

		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		cp.add(destSelect, c);

		createSingleTrip = new JRadioButton(getString("radiobtn.createsingletrip.title"));
		createRoute = new JRadioButton(getString("radiobtn.createroute.title"));

		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(createSingleTrip);
		buttonGroup.add(createRoute);
		createSingleTrip.setSelected(true);
		createSingleTrip.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					considerShipRange.setEnabled(true);

					if(considerShipRange.isSelected()) {
						createVorlageOrders.setEnabled(true);
					}
				}
			});
		createRoute.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					considerShipRange.setEnabled(false);
					createVorlageOrders.setEnabled(false);
				}
			});

		c.gridy = 1;
		cp.add(createSingleTrip, c);

		c.gridy = 2;
		cp.add(createRoute, c);

		c.insets.left = 30;
		c.gridy = 3;

		considerShipRange = new JCheckBox(getString("chkbox.considerrange.title"));
		considerShipRange.setSelected(true);
		considerShipRange.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(considerShipRange.isSelected()) {
						createVorlageOrders.setEnabled(true);
					} else {
						createVorlageOrders.setEnabled(false);
					}
				}
			});
		cp.add(considerShipRange, c);

		c.gridy = 4;
		createVorlageOrders = new JCheckBox(getString("chkbox.createvorlageorders.title"));
		cp.add(createVorlageOrders, c);

		c.gridy = 5;
		c.insets.left = 3;
		replaceOrders = new JCheckBox(getString("chkbox.replaceorders.title"));
		cp.add(replaceOrders, c);

		c.gridy = 6;
		c.gridwidth = 1;
		ok = new JButton(getString("okbutton.text"));
		ok.setMnemonic(getString("okbutton.mnemonic").charAt(0));
		cp.add(ok, c);

		c.gridx = 1;
		cancel = new JButton(getString("cancelbutton.text"));
		cancel.setMnemonic(getString("cancelbutton.mnemonic").charAt(0));
		cancel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					quit();
				}
			});
		cp.add(cancel, c);
	}

	/**
	 * Shows the dialog and returns the above explained values
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public RetValue showRoutingDialog() {
		final RetValue retVal = new RetValue(null, false, false, false, false);
		ActionListener okButtonAction = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int x = 0;
				int y = 0;

				try {
					x = Integer.parseInt(xCor.getText());
					y = Integer.parseInt(yCor.getText());
					retVal.dest = new Coordinate(x, y);
					retVal.makeRoute = createRoute.isSelected();
					retVal.useRange = considerShipRange.isSelected();
					retVal.useVorlage = createVorlageOrders.isSelected();
					retVal.replaceOrders = replaceOrders.isSelected();
				} catch(NumberFormatException exc) {
				}

				quit();
			}
		};

		ok.addActionListener(okButtonAction);
		pack();
		setLocationRelativeTo(getOwner());
		show();

		if(retVal.dest == null) {
			return null;
		} else {
			return retVal;
		}
	}

	/**
	 * Just to order regions by their names.
	 */
	private class RegionNameComparator implements Comparator {
		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param o1 TODO: DOCUMENT ME!
		 * @param o2 TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public int compare(Object o1, Object o2) {
			String n1 = "";
			String n2 = "";

			if(o1 instanceof Region) {
				Region r1 = (Region) o1;
				n1 = r1.getName();

				if(n1 == null) {
					n1 = r1.getCoordinate().toString();
				}
			} else {
				n1 = o1.toString();
			}

			if(o2 instanceof Region) {
				Region r2 = (Region) o2;
				n2 = r2.getName();

				if(n2 == null) {
					n2 = r2.getCoordinate().toString();
				}
			} else {
				n2 = o2.toString();
			}

			return n1.compareToIgnoreCase(n2);
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @author $author$
	 * @version $Revision$
	 */
	public class RetValue {
		/** TODO: DOCUMENT ME! */
		public Coordinate dest;

		/** TODO: DOCUMENT ME! */
		public boolean makeRoute;

		/** TODO: DOCUMENT ME! */
		public boolean useRange;

		/** TODO: DOCUMENT ME! */
		public boolean useVorlage;

		/** TODO: DOCUMENT ME! */
		public boolean replaceOrders;

		/**
		 * Creates a new RetValue object.
		 *
		 * @param d TODO: DOCUMENT ME!
		 * @param b1 TODO: DOCUMENT ME!
		 * @param b2 TODO: DOCUMENT ME!
		 * @param b3 TODO: DOCUMENT ME!
		 * @param b4 TODO: DOCUMENT ME!
		 */
		public RetValue(Coordinate d, boolean b1, boolean b2, boolean b3, boolean b4) {
			dest = d;
			makeRoute = b1;
			useRange = b2;
			useVorlage = b3;
			replaceOrders = b4;
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
			defaultTranslations.put("window.title", "Route scheduler");
			defaultTranslations.put("window.message", "Destination region:");
			defaultTranslations.put("xcoor", "X-Coordinate:");
			defaultTranslations.put("ycoor", "Y-Coordinate:");
			defaultTranslations.put("chkbox.considerrange.title", "Consider range");
			defaultTranslations.put("chkbox.createvorlageorders.title", "Create Vorlage meta orders");
			defaultTranslations.put("chkbox.replaceorders.title", "Replace unit's orders");
			defaultTranslations.put("okbutton.text", "OK");
			defaultTranslations.put("okbutton.mnemonic", "o");
			defaultTranslations.put("cancelbutton.text", "Cancel");
			defaultTranslations.put("cancelbutton.mnemonic", "c");
			defaultTranslations.put("regionname", "Region name:");

			defaultTranslations.put("radiobtn.createsingletrip.title", "Plan single trip");
			defaultTranslations.put("radiobtn.createroute.title", "Plan permanent route");
		}

		return defaultTranslations;
	}
}
