// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Ulrich Küster, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.util.comparator;


import java.util.Comparator;

import com.eressea.Faction;
import com.eressea.swing.FactionStatsPanel;
import com.eressea.util.Translations;


/**
 * A comparator imposing an ordering on <tt>Faction</tt> objects by
 * comparing the trust levels.
 * <p>Note: this comparator imposes orderings that are inconsistent with
 * equals.</p>
 * <p>In order to overcome the inconsistency with equals this comparator
 * allows the introduction of a sub-comparator which is applied in cases
 * of equality. I.e. if the two compared factions belong to the same
 * trust level and they would be regarded as equal by this comparator,
 * instead of 0 the result of the sub-comparator's comparison is
 * returned.</p>
 */
public class FactionTrustComparator implements Comparator {
	protected Comparator sameTrustSubCmp = null;

	/**
	 * Creates a new <tt>FactionTrustComparator</tt> object.
	 * @param sameFactionSubComparator if two factions with the
	 * same trust level are compared, this sub-comparator is applied
	 * if it is not <tt>null</tt>.
	 */
	public FactionTrustComparator (Comparator sameFactionSubComparator) {
		sameTrustSubCmp = sameFactionSubComparator;
	}


	// 	public final static FactionTrustComparator DEFAULT_COMPARATOR = new FactionTrustComparator(new NameComparator(new IDComparator()));
	public final static FactionTrustComparator DEFAULT_COMPARATOR = new FactionTrustComparator(null);

	public final static int PRIVILEGED = Faction.TL_PRIVILEGED;
	public final static int ALLIED     = Faction.TL_DEFAULT+1;
	public final static int DEFAULT    = Faction.TL_DEFAULT;
	public final static int ENEMY      = Faction.TL_DEFAULT-1;

	/**
	 * Compares its two arguments for order with regard to their
	 * trust levels.
	 * @returns the difference of <tt>o2</tt>'s and <tt>o1</tt>'s
	 *	trust level values. If this is 0 and a sub-comparator is
	 *	specified, the result of that sub-comparator's comparison
	 *	is returned.
	 */
	public int compare(Object o1, Object o2) {
		int ret = 0;
		int t1 = getTrustLevel(((Faction)o1).trustLevel);
		int t2 = getTrustLevel(((Faction)o2).trustLevel);

		return (t1 == t2 && sameTrustSubCmp != null) ? sameTrustSubCmp.compare(o1,o2): t2 - t1;
	}

	/**
	 * Checks the Object <tt>o</tt> for equality.
	 * @returns <tt>false</tt>
	 */
	public boolean equals(Object o1) {
		return false;
	}

	public static int getTrustLevel(Faction f) {
		return getTrustLevel(f.trustLevel);
	}
	
	public static int getTrustLevel(int trustLevel) {
		if(trustLevel >= PRIVILEGED) return PRIVILEGED;
		if(trustLevel >= ALLIED)     return ALLIED;
		if(trustLevel >= DEFAULT)    return DEFAULT;
		return ENEMY;
	}

	public static String getTrustLevelLabel(int level) {
		// TODO(pavkovic): move functions and translations to a suitable position
		String nodeLabel = "";
		switch(getTrustLevel(level)) {
		case FactionTrustComparator.PRIVILEGED:
			nodeLabel = getString("node.trust.privileged");
			break;
		case FactionTrustComparator.ALLIED:
			nodeLabel = getString("node.trust.allied");
			break;
		case FactionTrustComparator.DEFAULT:
			nodeLabel = getString("node.trust.standard");
			break;
		case FactionTrustComparator.ENEMY:
			nodeLabel = getString("node.trust.enemy");
			break;
		}
		return nodeLabel;
	}

	private static String getString(String key) {
		return Translations.getTranslation(FactionStatsPanel.class,key);
	}
}
