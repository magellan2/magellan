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

package com.eressea.util.replacers;

import java.lang.reflect.Field;

import com.eressea.Region;

/**
 * DOCUMENT ME!
 *
 * @author unknown
 * @version
 */
public class RegionFieldReplacer extends AbstractRegionReplacer {
	/** TODO: DOCUMENT ME! */
	public static final int MODE_ALL = 0;

	/** TODO: DOCUMENT ME! */
	public static final int MODE_NON_NEGATIVE = 1;

	/** TODO: DOCUMENT ME! */
	public static final int MODE_POSITIVE = 2;
	protected Field		    field;
	protected int		    mode;

	/**
	 * Creates a new RegionFieldReplacer object.
	 *
	 * @param field TODO: DOCUMENT ME!
	 * @param mode TODO: DOCUMENT ME!
	 *
	 * @throws RuntimeException TODO: DOCUMENT ME!
	 */
	public RegionFieldReplacer(String field, int mode) {
		try {
			this.field = Class.forName("com.eressea.Region").getField(field);
		} catch(Exception exc) {
			throw new RuntimeException("Error retrieving region field " +
									   field);
		}

		this.mode = mode;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param r TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Object getRegionReplacement(Region r) {
		try {
			Object o = field.get(r);

			if(o != null) {
				if(!(o instanceof Number)) {
					return o;
				}

				Number n = (Number) o;

				switch(mode) {
				case MODE_ALL:
					return o;

				case MODE_NON_NEGATIVE:

					if(n.doubleValue() >= 0) {
						return o;
					}

					break;

				case MODE_POSITIVE:

					if(n.doubleValue() > 0) {
						return o;
					}

					break;

				default:
					break;
				}
			}
		} catch(Exception exc) {
		}

		return null;
	}
}
