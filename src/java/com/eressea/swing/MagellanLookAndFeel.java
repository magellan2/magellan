// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Ulrich Küster, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.swing;

import java.awt.Color;
import java.awt.Component;
import java.io.InputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.LookAndFeel;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.basic.BasicLookAndFeel;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalTheme;


import com.eressea.resource.ResourcePathClassLoader;
import com.eressea.demo.desktop.DesktopEnvironment;
import com.eressea.util.CollectionFactory;
import com.eressea.util.Colors;
import com.eressea.util.JVMUtilities;
import com.eressea.util.logging.Logger;

/**
 *
 * @author  Andreas Gampe
 * @author  Ilja Pavkovic
 * @version
 */
public class MagellanLookAndFeel {
	private final static Logger log = Logger.getInstance(MagellanLookAndFeel.class);

	public static void loadBackground(Properties settings) {
		String s = settings.getProperty("MagLookAndFeel.Background");
		if (s != null) {
			Color col = null;
			try{
				col = Colors.decode(s);
			} catch(RuntimeException exc) {}
			if (col != null) {
				setBackground(col, settings);
			}
		}
	}

	public static void setBackground(Color col, Properties settings) {
		if (col.equals(MetalLookAndFeel.getWindowBackground())) {
			return;
		}
		MetalTheme mt = new MagMetalTheme(col);
		MetalLookAndFeel.setCurrentTheme(mt);
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
		} catch(Exception exc) {}
		DesktopEnvironment.updateLaF();
		DesktopEnvironment.repaintAll();
		if (settings != null) {
			if (!col.equals(Color.white)) {
				settings.setProperty("MagLookAndFeel.Background", Colors.encode(col));
			} else {
				settings.remove("MagLookAndFeel.Background");
			}
		}
	}

	protected static class MagMetalTheme extends DefaultMetalTheme {
		protected ColorUIResource magDesktopColor;

		public MagMetalTheme(Color col) {
			magDesktopColor = new ColorUIResource(col);
		}

		public ColorUIResource getWindowBackground() {
			return magDesktopColor;
		}
	}

	/**
	 * Function determines if name of current look and feel corresponds to given laf name.
	 */
	public static boolean equals(String laf) {
		return UIManager.getLookAndFeel().getName().equals(laf);
	}

	/** a static variable to initialize look and feels only once */
	private static Map lafCache;

	/** this function delivers a Map (String, MagLookAndFeelWrapper) of useable look and feel implementations */
	public static Map getLookAndFeels() {
		if(lafCache == null) {
			Map lookAndFeels = CollectionFactory.createOrderedHashtable();
			//Map defaultMap   = CollectionFactory.createTreeMap();
			try {
				URL plaf_ini_URL = ResourcePathClassLoader.getResourceStatically("plaf/plaf.ini");
				if(plaf_ini_URL != null) {
					Properties plaf_ini = new Properties();
					InputStream is = plaf_ini_URL.openStream();
					plaf_ini.load(is);
					is.close();
					String s = (String) plaf_ini.get("plaf.count");
					int plafCount = 0;
					if (s != null) {
						plafCount = Integer.parseInt(s);
					}
					for (int i = 0; i < plafCount; i++) {
						String name  = plaf_ini.getProperty("plaf.name." + i);
						String clazz = plaf_ini.getProperty("plaf.class." + i);
						String theme = plaf_ini.getProperty("plaf.themepack." + i);
						String jre   = plaf_ini.getProperty("plaf.jre." + i,"1.3");
						//Integer prio = new Integer(plaf_ini.getProperty("plaf.priority." + i,String.valueOf(Integer.MIN_VALUE+plafCount-i)));
						if(name != null && clazz != null) {
							try {
								Class c = Class.forName(clazz);
								LookAndFeel laf = (LookAndFeel) c.newInstance();
								if (laf.isSupportedLookAndFeel()) {
									if(checkJREIsRunning(jre)) {
										if(theme == null || canLoadSkinLFThemepack(theme)) {
											log.debug("MagellanLookAndfeel.getLookAndFeel("+name+","+clazz+"): "+laf.getID());
											lookAndFeels.put(name,new MagLookAndFeelWrapper(name,laf,theme));
											//defaultMap.put(prio,name);
										}
									}
								}
							} catch (ClassNotFoundException e) {
								log.debug("MagellanLookAndfeel.getLookAndFeel("+name+","+clazz+"): class not found.");
							} catch (InstantiationException e) {
								log.error("MagellanLookAndfeel.getLookAndFeel("+name+","+clazz+"): unable to instantiate.");
							} catch (IllegalAccessException e) {
								log.error("MagellanLookAndfeel.getLookAndFeel("+name+","+clazz+"): unable to access instantiation method.");
							}
						}
					}
				}
			} catch(IOException ioe) {
				log.error("MagellanLookAndfeel.getLookAndFeels(): Unable to read property file plaf.ini",ioe);
			}
			synchronized(MagellanLookAndFeel.class) {
				lafCache = lookAndFeels;
				//defaultLafCache = CollectionFactory.createLinkedList();
				//for(Iterator iter=defaultMap.keySet().iterator(); iter.hasNext(); ) {
				//	defaultLafCache.add(defaultMap.get(iter.next()));
				//}
			}
		}
		return Collections.unmodifiableMap(lafCache);
	}

	private static boolean checkJREIsRunning(String jre) {
		if(jre.startsWith("1.4")) {
			return JVMUtilities.JRE_1_4_PLUS;
		}
		return JVMUtilities.JRE_1_3_PLUS;
		
	}
	private static Class getSkinLFClass() throws ClassNotFoundException {
		return Class.forName("com.l2fprod.gui.plaf.skin.SkinLookAndFeel");
	}

	/**
	 * This is a special function to test if a themepack for skinlf is loadable.
	 */
	private static boolean canLoadSkinLFThemepack(String themepack) {
		return loadSkinLFThemepack(themepack) != null;
	}

	/**
	 * This is a special function to test if a themepack for skinlf is loadable.
	 */
	private static Object loadSkinLFThemepack(String themepack) {
		try {
			// call method public static Skin SkinLookAndFeel.loadThemePack(String themepack)
			return getSkinLFClass().getMethod("loadThemePack", new Class[] { String.class } ).invoke(null, new Object[] { themepack } );
		} catch (ClassNotFoundException e) {
			;
		} catch (IllegalAccessException e) {
			log.error("MagellanLookAndfeel.loadSkinLFThemepack("+themepack+"): unable to access instantiation method.");
		} catch (NoSuchMethodException e) {
			log.error("MagellanLookAndfeel.loadSkinLFThemepack("+themepack+"): unable to find method loadThemePack.");
		} catch (InvocationTargetException e) {
			log.error("MagellanLookAndfeel.loadSkinLFThemepack("+themepack+"): InvocationTargetException.",e);
		} catch (Error e) {
			log.error("MagellanLookAndfeel.loadSkinLFThemepack("+themepack+"): error thrown while loading.",e);
		}
		return null;
	}

	private static boolean prepareSkinLFTheme(String themepack) {
		try {
			Object skin = loadSkinLFThemepack(themepack);
			// call method public static Skin SkinLookAndFeel.setSkin(Skin skin)
			Class cSkin = Class.forName("com.l2fprod.gui.plaf.skin.Skin");
			getSkinLFClass().getMethod("setSkin", new Class[] { cSkin } ).invoke(null, new Object[] { skin } );
			return true;
		} catch (ClassNotFoundException e) {
			;
		} catch (IllegalAccessException e) {
			log.error("MagellanLookAndfeel.prepareSkinLFTheme(): unable to access instantiation method.");
		} catch (NoSuchMethodException e) {
			log.error("MagellanLookAndfeel.prepareSkinLFTheme(): unable to find method setSkin.");
		} catch (InvocationTargetException e) {
			log.error("MagellanLookAndfeel.prepareSkinLFTheme(): InvocationTargetException.");
		}
		return false;
	}

	/** boolean */
	public static boolean setLookAndFeel(String laf) {
		if(laf == null) {
			return false;
		}
		LookAndFeel old = UIManager.getLookAndFeel();
		LookAndFeel olaf = (MagLookAndFeelWrapper) getLookAndFeels().get(laf);
		if(olaf == null) {
			log.error("Could not switch look and feel to " + laf + ")" +olaf);
			return false;
		}
		try {
			UIManager.setLookAndFeel(olaf);
		} catch (Exception e) {
			log.info("Could not switch look and feel to " + laf + "(" + olaf + ")");
			log.debug("Could not switch look and feel to " + laf + "(" + olaf + ")",e);
			try {
				UIManager.setLookAndFeel(old);
			} catch (UnsupportedLookAndFeelException ue) {
			}
			return false;
		}
		try {
			if(log.isDebugEnabled()) {
				log.debug("MagellanLookAndfeel.setLookAndFeel("+laf+"): "+UIManager.getLookAndFeel().getClass()+", "+
						  UIManager.getLookAndFeel().getName()+", "+UIManager.getLookAndFeel().getID());
			}
		} catch (Exception e) {
		}
		return true;
	}

	/** Function delivers a sorted list of look and feel names */
	public static List getLookAndFeelNames() {
		List s = CollectionFactory.createArrayList();
		s.addAll(getLookAndFeels().keySet());
		Collections.sort(s);
		//Collections.reverse(s);
		// s.add("System");
		//Collections.reverse(s);

		return s;

	}

	/**
	 * a wrapper class for two purposes:
	 * a) storing a possible themepack name
	 * b) adjust name of the look and feel to given string
	 */
	private static class MagLookAndFeelWrapper extends BasicLookAndFeel {
		private String name;
		private String theme;
		private LookAndFeel delegateTo;

		public MagLookAndFeelWrapper(String name,LookAndFeel laf, String theme) {
			this.name=name;
			this.delegateTo=laf;
			this.theme=theme;
		}

		public String getTheme() {
			return theme;
		}

		//delegation of all methods
		/*
		// @since 1.4
		public void provideErrorFeedback(Component component) {
			delegateTo.provideErrorFeedback(component);
		}
		*/
		public String getID() {
			// NO delegation
			return delegateTo.getID();
		}
		public String getName() {
			// NO delegation
			return name;
		}
		public String getDescription() {
			return delegateTo.getDescription();
		}

		/*
		//  @since 1.4
		public boolean getSupportsWindowDecorations() {
			return false;
		}
		*/

		public  boolean isNativeLookAndFeel() {
			return delegateTo.isNativeLookAndFeel();
		}
		public  boolean isSupportedLookAndFeel() {
			return delegateTo.isSupportedLookAndFeel();
		}
		public void initialize() {
			if(theme != null) {
				prepareSkinLFTheme(theme);
				// do some skinlf initialitation
			}
			delegateTo.initialize();
		}
		public void uninitialize() {
			delegateTo.uninitialize();
		}
		public UIDefaults getDefaults() {
			return delegateTo.getDefaults();
		}


		/*
		public String toString() {
			return "[" + getDescription() + " - " + getClass().getName() + "]";
		}
		*/
	}
}
