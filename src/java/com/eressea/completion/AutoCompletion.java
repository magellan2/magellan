// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Ulrich Küster, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===


package com.eressea.completion;

import java.awt.Component;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.CaretListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.SimpleAttributeSet;

import com.eressea.event.EventDispatcher;
import com.eressea.event.GameDataEvent;
import com.eressea.event.GameDataListener;
import com.eressea.event.SelectionEvent;
import com.eressea.event.SelectionListener;
import com.eressea.swing.InternationalizedDialog;
import com.eressea.swing.completion.CompletionGUI;
import com.eressea.swing.completion.OrderEditorList;
import com.eressea.swing.preferences.PreferencesAdapter;
import com.eressea.util.CollectionFactory;
import com.eressea.util.Completion;
import com.eressea.util.Translations;
import com.eressea.util.logging.Logger;

/**
 * @author  Andreas Gampe, Ulrich Küster
 */
public class AutoCompletion implements SelectionListener, KeyListener, ActionListener, CaretListener, FocusListener, GameDataListener {
	private final static Logger log = Logger.getInstance(AutoCompletion.class);

	private OrderEditorList editors;
	private Vector completionGUIs;
	private CompletionGUI currentGUI;
	private Completer completer;
	private int lastCaretPosition = 0;

	/** Keys for cycling, completing and breaking.
	 *
	 * completerKeys[]	completerKeys[][0]	completerKeys[1]
	 *	cycle forward			modifier						key
	 *	cycle backward		modifier						key
	 *	complete					modifier						key
	 *	break							modifier						key
	 */
	private int completerKeys[][];

	private Timer timer;

	private List completions = null;
	private int completionIndex = 0;
	private String lastStub = null;

	private boolean enableAutoCompletion=true;
	// limits the completion of the make-order to items
	// whose resources are available
	private boolean limitMakeCompletion = true;
	private boolean emptyStubMode=false;
	private String activeGUI=null;
	private int time=150;

	// self defined completion objects (mapping a name (String) to a value (String))
	private Hashtable selfDefinedCompletions = new Hashtable();

	protected Properties settings;

	/** Creates new AutoCompletion */
	public AutoCompletion(Properties settings,EventDispatcher d) {
		this.settings=settings;
		d.addSelectionListener(this);
		d.addGameDataListener(this);

		editors=null;
		completionGUIs=new Vector();
		currentGUI=null;
		completer=null;

		loadSettings();
		timer=new Timer(time,this);

	}

	protected void loadSettings() {
		String autoCmp=settings.getProperty("AutoCompletion.Enabled");
		if (autoCmp!=null)
			enableAutoCompletion=autoCmp.equalsIgnoreCase("true");
		else {
			autoCmp=settings.getProperty("OrderEditingPanel.useOrderCompletion");
			if (autoCmp!=null) {
				enableAutoCompletion=new Boolean(autoCmp).booleanValue();
				settings.remove("OrderEditingPanel.useOrderCompletion");
				settings.setProperty("AutoCompletion.Enabled",enableAutoCompletion?"true":"false");
			}
			else
				enableAutoCompletion=true;
		}

		limitMakeCompletion = settings.getProperty("AutoCompletion.limitMakeCompletion", "true").equalsIgnoreCase("true");

		String stubMode=settings.getProperty("AutoCompletion.EmptyStubMode","true");
		emptyStubMode = stubMode.equalsIgnoreCase("true");

		activeGUI=settings.getProperty("AutoCompletion.CompletionGUI","List");

		try{
			time=Integer.parseInt(settings.getProperty("AutoCompletion.ActivationTime","150"));
		}catch(Exception exc) {time=150;}

		completerKeys=new int[4][2]; // cycle forward, cycle backward, complete, break
		String cycleForward=settings.getProperty("AutoCompletion.Keys.Cycle.Forward");
		try{
			StringTokenizer st=new StringTokenizer(cycleForward,",");
			completerKeys[0][0]=Integer.parseInt(st.nextToken());
			completerKeys[0][1]=Integer.parseInt(st.nextToken());
		}catch(Exception exc) {
			completerKeys[0][0]=KeyEvent.CTRL_MASK;
			completerKeys[0][1]=KeyEvent.VK_DOWN;
		}
		String cycleBackward=settings.getProperty("AutoCompletion.Keys.Cycle.Backward");
		try{
			StringTokenizer st=new StringTokenizer(cycleBackward,",");
			completerKeys[1][0]=Integer.parseInt(st.nextToken());
			completerKeys[1][1]=Integer.parseInt(st.nextToken());
		}catch(Exception exc) {
			completerKeys[1][0]=KeyEvent.CTRL_MASK;
			completerKeys[1][1]=KeyEvent.VK_UP;
		}
		String complete=settings.getProperty("AutoCompletion.Keys.Complete");
		try{
			StringTokenizer st=new StringTokenizer(complete,",");
			completerKeys[2][0]=Integer.parseInt(st.nextToken());
			completerKeys[2][1]=Integer.parseInt(st.nextToken());
		}catch(Exception exc) {
			completerKeys[2][0]=0;
			completerKeys[2][1]=KeyEvent.VK_TAB;
		}
		String breakKey=settings.getProperty("AutoCompletion.Keys.Break");
		try{
			StringTokenizer st=new StringTokenizer(breakKey,",");
			completerKeys[3][0]=Integer.parseInt(st.nextToken());
			completerKeys[3][1]=Integer.parseInt(st.nextToken());
		}catch(Exception exc) {
			completerKeys[3][0]=0;
			completerKeys[3][1]=KeyEvent.VK_ESCAPE;
		}
		// load selfdefined completions
		String s = (String)settings.get("AutoCompletion.SelfDefinedCompletions.count");
		int completionCount = 0;
		if (s != null) {
			completionCount = Integer.parseInt(s);
		}
		for (int i = 0; i < completionCount; i++) {
			String name = (String)settings.get("AutoCompletion.SelfDefinedCompletions.name" + i);
			String value = (String)settings.get("AutoCompletion.SelfDefinedCompletions.value" + i);
			selfDefinedCompletions.put(name, value);
		}
	}

