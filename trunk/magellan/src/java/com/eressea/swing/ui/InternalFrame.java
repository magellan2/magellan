package com.eressea.swing.ui;

import java.awt.*;
import javax.swing.*;

import com.jgoodies.uif_lite.panel.SimpleInternalFrame;

public class InternalFrame extends JPanel {
	private SimpleInternalFrame sif;
	
	public InternalFrame() {
		this("");
	}

	public InternalFrame(String title) {
		this(title, null);
	}

	public InternalFrame(String title, Component content) {
 		super(new BorderLayout());
 		sif = new SimpleInternalFrame(title);
		add(sif, BorderLayout.CENTER);
		if(content != null) {
			setContent(content);
		}
	}

	public void setContent(Component content) {
		if(content != null) {
			sif.setContent(content);
		}
	}
	
	public Component getContent() {
		return sif.getContent();
	}

	public void setTitle(String title) {
		sif.setTitle(title);
	}
}



