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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import com.eressea.resource.ResourcePathClassLoader;
import com.eressea.util.logging.Logger;

/**
 * A class for retrieving versioning information about Magellan.
 */
public class VersionInfo {
	private static final Logger log = Logger.getInstance(VersionInfo.class);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static String getVersion() {
		URL url = ResourcePathClassLoader.getResourceStatically("res/VERSION");

		if(url != null) {
			try {
				ResourceBundle bundle = new PropertyResourceBundle(url.openStream());

				if(bundle != null) {
					return bundle.getString("VERSION");
				}
			} catch(IOException e) {
			} catch(MissingResourceException e) {
			}
		}

		return null;
	}

	/**
	 * Returns the build date of the currently executing instance of Magellan. If this information
	 * cannot be determined, null is returned.
	 *
	 * @return TODO: DOCUMENT ME!
	 *
	 * @deprecated
	 */
	public static Date getBuildDate() {
		return readDate(ResourcePathClassLoader.getResourceStatically("build.txt"));
	}

	/**
	 * Returns the build date of the latest version of Magellan that is available on the server. If
	 * this information cannot be determined, null is returned.
	 *
	 * @return TODO: DOCUMENT ME!
	 *
	 * @deprecated
	 */
	public static Date getServerBuildDate() {
		try {
			return readDate(new URL("http://eressea.upb.de/magellan/downloads/pkg/res/build.txt"));
		} catch(MalformedURLException e) {
			return null;
		}
	}

	private static Date readDate(URL url) {
		if(url == null) {
			return null;
		}

		BufferedReader r = null;

		try {
			r = new BufferedReader(new InputStreamReader(url.openStream()));

			long msecs = Long.parseLong(r.readLine()) * 1000;

			return new Date(msecs);
		} catch(Exception e) {
			log.info("VersionInfo.readDate(): unable to read from URL: " + e.toString());
		} finally {
			if(r != null) {
				try {
					r.close();
				} catch(IOException e) {
				}
			}
		}

		return null;
	}
}