	// to choose the current GUI
	public void loadComplete() {
		if (completionGUIs.size()==0)
			return;
		if (activeGUI==null) {
			setCurrentGUI((CompletionGUI)completionGUIs.get(0));
			return;
		}
		Iterator it=completionGUIs.iterator();
		while(it.hasNext()) {
			CompletionGUI cGUI=(CompletionGUI)it.next();
			if (cGUI.getTitle().equals(activeGUI)) {
				setCurrentGUI(cGUI);
				return;
			}
		}
		setCurrentGUI((CompletionGUI)completionGUIs.get(0));
	}

	public void attachEditorManager(OrderEditorList cel) {
		if (editors!=null) {
			editors.removeExternalKeyListener(this);
			editors.removeExternalCaretListener(this);
			editors.removeExternalFocusListener(this);
		}
		editors=cel;
		if (editors!=null) {
			editors.addExternalKeyListener(this);
			editors.addExternalCaretListener(this);
			editors.addExternalFocusListener(this);
		}
	}

	public void addCompletionGUI(CompletionGUI cGUI) {
		cGUI.init(this);
		completionGUIs.add(cGUI);
	}

	public List getCompletionGUIs() {
		return completionGUIs;
	}

	public void setCurrentGUI(CompletionGUI cGUI) {
		if(log.isDebugEnabled()) {
			log.debug("AutoCompletion.setCurrentGUI called with "+cGUI);
		}
		if (!completionGUIs.contains(cGUI))
			addCompletionGUI(cGUI);
		if (currentGUI!=null)
			currentGUI.stopOffer();
		currentGUI=cGUI;
		settings.setProperty("AutoCompletion.CompletionGUI",cGUI.getTitle());
		timer.restart();
	}

	public CompletionGUI getCurrentGUI() {
		return currentGUI;
	}

	public void setCompleter(Completer c) {
		completer=c;
	}

	public void selectionChanged(SelectionEvent e) {
		log.debug("AutoCompletion.selectionChanged called with "+e);

		// stop offering a completion
		if (currentGUI!=null)
			currentGUI.stopOffer();

		// let the editor update
		if (editors!=null)
			editors.selectionChanged(e);

		// search for new completion
		JTextComponent j=editors.getCurrentEditor();
		if (j!=null && enableAutoCompletion)
			timer.restart();
	}

	protected void offerCompletion(JTextComponent j) {
		if (!enableAutoCompletion || currentGUI==null || completer==null || j==null || completer==null || !j.isVisible())
			return;

		// find the piece of text
		String line=getCurrentLine(j);
		if (line==null)
			return;
		if (line.length() == 0
				|| (j.getCaretPosition() > 0 && j.getText().charAt(j.getCaretPosition() - 1) == '\n' )
				|| (j.getCaretPosition() > 0 && j.getText().charAt(j.getCaretPosition() - 1) == '\r' )) {
			// new line, delete old completions
			completions = null;
		} else if (lastCaretPosition > j.getCaretPosition()) {
			// Caret went backwards so line must be parsed again
			completions = null;
		} else if (j.getCaretPosition() > 0 && j.getText().charAt(j.getCaretPosition() - 1) == ' ') {
			// if Space typed, delete old completions to enforce new parsing
			completions = null;
		}
		// remember CaretPosition
		lastCaretPosition = j.getCaretPosition();

		// run completer engine
		if (editors.getCurrentUnit() != null) {
			completions=completer.getCompletions(editors.getCurrentUnit(),line,completions);

			// determine, wheter the curser is in a word
			boolean inWord = true;
			if (j.getText().length() == j.getCaretPosition() || j.getCaretPosition() == 0) {
				inWord = false;
			} else {
				char c = j.getText().charAt(j.getCaretPosition());
				if (c == ' ' || c == '\n' || c == '\t') {
					inWord = false;
				} else {
					c = j.getText().charAt(j.getCaretPosition() - 1);
					if (c == ' ' || c == '\n' || c == '\t') {
						inWord = false;
					}
				}
			}
			// show completions
			if (line.length() > 0) {
				String stub=getStub(line);
				lastStub=stub;
				if ((emptyStubMode || stub.length()>0)
						&& completions!=null
						&& completions.size()>0
						&& !inWord ){
					currentGUI.offerCompletion(j,completions,stub);
					completionIndex=0;
				}
			}
		}
	}

