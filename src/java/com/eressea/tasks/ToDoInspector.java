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

package com.eressea.tasks;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.eressea.Unit;
import com.eressea.util.CollectionFactory;

/**
 * An Inspector inspects the given resource and returns a list of problems.
 */
public class ToDoInspector extends AbstractInspector implements Inspector {
	/** TODO: DOCUMENT ME! */
	public static final ToDoInspector INSPECTOR = new ToDoInspector();

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static ToDoInspector getInstance() {
		return INSPECTOR;
	}

	protected ToDoInspector() {
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param u TODO: DOCUMENT ME!
	 * @param type TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public List reviewUnit(Unit u, int type) {
		if((u == null) || u.ordersAreNull()) {
			return Collections.EMPTY_LIST;
		}

		if(type != Problem.WARNING) {
			return Collections.EMPTY_LIST;
		}

		List problems = CollectionFactory.createArrayList(2);

		int line = 0;

		for(Iterator iter = u.getOrders().iterator(); iter.hasNext();) {
			line++;

			String order = ((String) iter.next()).trim();

			if(order.startsWith("//")) {
				order = order.substring(2).trim();

				if(order.startsWith("TODO")) {
					problems.add(new CriticizedInformation(u, u, this, order, line));
				}
			} else {
				if(order.startsWith(";")) {
					order = order.substring(1).trim();

					if(order.startsWith("TODO")) {
						problems.add(new CriticizedInformation(u, u, this, order, line));
					}
				}
			}
		}

		if(problems.isEmpty()) {
			return Collections.EMPTY_LIST;
		} else {
			return problems;
		}
	}
}
