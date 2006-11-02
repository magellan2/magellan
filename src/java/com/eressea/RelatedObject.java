package com.eressea;

import java.util.Collection;
import java.util.List;
import java.util.Iterator;

import com.eressea.relation.RenameNamedRelation;
import com.eressea.relation.UnitRelation;
import com.eressea.util.CollectionFactory;

/** 
 * A RelatedObject knows concepts of relations
 */
public abstract class RelatedObject extends DescribedObject implements Related {
    /**
     * Constructs a new described object that is uniquely identifiable by the specified id.
     *
     * @param id TODO: DOCUMENT ME!
     */
    public RelatedObject(ID id) {
        super(id);
    }

    /**
     * @see com.eressea.Related#addRelation(com.eressea.relation.UnitRelation)
     */
    public void addRelation(UnitRelation rel) {
        getRelations().add(rel);
    }

    /**
     * @see com.eressea.Related#removeRelation(com.eressea.relation.UnitRelation)
     */
    public UnitRelation removeRelation(UnitRelation rel) {
        if(getRelations().remove(rel)) {
            return rel;
        } else {
            return null;
        }
    }
    
    protected abstract Collection getRelations();

    /**
     * Returns a Collection over the relations this unit has to other units. The collection consist
     * of  <tt>UnitRelation</tt> objects.  The UnitRelation objects are filtered by the given
     * relation class.
     *
     * @param relationClass TODO: DOCUMENT ME!
     *
     * @return TODO: DOCUMENT ME!
     */
    public List getRelations(Class relationClass) {
    	List ret = CollectionFactory.createLinkedList();
    
    	for(Iterator iter = getRelations().iterator(); iter.hasNext();) {
    		Object relation = iter.next();
    
    		if(relationClass.isInstance(relation)) {
    			ret.add(relation);
    		}
    	}
    
    	return ret;
    }

    
    /**
     * @see com.eressea.Named#getModifiedName()
     */
    public String getModifiedName() {
        List renameRelations = getRelations(RenameNamedRelation.class);
        if(renameRelations.isEmpty()) {
            return null;
        } else {
            // return first rename relation
            return ((RenameNamedRelation) renameRelations.get(0)).name;
        }
    }
}