	public void cycleForward() {
		if (completions==null || completions.size()==1 || editors==null)
			return;
		completionIndex++;
		completionIndex%=completions.size();
		if (currentGUI.isOfferingCompletion())
			currentGUI.cycleCompletion(editors.getCurrentEditor(),completions, lastStub, completionIndex);
	}

	public void cycleBackward() {
		if (completions==null || completions.size()==1 || editors==null)
			return;
		completionIndex--;
		if (completionIndex < 0) {
			completionIndex += completions.size();
		}
		completionIndex%=completions.size();
		if (currentGUI.isOfferingCompletion())
			currentGUI.cycleCompletion(editors.getCurrentEditor(),completions, lastStub, completionIndex);
	}

	public void insertCompletion(Completion completion) {
		if (!enableAutoCompletion) {
			return;
		}
		completions = null;
		if (currentGUI != null) {
			currentGUI.stopOffer();
		}
		JTextComponent j = editors.getCurrentEditor();
		int caretPos = j.getCaretPosition();
		String line = getCurrentLine(j);
		String stub = getStub(line);
		int stubLen = stub.length();
		int stubBeg = caretPos - stubLen;
		String text = j.getText();
		int temp1 = text.indexOf('\n', caretPos);
		if (temp1 == -1) {
			temp1 = Integer.MAX_VALUE;
		}
		int temp2 = text.indexOf('\r', caretPos);
		if (temp2 == -1) {
			temp2 = Integer.MAX_VALUE;
		}
		int temp3 = text.indexOf('\t', caretPos);
		if (temp3 == -1) {
			temp3 = Integer.MAX_VALUE;
		}
		int temp4 = text.indexOf(' ', caretPos);
		if (temp4 == -1) {
			temp4 = Integer.MAX_VALUE;
		}
		temp1 = Math.min(temp1, temp2);
		temp3 = Math.min(temp3, temp4);
		int stubEnd = Math.min(temp1, temp3);
		if (stubEnd == Integer.MAX_VALUE) {
			stubEnd = text.length();
		} else if (text.charAt(stubEnd) == ' ') {
			stubEnd++;
		}
		stubLen = stubEnd - stubBeg;
		try{
			j.getDocument().remove(stubBeg, stubLen);
			j.getDocument().insertString(stubBeg, completion.getValue(), new SimpleAttributeSet());
			j.getCaret().setDot(stubBeg + completion.getValue().length()-completion.getCursorOffset());
			// pavkovic 2003.03.04: enforce focus request
			j.requestFocus();
		}catch(BadLocationException exc) {
			log.info(exc);
		}

	}

	public static String getCurrentLine(JTextComponent j) {
		int offset=j.getCaretPosition();
		int lineBounds[]=getCurrentLineBounds(j.getText(),offset);
		if (lineBounds[0]<0)
			return null;
		lineBounds[1]=Math.min(offset,lineBounds[1])-lineBounds[0];
		try{
			return j.getText(lineBounds[0],lineBounds[1]);
		}catch(BadLocationException ble) {}
		return null;
	}

	public static int[] getCurrentLineBounds(String text, int offset) {
		int ret[] = { -1, -1 };
		if (offset > -1 && offset <= text.length()) {
			for (ret[0] = offset - 1; ret[0] > -1; ret[0]--) {
				char c = text.charAt(ret[0]);
				if (c == '\n' || c == '\r')
					break;
			}
			ret[0]++;
			ret[1] = text.indexOf('\n', offset);
			if (ret[1] == -1) {
				ret[1] = text.length();
			}
		}
		return ret;
	}

	public static String getStub(String txt) {
		StringBuffer retVal = new StringBuffer();
		for (int i = txt.length() - 1; i >= 0; i--) {
			char c = txt.charAt(i);
			if (c == '"' || c == '_' || c == '-' || Character.isLetterOrDigit(c) == true) {
				retVal.append(c);
			} else {
				break;
			}
		}
		return retVal.reverse().toString();
	}

