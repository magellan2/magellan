// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// ===


package com.eressea.swing.tree;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.List;

import com.eressea.util.CollectionFactory;

/**
 *
 * @author  Andreas
 * @version
 */
public abstract class AbstractNodeWrapperDrawPolicy implements NodeWrapperDrawPolicy {

	protected List nodes;
	protected ReferenceQueue refQueue;
	protected boolean inUpdate = false;
			
	/** Creates new NodeWrapperPreferencesDialog */
	public AbstractNodeWrapperDrawPolicy() {
		nodes = CollectionFactory.createLinkedList();
		refQueue = new ReferenceQueue();
	}		
	
	public void addCellObject(CellObject co) {
		clearNodes();
		nodes.add(new WeakReference(co,refQueue));		
	}
	
	protected void clearNodes() {
		if (inUpdate) {
			return;
		}
		Object o = null;
		do{
			o = refQueue.poll();
			if (o!=null) {
				nodes.remove(o);
			}
		}while(o!=null);
	}
	
	public void applyPreferences() {
		clearNodes();
		inUpdate = true;
		Iterator it=nodes.iterator();
		try{ // because of deletion of weak ref
			while(it.hasNext()) try { // - || -
				CellObject co=(CellObject)((WeakReference)it.next()).get();
				co.propertiesChanged();
			}catch(Exception inner) {
				try {it.remove();} // remove the broken weak reference
				catch(Exception exc2) {}
			}
		}catch(Exception outer) {}
		inUpdate = false;
	}
	
}
