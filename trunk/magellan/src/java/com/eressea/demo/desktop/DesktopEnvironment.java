// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===


package com.eressea.demo.desktop;

import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;

import javax.swing.KeyStroke;

import com.eressea.util.CollectionFactory;
/**
 *
 * @author  Andreas
 * @version 
 */
public class DesktopEnvironment extends Object {

	public static MagellanDesktop desktop;
	
	public final static int SPLIT=MagellanDesktop.MODE_SPLIT;
	public final static int FRAME=MagellanDesktop.MODE_FRAME;
	public final static int LAYOUT=MagellanDesktop.MODE_LAYOUT;
	
	// init state
	private static boolean initialized=false;
	private static List pendingSCListeners;
	private static List pendingSingleSCListeners;
	private static List pendingAEListeners;
	
	public static void init(MagellanDesktop md) {
		desktop=md;
		initialized=true;
		// somebody registered before init
		if (pendingSCListeners!=null) {
			Iterator it=pendingSCListeners.iterator();
			while(it.hasNext()) try{
				registerShortcutListener((ShortcutListener)it.next());
			}catch(Exception exc) {}
			pendingSCListeners=null;
		}
		if (pendingSingleSCListeners!=null) {
			Iterator it=pendingSingleSCListeners.iterator();
			while(it.hasNext()) try{
				KeyStroke ks=(KeyStroke)it.next();
				ShortcutListener sl=(ShortcutListener)it.next();
				registerShortcutListener(ks,sl);
			}catch(Exception exc) {}
			pendingSCListeners=null;
		}
		
		if (pendingAEListeners!=null) {
			Iterator it=pendingAEListeners.iterator();
			while(it.hasNext()) try{
				KeyStroke ks=(KeyStroke)it.next();
				ActionListener al=(ActionListener)it.next();
				registerActionListener(ks,al);
			}catch(Exception exc) {}
			pendingAEListeners=null;
		}
		
	}
	
	public static void registerShortcutListener(ShortcutListener sl) {
		if (initialized)
			desktop.registerShortcut(sl);
		else {
			if (pendingSCListeners==null)
				pendingSCListeners=CollectionFactory.createLinkedList();
			pendingSCListeners.add(sl);
		}
	}
	
	public static void registerShortcutListener(KeyStroke stroke,ShortcutListener sl) {
		if (initialized)
			desktop.registerShortcut(stroke,sl);
		else {
			if (pendingSingleSCListeners==null)
				pendingSingleSCListeners=CollectionFactory.createLinkedList();
			pendingSingleSCListeners.add(stroke);
			pendingSingleSCListeners.add(sl);
		}
	}
	
	public static void registerActionListener(KeyStroke stroke,ActionListener al) {
		if (initialized)
			desktop.registerShortcut(stroke,al);
		else {
			if (pendingAEListeners==null)
				pendingAEListeners=CollectionFactory.createLinkedList();
			pendingAEListeners.add(stroke);
			pendingAEListeners.add(al);
		}
	}
	
	public static MagellanDesktop getDesktop() {
		return desktop;
	}
	
	public static void requestFocus(String component) {
		if (initialized)
			getDesktop().componentRequestFocus(component);
	}
	
	public static void repaintComponent(String component) {
		if (initialized)
			getDesktop().repaint(component);
	}
	
	public static void repaintAll() {
		if (initialized) {
			getDesktop().repaintAllComponents();
		}
	}
	
	public static void updateLaF() {
		if (initialized) {
			getDesktop().updateLaF();
		}
	}
	
	public static int getMode() {
		if (initialized)
			return getDesktop().getMode();
		return -1;
	}
}
