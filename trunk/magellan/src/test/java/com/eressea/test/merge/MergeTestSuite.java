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
		TestSuite suite = new TestSuite("Merge Test Suite");

		suite.addTest (new TestSuite (WriteGameData.class));
		suite.addTest (new TestSuite (MergeSimplestGameData.class));

		/*
	

import junit.framework.*;
import junit.extensions.TestSetup;

public class AllTestsOneTimeSetup {

    public static Test suite() {

        TestSuite suite = new TestSuite();

        suite.addTest(SomeTest.suite());
        suite.addTest(AnotherTest.suite());

        TestSetup wrapper = new TestSetup(suite) {

            protected void setUp() {
                oneTimeSetUp();
            }

            protected void tearDown() {
                oneTimeTearDown();
            }
        };

        return wrapper;
    }

    public static void oneTimeSetUp() {
        // one-time initialization code
    }

    public static void oneTimeTearDown() {
        // one-time cleanup code
    }
}

		*/
		return suite;
	}
}
