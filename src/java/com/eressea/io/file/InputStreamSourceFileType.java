package com.eressea.io.file;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import com.eressea.resource.ResourcePathClassLoader;


/** 
 * This FileType represent a "File" via an input stream URL.
 * This is a convenient object for encapsulating ClassLoader stuff
 */
public class InputStreamSourceFileType extends FileType {
	InputStreamSourceFileType(String url) throws IOException {
		super(url);
	}

	protected InputStream createInputStream() throws IOException {
		URL url = ResourcePathClassLoader.getResourceStatically(filename.toLowerCase());
		if(url == null) {
			throw new IOException("Resource '"+filename+"' not readable.");
		}
		return url.openStream();
	}

	protected OutputStream createOutputStream() throws IOException{
		throw new IOException("InputStreamSourceFileType does not support writing to a resource."); 
	}
	

	public File getFile() throws IOException {
		throw new IOException("Unable to determine File for InputStream resource '"+toString()+"'.");
	}

}


