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

import java.io.File;
import java.io.InputStream;

import java.lang.reflect.Constructor;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.eressea.swing.map.CellGeometry;
import com.eressea.swing.map.ExternalMapCellRenderer;
import com.eressea.swing.map.MapCellRenderer;

import com.eressea.util.logging.Logger;

/**
 * DOCUMENT ME!
 *
 * @author Andreas
 * @version
 */
public class RendererLoader extends Object {
	private static final Logger log = Logger.getInstance(RendererLoader.class);
	private File directory;
	private String dirString;
	private ZipClassLoader loader;
	private Class paramClass[];
	private Object paramInst[];
	private Properties settings;
	private static final String RENDERER_CLASS_STRING = "com.eressea.swing.map.ExternalMapCellRenderer";
	private static Class RENDERER_CLASS;

	/**
	 * Creates new RendererLoader
	 *
	 * @param dir TODO: DOCUMENT ME!
	 * @param sDir TODO: DOCUMENT ME!
	 * @param geom TODO: DOCUMENT ME!
	 * @param sett TODO: DOCUMENT ME!
	 *
	 * @throws IllegalArgumentException TODO: DOCUMENT ME!
	 */
	public RendererLoader(File dir, String sDir, CellGeometry geom, Properties sett) {
		try {
			paramClass = new Class[2];
			paramClass[0] = geom.getClass();
			paramClass[1] = (new Properties()).getClass();

			paramInst = new Object[2];
			paramInst[0] = geom;
			paramInst[1] = sett;
			settings = sett;

			directory = new File(dir, sDir);
			;
			loader = new ZipClassLoader();
			dirString = sDir;

			RENDERER_CLASS = Class.forName(RENDERER_CLASS_STRING);
		} catch(Exception exc) {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Collection loadRenderers() {
		log.info("Searching for additional renderers...");

		if(settings.getProperty("RendererLoader.dontSearchAdditionalRenderers", "false").equals("true")) {
			log.info("Searching for additional renderers disabled.");

			return null;
		}

		long start = System.currentTimeMillis();

		try {
			String names[] = directory.list();
			List list = CollectionFactory.createArrayList();

			for(int i = 0; i < names.length; i++) {
				boolean found = false;
				boolean error = false;
				StringBuffer msg = new StringBuffer();

				try {
					if(names[i].endsWith(".jar") || names[i].endsWith(".zip")) {
						msg.append("Checking " + names[i] + "...");

						ZipFile jar = new ZipFile(dirString + File.separator + names[i]);
						loader.setToLoad(jar);

						Enumeration e = jar.entries();

						while(e.hasMoreElements()) {
							ZipEntry next = (ZipEntry) e.nextElement();

							if(!next.isDirectory() && next.getName().endsWith("Renderer.class")) {
								String name = next.getName();
								name = name.substring(0, name.indexOf(".class")).replace('\\', '.')
										   .replace('/', '.');

								try {
									Class rclass = loader.loadClass(name);

									if(isRenderer(rclass)) {
										try {
											try {
												Constructor constr = rclass.getConstructor(paramClass);
												Object obj = constr.newInstance(paramInst);
												loadResourceBundle(jar, obj);
												list.add(obj); // try with arguments
												found = true;
											} catch(Exception parameterException) {
												Object obj = rclass.newInstance();
												loadResourceBundle(jar, obj);
												list.add(obj); // try without arguments
												found = true;
											}
										} catch(Exception loadException) {
											error = true;
											log.info(msg);
											log.info("Unable to load " + rclass.getName() + ':' +
													 loadException + '!');
										}
									}
								} catch(ClassNotFoundException cnfe) {
								}
							}
						}

						if(found) {
							msg.append("Successful!");
							log.info(msg);
						} else if(!error) {
							msg.append("Nothing found!");
							log.info(msg);
						}
					}
				} catch(Exception exc) {
				}
			}

			if(list.size() > 0) {
				Iterator it = list.iterator();
				StringBuffer msg = new StringBuffer();

				if(list.size() > 1) {
					msg.append("Additional renderers(" + list.size() + ") loaded: ");
				} else {
					msg.append("Additional renderer loaded: ");
				}

				while(it.hasNext()) {
					msg.append(((MapCellRenderer) it.next()).getName());

					if(it.hasNext()) {
						msg.append(';');
					}
				}

				log.info(msg);

				long end = System.currentTimeMillis();
				log.info("Searching for additional renderers done. Found " + list.size() +
						 " instances in " + String.valueOf((end - start)) + " msecs");

				return list;
			}
		} catch(Exception exc) {
		}

		long end = System.currentTimeMillis();
		log.info("Searching for additional renderers done. Found " + "0" + " instances in " +
				 String.valueOf((end - start)) + " msecs");

		return null;
	}

	protected void loadResourceBundle(ZipFile jar, Object obj) {
		ResourceBundle rb = Translations.loadResourceBundle("res/lang/" +
															obj.getClass().getName()
															   .replace('.', '-').toLowerCase(),
															Locales.getGUILocale(), loader);

		if(rb == null) {
			throw new RuntimeException("ResourceBundle not found.");
		}

		((ExternalMapCellRenderer) obj).setResourceBundle(rb);
	}

	protected boolean isRenderer(Object cur) {
		if(RENDERER_CLASS != null) {
			return RENDERER_CLASS.isInstance(cur);
		}

		return isRenderer(cur.getClass());
	}

	protected boolean isRenderer(Class cur) {
		Class inf[] = cur.getInterfaces();

		if((inf != null) && (inf.length > 0)) {
			for(int i = 0; i < inf.length; i++) {
				if(inf[i].getName().equals(RENDERER_CLASS_STRING)) {
					return true;
				}
			}
		}

		Class parent = cur.getSuperclass();

		return (parent == null) ? false : isRenderer(parent);
	}

	protected class ZipClassLoader extends ClassLoader {
		protected ZipFile jar = null;

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param jar TODO: DOCUMENT ME!
		 */
		public void setToLoad(ZipFile jar) {
			this.jar = jar;
		}

		protected Class findClass(String name) throws ClassNotFoundException {
			try {
				//find the according entry
				ZipEntry entry = jar.getEntry(name.replace('.', '\\') + ".class");

				if(entry == null) {
					entry = jar.getEntry(name.replace('.', '/') + ".class");
				}

				//allocate buffer
				long size = entry.getSize();
				byte buf[] = new byte[(int) size];

				//open connection
				InputStream in = jar.getInputStream(entry);

				int curSize = 0;
				int i = 0;

				do {
					i = in.read(buf, curSize, (int) (size - curSize));

					if(i != -1) {
						curSize += i;
					}
				} while((curSize < size) && (i != -1));

				in.close();

				if(i == -1) {
					throw new RuntimeException("IO Error.");
				}

				return defineClass(name, buf, 0, buf.length);
			} catch(Exception exc) {
				throw new ClassNotFoundException(exc.toString());
			}
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param name TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public InputStream getResourceAsStream(String name) {
			try {
				ZipEntry zip = jar.getEntry(name);

				if(zip == null) {
					zip = jar.getEntry(name.replace('/', '\\'));
				}

				if(zip == null) {
					zip = jar.getEntry(name.replace('\\', '/'));
				}

				return jar.getInputStream(zip);
			} catch(Exception exc) {
				// FIXME(pavkovic): it is generally a bad idea to catch java.lang.Exception
			}

			return null;
		}
	}
}
