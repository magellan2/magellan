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
 * $Id$
 */

package com.eressea;

import java.util.Map;

import com.eressea.util.CollectionFactory;
import com.eressea.util.EresseaOrderConstants;
import com.eressea.util.IDBaseConverter;

/**
 * A class for identifying unit objects through an integer. This class makes
 * equivalent assumptions about the representation of the integer as the
 * EntityID class. It also provides additional convenience methods and
 * knowledge about TEMP unit ids (which are represented as negative integers).
 */
public class UnitID extends EntityID {
	/**
	 * Constructs a new UnitID object based on an Integer object created from
	 * the specified int.
	 *
	 * @param i TODO: DOCUMENT ME!
	 */
	protected UnitID(int i) {
		super(i);
	}

	protected UnitID(Integer i) {
		super(i);
	}

	/** a static cache to use this class as flyweight factory */
	private static Map idMap = CollectionFactory.createHashMap();

	/**
	 * Constructs a new UnitID object based on the specified Integer.
	 *
	 * @param o TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 *
	 * @throws NullPointerException TODO: DOCUMENT ME!
	 */
	public static UnitID createUnitID(Integer o) {
		if(o == null) {
			throw new NullPointerException();
		}

		UnitID id = (UnitID) idMap.get(o);

		if(id == null) {
			id = new UnitID(o);
			idMap.put(o, id);
		}

		return id;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param i TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static UnitID createUnitID(int i) {
		return createUnitID(new Integer(i));
	}

	/**
	 * Constructs a new UnitID object by parsing the specified string for an
	 * integer in the default representation of class IDBaseConverter.
	 *
	 * @param s TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static UnitID createUnitID(String s) {
		return createUnitID(s, IDBaseConverter.getBase());
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param s TODO: DOCUMENT ME!
	 * @param radix TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static UnitID createUnitID(String s, int radix) {
		return createUnitID(valueOf(s, radix));
	}

	/**
	 * Returns a String representation of this UnitID. The radix of the output
	 * depends on the default set in the IDBaseConverter class. This method is
	 * not TEMP id aware, i.e. negative ids are returned as the string
	 * representation of the absolute value but without a 'TEMP' prefix.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String toString() {
		return IDBaseConverter.toString(Math.abs(this.intValue()));
	}

	/**
	 * Indicates that this UnitID is equal to some other object.
	 *
	 * @param o TODO: DOCUMENT ME!
	 *
	 * @return true, if o is an instance of UnitID and the integer values of
	 * 		   this and the specfied object o are equal.
	 */
	public boolean equals(Object o) {
		return (this == o) ||
			   (o instanceof EntityID && (id == ((EntityID) o).id));
	}

	/**
	 * Imposes a natural ordering on UnitID objects based on the natural
	 * ordering of the absolute values of the underlying integers.
	 *
	 * @param o TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int compareTo(Object o) {
		return Math.abs(this.intValue()) - Math.abs(((EntityID) o).intValue());
	}

	/**
	 * Returns the integer contained in the specified string with the specified
	 * radix. This method is TEMP id aware, i.e. the string "TEMP 909" would
	 * return an Integer object with the numerical value -909.
	 *
	 * @param s TODO: DOCUMENT ME!
	 * @param radix TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 *
	 * @throws NumberFormatException TODO: DOCUMENT ME!
	 */
	private static Integer valueOf(String s, int radix) {
		s = s.trim().replace('\t', ' ');

		int blankPos = s.indexOf(" ");

		if(blankPos == -1) {
			return Integer.valueOf(s, radix);
		} else {
			String part1 = s.substring(0, blankPos);

			if(part1.equalsIgnoreCase(com.eressea.util.Translations.getOrderTranslation(EresseaOrderConstants.O_TEMP))) {
				return new Integer(-1 * Integer.parseInt(s.substring(blankPos)
														  .trim(), radix));
			} else {
				throw new NumberFormatException("UnitID: unable to parse id " +
												s);
			}
		}
	}
}
