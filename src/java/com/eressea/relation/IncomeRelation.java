package com.eressea.relation;

import com.eressea.Unit;
import com.eressea.Region;

/** 
 * A relation indicating earning money from a unit container (region)
 * based on O_WORK, (O_STEAL) (O_BUY) O_TAX, O_ENTERTAIN (O_SELL)
 */
public class IncomeRelation extends UnitContainerRelation implements LongOrderRelation {
	public final int amount;
	public IncomeRelation(Unit s, Region r, int amount, int line) {
		super(s, r, line);
		this.amount = amount;
	}
}
