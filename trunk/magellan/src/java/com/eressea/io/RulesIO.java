package com.eressea.io;

import java.io.IOException;

import com.eressea.Rules;
import com.eressea.io.file.FileType;

public interface RulesIO {
	/** 
	 * Reads the rules from a FileType.
	 */
	public Rules readRules(FileType filetype) throws IOException;
}