	public void keyReleased(java.awt.event.KeyEvent p1) {
	}

	public void keyPressed(java.awt.event.KeyEvent e) {
		if (!enableAutoCompletion || currentGUI==null || !currentGUI.isOfferingCompletion())
			return;

		int code = e.getKeyCode();
		int modifiers=e.getModifiers();
		boolean plain = (e.getModifiers() == 0);

		if (completerKeys[0][0]==modifiers && completerKeys[0][1]==code) {
			cycleForward();
			e.consume();
			return;
		}
		if (completerKeys[1][0]==modifiers && completerKeys[1][1]==code) {
			cycleBackward();
			e.consume();
			return;
		}
		if (completerKeys[2][0]==modifiers && completerKeys[2][1]==code) {
			Completion cmp=currentGUI.getSelectedCompletion();
			if (cmp!=null)
				insertCompletion(cmp);
			e.consume();
			return;
		}
		if (completerKeys[3][0]==modifiers && completerKeys[3][1]==code) {
			currentGUI.stopOffer();
			return;
		}


		if (!plain)
			return;
		int keys[]=currentGUI.getSpecialKeys();
		if (keys!=null)
			for(int i=0;i<keys.length;i++) {
				if (code == keys[i]) {
					currentGUI.specialKeyPressed(code);
					e.consume();
				}
			}
	}

	public void keyTyped(java.awt.event.KeyEvent p1) {
	}

	public void actionPerformed(java.awt.event.ActionEvent e) {
		log.debug("AutoCompletion.actionPerformed called with "+e);

		timer.stop();
		if (enableAutoCompletion) {
			offerCompletion(editors.getCurrentEditor());
		}
	}

	public void caretUpdate(javax.swing.event.CaretEvent e) {
		log.debug("AutoCompletion.caretUpdate called with "+e);

		if (enableAutoCompletion && currentGUI!=null && !currentGUI.editorMayUpdateCaret()) {
			currentGUI.stopOffer();
			timer.restart();
		}
	}

	public void gameDataChanged(GameDataEvent e) {
		if (currentGUI!=null)
			currentGUI.stopOffer();
	}

	public PreferencesAdapter getPreferencesAdapter() {
		return new DetailAutoCompletionPreferencesAdapter(this);
	}

	public void focusGained(java.awt.event.FocusEvent e) {
		log.debug("AutoCompletion.focusGained called with "+e);

		if (enableAutoCompletion)
			timer.restart();
	}

	public void focusLost(java.awt.event.FocusEvent p1) {
		if (currentGUI != null && !currentGUI.editorMayLoseFocus())
			currentGUI.stopOffer();
	}

	public void setEnableAutoCompletion(boolean b) {
		enableAutoCompletion=b;
		settings.setProperty("AutoCompletion.Enabled",enableAutoCompletion?"true":"false");
		if (!b && currentGUI!=null)
			currentGUI.stopOffer();
	}

	public boolean isEnableAutoCompletion() {
		return enableAutoCompletion;
	}

	public void setLimitMakeCompletion(boolean value) {
		limitMakeCompletion = value;
		settings.setProperty("AutoCompletion.limitMakeCompletion", value? "true" : "false");
	}

	public boolean getLimitMakeCompletion() {
		return limitMakeCompletion;
	}

	public void setEmptyStubMode(boolean b) {
		emptyStubMode=b;
		settings.setProperty("AutoCompletion.EmptyStubMode",emptyStubMode?"true":"false");
	}

	public boolean getEmptyStubMode() {
		return emptyStubMode;
	}

	public int getActivationTime() {
		return time;
	}

	public void setActivationTime(int t) {
		time=t;
		if (time<1)
			time=1;
		settings.setProperty("AutoCompletion.ActivationTime",String.valueOf(time));
		timer.setDelay(time);
	}

	public int[][] getCompleterKeys() {
		return completerKeys;
	}

	public void setCompleterKeys(int ck[][]) {
		completerKeys=ck;
		settings.setProperty("AutoCompletion.Keys.Cycle.Forward",String.valueOf(ck[0][0])+','+String.valueOf(ck[0][1]));
		settings.setProperty("AutoCompletion.Keys.Cycle.Backward",String.valueOf(ck[1][0])+','+String.valueOf(ck[1][1]));
		settings.setProperty("AutoCompletion.Keys.Complete",String.valueOf(ck[2][0])+','+String.valueOf(ck[2][1]));
		settings.setProperty("AutoCompletion.Keys.Break",String.valueOf(ck[3][0])+','+String.valueOf(ck[3][1]));
	}

