package com.eressea.io;

import java.io.IOException;

import com.eressea.io.cr.CRGameNameIO;
import com.eressea.io.file.FileType;
import com.eressea.io.xml.XMLGameNameIO;


public class GameNameReader {

	public GameNameReader() {
	}

	public String getGameName(FileType filetype) {
		try {
			String gameName = CRGameNameIO.singleton().getGameName(filetype);
			return gameName!=null ? gameName : XMLGameNameIO.singleton().getGameName(filetype);
		} catch (IOException e) {
			return null;
		}
		
	}
}
