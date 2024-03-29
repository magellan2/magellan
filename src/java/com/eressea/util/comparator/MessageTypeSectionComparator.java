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

package com.eressea.util.comparator;

import java.util.Comparator;

import com.eressea.rules.MessageType;

/**
 * A comparator imposing an ordering on MessageType objects by comparing the sections (categories)
 * they belong to.
 * 
 * <p>
 * Note: this comparator imposes orderings that are inconsistent with equals.
 * </p>
 * 
 * <p>
 * In order to overcome the inconsistency with equals this comparator allows the introduction of a
 * sub-comparator which is applied in cases of equality. I.e. if the two compared messages belong
 * to the same section and they would be regarded as equal by this comparator, instead of 0 the
 * result of the sub-comparator's comparison is returned.
 * </p>
 */
public class MessageTypeSectionComparator implements Comparator {
	protected Comparator sameSectionSubCmp = null;

	/**
	 * Creates a new MessageTypeSectionComparator object.
	 *
	 * @param sameSectionSubComparator if two messages belonging to the same section are compared,
	 * 		  this sub-comparator is applied if it is not <tt>null</tt>.
	 */
	public MessageTypeSectionComparator(Comparator sameSectionSubComparator) {
		sameSectionSubCmp = sameSectionSubComparator;
	}

	/**
	 * Compares its two arguments for order according to the sections they belong to.
	 *
	 * @param o1 TODO: DOCUMENT ME!
	 * @param o2 TODO: DOCUMENT ME!
	 *
	 * @return the result of the String.compareTo() method applied to <tt>o1</tt>'s and
	 * 		   <tt>o2</tt>. If both belong to the same section and a sub-comparator was specified,
	 * 		   the result that sub-comparator's comparison is returned.
	 */
	public int compare(Object o1, Object o2) {
		String s1 = ((MessageType) o1).getSection();
		String s2 = ((MessageType) o2).getSection();

		if(s1 == null) {
			return (s2 == null) ? 0 : 1;
		} else {
			if(s2 == null) {
				return -1;
			} else {
				int retVal = s1.compareTo(s2);

				return ((retVal == 0) && (sameSectionSubCmp != null))
					   ? sameSectionSubCmp.compare(o1, o2) : retVal;
			}
		}
	}
}
