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

/*
 * DefaultReplacerFactory.java
 *
 * Created on 20. Mai 2002, 15:49
 */
package com.eressea.util.replacers;

import java.util.HashMap;
import java.util.Map;

/**
 * DOCUMENT ME!
 *
 * @author Andreas
 * @version
 */
public class DefaultReplacerFactory implements ReplacerFactory {
	protected Map replacers;

	/**
	 * Creates new DefaultReplacerFactory
	 */
	public DefaultReplacerFactory() {
		replacers = new HashMap();
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param name TODO: DOCUMENT ME!
	 * @param repClass TODO: DOCUMENT ME!
	 * @param args TODO: DOCUMENT ME!
	 */
	public void putReplacer(String name, Class repClass, Object args[]) {
		putReplacer(name, repClass);

		if(args != null) {
			setArguments(name, args);
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param name TODO: DOCUMENT ME!
	 * @param repClass TODO: DOCUMENT ME!
	 * @param arg TODO: DOCUMENT ME!
	 */
	public void putReplacer(String name, Class repClass, Object arg) {
		putReplacer(name, repClass);

		if(arg != null) {
			setArguments(name, arg);
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param name TODO: DOCUMENT ME!
	 * @param repClass TODO: DOCUMENT ME!
	 */
	public void putReplacer(String name, Class repClass) {
		replacers.put(name, new ReplacerInfo(repClass));
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param name TODO: DOCUMENT ME!
	 * @param arg TODO: DOCUMENT ME!
	 */
	public void setArguments(String name, Object arg) {
		Object args[] = new Object[1];
		args[0] = arg;
		setArguments(name, args);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param name TODO: DOCUMENT ME!
	 * @param args TODO: DOCUMENT ME!
	 */
	public void setArguments(String name, Object args[]) {
		Object argCopy[] = new Object[args.length];
		System.arraycopy(args, 0, argCopy, 0, argCopy.length);
		((ReplacerInfo) replacers.get(name)).setArgs(argCopy);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param name TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Replacer createReplacer(String name) {
		ReplacerInfo repInfo = (ReplacerInfo) replacers.get(name);

		try {
			if(repInfo.args == null) {
				return (Replacer) repInfo.replacerClass.newInstance();
			}

			return (Replacer) repInfo.replacerClass.getConstructor(repInfo.argClasses)
												   .newInstance(repInfo.args);
		} catch(Exception exc) {
		}

		return null;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param name TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean isReplacer(String name) {
		return replacers.containsKey(name);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public java.util.Set getReplacers() {
		return replacers.keySet();
	}

	protected class ReplacerInfo {
		Class   replacerClass;
		Object  args[]		 = null;
		Class   argClasses[] = null;

		/**
		 * Creates a new ReplacerInfo object.
		 *
		 * @param repClass TODO: DOCUMENT ME!
		 */
		public ReplacerInfo(Class repClass) {
			replacerClass = repClass;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param arg TODO: DOCUMENT ME!
		 */
		public void setArgs(Object arg[]) {
			args	   = arg;
			argClasses = new Class[args.length];

			for(int i = 0; i < argClasses.length; i++) {
				if(args[i] instanceof Integer) {
					argClasses[i] = int.class;
				} else if(args[i] instanceof Boolean) {
					argClasses[i] = boolean.class;
				} else {
					argClasses[i] = args[i].getClass();
				}
			}
		}
	}
}
