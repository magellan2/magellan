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

package com.eressea.extern;

import java.io.File;
import java.io.IOException;

import java.net.URL;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.eressea.resource.ResourcePathClassLoader;

import com.eressea.util.CollectionFactory;
import com.eressea.util.logging.Logger;

/**
 * Loads all external modules that can be found. Please see com.eressea.extern.ExternalModule for
 * documentation.
 *
 * @author Ulrich Küster
 */
public class ExternalModuleLoader {
	private static final Logger log = Logger.getInstance(ExternalModuleLoader.class);

	/**
	 * Searches the resource paths for classes that implement the interface
	 * com.eressea.extern.ExternalModule. Returns them as Collection of Class objects.
	 *
	 * @param settings TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static Collection getExternalModuleClasses(Properties settings) {
		log.info("Searching for external modules...");

		// found classes
		Collection classes = CollectionFactory.createHashSet();

		//if(settings.getProperty("ExternalModuleLoader.dontSearchExternalModules","false").equals("true")) {
		//	log.info("Searching for external modules disabled.");
		//	return classes;
		//}
		long start = System.currentTimeMillis();

		classes.addAll(getExternalModuleClasses(settings, ExternalModule.class));
		classes.addAll(getExternalModuleClasses(settings, ExternalModule2.class));

		List returnClasses = CollectionFactory.createArrayList();
		returnClasses.addAll(classes);

		// pavkovic 200307.18: deactivated, does not work properly right now
		//Collections.sort(returnClasses);
		long end = System.currentTimeMillis();
		log.info("Searching for external modules done. Found " + returnClasses.size() +
				 " instances in " + String.valueOf((end - start)) + " msecs");

		return returnClasses;
	}

	private static Collection getPathsFromResourcePathClassLoader(ResourcePathClassLoader resLoader,
																  Properties settings) {
		Collection paths = CollectionFactory.createArrayList();

		for(Iterator iter = resLoader.getPaths().iterator(); iter.hasNext();) {
			URL url = (URL) iter.next();
			String s = url.getFile();

			if(s.startsWith("file:/")) {
				s = s.substring(6, s.length());
			}

			if(s.endsWith("!/")) {
				s = s.substring(0, s.length() - 2);
			}

			paths.add(s);
		}

		return paths;
	}

	private static Collection getPathsFromClassPath() {
		Collection paths = CollectionFactory.createArrayList();

		// String classpath = System.getProperty("java.class.path");
		// classpath += System.getProperty("path.separator")+path;
		StringTokenizer st = new StringTokenizer(System.getProperty("java.class.path"),
												 System.getProperty("path.separator"));

		while(st.hasMoreTokens()) {
			paths.add(st.nextToken());
		}

		return paths;
	}

	private static Collection getClassesFromPath(ResourcePathClassLoader resLoader,
												 Class externalModuleClass, String path) {
		return getClassesFromPath(resLoader, externalModuleClass, path, "",
								  getLastCapitalizedString(externalModuleClass.getName())
									  .toLowerCase() + ".class");
	}

	private static Collection getClassesFromPath(ResourcePathClassLoader resLoader,
												 Class externalModuleClass, String path,
												 String packagePrefix, String postfix) {
		Collection classes = CollectionFactory.createArrayList();

		try {
			File file = new File(path);

			if(file.exists()) {
				if(file.isDirectory()) {
					log.info("Searching in " + file.getAbsolutePath() + "...");

					// add files or subdirectories to search list
					File newPaths[] = file.listFiles();

					for(int i = 0; i < newPaths.length; i++) {
						// add in first position
						String newPrefix = packagePrefix + file.getName() +
										   ((packagePrefix == "") ? "" : ".");
						classes.addAll(getClassesFromPath(resLoader, externalModuleClass,
														  newPaths[i].getAbsolutePath(), newPrefix,
														  postfix));
					}
				} else if(file.getName().toLowerCase().endsWith(".jar") ||
							  file.getName().toLowerCase().endsWith(".zip")) {
					log.info("Searching " + file.getAbsolutePath() + "...");

					ZipFile zip = new ZipFile(file);

					for(Enumeration e = zip.entries(); e.hasMoreElements();) {
						ZipEntry entry = (ZipEntry) e.nextElement();

						if(!entry.isDirectory() && entry.getName().toLowerCase().endsWith(postfix)) {
							// class file found!
							// check whether it implements ExternalModule
							String name = entry.getName();
							name = name.substring(0, name.indexOf(".class")).replace('\\', '.')
									   .replace('/', '.');

							Class foundClass = resLoader.loadClass(name);
							Class interfaces[] = foundClass.getInterfaces();
							boolean found = false;

							for(int i = 0; (i < interfaces.length) && !found; i++) {
								if(interfaces[i].equals(externalModuleClass)) {
									found = true;
								}
							}

							if(found) {
								// found a class that implements ExternalModule
								classes.add(foundClass);
								log.info("Found " + foundClass.getName());
							}
						}
					}
				} else if(file.getName().toLowerCase().endsWith(postfix)) {
					String name = file.getName();
					name = name.substring(0, name.indexOf(".class")).replace('\\', '.').replace('/',
																								'.');

					Class foundClass;

					try {
						foundClass = resLoader.loadClass(name);
					} catch(ClassNotFoundException e) {
						// pavkovic 2003.07.09: now retry with prefix
						name = packagePrefix + name;
						foundClass = resLoader.loadClass(name);
					}

					Class interfaces[] = foundClass.getInterfaces();

					for(int i = 0; i < interfaces.length; i++) {
						if(interfaces[i].equals(externalModuleClass)) {
							// found a class that implements ExternalModule
							classes.add(foundClass);
							log.info("Found " + foundClass.getName());

							break;
						}
					}
				}
			} else {
				log.info("File not found: " + file);
			}
		} catch(IOException ioe) {
			log.error(ioe);
		} catch(NoClassDefFoundError ncdfe) {
			log.error(ncdfe);
		} catch(ClassNotFoundException cnfe) {
			log.error(cnfe);
		}

		return classes;
	}

	private static Collection getExternalModuleClasses(Properties settings,
													   Class externalModuleClass) {
		Collection classes = CollectionFactory.createHashSet();

		ResourcePathClassLoader resLoader = new ResourcePathClassLoader(settings);

		// pathes to search
		Collection paths = CollectionFactory.createArrayList();

		// a) read possible paths from ResourcePathClassLoader
		// b) read property java.class.path and iterate over the entries
		if(settings.getProperty("ExternalModuleLoader.searchResourcePathClassLoader", "true")
					   .equals("true")) {
			paths.addAll(getPathsFromResourcePathClassLoader(resLoader, settings));
		}

		if(settings.getProperty("ExternalModuleLoader.searchClassPath", "true").equals("true")) {
			paths.addAll(getPathsFromClassPath());
		}

		for(Iterator iter = paths.iterator(); iter.hasNext();) {
			String path = (String) iter.next();
			classes.addAll(getClassesFromPath(resLoader, externalModuleClass, path));
		}

		return classes;
	}

	/**
	 * delivers last capitalized String, e.g.: for input "StringBuffer.class" this function returns
	 * "Buffer.class"
	 *
	 * @param aString TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	private static String getLastCapitalizedString(String aString) {
		StringCharacterIterator iter = new StringCharacterIterator(aString);

		for(char c = iter.last(); c != CharacterIterator.DONE; c = iter.previous()) {
			if((c >= 'A') && (c <= 'Z')) {
				if(log.isDebugEnabled()) {
					log.debug("ExternalModuleLoader.getLastCapitalizedString(" + aString + "): " +
							  aString.substring(iter.getIndex()));
				}

				return aString.substring(iter.getIndex());
			}
		}

		return aString;
	}
}
