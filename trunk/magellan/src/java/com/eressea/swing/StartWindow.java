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

package com.eressea.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.ResourceBundle;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTextPane;
import javax.swing.JWindow;
import javax.swing.border.LineBorder;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.eressea.demo.Client;

/**
 * DOCUMENT ME!
 *
 * @author Andreas
 * @version
 */
public class StartWindow extends JWindow {
	protected Collection images;
	protected int steps;
	protected int currentStep = 0;
	protected JLabel imageLabel;
	protected JProgressBar progress;
	protected JTextPane text;
	protected static final JFrame parent = new JFrame();

	/**
	 * Creates new StartWindow
	 *
	 * @param icon TODO: DOCUMENT ME!
	 * @param steps TODO: DOCUMENT ME!
	 */
	public StartWindow(Icon icon, int steps) {
		super(parent);
		init(icon, steps);
	}

	/**
	 * Creates a new StartWindow object.
	 *
	 * @param icons TODO: DOCUMENT ME!
	 * @param steps TODO: DOCUMENT ME!
	 */
	public StartWindow(Collection icons, int steps) {
		super(parent);
		init(icons, steps);
	}

	protected void init(Icon icon, int steps) {
		Collection icons = new ArrayList(1);

		if(icon != null) {
			icons.add(icon);
		}

		init(icons, steps);
	}

	protected void init(Collection icons, int steps) {
		this.images = icons;
		this.steps = steps;

		Image iconImage = Client.getApplicationIcon();

		// set the application icon
		if(iconImage != null) {
			parent.setIconImage(iconImage);
		}

		Container cont = getContentPane();
		cont.setLayout(new SimpleLayout());

		// use the colors from the default file
		Color foreground = new Color(79, 63, 48);
		Color background = new Color(213, 169, 131);

		cont.setBackground(background);
		((JComponent) cont).setBorder(new LineBorder(background, 2));

		int prefwidth = 0;

		if((images != null) && !images.isEmpty()) {
			Icon icon = (Icon) images.iterator().next();
			prefwidth = icon.getIconWidth();
			imageLabel = new JLabel(icon);
			imageLabel.setBackground(background);
			cont.add(imageLabel);
		} else {
			prefwidth = 400;
		}

		if(steps > 0) {
			progress = new JProgressBar(JProgressBar.HORIZONTAL, 0, steps);
			progress.setStringPainted(true);
			progress.setBorderPainted(false);
			progress.setForeground(foreground);
			progress.setBackground(background);
			cont.add(progress);
		}

		String names = null;
		String descr = null;

		try {
			ResourceBundle rb = ResourceBundle.getBundle("res/lang/com-eressea-swing-startwindow");
			descr = "\n" + rb.getString("infotext");
			names = rb.getString("names") + "\n";
		} catch(Exception exc) {
		}

		if((descr == null) || (descr.length() == 0)) {
			descr = "\nHier wurde garantiert jemand vergessen. Aber bitte nicht b\u00f6se sein, sondern kurz melden." +
					"\n--------------------------------\nMagellan is an Open Source Project under GNU General Public License. You may use and distribute it freely. Persons above have contributed to this project.";
		}

		if((names == null) || (names.length() == 0)) {
			names = "Roger Butenuth, Enno Rehling, Stefan G\u00f6tz, Klaas Prause, Sebastian Tusk, Andreas Gampe, Roland Behme, Michael Schmidt, Henning Zahn, Oliver Hertel, Guenter Grossberger, Sören Bendig, Marc Geerligs, Matthias Müller, Ulrich Küster, Jake Hofer, Ilja Pavkovic\n";
		}

		StyledDocument styled = new DefaultStyledDocument();

		MutableAttributeSet set = new SimpleAttributeSet();
		StyleConstants.setBold(set, true);

		MutableAttributeSet set2 = new SimpleAttributeSet();
		StyleConstants.setFontSize(set2, 10);

		try {
			styled.insertString(0, names, set);
			styled.insertString(styled.getLength(), descr, set2);
		} catch(Exception exc) {
		}

		text = new JTextPane(styled);
		text.setEditable(false);

		text.setForeground(foreground);
		text.setBackground(background);

		cont.add(text, BorderLayout.SOUTH);

		// make all same length
		Dimension prefDim;

		if(progress != null) {
			prefDim = progress.getPreferredSize();

			if(prefDim.width != prefwidth) {
				prefDim.width = prefwidth;
				progress.setPreferredSize(prefDim);
			}
		}

		prefDim = text.getPreferredSize();

		if(prefDim.width != prefwidth) {
			prefDim.width = prefwidth;
			text.setPreferredSize(prefDim);
			text.setSize(prefDim);

			// try to change height
			try {
				Rectangle rect = text.modelToView(styled.getLength());
				prefDim.height = rect.y + rect.height;
				text.setPreferredSize(prefDim);
			} catch(Exception exc) {
			}
		}

		pack();

		Toolkit t = getToolkit();
		Dimension screen = t.getScreenSize();
		Dimension size = getSize();
		setLocation((screen.width - size.width) / 2, (screen.height - size.height) / 2);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param step TODO: DOCUMENT ME!
	 * @param message TODO: DOCUMENT ME!
	 */
	public void progress(int step, String message) {
		if((progress != null) && (step <= steps)) {
			progress.setValue(step);
			progress.setString(message);

			if((images != null) && (step < images.size())) {
				Icon icon = null;
				Iterator it = images.iterator();

				for(int i = 0; i <= step; i++) {
					icon = (Icon) it.next();
				}

				imageLabel.setIcon(icon);
			}
		}
	}

	protected class SimpleLayout implements LayoutManager {
		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param container TODO: DOCUMENT ME!
		 */
		public void layoutContainer(java.awt.Container container) {
			int width = 0;
			int height = 0;
			Component c[] = container.getComponents();

			if((c != null) && (c.length > 0)) {
				int i;
				Dimension d;

				for(i = 0; i < c.length; i++) {
					d = c[i].getPreferredSize();

					if(d.width > width) {
						width = d.width;
					}

					height += d.height;
				}

				Insets insets = container.getInsets();
				int x = 0;
				int y = 0;

				if(insets != null) {
					x += insets.left;
					y += insets.top;
				}

				for(i = 0; i < c.length; i++) {
					int h = c[i].getPreferredSize().height;
					c[i].setBounds(x, y, width, h);
					y += h;
				}
			}
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param container TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public java.awt.Dimension preferredLayoutSize(java.awt.Container container) {
			int width = 0;
			int height = 0;
			Component c[] = container.getComponents();

			if((c != null) && (c.length > 0)) {
				int i;
				Dimension d;

				for(i = 0; i < c.length; i++) {
					d = c[i].getPreferredSize();

					if(d.width > width) {
						width = d.width;
					}

					height += d.height;
				}
			}

			Insets insets = container.getInsets();

			if(insets != null) {
				width += (insets.left + insets.right);
				height += (insets.top + insets.bottom);
			}

			return new Dimension(width, height);
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param str TODO: DOCUMENT ME!
		 * @param component TODO: DOCUMENT ME!
		 */
		public void addLayoutComponent(java.lang.String str, java.awt.Component component) {
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param container TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public java.awt.Dimension minimumLayoutSize(java.awt.Container container) {
			return preferredLayoutSize(container);
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param component TODO: DOCUMENT ME!
		 */
		public void removeLayoutComponent(java.awt.Component component) {
		}
	}
}
