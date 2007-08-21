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

package com.eressea.demo.actions;

import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import com.eressea.CoordinateID;
import com.eressea.Region;
import com.eressea.demo.Client;
import com.eressea.event.GameDataEvent;
import com.eressea.event.GameDataListener;
import com.eressea.event.SelectionEvent;
import com.eressea.event.SelectionListener;
import com.eressea.util.CollectionFactory;

/**
 * Expanding a Selection about 1 region in each direction
 * (all neighbours, feature request)
 *
 * @author Fiete
 */
public class ExpandSelectionAction extends MenuAction implements GameDataListener,
																 SelectionListener
{
	private Map selectedRegions = CollectionFactory.createHashtable();

	/**
	 * Creates a new InvertSelectionAction object.
	 *
	 * @param client
	 */
	public ExpandSelectionAction(Client client) {
        super(client);
		client.getDispatcher().addSelectionListener(this);
		client.getDispatcher().addGameDataListener(this);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param e TODO: DOCUMENT ME!
	 */
	public void selectionChanged(SelectionEvent e) {
		if(e.getSource() == this) {
			return;
		}

		if((e.getSelectedObjects() != null) && (e.getSelectionType() == SelectionEvent.ST_REGIONS)) {
			selectedRegions.clear();

			for(Iterator iter = e.getSelectedObjects().iterator(); iter.hasNext();) {
				Object o = iter.next();

				if(o instanceof Region) {
					Region r = (Region) o;
					selectedRegions.put(r.getID(), r);
				}
			}
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param e TODO: DOCUMENT ME!
	 */
	public void gameDataChanged(GameDataEvent e) {
		selectedRegions.clear();
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param e TODO: DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent e) {
		Map newSelectedRegions = CollectionFactory.createHashtable();

		// append the neighboring regions to the already selected regions
		if (client.getData().getSelectedRegionCoordinates()==null){
			return;
		}
		// add all the current selected region in one move
		newSelectedRegions.putAll(client.getData().getSelectedRegionCoordinates());
		for(Iterator iter = client.getData().getSelectedRegionCoordinates().keySet().iterator(); iter.hasNext();) {
			CoordinateID c = (CoordinateID) iter.next();
			Region region = (Region) client.getData().regions().get(c);
			// get neighbors
			Collection neighbours = region.getNeighbours(); 
			if (neighbours!=null){
				for (Iterator it = neighbours.iterator();it.hasNext();){
					CoordinateID checkRegionID = (CoordinateID) it.next();
					if (!newSelectedRegions.containsKey(checkRegionID)){
						newSelectedRegions.put(checkRegionID, client.getData().regions().get(checkRegionID));
					}
				}
			}
		}

		selectedRegions = newSelectedRegions;
		client.getData().setSelectedRegionCoordinates(selectedRegions);
		client.getDispatcher().fire(new SelectionEvent(this, selectedRegions.values(), null,
													   SelectionEvent.ST_REGIONS));
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
			defaultTranslations.put("name", "Expand selection");
			defaultTranslations.put("mnemonic", "e");
			defaultTranslations.put("accelerator", "");
			defaultTranslations.put("tooltip", "");
		}

		return defaultTranslations;
	}
}