	/**
	 * Returns a Vector containing the self defined completions as
	 * Completion objects.
	 */
	public Vector getSelfDefinedCompletions() {
		Vector retVal = new Vector();
		for (Iterator iter = selfDefinedCompletions.keySet().iterator(); iter.hasNext(); ) {
			String name = (String)iter.next();
			String value = (String)selfDefinedCompletions.get(name);
			Completion c = new Completion(name, value, "", 0);
			retVal.add(c);
		}
		return retVal;
	}

	private class DetailAutoCompletionPreferencesAdapter extends JPanel implements PreferencesAdapter {
		protected AutoCompletion source;

		protected JCheckBox cEnable;
		protected JCheckBox iPopup;
		protected JCheckBox limitMakeCompletion;
		protected JComboBox forGUIs;
		protected JTextField tTime;
		private JScrollPane sPane;
		protected KeyTextField keyFields[];

		// a copy of AutoCompletion.selfDefinedCompletions
		private Hashtable selfDefinedCompletions;

		public DetailAutoCompletionPreferencesAdapter(AutoCompletion s) {
			source=s;
			this.setLayout(new java.awt.GridBagLayout());
			GridBagConstraints c = new GridBagConstraints(0, 0, 2, 1, 0.1, 1.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0);

			this.add(new JPanel(), c);

			c.gridwidth = 1;
			c.weighty = 0;
			c.gridy++;
			cEnable = new JCheckBox(getString("prefs.autocompletion"), source.isEnableAutoCompletion());
			this.add(cEnable, c);

			c.gridy++;
			limitMakeCompletion = new JCheckBox(getString("prefs.limitmakecompletion"),
												source.getLimitMakeCompletion());
			limitMakeCompletion.setToolTipText(getString("prefs.limitmakecompletion.tooltip"));
			this.add(limitMakeCompletion, c);

			iPopup=new JCheckBox(getString("prefs.stubmode"),
								 source.getEmptyStubMode());
			c.gridy++;
			this.add(iPopup,c);

			c.gridy++;

			JPanel inner = new JPanel(new GridBagLayout());
			GridBagConstraints con2 = new GridBagConstraints(0,0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, c.insets, 0, 0);

			inner.add(new JLabel(getString("prefs.time")),con2);
			con2.gridy++;
			inner.add(new JLabel(getString("prefs.gui")+":"),con2);

			con2.gridx = 1;
			con2.gridy = 0;
			con2.weightx = 1;
			tTime=new JTextField(String.valueOf(source.getActivationTime()),5);
			inner.add(tTime,con2);
			con2.gridy++;

			forGUIs=new JComboBox(completionGUIs);
			forGUIs.setEditable(false);
			if (source.getCurrentGUI()!=null)
				forGUIs.setSelectedItem(source.getCurrentGUI());
			inner.add(forGUIs,con2);

			this.add(inner, c);

			Component keys=createKeyComponents(s);
			c.gridx=1;
			c.gridy=1;
			c.weightx = 1;
			c.gridheight=4;
			this.add(keys,c);

			/**
			 * Code for self defined completions.
			 * @author Ulrich Küster
			 *
			 * They are stored in the settings as follows:
			 *
			 * AutoCompletion.SelfDefinedCompletion.count=<tt>number of self defined completions</tt>
			 * AutoCompletion.SelfDefinedCompletion.name<tt>number</tt>=<tt>name of this completion</tt>
			 * AutoCompletion.SelfDefinedCompletion.value<tt>number</tt>=<tt>value of this completion</tt>
			 * where 0 <= <tt>number</tt> < AutoCompletion.SelfDefinedCompletion.count
			 */

			// make a copy of the self defined completions that will be
			// written back in applyPreferences()
			this.selfDefinedCompletions = (Hashtable)source.selfDefinedCompletions.clone();

			c.gridx = 0;
			c.gridy = 5;
			c.gridheight = 1;
			c.gridwidth = 2;
			c.weighty = 0;
			c.insets = new Insets(10, 0, 10, 0);
			this.add(new JSeparator(SwingConstants.HORIZONTAL), c);

			JPanel sDCPanel = getSelfDefinedCompletionPanel();
			c.gridy = 6;
			c.weighty = 0.2;
			c.fill = GridBagConstraints.BOTH;
			c.insets = new Insets(0, 0, 0, 0);
			this.add(sDCPanel, c);

			c.weighty = 1;
			c.gridy++;
			this.add(new JPanel(), c);
		}

