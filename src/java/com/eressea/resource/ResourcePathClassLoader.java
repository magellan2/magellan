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

package com.eressea.resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import com.eressea.util.CollectionFactory;
import com.eressea.util.IteratorEnumeration;
import com.eressea.util.PropertiesHelper;
import com.eressea.util.logging.Logger;

/**
 * Loads classes and resources from a configurable set of places. Well-known
 * local directories and/or contents of the executed jar file are used as
 * fall-back resourcePaths.
 */
public class ResourcePathClassLoader extends ClassLoader {
	private static final Logger log				    = Logger.getInstance(ResourcePathClassLoader.class);
	private static Collection   staticResourcePaths = CollectionFactory.createLinkedList();
	private Collection		    resourcePaths	    = CollectionFactory.createLinkedList();

	/**
	 * Creates a new class loader initializing itself with the specified
	 * settings.
	 *
	 * @param settings TODO: DOCUMENT ME!
	 */
	public ResourcePathClassLoader(Properties settings) {
		this.resourcePaths = loadResourcePaths(settings);
	}

	/**
	 * Initializes this class loader for static access.
	 *
	 * @param settings TODO: DOCUMENT ME!
	 */
	public static void init(Properties settings) {
		staticResourcePaths = loadResourcePaths(settings);
	}

	/**
	 * Returns the resource paths this loader operates on.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Collection getPaths() {
		return CollectionFactory.unmodifiableCollection(this.resourcePaths);
	}

	/**
	 * Tells this loader which resource paths to search for classes and
	 * resources.
	 *
	 * @param paths TODO: DOCUMENT ME!
	 */
	public void setPaths(Collection paths) {
		if(paths == null) {
			this.resourcePaths = CollectionFactory.createLinkedList();
		} else {
			this.resourcePaths = paths;
		}
	}

	/**
	 * Returns the resource paths the static methods of this class operate on.
	 *
	 * @see com.eressea.util.ResourcePathClassLoader#init(java.util.Properties)
	 */
	public static Collection getStaticPaths() {
		return CollectionFactory.unmodifiableCollection(staticResourcePaths);
	}

	/**
	 * Tells this loader which resource paths to search for classes and
	 * resources when operating statically.
	 *
	 * @param paths TODO: DOCUMENT ME!
	 */
	public static void setStaticPaths(Collection paths) {
		if(paths == null) {
			staticResourcePaths = CollectionFactory.createLinkedList();
		} else {
			staticResourcePaths = paths;
		}
	}

	/**
	 * Finds the specified class.
	 *
	 * @param name the name of the class.
	 *
	 * @return the resulting <tt>Class</tt> object.
	 *
	 * @throws ClassNotFoundException if the class could not be found.
	 */
	protected Class findClass(String name) throws ClassNotFoundException {
		URL    url	    = null;
		String fileName = name.replace('.', '/').concat(".class");

		url = getResourceFromPaths(fileName, this.resourcePaths);

		if(url == null) {
			url = getResourceFromCurrentDir(fileName);
		}

		if(url != null) {
			try {
				InputStream is     = url.openStream();
				List	    buffer = CollectionFactory.createLinkedList();
				int		    read   = is.read();

				while(read != -1) {
					buffer.add(new Byte((byte) read));
					read = is.read();
				}

				is.close();

				byte buf[] = new byte[buffer.size()];

				for(int i = 0; i < buffer.size(); i++) {
					buf[i] = ((Byte) buffer.get(i)).byteValue();
				}

				return defineClass(name, buf, 0, buf.length);
			} catch(Exception e) {
				throw new ClassNotFoundException(e.toString());
			}
		} else {
			return findSystemClass(name);
		}
	}

	/**
	 * Finds the resource with the given name.
	 *
	 * @param name the resource name
	 *
	 * @return a URL for reading the resource, or <code>null</code> if the
	 * 		   resource could not be found
	 */
	protected URL findResource(String name) {
		return getResourceStatically(name, this.resourcePaths);
	}

	/**
	 * Finds the resource with the given name.
	 *
	 * @param name the resource name
	 *
	 * @return a URL for reading the resource, or <code>null</code> if the
	 * 		   resource could not be found
	 */
	public static URL getResourceStatically(String name) {
		return getResourceStatically(name, staticResourcePaths);
	}

