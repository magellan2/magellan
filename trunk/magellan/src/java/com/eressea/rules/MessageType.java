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

package com.eressea.rules;

import com.eressea.GameData;
import com.eressea.ID;
import com.eressea.Identifiable;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class MessageType extends Identifiable {
	private String pattern = null;
	private String section = null;

	/**
	 * Creates a new MessageType object.
	 *
	 * @param id TODO: DOCUMENT ME!
	 */
	public MessageType(ID id) {
		this(id, null);
	}

	/**
	 * Creates a new MessageType object.
	 *
	 * @param id TODO: DOCUMENT ME!
	 * @param pattern TODO: DOCUMENT ME!
	 */
	public MessageType(ID id, String pattern) {
		super(id);
		setPattern(pattern);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param pattern TODO: DOCUMENT ME!
	 */
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getPattern() {
		return pattern;
	}

	/**
	 * Sets the name of the category of messages this message type belongs to.
	 *
	 * @param section TODO: DOCUMENT ME!
	 */
	public void setSection(String section) {
		this.section = section;
	}

	/**
	 * Returns the name of the category of messages this message type belongs
	 * to.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getSection() {
		return this.section;
	}

	/**
	 * Indicates whether this MessageType object is equal to another object.
	 * Returns true only if o is not null and an instance of class MessageType
	 * and o's id is equal to the id of this MessageType object.
	 *
	 * @param o TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean equals(Object o) {
		return (this == o) ||
			   (o instanceof MessageType &&
			   this.getID().equals(((MessageType) o).getID()));
	}

	/**
	 * Imposes a natural ordering on MessageType objects equivalent to the
	 * natural ordering of their ids.
	 *
	 * @param o TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int compareTo(Object o) {
		return this.getID().compareTo(((MessageType) o).getID());
	}

	/**
	 * Transfers all available information from the current message type to the
	 * new one.
	 *
	 * @param curGD fully loaded game data
	 * @param curMsgType a fully initialized and valid message type
	 * @param newGD the game data to be updated
	 * @param newMsgType an uninitialized message type to be updated with the
	 * 		  date from curMsgType
	 */
	public static void merge(GameData curGD, MessageType curMsgType,
							 GameData newGD, MessageType newMsgType) {
		if(curMsgType.getPattern() != null) {
			newMsgType.setPattern(curMsgType.getPattern());
		}

		if(curMsgType.getSection() != null) {
			newMsgType.setSection(curMsgType.getSection());
		}
	}
}
