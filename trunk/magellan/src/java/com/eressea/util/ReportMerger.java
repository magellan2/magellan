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

import java.io.File;

import java.util.Iterator;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.eressea.Coordinate;
import com.eressea.Faction;
import com.eressea.GameData;
import com.eressea.Region;
import com.eressea.StringID;

import com.eressea.rules.RegionType;

import com.eressea.util.logging.Logger;

/**
 * Helper class.
 */
public class ReportMerger extends java.lang.Object {
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

	private class UserInterface {
		// user interface
		ProgressDlg dlg = null;

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param parent TODO: DOCUMENT ME!
		 */
		public void init(JFrame parent) {
			dlg = new ProgressDlg(parent, true);
			dlg.labelText.setText(getString("status.merge"));
			dlg.progressBar.setMinimum(0);
			dlg.progressBar.setMaximum(reports.length * 4);
		}

		/**
		 * TODO: DOCUMENT ME!
		 */
		public void show() {
			dlg.show();
		}

		private class Confirm implements Runnable {
			String  strMessage;
			String  strTitle;
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
			conf.strTitle   = strTitle;

			try {
				SwingUtilities.invokeAndWait(conf);
			} catch(Exception e) {
				log.error(e);
				System.exit(0);
			}

			return conf.bResult;
		}

		private class Progress implements Runnable {
			String strMessage;
			int    iProgress;

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
			progress.iProgress  = iProgress;

			SwingUtilities.invokeLater(progress);
		}

		private class Ready implements Runnable {
			/**
			 * TODO: DOCUMENT ME!
			 */
			public void run() {
				dlg.setVisible(false);
				dlg.dispose();

				assignData.assign(data);
			}
		}

		/**
		 * TODO: DOCUMENT ME!
		 */
		public void ready() {
			Ready ready = new Ready();

			SwingUtilities.invokeLater(ready);
		}
	}

	UserInterface ui	    = new UserInterface();
	int			  iProgress;

	/**
	 * Creates new ReportMerger
	 *
	 * @param _data TODO: DOCUMENT ME!
	 * @param files TODO: DOCUMENT ME!
	 * @param _loader TODO: DOCUMENT ME!
	 * @param _assignData TODO: DOCUMENT ME!
	 */
	public ReportMerger(GameData _data, File files[], Loader _loader,
						AssignData _assignData) {
		data = _data;

		reports = new Report[files.length];

		for(int i = 0; i < files.length; i++) {
			reports[i]	    = new Report();
			reports[i].file = files[i];
		}

		loader     = _loader;
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
	public ReportMerger(GameData _data, File file, Loader _loader,
						AssignData _assignData) {
		data = _data;

		reports		    = new Report[1];
		reports[0]	    = new Report();
		reports[0].file = file;

		loader     = _loader;
		assignData = _assignData;
	}

	/**
	 * Starts merging. Parent is used as parent for the userinterface.
	 *
	 * @param parent TODO: DOCUMENT ME!
	 */
	public void merge(JFrame parent) {
		// init progress dialog
		ui.init(parent);

		// start worker thread
		new Thread(new Worker(this)).start();

		ui.show();
	}

	private class Worker implements Runnable {
		ReportMerger merger = null;

		Worker(ReportMerger _merger) {
			merger = _merger;
		}

		/**
		 * TODO: DOCUMENT ME!
		 */
		public void run() {
			merger.mergeThread();
		}
	}

	private void mergeThread() {
		try {
			int iPosition		    = 0;
			int iFailedConnectivity = 0;
			int iMerged			    = 0;

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
					iPosition		    = 0;
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

				if(ui.confirm(strMessage, getString("msg.confirmmerge.title"))) {
					for(int i = 0; i < reports.length; i++) {
						if(!reports[i].merged) {
							iProgress += 2;
							ui.setProgress(reports[i].file.getName() + " - " +
										   getString("status.merging"),
										   iProgress);

							//data.mergeWith( reports[i].data );
							data = GameData.merge(data, reports[i].data);
						}
					}
				}
			}
		} catch(Exception e) {
			log.error(e);
		}

		ui.ready();
	}

