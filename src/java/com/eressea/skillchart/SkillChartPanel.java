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

package com.eressea.skillchart;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import com.eressea.Faction;
import com.eressea.GameData;
import com.eressea.Region;
import com.eressea.Skill;
import com.eressea.Unit;
import com.eressea.event.EventDispatcher;
import com.eressea.event.SelectionEvent;
import com.eressea.event.SelectionListener;
import com.eressea.rules.SkillType;
import com.eressea.swing.InternationalizedDataPanel;
import com.eressea.util.SkillStats;
import com.eressea.util.Translations;
import com.eressea.util.logging.Logger;
import com.jrefinery.chart.Axis;
import com.jrefinery.chart.AxisNotCompatibleException;
import com.jrefinery.chart.DefaultCategoryDataSource;
import com.jrefinery.chart.HorizontalCategoryAxis;
import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.Plot;
import com.jrefinery.chart.VerticalNumberAxis;

/**
 * DOCUMENT ME!
 *
 * @author Ulrich K�ster A class painting barcharts out of skills of eressea units. The data with
 * 		   the units to be considered is received solely by SelectionEvents.
 */
public class SkillChartPanel extends InternationalizedDataPanel implements SelectionListener {
	private static final Logger log = Logger.getInstance(SkillChartPanel.class);
	private SkillChartJFreeChartPanel chartPanel;
	private JComboBox persons;
	private JComboBox totalSkillPoints;
	private JComboBox totalSkillLevel;
	private JComboBox skills;
	private SkillStats skillStats = new SkillStats();
	private Map regions = new Hashtable();
	private Map factions = new Hashtable();

