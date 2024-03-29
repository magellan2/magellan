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

package com.eressea.tasks.swing;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import com.eressea.GameData;
import com.eressea.HasRegion;
import com.eressea.Region;
import com.eressea.Unit;
import com.eressea.event.EventDispatcher;
import com.eressea.event.GameDataEvent;
import com.eressea.event.SelectionEvent;
import com.eressea.event.SelectionListener;
import com.eressea.event.UnitOrdersEvent;
import com.eressea.event.UnitOrdersListener;
import com.eressea.swing.InternationalizedDataPanel;
import com.eressea.swing.table.TableSorter;
import com.eressea.tasks.Inspector;
import com.eressea.tasks.MovementInspector;
import com.eressea.tasks.Problem;
import com.eressea.tasks.ShipInspector;
import com.eressea.tasks.ToDoInspector;
import com.eressea.util.CollectionFactory;
import com.eressea.util.logging.Logger;

/**
 * A panel for showing reviews about unit, region and/or gamedata.
 */
public class TaskTablePanel extends InternationalizedDataPanel implements UnitOrdersListener,
																		  SelectionListener
{
	private static final Logger log = Logger.getInstance(TaskTablePanel.class);
	
	protected JTable table;
	TableSorter sorter;
	TaskTableModel model;
	protected List inspectors;

	/**
	 * Creates a new TaskTablePanel object.
	 *
	 * @param d 
	 * @param initData 
	 * @param p 
	 * @deprecated see InternationalizedDataPanel
	 */
	public TaskTablePanel(EventDispatcher d, GameData initData, Properties p) {
		super(d, initData, p);
		init(d);
	}

	private void init(EventDispatcher d) {
		d.addUnitOrdersListener(this);
		// TODO (stm): this is broken, so for now we don't care about selection
//		d.addSelectionListener(this);

		initGUI();
		initInspectors();
		refreshProblems();
	}

	private void initGUI() {
		model = new TaskTableModel(getHeaderTitles());
		sorter = new TableSorter(model);
		table = new JTable(sorter);
//		sorter.addMouseListenerToHeaderInTable(table); // OLD
		sorter.setTableHeader(table.getTableHeader());   //NEW
		
		
		// allow reordering of headers
		table.getTableHeader().setReorderingAllowed(true);

		// set row selection to single selection
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Row 0 ("I"): smallest possible, not resizeable
		table.getColumnModel().getColumn(TaskTableModel.IMAGE_POS).setResizable(false);
		table.getColumnModel().getColumn(TaskTableModel.IMAGE_POS).setMaxWidth(table.getColumnModel()
																					.getColumn(TaskTableModel.IMAGE_POS)
																					.getMinWidth()*3);

//		// Row 1 ("!"): smallest possible, not resizeable
//		table.getColumnModel().getColumn(TaskTableModel.UNKNOWN_POS).setResizable(false);
//		table.getColumnModel().getColumn(TaskTableModel.UNKNOWN_POS).setMaxWidth(table.getColumnModel()
//																					  .getColumn(TaskTableModel.UNKNOWN_POS)
//																					  .getMinWidth()*3);

		// Row 2 ("Line"): smallest possible, not resizeable
		table.getColumnModel().getColumn(TaskTableModel.LINE_POS).setResizable(true);
		table.getColumnModel().getColumn(TaskTableModel.LINE_POS).setMaxWidth(table.getColumnModel()
																				   .getColumn(TaskTableModel.LINE_POS)
																				   .getMinWidth()*2);
		// react on double clicks on a row
		table.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					if(e.getClickCount() == 2) {
						JTable target = (JTable) e.getSource();
						int row = target.getSelectedRow();

						if(log.isDebugEnabled()) {
							log.debug("TaskTablePanel: Double click on row " + row);
						}
						selectObjectOnRow(row);
					}
				}
			});

		// layout component
		this.setLayout(new BorderLayout());
		this.add(new JScrollPane(table), BorderLayout.CENTER);
	}

	private void selectObjectOnRow(int row) {
		Object obj = sorter.getValueAt(row, TaskTableModel.OBJECT_POS);
		dispatcher.fire(new SelectionEvent(this, null, obj));
	}

	private static final int RECALL_IN_MS = 10;
	// TODO make this configurable
	private static final boolean REGIONS_WITH_UNCONFIRMED_UNITS_ONLY = true;
	private Timer timer;
	private Iterator regionsIterator;

	private void refreshProblems() {
		if(model != null) {
			model.clearProblems();
		}

		if((data != null) && (data.regions() != null)) {
			regionsIterator = data.regions().values().iterator();

			if(timer == null) {
				timer = new Timer(true);
				timer.scheduleAtFixedRate(new TimerTask() {
						public void run() {
							inspectNextRegion();
						}
					}, RECALL_IN_MS, RECALL_IN_MS);
			}
		} else {
			regionsIterator = null;

			if(timer != null) {
				timer.cancel();
				timer = null;
			}
		}
	}

	private boolean inspectNextRegion() {
		Region r = getNextRegion();

		if(r != null) {
			r.refreshUnitRelations();
			reviewRegionAndUnits(r);
		}

		return r != null;
	}

	private Region getNextRegion() {
		if(regionsIterator == null) {
			return null;
		}

		// find next interesting region
		while(regionsIterator.hasNext()) {
			Region r = (Region) regionsIterator.next();

			if((r.units() != null) && !r.units().isEmpty()) {
				if(REGIONS_WITH_UNCONFIRMED_UNITS_ONLY) {
					// only show regions with unconfirmed units
					for(Iterator iter = r.units().iterator(); iter.hasNext();) {
						Unit u = (Unit) iter.next();

						if(!u.ordersConfirmed) {
							return r;
						}
					}
				} else {
					return r;
				}
			}
		}

		return null;
	}

	private void initInspectors() {
		inspectors = CollectionFactory.createArrayList();
		inspectors.add(ToDoInspector.getInstance());
		inspectors.add(MovementInspector.getInstance());
		inspectors.add(ShipInspector.getInstance());
	}

	/**
	 * clean up
	 */
	public void quit() {
		if(this.dispatcher != null) {
			dispatcher.removeUnitOrdersListener(this);
			dispatcher.removeSelectionListener(this);
		}

		super.quit();
	}

	/* (non-Javadoc)
	 * @see com.eressea.swing.InternationalizedDataPanel#gameDataChanged(com.eressea.event.GameDataEvent)
	 */
	public void gameDataChanged(GameDataEvent e) {
		super.gameDataChanged(e);

		// rebuild warning list
		refreshProblems();
	}

	/* (non-Javadoc)
	 * @see com.eressea.event.SelectionListener#selectionChanged(com.eressea.event.SelectionEvent)
	 */
	public void selectionChanged(SelectionEvent e) {
		// (stm) this is pretty broken
		if((e.getSource() == this) || (e.getSelectionType() == SelectionEvent.ST_REGIONS)) {
			// ignore multiple region selections
			return;
		}

		Unit u = null;

		try {
			u = (Unit) e.getActiveObject();
		} catch(ClassCastException cce) {
		}

		Region r = null;

		try {
			r = ((HasRegion) e.getActiveObject()).getRegion();
		} catch(ClassCastException cce) {
		}

		if(r == null) {
			try {
				r = (Region) e.getActiveObject();
			} catch(ClassCastException cce) {
			}
		}

		reviewRegionAndUnits(r);
		reviewObjects(u, null);
	}

	/**
	 * Updates reviews when orders have changed.
	 *
	 * @param e 
	 * @see com.eressea.event.UnitOrdersListener#unitOrdersChanged(com.eressea.event.UnitOrdersEvent)
	 */
	public void unitOrdersChanged(UnitOrdersEvent e) {
		// rebuild warning list for given unit
		reviewObjects(e.getUnit(), e.getUnit().getRegion());
	}

	private void reviewRegionAndUnits(Region r) {
		if(r == null) {
			return;
		}

		if(log.isDebugEnabled()) {
			log.debug("TaskTablePanel.reviewRegionAndUnits(" + r + ") called");
		}

		reviewObjects(null, r);

		if(r.units() == null) {
			return;
		}

		for(Iterator iter = r.units().iterator(); iter.hasNext();) {
			Unit u = (Unit) iter.next();
			reviewObjects(u, null);
		}
	}

	private void reviewObjects(Unit u, Region r) {
		for(Iterator iter = inspectors.iterator(); iter.hasNext();) {
			Inspector c = (Inspector) iter.next();
			if(r != null) {
				// remove previous problems of this unit AND the given inspector
				model.removeProblems(c, r);

				// add new problems if found
				List problems = c.reviewRegion(r);
				model.addProblems(problems);
			}

			if(u != null) {
				// remove previous problems of this unit AND the given inspector
				model.removeProblems(c, u);

				// add new problems if found
				List problems = c.reviewUnit(u);
				model.addProblems(problems);
			}
		}
	}

	private Vector getHeaderTitles() {
		Vector v = new Vector(6);
		v.add(getString("header.type"));
		v.add(getString("header.description"));
		v.add(getString("header.object"));
		v.add(getString("header.region"));
		v.add(getString("header.line"));
//		v.add(getString("header.unknown"));

		return v;
	}

	private static class TaskTableModel extends DefaultTableModel {
		/**
		 * Creates a new TaskTableModel object.
		 *
		 * @param header 
		 * @see javax.swing.table.DefaultTableModel#DefaultTableModel(Vector, int)
		 */
		public TaskTableModel(Vector header) {
			super(header, 0);
			init();
		}

		private void init() {
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.DefaultTableModel#isCellEditable(int, int)
		 */
		public boolean isCellEditable(int row, int column) {
			return false;
		}

		// TODO : find better solution!
		public void clearProblems() {
			for(int i = getRowCount() - 1; i >= 0; i--) {
				removeRow(i);
			}
		}

		/**
		 * Adds a list of problems one by one.
		 *
		 * @param p 
		 */
		public void addProblems(List p) {
			for(Iterator iter = p.iterator(); iter.hasNext();) {
				addProblem((Problem) iter.next());
			}
		}

		private static final int IMAGE_POS = 0;
		private static final int PROBLEM_POS = 1;
		private static final int OBJECT_POS = 2;
		private static final int REGION_POS = 3;
		private static final int LINE_POS = 4;
//		private static final int UNKNOWN_POS = 5;

		/**
		 * Add a problem to this model.
		 *
		 * @param p 
		 */
		public void addProblem(Problem p) {
			Vector v = new Vector(6);
			v.add(Integer.toString(p.getType()));
			v.add(p);
			v.add(p.getObject());
			v.add(p.getObject().getRegion());
			v.add((p.getLine() < 1) ? "" : Integer.toString(p.getLine()));
//			v.add("");
			
			this.addRow(v);
		}

		// TODO : find better solution!
		public void removeProblems(Inspector inspector, Object source) {
			Vector dataVector = getDataVector();

			for(int i = getRowCount() - 1; i >= 0; i--) {
				Vector v = (Vector) dataVector.get(i);
				Problem p = (Problem) v.get(PROBLEM_POS);

				// Inspector and region: only non unit objects will be removed
				if(p.getInspector().equals(inspector) && p.getSource().equals(source)) {
					removeRow(i);
				}
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

			defaultTranslations.put("header.type", "Type");
			defaultTranslations.put("header.description", "Description");
			defaultTranslations.put("header.object", "Object");
			defaultTranslations.put("header.line", "Line");
			defaultTranslations.put("header.region", "Region");
//			defaultTranslations.put("header.unknown", "!");
		}

		return defaultTranslations;
	}
}
