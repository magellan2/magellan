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

package com.eressea.util;

import java.awt.image.BufferedImage;

/**
 * DOCUMENT ME!
 *
 * @author
 * @version
 */
public class PNG extends Object {
	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param bimg TODO: DOCUMENT ME!
	 * @param out TODO: DOCUMENT ME!
	 *
	 * @throws java.io.IOException TODO: DOCUMENT ME!
	 */
	public static void savePNG(BufferedImage bimg, java.io.OutputStream out)
						throws java.io.IOException
	{
		/*com.sun.media.jai.codec.PNGEncodeParam pngEncodeParam =
		  com.sun.media.jai.codec.PNGEncodeParam.getDefaultEncodeParam(bimg);

		com.sun.media.jai.codec.ImageEncoder enc =
		  com.sun.media.jai.codec.ImageCodec.createImageEncoder("PNG", out, pngEncodeParam);
		enc.encode(bimg);*/
	}
}
