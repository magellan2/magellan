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

import java.awt.*;

import javax.swing.*;

import com.jgoodies.uif_lite.panel.SimpleInternalFrame;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class InternalFrame extends JPanel {
	private SimpleInternalFrame sif;
	private Component content;

	/**
	 * Creates a new InternalFrame object.
	 */
	public InternalFrame() {
		this("");
	}

	/**
	 * Creates a new InternalFrame object.
	 *
	 * @param title TODO: DOCUMENT ME!
	 */
	public InternalFrame(String title) {
		this(title, null);
	}

	/**
	 * Creates a new InternalFrame object.
	 *
	 * @param title TODO: DOCUMENT ME!
	 * @param content TODO: DOCUMENT ME!
	 */
	public InternalFrame(String title, Component content) {
		super(new BorderLayout());
		//sif = new SimpleInternalFrame(title);
		//add(sif);

		if(content != null) {
			setContent(content);
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param content TODO: DOCUMENT ME!
	 */
	public void setContent(Component content) {
		if(content != null) {
			if(sif == null ) {
				if(this.content != null) {
					remove(this.content);
				}
				this.content = content;
				add(content);
				setBorder(BorderFactory.createLineBorder(Color.gray));
				//setBorder(BorderFactory.createEtchedBorder());
			} else {
				this.setBorder(BorderFactory.createEmptyBorder());
				sif.setContent(content);
			}
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Component getContent() {
		return sif.getContent();
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param title TODO: DOCUMENT ME!
	 */
	public void setTitle(String title) {
		sif.setTitle(title);
	}
}
