package com.eressea.swing.context.actions;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;

import com.eressea.Unit;
import com.eressea.event.EventDispatcher;
import com.eressea.util.CollectionFactory;
import com.eressea.util.Translations;

/**
 * 
 * This class encapsulates basic functions for contextmenu actions.
 * @author Ilja Pavkovic - illsen@gumblfarz.de
 *
 */
public abstract class ContextAction extends AbstractAction {

    protected EventDispatcher dispatcher;
    protected Object selected;
    protected List selectedObjects;
    
    
    public ContextAction(Object selected, EventDispatcher dispatcher) {
        if(selected == null) throw new NullPointerException();
        this.selected = selected;
        this.dispatcher = dispatcher;
        
        setName(getNameTranslated());
    }
    
    public ContextAction(Object selected, List selectedObjects, EventDispatcher dispatcher) {
        this(selected,dispatcher);
        this.selectedObjects = selectedObjects;
    }   

    /**
     * @return the selected units from the selected objects.
     */
    public static List getSelectedUnits(Collection selectedObjects) {
        if(selectedObjects == null) return CollectionFactory.EMPTY_LIST;
        List selectedUnits = CollectionFactory.createArrayList(selectedObjects.size());
        for(Iterator iter = selectedObjects.iterator(); iter.hasNext(); ) {
            Object o = iter.next();
            try{
                selectedUnits.add((Unit) o);
            } catch(ClassCastException ignore) {                
            }
        }
        return selectedUnits;
    }

    public abstract void actionPerformed(java.awt.event.ActionEvent e);
    
    protected String getString(String key) {
        return Translations.getTranslation(this, key);
    }
    
    public String toString() {
        return getName();
    };
    
    /**
     * Sets the name of this menu action.
     *
     * @param name TODO: DOCUMENT ME!
     */
    protected void setName(String name) {
        this.putValue(Action.NAME, name);
    }

    /**
     * Returns the name of this menu action.
     *
     * @return TODO: DOCUMENT ME!
     */
    protected String getName() {
        return (String) this.getValue(Action.NAME);
    }

    /**
     * These methods are now needed to keep translation in the corresponding class. they MAY
     * deliver null!
     *
     * @return TODO: DOCUMENT ME!
     */
    protected final String getNameTranslated() {
        return Translations.getTranslation(this, "name");
    }

}
