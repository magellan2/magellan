// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.cr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import com.eressea.util.logging.Logger;
import com.eressea.util.StringFactory;

public class Scanner {
	private final static Logger log = Logger.getInstance(Scanner.class);
	private BufferedReader stream;

	public int	 argc;	// number of tokens
	public String  argv[];	// the tokens
	public boolean isString[];  // Is it a string? (enclosed in "")
	public boolean eof;		// end of file reached?
	public int	 lnr;		// current line number
	public boolean isBlock;	 // Is this a begin of block token?
	public boolean isIdBlock;   // Is this a block with Id?

	public Scanner(Reader in) throws IOException {
		stream = new BufferedReader(in);
		argv	  = new String[2];
		isString  = new boolean[2];
		lnr	   = 0;
		eof	   = false;
	}

	public void getNextToken() throws IOException {
		String line;
		char   buf[];
		int	len;
		int	i, start;
	
		argv[0]   = null;
		argv[1]   = null;
		argc	  = 0;
		isBlock   = false;
		isIdBlock = false;
		lnr++;
	
		if (!stream.ready()) {
			eof = true;
			stream.close();
			return;
		}
		line = stream.readLine();
		// skip empty lines
		if (line == null) {
			getNextToken();
			return;
		}		
		buf  = line.toCharArray();
		len  = line.length();
	
		//System.out.println(lnr + ": " + line);
			
		// skip empty lines
		if (line.length() == 0) {
			getNextToken();
			return;
		}
	
		if ('A' <= buf[0] && buf[0] <= 'Z') {
			isBlock = true;
			if (line.indexOf(' ') >= 0)
				isIdBlock = true;
		}
		i = 0;
		while (i < len && buf[i] != '\r' && buf[i] != '\n') {
			if (argc > 1) {
				throw new IOException("Scanner.getNextToken(): invalid token format in line " + lnr);
			}
			
			if (buf[i] == '"') {
				char outbuf[] = new char[len];
				int outPtr = 0;
				i++;		// skip start "
				start = i;	// marker for begin of string
				while (i < len) {
					if (buf[i] == '"') {
						if (buf[i-1] == '\\') {	// escaped quotation mark
							outbuf[outPtr-1] = '"';
							i++;
						} else {	// unescaped quotation mark, stop reading
							break;
						}
					} else if (buf[i] != '\r' && buf[i] != '\n') {
						outbuf[outPtr++] = buf[i++];
					}
				}
				if (i == len) {
					log.warn("Missing \" in line " + lnr);
				}
				// pavkovic 2003.07.02: use String.intern() method to reduce memory consumption
				argv[argc] = StringFactory.getFactory().intern(new String(outbuf, 0, outPtr));
				isString[argc] = true;
				argc++;
				
				i++;		// skip "
			} else {
				start = i;
				while (i < len && buf[i] != ';' && 
					   buf[i] != '\r' && buf[i] != '\n')
					i++;
				// pavkovic 2003.07.02: use String.intern() method to reduce memory consumption
				argv[argc] = StringFactory.getFactory().intern(line.substring(start, i));
				isString[argc] = false;
				argc++;
			}
			if (i < len && buf[i] == ';')
				i++;		/* skip ; */
		}
	}
}
