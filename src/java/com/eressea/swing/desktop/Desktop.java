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
import java.util.*;

import javax.swing.*;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class Desktop extends JPanel {
	private JPanel contentPanel;
	private JPanel content;
	private JToolBar chooserBar;

	// private Perspective[] perspectives;

	/*
	 * A Desktop consists of four parts:
	 * - perspective chooser panel
	 * (- toolbar panel)
	 * - perspective panel
	 * - status panel
	 */
	public Desktop() {
		this(null);
	}

	public Desktop(ButtonGroup buttonGroup) {
		initUI(buttonGroup);
	}

	private void initUI(ButtonGroup buttonGroup) {
		contentPanel = createContentPanel();
		content = new EmptyPerspective().build();
		contentPanel.add(content);

		this.setLayout(new BorderLayout());
		this.add(contentPanel,BorderLayout.CENTER);

		JPanel chooser = createChooser(buttonGroup);
		if(chooser != null) {
			this.add(chooser,BorderLayout.WEST);
		}

		// 		JPanel status = createDefaultStatus();

		//  		JSplitPane perspectiveOverStatus = UIFactory.createBorderlessJSplitPane(JSplitPane.VERTICAL_SPLIT);
		//  		perspectiveOverStatus.setTopComponent(perspective);
		//  		perspectiveOverStatus.setBottomComponent(status);
		//  		JPanel perspectiveOverStatus = new JPanel(new BorderLayout());
		//  		perspectiveOverStatus.add(contentPanel, BorderLayout.CENTER);
		//  		perspectiveOverStatus.add(status, BorderLayout.SOUTH);
		//   		JSplitPane chooserLeftPerspective = UIFactory.createBorderlessJSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		//   		chooserLeftPerspective.setLeftComponent(chooser);
		//    		chooserLeftPerspective.setRightComponent(perspectiveOverStatus);
		//    		JPanel chooserLeftPerspective = new JPanel(new BorderLayout());
		//   		chooserLeftPerspective.add(chooser, BorderLayout.WEST);
		//   		chooserLeftPerspective.add(perspectiveOverStatus, BorderLayout.CENTER);
		// 		this.setLayout(new BorderLayout());
		// 		this.add(chooserLeftPerspective, BorderLayout.CENTER);
	}

	private JPanel createContentPanel() {
		return new JSplitPaneBorderedJPanel(new BorderLayout());
	}

	private JPanel createDefaultStatus() {
		JPanel ret = new JPanel(new BorderLayout());
		ret.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.black));
		ret.add(new JLabel("Status"), BorderLayout.WEST);

		return ret;
	}

	private JPanel createChooser(ButtonGroup buttonGroup) {
		if(buttonGroup ==null) return null;

		JPanel ret = new JPanel(new BorderLayout());
		Color sepColor = UIManager.getColor("Separator.foreground");
		ret.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, sepColor));
		chooserBar = new JToolBar(SwingConstants.VERTICAL);
		// may not float into a different position
		chooserBar.setFloatable(false);
		ButtonGroup group = new ButtonGroup();
		Action lastAction = null;
		Dimension DIM = new Dimension(24,24);

 		for(Enumeration enum = buttonGroup.getElements(); enum.hasMoreElements(); ) {
			AbstractButton origButton = (AbstractButton) enum.nextElement();

 			Action action = origButton.getAction();

			// here we bind a new JButton to a given ButtonModel
			// to effectively using the underlying MVC-Pattern
			JToggleButton button = new JToggleButton(action);
			button.setModel(origButton.getModel());

 			button.setPreferredSize(DIM);
 			button.setSize(DIM);
 			button.setMinimumSize(DIM);
 			button.setMaximumSize(DIM);

 			String text = (String) action.getValue(Action.NAME);
 			if(text.indexOf(":") !=-1) {
 				text = text.substring(0,text.indexOf(":"));
 			}
 			button.setText(text);
 			if(lastAction != null && !action.getClass().isInstance(lastAction)) {
 				chooserBar.addSeparator();
 			}
 			lastAction =action;
			
			chooserBar.add(button);
		}


		ret.add(chooserBar, BorderLayout.CENTER);

		return ret;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param newContent TODO: DOCUMENT ME!
	 */
	public void setContent(Component newContent) {
		if(content != null) {
			contentPanel.remove(content);
		}

		contentPanel.add(newContent);
	}

	// 	private JPanel createPanel() {
	// 		JPanel panel = new JPanel();
	// 		panel.setBorder(EMPTY_BORDER);
	// 		return panel;
	// 	}
	// 	private final static Border EMPTY_BORDER =new EmptyBorder(5,5,5,5);
	public static void main(String args[]) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		JFrame frame = new JFrame();
		frame.setContentPane(new Desktop());
		frame.setSize(600, 400);
		frame.setTitle("Magellan - Desktop");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	/**
	 * This class represents a self bordering JPanel that keeps the size of the border in sync with
	 * a JSplitPane divider size
	 */
	private static class JSplitPaneBorderedJPanel extends JPanel {
		/**
		 * Creates a new JSplitPaneBorderedJPanel object.
		 *
		 * @param layout TODO: DOCUMENT ME!
		 */
		public JSplitPaneBorderedJPanel(LayoutManager layout) {
			super(layout);
			setBorderToJSplitpaneDivider();
		}

		private void setBorderToJSplitpaneDivider() {
			int size = new JSplitPane().getDividerSize();
			setBorder(BorderFactory.createEmptyBorder(size, size, size, size));
		}

		/**
		 * TODO: DOCUMENT ME!
		 */
		public void updateUI() {
			super.updateUI();
			setBorderToJSplitpaneDivider();
		}
	}
}
