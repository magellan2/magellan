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

package com.eressea.swing.desktop;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.*;

import com.eressea.swing.ui.*;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class EmptyPerspective implements Perspective {
	/*
	 * A Perspective holds informations about the desktop view
	 */
	public JPanel build() {
		int border = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT).getDividerSize();
		JPanel ret = new JPanel();
		ret.setBorder(BorderFactory.createEmptyBorder(border, border, border, border));

		ret.setLayout(new BorderLayout());
		ret.add(new JLabel("This is an empty perspective"), BorderLayout.NORTH);

		InternalFrame ifs = new InternalFrame("Empty perspective");
		ifs.setContent(ret);

		return ifs;
	}
}
