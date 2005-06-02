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

/*
 * ArmyStatsPanel.java
 *
 * Created on 4. März 2002, 12:19
 */
package com.eressea.swing;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.eressea.Alliance;
import com.eressea.Faction;
import com.eressea.GameData;
import com.eressea.Island;
import com.eressea.Item;
import com.eressea.Region;
import com.eressea.Rules;
import com.eressea.Skill;
import com.eressea.StringID;
import com.eressea.Unit;
import com.eressea.event.EventDispatcher;
import com.eressea.event.SelectionEvent;
import com.eressea.event.SelectionListener;
import com.eressea.rules.ItemCategory;
import com.eressea.rules.ItemType;
import com.eressea.rules.SkillType;
import com.eressea.swing.tree.CellRenderer;
import com.eressea.swing.tree.CopyTree;
import com.eressea.swing.tree.NodeWrapperFactory;
import com.eressea.swing.tree.SimpleNodeWrapper;
import com.eressea.swing.tree.UnitNodeWrapper;
import com.eressea.util.CollectionFactory;
import com.eressea.util.logging.Logger;

/**
 * DOCUMENT ME!
 *
 * @author Andreas
 * @version
 */
public class ArmyStatsPanel extends InternationalizedDataPanel implements TreeSelectionListener,
																		  SelectionListener
{
	private static final Logger log = Logger.getInstance(ArmyStatsPanel.class);
	protected NodeWrapperFactory factory;
	protected CopyTree tree;
	protected CopyTree tree2;
	protected DefaultMutableTreeNode treeRoot;
	protected DefaultMutableTreeNode tree2Root;
	protected JSplitPane content;
	protected int dividerPos = Integer.MIN_VALUE;
	protected List armies;
	protected Map weapons;
	protected ItemCategory weapon;
	protected ItemCategory front;
	protected ItemCategory back;
	protected ItemCategory armourType;
	protected ItemCategory shieldType;
	protected boolean categorize = true;
	protected List excludeSkills;
	protected List excludeNames;
	protected List excludeCombatStates;
	protected Collection lastSelected;

	/**
	 * Creates new ArmyStatsPanel
	 *
	 * @param ed TODO: DOCUMENT ME!
	 * @param data TODO: DOCUMENT ME!
	 * @param settings TODO: DOCUMENT ME!
	 * @param doCategorize TODO: DOCUMENT ME!
	 */
	public ArmyStatsPanel(EventDispatcher ed, GameData data, Properties settings,
						  boolean doCategorize) {
		this(ed, data, settings, doCategorize, null);
	}

	/**
	 * Creates a new ArmyStatsPanel object.
	 *
	 * @param ed TODO: DOCUMENT ME!
	 * @param data TODO: DOCUMENT ME!
	 * @param settings TODO: DOCUMENT ME!
	 * @param doCategorize TODO: DOCUMENT ME!
	 * @param selRegions TODO: DOCUMENT ME!
	 */
	public ArmyStatsPanel(EventDispatcher ed, GameData data, Properties settings,
						  boolean doCategorize, Collection selRegions) {
		super(ed, data, settings);

		lastSelected = selRegions;

		ed.addSelectionListener(this);

		factory = new NodeWrapperFactory(settings, "EMapOverviewPanel", "Dummy-Factory");

		String dPos = settings.getProperty("ArmyStatsPanel.DividerLoc");

		if((dPos != null) && !dPos.equals("")) {
			try {
				dividerPos = Integer.parseInt(dPos);
			} catch(Exception exc) {
			}
		}

		categorize = doCategorize;

		initTrees(ed);

		createArmies(data, selRegions);
		createTrees();

		setLayout(new java.awt.BorderLayout());
		add(content, java.awt.BorderLayout.CENTER);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param b TODO: DOCUMENT ME!
	 */
	public void setCategorized(boolean b) {
		categorize = b;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param l TODO: DOCUMENT ME!
	 */
	public void setExcludeSkills(List l) {
		excludeSkills = l;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param l TODO: DOCUMENT ME!
	 */
	public void setExcludeNames(List l) {
		excludeNames = l;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param l TODO: DOCUMENT ME!
	 */
	public void setExcludeCombatStates(List l) {
		excludeCombatStates = l;
	}

	protected void updateData(GameData data, Collection regions) {
		armies.clear();
		createArmies(data, regions, categorize); // jump over rule base
		createTrees();
		doUpdate();
		repaint();
	}

	/**
	 * TODO: DOCUMENT ME!
	 */
	public void doUpdate() {
		/** Try to fix the swing bug of a a too small tree */
		javax.swing.plaf.TreeUI ui = tree.getUI();

		if(ui instanceof javax.swing.plaf.basic.BasicTreeUI) {
			javax.swing.plaf.basic.BasicTreeUI ui2 = (javax.swing.plaf.basic.BasicTreeUI) ui;
			int i = ui2.getLeftChildIndent();
			ui2.setLeftChildIndent(100);
			ui2.setLeftChildIndent(i);
		}

		ui = tree2.getUI();

		if(ui instanceof javax.swing.plaf.basic.BasicTreeUI) {
			javax.swing.plaf.basic.BasicTreeUI ui2 = (javax.swing.plaf.basic.BasicTreeUI) ui;
			int i = ui2.getLeftChildIndent();
			ui2.setLeftChildIndent(100);
			ui2.setLeftChildIndent(i);
		}

		if(dividerPos != Integer.MIN_VALUE) {
			content.setDividerLocation(dividerPos);
		} else {
			content.setDividerLocation(0.5);
			dividerPos = content.getDividerLocation();
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param data TODO: DOCUMENT ME!
	 */
	public void recreate(GameData data) {
		dividerPos = content.getDividerLocation();

		createArmies(data, data.equals(this.data) ? lastSelected : null);
		this.data = data;
		createTrees();
		content.setDividerLocation(dividerPos);
	}

	/**
	 * TODO: DOCUMENT ME!
	 */
	public void quit() {
		settings.setProperty("ArmyStatsPanel.DividerLoc", String.valueOf(dividerPos));
	}

	protected void initTrees(EventDispatcher ed) {
		CellRenderer renderer = new CellRenderer(getMagellanContext());

		treeRoot = new DefaultMutableTreeNode();

		DefaultTreeModel dtm = new DefaultTreeModel(treeRoot);
		tree = new CopyTree(dtm);
		tree.setRootVisible(false);
		tree.setCellRenderer(renderer);
		tree.addTreeSelectionListener(this);

		tree2Root = new DefaultMutableTreeNode();
		dtm = new DefaultTreeModel(tree2Root);
		tree2 = new CopyTree(dtm);
		tree2.setRootVisible(false);
		tree2.setCellRenderer(renderer);
		tree2.addTreeSelectionListener(this);

		JSplitPane pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(tree),
										 new JScrollPane(tree2));
		content = pane;
	}

	////////////////////////////
	// T R E E   FILLING Code //
	////////////////////////////
	protected void createTrees() {
		treeRoot.removeAllChildren();
		addList(treeRoot, armies, false);
		((DefaultTreeModel) tree.getModel()).reload();

		tree2Root.removeAllChildren();
		addList(tree2Root, armies, true);
		((DefaultTreeModel) tree2.getModel()).reload();
	}

	protected void addList(DefaultMutableTreeNode root, Collection list, boolean mode) {
		Iterator it = list.iterator();

		while(it.hasNext()) {
			Object obj = it.next();

			if(!mode && obj instanceof IslandArmies) {
				addIslandArmies(root, (IslandArmies) obj);
			} else if(!mode && obj instanceof RegionArmies) {
				addRegionArmies(root, (RegionArmies) obj);
			} else if(mode && obj instanceof WholeArmy) {
				addWholeArmy(root, (WholeArmy) obj);
			}
		}
	}

	protected DefaultMutableTreeNode addIslandArmies(DefaultMutableTreeNode root,
													 IslandArmies armies) {
		DefaultMutableTreeNode islRoot = new DefaultMutableTreeNode(new SimpleNodeWrapper(armies,
																						  "insel"));
		root.add(islRoot);

		Iterator it = armies.armies.iterator();

		while(it.hasNext()) {
			IslandArmy iArmy = (IslandArmy) it.next();
			DefaultMutableTreeNode iRoot = new DefaultMutableTreeNode(new SimpleNodeWrapper(iArmy,
																							getArmyIcon(iArmy.owner)));
			islRoot.add(iRoot);
		}

		it = armies.rarmies.iterator();

		while(it.hasNext()) {
			addRegionArmies(islRoot, (RegionArmies) it.next());
		}

		return islRoot;
	}

	protected DefaultMutableTreeNode addRegionArmies(DefaultMutableTreeNode root,
													 RegionArmies armies) {
		DefaultMutableTreeNode regRoot = new DefaultMutableTreeNode(new SimpleNodeWrapper(armies,
																						  armies.region.getType()
																									   .getID()
																									   .toString() +
																						  "-detail"));
		root.add(regRoot);

		Iterator it = armies.armies.iterator();

		while(it.hasNext()) {
			addArmy(regRoot, (Army) it.next());
		}

		return regRoot;
	}

	protected String getArmyIcon(Faction fac) {
		String icon = "kampfstatus";

		if(fac.isPrivileged()) {
			icon = "alliancestate_basisfaction";
		} else if(data != null) {
			int minTrust = 255;

			for(Iterator iter = data.factions().values().iterator(); iter.hasNext();) {
				Faction f = (Faction) iter.next();

				if(f.isPrivileged()) {
					if((f.allies != null) && f.allies.containsKey(fac.getID())) {
						Alliance a = (Alliance) f.allies.get(fac.getID());
						minTrust &= a.getState();
					} else {
						minTrust = 0;
					}
				}
			}

			icon = "alliancestate_" + String.valueOf(minTrust);
		}

		return icon;
	}

	protected DefaultMutableTreeNode addWholeArmy(DefaultMutableTreeNode root, WholeArmy army) {
		DefaultMutableTreeNode armRoot = new DefaultMutableTreeNode(new SimpleNodeWrapper(army,
																						  getArmyIcon(army.owner)));
		root.add(armRoot);

		Iterator it = army.armies.iterator();

		while(it.hasNext()) {
			Object obj = it.next();

			if(obj instanceof Army) {
				addArmy(armRoot, (Army) obj);
			} else if(obj instanceof IslandArmy) {
				IslandArmy ia = (IslandArmy) obj;
				DefaultMutableTreeNode iRoot = new DefaultMutableTreeNode(new SimpleNodeWrapper(ia,
																								"insel"));
				armRoot.add(iRoot);

				Iterator it2 = ia.armies.iterator();

				while(it2.hasNext()) {
					addArmy(iRoot, (Army) it2.next());
				}
			}
		}

		return armRoot;
	}

	protected void addArmy(DefaultMutableTreeNode root, Army army) {
		DefaultMutableTreeNode armyRoot = new DefaultMutableTreeNode(new SimpleNodeWrapper(army,
																						   army.shortString
																						   ? (army.region.getType()
																										 .getID()
																										 .toString() +
																						   "-detail")
																						   : getArmyIcon(army.owner)));
		root.add(armyRoot);

		for(int i = 0; i < 2; i++) {
			if(army.warLines[i] != null) {
				WarLine wl = army.warLines[i];
				DefaultMutableTreeNode lineRoot = new DefaultMutableTreeNode(wl);
				armyRoot.add(lineRoot);

				if(wl.categorized) {
					Iterator it = wl.groups.iterator();

					while(it.hasNext()) {
						WeaponGroup wg = (WeaponGroup) it.next();

						if(wg.units.size() == 1) {
							PartUnit u = (PartUnit) wg.units.iterator().next();
							lineRoot.add(createNodeWrapper(u));
						} else {
							Object icon = null;

							if(wg.weapon != null) {
								icon = "items/" + wg.weapon.getIconName();
							} else {
								icon = "warnung";
							}

							DefaultMutableTreeNode groupRoot = new DefaultMutableTreeNode(new SimpleNodeWrapper(wg,
																												icon));
							lineRoot.add(groupRoot);

							Iterator it2 = wg.units.iterator();

							while(it2.hasNext()) {
								PartUnit u = (PartUnit) it2.next();
								groupRoot.add(createNodeWrapper(u));
							}
						}
					}
				} else {
					addUnits(lineRoot, wl.groups);
				}
			}
		}
	}

	protected void addUnits(DefaultMutableTreeNode root, Collection units) {
		if(units != null) {
			Iterator it = units.iterator();

			while(it.hasNext()) {
				Unit u = (Unit) it.next();
				root.add(new DefaultMutableTreeNode(factory.createUnitNodeWrapper(u, u.persons)));
			}
		}
	}

	protected Object getIconObject(PartUnit unit) {
		int i = 0;

		if(unit.weapon != null) {
			i++;
		}

		if(unit.armour != null) {
			i++;
		}

		if(unit.shield != null) {
			i++;
		}

		if(i == 0) {
			return "warnung";
		}

		if(i == 1) {
			if(unit.weapon != null) {
				return "items/" + unit.weapon.getIconName();
			}

			if(unit.armour != null) {
				return "items/" + unit.armour.getIconName();
			}

			if(unit.shield != null) {
				return "items/" + unit.shield.getIconName();
			}
		}

		Collection col = CollectionFactory.createArrayList(i);

		if(unit.weapon != null) {
			col.add("items/" + unit.weapon.getIconName());
		}

		if(unit.armour != null) {
			col.add("items/" + unit.armour.getIconName());
		}

		if(unit.shield != null) {
			col.add("items/" + unit.shield.getIconName());
		}

		return col;
	}

	protected DefaultMutableTreeNode createNodeWrapper(PartUnit partUnit) {
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(new SimpleNodeWrapper(partUnit,
																					   getIconObject(partUnit)));
		node.add(new DefaultMutableTreeNode(factory.createUnitNodeWrapper(partUnit.parent,
																		  partUnit.parent.persons)));

		return node;
	}

	//////////////////////////////
	// A R M Y    CREATION CODE //
	//////////////////////////////
	protected void createArmies(GameData data) {
		createArmies(data, null);
	}

	protected void createArmies(GameData data, Collection regions) {
		createRuleBase(data);

		if(armies == null) {
			armies = CollectionFactory.createLinkedList();
		} else {
			armies.clear();
		}

		this.data = data;
		createArmies(data, regions, categorize);
	}

	protected void createArmies(GameData data, Collection regions, boolean cat) {
		Comparator nameComp = new com.eressea.util.comparator.NameComparator(null);
		Comparator compare = com.eressea.util.comparator.FactionTrustComparator.DEFAULT_COMPARATOR;

		List allRegions = null;

		if((regions != null) && (regions.size() > 0)) {
			allRegions = CollectionFactory.createLinkedList(regions);
		} else {
			allRegions = CollectionFactory.createLinkedList(data.regions().values());
		}

		List islList = CollectionFactory.createLinkedList(data.islands().values());
		Collections.sort(islList, nameComp);

		Iterator islIt = islList.iterator();
		Map armyMap = CollectionFactory.createHashMap();
		Map facMap = CollectionFactory.createHashMap();

		if(islIt != null) {
			while(islIt.hasNext()) {
				armyMap.clear();

				Island isl = (Island) islIt.next();
				List iregions = CollectionFactory.createLinkedList(isl.regions());
				Collections.sort(iregions, nameComp);

				Iterator regIt = iregions.iterator();
				IslandArmies islArmies = new IslandArmies(isl);

				while(regIt.hasNext()) {
					Region reg = (Region) regIt.next();
					allRegions.remove(reg);

					Collection col = createRegionArmies(reg, cat);
					Iterator colIt = col.iterator();
					RegionArmies regArmies = new RegionArmies(reg);
					Map map = CollectionFactory.createHashMap();

					while(colIt.hasNext()) {
						Army army = (Army) colIt.next();
						map.put(army.owner, army);
					}

					List list = CollectionFactory.createLinkedList(map.keySet());
					Collections.sort(list, compare);
					colIt = list.iterator();

					while(colIt.hasNext()) {
						Army army = (Army) map.get(colIt.next());
						regArmies.addArmy(army);

						Faction fac = army.owner;

						if(!armyMap.containsKey(fac)) {
							armyMap.put(fac, new IslandArmy(fac, isl));
						}

						IslandArmy islArmy = (IslandArmy) armyMap.get(fac);
						islArmy.addArmy(army);
					}

					if(regArmies.men > 0) {
						islArmies.rarmies.add(regArmies);
					}
				}

				if(armyMap.size() > 0) {
					List list = CollectionFactory.createLinkedList(armyMap.keySet());
					Collections.sort(list, compare);

					Iterator iarmies = list.iterator();

					while(iarmies.hasNext()) {
						IslandArmy ia = (IslandArmy) armyMap.get(iarmies.next());
						islArmies.addArmy(ia);

						if(!facMap.containsKey(ia.owner)) {
							facMap.put(ia.owner, CollectionFactory.createLinkedList());
						}

						((Collection) facMap.get(ia.owner)).add(ia);
					}

					armies.add(islArmies);
				}
			}
		}

		ArmyComparator ac = new ArmyComparator();
		Collections.sort(armies, ac);

		// rest
		Collections.sort(allRegions, nameComp);

		Iterator regIt = allRegions.iterator();
		List allRegArmies = CollectionFactory.createLinkedList();

		while(regIt.hasNext()) {
			Region reg = (Region) regIt.next();
			Collection col = createRegionArmies(reg, cat);

			if(col.size() > 0) {
				RegionArmies regArmies = new RegionArmies(reg);
				Iterator colIt = col.iterator();
				Map map = CollectionFactory.createHashMap();

				while(colIt.hasNext()) {
					Army army = (Army) colIt.next();
					map.put(army.owner, army);
				}

				List list = CollectionFactory.createLinkedList(map.keySet());
				Collections.sort(list, compare);
				colIt = list.iterator();

				while(colIt.hasNext()) {
					Army army = (Army) map.get(colIt.next());
					regArmies.addArmy(army);

					if(!facMap.containsKey(army.owner)) {
						facMap.put(army.owner, CollectionFactory.createLinkedList());
					}

					((Collection) facMap.get(army.owner)).add(army);
				}

				allRegArmies.add(regArmies);
			}
		}

		Collections.sort(allRegArmies, ac);
		armies.addAll(allRegArmies);

		// now create the WholeArmy objects
		if(facMap.size() > 0) {
			List wholeList = CollectionFactory.createLinkedList(facMap.keySet());
			Collections.sort(wholeList, compare);

			Iterator facIt = wholeList.iterator();

			while(facIt.hasNext()) {
				Faction fac = (Faction) facIt.next();
				WholeArmy wa = new WholeArmy(fac);
				Iterator facIt2 = ((Collection) facMap.get(fac)).iterator();

				while(facIt2.hasNext()) {
					Object o = facIt2.next();

					if(o instanceof IslandArmy) {
						IslandArmy ia = (IslandArmy) o;
						IslandArmy ia2 = new IslandArmy(ia.owner, ia.island);
						Iterator facIt3 = ia.armies.iterator();

						while(facIt3.hasNext()) {
							Army army = (Army) facIt3.next();
							army = army.copy();
							army.shortString = true;
							ia2.addArmy(army);
						}

						ia2.shortString = false;
						wa.addIslandArmy(ia2);
					} else if(o instanceof Army) {
						Army army = (Army) o;
						army = army.copy();
						army.shortString = true;
						wa.addArmy(army);
					}
				}

				armies.add(wa);
			}
		}
	}

	protected void createRuleBase(GameData data) {
		if(weapons == null) {
			weapons = CollectionFactory.createHashMap();
		} else {
			weapons.clear();
		}

		Rules r = data.rules;
		weapon = r.getItemCategory(StringID.create("weapons"), false);

		if(weapon == null) {
			return;
		}

		front = r.getItemCategory(StringID.create("front weapons"), false);
		back = r.getItemCategory(StringID.create("distance weapons"), false);
		armourType = r.getItemCategory(StringID.create("armour"), false);
		shieldType = r.getItemCategory(StringID.create("shield"), false);

		for(Iterator iter = r.getItemTypeIterator(); iter.hasNext();) {
			ItemType it = (ItemType) iter.next();

			if((it.getCategory() != null) && it.getCategory().isDescendant(weapon)) {
				Skill sk = it.getUseSkill();

				if(!weapons.containsKey(sk)) {
					weapons.put(sk, CollectionFactory.createLinkedList());
				}

				Collection col = (Collection) weapons.get(sk);
				col.add(it);
			}
		}
	}

	protected Collection createRegionArmies(Region r, boolean cat) {
		Collection col = CollectionFactory.createLinkedList();

		if(r.units().size() == 0) {
			return col;
		}

		Iterator unitIt = r.units().iterator();
		Map facMap = CollectionFactory.createHashMap();
		Map unitMap = null;
		Collection unitSkills = null;
		Collection armour = null;
		Collection nonSkillWeapons = null;

		while(unitIt.hasNext()) {
			Unit unit = (Unit) unitIt.next();

			if(excludeSkills != null) {
				boolean doContinue = true;
				Iterator it = excludeSkills.iterator();

				while(doContinue && it.hasNext()) {
					SkillType sk = (SkillType) it.next();
					Skill skill = unit.getSkill(sk);

					if(skill != null /* && skill.getLevel()>0) */   ) {
						doContinue = false;
					}
				}

				if(!doContinue) {
					continue;
				}
			}

			if((unit.getName() != null) && (excludeNames != null)) {
				boolean doContinue = true;
				Iterator it = excludeNames.iterator();
				String name = unit.getName();

				while(doContinue && it.hasNext()) {
					String st = (String) it.next();

					if(name.indexOf(st) != -1) {
						doContinue = false;
					}
				}

				if(!doContinue) {
					continue;
				}
			}

			if(unit.getFaction().isPrivileged() && (excludeCombatStates != null)) {
				boolean doContinue = true;
				Iterator it = excludeCombatStates.iterator();

				while(doContinue && it.hasNext()) {
					Integer i = (Integer) it.next();
					doContinue = i.intValue() != unit.combatStatus;
				}

				if(!doContinue) {
					continue;
				}
			}

			boolean inFront = unit.combatStatus < 2;

			unitMap = getSkillsWithWeapons(unit, unitSkills = getWeaponSkills(unit, unitSkills),
										   unitMap);
			nonSkillWeapons = getNonSkillWeapons(unit, unitMap.keySet(), nonSkillWeapons);
			armour = getArmour(unit, armour);

			if(unit.getFaction().trustLevel <= Faction.TL_PRIVILEGED) {
				if((unitMap.size() == 0) && (nonSkillWeapons.size() <= 1) && (armour.size() == 0)) {
					continue;
				}

				if(unitMap.size() == 0) {
					inFront = guessCombatState(nonSkillWeapons) == 0;
				}
			}

			if(!facMap.containsKey(unit.getFaction())) {
				facMap.put(unit.getFaction(), new Army(unit.getFaction(), r));
			}

			Army army = (Army) facMap.get(unit.getFaction());

			if(cat) {
				Collection col2 = unitMap.keySet();
				int persons = unit.persons;
				int line = inFront ? 0 : 1;
				int maxSkillLevel = 0;
				Skill maxSkill = getHighestSkill(unit, col2);

				if(maxSkill != null) {
					maxSkillLevel = maxSkill.getLevel();
				}

				while(persons > 0) {
					if(col2.size() > 0) {
						maxSkill = getHighestSkill(unit, col2);

						Collection col3 = (Collection) unitMap.get(maxSkill);
						col2.remove(maxSkill);

						Iterator itemIt = col3.iterator();

						while((persons > 0) && itemIt.hasNext()) {
							Item item = (Item) itemIt.next();
							int amount = Math.min(persons, item.getAmount());
							persons -= amount;
							addArmoured(unit, amount, maxSkill.getLevel(), army, line,
										item.getItemType(), null, armour, false, true);
						}
					} else {
						Iterator itemIt = nonSkillWeapons.iterator();

						while((persons > 0) && itemIt.hasNext()) {
							Item item = (Item) itemIt.next();
							int amount = 0;

							if(item != null) {
								amount = Math.min(persons, item.getAmount());
							} else {
								amount = persons;
							}

							persons -= amount;
							addArmoured(unit, amount, maxSkillLevel - 2, army, line,
										(item != null) ? item.getItemType() : null, null, armour,
										false, false);
						}
					}
				}
			} else { // uncategorized

				int persons = unit.persons;
				int unarmed = persons;
				Iterator weaponIt = unitMap.values().iterator();

				while((unarmed > 0) && weaponIt.hasNext()) {
					Collection col2 = (Collection) weaponIt.next();
					Iterator colIt = col2.iterator();

					while((unarmed > 0) && colIt.hasNext()) {
						Item item = (Item) colIt.next();
						unarmed -= item.getAmount();

						if(unarmed < 0) {
							unarmed = 0;
						}
					}
				}

				if((unarmed > 0) && (nonSkillWeapons.size() > 1)) {
					weaponIt = nonSkillWeapons.iterator();

					while((unarmed > 0) && weaponIt.hasNext()) {
						Item item = (Item) weaponIt.next();

						if(item != null) {
							unarmed -= item.getAmount();

							if(unarmed < 0) {
								unarmed = 0;
							}
						}
					}
				}

				addUnit(unit, persons, unarmed, army, inFront ? 0 : 1);
			}
		}

		if(facMap.size() > 0) {
			col.addAll(facMap.values());
		}

		facMap = null;

		return col;
	}

	protected int guessCombatState(Collection weapons) {
		int guess = 0; // guess front

		// now search for distance weapons
		if((weapons != null) && (back != null)) {
			Iterator it = weapons.iterator();

			while(it.hasNext()) {
				Item item = (Item) it.next();

				if(item != null) {
					if((item.getItemType().getCategory() != null) &&
						   item.getItemType().getCategory().isDescendant(back)) {
						guess = 2; // now guess back
					}
				}
			}
		}

		return guess;
	}

	protected WarLine getWarLine(Army army, int line, boolean cat) {
		if(army.warLines[line] == null) {
			army.setWarLine(new WarLine(line));
			army.warLines[line].categorized = cat;
		}

		return army.warLines[line];
	}

	protected boolean addArmoured(Unit unit, int amount, int skill, Army army, int line,
								  ItemType weapon, ItemType armour, Collection armourCol,
								  boolean shield, boolean hasSkill) {
		Iterator aIt = armourCol.iterator();

		boolean ret = false; // something deleted within, need a new iterator

		while((amount > 0) && aIt.hasNext()) {
			Item aItem = (Item) aIt.next();

			if(shield) {
				if(aItem.getItemType().getCategory().isDescendant(shieldType)) {
					int aAmount = aItem.getAmount();

					if(aAmount > amount) {
						aItem.setAmount(aAmount - amount);
						addPartUnit(unit, amount, skill, army, line, weapon, armour,
									aItem.getItemType(), hasSkill, true);
						amount = 0;
					} else if(aAmount == amount) {
						aIt.remove();
						addPartUnit(unit, amount, skill, army, line, weapon, armour,
									aItem.getItemType(), hasSkill, true);
						amount = 0;
						ret = true;
					} else {
						addPartUnit(unit, aAmount, skill, army, line, weapon, armour,
									aItem.getItemType(), hasSkill, true);
						aIt.remove();
						amount -= aAmount;
						ret = true;
					}
				}
			} else {
				if(!aItem.getItemType().getCategory().isDescendant(shieldType)) {
					int aAmount = aItem.getAmount();

					if(aAmount > amount) {
						aItem.setAmount(aAmount - amount);

						if(addArmoured(unit, amount, skill, army, line, weapon,
										   aItem.getItemType(), armourCol, true, hasSkill)) {
							aIt = armourCol.iterator();
						}

						amount = 0;
					} else if(aAmount == amount) {
						aIt.remove();

						if(addArmoured(unit, amount, skill, army, line, weapon,
										   aItem.getItemType(), armourCol, true, hasSkill)) {
							aIt = armourCol.iterator();
						}

						amount = 0;
					} else {
						aIt.remove();
						amount -= aAmount;

						if(addArmoured(unit, aAmount, skill, army, line, weapon,
										   aItem.getItemType(), armourCol, true, hasSkill)) {
							aIt = armourCol.iterator();
						}
					}
				}
			}
		}

		if(amount > 0) {
			if(shield) {
				addPartUnit(unit, amount, skill, army, line, weapon, armour, null, hasSkill, true);
			} else {
				addArmoured(unit, amount, skill, army, line, weapon, null, armourCol, true, hasSkill);
			}
		}

		return ret;
	}

	protected void addPartUnit(Unit full, int persons, int skill, Army army, int line,
							   ItemType weapon, ItemType armour, ItemType shield, boolean cat) {
		addPartUnit(full, persons, skill, army, line, weapon, armour, shield, true, cat);
	}

	protected void addPartUnit(Unit full, int persons, int skill, Army army, int line,
							   ItemType weapon, ItemType armour, ItemType shield, boolean hasSkill,
							   boolean cat) {
		WarLine wl = getWarLine(army, line, cat);
		WeaponGroup wg = getWeaponGroup(wl, weapon);
		wg.addUnit(new PartUnit(full, weapon, persons, skill, armour, shield, hasSkill));
	}

	protected void addUnit(Unit unit, int persons, int unarmed, Army army, int line) {
		WarLine wl = getWarLine(army, line, false);
		wl.addUnit(unit, persons, unarmed);
	}

	protected Collection getNonSkillWeapons(Unit unit, Collection used, Collection col) {
		if(col == null) {
			col = CollectionFactory.createLinkedList();
		} else {
			col.clear();
		}

		Iterator it = unit.getItems().iterator();

		while(it.hasNext()) {
			Item item = (Item) it.next();

			if((item.getItemType().getCategory() != null) &&
				   item.getItemType().getCategory().isDescendant(weapon)) {
				col.add(item);
			}
		}

		if(used != null) {
			it = used.iterator();

			while((col.size() > 0) && it.hasNext()) {
				SkillType st = ((Skill) it.next()).getSkillType();
				Iterator it2 = col.iterator();

				while(it2.hasNext()) {
					Item item = (Item) it2.next();

					if((item.getItemType().getUseSkill() != null) &&
						   st.equals(item.getItemType().getUseSkill().getSkillType())) {
						it2.remove();
					}
				}
			}
		}

		col.add(null);

		return col;
	}

	protected Collection getArmour(Unit unit, Collection col) {
		if(col == null) {
			col = CollectionFactory.createLinkedList();
		} else {
			col.clear();
		}

		if(armourType == null) {
			return col;
		}

		Iterator it = unit.getItems().iterator();

		while(it.hasNext()) {
			Item item = (Item) it.next();

			if((item.getItemType().getCategory() != null) &&
				   item.getItemType().getCategory().isDescendant(armourType)) {
				col.add(new Item(item.getItemType(), item.getAmount()));
			}
		}

		return col;
	}

	protected WeaponGroup getWeaponGroup(WarLine wl, ItemType weapon) {
		Iterator it = wl.groups.iterator();

		while(it.hasNext()) {
			WeaponGroup wg = (WeaponGroup) it.next();

			if(wg.weapon == weapon) {
				return wg;
			}
		}

		WeaponGroup wg = new WeaponGroup(weapon);
		wl.addGroup(wg);

		return wg;
	}

	protected Collection getWeaponSkills(Unit unit, Collection col) {
		if(col == null) {
			col = CollectionFactory.createLinkedList();
		} else {
			col.clear();
		}

		Iterator it = weapons.keySet().iterator();

		while(it.hasNext()) {
			Skill sk = (Skill) it.next();

			if(sk != null) {
				Skill usk = unit.getSkill(sk.getSkillType());

				if((usk != null) && (usk.getLevel() >= sk.getLevel())) {
					col.add(usk);
				}
			}
		}

		return col;
	}

	protected Collection getWeaponsForSkill(Unit unit, Skill skill, Collection col) {
		if(col == null) {
			col = CollectionFactory.createLinkedList();
		} else {
			col.clear();
		}

		Collection col2 = null;
		Iterator it = weapons.keySet().iterator();

		while(it.hasNext()) {
			Skill sk = (Skill) it.next();

			if(sk.getSkillType().equals(skill.getSkillType()) &&
				   (skill.getLevel() >= sk.getLevel())) {
				if(col2 == null) {
					col2 = (Collection) weapons.get(sk);
				} else if(weapons.containsValue(col2)) {
					Collection col3 = CollectionFactory.createLinkedList(col2);
					col2 = col3;
					col2.addAll((Collection) weapons.get(sk));
				} else {
					col2.addAll((Collection) weapons.get(sk));
				}
			}
		}

		if(col2 == null) {
			log.error("No weapons for skill " + skill);

			return col;
		}

		it = col2.iterator();

		while(it.hasNext()) {
			ItemType type = (ItemType) it.next();

			if(unit.getItem(type) != null) {
				col.add(unit.getItem(type));
			}
		}

		return col;
	}

	protected Map getSkillsWithWeapons(Unit unit, Collection skills, Map map) {
		Collection col = null;
		Iterator it = skills.iterator();

		if(map == null) {
			map = CollectionFactory.createHashMap();
		} else {
			map.clear();
		}

		while(it.hasNext()) {
			Skill sk = (Skill) it.next();
			col = getWeaponsForSkill(unit, sk, null);

			if(col.size() > 0) {
				map.put(sk, col);
			}
		}

		return map;
	}

	protected Skill getHighestSkill(Unit unit, Collection col) {
		Skill maxSkill = null;
		int maxValue = -1;
		Iterator it = col.iterator();

		while(it.hasNext()) {
			Skill sk = (Skill) it.next();

			if(sk.getLevel() > maxValue) {
				maxValue = sk.getLevel();
				maxSkill = sk;
			}
		}

		return maxSkill;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param treeSelectionEvent TODO: DOCUMENT ME!
	 */
	public void valueChanged(javax.swing.event.TreeSelectionEvent treeSelectionEvent) {
		Object o = ((JTree) treeSelectionEvent.getSource()).getLastSelectedPathComponent();

		if((o != null) && (o instanceof DefaultMutableTreeNode)) {
			Object o2 = ((DefaultMutableTreeNode) o).getUserObject();

			if(o2 instanceof UnitNodeWrapper) {
				dispatcher.fire(new SelectionEvent(this, null, ((UnitNodeWrapper) o2).getUnit()));
			} else if(o2 instanceof SimpleNodeWrapper) {
				Object o3 = ((SimpleNodeWrapper) o2).getObject();

				if(o3 instanceof PartUnit) {
					dispatcher.fire(new SelectionEvent(this, null, ((PartUnit) o3).parent));
				} else if(o3 instanceof WeaponGroup) {
					fireWeaponGroupSelection((WeaponGroup) o3);
				} else if(o3 instanceof WarLine) {
					fireWareLineSelection((WarLine) o3);
				} else if(o3 instanceof Army) {
					fireArmySelection((Army) o3);
				} else if(o3 instanceof RegionArmies) {
					dispatcher.fire(new SelectionEvent(this, null, ((RegionArmies) o3).region));
				} else if(o3 instanceof IslandArmy) {
					dispatcher.fire(new SelectionEvent(this, null, ((IslandArmy) o3).island));
				} else if(o3 instanceof IslandArmies) {
					dispatcher.fire(new SelectionEvent(this, null, ((IslandArmies) o3).island));
				}
			} else if(o2 instanceof WarLine) {
				fireWareLineSelection((WarLine) o2);
			}
		}
	}

	protected Set getWeaponGroupUnits(WeaponGroup g, Set set) {
		if(set == null) {
			set = CollectionFactory.createHashSet();
		}

		Iterator it = g.units.iterator();

		while(it.hasNext()) {
			set.add(((PartUnit) it.next()).parent);
		}

		return set;
	}

	protected void fireWeaponGroupSelection(WeaponGroup w) {
		Set s = getWeaponGroupUnits(w, null);
		Object o = null;

		if(s.size() > 0) {
			o = s.iterator().next();
		}

		dispatcher.fire(new SelectionEvent(this, s, o));
	}

	protected Set getWarLineUnits(WarLine w, Set set) {
		if(set == null) {
			set = CollectionFactory.createHashSet();
		}

		if(w.categorized) {
			Iterator it = w.groups.iterator();

			while(it.hasNext()) {
				getWeaponGroupUnits((WeaponGroup) it.next(), set);
			}
		} else {
			set.addAll(w.groups);
		}

		return set;
	}

	protected void fireWareLineSelection(WarLine w) {
		Set s = getWarLineUnits(w, null);
		Object o = null;

		if(s.size() > 0) {
			o = s.iterator().next();
		}

		dispatcher.fire(new SelectionEvent(this, s, o));
	}

	protected void fireArmySelection(Army a) {
		Set set = CollectionFactory.createHashSet();

		for(int i = 0; i < 2; i++) {
			if(a.warLines[i] != null) {
				set = getWarLineUnits(a.warLines[i], set);
			}
		}

		dispatcher.fire(new SelectionEvent(this, set, a.region));
	}

	/**
	 * Invoked when different objects are activated or selected.
	 *
	 * @param e TODO: DOCUMENT ME!
	 */
	public void selectionChanged(SelectionEvent e) {
		if((e.getSource() != this) && (e.getSelectionType() == SelectionEvent.ST_REGIONS)) {
			lastSelected = e.getSelectedObjects();
			updateData(data, e.getSelectedObjects());
		}
	}

	protected class PartUnit {
		protected ItemType weapon;
		protected ItemType armour;
		protected ItemType shield;
		protected Unit parent;
		protected int persons = 0;
		protected int skill = 0;
		protected boolean hasSkill = true;

		/**
		 * Creates a new PartUnit object.
		 *
		 * @param p TODO: DOCUMENT ME!
		 * @param w TODO: DOCUMENT ME!
		 * @param persons TODO: DOCUMENT ME!
		 * @param s TODO: DOCUMENT ME!
		 * @param a TODO: DOCUMENT ME!
		 * @param sh TODO: DOCUMENT ME!
		 * @param has TODO: DOCUMENT ME!
		 */
		public PartUnit(Unit p, ItemType w, int persons, int s, ItemType a, ItemType sh, boolean has) {
			parent = p;
			weapon = w;
			shield = sh;
			this.persons = persons;
			skill = s;
			hasSkill = has;
			armour = a;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public String toString() {
			StringBuffer buf = new StringBuffer();
			buf.append(persons);
			buf.append(' ');
			buf.append(getString("soldiersofunit"));
			buf.append(' ');
			buf.append(parent.getName());

			if(weapon != null) {
				buf.append(' ');
				buf.append(getString("with"));
				buf.append(' ');
				buf.append(weapon.getName());
			} else {
				buf.append(", ");
				buf.append(getString("withoutweapon"));
			}

			if((armour != null) || (shield != null)) {
				buf.append(", ");

				if(armour != null) {
					buf.append(armour.getName());

					if(shield != null) {
						buf.append(' ');
						buf.append(getString("and"));
						buf.append(' ');
						buf.append(shield.getName());
					}
				} else {
					buf.append(shield.getName());
				}
			}

			if(parent.skills != null) {
				buf.append(", ");
				buf.append(getString("skill"));
				buf.append(' ');

				if(skill > 0) {
					buf.append(skill);
				} else {
					buf.append(getString("missing"));
				}

				if(!hasSkill) {
					buf.append('(');
					buf.append(getString("wrongskill"));
					buf.append(')');
				}
			}

			return buf.toString();
		}
	}

	protected class WeaponGroup {
		protected ItemType weapon;
		protected List units;

		/**
		 * Creates a new WeaponGroup object.
		 *
		 * @param w TODO: DOCUMENT ME!
		 */
		public WeaponGroup(ItemType w) {
			weapon = w;
			units = CollectionFactory.createLinkedList();
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param p TODO: DOCUMENT ME!
		 */
		public void addUnit(PartUnit p) {
			units.add(p);
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public int getMen() {
			int men = 0;
			Iterator it = units.iterator();

			while(it.hasNext()) {
				PartUnit pu = (PartUnit) it.next();
				men += pu.persons;
			}

			return men;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public String toString() {
			StringBuffer buf = new StringBuffer();
			buf.append(getMen());
			buf.append(' ');
			buf.append(getString("soldiers"));

			if(weapon != null) {
				buf.append(' ');
				buf.append(getString("with"));
				buf.append(' ');
				buf.append(weapon.getName());
			} else {
				buf.append(", ");
				buf.append(getString("withoutweapon"));
			}

			return buf.toString();
		}
	}

	protected class WarLine {
		protected int lineType;
		protected List groups;
		protected boolean categorized = true;
		protected int men = 0;
		protected int unarmed = 0;

		/**
		 * Creates a new WarLine object.
		 *
		 * @param lt TODO: DOCUMENT ME!
		 */
		public WarLine(int lt) {
			groups = CollectionFactory.createLinkedList();
			lineType = lt;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param w TODO: DOCUMENT ME!
		 */
		public void addGroup(WeaponGroup w) {
			groups.add(w);
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param u TODO: DOCUMENT ME!
		 * @param persons TODO: DOCUMENT ME!
		 * @param unarmed TODO: DOCUMENT ME!
		 */
		public void addUnit(Unit u, int persons, int unarmed) {
			groups.add(u);
			men += persons;
			this.unarmed += unarmed;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public int getMen() {
			if(categorized) {
				int men = 0;
				Iterator it = groups.iterator();

				while(it.hasNext()) {
					WeaponGroup wg = (WeaponGroup) it.next();
					men += wg.getMen();
				}

				return men;
			} else {
				return men;
			}
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public int getUnarmed() {
			if(categorized) {
				int unarmed = 0;
				Iterator it = groups.iterator();

				while(it.hasNext()) {
					WeaponGroup wg = (WeaponGroup) it.next();

					if(wg.weapon == null) {
						unarmed += wg.getMen();
					}
				}

				return unarmed;
			} else {
				return unarmed;
			}
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public String toString() {
			StringBuffer buf = new StringBuffer();

			if(lineType == 0) {
				buf.append(getString("frontline"));
			} else {
				buf.append(getString("backline"));
			}

			buf.append(", ");
			buf.append(getMen());
			buf.append(' ');
			buf.append(getString("soldiers"));

			int unarmed = getUnarmed();

			if(unarmed > 0) {
				buf.append('(');
				buf.append(unarmed);
				buf.append(' ');
				buf.append(getString("withoutweapon"));
				buf.append(')');
			}

			return buf.toString();
		}
	}

	protected class Army {
		protected WarLine warLines[];
		protected Faction owner;
		protected Region region;
		protected boolean shortString = false;

		/**
		 * Creates a new Army object.
		 *
		 * @param o TODO: DOCUMENT ME!
		 * @param r TODO: DOCUMENT ME!
		 */
		public Army(Faction o, Region r) {
			owner = o;
			region = r;
			warLines = new WarLine[2];
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param w TODO: DOCUMENT ME!
		 */
		public void setWarLine(WarLine w) {
			warLines[w.lineType] = w;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public int getMen() {
			int men = 0;

			if(warLines[0] != null) {
				men += warLines[0].getMen();
			}

			if(warLines[1] != null) {
				men += warLines[1].getMen();
			}

			return men;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public int getUnarmed() {
			int unarmed = 0;

			if(warLines[0] != null) {
				unarmed += warLines[0].getUnarmed();
			}

			if(warLines[1] != null) {
				unarmed += warLines[1].getUnarmed();
			}

			return unarmed;
		}

		protected Army copy() {
			Army a = new Army(owner, region);
			a.warLines = warLines;

			return a;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public String toString() {
			StringBuffer buf = new StringBuffer();

			if(!shortString) {
				buf.append(owner.toString());
			} else {
				buf.append(region.toString());
			}

			buf.append(": ");
			buf.append(getMen());
			buf.append(' ');
			buf.append(getString("soldiers"));

			int unarmed = getUnarmed();

			if(unarmed > 0) {
				buf.append('(');
				buf.append(unarmed);
				buf.append(' ');
				buf.append(getString("withoutweapon"));
				buf.append(')');
			}

			return buf.toString();
		}
	}

	protected class RegionArmies {
		protected Region region;
		protected List armies;
		protected int men = 0;
		protected int unarmed = 0;

		/**
		 * Creates a new RegionArmies object.
		 *
		 * @param r TODO: DOCUMENT ME!
		 */
		public RegionArmies(Region r) {
			region = r;
			armies = CollectionFactory.createLinkedList();
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param a TODO: DOCUMENT ME!
		 */
		public void addArmy(Army a) {
			armies.add(a);
			men += a.getMen();
			unarmed += a.getUnarmed();
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public String toString() {
			StringBuffer buf = new StringBuffer(region.toString());
			buf.append('(');
			buf.append(men);
			buf.append(' ');
			buf.append(getString("soldiers"));

			if(unarmed > 0) {
				buf.append(", ");
				buf.append(unarmed);
				buf.append(' ');
				buf.append(getString("withoutweapon"));
			}

			buf.append(')');

			return buf.toString();
		}
	}

	protected class IslandArmy {
		protected Faction owner;
		protected Island island;
		protected List armies;
		protected boolean shortString = true;
		protected int men = 0;
		protected int unarmed = 0;

		/**
		 * Creates a new IslandArmy object.
		 *
		 * @param o TODO: DOCUMENT ME!
		 * @param i TODO: DOCUMENT ME!
		 */
		public IslandArmy(Faction o, Island i) {
			owner = o;
			island = i;
			armies = CollectionFactory.createLinkedList();
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param a TODO: DOCUMENT ME!
		 */
		public void addArmy(Army a) {
			armies.add(a);
			men += a.getMen();
			unarmed += a.getUnarmed();
		}

		protected IslandArmy copy() {
			IslandArmy o = new IslandArmy(owner, island);
			o.armies = armies;
			o.men = men;
			o.unarmed = unarmed;

			return o;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public String toString() {
			StringBuffer buf = new StringBuffer();

			if(!shortString) {
				buf.append(island.getName());
			} else {
				buf.append(owner.toString());
			}

			buf.append(": ");
			buf.append(men);
			buf.append(' ');
			buf.append(getString("soldiers"));

			if(unarmed > 0) {
				buf.append('(');
				buf.append(unarmed);
				buf.append(' ');
				buf.append(getString("withoutweapon"));
				buf.append(')');
			}

			return buf.toString();
		}
	}

	protected class IslandArmies {
		protected Island island;
		protected List armies;
		protected List rarmies;
		protected int men = 0;
		protected int unarmed = 0;

		/**
		 * Creates a new IslandArmies object.
		 *
		 * @param i TODO: DOCUMENT ME!
		 */
		public IslandArmies(Island i) {
			island = i;
			armies = CollectionFactory.createLinkedList();
			rarmies = CollectionFactory.createLinkedList();
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param army TODO: DOCUMENT ME!
		 */
		public void addArmy(IslandArmy army) {
			armies.add(army);
			men += army.men;
			unarmed += army.unarmed;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public String toString() {
			StringBuffer buf = new StringBuffer(island.getName());
			buf.append(" (");
			buf.append(men);
			buf.append(getString("soldiers"));

			if(unarmed > 0) {
				buf.append(", ");
				buf.append(unarmed);
				buf.append(' ');
				buf.append(getString("withoutweapon"));
			}

			buf.append(')');

			return buf.toString();
		}
	}

	protected class WholeArmy {
		protected Faction owner;
		protected List armies;
		protected int men = 0;
		protected int unarmed = 0;

		/**
		 * Creates a new WholeArmy object.
		 *
		 * @param o TODO: DOCUMENT ME!
		 */
		public WholeArmy(Faction o) {
			owner = o;
			armies = CollectionFactory.createLinkedList();
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param a TODO: DOCUMENT ME!
		 */
		public void addArmy(Army a) {
			armies.add(a);
			men += a.getMen();
			unarmed += a.getUnarmed();
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param ia TODO: DOCUMENT ME!
		 */
		public void addIslandArmy(IslandArmy ia) {
			armies.add(ia);
			men += ia.men;
			unarmed += ia.unarmed;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public String toString() {
			StringBuffer buf = new StringBuffer(getString("armeeof"));
			buf.append(' ');
			buf.append(owner.toString());
			buf.append(" (");
			buf.append(men);
			buf.append(' ');
			buf.append(getString("soldiers"));

			if(unarmed > 0) {
				buf.append(", ");
				buf.append(unarmed);
				buf.append(' ');
				buf.append(getString("withoutweapon"));
			}

			buf.append(')');

			return buf.toString();
		}
	}

	protected class ArmyComparator implements Comparator {
		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param o1 TODO: DOCUMENT ME!
		 * @param o2 TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public int compare(Object o1, Object o2) {
			String s1 = null;
			String s2 = null;

			if(o1 instanceof IslandArmies) {
				s1 = ((IslandArmies) o1).island.getName();
			} else if(o1 instanceof RegionArmies) {
				s1 = ((RegionArmies) o1).region.getName();
			}

			if(o2 instanceof IslandArmies) {
				s2 = ((IslandArmies) o2).island.getName();
			} else if(o2 instanceof RegionArmies) {
				s2 = ((RegionArmies) o2).region.getName();
			}

			return s1 == null ? 
				(s2 == null ? 0 : -1) :
				s1.compareTo(s2);
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param o TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public boolean equals(Object o) {
			return false;
		}
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
			defaultTranslations.put("soldiers", "Soldiers");
			defaultTranslations.put("with", "with");
			defaultTranslations.put("backline", "Back line");
			defaultTranslations.put("armeeof", "Army of");
			defaultTranslations.put("frontline", "Front line");
			defaultTranslations.put("wrongskill", "wrong skill");
			defaultTranslations.put("missing", "missing");
			defaultTranslations.put("skill", "Skill");
			defaultTranslations.put("and", "and");
			defaultTranslations.put("withoutweapon", "without weapon");
			defaultTranslations.put("soldiersofunit", "Soldiers of Unit");
		}

		return defaultTranslations;
	}
}
