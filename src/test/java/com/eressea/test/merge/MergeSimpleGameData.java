package com.eressea.test.merge;

import java.util.Properties;

import junit.framework.TestCase;

import com.eressea.GameData;

import com.eressea.main.*;

public class MergeSimpleGameData extends TestCase {

	public MergeSimpleGameData(String aName) {
		super(aName);
	}

	public void setUp() throws Exception {
		MagellanContext.getInstance().init(new Properties());
	}

	public void tearDown() throws Exception {
	}

	public void testMergeSameRound() throws Exception {
		GameData gd1 = new GameDataBuilder().createSimpleGameData();
		GameData gd2 = new GameDataBuilder().createSimpleGameData();
		
		GameData gd3 = GameData.merge(gd1, gd2);
	}

	public void testMergeDifferentRound() throws Exception {
		GameData gd1 = new GameDataBuilder().createSimpleGameData(351);
		GameData gd2 = new GameDataBuilder().createSimpleGameData(350);
		
		GameData gd3 = GameData.merge(gd1, gd2);
	}

}
