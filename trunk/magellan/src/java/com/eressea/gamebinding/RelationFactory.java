package com.eressea.gamebinding;

import java.util.List;

import com.eressea.Unit;

public interface RelationFactory {
	/**
	 * Creates a list of com.eressea.util.Relation objects 
	 * for a unit starting at order position <tt>from</tt>
	 */
	public List createRelations(Unit u, int from);
}
