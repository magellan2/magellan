package com.eressea.swing.ui;

import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.JSplitPane;


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

	public static JSplitPane createBorderlessJSplitPane(int orientation, Component first, Component second) {
		JSplitPane ret = createBorderlessJSplitPane(orientation);
		ret.setTopComponent(first);
		ret.setBottomComponent(second);
		return ret;

		
	}

}
