/*
 *  Copyright (C) 2000-2003 Roger Butenuth, Andreas Gampe,
 *                          Stefan Goetz, Sebastian Pappert,
 *                          Klaas Prause, Enno Rehling,
 *                          Sebastian Tusk, Ulrich Kuester,
 *                          Ilja Pavkovic
 *
 * This file is part of the Eressea Java Code Base, see the
 * file LICENSING for the licensing information applying to
 * this file.
 *
 * $Id$
 */

package com.eressea.swing.tree;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.eressea.Skill;
import com.eressea.Unit;

import com.eressea.util.CollectionFactory;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class SkillNodeWrapper implements CellObject2, SupportsClipboard {
	private Unit						   unit;
	private Skill						   skill;
	private Skill						   modSkill;
	private boolean						   showNextLevelPoints     = true;
	private boolean						   showNextLevelLearnTurns = true;
	protected DetailsNodeWrapperDrawPolicy adapter;
	protected String					   text;
	protected List						   icon;
	protected List						   GEs;

	/** TODO: DOCUMENT ME! */
	public static final int SHOW_NEXTLEVEL = 0;

	/** TODO: DOCUMENT ME! */
	public static final int SHOW_NEXTLEVELPOINTS = 1;

	/** TODO: DOCUMENT ME! */
	public static final int SHOW_NEXTLEVELTURNS = 2;

	/** TODO: DOCUMENT ME! */
	public static final int SHOW_CHANGES = 3;

	/** TODO: DOCUMENT ME! */
	public static final int SHOW_CHANGE_STYLED = 4;

	/** TODO: DOCUMENT ME! */
	public static final int     SHOW_CHANGE_TEXT		  = 5;
	private static final String SKILL_CHANGE_STYLE_PREFIX = "Talent";

	/**
	 * Creates a new SkillNodeWrapper object.
	 *
	 * @param u the unit with the specified skills.
	 * @param s the base skill. If s is null, it is assumed that the unit
	 * 		  aquires that skill only through a person transfer. s and ms may
	 * 		  not both be null.
	 * @param ms the modified skill. If ms is null, it is assumed that the
	 * 		  modification of the skill cannot be determined. s and ms may not
	 * 		  both be null.
	 */
	public SkillNodeWrapper(Unit u, Skill s, Skill ms) {
		unit     = null;
		skill    = null;
		modSkill = null;
		unit     = u;

		if(s != null) {
			skill = s;
		} else {
			skill = new Skill(ms.getSkillType(), 0, 0, 0, ms.noSkillPoints());
		}

		modSkill = ms;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String toString() {
		if(text == null) {
			StringBuffer sb = new StringBuffer();
			sb.append(skill.getName()).append(' ');

			if(!skill.isLostSkill()) {
				sb.append(skill.getLevel());
			} else {
				sb.append('-');
			}

			if(skill.isLevelChanged() && isShowingChanges() &&
				   isShowingChangesText()) {
				sb.append('(');

				if(skill.getChangeLevel() >= 0) {
					sb.append('+');
				}

				sb.append(skill.getChangeLevel());
				sb.append(')');
			}

			// FIX!
			if(!skill.noSkillPoints()) {
				if(modSkill != null) {
					if((skill.getPoints() == modSkill.getPoints()) &&
						   (unit.persons == unit.getModifiedPersons())) {
						sb.append(" [").append(skill.getPointsPerPerson());

						if(isShowingNextLevelPoints() ||
							   isShowingNextLevelLearnTurns()) {
							int nextLevel = Skill.getLevelAtPoints(skill.getPointsPerPerson()) +
											1;
							int nextLevelPoints = Skill.getPointsAtLevel(nextLevel);
							int pointsToLearn   = nextLevelPoints -
												  skill.getPointsPerPerson();
							int turnsToLearn = pointsToLearn / 30;

							if((pointsToLearn % 30) > 0) {
								turnsToLearn++;
							}

							if(isShowingNextLevelPoints()) {
								sb.append(" -> ").append(nextLevelPoints);
							}

							if(isShowingNextLevelLearnTurns()) {
								sb.append(" {").append(turnsToLearn).append("}");
							}
						}

						sb.append("]");
					} else {
						sb.append(" [").append(skill.getPointsPerPerson())
						  .append("]");
						sb.append(" (").append(modSkill.getLevel()).append(" [")
						  .append(modSkill.getPointsPerPerson()).append("])");
					}
				} else {
					sb.append(" [").append(skill.getPointsPerPerson()).append("]");
					sb.append(" (? [?])");
				}
			} else {
				if(modSkill != null) {
					if(skill.getLevel() != modSkill.getLevel()) {
						sb.append(" (").append(modSkill.getLevel()).append(")");
					}
				} else {
					sb.append(" (?)");
				}
			}

			text = sb.toString();
		}

		return text;
	}

	/**
	 * Controls whether this wrapper shows the skill points required before the
	 * next skill level can be reached.
	 *
	 * @param bool TODO: DOCUMENT ME!
	 */
	public void showNextLevelPoints(boolean bool) {
		adapter					 = null;
		this.showNextLevelPoints = bool;
	}

	/**
	 * Returns whether this wrapper shows the skill points required before the
	 * next skill level can be reached.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean isShowingNextLevelPoints() {
		if(adapter != null) {
			return adapter.properties[SHOW_NEXTLEVELPOINTS];
		}

		return this.showNextLevelPoints;
	}

	/**
	 * Controls whether this wrapper shows the number of turns to learn before
	 * the next skill level can be reached.
	 *
	 * @param bool TODO: DOCUMENT ME!
	 */
	public void showNextLevelLearnTurns(boolean bool) {
		adapter						 = null;
		this.showNextLevelLearnTurns = bool;
	}

	/**
	 * Returns whether this wrapper shows the number of turns to learn before
	 * the next skill level can be reached.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean isShowingNextLevelLearnTurns() {
		if(adapter != null) {
			return adapter.properties[SHOW_NEXTLEVELTURNS];
		}

		return this.showNextLevelLearnTurns;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean isShowingNextLevel() {
		if(adapter != null) {
			return adapter.properties[SHOW_NEXTLEVEL];
		}

		return true;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean isShowingChanges() {
		if(adapter != null) {
			return adapter.properties[SHOW_CHANGES];
		}

		return true;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean isShowingChangesStyled() {
		if(adapter != null) {
			return adapter.properties[SHOW_CHANGE_STYLED];
		}

		return false;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean isShowingChangesText() {
		if(adapter != null) {
			return adapter.properties[SHOW_CHANGE_TEXT];
		}

		return true;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean emphasized() {
		return false;
	}

	// TODO: possibly make static
	public List getIconNames() {
		if(icon == null) {
			icon = CollectionFactory.createArrayList(1);

			if(skill != null) {
				icon.add(skill.getSkillType().getID().toString());
			} else if(modSkill != null) {
				icon.add(modSkill.getSkillType().getID().toString());
			} else {
				icon = null;
			}
		}

		return icon;
	}

	/**
	 * TODO: DOCUMENT ME!
	 */
	public void propertiesChanged() {
		text = null;
		GEs  = null;
	}

	// pavkovic 2003.01.28: this is a Map of the default Translations mapped to this class
	// it is called by reflection (we could force the implementation of an interface,
	// this way it is more flexible.)
	// Pls use this mechanism, so the translation files can be created automagically
	// by inspecting all classes.
	private static final Map defaultTranslations = CollectionFactory.createHashtable();

	static {
		defaultTranslations.put("prefs.title", "Skills");
		defaultTranslations.put("prefs.dialogs.1.title", "Skill changes...");
		defaultTranslations.put("prefs.dialogs.0.title",
								"Next level information...");
		defaultTranslations.put("prefs.showskill.text", "Show next level info");
		defaultTranslations.put("prefs.changes.text", "Show skill changes");
		defaultTranslations.put("prefs.changes.mode1.text", "Per Text");
		defaultTranslations.put("prefs.changes.mode0.text", "Per Styleset");
		defaultTranslations.put("prefs.turns.text",
								"Anzahl der Runden bis zur nächsten Stufe anzeigen"); // TODO: why german here?
		defaultTranslations.put("prefs.changes.mode0.text.tooltip",
								"Uses a customizable styleset for display. You may change the text font, fore- and background color.");
		defaultTranslations.put("prefs.dialogs.0.help", "DEFAULT TEXT MISSING.");
		defaultTranslations.put("prefs.dialogs.1.help",
								"Choose the type of talent change visualisation you wish to be used.");
		defaultTranslations.put("prefs.changes.mode1.text.tooltip",
								"Shows the difference in brackets behind the current value.");
		defaultTranslations.put("prefs.changes.text.tooltip",
								"Makes skill changes visible");
		defaultTranslations.put("prefs.points.text",
								"Show skill points till next level");
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static Map getDefaultTranslations() {
		return defaultTranslations;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getClipboardValue() {
		if(skill != null) {
			return skill.getName();
		} else {
			return toString();
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public List getGraphicsElements() {
		if(GEs == null) {
			GraphicsElement ge = new GraphicsElement(toString(), null, null,
													 null);
			ge.setType(GraphicsElement.MAIN);

			if(skill != null) {
				ge.setImageName(skill.getSkillType().getID().toString());
			} else if(modSkill != null) {
				ge.setImageName(modSkill.getSkillType().getID().toString());
			}

			boolean isDiff = false;

			if(skill != null) {
				isDiff = skill.isLevelChanged();
			}

			if(isDiff && isShowingChanges() && isShowingChangesStyled()) {
				ge.setStyleset(SKILL_CHANGE_STYLE_PREFIX +
							   ((skill.getChangeLevel() >= 0) ? ">." : "<.") +
							   SKILL_CHANGE_STYLE_PREFIX +
							   String.valueOf(skill.getChangeLevel()));
			}

			GEs = CollectionFactory.singletonList(ge);
		}

		return GEs;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean reverseOrder() {
		return false;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param settings TODO: DOCUMENT ME!
	 * @param adapter TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public NodeWrapperDrawPolicy init(Properties settings,
									  NodeWrapperDrawPolicy adapter) {
		return init(settings, "SkillNodeWrapper", adapter);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param p1 TODO: DOCUMENT ME!
	 * @param p2 TODO: DOCUMENT ME!
	 * @param p3 TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public NodeWrapperDrawPolicy init(Properties p1, String p2,
									  NodeWrapperDrawPolicy p3) {
		if(p3 == null) {
			p3 = createSkillDrawPolicy(p1, p2);
		}

		p3.addCellObject(this);
		adapter = (DetailsNodeWrapperDrawPolicy) p3;

		return p3;
	}

	private NodeWrapperDrawPolicy createSkillDrawPolicy(Properties settings,
														String prefix) {
		return new DetailsNodeWrapperDrawPolicy(2, new int[] { 2, 2 },
												settings, prefix,
												new String[][] {
													{
														".units.showNextSkillLevel",
														"true"
													},
													{
														".units.showNextSkillLevelPoints",
														"true"
													},
													{
														".units.showNextSkillLevelLearnTurns",
														"true"
													},
													{
														".units.showChanges",
														"true"
													},
													{
														".units.showChangesStyled",
														"false"
													},
													{
														".units.showChangesText",
														"true"
													},
												},
												new String[] {
													"prefs.showskill.text",
													"prefs.points.text",
													"prefs.turns.text",
													"prefs.changes.text",
													"prefs.changes.mode0.text",
													"prefs.changes.mode1.text",
												}, 0, getClass(),
												getDefaultTranslations());
	}
}
