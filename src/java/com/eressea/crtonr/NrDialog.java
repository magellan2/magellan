// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Martin Hershoff, Sebastian Pappert,
//							Klaas Prause,  Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.crtonr;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class NrDialog extends JDialog implements ActionListener
{	
	JButton inButton;
	JButton outButton;
	JButton okButton;
	JButton cancelButton;
	JTextField inFile;
	JTextField outFile;
	
	public NrDialog()
	{
		// -i "E:\Eressea\Ritter von Go\rvgo.cr" -o "E:\Eressea\Neue Partei\k7nw-test.nr"
		super(new JFrame());
		setModal(true);
		setSize(590,120);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setResizable(false);
		JLabel inLabel  = new JLabel("Input File");
		JLabel outLabel = new JLabel("Output File");
		java.awt.Container comp = getContentPane();
		comp.setLayout(null);
		inLabel.setBounds(5, 15, 80, 20);
		inLabel.setVisible(true);
		comp.add(inLabel);
		outLabel.setBounds(5, 55, 80, 20);
		comp.add(outLabel);
		outLabel.setVisible(true);
		inFile  = new JTextField();
		outFile = new JTextField();
		inFile.setBounds(85, 15, 380, 20);
		inFile.setVisible(true);
		comp.add(inFile);
		outFile.setBounds(85, 55, 380, 20);
		comp.add(outFile);
		outFile.setVisible(true);
		inButton = new JButton("...");
		inButton.setBounds(470, 15, 20, 20);
		inButton.addActionListener(this);
		comp.add(inButton);
		outButton = new JButton("...");
		outButton.setBounds(470, 55, 20, 20);
		outButton.addActionListener(this);
		comp.add(outButton);
		okButton = new JButton("Ok");
		okButton.setBounds(500, 15, 80, 20);
		okButton.addActionListener(this);
		comp.add(okButton);
		cancelButton = new JButton("Cancel");
		cancelButton.setBounds(500, 55, 80, 20);
		cancelButton.addActionListener(this);
		comp.add(cancelButton);
		setVisible(true);
	}
	
	protected String getFile(String title, javax.swing.filechooser.FileFilter filter)
	{
		JFileChooser d = new JFileChooser();
		d.addChoosableFileFilter(filter);
		d.setFileFilter(filter);
		if (d.showDialog(this, "Set") == JFileChooser.APPROVE_OPTION)
			return d.getSelectedFile().toString();
		else 
			return "";
	}
	
	public String getInfile()
	{
		return inFile.getText();
	}
	
	public String getOutfile()
	{
		return outFile.getText();
	}
	
	public void cancel()
	{
		System.exit(0);
	}
	
	public void actionPerformed(ActionEvent ev)
	{
		Object src = ev.getSource();
		if (src == inButton) {
			inFile.setText(getFile("Input File", new CrFileFilter()));
		}
		if (src == outButton) {
			outFile.setText(getFile("Output File", new NrFileFilter()));
		}	
		if (src == okButton) {
			setVisible(false);
		}
		if (src == cancelButton) {
			cancel();
		}
	}
	
	class NrFileFilter extends javax.swing.filechooser.FileFilter
	{	
		public boolean accept(File dir)
		{
			return dir.isDirectory() ||  dir.toString().endsWith(".nr");
		}
		
		public String getDescription() {
			return "nr-Files";
		}
	}
	
	class CrFileFilter extends javax.swing.filechooser.FileFilter
	{	
		public boolean accept(File dir)
		{
			return dir.isDirectory() || dir.toString().endsWith(".cr");
		}
		
		public String getDescription() {
			return "cr-Files";
		}
	}
}
