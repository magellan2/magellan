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

import com.eressea.util.Taggable;

/**
 * FIXME: Wrong description!!!
 * A comparator imposing an ordering on Unit objects by comparing the factions they belong to.
 * 
 * <p>
 * Note: this comparator imposes orderings that are inconsistent with equals.
 * </p>
 * 
 * <p>
 * In order to overcome the inconsistency with equals this comparator allows the introduction of a
 * sub-comparator which is applied in cases of equality. I.e. if the two compared units belong to
 * the same faction and they would be regarded as equal by this comparator, instead of 0 the
 * result of the sub-comparator's comparison is returned.
 * </p>
 */
public class TaggableComparator implements Comparator {

	public final static TaggableComparator DEFAULT_COMPARATOR = new TaggableComparator(null);

	protected Comparator subCmp = null;
	protected String tagToCompare = null;


	public static String getLabel(Taggable o1) {
		return o1.getTag(DEFAULT_COMPARATOR.tagToCompare);
	}


	/**
	 * Creates a new TagBasedComparatorComparator object with the default tag "ejcTagBasedComparatorTag"
	 *
	 * @param subComparator the comparator used to compare the Tagged tags if this one thinks they are equal
	 */
	public TaggableComparator(Comparator subComparator) {
		this(subComparator,"ejcTaggableComparator");
	}

	/**
	 * Creates a new TagBasedComparatorComparator object with the default tag "ejcTagBasedComparatorTag"
	 *
	 * @param subComparator the comparator used to compare the Tagged tags if this one thinks they are equal
	 * @param the tag to compare the two Tagged objects
	 */
	private TaggableComparator(Comparator subComparator, String tag) {
		subCmp = subComparator;
		tagToCompare = tag;
	}

	/**
	 * Compares its two arguments for order according to the factions they belong to.
	 *
	 * @param o1 TODO: DOCUMENT ME!
	 * @param o2 TODO: DOCUMENT ME!
	 *
	 * @return the result of the faction comparator's comparison of <tt>o1</tt>'s and <tt>o2</tt>.
	 * 		   If both belong to the same faction and a sub-comparator was specified, the result
	 * 		   that sub-comparator's comparison is returned.
	 */
	public int compare(Object o1, Object o2) {
		String t1 = ((Taggable) o1).getTag(tagToCompare);
		String t2 = ((Taggable) o2).getTag(tagToCompare);

		int retVal = 0;
		if(t1 == null) {
			//retVal (t2 == null) ?  0 : t2.compareTo(t1);
			retVal = (t2 == null) ?  0 : 1;
		} else {
			retVal = (t2 == null) ? -1 : t1.compareTo(t2);
		}
		return (retVal == 0 && subCmp != null) ? subCmp.compare(o1,o2) : retVal; 
	}

	/**
	 * Checks the Object <tt>o</tt> for equality.
	 *
	 * @param o1 TODO: DOCUMENT ME!
	 *
	 * @return <tt>false</tt>
	 */
	public boolean equals(Object o1) {
		return false;
	}
}
