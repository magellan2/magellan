package com.eressea.relation;

import com.eressea.Named;
import com.eressea.Unit;

/**
 * A relation indicating that the source unit renames the Named Object
 */
public class RenameNamedRelation extends UnitRelation {
    public Named named;
    public String name;
    
    public RenameNamedRelation(Unit s, Named named, String name, int line) {
        super(s, line);
        this.named = named;
        this.name = name;
    }

}
