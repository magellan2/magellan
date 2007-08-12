package com.eressea.test.merge;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import com.eressea.GameData;

import com.eressea.io.cr.*;
import com.eressea.io.file.*;

public class WriteGameData extends TestCase {

	public WriteGameData(String aName) {
		super(aName);
	}

	public void testWriteCR() throws Exception {
		GameData data = new GameDataBuilder().createSimpleGameData();

		String file = data.getDate().getDate()+"_testWriteCR.cr";

		WriteGameData.writeCR(data, file);
	}

	public final static String FILE_PREFIX="build/build/test/";

	public static void writeCR(GameData data, String fName) throws IOException {
		File file = new File(FILE_PREFIX + fName);
		// System.out.println("Writing file "+file);
		FileType ft = FileTypeFactory.singleton().createFileType(file, false);
		ft.setCreateBackup(false);
		CRWriter crw = new CRWriter(ft,data.encoding);
		crw.write(data);
		crw.close();
	}
}
