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

package com.eressea.demo.desktop;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JSplitPane;

import com.eressea.util.CollectionFactory;

/**
 * DOCUMENT ME!
 *
 * @author Andreas
 * @version
 */
public class FrameTreeBuilder extends Object {
	/** TODO: DOCUMENT ME! */
	public static final String DEFAULT[] = { "SPLIT", "COMPONENT", "/SPLIT" };

	/** TODO: DOCUMENT ME! */
	public String tags[] = { "SPLIT", "COMPONENT", "/SPLIT" };
	private FrameTreeNode root;

	/**
	 * Creates new FrameTreeBuilder
	 */
	public FrameTreeBuilder() {
		this(DEFAULT);
	}

	/**
	 * Creates a new FrameTreeBuilder object.
	 *
	 * @param tags TODO: DOCUMENT ME!
	 */
	public FrameTreeBuilder(String tags[]) {
		this.tags = tags;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param r TODO: DOCUMENT ME!
	 *
	 * @throws Exception TODO: DOCUMENT ME!
	 */
	public void buildTree(Reader r) throws Exception {
		BufferedReader reader;

		if(r instanceof BufferedReader) {
			reader = (BufferedReader) r;
		} else {
			reader = new BufferedReader(r);
		}

		List v = CollectionFactory.createLinkedList();
		String s = null;

		do {
			s = reader.readLine();

			if(s != null) {
				v.add(s);
			}
		} while(s != null);

		buildTree(v.iterator());
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param file TODO: DOCUMENT ME!
	 *
	 * @throws Exception TODO: DOCUMENT ME!
	 */
	public void buildTree(String file[]) throws Exception {
		List v = CollectionFactory.createLinkedList();

		for(int i = 0; i < file.length; i++) {
			v.add(file[i]);
		}

		buildTree(v.iterator());
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param e TODO: DOCUMENT ME!
	 *
	 * @throws Exception TODO: DOCUMENT ME!
	 */
	public void buildTree(Iterator e) throws Exception {
		List dummyVec = CollectionFactory.createLinkedList();

		while(e.hasNext()) {
			dummyVec.add(((String) e.next()).trim());
		}

		e = dummyVec.iterator();

		String s = (String) e.next();

		if(s.startsWith(tags[0])) // "good" split
		 {
			root = new FrameTreeNode();

			if(buildPart(e, root, s)) {
				root = deleteUnnecessary(root);
			} else {
				root = null;
			}
		} else if(s.startsWith(tags[1])) // just one component
		 {
			root = new FrameTreeNode();
			root.setLeaf(true);
			s = s.substring(10);

			if(s.indexOf(' ') > 0) {
				StringTokenizer st = new StringTokenizer(s, " ");
				root.setName(st.nextToken());
				root.setConfiguration(st.nextToken());
			} else {
				root.setName(s);
			}
		}
	}

	protected boolean buildPart(Iterator e, FrameTreeNode me, String splitDef)
						 throws Exception
	{
		double percent = 0;
		int orientation = 0;
		String ppo = splitDef.substring(6);

		try {
			percent = Double.parseDouble(ppo.substring(0, ppo.indexOf(' ')));
		} catch(Exception exc) {
			percent = 0.5;
		}

		me.setPercentage(percent);

		if((percent - (double) ((int) percent)) == 0) {
			me.setAbsolute(true);
		} else {
			me.setAbsolute(false);
		}

		String os = ppo.substring(ppo.indexOf(' ') + 1).toLowerCase();

		if(os.equals("h")) {
			orientation = JSplitPane.HORIZONTAL_SPLIT;
		} else {
			orientation = JSplitPane.VERTICAL_SPLIT;
		}

		me.setOrientation(orientation);

		int cfound = 0;
		String s = null;

		do {
			s = (String) e.next();

			if(s.startsWith(tags[0]) && (cfound < 2)) // that's a new split
			 {
				FrameTreeNode ftn = new FrameTreeNode();
				me.setChild(cfound, ftn);

				if(buildPart(e, ftn, s)) {
					cfound++;
				}
			}

			if(s.startsWith(tags[1]) && (cfound < 2)) // that's a leaf
			 {
				FrameTreeNode ftn = new FrameTreeNode();
				ftn.setLeaf(true);
				s = s.substring(10);

				if(s.indexOf(' ') > 0) {
					StringTokenizer st = new StringTokenizer(s, " ");
					ftn.setName(st.nextToken());
					ftn.setConfiguration(st.nextToken());
				} else {
					ftn.setName(s);
				}

				me.setChild(cfound, ftn);
				cfound++;
			}
		} while(!s.equals(tags[2]));

		return cfound > 0;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param current TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static FrameTreeNode deleteUnnecessary(FrameTreeNode current) {
		if(!current.isLeaf()) {
			for(int i = 0; i < 2; i++) {
				FrameTreeNode child = current.getChild(i);

				if((child != null) && !child.isLeaf()) {
					child = deleteUnnecessary(child);

					if(child == null) {
						current.setChild(i, null);

						continue;
					}

					if((child.getChild(0) == null) || (child.getChild(1) == null)) {
						FrameTreeNode ftn = child.getChild(0);

						if(ftn == null) {
							ftn = child.getChild(1);
						}

						current.setChild(i, ftn);
					}
				}
			}

			if(current.getChild(0) == null) {
				if(current.getChild(1) == null) {
					return null;
				} else {
					current.setChild(0, current.getChild(1));
					current.setChild(1, null);
				}
			}
		}

		return current;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public FrameTreeNode getRoot() {
		return root;
	}
}
