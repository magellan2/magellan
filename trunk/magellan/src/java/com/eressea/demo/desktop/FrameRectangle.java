// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===


package com.eressea.demo.desktop;

import java.awt.Component;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.ComponentListener;
import java.awt.event.WindowListener;

/**
 *
 * @author  Andreas
 * @version 
 */
public class FrameRectangle extends java.awt.Rectangle implements ComponentListener, WindowListener{
	protected Frame myFrame;
	protected String frameTitle,frameComponent;
	protected int state=Frame.NORMAL;
	protected boolean visible=true;
	
	/** Holds value of property configuration. */
	private String configuration;
	
	/** Creates new FrameRectangle */
    public FrameRectangle(String ft,String fc) {
		frameTitle=ft;
		frameComponent=fc;
    }
	
	public void connectToFrame(Frame f) {
		if (myFrame!=null) {
			myFrame.removeComponentListener(this);
			myFrame.removeWindowListener(this);
		}
		myFrame=f;
		if (myFrame!=null) {
			myFrame.addComponentListener(this);
			myFrame.addWindowListener(this);
		}
	}
	
	public void componentShown(java.awt.event.ComponentEvent p1) {
	}
	
	public void componentResized(java.awt.event.ComponentEvent p1) {
		Rectangle rect=((Component)p1.getSource()).getBounds();
		x=rect.x;y=rect.y;width=rect.width;height=rect.height;
	}
	
	public void componentHidden(java.awt.event.ComponentEvent p1) {
	}
	
	public void componentMoved(java.awt.event.ComponentEvent p1) {
		Rectangle rect=((Component)p1.getSource()).getBounds();
		x=rect.x;y=rect.y;width=rect.width;height=rect.height;
	}
	
	public void setFrameTitle(String s) {
		frameTitle=s;
	}
	public void setFrameComponent(String s) {
		frameComponent=s;
	}
	public String getFrameTitle() {
		return frameTitle;
	}
	public String getFrameComponent() {
		return frameComponent;
	}
	
	public Frame getConnectedFrame() {
		return myFrame;
	}
	
	public void setState(int s) {
		state=s;
	}
	public int getState() {
		return state;
	}
	public void setVisible(boolean b) {
		visible=b;
	}
	public boolean isVisible() {
		return visible;
	}
	
	public void windowDeactivated(java.awt.event.WindowEvent p1) {
	}
	
	public void windowClosed(java.awt.event.WindowEvent p1) {
	}
	
	public void windowDeiconified(java.awt.event.WindowEvent p1) {
		state=Frame.NORMAL;
	}
	
	public void windowOpened(java.awt.event.WindowEvent p1) {
		visible=true;
	}
	
	public void windowIconified(java.awt.event.WindowEvent p1) {
		state=Frame.ICONIFIED;
	}
	
	public void windowClosing(java.awt.event.WindowEvent p1) {
		visible=false;
	}
	
	public void windowActivated(java.awt.event.WindowEvent p1) {
	}
	
	/** Getter for property configuration.
	 * @return Value of property configuration.
	 */
	public String getConfiguration() {
		return configuration;
	}
	
	/** Setter for property configuration.
	 * @param configuration New value of property configuration.
	 */
	public void setConfiguration(String configuration) {
		this.configuration = configuration;
	}
	
}
