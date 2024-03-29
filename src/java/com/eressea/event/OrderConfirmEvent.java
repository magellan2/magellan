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

package com.eressea.event;

import java.util.Collection;
import java.util.Iterator;

import com.eressea.Unit;

/**
 * An event indicating that the order confirmation status of one or more units has changed.
 *
 * @see OrderConfirmListener
 * @see EventDispatcher
 */
public class OrderConfirmEvent extends TimeStampedEvent {
	private Collection units;
	private boolean changedToUnConfirmed = false;

	/**
	 * Constructs a new order confirmation event.
	 *
	 * @param source the object issuing the event.
	 * @param units the units which order confirmation status was modified.
	 */
	public OrderConfirmEvent(Object source, Collection units) {
		super(source);
		this.units = units;
		this.changedToUnConfirmed = this.calcChangedToUnConfirmed();
	}

	/**
	 * Returns the units affected by the event.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Collection getUnits() {
		return units;
	}
	
	/**
	 * BUG in JTree. UI calculates the bounding not correct
	 * if text is not bold when init
	 * Overviewpanel - tree calls updateUI if one or more units
	 * had changed the order confirm to yes
	 * this is here calculated 
	 * @return true, if one or more units are confirmed, else false
	 * @author Fiete
	 */
	private boolean calcChangedToUnConfirmed(){
		if (units==null){
			return false;
		}
		for (Iterator iter = this.units.iterator();iter.hasNext();){
			Unit u = (Unit)iter.next();
			if (!u.ordersConfirmed){return true;}
		}
		return false;
	}
	
	public boolean changedToUnConfirmed(){
		return this.changedToUnConfirmed;
	}
	
}
