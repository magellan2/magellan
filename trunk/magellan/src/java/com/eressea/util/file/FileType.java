package com.eressea.util.file;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;


import java.util.Enumeration;

import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

// for MissingCRException
import com.eressea.cr.Loader;

// the * is used on purpose, dont change!
import org.apache.tools.bzip2.*;


public class FileType {
	public final static String DEFAULT_ENCODING="iso-8859-1";

	public final static int CR =0;
	public final static int GZ =1;
	public final static int BZ2=2;
	public final static int ZIP=3;

	private String   filename;
	private ZipEntry zipentry = null;
	private int type = -1;

	private FileType(ZipFile aFile, ZipEntry aEntry) {
		this(aFile.getName());
		zipentry = new ZipEntry(aEntry);
		type = ZIP;
	}
	private FileType(File aFile) {
		this(aFile.getPath());
	}
	private FileType(String aFile) {
		filename=aFile;
	}
	public FileType(FileType clone) {
		filename=clone.filename;
		zipentry=clone.zipentry;
		type=clone.type;
	}
	
	public static FileType createFileType(File aFile) {
		return new FileType(aFile);
	}

	public static FileType createFileType(String aFile) {
		return new FileType(aFile);
	}
	public static FileType createFileType(ZipFile aFile, ZipEntry aZipEntry) {
		return new FileType(aFile, aZipEntry);
	}

	private static int determineType(String aFilename) {
		int ret = CR;
		if(aFilename.toLowerCase().endsWith(".gz")) {
			ret = GZ;
		}
		if(aFilename.toLowerCase().endsWith(".bz2")) {
			ret = BZ2;
		}
		if(aFilename.toLowerCase().endsWith(".zip")) {
			ret = ZIP;
		} 
		return ret;
	}

	private int determineType() {
		if(type == -1) {
			type = determineType(filename);
		}
		return type;
	}
	

	public Reader createReader() throws IOException {
		return FileType.createEncodingReader(createInputStream());
	}

	public Writer createWriter() throws IOException {
		return FileType.createEncodingWriter(createOutputStream());
	}

	protected OutputStream createOutputStream() throws IOException {
		FileBackup.create(new File(filename));
		switch(determineType()) {
		case CR: 
			return new FileOutputStream(new File(filename));
		case GZ: 
			return new GZIPOutputStream(new FileOutputStream(new File(filename)));
		case BZ2:
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(filename)));
			bos.write('B');
			bos.write('Z');
			return new CBZip2OutputStream(bos);
		case ZIP:
			return createZipOutputStream();
		default:
			throw new IOException("Undeterminated file type for file "+filename);
		}
	}
	
	private OutputStream createZipOutputStream() throws IOException {
		// here we need to do something special: all entries are copied expect the named zipenty, which will be overwritten
		File tmpfile = CopyFile.copy(new File(filename));
		ZipFile zfile = new ZipFile(tmpfile);
		ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(new File(filename)));
		for(Enumeration e = zfile.entries(); e.hasMoreElements(); ) {
			ZipEntry oldEntry = (ZipEntry) e.nextElement();
			if(!oldEntry.getName().equals(zipentry.getName())) {
				zos.putNextEntry(oldEntry);
				CopyFile.copyStreams(zfile.getInputStream(oldEntry), zos);
			}
		}
		zos.putNextEntry(zipentry);
		return zos;
	}

	protected InputStream createInputStream() throws IOException {
		switch(determineType()) {
		case CR: 
			return new FileInputStream(new File(filename));
		case GZ: 
			return new GZIPInputStream(new FileInputStream(new File(filename)));
		case BZ2:
			return createBZIP2InputStream();
		case ZIP:
			return createZipInputStream();
		default:
			throw new IOException("Undetermined file type for file "+filename);
		}
	}

	private InputStream createBZIP2InputStream() throws IOException {
		// normally the following lines would be ok. But somehow it does not work, so we copy the content of the 
		// bzip2file into a tmpfile for reading with deleteonexit set.
		//return new CBZip2InputStream(new FileInputStream(fileName));
		File tmpfile = CopyFile.createTempFile();
		InputStream fis = new FileInputStream(new File(filename));
		int magic3 = fis.read();
		int magic4 = fis.read();
		if(magic3 != 'B' || magic4 != 'Z') {
			throw new IOException("File "+filename+" is missing bzip2 header BZ.");
		}
		CopyFile.copyStreams(new CBZip2InputStream(fis),new FileOutputStream(tmpfile));
		return new FileInputStream(tmpfile);
	}	

	private InputStream createZipInputStream() throws IOException {
		ZipFile zFile = new ZipFile(filename);
		if(zipentry == null) {
			zipentry = getFirstCRFile(zFile);
			if(zipentry == null) {
				throw new Loader.MissingCRException();
			}
		}
		return zFile.getInputStream(zipentry);
	}
	/**
	 * Returns the first file with a '.cr' extension contained in
	 * the compressed file zip. <tt>null</tt> if none can be found.
	 */
	private ZipEntry getFirstCRFile(ZipFile zip) {
		for(Enumeration iter = zip.entries(); iter.hasMoreElements(); ) {
			ZipEntry entry = (ZipEntry) iter.nextElement();
			String ext = entry.getName().substring(entry.getName().length() - 3,
												   entry.getName().length());
			if (ext.compareToIgnoreCase(".cr") == 0) {
				return entry;
			}
		}
		return null;
	}

	public File getFile() {
		return new File(getName());
	}

	public String getName() {
		return filename;
	}
	
	
	public String toString() {
		return getFile().getName()+
			(zipentry == null ? "" : (" ("+zipentry.getName()+")"));
	}


	public static Reader createEncodingReader(InputStream is) throws IOException {
		return new InputStreamReader(is,DEFAULT_ENCODING);
	}

	public static Writer createEncodingWriter(OutputStream os) throws IOException {
		return new OutputStreamWriter(os,DEFAULT_ENCODING);
	}

}
