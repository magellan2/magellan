package com.eressea.io;

import java.io.IOException;

/** 
 * A MissingInputException is thrown if it was impossible to achieve
 * a valid input e.g. a cr from a zip file.
 */
public class MissingInputException extends IOException {
	public MissingInputException() {
	}
}
