package com.eressea.io.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


public class GZipFileType extends FileType {
	GZipFileType(String aFile) throws IOException  {
		super(aFile);
	}

	protected InputStream createInputStream() throws IOException {
		return new GZIPInputStream(new FileInputStream(new File(filename)));
	}
	
	protected OutputStream createOutputStream() throws IOException {
		return new GZIPOutputStream(new FileOutputStream(new File(filename)));
	}
}
