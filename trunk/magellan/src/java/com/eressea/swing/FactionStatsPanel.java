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

package com.eressea.swing;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.eressea.Building;
import com.eressea.Coordinate;
import com.eressea.Faction;
import com.eressea.GameData;
import com.eressea.Group;
import com.eressea.ID;
import com.eressea.IntegerID;
import com.eressea.Item;
import com.eressea.Message;
import com.eressea.Region;
import com.eressea.Ship;
import com.eressea.Skill;
import com.eressea.StringID;
import com.eressea.TempUnit;
import com.eressea.Unit;
import com.eressea.UnitContainer;
import com.eressea.UnitID;
import com.eressea.event.EventDispatcher;
import com.eressea.event.GameDataEvent;
import com.eressea.event.SelectionEvent;
import com.eressea.event.SelectionListener;
import com.eressea.rules.ItemCategory;
import com.eressea.rules.ItemType;
import com.eressea.rules.Race;
import com.eressea.rules.SkillType;
import com.eressea.rules.UnitContainerType;
import com.eressea.swing.tree.CellRenderer;
import com.eressea.swing.tree.CopyTree;
import com.eressea.swing.tree.NodeWrapperFactory;
import com.eressea.swing.tree.SimpleNodeWrapper;
import com.eressea.swing.tree.UnitContainerNodeWrapper;
import com.eressea.swing.tree.UnitNodeWrapper;
import com.eressea.util.CollectionFactory;
import com.eressea.util.SkillStats;
import com.eressea.util.Units;
import com.eressea.util.comparator.AllianceFactionComparator;
import com.eressea.util.comparator.FactionTrustComparator;
import com.eressea.util.comparator.IDComparator;
import com.eressea.util.comparator.NameComparator;
import com.eressea.util.comparator.SkillComparator;
import com.eressea.util.comparator.SpecifiedSkillTypeSkillComparator;
import com.eressea.util.comparator.UnitSkillComparator;
import com.eressea.util.logging.Logger;

/**
 * A panel for showing statistics about factions.
 */
