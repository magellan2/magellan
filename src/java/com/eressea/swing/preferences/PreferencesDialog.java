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

package com.eressea.swing.preferences;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.eressea.swing.CenterLayout;
import com.eressea.swing.InternationalizedDialog;
import com.eressea.util.CollectionFactory;

/**
 * A dialog allowing the user to set preferences or similar data.
 * 
 * <p>
 * This class has a kind of plug-in mechanism, that allows displaying a dynamic set of panels as
 * tab panes without changing the implementation.
 * </p>
 * 
 * <p>
 * In order to use this feature, these are the preconditions:
 * 
 * <ul>
 * <li>
 * Make all preferences of your actual UI component (a subclass of DataPanel or something similar,
 * e.g. the EMapDetailsPanel) publicly accessible (usually via get/set methods).
 * </li>
 * <li>
 * Write a container that makes these preferences editable (nice checkboxes and so on).
 * </li>
 * <li>
 * That container must, upon creation, reflect the current preferences of your UI component.
 * </li>
 * <li>
 * That container also has to provide a means of applying the changes made by the user to your UI
 * component.
 * </li>
 * <li>
 * Create a class implementing the PreferencesAdapter interface, which holds a reference to the
 * container. It is responsible for passing the your container to this PreferencesDialog and
 * making it 'apply' the changes made by the user to your UI component.
 * </li>
 * <li>
 * Finally, plug in your container into this PreferencesDialog object by calling the addTab()
 * method.
 * </li>
 * </ul>
 * </p>
 * 
 * <p>
 * Note that such a preferences container has to resize with its parent component, i.e. the
 * preferences dialog
 * </p>
 */
public class PreferencesDialog extends InternationalizedDialog {
	/**
	 * list of all PreferencesAdapters connected (also the children of ExtendedPreferencesAdapters)
	 */
	private Collection adapters = null;
	private JTabbedPane tabs = null;
	private DialogTree dialogtree;
	private Properties settings = null;

	/**
	 * Creates a modal or non-modal dialog with the specified title and the specified owner frame.
	 *
	 * @param owner the frame from which the dialog is displayed.
	 * @param modal true for a modal dialog, false for one that     allows others windows to be
	 * 		  active at the same     time.
	 * @param settings the String to display in the dialog's title     bar.
	 */
	public PreferencesDialog(Frame owner, boolean modal, Properties settings) {
		super(owner, modal);
		this.settings = settings;
		setTitle(getString("window.title"));

		adapters = CollectionFactory.createLinkedList();

		setContentPane(getMainPane());

		int width = Integer.parseInt(settings.getProperty("PreferencesDialog.width", "640"));
		int height = Integer.parseInt(settings.getProperty("PreferencesDialog.height", "400"));
		setSize(width, height);

		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int x = Integer.parseInt(settings.getProperty("PreferencesDialog.x",
													  ((screen.width - width) / 2) + ""));
		int y = Integer.parseInt(settings.getProperty("PreferencesDialog.y",
													  ((screen.height - height) / 2) + ""));
		setLocation(x, y);
	}

