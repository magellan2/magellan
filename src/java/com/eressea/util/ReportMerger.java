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

package com.eressea.util;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.eressea.CoordinateID;
import com.eressea.Faction;
import com.eressea.GameData;
import com.eressea.Region;
import com.eressea.Scheme;
import com.eressea.StringID;
import com.eressea.rules.RegionType;
import com.eressea.util.logging.Logger;

/**
 * Helper class.
 */
public class ReportMerger extends Object {
	private static final Logger log = Logger.getInstance(ReportMerger.class);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @author $author$
	 * @version $Revision$
	 */
	public interface Loader {
		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param file TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public GameData load(File file);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @author $author$
	 * @version $Revision$
	 */
	public interface AssignData {
		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param _data TODO: DOCUMENT ME!
		 */
		public void assign(GameData _data);
	}

	private class Report {
		// data set
		GameData data = null;

		// load data from
		File file = null;

		// maps region names to region coordinate
		Map regionMap = null;
		
		// maps region UIDs to Regions
		Map regionUIDMap = null;

		// maps schemes (region names) to a Collection of astral regions
		// which contain that scheme
		Map schemeMap = null;

		// already merged with another report
		boolean merged = false;
	}

	// merged data set
	GameData data = null;

	// reports to merge
	Report reports[] = null;

	// loader interface
	Loader loader = null;

	// data assign interface
	AssignData assignData = null;

	public interface UserInterface {
		public void ready();
		public void show();
		public void setProgress(String strMessage, int iProgress);
		public boolean confirm(String strMessage, String strTitle);
	}

	public class NullUserInterface implements UserInterface {
		public void ready() {}
		public void show() {}
		public void setProgress(String strMessage, int iProgress) {}
		public boolean confirm(String strMessage, String strTitle) {
			return true;
		}
	}

	public class SwingUserInterface implements UserInterface {
		// user interface
		ProgressDlg dlg = null;

		public SwingUserInterface(JFrame parent) {
			init(parent);
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param parent TODO: DOCUMENT ME!
		 */
		private void init(JFrame parent) {
			dlg = new ProgressDlg(parent, true);
			dlg.labelText.setText(getString("status.merge"));
			dlg.progressBar.setMinimum(0);
			dlg.progressBar.setMaximum(reports.length * 4);
		}

		/**
		 * TODO: DOCUMENT ME!
		 */
		public void show() {
			new Thread(new Runnable() {public void run() {
				SwingUserInterface.this.dlg.setVisible(true);
			}}).start();
		}

		private class Confirm implements Runnable {
			String strMessage;
			String strTitle;
			boolean bResult = false;

			/**
			 * TODO: DOCUMENT ME!
			 */
			public void run() {
				if(JOptionPane.showConfirmDialog(dlg, strMessage,
													 getString("msg.confirmmerge.title"),
													 JOptionPane.YES_NO_OPTION,
													 JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
					bResult = true;
				} else {
					bResult = false;
				}
			}
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param strMessage TODO: DOCUMENT ME!
		 * @param strTitle TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public boolean confirm(String strMessage, String strTitle) {
			Confirm conf = new Confirm();
			conf.strMessage = strMessage;
			conf.strTitle = strTitle;

			try {
				SwingUtilities.invokeAndWait(conf);
			} catch(Exception e) {
				log.error(e);
			}

			return conf.bResult;
		}

		private class Progress implements Runnable {
			String strMessage;
			int iProgress;

			/**
			 * TODO: DOCUMENT ME!
			 */
			public void run() {
				dlg.labelText.setText(strMessage);
				dlg.progressBar.setValue(iProgress);
			}
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param strMessage TODO: DOCUMENT ME!
		 * @param iProgress TODO: DOCUMENT ME!
		 */
		public void setProgress(String strMessage, int iProgress) {
			Progress progress = new Progress();
			progress.strMessage = strMessage;
			progress.iProgress = iProgress;

			SwingUtilities.invokeLater(progress);
		}

		/**
		 * TODO: DOCUMENT ME!
		 */
		public void ready() {
			SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						SwingUserInterface.this.dlg.setVisible(false);
						SwingUserInterface.this.dlg.dispose();
					}
				});
		}
	}

	UserInterface ui;
	int iProgress;

	/**
	 * Creates new ReportMerger
	 *
	 * @param _data TODO: DOCUMENT ME!
	 * @param files TODO: DOCUMENT ME!
	 * @param _loader TODO: DOCUMENT ME!
	 * @param _assignData TODO: DOCUMENT ME!
	 */
	public ReportMerger(GameData _data, File files[], Loader _loader, AssignData _assignData) {
		data = _data;
		data.removeTheVoid(); // removes void regions
		reports = new Report[files.length];

		for(int i = 0; i < files.length; i++) {
			reports[i] = new Report();
			reports[i].file = files[i];
		}

		loader = _loader;
		assignData = _assignData;
	}

	/**
	 * Creates a new ReportMerger object.
	 *
	 * @param _data TODO: DOCUMENT ME!
	 * @param file TODO: DOCUMENT ME!
	 * @param _loader TODO: DOCUMENT ME!
	 * @param _assignData TODO: DOCUMENT ME!
	 */
	public ReportMerger(GameData _data, File file, Loader _loader, AssignData _assignData) {
		data = _data;
		data.removeTheVoid(); // removes void regions
		reports = new Report[1];
		reports[0] = new Report();
		reports[0].file = file;

		loader = _loader;
		assignData = _assignData;
	}

	public GameData merge() {
		return merge(new NullUserInterface(), false);
	}

	public GameData merge(UserInterface aUI, boolean async) {
		ui = aUI;
		if(async) {
			new Thread(new Runnable() {
					public void run() {
						ReportMerger.this.mergeThread();
					}
				}).start();
			return null;
		} else {
			return this.mergeThread();
		}
	}

	/**
	 * Starts merging. Parent is used as parent for the userinterface.
	 *
	 * @param parent TODO: DOCUMENT ME!
	 * 
	 * @deprecated
	 */
	public GameData merge(JFrame parent) {
		return merge(new SwingUserInterface(parent),true);
	}

