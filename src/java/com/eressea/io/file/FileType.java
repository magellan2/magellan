package com.eressea.io.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

/**
 * A FileType represents a file for reading and writing data
 * Special care will be taken in the corresponding child objects 
 * for gzip, bzip2 and zip files.
 */
public class FileType {

	// basically identified file types
	final static String  CR   = ".cr";
	final static String  XML  = ".xml";

	// basically identified compression types with single entry
	final static String    GZIP = ".gz";
	final static String    BZIP2= ".bz2";

	// basically identified compression types with multiple entries
	final static String    ZIP  = ".zip";

	/** The file this file type identifies */
	protected String   filename;

	FileType(String aFile) throws IOException {
		if(aFile == null) throw new IOException();
		filename=aFile;
	}

	/** 
	 * Tests if an InputStream can be opened for this FileType
	 */
	public FileType checkConnection() throws IOException {
		createInputStream().close();
		return this;
	}

	public File getFile() throws IOException {
		return new File(getName());
	}
	
	/**
	 * return the most inner name of the FileType. Will be overwritten in
	 * ZipFileType
	 */
	public String getInnerName() {
		return getName();
	}

	public String getName() {
		return filename;
	}

	public String toString() {
		return getName();
	}

	/** 
	 * Creates a Reader for this FileType.
	 */
	public Reader createReader() throws IOException {
		return new BufferedReader(FileType.createEncodingReader(createInputStream()));
	}

	/** 
	 * Creates a backup of the underlying file and creates a Writer for this FileType.
	 */
	public Writer createWriter() throws IOException {
		FileBackup.create(new File(filename));
		return new BufferedWriter(FileType.createEncodingWriter(createOutputStream()));
	}

	/** 
	 * Creates an InputStream for the underlying file.
	 */
	protected InputStream createInputStream() throws IOException {
		return new FileInputStream(new File(filename));
	}

	/** 
	 * Creates an OutputStream for the underlying file.
	 */	
	protected OutputStream createOutputStream() throws IOException{
		return new FileOutputStream(new File(filename));
	}

	/** 
	 * Creates a Reader with the default encoding iso-8859-1
	 */
	public static Reader createEncodingReader(InputStream is) throws IOException {
		return new InputStreamReader(is,DEFAULT_ENCODING);
	}

	/** 
	 * Creates a Writer with the default encoding specified in 
	 */
	public static Writer createEncodingWriter(OutputStream os) throws IOException {
		return new OutputStreamWriter(os,DEFAULT_ENCODING);
	}

	public final static String DEFAULT_ENCODING="iso-8859-1";
}
