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

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Document;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import javax.swing.undo.UndoManager;

import com.eressea.GameData;
import com.eressea.Unit;
import com.eressea.completion.OrderParser;
import com.eressea.event.EventDispatcher;
import com.eressea.event.SelectionEvent;
import com.eressea.event.SelectionListener;
import com.eressea.event.UnitOrdersEvent;
import com.eressea.event.UnitOrdersListener;
import com.eressea.util.CollectionFactory;
import com.eressea.util.Colors;
import com.eressea.util.MergeLineReader;
import com.eressea.util.OrderToken;
import com.eressea.util.logging.Logger;

/**
 * A text pane for convenient editing and handling of Eressea orders.
 */
public class OrderEditor extends JTextPane implements DocumentListener, KeyListener,
													  SelectionListener, FocusListener
{
	private static final Logger log = Logger.getInstance(OrderEditor.class);

	// Style name constants

	/** TODO: DOCUMENT ME! */
	public static final String S_REGULAR = "regular";

	/** TODO: DOCUMENT ME! */
	public static final String S_KEYWORD = "keyword";

	/** TODO: DOCUMENT ME! */
	public static final String S_STRING = "string";

	/** TODO: DOCUMENT ME! */
	public static final String S_NUMBER = "number";

	/** TODO: DOCUMENT ME! */
	public static final String S_ID = "id";

	/** TODO: DOCUMENT ME! */
	public static final String S_COMMENT = "comment";
	private Unit unit = null;
	private boolean modified = false;
	private List orders = new LinkedList();
	private OrderParser parser = null;
	private Properties settings = null;
	private boolean highlightSyntax = true;
	private boolean ignoreModifications = false;
	private EventDispatcher dispatcher = null;

	//private GameData data = null;
	private UndoManager undoMgr = null;
	private DocumentUpdateRunnable docUpdateThread = new DocumentUpdateRunnable(null); // keep this udpate runnable instead of re-creating it over and over again
	private OrderEditorCaret myCaret = null;

	/**
	 * Creates a new OrderEditor object.
	 *
	 * @param data TODO: DOCUMENT ME!
	 * @param settings TODO: DOCUMENT ME!
	 * @param _undoMgr TODO: DOCUMENT ME!
	 * @param d TODO: DOCUMENT ME!
	 */
	public OrderEditor(GameData data, Properties settings, UndoManager _undoMgr, EventDispatcher d) {
		super();

		// pavkovic 2002.11.11: use own caret for more logical refreshing
		myCaret = new OrderEditorCaret();
		setCaret(myCaret);

		// for new DefaultEditorKit
		setEditorKit(new OrderEditorKit());
		this.settings = settings;

		this.parser = (data != null) ? data.getGameSpecificStuff().getOrderParser(data) : null;

		this.undoMgr = _undoMgr;

		this.dispatcher = d;

		//this.dispatcher.addGameDataListener(this);
		highlightSyntax = (Boolean.valueOf(settings.getProperty("OrderEditor.highlightSyntax", "true")).booleanValue());

		getDocument().addDocumentListener(this);

		SignificantUndos sigUndos = new SignificantUndos();
		getDocument().addUndoableEditListener(sigUndos);
		getDocument().addDocumentListener(sigUndos);

		addKeyListener(this);
		addFocusListener(this);

		dispatcher.addUnitOrdersListener(new UnitOrdersListener() {
				public void unitOrdersChanged(UnitOrdersEvent e) {
					// refresh local copy of orders in editor
					if((e.getSource() != OrderEditor.this) &&
						   e.getUnit().equals(OrderEditor.this.unit)) {
						setOrders(OrderEditor.this.unit.getOrders());
					}
				}
			});
		initStyles();

		//bind ctrl-shift C to OrderEditor
		super.getInputMap().put(KeyStroke.getKeyStroke(OrderEditorKit.copyLineActionKeyStroke),
								OrderEditorKit.copyLineAction);

		//if(log.isDebugEnabled()) {
		//	if(!swingInspected) {
		//		swingInspected = true;
		//		log.debug("KEYBINDING FOR OrderEditor:\n"+
		//				  com.eressea.util.logging.SwingInspector.printKeybindings(this));
		//	}
		//}
	}

	//private boolean swingInspected;
	public void changedUpdate(DocumentEvent e) {
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param e TODO: DOCUMENT ME!
	 */
	public void insertUpdate(DocumentEvent e) {
		removeUpdate(e);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param e TODO: DOCUMENT ME!
	 */
	public void removeUpdate(DocumentEvent e) {
		if(ignoreModifications) {
			return;
		}

		setModified(true);
		fireDelayedOrdersEvent();
		docUpdateThread.setEvent(e);
		SwingUtilities.invokeLater(docUpdateThread);
	}

	Timer timer = null;

	/*
	 * Timer support routine for informing other objects about
	 * changed orders.
	 */
	private void fireDelayedOrdersEvent() {
		if(timer == null) {
			timer = new Timer(700,
							  new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						setOrdersAndFireEvent();

						//dispatcher.fire(new UnitOrdersEvent(OrderEditor.this,unit));
					}
				});
			timer.setRepeats(false);
		}

		// always restart to prevent refreshing while moving around
		timer.restart();
	}

	/**
	 * Indicates whether the contents of this editor have been modified.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean isModified() {
		return modified;
	}

	/**
	 * If the active object in the selection event is a unit it is registered with this editor.
	 *
	 * @param e TODO: DOCUMENT ME!
	 */
	public void selectionChanged(SelectionEvent e) {
		if(log.isDebugEnabled()) {
			log.debug("OrderEditor.selectionChanged: " + e.getActiveObject());
			log.debug("OrderEditor.selectionChanged: " + e.getActiveObject().getClass());
		}

		Object activeObject = e.getActiveObject();

		if(activeObject instanceof Unit) {
			setUnit((Unit) activeObject);
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param e TODO: DOCUMENT ME!
	 */
	public void keyPressed(KeyEvent e) {
		if((unit != null) &&
			   ((e.getKeyCode() == KeyEvent.VK_ENTER) ||
			   (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) ||
			   (e.getKeyCode() == KeyEvent.VK_DELETE))) {
			setOrdersAndFireEvent();
		} else if(e.getKeyCode() == KeyEvent.VK_C) {
			int mask = KeyEvent.SHIFT_MASK | KeyEvent.CTRL_MASK;

			//if(e.getModifiers() == mask) {
			// moved to ordereditorkit!
			//}
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
	 * TODO: DOCUMENT ME!
	 *
	 * @param e TODO: DOCUMENT ME!
	 */
	public void focusGained(FocusEvent e) {
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param e TODO: DOCUMENT ME!
	 */
	public void focusLost(FocusEvent e) {
		setOrdersAndFireEvent();
	}

	private void setOrdersAndFireEvent() {
		if(log.isDebugEnabled()) {
			log.debug("OrderEditor.setOrdersAndFireEvent(" + isModified() + "," + unit + ")");
		}

		if(isModified() && (unit != null)) {
			// this is done on purpose: in init of UnitOrdersEvent the list of related units is built
			// so we need to create it before changing orders of the unit
			UnitOrdersEvent e = new UnitOrdersEvent(this, unit);
			unit.setOrders(getOrders());
			// we also need to notify that the unit orders are now unmodified
			setModified(false);
			dispatcher.fire(e);
		}
	}

	/**
	 * Returns the unit currently registered with this editor.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Unit getUnit() {
		return unit;
	}

	/**
	 * Sets the unit for this editor component. If the orders have been modified and there is a
	 * previous unit registered with this component, its orders are updated.
	 *
	 * @param u TODO: DOCUMENT ME!
	 */
	public void setUnit(Unit u) {
		setOrdersAndFireEvent();

		ignoreModifications = true;
		unit = u;

		if(unit != null) {
			setOrders(unit.getOrders());
		} else {
			setText("");
		}

		ignoreModifications = false;
	}

	/**
	 * Returns a list of the orders this text pane is currently containing. A order extending over
	 * more than one line is stripped of the trailing backslashes, concatenated and returned as
	 * one order.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public List getOrders() {
		if(modified == false) {
			return orders;
		}

		orders = new LinkedList();

		try {
			String text = getText();
			String line = null;
			LineNumberReader stream = new LineNumberReader(new MergeLineReader(new StringReader(text)));

			// note: stream.ready() is not false at end of string
			while(stream.ready()) {
				if((line = stream.readLine()) != null) {
					orders.add(line);
				} else {
					break;
				}
			}

			stream.close();
		} catch(IOException ioe) {
			log.warn(ioe);
		}

		return orders;
	}

	/**
	 * Puts the list elements in <tt>c</tt> into this text pane, one at a line.
	 *
	 * @param c TODO: DOCUMENT ME!
	 */
	public void setOrders(Collection c) {
		orders = CollectionFactory.createLinkedList();
		orders.addAll(c);

		ignoreModifications = true;
		setText("");

		for(Iterator iter = orders.iterator(); iter.hasNext(); ) {
			addOrder((String) iter.next());
		}

		setCaretPosition(0);
		setModified(false);
		ignoreModifications = false;
		undoMgr.discardAllEdits();
	}

	/**
	 * Adds a order to this text pane.
	 *
	 * @param cmd TODO: DOCUMENT ME!
	 */
	public void addOrder(String cmd) {
		ignoreModifications = true;

		Document doc = getDocument();

		try {
			//if((doc.getLength() > 0) && !doc.getText(doc.getLength(), 1).equals("\n")) {
			if((doc.getLength() > 0)) {
				doc.insertString(doc.getLength(), "\n", null);
			}

			int insertPos = doc.getLength();
			doc.insertString(insertPos, cmd, null);

			if(highlightSyntax) {
				formatTokens(insertPos);
			}
		} catch(BadLocationException e) {
			log.warn("OrderEditor.addOrder(): " + e.toString());
		}

		ignoreModifications = false;
	}

	/**
	 * Allows to change the modified state of this text pane. Setting it to <tt>true</tt> currently
	 * does not automatically result in an event being fired.
	 *
	 * @param isModified TODO: DOCUMENT ME!
	 */
	private void setModified(boolean isModified) {
		modified = isModified;
	}

	/**
	 * Return whether syntax highlighting is enabled or disabled.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean getUseSyntaxHighlighting() {
		return highlightSyntax;
	}

	/**
	 * Enable or disable syntax highlighting.
	 *
	 * @param bool TODO: DOCUMENT ME!
	 */
	public void setUseSyntaxHighlighting(boolean bool) {
		if(highlightSyntax != bool) {
			highlightSyntax = bool;

			if(settings != null) {
				settings.setProperty("OrderEditor.highlightSyntax", String.valueOf(bool));
			}

			formatTokens();
		}
	}

	/**
	 * Return the color of the specified token style used for syntax highlighting.
	 *
	 * @param styleName TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Color getTokenColor(String styleName) {
		return Colors.decode(settings.getProperty("OrderEditor.styles." + styleName + ".color",
												  getDefaultColor(styleName)));
	}

	/**
	 * Set the color of the specified token style used for syntax highlighting.
	 *
	 * @param styleName TODO: DOCUMENT ME!
	 * @param color TODO: DOCUMENT ME!
	 */
	public void setTokenColor(String styleName, Color color) {
		settings.setProperty("OrderEditor.styles." + styleName + ".color", Colors.encode(color));

		Style s = ((StyledDocument) getDocument()).getStyle(styleName);

		if(s != null) {
			StyleConstants.setForeground(s, color);
		}
	}

	/**
	 * Refresh the editor's contents.
	 */
	public void reloadOrders() {
		if(unit != null) {
			setOrders(unit.getOrders());
		}
	}

	/**
	 * Adds styles to this component with one style for each token type.
	 */
	private void initStyles() {
		Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
		Style regular = addStyle(S_REGULAR, def);
		Style s = addStyle(S_KEYWORD, regular);
		StyleConstants.setForeground(s,
									 Colors.decode(settings.getProperty("OrderEditor.styles." +
																		S_KEYWORD + ".color",
																		getDefaultColor(S_KEYWORD))));
		s = addStyle(S_STRING, regular);
		StyleConstants.setForeground(s,
									 Colors.decode(settings.getProperty("OrderEditor.styles." +
																		S_STRING + ".color",
																		getDefaultColor(S_STRING))));
		s = addStyle(S_NUMBER, regular);
		StyleConstants.setForeground(s,
									 Colors.decode(settings.getProperty("OrderEditor.styles." +
																		S_NUMBER + ".color",
																		getDefaultColor(S_NUMBER))));
		s = addStyle(S_ID, regular);
		StyleConstants.setForeground(s,
									 Colors.decode(settings.getProperty("OrderEditor.styles." +
																		S_ID + ".color",
																		getDefaultColor(S_ID))));
		s = addStyle(S_COMMENT, regular);
		StyleConstants.setForeground(s,
									 Colors.decode(settings.getProperty("OrderEditor.styles." +
																		S_COMMENT + ".color",
																		getDefaultColor(S_COMMENT))));
	}

	/**
	 * Return the default color as a string for the specified token style.
	 *
	 * @param styleName TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	private String getDefaultColor(String styleName) {
		String retVal = "0,0,0";

		if(styleName.equals(S_KEYWORD)) {
			retVal = "0,0,255";
		} else if(styleName.equals(S_STRING)) {
			retVal = "192,0,0";
		} else if(styleName.equals(S_NUMBER)) {
			retVal = "0,0,0";
		} else if(styleName.equals(S_ID)) {
			retVal = "168,103,0";
		} else if(styleName.equals(S_COMMENT)) {
			retVal = "0,128,0";
		}

		return retVal;
	}

	/**
	 * Returns a style corresponding to a token type
	 *
	 * @param t TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	private Style getTokenStyle(OrderToken t) {
		StyledDocument doc = (StyledDocument) getDocument();
		Style retVal = doc.getStyle(S_REGULAR);

		switch(t.ttype) {
		case OrderToken.TT_UNDEF:
			retVal = doc.getStyle(S_REGULAR);

			break;

		case OrderToken.TT_KEYWORD:
			retVal = doc.getStyle(S_KEYWORD);

			break;

		case OrderToken.TT_STRING:
			retVal = doc.getStyle(S_STRING);

			break;

		case OrderToken.TT_NUMBER:
			retVal = doc.getStyle(S_NUMBER);

			break;

		case OrderToken.TT_ID:
			retVal = doc.getStyle(S_ID);

			break;

		case OrderToken.TT_COMMENT:
			retVal = doc.getStyle(S_COMMENT);

			break;
		}

		return retVal;
	}

	/**
	 * Gives back the text in the text pane. Additionally \r is replaced by space. This is a
	 * so-called "minor incompatibility" introduced by jdk 1.4.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getText() {
		String text = super.getText();

		if((text == null) || (text.indexOf('\r') == -1)) {
			return text;
		}

		StringBuffer sb = new StringBuffer(text.length());
		char chars[] = text.toCharArray();

		for(int i = 0; i < chars.length; i++) {
			if(chars[i] != '\r') {
				sb.append(chars[i]);
			}
		}

		return sb.toString();
	}

	/**
	 * Format the tokens in the line starting in the document at insertPos, with regard to the
	 * token type colors.
	 *
	 * @param startPos TODO: DOCUMENT ME!
	 */
	private void formatTokens(int startPos) {
		StyledDocument doc = (StyledDocument) getDocument();

		String text = getText();

		if((text == null) || text.equals("")) {
			return;
		}

		int pos[] = getLineBorders(text, startPos);
		int newStartPos = startPos;

		if(pos[0] == -1) {
			return;
		}

		newStartPos = pos[0];

		if(log.isDebugEnabled()) {
			log.debug("OrderEditor.formatTokens(" + startPos + "): (" + text + "," + pos[0] + "," +
					  pos[1] + ")");
		}

		text = text.substring(pos[0], pos[1]);

		if(log.isDebugEnabled()) {
			log.debug("OrderEditor.formatTokens(" + startPos + "): (" + text + ")");
		}

		parser.read(new StringReader(text));

		for(Iterator iter = parser.getTokens().iterator(); iter.hasNext();) {
			OrderToken token = (OrderToken) iter.next();

			if(log.isDebugEnabled()) {
				log.debug("OrderEditor.formatTokens: token " + token);
			}

			if(token.ttype != OrderToken.TT_EOC) {
				Style style = getTokenStyle(token);

				if(style != null) {
					if(log.isDebugEnabled()) {
						log.debug("OrderEditor.formatTokens setting style from " +
								  (newStartPos + token.getStart()) + " length " +
								  (token.getEnd() - token.getStart()));
					}

					doc.setCharacterAttributes(newStartPos + token.getStart(),
											   token.getEnd() - token.getStart(),
											   getTokenStyle(token), true);
				}
			}
		}
	}

	/**
	 * Format all tokens with regard to the token type colors.
	 */
	public void formatTokens() {
		if(!highlightSyntax) {
			return;
		}

		String text = getText();

		if((text == null) || text.equals("")) {
			return;
		}

		int pos = 0;

		while(pos != -1) {
			if(log.isDebugEnabled()) {
				log.debug("OrderEditor.formatTokens(): formatting pos " + pos);
			}

			formatTokens(pos);
			pos = text.indexOf("\n", pos);

			if(pos != -1) {
				pos++;
			}
		}

		if(log.isDebugEnabled()) {
			log.debug("OrderEditor.formatTokens(): formatting done");
		}
	}

	/**
	 * Returns the start and end of the line denoted by offset.
	 *
	 * @param text the string to search.
	 * @param offset the position from where to look for linebreaks.
	 *
	 * @return start of line at array index 0, end of line at array index 1.
	 */
	private int[] getLineBorders(String text, int offset) {
		int ret[] = new int[2];

		// 2002.02.20 pavkovic: if offset is greater than text size, we won't find any position
		// -> we can return -1 very fast
		if(offset > text.length()) {
			ret[0] = -1;

			return ret;
		}

		try {
			// pavkovic 2003.01.20: line ending may be \r\n!
			// prior to jdk 1.4 this only may have been \n OR \r
			// if current position IS \n or \r move to the right
			// if not, move to the left
			text = text.replace('\r', ' ');

			for(ret[0] = offset - 1; ret[0] > -1; ret[0]--) {
				char c = text.charAt(ret[0]);

				if((c == '\n') || (c == '\r')) {
					break;
				}
			}

			ret[0]++;
			ret[1] = text.indexOf('\n', offset);

			// ret[1] = text.indexOf('\n', ret[0]);
			if(ret[1] == -1) {
				ret[1] = text.length();
			}
		} catch(StringIndexOutOfBoundsException e) {
			log.error("OrderEditor.getLineBorders(), text: " + text + ", offset: " + offset +
					  ", ret: (" + ret[0] + ", " + ret[1] + ")", e);
		}

		if(log.isDebugEnabled()) {
			log.debug("OrderEditor.getLineBorders:(" + text + "," + offset + ") " + ret[0] + "," +
					  ret[1]);
		}

		if(ret[0] >= 0) {
			if(ret[1] > ret[0]) {
				log.debug("OrderEditor.getLineBorders would be \"" +
						  text.substring(ret[0], ret[1]) + "\"");
			}
		}

		return ret;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param b TODO: DOCUMENT ME!
	 */
	public void setKeepVisible(boolean b) {
		myCaret.setKeepVisible(b);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean keepVisible() {
		return myCaret.keepVisible();
	}

	// pavkovic 2002.11.12
	// this is a dumb caret that does NOT track JScrollpane!
	// try to search for "JScrollPane tracks JTextComponent" (grr, took me days to find it!)
	// we use it in a MultiEditor-situation for the order editors
	// but this also means we need to handle setText
	private class OrderEditorCaret extends DefaultCaret {
		private boolean keepVis = true;

		protected void adjustVisibility(Rectangle nloc) {
			if(log.isDebugEnabled()) {
				log.debug("OrderEditor(" + unit + "): adjustVisibility(" + keepVis + "," + nloc +
						  ")");
			}

			if(keepVisible()) {
				super.adjustVisibility(nloc);
			}
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param mode TODO: DOCUMENT ME!
		 */
		public void setKeepVisible(boolean mode) {
			if(log.isDebugEnabled()) {
				log.debug("OrderEditor: setting caret.setKeepVisible(" + mode + ")");
			}

			keepVis = mode;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public boolean keepVisible() {
			return keepVis;
		}
	}

	private class DocumentUpdateRunnable implements Runnable {
		private DocumentEvent e;

		/**
		 * Creates a new DocumentUpdateRunnable object.
		 *
		 * @param e TODO: DOCUMENT ME!
		 */
		public DocumentUpdateRunnable(DocumentEvent e) {
			this.e = e;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param e TODO: DOCUMENT ME!
		 */
		public void setEvent(DocumentEvent e) {
			this.e = e;
		}

		/**
		 * TODO: DOCUMENT ME!
		 */
		public void run() {
			int offset = e.getOffset();
			String text = getText();
			int pos = getLineBorders(text, offset)[0];

			if((pos < 0) || (pos > OrderEditor.this.getDocument().getLength())) {
				return;
			}

			if(e.getType().equals(DocumentEvent.EventType.REMOVE)) {
				formatTokens(pos);
			} else if(e.getType().equals(DocumentEvent.EventType.INSERT)) {
				if((text.length() >= (offset + e.getLength())) &&
					   (text.substring(offset, offset + e.getLength()).indexOf("\n") > -1)) {
					// multiple-line-insert happened
					//try {
					if(text.equals("") == false){
						int p = pos;

						while(p > -1) {
							formatTokens(p);
							p = text.indexOf("\n", p);

							if(p != -1) {
								p++;
							}
						}
					}

					//} catch (Exception e) {
					//log.error(e);
					//}
				} else {
					// single-line-insert happened
					formatTokens(pos);
				}
			}
		}
	}

	private class SignificantUndos implements UndoableEditListener, DocumentListener {
		boolean bSignificant = true;
		boolean bMoreThanOne = false;
		javax.swing.undo.CompoundEdit compoundEdit = new javax.swing.undo.CompoundEdit();

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param e TODO: DOCUMENT ME!
		 */
		public void changedUpdate(DocumentEvent e) {
			documentEvent(e);
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param e TODO: DOCUMENT ME!
		 */
		public void insertUpdate(DocumentEvent e) {
			documentEvent(e);
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param e TODO: DOCUMENT ME!
		 */
		public void removeUpdate(DocumentEvent e) {
			documentEvent(e);
		}

		private void documentEvent(DocumentEvent e) {
			bSignificant = (!DocumentEvent.EventType.CHANGE.equals(e.getType()));
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param e TODO: DOCUMENT ME!
		 */
		public void undoableEditHappened(UndoableEditEvent e) {
			if(bSignificant) {
				if(bMoreThanOne) {
					compoundEdit.addEdit(e.getEdit());
					compoundEdit.end();
					undoMgr.undoableEditHappened(new UndoableEditEvent(e.getSource(), compoundEdit));
					compoundEdit = new javax.swing.undo.CompoundEdit();
					bMoreThanOne = false;
				} else {
					undoMgr.undoableEditHappened(e);
				}
			} else {
				compoundEdit.addEdit(e.getEdit());
				bMoreThanOne = true;
			}
		}
	}
}