	/**
	 * Finds the resource with the given name in the given resource paths.
	 *
	 * @param name the resource name
	 * @param resourcePaths additional resource paths to find the resource in
	 *
	 * @return a URL for reading the resource, or <code>null</code> if the
	 * 		   resource could not be found
	 */
	public static URL getResourceStatically(String name,
											Collection resourcePaths) {
		URL url = null;

		url = getResourceFromPaths(name, resourcePaths);

		if(url == null) {
			url = getResourceFromCurrentDir(name);
		}

		// try to get the resource from the class path
		if(url == null) {
			url = getSystemClassLoader().getResource(name);
		}

		// try to get the resource from the jar
		if(url == null) {
			url = getSystemClassLoader().getResource("res/" + name);
		}

		return url;
	}

	/**
	 * Searches the available resource paths for a resource with the specified
	 * name and returns the first match.
	 *
	 * @param name TODO: DOCUMENT ME!
	 * @param resourcePaths TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	private static URL getResourceFromPaths(String name,
											Collection resourcePaths) {
		URL url = null;

		for(Iterator iter = resourcePaths.iterator();
				iter.hasNext() && (url == null);) {
			url = verifyResource((URL) iter.next(), name);
		}

		return url;
	}

	/**
	 * Searches the available resource paths for all resources with the
	 * specified name.
	 *
	 * @param name TODO: DOCUMENT ME!
	 * @param resourcePaths TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	private static Collection getResourcesFromPaths(String name,
													Collection resourcePaths) {
		Collection urls = CollectionFactory.createLinkedList();

		for(Iterator iter = resourcePaths.iterator(); iter.hasNext();) {
			URL url = verifyResource((URL) iter.next(), name);

			if(url != null) {
				urls.add(url);
			}
		}

		return urls;
	}

	/**
	 * Searches the available resource paths for the resources with the
	 * specified names until at least one is found.
	 *
	 * @param names TODO: DOCUMENT ME!
	 * @param resourcePaths TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	private static Collection getResourcesFromPaths(Collection names,
													Collection resourcePaths) {
		Collection urls = CollectionFactory.createLinkedList();

		for(Iterator iter = resourcePaths.iterator();
				iter.hasNext() && (urls.size() == 0);) {
			URL path = (URL) iter.next();

			for(Iterator nameIter = names.iterator(); nameIter.hasNext();) {
				String name = (String) nameIter.next();
				URL    url = verifyResource(path, name);

				if(url != null) {
					urls.add(url);
				}
			}
		}

		return urls;
	}

	/**
	 * Searches the current directory for a resource with the specified name.
	 *
	 * @param name TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	private static URL getResourceFromCurrentDir(String name) {
		URL url = null;

		try {
			File currentDirectory = new File(".");
			URL  baseLocation = new URL(currentDirectory.toURL(), "res/");
			url = verifyResource(baseLocation, name);
		} catch(Exception e) {
			log.error(e);
		}

		return url;
	}

	/**
	 * Searches the current directory for the resources with the specified
	 * names.
	 *
	 * @param names TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	private static Collection getResourcesFromCurrentDir(Collection names) {
		Collection urls = CollectionFactory.createLinkedList();

		try {
			File currentDirectory = new File(".");
			URL  baseLocation = new URL(currentDirectory.toURL(), "res/");

			for(Iterator iter = names.iterator(); iter.hasNext();) {
				String name = (String) iter.next();
				URL    url = verifyResource(baseLocation, name);

				if(url != null) {
					urls.add(url);
				}
			}
		} catch(Exception e) {
			log.error(e);
		}

		return urls;
	}

	/**
	 * Checks if the object specified by the base location and the name exists.
	 * If it does, a valid URL pointing to is returned else null.
	 *
	 * @param location TODO: DOCUMENT ME!
	 * @param name TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	private static URL verifyResource(URL location, String name) {
		URL url = null;

		try {
			url = new URL(location, name);

			InputStream istream = url.openStream();
			istream.close();
		} catch(Exception ex) {
			url = null;
		}

		return url;
	}

	/**
	 * Returns an Enumeration of URLs representing all the resources with the
	 * given name.
	 *
	 * @param name the resource name
	 *
	 * @return an Enumeration of URLs for the resources
	 *
	 * @throws IOException if I/O errors occur
	 */
	protected Enumeration findResources(String name) throws IOException {
		Collection urls = CollectionFactory.createLinkedList();

		urls.addAll(getResourcesFromPaths(name, resourcePaths));

		URL url = getResourceFromCurrentDir(name);

		if(url != null) {
			urls.add(url);
		}

		// try to get the resource from the class path
		url = getSystemClassLoader().getResource(name);

		if(url != null) {
			urls.add(url);
		}

		// try to get the resource from the jar
		url = getSystemClassLoader().getResource("res/" + name);

		if(url != null) {
			urls.add(url);
		}

		return new IteratorEnumeration(urls.iterator());
	}

