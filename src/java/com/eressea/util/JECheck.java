/*
 *  Copyright (C) 2000-2003 Roger Butenuth, Andreas Gampe,
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

package com.eressea.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import com.eressea.Coordinate;
import com.eressea.GameData;
import com.eressea.ID;
import com.eressea.Region;
import com.eressea.Unit;
import com.eressea.UnitID;

import com.eressea.util.logging.Logger;

/**
 * A class for executing ECheck and reading the output returned.
 */
public class JECheck extends Reader {
	private static final Logger log = Logger.getInstance(JECheck.class);
	private static String FIELD_SEP = "|";
	private Reader outputReader = null;
	private File tempFile = null;

	/**
	 * Creates a new JECheck object by executing ECheck with the specified parameters and making
	 * its output accessible through the Reader interface.
	 *
	 * @param eCheckExe a file specifying the ECheck executable. It is assumed that the files
	 * 		  items.txt and zauber.txt are located in same directory as the executable itself.
	 * @param orders the file containing the orders to be parsed by ECheck. If orders is null
	 * 		  ECheck is executed without a specified orders file.
	 * @param options additional options to be passed to ECheck to control its operation and
	 * 		  output.
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public JECheck(File eCheckExe, File orders, String options) throws IOException {
		List commandLine = CollectionFactory.createLinkedList();
		Process p = null;
		long start = 0;

		/* check ECheck executable */
		if((eCheckExe.exists() == false) || (eCheckExe.canRead() == false)) {
			throw new IOException("Specified ECheck executable " + eCheckExe.getAbsolutePath() +
								  " is invalid");
		}

		/* put the exe into the command line, tell ECheck to use
		   magellan mode output and where to find items.txt and
		   zauber.txt */
		commandLine.add(eCheckExe.getAbsolutePath());

		// create a temp file for ECheck's output
		try {
			tempFile = File.createTempFile("JECheck", null);
		} catch(Exception e) {
			log.error("JECheck.JECheck(): unable to create temporary file for output", e);
			throw new IOException("Unable to create temporary file for output: " + e);
		}

		commandLine.add("-O" + tempFile.getAbsolutePath());

		commandLine.add("-P" + eCheckExe.getParentFile().getAbsolutePath());

		StringTokenizer optiontokens = new StringTokenizer(options);

		while(optiontokens.hasMoreTokens()) {
			commandLine.add(optiontokens.nextToken());
		}

		if(orders != null) {
			/* do this so the -m option is not necessarily specified
			   in the arguments but prevent problems with older
			   versions */
			if(JECheck.getVersion(eCheckExe).compareTo(new Version("4.0.6", ".")) >= 0) {
				commandLine.add("-m");
			}

			commandLine.add(orders.getAbsolutePath());
		}

		// run the beast
		// FIXME(pavkovic) log.debug("ECheck is executed with this command line:\n" + commandLine);
		log.error("ECheck is executed with this command line:\n" + commandLine);
		start = System.currentTimeMillis();

		try {
			p = Runtime.getRuntime().exec((String[]) commandLine.toArray(new String[] {  }));
		} catch(Exception e) {
			log.error("JECheck.JECheck(): exception while executing echeck", e);
			throw new IOException("Cannot execute ECheck: " + e.toString());
		}

		while(true) {
			try {
				Thread.sleep(100);
			} catch(InterruptedException e) {
			}

			// bail out if ECheck runs for longer than 20 secs
			if((System.currentTimeMillis() - start) > 20000) {
				p.destroy();
				log.warn("JECheck.JECheck(): echeck was terminated since it ran for too long");

				break;
			}

			// the loop is left when the process has terminated
			// termination is indicated by the exitValue() method
			// throwing no IllegalStateException
			try {
				p.exitValue();

				break;
			} catch(IllegalThreadStateException e) {
			}
		}

