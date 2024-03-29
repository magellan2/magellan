/*
 *  Copyright (C) 2000-2004 Roger Butenuth, Andreas Gampe,
 *                          Stefan Goetz, Sebastian Pappert,
 *                          Klaas Prause, Enno Rehling,
 *                          Sebastian Tusk, Ulrich Kuester,
 *                          Ilja Pavkovic
 *
 * This file is part of the Eressea Java Code Base, see the
 * file LICENSING for the licensing information applying to
 * this file.
 *
 */

package com.eressea.io.cr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import com.eressea.util.StringFactory;
import com.eressea.util.Umlaut;
import com.eressea.util.logging.Logger;

/**
 * A simple class for tokenizing lines of an input file. 
 * 
 * Lines of the form <br/>
 * <tt>number;key</tt> are parsed as two tokens,<br/>
 * <tt>"string";key</tt> are parsed as two tokens,<br/>
 * <tt>BLOCK</tt> are parsed as a block,<br/>
 * <tt>BLOCK id</tt> are parsed as a block with ID.<br/>
 *
 * @author $author$
 * @version $Revision$
 */
public class Scanner {
	private static final Logger log = Logger.getInstance(Scanner.class);
	private BufferedReader stream;

	/** number of tokens */
	public int argc; 

	/** the tokens */
	public String argv[]; 

	/** Is it a string? (enclosed in "") */
	public boolean isString[];

	/** end of file reached? */
	public boolean eof; 

	/** current line number */
	public int lnr; 

	/** Is this a begin of block token? */
	public boolean isBlock; 

	/** Is this a block with Id? */
	public boolean isIdBlock; 

	/**
	 * Creates a new Scanner object.
	 *
	 * @param in The Reader used for reading input
	 *
	 * @throws IOException Never thrown
	 */
	public Scanner(Reader in) throws IOException {
		stream = new BufferedReader(in);
		argv = new String[2];
		isString = new boolean[2];
		lnr = 0;
		eof = false;
	}

	/**
	 * Parses until the next token, skipping empty lines.
	 *
	 * @throws IOException 
	 */
	public void getNextToken() throws IOException {
		String line;
		char buf[];
		int len;
		int i;
		int start;

		argv[0] = null;
		argv[1] = null;
		argc = 0;
		isBlock = false;
		isIdBlock = false;
		lnr++;

		if(!stream.ready()) {
			eof = true;
			stream.close();

			return;
		}

		line = stream.readLine();

		// skip empty lines
		if(line == null) {
			getNextToken();

			return;
		}

		buf = line.toCharArray();
		len = line.length();

		//System.out.println(lnr + ": " + line);
		// skip empty lines
		if(line.length() == 0) {
			getNextToken();

			return;
		}

		if(('A' <= buf[0]) && (buf[0] <= 'Z')) {
			isBlock = true;

			if(line.indexOf(' ') >= 0) {
				isIdBlock = true;
			}
		}

		i = 0;

		while((i < len) && (buf[i] != '\r') && (buf[i] != '\n')) {
			if(argc > 1) {
				throw new IOException("Scanner.getNextToken(): invalid token format in line " +
									  lnr);
			}

			if(buf[i] == '"') {
				// quoted string
				i++; // skip start "
				if(line.lastIndexOf('"')!= -1) {
					int lastQuote = line.lastIndexOf('"');
					if (lastQuote<i){
						// TODO throw IOException?
						log.warn("Error parsing line "+lnr+": "+line);
						break;
					}
					String str = Umlaut.replace(line.substring(i,lastQuote),"\\\"","\"");
					argv[argc] = StringFactory.getFactory().intern(str);
					i = lastQuote;
				} else {
					// old code: May be better but we wont use it
					char outbuf[] = new char[len];
					int outPtr = 0;
					start = i; // marker for begin of string
					
					while(i < len) {
						if(buf[i] == '"') {
							if(buf[i - 1] == '\\') { // escaped quotation mark
								outbuf[outPtr - 1] = '"';
								i++;
							} else { // unescaped quotation mark, stop reading
								
								break;
							}
						} else if((buf[i] != '\r') && (buf[i] != '\n')) {
							outbuf[outPtr++] = buf[i++];
						}
					}
					
					if(i == len) {
						log.warn("Missing \" in line " + lnr);
					}
					
					// pavkovic 2003.07.02: use String.intern() method to reduce memory consumption
					argv[argc] = StringFactory.getFactory().intern(new String(outbuf, 0, outPtr));
				}
				isString[argc] = true;
				argc++;
				i++; // skip "
			} else {
				// normal token
				start = i;

				while((i < len) && (buf[i] != ';') && (buf[i] != '\r') && (buf[i] != '\n')) {
					i++;
				}

				// pavkovic 2003.07.02: use String.intern() method to reduce memory consumption
				argv[argc] = StringFactory.getFactory().intern(line.substring(start, i));
				isString[argc] = false;
				argc++;
			}

			if((i < len) && (buf[i] == ';')) {
				i++; /* skip ; */
			}
		}
	}
}
