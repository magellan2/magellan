package com.eressea.test.merge;

import junit.framework.TestSuite;

public class MergeTestSuite {
	public static void main(String [] args) {
		if ((args.length > 0) && args [0].toUpperCase ().equals ("TEXT")) {
			junit.textui.TestRunner.run (suite ());
		} else {
			junit.swingui.TestRunner.run (MergeTestSuite.class);
		}
	}
	
	public static TestSuite suite() {
		TestSuite suite = new TestSuite ("Merge Test Suite");

		//suite.addTest (ParserTestSuite.suite ());
		suite.addTest (new TestSuite (WriteGameData.class));

		return suite;
	}
}