	private GameData mergeThread() {
		if(ui != null) {
			ui.show();
		}
		try {
			int iPosition = 0;
			int iFailedConnectivity = 0;
			int iMerged = 0;

			while(true) {
				if(!reports[iPosition].merged) {
					if(!mergeReport(reports[iPosition])) {
						iFailedConnectivity++;
					} else {
						iFailedConnectivity = 0;
						iMerged++;
					}
				}

				if((iMerged + iFailedConnectivity) == reports.length) {
					// some reports with out connection to central report
					break;
				}

				iPosition++;

				if(iPosition >= reports.length) {
					iPosition = 0;
					iFailedConnectivity = 0;
				}
			}

			if(iFailedConnectivity > 0) {
				String strMessage = getString("msg.noconnection.text.1");

				for(int i = 0; i < reports.length; i++) {
					if(!reports[i].merged) {
						strMessage += reports[i].file.getName();

						if((i + 1) < reports.length) {
							strMessage += ", ";
						}
					}
				}

				strMessage += getString("msg.noconnection.text.2");

				if(ui != null && ui.confirm(strMessage, getString("msg.confirmmerge.title"))) {
					for(int i = 0; i < reports.length; i++) {
						if(!reports[i].merged) {
							iProgress += 2;
							ui.setProgress(reports[i].file.getName() + " - " +
										   getString("status.merging"), iProgress);

							//data.mergeWith( reports[i].data );
							data = GameData.merge(data, reports[i].data);
							reports[i].merged = true;

							reports[i].data = null;
							reports[i].regionMap = null;

						}
					}
				}
			}
		} catch(Exception e) {
			log.error(e);
		}

		if(ui != null) { 
			ui.ready();
		}

		if(assignData != null) {
			assignData.assign(data);
		}

		return data;
	}

	
	/**
	 * Merges a report to the current report.
	 * 
	 * @param report
	 * @return true iff reports were merged or report data null or report types don't match 
	 */
	// TODO: We need to break this monster method up, urgently!!!
	private boolean mergeReport(Report report) {
		if(report.data == null) {
			iProgress += 1;
			if(ui != null) { 
				ui.setProgress(report.file.getName() + " - " + getString("status.loading"), iProgress);
			}

			report.data = loader.load(report.file);
		}

		if(report.data == null || !data.name.equalsIgnoreCase(report.data.name)) {
			// no report loaded or 
			// game types doesn't match. Make sure, it will not be tried again.
			// TODO: maybe issue a message here.
			if (report.data == null)
				log.warn("ReportMerger.mergeReport(): got empty data.");
			else
				log.warn("ReportMerger.mergeReport(): game types don't match.");

			report.merged = true;

			return true;
		}

		/**
		 * prepare faction trustlevel for merging: - to be added CR is older or of same age -> hold
		 * existing trust levels - to be added CR is newer and contains trust level that were set
		 * by the user explicitly (or read from CR what means the same) -> take the trust levels
		 * out of the new CR otherwise -> hold existing trust levels This means: set those trust
		 * levels, that will not be retained to default values
		 */
		if((data.getDate() != null) && (report.data.getDate() != null) &&
			   (data.getDate().getDate() < report.data.getDate().getDate()) &&
			   TrustLevels.containsTrustLevelsSetByUser(report.data)) {
			// take the trust levels out of the to be added data
			// set those in the existing data to default-values
			for(Iterator iterator = data.factions().values().iterator(); iterator.hasNext();) {
				Faction f = (Faction) iterator.next();
				f.trustLevel = Faction.TL_DEFAULT;
				f.trustLevelSetByUser = false;
			}
		} else {
			// take the trust levels out of the existing data
			// set those in the to be added data to default-values
			for(Iterator iterator = report.data.factions().values().iterator(); iterator.hasNext();) {
				Faction f = (Faction) iterator.next();
				f.trustLevel = Faction.TL_DEFAULT;
				f.trustLevelSetByUser = false;
			}
		}

		/**
		 * Prepare curTempID-Value for merging. If reports are of the same age, keep existing by
		 * setting the new one to default value. Otherwise set the existing to default value.
		 */
		if((data.getDate() != null) && (report.data.getDate() != null) &&
			   (data.getDate().getDate() < report.data.getDate().getDate())) {
			data.setCurTempID(-1);
		} else {
			report.data.setCurTempID(-1);
		}

		boolean reportHasAstralRegions=false;
		boolean dataHasAstralRegions=false;
		
		
		
		
       /**
        *      Astral:         A...B
        *                              |   |
        *      Real:           A---B
        *
        *  To merge two non-overlapping Astral Spaces we need:
        *  - an astral to real mapping of report A
        *  - an overlapping real world between both reports
        *  - an astral to real mapping of report B
        *
        *  Astral to real mapping can be done by two ways:
        *  1. from two neighbour astral spaces with schemes
        *     (there can be seen exactly one same scheme from both astral regions)
        *  2. from several astral regions with schemes, calculating the "extend" of the schemes
        *
        *  ==> Having only one astral region with schemes will often not be enough
        *      to calculate the mapping between astral and real space
        *
        *  Variant 2 not jet implemented!
        **/

       CoordinateID reportAstralToReal = null;
       CoordinateID dataAstralToReal = null;
       CoordinateID minExtend = null;
       CoordinateID maxExtend = null;
       Map dataSchemeMap = CollectionFactory.createHashMap();
       for(Iterator iter = data.regions().values().iterator(); iter.hasNext()&&(dataAstralToReal==null);) {
               Region region = (Region) iter.next();
               if(region.getCoordinate().z == 1) {
                       for(Iterator schemes = region.schemes().iterator(); schemes.hasNext()&&(dataAstralToReal==null);) {
                               Scheme scheme = (Scheme) schemes.next();
                               Collection col = (Collection) dataSchemeMap.get(scheme.getName());
                               if(col == null) {
                                       col = CollectionFactory.createLinkedList();
                                       dataSchemeMap.put(scheme.getName(), col);
                               } else {
                                       /**
                                        * This is the second astral region showing the same scheme.
                                        * From this we can calculate an astral to real mapping for the gamedata by variant 1
                                        */
                                       // in case of errors in the current Astral Regions (merged schemes) we will get a wrong mapping here. Therefore a scheme consistency check should be done in advance. (several posibilities)
                                       CoordinateID firstCoord = ((Region) col.iterator().next()).getCoordinate();
                                       CoordinateID secondCoord = region.getCoordinate();
                                       CoordinateID schemeCoord = scheme.getCoordinate();
                                       dataAstralToReal = new CoordinateID(
                                               schemeCoord.x - 2 * (firstCoord.x + secondCoord.x),
                                               schemeCoord.y - 2 * (firstCoord.y + secondCoord.y));
                               }
                               col.add(region);
                               // we may not find any astral to real mapping by variant 1 above
                               // therefore also do calculations for variant 2 here
                               // we "normalize" all schemes to be in the area
                               int nx = scheme.getCoordinate().x - 4 * region.getCoordinate().x;
                               int ny = scheme.getCoordinate().y - 4 * region.getCoordinate().y;
                               // this is a virtual 3 axis diagonal to x and y in the same level, but we store it in the z coordinate
                               int nd = nx + ny;
                               if (minExtend == null) {
                                       minExtend = new CoordinateID(nx, ny, nd);
                                       maxExtend = new CoordinateID(nx, ny, nd);
                               } else {
                                       minExtend.x = Math.min(minExtend.x, nx);
                                       minExtend.y = Math.min(minExtend.y, ny);
                                       minExtend.z = Math.min(minExtend.z, nd);
                                       maxExtend.x = Math.max(maxExtend.x, nx);
                                       maxExtend.y = Math.max(maxExtend.y, ny);
                                       maxExtend.z = Math.max(maxExtend.z, nd);
                               }
                               // now check if we found an "extend of 4" in at least two directions of the three directions
                               boolean dx = maxExtend.x-minExtend.x==4;
                               boolean dy = maxExtend.y-minExtend.y==4;
                               boolean dd = maxExtend.z-minExtend.z==4;
                               if (dx&&dy) {
                                       dataAstralToReal = new CoordinateID(maxExtend.x - 2, maxExtend.y - 2);
                               } else if (dx&&dd) {
                                       dataAstralToReal = new CoordinateID(maxExtend.x - 2, maxExtend.z - maxExtend.x);
                               } else if (dy&&dd) {
                                       dataAstralToReal = new CoordinateID(maxExtend.z - maxExtend.y, maxExtend.y - 2);
                               }
                       }
               }
       }

       // free up min and max, we use them again for the report astral to real mapping
       minExtend = null;
       maxExtend = null;
               
		// it is safe to assume, that when regionMap is null, schemeMap is null, too
		if(report.regionMap == null) {
			iProgress += 1;
			if(ui != null) {				
				ui.setProgress(report.file.getName() + " - " + getString("status.processing"), iProgress);
			}
			report.regionMap = CollectionFactory.createHashMap();
			report.schemeMap = CollectionFactory.createHashMap();
			report.regionUIDMap = CollectionFactory.createHashMap();
			
			for(Iterator iter = report.data.regions().values().iterator(); iter.hasNext();) {
				Region region = (Region) iter.next();

				if((region.getName() != null) && (region.getName().length() > 0)) {
					/*if (report.regionMap.containsKey(region.getName())) {
					    report.regionMap.put(region.getName(), null);
					}else{*/
					report.regionMap.put(region.getName(), region);

					//}
				}
				
				if (region.getUID()!=0){
					report.regionUIDMap.put(new Long(region.getUID()), region);
				}
				
				if(region.getCoordinate().z == 1) {
					reportHasAstralRegions=true;
					for(Iterator schemes = region.schemes().iterator(); schemes.hasNext();) {
						Scheme scheme = (Scheme) schemes.next();
						Collection col = (Collection) report.schemeMap.get(scheme.getName());

						if(col == null) {
							col = CollectionFactory.createLinkedList();
							report.schemeMap.put(scheme.getName(), col);
                                               } else {
                                                       /**
                                                        * This is the second astral region showing the same scheme.
                                                        * From this we can calculate an astral to real mapping for the new report by variant 1
                                                        */
                                                       // only if not already found a mapping
                                                       if (reportAstralToReal == null) {
                                                               CoordinateID firstCoord = ((Region) col.iterator().next()).getCoordinate();
                                                               CoordinateID secondCoord = region.getCoordinate();
                                                               CoordinateID schemeCoord = scheme.getCoordinate();
                                                               reportAstralToReal = new CoordinateID(
                                                                       schemeCoord.x - 2 * (firstCoord.x + secondCoord.x),
                                                                       schemeCoord.y - 2 * (firstCoord.y + secondCoord.y));
                                                       }
						}
                                               col.add(region);

                                               // we may not find any astral to real mapping by variant 1 above
                                               // therefore also do calculations for variant 2 here
                                               // we "normalize" all schemes to be in the area
                                               // only if not already found a mapping
                                               if (reportAstralToReal == null) {
                                                       int nx = scheme.getCoordinate().x - 4 * region.getCoordinate().x;
                                                       int ny = scheme.getCoordinate().y - 4 * region.getCoordinate().y;
                                                       // this is a virtual 3 axis diagonal to x and y in the same level, but we store it in the z coordinate
                                                       int nd = nx + ny;
                                                       if (minExtend == null) {
                                                               minExtend = new CoordinateID(nx, ny, nd);
                                                               maxExtend = new CoordinateID(nx, ny, nd);
                                                       } else {
                                                               minExtend.x = Math.min(minExtend.x, nx);
                                                               minExtend.y = Math.min(minExtend.y, ny);
                                                               minExtend.z = Math.min(minExtend.z, nd);
                                                               maxExtend.x = Math.max(maxExtend.x, nx);
                                                               maxExtend.y = Math.max(maxExtend.y, ny);
                                                               maxExtend.z = Math.max(maxExtend.z, nd);
                                                       }
                                                       // now check if we found an "extend of 4" in at least two directions of the three directions
                                                       boolean dx = maxExtend.x-minExtend.x==4;
                                                       boolean dy = maxExtend.y-minExtend.y==4;
                                                       boolean dd = maxExtend.z-minExtend.z==4;
                                                       if (dx&&dy) {
                                                               reportAstralToReal = new CoordinateID(maxExtend.x - 2, maxExtend.y - 2);
                                                       } else if (dx&&dd) {
                                                               reportAstralToReal = new CoordinateID(maxExtend.x - 2, maxExtend.z - maxExtend.x);
                                                       } else if (dy&&dd) {
                                                               reportAstralToReal = new CoordinateID(maxExtend.z - maxExtend.y, maxExtend.y - 2);
                                                       }
                                               }
					}
				}
			}
		}

		// determine translation of coordinate system
		/**
		 * Important: A faction's coordinate system for astral space is indepent of it's coordinate
		 * system for normal space. It depends (as far as I know) on the astral space region where
		 * the faction first enters the astral space (this region will have the coordinate
		 * (0,0,1). Thus a special translation for the astral space (beside that one for normal
		 * space) has to be found.
		 */
		iProgress += 1;
		if(ui != null) {				
			ui.setProgress(report.file.getName() + " - " + getString("status.connecting"), iProgress);
		}

		// maps translation (Coordinate) to match count (Integer)
		Map translationMap = new Hashtable();
		Map astralTranslationMap = new Hashtable();

		for(Iterator iter = data.regions().values().iterator(); iter.hasNext();) {
			Region region = (Region) iter.next();

			CoordinateID coord = region.getCoordinate();

			if(coord.z == 0) {
				if((region.getName() != null) && (region.getName().length() > 0)) {
					Region foundRegion = (Region) report.regionMap.get(region.getName());

					if(foundRegion != null) {
						CoordinateID foundCoord = foundRegion.getCoordinate();

						CoordinateID translation = new CoordinateID(foundCoord.x - coord.x,
																foundCoord.y - coord.y);

						Integer count = (Integer) translationMap.get(translation);

						if(count == null) {
							count = new Integer(1);
						} else {
							count = new Integer(count.intValue() + 1);
						}

						translationMap.put(translation, count);
					}
				}
			} else if(coord.z == 1) {
				// Now try to find an astral space region that matches this region
				// We can't use region name for this, since all astral space
				// regions are named "Nebel". We use the schemes instead.
				// Since all schemes have to match it's sufficient to look at the
				// first one to find a possible match. To check whether that
				// match really is one, we have to look at all schemes.
				dataHasAstralRegions=true;
				if(!region.schemes().isEmpty()) {
					Scheme scheme = (Scheme) region.schemes().iterator().next();
					Object o = report.schemeMap.get(scheme.getName());

					if(o != null) {
						// we found some astral region that shares at least
						// one scheme with the actual region. However, this
						// doesn't mean a lot, since schemes belong to several
						// astral regions.
						// check whether any of those regions shares all schemes
						for(Iterator regIter = ((Collection) o).iterator(); regIter.hasNext();) {
							Region foundRegion = (Region) regIter.next();

							if(foundRegion.schemes().size() == region.schemes().size()) {
								// at least the size fits
								boolean mismatch = false;

								for(Iterator schemes1 = region.schemes().iterator();
										schemes1.hasNext() && !mismatch;) {
									Scheme s1 = (Scheme) schemes1.next();
									boolean found = false;

									for(Iterator schemes2 = foundRegion.schemes().iterator();
											schemes2.hasNext() && !found;) {
										Scheme s2 = (Scheme) schemes2.next();

										if(s1.getName().equals(s2.getName())) {
											found = true; // found a scheme match
										}
									}

									if(!found) {
										mismatch = true;
									}
								}

								if(!mismatch) {
									// allright, seems we found a valid translation
									CoordinateID foundCoord = foundRegion.getCoordinate();
									CoordinateID translation = new CoordinateID(foundCoord.x - coord.x,
																			foundCoord.y - coord.y,
																			1);
									Integer count = (Integer) astralTranslationMap.get(translation);

									if(count == null) {
										count = new Integer(1);
									} else {
										count = new Integer(count.intValue() + 1);
									}

									astralTranslationMap.put(translation, count);
								}
							}
						}
					}
				}
			}
		}

		// Fiete: add Translations found by using regionUIDs
		translationMap.putAll(this.getTranslationCandidatesRegionUID(report, 0));
		astralTranslationMap.putAll(this.getTranslationCandidatesRegionUID(report, 1));
		
		// end of search for translations, now check the found ones

		/* check whether any of the normal space translations is impossible by
		   comparing the terrains */
		int maxTerrainMismatches = (int) (Math.max(data.regions().size(),
												   report.data.regions().size()) * 0.02);
		CoordinateID loopCoord = new CoordinateID(0, 0, 0);
		RegionType forestTerrain = data.rules.getRegionType(StringID.create("Wald"));
		RegionType plainTerrain = data.rules.getRegionType(StringID.create("Ebene"));
		RegionType oceanTerrain = data.rules.getRegionType(StringID.create("Ozean"));
		RegionType glacierTerrain = data.rules.getRegionType(StringID.create("Gletscher"));
		RegionType activeVolcanoTerrain = data.rules.getRegionType(StringID.create("Aktiver Vulkan"));
		RegionType volcanoTerrain = data.rules.getRegionType(StringID.create("Vulkan"));

		for(Iterator iter = translationMap.keySet().iterator(); iter.hasNext();) {
			CoordinateID translation = (CoordinateID) iter.next();
			int mismatches = 0; // the number of regions not having the same region type at the current translations

			/* for each traslations we have to compare the regions'
			   terrains */
			for(Iterator regionIter = data.regions().values().iterator(); regionIter.hasNext();) {
				Region r = (Region) regionIter.next();

				if((r.getType() == null) || r.getType().equals(RegionType.unknown)) {
					continue;
				}

				CoordinateID c = r.getCoordinate();

				/* do the translation and find the corresponding
				   region in the report data */
				if(c.z == 0) {
					loopCoord.x = c.x;
					loopCoord.y = c.y;
					loopCoord.translate(translation);

					Region reportDataRegion = (Region) report.data.regions().get(loopCoord);

					/* the hit count for the current translation must
					   only be modified, if there actually are regions
					   to be compared and their terrains are valid */
					if((reportDataRegion != null) && (reportDataRegion.getType() != null) &&
						   !(reportDataRegion.getType().equals(RegionType.unknown))) {
						if(!r.getType().equals(reportDataRegion.getType())) {
							/* now we have a mismatch. If the reports
							   are from the same turn, terrains may
							   not differ at all. If the reports are
							   from different turns, some terrains
							   can be transformed. */
							if((data.getDate() != null) && (report.data.getDate() != null) &&
								   data.getDate().equals(report.data.getDate())) {
								mismatches++;
							} else {
								if(!(((forestTerrain != null) && (plainTerrain != null) &&
									   ((forestTerrain.equals(r.getType()) &&
									   plainTerrain.equals(reportDataRegion.getType())) ||
									   (plainTerrain.equals(r.getType()) &&
									   forestTerrain.equals(reportDataRegion.getType())))) ||
									   ((oceanTerrain != null) && (glacierTerrain != null) &&
									   ((oceanTerrain.equals(r.getType()) &&
									   glacierTerrain.equals(reportDataRegion.getType())) ||
									   (glacierTerrain.equals(r.getType()) &&
									   oceanTerrain.equals(reportDataRegion.getType())))) ||
									   ((activeVolcanoTerrain != null) && (volcanoTerrain != null) &&
									   ((activeVolcanoTerrain.equals(r.getType()) &&
									   volcanoTerrain.equals(reportDataRegion.getType())) ||
									   (volcanoTerrain.equals(r.getType()) &&
									   activeVolcanoTerrain.equals(reportDataRegion.getType())))))) {
									mismatches++;
								}
							}

							if(mismatches > maxTerrainMismatches) {
								translationMap.put(translation, new Integer(-1));

								break;
							}
						}
					}
				}
			}
		}

		/**
		 * Check the astral space translation map by comparing the schemes. Heuristic: If both
		 * space regions have schemes, they shouldn't differ. If they do, somethink is probably
		 * wrong!
		 */
		for(Iterator iter = astralTranslationMap.keySet().iterator(); iter.hasNext();) {
			CoordinateID translation = (CoordinateID) iter.next();

			// the number of astral space region where a scheme mismatch was found.
			int mismatches = 0;

			/* for each traslations we have to compare the regions' schemes */
			for(Iterator regionIter = data.regions().values().iterator(); regionIter.hasNext();) {
				Region r = (Region) regionIter.next();

				if(r.getCoordinate().z != 1) {
					continue;
				}

				CoordinateID c = r.getCoordinate();

				/* do the translation and find the corresponding
				   region in the report data */
				loopCoord.x = c.x + translation.x;
				loopCoord.y = c.y + translation.y;

				Region reportDataRegion = (Region) report.data.regions().get(loopCoord);

				if((reportDataRegion != null) && !reportDataRegion.schemes().isEmpty() &&
					   !r.schemes().isEmpty()) {
					// number of schemes the same?
					boolean mismatch = reportDataRegion.schemes().size() != r.schemes().size();

					// if number is ok, use nested loop to compare scheme names
					for(Iterator schemes1 = reportDataRegion.schemes().iterator();
							schemes1.hasNext() && !mismatch;) {
						Scheme s1 = (Scheme) schemes1.next();
						boolean foundname = false;

						for(Iterator schemes2 = r.schemes().iterator(); schemes2.hasNext();) {
							Scheme s2 = (Scheme) schemes2.next();

							if(s1.getName().equals(s2.getName())) {
								foundname = true; // found a scheme match

								break;
							}
						}

						if(!foundname) {
							mismatch = true;
						}
					}

					if(mismatch) {
						mismatches++;
					}
				}
			}

			// decrease hit count of this translation for each mismatch
			Integer i = (Integer) astralTranslationMap.get(translation);
			Integer i2 = new Integer(i.intValue() - mismatches);
			astralTranslationMap.put(translation, i2);
		}

		int iDX = 0;
		int iDY = 0;
		int iCount = 0;
		boolean bEqual = false;

		// search highest hit count
		Iterator iter = translationMap.entrySet().iterator();

		while(iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();

			CoordinateID translation = (CoordinateID) entry.getKey();
			int count = ((Integer) entry.getValue()).intValue();

			/*System.out.println( "Translation X:" + translation.x + " Y:" + translation.y +
			    " Hits:" + count );*/
			if(count > iCount) {
				iDX = translation.x;
				iDY = translation.y;
				iCount = count;
				bEqual = false;
			} else {
				if(count == iCount) {
					bEqual = true;
				}
			}
		}

       // Put AstralTranslation via Real Space Translation
       if ((dataAstralToReal!=null)&&(reportAstralToReal!=null)) {
               log.info("ReportMerger: Data   AR-Real: " + dataAstralToReal);
               log.info("ReportMerger: Report AR-Real: " + reportAstralToReal);
               log.info("ReportMerger: Real Data-Report: " + iDX + ", " + iDY );

               
               astralTranslationMap.put(new CoordinateID(
                       (new Integer((dataAstralToReal.x - reportAstralToReal.x + iDX) / 4)).intValue(),
                       (new Integer((dataAstralToReal.y - reportAstralToReal.y + iDY) / 4)).intValue(),
                       1 ),new Integer(99999));
       }
       
       
       // Fiete - added OneRegion Astral-Real-Mapping
       CoordinateID dataAstralToReal_OneRegion = this.getOneRegion_AR_RR_Translation(data);
       CoordinateID reportAstralToReal_OneRegion = this.getOneRegion_AR_RR_Translation(report.data);
       if ((dataAstralToReal_OneRegion!=null)&&(reportAstralToReal_OneRegion!=null)) {
           log.info("ReportMerger (OneRegion): Data   AR-Real: " + dataAstralToReal_OneRegion);
           log.info("ReportMerger (OneRegion): Report AR-Real: " + reportAstralToReal_OneRegion);
           CoordinateID astralTrans = new CoordinateID(
                   (new Integer((dataAstralToReal_OneRegion.x - reportAstralToReal_OneRegion.x + iDX ) / 4)).intValue(),
                   (new Integer((dataAstralToReal_OneRegion.y - reportAstralToReal_OneRegion.y  + iDY) / 4)).intValue(),
                   1 );
           log.info("ReportMerger (OneRegion): Resulting Trans: " + astralTrans);
           astralTranslationMap.put(astralTrans,new Integer(100000));
      }
       
		// search for best astral translation
		CoordinateID bestAstralTranslation = new CoordinateID(0, 0, 1);
		int bestHitCount = -1;

		for(iter = astralTranslationMap.keySet().iterator(); iter.hasNext();) {
			CoordinateID translation = (CoordinateID) iter.next();
			int count = ((Integer) astralTranslationMap.get(translation)).intValue();

			if(count > bestHitCount) {
				bestHitCount = count;
				bestAstralTranslation = translation;
			}
		}

		if(reportHasAstralRegions && dataHasAstralRegions && bestHitCount <= 0) {
			log.warn("Warning: ReportMerger: Couldn't find a good translation for astral space coordinate systems. " + 
					 "Merge results on level 1 may be poor and undefined!");
		} else if(astralTranslationMap.size() > 0) {
			log.info("ReportMerger: Found " + astralTranslationMap.size() +
							   " possible translations for astral space. Using this one: " +
							   bestAstralTranslation);
		}

		CoordinateID usedAstralTranslation = null;
		
		// use astral space translation anyway
		if((data.getDate() == null) || (report.data.getDate() == null)) {
			usedAstralTranslation = bestAstralTranslation;
//			report.data.placeOrigin(bestAstralTranslation);
		} else {
			// TODO: figure out if it is "<" or ">" here
			if(data.getDate().getDate() > report.data.getDate().getDate()) {
				usedAstralTranslation = new CoordinateID(-bestAstralTranslation.x, -bestAstralTranslation.y,
						bestAstralTranslation.z);
//				data.placeOrigin(new Coordinate(-bestAstralTranslation.x, -bestAstralTranslation.y,
//												bestAstralTranslation.z));
			} else {
				usedAstralTranslation = bestAstralTranslation;
//				report.data.placeOrigin(bestAstralTranslation);
			}
		}

		// TODO: manual translation
//		if (newAstralOrigin!= null && forceAstralOrigin)
//			usedAstralTranslation= newAstralOrigin;
		
		if (usedAstralTranslation != null && usedAstralTranslation != new CoordinateID(0,0,1)){
			log.info("ReportMerger: Using this astral translation: " + usedAstralTranslation.toString());
//			report.data.placeOrigin(usedAstralTranslation);
			try {
				if (usedAstralTranslation.x != 0 || usedAstralTranslation.y != 0)
					report.data = (GameData) report.data.clone(usedAstralTranslation);
			} catch (CloneNotSupportedException e) {
				log.error(e);
			}

		}
		

		CoordinateID usedTranslation = null;

		if ((data.getDate() == null) || (report.data.getDate() == null)) {
			usedTranslation = new CoordinateID(iDX, iDY);
			// report.data.placeOrigin(new Coordinate(iDX, iDY));
		} else {
			if (data.getDate().getDate() > report.data.getDate().getDate()) {
				usedTranslation = new CoordinateID(-iDX, -iDY);
				// data.placeOrigin(new Coordinate(-iDX, -iDY));
			} else {
				usedTranslation = new CoordinateID(iDX, iDY);
				// report.data.placeOrigin(new Coordinate(iDX, iDY));
			}
		}

		// TODO: manual translation
//		if (newOrigin != null && forceOrigin)
//			usedTranslation = newOrigin;

		if (usedTranslation != null
				&& usedTranslation != new CoordinateID(0, 0, 0)) {
			log.info("ReportMerger: Using this translation: "
					+ usedTranslation.toString());
//			report.data.placeOrigin(usedTranslation);
			try {				
				if (usedTranslation.x != 0 || usedTranslation.y != 0)
					report.data = (GameData) report.data.clone(usedTranslation);
			} catch (CloneNotSupportedException e) {
				log.error(e);
			}
		}

		// valid translation?
		if ((iCount > 0) && (!bEqual)) {
			iProgress += 1;
			if (ui != null) {
				ui.setProgress(report.file.getName() + " - "
						+ getString("status.merging"), iProgress);
			}
			
			
			///////////////////////////////////////////////////
			// Merge the reports, finally!
			
			// data.mergeWith( report.data );
			data = GameData.merge(data, report.data);
			report.merged = true;

			report.data = null;
			report.regionMap = null;
		} else {
			iProgress -= 1;
			if(ui != null) {				
				ui.setProgress(report.file.getName(), iProgress);
			}
		}

		return report.merged;
	}

