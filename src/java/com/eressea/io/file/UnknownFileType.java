package com.eressea.io.file;

import java.io.IOException;


/** 
 * This FileType represent an not specified FileType.
 * Right now it will be treated equal to cr files.
 */
public class UnknownFileType extends FileType {
	UnknownFileType(String aFile) throws IOException {
		super(aFile);
	}
}
