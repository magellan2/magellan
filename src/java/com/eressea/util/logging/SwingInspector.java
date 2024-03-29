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

package com.eressea.util.logging;

import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.text.JTextComponent;
import javax.swing.text.Keymap;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class SwingInspector {
	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param comp TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static String printKeybindings(JTextComponent comp) {
		return printInputMaps(comp) + printKeysAndActions(comp);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param comp TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static String printInputMaps(JComponent comp) {
		return printMap(comp.getInputMap(JComponent.WHEN_FOCUSED), "WHEN_FOCUSED",
						comp.getActionMap()) +
			   printMap(comp.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT),
						"WHEN_ANCESTOR_OF_FOCUSED_COMPONENT", comp.getActionMap()) +
			   printMap(comp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW),
						"WHEN_IN_FOCUSED_WINDOW", comp.getActionMap()) +
			   printMap(comp.getActionMap());
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param comp TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static String printKeysAndActions(JTextComponent comp) {
		return printKeysAndActions(comp.getKeymap()).toString();
	}

	private static String printMap(InputMap map, String desc, ActionMap actionmap) {
		StringBuffer sb = new StringBuffer();

		if(map != null) {
			KeyStroke keystrokes[] = map.keys();
			sb.append("InputMap " + map + " for: " + desc + "\n");

			if(keystrokes == null) {
				sb.append("KeyStrokes[] ist null!" + "\n");
			} else {
				sb.append(keystrokes.length + "\n");
				sb.append(printKeyStrokes(keystrokes, new ActionProvider(map, actionmap)));
			}

			sb.append("-------------------------------------------" + "\n");
			sb.append(printMap(map.getParent(), desc, actionmap));
		}

		return sb.toString();
	}

	private static String printMap(ActionMap map) {
		StringBuffer sb = new StringBuffer();

		if(map != null) {
			Object keynames[] = map.allKeys();

			if(keynames != null) {
				for(int i = 0; i < keynames.length; i++) {
					sb.append("\"" + keynames[i] + "\"" + "\n");
				}
			}
		}

		return sb.toString();
	}

	//Recursive call that prints out the keys and actions for keymap, 
	//and all of its parents.
	private static StringBuffer printKeysAndActions(Keymap keymap) {
		StringBuffer sb = new StringBuffer();

		if(keymap != null) {
			Action actions[] = keymap.getBoundActions();
			KeyStroke keystrokes[] = keymap.getBoundKeyStrokes();
			sb.append("Keymap: " + keymap.getName() + "\n");
			sb.append(actions.length + " " + keystrokes.length + "\n");
			sb.append(printKeyStrokes(keystrokes, new ActionProvider(keymap)));
			sb.append("-------------------------------------------" + "\n");
			sb.append(printKeysAndActions(keymap.getResolveParent()));
		}

		return sb;
	}

	private static StringBuffer printKeyStrokes(KeyStroke keystrokes[],
												ActionProvider actionprovider) {
		StringBuffer sb = new StringBuffer();

		for(int i = 0; i < keystrokes.length; i++) {
			KeyStroke keystroke = keystrokes[i];

			if((keystroke.getModifiers() & java.awt.Event.CTRL_MASK) != 0) {
				sb.append("Ctrl  ");
			} else {
				sb.append("      ");
			}

			if((keystroke.getModifiers() & java.awt.Event.SHIFT_MASK) != 0) {
				sb.append("Shift ");
			} else {
				sb.append("      ");
			}

			if((keystroke.getModifiers() & java.awt.Event.META_MASK) != 0) {
				sb.append("Meta  ");
			} else {
				sb.append("      ");
			}

			if((keystroke.getModifiers() & java.awt.Event.ALT_MASK) != 0) {
				sb.append("Alt   ");
			} else {
				sb.append("      ");
			}

			String keyText = ppString((keystroke.getKeyChar() != 0)
									  ? String.valueOf(keystroke.getKeyChar())
									  : KeyEvent.getKeyText(keystroke.getKeyCode()), 15);
			sb.append(keyText + " ");

			if(keystroke.isOnKeyRelease()) {
				sb.append("released ");
			} else {
				sb.append("pressed  ");
			}

			sb.append(": " + actionprovider.getAction(keystroke));
			sb.append(keystroke + "\n");
		}

		return sb;
	}

	private static String ppString(String str, int len) {
		StringBuffer sb = new StringBuffer(str);

		for(int i = 0; i < (len - str.length()); i++) {
			sb.append(" ");
		}

		return sb.toString();
	}

	private static class ActionProvider {
		private Keymap keymap;
		private InputMap inputmap;
		private ActionMap actionmap;

		ActionProvider(Keymap km) {
			keymap = km;
		}

		ActionProvider(InputMap im, ActionMap am) {
			inputmap = im;
			actionmap = am;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param keystroke TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public String getAction(KeyStroke keystroke) {
			if(keymap != null) {
				return keymap.getAction(keystroke).toString();
			}

			if(inputmap != null) {
				Object action = inputmap.get(keystroke);

				if((action == null) || (actionmap.get(action) == null)) {
					return null;
				}

				return "\"" + action + "\" [" + actionmap.get(action) + "]";
			}

			return null;
		}
	}
}
