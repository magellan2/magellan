package com.eressea.swing.desktop;


import javax.swing.JPanel;

/** 
 * This object represents a kind of view. Views are a JPanel together
 * with a title and an identifier
 */
public interface View {
	
	/** 
	 * Returns the (localized) name of the View
	 */
	public String getName();
	
	/** 
	 * Returns the unique identifier name of the View
	 */
	public String getIdentifier();

	/** 
	 * returns a Panel of this view
	 */
	public JPanel getPanel();
}