	/**
	 * Weiterer Versuch des Findens eines AR-RR Mappings
	 * Hierbei soll EINE AR Region mit ausreichend Schemen reichen
	 * @param data
	 * @return
	 */
	private CoordinateID getOneRegion_AR_RR_Translation(GameData data){
		/**
		 * Ansatz:
		 * Sind die Schemen günstig verteilt, reicht eine AR Region und ihre Schemen
		 * zum Bestimmen des Mappings AR->RR
		 * Dazu wird versucht, die RR-Region genau unter der AR-Region zu finden.
		 * Diese darf maximal 2 Regionen von allen Schemen entfernt sein.
		 * Die Entfernung dieser Region zu benachbarten Regionen der Schemen, die NICHT 
		 * selbst Schemen sind, muss allerdings > 2 sein
		 * (da sonst auch diese Region in den Schemen enthalten sein müsste)
		 * Die endgültieg Region unter der AR-Region kann Ozean sein und muss daher
		 * nicht in den Schemen sichtbar sein.
		 * Daher wird zuerst ein Pool von möglichen Regionen gebildet, indem 
		 * alle Schemen, ihre Nachbarn und wiederum deren Nachbarn erfasst werden.
		 * Dann werden nicht in Frage kommende Region sukkzessive eliminiert
		 * 
		 * 
		 */
		// Das Ergebnis des Vorganges...
		CoordinateID erg = null;
		
		// regionen durchlaufen und AR Regionen bearbeiten
		for(Iterator iter = data.regions().values().iterator(); iter.hasNext()&&(erg==null);) {
			Region actAR_Region = (Region) iter.next();
			if (actAR_Region.getCoordinate().z==1){
				// Es ist eine AR Region
				erg = this.getOneRegion_processARRegion(data, actAR_Region);
			}
		}
		
		return erg;
	}
	