	/**
	 * Creates a new SkillChartPanel. The data is received by SelectionEvents (where factions and
	 * regions are considered). Despite of that an GameData-reference is necessary to set all
	 * regions as data, if no region is selected
	 *
	 * @param ed TODO: DOCUMENT ME!
	 * @param data TODO: DOCUMENT ME!
	 * @param settings TODO: DOCUMENT ME!
	 */
	public SkillChartPanel(EventDispatcher ed, GameData data, Properties settings) {
		super(ed, data, settings);
		regions.putAll(data.regions());

		// create axis, plot, chart
		HorizontalCategoryAxis xAxis = new HorizontalCategoryAxis(getString("labeltext.horizontalaxis"));
		VerticalNumberAxis yAxis = new VerticalNumberAxis(getString("labeltext.verticalaxis"));
		yAxis.setTickValue(new Integer(1));
		yAxis.setAutoRangeIncludesZero(true);

		try {
			Plot verticalBarPlot = new VerticalBarPlot(null, (Axis) xAxis, (Axis) yAxis);
			DefaultCategoryDataSource dataSource = createDataSource(null);
			chartPanel = new SkillChartJFreeChartPanel(new JFreeChart("",
																	  new Font("Arial", Font.BOLD,
																			   24), dataSource,
																	  verticalBarPlot), this);
		} catch(AxisNotCompatibleException e) { // work on this later...
			log.warn(e);
		}

		JFreeChart chart = chartPanel.getChart();
		chart.setLegend(null);
		chart.setChartBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.blue));

		//		chart.setChartBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 500, new Color(153, 153, 204)));
		chart.getPlot().setInsets(new Insets(10, 20, 20, 40));

		// initialize the Panel with all the stuff
		GridBagLayout grid = new GridBagLayout();
		setLayout(grid);

		GridBagConstraints c = new GridBagConstraints(0, 0, 2, 1, 1.0, 1.0,
													  GridBagConstraints.CENTER,
													  GridBagConstraints.BOTH,
													  new Insets(5, 5, 5, 5), 2, 2);
		add(chartPanel, c);

		c = new GridBagConstraints(0, 1, 1, 1, 0.1, 0.0, GridBagConstraints.CENTER,
								   GridBagConstraints.BOTH, new Insets(6, 6, 6, 6), 2, 2);
		skills = new JComboBox();
		skills.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					SkillType skillType = (SkillType) skills.getSelectedItem();

					if(skillType != null) {
						chartPanel.getChart().setTitle(skillType.getName());
					} else {
						chartPanel.getChart().setTitle("");
					}

					setComboBoxes(skillType);
					chartPanel.getChart().setDataSource(createDataSource(skillType));
				}
			});
		add(skills, c);

		persons = new JComboBox(new String[] { getString("labeltext.totalpersons") });
		c = new GridBagConstraints(1, 1, 1, 1, 0.1, 0.0, GridBagConstraints.CENTER,
								   GridBagConstraints.BOTH, new Insets(6, 6, 6, 6), 2, 2);
		add(persons, c);

		totalSkillPoints = new JComboBox(new String[] { getString("labeltext.totalskilllevel") });
		c = new GridBagConstraints(0, 2, 1, 1, 0.1, 0.0, GridBagConstraints.CENTER,
								   GridBagConstraints.BOTH, new Insets(6, 6, 6, 6), 2, 2);
		add(totalSkillPoints, c);

		if(data.noSkillPoints) {
			totalSkillPoints.setVisible(false);
		}

		totalSkillLevel = new JComboBox(new String[] { getString("labeltext.totalskillpoints") });
		c = new GridBagConstraints(1, 2, 1, 1, 0.1, 0.0, GridBagConstraints.CENTER,
								   GridBagConstraints.BOTH, new Insets(6, 6, 6, 6), 2, 2);
		add(totalSkillLevel, c);
	}

	/**
	 * just to reset the combo boxes
	 *
	 * @param skillType TODO: DOCUMENT ME!
	 */
	private void setComboBoxes(SkillType skillType) {
		if(skillType != null) {
			Vector vPersons = new Vector();
			Vector vTotalSkillLevel = new Vector();
			Vector vTotalSkillPoints = new Vector();
			vPersons.add(getString("labeltext.totalpersons") +
						 skillStats.getPersonNumber(skillType));
			vTotalSkillLevel.add(getString("labeltext.totalskilllevel") +
								 skillStats.getSkillLevelNumber(skillType));
			vTotalSkillPoints.add(getString("labeltext.totalskillpoints") +
								  skillStats.getSkillPointsNumber(skillType));

			for(Iterator iter = skillStats.getKnownSkills(skillType).iterator(); iter.hasNext();) {
				Skill skill = (Skill) iter.next();
				vPersons.add("T" + skill.getLevel() + ": " + skillStats.getPersonNumber(skill));
				vTotalSkillLevel.add("T" + skill.getLevel() + ": " +
									 skillStats.getSkillLevelNumber(skill));
				vTotalSkillPoints.add("T" + skill.getLevel() + ": " +
									  skillStats.getSkillPointsNumber(skill));
			}

			persons.setModel(new DefaultComboBoxModel(vPersons));
			totalSkillLevel.setModel(new DefaultComboBoxModel(vTotalSkillLevel));
			totalSkillPoints.setModel(new DefaultComboBoxModel(vTotalSkillPoints));
		} else {
			persons.setModel(new DefaultComboBoxModel(new String[] {
														  getString("labeltext.totalpersons")
													  }));
			totalSkillPoints.setModel(new DefaultComboBoxModel(new String[] {
																   getString("labeltext.totalskilllevel")
															   }));
			totalSkillLevel.setModel(new DefaultComboBoxModel(new String[] {
																  getString("labeltext.totalskillpoints")
															  }));
		}
	}

	/**
	 * creates a DefaultCategoryDataSource as basis of a skillchart out of the SkillStats-Object of
	 * this class and the specified skillType. If skillType is null, an empty datasource is
	 * created, containing the single value (0,0)
	 *
	 * @param skillType TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	private DefaultCategoryDataSource createDataSource(SkillType skillType) {
		Number dataArray[][];
		Vector names;

		if(skillType == null) {
			dataArray = new Number[][] {
							{ new Integer(0) }
						};
			names = new Vector();
			names.add("");
		} else {
			int lowLevel = skillStats.getLowestKnownSkillLevel(skillType);
			int highLevel = skillStats.getHighestKnownSkillLevel(skillType);

			if(highLevel != 0) {
				highLevel++;
			}

			if(lowLevel != 0) {
				lowLevel--;
			}

			dataArray = new Number[1][highLevel - lowLevel + 1];
			names = new Vector();

			int loopCounter = 0;

			for(int level = lowLevel; level <= highLevel; loopCounter++, level++) {
				Skill skill = new Skill(skillType, 1, level, 1, false);
				dataArray[0][loopCounter] = new Integer(skillStats.getPersonNumber(skill));
				names.add(new Integer(level));
			}
		}

		return new DefaultCategoryDataSource(new Vector(), names, dataArray);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param e TODO: DOCUMENT ME!
	 */
	public void selectionChanged(SelectionEvent e) {
		// we are only interested in the selectedObjects, not in activeObject
		// and there only in regions and factions
		if(e.getSelectedObjects() != null) {
			boolean modified = false;

			// empty Selection => set all region as data
			if(e.getSelectedObjects().isEmpty()) {
				regions.clear();

				if(data != null) {
					regions.putAll(data.regions());
				}

				modified = true;
			} else {
				Object o = e.getSelectedObjects().iterator().next();

				// some regions were selected:
				if(o instanceof Region) {
					modified = true;
					regions.clear();

					for(Iterator iter = e.getSelectedObjects().iterator(); iter.hasNext();) {
						Region region = (Region) iter.next();
						regions.put(region.getCoordinate(), region);
					}
				} else if(o instanceof Faction) {
					// a faction has been selected
					modified = true;
					factions.clear();

					for(Iterator iter = e.getSelectedObjects().iterator(); iter.hasNext();) {
						Faction faction = (Faction) iter.next();
						factions.put(faction.getID(), faction);
					}
				}
			}

			if(modified) {
				// update the skillStats-Object to the new data (new factions or new regions)
				skillStats = new SkillStats();

				for(Iterator iter = regions.values().iterator(); iter.hasNext();) {
					Region r = (Region) iter.next();

					for(Iterator i = r.units().iterator(); i.hasNext();) {
						Unit u = (Unit) i.next();

						if(factions.containsValue(u.getFaction())) {
							skillStats.addUnit(u);
						}
					}
				}

				skills.removeAllItems();

				for(Iterator iter = skillStats.getKnownSkillTypes().iterator(); iter.hasNext();) {
					skills.addItem(iter.next());
				}

				if(skills.getItemCount() > 0) {
					skills.setSelectedIndex(0);
				}

				chartPanel.getChart().setDataSource(createDataSource((SkillType) skills.getSelectedItem()));
			}
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param e TODO: DOCUMENT ME!
	 */
	public void gameDataChanged(com.eressea.event.GameDataEvent e) {
		data = e.getGameData();

		if(data.noSkillPoints) {
			totalSkillPoints.setVisible(false);
		} else {
			totalSkillPoints.setVisible(true);
		}

		/**
		 * Don't clear factions as the SelectionEvent of the updated List in FactionStatsDialog
		 * might be processed befor the GameDataEvent
		 */
		// factions.clear();
		// enforce refreshing of regions-table and redrawing of chart
		selectionChanged(new SelectionEvent(this, new Vector(), null));
	}

	/**
	 * returns a tooltip for the (i)th Bar of the chart
	 *
	 * @param i TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getToolTip(int i) {
		SkillType type = (SkillType) skills.getSelectedItem();

		if((type == null) || (i >= skillStats.getKnownSkills(type).size())) {
			return null;
		} else {
			Skill skill = (Skill) skillStats.getKnownSkills(type).get(i);
			String retVal = getString("labeltext.totalpersons") +
							skillStats.getPersonNumber(skill);
			retVal += (", " + getString("labeltext.totalskilllevel") +
			skillStats.getSkillLevelNumber(skill));
			retVal += (", " + getString("labeltext.totalskillpoints") +
			skillStats.getSkillPointsNumber(skill));

			return retVal;
		}
	}

	protected String getString(String key) {
		return Translations.getTranslation(this, key);
	}
}
