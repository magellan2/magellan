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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.eressea.GameData;
import com.eressea.swing.context.ContextFactory;
import com.eressea.util.logging.Logger;

/**
 * A context management class for trees.
 *
 * @author Andreas
 * @version
 */
public class ContextManager extends MouseAdapter {
	private final static Logger log = Logger.getInstance(ContextManager.class);

	private Collection listeners = null;
	private JTree source;
	private Collection selection = null;
	private GameData data = null;
	private ContextFactory failFactory = null;
	private Object failArgument = null;
	private Map simpleObjects = null;

	/**
	 * Creates new ContextManager
	 *
	 * @param source TODO: DOCUMENT ME!
	 */
	public ContextManager(JTree source) {
		this.source = source;
		source.addMouseListener(this);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param data TODO: DOCUMENT ME!
	 */
	public void setGameData(GameData data) {
		this.data = data;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param selection TODO: DOCUMENT ME!
	 */
	public void setSelection(Collection selection) {
		this.selection = selection;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param failArgument TODO: DOCUMENT ME!
	 * @param failFactory TODO: DOCUMENT ME!
	 */
	public void setFailFallback(Object failArgument, ContextFactory failFactory) {
		this.failArgument = failArgument;
		this.failFactory = failFactory;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param simpleObjects TODO: DOCUMENT ME!
	 */
	public void setSimpleObjects(Map simpleObjects) {
		this.simpleObjects = simpleObjects;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param o TODO: DOCUMENT ME!
	 * @param factory TODO: DOCUMENT ME!
	 */
	public void putSimpleObject(Object o, ContextFactory factory) {
		if(simpleObjects == null) {
			simpleObjects = new HashMap();
		}

		simpleObjects.put(o, factory);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param o TODO: DOCUMENT ME!
	 */
	public void removeSimpleObject(Object o) {
		if(simpleObjects == null) {
			return;
		}

		simpleObjects.remove(o);

		if(simpleObjects.size() == 0) {
			simpleObjects = null;
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param cl TODO: DOCUMENT ME!
	 */
	public void addContextListener(ContextListener cl) {
		if(listeners == null) {
			listeners = new LinkedList();
		}

		listeners.add(cl);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param cl TODO: DOCUMENT ME!
	 */
	public void removeContextListener(ContextListener cl) {
		if(listeners == null) {
			return;
		}

		listeners.remove(cl);

		if(listeners.size() == 0) {
			listeners = null;
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param e TODO: DOCUMENT ME!
	 */
	public void mouseClicked(MouseEvent e) {
		if((e.getModifiers() & MouseEvent.BUTTON3_MASK) != 0) {
			TreePath path = source.getPathForLocation(e.getX(), e.getY());
			DefaultMutableTreeNode node = null;
			boolean found = false;

			if(path != null) {
				Object last = path.getLastPathComponent();

				if(last instanceof DefaultMutableTreeNode) {
					node = (DefaultMutableTreeNode) last;

					Object user = node.getUserObject();

					if((user != null) && (user instanceof Changeable)) {
						Changeable change = (Changeable) user;

						if(((change.getChangeModes() & Changeable.CONTEXT_MENU) != 0) &&
							   (change.getContextFactory() != null)) {
							JPopupMenu menu = change.getContextFactory().createContextMenu(data,
																						   change.getArgument(),
																						   selection,
																						   node);

							if(menu != null) {
								showMenu(menu, source, e.getX(), e.getY());
								found = true;
							}
						}
					}

					if(!found) {
						found = checkSimpleObjects(e, node, user);
					}
				}
			}

			if(!found) {
				if(failFactory != null) {
					JPopupMenu menu = null;

					if(failArgument != null) {
						menu = failFactory.createContextMenu(data, failArgument, selection, node);
					} else {
						if(node != null) {
							menu = failFactory.createContextMenu(data, node.getUserObject(),
																 selection, node);
						}

						if(menu == null) {
							menu = failFactory.createContextMenu(data, node, selection, node);
						}
					}

					if(menu != null) {
						showMenu(menu, source, e.getX(), e.getY());
					} else {
						fireContextFailed(source, e);
					}
				} else {
					fireContextFailed(source, e);
				}
			}
		}
	}

	protected boolean checkSimpleObjects(MouseEvent e, DefaultMutableTreeNode node, Object user) {
		try {
			if(simpleObjects != null) {
				if(simpleObjects.containsKey(user.getClass())) {
					ContextFactory factory = (ContextFactory) simpleObjects.get(user.getClass());
					JPopupMenu menu = factory.createContextMenu(data, user, selection, node);

					if(menu != null) {
						showMenu(menu, source, e.getX(), e.getY());

						return true;
					}
				}

				if(simpleObjects.containsKey(node.getClass())) {
					ContextFactory factory = (ContextFactory) simpleObjects.get(node.getClass());
					JPopupMenu menu = factory.createContextMenu(data, node, selection, node);

					if(menu != null) {
						showMenu(menu, source, e.getX(), e.getY());

						return true;
					}
				}
			}
		} catch(Exception exc) {
			log.error(exc);
		}

		return false;
	}

	protected void fireContextFailed(Component src, MouseEvent e) {
		if(listeners != null) {
			Iterator it = listeners.iterator();

			while(it.hasNext()) {
				((ContextListener) it.next()).contextFailed(src, e);
			}
		}
	}

	/**
	 * Shows the given menu at position x,y. If there is not enough space on the screen, the menu
	 * not shown down and right from (x,y) but left or up or both of that point.
	 *
	 * @param menu TODO: DOCUMENT ME!
	 * @param c TODO: DOCUMENT ME!
	 * @param x TODO: DOCUMENT ME!
	 * @param y TODO: DOCUMENT ME!
	 */
	private void showMenu(JPopupMenu menu, Component c, int x, int y) {
		Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		Point showAtPoint = new Point(x, y);
		javax.swing.SwingUtilities.convertPointToScreen(showAtPoint, c);

		int width = (int) menu.getPreferredSize().getWidth();
		int height = (int) menu.getPreferredSize().getHeight();

		if((screen.width - showAtPoint.x) < width) {
			showAtPoint.x -= width;
		}

		if((screen.height - showAtPoint.y) < height) {
			showAtPoint.y -= height;
		}

		javax.swing.SwingUtilities.convertPointFromScreen(showAtPoint, c);
		menu.show(c, showAtPoint.x, showAtPoint.y);
	}
}
