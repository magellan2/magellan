package com.eressea.relation;

import com.eressea.Region;
import com.eressea.Unit;

/** 
 * A relation indicating earning money from a unit container (region)
 * based on WORK, (STEAL) (BUY) TAX, ENTERTAIN (SELL)
 */
public class IncomeRelation extends UnitContainerRelation implements LongOrderRelation {
	public final int amount;
	public IncomeRelation(Unit s, Region r, int amount, int line) {
		super(s, r, line);
		this.amount = amount;
	}
}
