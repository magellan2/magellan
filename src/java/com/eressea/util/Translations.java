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
 */

package com.eressea.util;

import java.io.IOException;
import java.io.InputStream;

import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Set;

import com.eressea.util.logging.Logger;

/**
 * Helper class for centrally managing translation tables (resource bundles) for components.
 */
public class Translations {
	private static final Logger log = Logger.getInstance(Translations.class);

	/** Associates bundle names with translation bundles for caching. */
	private static Map bundles = CollectionFactory.createHashMap();

	/** Associates bundle names with translations found in classes for caching. */
	private static Map classBundles = CollectionFactory.createHashMap();

	/** The class loader used to find the resource bundles. */
	private static ClassLoader bundleLoader = ClassLoader.getSystemClassLoader();

	/**
	 * Sets the class loader to use for loading resource bundles.
	 *
	 * @param l TODO: DOCUMENT ME!
	 */
	public static void setClassLoader(ClassLoader l) {
		bundleLoader = l;
	}

	/**
	 * Returns the translated string for the specified class and string using the GUI locale. The
	 * corresponding properties file must be named like the class c with all '.' (periods)
	 * replaced bu '-' (dashes).
	 *
	 * @param o for which the translation is searched
	 * @param key the key to translate
	 *
	 * @return the translation, if found, or null
	 */
	public static String getTranslation(Object o, String key) {
		return getTranslation(o.getClass(), key);
	}

	/**
	 * Returns the translated string for the specified class and string using the GUI locale. The
	 * corresponding properties file must be named like the class c with all '.' (periods)
	 * replaced bu '-' (dashes).
	 *
	 * @param c for which the translation is searched
	 * @param key the key to translate
	 *
	 * @return the translation, if found, or key instead
	 */
	public static String getTranslation(Class c, String key) {
		String result = getBundleTranslation(c, key);

		if(result == null) {
			result = getTranslationFromClass(c, key);

			if(result == null) {
				// here we are going to make some more implications:
				// We now search for a static class method (public static Set getTranslations()),
				// which delivers a map of keys and their english translation.
				//
				if(!Locale.ENGLISH.equals(Locales.getGUILocale())) {
					if(!key.endsWith(".tooltip") &&
						   !isInstanceOf(c, "com.eressea.demo.actions.MenuAction")) {
						log.error("Translations.getTranslation(" + c + "," + key +
								  "): no translation found for Locale " + Locales.getGUILocale() +
								  ", calling getDefaultTranslationFromClass");
					}
				}

				if(log.isDebugEnabled()) {
					log.debug("Translations.getTranslation(" + c + "," + key +
							  "): no translation found for Locale " + Locales.getGUILocale() +
							  ", calling getDefaultTranslationFromClass");
				}

				// fall back to key
				result = key;
			}

			if(log.isDebugEnabled()) {
				log.debug("Translations.getTranslation(" + c + "," + key + ") RESULT: \"" + result +
						  "\")");
			}
		}

		if("".equals(result)) {
			// now also remove "" results as they are assumed to be null
			result = null;
		}

		return result;
	}

	/**
	 * Returns if the given class is an instance of the given string.
	 *
	 * @param aClass the class
	 * @param className the name of the superclass to check
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	private static boolean isInstanceOf(Class aClass, String className) {
		Class c = aClass;

		while(c != null) {
			if(c.getName().equals(className)) {
				return true;
			}

			c = c.getSuperclass();
		}

		return false;
	}

	/**
	 * Returns the translated string for the specified class and string using the GUI locale.
	 *
	 * @param c the name of the class
	 * @param key the key to translate
	 *
	 * @return the translation, if found, or null instead
	 */
	private static String getTranslationFromClass(Class c, String key) {
		Map t = getDefaultTranslationsFromClass(c);

		if(t != null) {
			if((t.get(key) != null) || !t.containsKey(key)) {
				return (String) t.get(key);
			}

			// we now ascend to the parent object, but only if this object had a translation map
			if(c.getSuperclass() != null) {
				if(log.isDebugEnabled()) {
					log.debug("Translations.getTranslationFromClass(" + c + "," + key +
							  "): climbing to super class " + c.getSuperclass());
				}

				return getTranslationFromClass(c.getSuperclass(), key);
			}
		}

		return null;
	}

