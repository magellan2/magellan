// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===


package com.eressea.demo;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

/**
 * TODO: undo/redo seems broken
 * @author  Andreas
 */
public class MagellanUndoManager extends UndoManager {

    public final static String UNDO="Undo_Changed";
    public final static String REDO="Redo_Changed";
   
	// this is basicall needed to attach RedoAction and UndoAction to this UndoManager
    private PropertyChangeSupport list;
    
    /** Creates new MagellanUndoManager */
    public MagellanUndoManager() {
        list=new PropertyChangeSupport(this);
    }
    
    public void addPropertyChangeListener(PropertyChangeListener l) {
        list.addPropertyChangeListener(l);
    }
    
    public void addPropertyChangeListener(String property,PropertyChangeListener l) {
        list.addPropertyChangeListener(property,l);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener l) {
        list.removePropertyChangeListener(l);
    }
    
    public void removePropertyChangeListener(String property,PropertyChangeListener l) {
        list.removePropertyChangeListener(property,l);
    }
    
    /*
     This methods must be changed to throw the events.
     */
    public synchronized void undo() {
			  // TODO: implement undo history?
        // String oldUndo=getUndoPresentationName(),oldRedo=getRedoPresentationName();
        super.undo();
        list.firePropertyChange(REDO,false,canRedo());
        list.firePropertyChange(UNDO,false,canUndo());
    }
    
    public synchronized void redo() {
		// TODO: implement redo history?
        // String oldUndo=getUndoPresentationName(),oldRedo=getRedoPresentationName();
        super.redo();
        list.firePropertyChange(REDO,false,canRedo());
        list.firePropertyChange(UNDO,false,canUndo());
    }

    
    public synchronized boolean addEdit(UndoableEdit e) {
		// TODO: implement undo/redo history?
        // String oldUndo=getUndoPresentationName(),oldRedo=getRedoPresentationName();
        boolean b=super.addEdit(e);
        if (b) {
            list.firePropertyChange(UNDO,false,canUndo());
            list.firePropertyChange(REDO,false,canRedo());
        }
        return b;
    }
}
