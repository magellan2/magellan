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
 * $Id$
 */

package com.eressea.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

/**
 * Simple layout that arranges the first component of the container centered
 * and spans the whole area if the corresponding mode is set.
 *
 * @author Andreas
 * @version
 */
public class CenterLayout implements LayoutManager {
	/** TODO: DOCUMENT ME! */
	public static final Dimension NULL = new Dimension(0, 0);

	/** TODO: DOCUMENT ME! */
	public static final int SPAN_X = 1;

	/** TODO: DOCUMENT ME! */
	public static final int SPAN_Y = 2;
	protected int		    mode = 0;

	/** TODO: DOCUMENT ME! */
	public static final CenterLayout SPAN_X_LAYOUT = new CenterLayout(SPAN_X);

	/** TODO: DOCUMENT ME! */
	public static final CenterLayout SPAN_Y_LAYOUT = new CenterLayout(SPAN_Y);

	/** TODO: DOCUMENT ME! */
	public static final CenterLayout SPAN_BOTH_LAYOUT = new CenterLayout(SPAN_X |
																		 SPAN_Y);

	/**
	 * Creates a new CenterLayout object.
	 *
	 * @param mode TODO: DOCUMENT ME!
	 */
	public CenterLayout(int mode) {
		this.mode = mode;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param container TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Dimension minimumLayoutSize(Container container) {
		if(container.getComponentCount() > 0) {
			return container.getComponent(0).getMinimumSize();
		}

		return NULL;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param str TODO: DOCUMENT ME!
	 * @param component TODO: DOCUMENT ME!
	 */
	public void addLayoutComponent(String str, Component component) {
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param container TODO: DOCUMENT ME!
	 */
	public void layoutContainer(Container container) {
		Dimension size;

		if(container.getComponentCount() > 0) {
			size = container.getSize();

			Component first = container.getComponent(0);
			Dimension pSize = first.getPreferredSize();
			int		  x     = (size.width - pSize.width) / 2;

			if((x < 0) || ((mode & SPAN_X) != 0)) {
				x = 0;
			}

			int y = (size.height - pSize.height) / 2;

			if((y < 0) || ((mode & SPAN_Y) != 0)) {
				y = 0;
			}

			int width;

			if((mode & SPAN_X) != 0) {
				width = size.width;
			} else {
				width = Math.min(pSize.width, size.width);
			}

			int height;

			if((mode & SPAN_Y) != 0) {
				height = size.height;
			} else {
				height = Math.min(pSize.height, size.height);
			}

			first.setBounds(x, y, width, height);

			if(container.getComponentCount() > 1) {
				for(int i = 1; i < container.getComponentCount(); i++) {
					container.getComponent(i).setBounds(-1, -1, 0, 0);
				}
			}
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param component TODO: DOCUMENT ME!
	 */
	public void removeLayoutComponent(Component component) {
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param container TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public java.awt.Dimension preferredLayoutSize(Container container) {
		if(container.getComponentCount() > 0) {
			return container.getComponent(0).getPreferredSize();
		}

		return NULL;
	}
}
