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
public class WorkSpace extends JPanel {
	private JPanel contentPanel;
	private JPanel chooser;

	// private Perspective[] perspectives;

	/*
	 * A WorkSpace  consists of four parts:
	 * - perspective chooser panel
	 * (- toolbar panel)
	 * - perspective panel
	 * - status panel
	 */
	public WorkSpace(ButtonGroup buttonGroup) {
		initUI(buttonGroup);
	}


	public void setEnabledChooser(boolean bool) {
		if(bool) {
			this.add(chooser, BorderLayout.WEST);
		} else {
			this.remove(chooser);
		}
		validate();
	}

	public boolean isEnabledChooser() {
		Component[] components = this.getComponents();
		for(int i=0; i<components.length; i++) {
			if(components[i].equals(chooser)) {
				return true;
			}
		}
		return false;
	}

	private void initUI(ButtonGroup buttonGroup) {
		contentPanel = createContentPanel();
		setContent(new EmptyPerspective().getJPanel());

		this.setLayout(new BorderLayout());
		this.add(contentPanel,BorderLayout.CENTER);

		chooser = createChooser(buttonGroup);
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
		JToolBar chooserBar = new JToolBar(SwingConstants.VERTICAL);
		// may not float into a different position
		chooserBar.setFloatable(false);
		ButtonGroup group = new ButtonGroup();
		Action lastAction = null;
		Dimension DIM = new Dimension(24,24);

 		for(Enumeration enumeration = buttonGroup.getElements(); enumeration.hasMoreElements(); ) {
			AbstractButton origButton = (AbstractButton) enumeration.nextElement();

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
		removeContent();
		contentPanel.add(newContent);
	}

	public void removeContent() {
		contentPanel.removeAll();
	}

	public static void main(String args[]) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		JFrame frame = new JFrame();
		frame.setContentPane(new WorkSpace(null));
		frame.setSize(600, 400);
		frame.setTitle("Magellan - Desktop");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	/**
	 * This class represents a self bordering JPanel that keeps the size of the border in sync with
	 * a JSplitPane divider size.
	 */
	private static class JSplitPaneBorderedJPanel extends JPanel {
		/**
		 * Creates a new JSplitPaneBorderedJPanel object.
		 *
		 * @param layout a Layout for this panel.
		 */
		public JSplitPaneBorderedJPanel(LayoutManager layout) {
			super(layout);
			setBorderToJSplitpaneDivider();
		}

		/**
		 * called from UIManager.setLookAndFeel
		 */
		public void updateUI() {
			super.updateUI();
			setBorderToJSplitpaneDivider();
		}

		private void setBorderToJSplitpaneDivider() {
			int size = new JSplitPane().getDividerSize();
			setBorder(BorderFactory.createEmptyBorder(size, size, size, size));
		}
	}
}
