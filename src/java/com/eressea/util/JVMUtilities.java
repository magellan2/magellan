// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Ilja Pavkovic, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===
package com.eressea.util;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Window;

import javax.swing.UIManager;

import com.eressea.util.logging.Logger;

/**
 * This class provides static functions for jvm specific bullshit (e.g. changed focus system 
 * from jvm 1.3.x to 1.4.x
 * It also checks for necessary systems, e.g. XML
 */
public class JVMUtilities {
	private final static Logger log = Logger.getInstance(JVMUtilities.class);


	/** 
	 * this is a helper function to catch a jvm 1.4.2_01 bug
	 */
	public final static Color getTreeSelectionBorderColor() {
		try {
			return (Color)UIManager.getDefaults().get("Tree.selectionBorderColor");
		} catch(ClassCastException cce) {
			log.warn("JVM 1.4.2_01 bug! Switching to color black!");
			if(log.isDebugEnabled()) {
				log.debug("JVM 1.4.2_01 bug: class "+
						  UIManager.getDefaults().get("Tree.selectionBorderColor").getClass().getName()+
						  " instead of Color!");
			}
			return Color.black;
		}
	}

	/** 
	 * This function calls Component.setFocusableWindowState (in java >= 1.4) to 
	 * keep GUI consistent with java 1.3.x
	 */
	public final static void setFocusableWindowState(Window aObj, boolean aFlag) { 
		try {
			//try to call setFocusableWindowState (true) on java 1.4 while staying compatible with Java 1.3
			aObj.getClass().getMethod("setFocusableWindowState", new Class[] { Boolean.TYPE })
				.invoke(aObj, new Object[] { aFlag ? Boolean.TRUE : Boolean.FALSE });
			log.debug("JVMUtitities : successfully called Component.setFocusableWindowState("+aFlag+")!");
		} catch (java.lang.NoSuchMethodException ex) {
		} catch (java.lang.IllegalAccessException ex) {
		} catch (java.lang.reflect.InvocationTargetException ex) {
		}
	}


	public static final int UNKNOWN = -1;
	
	public static final int NORMAL = 0;
	public static final int ICONIFIED = 1;
	public static final int MAXIMIZED_HORIZ = 2;
	public static final int MAXIMIZED_VERT = 4;
	public static final int MAXIMIZED_BOTH = MAXIMIZED_VERT | MAXIMIZED_HORIZ;

	/** 
	 * This function calls Frame.getExtendedState (in java >= 1.4) to 
	 * keep GUI consistent with java 1.3.x
	 */
	public final static int getExtendedState(Frame aObj) { 
		try {
			Object result = aObj.getClass().getMethod("getExtendedState", new Class[] { }).
				invoke(aObj, new Object[] { });
			log.debug("JVMUtitities : successfully called Window.getExtendedState()!");
			return ((Integer) result).intValue();
		} catch (java.lang.NoSuchMethodException ex) {
		} catch (java.lang.IllegalAccessException ex) {
		} catch (java.lang.reflect.InvocationTargetException ex) {
		} catch (ClassCastException ex) {
			log.debug(ex);
		}
		return UNKNOWN;
	}
	
	/** 
	 * This function calls Frame.setExtendedState (in java >= 1.4) to 
	 * keep GUI consistent with java 1.3.x
	 */
	public final static boolean setExtendedState(Frame aObj, int state) {
		if(state == UNKNOWN) return false;
		try {
			Object result = aObj.getClass().getMethod("setExtendedState", new Class[] { Integer.TYPE }).
				invoke(aObj, new Object[] { new Integer(state) });
			log.debug("JVMUtitities : successfully called Window.setExtendedState("+state+")!");
			return true;
		} catch (java.lang.NoSuchMethodException ex) {
		} catch (java.lang.IllegalAccessException ex) {
		} catch (java.lang.reflect.InvocationTargetException ex) {
		} catch (ClassCastException ex) {
			log.debug(ex);
		}
		return false;
	}
	

	/** 'true' iff the current runtime version is 1.2 or later */
	public final static boolean JRE_1_2_PLUS = checkForJRE_1_2_PLUS();

    /** 'true' iff the current runtime version is 1.3 or later */
	public final static boolean JRE_1_3_PLUS = checkForJRE_1_3_PLUS();

    /** 'true' iff the current runtime version is 1.4 or later */
	public final static boolean JRE_1_4_PLUS = checkForJRE_1_4_PLUS();

	static {
		if(log.isDebugEnabled()) {
			log.debug("Check for JRE: JRE_1_2_PLUS: " +JRE_1_2_PLUS);
			log.debug("Check for JRE: JRE_1_3_PLUS: " +JRE_1_3_PLUS);
			log.debug("Check for JRE: JRE_1_4_PLUS: " +JRE_1_4_PLUS);
			log.debug("Check for JRE done");
		}
	}

	private static boolean checkForJRE_1_4_PLUS() {
		try {
			// this would be a test without Class.forName, we are too stupid so we use the Class.forName test
			// " ".subSequence (0, 0);
			String.class.getMethod("subSequence", new Class[] { Integer.TYPE, Integer.TYPE });
			return true;
		} catch (Throwable ignore) {
			return false;
		}
	}
	
	private static boolean checkForJRE_1_2_PLUS() {
		return (SecurityManager.class.getModifiers () & 0x0400) == 0;
	}
	
	private static boolean checkForJRE_1_3_PLUS() {
		try {
			// this would be a test without Class.forName, we are too stupid so we use the Class.forName test
			Class.forName("java.lang.StrictMath");
			// StrictMath.abs (1.0);
			return true;
		} catch (Throwable ignore) {
			return false;
		}
	}

	/** !null iff the current system has a functional xml parser */
	public final static String XML_PARSER_FOUND = checkForXML_Parser();
	
	/** 
	 * Returns the String of a found (and tested) xml parser
	 */
	private static String checkForXML_Parser() {
		return null;
	}
}