		private JPanel getSelfDefinedCompletionPanel() {
			JPanel sDCPanel = new JPanel();
			sDCPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
			sDCPanel.setLayout(new GridBagLayout());

			GridBagConstraints c = new GridBagConstraints(0, 0, 1, 1, 0.5, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0);
			sDCPanel.add(new JLabel(getString("prefs.SelfDefinedCompletions.title")+":"), c);

			final JLabel completionValue = new JLabel();
			JScrollPane temp = new JScrollPane(completionValue);
			temp.setBorder(new CompoundBorder(new TitledBorder(BorderFactory.createEtchedBorder(), getString( "prefs.SelfDefinedCompletions.completionValue.title")),new EmptyBorder(5, 5, 5, 5)));
			c.gridx = 1;
			c.gridy = 1;
			c.weighty = 0.5;
			sDCPanel.add(temp, c);

			List l = CollectionFactory.createLinkedList(this.selfDefinedCompletions.keySet());
			Collections.sort(l);
			DefaultListModel listModel = new DefaultListModel();
			for (Iterator iter = l.iterator(); iter.hasNext(); ) {
				listModel.addElement(iter.next());
			}
			final JList completionNames = new JList(listModel);
			completionNames.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			completionNames.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					if (!e.getValueIsAdjusting()) {
						String str = (String)completionNames.getSelectedValue();
						String display = "";
						if (str != null) {
							str = (String)DetailAutoCompletionPreferencesAdapter.this.selfDefinedCompletions.get(str);
							display = "<html><b><p>";
							for (int i = 0; i < str.length(); i++) {
								if (str.charAt(i) == '\n') {
									display += "</p><p>";
								} else {
									display += str.charAt(i);
								}
							}
							display += "</p></b></html>";
						}
						completionValue.setText(display);
					}
				}
			});
			if (completionNames.getModel().getSize() > 0) {
				completionNames.setSelectedIndex(0);
			}
			temp = new JScrollPane(completionNames);
			temp.setBorder(new TitledBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED), getString("prefs.SelfDefinedCompletions.completionNames.title")));
			c.gridx = 0;
			c.gridheight = 3;
			sDCPanel.add(temp, c);

			final JButton delete = new JButton(getString("prefs.SelfDefinedCompletions.deleteButton.caption"));
			delete.setMnemonic(getString("prefs.SelfDefinedCompletions.deleteButton.mnemonic").charAt(0));
			if (completionNames.getModel().getSize() == 0) {
				delete.setEnabled(false);
			}
			delete.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int index = completionNames.getSelectedIndex();
					String name = (String)completionNames.getSelectedValue();
					((DefaultListModel)completionNames.getModel()).remove(index);
					if (completionNames.getModel().getSize() == 0) {
						delete.setEnabled(false);
						completionValue.setText("");
					} else if (completionNames.getModel().getSize() > index) {
						completionNames.setSelectedIndex(index);
					} else {
						completionNames.setSelectedIndex(index - 1);
					}
					completionNames.repaint();
					DetailAutoCompletionPreferencesAdapter.this.selfDefinedCompletions.remove(name);
				}
			});

			final JButton newCompletion = new JButton(getString("prefs.SelfDefinedCompletions.newCompletionButton.caption"));
			newCompletion.setMnemonic(getString("prefs.SelfDefinedCompletions.newCompletionButton.mnemonic").charAt(0));
			newCompletion.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String[] nameAndValue = (new DefineCompletionDialog(JOptionPane.getFrameForComponent(newCompletion))).getNewCompletionNameAndValue();
					String name = nameAndValue[0];
					String value = nameAndValue[1];
					if (!name.equals("") && !value.equals("")) {
						DetailAutoCompletionPreferencesAdapter.this.selfDefinedCompletions.put(name, value);
						DefaultListModel lstModel = (DefaultListModel)completionNames.getModel();
						if (!lstModel.contains(name)) {
							lstModel.addElement(name);
						}
						completionNames.clearSelection();
						completionNames.setSelectedIndex(lstModel.indexOf(name));
						completionNames.repaint();
						delete.setEnabled(true);
					}
				}
			});

			c.gridx = 1;
			c.gridy = 2;
			c.gridheight = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weighty = 0;
			sDCPanel.add(newCompletion, c);

			c.gridy = 3;
			sDCPanel.add(delete, c);

			return sDCPanel;
		}

		protected Component createKeyComponents(AutoCompletion s) {
			JPanel p=new JPanel(new java.awt.GridBagLayout());
			java.awt.GridBagConstraints c=new java.awt.GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0);

			for(int i=0;i<4;i++) {
				c.gridy=i;
				p.add(new JLabel(getString("prefs.keys."+String.valueOf(i))),c);
			}

			c.gridx=1;
			keyFields=new KeyTextField[4];
			int ck[][]=s.getCompleterKeys();
			for(int i=0;i<4;i++) {
				c.gridy=i;
				keyFields[i]=new KeyTextField();
				keyFields[i].init(ck[i][0],ck[i][1]);
				p.add(keyFields[i],c);
			}
			return p;
		}

		public void addCompletionGUI(CompletionGUI cGUI) {
			forGUIs.addItem(cGUI);
			this.revalidate();
			this.doLayout();
		}

		public void setCurrentGUI(CompletionGUI cGUI) {
			forGUIs.setSelectedItem(cGUI);
		}

		public Component getComponent() {
			return this;
		}

		public String getTitle() {
			return getString("prefs.title");
		}

		public void applyPreferences() {
			source.setEnableAutoCompletion(cEnable.isSelected());
			source.setCurrentGUI((CompletionGUI)forGUIs.getSelectedItem());
			source.setLimitMakeCompletion(limitMakeCompletion.isSelected());
			source.setEmptyStubMode(iPopup.isSelected());
			int t=150;
			try{
				t=Integer.parseInt(tTime.getText());
			}catch(Exception exc) {tTime.setText("150");}
			source.setActivationTime(t);
			// maybe we could use the given array from AutoCompletion directly?
			int ck[][]=new int[4][2];
			for(int i=0;i<4;i++) {
				ck[i][0]=keyFields[i].getModifiers();
				ck[i][1]=keyFields[i].getKeyCode();
			}
			source.setCompleterKeys(ck);

			/**
			 * Apply preferences for self defined completions
			 */
			// delete old entries out of settings file
			String s = (String)settings.get("AutoCompletion.SelfDefinedCompletions.count");
			int completionCount = 0;
			if (s != null) {
				completionCount = Integer.parseInt(s);
			}
			for (int i = 0; i < completionCount; i++) {
				settings.remove("AutoCompletion.SelfDefinedCompletions.name" + i);
				settings.remove("AutoCompletion.SelfDefinedCompletions.value" + i);
			}
			// insert new values
			completionCount = 0;
			for (Iterator iter = selfDefinedCompletions.keySet().iterator(); iter.hasNext(); ) {
				String name = (String)iter.next();
				String value = (String)selfDefinedCompletions.get(name);
				settings.setProperty("AutoCompletion.SelfDefinedCompletions.name" + completionCount, name);
				settings.setProperty("AutoCompletion.SelfDefinedCompletions.value" + completionCount, value);
				completionCount++;
			}
			settings.setProperty("AutoCompletion.SelfDefinedCompletions.count", String.valueOf(completionCount));
			// update selfDefinedCompletion table of AutoCompletion
			source.selfDefinedCompletions.clear();
			source.selfDefinedCompletions.putAll(this.selfDefinedCompletions);
		}

		private class KeyTextField extends JTextField implements KeyListener{
			protected int modifiers=0;
			protected int key=0;
			public KeyTextField() {
				super(20);
				this.addKeyListener(this);
			}
			public void init(int modifiers,int key) {
				this.key=key;
				this.modifiers=modifiers;
				String s=KeyEvent.getKeyModifiersText(modifiers);
				if (s!=null && s.length()>0)
					s+='+'+KeyEvent.getKeyText(key);
				else
					s=KeyEvent.getKeyText(key);
				setText(s);
			}
			public void keyReleased(java.awt.event.KeyEvent p1) {
				// maybe should delete any input if there's no "stable"(non-modifying) key
			}
			public void keyPressed(java.awt.event.KeyEvent p1) {
				modifiers=p1.getModifiers();
				key=p1.getKeyCode();
				// avoid double string
				if (key==KeyEvent.VK_SHIFT || key==KeyEvent.VK_CONTROL || key==KeyEvent.VK_ALT || key==KeyEvent.VK_ALT_GRAPH) {
					int xored=0;
					switch(key) {
						case KeyEvent.VK_SHIFT: xored=KeyEvent.SHIFT_MASK;break;
						case KeyEvent.VK_CONTROL: xored=KeyEvent.CTRL_MASK;break;
						case KeyEvent.VK_ALT: xored=KeyEvent.ALT_MASK;break;
						case KeyEvent.VK_ALT_GRAPH: xored=KeyEvent.ALT_GRAPH_MASK;break;
					}
					modifiers^=xored;
				}
				String s=KeyEvent.getKeyModifiersText(modifiers);
				if (s!=null && s.length()>0)
					s+='+'+KeyEvent.getKeyText(key);
				else
					s=KeyEvent.getKeyText(key);
				setText(s);
				p1.consume();
			}
			public void keyTyped(java.awt.event.KeyEvent p1) {
			}
			// to allow "tab" as a key
			public boolean isManagingFocus() {
				return true;
			}
			public int getKeyCode() {
				return key;
			}
			public int getModifiers() {
				return modifiers;
			}
		}

		private class DefineCompletionDialog extends InternationalizedDialog {
			private JTextField name;
			private JTextArea value;
			private JButton ok, cancel;

			private DefineCompletionDialog(Frame frame) {
				super(frame, true);
				this.setTitle(AutoCompletion.getString("DefineCompletionDialog.title"));
				this.setSize(500, 200);

				JPanel cp = new JPanel();
				this.getContentPane().add(cp);
				cp.setBorder(new CompoundBorder(BorderFactory.createEtchedBorder(), new EmptyBorder(5, 5, 5, 5)));
				cp.setLayout(new GridBagLayout());
				GridBagConstraints c = new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0);

				name = new JTextField();
				name.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(), AutoCompletion.getString("DefineCompletionDialog.nameField.title")));
				cp.add(name, c);

				value = new JTextArea();
				value.setMargin(new Insets(4, 4, 4, 4));
				JScrollPane temp = new JScrollPane(value);
				temp.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(), AutoCompletion.getString("DefineCompletionDialog.valueField.title")));
				c.gridy = 1;
				c.fill = GridBagConstraints.BOTH;
				c.weighty = 1.0;
				cp.add(temp, c);

				ok = new JButton(AutoCompletion.getString("DefineCompletionDialog.okButton.caption"));
				ok.setMnemonic(AutoCompletion.getString("DefineCompletionDialog.okButton.mnemonic").charAt(0));
				c.gridy = 0;
				c.gridx = 1;
				c.weighty = 0.0;
				c.weightx = 0.0;
				c.fill = GridBagConstraints.HORIZONTAL;
				cp.add(ok, c);

				cancel = new JButton(AutoCompletion.getString("DefineCompletionDialog.cancelButton.caption"));
				cancel.setMnemonic(AutoCompletion.getString("DefineCompletionDialog.cancelButton.mnemonic").charAt(0));
				cancel.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						DefineCompletionDialog.this.quit();
					}
				});
				c.gridy = 1;
				c.anchor = GridBagConstraints.NORTH;
				cp.add(cancel, c);

				name.setNextFocusableComponent(value);
				value.setNextFocusableComponent(ok);
				ok.setNextFocusableComponent(cancel);
				cancel.setNextFocusableComponent(name);
			}

			private String[] getNewCompletionNameAndValue() {
				final String[] retVal = {"", ""};
				ok.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						retVal[0] = name.getText();
						retVal[1] = value.getText();
						quit();
					}
				});
				this.setLocationRelativeTo(getOwner());
				this.show();
				return retVal;
			}
		}
	}

	private static String getString(String key) {
		return Translations.getTranslation(AutoCompletion.class, key);
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
			defaultTranslations.put("prefs.autocompletion"        , "activate auto completion");
			defaultTranslations.put("prefs.limitmakecompletion"   , "limit completion of MAKE");
			defaultTranslations.put("prefs.limitmakecompletion.tooltip",
									"Determines, whether the completions of the make order shall be limited "+
									"to those items, whose resources are available.");
			defaultTranslations.put("prefs.stubmode"              , "Immediate proposal");
			defaultTranslations.put("prefs.time"                  , "Time");
			defaultTranslations.put("prefs.gui"                   , "Mode");
			defaultTranslations.put("prefs.SelfDefinedCompletions.title" , "Selfdefined order completions");
			defaultTranslations.put("prefs.SelfDefinedCompletions.completionValue.title" , "Value");
			defaultTranslations.put("prefs.SelfDefinedCompletions.completionNames.title" ,"Names");
			defaultTranslations.put("prefs.SelfDefinedCompletions.deleteButton.caption"  , "Delete");
			defaultTranslations.put("prefs.SelfDefinedCompletions.deleteButton.mnemonic" ,"d");
			defaultTranslations.put("prefs.SelfDefinedCompletions.newCompletionButton.caption" , "Add");
			defaultTranslations.put("prefs.SelfDefinedCompletions.newCompletionButton.mnemonic", "a");
			defaultTranslations.put("prefs.keys.0"                , "Forward");
			defaultTranslations.put("prefs.keys.1"                , "Backward");
			defaultTranslations.put("prefs.keys.2"                , "Complete");
			defaultTranslations.put("prefs.keys.3"                , "Cancel");
			defaultTranslations.put("prefs.title"                 , "Order Completion");
			defaultTranslations.put("DefineCompletionDialog.title","Define new order completion...");
			defaultTranslations.put("DefineCompletionDialog.nameField.title" , "Name");
			defaultTranslations.put("DefineCompletionDialog.valueField.title", "Value");
			defaultTranslations.put("DefineCompletionDialog.okButton.caption", "OK");
			defaultTranslations.put("DefineCompletionDialog.okButton.mnemonic","o");
			defaultTranslations.put("DefineCompletionDialog.cancelButton.caption","Cancel");
			defaultTranslations.put("DefineCompletionDialog.cancelButton.mnemonic","C");
		}
		return defaultTranslations;
	}
}
