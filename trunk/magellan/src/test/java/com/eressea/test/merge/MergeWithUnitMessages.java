package com.eressea.test.merge;

import junit.framework.TestCase;

import com.eressea.GameData;
import com.eressea.Region;
import com.eressea.Skill;
import com.eressea.Unit;

import com.eressea.util.CollectionFactory;

public class MergeWithUnitMessages extends TestCase {

	public MergeWithUnitMessages(String aName) {
		super(aName);
	}

	// bugzilla bug #8??
	public void testMergeDifferentRound() throws Exception {
		GameDataBuilder builder = new GameDataBuilder();

		GameData gd1 = builder.createSimpleGameData(350);
		GameData gd2 = builder.createSimpleGameData(351);

		Unit u1 = (Unit) gd1.units().values().iterator().next();
		u1.unitMessages = CollectionFactory.createLinkedList();
		u1.unitMessages.add(builder.createMessage("TEST_m1"));

		Unit u2 = (Unit) gd2.units().values().iterator().next();
		u2.unitMessages = CollectionFactory.createLinkedList();
		u2.unitMessages.add(builder.createMessage("TEST_m2"));

		GameData gd4 = GameData.merge(gd1, gd2);
//		// WriteGameData.writeCR(gdMerged, gdMerged.getDate().getDate()+"_gd.cr");

		Unit u4 = gd4.getUnit(u1.getID());
		
		assertTrue(u4 != null);
		assertTrue(u4.unitMessages != null);
		assertEquals(1, u4.unitMessages.size());
	}

	public void testMergeSameRound() throws Exception {
		GameDataBuilder builder = new GameDataBuilder();

		GameData gd1 = builder.createSimpleGameData(351);
		GameData gd2 = builder.createSimpleGameData(351);

		Unit u1 = (Unit) gd1.units().values().iterator().next();
		u1.unitMessages = CollectionFactory.createLinkedList();
		u1.unitMessages.add(builder.createMessage("TEST_m1"));

		Unit u2 = (Unit) gd2.units().values().iterator().next();
		u2.unitMessages = CollectionFactory.createLinkedList();
		u2.unitMessages.add(builder.createMessage("TEST_m2"));

		GameData gd4 = GameData.merge(gd1, gd2);
//		// WriteGameData.writeCR(gdMerged, gdMerged.getDate().getDate()+"_gd.cr");

		Unit u4 = gd4.getUnit(u1.getID());
		
		assertTrue(u4 != null);
		assertTrue(u4.unitMessages != null);
		assertEquals(2, u4.unitMessages.size());
	}


}