	/**
	 * Tries to find the specified bunch of resources. As soon as one of the
	 * resources is found in a location no other location is searched but it
	 * is attempted to find as many of the other specified resources in the
	 * same location.
	 *
	 * @param names the resource names
	 * @param resourcePaths additional resource paths to find the resources in
	 *
	 * @return the URLs the found resources can be read from
	 */
	private static Collection getResourcesStatically(Collection names,
													 Collection resourcePaths) {
		Collection urls = getResourcesFromPaths(names, resourcePaths);

		if(urls.size() == 0) {
			urls = getResourcesFromCurrentDir(names);
		}

		// try to get the resource from the class path
		if(urls.size() == 0) {
			for(Iterator iter = names.iterator(); iter.hasNext();) {
				String name = (String) iter.next();
				URL    url = getSystemClassLoader().getResource(name);

				if(url != null) {
					urls.add(url);
				}
			}
		}

		// try to get the resource from the jar
		if(urls.size() == 0) {
			for(Iterator iter = names.iterator(); iter.hasNext();) {
				String name = (String) iter.next();
				URL    url = getSystemClassLoader().getResource("res/" + name);

				if(url != null) {
					urls.add(url);
				}
			}
		}

		return urls;
	}

	/**
	 * Tries to find the specified bunch of resources. As soon as one of the
	 * resources is found in a location no further locations are scanned for
	 * the resources, but it is attempted to find as many of the other
	 * specified resources in the same location.
	 *
	 * @param names the resource names
	 *
	 * @return the URLs the found resources can be read from
	 */
	public static Collection getResourcesStatically(Collection names) {
		return getResourcesStatically(names, staticResourcePaths);
	}

	/**
	 * Tries to find the given resource in all resource paths including working
	 * directory and source jar. It will return all found URLs.
	 *
	 * @param name TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static Collection getResourceFromAllPaths(String name) {
		Collection col = CollectionFactory.createLinkedList();

		URL		   url = getSystemClassLoader().getResource(name);

		if(url != null) {
			col.add(url);
		}

		url = getResourceFromCurrentDir(name);

		if(url != null) {
			col.add(url);
		}

		for(Iterator iter = staticResourcePaths.iterator(); iter.hasNext();) {
			url = verifyResource((URL) iter.next(), name);

			if(url != null) {
				col.add(url);
			}
		}

		if(col.size() == 0) {
			col = null;
		}

		return col;
	}

	/**
	 * Loads the resource paths from the specified settings.
	 *
	 * @param settings TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	private static Collection loadResourcePaths(Properties settings) {
		Collection properties = PropertiesHelper.getList(settings,
														 "Resources.preferredPathList");

		Collection resourcePaths = CollectionFactory.createArrayList(properties.size());

		for(Iterator iter = properties.iterator(); iter.hasNext();) {
			String location = (String) iter.next();

			try {
				resourcePaths.add(new URL(location));
			} catch(MalformedURLException e) {
				log.error(e);
			}
		}

		return resourcePaths;
	}

	/**
	 * Stores the specified resource paths to the specified settings.
	 *
	 * @param resourcePaths TODO: DOCUMENT ME!
	 * @param settings TODO: DOCUMENT ME!
	 */
	public static void storePaths(Collection resourcePaths, Properties settings) {
		if(resourcePaths == null) {
			resourcePaths = CollectionFactory.createLinkedList();
		}

		PropertiesHelper.setList(settings, "Resources.preferredPathList",
								 resourcePaths);
	}
}
