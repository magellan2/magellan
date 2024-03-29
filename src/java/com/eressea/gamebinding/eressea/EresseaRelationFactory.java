/*
 *  Copyright (C) 2000-2004 Roger Butenuth, Andreas Gampe,
 *                          Stefan Goetz, Sebastian Pappert,
 *                          Klaas Prause, Enno Rehling,
 *                          Sebastian Tusk, Ulrich Kuester,
 *                          Ilja Pavkovic
 *
 * This file is part of the Eressea Java Code Base, see the
 * file LICENSING for the licensing information applying to
 * this file.
 *
 */

package com.eressea.gamebinding.eressea;

import java.io.StringReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.eressea.CoordinateID;
import com.eressea.EntityID;
import com.eressea.GameData;
import com.eressea.Item;
import com.eressea.Region;
import com.eressea.StringID;
import com.eressea.Unit;
import com.eressea.UnitContainer;
import com.eressea.UnitID;
import com.eressea.gamebinding.RelationFactory;
import com.eressea.relation.AttackRelation;
import com.eressea.relation.ControlRelation;
import com.eressea.relation.EnterRelation;
import com.eressea.relation.InterUnitRelation;
import com.eressea.relation.ItemTransferRelation;
import com.eressea.relation.LeaveRelation;
import com.eressea.relation.MovementRelation;
import com.eressea.relation.PersonTransferRelation;
import com.eressea.relation.RecruitmentRelation;
import com.eressea.relation.RenameNamedRelation;
import com.eressea.relation.ReserveRelation;
import com.eressea.relation.TeachRelation;
import com.eressea.relation.TransferRelation;
import com.eressea.relation.TransportRelation;
import com.eressea.relation.UnitRelation;
import com.eressea.relation.UnitTransferRelation;
import com.eressea.rules.ItemCategory;
import com.eressea.rules.ItemType;
import com.eressea.util.CollectionFactory;
import com.eressea.util.Direction;
import com.eressea.util.OrderToken;
import com.eressea.util.Translations;
import com.eressea.util.logging.Logger;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class EresseaRelationFactory implements RelationFactory {
	private static final Logger log = Logger.getInstance(EresseaRelationFactory.class);

	private EresseaRelationFactory() {
	}

	private static final EresseaRelationFactory singleton = new EresseaRelationFactory();

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static EresseaRelationFactory getSingleton() {
		return singleton;
	}

	private static final int REFRESHRELATIONS_ALL = -2;

	/**
	 * Creates a list of com.eressea.util.Relation objects  for a unit starting at order position
	 * <tt>from</tt>. Note: The parameter <code>from</code> is ignored by this implementation!
	 *
	 * @param u The unit
	 * @param from The line of the <code>unit</code>'s orders where to start. Must be > 0
	 * @return A List of Relations for this unit
	 */
	// FIXME (stm 2007-02-24) Should we remove the parameter from from the interface? It violates
	// the unit execution order but it might be useful for other games.
	public List createRelations(Unit u, int from) {
		return createRelations(u, u.getOrders().iterator(), from);
	}

	/**
	 * Creates a list of com.eressea.util.Relation objects  for a unit starting at order position
	 * <tt>from</tt> using <code>orders</code>.
	 *
	 * @param u The unit
	 * @param orders Use these orders instead of the unit's orders
	 * @return A List of Relations for this unit
	 */
	public List createRelations(Unit u, List orders) {
		return createRelations(u, orders.iterator(), 0);
	}

	/**
	 * Creates a list of com.eressea.util.Relation objects for a unit using <code>orders</code>.
	 * starting at order position <tt>from</tt>. Note: The parameter <code>from</code> is
	 * ignored by this implementation!
	 * 
	 * @param u
	 *            The unit
	 * @param orders Use these orders which may not be the unit's orders
	 * @param from
	 *            The line of the <code>unit</code>'s orders where to start. Must be > 0
	 * @return A List of Relations for this unit
	 */
	private List createRelations(Unit u, Iterator orders, int from) {
		from = 0;
		// NOTE: parameter from is ignored!
		List relations = CollectionFactory.createArrayList(5);

		GameData data = u.getRegion().getData();
		Map modItems = null; // needed to track changes in the items for GIB orders
		int modPersons = u.getPersons();

		// clone u unit's items
		modItems = CollectionFactory.createHashtable();
		for(Iterator iter = u.getItems().iterator(); iter.hasNext();) {
			Item i = (Item) iter.next();
			modItems.put(i.getItemType().getID(), new Item(i.getItemType(), i.getAmount()));
		}

		// 4. parse the orders and create new relations
		EresseaOrderParser parser = new EresseaOrderParser(data);

		List ordersCopy = new LinkedList();
		for(Iterator iter = orders; iter.hasNext();) {
			String order = (String) iter.next();
			ordersCopy.add(order);
		}
		
		// TODO (stm): sort order according to execution order and process them in that order.
		// In that case, the parameter from should be ignored entirely 
		
		// process RESERVE orders first
//		Collections.sort(ordersCopy, new EresseaOrderComparator(null));
		createReserveRelations(u, ordersCopy, from, parser, modItems, relations);
	

		// process all other orders
		int line = 0;
		boolean tempOrders = false;
		line = 0;
		
		for(Iterator iter = ordersCopy.iterator(); iter.hasNext();) {
			String order = (String) iter.next();
			
			line++; // keep track of line
			
			// from is ignored
//			if(line < from) {
//				continue;
//			}
			
			if(!parser.read(new StringReader(order))) {
				continue;
			}
			
			List tokens = parser.getTokens();
			
			if(((OrderToken) tokens.get(0)).ttype == OrderToken.TT_COMMENT) {
				continue;
			}
			
			if(((OrderToken) tokens.get(0)).ttype == OrderToken.TT_PERSIST) {
				tokens.remove(0);
			}

			if(tempOrders) {
				// end of temp unit
				if(((OrderToken) tokens.get(0)).equalsToken(getOrder(EresseaConstants.O_END))) {
					tempOrders = false;

					continue;
				}
			}

			// begin of temp unit
			if(((OrderToken) tokens.get(0)).equalsToken(getOrder(EresseaConstants.O_MAKE)) &&
				   ((OrderToken) tokens.get(1)).getText().toUpperCase().startsWith(getOrder(EresseaConstants.O_TEMP))) {
				tempOrders = true;

				continue;
			}

			// movement relation 
			if(((OrderToken) tokens.get(0)).equalsToken(getOrder(EresseaConstants.O_MOVE)) ||
				   ((OrderToken) tokens.get(0)).equalsToken(getOrder(EresseaConstants.O_ROUTE))) {
				List modifiedMovement = CollectionFactory.createArrayList(2);

				// dissect the order into pieces to detect which way the unit
				// is taking
				CoordinateID c = u.getRegion().getCoordinate();
				modifiedMovement.add(c);

				for(Iterator iter2 = tokens.listIterator(1); iter2.hasNext();) {
					OrderToken token = (OrderToken) iter2.next();
					int dir = Direction.toInt(token.getText());

					if(dir != -1) {
						c = new CoordinateID(c); // make c a new copy
						c.translate(Direction.toCoordinate(dir));
						modifiedMovement.add(c);
					} else {
						break;
					}
				}

				relations.add(new MovementRelation(u, modifiedMovement, line));

				continue;
			}

            // enter relation
            if(((OrderToken) tokens.get(0)).equalsToken(getOrder(EresseaConstants.O_ENTER))) {
                OrderToken t = (OrderToken) tokens.get(1);
                UnitContainer uc = null;

                if(t.equalsToken(getOrder(EresseaConstants.O_CASTLE))) {
                    t = (OrderToken) tokens.get(2);
                    uc = u.getRegion().getBuilding(EntityID.createEntityID(t.getText(),data.base));
                } else if(t.equalsToken(getOrder(EresseaConstants.O_SHIP))) {
                    t = (OrderToken) tokens.get(2);
                    uc = u.getRegion().getShip(EntityID.createEntityID(t.getText(),data.base));
                }

                if(uc != null) {
                    EnterRelation rel = new EnterRelation(u, uc, line);
                    relations.add(rel);
                } else {
                    log.debug("Unit.refreshRelations(): cannot find target in order " + order);
                }

                // check whether the unit leaves a container
                UnitContainer leftUC = u.getBuilding();

                if(leftUC == null) {
                    leftUC = u.getShip();
                }

                if(leftUC != null) {
                    LeaveRelation rel = new LeaveRelation(u, leftUC, line);
                    relations.add(rel);
                }

                continue;
            }

            
			// income relation WORK
			if(((OrderToken) tokens.get(0)).equalsToken(getOrder(EresseaConstants.O_WORK))) {
				// TODO!
				continue;
			}

			// income relation ENTERTAIN
			if(((OrderToken) tokens.get(0)).equalsToken(getOrder(EresseaConstants.O_ENTERTAIN))) {
				// TODO!
				continue;
			}

			// income relation TAX
			if(((OrderToken) tokens.get(0)).equalsToken(getOrder(EresseaConstants.O_TAX))) {
				// TODO!
				continue;
			}

			// transport relation
			if(((OrderToken) tokens.get(0)).equalsToken(getOrder(EresseaConstants.O_CARRY))) {
				OrderToken t = (OrderToken) tokens.get(1);
				Unit target = getTargetUnit(t, u.getRegion());

				if((target == null) || u.equals(target)) {
					continue;
				}

				TransportRelation rel = new TransportRelation(u, target, line);
				relations.add(rel);

				continue;
			}

			// transfer relation
			if(((OrderToken) tokens.get(0)).equalsToken(getOrder(EresseaConstants.O_GIVE)) ||
				   ((OrderToken) tokens.get(0)).equalsToken(getOrder(EresseaConstants.O_SUPPLY))) {
				// GIB 0|<enr> (ALLES|EINHEIT|KRaeUTER|KOMMANDO|((([JE] <amount>)|ALLES) (SILBER|PERSONEN|<gegenstand>)))
				final int unitIndex = 1;
				int amountIndex = 2;
				int itemIndex = 3;
				boolean hasEach = false;

				OrderToken t = (OrderToken) tokens.get(unitIndex);
				Unit target = getTargetUnit(t, u.getRegion());

				if(target != null) {
					if(!target.equals(u)) {
						TransferRelation rel = new TransferRelation(u, target, -1, line);

						t = (OrderToken) tokens.get(amountIndex);
						if(t.equalsToken(getOrder(EresseaConstants.O_HERBS))) {
							// if the 'amount' is HERBS then create relations for all herbs the unit carries
							ItemCategory herbCategory = data.rules.getItemCategory(StringID.create(("HERBS")));

							if((herbCategory != null)) {
								for(Iterator items = modItems.values().iterator(); items.hasNext();) {
									Item i = (Item) items.next();

									if(herbCategory.equals(i.getItemType().getCategory())) {
										TransferRelation r = new ItemTransferRelation(u, target,
																					  i.getAmount(),
																					  i.getItemType(),
																					  line);
										i.setAmount(0);
										relations.add(r);
									}
								}
							}

						} else if(t.equalsToken(getOrder(EresseaConstants.O_CONTROL))) {
							UnitRelation r = new ControlRelation(u, target, line);
							relations.add(r);
						} else if(t.equalsToken(getOrder(EresseaConstants.O_UNIT))) {
							UnitRelation r = new UnitTransferRelation(u, target,  (u.realRace != null) ? u.realRace : u.race, line);
							relations.add(r);
						} else {
							boolean parseItem = false;
							// order is GIVE bla [EACH] <amount> <something>
							if ((t.ttype == OrderToken.TT_KEYWORD)
									&& t.equalsToken(getOrder(EresseaConstants.O_ALL))) {
								// -2 encodes that everything is to be transferred
								rel.amount = REFRESHRELATIONS_ALL;
								parseItem = true;
							} else {
								// GIVE bla EACH ALL does not make a lot of sense
								if ((t.ttype == OrderToken.TT_KEYWORD)
										&& t.equalsToken(getOrder(EresseaConstants.O_EACH))) {
									hasEach = true;
									t = (OrderToken) tokens.get(++amountIndex);
									++itemIndex;
								}
								if (t.ttype == OrderToken.TT_NUMBER) {
									// u is a plain number
									rel.amount = Integer.parseInt(t.getText());
									parseItem = true;
								}
							}
							if (parseItem) {
								if (rel.amount == -1) { // -1 means that the amount could not determined
									log.warn("Unit.updateRelations(): cannot parse amount in order "
													+ order);
								} else {
									t = (OrderToken) tokens.get(itemIndex);

									if (t.ttype != OrderToken.TT_EOC) {
										// now the order must look something like:
										// GIVE <unit id> <amount> <object><EOC>
										String itemName = stripQuotes(t.getText());

										if (t.equalsToken(getOrder(EresseaConstants.O_MEN))) {
											// if the specified amount was 'all':
											if (rel.amount == REFRESHRELATIONS_ALL) {
												rel.amount = modPersons;
											} else {
												// if not, only transfer the minimum amount the unit
												// has
												rel.amount = Math.min(modPersons, rel.amount);
											}

											rel = new PersonTransferRelation(u, target, rel.amount,
													(u.realRace != null) ? u.realRace : u.race,
													line);

											// update the modified person amount
											modPersons = Math.max(0, modPersons - rel.amount);
										} else if (itemName.length() > 0) {
											// TODO(pavkovic): korrigieren!!! Hier soll eigentlich
											// das Item �ber den
											// �bersetzten Namen gefunden werden!!!
											ItemType iType = data.rules.getItemType(itemName);

											// ItemType iType =
											// data.rules.getItemType(StringID.create(itemName));
											if (iType != null) {
												// get the item from the list of modified items
												Item i = (Item) modItems.get(iType.getID());

												if (i == null) {
													// item unknown
													rel.amount = 0;
												} else {
													// if the specified amount is 'all', convert u
													// to a decent number
													if (rel.amount == REFRESHRELATIONS_ALL) {
														rel.amount = i.getAmount();
													} else {
														// if not, only transfer the minimum amount
														// the unit has
														if (i.getAmount() < rel.amount)
															rel.warning = true;
														// GIVE ... EACH ALL does not make sense
														rel.amount = Math
																.min(
																		i.getAmount(),
																		rel.amount
																				* (hasEach ? target
																						.getModifiedPersons()
																						: 1));
													}
												}

												// create the new transfer relation
												rel = new ItemTransferRelation(u, target,
														rel.amount, iType, line, rel.warning);

												// update the modified item amount
												if (i != null) {
													i.setAmount(Math.max(0, i.getAmount()
															- rel.amount));
												}
											} else {
												rel = null;
											}
										} else {
											rel = null;
										}

										// let's see whether there is a valid relation to add
										if (rel != null) {
											relations.add(rel);
										}
									} else {
										// in u case the order looks like:
										// GIVE <unit id> <amount><EOC>
										if (rel.amount == REFRESHRELATIONS_ALL) { // -2 is used to
																					// encode that
																					// the amount
																					// was 'ALL'

											for (Iterator items = modItems.values().iterator(); items
													.hasNext();) {
												Item i = (Item) items.next();
												TransferRelation r = new ItemTransferRelation(u,
														target, i.getAmount(), i.getItemType(),
														line);
												i.setAmount(0);
												relations.add(r);
											}
										}
									}
								}
							}
						}
					} else {
						// relation to myself? you're sick
					}
				}

				continue;
			}

			// recruitment relation
			if(((OrderToken) tokens.get(0)).equalsToken(getOrder(EresseaConstants.O_RECRUIT))) {
				OrderToken t = (OrderToken) tokens.get(1);

				if(t.ttype == OrderToken.TT_NUMBER) {
					RecruitmentRelation rel = new RecruitmentRelation(u,
																	  Integer.parseInt(t.getText()),
																	  line);
					relations.add(rel);
				} else {
					log.warn("Unit.updateRelations(): invalid amount in order " + order);
				}

				continue;
			}

			// leave relation
			if(((OrderToken) tokens.get(0)).equalsToken(getOrder(EresseaConstants.O_LEAVE))) {
				UnitContainer uc = u.getBuilding();

				if(uc == null) {
					uc = u.getShip();
				}

				if(uc != null) {
					LeaveRelation rel = new LeaveRelation(u, uc, line);
					relations.add(rel);
				} else {
					log.warn("Unit.refreshRelations(): unit " + u +
							 " cannot leave a ship or a building as indicated by order " + order);
				}

				continue;
			}

			// teach relation
			if(((OrderToken) tokens.get(0)).equalsToken(getOrder(EresseaConstants.O_TEACH))) {
				int tokCtr = 1;
				OrderToken token = (OrderToken) tokens.get(tokCtr);

				while(token.ttype != OrderToken.TT_EOC) {
					Unit pupil = getTargetUnit(token, u.getRegion());

					if(pupil != null) {
						if(!u.equals(pupil)) {
							TeachRelation rel = new TeachRelation(u, pupil, line);
							relations.add(rel);
						}

						// else can't teach myself
					}

					// else pupil not found
					tokCtr++;
					token = (OrderToken) tokens.get(tokCtr);
				}

				continue;
			}

			// attack relation
			if(((OrderToken) tokens.get(0)).equalsToken(getOrder(EresseaConstants.O_ATTACK))) {
			    if(tokens.size() > 1) {
			        OrderToken enemyToken = (OrderToken) tokens.get(1);
			        Unit enemy = getTargetUnit(enemyToken, u.getRegion());

			        if(enemy != null) {
			            AttackRelation rel = new AttackRelation(u, enemy, line);
			            relations.add(rel);
			        }
                }
			}
            
            // name relation
            // TODO: Do it right
            if(((OrderToken) tokens.get(0)).equalsToken(getOrder(EresseaConstants.O_NAME))) {
                if(tokens.size() > 2) {
                    OrderToken whatToken = (OrderToken) tokens.get(1);
                    
                    if(whatToken.equalsToken(getOrder(EresseaConstants.O_UNIT))) {
                        if(tokens.size() > 3) {
                            relations.addAll(createRenameUnitRelation(u, (OrderToken) tokens.get(2), line));
                        }
                    } else {
                        if(tokens.size() > 4) {
                            if(whatToken.equalsToken(getOrder(EresseaConstants.O_CASTLE))) {
                                relations.addAll(createRenameUnitContainerRelation(u, (OrderToken) tokens.get(2), (OrderToken) tokens.get(3), line));                                
                            } else if(whatToken.equalsToken(getOrder(EresseaConstants.O_FACTION))) {
                                relations.addAll(createRenameUnitContainerRelation(u, (OrderToken) tokens.get(2), (OrderToken) tokens.get(3), line));                                
                            } else if(whatToken.equalsToken(getOrder(EresseaConstants.O_REGION))) {
                                relations.addAll(createRenameUnitContainerRelation(u, (OrderToken) tokens.get(2), (OrderToken) tokens.get(3), line));                                
                            } else if(whatToken.equalsToken(getOrder(EresseaConstants.O_SHIP))) {
                                relations.addAll(createRenameUnitContainerRelation(u, (OrderToken) tokens.get(2), (OrderToken) tokens.get(3), line));                                
                            }
                        }
                    }
                    if(whatToken.equalsToken(getOrder(EresseaConstants.O_FOREIGN))) {
                        //rels.addAll(createRenameForeignUnitContainerRelation(u, (OrderToken) tokens.get(2), (OrderToken) tokens.get(3)));
                            // retVal = readBenenneFremdes(t);
                    }   
                }   
            }
		}

		return relations;
	}

    /**
	 * Creates ReserveRelations for alle reserve orders of a unit 
	 *
	 * @param u The unit
	 * @param ordersCopy A copy of the unit's orders. TODO: remove this parameter
	 * @param from Ignored!
	 * @param parser An order parser which has been initialized with the game data
	 * @param modItems The Map of the unit's modified items
	 * @param rels The newly created {@link ReserveRelation}s are inserted into this list   
	 */
	private static void createReserveRelations(Unit u, List ordersCopy, int from,
			EresseaOrderParser parser, Map modItems, List rels) {
		from = 0;
		// parameter from is ignored because it violates execution order
		
		Map reservedItems = new HashMap();
		GameData data = u.getRegion().getData();
		int line = 0;
		
		for(Iterator iter = ordersCopy.iterator(); iter.hasNext();) {
			int orderIndex = 0;
			int amountIndex = 1;
			int itemIndex = 2;
			String order = (String) iter.next();

			line++; // keep track of line

			// parameter from is ignored because it violates execution order
//			if(line < from) {
//				continue;
//			}

			if(!parser.read(new StringReader(order))) {
				continue;
			}

			List tokens = parser.getTokens();


			if(((OrderToken) tokens.get(0)).ttype == OrderToken.TT_COMMENT) {
				continue;
			}

			if(((OrderToken) tokens.get(0)).ttype == OrderToken.TT_PERSIST) {
				orderIndex++; amountIndex++; itemIndex++;
			}

			if(tokens.size()>orderIndex && ((OrderToken) tokens.get(orderIndex)).equalsToken(getOrder(EresseaConstants.O_RESERVE))){
				// RESERVE [EACH] <amount> <object><EOC>
				// RESERVIERE[JE] <amount> <object><EOC>
				OrderToken t = (OrderToken) tokens.get(amountIndex);
				boolean hasEach = false;
				int amount = -1; 
				boolean warning = false;

				ReserveRelation rel = null;
				if (t.ttype == OrderToken.TT_KEYWORD){
					if (t.equalsToken(getOrder(EresseaConstants.O_EACH))){
						hasEach = true;
						t = (OrderToken) tokens.get(++amountIndex);
						++itemIndex;
					}
				}
				if(t.ttype == OrderToken.TT_NUMBER) {
					amount = Integer.parseInt(t.getText());

					if(amount != -1) { // -1 means that the amount could not determined
						t = (OrderToken) tokens.get(itemIndex);
						if(t.ttype != OrderToken.TT_EOC) {
							String itemName = stripQuotes(t.getText());
							
							if(itemName.length() > 0) {
								// TODO(pavkovic): korrigieren!!! Hier soll eigentlich das Item �ber den 
								// �bersetzten Namen gefunden werden!!!
								ItemType iType = data.rules.getItemType(itemName);
										
								if(iType != null) {
									// get the item from the list of modified items
									Item i = (Item) modItems.get(iType.getID());
									
									if(i == null) {
										// item unknown
										amount = 0;
										warning = true;
									} else {
//										// if the specified amount is 'all', convert u to a decent number
//										if(amount == REFRESHRELATIONS_ALL) {
//											amount = i.getAmount();
//											warning = true;
//										} else {
//											// if not, only transfer the minimum amount the unit has
											if (i.getAmount()<amount)
												warning=true;
											// TODO (stm) should this be persons or modified persons?
											amount *= hasEach?u.getModifiedPersons():1;
//										}
										amount = Math.min(i.getAmount(), amount);
									}

									// create the new reserve relation
									rel = new ReserveRelation(u, amount, iType, line, warning);
									
									// update the modified item amount and record reserved amount
									if(i != null) {
										i.setAmount(Math.max(0, i.getAmount() - rel.amount));
										Item rItem = (Item) reservedItems.get(iType.getID());
										if (rItem == null){
											rItem = new Item(i.getItemType(), rel.amount);
											reservedItems.put(i.getItemType(), rItem);
										}else{
											rItem.setAmount(rItem.getAmount()+rel.amount);
										}
									}
								}
							}
						}
						
						// let's see whether there is a valid relation to add
						if(rel != null) {
							rels.add(rel);
						}
					}
				}  else {
					log.warn("Unit.updateRelations(): cannot parse amount in order " +
							order);
				}
			}
		}

	}

	private List createRenameUnitRelation(Unit unit, OrderToken token, int line) {
        return CollectionFactory.singletonList(new RenameNamedRelation(unit, unit, stripQuotes(token.getText()), line));
    }

    private List createRenameUnitContainerRelation(Unit unit, OrderToken containerToken, OrderToken name, int line) {
        
        return CollectionFactory.EMPTY_LIST;
    }
    
	private Unit getTargetUnit(OrderToken t, Region r) {
		if (t.getText().startsWith(";")){return null;}
		try {
			UnitID id = UnitID.createUnitID(t.getText(),r.getData().base);

			return r.getUnit(id);
		} catch(NumberFormatException e) {
			log.warn("Unit.getTargetUnit(): cannot parse unit id \"" + t.getText() + "\"! (Region: " + r.toString() + ")");
		}

		return null;
	}

	/**
	 * Removes quotes at the beginning and at the end of str or replaces tilde characters with
	 * spaces.
	 *
	 * @param str TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	private static String stripQuotes(String str) {
		if(str == null) {
			return null;
		}

		int strLen = str.length();

		if((strLen >= 2) && (str.charAt(0) == '"') && (str.charAt(strLen - 1) == '"')) {
			return str.substring(1, strLen - 1);
		} else {
			return str.replace('~', ' ');
		}
	}

	private static String getOrder(String key) {
		return Translations.getOrderTranslation(key);
	}
}
