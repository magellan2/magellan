package com.eressea;

import java.util.List;

import com.eressea.relation.UnitRelation;

/**
 * An interface granting access to the relations of an object.
 */
public interface Related {

    /** 
     * add a new relation to this object
     *
     * @param rel
     */
    public void addRelation(UnitRelation rel);
    
    /**
     * removes the given relation
     * 
     * @param rel
     * @return old relation
     */
    public UnitRelation removeRelation(UnitRelation rel);

    /**
     * delivers all relations of the given class
     * 
     * @param relationClass
     * @return list of relations that are instance of relationClass
     */
    public List getRelations(Class relationClass);
}
