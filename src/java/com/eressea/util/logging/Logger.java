// ===
// Copyright (C) 2000, 2001, 2002 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Ilja Pavkovic, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===
package com.eressea.util.logging;

import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
//import org.apache.log4j.*;

/* 
 * @author Ilja Pavkovic
 */
public class Logger {
	//	private Category ivTraceLog;

	/** This log level entirely stops all logging */
	public final static int OFF   = 0;

	/** Fatal messages are messages which are printed before a fatal program exit */
	public final static int FATAL = 1;

	/** Error messages are printed if an error occurs but the application can continue */
	public final static int ERROR = 2;

	/** Warning messages are info messages with warning character */
	public final static int WARN  = 3;

	/** Info messages are printed for informational purposes */
	public final static int INFO  = 4;

	/** Debug messages are printed for debugging purposes */
	public final static int DEBUG = 5;

	/** AWT messages are printed for debugging awt purposes */
	public final static int AWT = 6;

	private static int verboseLevel = INFO;

	private static Object  awtLogger = null;
	private static boolean searchAwtLogger = true;

	private Logger(String aBase) {
		// be fail-fast
		if(aBase == null) throw new NullPointerException();
		//ivTraceLog = Category.getInstance(aBase);
	}
	
	public static Logger getInstance(Class aClass) {
		// be fail-fast
		if(aClass == null) throw new NullPointerException();
		return new Logger(aClass.getName());
	}
	
	public static Logger getInstance(String aBase) {
		return new Logger(aBase);
	}
	
	public static void setLevel(int level) {
		verboseLevel = level;
	}
	
	public static void setLevel(String aLevel) {
		String level = aLevel.toUpperCase();
		if(level.startsWith("O")) { setLevel(OFF);   } 
		if(level.startsWith("F")) { setLevel(FATAL); }
		if(level.startsWith("E")) { setLevel(ERROR); }
		if(level.startsWith("W")) { setLevel(WARN);  }
		if(level.startsWith("I")) { setLevel(INFO);  }
		if(level.startsWith("D")) { setLevel(DEBUG); }
		if(level.startsWith("A")) { setLevel(AWT);   }
	}

	public static String getLevel(int level) { 
		if(level <= OFF) {
			return "OFF";
		}
		switch(level) {
		case FATAL: return "FATAL";
		case ERROR: return "ERROR";
		case WARN:  return "WARN";
		case INFO:  return "INFO";
		case DEBUG: return "DEBUG";
		default:    return "ALL";
		}
	}
	private void log(PrintStream aOut, int aLevel, Object aObj, Throwable aThrowable) {
		if(verboseLevel>=aLevel) {
			if(aObj != null) {
				if(aObj instanceof Throwable) {
					((Throwable) aObj).printStackTrace(aOut);
				} else {
					aOut.println(aObj);
				}
			}
			if(aThrowable != null) {
				aThrowable.printStackTrace(aOut);
			} else {
				if(aObj != null && !(aObj instanceof Throwable) && aObj.toString().endsWith("Error")) {
					new Exception("SELF GENERATED STACK TRACE").printStackTrace(aOut);
				}
			}
		}
	}
	
	public void fatal(Object aObj) {
		fatal(aObj,null);
	}
	public void fatal(Object aObj, Throwable aThrowable) {
		log(System.out, FATAL, aObj, aThrowable);
	}
	public boolean isFatalEnabled() {
		return verboseLevel >= FATAL;
	}

	public void error(Object aObj) {
		error(aObj,null);
	}
	public void error(Object aObj, Throwable aThrowable) {
		log(System.err, ERROR, aObj, aThrowable);
	}
	public boolean isErrorEnabled() {
		return verboseLevel >= ERROR;
	}

	public void warn(Object aObj) {
		warn(aObj,null);
	}
	public void warn(Object aObj, Throwable aThrowable) {
		log(System.out, WARN, aObj, aThrowable);
	}
	public boolean isWarnEnabled() {
		return verboseLevel >= WARN;
	}

	public void info(Object aObj) {
		info(aObj,null);
	}
	public void info(Object aObj, Throwable aThrowable) {
		log(System.out, INFO, aObj, aThrowable);
	}
	public boolean isInfoEnabled() {
		return verboseLevel >= INFO;
	}

	public void debug(Object aObj) {
		debug(aObj,null);
	}
	public void debug(Object aObj, Throwable aThrowable) {
		log(System.err, DEBUG, aObj, aThrowable);
	}
	public boolean isDebugEnabled() {
		return verboseLevel >= DEBUG;
	}

	public void awt(Object aObj) {
		awt(aObj,null);
	}
	public void awt(Object aObj, Throwable aThrowable) {
		log(System.err, AWT, aObj, aThrowable);
		if(isAwtEnabled()) {
			if(searchAwtLogger) {
				searchAwtLogger = false;
				try {
					awtLogger = Class.forName("com.eressea.util.logging.AWTLogger").newInstance();
				} catch(ClassNotFoundException e) {
					debug("AWTLogger not found",e);
				} catch(InstantiationException e) {
					debug("Cannot instanciate AWTLogger",e);
				} catch(IllegalAccessException e) {
					debug("Cannot access AWTLogger",e);
				}
			}
		}
		if(awtLogger != null) {
			try	{
				Class[] parameterTypes = new Class[] { Object.class, Throwable.class };
				Object[] arguments = new Object[] { aObj, aThrowable };
				Method method = awtLogger.getClass().getMethod("log" ,parameterTypes);
				method.invoke(awtLogger, arguments);
			} catch (NoSuchMethodException e) {
				debug(e);
			} catch (InvocationTargetException e) {
				debug(e);
			} catch (IllegalAccessException e) {
				debug(e);
			}
		}
	}

	public boolean isAwtEnabled() {
		return verboseLevel >= AWT;
	}

}