	/**
	 * Creates a new PreferencesDialog object.
	 *
	 * @param owner TODO: DOCUMENT ME!
	 * @param modal TODO: DOCUMENT ME!
	 * @param settings TODO: DOCUMENT ME!
	 * @param prefAdapters TODO: DOCUMENT ME!
	 */
	public PreferencesDialog(Frame owner, boolean modal, Properties settings,
							 Collection prefAdapters) {
		this(owner, modal, settings);

		for(Iterator iter = prefAdapters.iterator(); iter.hasNext();) {
			Object obj = iter.next();

			if(obj instanceof PreferencesAdapter) { // old style, direct adapter
				this.addTab((PreferencesAdapter) obj);
			} else if(obj instanceof PreferencesFactory) { // new style, create an adapter
				this.addTab(((PreferencesFactory) obj).createPreferencesAdapter());
			}
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 */
	public void updateLaF() {
		if(dialogtree != null) {
			dialogtree.updateUI();
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 */
	public void show() {
        initPreferences();
		dialogtree.showFirst();
		super.show();
	}

	/**
	 * Register a new container with this dialog. It will be put into its own tab and notified via
	 * the apply() method, if the user confirmed the presumably modified preferences.
	 *
	 * @param adapter TODO: DOCUMENT ME!
	 */
	public void addTab(PreferencesAdapter adapter) {
		//tabs.add(adapter.getTitle(), adapter.getComponent());
		dialogtree.addAdapter(adapter);
	}

	private void applyPreferences() {
		for(Iterator iter = adapters.iterator(); iter.hasNext();) {
			PreferencesAdapter pA = (PreferencesAdapter)iter.next();
			pA.applyPreferences();
		}
	}

	private void initPreferences() {
		for(Iterator iter = adapters.iterator(); iter.hasNext(); ) {
		    ((PreferencesAdapter)iter.next()).initPreferences();
		}
	}

	private Container getMainPane() {
		JButton okButton = new JButton(getString("btn.ok.caption"));
		okButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					applyPreferences();
					quit();
				}
			});

		JButton cancelButton = new JButton(getString("btn.cancel.caption"));
		cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					//reloadPreferences();
					quit();
				}
			});

		JButton applyButton = new JButton(getString("btn.apply.caption"));
		applyButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					applyPreferences();
				}
			});

		JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 5, 0));
		buttonPanel.setBorder(new EmptyBorder(5, 0, 0, 0));
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);

		// pavkovic 2003.09.09: deactivated until better preferences implementation
		// buttonPanel.add(applyButton);
		dialogtree = new DialogTree();

		JPanel main = new JPanel(new BorderLayout());
		main.setBorder(new EmptyBorder(5, 5, 5, 5));
		main.add(dialogtree, BorderLayout.CENTER);
		main.add(buttonPanel, BorderLayout.SOUTH);

		return main;
	}

	/**
	 * TODO: DOCUMENT ME!
	 */
	public void quit() {
		settings.setProperty("PreferencesDialog.width", getWidth() + "");
		settings.setProperty("PreferencesDialog.height", getHeight() + "");
		settings.setProperty("PreferencesDialog.x", getX() + "");
		settings.setProperty("PreferencesDialog.y", getY() + "");
		settings = null;
		adapters.clear();
		adapters = null;

		//tabs.removeAll();
		tabs = null;
		super.quit();
	}

	protected class DialogTree extends JPanel implements TreeSelectionListener {
		protected JTree tree;
		protected DefaultMutableTreeNode root;
		protected DefaultTreeModel model;
		protected TreePath firstAdapter;
		protected JPanel content;
		protected CardLayout cardLayout;

		/**
		 * Creates a new DialogTree object.
		 */
		public DialogTree() {
			super(new BorderLayout(10, 0));

			root = new DefaultMutableTreeNode();
			tree = new JTree(model = new DefaultTreeModel(root));
			tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
			tree.setEditable(false);
			tree.setRootVisible(false);
			tree.addTreeSelectionListener(this);

			//tree.setMinimumSize(new Dimension(100,100));
			//tree.setPreferredSize(new Dimension(200, 300));
			//tree.setSize(200, 300);
			JScrollPane treePane = new JScrollPane(tree);

			// maybe some code to center the tree and make it smaller
			JPanel help = new JPanel(CenterLayout.SPAN_BOTH_LAYOUT);

			//help.add(new JTextfield(getString("tree.title")));
			help.add(treePane);

			//this.add(help, BorderLayout.WEST);
			this.add(help, BorderLayout.WEST);

			content = new JPanel(cardLayout = new CardLayout(0, 0));

			//this.add(content, BorderLayout.CENTER);
			this.add(content, BorderLayout.CENTER);
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param a TODO: DOCUMENT ME!
		 */
		public void addAdapter(PreferencesAdapter a) {
			DefaultMutableTreeNode node = addAdapterImpl(a);
			model.insertNodeInto(node, root, root.getChildCount());
			tree.setModel(null);
			tree.setModel(model);

			if(firstAdapter == null) {
				firstAdapter = new TreePath(model.getPathToRoot(node));
			}

			/*Dimension dim = tree.getMaximumSize();
			  dim.width += 5;
			  treeContainer.setPreferredSize(dim);*/
		}

		protected DefaultMutableTreeNode addAdapterImpl(PreferencesAdapter pa) {
			// add adapter to adapter list
			adapters.add(pa);

			PreferencesInfo pref = new PreferencesInfo(pa);
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(pref);

			Component c = pa.getComponent();
			Dimension dim = c.getPreferredSize();
			dim.width += 5;
			dim.height += 5;

			JScrollPane pane = new JScrollPane(c);
			pane.setBorder(null);

			JPanel help = new JPanel(CenterLayout.SPAN_X_LAYOUT);
			help.add(pane);

			content.add(pane, pa.getTitle());

			if(pa instanceof ExtendedPreferencesAdapter) {
				for(Iterator iter = ((ExtendedPreferencesAdapter) pa).getChildren().iterator();
						iter.hasNext();) {
					DefaultMutableTreeNode subNode = addAdapterImpl((PreferencesAdapter) iter.next());

					if(subNode != null) {
						node.add(subNode);
					}
				}
			}

			return node;
		}

		/**
		 * TODO: DOCUMENT ME!
		 */
		public void showFirst() {
			// expand all nodes
			if(root.getChildCount() > 0) {
				for(int i = 0; i < root.getChildCount(); i++) {
					openNode(root.getChildAt(i));
				}
			}

			String s = settings.getProperty("PreferencesDialog.DialogTree.SelectedRow", "-1");

			if(!s.equals("-1")) {
				tree.setSelectionRow(Integer.parseInt(s));
			} else {
				tree.setSelectionPath(firstAdapter);
			}
		}

		protected void openNode(TreeNode node) {
			tree.expandPath(new TreePath(model.getPathToRoot(node)));

			if(node.getChildCount() > 0) {
				for(int i = 0; i < node.getChildCount(); i++) {
					openNode(node.getChildAt(i));
				}
			}
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param treeSelectionEvent TODO: DOCUMENT ME!
		 */
		public void valueChanged(javax.swing.event.TreeSelectionEvent treeSelectionEvent) {
			// store the selected row (we know, that it can be only one)
			int selection[] = tree.getSelectionRows();

			if(selection.length > 0) {
				settings.setProperty("PreferencesDialog.DialogTree.SelectedRow",
									 String.valueOf(selection[0]));
			}

			PreferencesInfo pref = (PreferencesInfo) ((DefaultMutableTreeNode) tree.getSelectionPath()
																				   .getLastPathComponent()).getUserObject();

			if(pref != null) {
				cardLayout.show(content, pref.toString());
			}
		}

		/**
		 * a wrapper class to display a PreferencesAdapter node with its title.
		 */
		protected class PreferencesInfo {
			PreferencesAdapter adapter;

			/**
			 * Creates a new PreferencesInfo object.
			 *
			 * @param pa TODO: DOCUMENT ME!
			 */
			public PreferencesInfo(PreferencesAdapter pa) {
				adapter = pa;
			}

			/**
			 * TODO: DOCUMENT ME!
			 *
			 * @return TODO: DOCUMENT ME!
			 */
			public String toString() {
				return adapter.getTitle();
			}
		}
	}

	// pavkovic 2003.01.28: this is a Map of the default Translations mapped to this class
	// it is called by reflection (we could force the implementation of an interface,
	// this way it is more flexible.)
	// Pls use this mechanism, so the translation files can be created automagically
	// by inspecting all classes.
	private static Map defaultTranslations;

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static synchronized Map getDefaultTranslations() {
		if(defaultTranslations == null) {
			defaultTranslations = CollectionFactory.createHashtable();
			defaultTranslations.put("window.title", "Options");
			defaultTranslations.put("tree.title", "Category");
			defaultTranslations.put("btn.ok.caption", "OK");
			defaultTranslations.put("btn.cancel.caption", "Abort");
			defaultTranslations.put("btn.apply.caption", "Apply");
		}

		return defaultTranslations;
	}
}
