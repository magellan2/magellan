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

package com.eressea.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Window;

import javax.swing.UIManager;

import com.eressea.util.logging.Logger;

/**
 * This class provides static functions for jvm specific bullshit (e.g. changed focus system  from
 * jvm 1.3.x to 1.4.x It also checks for necessary systems, e.g. XML
 */
public class JVMUtilities {
	private static final Logger log = Logger.getInstance(JVMUtilities.class);

	/**
	 * this is a helper function to catch a jvm 1.4.2_01 bug
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static final Color getTreeSelectionBorderColor() {
		try {
			return (Color) UIManager.getDefaults().get("Tree.selectionBorderColor");
		} catch(ClassCastException cce) {
			log.warn("JVM 1.4.2_01 bug! Switching to color black!");

			if(log.isDebugEnabled()) {
				log.debug("JVM 1.4.2_01 bug: class " +
						  UIManager.getDefaults().get("Tree.selectionBorderColor").getClass()
								   .getName() + " instead of Color!");
			}

			return Color.black;
		}
	}

	/**
	 * Request the focus in the current window.
	 *
	 * @param aObj TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static final boolean requestFocusInWindow(Component aObj) {
		try {
			Object result = aObj.getClass().getMethod("requestFocusInWindow", new Class[] {  })
								.invoke(aObj, new Object[] {  });

			if(log.isDebugEnabled()) {
				log.debug("JVMUtitities : successfully called Component.requestFocusInWindow()!");
			}

			return ((Boolean) result).booleanValue();
		} catch(java.lang.NoSuchMethodException ex) {
		} catch(java.lang.IllegalAccessException ex) {
		} catch(java.lang.reflect.InvocationTargetException ex) {
		} catch(ClassCastException ex) {
			if(log.isDebugEnabled()) {
				log.debug(ex);
			}
		}

		// fallback for java < 1.4
		aObj.requestFocus();

		return false;
	}

	/**
	 * This function calls Component.setFocusableWindowState (in java >= 1.4) to  keep GUI
	 * consistent with java 1.3.x
	 *
	 * @param aObj TODO: DOCUMENT ME!
	 * @param aFlag TODO: DOCUMENT ME!
	 */
	public static final void setFocusableWindowState(Window aObj, boolean aFlag) {
		try {
			//try to call setFocusableWindowState (true) on java 1.4 while staying compatible with Java 1.3
			aObj.getClass().getMethod("setFocusableWindowState", new Class[] { Boolean.TYPE })
				.invoke(aObj, new Object[] { aFlag ? Boolean.TRUE : Boolean.FALSE });

			if(log.isDebugEnabled()) {
				log.debug("JVMUtitities : successfully called Component.setFocusableWindowState(" +
						  aFlag + ")!");
			}
		} catch(java.lang.NoSuchMethodException ex) {
		} catch(java.lang.IllegalAccessException ex) {
		} catch(java.lang.reflect.InvocationTargetException ex) {
		}
	}

	/** TODO: DOCUMENT ME! */
	public static final int UNKNOWN = -1;

	/** TODO: DOCUMENT ME! */
	public static final int NORMAL = 0;

	/** TODO: DOCUMENT ME! */
	public static final int ICONIFIED = 1;

	/** TODO: DOCUMENT ME! */
	public static final int MAXIMIZED_HORIZ = 2;

	/** TODO: DOCUMENT ME! */
	public static final int MAXIMIZED_VERT = 4;

	/** TODO: DOCUMENT ME! */
	public static final int MAXIMIZED_BOTH = MAXIMIZED_VERT | MAXIMIZED_HORIZ;

	/**
	 * This function calls Frame.getExtendedState (in java >= 1.4) to  keep GUI consistent with
	 * java 1.3.x
	 *
	 * @param aObj TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static final int getExtendedState(Frame aObj) {
		try {
			Object result = aObj.getClass().getMethod("getExtendedState", new Class[] {  }).invoke(aObj,
																								   new Object[] {
																									   
																								   });

			if(log.isDebugEnabled()) {
				log.debug("JVMUtitities : successfully called Window.getExtendedState()!");
			}

			return ((Integer) result).intValue();
		} catch(java.lang.NoSuchMethodException ex) {
		} catch(java.lang.IllegalAccessException ex) {
		} catch(java.lang.reflect.InvocationTargetException ex) {
		} catch(ClassCastException ex) {
			if(log.isDebugEnabled()) {
				log.debug(ex);
			}
		}

		return UNKNOWN;
	}

	/**
	 * This function calls Frame.setExtendedState (in java >= 1.4) to  keep GUI consistent with
	 * java 1.3.x
	 *
	 * @param aObj TODO: DOCUMENT ME!
	 * @param state TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static final boolean setExtendedState(Frame aObj, int state) {
		if(state == UNKNOWN) {
			return false;
		}

		try {
			aObj.getClass().getMethod("setExtendedState", new Class[] { Integer.TYPE }).invoke(aObj,
																							   new Object[] {
																								   new Integer(state)
																							   });

			if(log.isDebugEnabled()) {
				log.debug("JVMUtitities : successfully called Window.setExtendedState(" + state +
						  ")!");
			}

			return true;
		} catch(java.lang.NoSuchMethodException ex) {
		} catch(java.lang.IllegalAccessException ex) {
		} catch(java.lang.reflect.InvocationTargetException ex) {
		} catch(ClassCastException ex) {
			if(log.isDebugEnabled()) {
				log.debug(ex);
			}
		}

		return false;
	}

	/** 'true' iff the current runtime version is 1.2 or later */
	public static final boolean JRE_1_2_PLUS = checkForJRE_1_2_PLUS();

	/** 'true' iff the current runtime version is 1.3 or later */
	public static final boolean JRE_1_3_PLUS = checkForJRE_1_3_PLUS();

	/** 'true' iff the current runtime version is 1.4 or later */
	public static final boolean JRE_1_4_PLUS = checkForJRE_1_4_PLUS();

	static {
		if(log.isDebugEnabled()) {
			log.debug("Check for JRE: JRE_1_2_PLUS: " + JRE_1_2_PLUS);
			log.debug("Check for JRE: JRE_1_3_PLUS: " + JRE_1_3_PLUS);
			log.debug("Check for JRE: JRE_1_4_PLUS: " + JRE_1_4_PLUS);
			log.debug("Check for JRE done");
		}
	}

	private static boolean checkForJRE_1_4_PLUS() {
		try {
			// this would be a test without Class.forName, we are too stupid so we use the Class.forName test
			// " ".subSequence (0, 0);
			String.class.getMethod("subSequence", new Class[] { Integer.TYPE, Integer.TYPE });

			return true;
		} catch(Throwable ignore) {
			return false;
		}
	}

	private static boolean checkForJRE_1_2_PLUS() {
		return (SecurityManager.class.getModifiers() & 0x0400) == 0;
	}

	private static boolean checkForJRE_1_3_PLUS() {
		try {
			// this would be a test without Class.forName, we are too stupid so we use the Class.forName test
			Class.forName("java.lang.StrictMath");

			// StrictMath.abs (1.0);
			return true;
		} catch(Throwable ignore) {
			return false;
		}
	}

	/** !null iff the current system has a functional xml parser */
	public static final String XML_PARSER_FOUND = checkForXML_Parser();

	/**
	 * Returns the String of a found (and tested) xml parser
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	private static String checkForXML_Parser() {
		return null;
	}
}
