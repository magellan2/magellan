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

package com.eressea.swing.completion;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.JTextComponent;
import javax.swing.undo.UndoManager;

import com.eressea.Faction;
import com.eressea.GameData;
import com.eressea.Island;
import com.eressea.Region;
import com.eressea.StringID;
import com.eressea.TempUnit;
import com.eressea.Unit;
import com.eressea.UnitID;
import com.eressea.demo.EMapDetailsPanel;
import com.eressea.demo.EMapOverviewPanel;
import com.eressea.demo.desktop.DesktopEnvironment;
import com.eressea.event.EventDispatcher;
import com.eressea.event.GameDataEvent;
import com.eressea.event.OrderConfirmEvent;
import com.eressea.event.OrderConfirmListener;
import com.eressea.event.SelectionEvent;
import com.eressea.event.SelectionListener;
import com.eressea.event.TempUnitEvent;
import com.eressea.event.TempUnitListener;
import com.eressea.event.UnitOrdersEvent;
import com.eressea.rules.ItemType;
import com.eressea.swing.InternationalizedDataPanel;
import com.eressea.swing.preferences.PreferencesAdapter;
import com.eressea.util.Cache;
import com.eressea.util.CacheHandler;
import com.eressea.util.CollectionFactory;
import com.eressea.util.Colors;
import com.eressea.util.EresseaOrderConstants;
import com.eressea.util.IDBaseConverter;
import com.eressea.util.Translations;
import com.eressea.util.logging.Logger;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class MultiEditorOrderEditorList extends InternationalizedDataPanel
	implements OrderEditorList, KeyListener, SelectionListener, TempUnitListener, FocusListener,
			   CacheHandler
{
	private static final Logger log = Logger.getInstance(MultiEditorOrderEditorList.class);
	private boolean multiEditorLayout = false;
	private boolean hideButtons = false;
	private List units = CollectionFactory.createLinkedList();
	private Unit currentUnit = null;
	private Region currentRegion = null;
	private int currentUnitIndex = -1;
	private Color standardBgColor = null;
	private Color activeBgColor = null;
	private Color standardBgColorConfirmed = null;
	private Color activeBgColorConfirmed = null;
	private OrderEditor editor = null;
	private static final Border standardBorder = new LineBorder(Color.lightGray, 2);
	private static final Border activeBorder = new LineBorder(Color.darkGray, 2);
	private UpdateThread updateThread = new UpdateThread();
	private SwingGlitchThread swingGlitchThread = new SwingGlitchThread();

	// undo listener
	private UndoManager undoMgr = null;
	protected List keyListeners = CollectionFactory.createLinkedList();
	protected MEKeyAdapter keyAdapter;
	protected List caretListeners = CollectionFactory.createLinkedList();
	protected MECaretAdapter caretAdapter;
	protected List focusListeners = CollectionFactory.createLinkedList();
	protected MEFocusAdapter focusAdapter;

	// we have no longer any container like OrderEditingPanel
	protected ScrollPanel content; // a Panel implementing the scrollable interface

	// protected JPanel content; // a Panel implementing the scrollable interface
	protected JScrollPane scpContent = null;

	// the buttons for temp units etc.
	protected ButtonPanel buttons;

	// editor list generation mode
	protected int listMode = 1 << LIST_REGION;
	private static final int LIST_UNIT = 0;
	private static final int LIST_FACTION = 1;
	private static final int LIST_REGION = 2;
	private static final int LIST_ISLAND = 3;

	/**
	 * Creates a new MultiEditorOrderEditorList object.
	 *
	 * @param d TODO: DOCUMENT ME!
	 * @param initData TODO: DOCUMENT ME!
	 * @param settings TODO: DOCUMENT ME!
	 * @param _undoMgr TODO: DOCUMENT ME!
	 */
	public MultiEditorOrderEditorList(EventDispatcher d, GameData initData, Properties settings,
									  UndoManager _undoMgr) {
		super(d, initData, settings);

		loadListProperty();

		d.addTempUnitListener(this);

		keyAdapter = new MEKeyAdapter(this);
		caretAdapter = new MECaretAdapter(this);
		focusAdapter = new MEFocusAdapter(this);

		undoMgr = _undoMgr;
		initGUI();

		//startTimer();
	}

	private void initGUI() {
		readSettings();

		content = new ScrollPanel();

		// content = new JPanel();
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
		scpContent = new JScrollPane(content);

		// ClearLook suggests to remove the border
		scpContent.setBorder(null);

		buttons = new ButtonPanel();

		redrawPane();

		if(!multiEditorLayout) {
			editor = new OrderEditor(data, settings, undoMgr, dispatcher);
			editor.setCursor(new Cursor(Cursor.TEXT_CURSOR));
			addListeners(editor);
			content.add(editor);
		}

		// update the color of the editor when the order confirmation status changes
		dispatcher.addOrderConfirmListener(new OrderConfirmListener() {
				public void orderConfirmationChanged(OrderConfirmEvent e) {
					if(!this.equals(e.getSource())) {
						for(Iterator iter = e.getUnits().iterator(); iter.hasNext();) {
							Unit u = (Unit) iter.next();

							if((u.cache != null) && (u.cache.orderEditor != null)) {
								if(u.equals(currentUnit)) {
									// u is active unit
									if(u.ordersConfirmed) {
										u.cache.orderEditor.setBackground(activeBgColorConfirmed);
									} else {
										u.cache.orderEditor.setBackground(activeBgColor);
									}
								} else {
									if(u.ordersConfirmed) {
										u.cache.orderEditor.setBackground(standardBgColorConfirmed);
									} else {
										u.cache.orderEditor.setBackground(standardBgColor);
									}
								}
							}
						}
					}
				}
			});
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param e TODO: DOCUMENT ME!
	 */
	public void gameDataChanged(GameDataEvent e) {
		super.gameDataChanged(e);

		// rebuild order editors
		// startTimer();
	}

	private void readSettings() {
		multiEditorLayout = Boolean.valueOf(settings.getProperty("OrderEditor.multiEditorLayout",
																 Boolean.TRUE.toString()))
								   .booleanValue();
		hideButtons = Boolean.valueOf(settings.getProperty("OrderEditor.hideButtons",
														   Boolean.FALSE.toString())).booleanValue();
		activeBgColor = Colors.decode(settings.getProperty("OrderEditor.activeBackgroundColor",
														   "255,255,255"));
		standardBgColor = Colors.decode(settings.getProperty("OrderEditor.standardBackgroundColor",
															 "228,228,228"));
		activeBgColorConfirmed = Colors.decode(settings.getProperty("OrderEditor.activeBackgroundColorConfirmed",
																	"255,255,102"));
		standardBgColorConfirmed = Colors.decode(settings.getProperty("OrderEditor.standardBackgroundColorConfirmed",
																	  "255,204,0"));
	}

	private boolean swingGlitch = false;

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param g TODO: DOCUMENT ME!
	 */
	public void paint(Graphics g) {
		super.paint(g);

		if(log.isDebugEnabled()) {
			log.debug("paint! [" + swingGlitch + "]");
		}

		// we are in a situation AFTER painting (hopefully!)
		if(swingGlitch) {
			swingGlitch = false;
			SwingUtilities.invokeLater(swingGlitchThread);
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param se TODO: DOCUMENT ME!
	 */
	public void selectionChanged(SelectionEvent se) {
		if(log.isDebugEnabled()) {
			log.debug("MultiEditorOrderEditorList.selectionChanged: " + se.getActiveObject());
		}

		if(se.getActiveObject() != null) {
			log.debug("MultiEditorOrderEditorList.selectionChanged: " +
					  se.getActiveObject().getClass());
		}

		if(log.isDebugEnabled()) {
			log.debug("MultiEditorOrderEditorList.selectionChanged: " + (se.getSource() == this));
		}

		boolean restoreFocus = ((currentUnit != null) && (currentUnit.cache != null) &&
							   (currentUnit.cache.orderEditor != null) &&
							   currentUnit.cache.orderEditor.hasFocus());

		// if WE triggered the selection change, the new unit DOES get the focus
		restoreFocus = restoreFocus || (se.getSource() == this);

		if(multiEditorLayout) {
			// reset old unit's editor border to normal
			if((currentUnit != null) && (currentUnit.cache != null) &&
				   (currentUnit.cache.orderEditor != null)) {
				currentUnit.cache.orderEditor.setBorder(new TitledBorder(standardBorder,
																		 currentUnit.toString() +
																		 ": " +
																		 currentUnit.persons));

				if(currentUnit.ordersConfirmed) {
					currentUnit.cache.orderEditor.setBackground(standardBgColorConfirmed);
				} else {
					currentUnit.cache.orderEditor.setBackground(standardBgColor);
				}

				// deactivate visibility call
				currentUnit.cache.orderEditor.setKeepVisible(false);

				// make the old editor do the syntax highlighting again later
				if(currentUnit.cache.orderEditor.isModified()) {
					updateThread.e = currentUnit.cache.orderEditor;
					SwingUtilities.invokeLater(updateThread);
				}
			}

			if(se.getActiveObject() != null) {
				if(se.getActiveObject() instanceof Unit &&
					   EMapDetailsPanel.isPrivilegedAndNoSpy((Unit) se.getActiveObject())) {
					Unit u = (Unit) se.getActiveObject();

					loadEditors(u);

					// only jump to a different unit
					if((currentUnit == null) || !currentUnit.equals(u)) {
						currentUnit = u;
						currentUnitIndex = units.indexOf(currentUnit);
					}

					// set different border for selected editor
					if((currentUnit.cache != null) && (currentUnit.cache.orderEditor != null)) {
						currentUnit.cache.orderEditor.setBorder(new TitledBorder(activeBorder,
																				 currentUnit.toString() +
																				 ": " +
																				 currentUnit.persons));

						if(currentUnit.ordersConfirmed) {
							currentUnit.cache.orderEditor.setBackground(activeBgColorConfirmed);
						} else {
							currentUnit.cache.orderEditor.setBackground(activeBgColor);
						}

						// activate visibility call
						currentUnit.cache.orderEditor.setKeepVisible(true);

						// we use a call to repaint to force the execution of paint()
						// so we call SwingGlitchThread.run() indirectly AFTER painting
						// tricky and dirty, but it works...
						SwingUtilities.invokeLater(swingGlitchThread);
					}
				} else if(se.getActiveObject() instanceof Region) {
					currentRegion = (Region) se.getActiveObject();
					currentUnit = null;
					currentUnitIndex = -1;
					loadEditors(currentRegion);
				} else if(se.getActiveObject() instanceof Faction) {
					currentUnit = null;
					currentUnitIndex = -1;

					if(((Faction) se.getActiveObject()).isPrivileged() && (currentRegion != null)) {
						loadEditors((Faction) se.getActiveObject(), currentRegion);
					} else {
						units.clear();
						removeListenersFromAll();
						content.removeAll();
						repaint();
					}
				} else if(se.getActiveObject() instanceof Island) {
					currentRegion = null;
					currentUnit = null;
					currentUnitIndex = -1;
					loadEditors((Island) se.getActiveObject());
				} else {
					currentUnit = null;
					currentUnitIndex = -1;
					units.clear();
					removeListenersFromAll();
					content.removeAll();
					repaint();
				}
			} else {
				currentUnit = null;
				currentRegion = null;
				currentUnitIndex = -1;
				units.clear();
				removeListenersFromAll();
				content.removeAll();
				repaint();
			}

			// make the UI component refresh itself - necessary at least under Windows
			revalidate();
		} else {
			// single editor mode
			Object activeObject = se.getActiveObject();

			if(activeObject instanceof Unit &&
				   EMapDetailsPanel.isPrivilegedAndNoSpy((Unit) activeObject)) {
				currentUnit = (Unit) activeObject;
				editor.setUnit(currentUnit);

				if(currentUnit.cache == null) {
					currentUnit.cache = new Cache();
				}

				currentUnit.cache.orderEditor = editor;

				currentUnit.cache.orderEditor.setBorder(new TitledBorder(activeBorder,
																		 currentUnit.toString() +
																		 ": " +
																		 currentUnit.persons));

				if(currentUnit.ordersConfirmed) {
					currentUnit.cache.orderEditor.setBackground(activeBgColorConfirmed);
				} else {
					currentUnit.cache.orderEditor.setBackground(activeBgColor);
				}

				editor.setEditable(true);
			} else {
				if(currentUnit != null) {
					if(currentUnit.cache != null) {
						currentUnit.cache.orderEditor = null;
					}

					currentUnit = null;
					currentRegion = null;
					editor.setUnit(null);
					editor.setEditable(false);
				}
			}
		}

		// restore focus
		if(((DesktopEnvironment.getMode() == DesktopEnvironment.FRAME) || restoreFocus) &&
			   (currentUnit != null) && (currentUnit.cache != null) &&
			   (currentUnit.cache.orderEditor != null)) {
			requestFocus(currentUnit.cache.orderEditor);
		}

		// update button state
		buttons.currentUnitChanged();
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param e TODO: DOCUMENT ME!
	 */
	public void tempUnitCreated(TempUnitEvent e) {
		if(log.isDebugEnabled()) {
			log.debug("MultiEditorOrderEditorList.tempUnitCreated: " + e.getTempUnit());
		}

		if((currentUnit != null) && multiEditorLayout) {
			loadEditors(currentRegion);
			this.revalidate();

			if(log.isDebugEnabled()) {
				log.debug("MultiEditorOrderEditorList.tempUnitCreated: " + e.getTempUnit().cache);
				log.debug("MultiEditorOrderEditorList.tempUnitCreated: " +
						  e.getTempUnit().cache.orderEditor);
			}

			if((e.getTempUnit().cache != null) && (e.getTempUnit().cache.orderEditor != null)) {
				// pavkovic 2002.02.15: here we don't request focus in an invokeLater runnable
				// because this would lead to intense focus change between parent and temp unit.
				e.getTempUnit().cache.orderEditor.requestFocus();
				//requestFocus(e.getTempUnit().cache.orderEditor);
			}
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param e TODO: DOCUMENT ME!
	 */
	public void tempUnitDeleted(TempUnitEvent e) {
		if((currentUnit != null) && multiEditorLayout) {
			loadEditors(currentRegion);
			this.revalidate();
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param e TODO: DOCUMENT ME!
	 */
	public void keyPressed(KeyEvent e) {
		if((e.getModifiers() & KeyEvent.CTRL_MASK) != 0) {
			if(multiEditorLayout) {
				switch(e.getKeyCode()) {
				case KeyEvent.VK_DOWN:

					if(currentUnitIndex < (units.size() - 1)) {
						/* refreshing the relations here is
						 necessary because this is the only place
						 where a different unit is selected and the
						 focusLost event in the order editor does
						 not occur before the selection event */
						if((currentUnit != null) && (currentUnit.cache != null) &&
							   (currentUnit.cache.orderEditor != null) &&
							   currentUnit.cache.orderEditor.isModified()) {
							currentUnit.refreshRelations();
						}

						Unit u = ((Unit) units.get(currentUnitIndex + 1));

						dispatcher.fire(new SelectionEvent(this, null, u));
					}

					break;

				case KeyEvent.VK_UP:

					if(currentUnitIndex > 0) {
						/* refreshing the relations here is
						 necessary because this is the only place
						 where a different unit is selected and the
						 focusLost event in the order editor does
						 not occur before the selection event */
						if((currentUnit != null) && (currentUnit.cache != null) &&
							   (currentUnit.cache.orderEditor != null) &&
							   currentUnit.cache.orderEditor.isModified()) {
							currentUnit.refreshRelations();
						}

						Unit u = ((Unit) units.get(currentUnitIndex - 1));

						dispatcher.fire(new SelectionEvent(this, null, u));
					}

					break;
				}
			}
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param e TODO: DOCUMENT ME!
	 */
	public void keyReleased(KeyEvent e) {
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param e TODO: DOCUMENT ME!
	 */
	public void keyTyped(KeyEvent e) {
	}

	/**
	 * Fires an SelectionChanged event if a different editor than the current one is selected.
	 *
	 * @param e TODO: DOCUMENT ME!
	 */
	public void focusGained(FocusEvent e) {
		if(multiEditorLayout && e.getSource() instanceof OrderEditor) {
			if((currentUnit == null) ||
				   ((currentUnit.cache != null) &&
				   (currentUnit.cache.orderEditor != e.getSource()))) {
				dispatcher.fire(new SelectionEvent(this, null,
												   ((OrderEditor) e.getSource()).getUnit()));
			}
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param e TODO: DOCUMENT ME!
	 */
	public void focusLost(FocusEvent e) {
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param c TODO: DOCUMENT ME!
	 */
	public void clearCache(Cache c) {
		if(c.orderEditor != null) {
			c.orderEditor.removeCaretListener(caretAdapter);
			c.orderEditor.removeFocusListener(focusAdapter);
			c.orderEditor.removeKeyListener(keyAdapter);
			c.orderEditor.setBorder(null);
			c.orderEditor.setBackground(null);
			c.orderEditor.setFont(null);
			c.orderEditor.setCursor(null);
		}
	}

	/**
	 * Return whether syntax highlighting in the editor is enabled or disabled.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean getUseSyntaxHighlighting() {
		if(multiEditorLayout) {
			return (new OrderEditor(data, settings, undoMgr, dispatcher)).getUseSyntaxHighlighting();
		} else {
			return editor.getUseSyntaxHighlighting();
		}
	}

	/**
	 * Enable or disable syntax highlighting in the editor.
	 *
	 * @param bool TODO: DOCUMENT ME!
	 */
	public void setUseSyntaxHighlighting(boolean bool) {
		if(multiEditorLayout) {
			boolean foundEditor = false;

			if(data.units() != null) {
				for(Iterator iter = data.units().values().iterator(); iter.hasNext();) {
					Unit u = (Unit) iter.next();

					if((u.cache != null) && (u.cache.orderEditor != null)) {
						foundEditor = true;
						u.cache.orderEditor.setUseSyntaxHighlighting(bool);
					}
				}
			}

			if(!foundEditor) {
				(new OrderEditor(data, settings, undoMgr, dispatcher)).setUseSyntaxHighlighting(bool);
			}
		} else {
			editor.setUseSyntaxHighlighting(bool);
		}
	}

	/**
	 * Return the color of the specified token style used for syntax highlighting in the editor.
	 *
	 * @param styleName TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Color getTokenColor(String styleName) {
		return (new OrderEditor(data, settings, undoMgr, dispatcher)).getTokenColor(styleName);
	}

	/**
	 * Set the color of the specified token style used for syntax highlighting in the editor.
	 *
	 * @param styleName TODO: DOCUMENT ME!
	 * @param color TODO: DOCUMENT ME!
	 */
	public void setTokenColor(String styleName, Color color) {
		if(multiEditorLayout) {
			boolean foundEditor = false;

			if(data.units() != null) {
				for(Iterator iter = data.units().values().iterator(); iter.hasNext();) {
					Unit u = (Unit) iter.next();

					if((u.cache != null) && (u.cache.orderEditor != null)) {
						foundEditor = true;
						u.cache.orderEditor.setTokenColor(styleName, color);
					}
				}
			}

			if(!foundEditor) {
				(new OrderEditor(data, settings, undoMgr, dispatcher)).setTokenColor(styleName,
																					 color);
			}
		} else {
			editor.setTokenColor(styleName, color);
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Color getStandardBackgroundColor() {
		return standardBgColor;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param c TODO: DOCUMENT ME!
	 */
	public void setStandardBackgroundColor(Color c) {
		if((standardBgColor != c) && (c != null)) {
			standardBgColor = c;
			settings.setProperty("OrderEditor.standardBackgroundColor", Colors.encode(c));
		}

		if(multiEditorLayout) {
			if(data.units() != null) {
				for(Iterator iter = data.units().values().iterator(); iter.hasNext();) {
					Unit u = (Unit) iter.next();

					if((u.cache != null) && (u.cache.orderEditor != null)) {
						u.cache.orderEditor.setBackground(c);
					}
				}
			}
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Color getStandardBackgroundColorConfirmed() {
		return standardBgColorConfirmed;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param c TODO: DOCUMENT ME!
	 */
	public void setStandardBackgroundColorConfirmed(Color c) {
		if((standardBgColorConfirmed != c) && (c != null)) {
			standardBgColorConfirmed = c;
			settings.setProperty("OrderEditor.standardBackgroundColorConfirmed", Colors.encode(c));
		}

		if(multiEditorLayout) {
			if(data.units() != null) {
				for(Iterator iter = data.units().values().iterator(); iter.hasNext();) {
					Unit u = (Unit) iter.next();

					if((u.cache != null) && (u.cache.orderEditor != null)) {
						u.cache.orderEditor.setBackground(c);
					}
				}
			}
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Color getActiveBackgroundColor() {
		return activeBgColor;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param c TODO: DOCUMENT ME!
	 */
	public void setActiveBackgroundColor(Color c) {
		if((activeBgColor != c) && (c != null)) {
			activeBgColor = c;
			settings.setProperty("OrderEditor.activeBackgroundColor", Colors.encode(c));
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Color getActiveBackgroundColorConfirmed() {
		return activeBgColorConfirmed;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param c TODO: DOCUMENT ME!
	 */
	public void setActiveBackgroundColorConfirmed(Color c) {
		if((activeBgColorConfirmed != c) && (c != null)) {
			activeBgColorConfirmed = c;
			settings.setProperty("OrderEditor.activeBackgroundColorConfirmed", Colors.encode(c));
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean isMultiEditorLayout() {
		return multiEditorLayout;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param bool TODO: DOCUMENT ME!
	 */
	public void setMultiEditorLayout(boolean bool) {
		if(bool != multiEditorLayout) {
			settings.setProperty("OrderEditor.multiEditorLayout", (new Boolean(bool)).toString());
			clearUnits();

			if(bool && (editor != null)) {
				// if before there was only one editor and now we
				// switch to multi editor layout
				content.removeAll();
				removeListeners(editor);
				editor = null;
			} else {
				editor = new OrderEditor(data, settings, undoMgr, dispatcher);
				editor.setCursor(new Cursor(Cursor.TEXT_CURSOR));

				// add listeners
				addListeners(editor);
				content.add(editor);
			}

			multiEditorLayout = bool;
			repaint();
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean isHideButtons() {
		return hideButtons;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param bool TODO: DOCUMENT ME!
	 */
	public void setHideButtons(boolean bool) {
		if(bool != hideButtons) {
			settings.setProperty("OrderEditor.hideButtons", (new Boolean(bool)).toString());
			hideButtons = bool;
			redrawPane();
		}
	}

	private void redrawPane() {
		remove(scpContent);
		remove(buttons);
		setLayout(new BorderLayout());
		add(scpContent, BorderLayout.CENTER);

		if(!hideButtons) {
			add(buttons, BorderLayout.SOUTH);
		}

		repaint();
	}

	/**
	 * Removes the Adapters for Key-, Caret- & Focusevents from the given JTextComponents.
	 *
	 * @param j TODO: DOCUMENT ME!
	 */
	private void removeListeners(JTextComponent j) {
		j.removeFocusListener(focusAdapter);
		j.removeKeyListener(keyAdapter);
		j.removeCaretListener(caretAdapter);
	}

	/**
	 * Removes the Adapters for Key-, Caret- & Focusevents from all sub-components that are
	 * JTextComponents.
	 */
	private void removeListenersFromAll() {
		Component c[] = content.getComponents();

		if((c != null) && (c.length > 0)) {
			for(int i = 0; i < c.length; i++) {
				if(c[i] instanceof JTextComponent) {
					removeListeners((JTextComponent) c[i]);
				}
			}
		}
	}

	/**
	 * Adds the Adapters for Key-, Caret- & Focusevents to the given JTextComponents.
	 *
	 * @param j TODO: DOCUMENT ME!
	 */
	private void addListeners(JTextComponent j) {
		j.addFocusListener(focusAdapter);
		j.addKeyListener(keyAdapter);
		j.addCaretListener(caretAdapter);
	}

	private void loadEditors(Island i) {
		List l = CollectionFactory.createLinkedList();

		if((listMode >> LIST_ISLAND) != 0) {
			// list units of specified Island
			for(Iterator iter = i.regions().iterator(); iter.hasNext();) {
				Region r = (Region) iter.next();
				Collection c = r.units();

				if(c != null) {
					l.addAll(c);
				}
			}
		}

		loadEditors(l);
	}

	private void loadEditors(Faction f, Region r) {
		currentRegion = r;

		if((listMode >> LIST_FACTION) != 0) {
			// list some units
			if(((listMode >> LIST_FACTION) & 1) != 0) {
				// list units in region of specified faction
				List l = CollectionFactory.createLinkedList(r.units());

				if(f != null) {
					for(Iterator iter = l.iterator(); iter.hasNext();) {
						Unit u = (Unit) iter.next();

						if(!f.equals(u.getFaction())) {
							iter.remove();
						}
					}
				}

				loadEditors(l);
			} else {
				// list all units in region
				loadEditors(r);
			}
		} else {
			// dont list any units
			loadEditors((List) null);
		}
	}

	private void loadEditors(Region r) {
		currentRegion = r;

		if((listMode >> LIST_REGION) != 0) {
			if(((listMode >> LIST_REGION) & 1) != 0) {
				if(r != null) {
					Collection c = r.units();

					if(c != null) {
						loadEditors(CollectionFactory.createLinkedList(c));
					} else {
						// dont list any units
						loadEditors((List) null);
					}
				} else {
					// Region is null
					// dont list any units
					loadEditors((List) null);
				}
			} else {
				Island i = r.getIsland();

				if(i != null) {
					loadEditors(i);
				} else {
					if(r != null) {
						Collection c = r.units();

						if(c != null) {
							loadEditors(CollectionFactory.createLinkedList(c));
						} else {
							// dont list any units
							loadEditors((List) null);
						}
					} else {
						// dont list any units
						loadEditors((List) null);
					}
				}
			}
		} else {
			// dont list any units
			loadEditors((List) null);
		}
	}

	private void loadEditors(Unit u) {
		if((listMode >> LIST_UNIT) != 0) {
			if(((listMode >> LIST_UNIT) & 1) != 0) {
				// don't know what to do in this case
				loadEditors(u.getFaction(), u.getRegion());
			} else {
				loadEditors(u.getFaction(), u.getRegion());
			}
		} else {
			loadEditors((List) null);
		}
	}

	/**
	 * Adds editors for all units in privileged factions that are in the specified list to this
	 * component. OrderEditors are created if necessary, else the cached version are used.
	 *
	 * @param unitsToAdd TODO: DOCUMENT ME!
	 */
	private void loadEditors(List unitsToAdd) {
		int unitIndex = 0;
		List allUnits = unitsToAdd;

		removeListenersFromAll();
		content.removeAll();
		units.clear();

		if((allUnits == null) || (allUnits.size() == 0)) {
			return;
		}

		// sort like in EMapOverviewPanel
		Comparator cmp = EMapOverviewPanel.getUnitSorting(settings);
		Collections.sort(allUnits, cmp);

		for(Iterator unitIter = allUnits.listIterator(); unitIter.hasNext();) {
			Unit regionUnit = (Unit) unitIter.next();

			if(EMapDetailsPanel.isPrivilegedAndNoSpy(regionUnit)) {
				addUnit(regionUnit);

				// this is done in order to find out the index of the
				// currently selected unit
				if(regionUnit.equals(currentUnit)) {
					currentUnitIndex = unitIndex;
				}

				unitIndex++;
			}
		}

		this.invalidate();
		this.validate();
	}

	/**
	 * Adds the specified unit to this component. This includes creating and adding a new order
	 * editor and updating the internal data structures.
	 *
	 * @param u TODO: DOCUMENT ME!
	 */
	private void addUnit(Unit u) {
		buildOrderEditor(u);

		content.add(u.cache.orderEditor);
		addListeners(u.cache.orderEditor);
		units.add(u);
	}

	/**
	 * Builds and attaches the order editor for and to the given unit.
	 *
	 * @param u TODO: DOCUMENT ME!
	 */
	private void buildOrderEditor(Unit u) {
		if((u.cache == null) || (u.cache.orderEditor == null)) {
			OrderEditor ce = new OrderEditor(data, settings, undoMgr, dispatcher);

			ce.setBorder(new TitledBorder(standardBorder, u.toString() + ": " + u.persons));

			if(u.ordersConfirmed) {
				ce.setBackground(standardBgColorConfirmed);
			} else {
				ce.setBackground(standardBgColor);
			}

			// deactivate visibility call
			ce.setKeepVisible(false);
			ce.setFont(new Font("Monospaced", Font.PLAIN, ce.getFont().getSize()));
			ce.setCursor(new Cursor(Cursor.TEXT_CURSOR));
			ce.setUnit(u);

			if(u.cache == null) {
				u.cache = new Cache();
			}

			u.cache.orderEditor = ce;
			u.cache.addHandler(this);
		}
	}

	/**
	 * Performs the clean-up necessary to put the editor list into a state without units and
	 * editors
	 */
	private void clearUnits() {
		if(multiEditorLayout) {
			if(units != null) {
				units.clear();
			}

			removeListenersFromAll();
			content.removeAll();
			currentUnitIndex = -1;
		} else {
			if(currentUnit != null) {
				if(currentUnit.cache != null) {
					removeListeners(currentUnit.cache.orderEditor);
					currentUnit.cache.orderEditor = null;
				}

				editor.setUnit(null);
				editor.setEditable(false);
				removeListeners(editor);
			}
		}

		currentUnit = null;
	}

	private void requestFocus(final OrderEditor editor) {
		// pavkovic 2004.02.14
		// THIS IS A HACK: I don't know why the focus 
		// gets lost somehow so put it at the end of the 
		// event dispatching thread.
		// It may be EMapOverviewPanel (SwingUtilities.invokeLater(new ScrollerRunnable()))).
		SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					editor.requestFocus();
				}
			});
	}

	/**
	 * Ensures that the right order editor gets the focus.
	 */
	public void requestFocus() {
		if((currentUnit != null) && (currentUnit.cache != null) &&
			   (currentUnit.cache.orderEditor != null)) {
			requestFocus(currentUnit.cache.orderEditor);
		}
	}

	protected void loadListProperty() {
		try {
			listMode = Integer.parseInt(settings.getProperty("OrderEditorList.listMode"));
		} catch(Exception exc) {
			listMode = 1 << LIST_REGION;
		}
	}

	protected void saveListProperty() {
		if(listMode == (1 << LIST_REGION)) {
			settings.remove("OrderEditorList.listMode");
		} else {
			settings.setProperty("OrderEditorList.listMode", String.valueOf(listMode));
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
			defaultTranslations.put("chk.orderconfirmation", "Orders confirmed");

			defaultTranslations.put("msg.newtempid.text",
									"Please enter an id for the new temporary unit:");
			defaultTranslations.put("msg.newtempid.title", "New temp id");
			defaultTranslations.put("msg.duplicatetempid.text",
									"The specified temp id is already assigned to a unit. Please enter a different id.");
			defaultTranslations.put("msg.duplicatetempid.title", "Temp id used");
			defaultTranslations.put("msg.invalidtempid.text",
									"The temp id you specified is invalid.");
			defaultTranslations.put("prefs.listMode.text",
									"This settings affects the units to be shown while selecting a game object. Only useful in Multieditor-Mode.");
			defaultTranslations.put("prefs.listMode", "Editor Listing");
			defaultTranslations.put("shortcuts.description.1", "Delete temp unit");
			defaultTranslations.put("msg.invalidtempid.title", "Invalid temp id");

			defaultTranslations.put("prefs.multieditorlayout", "Multi-editor layout");
			defaultTranslations.put("prefs.hidebuttons", "Hide buttons");
			defaultTranslations.put("prefs.syntaxhighlighting", "Syntax highlighting");
			defaultTranslations.put("prefs.colors", "Colors");
			defaultTranslations.put("prefs.orderautocompletion", "Auto-complete orders");
			defaultTranslations.put("prefs.title", "Order editor");
			defaultTranslations.put("prefs.colors.standard", "Default");
			defaultTranslations.put("prefs.colors.keywords", "Keywords");
			defaultTranslations.put("prefs.colors.strings", "Strings");
			defaultTranslations.put("prefs.listMode.2.text",
									"Shows all privileged units of the current Faction");
			defaultTranslations.put("prefs.listMode.1.text",
									"Shows all privileged units of the current region");
			defaultTranslations.put("prefs.listMode.0.text",
									"Shows all privileged units of the current island");
			defaultTranslations.put("prefs.listMode.2", "Factions");
			defaultTranslations.put("prefs.listMode.1", "Regions");
			defaultTranslations.put("prefs.listMode.0", "Islands");
			defaultTranslations.put("shortcuts.description.0", "Create temp unit");
			defaultTranslations.put("shortcuts.title", "Temp units");
			defaultTranslations.put("prefs.syntaxhighlighting.caption",
									"Activate Syntax highlighting");
			defaultTranslations.put("prefs.layout", "Layout");
			defaultTranslations.put("prefs.colors.numbers", "Numbers");
			defaultTranslations.put("prefs.colors.ids", "IDs");
			defaultTranslations.put("prefs.colors.comments", "Comments");
			defaultTranslations.put("prefs.inactivebackground", "Color of inactive editors");
			defaultTranslations.put("prefs.inactivebackground.confirmed",
									"Color of inactive and confirmed editors");
			defaultTranslations.put("prefs.backgroundcolor", "Background color");
			defaultTranslations.put("prefs.activebackground", "Color of active editor");
			defaultTranslations.put("prefs.activebackground.confirmed",
									"Color of active and confirmed editor");

			defaultTranslations.put("tempunit.recruitCost", "recruitment costs");
			defaultTranslations.put("tempunit.maintainCost", "maintainance costs");
		}

		return defaultTranslations;
	}

	class OrderEditorListPreferences extends JPanel implements PreferencesAdapter {
		private MultiEditorOrderEditorList source = null;
		private JPanel pnlStandardColor = null;
		private JPanel pnlStandardColorConfirmed = null;
		private JPanel pnlActiveColor = null;
		private JPanel pnlActiveColorConfirmed = null;
		private JPanel pnlStylesColor = null;
		private JCheckBox chkMultiEditorLayout;
		private JCheckBox chkHideButtons;
		private JCheckBox chkSyntaxHighlighting;
		private JComboBox comboSHColors = null;
		private Dimension prefDim = new Dimension(20, 20);
		private JCheckBox listModes[];

		/**
		 * Creates a new OrderEditorListPreferences object.
		 *
		 * @param source TODO: DOCUMENT ME!
		 */
		public OrderEditorListPreferences(MultiEditorOrderEditorList source) {
			this.source = source;

			this.setLayout(new GridBagLayout());

			GridBagConstraints c = new GridBagConstraints(0, 0, 1, 1, 1, 0,
														  GridBagConstraints.WEST,
														  GridBagConstraints.HORIZONTAL,
														  new Insets(0, 0, 0, 0), 0, 0);

			this.add(getLayoutPanel(), c);

			c.gridy++;
			this.add(getColorPanel(), c);

			c.gridy++;
			this.add(getHighlightPanel(), c);

			c.gridy++;
			this.add(getListModePanel(), c);
		}

		protected Container getLayoutPanel() {
			JPanel content = new JPanel(new FlowLayout(FlowLayout.LEADING, 2, 0));
			content.setBorder(new TitledBorder(getString("prefs.layout")));

			chkMultiEditorLayout = new JCheckBox(getString("prefs.multieditorlayout"),
												 source.isMultiEditorLayout());
			content.add(chkMultiEditorLayout);

			chkHideButtons = new JCheckBox(getString("prefs.hidebuttons"), source.isHideButtons());
			content.add(chkHideButtons);

			return content;
		}

		protected Container getColorPanel() {
			JPanel content = new JPanel(new GridBagLayout());
			content.setBorder(new TitledBorder(getString("prefs.colors")));

			GridBagConstraints c = new GridBagConstraints(0, 0, 1, 1, 1, 0,
														  GridBagConstraints.WEST,
														  GridBagConstraints.NONE,
														  new Insets(0, 2, 1, 1), 0, 0);

			JLabel lblStandardColor = new JLabel(getString("prefs.inactivebackground") + ": ");

			pnlStandardColor = new JPanel();
			pnlStandardColor.setBorder(new LineBorder(Color.black));
			pnlStandardColor.setPreferredSize(prefDim);
			pnlStandardColor.setBackground(source.getStandardBackgroundColor());
			pnlStandardColor.addMouseListener(new MouseAdapter() {
					public void mousePressed(MouseEvent e) {
						Color newColor = JColorChooser.showDialog(((JComponent) e.getSource()).getTopLevelAncestor(),
																  getString("prefs.backgroundcolor"),
																  ((Component) e.getSource()).getBackground());

						if(newColor != null) {
							((Component) e.getSource()).setBackground(newColor);
						}
					}
				});

			JLabel lblStandardColorConfirmed = new JLabel(getString("prefs.inactivebackground.confirmed") +
														  ": ");

			pnlStandardColorConfirmed = new JPanel();
			pnlStandardColorConfirmed.setBorder(new LineBorder(Color.black));
			pnlStandardColorConfirmed.setPreferredSize(prefDim);
			pnlStandardColorConfirmed.setBackground(source.getStandardBackgroundColorConfirmed());
			pnlStandardColorConfirmed.addMouseListener(new MouseAdapter() {
					public void mousePressed(MouseEvent e) {
						Color newColor = JColorChooser.showDialog(((JComponent) e.getSource()).getTopLevelAncestor(),
																  getString("prefs.backgroundcolor"),
																  ((Component) e.getSource()).getBackground());

						if(newColor != null) {
							((Component) e.getSource()).setBackground(newColor);
						}
					}
				});

			JLabel lblActiveColor = new JLabel(getString("prefs.activebackground") + ": ");

			pnlActiveColor = new JPanel();
			pnlActiveColor.setBorder(new LineBorder(Color.black));
			pnlActiveColor.setPreferredSize(prefDim);
			pnlActiveColor.setBackground(source.getActiveBackgroundColor());
			pnlActiveColor.addMouseListener(new MouseAdapter() {
					public void mousePressed(MouseEvent e) {
						Color newColor = JColorChooser.showDialog(((JComponent) e.getSource()).getTopLevelAncestor(),
																  getString("prefs.backgroundcolor"),
																  ((Component) e.getSource()).getBackground());

						if(newColor != null) {
							((Component) e.getSource()).setBackground(newColor);
						}
					}
				});

			JLabel lblActiveColorConfirmed = new JLabel(getString("prefs.activebackground.confirmed") +
														": ");

			pnlActiveColorConfirmed = new JPanel();
			pnlActiveColorConfirmed.setBorder(new LineBorder(Color.black));
			pnlActiveColorConfirmed.setPreferredSize(prefDim);
			pnlActiveColorConfirmed.setBackground(source.getActiveBackgroundColorConfirmed());
			pnlActiveColorConfirmed.addMouseListener(new MouseAdapter() {
					public void mousePressed(MouseEvent e) {
						Color newColor = JColorChooser.showDialog(((JComponent) e.getSource()).getTopLevelAncestor(),
																  getString("prefs.backgroundcolor"),
																  ((Component) e.getSource()).getBackground());

						if(newColor != null) {
							((Component) e.getSource()).setBackground(newColor);
						}
					}
				});

			content.add(lblActiveColor, c);
			c.gridx++;
			content.add(pnlActiveColor, c);

			c.gridx = 0;
			c.gridy++;

			content.add(lblActiveColorConfirmed, c);
			c.gridx++;
			content.add(pnlActiveColorConfirmed, c);

			c.gridx = 0;
			c.gridy++;

			content.add(lblStandardColor, c);
			c.gridx++;
			content.add(pnlStandardColor, c);

			c.gridx = 0;
			c.gridy++;

			content.add(lblStandardColorConfirmed, c);
			c.gridx++;
			content.add(pnlStandardColorConfirmed, c);

			return content;
		}

		protected Container getHighlightPanel() {
			JPanel content = new JPanel(new GridBagLayout());
			content.setBorder(new TitledBorder(getString("prefs.syntaxhighlighting")));

			GridBagConstraints c = new GridBagConstraints(0, 0, 1, 1, 1, 0,
														  GridBagConstraints.WEST,
														  GridBagConstraints.NONE,
														  new Insets(0, 2, 1, 1), 0, 0);

			chkSyntaxHighlighting = new JCheckBox(getString("prefs.syntaxhighlighting.caption"),
												  source.getUseSyntaxHighlighting());
			content.add(chkSyntaxHighlighting, c);

			c.gridy++;

			Container styles = createStylesContainer();
			content.add(styles, c);

			return content;
		}

		protected Container getListModePanel() {
			JPanel content = new JPanel(new BorderLayout(2, 2));
			content.setBorder(new TitledBorder(getString("prefs.listMode")));

			JTextArea text = new JTextArea(getString("prefs.listMode.text"));
			text.setBackground(content.getBackground());
			text.setEditable(false);
			text.setLineWrap(true);
			text.setWrapStyleWord(true);
			content.add(text, BorderLayout.NORTH);

			JPanel help = new JPanel(new GridBagLayout());
			GridBagConstraints con = new GridBagConstraints(0, 0, 1, 1, 0.5, 0,
															GridBagConstraints.WEST,
															GridBagConstraints.NONE,
															new Insets(1, 1, 1, 1), 0, 0);
			listModes = new JCheckBox[3];

			for(int i = 0; i < 3; i++) {
				listModes[i] = new JCheckBox(getString("prefs.listMode." + i));
				listModes[i].setSelected(((listMode >> (3 - i)) & 1) != 0);
				help.add(listModes[i], con);
				con.gridy++;
			}

			con.gridy = 0;
			con.gridx = 1;
			con.fill = GridBagConstraints.HORIZONTAL;

			for(int i = 0; i < 3; i++) {
				text = new JTextArea(getString("prefs.listMode." + i + ".text"));
				text.setBackground(content.getBackground());
				text.setEditable(false);
				text.setLineWrap(true);
				text.setWrapStyleWord(true);
				help.add(text, con);
				con.gridy++;
			}

			content.add(help, BorderLayout.CENTER);

			help = new JPanel();
			help.setPreferredSize(new Dimension(15, 5));
			content.add(help, BorderLayout.WEST);

			return content;
		}

		protected Container createStylesContainer() {
			JPanel content = new JPanel();

			comboSHColors = new JComboBox(getStyles());
			comboSHColors.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						StyleContainer c = (StyleContainer) comboSHColors.getSelectedItem();
						pnlStylesColor.setBackground(c.color);
					}
				});
			content.add(comboSHColors);

			pnlStylesColor = new JPanel();
			pnlStylesColor.setBorder(new LineBorder(Color.black));
			pnlStylesColor.setPreferredSize(prefDim);
			pnlStylesColor.setBackground(((StyleContainer) comboSHColors.getItemAt(0)).color);
			pnlStylesColor.addMouseListener(new MouseAdapter() {
					public void mousePressed(MouseEvent me) {
						StyleContainer sc = (StyleContainer) comboSHColors.getSelectedItem();
						Color newColor = JColorChooser.showDialog(((JComponent) me.getSource()).getTopLevelAncestor(),
																  sc.description + " Farbe",
																  sc.color);

						if(newColor != null) {
							sc.color = newColor;
							pnlStylesColor.setBackground(newColor);
						}
					}
				});
			content.add(pnlStylesColor);

			return content;
		}

		private Object[] getStyles() {
			Object styles[] = new Object[6];

			styles[0] = new StyleContainer(getString("prefs.colors.standard"),
										   OrderEditor.S_REGULAR,
										   source.getTokenColor(OrderEditor.S_REGULAR));
			styles[1] = new StyleContainer(getString("prefs.colors.keywords"),
										   OrderEditor.S_KEYWORD,
										   source.getTokenColor(OrderEditor.S_KEYWORD));
			styles[2] = new StyleContainer(getString("prefs.colors.strings"), OrderEditor.S_STRING,
										   source.getTokenColor(OrderEditor.S_STRING));
			styles[3] = new StyleContainer(getString("prefs.colors.numbers"), OrderEditor.S_NUMBER,
										   source.getTokenColor(OrderEditor.S_NUMBER));
			styles[4] = new StyleContainer(getString("prefs.colors.ids"), OrderEditor.S_ID,
										   source.getTokenColor(OrderEditor.S_ID));
			styles[5] = new StyleContainer(getString("prefs.colors.comments"),
										   OrderEditor.S_COMMENT,
										   source.getTokenColor(OrderEditor.S_COMMENT));

			return styles;
		}

		/**
		 * TODO: DOCUMENT ME!
		 */
		public void applyPreferences() {
			source.setActiveBackgroundColor(pnlActiveColor.getBackground());
			source.setActiveBackgroundColorConfirmed(pnlActiveColorConfirmed.getBackground());
			source.setStandardBackgroundColor(pnlStandardColor.getBackground());
			source.setStandardBackgroundColorConfirmed(pnlStandardColorConfirmed.getBackground());
			source.setMultiEditorLayout(chkMultiEditorLayout.isSelected());
			source.setHideButtons(chkHideButtons.isSelected());
			source.setUseSyntaxHighlighting(chkSyntaxHighlighting.isSelected());

			for(int i = 0; i < comboSHColors.getItemCount(); i++) {
				StyleContainer sc = (StyleContainer) comboSHColors.getItemAt(i);
				source.setTokenColor(sc.name, sc.color);
			}

			listMode = 0;

			for(int i = 0; i < 3; i++) {
				if(listModes[i].isSelected()) {
					listMode |= (1 << (3 - i));
				}
			}

			if(listMode == 0) {
				listMode = (1 << LIST_REGION);
			}

			saveListProperty();
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public Component getComponent() {
			return this;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public String getTitle() {
			return getString("prefs.title");
		}

		private class StyleContainer {
			/** TODO: DOCUMENT ME! */
			public String description;

			/** TODO: DOCUMENT ME! */
			public String name;

			/** TODO: DOCUMENT ME! */
			public Color color;

			/**
			 * Creates a new StyleContainer object.
			 *
			 * @param description TODO: DOCUMENT ME!
			 * @param name TODO: DOCUMENT ME!
			 * @param color TODO: DOCUMENT ME!
			 */
			public StyleContainer(String description, String name, Color color) {
				this.description = description;
				this.name = name;
				this.color = color;
			}

			/**
			 * TODO: DOCUMENT ME!
			 *
			 * @return TODO: DOCUMENT ME!
			 */
			public String toString() {
				return description;
			}
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public JTextComponent getCurrentEditor() {
		if(multiEditorLayout) {
			if((currentUnit != null) && (currentUnit.cache != null)) {
				return currentUnit.cache.orderEditor;
			} else {
				return null;
			}
		} else {
			return editor;
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Unit getCurrentUnit() {
		return currentUnit;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public PreferencesAdapter getPreferencesAdapter() {
		return new OrderEditorListPreferences(this);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param k TODO: DOCUMENT ME!
	 */
	public void addExternalKeyListener(KeyListener k) {
		keyListeners.add(k);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param k TODO: DOCUMENT ME!
	 */
	public void removeExternalKeyListener(KeyListener k) {
		keyListeners.remove(k);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param k TODO: DOCUMENT ME!
	 */
	public void addExternalCaretListener(CaretListener k) {
		caretListeners.add(k);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param k TODO: DOCUMENT ME!
	 */
	public void removeExternalCaretListener(CaretListener k) {
		caretListeners.remove(k);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param k TODO: DOCUMENT ME!
	 */
	public void addExternalFocusListener(FocusListener k) {
		focusListeners.add(k);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param k TODO: DOCUMENT ME!
	 */
	public void removeExternalFocusListener(FocusListener k) {
		focusListeners.remove(k);
	}

	private class MEKeyAdapter extends KeyAdapter {
		protected MultiEditorOrderEditorList source;

		/**
		 * Creates a new MEKeyAdapter object.
		 *
		 * @param s TODO: DOCUMENT ME!
		 */
		public MEKeyAdapter(MultiEditorOrderEditorList s) {
			source = s;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param e TODO: DOCUMENT ME!
		 */
		public void keyPressed(KeyEvent e) {
			// first external listeners
			for(Iterator iter = source.keyListeners.iterator(); iter.hasNext();) {
				((KeyListener) iter.next()).keyPressed(e);

				if(e.isConsumed()) {
					return;
				}
			}

			// now the source
			source.keyPressed(e);
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param e TODO: DOCUMENT ME!
		 */
		public void keyReleased(KeyEvent e) {
			// first external listeners
			for(Iterator iter = source.keyListeners.iterator(); iter.hasNext();) {
				((KeyListener) iter.next()).keyReleased(e);

				if(e.isConsumed()) {
					return;
				}
			}

			// now the source
			source.keyReleased(e);
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param e TODO: DOCUMENT ME!
		 */
		public void keyTyped(KeyEvent e) {
			// first external listeners
			for(Iterator iter = source.keyListeners.iterator(); iter.hasNext();) {
				((KeyListener) iter.next()).keyTyped(e);

				if(e.isConsumed()) {
					return;
				}
			}

			// now the source
			source.keyTyped(e);
		}
	}

	private class MEFocusAdapter extends FocusAdapter {
		protected MultiEditorOrderEditorList source;

		/**
		 * Creates a new MEFocusAdapter object.
		 *
		 * @param s TODO: DOCUMENT ME!
		 */
		public MEFocusAdapter(MultiEditorOrderEditorList s) {
			source = s;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param e TODO: DOCUMENT ME!
		 */
		public void focusGained(FocusEvent e) {
			// first the source (maybe selection event!)
			source.focusGained(e);

			// then external listeners
			for(Iterator iter = source.focusListeners.iterator(); iter.hasNext();) {
				((FocusListener) iter.next()).focusGained(e);
			}
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param e TODO: DOCUMENT ME!
		 */
		public void focusLost(FocusEvent e) {
			// first external listeners
			for(Iterator iter = source.focusListeners.iterator(); iter.hasNext();) {
				((FocusListener) iter.next()).focusLost(e);
			}

			// now the source
			source.focusLost(e);
		}
	}

	private class MECaretAdapter implements CaretListener {
		protected MultiEditorOrderEditorList source;

		/**
		 * Creates a new MECaretAdapter object.
		 *
		 * @param s TODO: DOCUMENT ME!
		 */
		public MECaretAdapter(MultiEditorOrderEditorList s) {
			source = s;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param e TODO: DOCUMENT ME!
		 */
		public void caretUpdate(CaretEvent e) {
			for(Iterator iter = source.caretListeners.iterator(); iter.hasNext();) {
				((CaretListener) iter.next()).caretUpdate(e);
			}
		}
	}

	// this is a very special thread to position the actual orderEditor into the viewport of the
	// scrollpane
	// In very ugly situations it is called twice, but that does not really hurt
	private class SwingGlitchThread implements Runnable {
		SwingGlitchThread() {
		}

		private int loopCounter = 0;

		/**
		 * TODO: DOCUMENT ME!
		 */
		public void run() {
			// waiting for isValid() to become true
			// does not work, because when we really
			// need it, i.e. editors are shown for the
			// first time they stay invalid.
			Rectangle viewRect = scpContent.getViewport().getViewRect();

			if(log.isDebugEnabled()) {
				log.debug("MultiEditorOrderEditorList.selectionChanged.runnable: viewRect:" +
						  viewRect);
			}

			if((currentUnit != null) && (currentUnit.cache != null) &&
				   (currentUnit.cache.orderEditor != null)) {
				Rectangle bounds = currentUnit.cache.orderEditor.getBounds();
				log.debug("MultiEditorOrderEditorList.selectionChanged.runnable: Bounds:" + bounds);

				while(!viewRect.contains(viewRect.x, bounds.y, viewRect.width,
											 Math.min(viewRect.height, bounds.height))) {
					Point newPos = null;

					if(bounds.height < viewRect.height) {
						newPos = new Point(0, bounds.y - ((viewRect.height - bounds.height) / 2));
					} else {
						newPos = new Point(0, bounds.y);
					}

					newPos.y = Math.max(0, newPos.y);

					if(log.isDebugEnabled()) {
						log.debug("MultiEditorOrderEditorList.selectionChanged.runnable: newPos : " +
								  newPos);
					}

					Rectangle newRect = new Rectangle(viewRect);
					newRect.setLocation(newPos);

					//currentUnit.cache.orderEditor.scrollRectToVisible(newRect);
					content.scrollRectToVisible(newRect);

					// scpContent.getViewport().setViewPosition(newPos);
					viewRect = scpContent.getViewport().getViewRect();
					bounds = currentUnit.cache.orderEditor.getBounds();

					if(++loopCounter > 3) {
						loopCounter = 0;

						break;
					}
				}

				log.debug("MultiEditorOrderEditorList.selectionChanged.runnable: viewRect after:" +
						  viewRect);
				log.debug("MultiEditorOrderEditorList.selectionChanged.runnable: Bounds after:" +
						  bounds);
			}

			content.validate();
			scpContent.getViewport().invalidate();
			repaint();
		}
	}

	private class UpdateThread implements Runnable {
		/** TODO: DOCUMENT ME! */
		public OrderEditor e = null;

		/**
		 * TODO: DOCUMENT ME!
		 */
		public void run() {
			e.formatTokens();
			e.setModified(false);
		}
	}

	// for "check orders", "create temp unit" and "delete temp unit"
	protected class ButtonPanel extends JPanel implements ActionListener {
		private JCheckBox checkOrderConfirm = null;
		private JButton btnCreateTempUnit = null;
		private JButton btnDeleteTempUnit = null;
		private TempUnitDialog dialog;

		/**
		 * Creates a new ButtonPanel object.
		 */
		public ButtonPanel() {
			DesktopEnvironment.registerShortcutListener(new com.eressea.demo.desktop.ShortcutListener() {
					List shortcuts = null;

					public Iterator getShortCuts() {
						if(shortcuts == null) {
							shortcuts = CollectionFactory.createLinkedList();
							shortcuts.add(KeyStroke.getKeyStroke(KeyEvent.VK_T, KeyEvent.CTRL_MASK));
							shortcuts.add(KeyStroke.getKeyStroke(KeyEvent.VK_T,
																 KeyEvent.CTRL_MASK |
																 KeyEvent.SHIFT_MASK));
						}

						return shortcuts.iterator();
					}

					public void shortCut(KeyStroke s) {
						int index = shortcuts.indexOf(s);

						switch(index) {
						case 0:
							createTempUnit();

							break;

						case 1:
							deleteTempUnit();

							break;
						}
					}

					public String getListenerDescription() {
						return getString("shortcuts.title");
					}

					public String getShortcutDescription(Object stroke) {
						int index = shortcuts.indexOf(stroke);

						return getString("shortcuts.description." + String.valueOf(index));
					}
				});

			checkOrderConfirm = new JCheckBox(getString("chk.orderconfirmation"), false);
			checkOrderConfirm.addActionListener(this);
			checkOrderConfirm.setEnabled(false);

			Icon icon = new ImageIcon(com.eressea.resource.ResourcePathClassLoader.getResourceStatically("images/gui/createtempunit.gif"));
			btnCreateTempUnit = new JButton(icon);
			btnCreateTempUnit.addActionListener(this);
			btnCreateTempUnit.setEnabled(false);

			icon = new ImageIcon(com.eressea.resource.ResourcePathClassLoader.getResourceStatically("images/gui/deletetempunit.gif"));
			btnDeleteTempUnit = new JButton(icon);
			btnDeleteTempUnit.addActionListener(this);
			btnDeleteTempUnit.setEnabled(false);

			this.setLayout(new GridBagLayout());
			this.setBorder(new EmptyBorder(4, 4, 4, 4));

			GridBagConstraints c = new GridBagConstraints();

			c.anchor = GridBagConstraints.WEST;
			this.add(checkOrderConfirm, c);

			c.anchor = GridBagConstraints.CENTER;
			c.gridx = 1;
			c.weightx = 1.0;
			c.fill = GridBagConstraints.HORIZONTAL;
			this.add(new JPanel(), c);

			c.anchor = GridBagConstraints.CENTER;
			c.gridx = 2;
			this.add(btnCreateTempUnit, c);

			c.gridx = 3;
			this.add(btnDeleteTempUnit, c);

			// update the check box when the order confirmation status changes
			dispatcher.addOrderConfirmListener(new OrderConfirmListener() {
					public void orderConfirmationChanged(OrderConfirmEvent e) {
						if(!this.equals(e.getSource())) {
							for(Iterator iter = e.getUnits().iterator(); iter.hasNext();) {
								Unit u = (Unit) iter.next();

								if(u.equals(currentUnit)) {
									checkOrderConfirm.setSelected(u.ordersConfirmed);

									return;
								}
							}
						}
					}
				});

			// update the check box when a different unit is selected
			dispatcher.addSelectionListener(new SelectionListener() {
					public void selectionChanged(SelectionEvent e) {
						if((e.getActiveObject() != null) && e.getActiveObject() instanceof Unit) {
							Unit u = (Unit) e.getActiveObject();

							checkOrderConfirm.setSelected(u.ordersConfirmed);
						}
					}
				});
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param p1 TODO: DOCUMENT ME!
		 */
		public void actionPerformed(java.awt.event.ActionEvent p1) {
			if(p1.getSource() == checkOrderConfirm) {
				toggleOrderConfirmation();
			}

			if(p1.getSource() == btnCreateTempUnit) {
				createTempUnit();
			}

			if(p1.getSource() == btnDeleteTempUnit) {
				deleteTempUnit();
			}
		}

		protected void toggleOrderConfirmation() {
			if(currentUnit != null) {
				currentUnit.ordersConfirmed = !currentUnit.ordersConfirmed;
				checkOrderConfirm.setSelected(currentUnit.ordersConfirmed);

				List units = CollectionFactory.createLinkedList();

				units.add(currentUnit);
				dispatcher.fire(new OrderConfirmEvent(this, units));
			}
		}

		protected void createTempUnit() {
			if((currentUnit != null) && currentUnit.getFaction().isPrivileged()) {
				// Use the current unit as the parent or its parent if it
				// is itself a temp unit
				Unit parentUnit = currentUnit;

				if(currentUnit instanceof TempUnit) {
					parentUnit = ((TempUnit) currentUnit).getParent();
				}

				UnitID id = UnitID.createTempID(data, settings, parentUnit);
				createTempImpl(parentUnit, id, parentUnit.getRegion());
			}
		}

		private void createTempImpl(Unit parentUnit, UnitID id, Region parentRegion) {
			int unitIntID = id.intValue();
			int newIDInt = unitIntID;
			UnitID newID = null;

			//			for(newIDInt = unitIntID; newIDInt != (unitIntID - 1);
			//					newIDInt = (newIDInt + (1 % IDBaseConverter.getMaxId()))) {
			//				if(parentRegion.getUnit(UnitID.createUnitID(-newIDInt)) == null) {
			//					break;
			//				}
			//			}
			if(!settings.getProperty("MultiEditorOrderEditorList.ButtonPanel.ShowTempUnitDialog",
										 "true").equalsIgnoreCase("true")) {
				// don't show any dialogs, simply create the tempunit and finish.
				TempUnit tempUnit = parentUnit.createTemp(UnitID.createUnitID(-newIDInt));
				dispatcher.fire(new TempUnitEvent(this, tempUnit, TempUnitEvent.CREATED));
				//dispatcher.fire(new SelectionEvent(this, null, tempUnit));
			} else {
				// do all the tempunit-dialog-stuff
				newID = UnitID.createUnitID(newIDInt);

				if(dialog == null) {
					dialog = new TempUnitDialog((Frame) this.getTopLevelAncestor(), this, settings);
				}

				// ask the user for a valid id repeatedly
				boolean first = true;

				while(true) {
					if(first) { // reset if it's the first dialog for this temp unit
						dialog.show(newID.toString(), parentUnit.getName());
					} else { // do not reset if we had formerly wrong data
						dialog.show(parentUnit.getName());
					}

					first = false;

					if(dialog.isApproved()) {
						String tempID = dialog.getID();

						if((tempID == null) || tempID.trim().equals("")) {
							//JOptionPane.show...
							return;
						}

						try {
							int realNewIDInt = IDBaseConverter.parse(tempID);
							UnitID checkID = UnitID.createUnitID(-realNewIDInt);

							if(data.tempUnits().get(checkID) == null) {
								TempUnit tempUnit = parentUnit.createTemp(checkID);

								// Name
								String name = dialog.getName();

								if((name != null) && !name.trim().equals("")) {
									tempUnit.setName(name);
									data.getGameSpecificStuff().getOrderChanger().addNamingOrder(tempUnit,
																								 name);
								}

								// extended features
								if(dialog.wasExtendedDialog()) {
									// Recruiting
									String recruit = dialog.getRecruit();

									if(recruit != null) {
										try {
											int i = Integer.parseInt(recruit);

											if(i > 0) {
												data.getGameSpecificStuff().getOrderChanger()
													.addRecruitOrder(tempUnit, i);

												if(dialog.isGiveMaintainCost() ||
													   dialog.isGiveRecruitCost()) {
													ItemType silverType = data.rules.getItemType(StringID.create("Silber"),
																								 false);
													String silver = null;

													if(silverType != null) {
														silver = silverType.getName();
													} else {
														silver = "Silver";
													}

													if(dialog.isGiveRecruitCost()) {
														int recCost = 0;

														if(parentUnit.realRace != null) {
															recCost = parentUnit.realRace.getRecruitmentCosts();
														} else {
															recCost = parentUnit.race.getRecruitmentCosts();
														}

														recCost = i * recCost;

														// TODO(pavkovic) extract to EresseaOrderChanger
														String tmpOrders = Translations.getOrderTranslation(EresseaOrderConstants.O_GIVE) +
																		   " TEMP " +
																		   tempUnit.getID()
																				   .toString() +
																		   " " + recCost + " " +
																		   silver + "; " +
																		   getString("tempunit.recruitCost");
														parentUnit.addOrders(tmpOrders);
													}

													if(dialog.isGiveMaintainCost()) {
														String tmpOrders = Translations.getOrderTranslation(EresseaOrderConstants.O_GIVE) +
																		   " TEMP " +
																		   tempUnit.getID()
																				   .toString() +
																		   " " +
																		   String.valueOf(10 * i) +
																		   " " + silver + "; " +
																		   getString("tempunit.maintainCost");
														parentUnit.addOrders(tmpOrders);
													}

													// TODO(pavkovic) extract to EresseaOrderChanger
													dispatcher.fire(new UnitOrdersEvent(this,
																						parentUnit));
												}
											}
										} catch(NumberFormatException nfe) {
										} catch(MissingResourceException mre) {
										}
									}

									// simple order
									String order = dialog.getOrder();

									if((order != null) && !order.trim().equals("")) {
										tempUnit.addOrders(order);
									}

									// description
									String descript = dialog.getDescript();

									if((descript != null) && !descript.trim().equals("")) {
										descript.replace('\n', ' ');
										tempUnit.setDescription(descript);
										data.getGameSpecificStuff().getOrderChanger()
											.addDescribeUnitOrder(tempUnit, descript);
									}
								}

								// data update
								dispatcher.fire(new TempUnitEvent(this, tempUnit,
																  TempUnitEvent.CREATED));
								//TODO: reallay fire an update here?
								//dispatcher.fire(new SelectionEvent(this, null, tempUnit));

								return;
							} else {
								JOptionPane.showMessageDialog(this,
															  getString("msg.duplicatetempid.text"),
															  getString("msg.duplicatetempid.title"),
															  JOptionPane.ERROR_MESSAGE);
							}
						} catch(NumberFormatException nfe) {
							JOptionPane.showMessageDialog(this,
														  getString("msg.invalidtempid.text"),
														  getString("msg.invalidtempid.title"),
														  JOptionPane.ERROR_MESSAGE);
						}
					} else {
						// dialog canceled
						return;
					}
				}
			}
		}

		protected void deleteTempUnit() {
			if((currentUnit != null) && currentUnit instanceof TempUnit) {
				Unit u = ((TempUnit) currentUnit).getParent();

				dispatcher.fire(new TempUnitEvent(this, (TempUnit) currentUnit,
												  TempUnitEvent.DELETED), true);
				u.deleteTemp(currentUnit.getID(), data);
				dispatcher.fire(new SelectionEvent(this, null, u));
			}
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param enabled TODO: DOCUMENT ME!
		 */
		public void setConfirmEnabled(boolean enabled) {
			checkOrderConfirm.setEnabled(enabled);
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param enabled TODO: DOCUMENT ME!
		 */
		public void setCreationEnabled(boolean enabled) {
			btnCreateTempUnit.setEnabled(enabled);
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param enabled TODO: DOCUMENT ME!
		 */
		public void setDeletionEnabled(boolean enabled) {
			btnDeleteTempUnit.setEnabled(enabled);
		}

		/**
		 * TODO: DOCUMENT ME!
		 */
		public void currentUnitChanged() {
			boolean enabled = (currentUnit != null) && currentUnit.getFaction().isPrivileged();

			setConfirmEnabled(enabled);
			setCreationEnabled(enabled);
			setDeletionEnabled(enabled && (currentUnit instanceof TempUnit));
		}
	}

	/**
	 * A simple JPanel that implements the Scrollable interface and is used to hold the order
	 * editors.
	 */
	private class ScrollPanel extends JPanel implements Scrollable {
		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public Dimension getPreferredScrollableViewportSize() {
			return this.getPreferredSize();
		}

		/**
		 * If the parent of this component is an instance of JViewport this method returns the
		 * maximum of the original preferred size and the viewport size.
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public Dimension getPreferredSize() {
			Container parent = this.getParent();

			// this special case has become necessary with the
			// implementation of the Scrollable interface
			if(parent instanceof JViewport) {
				JViewport viewport = (JViewport) parent;
				Dimension psize = super.getPreferredSize();
				psize.width = Math.max(psize.width, viewport.getWidth());
				psize.height = Math.max(psize.height, viewport.getHeight());

				return psize;
			} else {
				return super.getPreferredSize();
			}
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param visibleRect TODO: DOCUMENT ME!
		 * @param orientation TODO: DOCUMENT ME!
		 * @param direction TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public int getScrollableUnitIncrement(java.awt.Rectangle visibleRect, int orientation,
											  int direction) {
			if(orientation == SwingConstants.HORIZONTAL) {
				return visibleRect.width / 5;
			} else {
				Component lastVisibleComponent = null;
				Component nextComponent = null;

				if(direction < 0) { // up
					lastVisibleComponent = this.getComponentAt(visibleRect.x, visibleRect.y);

					if(lastVisibleComponent != null) {
						if(visibleRect.y > lastVisibleComponent.getY()) {
							// component is not fully visible
							return visibleRect.y - lastVisibleComponent.getY();
						} else {
							// component is fully visible, get next component
							List components = Arrays.asList(this.getComponents());
							int count = components.indexOf(lastVisibleComponent) - 1;

							if((count >= 0) && (count < this.getComponentCount())) {
								nextComponent = this.getComponent(count);

								return nextComponent.getHeight();
							}
						}
					}
				} else { // down
					lastVisibleComponent = this.getComponentAt(visibleRect.x,
															   visibleRect.y + visibleRect.height);

					if(lastVisibleComponent != null) {
						if((visibleRect.y + visibleRect.height) < (lastVisibleComponent.getY() +
							   lastVisibleComponent.getHeight())) {
							// component is not fully visible
							return (lastVisibleComponent.getY() + lastVisibleComponent.getHeight()) -
								   visibleRect.y - visibleRect.height;
						} else {
							// component is fully visible, get next component
							List components = Arrays.asList(this.getComponents());
							int count = components.indexOf(lastVisibleComponent) + 1;

							if((count >= 0) && (count < this.getComponentCount())) {
								nextComponent = this.getComponent(count);

								return nextComponent.getHeight();
							}
						}
					}
				}

				// fallback:
				return visibleRect.height / 5;
			}
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param visibleRect TODO: DOCUMENT ME!
		 * @param orientation TODO: DOCUMENT ME!
		 * @param direction TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public int getScrollableBlockIncrement(java.awt.Rectangle visibleRect, int orientation,
											   int direction) {
			if(orientation == SwingConstants.HORIZONTAL) {
				return visibleRect.width;
			} else {
				return visibleRect.height;
			}
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public boolean getScrollableTracksViewportWidth() {
			return false;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public boolean getScrollableTracksViewportHeight() {
			return false;
		}
	}
}