	/**
	 * Bearbeitet eine AR-Region
	 * @param data
	 * @param r
	 * @return
	 */
	private CoordinateID getOneRegion_processARRegion(GameData data,Region r){
		CoordinateID erg = null;
		
		// wir brechen ab, wenn gar keine Schemen vorhanden sind...16 Felder Ozean!
		if (r.schemes()==null){
			return null;
		}
		
		ArrayList possibleRR_Regions = new ArrayList(0);
		// possibleRR_Regions sind erstmal alle Schemen
		for(Iterator iter = r.schemes().iterator(); iter.hasNext();) {
			Scheme actScheme = (Scheme) iter.next();
			Region actSchemeRegion = data.getRegion(actScheme.getCoordinate());
			// fail safe...
			if (actSchemeRegion==null){
				continue;
			}
			if (!possibleRR_Regions.contains(actSchemeRegion)){
				possibleRR_Regions.add(actSchemeRegion);
			}
		}
		// sollte die Liste jetzt leer sein (unwahrscheinlich), brechen wir ab
		if (possibleRR_Regions.size()==0){
			// log.warn?
			return null;
		}
		// die schemen erfahren eine sonderbehandlung, diese extra listen
		ArrayList actSchemeRegions = new ArrayList(0);
		actSchemeRegions.addAll(possibleRR_Regions);
		// die possible Regions mit Nachbarn füllen, für den ungünstigsten
		// Fall sind 4 Läufe notwendig
		for (int i = 0;i<4;i++){
			possibleRR_Regions = this.getOneRegion_explodeRegionList(data, possibleRR_Regions);
		}
		
		// Ab jetzt versuchen, unmögliche Regionen zu entfernen...
		// erste bedingung: alle regionen, die sich auch nur von einer schemenRegionen
		// weiter entfernt befinden als 2 Regionen können raus.
		possibleRR_Regions = this.getOneRegion_deleteIfDist(data,actSchemeRegions, possibleRR_Regions, 2,true);
		
		// zweite bedingung: Randregionen von schemen (nicht ozean-Regionen...), die nicht selbst schemen sind, dürfen nicht weniger als 3
		// Regionen entfernt sein.
		// Dazu: Randregionen basteln
		ArrayList schemenRandRegionen = new ArrayList(0);
		schemenRandRegionen = this.getOneRegion_explodeRegionList(data, actSchemeRegions);
		// schemen selbst abziehen
		schemenRandRegionen.removeAll(actSchemeRegions);
		// Ozeanfelder löschen
		schemenRandRegionen = this.getOneRegion_deleteOceans(schemenRandRegionen);
		// alle löschen, die weniger als 3 Regionen an den randregionen dranne sind
		possibleRR_Regions = this.getOneRegion_deleteIfDist(data, schemenRandRegionen, possibleRR_Regions, 3,false);
		// jetzt sollte im Idealfall nur noch eine Region vorhanden sein ;-))
		if (possibleRR_Regions.size()==1){
			// Treffer, wir können Translation bestimmen
			// Verständnisfrage: ist gesichert, dass sich das einzige
			// Element einer ArrayList immer auf Index=0 befindet?
			for (Iterator iter = possibleRR_Regions.iterator();iter.hasNext();){
				Region RR_Region = (Region)iter.next();
				erg = new CoordinateID(RR_Region.getCoordinate().x - 4*r.getCoordinate().x,RR_Region.getCoordinate().y - 4*r.getCoordinate().y);
				break;
			}
		}
		return erg;
	}
	
