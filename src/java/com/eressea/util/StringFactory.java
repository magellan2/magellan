package com.eressea.util;

import java.util.Map;

public class StringFactory {
	private final static StringFactory sf = new StringFactory();
		
	private StringFactory() {}

	public static StringFactory getFactory() {
		return sf;
	}
	
	private Map strings = CollectionFactory.createHashMap();

	public String intern(String s) {
		String is = (String) strings.get (s);
		if (is == null) {
			is = getOptimizedString(s);
            strings.put (is, is);
        }
		return is;
    }

	public String getOptimizedString(String s) {
		// copy all strings into new char and recreate string with it. 
		// Prevent inefficient use of char[]
        char [] allchars = new char [s.length()];
		s.getChars (0, s.length(), allchars, 0);
		return new String(allchars);
    }
}