	/**
	 * Returns the translated string for the specified bundle and string using the GUI locale. The
	 * corresponding properties file must be named like the class c with all '.' (periods)
	 * replaced bu '-' (dashes).
	 *
	 * @param c the name of the bundle
	 * @param key the key to translate
	 *
	 * @return the translation, if found, or null instead
	 */
	private static String getBundleTranslation(Class c, String key) {
		ResourceBundle rb = getBundle(c, Locales.getGUILocale());

		if(rb != null) {
			try {
				String trans = rb.getString(key);

				if(trans != null) {
					return trans;
				}
			} catch(MissingResourceException e) {
			}

			// we now ascend to the parent object, but only if this object had a resource bundle
			if(c.getSuperclass() != null) {
				if(log.isDebugEnabled()) {
					log.debug("Translations.getBundleTranslation(" + c + "," + key +
							  "): climbing to super class " + c.getSuperclass());
				}

				return getBundleTranslation(c.getSuperclass(), key);
			}
		}

		return null;
	}

	/**
	 * Returns the translated string for the specified class and string using the order locale. The
	 * corresponding properties file must be named like the class c with all '.' (periods)
	 * replaced by '-' (dashes).
	 *
	 * @param key the key to translate
	 *
	 * @return the translation, if found, or default instead
	 */
	public static String getOrderTranslation(String key) {
		return getOrderTranslation(key, Locales.getOrderLocale());
	}

	/**
	 * Returns the translated string for the specified class and string using the order locale. The
	 * corresponding properties file must be named like the class c with all '.' (periods)
	 * replaced by '-' (dashes).
	 */
	private static Set loggedOrderTranslations = CollectionFactory.createHashSet();

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param key TODO: DOCUMENT ME!
	 * @param l TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static String getOrderTranslation(String key, Locale l) {
		ResourceBundle rb = getBundle(null, l);

		if(rb != null) {
			try {
				String translation = rb.getString(key);

				if(translation != null) {
					if(log.isDebugEnabled() && !loggedOrderTranslations.contains(key)) {
						log.debug("Translations.getOrderTranslation(" + key + "," + l + "): \"" +
								  translation + "\"");
						loggedOrderTranslations.add(key);
					}

					return translation;
				}
			} catch(MissingResourceException me) {
			}
		}

		// no translation found, give back key
		if(log.isDebugEnabled() && !loggedOrderTranslations.contains(key)) {
			log.debug("Translations.getOrderTranslation(" + key + "," + l + "): \"" + key + "\"");
			loggedOrderTranslations.add(key);
		}

		// no translation found, give back key
		if(!Locale.ENGLISH.equals(l)) {
			log.warn("Translations.getOrderTranslation(" + key + "," + l +
					 "): no valid translation found, returning key");
		}

		return key;
	}

	/**
	 * Loads a resource bundle with the specified name and locale from a 'lang' subdirectory.
	 *
	 * @param c TODO: DOCUMENT ME!
	 * @param l TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	private static ResourceBundle getBundle(Class c, Locale l) {
		// c == null indicates orders bundle
		String name = (c == null) ? "orders" : c.getName().replace('.', '-').toLowerCase();

		// lower casing the name is required as all resources are
		// supposed to be in lower case
		name = "lang/" + name;

		TranslationBundle t = (TranslationBundle) bundles.get(name);

		if((t == null) && bundles.containsKey(name)) {
			// in this situation we stored an empty bundle
			return null;
		}

		if((t == null) || !t.locale.equals(l)) {
			/* Using ResourceBundle.getBundle() was abandoned because
			   it tries to load a bundle for the default locale when
			   it fails to load a bundle for the specified locale. We
			   do not care about the default locale, so we try the
			   base bundle if loading a bundle for the specified
			   locale fails. */
			ResourceBundle b = loadResourceBundle(name, l, bundleLoader);

