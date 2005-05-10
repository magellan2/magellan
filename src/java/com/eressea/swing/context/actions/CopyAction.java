package com.eressea.swing.context.actions;

import java.awt.event.ActionEvent;
import java.util.List;

import com.eressea.ID;
import com.eressea.event.EventDispatcher;

public class CopyAction extends ContextAction {

    public CopyAction(ID selected, EventDispatcher dispatcher) {
        super(selected, dispatcher);
    }

    public CopyAction(ID selected, List selectedObjects,
            EventDispatcher dispatcher) {
        super(selected, selectedObjects, dispatcher);
    }

    public void actionPerformed(ActionEvent e) {
    }

}
