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
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import com.eressea.GameData;
import com.eressea.ID;
import com.eressea.Item;
import com.eressea.LuxuryPrice;
import com.eressea.Region;
import com.eressea.StringID;
import com.eressea.Unit;
import com.eressea.event.EventDispatcher;
import com.eressea.event.GameDataEvent;
import com.eressea.event.SelectionEvent;
import com.eressea.event.SelectionListener;
import com.eressea.rules.ItemType;
import com.eressea.util.CollectionFactory;
import com.eressea.util.comparator.FactionTrustComparator;
import com.eressea.util.comparator.NameComparator;

/**
 * DOCUMENT ME!
 *
 * @author Ulrich Küster
 */
public class TradeOrganizer extends InternationalizedDataDialog implements SelectionListener {
	protected java.util.List regions = new LinkedList();
	protected int minSellMultiplier = 1;
	protected SellTable sell;
	protected BuyTable buy;
	protected StocksTable stocks;
	protected JComboBox luxuries;
	protected JSlider minSellMultiplierSlider;
	protected JLabel totalSellingVolume;
	protected JLabel totalBuyingVolume;
	protected JLabel averagePrice;
	protected JList factionList;

	/**
	 * Creates a new TradeOrganizer object.
	 *
	 * @param owner TODO: DOCUMENT ME!
	 * @param dispatcher TODO: DOCUMENT ME!
	 * @param data TODO: DOCUMENT ME!
	 * @param settings TODO: DOCUMENT ME!
	 */
	public TradeOrganizer(Frame owner, EventDispatcher dispatcher, GameData data,
						  Properties settings) {
		this(owner, dispatcher, data, settings, null);
	}

	/**
	 * Creates a new TradeOrganizer object.
	 *
	 * @param owner TODO: DOCUMENT ME!
	 * @param dispatcher TODO: DOCUMENT ME!
	 * @param data TODO: DOCUMENT ME!
	 * @param settings TODO: DOCUMENT ME!
	 * @param newRegions TODO: DOCUMENT ME!
	 */
	public TradeOrganizer(Frame owner, EventDispatcher dispatcher, GameData data,
						  Properties settings, Collection newRegions) {
		super(owner, false, dispatcher, data, settings);

		// register for events
		dispatcher.addGameDataListener(this);
		dispatcher.addSelectionListener(this);

		init();
		setRegions((newRegions == null) ? Collections.EMPTY_SET : newRegions);
		show();
	}

