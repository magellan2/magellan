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

package com.eressea.io.file;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import com.eressea.resource.ResourcePathClassLoader;

/**
 * This FileType represent a "File" via an input stream URL. This is a convenient object for
 * encapsulating ClassLoader stuff.
 */
public class InputStreamSourceFileType extends FileType {
	InputStreamSourceFileType(File url) throws IOException {
		super(url, true);
	}

	protected InputStream createInputStream() throws IOException {
		URL url = ResourcePathClassLoader.getResourceStatically(filename.getPath().toLowerCase());

		if(url == null) {
			throw new IOException("URL '" + filename.getPath().toLowerCase() + "' not readable.");
		}

		return url.openStream();
	}

	protected OutputStream createOutputStream() throws IOException {
		throw new IOException("InputStreamSourceFileType does not support writing to a resource.");
	}

	/**
	 * Returns the underlying file.
	 *
	 * @return a File object
	 *
	 * @throws IOException if file cannot be determined, e.g. for  an url pointing to an
	 * 		   InputStream.
	 */
	public File getFile() throws IOException {
		throw new IOException("Unable to determine File for InputStream URL '" + toString() + "'.");
	}
}
