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

import com.eressea.util.logging.Logger;

/**
 * A FileType represents a file for reading and writing data. Special care will be taken for
 * compressed files in the corresponding child objects.
 */
public class FileType {
    private final static Logger log = Logger.getInstance(FileType.class);
    
	// basically identified file types
	public static final String CR = ".cr";
	public static final String XML = ".xml";

	// basically identified compression types with single entry
	static final String GZIP = ".gz";
	static final String BZIP2 = ".bz2";

	// basically identified compression types with multiple entries
	static final String ZIP = ".zip";

	/** The file this file type identifies. */
	protected File filename;

	/** true iff file is readonly. */
	protected boolean readonly = false;
	protected boolean createBackup = true;

	public FileType(File aFile, boolean readonly) throws IOException {
		if(aFile == null) {
			throw new IOException();
		}

		filename = aFile;

		this.readonly = readonly;
	}

	/**
	 * Sets if file is readonly
	 *
	 * @param readonly TODO: DOCUMENT ME!
	 */
	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param aCreateBackup TODO: DOCUMENT ME!
	 */
	public void setCreateBackup(boolean aCreateBackup) {
		createBackup = aCreateBackup;
	}

	/**
	 * Tests if an InputStream can be opened for this FileType.
	 *
	 * @return <code>this</code>
	 *
	 * @throws IOException
	 */
	public FileType checkConnection() throws IOException {
		try {
//            if(readonly) {
                createInputStream().close();
//            } else {
//                createOutputStream().close();
//            }
		} catch(FileNotFoundException e) {
			// if file is readonly, this will be a problem
			// if not, it may be ok that the file does not exist 
			if(readonly) {
				throw e;
			}
		}

		return this;
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
		return new File(getName());
	}

	/**
	 * Returns the most inner name of the FileType. Will be overwritten in ZipFileType
	 *
	 * @return the most inner name of a FileType.
	 */
	public String getInnerName() {
		return getName();
	}


	/**
	 * Returns the name of the FileType.
	 *
	 * @return the name of the FileType
	 */
	public String getName() {
		return filename.getAbsolutePath();
	}

	/**
	 * Returns a String representation of the FileType.
	 *
	 * @return a String representation of the FileType.
	 */
	public String toString() {
		if(getInnerName() == null) {
			return getName();
		} else {
			return getName() + " (" + getInnerName() + ")";
		}
	}

	/**
	 * Creates a Reader for this FileType.
	 *
	 * @return a Reader of the underlying File.
	 *
	 * @throws IOException
	 */
	public Reader createReader() throws IOException {
		return new BufferedReader(FileType.createEncodingReader(createInputStream()));
	}

	/**
	 * Creates a backup of the underlying file and returns  a Writer for this.
	 *
	 * @return a Writer of the underlying File.
	 *
	 * @throws IOException If file is marked as readonly or  another IOException occured.
	 * @throws ReadOnlyException TODO: DOCUMENT ME!
	 */
	public Writer createWriter() throws IOException {
		if(readonly) {
			throw new ReadOnlyException();
		}

		if(createBackup) {
            File backup = FileBackup.create(filename);
            log.info("Created backupfile " + backup +" (FileType.java)");
		}

		return new BufferedWriter(FileType.createEncodingWriter(createOutputStream()));
	}

	/**
	 * Creates an InputStream for the underlying file.
	 *
	 * @return an InputStream of the underlying file.
	 *
	 * @throws IOException
	 */
	protected InputStream createInputStream() throws IOException {
		return new FileInputStream(filename);
	}

	/**
	 * Creates an OutputStream for the underlying file.
	 *
	 * @return an OutputStream of the underlying file.
	 *
	 * @throws IOException
	 */
	protected OutputStream createOutputStream() throws IOException {
		return new FileOutputStream(filename);
	}

	/**
	 * Creates a Reader with the default encoding iso-8859-1.
	 *
	 * @param is the InputStream
	 *
	 * @return a Reader for the given InputStream
	 *
	 * @throws IOException
	 */
	public static Reader createEncodingReader(InputStream is) throws IOException {
		return new InputStreamReader(is, DEFAULT_ENCODING);
	}

	/**
	 * Creates a Writer with the default encoding iso-8859-1.
	 *
	 * @param os the OutputStream
	 *
	 * @return a Writer for the given OutputStream
	 *
	 * @throws IOException
	 */
	public static OutputStreamWriter createEncodingWriter(OutputStream os)
												   throws IOException
	{
		return new OutputStreamWriter(os, DEFAULT_ENCODING);
	}

	/** A String representation of the default encoding. */
	public static final String DEFAULT_ENCODING = "iso-8859-1";

	/**
	 * Determines, whether a file is of XML filetype, moved from
	 * com.eressea.io.GameDataReader
	 * by Jonathan 20060917 (Fiete)
	 *
	 * @return true, if the file is of XML type
	 *
	 * @throws IOException
	 */
	public boolean isXMLFile() throws IOException {
        return getInnerName().endsWith(FileType.XML);
	}

	/**
	 * Determines, whether a file is of CR filetype, moved from
	 * com.eressea.io.GameDataReader
	 * by Jonathan 20060917 (Fiete)
	 *
	 * @return true, if the file is of CR type or of unknown type
	 *
	 * @throws IOException
	 */
	public boolean isCRFile() throws IOException {
        /* Unknown files are treated like CR files
         */
        return getInnerName().endsWith(FileType.CR) || this instanceof UnknownFileType;
	}

	/**
	 * Determines, whether a file is a ZIP filetype
	 * by Jonathan 20060917 (Fiete)
	 * @return true, if the file is of ZIP type
	 *
	 * @throws IOException
	 */
	public boolean isZIPFile() throws IOException {
        return this instanceof ZipFileType;
	}

	/**
	 * Determines, whether a file is a GZIP filetype
	 * by Jonathan 20060917 (Fiete)
	 * @return true, if the file is of GZIP type
	 *
	 * @throws IOException
	 */
	public boolean isGZIPFile() throws IOException {
        return this instanceof GZipFileType;
	}

	/**
	 * Determines, whether a file is a BZIP2 filetype
	 * by Jonathan 20060917 (Fiete)
	 * @return true, if the file is of BZIP2 type
	 *
	 * @throws IOException
	 */
	public boolean isBZIP2File() throws IOException {
        return this instanceof BZip2FileType;
	}	
	
	
	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @author $author$
	 * @version $Revision$
	 */
	public static class ReadOnlyException extends IOException {
	}
}