package com.eressea.io;

import java.io.IOException;
import java.io.InputStream;

import com.eressea.Rules;

public interface RulesIO {
	public Rules readRules(InputStream is) throws IOException;

}
