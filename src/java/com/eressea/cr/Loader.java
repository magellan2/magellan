// ===
// Copyright (C) 2000, 2001,2002 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Ilja Pavkovic, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.cr;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.eressea.CompleteData;
import com.eressea.Coordinate;
import com.eressea.Faction;
import com.eressea.GameData;
import com.eressea.ID;
import com.eressea.IntegerID;
import com.eressea.LongID;
import com.eressea.Message;
import com.eressea.Region;
import com.eressea.RegionResource;
import com.eressea.Rules;
import com.eressea.StringID;
import com.eressea.Unit;
import com.eressea.resource.ResourcePathClassLoader;
import com.eressea.rules.Eressea;
import com.eressea.rules.ItemType;
import com.eressea.util.CollectionFactory;
import com.eressea.util.comparator.IDComparator;
import com.eressea.util.comparator.SortIndexComparator;
import com.eressea.util.file.CopyFile;
import com.eressea.util.file.FileType;
import com.eressea.util.logging.Logger;

public class Loader {
	private final static Logger log = Logger.getInstance(Loader.class);

	public static class MissingCRException extends IOException {
		public MissingCRException() {
		}
	}

	/** @author Rainer Klaffehn
	 *  @author Ilja Pavkovic
	 *  Moved method from com.eressea.demo.Client (including submethods)
	 * Rewrite of the original method. This version is cleaned up a bit, and
	 * attaches a ruleset based on the game type in the CR.
	 * @param fileName The file name to be loaded.
	 * @return a new <tt>GameData</tt> object filled with the data from the CR.
	 */
	public static GameData loadCR(String fileName) throws MissingCRException, IOException {

		FileType fileType = FileType.createFileType(fileName);
		if(log.isDebugEnabled()) {
			fileType.createReader().close();
			log.debug("Loader.loadCR("+fileName+"): "+fileType);
		}
		Reader file = new BufferedReader(fileType.createReader());
		Reader head = new BufferedReader(fileType.createReader());

		// find out the name of the game of this report
		String game = getGameName(head);
		head.close();

		/* Now that we know the game, we can create and read the data object. */
		GameData newData = new CompleteData(loadRules(game), game);
		newData.filetype = fileType;
		(new CRParser()).read(file, newData);
		file.close();
		/* the gamedata is read. now we do the necessary post processing. */
		Loader.postProcess(newData);
		return newData;
	}

	/**
	 * Returns the game's name of the specified computer report.
	 */
	private static String getGameName(Reader report) {
		Map headerMap = CollectionFactory.createHashMap();
		try {
			headerMap = (new CRParser()).readHeader(report);
		} catch (IOException e) {
			log.error("Loader.getGameName(): unable to determine game's name of report " + report,e);
		}
		if (headerMap.containsKey("Spiel")) {
			return (String)headerMap.get("Spiel");
		} else {
			log.warn("Loader.getGameName(): report header does not contain 'Spiel' tag!");
			return "Eressea";
		}
	}

	/**
	 * Loads the Eressea rules from the file rules.cr and adds it
	 * to the current game data.
	 */
	private static void loadRules(GameData _data) {
		URL url = ResourcePathClassLoader.getResourceStatically("rules/rules.cr");
		if (url != null) {
			try {
				Reader inReader = FileType.createEncodingReader(url.openStream());
				(new CRParser()).read(inReader, _data);
				inReader.close();
			} catch (IOException e) {
				log.error(e);
			}
		} else {
			log.warn("The rules file could not be found");
		}
	}

	/** @author Rainer Klaffehn
	 * Read a rule file given by the specific name. This allows us to have
	 * separate rule files for different games. If a specific rule file
	 * couldn't be found, we use a default fallback.
	 * @param name The name of the game, for which we want a rule file.
	 * @return the ruleset object.
	 */
	private static Rules loadRules(String name) {
		log.debug("loading rules for \""+name+"\"");
		URL url = ResourcePathClassLoader.getResourceStatically("rules/" + name.toLowerCase() + ".cr");
		if (url != null) {
			Rules rules = null;
			try {
				Reader reader = new InputStreamReader(url.openStream());
				rules = (new CRParser()).readRules(reader);
			} catch (IOException e) {
				log.error("Exception while reading the rules for game " + name + ".",e);
				rules = null;
			}
			if (rules == null) {
				/* This doesn't seem to be a rule file. Fallback to default,
				 if we haven't tried that yet. */
				log.warn("Encountered invalid rule file for game " + name + ".");
				if (name.equalsIgnoreCase("eressea")) {
					/* This is bad, the default rules are invalid. */
					log.warn("The default ruleset is invalid. Operating with empty ruleset.");
					return new Eressea();
				} else {
					return loadRules("eressea");
				}
			}
			return rules;
		} else {
			/* The desired rule file doesn't exist. Fallback to default, if
			 we haven't tried that yet. */
			if (name.equalsIgnoreCase("eressea")) {
				/* This is bad. We don't even have the default rules. */
				log.warn("The default ruleset couldn't be found! Operating with an empty ruleset.");
				return new Eressea();
			} else {
				return loadRules("eressea");
			}
		}
	}

