/*
 * Created on 17.11.2004
 *
 */
package com.eressea.util.logging;

import java.awt.BorderLayout;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.eressea.event.EventDispatcher;
import com.eressea.event.UnitOrdersEvent;
import com.eressea.event.UnitOrdersListener;
import com.eressea.swing.InternationalizedPanel;

/**
 * @author pavkovic
 *
 */
public class EventDisplayPanel extends InternationalizedPanel implements UnitOrdersListener {
	JTextArea area;
	EventDispatcher dispatcher;
	
	public EventDisplayPanel(EventDispatcher d) {
		super();
		init(d);
	}

	private void init(EventDispatcher d) {	
		dispatcher = d;
		d.addUnitOrdersListener(this);
		initGUI();
	}
	
	private void initGUI() {
		this.setLayout(new BorderLayout());
		
		area = new JTextArea();
		area.setText("");
		this.add(new JScrollPane(area),BorderLayout.CENTER);
	}
	
	public void unitOrdersChanged(UnitOrdersEvent e) {
		area.setText(area.getText()+e.toString()+'\n');
	}
}
