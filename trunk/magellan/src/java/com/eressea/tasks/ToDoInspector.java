
package com.eressea.tasks;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.eressea.Unit;
import com.eressea.util.CollectionFactory;

/** 
 * A Inspector inspects the given resource (TODO: Unit, region or whole gamedata?) 
 * and returns a list of problems;
 */
public class ToDoInspector extends AbstractInspector implements Inspector {
	public final static ToDoInspector INSPECTOR = new ToDoInspector();

	public static ToDoInspector getInstance() {
		return INSPECTOR;
	}

	protected ToDoInspector() {
	}

	public List reviewUnit(Unit u, int type) {
		if(u==null || u.ordersAreNull()) {
			return Collections.EMPTY_LIST;
		}
		
		if(type != Problem.WARNING) {
			return Collections.EMPTY_LIST;
		}
		
		List problems = CollectionFactory.createArrayList(2);

		int line=0;
		for(Iterator iter = u.getOrders().iterator(); iter.hasNext(); ) {
			line++;
			String order = ((String) iter.next()).trim();
			if(order.startsWith("//")) {
				order=order.substring(2).trim();
				if(order.startsWith("TODO")) {
					problems.add(new CriticizedInformation(u,u, this, order, line));
				}
			} else {
				if(order.startsWith(";")) {
					order=order.substring(1).trim();
					if(order.startsWith("TODO")) {
						problems.add(new CriticizedInformation(u,u, this, order, line));
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