	/**
	 * Löscht die Regionen aus regionList, welche nicht von allen Regionen in 
	 * schemen mindestens einen abstand von dist haben
	 * @param schemen
	 * @param regionen
	 * @param abstand
	 * @return
	 */
	private ArrayList getOneRegion_deleteIfDist(GameData data, ArrayList schemen, ArrayList regionList, int abstand,boolean innerhalb){
		ArrayList regionsToDel = new ArrayList(0);
		for(Iterator iter = regionList.iterator(); iter.hasNext();) {
			Region actRegion = (Region) iter.next();
			// nur die betrachten, die nicht schon in del sind
			if (!regionsToDel.contains(actRegion)){
				// Durch alle Schemen laufen und Abstand berechnen
				for(Iterator iter2= schemen.iterator(); iter2.hasNext();) {
					Region actSchemenRegion = (Region)iter2.next();
					// Abstand berechnen
					
					int dist = Regions.getRegionDist(actRegion.getCoordinate(), actSchemenRegion.getCoordinate());
					
					if ((dist > abstand && innerhalb) || (dist < abstand && !innerhalb)){
						// actRegion ist weiter als abstand von actSchemenregion entfernt
						// muss gelöscht werden
						regionsToDel.add(actRegion);
						break;
					}
				}
			}
		}
		// Löschung durchführen
		ArrayList erg = new ArrayList(0);
		erg.addAll(regionList);
		erg.removeAll(regionsToDel);
		return erg;
	}
	
