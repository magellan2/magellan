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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JSlider;

import com.eressea.util.CollectionFactory;
import com.eressea.util.logging.Logger;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

/**
 * DOCUMENT ME!
 *
 * @author Sebastian
 * @version
 */
public class MapSaverUI extends InternationalizedDialog {
	private static final Logger log = Logger.getInstance(MapSaverUI.class);

	/**
	 * Creates a new MapSaverUI object.
	 *
	 * @param parent TODO: DOCUMENT ME!
	 * @param modal TODO: DOCUMENT ME!
	 * @param cmpSave TODO: DOCUMENT ME!
	 */
	public MapSaverUI(java.awt.Frame parent, boolean modal, Component cmpSave) {
		super(parent, modal);
		initComponents();

		String strList[] = {  /*"PNG",*/"JPEG" };

		cbFormat.setModel(new DefaultComboBoxModel(strList));
		cbFormat.setSelectedIndex(0);

		btnGroup = new ButtonGroup();
		btnGroup.add(rbtnCount);
		btnGroup.add(rbtnSize);

		rbtnCount.setSelected(true);

		pack();

		outComponent = cmpSave;
	}

	private void initComponents() {
		cbFormat = new javax.swing.JComboBox();
		btnCancel = new javax.swing.JButton(getString("btn.cancel.caption"));
		jPanel1 = new javax.swing.JPanel();
		rbtnSize = new javax.swing.JRadioButton(getString("radio.size.caption"));
		textX = new javax.swing.JTextField();
		rbtnCount = new javax.swing.JRadioButton(getString("radio.amount.caption"));
		textY = new javax.swing.JTextField();
		jLabel1 = new javax.swing.JLabel();
		jLabel2 = new javax.swing.JLabel();
		btnSave = new javax.swing.JButton(getString("btn.save.caption"));
		getContentPane().setLayout(new java.awt.GridBagLayout());

		java.awt.GridBagConstraints gridBagConstraints1;

		setTitle(getString("window.title"));
		addWindowListener(new java.awt.event.WindowAdapter() {
				public void windowClosing(java.awt.event.WindowEvent evt) {
					closeDialog(evt);
				}
			});

		DefaultBoundedRangeModel dbrm = new DefaultBoundedRangeModel(5, 0, 1, 10);

		qSlider = new JSlider(dbrm);
		qSlider.setMajorTickSpacing(1);
		qSlider.setPaintTicks(true);
		qSlider.setPaintLabels(true);
		qSlider.createStandardLabels(1);
		qSlider.setSnapToTicks(true);

		JLabel qLabel = new JLabel(getString("quality.label") + ":");

		gridBagConstraints1 = new java.awt.GridBagConstraints();
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.gridy = 1;
		gridBagConstraints1.insets = new java.awt.Insets(0, 5, 5, 5);
		getContentPane().add(cbFormat, gridBagConstraints1);

		btnCancel.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					btnCancelAction(evt);
				}
			});

		gridBagConstraints1 = new java.awt.GridBagConstraints();
		gridBagConstraints1.gridx = 2;
		gridBagConstraints1.gridy = 1;
		gridBagConstraints1.insets = new java.awt.Insets(0, 0, 5, 5);
		getContentPane().add(btnCancel, gridBagConstraints1);

		jPanel1.setLayout(new java.awt.GridBagLayout());

		java.awt.GridBagConstraints gridBagConstraints2;

		jPanel1.setBorder(new javax.swing.border.TitledBorder(getString("border.imageoptions")));

		rbtnSize.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					if(lastWasCount) {
						rbtnSizeAction(evt);
						lastWasCount = false;
					}
				}
			});

		gridBagConstraints2 = new java.awt.GridBagConstraints();
		gridBagConstraints2.gridx = 0;
		gridBagConstraints2.gridy = 1;
		gridBagConstraints2.gridwidth = 4;
		gridBagConstraints2.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints2.insets = new java.awt.Insets(0, 5, 5, 5);
		jPanel1.add(rbtnSize, gridBagConstraints2);

		textX.setText("1");

		gridBagConstraints2 = new java.awt.GridBagConstraints();
		gridBagConstraints2.gridx = 1;
		gridBagConstraints2.gridy = 2;
		gridBagConstraints2.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints2.insets = new java.awt.Insets(0, 0, 5, 5);
		gridBagConstraints2.weightx = 0.5;
		jPanel1.add(textX, gridBagConstraints2);

		rbtnCount.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					if(!lastWasCount) {
						rbtnCountAction(evt);
						lastWasCount = true;
					}
				}
			});

		gridBagConstraints2 = new java.awt.GridBagConstraints();
		gridBagConstraints2.gridx = 0;
		gridBagConstraints2.gridy = 0;
		gridBagConstraints2.gridwidth = 4;
		gridBagConstraints2.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints2.insets = new java.awt.Insets(5, 5, 0, 5);
		jPanel1.add(rbtnCount, gridBagConstraints2);

		textY.setText("1");

		gridBagConstraints2 = new java.awt.GridBagConstraints();
		gridBagConstraints2.gridx = 3;
		gridBagConstraints2.gridy = 2;
		gridBagConstraints2.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints2.insets = new java.awt.Insets(0, 0, 5, 5);
		gridBagConstraints2.weightx = 0.5;
		jPanel1.add(textY, gridBagConstraints2);

		jLabel1.setText("X:");
		jLabel1.setLabelFor(textX);

		gridBagConstraints2 = new java.awt.GridBagConstraints();
		gridBagConstraints2.gridx = 0;
		gridBagConstraints2.gridy = 2;
		gridBagConstraints2.insets = new java.awt.Insets(0, 5, 5, 5);
		jPanel1.add(jLabel1, gridBagConstraints2);

		jLabel2.setText("Y:");
		jLabel2.setLabelFor(textY);

		gridBagConstraints2 = new java.awt.GridBagConstraints();
		gridBagConstraints2.gridx = 2;
		gridBagConstraints2.gridy = 2;
		gridBagConstraints2.insets = new java.awt.Insets(0, 0, 5, 5);
		jPanel1.add(jLabel2, gridBagConstraints2);

		gridBagConstraints2.gridx = 0;
		gridBagConstraints2.gridy = 3;
		jPanel1.add(qLabel, gridBagConstraints2);

		gridBagConstraints2.gridx = 1;
		gridBagConstraints2.gridwidth = 3;
		jPanel1.add(qSlider, gridBagConstraints2);

		gridBagConstraints1 = new java.awt.GridBagConstraints();
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.gridy = 0;
		gridBagConstraints1.gridwidth = 3;
		gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints1.insets = new java.awt.Insets(5, 5, 5, 5);
		gridBagConstraints1.weightx = 1.0;
		gridBagConstraints1.weighty = 1.0;
		getContentPane().add(jPanel1, gridBagConstraints1);

		btnSave.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					btnSaveAction(evt);
				}
			});

		gridBagConstraints1 = new java.awt.GridBagConstraints();
		gridBagConstraints1.gridx = 1;
		gridBagConstraints1.gridy = 1;
		gridBagConstraints1.insets = new java.awt.Insets(0, 0, 5, 5);
		getContentPane().add(btnSave, gridBagConstraints1);
	}

	private void btnCancelAction(java.awt.event.ActionEvent evt) {
		setVisible(false);
		dispose();
	}

	private void btnSaveAction(java.awt.event.ActionEvent evt) {
		try {
			int iType = SAVEAS_IMAGETYPE_JPEG;
			String strBase;

			switch(cbFormat.getSelectedIndex()) {
			case 0:
				iType = SAVEAS_IMAGETYPE_JPEG;

				break;

			case 1:
				iType = SAVEAS_IMAGETYPE_PNG;

				break;
			}

			javax.swing.filechooser.FileFilter ff = new javax.swing.filechooser.FileFilter() {
				public boolean accept(File f) {
					String fileName = f.getName().toLowerCase();

					return f.isDirectory() ||
						   (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg"));
				}

				public String getDescription() {
					return getString("filter.jpg.description");
				}
			};

			javax.swing.JFileChooser fc = new javax.swing.JFileChooser();

			fc.addChoosableFileFilter(ff);
			fc.setFileFilter(ff);

			if(fc.showSaveDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION) {
				strBase = fc.getSelectedFile().toString();

				float quality = ((float) qSlider.getValue()) / 10f;

				if(rbtnCount.isSelected()) {
					saveAs_SC(strBase, Integer.parseInt(textX.getText()),
							  Integer.parseInt(textY.getText()), quality, iType);
				} else {
					saveAs(strBase, Integer.parseInt(textX.getText()),
						   Integer.parseInt(textY.getText()), quality, iType);
				}

				System.gc();
				setVisible(false);
				dispose();
			}
		} catch(Exception ex) {
			log.error(ex);
			javax.swing.JOptionPane.showMessageDialog(this,
													  getString("msg.erroronsave.text") +
													  ex.toString(),
													  getString("msg.erroronsave.title"),
													  javax.swing.JOptionPane.ERROR_MESSAGE);
		} catch(OutOfMemoryError oomError) {
			log.error(oomError);
			javax.swing.JOptionPane.showMessageDialog(this, getString("msg.outofmem.text"),
													  getString("msg.outofmem.title"),
													  javax.swing.JOptionPane.ERROR_MESSAGE);
		}
	}

	private void rbtnSizeAction(java.awt.event.ActionEvent evt) {
		int iX = Integer.parseInt(textX.getText());
		int iY = Integer.parseInt(textY.getText());
		int iWidth = outComponent.getBounds().width;
		int iHeight = outComponent.getBounds().height;

		textX.setText(Integer.toString(iWidth / iX));
		textY.setText(Integer.toString(iHeight / iY));
	}

	private void rbtnCountAction(java.awt.event.ActionEvent evt) {
		int iX = Integer.parseInt(textX.getText());
		int iY = Integer.parseInt(textY.getText());
		int iWidth = outComponent.getBounds().width;
		int iHeight = outComponent.getBounds().height;

		textX.setText(Integer.toString(iWidth / iX));
		textY.setText(Integer.toString(iHeight / iY));
	}

	/**
	 * Closes the dialog
	 *
	 * @param evt TODO: DOCUMENT ME!
	 */
	private void closeDialog(java.awt.event.WindowEvent evt) {
		setVisible(false);
		dispose();
	}

	private javax.swing.JComboBox cbFormat;
	private javax.swing.JButton btnCancel;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JRadioButton rbtnCount;
	private javax.swing.JTextField textX;
	private javax.swing.JRadioButton rbtnSize;
	private javax.swing.JTextField textY;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JButton btnSave;
	private JSlider qSlider;
	private ButtonGroup btnGroup;
	private boolean lastWasCount = true;
	private Component outComponent;

	/** TODO: DOCUMENT ME! */
	public static final int SAVEAS_IMAGETYPE_JPEG = 0;

	/** TODO: DOCUMENT ME! */
	public static final int SAVEAS_IMAGETYPE_PNG = 1;

	/*final static public int SAVEAS_IMAGETYPE_PNG256 = 2;
	 final static public int SAVEAS_IMAGETYPE_PNG16 = 3;*/
	public void saveAs(String strOut, int iWidth, int iHeight, float fQuality, int iSaveType)
				throws Exception
	{
		int iX = 0;
		int iY = 0;
		BufferedImage bimg = new BufferedImage(iWidth, iHeight, BufferedImage.TYPE_INT_RGB);
		Dimension dim = new Dimension(outComponent.getBounds().width,
									  outComponent.getBounds().height);
		Graphics2D g2 = null;

		iX = (int) (dim.getWidth() / iWidth);

		if((((int) dim.getWidth()) % iWidth) > 0) {
			iX++;
		}

		iY = (int) (dim.getHeight() / iHeight);

		if((((int) dim.getHeight()) % iHeight) > 0) {
			iY++;
		}

		dim = null;

		try {
			for(int y = 0; y < iY; y++) {
				for(int x = 0; x < iX; x++) {
					g2 = bimg.createGraphics();
					g2.setClip(0, 0, iWidth, iHeight);

					java.awt.geom.AffineTransform transform = new java.awt.geom.AffineTransform();
					transform.setToIdentity();
					transform.translate(-x * iWidth, -y * iHeight);
					g2.transform(transform);

					outComponent.paint(g2);

					transform = null;
					g2.dispose();
					g2 = null;

					SaveAs(strOut, bimg, x, y, x + (y * iX) + 1, iX * iY, iSaveType, fQuality);
				}
			}

			bimg.flush();
			bimg = null;
		} catch(OutOfMemoryError e) {
			if(g2 != null) {
				g2.dispose();
				g2 = null;
			}

			bimg.flush();
			bimg = null;

			throw e;
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param strOut TODO: DOCUMENT ME!
	 * @param iCountX TODO: DOCUMENT ME!
	 * @param iCountY TODO: DOCUMENT ME!
	 * @param fQuality TODO: DOCUMENT ME!
	 * @param iSaveType TODO: DOCUMENT ME!
	 *
	 * @throws Exception TODO: DOCUMENT ME!
	 */
	public void saveAs_SC(String strOut, int iCountX, int iCountY, float fQuality, int iSaveType)
				   throws Exception
	{
		Dimension dim = new Dimension(outComponent.getBounds().width,
									  outComponent.getBounds().height);

		int iWidth = ((int) dim.getWidth()) / iCountX;

		if(((int) dim.getWidth()) > (iWidth * iCountX)) {
			iWidth++;
		}

		int iHeight = ((int) dim.getHeight()) / iCountY;

		if(((int) dim.getHeight()) > (iHeight * iCountY)) {
			iHeight++;
		}

		saveAs(strOut, iWidth, iHeight, fQuality, iSaveType);
	}

	private void SaveAs(String strOut, BufferedImage bimg, int x, int y, int iOf, int iMax,
						int iSaveType, float fQuality) throws Exception
	{
		switch(iSaveType) {
		case SAVEAS_IMAGETYPE_JPEG: {
			String strOutput;

			strOutput = strOut + "_" + x + "_" + y + ".jpg";

			java.io.OutputStream out = new java.io.FileOutputStream(strOutput);

			log.info(strOutput + " " + iOf + " of " + iMax);

			com.sun.image.codec.jpeg.JPEGEncodeParam jpegEncodeParam = JPEGCodec.getDefaultJPEGEncodeParam(bimg);

			jpegEncodeParam.setQuality(fQuality, true);

			JPEGImageEncoder jpegImageEncoder = JPEGCodec.createJPEGEncoder(out, jpegEncodeParam);

			try {
				jpegImageEncoder.encode(bimg);
				jpegImageEncoder = null;
			} catch(OutOfMemoryError e) {
				jpegImageEncoder = null;
				throw e;
			}
		}

		break;

		case SAVEAS_IMAGETYPE_PNG: {
			//check for JAI
			try {
				ClassLoader cl = ClassLoader.getSystemClassLoader();

				cl.loadClass("com.sun.media.jai.codec.PNGEncodeParam");
			} catch(ClassNotFoundException e) {
				throw new Exception("F�r PNG Ausgabe wird das JAI (Java Advanced Imaging) " +
									"ben�tigt. Das JAI kann man unter http://java.sun.com erhalten.");
			}

			String strOutput;

			strOutput = strOut + "_" + x + "_" + y + ".png";

			java.io.OutputStream out = new java.io.FileOutputStream(strOutput);

			log.info(strOutput + " " + iOf + " of " + iMax);

			com.eressea.util.PNG.savePNG(bimg, out);
		}

		break;

			/*case SAVEAS_IMAGETYPE_PNG256: {
			 //check for JAI
			 try {
			 ClassLoader cl = ClassLoader.getSystemClassLoader();
			 cl.loadClass("com.sun.media.jai.codec.PNGEncodeParam");
			 }catch(ClassNotFoundException e) {
			 System.out.println("F�r PNG Ausgabe wird das JAI (Java Advanced Imaging) " +
			 "ben�tigt. Das JAI kann man unter http://java.sun.com erhalten.");
			 System.exit(0);
			 }
			 String strOutput;

			 strOutput = strOut + "_" + x + "_" + y + ".png";
			 java.io.OutputStream out = new java.io.FileOutputStream(strOutput);

			 System.out.println(strOutput + " " + iOf + " von " + iMax);

			 BufferedImage bimg2;
			 bimg2 = new BufferedImage(bimg.getWidth(), bimg.getHeight(),
			 BufferedImage.TYPE_BYTE_INDEXED, _icmPalette16);

			 imgCopyRGBToIndex(bimg, bimg2);

			 priv.tsLib.images.PNG.savePNG(bimg2, out);
			 }break;
			 case SAVEAS_IMAGETYPE_PNG16: {
			 //check for JAI
			 try {
			 ClassLoader cl = ClassLoader.getSystemClassLoader();
			 cl.loadClass("com.sun.media.jai.codec.PNGEncodeParam");
			 }catch(ClassNotFoundException e) {
			 System.out.println("F�r PNG Ausgabe wird das JAI (Java Advanced Imaging) " +
			 "ben�tigt. Das JAI kann man unter http://java.sun.com erhalten.");
			 e.printStackTrace();
			 System.exit(0);
			 }
			 String strOutput;

			 strOutput = strOut + "_" + x + "_" + y + ".png";
			 java.io.OutputStream out = new java.io.FileOutputStream(strOutput);

			 System.out.println(strOutput + " " + iOf + " von " + iMax);

			 BufferedImage bimg2;
			 bimg2 = new BufferedImage(bimg.getWidth(), bimg.getHeight(),
			 BufferedImage.TYPE_BYTE_INDEXED, _icmPalette16);

			 imgCopyRGBToIndex(bimg, bimg2);

			 priv.tsLib.images.PNG.savePNG(bimg2, out);
			 }break;*/
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
			defaultTranslations.put("btn.cancel.caption", "Cancel");
			defaultTranslations.put("quality.label", "Quality");
			defaultTranslations.put("radio.size.caption", "Size");
			defaultTranslations.put("radio.amount.caption", "Number of images");
			defaultTranslations.put("btn.save.caption", "Save...");
			defaultTranslations.put("window.title", "Save map");
			defaultTranslations.put("border.imageoptions", "Image parameters");
			defaultTranslations.put("filter.jpg.description", "JPEG files (*.jpg)");

			defaultTranslations.put("msg.erroronsave.text", "An error occurred while saving: ");
			defaultTranslations.put("msg.erroronsave.title", "Error");
			defaultTranslations.put("msg.outofmem.text",
									"There was not enough memory to save the image. Try to save the image in smaller pieces.");
			defaultTranslations.put("msg.outofmem.title", "Error");
		}

		return defaultTranslations;
	}
}