	/**
	 * Processes a GameData object augmenting objects with additional
	 * information from messages, simplifying the GameData structure,
	 * creating temp units as distinct objects etc.
	 * Note that this method requires the classes Locales and
	 * Translations to be set up properly so the order translations
	 * can be found.
	 *
	 * @param data the GameData object to process.
	 */
	private static void postProcess(GameData data) {
		/* scan the messages for additional information */
		if (data != null && data.factions() != null) {
			for (Iterator factions = data.factions().values().iterator(); factions.hasNext(); ) {
				Faction f = (Faction)factions.next();
				if (f.messages != null) {
					for (Iterator iter = f.messages.iterator(); iter.hasNext(); ) {
						Message m = (Message)iter.next();
						if (m.getMessageType() != null) {
							switch ((((IntegerID)m.getMessageType().getID()).intValue())) {
							case 1511758069:
							case 18362:
								// a herb was found in a region
							case 1349776898:
								// a certain amount of herbs has been detected in a region
								if (m.attributes != null && m.attributes.containsKey("region")) {
									String str = (String) m.attributes.get("region");
									Coordinate coord = Coordinate.parse(str, ",");
									if (coord == null) {
										coord = Coordinate.parse(str, " ");
									}
									Region r = data.getRegion(coord);
									if (r != null) {
										String value =  (String)m.attributes.get("herb");
										if (value != null) {
											ItemType type = data.rules.getItemType(StringID.create(value), true);
											r.herb = type;
										}
										if((((IntegerID)m.getMessageType().getID()).intValue()) == 1349776898) {
											// a certain amount of herbs has been detected in a region
											String amount = (String) m.attributes.get("amount");
											if (amount != null) {
												r.herbAmount = amount;
											}
										}
									}
								}
								break;
							}
						}
					}
				}
			}
		}

		// there can be dummy units (UnitContainer owners and such), find and remove these
		if (data.units() != null) {
			Collection dummyUnitIDs = CollectionFactory.createLinkedList();
			for (Iterator iter = data.units().values().iterator(); iter.hasNext(); ) {
				Unit unit = (Unit)iter.next();
				if (unit.getName() == null) {
					dummyUnitIDs.add(unit.getID());
				}
			}
			for (Iterator iter = dummyUnitIDs.iterator(); iter.hasNext(); ) {
				data.units().remove(iter.next());
			}
		}

		/* retrieve the temp units mentioned in the orders and
		 create them as TempUnit objects */
		int sortIndex = 0;
		List sortedUnits = CollectionFactory.createLinkedList(data.units().values());
		Collections.sort(sortedUnits, new SortIndexComparator(new IDComparator()));
		for (Iterator unitIter = sortedUnits.iterator(); unitIter.hasNext(); ) {
			Unit unit = (Unit)unitIter.next();
			unit.setSortIndex(sortIndex++);
			sortIndex = unit.extractTempUnits(sortIndex);
		}

		/* 'known' information does not necessarily show up in the
		report. e.g. depleted region resources are not mentioned
		although we actually know that the resource is available with
		an amount of 0. Resolve this ambiguity here: */
		if (data != null && data.regions() != null) {
			ID sproutResourceID = StringID.create("Schößlinge");
			ID treeResourceID = StringID.create("Bäume");
			ID mallornSproutResourceID = StringID.create("Mallornschößlinge");
			ID mallornTreeResourceID = StringID.create("Mallorn");
			for (Iterator regionIter = data.regions().values().iterator(); regionIter.hasNext(); ) {
				Region region = (Region)regionIter.next();
				/* first determine whether we know everything about
				this region */
				if (!region.units().isEmpty()) {
					/* now patch as much missing information as
					possible */
					if (region.horses < 0) {
						region.horses = 0;
					}
					if (region.peasants < 0) {
						region.peasants = 0;
					}
					if (region.silver < 0) {
						region.silver = 0;
					}
					if (region.sprouts < 0) {
						region.sprouts = 0;
					}
					if (data.rules != null) {
						// 2002.05.21 pavkovic:
						// first of all: Remove resource information for sprouts, trees,
						// mallornsprouts and mallorntrees!
						// this is cumbersome, and will only stay for some time (two months)
						// to get rid of double or triple entries of these resources
						//
						boolean cleanup = true;
						if(cleanup) {
							Set cleanupSet = CollectionFactory.createHashSet();
							for(Iterator riter = region.resources().iterator();riter.hasNext(); ) {
								RegionResource rr =(RegionResource) riter.next();
								if(rr.getID().equals(sproutResourceID) ||
								   rr.getID().equals(treeResourceID) ||
								   rr.getID().equals(mallornSproutResourceID) ||
								   rr.getID().equals(mallornTreeResourceID) ||
								   rr.getType().getID().equals(sproutResourceID) ||
								   rr.getType().getID().equals(treeResourceID) ||
								   rr.getType().getID().equals(mallornSproutResourceID) ||
								   rr.getType().getID().equals(mallornTreeResourceID)) {
									cleanupSet.add(rr.getID());
									cleanupSet.add(rr.getType().getID());
								}
							}
							for(Iterator riter = cleanupSet.iterator(); riter.hasNext(); ) {
								ID id = (ID) riter.next();
								region.removeResource(id);
							}
						}
						if(region.mallorn) {
							// pavkovic 2002.05.06: remove disjunct resource (trees and sprouts)
							region.removeResource(sproutResourceID);
							// add new resource
							if(region.getResource(mallornSproutResourceID) == null) {
								RegionResource res = new RegionResource(LongID.create(mallornSproutResourceID.hashCode()), data.rules.getItemType(mallornSproutResourceID, true));
								res.setAmount(region.sprouts);
								region.addResource(res);
							}
						} else {
							// pavkovic 2002.05.06: remove disjunct resource (trees and sprouts)
							region.removeResource(mallornSproutResourceID);
							// add new resource
							if (region.getResource(sproutResourceID) == null) {
								RegionResource res = new RegionResource(LongID.create(sproutResourceID.hashCode()), data.rules.getItemType(sproutResourceID, true));
								res.setAmount(region.sprouts);
								region.addResource(res);
							}
						}
					}
					if (region.trees < 0) {
						region.trees = 0;
					}
					if(region.mallorn) {
						// pavkovic 2002.05.06: remove disjunct resource (trees and sprouts)
						region.removeResource(treeResourceID);
						// add new resource
						if (region.getResource(mallornTreeResourceID) == null) {
							RegionResource res = new RegionResource(LongID.create(mallornTreeResourceID.hashCode()), data.rules.getItemType(mallornTreeResourceID, true));
							res.setAmount(region.trees);
							region.addResource(res);
						}
					} else {
						// pavkovic 2002.05.06: remove disjunct resource (trees and sprouts)
						region.removeResource(mallornTreeResourceID);
						// add new resource
						if(data.rules != null) {
							if (region.getResource(treeResourceID) == null) {
								RegionResource res = new RegionResource(LongID.create(treeResourceID.hashCode()), data.rules.getItemType(treeResourceID, true));
								res.setAmount(region.trees);
								region.addResource(res);
							}
						}
					}

					if (region.wage < 0) {
						region.wage = 0;
					}
				}
			}
		}
		data.resetToUnchanged();
	}

	/** 
	 * this method creates a clone of the gamedata using CRWriter/CRParser 
	 */
	public static GameData cloneGameData(GameData data) throws CloneNotSupportedException {
		try {
			File tempFile = CopyFile.createCrTempFile();
			tempFile.deleteOnExit();
			
			// write cr to file
			CRWriter crw = new CRWriter(FileType.createFileType(tempFile));
			crw.write(data);
			crw.close();
			
			GameData newData = Loader.loadCR(tempFile.getPath());
			newData.filetype=new FileType(data.filetype);
			tempFile.delete();
			
			return newData;
		} catch(IOException ioe) {
			log.error("Loader.cloneGameData failed!",ioe);
			throw new CloneNotSupportedException();
		}
	}
}
