package com.eressea.gamebinding.eressea;

import java.util.Collection;
import java.util.Iterator;

import com.eressea.Item;
import com.eressea.Skill;
import com.eressea.StringID;
import com.eressea.Unit;
import com.eressea.gamebinding.MovementEvaluator;
import com.eressea.rules.ItemType;
import com.eressea.rules.Race;
import com.eressea.rules.SkillType;

public class EresseaMovementEvaluator implements MovementEvaluator {

	private EresseaMovementEvaluator() {
	}

	private final static EresseaMovementEvaluator singleton= new EresseaMovementEvaluator();
	public static EresseaMovementEvaluator getSingleton() {
		return singleton;
	}

	/**
	 * Returns the maximum payload in GE * 100 of this unit when it
	 * travels by horse.
	 * Horses, carts and persons are taken into account for this
	 * calculation.
	 * If the unit has a sufficient skill in horse riding but there
	 * are too many carts for the horses, the weight of the additional
	 * carts are also already considered.
	 *
	 * @returns the payload in GE * 100, CAP_NO_HORSES if the unit
	 * does not possess horses or CAP_UNSKILLED if the unit is not
	 * sufficiently skilled in horse riding to travel on horseback.
	 */
	public int getPayloadOnHorse(Unit unit) {
		int capacity = 0;
		int horses = 0;
		Item i = unit.getModifiedItem(new ItemType(EresseaConstants.I_HORSE));
		if (i != null) {
			horses = i.getAmount();
		}
		if (horses <= 0) {
			return CAP_NO_HORSES;
		}

		int skillLevel = 0;
		Skill s = unit.getModifiedSkill(new SkillType(StringID.create("Reiten")));
		if (s != null) {
			skillLevel = s.getLevel();
		}
		if (horses > skillLevel * unit.getModifiedPersons() * 2) {
			return CAP_UNSKILLED;
		}

		int carts = 0;
		i = unit.getModifiedItem(new ItemType(EresseaConstants.I_CART));
		if (i != null) {
			carts = i.getAmount();
		}

		int horsesWithoutCarts = horses - carts * 2;
		if (horsesWithoutCarts >= 0) {
			capacity = (carts * 140 + horsesWithoutCarts * 20) * 100 - ((int)((unit.realRace != null ? unit.realRace.getWeight() : unit.race.getWeight()) * 100)) * unit.getModifiedPersons();
		} else {
			int cartsWithoutHorses = carts - horses / 2;
			horsesWithoutCarts = horses % 2;
			capacity = ((carts - cartsWithoutHorses) * 140 + horsesWithoutCarts * 20 - cartsWithoutHorses * 40) * 100 - ((int)((unit.realRace != null ? unit.realRace.getWeight() : unit.race.getWeight()) * 100)) * unit.getModifiedPersons();
		}
		return capacity;
	}

	/**
	 * Returns the maximum payload in GE * 100 of this unit when it
	 * travels on foot.
	 * Horses, carts and persons are taken into account for this
	 * calculation.
	 * If the unit has a sufficient skill in horse riding but there
	 * are too many carts for the horses, the weight of the additional
	 * carts are also already considered.
	 * The calculation also takes into account that trolls can tow
	 * carts.
	 *
	 * @returns the payload in GE * 100, CAP_UNSKILLED if the unit is
	 * not sufficiently skilled in horse riding to travel on horseback.
	 */
	public int getPayloadOnFoot(Unit unit) {
		int capacity = 0;
		int horses = 0;
		Item i = unit.getModifiedItem(new ItemType(EresseaConstants.I_HORSE));

		if (i != null) {
			horses = i.getAmount();
		}
		if (horses < 0) {
			horses = 0;
		}

		int skillLevel = 0;
		Skill s = unit.getModifiedSkill(new SkillType(StringID.create("Reiten")));
		if (s != null) {
			skillLevel = s.getLevel();
		}
		if (horses > (skillLevel * unit.getModifiedPersons() * 4) + unit.getModifiedPersons()) {
			// too many horses
			return CAP_UNSKILLED;
		}

		int carts = 0;
		i = unit.getModifiedItem(new ItemType(EresseaConstants.I_CART));
		if (i != null) {
			carts = i.getAmount();
		}
		if (carts < 0) {
			carts = 0;
		}

		int horsesWithoutCarts = 0;
		int cartsWithoutHorses = 0;
		if (skillLevel == 0) {
			// can't use carts!!!
			horsesWithoutCarts = horses;
			cartsWithoutHorses = carts;
		} else if (carts > horses / 2) {
			// too many carts
			cartsWithoutHorses = carts - (horses / 2);
		} else {
			// too many horses (or exactly right number)
			horsesWithoutCarts = horses - (carts * 2);
		}
		Race race = unit.race;
		if (unit.realRace != null) {
			race = unit.realRace;
		}
		if (race == null || race.getID().equals(EresseaConstants.R_TROLLE) == false) {
			capacity = ((carts - cartsWithoutHorses) * 140 + horsesWithoutCarts * 20 - cartsWithoutHorses * 40) * 100 + ((int)(race.getCapacity() * 100)) * unit.getModifiedPersons();
		} else {
			int horsesMasteredPerPerson = (skillLevel * 4) + 1;
			int trollsMasteringHorses = horses / horsesMasteredPerPerson;
			if (horses % horsesMasteredPerPerson != 0) {
				trollsMasteringHorses++;
			}
			int cartsTowedByTrolls = Math.min((unit.getModifiedPersons() - trollsMasteringHorses) / 4, cartsWithoutHorses);
			int trollsTowingCarts = cartsTowedByTrolls * 4;
			int untowedCarts = cartsWithoutHorses - cartsTowedByTrolls;
			capacity = ((carts - untowedCarts) * 140 + horsesWithoutCarts * 20 - untowedCarts * 40) * 100 + ((int)(race.getCapacity() * 100)) * (unit.getModifiedPersons() - trollsTowingCarts);
		}

		return capacity;


	}


	public int getLoad(Unit unit) {
		return getLoad(unit, unit.getItems());
	}

	public int getModifiedLoad(Unit unit) {
		return getLoad(unit, unit.getModifiedItems());
	}

	private int getLoad(Unit unit, Collection items) {
		int load = 0;
		ItemType horse = unit.getRegion().getData().rules.getItemType(EresseaConstants.I_HORSE);
		ItemType cart  = unit.getRegion().getData().rules.getItemType(EresseaConstants.I_CART);
		for (Iterator iter = items.iterator(); iter.hasNext(); ) {
			Item i = (Item)iter.next();
			if (!i.getItemType().equals(horse) && !i.getItemType().equals(cart)) {
				// pavkovic 2003.09.10: only take care about (possibly) modified items with positive amount
				if(i.getAmount() > 0) {
					load += ((int)(i.getItemType().getWeight() * 100)) * i.getAmount();
				}
			}
		}
		return load;
	}

}
