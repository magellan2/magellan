package com.eressea.io.cr;

import java.io.IOException;
import java.io.Reader;
import java.util.Map;

import com.eressea.io.GameNameIO;
import com.eressea.io.file.FileType;
import com.eressea.util.logging.Logger;

public class CRGameNameIO implements GameNameIO {
	private final static Logger log = Logger.getInstance(CRGameNameIO.class);

	private CRGameNameIO() {
	}
	
	private static CRGameNameIO singleton = new CRGameNameIO();
	public  static CRGameNameIO singleton() {
		return singleton;
	}

	public String getGameName(FileType filetype) throws IOException {
		Reader report = filetype.createReader();
		try {
			Map headerMap = (new CRParser()).readHeader(report);
			if (headerMap.containsKey("Spiel")) {
				return (String)headerMap.get("Spiel");
			}
		} catch (IOException e) {
			log.error("Loader.getGameName(): unable to determine game's name of report " + report,e);
		}  finally {
			report.close();
		}
		log.warn("Loader.getGameName(): report header does not contain 'Spiel' tag!");
		return "Eressea";
	}
}
