package com.eressea.test;

import junit.framework.TestSuite;

public class MagellanTestSuite {
	public static void main(String [] args) {
		if ((args.length > 0) && args [0].toUpperCase ().equals ("TEXT")) {
			junit.textui.TestRunner.run (suite ());
		} else {
			junit.swingui.TestRunner.run (MagellanTestSuite.class);
		}
	}
	
	public static TestSuite suite() {
		TestSuite suite = new TestSuite ("Magellan Test Suite");

		//suite.addTest (ParserTestSuite.suite ());

		return suite;
	}
}
