package com.eressea.io.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;


public class ZipFileType extends FileType {

	// TODO: ZipEntry may also be a "normal" FileType
	protected ZipEntry zipentry = null;

	ZipFileType(String aFile, ZipEntry aEntry) throws IOException {
		super(aFile);
		if(aEntry == null) {
			throw new IOException("ZipEntry is null in ZipFileType");
		}
		zipentry = new ZipEntry(aEntry);
	}

	public String toString() {
		return super.toString()+" ("+zipentry.getName()+")";
	}

	public String getInnerName() {
		return zipentry.getName();
	}
	
	/**
	 * Returns all files inside the zip ending with one of 
	 * the given endings case insensitive
	 */
	public static ZipEntry[] getZipEntries(ZipFile zip, String[] endings) {
		Collection ret = new ArrayList();
		for(Enumeration iter = zip.entries(); iter.hasMoreElements(); ) {
			ZipEntry entry = (ZipEntry) iter.nextElement();
			String entryName = entry.getName();
			for(int i=0; i<endings.length; i++) {
				if(entryName.toLowerCase().endsWith(endings[i])) {
					ret.add(entry);
					break;
				}
			}
		}
		return (ZipEntry[]) ret.toArray(new ZipEntry[] {});
	}

	protected InputStream createInputStream() throws IOException {
		InputStream is = new ZipFile(filename).getInputStream(zipentry);
		if(is == null) {
			throw new IOException("Cannot read zip entry '"+zipentry+"' in file '"+filename+"',");
		}
		return is;
	}

	protected OutputStream createOutputStream() throws IOException {
		// here we need to do something special: all entries are copied expect the named zipentry, which will be overwritten
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
}
