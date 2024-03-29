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

package com.eressea.swing;

import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;

import com.eressea.util.CollectionFactory;
import com.eressea.util.logging.Logger;

/**
 * A GUI for Vorlage by Steffen Schuemann (Gulrak). See <A HREF="http://www.gulrak.de/">Gulrak's
 * Homepage</A> for details on Vorlage. This class can be used as a stand-alone application or can
 * be integrated as dialog into a different application.
 */
public class JVorlage extends InternationalizedDialog {
	private static final Logger log = Logger.getInstance(JVorlage.class);
	private Properties settings = null;
	private boolean standAlone = false;
	private JComboBox comboInputFile = null;
	private JComboBox comboOutputFile = null;
	private JComboBox comboScriptFile = null;
	private JTextField txtVorlageFile = null;
	private JCheckBox chkOptionCR = null;
	private JTextField txtOptions = null;
	private JTextArea txtOutput = null;

	/**
	 * Create a stand-alone instance of JVorlage.
	 */
	public JVorlage() {
		super((Frame) null, false);
		standAlone = true;

		settings = new Properties();

		try {
			settings.load(new FileInputStream(new File(System.getProperty("user.home"),
													   "JVorlage.ini")));
		} catch(IOException e) {
			log.error("JVorlage.JVorlage()", e);
		}

		init();
	}

	/**
	 * Create a new JVorlage object as a dialog with a parent window.
	 *
	 * @param owner TODO: DOCUMENT ME!
	 * @param modal TODO: DOCUMENT ME!
	 * @param p TODO: DOCUMENT ME!
	 */
	public JVorlage(Frame owner, boolean modal, Properties p) {
		super(owner, modal);
		settings = p;
		init();
	}

	private void init() {
		setContentPane(getMainPane());
		setTitle(getString("window.title"));

		int width = Math.max(Integer.parseInt(settings.getProperty("JVorlage.width", "450")), 450);
		int height = Math.max(Integer.parseInt(settings.getProperty("JVorlage.height", "310")), 310);
		setSize(width, height);

		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int x = Integer.parseInt(settings.getProperty("JVorlage.x",
													  ((screen.width - width) / 2) + ""));
		int y = Integer.parseInt(settings.getProperty("JVorlage.y",
													  ((screen.height - height) / 2) + ""));
		setLocation(x, y);
	}

	private Container getMainPane() {
		JPanel mainPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		mainPanel.setBorder(new EmptyBorder(4, 4, 4, 4));

		c.anchor = GridBagConstraints.NORTH;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.insets = new Insets(5, 5, 5, 5);
		mainPanel.add(getInfoPanel(), c);

		c.anchor = GridBagConstraints.NORTH;
		c.gridx = 1;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 2;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.insets = new Insets(0, 0, 0, 0);
		mainPanel.add(getButtonPanel(), c);

		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.1;
		c.weighty = 0.0;
		c.insets = new Insets(0, 0, 0, 5);
		mainPanel.add(getFilePanel(), c);

		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 2;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.1;
		c.weighty = 0.0;
		c.insets = new Insets(0, 0, 0, 0);
		mainPanel.add(getOptionPanel(), c);

		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 2;
		c.gridheight = 1;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0.2;
		c.weighty = 0.2;
		c.insets = new Insets(0, 0, 0, 0);
		mainPanel.add(getOutputPanel(), c);

		return mainPanel;
	}

	private Container getInfoPanel() {
		JTextArea txtInfo = new JTextArea(getString("txt.info.text"));
		txtInfo.setOpaque(false);
		txtInfo.setEditable(false);
		txtInfo.setLineWrap(true);
		txtInfo.setWrapStyleWord(true);

		return txtInfo;
	}

