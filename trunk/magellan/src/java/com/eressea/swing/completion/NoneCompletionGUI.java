/*
 * NoneCompletionGUI.java
 *
 * Created on 16. Oktober 2001, 12:53
 */

package com.eressea.swing.completion;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.swing.text.JTextComponent;

import com.eressea.completion.AutoCompletion;
import com.eressea.completion.Completion;
import com.eressea.util.CollectionFactory;

/**
 *
 * @author  Andreas
 * @version 
 */
public class NoneCompletionGUI extends AbstractCompletionGUI {
	
	protected Completion last=null;
	protected boolean offering=false;
	
	/** Creates new NoneCompletionGUI */
    public NoneCompletionGUI() {
    }
	
	public boolean editorMayLoseFocus() {
		return false;
	}
	
	public boolean editorMayUpdateCaret() {
		return false;
	}
	
	public Completion getSelectedCompletion() {
		return last;
	}
	
	public int[] getSpecialKeys() {
		return null;
	}
	
	public void init(AutoCompletion autoCompletion) {
	}
	
	public boolean isOfferingCompletion() {
		return offering;
	}
	
	public void offerCompletion(javax.swing.text.JTextComponent jTextComponent,java.util.Collection collection,java.lang.String str) {
		last=(Completion)collection.iterator().next();
		offering=true;
	}
	
	public void cycleCompletion(JTextComponent editor,Collection completions, String stub, int index) {
		Iterator it=completions.iterator();
		for(int i=0;i<=index;i++)
			last=(Completion)it.next();
	}
	
	public void specialKeyPressed(int param) {
	}
	
	public void stopOffer() {
		offering=false;
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
			defaultTranslations.put("gui.title","No display");
		}
		return defaultTranslations;
	}
	
}
