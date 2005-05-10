package com.eressea.swing.context;

import java.util.Collection;

import javax.swing.JMenu;
import javax.swing.tree.DefaultMutableTreeNode;

import com.eressea.GameData;
import com.eressea.event.EventDispatcher;

/**
 * 
 * 
 * @author pavkovic
 * 
 *
 */
public interface ContextMenuProvider {
    public JMenu createContextMenu(
            EventDispatcher dispatcher, 
            GameData data, 
            Object argument,
            Collection selectedObjects);
}
