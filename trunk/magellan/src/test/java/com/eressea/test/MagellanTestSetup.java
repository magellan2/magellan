package com.eressea.test;

import java.util.Properties;

import com.eressea.main.MagellanContext;

import junit.extensions.TestSetup;
import junit.framework.TestSuite;

public class MagellanTestSetup extends TestSetup {
	public MagellanTestSetup(TestSuite test) {
		super(test);
	}

	protected void setUp() {
		MagellanContext context = new MagellanContext();
		context.setProperties(new Properties());
		context.init();
	}

	protected void tearDown() {
	}
}