	/**
	 * Löscht die Regionen aus regionList, welche als Ozean deklariert
	 * sind
	 * @param regionen
	 * @return
	 */
	private ArrayList getOneRegion_deleteOceans(ArrayList regionList){
		ArrayList regionsToDel = new ArrayList(0);
		for(Iterator iter = regionList.iterator(); iter.hasNext();) {
			Region actRegion = (Region) iter.next();
			if (actRegion.getRegionType().isOcean() && !regionsToDel.contains(actRegion)){
				regionsToDel.add(actRegion);
			}
			
		}
		// Löschung durchführen
		ArrayList erg = new ArrayList(0);
		erg.addAll(regionList);
		erg.removeAll(regionsToDel);
		return erg;
	}
	
	/**
	 * Erweitert die Liste der Regionen um die Nachbarn der aktuellen Regionen
	 * @param data
	 * @param regionList
	 * @return
	 */
	private ArrayList getOneRegion_explodeRegionList(GameData data, ArrayList regionList){
		// Liste verlängern nach durchlauf
		ArrayList regionsToAdd = new ArrayList();
		for(Iterator iter = regionList.iterator(); iter.hasNext();) {
			Region actRegion = (Region) iter.next();
			// liefert die IDs der Nachbarregionen
			Collection neighbors = actRegion.getNeighbours();
			for(Iterator iter2 = neighbors.iterator(); iter2.hasNext();) {
				CoordinateID newRegionID = (CoordinateID)iter2.next();
				Region newRegion = data.getRegion(newRegionID);
				// hinzufügen, wenn noch nicht vorhanden
				if (!regionList.contains(newRegion) && !regionsToAdd.contains(newRegion)){
					regionsToAdd.add(newRegion);
				}
			}
			
		}
		// alle hinzufügen
		ArrayList erg = new ArrayList(0);
		erg.addAll(regionList);
		erg.addAll(regionsToAdd);
		return erg;
	}
	
	
	private class ProgressDlg extends JDialog {
		/**
		 * Creates new form ProgressDlg
		 *
		 * @param parent TODO: DOCUMENT ME!
		 * @param modal TODO: DOCUMENT ME!
		 */
		public ProgressDlg(Frame parent, boolean modal) {
			super(parent, modal);
			initComponents();
			pack();
		}

