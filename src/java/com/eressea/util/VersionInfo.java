// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.util;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import com.eressea.util.logging.Logger;

/**
 * A class for retrieving versioning information about Magellan.
 */
public class VersionInfo {
	private final static Logger log = Logger.getInstance(VersionInfo.class);

	/**
	 * Returns the build date of the currently executing instance of
	 * Magellan. If this information cannot be determined, null is
	 * returned.
	 */
	public static Date getBuildDate() {
		return readDate(com.eressea.resource.ResourcePathClassLoader.getResourceStatically("build.txt"));
	}
	
	/**
	 * Returns the build date of the latest version of Magellan that
	 * is available on the server. If this information cannot be
	 * determined, null is returned.
	 */
	public static Date getServerBuildDate() {
		try {
			return readDate(new URL("http://eressea.upb.de/magellan/downloads/pkg/res/build.txt"));
		} catch (MalformedURLException e) {
		}
		return null;
	}
	
	private static Date readDate(URL url) {
		Date d = null;

		if (url != null) {
			try {
				BufferedReader r = new BufferedReader(new InputStreamReader(url.openStream()));
				long msecs = Long.parseLong(r.readLine()) * 1000;
				r.close();
				d = new Date(msecs);
			} catch (Exception e) {
				log.info("VersionInfo.readDate(): unable to read from URL: " + e.toString());
			}
		}
		
		return d;
	}
}
