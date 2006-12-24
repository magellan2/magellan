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

package com.eressea.util.replacers;

import java.lang.reflect.Method;
import java.util.Map;

import com.eressea.Region;
import com.eressea.util.CollectionFactory;

/**
 * DOCUMENT ME!
 *
 * @author unknown
 * @version
 */
public class RegionMethodReplacer extends AbstractRegionReplacer {
	/** TODO: DOCUMENT ME! */
	public static final int MODE_ALL = 0;

	/** TODO: DOCUMENT ME! */
	public static final int MODE_NON_NEGATIVE = 1;

	/** TODO: DOCUMENT ME! */
	public static final int MODE_POSITIVE = 2;
	protected Method method;
	protected int mode;

	/**
	 * Creates a new RegionMethodReplacer object.
	 *
	 * @param method TODO: DOCUMENT ME!
	 * @param mode TODO: DOCUMENT ME!
	 *
	 * @throws RuntimeException TODO: DOCUMENT ME!
	 */
	public RegionMethodReplacer(String method, int mode) {
		try {
			this.method = Class.forName("com.eressea.Region").getMethod(method, null);
		} catch(Exception exc) {
			throw new RuntimeException("Error retrieving region method " + method);
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
			Object o = method.invoke(r, null);

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
	public String getDescription() {
		String s = super.getDescription();

		if(s == null) {
			if(method != null) {
				return method.getName();
			}

			return "no desc";
		} else {
			if(method != null) {
				return s + " " + method.getName();
			}

			return s + " no further desc";
		}
	}
}
