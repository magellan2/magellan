// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.swing.completion;


import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JWindow;
import javax.swing.ListSelectionModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

import com.eressea.completion.AutoCompletion;
import com.eressea.util.CollectionFactory;
import com.eressea.util.Completion;
import com.eressea.util.JVMUtilities;
import com.eressea.util.logging.Logger;

/**
 *
 * @author  Andreas
 * @version
 */
public class ListCompletionGUI extends AbstractCompletionGUI {
	private final static Logger log = Logger.getInstance(ListCompletionGUI.class);

	protected ListPane listPane;
	protected AutoCompletion ac;
	protected int[] specialKeys;

	public void init(AutoCompletion ac) {
		this.ac=ac;
		listPane=new ListPane();
		specialKeys=new int[2];
		specialKeys[0]=KeyEvent.VK_UP;
		specialKeys[1]=KeyEvent.VK_DOWN;
	}

	public void offerCompletion(JTextComponent editor,Collection completions,String stub) {
		listPane.choiceList.setListData(completions.toArray());
		listPane.choiceList.setSelectedIndex(0);
		listPane.choiceList.setVisibleRowCount(0);

		// align list pane
		try{
			Rectangle caretBounds = editor.modelToView(editor.getCaretPosition());
			Point p = new Point(editor.getLocationOnScreen());
			p.translate(caretBounds.x, caretBounds.y + caretBounds.height);
			if ((p.getY() + listPane.getHeight()) > Toolkit.getDefaultToolkit().getScreenSize().getHeight()) {
				p.translate(0, (int)( -listPane.getHeight() - caretBounds.getHeight() ));
			}
			int outOfSight = (int)((p.getX() + listPane.getWidth()) - Toolkit.getDefaultToolkit().getScreenSize().getWidth());
			if (outOfSight > 0) {
				p.translate(-outOfSight, 0);
			}
			listPane.setLocation(p);
		}catch(BadLocationException ble) {}

		if (!listPane.isVisible()) {
			listPane.setVisible(true);
		}
	}

	// sets the currently selected index in the list
	public void cycleCompletion(JTextComponent editor,Collection completions, String stub, int index) {
		listPane.setSelectedIndex(index);
	}

	public void stopOffer() {
		listPane.setVisible(false);
	}

	public Completion getSelectedCompletion() {
		return (Completion)listPane.choiceList.getSelectedValue();
	}

	// Inserts a completion triggered by the Choice list
	protected void insertCompletion() {
		ac.insertCompletion((Completion)listPane.choiceList.getSelectedValue());
	}

	// pavkovic 2003.01.28: this is a Map of the default Translations mapped to this class
	// it is called by reflection (we could force the implementation of an interface,
	// this way it is more flexible.)
	// Pls use this mechanism, so the translation files can be created automagically
	// by inspecting all classes.
	private static Map defaultTranslations;
	public synchronized static Map getDefaultTranslations() {
		if(defaultTranslations == null) {
			defaultTranslations = CollectionFactory.createHashtable();
			defaultTranslations.put("gui.title","List");
		}
		return defaultTranslations;
	}


	class CompletionList extends JList implements KeyListener {

		public CompletionList() {
			this.addKeyListener(this);
			this.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					CompletionList.this.mouseClicked(e);
				}
			});
		}

		public boolean isManagingFocus() {
			return true;
		}

		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_TAB || e.getKeyCode() == KeyEvent.VK_ENTER) {
				insertCompletion();
				this.getTopLevelAncestor().setVisible(false);
				e.consume();
			}

			if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				this.getTopLevelAncestor().setVisible(false);
				e.consume();
			}
		}

		public void keyReleased(KeyEvent e) {}
		public void keyTyped(KeyEvent e) {}

		private void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2) {
				insertCompletion();
				this.getTopLevelAncestor().setVisible(false);
				e.consume();
			}
		}
	}

	class ListPane extends JWindow {
		public CompletionList choiceList = null;

		public ListPane() {
			super (new JFrame() {
					public boolean isShowing() {
						return true;
					}
				});
			// call setFocusableWindowState (true) on java 1.4 while staying compatible with Java 1.3
			JVMUtilities.setFocusableWindowState(this,true);

			JScrollPane scrollPane = new JScrollPane();

			scrollPane.setCursor(Cursor.getDefaultCursor());
			choiceList = new CompletionList();
			choiceList.setFont(new Font("Monospaced", Font.PLAIN, 10));
			choiceList.setBackground(new Color(255, 255, 204));
			choiceList.setMinimumSize(new Dimension(100, 50));
			choiceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

			scrollPane.setViewportView(choiceList);
			scrollPane.setBounds(new Rectangle(0, 100, 150, 75));
			scrollPane.setMaximumSize(new Dimension(150, 75));

			this.getContentPane().add( scrollPane );

			this.pack();

			this.addFocusListener(new FocusAdapter() {
				public void focusGained(FocusEvent e) {
					choiceList.requestFocus();
				}
			});
		}

		public void setSelectedIndex(int index) {
			choiceList.setSelectedIndex(index);
		}
	}

	public int[] getSpecialKeys() {
		return specialKeys;
	}

	public void specialKeyPressed(int key) {
		if (listPane.isVisible()) {
			listPane.requestFocus();
		}
	}

	public boolean isOfferingCompletion() {
		return listPane.isVisible();
	}

	public boolean editorMayLoseFocus() {
		return true;
	}

	public boolean editorMayUpdateCaret() {
		return false;
	}
}
