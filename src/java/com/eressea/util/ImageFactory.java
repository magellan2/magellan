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
 * $Id$
 */

package com.eressea.util;

import java.awt.Image;
import java.awt.Toolkit;

import java.net.URL;

import java.util.Map;

import com.eressea.resource.ResourcePathClassLoader;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class ImageFactory {
	private static final ImageFactory factory = new ImageFactory();

	private ImageFactory() {
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static ImageFactory getFactory() {
		return factory;
	}

	private Map images = CollectionFactory.createHashMap();

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param name TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Image loadImage(String name) {
		Image img = (Image) images.get(name);

		if(img == null) {
			URL url = ResourcePathClassLoader.getResourceStatically(name);

			if(url != null) {
				img = Toolkit.getDefaultToolkit().getImage(url);
			}

			images.put(name, img);
		}

		return img;
	}
}