	protected void init() {
		int width = Integer.parseInt(settings.getProperty("TradeOrganizer.width", "800"));
		int height = Integer.parseInt(settings.getProperty("TradeOrganizer.height", "600"));
		int xPos = Integer.parseInt(settings.getProperty("TradeOrganizer.xPos", "-1"));
		int yPos = Integer.parseInt(settings.getProperty("TradeOrganizer.yPos", "-1"));

		minSellMultiplier = Integer.parseInt(settings.getProperty("TradeOrganizer.minSellMultiplier",
																  "1"));

		if(xPos == -1) {
			xPos = ((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() - width) / 2;
		}

		if(yPos == -1) {
			yPos = ((int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() - height) / 2;
		}

		setSize(width, height);
		setLocation(xPos, yPos);
		setTitle(getString("title"));

		// build GUI
		JTabbedPane tabPane = new JTabbedPane();

		// build top panel
		JPanel topPanel = new JPanel();
		topPanel.setBorder(new TitledBorder(""));
		topPanel.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
													  GridBagConstraints.NONE,
													  new Insets(5, 5, 5, 5), 0, 0);

		topPanel.add(new JLabel(getString("minsellmultiplier")), c);
		c.gridx++;
		minSellMultiplierSlider = new JSlider(1, 30, minSellMultiplier);
		minSellMultiplierSlider.setMinorTickSpacing(1);
		minSellMultiplierSlider.setMajorTickSpacing(1);

		Hashtable sliderLabels = minSellMultiplierSlider.createStandardLabels(5);
		minSellMultiplierSlider.setLabelTable(sliderLabels);
		minSellMultiplierSlider.setSnapToTicks(true);
		minSellMultiplierSlider.setPaintTicks(true);
		minSellMultiplierSlider.setPaintLabels(true);

		minSellMultiplierSlider.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent ce) {
					minSellMultiplier = Math.max(minSellMultiplierSlider.getValue(), 1);
					setSellTableRegions();
				}
			});
		topPanel.add(minSellMultiplierSlider, c);
		c.gridx++;

		topPanel.add(new JLabel(getString("luxury")), c);
		c.gridx++;
		c.weightx = 1.0;

		// Initialize ComboBox with luxuries
		LinkedList items = new LinkedList();

		if((data != null) && (data.rules != null)) {
			String help[] = new String[] {
								"Balsam", "Gewürz", "Juwel", "Myrrhe", "Öl", "Seide", "Weihrauch"
							};

			for(int i = 0; i < help.length; i++) {
				items.add(StringID.create(help[i]));
			}
		}

		luxuries = new JComboBox(items.toArray());
		luxuries.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setSellTableRegions();
					setBuyTableRegions();
					setStocksTableRegions();
				}
			});
		topPanel.add(luxuries, c);

		JPanel help = new JPanel();
		help.setLayout(new GridBagLayout());
		c.insets = new Insets(0, 0, 0, 0);
		c.gridx = 0;
		c.weightx = 0;
		totalBuyingVolume = new JLabel();
		help.add(totalBuyingVolume, c);
		c.gridx++;
		totalSellingVolume = new JLabel();
		help.add(totalSellingVolume, c);
		c.gridx++;
		c.weightx = 1;
		averagePrice = new JLabel();
		help.add(averagePrice, c);

		c.gridx = 0;
		c.gridy = 1;
		c.insets = new Insets(5, 5, 5, 5);
		c.gridwidth = 2;
		topPanel.add(help, c);

		Container cp = getContentPane();
		cp.setLayout(new BorderLayout());
		cp.add(topPanel, BorderLayout.NORTH);
		cp.add(tabPane, BorderLayout.CENTER);

		// build Tables in TabbedPane
		cp = new JPanel();
		cp.setLayout(new BorderLayout());
		tabPane.addTab(getString("buy"), cp);
		buy = new BuyTable();
		cp.add(new JScrollPane(buy), BorderLayout.CENTER);

		cp = new JPanel();
		cp.setLayout(new BorderLayout());
		tabPane.addTab(getString("sell"), cp);
		sell = new SellTable();
		cp.add(new JScrollPane(sell), BorderLayout.CENTER);

		tabPane.add(getString("stocks"), getStocksPanel());
	}

	private JPanel getStocksPanel() {
		JPanel panel = new JPanel();

		//panel.setLayout(new GridBagLayout());
		//GridBagConstraints c = new GridBagConstraints(0, 0, 1, 1, 0, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(3, 3, 3, 3), 0, 0);
		factionList = new JList();
		updateFactions();
		factionList.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					stocks.setFactions(java.util.Arrays.asList(factionList.getSelectedValues()));
				}
			});

		JScrollPane factionsScrollPane = new JScrollPane(factionList);
		stocks = new StocksTable();

		JScrollPane stocksTableScrollPane = new JScrollPane(stocks);

		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, factionsScrollPane,
										  stocksTableScrollPane);
		split.setDividerLocation(0.4);

		panel.setLayout(new BorderLayout());
		panel.add(split, BorderLayout.CENTER);

		return panel;
	}

	/**
	 * Called to update the faction list in the stocks panel
	 */
	private void updateFactions() {
		Vector factionVector = new Vector(data.factions().values());
		Collections.sort(factionVector, FactionTrustComparator.DEFAULT_COMPARATOR);

		DefaultListModel model = new DefaultListModel();

		for(Iterator iter = factionVector.listIterator(); iter.hasNext();) {
			model.addElement(iter.next());
		}

		factionList.setModel(model);
		factionList.setSelectedIndex(0);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param ge TODO: DOCUMENT ME!
	 */
	public void gameDataChanged(GameDataEvent ge) {
		super.gameDataChanged(ge);
		setRegions(Collections.EMPTY_SET);

		updateFactions();
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param se TODO: DOCUMENT ME!
	 */
	public void selectionChanged(SelectionEvent se) {
		if((se.getSelectionType() == SelectionEvent.ST_REGIONS) &&
			   (se.getSelectedObjects() != null)) {
			setRegions(se.getSelectedObjects());
		}
	}

	/**
	 * Sets this classes region list. Filters out all regions, which trade information is unknown.
	 *
	 * @param newRegions TODO: DOCUMENT ME!
	 */
	private void setRegions(Collection newRegions) {
		if(newRegions.isEmpty() && (data.regions() != null)) {
			newRegions = data.regions().values();
		}

		regions = new LinkedList();

		for(Iterator iter = newRegions.iterator(); iter.hasNext();) {
			Region region = (Region) iter.next();

			if((region.prices != null) && (region.prices.size() > 0)) {
				regions.add(region);
			}
		}

		setSellTableRegions();
		setBuyTableRegions();
		setStocksTableRegions();
	}

	/**
	 * Resets the regions, that the buytable shows. Also resets the information about the total
	 * buying volume. Assumes, that the prices of all regions contained in this class's
	 * region-list are known.
	 */
	private void setBuyTableRegions() {
		Object curLux = luxuries.getSelectedItem();

		if(curLux == null) {
			return;
		}

		LinkedList newRegions = new LinkedList();
		int total = 0;

		for(Iterator iter = regions.iterator(); iter.hasNext();) {
			Region region = (Region) iter.next();
			LuxuryPrice price = (LuxuryPrice) region.prices.get(curLux);

			if(price.getPrice() < 0) {
				newRegions.add(region);
				total += (region.peasants / 100);
			}
		}

		totalBuyingVolume.setText(getString("totalBuyingVolume") + total + ", ");
		buy.setRegions(newRegions);
		buy.sort();
	}

	/**
	 * Resets the regions, that the sell table shows. Also resets the information about the total
	 * selling volume and the average price. Assumes, that the prices of all regions contained in
	 * this class's region-list are known.
	 */
	private void setSellTableRegions() {
		Object curLux = luxuries.getSelectedItem();

		if(curLux == null) {
			return;
		}

		LinkedList newRegions = new LinkedList();
		int total = 0;
		int totalPrice = 0;

		for(Iterator iter = regions.iterator(); iter.hasNext();) {
			Region region = (Region) iter.next();
			LuxuryPrice price = (LuxuryPrice) region.prices.get(curLux);

			if(checkPrice(price)) {
				newRegions.add(region);

				int volume = region.peasants / 100;
				total += volume;
				totalPrice += (volume * price.getPrice());
			}
		}

		sell.setRegions(newRegions);
		totalSellingVolume.setText(getString("totalSellingVolume") + total + ", ");

		if(total != 0) {
			averagePrice.setText(getString("averagePrice") + (totalPrice / total));
		} else {
			averagePrice.setText(getString("averagePrice") + 0);
		}

		sell.sort();
	}

	/**
	 * Returns false iff price &lt; 0 or price/itemtype.getResources("Silber") &lt;
	 * minSellMultiplier
	 *
	 * @param price TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	private boolean checkPrice(LuxuryPrice price) {
		if(price.getPrice() > 0) {
			Item luxuryPrice = price.getItemType().getResource(StringID.create("Silber"));

			if((luxuryPrice != null) && (luxuryPrice.getAmount() > 0)) {
				if((price.getPrice() / luxuryPrice.getAmount()) < minSellMultiplier) {
					return false;
				}
			}

			return true;
		}

		return false;
	}

	/**
	 * Just for convenience. Sets the regions included in the stocks table
	 */
	private void setStocksTableRegions() {
		stocks.setRegions(regions);
	}

	protected void quit() {
		// store settings
		settings.setProperty("TradeOrganizer.width", String.valueOf(getWidth()));
		settings.setProperty("TradeOrganizer.height", String.valueOf(getHeight()));
		settings.setProperty("TradeOrganizer.xPos", String.valueOf(getLocation().x));
		settings.setProperty("TradeOrganizer.yPos", String.valueOf(getLocation().y));
		settings.setProperty("TradeOrganizer.minSellMultiplier", String.valueOf(minSellMultiplier));

		super.quit();
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @author $author$
	 * @version $Revision$
	 */
	public class SellTable extends JTable {
		private SellTableModel model;

		/**
		 * Creates a new SellTable object.
		 */
		public SellTable() {
			super();
			model = new SellTableModel();
			this.setModel(model);
			this.getTableHeader().setReorderingAllowed(false);
			this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			this.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent e) {
						int i = SellTable.this.getSelectedRow();
						Region r = model.getRegion(i);

						if(r != null) {
							dispatcher.fire(new SelectionEvent(this, null, r,
															   SelectionEvent.ST_DEFAULT));
						}
					}
				});
			;

			// sorting
			this.getTableHeader().addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent e) {
						int i = getTableHeader().getColumnModel().getColumnIndexAtX(e.getPoint().x);
						model.sort(i);
					}
				});
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public Collection getRegions() {
			return model.getRegions();
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param regions TODO: DOCUMENT ME!
		 */
		public void setRegions(Collection regions) {
			model.setRegions(regions);
		}

		/**
		 * TODO: DOCUMENT ME!
		 */
		public void sort() {
			model.sort(model.curSort);
		}
	}

	private class SellTableModel extends AbstractTableModel {
		private int curSort = 1;
		private LinkedList tableRegions = new LinkedList();

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public int getRowCount() {
			return tableRegions.size();
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public int getColumnCount() {
			return 3;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param row TODO: DOCUMENT ME!
		 * @param col TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public Object getValueAt(int row, int col) {
			Region region = (Region) tableRegions.get(row);

			switch(col) {
			case 0:
				return region.toString();

			case 1: {
				Object o = luxuries.getSelectedItem();

				if((o == null) && (luxuries.getItemCount() > 0)) {
					luxuries.setSelectedIndex(0);
				}

				LuxuryPrice price = (LuxuryPrice) region.prices.get(o);

				if(price == null) {
					return "-?-";
				} else {
					return new Integer(price.getPrice());
				}
			}

			case 2:
				return new Integer(region.peasants / 100);
			}

			return null;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param col TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public String getColumnName(int col) {
			switch(col) {
			case 0:
				return getString("buycolumnname1");

			case 1:
				return getString("buycolumnname2");

			case 2:
				return getString("buycolumnname3");
			}

			return "";
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param col TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public Class getColumnClass(int col) {
			return this.getValueAt(0, col).getClass();
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public Collection getRegions() {
			return tableRegions;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param regs TODO: DOCUMENT ME!
		 */
		public void setRegions(Collection regs) {
			tableRegions.clear();
			tableRegions.addAll(regs);
			sort(curSort);
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param row TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public Region getRegion(int row) {
			return (Region) tableRegions.get(row);
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param i TODO: DOCUMENT ME!
		 */
		public void sort(int i) {
			curSort = i;

			switch(i) {
			case 0:
				Collections.sort(tableRegions, new NameComparator(null));

				break;

			case 1:
				Collections.sort(tableRegions, new RegionPriceComparator());

				break;

			case 2:
				Collections.sort(tableRegions, new RegionTradeVolumeComparator());
			}

			sell.revalidate();
			sell.repaint();
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @author $author$
	 * @version $Revision$
	 */
	public class BuyTable extends JTable {
		private BuyTableModel model;

		/**
		 * Creates a new BuyTable object.
		 */
		public BuyTable() {
			model = new BuyTableModel();
			this.setModel(model);
			this.getTableHeader().setReorderingAllowed(false);
			this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			this.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent e) {
						int i = BuyTable.this.getSelectedRow();
						Region r = model.getRegion(i);

						if(r != null) {
							dispatcher.fire(new SelectionEvent(this, null, r,
															   SelectionEvent.ST_DEFAULT));
						}
					}
				});
			;

			// sorting
			this.getTableHeader().addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent e) {
						int i = getTableHeader().getColumnModel().getColumnIndexAtX(e.getPoint().x);
						model.sort(i);
					}
				});
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public Collection getRegions() {
			return model.getRegions();
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param regions TODO: DOCUMENT ME!
		 */
		public void setRegions(Collection regions) {
			model.setRegions(regions);
		}

		/**
		 * TODO: DOCUMENT ME!
		 */
		public void sort() {
			model.sort(model.curSort);
		}
	}

	private class BuyTableModel extends AbstractTableModel {
		private int curSort = 1;
		private LinkedList tableRegions = new LinkedList();

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public int getRowCount() {
			return tableRegions.size();
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public int getColumnCount() {
			return 2;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param row TODO: DOCUMENT ME!
		 * @param col TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public Object getValueAt(int row, int col) {
			Region region = (Region) tableRegions.get(row);

			switch(col) {
			case 0:
				return region.toString();

			case 1:
				return new Integer(region.peasants / 100);
			}

			return null;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param col TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public String getColumnName(int col) {
			switch(col) {
			case 0:
				return getString("sellcolumnname1");

			case 1:
				return getString("sellcolumnname2");
			}

			return "";
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param col TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public Class getColumnClass(int col) {
			return this.getValueAt(0, col).getClass();
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public Collection getRegions() {
			return tableRegions;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param regs TODO: DOCUMENT ME!
		 */
		public void setRegions(Collection regs) {
			tableRegions.clear();
			tableRegions.addAll(regs);
			sort(curSort);
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param row TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public Region getRegion(int row) {
			return (Region) tableRegions.get(row);
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param i TODO: DOCUMENT ME!
		 */
		public void sort(int i) {
			curSort = i;

			switch(i) {
			case 0:
				Collections.sort(tableRegions, new NameComparator(null));

				break;

			case 1:
				Collections.sort(tableRegions, new RegionTradeVolumeComparator());
			}

			buy.revalidate();
			buy.repaint();
		}
	}

	private class StocksTable extends JTable {
		private StocksTableModel model;

		/**
		 * Creates a new StocksTable object.
		 */
		public StocksTable() {
			model = new StocksTableModel();
			this.setModel(model);
			this.getTableHeader().setReorderingAllowed(false);
			this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			this.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent e) {
						int i = StocksTable.this.getSelectedRow();
						Region r = model.getRegion(i);

						if(r != null) {
							dispatcher.fire(new SelectionEvent(this, null, r,
															   SelectionEvent.ST_DEFAULT));
						}
					}
				});
			;

			// sorting
			this.getTableHeader().addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent e) {
						int i = getTableHeader().getColumnModel().getColumnIndexAtX(e.getPoint().x);
						model.sort(i);
					}
				});
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param regions TODO: DOCUMENT ME!
		 */
		public void setRegions(Collection regions) {
			model.setRegions(regions);
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param factions TODO: DOCUMENT ME!
		 */
		public void setFactions(Collection factions) {
			model.setFactions(factions);
		}

		/**
		 * TODO: DOCUMENT ME!
		 */
		public void sort() {
			model.sort(model.curSort);
		}
	}

	private class StocksTableModel extends AbstractTableModel {
		private int curSort = 1;
		private LinkedList tableRegions = new LinkedList();
		private LinkedList tableFactions = new LinkedList();

		// maps Region to Integer (available luxury in stock of selected factions)
		private Hashtable stocks = new Hashtable();

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public int getRowCount() {
			return tableRegions.size();
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public int getColumnCount() {
			return 2;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param row TODO: DOCUMENT ME!
		 * @param col TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public Object getValueAt(int row, int col) {
			Region region = (Region) tableRegions.get(row);

			switch(col) {
			case 0:
				return region.toString();

			case 1:
				return stocks.get(region);
			}

			return null;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param col TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public String getColumnName(int col) {
			switch(col) {
			case 0:
				return getString("sellcolumnname1");

			case 1:
				return getString("sellcolumnname2");
			}

			return "";
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param col TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public Class getColumnClass(int col) {
			return this.getValueAt(0, col).getClass();
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param regs TODO: DOCUMENT ME!
		 */
		public void setRegions(Collection regs) {
			tableRegions.clear();
			tableRegions.addAll(regs);
			updateStocksHashtable();
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param factions TODO: DOCUMENT ME!
		 */
		public void setFactions(Collection factions) {
			tableFactions.clear();
			tableFactions.addAll(factions);
			updateStocksHashtable();
		}

		/**
		 * Collects the information about how many units of the selected luxury good is in stock in
		 * each region. If none is there, the region is removed out of the region pool.
		 */
		private void updateStocksHashtable() {
			stocks.clear();
			tableRegions.clear();
			tableRegions.addAll(regions);

			if(luxuries.getSelectedIndex() == -1) {
				luxuries.setSelectedIndex(0);
			}

			ItemType luxury = data.rules.getItemType((ID) luxuries.getSelectedItem());

			for(Iterator regionIter = tableRegions.iterator(); regionIter.hasNext();) {
				Region r = (Region) regionIter.next();
				int amount = 0;

				for(Iterator unitIter = r.units().iterator(); unitIter.hasNext();) {
					Unit u = (Unit) unitIter.next();

					if((u.getFaction() != null) && tableFactions.contains(u.getFaction())) {
						Item item = u.getItem(luxury);

						if(item != null) {
							amount += item.getAmount();
						}
					}
				}

				if(amount > 0) {
					stocks.put(r, new Integer(amount));
				} else {
					regionIter.remove();
				}
			}

			sort(curSort);
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param row TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public Region getRegion(int row) {
			return (Region) tableRegions.get(row);
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param i TODO: DOCUMENT ME!
		 */
		public void sort(int i) {
			curSort = i;

			switch(i) {
			case 0:
				Collections.sort(tableRegions, new NameComparator(null));

				break;

			case 1:
				Collections.sort(tableRegions, new RegionStockVolumeComparator(stocks));
			}

			TradeOrganizer.this.stocks.revalidate();
			TradeOrganizer.this.stocks.repaint();
		}
	}

	/**
	 * Compares two regions using their stock volume of the current luxury. The stock volume is
	 * retrieved out of the given Hashtable.
	 */
	private class RegionStockVolumeComparator implements Comparator {
		private Hashtable stocks;

		/**
		 * Creates a new RegionStockVolumeComparator object.
		 *
		 * @param stocks TODO: DOCUMENT ME!
		 */
		public RegionStockVolumeComparator(Hashtable stocks) {
			this.stocks = stocks;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param o1 TODO: DOCUMENT ME!
		 * @param o2 TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public int compare(Object o1, Object o2) {
			Region r1 = (Region) o1;
			Region r2 = (Region) o2;
			Integer i1 = (Integer) stocks.get(r1);
			Integer i2 = (Integer) stocks.get(r2);

			if((i1 == null) || (i2 == null)) {
				return 0;
			} else {
				return i2.intValue() - i1.intValue();
			}
		}
	}

	/**
	 * compares two regions using the current luxuries prices
	 */
	private class RegionPriceComparator implements Comparator {
		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param o1 TODO: DOCUMENT ME!
		 * @param o2 TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public int compare(Object o1, Object o2) {
			Region r1 = (Region) o1;
			Region r2 = (Region) o2;
			LuxuryPrice p1 = (LuxuryPrice) r1.prices.get(luxuries.getSelectedItem());
			LuxuryPrice p2 = (LuxuryPrice) r2.prices.get(luxuries.getSelectedItem());

			if((p1 == null) || (p2 == null)) {
				return 0;
			} else {
				return p2.getPrice() - p1.getPrice();
			}
		}
	}

	/**
	 * compares two regions unsing their trade volume
	 */
	private class RegionTradeVolumeComparator implements Comparator {
		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param o1 TODO: DOCUMENT ME!
		 * @param o2 TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public int compare(Object o1, Object o2) {
			Region r1 = (Region) o1;
			Region r2 = (Region) o2;

			return r2.peasants - r1.peasants;
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
			defaultTranslations.put("title", "Trade organizer");
			defaultTranslations.put("buy", "Buying...");
			defaultTranslations.put("sell", "Selling...");
			defaultTranslations.put("buycolumnname1", "Region name");
			defaultTranslations.put("buycolumnname2", "Price");
			defaultTranslations.put("sellcolumnname1", "Region name");
			defaultTranslations.put("sellcolumnname2", "Volume");
			defaultTranslations.put("buycolumnname3", "Volume");
			defaultTranslations.put("luxury", "Trade good: ");
			defaultTranslations.put("minsellmultiplier", "Minimum multiplier for sale: ");
			defaultTranslations.put("totalBuyingVolume", "Total buying volume: ");
			defaultTranslations.put("totalSellingVolume", "Total selling volume: ");
			defaultTranslations.put("averagePrice", "Average selling price: ");
			defaultTranslations.put("stocks", "Stocks...");
		}

		return defaultTranslations;
	}
}
