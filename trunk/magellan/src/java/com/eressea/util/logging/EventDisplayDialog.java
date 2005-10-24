package com.eressea.util.logging;

import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;

import com.eressea.event.EventDispatcher;
import com.eressea.swing.InternationalizedDialog;

/**
 * @author pavkovic
 *
 */
public class EventDisplayDialog extends InternationalizedDialog {
	EventDisplayPanel panel;
	
	/**
	 * @param owner
	 * @param modal
	 */
	public EventDisplayDialog(Frame owner, boolean modal, EventDispatcher d) {
		super(owner, modal);
		init(d);
	}

	/**
	 * @param owner
	 * @param modal
	 */
	public EventDisplayDialog(Dialog owner, boolean modal, EventDispatcher d) {
		super(owner, modal);
		init(d);
	}
	
	private void init(EventDispatcher d) {
		setContentPane(getMainPane(d));
		setTitle("Event Display");
	}

	private Container getMainPane(EventDispatcher d) {
		if(panel == null) {
			panel = new EventDisplayPanel(d);
		}

		return panel;
	}
	
}
