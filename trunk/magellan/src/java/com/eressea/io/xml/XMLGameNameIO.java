package com.eressea.io.xml;

import java.io.IOException;

import com.eressea.io.GameNameIO;
import com.eressea.io.file.FileType;

public class XMLGameNameIO implements GameNameIO {
	private XMLGameNameIO() {
	}
	
	private static XMLGameNameIO singleton = new XMLGameNameIO();
	public  static XMLGameNameIO singleton() {
		return singleton;
	}

	public String getGameName(FileType filetype) throws IOException {
		return null;
	}
}
