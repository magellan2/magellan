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

package com.eressea.io.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

/**
 * A FileType represents a file for reading and writing data Special care will
 * be taken in the corresponding child objects  for gzip, bzip2 and zip files.
 */
public class FileType {
	// basically identified file types
	static final String CR  = ".cr";
	static final String XML = ".xml";

	// basically identified compression types with single entry
	static final String GZIP  = ".gz";
	static final String BZIP2 = ".bz2";

	// basically identified compression types with multiple entries
	static final String ZIP = ".zip";

	/** The file this file type identifies. */
	protected String filename;

	/** true if file will opened for writing. */
	protected boolean writeFile;

	FileType(String aFile) throws IOException {
		this(aFile, false);
	}

	FileType(String aFile, boolean writeFile) throws IOException {
		if(aFile == null) {
			throw new IOException();
		}

		filename = aFile;

		this.writeFile = writeFile;
	}

	/**
	 * Tests if an InputStream can be opened for this FileType.
	 *
	 * @return TODO: DOCUMENT ME!
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public FileType checkConnection() throws IOException {
		try {
			createInputStream().close();
		} catch(FileNotFoundException e) {
			// it may be ok, if file does not exist 
		}

		return this;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public File getFile() throws IOException {
		return new File(getName());
	}

	/**
	 * return the most inner name of the FileType. Will be overwritten in
	 * ZipFileType
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getInnerName() {
		return getName();
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getName() {
		return filename;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String toString() {
		return getName();
	}

	/**
	 * Creates a Reader for this FileType.
	 *
	 * @return TODO: DOCUMENT ME!
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public Reader createReader() throws IOException {
		return new BufferedReader(FileType.createEncodingReader(createInputStream()));
	}

	/**
	 * Creates a backup of the underlying file and creates a Writer for this
	 * FileType.
	 *
	 * @return TODO: DOCUMENT ME!
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public Writer createWriter() throws IOException {
		// TODO if(!writeFile) throw new IOException("File may not be opened in write mode!");
		FileBackup.create(new File(filename));

		return new BufferedWriter(FileType.createEncodingWriter(createOutputStream()));
	}

	/**
	 * Creates an InputStream for the underlying file.
	 *
	 * @return TODO: DOCUMENT ME!
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	protected InputStream createInputStream() throws IOException {
		return new FileInputStream(new File(filename));
	}

	/**
	 * Creates an OutputStream for the underlying file.
	 *
	 * @return TODO: DOCUMENT ME!
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	protected OutputStream createOutputStream() throws IOException {
		return new FileOutputStream(new File(filename));
	}

	/**
	 * Creates a Reader with the default encoding iso-8859-1
	 *
	 * @param is TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public static Reader createEncodingReader(InputStream is)
									   throws IOException
	{
		return new InputStreamReader(is, DEFAULT_ENCODING);
	}

	/**
	 * Creates a Writer with the default encoding specified in
	 *
	 * @param os TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public static Writer createEncodingWriter(OutputStream os)
									   throws IOException
	{
		return new OutputStreamWriter(os, DEFAULT_ENCODING);
	}

	/** TODO: DOCUMENT ME! */
	public static final String DEFAULT_ENCODING = "iso-8859-1";
}
