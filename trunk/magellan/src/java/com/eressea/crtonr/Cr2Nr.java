// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Martin Hershoff, Sebastian Pappert,
//							Klaas Prause,  Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.crtonr;

import java.util.List;

import com.eressea.CompleteData;
import com.eressea.GameData;
import com.eressea.rules.Eressea;
import com.eressea.util.CollectionFactory;
import com.eressea.util.logging.Logger;

public class Cr2Nr
{
	private final static Logger log = Logger.getInstance(Cr2Nr.class);

	public static void main(String[] args)
	{
		String inFile = null, outFile = null;
		List inFiles = CollectionFactory.createArrayList();
		
		log.info("Drin");
		int i=0;
		while (i<args.length) {
			if (args[i].equals("-i")) {
				inFile = args[++i];
			}
			else if (args[i].equals("-o")) {
				outFile = args[++i];
			}
			else {
				log.warn("Unknown Option " + args[i]);
			}
			i++;
		}
		if (inFile == null) {
			NrDialog dialog = new NrDialog();
			inFile  = dialog.getInfile();
			outFile = dialog.getOutfile();
			if (inFile == null || inFile.equals(""))
				System.exit(0);
		}
		
		inFiles.add(inFile);	
		
		log.info("Infile is  " + inFile);
		log.info("Outfile is " + outFile);
		
		GameData data = new CompleteData(new Eressea());
		NonGraphicalClient c = new NonGraphicalClient(data, new java.io.File(System.getProperties().getProperty("user.home")));
		GameData newData = c.loadCR(inFile);
		
		NrWriter writer = new NrWriter(newData);
		if (outFile == null || outFile.equals("")) 
			outFile = inFile.substring(inFile.lastIndexOf(".")+1)+".cr";
		try {
			writer.writeNr(outFile);
		}
		catch (java.io.IOException e) {
			log.error(e);
		}
		System.exit(0);
	}
}