	private boolean mergeReport(Report report) {
		if(report.data == null) {
			iProgress += 1;
			ui.setProgress(report.file.getName() + " - " +
						   getString("status.loading"), iProgress);

			report.data = loader.load(report.file);
		}

		if(!data.name.equalsIgnoreCase(report.data.name)) {
			// game types doesn't match. Make sure, it will not be tried again.
			// TODO: maybe issue a message here.
			report.merged = true;

			return true;
		}

		/**
		 * prepare faction trustlevel for merging: - to be added CR is older or
		 * of same age -> hold existing trust levels - to be added CR is newer
		 * and contains trust level that were set by the user explicitly (or
		 * read from CR what means the same) -> take the trust levels out of
		 * the new CR otherwise -> hold existing trust levels This means: set
		 * those trust levels, that will not be retained to default values
		 */
		if((data.getDate() != null) && (report.data.getDate() != null) &&
			   (data.getDate().getDate() < report.data.getDate().getDate()) &&
			   TrustLevels.containsTrustLevelsSetByUser(report.data)) {
			// take the trust levels out of the to be added data
			// set those in the existing data to default-values
			for(Iterator iterator = data.factions().values().iterator();
					iterator.hasNext();) {
				Faction f = (Faction) iterator.next();
				f.trustLevel		  = Faction.TL_DEFAULT;
				f.trustLevelSetByUser = false;
			}
		} else {
			// take the trust levels out of the existing data
			// set those in the to be added data to default-values
			for(Iterator iterator = report.data.factions().values().iterator();
					iterator.hasNext();) {
				Faction f = (Faction) iterator.next();
				f.trustLevel		  = Faction.TL_DEFAULT;
				f.trustLevelSetByUser = false;
			}
		}

		/**
		 * Prepare curTempID-Value for merging. If reports are of the same age,
		 * keep existing by setting the new one to default value. Otherwise
		 * set the existing to default value.
		 */
		if((data.getDate() != null) && (report.data.getDate() != null) &&
			   (data.getDate().getDate() < report.data.getDate().getDate())) {
			data.setCurTempID(-1);
		} else {
			report.data.setCurTempID(-1);
		}

		if(report.regionMap == null) {
			iProgress += 1;
			ui.setProgress(report.file.getName() + " - " +
						   getString("status.processing"), iProgress);

			report.regionMap = CollectionFactory.createHashMap();

			for(Iterator iter = report.data.regions().values().iterator();
					iter.hasNext();) {
				Region region = (Region) iter.next();

				if((region.getName() != null) &&
					   (region.getName().length() > 0)) {
					/*if (report.regionMap.containsKey(region.getName())) {
					    report.regionMap.put(region.getName(), null);
					}else{*/
					report.regionMap.put(region.getName(), region);

					//}
				}
			}
		}

		// determine translation
		iProgress += 1;
		ui.setProgress(report.file.getName() + " - " +
					   getString("status.connecting"), iProgress);

		// maps translation (Coordinate) to match count (Integer)
		Map translationMap = new java.util.Hashtable();

		for(Iterator iter = data.regions().values().iterator(); iter.hasNext();) {
			Region region = (Region) iter.next();

			if((region.getName() != null) && (region.getName().length() > 0)) {
				Coordinate coord = region.getCoordinate();

				if(coord.z == 0) {
					Region foundRegion = (Region) report.regionMap.get(region.getName());

					if(foundRegion != null) {
						Coordinate foundCoord = foundRegion.getCoordinate();

						Coordinate translation = new Coordinate(foundCoord.x -
																coord.x,
																foundCoord.y -
																coord.y);

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
		}

		/* check whether any of the translations is impossible by
		   comparing the terrains */
		int		   maxTerrainMismatches = (int) (Math.max(data.regions().size(),
														  report.data.regions()
																	 .size()) * 0.02);
		Coordinate loopCoord		    = new Coordinate(0, 0, 0);
		RegionType forestTerrain	    = data.rules.getRegionType(StringID.create("Wald"));
		RegionType plainTerrain		    = data.rules.getRegionType(StringID.create("Ebene"));
		RegionType oceanTerrain		    = data.rules.getRegionType(StringID.create("Ozean"));
		RegionType glacierTerrain	    = data.rules.getRegionType(StringID.create("Gletscher"));
		RegionType activeVolcanoTerrain = data.rules.getRegionType(StringID.create("Aktiver Vulkan"));
		RegionType volcanoTerrain	    = data.rules.getRegionType(StringID.create("Vulkan"));

		for(Iterator iter = translationMap.keySet().iterator(); iter.hasNext();) {
			Coordinate translation = (Coordinate) iter.next();
			int		   mismatches = 0; // the number of regions not having the same region type at the current translations

			/* for each traslations we have to compare the regions'
			   terrains */
			for(Iterator regionIter = data.regions().values().iterator();
					regionIter.hasNext();) {
				Region r = (Region) regionIter.next();

				if(r.getType() == null) {
					continue;
				}

				Coordinate c = r.getCoordinate();

				/* do the translation and find the corresponding
				   region in the report data */
				if(c.z == 0) {
					loopCoord.x = c.x;
					loopCoord.y = c.y;
					loopCoord.translate(translation);

					Region reportDataRegion = (Region) report.data.regions()
																  .get(loopCoord);

					/* the hit count for the current translation must
					   only be modified, if there actually are regions
					   to be compared and their terrains are valid */
					if((reportDataRegion != null) &&
						   (reportDataRegion.getType() != null)) {
						if(!r.getType().equals(reportDataRegion.getType())) {
							/* now we have a mismatch. If the reports
							   are from the same turn, terrains may
							   not differ at all. If the reports are
							   from different turns, some terrains
							   can be transformed. */
							if((data.getDate() != null) &&
								   (report.data.getDate() != null) &&
								   data.getDate().equals(report.data.getDate())) {
								mismatches++;
							} else {
								if(!(((forestTerrain != null) &&
									   (plainTerrain != null) &&
									   ((forestTerrain.equals(r.getType()) &&
									   plainTerrain.equals(reportDataRegion.getType())) ||
									   (plainTerrain.equals(r.getType()) &&
									   forestTerrain.equals(reportDataRegion.getType())))) ||
									   ((oceanTerrain != null) &&
									   (glacierTerrain != null) &&
									   ((oceanTerrain.equals(r.getType()) &&
									   glacierTerrain.equals(reportDataRegion.getType())) ||
									   (glacierTerrain.equals(r.getType()) &&
									   oceanTerrain.equals(reportDataRegion.getType())))) ||
									   ((activeVolcanoTerrain != null) &&
									   (volcanoTerrain != null) &&
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

		int     iDX    = 0;
		int     iDY    = 0;
		int     iCount = 0;
		boolean bEqual = false;

		// search highest hit count
		Iterator iter = translationMap.entrySet().iterator();

		while(iter.hasNext()) {
			Map.Entry  entry = (Map.Entry) iter.next();

			Coordinate translation = (Coordinate) entry.getKey();
			int		   count	   = ((Integer) entry.getValue()).intValue();

			/*System.out.println( "Translation X:" + translation.x + " Y:" + translation.y +
			    " Hits:" + count );*/
			if(count > iCount) {
				iDX    = translation.x;
				iDY    = translation.y;
				iCount = count;
				bEqual = false;
			} else {
				if(count == iCount) {
					bEqual = true;
				}
			}
		}

		// valid translation?
		if((iCount > 0) && (!bEqual)) {
			iProgress += 1;
			ui.setProgress(report.file.getName() + " - " +
						   getString("status.merging"), iProgress);

			if((data.getDate() == null) || (report.data.getDate() == null)) {
				report.data.placeOrigin(new Coordinate(iDX, iDY));
			} else {
				if(data.getDate().getDate() < report.data.getDate().getDate()) {
					data.placeOrigin(new Coordinate(-iDX, -iDY));
				} else {
					report.data.placeOrigin(new Coordinate(iDX, iDY));
				}
			}

			//data.mergeWith( report.data );
			data		  = GameData.merge(data, report.data);
			report.merged = true;

			report.data		 = null;
			report.regionMap = null;
		} else {
			iProgress -= 1;
			ui.setProgress(report.file.getName(), iProgress);
		}

		return report.merged;
	}

	private class ProgressDlg extends javax.swing.JDialog {
		/**
		 * Creates new form ProgressDlg
		 *
		 * @param parent TODO: DOCUMENT ME!
		 * @param modal TODO: DOCUMENT ME!
		 */
		public ProgressDlg(java.awt.Frame parent, boolean modal) {
			super(parent, modal);
			initComponents();
			pack();
		}

		private void initComponents() {
			labelText   = new javax.swing.JLabel();
			progressBar = new javax.swing.JProgressBar();
			getContentPane().setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints gridBagConstraints1;
			setTitle(getString("window.title"));

			/*addWindowListener(new java.awt.event.WindowAdapter() {
			    public void windowClosing(java.awt.event.WindowEvent evt) {
			        closeDialog(evt);
			    }
			}
			);*/
			labelText.setPreferredSize(new java.awt.Dimension(250, 16));
			labelText.setMinimumSize(new java.awt.Dimension(250, 16));
			labelText.setText("jLabel1");
			labelText.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			labelText.setMaximumSize(new java.awt.Dimension(32767, 16));

			gridBagConstraints1		    = new java.awt.GridBagConstraints();
			gridBagConstraints1.gridx   = 0;
			gridBagConstraints1.gridy   = 1;
			gridBagConstraints1.fill    = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints1.insets  = new java.awt.Insets(0, 5, 5, 5);
			gridBagConstraints1.weightx = 1.0;
			gridBagConstraints1.weighty = 0.5;
			getContentPane().add(labelText, gridBagConstraints1);

			progressBar.setPreferredSize(new java.awt.Dimension(250, 14));
			progressBar.setMinimumSize(new java.awt.Dimension(250, 14));

			gridBagConstraints1		    = new java.awt.GridBagConstraints();
			gridBagConstraints1.fill    = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints1.insets  = new java.awt.Insets(5, 5, 5, 5);
			gridBagConstraints1.weightx = 1.0;
			gridBagConstraints1.weighty = 0.5;
			getContentPane().add(progressBar, gridBagConstraints1);
		}

		/** Closes the dialog */

		/*private void closeDialog(java.awt.event.WindowEvent evt) {
		    setVisible (false);
		    dispose ();
		}*/
		public javax.swing.JLabel labelText;

		/** TODO: DOCUMENT ME! */
		public javax.swing.JProgressBar progressBar;
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

			defaultTranslations.put("msg.confirmmerge.title",
									"Merge without connection");
			defaultTranslations.put("msg.noconnection.text.1",
									"These reports seem to be non-overlapping and therefore not connectable:");
			defaultTranslations.put("msg.noconnection.text.2",
									"\nAre you sure you want to merge these reports?");
		}

		return defaultTranslations;
	}
}
