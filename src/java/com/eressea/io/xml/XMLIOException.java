package com.eressea.io.xml;

import java.io.*;


// standard java imports
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class XMLIOException extends IOException {
	private Exception exception;
	private static final String EXCEPTION_SEPARATOR = "______________ORIGINAL EXCEPTION____________";

	public XMLIOException(String aMessage) {
		super(aMessage);
	}

	public XMLIOException(Exception exception) {
		super(exception.getMessage());
		this.exception = exception;
	}

	public void printStackTrace() {
		printStackTrace(System.err);
	}

	public void printStackTrace(PrintStream aTarget) {
		synchronized(aTarget) {
			super.printStackTrace(aTarget);
			
			if(exception != null) {
				aTarget.println(EXCEPTION_SEPARATOR);
				exception.printStackTrace(aTarget);
			}
		}
	}

	public void printStackTrace(PrintWriter aTarget) {
		synchronized(aTarget) {
			super.printStackTrace(aTarget);
			
			if(exception != null) {
				aTarget.println(EXCEPTION_SEPARATOR);
				exception.printStackTrace(aTarget);
			}
		}
	}
}