public class FactionStatsPanel extends InternationalizedDataPanel implements SelectionListener,
																			 TreeSelectionListener
{
	private static final Logger log = Logger.getInstance(FactionStatsPanel.class);
	private Map factions = null;
	private Map regions = null;
	private DefaultTreeModel treeModel = null;
	private DefaultMutableTreeNode rootNode = null;
	private CopyTree tree = null;
	private NodeWrapperFactory nodeWrapperFactory;
	private Units unitsTools = null;

	/**
	 * Creates a new FactionStatsPanel object.
	 *
	 * @param d TODO: DOCUMENT ME!
	 * @param initData TODO: DOCUMENT ME!
	 * @param p TODO: DOCUMENT ME!
	 */
	public FactionStatsPanel(EventDispatcher d, GameData initData, Properties p) {
		super(d, initData, p);
		this.setLayout(new BorderLayout());
		this.add(getStatPanel(), BorderLayout.CENTER);
		factions = CollectionFactory.createHashtable(data.factions());
		regions = CollectionFactory.createHashtable(data.regions());
		dispatcher.addSelectionListener(this);
		unitsTools = (data != null) ? new Units(data.rules) : new Units(null);
		nodeWrapperFactory = new NodeWrapperFactory(settings);

		// to get the pref-adapter
		Unit temp = new Unit(UnitID.createUnitID(0));
		nodeWrapperFactory.createUnitNodeWrapper(temp);
		nodeWrapperFactory.createSkillNodeWrapper(temp,
												  new Skill(new SkillType(StringID.create("Test")),
															0, 0, 0, false), null);
		nodeWrapperFactory.createItemNodeWrapper(new Item(new ItemType(StringID.create("Test")), 0));
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param e TODO: DOCUMENT ME!
	 */
	public void gameDataChanged(GameDataEvent e) {
		data = e.getGameData();

		/**
		 * Don't clear factions as the SelectionEvent of the updated List in FactionStatsDialog
		 * might be processed befor the GameDataEvent
		 */

		// factions.clear();
		if(data != null) {
			unitsTools.setRules(data.rules);
			setRegions(data.regions().values());
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param e TODO: DOCUMENT ME!
	 */
	public void selectionChanged(SelectionEvent e) {
		if(e.getSource() == this) {
			return;
		}

		if((e.getSelectedObjects() != null) && (e.getSelectionType() == SelectionEvent.ST_REGIONS)) {
			List regions = CollectionFactory.createLinkedList();

			for(Iterator iter = e.getSelectedObjects().iterator(); iter.hasNext();) {
				Object o = iter.next();

				if(o instanceof Region) {
					regions.add(o);
				}
			}

			/**
			 * Ulrich Küster: (!) Special care has to be taken. Generelly it can not be differed,
			 * if SelectionEvents come from the faction list in FactionStatsDialog or from other
			 * components of Magellan (except if a special SelectionType has been defined in
			 * SelectionEvent and is used). To keep the faction list in FactionStatsDialog
			 * consistent to the displayed data in this FactionStatsPanel object, setFaction()
			 * should be _never_ called by this selectionChanged()-method, but directly by the
			 * valueChanged()-method of FactionStatsDialog.
			 */
			setRegions(regions);
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param fs TODO: DOCUMENT ME!
	 */
	public void setFactions(Collection fs) {
		factions.clear();

		if(fs != null) {
			for(Iterator iter = fs.iterator(); iter.hasNext();) {
				Faction f = (Faction) iter.next();
				factions.put(f.getID(), f);
			}
		}

		updateTree();
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param rs TODO: DOCUMENT ME!
	 */
	public void setRegions(Collection rs) {
		if((rs == null) || rs.isEmpty()) {
			regions = CollectionFactory.createHashtable(data.regions());
		} else {
			regions.clear();

			for(Iterator iter = rs.iterator(); iter.hasNext();) {
				Region r = (Region) iter.next();
				regions.put(r.getID(), r);
			}
		}

		updateTree();
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param e TODO: DOCUMENT ME!
	 */
	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

		if(node == null) {
			return;
		}

		Object o = node.getUserObject();

		if(o instanceof UnitNodeWrapper) {
			Unit u = ((UnitNodeWrapper) o).getUnit();
			dispatcher.fire(new SelectionEvent(this, null, u));
		} else if(o instanceof UnitContainerNodeWrapper) {
			UnitContainer uc = ((UnitContainerNodeWrapper) o).getUnitContainer();
			dispatcher.fire(new SelectionEvent(this, null, uc));
		}
	}

	/* Ilja Pavkovic 2001.10.19:
	 * I wanted to see the original values and the predictions of
	 * units and persons.
	 */

	/**
	 * Ulrich Küster: The algorithm wasn't correct as it is wrong to count the number of persons in
	 * the temp units to get the number of recruited persons. a) Temp units always have zero
	 * persons b) 'normal' units can recruit persons So it is necessary to look at all unit's
	 * RecruitmentRelations in the units's cache-object. I changed that. (Of course this doesn't
	 * consider, that persons can be given to '0'.)
	 */
	private void updateTree() {
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		DefaultMutableTreeNode n = null;
		DefaultMutableTreeNode m = null;
		DefaultMutableTreeNode o = null;
		Map units = CollectionFactory.createHashtable();
		int personCounter = 0;
		int modifiedUnitsCounter = 0;
		int tempUnitsCounter = 0;
		int modifiedPersonCounter = 0;
		Faction f = null;
		rootNode.removeAllChildren();

		/**
		 * Used to collect persons of different race than their faction. Key: String (racename),
		 * Value: List containing the units
		 */
		Map specialPersons = CollectionFactory.createHashtable();

		if(factions.size() == 1) {
			f = (Faction) factions.values().iterator().next();
		}

		SkillStats skillStats = new SkillStats();

		for(Iterator iter = regions.values().iterator(); iter.hasNext();) {
			Region r = (Region) iter.next();

			for(Iterator it = r.units().iterator(); it.hasNext();) {
				Unit u = (Unit) it.next();

				if(factions.containsKey(u.getFaction().getID())) {
					units.put(u.getID(), u);
					personCounter += u.persons;
					skillStats.addUnit(u);

					Race race = u.realRace;

					if(race == null) {
						race = u.race;
					}

					if((u.getFaction().getRace() != null) &&
						   !race.equals(u.getFaction().getRace())) {
						List v = (List) specialPersons.get(race.getName());

						if(v == null) {
							v = CollectionFactory.createLinkedList();
							specialPersons.put(race.getName(), v);
						}

						v.add(u);
					}

					/**
					 * poorly it is necessary to refresh all relations, as at this time it is not
					 * assured that they are always up to date. Possibly it would be better, to
					 * calculate them only, if orders are loaded or changed...
					 */
					u.getRegion().refreshUnitRelations();

					if(u.getModifiedPersons() > 0) {
						modifiedUnitsCounter++;
						modifiedPersonCounter += u.getModifiedPersons();
					}

					if(u instanceof TempUnit) {
						tempUnitsCounter++;
					}
				}
			}
		}

		n = new DefaultMutableTreeNode(getString("node.units") + (units.size() - tempUnitsCounter) +
									   " (" + modifiedUnitsCounter + ")");
		rootNode.add(n);
		n = new DefaultMutableTreeNode(getString("node.persons") + personCounter + " (" +
									   modifiedPersonCounter + ")");
		rootNode.add(n);

		if(f != null) {
			if(f.getType() != null) {
				n = new DefaultMutableTreeNode(getString("node.race") + f.getType().getName());
				rootNode.add(n);
			}

			if(specialPersons.size() > 0) {
				n = new DefaultMutableTreeNode(getString("node.otherrace"));
				rootNode.add(n);

				for(Iterator iter = specialPersons.keySet().iterator(); iter.hasNext();) {
					Object obj = iter.next();
					List v = (List) specialPersons.get(obj);
					int count = 0;

					for(Iterator iterator = v.iterator(); iterator.hasNext();) {
						count += ((Unit) iterator.next()).persons;
					}

					m = new DefaultMutableTreeNode(obj + ": " + count);
					n.add(m);

					for(Iterator iterator = v.iterator(); iterator.hasNext();) {
						o = new DefaultMutableTreeNode(nodeWrapperFactory.createUnitNodeWrapper((Unit) iterator.next()));
						m.add(o);
					}
				}
			}

			if(f.spellSchool != null) {
				n = new DefaultMutableTreeNode(getString("node.magicschool") + f.spellSchool);
				rootNode.add(n);
			}

			String description = f.getDescription();

			if((description != null) && (description.length() > 0)) {
				n = new DefaultMutableTreeNode(getString("node.banner") + description);
				rootNode.add(n);
			}

			if(f.email != null) {
				n = new DefaultMutableTreeNode(new SimpleNodeWrapper(getString("node.e-mail") +
																	 f.email, null, f.email));
				rootNode.add(n);
			}

			String nodeLabel = FactionTrustComparator.getTrustLevelLabel(FactionTrustComparator.getTrustLevel(f.trustLevel));
			nodeLabel += (" (" + f.trustLevel + ")");
			n = new DefaultMutableTreeNode(nodeLabel);
			rootNode.add(n);

			if(f.score > 0) {
				n = new DefaultMutableTreeNode(getString("node.score") + f.score + "/" +
											   f.averageScore + " (" +
											   (int) ((100.0 * f.score) / f.averageScore) + "%)");
				rootNode.add(n);
			}

			if(f.migrants > 0) {
				n = new DefaultMutableTreeNode(getString("node.migrants") + f.migrants + "/" +
											   f.maxMigrants);
				rootNode.add(n);
			}

			if(f.getRaceNamePrefix() != null) {
				n = new DefaultMutableTreeNode(getString("node.racenameprefix") +
											   f.getRaceNamePrefix());
				rootNode.add(n);
			}

			if((f.allies != null) && (f.allies.size() > 0)) {
				n = new DefaultMutableTreeNode(getString("node.alliances"));
				rootNode.add(n);
				showAlliances(f.allies, n);
			}

			if((f.groups != null) && (f.groups.size() > 0)) {
				n = new DefaultMutableTreeNode(getString("node.groups"));
				rootNode.add(n);

				for(Iterator iter = f.groups.values().iterator(); iter.hasNext();) {
					Group g = (Group) iter.next();
					m = new DefaultMutableTreeNode(g);
					n.add(m);

					if(g.getRaceNamePrefix() != null) {
						m.add(new DefaultMutableTreeNode(getString("node.racenameprefix") +
														 g.getRaceNamePrefix()));
					}

					showAlliances(g.allies(), m);
				}
			}
		}

		// earned amount of money
		// it is necessary to parse messages to get this information
		for(Iterator fIter = factions.values().iterator(); fIter.hasNext();) {
			// 0 = Arbeiten
			// 1 = Unterhaltung
			// 2 = Treiben
			// 3 = Handel
			// 4 = am Handel
			// 5 = Diebstahl
			// 6 = Zauberei
			int earned[] = new int[] { 0, 0, 0, 0, 0, 0, 0 };
			int wanted[] = new int[] { 0, 0, 0, 0, 0, 0, 0 };
			int spentForTrade = 0;
			Faction faction = (Faction) fIter.next();

			if(faction.messages != null) {
				Iterator iter = faction.messages.iterator();

				while(iter.hasNext()) {
					Message msg = (Message) iter.next();

					if(msg.attributes != null) {
						// check whether the message belongs to one of the selected regions
						String value = (String) msg.attributes.get("region");

						if(value != null) {
							String regionCoordinate = value;
							ID coordinate = Coordinate.parse(regionCoordinate, ",");

							if(coordinate == null) {
								coordinate = Coordinate.parse(regionCoordinate, " ");
							}

							if(!regions.containsKey(coordinate)) {
								continue;
							}
						} else {
							// some messages, for instance of type 170076 don't have
							// a region tag but a unit tag. Therefore get the region
							// via the unit of that message!
							value = (String) msg.attributes.get("unit");

							if(value != null) {
								String number = value;
								UnitID id = UnitID.createUnitID(number, 10);
								Unit unit = (Unit) data.units().get(id);

								if(unit != null) {
									Region r = unit.getRegion();

									if(r != null) {
										Coordinate c = r.getCoordinate();

										if(!regions.containsKey(c)) {
											continue;
										}
									}
								}
							}
						}

						// region check done
						if((msg.getMessageType() != null) &&
							   (msg.getMessageType().getID() != null)) {
							int msgID = ((IntegerID) msg.getMessageType().getID()).intValue();

							if((msgID == 771334452) || (msgID == 2097)) {
								// Einnahmen
								String modeValue = (String) msg.attributes.get("mode");

								if(modeValue != null) {
									int i = Integer.parseInt(modeValue);
									value = (String) msg.attributes.get("amount");

									if(value != null) {
										earned[i] += Integer.parseInt(value);
									}

									value = (String) msg.attributes.get("wanted");

									if(value != null) {
										wanted[i] += Integer.parseInt(value);
									}
								}
							} else if(msgID == 170076) {
								// bezahlt für Kauf von Luxusgütern
								value = (String) msg.attributes.get("money");

								if(value != null) {
									spentForTrade += Integer.parseInt(value);
								}
							}
						}
					}
				}
			}

			int totalIncome = 0;
			int totalWanted = 0;

			for(int i = 0; i < earned.length; i++) {
				totalIncome += earned[i];
				totalWanted += wanted[i];
			}

			if((totalIncome != 0) || (totalWanted != 0)) {
				Object msgArgs[] = { new Integer(totalIncome), new Integer(totalWanted) };
				n = new DefaultMutableTreeNode((new java.text.MessageFormat(getString("node.income"))).format(msgArgs));
				rootNode.add(n);
			}

			for(int i = 0; i < earned.length; i++) {
				if((earned[i] != 0) || (wanted[i] != 0)) {
					Object msgArgs[] = { new Integer(earned[i]) };
					String s = (new java.text.MessageFormat(getString("node.income" + i)).format(msgArgs));

					if(earned[i] != wanted[i]) {
						msgArgs = new Object[] { new Integer(wanted[i]) };
						s += (new java.text.MessageFormat(getString("node.incomewanted"))).format(msgArgs);
					}

					m = new DefaultMutableTreeNode(s);
					n.add(m);
				}
			}

			if(spentForTrade != 0) {
				Object msgArgs[] = { new Integer(-spentForTrade) };
				String s = (new java.text.MessageFormat(getString("node.spentfortrade")).format(msgArgs));
				m = new DefaultMutableTreeNode(s);
				n.add(m);
			}
		}

		// show items and clear categories
		unitsTools.addCategorizedUnitItems(units.values(), rootNode, null, null, true);

		// add buildings
		// maps BuildingTypes to a List, containing the single buildings
		Map buildingsCounter = CollectionFactory.createHashtable();

		// collect the buildings
		for(Iterator iterator = regions.values().iterator(); iterator.hasNext();) {
			Region r = (Region) iterator.next();

			for(Iterator iter = r.buildings().iterator(); iter.hasNext();) {
				Building building = (Building) iter.next();

				if((building.getOwnerUnit() != null) &&
					   factions.containsKey(building.getOwnerUnit().getFaction().getID())) {
					if(!buildingsCounter.containsKey(building.getType())) {
						buildingsCounter.put(building.getType(),
											 CollectionFactory.createLinkedList());
					}

					((List) buildingsCounter.get(building.getType())).add(building);
				}
			}
		}

		// add the buildings to the tree
		if(buildingsCounter.keySet().size() > 0) {
			n = new DefaultMutableTreeNode(getString("node.buildings"));
			rootNode.add(n);
		}

		for(Iterator iter = buildingsCounter.keySet().iterator(); iter.hasNext();) {
			UnitContainerType buildingType = (UnitContainerType) iter.next();
			m = new DefaultMutableTreeNode(buildingType.getName() + ": " +
										   ((List) buildingsCounter.get(buildingType)).size());
			n.add(m);

			for(Iterator i = ((List) buildingsCounter.get(buildingType)).iterator(); i.hasNext();) {
				UnitContainerNodeWrapper uc = nodeWrapperFactory.createUnitContainerNodeWrapper((Building) i.next());
				m.add(new DefaultMutableTreeNode(uc));
			}
		}

		// add ships
		// maps ShipTypes to Lists, containing the single ships
		Map shipsCounter = CollectionFactory.createHashtable();

		// collect the ships
		for(Iterator iterator = regions.values().iterator(); iterator.hasNext();) {
			Region r = (Region) iterator.next();

			for(Iterator iter = r.ships().iterator(); iter.hasNext();) {
				Ship ship = (Ship) iter.next();

				if((ship.getOwnerUnit() != null) &&
					   factions.containsKey(ship.getOwnerUnit().getFaction().getID())) {
					if(!shipsCounter.containsKey(ship.getType())) {
						shipsCounter.put(ship.getType(), CollectionFactory.createLinkedList());
					}

					((List) shipsCounter.get(ship.getType())).add(ship);
				}
			}
		}

		// add the ships to the tree
		if(shipsCounter.keySet().size() > 0) {
			n = new DefaultMutableTreeNode(getString("node.ships"));
			rootNode.add(n);
		}

		for(Iterator iter = shipsCounter.keySet().iterator(); iter.hasNext();) {
			UnitContainerType shipType = (UnitContainerType) iter.next();
			m = new DefaultMutableTreeNode(shipType.getName() + ": " +
										   ((List) shipsCounter.get(shipType)).size());
			n.add(m);

			for(Iterator i = ((List) shipsCounter.get(shipType)).iterator(); i.hasNext();) {
				UnitContainerNodeWrapper uc = nodeWrapperFactory.createUnitContainerNodeWrapper((Ship) i.next());
				m.add(new DefaultMutableTreeNode(uc));
			}
		}

		// add skills
		List sortedSkillTypes = skillStats.getKnownSkillTypes();

		if(sortedSkillTypes.size() > 0) {
			n = new DefaultMutableTreeNode(getString("node.skills"));
			rootNode.add(n);

			for(Iterator iter = sortedSkillTypes.iterator(); iter.hasNext();) {
				SkillType type = (SkillType) iter.next();
				List sortedSkills = skillStats.getKnownSkills(type);

				for(Iterator i = sortedSkills.iterator(); i.hasNext();) {
					Skill skill = (Skill) i.next();
					m = new DefaultMutableTreeNode(new SimpleNodeWrapper(type.getName() + " T" +
																		 skill.getLevel() + ": " +
																		 skillStats.getPersonNumber(skill),
																		 type.getName()));
					n.add(m);

					List unitList = skillStats.getUnits(skill);

					// now sort the units and add them as nodes...
					Comparator idCmp = new IDComparator();
					Comparator unitCmp = new UnitSkillComparator(new SpecifiedSkillTypeSkillComparator(type,
																									   new SkillComparator(),
																									   null),
																 idCmp);
					Collections.sort(unitList, unitCmp);

					for(Iterator iterator = unitList.iterator(); iterator.hasNext();) {
						Unit u = (Unit) iterator.next();
						String text = u.toString();

						if(!data.noSkillPoints) {
							int bonus = u.race.getSkillBonus(type);
							int currentDays = u.getSkill(type).getPointsPerPerson();
							int nextLevelDays = Skill.getPointsAtLevel(skill.getLevel() - bonus +
																	   1);
							int pointsToLearn = nextLevelDays - currentDays;
							int turnsToLearn = pointsToLearn / 30;

							if((pointsToLearn % 30) > 0) {
								turnsToLearn++;
							}

							text += (": " + " [" + currentDays + " -> " + nextLevelDays + " {" +
							turnsToLearn + "}], " + u.persons);
						}

						UnitNodeWrapper w = nodeWrapperFactory.createUnitNodeWrapper(u, text);
						m.add(new DefaultMutableTreeNode(w));
					}
				}
			}
		}

		// Add production
		// Mapping ItemCategory to Hashtable that maps resource (String) to ProductionStats-Objects
		Map production = CollectionFactory.createHashtable();

		for(Iterator Iter = factions.values().iterator(); Iter.hasNext();) {
			Faction faction = (Faction) Iter.next();

			if(faction.messages != null) {
				for(Iterator i = faction.messages.iterator(); i.hasNext();) {
					Message msg = (Message) i.next();

					// check whether the message belongs to one of the selected regions
					if(msg.attributes != null) {
						String regionCoordinate = (String) msg.attributes.get("region");

						if(regionCoordinate != null) {
							ID coordinate = Coordinate.parse(regionCoordinate, ",");

							if(coordinate == null) {
								coordinate = Coordinate.parse(regionCoordinate, " ");
							}

							if(!regions.containsKey(coordinate)) {
								continue;
							}
						}

						// find a valid amount
						if((msg.getMessageType() != null) &&
							   (msg.getMessageType().getSection() != null) &&
							   msg.getMessageType().getSection().equalsIgnoreCase("production")) {
							String value = (String) msg.attributes.get("amount");
							int amount = 0;

							if(value != null) {
								amount = Integer.parseInt(value);
							}

							if(amount != 0) {
								String resource = (String) msg.attributes.get("resource");

								// get the resource
								if(resource == null) {
									// possible a message containing ;herb instead of ;resource
									resource = (String) msg.attributes.get("herb");
								}

								if(resource != null) {
									// find the category
									ItemType itemType = data.rules.getItemType(StringID.create(resource));
									ItemCategory itemCategory = null;

									if(itemType != null) {
										itemCategory = itemType.getCategory();
									}

									if(itemCategory == null) {
										log.info(resource);
									} else {
										// add the data
										Map h = (Map) production.get(itemCategory);

										if(h == null) {
											h = CollectionFactory.createHashtable();
											production.put(itemCategory, h);
										}

										ProductionStats p = (ProductionStats) h.get(resource);

										if(p == null) {
											p = new ProductionStats();
											h.put(resource, p);
										}

										p.totalAmount += amount;

										// try to get the unit that did the production
										value = (String) msg.attributes.get("unit");

										if(value != null) {
											int number = 0;
											number = Integer.parseInt(value);

											UnitID id = UnitID.createUnitID(number);
											Unit u = data.getUnit(id);

											if(u != null) {
												p.units.put(u, new Integer(amount));
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}

		// remember: production maps ItemCategory to Map that maps resource (String) to ProductionStats-Objects
		DefaultMutableTreeNode prodNode = new DefaultMutableTreeNode(getString("node.production"));

		for(Iterator iter = production.keySet().iterator(); iter.hasNext();) {
			ItemCategory iCategory = (ItemCategory) iter.next();
			m = new DefaultMutableTreeNode();
			prodNode.add(m);

			Map h = (Map) production.get(iCategory);
			int totalAmount = 0;

			for(Iterator iterator = h.keySet().iterator(); iterator.hasNext();) {
				String resource = (String) iterator.next();
				ProductionStats stats = (ProductionStats) h.get(resource);
				totalAmount += stats.totalAmount;
				o = new DefaultMutableTreeNode(resource + ": " + stats.totalAmount);
				m.add(o);

				for(Iterator i = stats.units.keySet().iterator(); i.hasNext();) {
					Unit u = (Unit) i.next();
					int amount = ((Integer) stats.units.get(u)).intValue();
					o.add(new DefaultMutableTreeNode(nodeWrapperFactory.createUnitNodeWrapper(u,
																							  amount)));
				}
			}

			m.setUserObject(iCategory.toString() + ": " + totalAmount);
		}

		if(prodNode.getChildCount() > 0) {
			rootNode.add(prodNode);
		}

		treeModel.reload();
		setCursor(Cursor.getDefaultCursor());
	}

	private void showAlliances(Map allies, DefaultMutableTreeNode rootNode) {
		if((allies == null) || (rootNode == null)) {
			return;
		}

		List sortedAllies = CollectionFactory.createLinkedList(allies.values());
		Collections.sort(sortedAllies,
						 new AllianceFactionComparator(new NameComparator(new IDComparator())));

		for(Iterator iter = sortedAllies.iterator(); iter.hasNext();) {
			DefaultMutableTreeNode n = new DefaultMutableTreeNode(iter.next());
			rootNode.add(n);
		}
	}

	private Container getStatPanel() {
		rootNode = new DefaultMutableTreeNode(null);
		treeModel = new DefaultTreeModel(rootNode);
		tree = new CopyTree(treeModel);
		tree.setRootVisible(false);
		tree.addTreeSelectionListener(this);

		CellRenderer tr = new CellRenderer(settings);
		tree.setCellRenderer(tr);

		JScrollPane treeScrollPane = new JScrollPane(tree, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
													 JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		treeScrollPane.setMinimumSize(new Dimension(100, 50));

		JPanel stats = new JPanel(new GridLayout(1, 1));
		stats.add(treeScrollPane);

		return stats;
	}

	// pavkovic 2003.01.28: this is a Map of the default Translations mapped to this class
	// it is called by reflection (we could force the implementation of an interface,
	// this way it is more flexible.)
	// Pls use this mechanism, so the translation files can be created automagically
	// by inspecting all classes.
	private static Map defaultTranslations;

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static synchronized Map getDefaultTranslations() {
		if(defaultTranslations == null) {
			defaultTranslations = CollectionFactory.createHashtable();
			defaultTranslations.put("node.units", "Units: ");
			defaultTranslations.put("node.persons", "Persons: ");
			defaultTranslations.put("node.race", "Race: ");
			defaultTranslations.put("node.magicschool", "Magic school: ");
			defaultTranslations.put("node.banner", "Banner: ");
			defaultTranslations.put("node.e-mail", "E-mail: ");
			defaultTranslations.put("node.trust.privileged", "Trust: privileged");
			defaultTranslations.put("node.trust.allied", "Trust: allied");
			defaultTranslations.put("node.trust.standard", "Trust: neutral");
			defaultTranslations.put("node.trust.enemy", "Trust: hostile");
			defaultTranslations.put("node.score", "Score: ");
			defaultTranslations.put("node.migrants", "Migrants: ");
			defaultTranslations.put("node.alliances", "Alliances");
			defaultTranslations.put("node.groups", "Groups");
			defaultTranslations.put("node.income", "Income: {0} of {1} silver");
			defaultTranslations.put("node.income0", "Work: {0} silver");
			defaultTranslations.put("node.income1", "Entertainment: {0} silver");
			defaultTranslations.put("node.income2", "Tax collection: {0} silver");
			defaultTranslations.put("node.income3", "Trade: {0} silver");
			defaultTranslations.put("node.income4", "Trace tax: {0} silver");
			defaultTranslations.put("node.income5", "Theft: {0} silver");
			defaultTranslations.put("node.income6", "Magic: {0} silver");
			defaultTranslations.put("node.incomewanted", " of {0} silver");
			defaultTranslations.put("node.spentfortrade", "Expenses for Trade: {0} silver");
			defaultTranslations.put("node.buildings", "Buildings");
			defaultTranslations.put("node.ships", "Ships");
			defaultTranslations.put("node.racenameprefix", "Prefix for race name: ");
			defaultTranslations.put("node.skills", "Skills");
			defaultTranslations.put("node.production", "Production");
			defaultTranslations.put("node.otherrace", "Persons of other race");
		}

		return defaultTranslations;
	}

	/**
	 * A little class used to store information about production statistics for a certain resource
	 */
	private class ProductionStats {
		// mapping units who produced the special resource (Unit) to the according amounts (Integer)
		private Map units = CollectionFactory.createHashtable();

		// total amount produced by all units
		private int totalAmount;
	}
}
