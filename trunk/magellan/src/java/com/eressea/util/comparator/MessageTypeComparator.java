// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Ulrich Küster, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.util.comparator;

import java.util.Comparator;

import com.eressea.Message;
import com.eressea.rules.MessageType;

/**
 * A comparator imposing an ordering on Message objects by comparing
 * their types.
 * <p>Note: this comparator imposes orderings that are inconsistent with
 * equals. This is the case when neither of the messages has a valid
 * type set.</p>
 */
public class MessageTypeComparator implements Comparator {
	protected Comparator typeCmp = null;

	/**
	 * Creates a new MessageTypeComparator object.
	 * @param typeComparator the comparator applied to compare the
	 * message types.
	 */
	public MessageTypeComparator (Comparator typeComparator) {
		if (typeComparator != null) {
			typeCmp = typeComparator;
		} else {
			throw new IllegalArgumentException("MessageTypeComparator: invalid type comparator specified");
		}
	}

	/**
	 * Compares its two arguments for order according to their types
	 * @returns the result specified message type comparator.
	 */
	public int compare(Object o1, Object o2) {
		int retVal = 0;

		MessageType t1 = ((Message)o1).getType();
		MessageType t2 = ((Message)o2).getType();

		if (t1 == null) {
			if (t2 == null) {
				retVal = 0;
			} else {
				retVal = Integer.MAX_VALUE;
			}
		} else {
			if (t2 == null) {
				retVal = Integer.MIN_VALUE;
			} else {
				retVal = typeCmp.compare(t1, t2);
			}
		}

		return retVal;
	}

	/**
	 * Checks the Object <tt>o</tt> for equality.
	 * @returns <tt>false</tt>
	 */
	public boolean equals(Object o1) {
		return false;
	}
}