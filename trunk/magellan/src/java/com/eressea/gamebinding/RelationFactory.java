package com.eressea.gamebinding;

import java.util.List;

import com.eressea.Unit;

public interface RelationFactory {
	public List createRelations(Unit u, int from);
}