			if(b != null) {
				t = new TranslationBundle(b, l);
			}
		}

		// pavkovic 2003.01.29: also keep t in mind even if it null!
		bundles.put(name, t);

		if(t != null) {
			return t.bundle;
		} else {
			return null;
		}
	}

	/**
	 * Loads a resource bundle from a file. This method behaves like
	 * java.util.ResourceBundle.getBundle() but it ignores the default locale and it only loads
	 * PropertyResourceBundle objects requiring appropriate .properties files.
	 *
	 * @param name TODO: DOCUMENT ME!
	 * @param l TODO: DOCUMENT ME!
	 * @param loader TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static PropertyResourceBundle loadResourceBundle(final String name, Locale l,
															ClassLoader loader) {
		String fileName = null;
		InputStream in = null;

		try {
			// baseclass + "_" + language1 + "_" + country1 + "_" + variant1
			fileName = name + "_" + l.getLanguage() + "_" + l.getCountry() + "_" + l.getVariant() +
					   ".properties";
			in = loader.getResourceAsStream(fileName);

			if(in != null) {
				return new PropertyResourceBundle(in);
			}

			// baseclass + "_" + language1 + "_" + country1
			fileName = name + "_" + l.getLanguage() + "_" + l.getCountry() + ".properties";
			in = loader.getResourceAsStream(fileName);

			if(in != null) {
				return new PropertyResourceBundle(in);
			}

			// baseclass + "_" + language1
			fileName = name + "_" + l.getLanguage() + ".properties";
			in = loader.getResourceAsStream(fileName);

			if(in != null) {
				return new PropertyResourceBundle(in);
			}

			// pavkovic 20030319: this is crap:
			// I cannot add the file com-eressea-swing-tree-skillnodewrapper_de.properties
			// because cvs does not allow me to do so
			// I added com-eressea-swing-tree-skillnodewrapper_de_2.properties
			// and hope it works now
			//
			// baseclass + "_" + language1 + "_2" (for shitty cvs!!!)
			fileName = name + "_" + l.getLanguage() + "_2" + ".properties";
			in = loader.getResourceAsStream(fileName);

			if(in != null) {
				return new PropertyResourceBundle(in);
			}

			// baseclass
			fileName = name + ".properties";
			in = loader.getResourceAsStream(fileName);

			if(in != null) {
				return new PropertyResourceBundle(in);
			}
		} catch(IOException e) {
		}

		return null;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param c TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static synchronized Map getDefaultTranslationsFromClass(Class c) {
		/*
		if(log.isDebugEnabled()) {
		    log.debug("Translations.getDefaultTranslationsFromClass("+c+
		              "). Searching for method public static Map getDefaultTranslations()");
		              }
		*/
		if(classBundles.containsKey(c)) {
			return (Map) classBundles.get(c);
		}

		try {
			//try to call the static method getDefaultTranslations() on the given class
			java.lang.reflect.Method method = c.getMethod("getDefaultTranslations", new Class[] {  });

			if(method != null) {
				Object result = method.invoke(null, new Object[] {  });

				if(result instanceof Map) {
					classBundles.put(c, result);

					return (Map) result;
				}
			}
		} catch(java.lang.NoSuchMethodException ex) {
			if(log.isDebugEnabled()) {
				log.debug("Translations.getDefaultTranslationsFromClass(" + c +
						  "): method getDefaultTranslations not found");
			}
		} catch(java.lang.IllegalAccessException ex) {
			if(log.isDebugEnabled()) {
				log.debug("Translations.getDefaultTranslationsFromClass", ex);
			}
		} catch(java.lang.reflect.InvocationTargetException ex) {
			if(log.isDebugEnabled()) {
				log.debug("Translations.getDefaultTranslationsFromClass", ex);
			}
		}

		classBundles.put(c, null);

		return null;
	}

	/**
	 * Binds a resource bundle and a locale together.
	 */
	private static class TranslationBundle {
		/** TODO: DOCUMENT ME! */
		public ResourceBundle bundle = null;

		/** TODO: DOCUMENT ME! */
		public Locale locale = null;

		/**
		 * Creates a new TranslationBundle object.
		 *
		 * @param b TODO: DOCUMENT ME!
		 * @param l TODO: DOCUMENT ME!
		 */
		public TranslationBundle(ResourceBundle b, Locale l) {
			this.bundle = b;
			this.locale = l;
		}
	}
}
