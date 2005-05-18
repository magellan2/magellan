package com.eressea.swing.context;

import java.util.Collection;

import javax.swing.JMenuItem;

import com.eressea.GameData;
import com.eressea.event.EventDispatcher;

public interface ContextMenuProvider {
    
    /*
     * creates a JMenuItem. Will be called on right-clicking units.
     */
    public JMenuItem createContextMenu(
            EventDispatcher dispatcher, 
            GameData data, 
            Object argument,
            Collection selectedObjects);
}