		// get a reader on the temporary output file (which is deleted on close)
		try {
			outputReader = new FileReader(tempFile);
		} catch(Exception e) {
			log.error("JECheck.JECheck(): cannot create a file reader on the temporary output file",
					  e);
			throw new IOException("Cannot create a file reader on the temporary output file: " + e);
		}
	}

	/**
	 * Reads from the output of ECheck.
	 *
	 * @param cbuf TODO: DOCUMENT ME!
	 * @param off TODO: DOCUMENT ME!
	 * @param len TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public int read(char cbuf[], int off, int len) throws IOException {
		if(outputReader != null) {
			return outputReader.read(cbuf, off, len);
		} else {
			throw new IOException("No ECheck output available");
		}
	}

	/**
	 * Closes the reader on the output of ECheck.
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public void close() throws IOException {
		if(outputReader != null) {
			outputReader.close();
		}

		tempFile.delete();
	}

	/**
	 * Mark the present position in the stream. Subsequent calls to reset() will attempt to
	 * reposition the stream to this point. Not all character-input streams support the mark()
	 * operation.
	 *
	 * @param readAheadLimit Limit on the number of characters that may be read while still
	 * 		  preserving the mark. After reading this many characters, attempting to reset the
	 * 		  stream may fail.
	 *
	 * @throws IOException If the stream does not support mark(), or if some other I/O error
	 * 		   occurs.
	 */
	public void mark(int readAheadLimit) throws IOException {
		if(outputReader != null) {
			outputReader.mark(readAheadLimit);
		} else {
			throw new IOException("No ECheck output available");
		}
	}

	/**
	 * Tell whether this stream supports the mark() operation.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean markSupported() {
		return outputReader.markSupported();
	}

	/**
	 * Tell whether this stream is ready to be read.
	 *
	 * @return True if the next read() is guaranteed not to block for input, false otherwise. Note
	 * 		   that returning false does not guarantee that the next read will block.
	 *
	 * @throws IOException If an I/O error occurs
	 */
	public boolean ready() throws IOException {
		if(outputReader != null) {
			return outputReader.ready();
		} else {
			throw new IOException("No ECheck output available");
		}
	}

	/**
	 * Reset the stream. If the stream has been marked, then attempt to reposition it at the mark.
	 * If the stream has not been marked, then attempt to reset it in some way appropriate to the
	 * particular stream, for example by repositioning it to its starting point. Not all
	 * character-input streams support the reset() operation, and some support reset() without
	 * supporting mark().
	 *
	 * @throws IOException If the stream has not been marked, or if the mark has been invalidated,
	 * 		   or if the stream does not support reset(), or if some other I/O error occurs
	 */
	public void reset() throws IOException {
		if(outputReader != null) {
			outputReader.reset();
		} else {
			throw new IOException("No ECheck output available");
		}
	}

	/**
	 * Skip characters. This method will block until some characters are available, an I/O error
	 * occurs, or the end of the stream is reached.
	 *
	 * @param n The number of characters to skip
	 *
	 * @return The number of characters actually skipped
	 *
	 * @throws IOException If an I/O error occurs
	 */
	public long skip(long n) throws IOException {
		if(outputReader != null) {
			return outputReader.skip(n);
		} else {
			throw new IOException("No ECheck output available");
		}
	}

	/**
	 * Runs ECheck with the specified parameters and returns the error and warning messages it
	 * reported. For reference for the parameters see the constructor. This method requires that
	 * the specified echeck executable not older than the version returned by
	 * getRequiredVersion().
	 *
	 * @param eCheckExe TODO: DOCUMENT ME!
	 * @param orders TODO: DOCUMENT ME!
	 * @param options TODO: DOCUMENT ME!
	 *
	 * @return a collection containing JECheck.ECheckMessage objects.
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 * @throws java.text.ParseException TODO: DOCUMENT ME!
	 */
	public static Collection getMessages(File eCheckExe, File orders, String options)
								  throws IOException, java.text.ParseException
	{
		return getMessages(new JECheck(eCheckExe, orders, options));
	}

	/**
	 * Returns a collection containing JECheck.ECheckMessage objects by reading from the specified
	 * reader. This method requires that the specified echeck executable not older than the
	 * version returned by getRequiredVersion().
	 *
	 * @param echeckOutput TODO: DOCUMENT ME!
	 *
	 * @return a collection containing JECheck.ECheckMessage objects.
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 * @throws java.text.ParseException TODO: DOCUMENT ME!
	 */
	public static Collection getMessages(Reader echeckOutput)
								  throws IOException, java.text.ParseException
	{
		Collection msgs = CollectionFactory.createLinkedList();
		BufferedReader in = new LineNumberReader(echeckOutput);
		String line = null;
		int errors = -1;
		int warnings = -1;

		boolean error = false;

		line = getLine(in);

		if(line == null) {
			throw new java.text.ParseException("ECheck output is empty", 0);
		}

		//
		// 2002.05.05 pavkovic: some more documentation: 
		// for echeck <= 4.1.4, the first line looked like
		// <filename>|<version>|<date><filename>:<faction>:<factionid>
		// That is ok for the code below.
		// In newer versions, the first two lines look like
		// <filename>|<version>|<date>
		// <filename>:<faction>:<factionid>
		// To fake the old behaviour we simulate the "first"-line of echeck <= 4.1.4
		//
		if(line.indexOf(":faction:") == -1) {
			line += getLine(in);
		}

		/* parse header <filename>:faction:<factionid> */
		StringTokenizer tokenizer = new StringTokenizer(line, FIELD_SEP);

		if(tokenizer.countTokens() == 4) {
			tokenizer.nextToken();

			if(!tokenizer.nextToken().equalsIgnoreCase("version")) {
				error = true;
			}
		} else {
			error = true;
		}

		/* check for error in header */
		if(error) {
			throw new java.text.ParseException("The header of the ECheck output seems to be corrupt: " +
											   line, 0);
		}

		/* parse messages */
		line = getLine(in);

		while(line != null) {
			tokenizer = new StringTokenizer(line, FIELD_SEP);

			if(tokenizer.countTokens() >= 4) {
				ECheckMessage msg = new ECheckMessage(line);
				msgs.add(msg);
			} else {
				break;
			}

			line = getLine(in);
		}

		if(line == null) {
			throw new java.text.ParseException("The ECheck output seems to miss a footer", 0);
		}

		/* parse footer */
		if(tokenizer.countTokens() == 3) {
			tokenizer.nextToken();

			if(tokenizer.nextToken().equalsIgnoreCase("warnings")) {
				warnings = Integer.parseInt(tokenizer.nextToken());
			} else {
				error = true;
			}

			line = getLine(in);

			if(line != null) {
				tokenizer = new StringTokenizer(line, FIELD_SEP);

				if(tokenizer.countTokens() == 3) {
					tokenizer.nextToken();

					if(tokenizer.nextToken().equalsIgnoreCase("errors")) {
						errors = Integer.parseInt(tokenizer.nextToken());
					} else {
						error = true;
					}
				} else {
					error = true;
				}
			} else {
				error = true;
			}
		} else {
			error = true;
		}

		if(error) {
			throw new java.text.ParseException("The footer in the ECheck output seems to be corrupt: " +
											   line, 0);
		}

		in.close();

		if((errors == -1) || (warnings == -1)) {
			throw new java.text.ParseException("Unable to determine number of ECheck errors and warnings in ECheck summary.",
											   0);
		}

		int errorsFound = 0;
		int warningsFound = 0;

		for(Iterator iter = msgs.iterator(); iter.hasNext();) {
			ECheckMessage msg = (ECheckMessage) iter.next();

			if(msg.type == ECheckMessage.ERROR) {
				errorsFound++;
			} else if(msg.type == ECheckMessage.WARNING) {
				warningsFound++;
			}
		}

		if(errorsFound != errors) {
			throw new java.text.ParseException("The number of errors found does not match the summary information from ECheck.",
											   0);
		}

		if(warningsFound != warnings) {
			throw new java.text.ParseException("The number of warnings found does not match the summary information from ECheck.",
											   0);
		}

		return msgs;
	}

	/**
	 * Returns the next line from the buffered Reader or null iff the the end of the stream is
	 * reached.
	 *
	 * @param in TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	private static String getLine(BufferedReader in) throws IOException {
		String line = null;

		while(in.ready()) {
			line = in.readLine();

			if(line == null) {
				continue;
			} else {
				break;
			}
		}

		return line;
	}

	/**
	 * Determines the Unit or Region referenced in an ECheck message by looking at the orders the
	 * ECheck messages were generated from.
	 *
	 * @param data the game data the orders belong to and from which the Unit or Region can be
	 * 		  retrieved.
	 * @param orderFile a file containing the orders that ECheck was run on when it produced the
	 * 		  specified message.
	 * @param msgs the messages to be processed.
	 *
	 * @return the collection of input messages with their affected objects fields set if possible.
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 * @throws IllegalArgumentException TODO: DOCUMENT ME!
	 */
	public static Collection determineAffectedObjects(GameData data, File orderFile, Collection msgs)
											   throws IOException
	{
		if(data == null) {
			throw new IllegalArgumentException("JECheck.getAffectedObject(): invalid data argument specified.");
		}

		if(orderFile == null) {
			throw new IllegalArgumentException("JECheck.getAffectedObject(): invalid orderFile argument specified.");
		}

		if(!orderFile.exists()) {
			throw new IllegalArgumentException("JECheck.getAffectedObject(): the specified orderFile file does not exist.");
		}

		if(msgs == null) {
			throw new IllegalArgumentException("JECheck.getAffectedObject(): invalid msgs argument specified.");
		}

		String line = null;
		LineNumberReader lnr = null;
		List orders = CollectionFactory.createLinkedList();

		/* frequently used strings */
		String unitOrder = com.eressea.util.Translations.getOrderTranslation(EresseaOrderConstants.O_UNIT);
		String regionOrder = com.eressea.util.Translations.getOrderTranslation(EresseaOrderConstants.O_REGION);

		/* first read in all the orders into a list to access them
		   quickly later */
		lnr = new LineNumberReader(new FileReader(orderFile));

		for(line = lnr.readLine(); (line != null) && lnr.ready(); line = lnr.readLine()) {
			orders.add(line);
		}

		lnr.close();

		/* now walk all messages and determine the affected object by
		   looking at the line referenced. */
		for(Iterator msgIter = msgs.iterator(); msgIter.hasNext();) {
			ECheckMessage msg = (ECheckMessage) msgIter.next();

			if(msg.getLineNr() > 0) {
				for(int i = msg.getLineNr() - 1; i > -1; i--) {
					String order = Umlaut.normalize((String) orders.get(i));
					StringTokenizer tokenizer = new StringTokenizer(order, " ;");

					if(tokenizer.countTokens() < 2) {
						continue;
					}

					String token = tokenizer.nextToken();

					if(unitOrder.startsWith(token)) {
						try {
							ID id = UnitID.createUnitID(tokenizer.nextToken());
							Unit u = data.getUnit(id);

							if(u != null) {
								msg.setAffectedObject(u);

								break;
							}
						} catch(Exception e) {
							log.error(e);
						}
					} else if(regionOrder.startsWith(token)) {
						try {
							ID id = Coordinate.parse(tokenizer.nextToken(), ",");
							Region r = data.getRegion(id);

							if(r != null) {
								msg.setAffectedObject(r);

								break;
							}
						} catch(Exception e) {
							log.error(e);
						}
					}
				}
			}
		}

		return msgs;
	}

	/**
	 * Returns the version of the specified ECheck executable file.
	 *
	 * @param eCheckExe TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public static Version getVersion(File eCheckExe) throws IOException {
		Version v = null;
		String version = null;
		String line = null;
		BufferedReader br = null;

		try {
			br = new BufferedReader(new JECheck(eCheckExe, null, "-h"));
			line = br.readLine();
			br.close();

			int verStart = line.indexOf("Version ") + "Version ".length();
			int verEnd = line.indexOf(",", verStart);

			if((verStart > 0) && (verEnd > verStart)) {
				version = line.substring(verStart, verEnd);
				v = new Version(version, ".");
			}
		} catch(Exception e) {
			v = null;
			log.error(e);
			throw new IOException("Cannot retrieve version information: " + e.toString());
		}

		if(v == null) {
			throw new IOException("Cannot retrieve version information");
		}

		return v;
	}

	/**
	 * Checks whether the version of the specified ECheck executable is valid and its output can be
	 * processed.
	 *
	 * @param eCheckExe TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public static boolean checkVersion(File eCheckExe) throws IOException {
		Version version = JECheck.getVersion(eCheckExe);

		return (version.compareTo(JECheck.getRequiredVersion()) >= 0);
	}

	/**
	 * Returns the oldest version of ECheck that is required by this class to process the orders
	 * correctly.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static Version getRequiredVersion() {
		return new Version("4.1.2", ".");
	}

	/**
	 * A class representing a message as it produced by ECheck.
	 */
	public static class ECheckMessage {
		/** An error message */
		public static final int ERROR = 1;

		/** A warning message */
		public static final int WARNING = 2;
		private String fileName = null;
		private int lineNr = 0;
		private int type = 0;
		private int warnLevel = 0;
		private String msg = null;
		private Object affectedObject = null; // the object affected by this message

		/**
		 * Creates a new ECheckMessage object by parsing the specified String.
		 *
		 * @param rawMessage TODO: DOCUMENT ME!
		 *
		 * @throws java.text.ParseException if the rawMessage String cannot be parsed.
		 */
		public ECheckMessage(String rawMessage) throws java.text.ParseException {
			String delim = FIELD_SEP;
			StringTokenizer tokenizer = new StringTokenizer(rawMessage, delim);

			if(tokenizer.countTokens() >= 4) {
				try {
					tokenizer.nextToken(); // skip file name
					lineNr = Integer.parseInt(tokenizer.nextToken());
					warnLevel = Integer.parseInt(tokenizer.nextToken());
					type = (warnLevel == 0) ? ERROR : WARNING;
					msg = tokenizer.nextToken();

					while(tokenizer.hasMoreTokens()) {
						msg += (delim + tokenizer.nextToken());
					}
				} catch(Exception e) {
					throw new java.text.ParseException("Unable to parse ECheck message \"" +
													   rawMessage + "\"", 0);
				}
			} else {
				throw new java.text.ParseException("Unable to parse ECheck message \"" +
												   rawMessage + "\"", 0);
			}
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public String getFileName() {
			return fileName;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public int getLineNr() {
			return lineNr;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public int getType() {
			return type;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public int getWarningLevel() {
			return warnLevel;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public String getMessage() {
			return msg;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public Object getAffectedObject() {
			return this.affectedObject;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param o TODO: DOCUMENT ME!
		 */
		public void setAffectedObject(Object o) {
			this.affectedObject = o;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append(fileName).append("(").append(lineNr).append("): ");

			if(type == ERROR) {
				sb.append("Fehler: ");
			} else if(type == WARNING) {
				sb.append("Warnung (").append(warnLevel).append("): ");
			}

			sb.append(msg);

			return sb.toString();
		}
	}
}
