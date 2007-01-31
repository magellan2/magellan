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

package com.eressea.swing.map;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import com.eressea.swing.InternationalizedPanel;
import com.eressea.swing.preferences.PreferencesAdapter;
import com.eressea.util.CollectionFactory;
import com.eressea.util.PropertiesHelper;
import com.eressea.util.logging.Logger;

/**
 * The pereferences panel providing a GUI to configure a Mapper object.
 */
public class MapperPreferences extends InternationalizedPanel implements PreferencesAdapter,
																		 ActionListener
{
	private static final Logger log = Logger.getInstance(MapperPreferences.class);

	// The source component to configure
	private Mapper source = null;
	

	
	// GUI elements
	private JCheckBox chkDeferPainting = null;
	private JCheckBox showTooltips;

	private JTabbedPane planes;

	// the map holding the associations between planes and renderers
	// maps RenderingPlane to MapCellRenderer
	private Map planeMap = CollectionFactory.createHashMap();
	private Collection rendererAdapters = CollectionFactory.createLinkedList();

	// for changing tooltips
	private ToolTipSwitcherDialog ttsDialog;
	private boolean dialogShown = false;

	/**
	 * Creates a new MapperPreferences object.
	 *
	 * @param m TODO: DOCUMENT ME!
	 */
	public MapperPreferences(Mapper m) {
		this.source = m;
		init();
	}

	private void init() {
		chkDeferPainting = new JCheckBox(getString("chk.deferpainting.caption"),
										 source.isDeferringPainting());
		showTooltips = new JCheckBox(getString("showtooltips.caption"), source.isShowingTooltip());

		JButton configureTooltips = new JButton(getString("showtooltips.configure.caption"));
		configureTooltips.addActionListener(this);

		JPanel helpPanel = new JPanel(new GridBagLayout());
		helpPanel.setBorder(BorderFactory.createTitledBorder(""));

		GridBagConstraints gbc = new GridBagConstraints(0, 0, 2, 1, 1, 0, GridBagConstraints.WEST,
														GridBagConstraints.HORIZONTAL,
														new Insets(3, 3, 3, 3), 0, 0);
		helpPanel.add(chkDeferPainting, gbc);
		gbc.gridy++;
		gbc.gridwidth = 1;
		gbc.weightx = 0;
		helpPanel.add(showTooltips, gbc);
		gbc.gridx++;
		gbc.fill = GridBagConstraints.NONE;
		helpPanel.add(configureTooltips, gbc);

		JPanel rendererPanel = new JPanel(new BorderLayout());
		rendererPanel.setBorder(BorderFactory.createTitledBorder(getString("border.rendereroptions")));
		planes = new JTabbedPane();
		rendererPanel.add(planes, BorderLayout.CENTER);

		for(Iterator iter = source.getPlanes().listIterator(); iter.hasNext();) {
			RenderingPlane plane = (RenderingPlane) iter.next();
			JPanel aRendererPanel = new JPanel(new GridBagLayout());
			gbc = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER,
										 GridBagConstraints.BOTH, new Insets(3, 3, 3, 3), 0, 0);

			JComboBox availableRenderers = new JComboBox();
			availableRenderers.setEditable(false);
			availableRenderers.addItem(getString("cmb.renderers.disabled"));

			final CardLayout cards = new CardLayout();
			final JPanel temp = new JPanel(cards);
			temp.add(new JPanel(), "NONE");

			for(Iterator iterator = source.getRenderers(plane.getIndex()).iterator();
					iterator.hasNext();) {
				MapCellRenderer r = (MapCellRenderer) iterator.next();
				availableRenderers.addItem(r);

				PreferencesAdapter adap = r.getPreferencesAdapter();
				temp.add(adap.getComponent(), r.getName());
				rendererAdapters.add(adap);
			}

			availableRenderers.addItemListener(new ItemListener() {
					public void itemStateChanged(ItemEvent e) {
						if(e.getStateChange() == ItemEvent.SELECTED) {
							if(e.getItem() instanceof MapCellRenderer) {
								MapCellRenderer r = (MapCellRenderer) e.getItem();
								cards.show(temp, r.getName());

								// store that a renderer has been set for the selected plane
								Object o = source.getPlanes().get(r.getPlaneIndex());
								planeMap.put(o, r);
							} else {
								// a mapper was deactivated
								int selectedIndex = planes.getSelectedIndex();

								if(selectedIndex > -1) {
									// store that a renderer has been set for the selected plane
									Object o = source.getPlanes().get(selectedIndex);
									planeMap.put(o, null);
								}

								cards.first(temp);
							}

							temp.revalidate();
							temp.repaint();
						}
					}
				});

			JLabel lblRenderers = new JLabel(getString("lbl.renderer.caption"));
			aRendererPanel.add(lblRenderers, gbc);
			gbc.gridx++;
			gbc.weightx = 1;
			aRendererPanel.add(availableRenderers, gbc);
			gbc.gridy++;
			gbc.gridx = 0;
			gbc.gridwidth = 2;
			gbc.weighty = 1;
			aRendererPanel.add(temp, gbc);

			MapCellRenderer r = null;
			r = plane.getRenderer();

			if(r != null) {
				availableRenderers.setSelectedItem(r);
			} else {
				availableRenderers.setSelectedIndex(0);
			}

			planes.addTab(plane.toString(), aRendererPanel);
		}

		setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.CENTER,
													  GridBagConstraints.HORIZONTAL,
													  new Insets(3, 3, 3, 3), 0, 0);
		add(helpPanel, c);
		c.gridy++;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		add(rendererPanel, c);
	}

    public void initPreferences() {
        // TODO: implement it
    }

	/**
	 * TODO: DOCUMENT ME!
	 */
	public void applyPreferences() {
		source.deferPainting(chkDeferPainting.isSelected());
		source.setShowTooltip(showTooltips.isSelected());
		
		if(dialogShown) {
			String tDefinition = ttsDialog.getSelectedToolTip();

			if(tDefinition != null) {
				source.setTooltipDefinition(tDefinition);
			}

			dialogShown = false;
		}

		// set renderer for plane, taking those from the map is enough since only they can be changed
		for(Iterator iter = planeMap.keySet().iterator(); iter.hasNext();) {
			RenderingPlane p = (RenderingPlane) iter.next();
			source.setRenderer((MapCellRenderer) planeMap.get(p), p.getIndex());
		}

		// apply changes on the renderers
		for(Iterator iter = rendererAdapters.iterator(); iter.hasNext();) {
			PreferencesAdapter adap = (PreferencesAdapter) iter.next();
			adap.applyPreferences();
		}

		Mapper.setRenderContextChanged(true);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Component getComponent() {
		return this;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getTitle() {
		return getString("title");
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param p1 TODO: DOCUMENT ME!
	 */
	public void actionPerformed(java.awt.event.ActionEvent p1) {
		if(ttsDialog == null) {
			Component parent = getTopLevelAncestor();

			if(parent instanceof Frame) {
				ttsDialog = new ToolTipSwitcherDialog((Frame) parent,
													  getString("tooltipdialog.title"));
			} else {
				ttsDialog = new ToolTipSwitcherDialog((Dialog) parent,
													  getString("tooltipdialog.title"));
			}
		}

		ttsDialog.show();
		dialogShown = true;
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
			defaultTranslations.put("chk.deferpainting.caption",
									"Paint map graphics immediately before they are fully loaded");
			defaultTranslations.put("tooltipdialog.export", "Export...");
			defaultTranslations.put("tooltipdialog.import", "Import...");
			defaultTranslations.put("tooltipdialog.add", "Add...");
			defaultTranslations.put("tooltipdialog.editbymask", "Edit with mask...");
			defaultTranslations.put("tooltipdialog.addbymask", "Add with mask...");
			defaultTranslations.put("tooltipdialog.addbymask.excludeocean", "Exclude oceans");
			defaultTranslations.put("tooltipdialog.addbymask.cancel", "Cancel");
			defaultTranslations.put("tooltipdialog.addbymask.ok", "OK");
			defaultTranslations.put("tooltipdialog.exportdialog.title", "Export");
			defaultTranslations.put("tooltipdialog.importdialog.title", "Import");
			defaultTranslations.put("tooltipdialog.tooltipinfo.title", "Info");
			defaultTranslations.put("tooltipdialog.info", "Info...");
			defaultTranslations.put("lbl.renderingplane.caption", "Rendering plane: ");
			defaultTranslations.put("lbl.renderer.caption", "Renderer: ");
			defaultTranslations.put("border.rendereroptions", "Renderer options");
			defaultTranslations.put("cmb.renderers.disabled", "<disabled>");
			defaultTranslations.put("title", "Map");
			defaultTranslations.put("tooltipdialog.addbymask.columns", "Number of columns");
			defaultTranslations.put("tooltipdialog.addbymask.rows", "Number of rows");
			defaultTranslations.put("tooltipdialog.addbymask.name", "Name of Tooltip");
			defaultTranslations.put("tooltipdialog.addbymask.title", "Add by mask");
			defaultTranslations.put("tooltipdialog.tooltipinfo.ok", "OK");
			defaultTranslations.put("tooltipdialog.addtooltipdialog.title2", "Edit...");
			defaultTranslations.put("tooltipdialog.edit", "Edit...");
			defaultTranslations.put("tooltipdialog.delete", "Delete");
			defaultTranslations.put("addtooltipdialog.value", "Value");
			defaultTranslations.put("addtooltipdialog.name", "Name");
			defaultTranslations.put("tooltipdialog.addtooltipdialog.title", "Add");
			defaultTranslations.put("addtooltipdialog.Cancel", "Cancel");
			defaultTranslations.put("addtooltipdialog.OK", "OK");
			defaultTranslations.put("imexportdialog.ioerror", "I/O-Error:");
			defaultTranslations.put("imexportdialog.fnf", "File not found!");
			defaultTranslations.put("imexportdialog.nodeffound", "No definitions found!");
			defaultTranslations.put("imexportdialog.Cancel", "Cancel");
			defaultTranslations.put("imexportdialog.OK", "OK");
			defaultTranslations.put("showtooltips.caption", "Show tooltips");
			defaultTranslations.put("tooltipdialog.title", "Choose Tool Tip");
			defaultTranslations.put("tooltipdialog.ok", "OK");
			defaultTranslations.put("tooltipdialog.notooltips", "No tool tip definitions found.");
			defaultTranslations.put("showtooltips.configure.caption", "Choose...");
		}

		return defaultTranslations;
	}

	protected class ToolTipSwitcherDialog extends JDialog implements ActionListener,
																	 javax.swing.event.ListSelectionListener
	{
		/**
		 * A simple info dialog consisting of a list on the left side, where all currently known
		 * replacers are shown, and a text area on the right that displays the description of the
		 * selected replacer.
		 */
		protected class ToolTipReplacersInfo extends JDialog
			implements javax.swing.event.ListSelectionListener, ActionListener
		{
			protected JList list;
			protected JTextArea text;
			protected List rList;
			protected com.eressea.util.replacers.ReplacerFactory replacerMap;

			/**
			 * Creates a new ToolTipReplacersInfo object.
			 *
			 * @param parent TODO: DOCUMENT ME!
			 * @param title TODO: DOCUMENT ME!
			 */
			public ToolTipReplacersInfo(Dialog parent, String title) {
				super(parent, title, false);

				list = new JList();
				list.setFixedCellWidth(150);
				list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				list.addListSelectionListener(this);

				text = new JTextArea(50, 25);
				text.setEditable(false);
				text.setLineWrap(true);
				text.setWrapStyleWord(true);

				Container c = this.getContentPane();
				c.setLayout(new BorderLayout());

				JScrollPane p = new JScrollPane(list);
				p.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
				c.add(p, BorderLayout.WEST);
				text.setBackground(c.getBackground());
				c.add(new JScrollPane(text), BorderLayout.CENTER);

				JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER));
				JButton exit = new JButton(getString("tooltipdialog.tooltipinfo.ok"));
				exit.addActionListener(this);
				south.add(exit);
				c.add(south, BorderLayout.SOUTH);

				this.setSize(600, 400); // because of some pack mysteries
				this.setLocationRelativeTo(parent);
			}

			/**
			 * TODO: DOCUMENT ME!
			 */
			public void show() {
				replacerMap = com.eressea.util.replacers.ReplacerHelp.getDefaultReplacerFactory();

				if(rList == null) {
					rList = CollectionFactory.createLinkedList();
				}

				rList.clear();
				rList.addAll(replacerMap.getReplacers());
				Collections.sort(rList);

				list.setListData(rList.toArray());

				super.show();
			}

			/**
			 * TODO: DOCUMENT ME!
			 *
			 * @param e TODO: DOCUMENT ME!
			 */
			public void actionPerformed(ActionEvent e) {
				this.setVisible(false);
			}

			/**
			 * TODO: DOCUMENT ME!
			 *
			 * @param lse TODO: DOCUMENT ME!
			 */
			public void valueChanged(javax.swing.event.ListSelectionEvent lse) {
				if(list.getSelectedIndex() >= 0) {
					com.eressea.util.replacers.Replacer rep = (replacerMap.createReplacer((String) rList.get(list.getSelectedIndex())));

					if(rep == null) {
						text.setText("Internal error - please report.");
					} else {
						text.setText(rep.getDescription());
					}
				}
			}
		}

		/**
		 * Imports/exports tooltips from/to text files. The text files are interpreted
		 * two-line-wise. The first line acts as the name and the second one as the definition.
		 */
		protected class ImExportDialog extends JDialog implements ActionListener {
			protected JFileChooser fileChooser;
			protected Dialog parent;
			protected JList list;
			protected java.util.List data;
			protected JButton ok;
			protected boolean approved = false;

			/**
			 * Creates a new ImExportDialog object.
			 *
			 * @param parent TODO: DOCUMENT ME!
			 * @param title TODO: DOCUMENT ME!
			 */
			public ImExportDialog(Dialog parent, String title) {
				super(parent, title, true);

				this.parent = parent;

				list = new JList();
				list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

				JScrollPane sp = new JScrollPane(list);
				sp.setPreferredSize(new Dimension(100, 200));

				this.getContentPane().add(sp, BorderLayout.CENTER);

				JPanel p = new JPanel(new FlowLayout());
				((FlowLayout) p.getLayout()).setAlignment(FlowLayout.CENTER);

				JButton b = new JButton(getString("imexportdialog.OK"));
				ok = b;
				b.addActionListener(this);
				p.add(b);
				b = new JButton(getString("imexportdialog.Cancel"));
				b.addActionListener(this);
				p.add(b);

				this.getContentPane().add(p, BorderLayout.SOUTH);
			}

			/**
			 * TODO: DOCUMENT ME!
			 *
			 * @param doImport TODO: DOCUMENT ME!
			 */
			public void showDialog(boolean doImport) {
				File file = null;
				JFileChooser jfc = new JFileChooser(com.eressea.demo.Client.getMagellanDirectory());
				int ret = 0;

				if(doImport) {
					ret = jfc.showOpenDialog(this);
				} else {
					ret = jfc.showSaveDialog(this);
				}

				if(ret == JFileChooser.APPROVE_OPTION) {
					try {
						file = jfc.getSelectedFile();
						data = CollectionFactory.createLinkedList();

						if(doImport) {
							if(file.exists()) {
								BufferedReader br = new BufferedReader(new FileReader(file));
								String s1 = null;
								String s2 = null;

								try {
									do {
										s1 = br.readLine();
										s2 = br.readLine();

										if((s1 != null) && !s1.equals("") && (s2 != null) &&
											   !s2.equals("")) {
											data.add(s1);
											data.add(s2);
										}
									} while(s2 != null);
								} catch(Exception inner) {
								}

								if(data.size() > 0) {
									Object a[] = new Object[data.size() / 2];

									for(int i = 0; i < a.length; i++) {
										a[i] = data.get(i * 2);
									}

									list.setListData(a);
									this.pack();
									this.setLocationRelativeTo(parent);
									approved = false;
									this.show();

									if(approved) {
										int indices[] = list.getSelectedIndices();

										if((indices != null) && (indices.length > 0)) {
											for(int i = 0; i < indices.length; i++) {
												source.addTooltipDefinition((String) data.get(indices[i] * 2),
																			(String) data.get((indices[i] * 2) +
																							  1));
											}
										}
									}
								} else { // no def found, show error
									JOptionPane.showMessageDialog(this,
																  getString("imexportdialog.nodeffound"));
								}
							} else { // file not found, show error
								JOptionPane.showMessageDialog(this, getString("imexportdialog.fnf"));
							}
						} else {
							data = source.getAllTooltipDefinitions();

							Object a[] = new Object[data.size() / 2];

							for(int i = 0; i < a.length; i++) {
								a[i] = data.get(i * 2);
							}

							list.setListData(a);
							this.pack();
							this.setLocationRelativeTo(parent);
							approved = false;
							this.show();

							if(approved) {
								int indices[] = list.getSelectedIndices();

								if((indices != null) && (indices.length > 0)) {
									PrintWriter bw = new PrintWriter(new FileWriter(file));
									ListModel model = list.getModel();

									for(int i = 0; i < indices.length; i++) {
										bw.println(model.getElementAt(indices[i]));
										bw.println(data.get((indices[i] * 2) + 1));
									}

									bw.close();
								}
							}
						}
					} catch(Exception exc) { // some I/O Error, show it
						JOptionPane.showMessageDialog(this,
													  getString("imexportdialog.ioerror") +
													  exc.toString());
						log.error(exc);
					}
				}
			}

			/**
			 * TODO: DOCUMENT ME!
			 *
			 * @param actionEvent TODO: DOCUMENT ME!
			 */
			public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
				if(actionEvent.getSource() == ok) {
					approved = true;
				}

				this.setVisible(false);
			}
		}

		/**
		 * A dialog for adding/editing tooltips. After successful editing(name and definition !=
		 * ""), all line breaks are terminated from the value since they cause errors in the
		 * replacer engine(keywords can't be recognized if divided by a line break character).
		 */
		protected class AddTooltipDialog extends JDialog implements ActionListener {
			protected JButton cancel;
			protected JTextField name;
			protected JTextArea value;
			protected boolean existed = false;
			protected String origName = null;
			protected int origIndex = -1;

			/**
			 * Creates a new AddTooltipDialog object.
			 *
			 * @param parent TODO: DOCUMENT ME!
			 * @param title TODO: DOCUMENT ME!
			 * @param name TODO: DOCUMENT ME!
			 * @param def TODO: DOCUMENT ME!
			 * @param index TODO: DOCUMENT ME!
			 */
			public AddTooltipDialog(Dialog parent, String title, String name, String def, int index) {
				super(parent, title, true);

				if((name != null) && (def != null)) {
					existed = true;
					origName = name;
					origIndex = index;
				}

				JPanel center = new JPanel(new GridBagLayout());
				GridBagConstraints gbc = new GridBagConstraints();
				gbc.fill = GridBagConstraints.HORIZONTAL;
				gbc.gridx = 0;
				gbc.gridy = 0;
				gbc.gridwidth = 1;
				gbc.gridheight = 1;
				gbc.weightx = 0;
				gbc.anchor = GridBagConstraints.WEST;

				center.add(new JLabel(getString("addtooltipdialog.name")), gbc);
				gbc.gridy++;
				center.add(new JLabel(getString("addtooltipdialog.value")), gbc);

				gbc.gridx = 1;
				gbc.gridy = 0;
				gbc.weightx = 1;
				center.add(this.name = new JTextField(name, 20), gbc);
				gbc.gridy++;
				gbc.weighty = 1;
				gbc.fill = GridBagConstraints.BOTH;
				center.add(new JScrollPane(value = new JTextArea(def, 5, 20)), gbc);

				value.setLineWrap(true);

				this.getContentPane().add(center, BorderLayout.CENTER);

				JPanel s = new JPanel(new FlowLayout(FlowLayout.CENTER));

				JButton b = new JButton(getString("addtooltipdialog.OK"));
				b.addActionListener(this);
				s.add(b);

				b = new JButton(getString("addtooltipdialog.Cancel"));
				b.addActionListener(this);
				cancel = b;
				s.add(b);

				this.getContentPane().add(s, BorderLayout.SOUTH);

				this.pack();

				// getMinimumSize doesn't seem to work properly, so a little bit more
				if(this.getWidth() < 300) {
					this.setSize(300, this.getHeight());
				}

				if(this.getHeight() < 100) {
					this.setSize(this.getWidth(), 100);
				}

				this.setLocationRelativeTo(parent);
			}

			/**
			 * TODO: DOCUMENT ME!
			 *
			 * @return TODO: DOCUMENT ME!
			 */
			public Dimension getMinimumSize() {
				return new Dimension(300, 100);
			}

			/**
			 * TODO: DOCUMENT ME!
			 *
			 * @param actionEvent TODO: DOCUMENT ME!
			 */
			public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
				if(actionEvent.getSource() != cancel) {
					if((name.getText() != null) && !name.getText().equals("") &&
						   (value.getText() != null) && !value.getText().equals("")) {
						StringBuffer buf = new StringBuffer(value.getText());

						//remove all '\n'
						int i = 0;

						while(i < buf.length()) {
							if(buf.charAt(i) == '\n') {
								buf.deleteCharAt(i);
								i = 0; // start again
							} else {
								i++;
							}
						}

						if(existed && origName.equals(name.getText())) { // need to overwrite

							java.util.List l = source.getAllTooltipDefinitions();
							l.set(origIndex + 1, buf.toString());
							source.setAllTooltipDefinitions(l);
						} else {
							source.addTooltipDefinition(name.getText(), buf.toString());
						}
					}
				}

				this.setVisible(false);
			}
		}

		protected class AddByMaskDialog extends JDialog implements ActionListener {
			protected JButton ok;
			protected JButton cancel;
			protected JTextField title;
			protected JTextField table[][];
			protected JCheckBox excludeOceans;
			protected String name;
			protected String leftPart = null;
			protected String midPart = null;
			protected String rightPart = null;
			protected String tableAttribs = null;
			protected int origIndex = -1;
			protected GridBagConstraints gbc = null;

			/**
			 * Creates a new AddByMaskDialog object.
			 *
			 * @param parent TODO: DOCUMENT ME!
			 * @param title TODO: DOCUMENT ME!
			 */
			public AddByMaskDialog(Dialog parent, String title) {
				super(parent, title, true);
			}

			/**
			 * TODO: DOCUMENT ME!
			 */
			public void show() {
				// clear parts to flag that this is a new mask
				leftPart = null;
				midPart = null;
				rightPart = null;

				// get name and size
				name = JOptionPane.showInputDialog(this, getString("tooltipdialog.addbymask.name"));

				if((name == null) || (name.length() < 1)) {
					return;
				}

				String s = JOptionPane.showInputDialog(this,
													   getString("tooltipdialog.addbymask.rows"));
				int rows = 0;

				try {
					rows = Integer.parseInt(s);
				} catch(Exception exc) {
					return;
				}

				if(rows <= 0) {
					return;
				}

				s = JOptionPane.showInputDialog(this, getString("tooltipdialog.addbymask.columns"));

				int columns = 0;

				try {
					columns = Integer.parseInt(s);
				} catch(Exception exc) {
					return;
				}

				if(columns <= 0) {
					return;
				}

				Container content = this.getContentPane();
				content.removeAll();

				initComponents(content);

				initUI(content, rows, columns, null, null, true); // emtpy table

				this.pack();
				this.setLocationRelativeTo(this.getParent());

				super.show();
			}

			protected String removePSigns(String s) {
				while(s.startsWith("§")) {
					s = s.substring(1);
				}

				while(s.endsWith("§")) {
					try {
						s = s.substring(0, s.length() - 1);
					} catch(Exception exc) {
						break;
					}
				}

				return s;
			}

			/**
			 * TODO: DOCUMENT ME!
			 *
			 * @param tipName TODO: DOCUMENT ME!
			 * @param tipDef TODO: DOCUMENT ME!
			 * @param index TODO: DOCUMENT ME!
			 */
			public void show(String tipName, String tipDef, int index) {
				/* try to find the parts of the mask
				 *
				 * Assume: Start ... "<b><center>"title"</center></b>"..."<table"table data"</table>"...End
				*/
				name = tipName;
				origIndex = index;

				int i = tipDef.indexOf("<b><center>");

				if(i == -1) { // not correct
					i = tipDef.indexOf("<center><b>");

					if(i == -1) {
						return;
					}
				}

				leftPart = tipDef.substring(0, i);

				String rest = tipDef.substring(i + 11);

				i = rest.indexOf("</center></b>");

				if(i == -1) {
					i = tipDef.indexOf("</b></center>");

					if(i == -1) {
						return;
					}
				}

				String titleS = removePSigns(rest.substring(0, i));

				rest = rest.substring(i + 13);

				i = rest.indexOf("<table");

				int j = rest.indexOf("</table>");

				if((i == -1) || (j == -1)) {
					return;
				}

				String tData = rest.substring(i, j);

				rest = rest.substring(j + 8);

				rightPart = rest;

				// now that we have the parts try to parse the table data
				int rows = 0;

				// now that we have the parts try to parse the table data
				int columns = 0;
				rest = tData;

				// first get possible table attribs
				tableAttribs = rest.substring(6, rest.indexOf('>'));

				// now compute table size
				rest = rest.substring(rest.indexOf('>') + 1);

				int ccolumns = 0;

				while(rest.length() > 0) {
					if(rest.indexOf('<') >= 0) {
						rest = rest.substring(rest.indexOf('<') + 1);

						if(rest.startsWith("tr")) { // new row
							ccolumns = 0;
							rows++;
						} else if(rest.startsWith("td")) { // new element
							ccolumns++;

							if(ccolumns > columns) {
								columns = ccolumns;
							}
						}
					} else {
						rest = "";
					}
				}

				if((rows == 0) || (columns == 0)) {
					return;
				}

				// now get the table elements
				String values[][] = new String[rows][columns];
				rest = tData.substring(tData.indexOf('>') + 1);
				i = -1;
				j = 0;

				while(rest.length() > 0) {
					if(rest.indexOf('<') >= 0) {
						rest = rest.substring(rest.indexOf('<') + 1);

						if(rest.startsWith("tr")) { // new row
							j = -1;
							i++;
						} else if(rest.startsWith("td")) { // new element
							j++;

							// extract data
							int k = rest.indexOf("</td>");

							if(k == -1) {
								k = Integer.MAX_VALUE;
							}

							int l = rest.indexOf("<td>");

							if(l == -1) {
								l = Integer.MAX_VALUE;
							}

							int m = rest.indexOf("</tr>");

							if(m == -1) {
								m = Integer.MAX_VALUE;
							}

							int n = rest.indexOf("<tr>");

							if(n == -1) {
								n = Integer.MAX_VALUE;
							}

							int o = rest.indexOf("</table>");

							if(o == -1) {
								o = Integer.MAX_VALUE;
							}

							int p = Math.min(k, l);
							int q = Math.min(m, n);
							p = Math.min(p, o);
							p = Math.min(p, q);

							if(p < 3) {
								values[i][j] = removePSigns(rest.substring(3));
							} else {
								values[i][j] = removePSigns(rest.substring(3, p));
							}

							rest = rest.substring(3 +
												  ((values[i][j] == null) ? 0 : values[i][j].length()));
						}
					} else {
						rest = "";
					}
				}

				Container content = this.getContentPane();
				content.removeAll();

				initComponents(content);

				initUI(content, rows, columns, titleS, values, false);

				this.pack();
				this.setLocationRelativeTo(this.getParent());

				super.show();
			}

			protected void initComponents(Container content) {
				if(gbc == null) {
					content.setLayout(new GridBagLayout());
					gbc = new GridBagConstraints();
					gbc.fill = GridBagConstraints.HORIZONTAL;
					gbc.anchor = GridBagConstraints.CENTER;
					gbc.weightx = 1;
					gbc.insets = new Insets(2, 2, 2, 2);
				}

				if(title == null) {
					title = new JTextField(20);
				} else {
					title.setText(null);
				}

				if(ok == null) {
					ok = new JButton(getString("tooltipdialog.addbymask.ok"));
					ok.addActionListener(this);
				}

				if(cancel == null) {
					cancel = new JButton(getString("tooltipdialog.addbymask.cancel"));
					cancel.addActionListener(this);
				}

				if(excludeOceans == null) {
					excludeOceans = new JCheckBox(getString("tooltipdialog.addbymask.excludeocean"),
												  true);
				}
			}

			protected void initUI(Container content, int rows, int cols, String titleS,
								  String values[][], boolean showExclude) {
				// title
				title.setText(titleS);
				gbc.gridx = 0;
				gbc.gridy = 0;
				gbc.gridwidth = 2;
				gbc.gridheight = 1;
				content.add(title, gbc);

				gbc.gridwidth = 1;

				// table
				table = new JTextField[rows][cols];

				for(int i = 0; i < rows; i++) {
					gbc.gridy++;

					for(int j = 0; j < cols; j++) {
						table[i][j] = new JTextField(20);
						gbc.gridx = j;

						if(values != null) {
							table[i][j].setText(values[i][j]);
						}

						content.add(table[i][j], gbc);
					}
				}

				// buttons
				JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 2));

				if(showExclude) {
					south.add(excludeOceans);
				}

				south.add(ok);
				south.add(cancel);
				gbc.gridy++;
				gbc.gridx = 0;
				gbc.gridwidth = 2;
				content.add(south, gbc);
			}

			/**
			 * TODO: DOCUMENT ME!
			 *
			 * @return TODO: DOCUMENT ME!
			 */
			public Dimension getMinimumSize() {
				return new Dimension(300, 100);
			}

			/**
			 * TODO: DOCUMENT ME!
			 *
			 * @param actionEvent TODO: DOCUMENT ME!
			 */
			public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
				if(actionEvent.getSource() != cancel) {
					createTable();
				}

				this.setVisible(false);
			}

			/**
			 * Creates the table out of the GUI mask. All text elements from the fields are packed
			 * inside paragraph signs. Uses some default things like bold title.
			 */
			protected void createTable() {
				if(leftPart == null) {
					createNewTable();
				} else {
					createEditedTable();
				}
			}

			protected void createEditedTable() {
				String rowStart = "<tr>";
				String columnStart = "<td>§";
				String columnEnd = "§</td>";
				String rowEnd = "</tr>";

				StringBuffer buf = new StringBuffer();

				if(leftPart != null) {
					buf.append(leftPart);
				}

				buf.append("<b><center>§");
				buf.append(title.getText());
				buf.append("§</center></b>");

				if(midPart != null) {
					buf.append(midPart);
				}

				buf.append("<table");

				if(tableAttribs != null) {
					buf.append(tableAttribs);
				}

				buf.append(">");

				//table
				for(int i = 0; i < table.length; i++) {
					buf.append(rowStart);

					for(int j = 0; j < table[i].length; j++) {
						buf.append(columnStart);

						if(table[i][j].getText() != null) {
							buf.append(removePSigns(table[i][j].getText()));
						}

						buf.append(columnEnd);
					}

					buf.append(rowEnd);
				}

				buf.append("</table>");

				if(rightPart != null) {
					buf.append(rightPart);
				}

				java.util.List l = source.getAllTooltipDefinitions();
				l.set(origIndex + 1, buf.toString());
				source.setAllTooltipDefinitions(l);
			}

			protected void createNewTable() {
				String rowStart = "<tr>";
				String columnStart = "<td>§";
				String columnEnd = "§</td>";
				String rowEnd = "</tr>";

				StringBuffer buf = new StringBuffer("<html>");

				// exclude oceans if selected
				if(excludeOceans.isSelected()) {
					buf.append("§if§isOzean§Ozean§else§");
				}

				buf.append("<b><center>§");

				// title
				if(title.getText() != null) {
					buf.append(title.getText());
				}

				buf.append("§</center></b><table cellpadding=\"0\">");

				//table
				for(int i = 0; i < table.length; i++) {
					buf.append(rowStart);

					for(int j = 0; j < table[i].length; j++) {
						buf.append(columnStart);

						if(table[i][j].getText() != null) {
							buf.append(removePSigns(table[i][j].getText()));
						}

						buf.append(columnEnd);
					}

					buf.append(rowEnd);
				}

				buf.append("</table>");

				if(excludeOceans.isSelected()) {
					buf.append("§end§");
				}

				buf.append("</html>");

				source.addTooltipDefinition(name, buf.toString());
			}
		}

		protected JList tooltipList;
		protected java.util.List tooltips;
		protected JButton add;
		protected JButton edit;
		protected JButton info;
		protected JButton importT;
		protected JButton exportT;
		protected JButton delete;
		protected JButton mask;
		protected JButton editmask;
		protected JTextField text;
		protected Component listComp = null;
		protected Container listCont;
		protected ToolTipReplacersInfo infoDialog = null;
		protected AddByMaskDialog maskDialog = null;

		/**
		 * Creates a new ToolTipSwitcherDialog object.
		 *
		 * @param parent TODO: DOCUMENT ME!
		 * @param title TODO: DOCUMENT ME!
		 */
		public ToolTipSwitcherDialog(Frame parent, String title) {
			super(parent, title, true);
			initDialog();
		}

		/**
		 * Creates a new ToolTipSwitcherDialog object.
		 *
		 * @param parent TODO: DOCUMENT ME!
		 * @param title TODO: DOCUMENT ME!
		 */
		public ToolTipSwitcherDialog(Dialog parent, String title) {
			super(parent, title, true);
			initDialog();
		}

		private void initDialog() {
			Container content = getContentPane();
			content.setLayout(new BorderLayout());

			JButton b;

			JPanel main = new JPanel(new BorderLayout());
			javax.swing.border.Border border = new CompoundBorder(BorderFactory.createEtchedBorder(),
																  new EmptyBorder(3, 3, 3, 3));

			main.setBorder(border);

			text = new JTextField(30);
			main.add(text, BorderLayout.NORTH);

			JPanel mInner = new JPanel(new BorderLayout());

			Container iButtons = new JPanel(new GridBagLayout());

			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridwidth = 1;
			gbc.gridheight = 2;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 0.5;
			gbc.insets = new Insets(0, 1, 10, 1);

			JPanel normal = new JPanel(new GridLayout(0, 1, 2, 3));
			normal.setBorder(border);

			b = new JButton(getString("tooltipdialog.add"));
			b.addActionListener(this);
			add = b;
			normal.add(b);
			b = new JButton(getString("tooltipdialog.edit"));
			b.addActionListener(this);
			edit = b;
			edit.setEnabled(false);
			normal.add(b);

			iButtons.add(normal, gbc);

			gbc.gridy = 2;

			normal = new JPanel(new GridLayout(0, 1, 2, 3));
			normal.setBorder(border);

			b = new JButton(getString("tooltipdialog.addbymask"));
			b.addActionListener(this);
			mask = b;
			normal.add(b);

			b = new JButton(getString("tooltipdialog.editbymask"));
			b.addActionListener(this);
			editmask = b;
			editmask.setEnabled(false);
			normal.add(b);

			iButtons.add(normal, gbc);

			gbc.gridheight = 1;
			gbc.gridy = 4;
			gbc.insets.left += 5;
			gbc.insets.right += 5;
			gbc.insets.bottom = 0;

			b = new JButton(getString("tooltipdialog.delete"));
			b.addActionListener(this);
			delete = b;
			delete.setEnabled(false);
			iButtons.add(b, gbc);

			JPanel iDummy = new JPanel();
			iDummy.add(iButtons);

			mInner.add(iDummy, BorderLayout.EAST);

			listCont = mInner;
			recreate();

			main.add(mInner, BorderLayout.CENTER);

			Container east = new JPanel(new GridLayout(0, 1, 2, 3));

			b = new JButton(getString("tooltipdialog.ok"));
			b.addActionListener(this);
			east.add(b);

			b = new JButton(getString("tooltipdialog.info"));
			info = b;
			b.addActionListener(this);
			east.add(b);

			b = new JButton(getString("tooltipdialog.import"));
			b.addActionListener(this);
			importT = b;
			east.add(b);

			b = new JButton(getString("tooltipdialog.export"));
			b.addActionListener(this);
			exportT = b;
			east.add(b);

			JPanel dummy = new JPanel();
			dummy.add(east);
			content.add(dummy, BorderLayout.EAST);
			content.add(main, BorderLayout.CENTER);

			pack();
			setLocationRelativeTo(this.getParent());
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public Dimension getMinimumSize() {
			return new Dimension(100, 100);
		}

		protected void recreate() {
			if(listCont == null) {
				return;
			}

			java.util.List list = source.getAllTooltipDefinitions();
			List v = CollectionFactory.createLinkedList();
			tooltips = CollectionFactory.createLinkedList();

			Iterator it = list.iterator();

			while(it.hasNext()) {
				String name = (String) it.next();
				String def = (String) it.next();
				v.add(name);
				tooltips.add(def);
			}

			if(tooltipList == null) {
				tooltipList = new JList(v.toArray());
				tooltipList.setBorder(BorderFactory.createEtchedBorder());
				tooltipList.addListSelectionListener(this);
				tooltipList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

				JScrollPane jsp = new JScrollPane(tooltipList);
				listComp = jsp;
				listCont.add(jsp, BorderLayout.CENTER);
			} else {
				int oldIndex = tooltipList.getSelectedIndex();
				tooltipList.setListData(v.toArray());

				if(oldIndex >= v.size()) {
					oldIndex = -1;
				}

				tooltipList.setSelectedIndex(oldIndex);

				if(oldIndex != -1) {
					text.setText((String) tooltips.get(oldIndex));
				}
			}

			pack();
			setLocationRelativeTo(this.getParent());
			getContentPane().repaint();
			this.repaint();
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param e TODO: DOCUMENT ME!
		 */
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == importT) {
				new ImExportDialog(this, getString("tooltipdialog.importdialog.title")).showDialog(true);
				recreate();
			} else if(e.getSource() == exportT) {
				new ImExportDialog(this, getString("tooltipdialog.exportdialog.title")).showDialog(false);
			} else if(e.getSource() == add) {
				new AddTooltipDialog(this, getString("tooltipdialog.addtooltipdialog.title"), null,
									 null, -1).show();
				recreate();
			} else if(e.getSource() == mask) {
				if(maskDialog == null) {
					maskDialog = new AddByMaskDialog(this,
													 getString("tooltipdialog.addbymask.title"));
				}

				maskDialog.show();
				recreate();
			} else if(e.getSource() == editmask) {
				if((tooltipList != null) && (tooltipList.getSelectedIndex() > -1)) {
					int index = tooltipList.getSelectedIndex();

					if(maskDialog == null) {
						maskDialog = new AddByMaskDialog(this,
														 getString("tooltipdialog.addbymask.title"));
					}

					log.info("Starting editing dialog...");
					maskDialog.show(tooltipList.getSelectedValue().toString(),
									(String) tooltips.get(index), index * 2);
					recreate();
				}
			} else if(e.getSource() == edit) {
				if((tooltipList != null) && (tooltipList.getSelectedIndex() > -1)) {
					int index = tooltipList.getSelectedIndex();
					new AddTooltipDialog(this, getString("tooltipdialog.addtooltipdialog.title2"),
										 tooltipList.getSelectedValue().toString(),
										 (String) tooltips.get(index), index * 2).show();
					recreate();
				}
			} else if(e.getSource() == delete) {
				try {
					java.util.List src = source.getAllTooltipDefinitions();
					src.remove(tooltipList.getSelectedIndex() * 2);
					src.remove(tooltipList.getSelectedIndex() * 2);
					source.setAllTooltipDefinitions(src);
					recreate();
				} catch(Exception exc) {
				}
			} else if(e.getSource() == info) {
				if(infoDialog == null) {
					infoDialog = new ToolTipReplacersInfo(this,
														  getString("tooltipdialog.tooltipinfo.title"));
				}

				if(!infoDialog.isVisible()) {
					infoDialog.show();
				}
			} else {
				this.setVisible(false);

				if(infoDialog != null) {
					infoDialog.setVisible(false);
				}
			}
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param e TODO: DOCUMENT ME!
		 */
		public void valueChanged(javax.swing.event.ListSelectionEvent e) {
			if(tooltipList.getSelectedIndex() >= 0) {
				text.setText((String) tooltips.get(tooltipList.getSelectedIndex()));
				edit.setEnabled(true);
				editmask.setEnabled(true);
				delete.setEnabled(true);
			} else {
				edit.setEnabled(false);
				editmask.setEnabled(false);
				delete.setEnabled(false);
			}
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public String getSelectedToolTip() {
			if(tooltipList != null) {
				int i = tooltipList.getSelectedIndex();
				i = Math.max(i, 0);

				return (String) tooltips.get(i);
			}

			return null;
		}
	}
}
