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

package com.eressea.swing.ui;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JSplitPane;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class UIFactory {
	/*
	 * This class is a helper class to consistently create different gui elements like JScrollPane, JSplitPane etc.
	 */
	public static JSplitPane createBorderlessJSplitPane(int orientation) {
		JSplitPane ret = new UISplitPane(orientation);

		// JSplitPane ret = new JSplitPane(orientation);
		ret.setBorder(BorderFactory.createEmptyBorder());

		return ret;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param orientation TODO: DOCUMENT ME!
	 * @param first TODO: DOCUMENT ME!
	 * @param second TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static JSplitPane createBorderlessJSplitPane(int orientation, Component first,
														Component second) {
		JSplitPane ret = createBorderlessJSplitPane(orientation);
		ret.setTopComponent(first);
		ret.setBottomComponent(second);

		return ret;
	}
}
