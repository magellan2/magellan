package com.eressea.swing.desktop;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

import com.eressea.swing.ui.UIFactory;

public class Desktop extends JPanel {
	
	private JPanel contentPanel;
	private JPanel content;
 	// private Perspective[] perspectives;

	/* 
	 * A Desktop consists of four parts:
	 * - perspective chooser panel
	 * (- toolbar panel)
	 * - perspective panel
	 * - status panel
	 */

	public Desktop() {
		initUI();
	}

	private void initUI() {
		contentPanel = createContentPanel();
		content = new EmptyPerspective().build();
		contentPanel.add(content);

 		this.setLayout(new BorderLayout());
		this.add(contentPanel);


// 		JPanel status = createDefaultStatus();
// 		JPanel chooser = createChooser();


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
		JPanel ret = new JPanel(new BorderLayout());
		int size = new JSplitPane().getDividerSize();
		ret.setBorder(BorderFactory.createEmptyBorder(size,size,size,size));
		return ret;
	}
	
	private JPanel createDefaultStatus() {
		JPanel ret = new JPanel(new BorderLayout());
		ret.setBorder(BorderFactory.createMatteBorder(1,0,0,0, Color.black));
		ret.add(new JLabel("Status"), BorderLayout.WEST);
		return ret;
	}

	private JPanel createChooser() {
		JPanel ret = new JPanel(new BorderLayout());
		ret.setBorder(BorderFactory.createMatteBorder(0,0,0,1, Color.black));
		ret.add(new JLabel("Per"), BorderLayout.NORTH);
		return ret;
	}

	private JSplitPane createSplitPane(int orientation) {
		JSplitPane ret = new JSplitPane(orientation);
		//ret.setBorder(null);
		return ret;
	}


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

}


