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

package com.eressea.swing.map;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.ImageObserver;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;

import java.net.URL;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;

import com.eressea.util.logging.Logger;

/**
 * A template for a renderer that uses images for rendering objects. This class
 * takes care of dynamic loading and proper scaling of the images. All images
 * are loaded from the images/map/ sub-directory of the current resource
 * bundle.
 */
public abstract class ImageCellRenderer extends HexCellRenderer {
	private static final Logger log     = Logger.getInstance(ImageCellRenderer.class);
	private Map				    images  = new HashMap();
	private static MediaTracker tracker = null;

	/**
	 * Creates a new ImageCellRenderer with the specified cell geometry and a
	 * Properties object to read the render settings from.
	 *
	 * @param geo TODO: DOCUMENT ME!
	 * @param settings TODO: DOCUMENT ME!
	 */
	public ImageCellRenderer(CellGeometry geo, Properties settings) {
		super(geo, settings);
	}

	/**
	 * Scale all images this renderer uses to a certain scale factor.
	 *
	 * @param scaleFactor the factor to scale the images with (a scaleFactor of
	 * 		  1.0 would scale all images to their original size).
	 */
	public void scale(float scaleFactor) {
		super.scale(scaleFactor);

		for(Iterator iter = images.values().iterator(); iter.hasNext();) {
			ImageContainer c = (ImageContainer) iter.next();

			if(c != null) {
				c.scaled = scale(c.unscaled);
			}
		}
	}

	/**
	 * Return a scaled version of the supplied using the current scale factor.
	 * If there is no media tracker, this function enforces synchronous
	 * scaling.
	 *
	 * @param img the img to scale
	 *
	 * @return a scaled instance of img or null, if img is null.
	 */
	public Image scale(Image img) {
		if(img != null) {
			Dimension size   = cellGeo.getImageSize();
			Image     scaled = img.getScaledInstance(size.width, size.height,
													 Image.SCALE_SMOOTH);

			if(tracker != null) {
				tracker.addImage(scaled,
								 (int) (Math.random() * Integer.MAX_VALUE));
			} else {
				waitForImage(scaled);
			}

			return scaled;
		} else {
			return null;
		}
	}

	/**
	 * method waits for loading of specified image.
	 *
	 * @param img TODO: DOCUMENT ME!
	 */
	private void waitForImage(Image img) {
		MediaTracker mt = new MediaTracker(new Frame());

		try {
			mt.addImage(img, 0);
			mt.waitForAll();
		} catch(InterruptedException e) {
		}
	}

	/**
	 * Make the renderer reload all of its cached images.
	 */
	public void reloadImages() {
		images.clear();
	}

	/**
	 * Set the cell geometry this renderer is based on and make it reload all
	 * of its cached images.
	 *
	 * @param geo TODO: DOCUMENT ME!
	 */
	public void setCellGeometry(CellGeometry geo) {
		super.setCellGeometry(geo);
		reloadImages();
	}

	/**
	 * Set a media tracker that is used to track all images that are scaled. If
	 * no media tracker is present scaling is synchronous.
	 *
	 * @param t TODO: DOCUMENT ME!
	 */
	public static void setTracker(MediaTracker t) {
		tracker = t;
	}

	/**
	 * Load an image by file name. This procedure tries different file formats
	 * in the following order: .png, if not found then .gif. If a .gif file is
	 * available, an optional -alpha.gif file is used for alpha-channel
	 * information. If no such -alpha.gif file can be found, the optional
	 * alpha information in the .gif file is used. If no such file seems to
	 * exist, null is returned. All file names are prepended with the path
	 * 'images/map/' enforcing that the files are located in such a
	 * sub-directory of the resources root directory /res.
	 *
	 * @param fileName a file name without extension.
	 *
	 * @return the image loaded from fileName, or null if not file could be
	 * 		   found.
	 */
	protected Image loadFile(String fileName) {
		String		 file     = "images/map/" + fileName.toLowerCase();
		Image		 imgAlpha = null;
		Image		 imgRGB   = null;
		Image		 img	  = null;
		java.net.URL pngURL   = null;
		java.net.URL gifURL   = null;
		java.net.URL alphaURL = null;

		Collection   names = new LinkedList();
		names.add(file + ".png");
		names.add(file + ".gif");
		names.add(file + "-alpha.gif");

		Collection files = com.eressea.resource.ResourcePathClassLoader.getResourcesStatically(names);

		for(Iterator iter = files.iterator(); iter.hasNext();) {
			URL    url = (URL) iter.next();
			String u = url.toString();

			if(u.endsWith(".png")) {
				pngURL = url;

				continue;
			} else if(u.endsWith("-alpha.gif")) {
				alphaURL = url;

				continue;
			} else if(u.endsWith(".gif")) {
				gifURL = url;

				continue;
			}
		}

		if(pngURL != null) {
			img = Toolkit.getDefaultToolkit().getImage(pngURL);
		} else {
			if(gifURL != null) {
				if(alphaURL != null) {
					imgRGB   = Toolkit.getDefaultToolkit().getImage(gifURL);
					imgAlpha = Toolkit.getDefaultToolkit().getImage(alphaURL);
					img		 = merge(imgRGB, imgAlpha);
				} else {
					img = Toolkit.getDefaultToolkit().getImage(gifURL);
				}
			}
		}

		return img;
	}

