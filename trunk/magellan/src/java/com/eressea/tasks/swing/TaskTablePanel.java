// ===
// Copyright (C) 2000-2003 Roger Butenuth, Andreas Gampe, Stefan Götz, Ulrich Küster, Sebastian Pappert, Ilja Pavkovic, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.tasks.swing;

import java.awt.*;
import java.awt.event.*;

import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.table.*;

import com.eressea.*;

import com.eressea.event.*;

import com.eressea.swing.InternationalizedDataPanel;
import com.eressea.swing.table.*;

import com.eressea.tasks.*;

import com.eressea.util.*;
import com.eressea.util.logging.Logger;


/**
 * A panel for showing reviews about unit, region and/or gamedata.
 */
public class TaskTablePanel extends InternationalizedDataPanel implements UnitOrdersListener,SelectionListener {
	private final static Logger log = Logger.getInstance(TaskTablePanel.class);

	protected TaskTableModel model;
	protected JTable table;
	protected List inspectors;
	
	public TaskTablePanel(EventDispatcher d, GameData initData, Properties p) {
		super(d, initData, p);
		init(d);
	}
	
	private void init(EventDispatcher d) {
		d.addUnitOrdersListener(this);
		d.addSelectionListener(this);

		initGUI();
		initInspectors();
	}

	private void initGUI() {
		model = new TaskTableModel();
		// table = new JTable(model);
		
		TableSorter sorter = new TableSorter(model);
		table = new JTable(sorter);
		sorter.addMouseListenerToHeaderInTable(table);

		// disallow reordering of headers
		table.getTableHeader().setReorderingAllowed(false);

		// set row selection to single selection
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Row 0 ("I"): smallest possible, not resizeable
		table.getColumnModel().getColumn(0).setResizable(false);
		table.getColumnModel().getColumn(0).setMaxWidth(table.getColumnModel().getColumn(0).getMinWidth());

		// Row 1 ("!"): smallest possible, not resizeable
		table.getColumnModel().getColumn(1).setResizable(false);
		table.getColumnModel().getColumn(1).setMaxWidth(table.getColumnModel().getColumn(1).getMinWidth());

		// react on double clicks on a row
		table.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					if(e.getClickCount() == 2) {
						JTable target = (JTable)e.getSource();
						int row = target.getSelectedRow();
						log.debug("TaskTablePanel: Double click on row "+row);
					}
             }
			});
		
		this.add(new JScrollPane(table));

	}
	
	private void initInspectors() {
		inspectors = CollectionFactory.createArrayList();
		inspectors.add(ToDoInspector.getInstance());
		inspectors.add(MovementInspector.getInstance());
		inspectors.add(ShipInspector.getInstance());

	}

	public void quit() {
		if (this.dispatcher != null) {
			dispatcher.removeUnitOrdersListener(this);
			dispatcher.removeSelectionListener(this);
		}
		super.quit();
	}
	
	public void gameDataChanged(GameDataEvent e) {
		super.gameDataChanged(e);
		// TODO: delete warning list
	}

	public void selectionChanged(SelectionEvent e) {
		if(e.getSelectionType() == SelectionEvent.ST_REGIONS) {
			// ignore multiple region selections
			return;
		}
		Unit u = null;
		try {
			u = (Unit) e.getActiveObject();
		} catch(ClassCastException cce) {}

		Region r = null;
		try {
			r = ((HasRegion) e.getActiveObject()).getRegion();
		} catch(ClassCastException cce) {}
		if(r == null) {
			try {
				r = (Region) e.getActiveObject();
			} catch(ClassCastException cce) {}
		}

		reviewObjects(u,r);
	}

	public void unitOrdersChanged(UnitOrdersEvent e) {
		// TODO: rebuild warning list for given unit
		reviewObjects(e.getUnit(), e.getUnit().getRegion());
	}

	private void reviewObjects(Unit u, Region r) {
		for(Iterator iter = inspectors.iterator(); iter.hasNext(); ) {
			Inspector c = (Inspector) iter.next();
			if(r != null) {
				// remove previous problems of this unit AND the given inspector
				model.removeProblems(c,r);
				// add new problems if found
				model.addProblems(c.reviewRegion(r));
			}
			if(u != null) {
				// remove previous problems of this unit AND the given inspector
				model.removeProblems(c,u);
				// add new problems if found
				model.addProblems(c.reviewUnit(u));
			}
		}
	}

	private static class TaskTableModel extends DefaultTableModel {
		public TaskTableModel() {
			super(getHeaderTitles(),0);
			init();
		}
		
		private void init() {

		}

		private static Vector getHeaderTitles() {
			Vector v = new Vector(6);
			v.add("I");
			v.add("!");
			v.add("Description");
			v.add("Object");
			v.add("Line");
			v.add("Region");
			return v;
		}

		// no cell is editable right now
		public boolean isCellEditable(int row, int column) {
			return false;
		}

		public void addProblems(List p) {
			for(Iterator iter = p.iterator(); iter.hasNext(); ) {
				addProblem((Problem) iter.next());
			}
		}

			
		private final static int PROBLEM_POS = 2;
		public void addProblem(Problem p) {
			
			Vector v = new Vector(6);
			v.add(Integer.toString(p.getType()));
			v.add("");
			v.add(p);
			v.add(p.getObject());
			v.add(p.getLine() < 1 ? "" : Integer.toString(p.getLine()));
			v.add(p.getObject().getRegion());
			
			this.addRow(v);
		}

		// TODO : find better solution!
		public void removeProblems(Inspector inspector, Object source) {
			Vector dataVector = getDataVector();
			for(int i=getRowCount()-1 ;i>=0;i--) {
				Vector v = (Vector) dataVector.get(i);
				Problem p= (Problem) v.get(PROBLEM_POS);
				// Inspector and region: only non unit objects will be removed
				if(p.getInspector().equals(inspector) && p.getSource().equals(source)) {
					removeRow(i);
				}
			}
		}
    }
}

