package com.eressea.util;

import java.awt.Image;
import java.awt.Toolkit;

import java.net.URL;

import java.util.Map;

import com.eressea.resource.ResourcePathClassLoader;

public class ImageFactory {
	
	private final static ImageFactory factory = new ImageFactory();
		
	private ImageFactory() {}
	
	public static ImageFactory getFactory() {
		return factory;
	}
	
	private Map images = CollectionFactory.createHashMap();
	public Image loadImage(String name) {
		Image img = (Image) images.get(name);
		if(img == null) {
			URL url = ResourcePathClassLoader.getResourceStatically(name);
			if(url != null) {
				img = Toolkit.getDefaultToolkit().getImage( url );
			}
			images.put(name,img);
		}
		return img;
	}
	
}
	
