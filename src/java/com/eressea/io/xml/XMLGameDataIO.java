package com.eressea.io.xml;

import java.io.IOException;
import java.io.Reader;

import com.eressea.GameData;
import com.eressea.io.GameDataIO;
import com.eressea.util.logging.Logger;


public class XMLGameDataIO implements GameDataIO {
	public final static Logger log = Logger.getInstance(XMLGameDataIO.class);
	
	public GameData read(Reader in, GameData world) throws IOException {
		throw new IOException("Implementation incomplete");
		// c) use corresponding gamebinding object (or eressea gamebinding object if 
		//    no special implementation found) to read the cr/xml
		// 
		//return new XMLGameDataReader(file).readGameData();
	} 
}
