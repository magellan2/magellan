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

import java.awt.Frame;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;

import java.awt.image.ImageObserver;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;

import java.net.URL;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import com.eressea.GameData;

import com.eressea.event.EventDispatcher;
import com.eressea.event.GameDataEvent;
import com.eressea.event.GameDataListener;
import com.eressea.resource.ResourcePathClassLoader;
import com.eressea.util.logging.Logger;


/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class ImageFactory implements GameDataListener {
	private final static Logger log = Logger.getInstance(ImageFactory.class);

	private static final ImageFactory factory = new ImageFactory();

	private String gamename="eressea";
	
	private ImageFactory() {
		EventDispatcher.getDispatcher().addGameDataListener(this);
	}

	public void gameDataChanged(GameDataEvent e) {
		if(e.getGameData() != null) {
			gamename = e.getGameData().name.toLowerCase();
			if(log.isDebugEnabled()) {
				log.debug("ImageFactory.gameDataChanged: set gamename to "+gamename);
			}
			
			images.clear();
		}
	}
	
	/**
	 * Get the singleton ImageFactory.
	 *
	 * @return the singleton ImageFactory
	 */
	public static ImageFactory getFactory() {
		return factory;
	}

	private Map images = CollectionFactory.createHashMap();

	/**
	 * Loads the given image. First it tests to load
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

		if(log.isDebugEnabled()) {
			log.debug("LoadImage("+name+"): "+(img!=null));
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
	public Image merge(Image rgb, Image alpha) {
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
	
	public void waitForImage(Image img){
		MediaTracker mt = new MediaTracker(new Frame());
		
		try {
			mt.addImage(img, 0);
			mt.waitForAll();
		} catch(InterruptedException e) {
		}
	}

	/**
	 * Load an image by file name. This procedure tries different file formats
	 * in the following order: .png, if not found then .gif. If a .gif file is
	 * available, an optional -alpha.gif file is used for alpha-channel
	 * information. If no such -alpha.gif file can be found, the optional
	 * alpha information in the .gif file is used. If no such file seems to
	 * exist, null is returned. All file names are prepended with the path
	 * 'images/map/'+gamename enforcing that the files are located in such a
	 * sub-directory of the resources root directory /res. If no such image
	 * is found, the fallback to 'images/map/' is used to load the file
	 *
	 * @param fileName a file name without extension.
	 *
	 * @return the image loaded from fileName, or null if not file could be
	 * 		   found.
	 */
	public Image loadCellRendererImage(String fileName) {
		String fName = "images/map/"+fileName.toLowerCase();
		if(images.get(fName) != null) {
			return (Image) images.get(fName);
		}
		
		Image img = loadFile(gamename+"/"+fName);

		if(img == null) {
			img = loadFile(fName);
		}
		if(img != null) {
			images.put(fName, img);
		}
		if(log.isDebugEnabled()) {
			log.debug("Tries to read image "+fileName+ "("+gamename+"): "+(img != null));
		}
		return img;
	}

	/**
	 * Load an image by file name. This procedure tries different file formats
	 * in the following order: .png, if not found then .gif. If a .gif file is
	 * available, an optional -alpha.gif file is used for alpha-channel
	 * information. If no such -alpha.gif file can be found, the optional
	 * alpha information in the .gif file is used. If no such file seems to
	 * exist, null is returned. 
	 *
	 * @param fileName a file name without extension.
	 *
	 * @return the image loaded from fileName, or null if not file could be
	 * 		   found.
	 */
	protected Image loadFile(String file) {

		Image		 imgAlpha = null;
		Image		 imgRGB   = null;
		Image		 img	  = null;
		URL pngURL   = null;
		URL gifURL   = null;
		URL alphaURL = null;

		Collection   names = CollectionFactory.createArrayList(3);
		names.add(file + ".png");
		names.add(file + ".gif");
		names.add(file + "-alpha.gif");

		Collection files = ResourcePathClassLoader.getResourcesStatically(names);

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


		if(log.isDebugEnabled()) {
			log.debug("LoadFile("+file+"): "+pngURL+", "+gifURL+")");
		}

		if(pngURL != null) {
			img = Toolkit.getDefaultToolkit().getImage(pngURL);
		} else {
			if(gifURL != null) {
				if(alphaURL != null) {
					imgRGB   = Toolkit.getDefaultToolkit().getImage(gifURL);
					imgAlpha = Toolkit.getDefaultToolkit().getImage(alphaURL);
					img		 = ImageFactory.getFactory().merge(imgRGB, imgAlpha);
				} else {
					img = Toolkit.getDefaultToolkit().getImage(gifURL);
				}
			}
		}
		return img;
	}
}
