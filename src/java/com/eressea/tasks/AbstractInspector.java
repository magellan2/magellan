
package com.eressea.tasks;

import java.util.Collections;
import java.util.List;

import com.eressea.Region;
import com.eressea.Unit;
import com.eressea.util.CollectionFactory;

public abstract class AbstractInspector implements Inspector {

	protected AbstractInspector() {
	}

	public List reviewUnit(Unit u) {
		List problems = CollectionFactory.createArrayList(10);

		problems.addAll(reviewUnit(u,Problem.INFORMATION));
		problems.addAll(reviewUnit(u,Problem.WARNING));
		problems.addAll(reviewUnit(u,Problem.ERROR));
		
		return problems.isEmpty() ? Collections.EMPTY_LIST : problems;
	}

	public List reviewUnit(Unit u, int type) {
		return Collections.EMPTY_LIST;
	}

	public List reviewRegion(Region r) {
		List problems = CollectionFactory.createArrayList(2);
		problems.addAll(reviewRegion(r,Problem.INFORMATION));
		problems.addAll(reviewRegion(r,Problem.WARNING));
		problems.addAll(reviewRegion(r,Problem.ERROR));
		return problems.isEmpty() ? Collections.EMPTY_LIST : problems;
	}

	public List reviewRegion(Region r,int type) {
		return Collections.EMPTY_LIST;
	}
}

