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

package com.eressea.swing;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.LookAndFeel;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.basic.BasicLookAndFeel;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalTheme;

import com.eressea.demo.desktop.DesktopEnvironment;
import com.eressea.resource.ResourcePathClassLoader;
import com.eressea.util.CollectionFactory;
import com.eressea.util.Colors;
import com.eressea.util.JVMUtilities;
import com.eressea.util.logging.Logger;

/**
 * DOCUMENT ME!
 *
 * @author Andreas Gampe
 * @author Ilja Pavkovic
 * @version
 */
public class MagellanLookAndFeel {
	private static final Logger log = Logger.getInstance(MagellanLookAndFeel.class);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param settings TODO: DOCUMENT ME!
	 */
	public static void loadBackground(Properties settings) {
		String s = settings.getProperty("MagLookAndFeel.Background");

		if(s != null) {
			Color col = null;

			try {
				col = Colors.decode(s);
			} catch(RuntimeException exc) {
			}

			if(col != null) {
				setBackground(col, settings);
			}
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param col TODO: DOCUMENT ME!
	 * @param settings TODO: DOCUMENT ME!
	 */
	public static void setBackground(Color col, Properties settings) {
		if(col.equals(MetalLookAndFeel.getWindowBackground())) {
			return;
		}

		MetalTheme mt = new MagMetalTheme(col);
		MetalLookAndFeel.setCurrentTheme(mt);

		DesktopEnvironment.updateLaF();
		DesktopEnvironment.repaintAll();

		if(settings != null) {
			if(!col.equals(Color.white)) {
				settings.setProperty("MagLookAndFeel.Background", Colors.encode(col));
			} else {
				settings.remove("MagLookAndFeel.Background");
			}
		}
	}

	protected static class MagMetalTheme extends DefaultMetalTheme {
		protected ColorUIResource magDesktopColor;

		/**
		 * Creates a new MagMetalTheme object.
		 *
		 * @param col TODO: DOCUMENT ME!
		 */
		public MagMetalTheme(Color col) {
			magDesktopColor = new ColorUIResource(col);
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public ColorUIResource getWindowBackground() {
			return magDesktopColor;
		}
	}

	/**
	 * Function determines if name of current look and feel corresponds to given laf name.
	 *
	 * @param laf TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static boolean equals(String laf) {
		return UIManager.getLookAndFeel().getName().equals(laf);
	}

	/** a static variable to initialize look and feels only once */
	private static Map lafCache;

	/**
	 * Delivers a Map (String, MagLookAndFeelWrapper) of possibly useable look and feel
	 * implementations
	 *
	 * @return TODO: DOCUMENT ME!
	 */
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

					if(s != null) {
						plafCount = Integer.parseInt(s);
					}

					for(int i = 0; i < plafCount; i++) {
						String name = plaf_ini.getProperty("plaf.name." + i);
						String clazz = plaf_ini.getProperty("plaf.class." + i);
						String theme = plaf_ini.getProperty("plaf.themepack." + i);
						String jre = plaf_ini.getProperty("plaf.jre." + i, "1.3");

						if((name != null) && (clazz != null)) {
							try {
								Class c = Class.forName(clazz);
								LookAndFeel laf = (LookAndFeel) c.newInstance();

								if(laf.isSupportedLookAndFeel()) {
									if(checkJREIsRunning(jre)) {
										if((theme == null) || canLoadSkinLFThemepack(theme)) {
											log.debug("MagellanLookAndfeel.getLookAndFeel(" + name +
													  "," + clazz + "): " + laf.getID());
											lookAndFeels.put(name,
															 new MagLookAndFeelWrapper(name, laf,
																					   theme));

											//defaultMap.put(prio,name);
										}
									}
								}
							} catch(ClassNotFoundException e) {
								if(log.isDebugEnabled()) {
									log.debug("MagellanLookAndfeel.getLookAndFeel(" + name + "," +
											  clazz + "): class not found.");
								}
							} catch(InstantiationException e) {
								log.error("MagellanLookAndfeel.getLookAndFeel(" + name + "," +
										  clazz + "): unable to instantiate.");
							} catch(IllegalAccessException e) {
								log.error("MagellanLookAndfeel.getLookAndFeel(" + name + "," +
										  clazz + "): unable to access instantiation method.");
							}
						}
					}
				}
			} catch(IOException ioe) {
				log.error("MagellanLookAndfeel.getLookAndFeels(): Unable to read property file plaf.ini",
						  ioe);
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

	private static Class getClearLookManagerClass() throws ClassNotFoundException {
		return Class.forName("com.jgoodies.clearlook.ClearLookManager");
	}

	private static Class getClearLookModeClass() throws ClassNotFoundException {
		return Class.forName("com.jgoodies.clearlook.ClearLookMode");
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static boolean enableClearLookDebug() {
		return enableClearLook("DEBUG");
	}

	// 	public static boolean enableClearLookVerbose() {
	// 		return enableClearLook("VERBOSE");
	// 	}
	static {
		//MagellanLookAndFeel.enableClearLookDebug();
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static boolean enableClearLookOn() {
		return enableClearLook("ON");
	}

	private static boolean enableClearLook(String fieldName) {
		try {
			// call method public static void ClearLookManager.setMode(fieldName)
			// fieldName may be "ON", "OFF", "VERBOSE" or "DEBUG"
			getClearLookManagerClass().getMethod("setMode", new Class[] { getClearLookModeClass() })
				.invoke(null, new Object[] { getClearLookModeClass().getField(fieldName).get(null) });

			if(log.isDebugEnabled()) {
				log.debug("Enabled ClearLookDebug.");
			}

			return true;
		} catch(ClassNotFoundException e) {
			log.error(e);
		} catch(IllegalAccessException e) {
			log.error(e);
		} catch(NoSuchFieldException e) {
			log.error(e);
		} catch(NoSuchMethodException e) {
			log.error(e);
		} catch(InvocationTargetException e) {
			log.error(e);
		} catch(Error e) {
			log.error(e);
		}

		if(log.isDebugEnabled()) {
			log.debug("Failed to invoke ClearLookDebug.");
		}

		return false;
	}

	/**
	 * This is a special function to test if a themepack for skinlf is loadable.
	 *
	 * @param themepack TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	private static boolean canLoadSkinLFThemepack(String themepack) {
		return loadSkinLFThemepack(themepack) != null;
	}

	/**
	 * This is a special function to test if a themepack for skinlf is loadable.
	 *
	 * @param themepack TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	private static Object loadSkinLFThemepack(String themepack) {
		try {
			// call method public static Skin SkinLookAndFeel.loadThemePack(String themepack)
			return getSkinLFClass().getMethod("loadThemePack", new Class[] { String.class }).invoke(null,
																									new Object[] {
																										themepack
																									});
		} catch(ClassNotFoundException e) {
			;
		} catch(IllegalAccessException e) {
			log.error("MagellanLookAndfeel.loadSkinLFThemepack(" + themepack +
					  "): unable to access instantiation method.");
		} catch(NoSuchMethodException e) {
			log.error("MagellanLookAndfeel.loadSkinLFThemepack(" + themepack +
					  "): unable to find method loadThemePack.");
		} catch(InvocationTargetException e) {
			log.error("MagellanLookAndfeel.loadSkinLFThemepack(" + themepack +
					  "): InvocationTargetException.", e);
		} catch(Error e) {
			log.error("MagellanLookAndfeel.loadSkinLFThemepack(" + themepack +
					  "): error thrown while loading.", e);
		}

		return null;
	}

	private static boolean prepareSkinLFTheme(String themepack) {
		try {
			Object skin = loadSkinLFThemepack(themepack);

			// call method public static Skin SkinLookAndFeel.setSkin(Skin skin)
			Class cSkin = Class.forName("com.l2fprod.gui.plaf.skin.Skin");
			getSkinLFClass().getMethod("setSkin", new Class[] { cSkin }).invoke(null,
																				new Object[] { skin });

			return true;
		} catch(ClassNotFoundException e) {
			;
		} catch(IllegalAccessException e) {
			log.error("MagellanLookAndfeel.prepareSkinLFTheme(): unable to access instantiation method.");
		} catch(NoSuchMethodException e) {
			log.error("MagellanLookAndfeel.prepareSkinLFTheme(): unable to find method setSkin.");
		} catch(InvocationTargetException e) {
			log.error("MagellanLookAndfeel.prepareSkinLFTheme(): InvocationTargetException.");
		}

		return false;
	}

	/**
	 * boolean
	 *
	 * @param laf TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static boolean setLookAndFeel(String laf) {
		if(laf == null) {
			return false;
		}

		LookAndFeel old = UIManager.getLookAndFeel();
		LookAndFeel olaf = (MagLookAndFeelWrapper) getLookAndFeels().get(laf);

		if(olaf == null) {
			log.error("Could not switch look and feel to " + laf + ")" + olaf);

			return false;
		}

		try {
			UIManager.setLookAndFeel(olaf);
		} catch(Exception e) {
			log.info("Could not switch look and feel to " + laf + "(" + olaf + ")");

			if(log.isDebugEnabled()) {
				log.debug("Could not switch look and feel to " + laf + "(" + olaf + ")", e);
			}

			try {
				UIManager.setLookAndFeel(old);
			} catch(UnsupportedLookAndFeelException ue) {
			}

			return false;
		}

		try {
			log.info("MagellanLookAndfeel.setLookAndFeel(" + laf + "): " +
					 UIManager.getLookAndFeel().getClass() + ", " +
					 UIManager.getLookAndFeel().getName() + ", " +
					 UIManager.getLookAndFeel().getID());
		} catch(Exception e) {
		}

		return true;
	}

	/**
	 * Function delivers a sorted list of look and feel names
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static List getLookAndFeelNames() {
		List s = CollectionFactory.createArrayList();
		s.addAll(getLookAndFeels().keySet());
		Collections.sort(s);

		return s;
	}

	/**
	 * a wrapper class for two purposes: a) storing a possible themepack name b) adjust name of the
	 * look and feel to given string
	 */
	private static class MagLookAndFeelWrapper extends BasicLookAndFeel {
		private String name;
		private String theme;
		private LookAndFeel delegateTo;

		/**
		 * Creates a new MagLookAndFeelWrapper object.
		 *
		 * @param name TODO: DOCUMENT ME!
		 * @param laf TODO: DOCUMENT ME!
		 * @param theme TODO: DOCUMENT ME!
		 */
		public MagLookAndFeelWrapper(String name, LookAndFeel laf, String theme) {
			this.name = name;
			this.delegateTo = laf;
			this.theme = theme;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
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

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public String getName() {
			// NO delegation
			return name;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public String getDescription() {
			return delegateTo.getDescription();
		}

		/*
		//  @since 1.4
		public boolean getSupportsWindowDecorations() {
		    return false;
		}
		*/
		public boolean isNativeLookAndFeel() {
			return delegateTo.isNativeLookAndFeel();
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public boolean isSupportedLookAndFeel() {
			return delegateTo.isSupportedLookAndFeel();
		}

		/**
		 * TODO: DOCUMENT ME!
		 */
		public void initialize() {
			if(theme != null) {
				prepareSkinLFTheme(theme);

				// do some skinlf initialitation
			}

			delegateTo.initialize();
		}

		/**
		 * TODO: DOCUMENT ME!
		 */
		public void uninitialize() {
			delegateTo.uninitialize();
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
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
