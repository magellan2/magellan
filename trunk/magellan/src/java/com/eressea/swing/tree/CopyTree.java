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
 */

package com.eressea.swing.tree;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.InputMap;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.eressea.event.EventDispatcher;

/**
 * An extended JTree that supports CopyToClipboard by STRG+C and STRG+INSERT
 *
 * @author Ulrich Küster
 */
public class CopyTree extends JTree implements KeyListener {
	private EventDispatcher dispatcher = null;

	/**
	 * Creates a new CopyTree object.
	 *
	 * @param model TODO: DOCUMENT ME!
	 * @param dispatcher TODO: DOCUMENT ME!
	 */
	public CopyTree(TreeModel model, EventDispatcher dispatcher) {
		super(model);
		initTree(dispatcher);
	}

	/**
	 * Creates a new CopyTree object.
	 *
	 * @param dispatcher TODO: DOCUMENT ME!
	 */
	public CopyTree(EventDispatcher dispatcher) {
		super();
		initTree(dispatcher);
	}

	/**
	 * Creates a new CopyTree object.
	 *
	 * @param root TODO: DOCUMENT ME!
	 * @param dispatcher TODO: DOCUMENT ME!
	 */
	public CopyTree(TreeNode root, EventDispatcher dispatcher) {
		super(root);
		initTree(dispatcher);
	}

	/**
	 * Used for initialization issues
	 *
	 * @param dispatcher TODO: DOCUMENT ME!
	 */
	private void initTree(EventDispatcher dispatcher) {
		// delete F2-key-binding to startEditing to allow bookmarking to be activ
		// @see com.eressea.demo.desktop.BookmarkManager
		try {
			((InputMap) getInputMap()).put(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0), "none");
		} catch(java.lang.NoSuchMethodError e) {
			// in case of 1.2.2 JRE
			// it's a bit crude, but unregisterKeyboardAction didn't work on my computer -
			// for unknown reasons... so this is the compromise
			this.unregisterKeyboardAction(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));

			// probably 1.2.2 users will have to live without bookmarking ;(
		}

		this.dispatcher = dispatcher;

		//		shortcuts = new Vector();
		// 0-1 copyshortcut
		//		shortcuts.add(KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, KeyEvent.CTRL_MASK));
		//		shortcuts.add(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_MASK));
		// register for shortcut
		this.addKeyListener(this);
	}

	private void shortCut_Copy() {
		if(!hasFocus()) {
			return;
		}

		String text = "";
		TreePath selectedPaths[] = this.getSelectionPaths();

		if(selectedPaths != null) {
			for(int i = 0; i < selectedPaths.length; i++) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectedPaths[i].getLastPathComponent();

				if(node != null) {
					Object obj = node.getUserObject();

					if(obj instanceof SupportsClipboard) {
						SupportsClipboard s = (SupportsClipboard) obj;

						if(text.length() == 0) {
							text = s.getClipboardValue();
						} else {
							text += ("\n" + s.getClipboardValue());
						}
					} else {
						if(text.length() == 0) {
							text = obj.toString();
						} else {
							text += ("\n" + obj.toString());
						}
					}
				}
			}
		}

		getToolkit().getSystemClipboard().setContents(new java.awt.datatransfer.StringSelection(text),
													  null);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param e TODO: DOCUMENT ME!
	 */
	public void keyPressed(KeyEvent e) {
		if((e.getModifiers() == InputEvent.CTRL_MASK) &&
			   ((e.getKeyCode() == KeyEvent.VK_C) || (e.getKeyCode() == KeyEvent.VK_INSERT))) {
			shortCut_Copy();
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param e TODO: DOCUMENT ME!
	 */
	public void keyTyped(KeyEvent e) {
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param e TODO: DOCUMENT ME!
	 */
	public void keyReleased(KeyEvent e) {
	}
}
