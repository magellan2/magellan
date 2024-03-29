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

package com.eressea.swing.tree;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.JTree;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.eressea.swing.InternationalizedPanel;
import com.eressea.swing.preferences.ExtendedPreferencesAdapter;
import com.eressea.swing.preferences.PreferencesAdapter;
import com.eressea.swing.preferences.PreferencesFactory;
import com.eressea.util.CollectionFactory;
import com.eressea.util.logging.Logger;

/**
 * DOCUMENT ME!
 *
 * @author Andreas
 * @version
 */
public class IconAdapter extends InternationalizedPanel implements ExtendedPreferencesAdapter,
																   ItemListener
{
	private static final Logger log = Logger.getInstance(IconAdapter.class);
	protected List nwfactorys;
	protected List nwadapters;
	protected Stylesets styles;
	protected ColorMapping cmapping;
	protected EmphasizeStyle eStyle;
	protected JCheckBox toolTipOn;
	protected JPanel factoryPanel;
	protected CardLayout cards;
	protected JComboBox factoryBox;
	protected List subAdapters;

	/**
	 * Creates new IconAdapter
	 *
	 * @param nw TODO: DOCUMENT ME!
	 */
	public IconAdapter(List nw) {
		subAdapters = CollectionFactory.createArrayList(2);

		nwfactorys = nw;
		nwadapters = CollectionFactory.createArrayList(nw.size());
		setLayout(new GridBagLayout());

		GridBagConstraints con = new GridBagConstraints();
		con.fill = GridBagConstraints.NONE;
		con.anchor = GridBagConstraints.WEST;
		con.gridwidth = 1;
		con.gridheight = 1;
		con.gridx = 0;
		con.gridy = 0;
		toolTipOn = new JCheckBox(getString("tooltips.show.text"), CellRenderer.showTooltips);
		add(toolTipOn, con);
		con.gridx = 1;

		Insets old = con.insets;

		if(con.insets == null) {
			con.insets = new Insets(1, 1, 1, 1);
		} else {
			con.insets = new Insets(con.insets.top, con.insets.left, con.insets.bottom,
									con.insets.right);
		}

		con.insets.left += 20;
		eStyle = new EmphasizeStyle();
		add(eStyle, con);
		con.insets = old;
		con.gridx = 0;

		JPanel p = new JPanel(new GridBagLayout());
		GridBagConstraints con2 = new GridBagConstraints();
		con2.gridwidth = 1;
		con2.gridheight = 1;
		con2.fill = GridBagConstraints.HORIZONTAL;
		con2.anchor = GridBagConstraints.WEST;
		con2.weightx = 1;
		con2.gridx = 0;
		con2.gridy = 0;

		Iterator it = nw.iterator();

		while(it.hasNext()) {
			PreferencesAdapter pref = ((PreferencesFactory) it.next()).createPreferencesAdapter();
			nwadapters.add(pref);

			JComponent comp = null;
			Component c = pref.getComponent();

			if(c instanceof JComponent) {
				comp = (JComponent) c;
			} else {
				comp = new JPanel(new GridLayout(1, 1));
				comp.add(c);
			}

			comp.setBorder(BorderFactory.createTitledBorder(pref.getTitle()));
			p.add(comp, con2);
			con2.gridy++;
		}

		factoryPanel = p;

		con.gridwidth = 2;
		con.gridy = 1;
		con.fill = GridBagConstraints.BOTH;
		con.weightx = 1;
		add(p, con);
		con.gridy = 2;
		add(styles = new Stylesets(), con);
		cmapping = new ColorMapping();
		subAdapters.add(cmapping);
		subAdapters.add(styles);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getTitle() {
		return getString("iconadapter.title");
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Component getComponent() {
		styles.updatePreferences();

		return this;
	}

    public void initPreferences() {
        // TODO: implement it
    }

    /**
	 * TODO: DOCUMENT ME!
	 */
	public void applyPreferences() {
		// apply node changes
		Iterator it = nwadapters.iterator();

		while(it.hasNext()) {
			((PreferencesAdapter) it.next()).applyPreferences();
		}

		CellRenderer.setAdditionalValueProperties(CellRenderer.colorMap, toolTipOn.isSelected());
		eStyle.apply();
	}

	protected class EmphasizeStyle extends JPanel implements ActionListener {
		protected JCheckBox bold;
		protected JCheckBox italic;
		protected JCheckBox actColor;
		protected JButton colButton;

		/**
		 * Creates a new EmphasizeStyle object.
		 */
		public EmphasizeStyle() {
			JLabel eLabel = new JLabel(getString("emphasize.text"));

			try {
				eLabel.setToolTipText(getString("emphasize.tooltip"));
			} catch(MissingResourceException mexc) {
			}

			this.add(eLabel);

			Container style = Box.createVerticalBox();
			style.add(bold = new JCheckBox(getString("emphasize.bold"),
										   (CellRenderer.emphasizeStyleChange & Font.BOLD) != 0));
			style.add(italic = new JCheckBox(getString("emphasize.italic"),
											 (CellRenderer.emphasizeStyleChange & Font.ITALIC) != 0));
			this.add(style);
			this.add(actColor = new JCheckBox(getString("emphasize.color.text"),
											  CellRenderer.emphasizeColor != null));
			colButton = new JButton(" ");
			colButton.addActionListener(this);
			colButton.setFocusPainted(false);

			if(CellRenderer.emphasizeColor != null) {
				colButton.setBackground(CellRenderer.emphasizeColor);
			}

			this.add(colButton);
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param e TODO: DOCUMENT ME!
		 */
		public void actionPerformed(ActionEvent e) {
			JButton source = (JButton) e.getSource();
			Color col = JColorChooser.showDialog(this, getString("colorchooser.title"),
												 source.getBackground());

			if(col != null) {
				source.setBackground(col);
				actColor.setSelected(true);
			}
		}

		/**
		 * TODO: DOCUMENT ME!
		 */
		public void apply() {
			try {
				int sChange = 0;

				if(bold.isSelected()) {
					sChange = Font.BOLD;
				}

				if(italic.isSelected()) {
					sChange |= Font.ITALIC;
				}

				Color sColor = null;

				if(actColor.isSelected()) {
					sColor = colButton.getBackground();
				}

				CellRenderer.setEmphasizeData(sChange, sColor);
			} catch(Exception exc) {
			}
		}
	}

	protected class ColorMapping extends JPanel implements ActionListener, PreferencesAdapter,
														   ListSelectionListener, KeyListener
	{
		protected class MapElement {
			protected String value;
			protected Color color;
		}

		protected class ColorMappingListCellRenderer extends JLabel implements ListCellRenderer {
			protected class RoundRectIcon implements Icon {
				protected Color color;
				protected boolean selected;
				protected int width;
				protected int height;

				/**
				 * Creates a new RoundRectIcon object.
				 *
				 * @param w TODO: DOCUMENT ME!
				 * @param h TODO: DOCUMENT ME!
				 */
				public RoundRectIcon(int w, int h) {
					width = w;
					height = h;
				}

				/**
				 * TODO: DOCUMENT ME!
				 *
				 * @return TODO: DOCUMENT ME!
				 */
				public int getIconHeight() {
					return height;
				}

				/**
				 * TODO: DOCUMENT ME!
				 *
				 * @return TODO: DOCUMENT ME!
				 */
				public int getIconWidth() {
					return width;
				}

				/**
				 * TODO: DOCUMENT ME!
				 *
				 * @param w TODO: DOCUMENT ME!
				 */
				public void setIconWidth(int w) {
					width = w;
				}

				/**
				 * TODO: DOCUMENT ME!
				 *
				 * @param c TODO: DOCUMENT ME!
				 */
				public void setColor(Color c) {
					color = c;
				}

				/**
				 * TODO: DOCUMENT ME!
				 *
				 * @param s TODO: DOCUMENT ME!
				 */
				public void setSelected(boolean s) {
					selected = s;
				}

				/**
				 * TODO: DOCUMENT ME!
				 *
				 * @param c TODO: DOCUMENT ME!
				 * @param g TODO: DOCUMENT ME!
				 * @param x TODO: DOCUMENT ME!
				 * @param y TODO: DOCUMENT ME!
				 */
				public void paintIcon(Component c, Graphics g, int x, int y) {
					g.setColor(color);
					g.fillRoundRect(x, y, width, height, 10, 10);

					if(selected) {
						g.setColor(Color.white);
						g.drawRoundRect(x, y, width, height, 10, 10);
					}
				}
			}

			protected RoundRectIcon icon;
			protected Border border1;
			protected Border border2;

			/**
			 * Creates a new ColorMappingListCellRenderer object.
			 */
			public ColorMappingListCellRenderer() {
				this.setOpaque(false);
				icon = new RoundRectIcon(32, 24);
				this.setIcon(icon);
				this.setHorizontalTextPosition(SwingConstants.CENTER);
				this.setFont(this.getFont().deriveFont(18.0f));
				this.setBorder(border1 = BorderFactory.createLineBorder(Color.white, 3));
				border2 = BorderFactory.createLineBorder(Color.blue, 3);
			}

			/**
			 * TODO: DOCUMENT ME!
			 *
			 * @param l TODO: DOCUMENT ME!
			 * @param value TODO: DOCUMENT ME!
			 * @param index TODO: DOCUMENT ME!
			 * @param sel TODO: DOCUMENT ME!
			 * @param foc TODO: DOCUMENT ME!
			 *
			 * @return TODO: DOCUMENT ME!
			 */
			public Component getListCellRendererComponent(JList l, Object value, int index,
														  boolean sel, boolean foc) {
				if(value instanceof MapElement) {
					setText(((MapElement) value).value);

					Color color = ((MapElement) value).color;
					icon.setColor(color);
					this.setForeground(new Color(255 - color.getRed(), 255 - color.getGreen(),
												 255 - color.getBlue()));
					icon.setSelected(sel);
					icon.setIconWidth(Math.abs(l.getSize().width - 6));

					if(sel) {
						this.setOpaque(true);
						this.setBackground(Color.blue);
						this.setBorder(border2);
					} else {
						this.setOpaque(false);
						this.setBorder(border1);
					}
				} else {
					this.setText("Error!");
					this.setIcon(null);
				}

				return this;
			}
		}

		protected class HelpDialog extends JDialog implements ActionListener {
			/**
			 * Creates a new HelpDialog object.
			 *
			 * @param owner TODO: DOCUMENT ME!
			 * @param text TODO: DOCUMENT ME!
			 * @param buttonText TODO: DOCUMENT ME!
			 */
			public HelpDialog(Frame owner, String text, String buttonText) {
				super(owner, true);

				Container con = this.getContentPane();

				con.setLayout(new BorderLayout());

				JTextArea expl = new JTextArea(text);
				expl.setBackground(con.getBackground());
				expl.setEditable(false);
				expl.setBorder(null);
				expl.setLineWrap(true);
				expl.setWrapStyleWord(true);

				JScrollPane tScroll = new JScrollPane(expl);
				tScroll.setBorder(null);
				con.add(tScroll, BorderLayout.CENTER);

				JButton button = new JButton(buttonText);
				button.addActionListener(this);

				JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
				panel.add(button);
				con.add(panel, BorderLayout.SOUTH);

				Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
				dim.width /= 2;
				dim.height /= 2;
				this.setSize(dim);
				this.setLocationRelativeTo(owner);
			}

			/**
			 * TODO: DOCUMENT ME!
			 *
			 * @param e TODO: DOCUMENT ME!
			 */
			public void actionPerformed(ActionEvent e) {
				this.setVisible(false);
			}
		}

		protected JList list;
		protected DefaultListModel listModel;
		protected JButton buttons[];
		protected HelpDialog dialog;

		/**
		 * Creates a new ColorMapping object.
		 */
		public ColorMapping() {
			this.setLayout(new GridBagLayout());
			this.setBorder(new TitledBorder(getString("colormap.title")));

			GridBagConstraints con = new GridBagConstraints();
			con.weighty = 1;
			con.fill = GridBagConstraints.BOTH;
			con.gridy = 0;
			con.gridx = 0;
			con.weightx = 1;
			con.anchor = GridBagConstraints.CENTER;
			con.gridwidth = 1;
			con.gridheight = 3;
			con.insets = new Insets(1, 3, 1, 3);

			listModel = createListModel();
			list = new JList(listModel);
			list.setCellRenderer(new ColorMappingListCellRenderer());
			list.addListSelectionListener(this);
			list.addKeyListener(this);
			this.add(new JScrollPane(list, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
									 JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), con);

			JPanel bBox = new JPanel(new GridLayout(0, 1));

			buttons = new JButton[5];

			Border border = BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED);

			buttons[0] = new JButton(getString("colormap.button0"));
			buttons[0].addActionListener(this);
			buttons[0].setBorder(border);
			bBox.add(buttons[0]);

			buttons[1] = new JButton(getString("colormap.button1"));
			buttons[1].addActionListener(this);
			buttons[1].setEnabled(false);
			buttons[1].setBorder(border);
			bBox.add(buttons[1]);

			buttons[2] = new JButton(getString("colormap.button2"));
			buttons[2].addActionListener(this);
			buttons[2].setEnabled(false);
			buttons[2].setBorder(border);
			bBox.add(buttons[2]);

			buttons[3] = new JButton(getString("colormap.button3"));
			buttons[3].addActionListener(this);
			buttons[3].setBorder(border);
			bBox.add(Box.createVerticalStrut(20));
			bBox.add(buttons[3]);

			buttons[4] = new JButton(getString("colormap.button4"));
			buttons[4].addActionListener(this);
			buttons[4].setBorder(border);
			bBox.add(Box.createVerticalStrut(20));
			bBox.add(buttons[4]);

			con.gridwidth = 1;
			con.gridx = 1;
			con.gridy = 2;
			con.fill = GridBagConstraints.NONE;
			con.weightx = 0;
			con.weighty = 0;
			con.gridheight = 1;
			this.add(bBox, con);

			dialog = new HelpDialog((Frame) this.getTopLevelAncestor(),
									getString("colormap.help.text"),
									getString("colormap.help.button"));
		}

		protected DefaultListModel createListModel() {
			Map m = CellRenderer.colorMap;
			DefaultListModel dlm = new DefaultListModel();

			if((m == null) || (m.size() == 0)) {
				return dlm;
			}

			Set s = m.keySet();
			List l = CollectionFactory.createArrayList(s.size());
			l.addAll(s);
			Collections.sort(l);

			Iterator it = l.iterator();

			while(it.hasNext()) {
				MapElement mapElem = new MapElement();
				mapElem.value = (String) it.next();
				mapElem.color = (Color) m.get(mapElem.value);
				dlm.addElement(mapElem);
			}

			return dlm;
		}

		// presumes that the value is not used
		protected void addPair(String value, Color col) {
			MapElement mapElem = new MapElement();
			mapElem.value = value;
			mapElem.color = col;
			listModel.addElement(mapElem);

			// list.setSelectedIndex(listModel.size()-1);
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param p1 TODO: DOCUMENT ME!
		 */
		public void actionPerformed(java.awt.event.ActionEvent p1) {
			if(p1.getSource() == buttons[0]) {
				createPair();
			}

			if(p1.getSource() == buttons[1]) {
				changePair();
			}

			if(p1.getSource() == buttons[2]) {
				deletePairs();
			}

			if(p1.getSource() == buttons[3]) {
				createDefaultPairs();
			}

			if(p1.getSource() == buttons[4]) {
				dialog.show();
			}
		}

		protected boolean valueExists(String s) {
			for(int i = 0, max = listModel.size(); i < max; i++) {
				if(((MapElement) listModel.get(i)).value.equals(s)) {
					return true;
				}
			}

			return false;
		}

		protected void createPair() {
			String value = null;
			boolean error = false;

			do {
				value = JOptionPane.showInputDialog(this, getString("colormap.create.value"));

				if(value == null) {
					return;
				}

				error = value.equals("") || valueExists(value);

				if(error) {
					JOptionPane.showMessageDialog(this, getString("colormap.create.valueExisting"));
				}
			} while(error);

			Color col = JColorChooser.showDialog(this, getString("colormap.create.color"),
												 CellRenderer.getTypeset(3).getForeground());

			if(col != null) {
				addPair(value, col);
			}
		}

		protected void changePair() {
			MapElement elem = (MapElement) list.getSelectedValue();
			int index = list.getSelectedIndex();
			String newValue = JOptionPane.showInputDialog(this, getString("colormap.change.value"));

			if(newValue == null) {
				return;
			}

			if(!newValue.equals("") && valueExists(newValue)) {
				JOptionPane.showMessageDialog(this, getString("colormap.change.valueExisting"));

				return;
			}

			if(newValue.equals("")) {
				newValue = elem.value;
			}

			Color col = JColorChooser.showDialog(this, getString("colormap.change.color"),
												 elem.color);

			if(col == null) {
				return;
			}

			elem.color = col;
			elem.value = newValue;
			listModel.set(index, elem);
		}

		protected void deletePairs() {
			int indices[] = list.getSelectedIndices();

			for(int i = 0; i < indices.length; i++) {
				listModel.remove(indices[i] - i);
			}
		}

		protected void createDefaultPairs() {
			listModel.clear();

			for(int i = 1; i < 31; i++) {
				addPair(String.valueOf(i), CellRenderer.getTypeset(3).getForeground());
			}
		}

        public void initPreferences() {
            // TODO: implement it
        }

		/**
		 * TODO: DOCUMENT ME!
		 */
		public void applyPreferences() {
			Map newMap = CollectionFactory.createHashMap();

			if(listModel.size() > 0) {
				for(int i = 0, max = listModel.size(); i < max; i++) {
					MapElement mapElem = (MapElement) listModel.get(i);
					newMap.put(mapElem.value, mapElem.color);
				}
			}

			CellRenderer.setColorMap(newMap);
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
			return getString("colormap.title");
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param e TODO: DOCUMENT ME!
		 */
		public void valueChanged(ListSelectionEvent e) {
			int indices[] = list.getSelectedIndices();

			if((indices == null) || (indices.length == 0)) {
				buttons[1].setEnabled(false);
				buttons[2].setEnabled(false);

				return;
			}

			buttons[1].setEnabled(false);

			if(indices.length == 1) {
				buttons[1].setEnabled(true);
			}

			if(indices.length >= 1) {
				buttons[2].setEnabled(true);
			}
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param e TODO: DOCUMENT ME!
		 */
		public void keyPressed(KeyEvent e) {
			if(e.getModifiers() != 0) {
				return;
			}

			switch(e.getKeyCode()) {
			case KeyEvent.VK_ENTER:

				if(buttons[1].isEnabled()) {
					changePair();
				}

				break;

			case KeyEvent.VK_DELETE:

				if(buttons[2].isEnabled()) {
					deletePairs();
				}

				break;

			default:
				break;
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
	}

	protected class Stylesets extends JPanel implements ActionListener, TreeSelectionListener,
														PreferencesAdapter
	{
		protected JPanel content;
		protected CardLayout contentLayout;
		protected Map subPanels;
		protected Map nodeMap;
		protected JTree stylesets;
		protected AbstractButton removeButton;
		protected DefaultTreeModel treeModel;

		protected class StylesetPanel extends JPanel {
			protected GraphicsStyleset set;
			protected DirectionPanel direction;
			protected FontPanel font;
			protected ColorPanel colors;
			protected JCheckBox fontEnabled;

			class DirectionPanel extends JPanel implements ActionListener {
				protected AbstractButton buttons[];
				protected AbstractButton last;
				protected int horiz;
				protected int vertic;

				/**
				 * Creates a new DirectionPanel object.
				 */
				public DirectionPanel() {
					this.setLayout(new GridLayout(3, 3));
					this.buttons = new AbstractButton[9];

					ButtonGroup gr = new ButtonGroup();

					for(int j = 0; j < 3; j++) {
						int jv = 0;

						switch(j) {
						case 0:
							jv = SwingConstants.TOP;

							break;

						case 1:
							jv = SwingConstants.CENTER;

							break;

						case 2:
							jv = SwingConstants.BOTTOM;

							break;
						}

						for(int i = 0; i < 3; i++) {
							int iv = 0;

							switch(i) {
							case 0:
								iv = SwingConstants.LEFT;

								break;

							case 1:
								iv = SwingConstants.CENTER;

								break;

							case 2:
								iv = SwingConstants.RIGHT;

								break;
							}

							JToggleButton button = new JToggleButton();
							button.addActionListener(this);
							button.setActionCommand(String.valueOf(iv) + ";" + String.valueOf(jv));
							this.add(button);
							buttons[(j * 3) + i] = button;
							gr.add(button);
						}
					}

					buttons[4].setText(getString("icontext.position.icon"));
					last = buttons[5];
					buttons[5].setSelected(true);
					buttons[5].setText(getString("icontext.position.t"));
				}

				/**
				 * TODO: DOCUMENT ME!
				 *
				 * @param set TODO: DOCUMENT ME!
				 */
				public void setStyleset(GraphicsStyleset set) {
					int j = 0;

					if(set.getVerticalPos() == SwingConstants.CENTER) {
						j = 1;
					}

					if(set.getVerticalPos() == SwingConstants.BOTTOM) {
						j = 2;
					}

					int i = 0;

					if(set.getHorizontalPos() == SwingConstants.CENTER) {
						i = 1;
					}

					if(set.getHorizontalPos() == SwingConstants.RIGHT) {
						i = 2;
					}

					buttons[(j * 3) + i].doClick();
					this.horiz = set.getHorizontalPos();
					this.vertic = set.getVerticalPos();
				}

				/**
				 * TODO: DOCUMENT ME!
				 *
				 * @param e TODO: DOCUMENT ME!
				 */
				public void actionPerformed(ActionEvent e) {
					if(last != null) {
						if(last == buttons[4]) {
							last.setText(getString("icontext.position.icon"));
						} else {
							last.setText(null);
						}
					}

					last = (AbstractButton) e.getSource();

					if(last == buttons[4]) {
						last.setText(getString("icontext.position.icon&text"));
					} else {
						last.setText(getString("icontext.position.t"));
					}

					StringTokenizer st = new StringTokenizer(e.getActionCommand(), ";");
					this.horiz = Integer.parseInt(st.nextToken());
					this.vertic = Integer.parseInt(st.nextToken());
				}

				/**
				 * TODO: DOCUMENT ME!
				 *
				 * @param set TODO: DOCUMENT ME!
				 */
				public void apply(GraphicsStyleset set) {
					// work-around for corner-bug
					int i = vertic;

					if((last == buttons[0]) || (last == buttons[2]) || (last == buttons[6]) ||
						   (last == buttons[8])) {
						if(vertic == SwingConstants.TOP) {
							i = SwingConstants.BOTTOM;
						} else {
							i = SwingConstants.TOP;
						}
					}

					set.setHorizontalPos(horiz);
					set.setVerticalPos(i);
				}
			}

			/**
			 * Displays a panel which shows selections for a font.
			 */
			class FontPanel extends JPanel {
				protected JComboBox fonts;
				protected JCheckBox styles[];
				protected JComboBox size;

				/**
				 * Creates a new FontPanel object.
				 */
				public FontPanel() {
					this.setLayout(new GridBagLayout());

					GridBagConstraints con = new GridBagConstraints();
					con.gridwidth = 1;
					con.gridheight = 1;
					con.fill = GridBagConstraints.HORIZONTAL;
					con.anchor = GridBagConstraints.CENTER;
					con.weightx = 1;

					// Font-Family box
					String fontNa[] = null;

					try {
						fontNa = GraphicsEnvironment.getLocalGraphicsEnvironment()
													.getAvailableFontFamilyNames();
					} catch(NullPointerException e) {
						// FIXME(pavkovic) 2003.03.17: This is bad!
						log.error("Probably your are running jdk1.4.1 on Apple. Perhaps we can keep Magellan running. But don't count on it!");
						fontNa = new String[0];
					}

					fonts = new JComboBox(fontNa);
					this.add(fonts, con);
					con.fill = GridBagConstraints.NONE;
					con.gridx = 1;

					// Style checkboxes
					styles = new JCheckBox[2];
					styles[0] = new JCheckBox(getString("icontext.text.font.bold"));
					styles[0].setFont(styles[0].getFont().deriveFont(Font.BOLD));
					styles[1] = new JCheckBox(getString("icontext.text.font.italic"));
					styles[1].setFont(styles[1].getFont().deriveFont(Font.ITALIC));

					JPanel p2 = new JPanel();
					p2.setLayout(new GridLayout(2, 1));
					p2.add(styles[0]);
					p2.add(styles[1]);
					this.add(p2, con);

					// Font-size box
					String sizes[] = new String[11];

					for(int i = 0; i < 11; i++) // values 8-18
					 {
						sizes[i] = String.valueOf(i + 8);
					}

					size = new JComboBox(sizes);
					con.gridx = 2;
					this.add(size, con);
				}

				/**
				 * TODO: DOCUMENT ME!
				 *
				 * @param set TODO: DOCUMENT ME!
				 */
				public void setStyleset(GraphicsStyleset set) {
					if(set.getFont() != null) {
						Font f = set.getFont();
						fonts.setSelectedItem(f.getFamily());
						styles[0].setSelected(f.isBold());
						styles[1].setSelected(f.isItalic());
						size.setSelectedItem(String.valueOf(f.getSize()));
					}
				}

				/**
				 * TODO: DOCUMENT ME!
				 *
				 * @return TODO: DOCUMENT ME!
				 */
				public Font getSelectedFont() {
					int style = 0;

					if(styles[0].isSelected()) {
						style |= Font.BOLD;
					}

					if(styles[1].isSelected()) {
						style |= Font.ITALIC;
					}

					int sizeI = Integer.parseInt((String) size.getSelectedItem());

					return new Font((String) fonts.getSelectedItem(), style, sizeI);
				}
			}

			protected class ColorPanel extends JPanel implements ActionListener {
				protected JCheckBox boxes[];
				protected JButton buttons[];

				/**
				 * Creates a new ColorPanel object.
				 */
				public ColorPanel() {
					this.setLayout(new GridBagLayout());

					GridBagConstraints con = new GridBagConstraints();
					con.fill = GridBagConstraints.NONE;
					con.anchor = GridBagConstraints.NORTHWEST;
					con.gridwidth = 1;
					con.gridheight = 1;
					boxes = new JCheckBox[4];
					buttons = new JButton[4];

					for(int i = 0; i < 4; i++) {
						con.gridx = 0;
						con.gridy = i;
						this.add(boxes[i] = new JCheckBox(getString("styles.color." +
																	String.valueOf(i))), con);
						con.gridx = 1;
						this.add(buttons[i] = new JButton(" "), con);
						buttons[i].addActionListener(this);
						buttons[i].setFocusPainted(false);
					}
				}

				/**
				 * TODO: DOCUMENT ME!
				 *
				 * @param set TODO: DOCUMENT ME!
				 */
				public void setStyleset(GraphicsStyleset set) {
					if(set.getForeground() != null) {
						boxes[0].setSelected(true);
						buttons[0].setBackground(set.getForeground());
					} else {
						boxes[0].setSelected(false);
					}

					if(set.getBackground() != null) {
						boxes[1].setSelected(true);
						buttons[1].setBackground(set.getBackground());
					} else {
						boxes[1].setSelected(false);
					}

					if(set.getSelectedForeground() != null) {
						boxes[2].setSelected(true);
						buttons[2].setBackground(set.getSelectedForeground());
					} else {
						boxes[2].setSelected(false);
					}

					if(set.getSelectedBackground() != null) {
						boxes[2].setSelected(true);
						buttons[2].setBackground(set.getSelectedBackground());
					} else {
						boxes[2].setSelected(false);
					}
				}

				/**
				 * TODO: DOCUMENT ME!
				 *
				 * @param set TODO: DOCUMENT ME!
				 */
				public void apply(GraphicsStyleset set) {
					if(boxes[0].isSelected()) {
						set.setForeground(buttons[0].getBackground());
					} else {
						set.setForeground(null);
					}

					if(boxes[1].isSelected()) {
						set.setBackground(buttons[1].getBackground());
					} else {
						set.setBackground(null);
					}

					if(boxes[2].isSelected()) {
						set.setSelectedForeground(buttons[2].getBackground());
					} else {
						set.setSelectedForeground(null);
					}

					if(boxes[3].isSelected()) {
						set.setSelectedBackground(buttons[3].getBackground());
					} else {
						set.setSelectedBackground(null);
					}
				}

				/**
				 * TODO: DOCUMENT ME!
				 *
				 * @param e TODO: DOCUMENT ME!
				 */
				public void actionPerformed(ActionEvent e) {
					JButton source = (JButton) e.getSource();
					Color col = JColorChooser.showDialog(this, getString("colorchooser.title"),
														 source.getBackground());

					if(col != null) {
						source.setBackground(col);

						int index = 0;

						while(source != buttons[index]) {
							index++;
						}

						boxes[index].setSelected(true);
					}
				}
			}

			/**
			 * Creates a new StylesetPanel object.
			 *
			 * @param set TODO: DOCUMENT ME!
			 */
			public StylesetPanel(GraphicsStyleset set) {
				this.set = set;
				this.setLayout(new GridBagLayout());

				GridBagConstraints con = new GridBagConstraints();
				con.fill = GridBagConstraints.BOTH;
				con.anchor = GridBagConstraints.CENTER;

				Border border = BorderFactory.createEtchedBorder();

				// font
				JPanel p = new JPanel(new GridBagLayout());
				fontEnabled = new JCheckBox(getString("icontext.text.font"), set.getFont() != null);
				con.fill = GridBagConstraints.NONE;
				con.gridx = 0;
				con.gridwidth = 1;
				con.gridheight = 1;
				p.add(fontEnabled, con);
				con.gridy = 1;
				con.gridwidth = 3;
				font = new FontPanel();
				font.setStyleset(set);
				p.add(font, con);
				con.fill = GridBagConstraints.BOTH;
				con.weightx = 1;
				con.gridx = 0;
				con.gridy = 1;
				con.gridheight = 1;

				p.setBorder(border);
				con.gridwidth = 2;
				con.anchor = GridBagConstraints.WEST;
				this.add(p, con);

				// direction
				direction = new DirectionPanel();
				direction.setStyleset(set);
				direction.setBorder(border);
				con.gridx = 0;
				con.gridy = 0;
				con.gridwidth = 1;
				this.add(direction, con);

				// colors
				colors = new ColorPanel();
				colors.setStyleset(set);
				con.gridx = 1;
				colors.setBorder(border);
				this.add(colors, con);
			}

			/**
			 * TODO: DOCUMENT ME!
			 */
			public void apply() {
				if(fontEnabled.isSelected()) {
					this.set.setFont(font.getSelectedFont());
				} else {
					this.set.setFont(null);
				}

				direction.apply(this.set);
				colors.apply(this.set);
			}
		}

		protected class TreeObject {
			protected String name;
			protected String sName;

			/**
			 * Creates a new TreeObject object.
			 *
			 * @param name TODO: DOCUMENT ME!
			 */
			public TreeObject(String name) {
				this.name = name;

				if(name.indexOf('.') > 0) {
					sName = name.substring(name.lastIndexOf('.') + 1);
				} else {
					sName = name;
				}
			}

			/**
			 * Creates a new TreeObject object.
			 *
			 * @param name TODO: DOCUMENT ME!
			 * @param sName TODO: DOCUMENT ME!
			 */
			public TreeObject(String name, String sName) {
				this.name = name;
				this.sName = sName;
			}

			/**
			 * TODO: DOCUMENT ME!
			 *
			 * @return TODO: DOCUMENT ME!
			 */
			public String toString() {
				return sName;
			}
		}

		/**
		 * Creates a new Stylesets object.
		 */
		public Stylesets() {
			this.setLayout(new BorderLayout(1, 5));
			this.setBorder(new TitledBorder(getString("styles.title")));

			// left: "add" & "remove" buttons + styleset combobox
			JPanel p = new JPanel(new FlowLayout(FlowLayout.LEADING));

			treeModel = new DefaultTreeModel(new DefaultMutableTreeNode());
			stylesets = new JTree(treeModel);
			stylesets.setRootVisible(false);
			stylesets.setEditable(false);
			stylesets.addTreeSelectionListener(this);

			this.add(new JScrollPane(stylesets), BorderLayout.WEST);

			JButton button = new JButton(getString("styles.add"));
			button.addActionListener(this);
			p.add(button);
			button = new JButton(getString("styles.remove"));
			button.addActionListener(this);
			p.add(button);
			removeButton = button;
			button.setEnabled(false);

			JPanel p2 = new JPanel();
			p2.setLayout(new BoxLayout(p2, BoxLayout.Y_AXIS));
			p2.add(p);
			p2.add(new JSeparator());
			this.add(p2, BorderLayout.NORTH);

			// center content
			content = new JPanel(contentLayout = new CardLayout());

			subPanels = CollectionFactory.createHashMap();

			for(int i = 0; i < 3; i++) {
				String key = null;

				switch(i) {
				case 0:
					key = "SIMPLE";

					break;

				case 1:
					key = "MAIN";

					break;

				case 2:
					key = "ADDITIONAL";

					break;
				}

				Component c = new StylesetPanel(CellRenderer.getTypeset(i));
				content.add(c, key);
				subPanels.put(key, c);
			}

			this.add(content, BorderLayout.CENTER);

			//stylesets.setSelectionPath(0);
		}

		/**
		 * TODO: DOCUMENT ME!
		 */
		public void updatePreferences() {
			DefaultMutableTreeNode newRoot = new DefaultMutableTreeNode();

			if(nodeMap == null) {
				nodeMap = CollectionFactory.createHashMap();
			} else {
				nodeMap.clear();
			}

			DefaultMutableTreeNode node = new DefaultMutableTreeNode(new TreeObject("SIMPLE",
																					getString("styles.simple")));
			DefaultMutableTreeNode firstNode = node;
			newRoot.add(node);
			nodeMap.put("SIMPLE", node);
			node = new DefaultMutableTreeNode(new TreeObject("MAIN", getString("styles.main")));
			newRoot.add(node);
			nodeMap.put("MAIN", node);
			node = new DefaultMutableTreeNode(new TreeObject("ADDITIONAL",
															 getString("styles.additional")));
			newRoot.add(node);
			nodeMap.put("MAIN", node);

			Map map = CellRenderer.getStylesets();
			List list = null;

			if(map != null) {
				list = CollectionFactory.createLinkedList(map.keySet());
				list.remove("DEFAULT");
				list.remove("MAIN");
				list.remove("SIMPLE");
				list.remove("ADDITIONAL");

				if(list.size() > 0) {
					Collections.sort(list);

					Iterator it = list.iterator();

					while(it.hasNext()) {
						String s = (String) it.next();

						node = new DefaultMutableTreeNode(new TreeObject(s));
						nodeMap.put(s, node);

						if(s.indexOf('.') > 0) {
							String parent = s.substring(0, s.lastIndexOf('.'));

							if(nodeMap.containsKey(parent)) {
								DefaultMutableTreeNode pNode = (DefaultMutableTreeNode) nodeMap.get(parent);
								pNode.add(node);
							} else { // this may happen if the user inserted one directly and it was not needed
								newRoot.add(node);
							}
						} else {
							newRoot.add(node);
						}
					}
				}
			}

			treeModel.setRoot(newRoot);

			content.removeAll();

			Collection old = CollectionFactory.createLinkedList(subPanels.keySet());

			for(int i = 0; i < 3; i++) {
				String key = null;

				switch(i) {
				case 0:
					key = "SIMPLE";

					break;

				case 1:
					key = "MAIN";

					break;

				case 2:
					key = "ADDITIONAL";

					break;
				}

				Component c = (Component) subPanels.get(key);
				content.add(c, key);
				old.remove(key);
			}

			if((list != null) && (list.size() > 0)) {
				Iterator it = list.iterator();

				while(it.hasNext()) {
					Component c = null;
					String name = (String) it.next();

					if(subPanels.containsKey(name)) {
						c = (Component) subPanels.get(name);
						old.remove(name);
					} else {
						c = new StylesetPanel((GraphicsStyleset) CellRenderer.getStylesets().get(name));
						subPanels.put(name, c);
					}

					content.add(c, name);
				}
			}

			if(old.size() > 0) {
				Iterator it = old.iterator();

				while(it.hasNext()) {
					subPanels.remove(it.next());
				}
			}

			openTree(newRoot, new TreePath(treeModel.getPathToRoot(firstNode)));
		}

		protected void openTree(TreeNode root, TreePath first) {
			// expand all nodes
			if(root.getChildCount() > 0) {
				for(int i = 0, max = root.getChildCount(); i < max; i++) {
					openNode(root.getChildAt(i));
				}
			}

			stylesets.setSelectionPath(first);
		}

		protected void openNode(TreeNode node) {
			stylesets.expandPath(new TreePath(treeModel.getPathToRoot(node)));

			if(node.getChildCount() > 0) {
				for(int i = 0, max = node.getChildCount(); i < max; i++) {
					openNode(node.getChildAt(i));
				}
			}
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param e TODO: DOCUMENT ME!
		 */
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() != removeButton) {
				addStyleset();

				return;
			}

			removeStyleset();
		}

		protected void addStyleset() {
			String name = JOptionPane.showInputDialog(this, getString("styles.add.text"));

			if(name == null) {
				return;
			}

			name = name.trim();

			if(name.equals("")) {
				return;
			}

			if(subPanels.containsKey(name)) {
				return;
			}

			if(!stylesets.isSelectionEmpty()) {
				TreeObject obj = (TreeObject) ((DefaultMutableTreeNode) stylesets.getSelectionPath()
																				 .getLastPathComponent()).getUserObject();
				name = obj.name + "." + name;
			}

			GraphicsStyleset set = new GraphicsStyleset(name);
			CellRenderer.addStyleset(set);
			content.add(new StylesetPanel(set), name);

			DefaultMutableTreeNode node = new DefaultMutableTreeNode(new TreeObject(name));
			MutableTreeNode root = (MutableTreeNode) treeModel.getRoot();

			if(name.indexOf('.') > 0) {
				String parent = name.substring(0, name.lastIndexOf('.'));

				if(nodeMap.containsKey(parent)) {
					root = (MutableTreeNode) nodeMap.get(parent);
					treeModel.insertNodeInto(node, root, root.getChildCount());
				} else {
					treeModel.insertNodeInto(node, root, root.getChildCount());
				}
			} else {
				treeModel.insertNodeInto(node, root, root.getChildCount());
			}

			stylesets.setSelectionPath(new TreePath(treeModel.getPathToRoot(node)));

			return;
		}

		protected void removeStyleset() {
			if(!stylesets.isSelectionEmpty()) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) stylesets.getSelectionPath()
																				.getLastPathComponent();
				TreeObject obj = (TreeObject) node.getUserObject();
				Component c = (Component) subPanels.get(obj.name);

				if(c != null) {
					content.remove(c);
				}

				CellRenderer.removeStyleset(obj.name);
				treeModel.removeNodeFromParent(node);
			}
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param e TODO: DOCUMENT ME!
		 */
		public void valueChanged(TreeSelectionEvent e) {
			if(stylesets.isSelectionEmpty()) {
				removeButton.setEnabled(false);
			} else {
				TreeObject obj = (TreeObject) ((DefaultMutableTreeNode) e.getPath()
																		 .getLastPathComponent()).getUserObject();

				if(obj != null) {
					contentLayout.show(content, obj.name);

					boolean removeEnabled = true;

					if(obj.name.equals("SIMPLE") || obj.name.equals("MAIN") ||
						   obj.name.equals("ADDITIONAL")) {
						removeEnabled = false;
					}

					removeButton.setEnabled(removeEnabled);
				}
			}
		}

        public void initPreferences() {
            // TODO: implement it
        }

		/**
		 * TODO: DOCUMENT ME!
		 */
		public void applyPreferences() {
			Component comp[] = content.getComponents();

			if(comp != null) {
				for(int i = 0; i < comp.length; i++) {
					if(comp[i] instanceof StylesetPanel) {
						((StylesetPanel) comp[i]).apply();
					}
				}
			}

			CellRenderer.saveStylesets();
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public String getTitle() {
			return getString("styles.title");
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public Component getComponent() {
			return this;
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param p1 TODO: DOCUMENT ME!
	 */
	public void itemStateChanged(java.awt.event.ItemEvent p1) {
		int i = Math.max(0, factoryBox.getSelectedIndex());
		cards.show(factoryPanel, String.valueOf(i));
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param p1 TODO: DOCUMENT ME!
	 */
	public void actionPerformed(java.awt.event.ActionEvent p1) {
	}

	/**
	 * Returns a list of preferences adapters that should be displayed in the given order.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public List getChildren() {
		return subAdapters;
	}

	// pavkovic 2003.01.28: this is a Map of the default Translations mapped to this class
	// it is called by reflection (we could force the implementation of an interface,
	// this way it is more flexible.)
	// Pls use this mechanism, so the translation files can be created automagically
	// by inspecting all classes.
	private static final Map defaultTranslations = CollectionFactory.createHashtable();

	static {
		defaultTranslations.put("icontext.position.text",
								"Choose the position of the text in the left field. The middle place marks the place of the icon.");
		defaultTranslations.put("icontext.text.font", "Font");
		defaultTranslations.put("icontext.text.font.bold", "Bold");
		defaultTranslations.put("icontext.text.font.italic", "Italic");
		defaultTranslations.put("icontext.text.font.color", "Font Color");
		defaultTranslations.put("tabs.icons", "Icon Configuration");
		defaultTranslations.put("iconadapter.title", "Icons");
		defaultTranslations.put("colorchooser.title", "Choose Color");
		defaultTranslations.put("colormap.addMapping", "Add");
		defaultTranslations.put("colormap.removeMapping", "Remove");
		defaultTranslations.put("colormap.adjustColor", "Adjust Color");
		defaultTranslations.put("colormap.title", "Value->Color Mapping");
		defaultTranslations.put("tooltips.show.text", "Show tooltips");
		defaultTranslations.put("colormap.button0", "Add...");
		defaultTranslations.put("colormap.button1", "Change...");
		defaultTranslations.put("colormap.button2", "Delete");
		defaultTranslations.put("colormap.button3", "Default");
		defaultTranslations.put("colormap.button4", "Help...");
		defaultTranslations.put("colormap.help.text",
								"Special values can be associated with certain colors with the help of this funtion.\n\nWith Add a new Value-Color-Pair is created. First input the value(this must not exist) and than choose the associated color.\n\nWith Change you can change an existing pair. Just select the pair you want(only one at a time) and press Change. You may change the value first or leave the field blank to indicate that you want to use your old value. Than choose the new color.\n\nWith Delete all selected pairs are deleted. Warning: There's no warning message!\n\nWith Default all existing pairs are deleted and values from 1 till 30 are inserted with default color.\n\nThere are also keyboard shortcuts for Change and Delete: \"Enter\" and \"Delete\".");
		defaultTranslations.put("colormap.help.button", "OK");
		defaultTranslations.put("colormap.create.value", "New Value:");
		defaultTranslations.put("colormap.create.valueExisting", "Value empty/existing!");
		defaultTranslations.put("colormap.create.color", "Choose Color");
		defaultTranslations.put("emphasize.tooltip",
								"Visual style of strong information, e.g. units with non-confirmed orders.");
		defaultTranslations.put("emphasize.color.text", "Color");
		defaultTranslations.put("emphasize.italic", "Italic");
		defaultTranslations.put("emphasize.bold", "Bold");
		defaultTranslations.put("emphasize.text", "Emphasize:");
		defaultTranslations.put("colormap.change.value",
								"Enter new value. Empty field indicates that you want to use your old value.");
		defaultTranslations.put("colormap.change.valueExisting", "Value existing!");
		defaultTranslations.put("colormap.change.color", "Choose new Color");
		defaultTranslations.put("styles.color.0", "Foreground");
		defaultTranslations.put("styles.color.1", "Background");
		defaultTranslations.put("styles.color.2", "Foreground(selected)");
		defaultTranslations.put("styles.color.3", "Background(selected)");
		defaultTranslations.put("styles.simple", "Simple");
		defaultTranslations.put("styles.main", "Main part");
		defaultTranslations.put("styles.additional", "Additional part");
		defaultTranslations.put("styles.add", "Add...");
		defaultTranslations.put("styles.remove", "Remove");
		defaultTranslations.put("styles.title", "Icon styles");
		defaultTranslations.put("styles.add.text", "New style-set");
		defaultTranslations.put("icontext.position.icon", "Icon");
		defaultTranslations.put("icontext.position.t", "Text");
		defaultTranslations.put("icontext.position.icon&text", "I+T");
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static Map getDefaultTranslations() {
		return defaultTranslations;
	}
}
