package com.eressea.test.merge;

import java.util.Properties;

import junit.framework.TestCase;

import com.eressea.GameData;

import com.eressea.main.*;

public class MergeSimplestGameData extends TestCase {

	public MergeSimplestGameData(String aName) {
		super(aName);
	}

	public void setUp() throws Exception {
		MagellanContext.getInstance().init(new Properties());
	}

	public void tearDown() throws Exception {
	}

	public void testMergeSameRound() throws Exception {
		GameData gd1 = new GameDataBuilder().createSimplestGameData();
		GameData gd2 = new GameDataBuilder().createSimplestGameData();
		
		GameData gd3 = GameData.merge(gd1, gd2);
	}

	public void testMergeDifferentRound() throws Exception {
		GameData gd1 = new GameDataBuilder().createSimplestGameData(351);
		GameData gd2 = new GameDataBuilder().createSimplestGameData(350);
		
		GameData gd3 = GameData.merge(gd1, gd2);
	}

}