		private void initComponents() {
			labelText = new JLabel();
			progressBar = new JProgressBar();
			getContentPane().setLayout(new GridBagLayout());

			GridBagConstraints gridBagConstraints1;
			setTitle(getString("window.title"));

			/*addWindowListener(new java.awt.event.WindowAdapter() {
			    public void windowClosing(java.awt.event.WindowEvent evt) {
			        closeDialog(evt);
			    }
			}
			);*/
			labelText.setPreferredSize(new Dimension(250, 16));
			labelText.setMinimumSize(new Dimension(250, 16));
			labelText.setText("jLabel1");
			labelText.setHorizontalAlignment(SwingConstants.CENTER);
			labelText.setMaximumSize(new Dimension(32767, 16));

			gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.gridy = 1;
			gridBagConstraints1.fill = GridBagConstraints.BOTH;
			gridBagConstraints1.insets = new Insets(0, 5, 5, 5);
			gridBagConstraints1.weightx = 1.0;
			gridBagConstraints1.weighty = 0.5;
			getContentPane().add(labelText, gridBagConstraints1);

			progressBar.setPreferredSize(new Dimension(250, 14));
			progressBar.setMinimumSize(new Dimension(250, 14));

			gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = GridBagConstraints.BOTH;
			gridBagConstraints1.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints1.weightx = 1.0;
			gridBagConstraints1.weighty = 0.5;
			getContentPane().add(progressBar, gridBagConstraints1);
		}