	/**
	 * Combine two images of equal size to one, where the resulting image
	 * contains the RGB information of the first image directly and the RGB
	 * information of the second one as alpha channel information.
	 *
	 * @param rgb the image to take rgb information from.
	 * @param alpha the image to take the alpha channel information from.
	 *
	 * @return the composite image, or null if rgb or alpha were null or they
	 * 		   were not of equal size.
	 */
	protected Image merge(Image rgb, Image alpha) {
		Image result = null;

		if((rgb == null) || (alpha == null)) {
			return result;
		}

		// pavkovic 2002.06.05: change way to wait for image data. This should dramatically
		// reduce the number of calls to getWidth and getHeight 
		waitForImage(rgb);

		int			   w = rgb.getWidth(null);
		int			   h = rgb.getHeight(null);

		int			   pixelsRGB[]   = new int[w * h];
		int			   pixelsAlpha[] = new int[pixelsRGB.length];
		PixelGrabber   pgRGB		 = new PixelGrabber(rgb, 0, 0, w, h,
														pixelsRGB, 0, w);
		PixelGrabber   pgAlpha = new PixelGrabber(alpha, 0, 0, w, h,
												  pixelsAlpha, 0, w);

		try {
			pgRGB.grabPixels();
			pgAlpha.grabPixels();
		} catch(InterruptedException e) {
			log.warn("interrupted waiting for pixels!");

			return null;
		}

		if(((pgRGB.getStatus() & ImageObserver.ABORT) != 0) ||
			   ((pgAlpha.getStatus() & ImageObserver.ABORT) != 0)) {
			log.warn("image fetch aborted or errored");

			return null;
		}

		for(int i = 0; i < pixelsRGB.length; i++) {
			pixelsRGB[i] &= (((pixelsAlpha[i] & 0x000000FF) << 24) |
			0x00FFFFFF);
		}

		result = Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(w,
																			   h,
																			   pixelsRGB,
																			   0,
																			   w));

		return result;
	}

	/**
	 * Returns an image that is associated with name. If name has never been
	 * supplied to this function before, it attempts to load an image with a
	 * file name of name. If no such file exists, there will be no further
	 * attempts to load the file when this function is called with the same
	 * value for name.
	 *
	 * @param name a name identifying the image to get. This name is also used
	 * 		  as a file name without extension to load the image from a file.
	 *
	 * @return the image associated with name or null, if there is no such
	 * 		   image and it cannot be loaded.
	 */
	protected Image getImage(String name) {
		Image img = null;

		if(name != null) {
			String normName = com.eressea.util.Umlaut.convertUmlauts(name);

			if(images.containsKey(normName)) {
				ImageContainer c = (ImageContainer) images.get(normName);

				if(c != null) {
					img = c.scaled;
				}
			} else {
				img = loadFile(normName);

				if(img != null) {
					// add loaded image to map
					images.put(normName, new ImageContainer(img, scale(img)));
				} else {
					// add null to the map so we do not attempt to load the file again
					images.put(normName, null);
				}
			}
		}

		return img;
	}

	protected class ImageContainer {
		/** TODO: DOCUMENT ME! */
		public Image unscaled = null;

		/** TODO: DOCUMENT ME! */
		public Image scaled = null;

		/**
		 * Creates a new ImageContainer object.
		 *
		 * @param unscaled TODO: DOCUMENT ME!
		 * @param scaled TODO: DOCUMENT ME!
		 */
		public ImageContainer(Image unscaled, Image scaled) {
			this.unscaled = unscaled;
			this.scaled   = scaled;
		}
	}
}
