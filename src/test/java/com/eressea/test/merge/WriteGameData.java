package com.eressea.test.merge;

import java.util.Properties;

import junit.framework.TestCase;

import com.eressea.GameData;

import com.eressea.io.cr.*;
import com.eressea.io.file.*;
import com.eressea.main.*;

public class WriteGameData extends TestCase {

	public WriteGameData(String aName) {
		super(aName);
	}

	public void setUp() throws Exception {
		MagellanContext.getInstance().init(new Properties());
	}

	public void tearDown() throws Exception {
	}

	public void testWriteCR() throws Exception {
		GameData data = new GameDataBuilder().createSimpleGameData();

		String file = data.getDate().getDate()+"_testWriteCR.cr";

		FileType ft = FileTypeFactory.singleton().createFileType(file, false);
		ft.setCreateBackup(false);
		CRWriter crw = new CRWriter(ft);
		crw.write(data);
		crw.close();
		
	}


}