		/** Closes the dialog */

		/*private void closeDialog(java.awt.event.WindowEvent evt) {
		    setVisible (false);
		    dispose ();
		}*/
		public JLabel labelText;

		/** TODO: DOCUMENT ME! */
		public JProgressBar progressBar;
	}

	private String getString(String key) {
		return com.eressea.util.Translations.getTranslation(this, key);
	}

	// pavkovic 2003.01.28: this is a Map of the default Translations mapped to this class
	// it is called by reflection (we could force the implementation of an interface,
	// this way it is more flexible.)
	// Pls use this mechanism, so the translation files can be created automagically
	// by inspecting all classes.
	private static Map defaultTranslations;

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static synchronized Map getDefaultTranslations() {
		if(defaultTranslations == null) {
			defaultTranslations = CollectionFactory.createHashtable();
			defaultTranslations.put("status.merge", "Merge reports");
			defaultTranslations.put("status.merging", "merging");
			defaultTranslations.put("status.loading", "loading");
			defaultTranslations.put("status.processing", "processing");
			defaultTranslations.put("status.connecting", "connecting");

			defaultTranslations.put("window.title", "Merging reports...");

			defaultTranslations.put("msg.confirmmerge.title", "Merge without connection");
			defaultTranslations.put("msg.noconnection.text.1",
									"These reports seem to be non-overlapping and therefore not connectable:");
			defaultTranslations.put("msg.noconnection.text.2",
									"\nAre you sure you want to merge these reports?");
		}

		return defaultTranslations;
	}
	
	/**
	   * Tries to find matching regions and adds translations to the map accordingly.
	   * takes care of given layer
	   * 
	   * @param newReport
	   * @return
	   */
	  private Map getTranslationCandidatesRegionUID(Report newReport, int layer) {
	    Map translationMap = new Hashtable();
	   
	    // loop regions in main report
	    // for (Region region : globalData.regions().values()) {
	    for (Iterator iter = data.regions().values().iterator();iter.hasNext();){
	      Region region = (Region)iter.next();	
	      CoordinateID coord = region.getCoordinate();
	      if ((coord.z == layer) && (region.getUID() != 0)) {

	        Region result = (Region)newReport.regionUIDMap.get(new Long(region.getUID()));
	        if (result != null) {
	          CoordinateID foundCoord = result.getCoordinate();
	          CoordinateID translation = new CoordinateID(foundCoord.x - coord.x, foundCoord.y - coord.y);
	          Integer count = (Integer) translationMap.get(translation);

				if(count == null) {
					count = new Integer(1);
				} else {
					count = new Integer(count.intValue() + 1);
				}

				translationMap.put(translation, count);
	        }
	      }
	    }
	    return translationMap;
	  }
	
	
}
