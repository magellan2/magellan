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

package com.eressea.io.file;

import java.io.File;
import java.io.IOException;

import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.eressea.io.MissingInputException;

/**
 * A class to create FileTypes
 */
public class FileTypeFactory {
	private FileTypeFactory() {
	}

	private static FileTypeFactory singleton = new FileTypeFactory();

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static FileTypeFactory singleton() {
		return singleton;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param fileName TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public FileType createInputStreamSourceFileType(String fileName)
											 throws IOException
	{
		return new InputStreamSourceFileType(fileName).checkConnection();
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param fileName TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public FileType createFileType(File fileName) throws IOException {
		return createFileType(fileName.getPath());
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param fileName TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public FileType createFileType(String fileName) throws IOException {
		return createFileType(fileName, null);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param fileName TODO: DOCUMENT ME!
	 * @param ftc TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public FileType createFileType(String fileName, FileTypeChooser ftc)
							throws IOException
	{
		return doCreateFileType(fileName, ftc).checkConnection();
	}

	private FileType doCreateFileType(String fileName, FileTypeChooser ftc)
							   throws IOException
	{
		if(fileName == null) {
			throw new NullPointerException();
		}

		String fileNameLC = fileName.toLowerCase();

		if(fileNameLC.endsWith(FileType.GZIP)) {
			return new GZipFileType(fileName);
		}

		if(fileNameLC.endsWith(FileType.BZIP2)) {
			return new BZip2FileType(fileName);
		}

		if(fileNameLC.endsWith(FileType.ZIP)) {
			return createZipFileType(fileName, ftc);
		}

		if(fileNameLC.endsWith(FileType.CR) ||
			   fileNameLC.endsWith(FileType.XML)) {
			return new FileType(fileName);
		}

		return new UnknownFileType(fileName);
	}

	private static final String ENDINGS[] = new String[] {
												FileType.CR, FileType.XML
											};

	protected FileType createZipFileType(String fileName, FileTypeChooser ftc)
								  throws IOException
	{
		ZipFile  zFile = new ZipFile(fileName);

		ZipEntry entries[] = ZipFileType.getZipEntries(zFile, ENDINGS);

		if(entries.length == 0) {
			throw new MissingInputException();
		}

		if(entries.length == 1) {
			return new ZipFileType(fileName, entries[0]);
		}

		// entries > 1, so we need to choose one
		if(ftc == null) {
			throw new MissingInputException();
		}

		ZipEntry chosenEntry = ftc.chooseZipEntry(entries);

		if(chosenEntry == null) {
			throw new MissingInputException();
		}

		return new ZipFileType(fileName, chosenEntry);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @author $author$
	 * @version $Revision$
	 */
	public static class FileTypeChooser {
		/**
		 * Choose a zip entry of a list of entries
		 *
		 * @param entries TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public ZipEntry chooseZipEntry(ZipEntry entries[]) {
			return null;
		}
	}
}