	private Container getButtonPanel() {
		JButton okButton = new JButton(getString("btn.ok.caption"));
		okButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					execUsersVorlage();
				}
			});

		JButton cancelButton = new JButton(getString("btn.close.caption"));
		cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					quit();
				}
			});

		JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 5, 5));
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);

		return buttonPanel;
	}

	private Container getFilePanel() {
		comboInputFile = new JComboBox(getList(settings.getProperty("JVorlage.inputFile", ""))
										   .toArray());
		comboInputFile.setEditable(true);
		comboInputFile.setToolTipText(getString("combo.inputfile.tooltip"));

		JLabel lblInputFile = new JLabel(getString("lbl.inputfile.caption"));
		lblInputFile.setLabelFor(comboInputFile);
		lblInputFile.setToolTipText(comboInputFile.getToolTipText());

		JButton btnInputFile = new JButton("...");
		btnInputFile.setToolTipText(getString("btn.inputfile.tooltip"));
		btnInputFile.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String inputFile = getFileName((String) comboInputFile.getSelectedItem(), true,
												   new EresseaFileFilter(EresseaFileFilter.CR_FILTER));

					if(inputFile != null) {
						comboInputFile.addItem(inputFile);
						comboInputFile.setSelectedItem(inputFile);
					}
				}
			});

		comboOutputFile = new JComboBox(getList(settings.getProperty("JVorlage.outputFile", ""))
											.toArray());
		comboOutputFile.setEditable(true);
		comboOutputFile.setToolTipText(getString("combo.outputfile.tooltip"));

		JLabel lblOutputFile = new JLabel(getString("lbl.outputfile.caption"));
		lblOutputFile.setLabelFor(comboOutputFile);
		lblOutputFile.setToolTipText(comboOutputFile.getToolTipText());

		JButton btnOutputFile = new JButton("...");
		btnOutputFile.setToolTipText(getString("btn.outputfile.tooltip"));
		btnOutputFile.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String outputFile = getFileName((String) comboOutputFile.getSelectedItem(),
													false,
													new EresseaFileFilter(EresseaFileFilter.CR_FILTER));

					if(outputFile != null) {
						comboOutputFile.addItem(outputFile);
						comboOutputFile.setSelectedItem(outputFile);
					}
				}
			});

		comboScriptFile = new JComboBox(getList(settings.getProperty("JVorlage.scriptFile", ""))
											.toArray());
		comboScriptFile.setEditable(true);
		comboScriptFile.setToolTipText(getString("combo.scriptfile.tooltip"));

		JLabel lblScriptFile = new JLabel(getString("lbl.scriptfile.caption"));
		lblScriptFile.setLabelFor(comboScriptFile);
		lblScriptFile.setToolTipText(comboScriptFile.getToolTipText());

		JButton btnScriptFile = new JButton("...");
		btnScriptFile.setToolTipText(getString("btn.scriptfile.tooltip"));
		btnScriptFile.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String scriptFile = getFileName((String) comboScriptFile.getSelectedItem(),
													false,
													new EresseaFileFilter(EresseaFileFilter.TXT_FILTER));

					if(scriptFile != null) {
						comboScriptFile.addItem(scriptFile);
						comboScriptFile.setSelectedItem(scriptFile);
					}
				}
			});

		txtVorlageFile = new JTextField(settings.getProperty("JVorlage.vorlageFile", ""));
		txtVorlageFile.setEditable(true);
		txtVorlageFile.setToolTipText(getString("txt.vorlagefile.tooltip"));

		JLabel lblVorlageFile = new JLabel(getString("lbl.vorlagefile.caption"));
		lblVorlageFile.setLabelFor(txtVorlageFile);
		lblVorlageFile.setToolTipText(txtVorlageFile.getToolTipText());

		JButton btnVorlageFile = new JButton("...");
		btnVorlageFile.setToolTipText(getString("btn.vorlagefile.tooltip"));
		btnVorlageFile.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String vorlageFile = getFileName(txtVorlageFile.getText(), false,
													 new EresseaFileFilter("exe",
																		   getString("filter.executable.description")));

					if(vorlageFile != null) {
						txtVorlageFile.setText(vorlageFile);
					}
				}
			});

		JPanel pnlFiles = new JPanel(new GridBagLayout());
		pnlFiles.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(),
											getString("border.files")));

		GridBagConstraints c = new GridBagConstraints();

		// inputFile
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.0;
		c.weighty = 0.0;
		pnlFiles.add(lblInputFile, c);
		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 1;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.1;
		c.weighty = 0.0;
		pnlFiles.add(comboInputFile, c);
		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 2;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.0;
		c.weighty = 0.0;
		pnlFiles.add(btnInputFile, c);

		// outputFile
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.0;
		c.weighty = 0.0;
		pnlFiles.add(lblOutputFile, c);
		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 1;
		c.gridy = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.1;
		c.weighty = 0.0;
		pnlFiles.add(comboOutputFile, c);
		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 2;
		c.gridy = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.0;
		c.weighty = 0.0;
		pnlFiles.add(btnOutputFile, c);

		// scriptFile
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.0;
		c.weighty = 0.0;
		pnlFiles.add(lblScriptFile, c);
		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 1;
		c.gridy = 2;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.1;
		c.weighty = 0.0;
		pnlFiles.add(comboScriptFile, c);
		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 2;
		c.gridy = 2;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.0;
		c.weighty = 0.0;
		pnlFiles.add(btnScriptFile, c);

		// vorlageFile
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.0;
		c.weighty = 0.0;
		pnlFiles.add(lblVorlageFile, c);
		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 1;
		c.gridy = 3;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.1;
		c.weighty = 0.0;
		pnlFiles.add(txtVorlageFile, c);
		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 2;
		c.gridy = 3;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.0;
		c.weighty = 0.0;
		pnlFiles.add(btnVorlageFile, c);

		return pnlFiles;
	}

	private Container getOptionPanel() {
		chkOptionCR = new JCheckBox(getString("chk.outputcr.caption"),
									(Boolean.valueOf(settings.getProperty("JVorlage.optionCR", "false"))).booleanValue());
		chkOptionCR.setToolTipText(getString("chk.outputcr.tooltip"));

		txtOptions = new JTextField(settings.getProperty("JVorlage.options", ""));
		txtOptions.setToolTipText(getString("txt.options.tooltip"));

		JLabel lblOptions = new JLabel(getString("lbl.options.caption"));
		lblOptions.setToolTipText(txtOptions.getToolTipText());
		lblOptions.setLabelFor(txtOptions);

		JPanel pnlOptions = new JPanel(new GridBagLayout());
		pnlOptions.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(),
											  getString("border.options")));

		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.0;
		c.weighty = 0.0;
		pnlOptions.add(chkOptionCR, c);
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.0;
		c.weighty = 0.0;
		pnlOptions.add(lblOptions, c);
		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 1;
		c.gridy = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.1;
		c.weighty = 0.0;
		pnlOptions.add(txtOptions, c);

		return pnlOptions;
	}

	private Container getOutputPanel() {
		/*JLabel lblVersion = new JLabel("Version: unbekannt");

		JButton btnShowArgs = new JButton("Hilfe anzeigen");
		btnShowArgs.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        StringBuffer sb = execVorlage("-?");
		        if (sb != null) {
		            txtOutput.setText(sb.toString());
		        }
		    }
		});*/
		txtOutput = new JTextArea();
		txtOutput.setBorder(BorderFactory.createEtchedBorder());

		JPanel pnlOutput = new JPanel(new GridBagLayout());
		pnlOutput.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(),
											 getString("border.output")));

		GridBagConstraints c = new GridBagConstraints();

		/*c.anchor = c.CENTER;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = c.NONE;
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.insets = new Insets(1, 1, 1, 1);
		pnlOutput.add(lblVersion, c);

		c.anchor = c.EAST;
		c.gridx = 1;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = c.NONE;
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.insets = new Insets(1, 1, 5, 1);
		pnlOutput.add(btnShowArgs, c);*/
		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		c.gridheight = 1;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0.1;
		c.weighty = 0.1;
		c.insets = new Insets(1, 1, 1, 1);
		pnlOutput.add(new JScrollPane(txtOutput), c);

		return pnlOutput;
	}

	/**
	 * Stores all properties of JVorlage that should be preserved to the global Properties object.
	 * In standalone mode this also saves this object to the file 'JVorlage.ini' in the user's
	 * home directory.
	 */
	private void storeSettings() {
		settings.setProperty("JVorlage.width", getWidth() + "");
		settings.setProperty("JVorlage.height", getHeight() + "");
		settings.setProperty("JVorlage.x", getX() + "");
		settings.setProperty("JVorlage.y", getY() + "");

		settings.setProperty("JVorlage.inputFile", getString(comboInputFile));
		settings.setProperty("JVorlage.outputFile", getString(comboOutputFile));
		settings.setProperty("JVorlage.scriptFile", getString(comboScriptFile));
		settings.setProperty("JVorlage.vorlageFile", txtVorlageFile.getText());

		settings.setProperty("JVorlage.optionCR", String.valueOf(chkOptionCR.isSelected()));
		settings.setProperty("JVorlage.options", txtOptions.getText());

		if(standAlone == true) {
			try {
				settings.store(new FileOutputStream(new File(System.getProperty("user.home"),
															 "JVorlage.ini")), "");
			} catch(IOException e) {
				log.error("JVorlage.storeSettings()", e);
			}
		}
	}

	/**
	 * Executes Vorlage with the specified options and returns the output Vorlage produced.
	 *
	 * @param options TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	private StringBuffer execVorlage(String options) {
		String commandLine = null;
		StringBuffer sb = null;
		FileReader reader = null;
		File tempFile = null;
		File vorlage = new File(txtVorlageFile.getText());

		if((vorlage.exists() == false) || (vorlage.canRead() == false)) {
			JOptionPane.showMessageDialog(this, getString("msg.invalidvorlage.text"),
										  getString("msg.invalidvorlage.title"),
										  JOptionPane.ERROR_MESSAGE);

			return sb;
		}

		commandLine = vorlage.getAbsolutePath();

		try {
			tempFile = File.createTempFile("JVorlage", null);
		} catch(Exception e) {
			log.error("JVorlage.execVorlage(): unable to create temporary file for Vorlage output",
					  e);
			JOptionPane.showMessageDialog(this, getString("msg.tempfileerror.text"),
										  getString("msg.tempfileerror.title"),
										  JOptionPane.ERROR_MESSAGE);
		}

		if(tempFile != null) {
			commandLine += (" -e " + tempFile.getAbsolutePath() + " -do " +
			tempFile.getAbsolutePath() + " -to " + tempFile.getAbsolutePath());
		}

		commandLine += options;

		Process p = null;
		long start = System.currentTimeMillis();

		try {
			setCursor(new Cursor(Cursor.WAIT_CURSOR));
			p = Runtime.getRuntime().exec(commandLine);
		} catch(Exception e) {
			log.error("JVorlage.execVorlage()", e);
		}

		while(true) {
			try {
				Thread.sleep(300);
			} catch(InterruptedException e) {
			}

			if((System.currentTimeMillis() - start) > 5000) {
				if(JOptionPane.showConfirmDialog(this, getString("msg.stopvorlage.text"),
													 getString("msg.stopvorlage.title"),
													 JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					p.destroy();

					break;
				} else {
					start = System.currentTimeMillis();
				}
			}

			try {
				if(p.exitValue() != 0) {
					Object msgArgs[] = { new Integer(p.exitValue()) };
					JOptionPane.showMessageDialog(this,
												  (new java.text.MessageFormat(getString("msg.execerror.text"))).format(msgArgs),
												  getString("msg.execerror.title"),
												  JOptionPane.WARNING_MESSAGE);
				}

				break;
			} catch(IllegalThreadStateException e) {
			}
		}

		setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

		if(tempFile != null) {
			try {
				reader = new FileReader(tempFile);
			} catch(FileNotFoundException e) {
				log.error("JVorlage.execVorlage(): cannot create a file reader on the temp output file",
						  e);
			}
		}

		if(reader != null) {
			try {
				sb = new StringBuffer();

				String line = null;
				LineNumberReader lnr = new LineNumberReader(reader);

				while((line = lnr.readLine()) != null) {
					sb.append(line).append("\n");
				}

				lnr.close();
			} catch(IOException e) {
				log.error("JVorlage.execVorlage(): unable to read the temporary output file: ", e);
				sb = null;
			} 

			try {
				reader.close();
			} catch(IOException e) {
			}
		}

		if(tempFile != null) {
			tempFile.delete();
		}

		return sb;
	}

	/**
	 * Assembles a set of options for Vorlage from the user's input in the GUI elements and
	 * executes Vorlage, writing its output to the output text area.
	 */
	private void execUsersVorlage() {
		String commandLine = "";

		if(!"".equals(txtOptions.getText())) {
			commandLine += (" " + txtOptions.getText());
		}

		if(chkOptionCR.isSelected() == true) {
			commandLine += " -cr";
		}

		if((comboScriptFile.getSelectedItem() != null) &&
			   (comboScriptFile.getSelectedItem().equals("") == false)) {
			commandLine += (" -i " + (String) comboScriptFile.getSelectedItem());
		}

		commandLine += (" -f -o " + (String) comboOutputFile.getSelectedItem());
		commandLine += (" " + (String) comboInputFile.getSelectedItem());

		StringBuffer sb = execVorlage(commandLine);

		if(sb != null) {
			txtOutput.setText(sb.toString());
		}
	}

	/**
	 * Converts the elements in the specified combo box to one String seperating the string
	 * representation of the items by '|' characters.
	 *
	 * @param combo TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	private String getString(JComboBox combo) {
		StringBuffer sb = new StringBuffer();

		if(combo.getSelectedIndex() == -1) {
			sb.append((String) combo.getEditor().getItem()).append("|");
		}

		for(int i = 0; i < Math.min(combo.getItemCount(), 6); i++) {
			if(i > 0) {
				sb.append("|");
			}

			sb.append((String) combo.getItemAt(i));
		}

		return sb.toString();
	}

	/**
	 * Takes a String containing substrings separated by '|' characters and assembles a list from
	 * that.
	 *
	 * @param str TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	private List getList(String str) {
		List retVal = CollectionFactory.createLinkedList();
		StringTokenizer t = new StringTokenizer(str, "|");

		while(t.hasMoreElements() == true) {
			retVal.add(t.nextElement());
		}

		return retVal;
	}

	protected void quit() {
		storeSettings();

		if(standAlone == true) {
			System.exit(0);
		} else {
			dispose();
		}
	}

	/**
	 * Returns a a file name by showing the user a file selection dialog. If the user's selection
	 * is empty, null is returned.
	 *
	 * @param defaultFile TODO: DOCUMENT ME!
	 * @param multSel TODO: DOCUMENT ME!
	 * @param ff TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	private String getFileName(String defaultFile, boolean multSel, FileFilter ff) {
		StringBuffer sb = new StringBuffer();
		File files[] = null;

		JFileChooser fc = new JFileChooser();
		fc.addChoosableFileFilter(ff);
		fc.setFileFilter(ff);
		fc.setMultiSelectionEnabled(multSel);

		if(defaultFile != null) {
			fc.setSelectedFile(new File(defaultFile));
		}

		if(fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			if(multSel) {
				files = fc.getSelectedFiles();
			} else {
				files = new File[1];
				files[0] = fc.getSelectedFile();
			}
		}

		if(files != null) {
			for(int i = 0; i < files.length; i++) {
				if(files[i].getAbsolutePath().indexOf(' ') > -1) {
					sb.append("\"").append(files[i].getAbsolutePath()).append("\"");
				} else {
					sb.append(files[i].getAbsolutePath());
				}

				if(i < (files.length - 1)) {
					sb.append(" ");
				}
			}
		}

		return sb.toString();
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param args TODO: DOCUMENT ME!
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public static void main(String args[]) throws IOException {
		Properties settings = new Properties();
		settings.load(new FileInputStream("magellan.ini"));
		com.eressea.util.Translations.setClassLoader(new com.eressea.resource.ResourcePathClassLoader(settings));

		JVorlage v = new JVorlage();
		v.setVisible(true);
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
			defaultTranslations.put("window.title", "Vorlage front-end");

			defaultTranslations.put("txt.info.text",
									"This is a graphical front-end to Vorlage similar to the WinVorlage program. Tool-tips will give you hints about the meaning of the controls how to use them.");
			defaultTranslations.put("btn.ok.caption", "OK");
			defaultTranslations.put("btn.close.caption", "Close");
			defaultTranslations.put("combo.inputfile.tooltip",
									"Vorlage generates the orders or a new CR from one or more source CRs.");
			defaultTranslations.put("lbl.inputfile.caption", "Source CR(s): ");
			defaultTranslations.put("btn.inputfile.tooltip", "Opens a file chooser dialog.");
			defaultTranslations.put("combo.outputfile.tooltip",
									"Vorlage will write the generated orders or the new cr to this file.");
			defaultTranslations.put("lbl.outputfile.caption", "Target file: ");
			defaultTranslations.put("btn.outputfile.tooltip", "Opens a file chooser dialog.");
			defaultTranslations.put("combo.scriptfile.tooltip",
									"Script files can be used to supply Vorlage with user defined functions.");
			defaultTranslations.put("lbl.scriptfile.caption", "Script file: ");
			defaultTranslations.put("btn.scriptfile.tooltip", "Opens a file chooser dialog.");
			defaultTranslations.put("txt.vorlagefile.tooltip",
									"The fully qualified path to the Vorlage executable.");
			defaultTranslations.put("lbl.vorlagefile.caption", "Vorlage: ");
			defaultTranslations.put("btn.vorlagefile.tooltip", "Opens a file chooser dialog.");
			defaultTranslations.put("filter.executable.description", "Executable");
			defaultTranslations.put("border.files", "Files");
			defaultTranslations.put("chk.outputcr.caption", "Output as computer report");
			defaultTranslations.put("chk.outputcr.tooltip",
									"Forces Vorlage to generate the orders into a complete cr instead of a text file.");
			defaultTranslations.put("txt.options.tooltip",
									"Additional command line parameters for Vorlage");
			defaultTranslations.put("lbl.options.caption", "Additional parameters: ");
			defaultTranslations.put("border.options", "Options");
			defaultTranslations.put("border.output", "Output");

			defaultTranslations.put("msg.invalidvorlage.text",
									"The specified Vorlage executable does not exist.");
			defaultTranslations.put("msg.invalidvorlage.title", "File does not exist");
			defaultTranslations.put("msg.tempfileerror.text",
									"Unable to create a temporary file for catching Vorlage's output.");
			defaultTranslations.put("msg.tempfileerror.title", "Error");
			defaultTranslations.put("msg.stopvorlage.text",
									"Do you want to stop Vorlage?\n\nVorlage is running for quite some time now, maybe an error occurred.");
			defaultTranslations.put("msg.stopvorlage.title", "Stop Vorlage?");
			defaultTranslations.put("msg.execerror.text",
									"An error occurred while executing Vorlage ({0}).");
			defaultTranslations.put("msg.execerror.title", "Error");
		}

		return defaultTranslations;
	}
}
