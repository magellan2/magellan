package com.eressea.relation;

import java.io.*;
import java.util.*;

import com.eressea.*;
import com.eressea.rules.*;
import com.eressea.util.*;

import com.eressea.util.logging.Logger;

public class RelationFactory {
	private final static Logger log = Logger.getInstance(RelationFactory.class);

	public RelationFactory() {
	}

	private final static int REFRESHRELATIONS_ALL = -2;

	public Collection createRelations(Unit u, int from) {
		Collection rels = CollectionFactory.createArrayList(5);
		
		GameData data = u.getRegion().getData();
		Map modItems = null;	// needed to track changes in the items for GIB orders
		int modPersons = u.getPersons();
		
		// clone u unit's items
		modItems = CollectionFactory.createHashtable();
		
		if (u.items != null) {
			for (Iterator iter = u.items.values().iterator(); iter.hasNext(); ) {
				Item i = (Item)iter.next();
				modItems.put(i.getItemType().getID(), new Item(i.getItemType(), i.getAmount()));
			}
		}
		
		// 4. parse the orders and create new relations
		OrderParser parser = new OrderParser((Eressea)data.rules);
		boolean tempOrders = false;
		int line = 0;
		for (Iterator iter = u.getOrders().iterator(); iter.hasNext(); ) {
			String order = (String)iter.next();

			line++; // keep track of line
			if(line<from) {
				continue;
			}

			if (!parser.read(new StringReader(order))) {
				continue;
			}
			
			List tokens = parser.getTokens();
			if (((OrderToken)tokens.get(0)).ttype == OrderToken.TT_COMMENT) {
				continue;
			}
			if (((OrderToken)tokens.get(0)).ttype == OrderToken.TT_PERSIST) {
				tokens.remove(0);
			}
			if (tempOrders) {
				// end of temp unit
				if (((OrderToken)tokens.get(0)).equalsToken(getOrder(EresseaOrderConstants.O_END))) {
					tempOrders = false;
					continue;
				}
			}
			// begin of temp unit
			if (((OrderToken)tokens.get(0)).equalsToken(getOrder(EresseaOrderConstants.O_MAKE)) &&
				((OrderToken)tokens.get(1)).getText().toUpperCase().startsWith(getOrder(EresseaOrderConstants.O_TEMP))) {
				tempOrders = true;
				continue;
			}
			
			// movement relation 
			if (((OrderToken)tokens.get(0)).equalsToken(getOrder(EresseaOrderConstants.O_MOVE)) ||
				((OrderToken)tokens.get(0)).equalsToken(getOrder(EresseaOrderConstants.O_ROUTE))) {

				List modifiedMovement = CollectionFactory.createArrayList(2);
				// dissect the order into pieces to detect which way the unit
				// is taking
				Coordinate c = u.getRegion().getCoordinate();
				modifiedMovement.add(c);
				
				for(Iterator iter2 = tokens.listIterator(1); iter2.hasNext(); ) {
					OrderToken token = (OrderToken) iter2.next();
					int dir = Direction.toInt(token.getText());
					if (dir != -1) {
						c = new Coordinate(c); // make c a new copy
						c.translate(Direction.toCoordinate(dir));
						modifiedMovement.add(c);
					} else {
						break;
					}
				}
				rels.add(new MovementRelation(u, modifiedMovement, line));
				continue;
			}
			// income relation WORK
			if (((OrderToken)tokens.get(0)).equalsToken(getOrder(EresseaOrderConstants.O_WORK))) {
				// TODO!
				continue;
			}
			// income relation ENTERTAIN
			if (((OrderToken)tokens.get(0)).equalsToken(getOrder(EresseaOrderConstants.O_ENTERTAIN))) {
				// TODO!
				continue;
			}
			// income relation TAX
			if (((OrderToken)tokens.get(0)).equalsToken(getOrder(EresseaOrderConstants.O_TAX))) {
				// TODO!
				continue;
			}
			// transport relation
			if (((OrderToken)tokens.get(0)).equalsToken(getOrder(EresseaOrderConstants.O_CARRY))) {
				OrderToken t = (OrderToken)tokens.get(1);
				Unit target = getTargetUnit(t, u.getRegion());
				if (target == null || u.equals(target)) {
					continue;
				}
				TransportRelation rel = new TransportRelation(u, target, line);
				rels.add(rel);
				continue;
			}
			
			// transfer relation
			if (((OrderToken)tokens.get(0)).equalsToken(getOrder(EresseaOrderConstants.O_GIVE)) ||
				((OrderToken)tokens.get(0)).equalsToken(getOrder(EresseaOrderConstants.O_SUPPLY))) {
				boolean parseTarget = false;	// indicates whether to parse the object to be transferred
				OrderToken t = (OrderToken)tokens.get(1);
				Unit target = getTargetUnit(t, u.getRegion());
				if (target != null) {
					if (!target.equals(u)) {
						TransferRelation rel = new TransferRelation(u, target, -1, line);
						// -1 means that the amount could not determined
						t = (OrderToken)tokens.get(2);
						if (t.ttype == OrderToken.TT_NUMBER) {
							// u is a plain number
							rel.amount = Integer.parseInt(t.getText());
							parseTarget = true;
						} else if (t.ttype == OrderToken.TT_KEYWORD && t.equalsToken(getOrder(EresseaOrderConstants.O_ALL))) {
							// -2 encodes that everything is to be transferred
							rel.amount = REFRESHRELATIONS_ALL;
							parseTarget = true;
						} else if (t.equalsToken(getOrder(EresseaOrderConstants.O_HERBS))) {
							// if the 'amount' is HERBS then create relations for all herbs the unit carries
							ItemCategory herbCategory = data.rules.getItemCategory(StringID.create(("HERBS")));
							if (herbCategory != null && u.items != null) {
								for (Iterator items = modItems.values().iterator(); items.hasNext(); ) {
									Item i = (Item)items.next();
									if (herbCategory.equals(i.getItemType().getCategory())) {
										TransferRelation r = new ItemTransferRelation(u, target, i.getAmount(), i.getItemType(), line);
										i.setAmount(0);
										rels.add(r);
									}
								}
							}
							parseTarget = false;
						}
						
						if (parseTarget) {
							if (rel.amount != -1) {	// -1 means that the amount could not determined
								t = (OrderToken)tokens.get(3);
								if (t.ttype != OrderToken.TT_EOC) {
									// now the order must look something like:
									// GIVE <unit id> <amount> <object><EOC>
									String itemName = stripQuotes(t.getText());
									if (t.equalsToken(getOrder(EresseaOrderConstants.O_MEN))) {
										// if the specified amount was 'all':
										if (rel.amount == REFRESHRELATIONS_ALL) {
											rel.amount = modPersons;
										} else {
											// if not, only transfer the minimum amount the unit has
											rel.amount = Math.min(modPersons, rel.amount);
										}
										rel = new PersonTransferRelation(u, target, rel.amount, u.realRace != null ? u.realRace : u.race, line);
										// update the modified person amount
										modPersons = Math.max(0, modPersons - rel.amount);
									} else if (itemName.length() > 0) {
										ItemType iType = ((Eressea)data.rules).getItemType(itemName);
										if (iType != null) {
											// get the item from the list of modified items
											Item i = (Item)modItems.get(iType.getID());
											if(i==null) {
												// item unknown
												rel.amount = 0;
											} else {
												// if the specified amount is 'all', convert u to a decent number
												if (rel.amount == REFRESHRELATIONS_ALL) {
													rel.amount = i.getAmount();
												} else {
													// if not, only transfer the minimum amount the unit has
													rel.amount = Math.min(i.getAmount(), rel.amount);
												}
											}
											// create the new transfer relation
											rel = new ItemTransferRelation(u, target, rel.amount, iType, line);
											// update the modified item amount
											if (i != null) {
												i.setAmount(Math.max(0,i.getAmount() - rel.amount));
											}
										} else {
											rel = null;
										}
									} else {
										rel = null;
									}
									
									// let's see whether there is a valid relation to add
									if (rel != null) {
										rels.add(rel);
									}
								} else {
									// in u case the order looks like:
									// GIVE <unit id> <amount><EOC>
									if (rel.amount == REFRESHRELATIONS_ALL) { // -2 is used to encode that the amount was 'ALL'
										for (Iterator items = modItems.values().iterator(); items.hasNext(); ) {
											Item i = (Item)items.next();
											TransferRelation r = new ItemTransferRelation(u, target, i.getAmount(), i.getItemType(), line);
											i.setAmount(0);
											rels.add(r);
										}
									}
								}
							} else {
								log.warn("Unit.updateRelations(): cannot parse amount in order " + order);
							}
						}
					} else {
						// relation to myself? you're sick
					}
				}
				continue;
			} 
			// recruitment relation
			if (((OrderToken)tokens.get(0)).equalsToken(getOrder(EresseaOrderConstants.O_RECRUIT))) {
				OrderToken t = (OrderToken)tokens.get(1);
				if (t.ttype == OrderToken.TT_NUMBER) {
					RecruitmentRelation rel = new RecruitmentRelation(u, Integer.parseInt(t.getText()), line);
					rels.add(rel);
				} else {
					log.warn("Unit.updateRelations(): invalid amount in order " + order);
				}
				continue;
			} 
			// enter relation
			if (((OrderToken)tokens.get(0)).equalsToken(getOrder(EresseaOrderConstants.O_ENTER))) {
				OrderToken t = (OrderToken)tokens.get(1);
				UnitContainer uc = null;
				if (t.equalsToken(getOrder(EresseaOrderConstants.O_CASTLE))) {
					t = (OrderToken)tokens.get(2);
					uc = u.getRegion().getBuilding(EntityID.createEntityID(t.getText()));
				} else
					if (t.equalsToken(getOrder(EresseaOrderConstants.O_SHIP))) {
						t = (OrderToken)tokens.get(2);
						uc = u.getRegion().getShip(EntityID.createEntityID(t.getText()));
					}
				if (uc != null) {
					EnterRelation rel = new EnterRelation(u, uc, line);
					rels.add(rel);
				} else {
					log.warn("Unit.refreshRelations(): cannot find target in order " + order);
				}
				// check whether the unit leaves a container
				UnitContainer leftUC = u.getBuilding();
				if (leftUC == null) {
					leftUC = u.getShip();
				}
				if (leftUC != null) {
					LeaveRelation rel = new LeaveRelation(u, leftUC, line);
					rels.add(rel);
				}
				continue;
			} 
			// leave relation
			if (((OrderToken)tokens.get(0)).equalsToken(getOrder(EresseaOrderConstants.O_LEAVE))) {
				UnitContainer uc = u.getBuilding();
				if (uc == null) {
					uc = u.getShip();
				}
				if (uc != null) {
					LeaveRelation rel = new LeaveRelation(u, uc, line);
					rels.add(rel);
				} else {
					log.warn("Unit.refreshRelations(): unit " + u + " cannot leave a ship or a building as indicated by order " + order);
				}
				continue;
			} 
			// teach relation
			if (((OrderToken)tokens.get(0)).equalsToken(getOrder(EresseaOrderConstants.O_TEACH))) {
				int tokCtr = 1;
				OrderToken token = (OrderToken)tokens.get(tokCtr);
				while (token.ttype != OrderToken.TT_EOC) {
					Unit pupil = getTargetUnit(token, u.getRegion());
					if (pupil != null) {
						if (!u.equals(pupil)) {
							TeachRelation rel = new TeachRelation(u, pupil, line);
							rels.add(rel);
						} // else can't teach myself
					} // else pupil not found
					tokCtr++;
					token = (OrderToken)tokens.get(tokCtr);
				}
				continue;
			} 
			// attack relation
			if (((OrderToken)tokens.get(0)).equalsToken(getOrder(EresseaOrderConstants.O_ATTACK))) {
				OrderToken token = (OrderToken)tokens.get(1);
				Unit enemy = getTargetUnit(token, u.getRegion());
				if(enemy != null) {
					AttackRelation rel = new AttackRelation(u, enemy, line);
					rels.add(rel);
				}
			}
		}
		
		return rels;
	}
	
	private Unit getTargetUnit(OrderToken t, Region r) {
		try {
			UnitID id = UnitID.createUnitID(t.getText());
			return r.getUnit(id);
		} catch (NumberFormatException e) {
			log.warn("Unit.getTargetUnit(): cannot parse unit id \"" + t.getText() + "\"!");
		}
		return null;
	}

	/**
	 * Removes quotes at the beginning and at the end of str or
	 * replaces tilde characters with spaces.
	 */
	private String stripQuotes(String str) {
		if (str == null) {
			return null;
		}

		int strLen = str.length();
		if (strLen >= 2 && str.charAt(0) == '"' && str.charAt(strLen - 1) == '"') {
			return str.substring(1, strLen - 1);
		} else {
			return str.replace('~', ' ');
		}
	}


	private String getOrder(String key) {
		return Translations.getOrderTranslation(key);
	}


}
